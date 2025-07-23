package s2m.me.regulation.steps.reportgeneration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import s2m.me.regulation.domain.transaction.Transaction;
import s2m.me.regulation.dto.CalculationResult;
import s2m.me.regulation.dto.InstitutionCurrencyPair;
import s2m.me.regulation.repository.InstitutionReportRepository;
import s2m.me.regulation.repository.SettlementReportRepository;
import s2m.me.regulation.service.ReportCalculationService;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import s2m.me.regulation.steps.workdetermination.WorkUnitDeterminationTasklet;

@Configuration
public class ReportGenerationStepConfig {

    // ===================================================================================
    // 1. LE MANAGER STEP
    // ===================================================================================

    @Bean("managerReportGenerationStep")
    public Step managerReportGenerationStep(
            JobRepository jobRepository,
            @Qualifier("institutionCurrencyPartitioner") Partitioner partitioner,
            @Qualifier("workerReportGenerationStep") Step workerStep,
            @Qualifier("partitionTaskExecutor") TaskExecutor taskExecutor
    ) {
        return new StepBuilder("reportGeneration.manager", jobRepository)
                .partitioner("workerStep", partitioner)
                .step(workerStep)
                .taskExecutor(taskExecutor)
                .gridSize(10)
                .build();
    }

    /**
     * Le Partitioner qui crée les partitions.
     * Il est @StepScope car il lit le JobExecutionContext qui est rempli par un step précédent.
     */
    @Bean
    @StepScope
    public Partitioner institutionCurrencyPartitioner(
            @Value("#{jobExecutionContext['" + WorkUnitDeterminationTasklet.INSTITUTION_CURRENCY_PAIRS_KEY + "']}") Set<InstitutionCurrencyPair> pairs
    ) {
        return new InstitutionCurrencyPartitioner(pairs);
    }


    // ===================================================================================
    // 2. LE WORKER STEP
    // ===================================================================================

    @Bean
    public Step workerReportGenerationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<List<Transaction>> transactionListReader,
            ItemProcessor<List<Transaction>, CalculationResult> reportGenerationProcessor,
            ItemWriter<CalculationResult> reportGenerationWriter
    ) {
        return new StepBuilder("reportGeneration.worker", jobRepository)
                .<List<Transaction>, CalculationResult>chunk(1, transactionManager)
                .reader(transactionListReader)
                .processor(reportGenerationProcessor)
                .writer(reportGenerationWriter)
                .build();
    }


    // ===================================================================================
    // 3. LES COMPOSANTS DU WORKER
    // ===================================================================================

    @Bean
    @StepScope
    public ItemReader<List<Transaction>> transactionListReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{jobParameters['sessionId']}") String sessionId,
            @Value("#{jobParameters['centerId']}") String centerId,
            @Value("#{stepExecutionContext['processingInstitutionId']}") String institutionId,
            @Value("#{stepExecutionContext['processingCurrency']}") String currency
    ) {
        return new TransactionListReader(entityManagerFactory, sessionId, centerId, currency, institutionId);
    }

    @Bean
    @StepScope
    public ItemProcessor<List<Transaction>, CalculationResult> reportGenerationProcessor(
            ReportCalculationService calculationService,
            @Value("#{jobParameters['sessionId']}") String sessionId,
            @Value("#{jobParameters['centerId']}") String centerId,
            @Value("#{jobParameters['sessionDate']}") Date sessionDate,
            @Value("#{stepExecutionContext['processingCurrency']}") String currency,
            @Value("#{stepExecutionContext['processingInstitutionId']}") String institutionId
    ) {
        return new ReportGenerationProcessor(calculationService, sessionId, centerId, sessionDate, currency, institutionId);
    }

    @Bean
    @StepScope
    public ItemWriter<CalculationResult> reportGenerationWriter(
            SettlementReportRepository settlementReportRepo,
            InstitutionReportRepository institutionReportRepo
    ) {
        return new ReportGenerationWriter(settlementReportRepo, institutionReportRepo);
    }
}