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

package ma.s2m.nxp.regulation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ma.s2m.nxp.regulation.enums.NettingStatus;
import ma.s2m.nxp.regulation.enums.NettingType;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "SETTLEMENT_NETTING")
@ToString
public class SettlementNetting {

	private static final String SETTLEMENT_NETTING_SEQ = "SETTLEMENT_NETTING_SEQ";

	/** id. pk */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SETTLEMENT_NETTING_SEQ)
	@SequenceGenerator(name = SETTLEMENT_NETTING_SEQ, sequenceName = SETTLEMENT_NETTING_SEQ, allocationSize = 1)
	private Long id;

	@Column(name = "NETT_TRX_STATUS")
	@Enumerated(EnumType.STRING)
	private NettingStatus transactionNettingStatus;

	@Column(name = "NETT_FEE_STATUS")
	@Enumerated(EnumType.STRING)
	private NettingStatus feeNettingStatus;

	@Column(name = "NETT_TRX_POSTING_INDICATOR")
	private Boolean transactionPostingIndicator;

	@Column(name = "NETT_FEE_POSTING_INDICATOR")
	private Boolean feePostingIndicator;

	@Column(name = "NETT_TRX_POSTING_CUM_COUNTER")
	private Integer transactionPostingCumulCounter;

	@Column(name = "NETT_FEE_POSTING_CUM_COUNTER")
	private Integer feePostingCumulCounter;

	@Column(name = "NETT_CURR")
	private String currency;

	@Column(name = "NETT_LAST_TRX_POSTING_DATE")
	private Date lastTransactionPostingDate;

	@Column(name = "NETT_LAST_FEE_POSTING_DATE")
	private Date lastFeePostingDate;

	@Lob
	@Column(name = "RTGS_TRX_REQ")
	private String rtgsTrxRequest;

	@Lob
	@Column(name = "RTGS_TRX_RESP")
	private String rtgsTrxResponse;

	@Column(name = "RTGS_TRX_RESP_STTS")
	private String rtgsTrxResponseStatus;

	@Lob
	@Column(name = "RTGS_FEE_REQ")
	private String rtgsFeeRequest;

	@Lob
	@Column(name = "RTGS_FEE_RESP")
	private String rtgsFeeResponse;

	@Column(name = "RTGS_FEE_RESP_STTS")
	private String rtgsFeeResponseStatus;

	@Column(name = "IS_NETT_FEE_CLOSING", nullable = false)
	private Boolean isNettingFeeClosing;

	@JsonIgnore
	@ToString.Exclude
	@JoinColumn(name = "SETTLEMENT_ID")
	@ManyToOne(cascade = CascadeType.ALL)
	private Settlement settlement;

}
