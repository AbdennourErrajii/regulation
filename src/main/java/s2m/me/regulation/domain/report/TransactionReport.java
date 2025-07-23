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
package s2m.me.regulation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "TX_REPORT")
public class TransactionReport {

	/** The Constant TX_REPORT_SEQ. */
	private static final String TX_REPORT_SEQ = "TX_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TX_REPORT_SEQ)
	@SequenceGenerator(name = TX_REPORT_SEQ, sequenceName = TX_REPORT_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "TX_TYPE")
	private String transactionType;

	@Column(name = "TX_DESC")
	private String transactionDescription;

	@Column(name = "TX_CURR")
	private String transactionCurrency;

	@Column(name = "C_TX_COUNT")
	private Integer creditTrxCount;

	@Column(name = "C_TRX_TOTAL_AMOUNT")
	private BigDecimal creditTrxTotalAmount;

	@Column(name = "D_TX_COUNT")
	private Integer debitTrxCount;

	@Column(name = "D_TX_TOTAL_AMOUNT")
	private BigDecimal debitTrxTotalAmount;

}
