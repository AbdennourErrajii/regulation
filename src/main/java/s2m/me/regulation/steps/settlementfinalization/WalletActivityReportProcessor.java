package s2m.me.regulation.steps.settlementfinalization;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import s2m.me.regulation.domain.Settlement;
import s2m.me.regulation.domain.report.WalletActivityReport;

import java.util.Date;

@RequiredArgsConstructor
public class WalletActivityReportProcessor implements ItemProcessor<String, WalletActivityReport> {
    private final Settlement settlement;
    private final String centerId;
    private final Date sessionDate;

    @Override
    public WalletActivityReport process(String currency) throws Exception {
        WalletActivityReport war = new WalletActivityReport();
        war.setSettlement(settlement);
        war.setSessionId(settlement.getSessionId());
        war.setCenterId(centerId);
        war.setSessionDate(sessionDate);
        war.setActivityReportCurrency(currency);
        return war;
    }
}