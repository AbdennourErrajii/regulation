package s2m.me.regulation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import s2m.me.regulation.domain.report.*;
import s2m.me.regulation.domain.transaction.Transaction;
import s2m.me.regulation.domain.transaction.TxFeeInfo;
import s2m.me.regulation.dto.CalculationResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportCalculationService {

    private static final String SIGN_DEBIT = "D";
    private static final String SIGN_CREDIT = "C";
    private static final String ACC_TYPE_WALLET = "0";
    private static final String ACC_TYPE_ACCOUNT = "1";

    public CalculationResult generateAllReportsForInstitutionCurrency(
            List<Transaction> transactions,
            String institutionId,
            String currency,
            String sessionId,
            Date sessionDate) {

        SettlementReport settlementReport = generateSettlementReport(
                transactions, institutionId, currency, sessionId, sessionDate
        );

        InstitutionReport institutionReport = generateInstitutionReport(
                transactions, settlementReport
        );

        return new CalculationResult(settlementReport, institutionReport);
    }

    private SettlementReport generateSettlementReport(
            List<Transaction> transactions,
            String institutionId,
            String currency,
            String sessionId,
            Date sessionDate) {

        SettlementReport report = new SettlementReport();
        report.setInstitutionId(institutionId);
        report.setSessionId(sessionId);
        report.setSessionDate(sessionDate);

        // Initialiser les collections pour éviter les NullPointerException
        report.setFeeInfo(new ArrayList<>());
        report.setReportByTrxType(new ArrayList<>());
        report.setReportByInstitution(new ArrayList<>());
        report.setReportByInstitutionAndTrxTypeResponse(new ArrayList<>());

        report.setGlobalReport(buildGlobalReport(transactions, institutionId, currency));
        report.setReportByTrxType(buildReportByTrxType(transactions, institutionId, currency, report));
        report.setReportByInstitution(buildReportByInstitution(transactions, institutionId, currency, report));
        report.setReportByInstitutionAndTrxTypeResponse(buildReportByInstAndTrxType(transactions, institutionId, currency, report));
        report.setFeeInfo(buildFeeInfos(transactions, institutionId, report));

        return report;
    }

    private GlobalReport buildGlobalReport(List<Transaction> transactions, String institutionId, String currency) {
        GlobalReport report = new GlobalReport();
        initializeGlobalReport(report, currency);

        for (Transaction tx : transactions) {
            boolean isDebitor = institutionId.equals(tx.getDebitorInstitutionId());
            boolean isCreditor = institutionId.equals(tx.getCreditorInstitutionId());
            BigDecimal amount = tx.getTransactionAmount();

            report.setTransactionCount(report.getTransactionCount() + 1);

            if (isCreditor) {
                report.setCreditTrxCount(report.getCreditTrxCount() + 1);
                report.setCreditTrxAmount(report.getCreditTrxAmount().add(amount));

                if (ACC_TYPE_ACCOUNT.equals(tx.getCreditorAccType())) {
                    report.setAccountCreditTrxCount(report.getAccountCreditTrxCount() + 1);
                    report.setAccountCreditTrxAmount(report.getAccountCreditTrxAmount().add(amount));
                } else if (ACC_TYPE_WALLET.equals(tx.getCreditorAccType())) {
                    report.setWalletCreditTrxCount(report.getWalletCreditTrxCount() + 1);
                    report.setWalletCreditTrxAmount(report.getWalletCreditTrxAmount().add(amount));
                }
            }

            if (isDebitor) {
                report.setDebitTrxCount(report.getDebitTrxCount() + 1);
                report.setDebitTrxAmount(report.getDebitTrxAmount().add(amount));

                if (ACC_TYPE_ACCOUNT.equals(tx.getDebitorAccType())) {
                    report.setAccountDebitTrxCount(report.getAccountDebitTrxCount() + 1);
                    report.setAccountDebitTrxAmount(report.getAccountDebitTrxAmount().add(amount));
                } else if (ACC_TYPE_WALLET.equals(tx.getDebitorAccType())) {
                    report.setWalletDebitTrxCount(report.getWalletDebitTrxCount() + 1);
                    report.setWalletDebitTrxAmount(report.getWalletDebitTrxAmount().add(amount));
                }
            }

            processFees(report, tx, institutionId);
        }

        calculateNetAmounts(report);
        return report;
    }

    private void initializeGlobalReport(GlobalReport report, String currency) {
        // Initialisation complète de tous les champs
        report.setNetSettlementCurrency(currency);
        report.setTransactionCount(0);
        report.setNetSettlementAmount(BigDecimal.ZERO);
        report.setNetSettlementSign(SIGN_CREDIT);

        report.setCreditTrxCount(0);
        report.setDebitTrxCount(0);
        report.setCreditTrxAmount(BigDecimal.ZERO);
        report.setDebitTrxAmount(BigDecimal.ZERO);

        report.setAccountCreditTrxCount(0);
        report.setAccountCreditTrxAmount(BigDecimal.ZERO);
        report.setWalletCreditTrxCount(0);
        report.setWalletCreditTrxAmount(BigDecimal.ZERO);

        report.setAccountDebitTrxCount(0);
        report.setAccountDebitTrxAmount(BigDecimal.ZERO);
        report.setWalletDebitTrxCount(0);
        report.setWalletDebitTrxAmount(BigDecimal.ZERO);

        report.setCenterFeeCount(0);
        report.setCenterFeeTotalAmount(BigDecimal.ZERO);
        report.setCreditInterFeeCount(0);
        report.setCreditInterFeeTotalAmount(BigDecimal.ZERO);
        report.setDebitInterFeeCount(0);
        report.setDebitInterFeeTotalAmount(BigDecimal.ZERO);

        report.setServiceFeeCount(0);
        report.setServiceFeeTotalAmount(BigDecimal.ZERO);
        report.setGrossSettlementAmount(BigDecimal.ZERO);
        report.setGrossSettlementSign(SIGN_CREDIT);
        report.setInterchangeFeeTotalAmount(BigDecimal.ZERO);
        report.setInterchangeFeeSign(SIGN_CREDIT);
    }

    private void processFees(GlobalReport report, Transaction tx, String institutionId) {
        if (tx.getFeeInfo() == null) return;

        for (TxFeeInfo fee : tx.getFeeInfo()) {
            if (!institutionId.equals(fee.getInstitutionReference())) continue;

            BigDecimal amount = fee.getFeeAmount();
            if (SIGN_CREDIT.equals(fee.getFeeSign())) {
                report.setCreditInterFeeCount(report.getCreditInterFeeCount() + 1);
                report.setCreditInterFeeTotalAmount(report.getCreditInterFeeTotalAmount().add(amount));
            } else if (SIGN_DEBIT.equals(fee.getFeeSign())) {
                report.setDebitInterFeeCount(report.getDebitInterFeeCount() + 1);
                report.setDebitInterFeeTotalAmount(report.getDebitInterFeeTotalAmount().add(amount));
            }
        }
    }

    private void calculateNetAmounts(GlobalReport report) {
        BigDecimal gross = report.getCreditTrxAmount().subtract(report.getDebitTrxAmount());
        report.setGrossSettlementAmount(gross.abs());
        report.setGrossSettlementSign(gross.compareTo(BigDecimal.ZERO) >= 0 ? SIGN_CREDIT : SIGN_DEBIT);

        BigDecimal credits = report.getCreditTrxAmount().add(report.getCreditInterFeeTotalAmount());
        BigDecimal debits = report.getDebitTrxAmount().add(report.getDebitInterFeeTotalAmount());
        BigDecimal net = credits.subtract(debits);

        report.setNetSettlementAmount(net.abs());
        report.setNetSettlementSign(net.compareTo(BigDecimal.ZERO) >= 0 ? SIGN_CREDIT : SIGN_DEBIT);

        BigDecimal interchange = report.getCreditInterFeeTotalAmount().subtract(report.getDebitInterFeeTotalAmount());
        report.setInterchangeFeeTotalAmount(interchange.abs());
        report.setInterchangeFeeSign(interchange.compareTo(BigDecimal.ZERO) >= 0 ? SIGN_CREDIT : SIGN_DEBIT);
    }

    private List<ReportByTrxType> buildReportByTrxType(
            List<Transaction> transactions,
            String institutionId,
            String currency,
            SettlementReport parent) {

        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionType))
                .entrySet().stream()
                .map(entry -> {
                    ReportByTrxType report = new ReportByTrxType();
                    initializeAbstractReport(report, currency);
                    report.setTransactionType(entry.getKey());
                    report.setSettlementReport(parent);
                    populateAbstractReport(report, entry.getValue(), institutionId);
                    return report;
                })
                .collect(Collectors.toList());
    }

    private void initializeAbstractReport(AbstractReport report, String currency) {
        report.setTransactionCurrency(currency);
        report.setTransactionCount(0);
        report.setTransactionTotalAmount(BigDecimal.ZERO);
        report.setTransactionTotalSign(SIGN_CREDIT);

        report.setCreditTrxCount(0);
        report.setCreditTrxTotalAmount(BigDecimal.ZERO);
        report.setDebitTrxCount(0);
        report.setDebitTrxTotalAmount(BigDecimal.ZERO);

        report.setCenterFeeCount(0);
        report.setCenterFeeTotalAmount(BigDecimal.ZERO);
        report.setCreditInterFeeCount(0);
        report.setCreditInterFeeTotalAmount(BigDecimal.ZERO);
        report.setDebitInterFeeCount(0);
        report.setDebitInterFeeTotalAmount(BigDecimal.ZERO);
    }

    private List<ReportByInstitution> buildReportByInstitution(
            List<Transaction> transactions,
            String institutionId,
            String currency,
            SettlementReport parent) {

        return transactions.stream()
                .collect(Collectors.groupingBy(tx -> getPeerInstitution(tx, institutionId)))
                .entrySet().stream()
                .map(entry -> {
                    ReportByInstitution report = new ReportByInstitution();
                    initializeAbstractReport(report, currency);
                    report.setPeerInstId(entry.getKey());
                    report.setSettlementReport(parent);
                    populateAbstractReport(report, entry.getValue(), institutionId);
                    return report;
                })
                .collect(Collectors.toList());
    }

    private String getPeerInstitution(Transaction tx, String institutionId) {
        if (institutionId.equals(tx.getDebitorInstitutionId())) {
            return tx.getCreditorInstitutionId();
        } else if (institutionId.equals(tx.getCreditorInstitutionId())) {
            return tx.getDebitorInstitutionId();
        }
        return "UNKNOWN";
    }

    private List<ReportByInstitutionAndTrxType> buildReportByInstAndTrxType(
            List<Transaction> transactions,
            String institutionId,
            String currency,
            SettlementReport parent) {

        Map<String, Map<String, List<Transaction>>> groupedData = transactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> getPeerInstitution(tx, institutionId),
                        Collectors.groupingBy(Transaction::getTransactionType)
                ));

        List<ReportByInstitutionAndTrxType> results = new ArrayList<>();
        groupedData.forEach((institution, typeMap) -> {
            typeMap.forEach((type, txs) -> {
                ReportByInstitutionAndTrxType report = new ReportByInstitutionAndTrxType();
                initializeAbstractReport(report, currency);
                report.setPeerInstId(institution);
                report.setTransactionType(type);
                report.setSettlementReport(parent);
                populateAbstractReport(report, txs, institutionId);
                results.add(report);
            });
        });

        return results;
    }

    private void populateAbstractReport(AbstractReport report,
                                        List<Transaction> transactions,
                                        String institutionId) {

        for (Transaction tx : transactions) {
            boolean isCreditor = institutionId.equals(tx.getCreditorInstitutionId());
            boolean isDebitor = institutionId.equals(tx.getDebitorInstitutionId());
            BigDecimal amount = tx.getTransactionAmount();

            report.setTransactionCount(report.getTransactionCount() + 1);

            if (isCreditor) {
                report.setCreditTrxCount(report.getCreditTrxCount() + 1);
                report.setCreditTrxTotalAmount(report.getCreditTrxTotalAmount().add(amount));
            }

            if (isDebitor) {
                report.setDebitTrxCount(report.getDebitTrxCount() + 1);
                report.setDebitTrxTotalAmount(report.getDebitTrxTotalAmount().add(amount));
            }

            if (tx.getFeeInfo() != null) {
                for (TxFeeInfo fee : tx.getFeeInfo()) {
                    if (!institutionId.equals(fee.getInstitutionReference())) continue;

                    BigDecimal feeAmount = fee.getFeeAmount();
                    if (SIGN_CREDIT.equals(fee.getFeeSign())) {
                        report.setCreditInterFeeCount(report.getCreditInterFeeCount() + 1);
                        report.setCreditInterFeeTotalAmount(report.getCreditInterFeeTotalAmount().add(feeAmount));
                    } else if (SIGN_DEBIT.equals(fee.getFeeSign())) {
                        report.setDebitInterFeeCount(report.getDebitInterFeeCount() + 1);
                        report.setDebitInterFeeTotalAmount(report.getDebitInterFeeTotalAmount().add(feeAmount));
                    }
                }
            }
        }

        BigDecimal netTx = report.getCreditTrxTotalAmount().subtract(report.getDebitTrxTotalAmount());
        report.setTransactionTotalAmount(netTx.abs());
        report.setTransactionTotalSign(netTx.compareTo(BigDecimal.ZERO) >= 0 ? SIGN_CREDIT : SIGN_DEBIT);
    }

    private List<ReportFeeInfo> buildFeeInfos(
            List<Transaction> transactions,
            String institutionId,
            SettlementReport parent) {

        return transactions.stream()
                .filter(tx -> tx.getFeeInfo() != null)
                .flatMap(tx -> tx.getFeeInfo().stream())
                .filter(fee -> institutionId.equals(fee.getInstitutionReference()))
                .map(fee -> {
                    ReportFeeInfo info = new ReportFeeInfo();
                    info.setFeeAmount(fee.getFeeAmount());
                    info.setFeeCode(fee.getFeeCode());
                    info.setFeeCurrency(fee.getFeeCurrency());
                    info.setFeeDescription(fee.getFeeDescription());
                    info.setFeeSign(fee.getFeeSign());
                    info.setFeeType(fee.getFeeType());
                    info.setSettlementReport(parent);
                    return info;
                })
                .collect(Collectors.toList());
    }

    private InstitutionReport generateInstitutionReport(
            List<Transaction> transactions,
            SettlementReport settlementReport) {

        InstitutionReport report = new InstitutionReport();
        report.setInstitutionId(settlementReport.getInstitutionId());
        report.setSessionId(settlementReport.getSessionId());

        GlobalReport global = settlementReport.getGlobalReport();
        report.setNetSettlementCurrency(global.getNetSettlementCurrency());
        report.setNetSettlementAmount(global.getNetSettlementAmount());
        report.setNetSettlementSign(global.getNetSettlementSign());

        report.setTrxReport(buildTransactionReports(transactions, settlementReport.getInstitutionId()));
        report.setFeeReport(buildInstitutionFeeInfos(transactions, settlementReport.getInstitutionId()));

        return report;
    }

    private List<TransactionReport> buildTransactionReports(
            List<Transaction> transactions,
            String institutionId) {

        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionType))
                .entrySet().stream()
                .map(entry -> {
                    TransactionReport report = new TransactionReport();
                    // Initialisation des valeurs
                    report.setTransactionType(entry.getKey());
                    report.setTransactionCurrency(entry.getValue().get(0).getTransactionCurrency());
                    report.setCreditTrxCount(0);
                    report.setCreditTrxTotalAmount(BigDecimal.ZERO);
                    report.setDebitTrxCount(0);
                    report.setDebitTrxTotalAmount(BigDecimal.ZERO);

                    for (Transaction tx : entry.getValue()) {
                        BigDecimal amount = tx.getTransactionAmount();
                        if (institutionId.equals(tx.getCreditorInstitutionId())) {
                            report.setCreditTrxCount(report.getCreditTrxCount() + 1);
                            report.setCreditTrxTotalAmount(report.getCreditTrxTotalAmount().add(amount));
                        } else if (institutionId.equals(tx.getDebitorInstitutionId())) {
                            report.setDebitTrxCount(report.getDebitTrxCount() + 1);
                            report.setDebitTrxTotalAmount(report.getDebitTrxTotalAmount().add(amount));
                        }
                    }

                    return report;
                })
                .collect(Collectors.toList());
    }

    private List<ReportFeeInfo> buildInstitutionFeeInfos(
            List<Transaction> transactions,
            String institutionId) {

        return transactions.stream()
                .filter(tx -> tx.getFeeInfo() != null)
                .flatMap(tx -> tx.getFeeInfo().stream())
                .filter(fee -> institutionId.equals(fee.getInstitutionReference()))
                .map(fee -> {
                    ReportFeeInfo info = new ReportFeeInfo();
                    info.setFeeAmount(fee.getFeeAmount());
                    info.setFeeCode(fee.getFeeCode());
                    info.setFeeCurrency(fee.getFeeCurrency());
                    info.setFeeDescription(fee.getFeeDescription());
                    info.setFeeSign(fee.getFeeSign());
                    info.setFeeType(fee.getFeeType());
                    return info;
                })
                .collect(Collectors.toList());
    }
}