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

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "REPORT_BY_INSTITUTION")
public class ReportByInstitution extends AbstractReport {

	@Column(name = "RECEIV_INST_ID")
	private String peerInstId;

	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ManyToOne
	private SettlementReport settlementReport;

}
