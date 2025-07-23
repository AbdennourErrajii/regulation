package s2m.me.regulation.steps.workdetermination;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class WorkUnitDeterminationStepConfig {

    @Bean
    public Step workUnitDeterminationStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            WorkUnitDeterminationTasklet workUnitDeterminationTasklet
    ) {
        return new StepBuilder("workUnitDeterminationStep", jobRepository)
                .tasklet(workUnitDeterminationTasklet, transactionManager)
                .build();
    }
}