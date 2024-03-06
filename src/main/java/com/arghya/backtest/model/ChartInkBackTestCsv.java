package com.arghya.backtest.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartInkBackTestCsv {

	@CsvBindByName
	private String date;

	@CsvBindByName
	private String symbol;

	@CsvBindByName
	private String marketcapname;

	@CsvBindByName
	private String sector;

}
