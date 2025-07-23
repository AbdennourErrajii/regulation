package s2m.me.regulation.steps.settlementfinalization;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import s2m.me.regulation.domain.Settlement;
import s2m.me.regulation.domain.report.WalletActivityReport;
import s2m.me.regulation.repository.SettlementRepository;
import s2m.me.regulation.steps.workdetermination.WorkUnitDeterminationTasklet;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Configuration
public class SettlementFinalizationStepConfig {
    @Bean
    public Step settlementFinalizationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<String> distinctCurrencyReader,
            ItemProcessor<String, WalletActivityReport> walletActivityReportProcessor,
            CompositeItemWriter<WalletActivityReport> finalizationCompositeWriter
    ) {
        return new StepBuilder("settlementFinalizationStep", jobRepository)
                .<String, WalletActivityReport>chunk(1, transactionManager)
                .reader(distinctCurrencyReader)
                .processor(walletActivityReportProcessor)
                .writer(finalizationCompositeWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> distinctCurrencyReader(
            @Value("#{jobExecutionContext['" + WorkUnitDeterminationTasklet.DISTINCT_CURRENCIES_KEY + "']}") Set<String> distinctCurrencies
    ) {
        return new DistinctCurrencyReader(distinctCurrencies);
    }

    @Bean
    @StepScope
    public ItemProcessor<String, WalletActivityReport> walletActivityReportProcessor(
            SettlementRepository settlementRepository,
            @Value("#{jobParameters['sessionId']}") String sessionId,
            @Value("#{jobParameters['centerId']}") String centerId,
            @Value("#{jobParameters['sessionDate']}") Date sessionDate
    ) {
        // Récupérer le parent Settlement une seule fois
        Settlement settlement = settlementRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("Settlement not found for sessionId: " + sessionId));
        return new WalletActivityReportProcessor(settlement, centerId, sessionDate);
    }

    @Bean
    public CompositeItemWriter<WalletActivityReport> finalizationCompositeWriter(
            EntityManagerFactory emf, DataSource dataSource
    ) {
        CompositeItemWriter<WalletActivityReport> compositeWriter = new CompositeItemWriter<>();
        compositeWriter.setDelegates(List.of(
                new WalletActivityReportWriter(emf),
                new OrphanReportsLinkerWriter(dataSource)
        ));
        return compositeWriter;
    }
}