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
import lombok.ToString;

import jakarta.persistence.*;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "REPORT_BY_INS_AND_TRX_TYPE")
@ToString(callSuper = true)
public class ReportByInstitutionAndTrxType extends AbstractReport{

	/** Transaction Entity Sequence Name. */
	private static final String TRX_REPORT_SEQ = "TRX_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TRX_REPORT_SEQ)
	@SequenceGenerator(name = TRX_REPORT_SEQ, sequenceName = TRX_REPORT_SEQ, allocationSize = 1)
	private Long id;


	@Column(name = "RECEIV_INST_ID")
	private String peerInstId;

	@Column(name = "TRX_TYPE")
	private String transactionType;

	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ManyToOne
	@ToString.Exclude
	private SettlementReport settlementReport;


}
