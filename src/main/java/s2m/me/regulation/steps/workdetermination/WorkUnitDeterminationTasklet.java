package s2m.me.regulation.steps.workdetermination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import s2m.me.regulation.dto.InstitutionCurrencyPair;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@StepScope
@Slf4j
public class WorkUnitDeterminationTasklet implements Tasklet {

    // Clés pour stocker les résultats dans le contexte d'exécution du Job
    public static final String INSTITUTION_CURRENCY_PAIRS_KEY = "institutionCurrencyPairs";
    public static final String DISTINCT_CURRENCIES_KEY = "distinctCurrencies";

    private final JdbcTemplate jdbcTemplate;
    private final String sessionId;
    private final String centerId;

    @Autowired
    public WorkUnitDeterminationTasklet(
            DataSource dataSource,
            @Value("#{jobParameters['sessionId']}") String sessionId,
            @Value("#{jobParameters['centerId']}") String centerId
    ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sessionId = sessionId;
        this.centerId = centerId;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("--- [Step 2/5] Starting WorkUnitDeterminationStep for sessionId: {} ---", sessionId);

        String sql = "SELECT DISTINCT DEBIT_INST_REF AS institution_id, TX_CURRENCY AS currency FROM TRANSACTION " +
                "WHERE SESSION_ID = ? AND TX_CENTER_ID = ? AND DEBIT_INST_REF IS NOT NULL " +
                "UNION " +
                "SELECT DISTINCT CRED_INST_ID AS institution_id, TX_CURRENCY AS currency FROM TRANSACTION " +
                "WHERE SESSION_ID = ? AND TX_CENTER_ID = ? AND CRED_INST_ID IS NOT NULL";

        // Exécute la requête et mappe les résultats directement en objets InstitutionCurrencyPair
        Set<InstitutionCurrencyPair> pairs = new HashSet<>(jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new InstitutionCurrencyPair(
                        rs.getString("institution_id"),
                        rs.getString("currency")
                ),
                sessionId, centerId, sessionId, centerId
        ));

        if (pairs.isEmpty()) {
            log.warn("No institution-currency pairs found for sessionId: {}. Subsequent steps will be skipped.", sessionId);
            // Il est important de mettre des sets vides pour que les steps suivants ne plantent pas
            ExecutionContext jobContext = getJobExecutionContext(chunkContext);
            jobContext.put(INSTITUTION_CURRENCY_PAIRS_KEY, new HashSet<>());
            jobContext.put(DISTINCT_CURRENCIES_KEY, new HashSet<>());
            return RepeatStatus.FINISHED;
        }

        // Extrait les devises distinctes à partir des paires trouvées
        Set<String> distinctCurrencies = pairs.stream()
                .map(InstitutionCurrencyPair::getCurrency)
                .collect(Collectors.toSet());

        log.info("Found {} distinct currencies: {}", distinctCurrencies.size(), distinctCurrencies);
        log.info("Found {} institution-currency pairs to process.", pairs.size());

        // Stocke les résultats dans le JobExecutionContext pour les steps suivants
        ExecutionContext jobContext = getJobExecutionContext(chunkContext);
        jobContext.put(INSTITUTION_CURRENCY_PAIRS_KEY, pairs);
        jobContext.put(DISTINCT_CURRENCIES_KEY, distinctCurrencies);

        log.info("--- [Step 2/5] WorkUnitDeterminationStep finished successfully. ---");
        return RepeatStatus.FINISHED;
    }

    private ExecutionContext getJobExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }
}