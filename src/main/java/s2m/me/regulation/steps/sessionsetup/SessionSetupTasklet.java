package s2m.me.regulation.steps.sessionsetup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import s2m.me.regulation.domain.Settlement;
import s2m.me.regulation.enums.SettlementStatus;
import s2m.me.regulation.repository.SettlementRepository;
import s2m.me.regulation.service.client.ProcessingClient;
import s2m.me.regulation.service.client.SafClient;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@StepScope
@Slf4j
public class SessionSetupTasklet implements Tasklet {

    private final SettlementRepository settlementRepository;
    private final ProcessingClient processingClient;
    private final SafClient safClient;
    private final String sessionIdToProcess;
    private final String centerId;

    @Autowired
    public SessionSetupTasklet(
            SettlementRepository settlementRepository,
            ProcessingClient processingClient,
            SafClient safClient,
            @Value("#{jobParameters['sessionId']}") String sessionIdToProcess,
            @Value("#{jobParameters['centerId']}") String centerId
    ) {
        this.settlementRepository = settlementRepository;
        this.processingClient = processingClient;
        this.safClient = safClient;
        this.sessionIdToProcess = sessionIdToProcess;
        this.centerId = centerId;
    }

    @Override
    @Transactional // Garantit que l'ensemble de la méthode est une transaction atomique
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--- [Step 1/5] Starting SessionSetupStep for sessionId: {} ---", sessionIdToProcess);

        // 1. & 2. Trouver et Valider le règlement
        Settlement currentSettlement = settlementRepository.findBySessionId(sessionIdToProcess)
                .orElseThrow(() -> new IllegalStateException("FATAL: Settlement not found for sessionId: " + sessionIdToProcess));

        if (currentSettlement.getStatus() != SettlementStatus.PENDING) {
            log.error("Settlement {} is not in PENDING state. Current state: {}. Aborting job.", sessionIdToProcess, currentSettlement.getStatus());
            throw new IllegalStateException("Settlement is not in PENDING state.");
        }

        // 3. Verrouiller le règlement actuel (Mise à jour en mémoire)
        log.info("Updating settlement {} status to IN_PROGRESS.", sessionIdToProcess);
        currentSettlement.setStatus(SettlementStatus.IN_PROGRESS);
        currentSettlement.setExecStartDate(new Date());

        // 4. Générer le NOUVEAU sessionId
        String newSessionId = generateNextSessionId(sessionIdToProcess);
        log.info("Generated next sessionId: {}", newSessionId);

        // 5. Créer le prochain règlement (Création en mémoire)
        Settlement nextSettlement = new Settlement();
        nextSettlement.setSessionId(newSessionId);
        nextSettlement.setCenterId(this.centerId);
        nextSettlement.setStatus(SettlementStatus.PENDING);
        nextSettlement.setGenerationDate(new Date());

        // 6. Sauvegarder les deux (Mise à jour et Création) en une seule transaction
        settlementRepository.save(currentSettlement);
        settlementRepository.save(nextSettlement);
        log.info("Persisted current settlement as IN_PROGRESS and created new settlement {} as PENDING.", newSessionId);

        // 7. Notifier les autres services avec la NOUVELLE liste des sessions PENDING
        try {
            // **CORRECTION APPLIQUÉE ICI**
            // 1. Récupérer toutes les sessions PENDING actuelles (qui inclut encore celle qu'on traite)
            List<Settlement> pendingSettlementsFromDb = settlementRepository.findByStatus(SettlementStatus.PENDING);

            // 2. Construire la map manuellement pour refléter l'état FUTUR
            Map<String, String> activeSessionsMap = new HashMap<>();
            for (Settlement s : pendingSettlementsFromDb) {
                // On exclut explicitement la session que l'on est en train de traiter de la liste PENDING
                if (!s.getSessionId().equals(this.sessionIdToProcess)) {
                    activeSessionsMap.put(s.getCenterId(), s.getSessionId());
                }
            }

            // 3. On ajoute la nouvelle session qui sera PENDING après le commit
            activeSessionsMap.put(nextSettlement.getCenterId(), nextSettlement.getSessionId());

            log.info("Notifying external services with the final active session map: {}", activeSessionsMap);
            //processingClient.sendSessionIds(activeSessionsMap);
            //safClient.sendSessionIds(activeSessionsMap);

        } catch (Exception e) {
            log.error("CRITICAL: Failed to notify external services about the new sessions. Rolling back transaction.", e);
            // On encapsule l'exception originale pour un meilleur débogage
            throw new RuntimeException("Failed to notify external services. The settlement process will be rolled back.", e);
        }

        // Stocker des informations pour les steps suivants si nécessaire
        contribution.getStepExecution().getJobExecution().getExecutionContext().put("currentSettlementId", currentSettlement.getId());

        log.info("--- [Step 1/5] SessionSetupStep finished successfully. ---");
        return RepeatStatus.FINISHED;
    }

    /**
     * Génère le prochain sessionId basé sur le dernier.
     * Format: YYYYJJJI (Année, Jour de l'année, Index)
     */
    private String generateNextSessionId(String lastSessionId) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        String yearAndDay = String.format("%d%03d", year, dayOfYear); // ex: "2024199"

        int nextIndex = 1;

        if (lastSessionId != null && lastSessionId.length() > 7 && lastSessionId.startsWith(yearAndDay)) {
            try {
                int lastIndex = Integer.parseInt(lastSessionId.substring(7));
                nextIndex = lastIndex + 1;
            } catch (NumberFormatException e) {
                log.warn("Could not parse index from lastSessionId [{}]. Defaulting to 1.", lastSessionId);
                nextIndex = 1;
            }
        }

        return yearAndDay + nextIndex;
    }
}