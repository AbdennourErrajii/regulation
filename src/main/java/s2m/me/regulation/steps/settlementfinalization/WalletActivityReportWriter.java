package s2m.me.regulation.steps.settlementfinalization;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import s2m.me.regulation.domain.report.WalletActivityReport;

@Slf4j
public class WalletActivityReportWriter implements ItemWriter<WalletActivityReport> {

    private final EntityManagerFactory entityManagerFactory;

    public WalletActivityReportWriter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void write(Chunk<? extends WalletActivityReport> chunk) throws Exception {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        if (em == null) {
            throw new IllegalStateException("Transactional EntityManager not found.");
        }

        for (WalletActivityReport war : chunk.getItems()) {
            if (war == null) continue;

            if (war.getId() == null) {
                log.debug("Persisting new WalletActivityReport for currency: {}", war.getActivityReportCurrency());
                em.persist(war);
            } else {
                log.debug("Merging existing WalletActivityReport ID: {} for currency: {}", war.getId(), war.getActivityReportCurrency());
                em.merge(war);
            }
        }

        em.flush();
        log.debug("Flushed EntityManager after persisting/merging WalletActivityReport(s).");
    }
}
