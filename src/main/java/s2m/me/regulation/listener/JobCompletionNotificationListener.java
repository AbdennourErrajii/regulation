package s2m.me.regulation.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import s2m.me.regulation.domain.Settlement;
import s2m.me.regulation.dto.PacsTriggerRequest;
import s2m.me.regulation.enums.SettlementStatus;
import s2m.me.regulation.repository.SettlementRepository;
import s2m.me.regulation.service.client.PacsGeneratorClient;
import s2m.me.regulation.steps.workdetermination.WorkUnitDeterminationTasklet;

import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final SettlementRepository settlementRepository;
    private final PacsGeneratorClient pacsGeneratorClient;



    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        String sessionId = jobExecution.getJobParameters().getString("sessionId");
        Settlement settlement = settlementRepository.findBySessionId(sessionId)
                .orElse(null);

        if (settlement == null) {
            log.error("LISTENER: Could not find Settlement with sessionId {}. Cannot update final status.", sessionId);
            return;
        }

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("LISTENER: Job {} finished successfully. Updating status and triggering Pacs Generator.", jobExecution.getJobInstance().getJobName());

            settlement.setStatus(SettlementStatus.COMPLETED);
            settlement.setExecEndDate(new Date());
            settlementRepository.save(settlement);
            log.info("LISTENER: Settlement {} status updated to COMPLETED.", sessionId);

            ExecutionContext jobContext = jobExecution.getExecutionContext();
            Set<String> currencies = (Set<String>) jobContext.get(WorkUnitDeterminationTasklet.DISTINCT_CURRENCIES_KEY);
            if (currencies == null || currencies.isEmpty()) {
                log.warn("LISTENER: No distinct currencies found in job context. Cannot trigger Pacs Generator.");
                return;
            }

            // Déclencher le Job 2
           /* try {
                PacsTriggerRequest triggerRequest = new PacsTriggerRequest(sessionId, new ArrayList<>(currencies));
                pacsGeneratorClient.triggerPacsGeneration(triggerRequest);
                log.info("LISTENER: Successfully triggered Pacs Generator for sessionId {} with currencies {}.", sessionId, currencies);
            } catch (Exception e) {
                log.error("LISTENER: CRITICAL - Failed to trigger Pacs Generator Job. The process is incomplete but Settlement is marked COMPLETED.", e);
            }*/

        }
        // CAS 2: Le job a échoué
        else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("LISTENER: Job {} failed. Updating status to ERROR.", jobExecution.getJobInstance().getJobName());
            settlement.setStatus(SettlementStatus.ERROR);
            settlement.setExecEndDate(new Date());
            settlementRepository.save(settlement);
            log.info("LISTENER: Settlement {} status updated to ERROR.", sessionId);
        }
    }
}