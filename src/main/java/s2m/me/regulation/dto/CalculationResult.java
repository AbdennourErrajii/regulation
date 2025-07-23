package s2m.me.regulation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import s2m.me.regulation.domain.report.InstitutionReport;
import s2m.me.regulation.domain.report.SettlementReport;

@Getter
@AllArgsConstructor
public class CalculationResult {
    private final SettlementReport settlementReport;
    private final InstitutionReport institutionReport;
}