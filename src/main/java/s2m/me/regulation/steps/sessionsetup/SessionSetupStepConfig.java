package s2m.me.regulation.steps;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SessionSetupStepConfig {

    /**
     * Définit le bean pour le Step de préparation de la session.
     * C'est la première étape du job de règlement.
     */
    @Bean
    public Step sessionSetupStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SessionSetupTasklet sessionSetupTasklet // Le tasklet est injecté par Spring
    ) {
        return new StepBuilder("sessionSetupStep", jobRepository)
                .tasklet(sessionSetupTasklet, transactionManager)
                .build();
    }
}