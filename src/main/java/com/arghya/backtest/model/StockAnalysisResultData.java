package com.arghya.backtest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockAnalysisResultData {

	private String symbol;

	private Date startDate;

	private Date endDate;

	private BigDecimal startDateClose;

	private BigDecimal highest;

	private BigDecimal lowest;

	private Long avgVolume;

	private BigDecimal percentUpDown;

	private boolean targetAchieved;

}
