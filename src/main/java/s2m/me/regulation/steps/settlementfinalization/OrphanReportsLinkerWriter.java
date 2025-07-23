package s2m.me.regulation.steps.settlementfinalization;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import s2m.me.regulation.domain.report.WalletActivityReport;

import javax.sql.DataSource;

public class OrphanReportsLinkerWriter implements ItemWriter<WalletActivityReport> {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // Requête pour lier les SettlementReport
    private static final String LINK_SETTLEMENT_REPORTS_SQL = """
        UPDATE SETTLEMENT_REPORT SET W_ACTIVITY_REPORT_ID = :warId
        WHERE SESSION_ID = :sessionId 
        AND W_ACTIVITY_REPORT_ID IS NULL
        AND GLOBAL_REPORT_ID IN (
            SELECT ID FROM GLOBAL_REPORT WHERE NET_SETTLEMENT_CURR = :currency
        )
    """;

    // Requête pour lier les InstitutionReport
    private static final String LINK_INSTITUTION_REPORTS_SQL = """
        UPDATE INST_REPORT SET W_ACTIVITY_REPORT_ID = :warId
        WHERE SESSION_ID = :sessionId
        AND W_ACTIVITY_REPORT_ID IS NULL
        AND NET_SETTLEMENT_CURR = :currency
    """;

    public OrphanReportsLinkerWriter(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends WalletActivityReport> chunk) throws Exception {
        for (WalletActivityReport war : chunk) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("warId", war.getId())
                    .addValue("sessionId", war.getSessionId())
                    .addValue("currency", war.getActivityReportCurrency());

            // Exécute les deux requêtes UPDATE en masse
            jdbcTemplate.update(LINK_SETTLEMENT_REPORTS_SQL, params);
            jdbcTemplate.update(LINK_INSTITUTION_REPORTS_SQL, params);
        }
    }
}