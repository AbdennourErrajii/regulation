/*
  * Copyright S2M 2020-2021 the original author or authors.
  *
  * you may not use this file except in compliance with the S2M License.
  * You may obtain a copy of the License from S2M
  *
  *      https://www.s2mworldwide.com
  *
  * Auteur  : S2M
  * Contact : www.s2mworldwide.com
  *
  */
package s2m.me.regulation.domain.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "GLOBAL_REPORT")
public class GlobalReport {

	/** Transaction Entity Sequence Name. */
	private static final String GLOBAL_REPORT_SEQ = "GLOBAL_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GLOBAL_REPORT_SEQ)
	@SequenceGenerator(name = GLOBAL_REPORT_SEQ, sequenceName = GLOBAL_REPORT_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "TRX_COUNT")
	private Integer transactionCount;

	@Column(name = "NET_SETTLEMENT_AMOUNT")
	private BigDecimal netSettlementAmount;

	@Column(name = "NET_SETTLEMENT_SIGN")
	private String netSettlementSign;

	@Column(name = "NET_SETTLEMENT_CURR")
	private String netSettlementCurrency;

	@Column(name = "C_TRX_COUNT")
	private Integer creditTrxCount;

	@Column(name = "C_TRX_AMOUNT")
	private BigDecimal creditTrxAmount;

	@Column(name = "D_TRX_COUNT")
	private Integer debitTrxCount;

	@Column(name = "D_TRX_AMOUNT")
	private BigDecimal debitTrxAmount;

	@Column(name = "CENTER_FEE_COUNT")
	private Integer centerFeeCount;

	@Column(name = "CENTER_FEE_TOTAL_AMOUNT")
	private BigDecimal centerFeeTotalAmount;

	@Column(name = "C_INTER_FEE_COUNT")
	private Integer creditInterFeeCount;

	@Column(name = "C_INTER_FEE_TOTAL_AMOUNT")
	private BigDecimal creditInterFeeTotalAmount;

	@Column(name = "D_INTER_FEE_COUNT")
	private Integer debitInterFeeCount;

	@Column(name = "D_INTER_FEE_TOTAL_AMOUNT")
	private BigDecimal debitInterFeeTotalAmount;

	@Column(name = "SERVICE_FEE_COUNT")
	private Integer serviceFeeCount;

	@Column(name = "SERVICE_FEE_TOTAL_AMOUNT")
	private BigDecimal serviceFeeTotalAmount;

	@Column(name = "GROSS_TRANS_AMOUNT")
	private BigDecimal grossSettlementAmount;

	@Column(name = "GROSS_TRANS_SIGN")
	private String grossSettlementSign;

	@Column(name = "GROSS_INTER_FEE_AMOUNT")
	private BigDecimal interchangeFeeTotalAmount;

	@Column(name = "GROSS_INTER_FEE_SIGN")
	private String interchangeFeeSign;

	@Column(name = "ACC_DB_TRX_COUNT")
	private Integer accountDebitTrxCount;

	@Column(name = "ACC_DB_TRX_AMOUNT")
	private BigDecimal accountDebitTrxAmount;

	@Column(name = "ACC_CR_TRX_COUNT")
	private Integer accountCreditTrxCount;

	@Column(name = "ACC_CR_TRX_AMOUNT")
	private BigDecimal accountCreditTrxAmount;

	@Column(name = "WAL_DB_TRX_COUNT")
	private Integer walletDebitTrxCount;

	@Column(name = "WAL_DB_TRX_AMOUNT")
	private BigDecimal walletDebitTrxAmount;

	@Column(name = "WAL_CR_TRX_COUNT")
	private Integer walletCreditTrxCount;

	@Column(name = "WAL_CR_TRX_AMOUNT")
	private BigDecimal walletCreditTrxAmount;

}
