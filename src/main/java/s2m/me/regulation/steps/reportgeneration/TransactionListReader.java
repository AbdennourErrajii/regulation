package s2m.me.regulation.steps.reportgeneration;



import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import s2m.me.regulation.domain.transaction.Transaction;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TransactionListReader implements ItemReader<List<Transaction>> {
    private final EntityManagerFactory entityManagerFactory;
    private final String sessionId;
    private final String centerId;
    private final String processingCurrency;
    private final String processingInstitutionId;
    private boolean dataFetched = false;

    @Override
    public List<Transaction> read() {
        if (dataFetched) return null;

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            String jpql = "SELECT t FROM Transaction t LEFT JOIN FETCH t.feeInfo " +
                    "WHERE t.sessionId = :sessionId AND t.centerId = :centerId " +
                    "AND t.transactionCurrency = :currency " +
                    "AND (t.debitorInstitutionId = :institutionId OR t.creditorInstitutionId = :institutionId)";

            TypedQuery<Transaction> query = em.createQuery(jpql, Transaction.class)
                    .setParameter("sessionId", sessionId)
                    .setParameter("centerId", centerId)
                    .setParameter("currency", processingCurrency)
                    .setParameter("institutionId", processingInstitutionId);

            List<Transaction> transactions = query.getResultList();
            this.dataFetched = true;

            if (transactions.isEmpty()) {
                log.info("No transactions found for partition [Inst: {}, Curr: {}].", processingInstitutionId, processingCurrency);
                return null;
            }
            log.info("Read {} transactions for partition [Inst: {}, Curr: {}].", transactions.size(), processingInstitutionId, processingCurrency);
            return transactions;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}