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
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;
import s2m.me.regulation.domain.report.WalletActivityReport;
import s2m.me.regulation.enums.SettlementStatus;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table
@ToString
public class Settlement {

	private static final String SETTLEMENT_SEQ = "SETTLEMENT_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SETTLEMENT_SEQ)
	@SequenceGenerator(name = SETTLEMENT_SEQ, sequenceName = SETTLEMENT_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "CENTER_ID")
	private String centerId;

	@Column(name = "EXEC_START_DATE")
	private Date execStartDate;

	@Column(name = "EXEC_END_DATE")
	private Date execEndDate;

	@Column(name = "GENERATION_DATE")
	private Date generationDate;

	@Column(name = "SESSION_ID", unique = true)
	private String sessionId;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private SettlementStatus status;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "SETTLEMENT_ID")
	private List<SettlementNetting> nettings;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "SETTLEMENT_ID")
	private List<WalletActivityReport> walletActivityReports;

}
