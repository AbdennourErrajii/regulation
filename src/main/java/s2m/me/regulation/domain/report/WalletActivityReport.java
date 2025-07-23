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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import s2m.me.regulation.domain.report.InstitutionReport;
import s2m.me.regulation.domain.report.SettlementReport;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "W_ACTIVITY_REPORT",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "REP_CURR", "SESSION_ID" }) })
public class WalletActivityReport {

	/** Transaction Entity Sequence Name. */
	private static final String W_ACTIVITY_REPORT_SEQ = "W_ACTIVITY_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = W_ACTIVITY_REPORT_SEQ)
	@SequenceGenerator(name = W_ACTIVITY_REPORT_SEQ, sequenceName = W_ACTIVITY_REPORT_SEQ, allocationSize = 1)
	private Long id;

	/** The center id. */
	@Column(name = "CENTER_ID")
	private String centerId;

	@Column(name = "REP_CURR")
	private String activityReportCurrency;

	/** The cut off id. */
	@Column(name = "SESSION_ID")
	private String sessionId;

	/** The cut off date. */
	@Column(name = "SESSION_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date sessionDate;

	/** The institution report. */
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "W_ACTIVITY_REPORT_ID")
	private List<InstitutionReport> institutionReport;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "W_ACTIVITY_REPORT_ID")
	private List<SettlementReport> reports;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "SETTLEMENT_ID")
	private Settlement settlement;

	/** The additional data in. */
	// private AdditionalDataDTO[] additionalDataIn;

}
