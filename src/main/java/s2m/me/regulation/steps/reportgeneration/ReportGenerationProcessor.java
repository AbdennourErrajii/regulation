package s2m.me.regulation.steps.reportgeneration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import s2m.me.regulation.domain.transaction.Transaction;
import s2m.me.regulation.dto.CalculationResult;
import s2m.me.regulation.service.ReportCalculationService;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class ReportGenerationProcessor implements ItemProcessor<List<Transaction>, CalculationResult> {
    private final ReportCalculationService calculationService;
    private final String sessionId;
    private final String centerId;
    private final Date sessionDate;
    private final String processingCurrency;
    private final String processingInstitutionId;

    @Override
    public CalculationResult process(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }
        return calculationService.generateAllReportsForInstitutionCurrency(
                transactions, processingInstitutionId, processingCurrency, sessionId, sessionDate
        );
    }
}