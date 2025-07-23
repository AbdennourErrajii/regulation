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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "SETTLEMENT_REPORT")
@ToString
public class SettlementReport {

	/** Transaction Entity Sequence Name. */
	private static final String SETTLEMENT_REPORT_SEQ = "SETTLEMENT_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SETTLEMENT_REPORT_SEQ)
	@SequenceGenerator(name = SETTLEMENT_REPORT_SEQ, sequenceName = SETTLEMENT_REPORT_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "INST_ID")
	private String institutionId;

	@Column(name = "SESSION_ID")
	private String sessionId;

	@Column(name = "SESSION_GENERATION_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date sessionDate;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ToString.Exclude
	private List<ReportFeeInfo> feeInfo;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ToString.Exclude
	private List<ReportByTrxType> reportByTrxType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	private List<ReportByInstitution> reportByInstitution;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "SETTLEMENT_REPORT_ID")
	@ToString.Exclude
	private List<ReportByInstitutionAndTrxType> reportByInstitutionAndTrxTypeResponse;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "GLOBAL_REPORT_ID")
	private GlobalReport globalReport;

	@Column(name = "SETTLEMENT_SIGN")
	private String settlementSignature;

	@ToString.Exclude
	@JoinColumn(name = "W_ACTIVITY_REPORT_ID")
	@ManyToOne(cascade = CascadeType.ALL)
	private WalletActivityReport walletActivityReport;

}
