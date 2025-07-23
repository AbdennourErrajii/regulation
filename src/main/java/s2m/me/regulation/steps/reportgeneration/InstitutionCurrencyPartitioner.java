package s2m.me.regulation.steps.reportgeneration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import s2m.me.regulation.dto.InstitutionCurrencyPair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class InstitutionCurrencyPartitioner implements Partitioner {

    private final Set<InstitutionCurrencyPair> pairsToProcess;

    public InstitutionCurrencyPartitioner(Set<InstitutionCurrencyPair> pairsToProcess) {
        this.pairsToProcess = pairsToProcess;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();
        if (pairsToProcess == null || pairsToProcess.isEmpty()) {
            log.warn("No pairs to process for partitioning. ReportGenerationStep will have no workers.");
            return result;
        }

        int partitionNumber = 0;
        for (InstitutionCurrencyPair pair : pairsToProcess) {
            ExecutionContext context = new ExecutionContext();
            context.putString("processingInstitutionId", pair.getInstitutionId());
            context.putString("processingCurrency", pair.getCurrency());

            String partitionName = "partition_" + partitionNumber++;
            result.put(partitionName, context);
        }
        log.info("Created {} partitions for report generation.", result.size());
        return result;
    }
}