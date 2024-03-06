package com.arghya.backtest.feign.model.stockedge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEdgeDelivery {

	@JsonProperty("DtFrom")
	private String DtFrom;

	@JsonProperty("DtTo")
	private String DtTo;

	@JsonProperty("ShortName")
	private String ShortName;

	@JsonProperty("Year")
	private Long Year;

	@JsonProperty("Name")
	private String Name;

	@JsonProperty("DQ")
	private BigDecimal DQ;

	@JsonProperty("DG")
	private BigDecimal DG;

	@JsonProperty("C")
	private BigDecimal C;

	@JsonProperty("PrevC")
	private BigDecimal PrevC;

	@JsonProperty("TQ")
	private BigDecimal TQ;

	@JsonProperty("CZG")
	private BigDecimal CZG;

	@JsonProperty("VWAP")
	private BigDecimal VWAP;

}
