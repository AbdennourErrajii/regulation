package s2m.me.regulation.steps.reportgeneration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import s2m.me.regulation.dto.CalculationResult;
import s2m.me.regulation.repository.InstitutionReportRepository;
import s2m.me.regulation.repository.SettlementReportRepository;

@RequiredArgsConstructor
public class ReportGenerationWriter implements ItemWriter<CalculationResult> {
    private final SettlementReportRepository settlementReportRepository;
    private final InstitutionReportRepository institutionReportRepository;

    @Override
    public void write(Chunk<? extends CalculationResult> chunk) throws Exception {
        for (CalculationResult result : chunk.getItems()) {
            settlementReportRepository.save(result.getSettlementReport());
            institutionReportRepository.save(result.getInstitutionReport());
        }
    }
}