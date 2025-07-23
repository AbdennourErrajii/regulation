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

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "INST_REPORT")
public class InstitutionReport {

	/** Transaction Entity Sequence Name. */
	private static final String INST_REPORT_SEQ = "INST_REPORT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = INST_REPORT_SEQ)
	@SequenceGenerator(name = INST_REPORT_SEQ, sequenceName = INST_REPORT_SEQ, allocationSize = 1)
	private Long id;

	/** The institution id. */
	@Column(name = "INST_ID")
	private String institutionId;

	@Column(name = "SESSION_ID") // <- AJOUTER CE CHAMP
	private String sessionId;

	/** The trx report. */
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "INST_REPORT_ID")
	private List<TransactionReport> trxReport;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "INST_REPORT_ID")
	private List<ReportFeeInfo> feeReport;

	/** The net settlement amount. */
	@Column(name = "NET_SETTLEMENT_AMOUNT")
	private BigDecimal netSettlementAmount;

	/** The net settlement currency. */
	@Column(name = "NET_SETTLEMENT_CURR")
	private String netSettlementCurrency;

	/** The net settlement sign. */
	@Column(name = "NET_SETTLEMENT_SIGN")
	private String netSettlementSign;

	@JoinColumn(name = "W_ACTIVITY_REPORT_ID")
	@ManyToOne
	@ToString.Exclude
	private WalletActivityReport walletActivityReport;

	/** The additional data in. */
	// private AdditionalDataDTO[] additionalDataIn;

}
