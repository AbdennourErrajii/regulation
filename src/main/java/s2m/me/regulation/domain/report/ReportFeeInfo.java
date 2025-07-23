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
import s2m.me.regulation.domain.FeeInfo;

import java.math.BigDecimal;

/**
 * The Class FeeInfo.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "REPORT_FEE_INFO")
public class ReportFeeInfo extends FeeInfo {

	/** FeeInfo Entity Sequence Name. */
	private static final String FEE_INFO_SEQ = "REPORT_FEE_INFO_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FEE_INFO_SEQ)
	@SequenceGenerator(name = FEE_INFO_SEQ, sequenceName = FEE_INFO_SEQ, allocationSize = 1)
	private Long id;

	/** The fee amount. */
	@Column(name = "FEE_AMOUNT")
	private BigDecimal feeAmount;

	/** The fee code. */
	@Column(name = "FEE_CODE")
	private String feeCode;

	/** The fee currency. */
	@Column(name = "FEE_CURRENCY")
	private String feeCurrency;

	/** The fee description. */
	@Column(name = "FEE_DESC")
	private String feeDescription;

	/** The fee sign. */
	@Column(name = "FEE_SIGN")
	private String feeSign;

	/** The fee type. */
	@Column(name = "FEE_TYPE")
	//@Enumerated
	//private FeeType feeType;
	private String feeType;

	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ManyToOne
	private SettlementReport settlementReport;

}
