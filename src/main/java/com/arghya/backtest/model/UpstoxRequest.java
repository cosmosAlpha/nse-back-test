package com.arghya.backtest.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpstoxRequest {

	private String instrumentKey;

	private String toDate;

	private String fromDate;

}
