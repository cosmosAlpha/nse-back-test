package com.arghya.backtest.service;

import com.arghya.backtest.feign.client.UpStoxClient;
import com.arghya.backtest.feign.model.CandleStickResponse;
import com.arghya.backtest.model.ChartInkBackTestCsv;
import com.arghya.backtest.model.StockAnalysisResultData;
import com.arghya.backtest.model.StockData;
import com.arghya.backtest.model.UpstoxInstrument;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

	private final UpStoxClient upStoxClient;

	public List<StockAnalysisResultData> getAnalysisResults(List<ChartInkBackTestCsv> backTestData, int numOfDays,
			int targetPercent) throws ParseException {
		List<UpstoxInstrument> upstoxInstruments = getUpstoxInstrumentMap();
		List<StockAnalysisResultData> stockAnalysisResultData = new ArrayList<>();
		backTestData.parallelStream().forEach(td -> {
			try {
				var upstoxInstrumentKey = upstoxInstruments.stream()
					.filter(i -> td.getSymbol().equals(i.getTradingSymbol()))
					.findFirst()
					.map(UpstoxInstrument::getInstrumentKey)
					.get();
				String toDate = null;
				try {
					toDate = getDateString(DateUtils.addDays(getSimpleDate(td.getDate().substring(0, 10)), numOfDays));
				}
				catch (ParseException e) {
					throw new RuntimeException(e);
				}
				String fromDate = null;
				try {
					fromDate = getDateString(DateUtils.addDays(getSimpleDate(td.getDate().substring(0, 10)), 1));
				}
				catch (ParseException e) {
					throw new RuntimeException(e);
				}
				CandleStickResponse cr = upStoxClient.getCandleStickData(upstoxInstrumentKey, "day", toDate, fromDate);
				log.info("CandelStick data for {}, upstox key {} : {}", td.getSymbol(), upstoxInstrumentKey, cr);
				if (!cr.getData().getCandles().isEmpty()) {
					stockAnalysisResultData.add(analyzeStockData(cr, td, targetPercent));
				}
				log.info("Analysis completed for {} records", stockAnalysisResultData.size());
			}
			catch (Exception e) {
				log.info("Exception during analysis {}", e);
			}
		});
		return stockAnalysisResultData;
	}

	public List<UpstoxInstrument> getUpstoxInstrumentMap() {
		try (InputStream in = new ClassPathResource("upstox-instruments-nse.csv").getInputStream();
				Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));) {
			CsvToBean<UpstoxInstrument> csvToBean = new CsvToBeanBuilder(reader).withType(UpstoxInstrument.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build();
			return csvToBean.parse();
		}
		catch (Exception ex) {

		}
		return new ArrayList<>();
	}

	private String getDateString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	private Date getSimpleDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		return df.parse(date);
	}

	private Date getDateString(String date) {
		ZonedDateTime zdt = ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		return Date.from(zdt.toInstant());
	}

	private StockAnalysisResultData analyzeStockData(CandleStickResponse candleStickResponse, ChartInkBackTestCsv td,
			int targetPercent) {
		List<List<String>> candles = candleStickResponse.getData().getCandles();
		List<StockData> stockData = candles.stream()
			.map(values -> StockData.builder()
				.symbol(td.getSymbol())
				.date(getDateString(values.get(0).toString()))
				.open(new BigDecimal(values.get(1).toString()))
				.high(new BigDecimal(values.get(2).toString()))
				.low(new BigDecimal(values.get(3).toString()))
				.close(new BigDecimal(values.get(4).toString()))
				.volume(Long.parseLong(values.get(5).toString()))
				.build())
			.toList();
		if (stockData.size() > 0) {
			BigDecimal highClose = stockData.stream()
				.map(StockData::getClose)
				.max(Comparator.naturalOrder())
				.orElse(BigDecimal.ZERO);
			BigDecimal tradingDayClose = stockData.get(stockData.size() - 1).getClose();
			BigDecimal percentUpDown = (highClose.subtract(tradingDayClose)).multiply(BigDecimal.valueOf(100))
				.divide(tradingDayClose, RoundingMode.HALF_UP);
			return StockAnalysisResultData.builder()
				.symbol(td.getSymbol())
				.avgVolume((long) stockData.stream().mapToLong(StockData::getVolume).average().getAsDouble())
				.lowest(stockData.stream()
					.map(StockData::getClose)
					.min(Comparator.naturalOrder())
					.orElse(BigDecimal.ZERO))
				.highest(highClose)
				.percentUpDown(percentUpDown)
				.targetAchieved(percentUpDown.compareTo(BigDecimal.valueOf(targetPercent)) > 0 ? true : false)
				.startDate(stockData.get(stockData.size() - 1).getDate())
				.startDateClose(stockData.get(stockData.size() - 1).getClose())
				.endDate(stockData.get(0).getDate())
				.build();
		}
		return StockAnalysisResultData.builder().build();
	}

}
