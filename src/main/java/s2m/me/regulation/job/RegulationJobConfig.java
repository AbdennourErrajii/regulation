package s2m.me.regulation.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegulationJobConfig {

    @Bean("RegulationJob")
    public Job regulationJob(
            JobRepository jobRepository,
            @Qualifier("sessionSetupStep") Step sessionSetupStep,
            @Qualifier("workUnitDeterminationStep") Step workUnitDeterminationStep,
            @Qualifier("managerReportGenerationStep") Step managerReportGenerationStep,
            @Qualifier("settlementFinalizationStep") Step settlementFinalizationStep
    ) {
        return new JobBuilder("RegulationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sessionSetupStep)
                .next(workUnitDeterminationStep)
                .next(managerReportGenerationStep)
                .next(settlementFinalizationStep)
                .build();
    }
}
