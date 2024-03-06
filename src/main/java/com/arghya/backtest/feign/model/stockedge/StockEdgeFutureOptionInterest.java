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
public class StockEdgeFutureOptionInterest {

	@JsonProperty("Date")
	private String date;

	@JsonProperty("OpenInterest")
	private BigDecimal openInterest;

	@JsonProperty("Close")
	private BigDecimal close;

	@JsonProperty("PreviousClose")
	private BigDecimal previousClose;

	@JsonProperty("CloseChangePercentage")
	private BigDecimal closeChangePercentage;

	@JsonProperty("isExpiry")
	private boolean isExpiry;

}
