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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@ToString
public abstract class AbstractReport {

	/** Transaction Entity Sequence Name. */
	private static final String TRX_REPORT_SEQ = "TRX_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TRX_REPORT_SEQ)
	@SequenceGenerator(name = TRX_REPORT_SEQ, sequenceName = TRX_REPORT_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "TRX_COUNT")
	private Integer transactionCount;

	@Column(name = "TRX_TOTAL_AMOUNT")
	private BigDecimal transactionTotalAmount;

	@Column(name = "TRX_TOTAL_SIGN")
	private String transactionTotalSign;

	@Column(name = "TRX_CURR")
	private String transactionCurrency;

	@Column(name = "CREDIT_TRX_COUNT")
	private Integer creditTrxCount;

	@Column(name = "CREDIT_TRX_TOTAL_AMOUNT")
	private BigDecimal creditTrxTotalAmount;

	@Column(name = "D_TRX_COUNT")
	private Integer debitTrxCount;

	@Column(name = "C_TRX_TOTAL_AMOUNT")
	private BigDecimal debitTrxTotalAmount;

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

}
