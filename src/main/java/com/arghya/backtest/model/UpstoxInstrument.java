package com.arghya.backtest.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpstoxInstrument {

	@CsvBindByName(column = "instrument_key")
	private String instrumentKey;

	@CsvBindByName(column = "tradingsymbol")
	private String tradingSymbol;

}
