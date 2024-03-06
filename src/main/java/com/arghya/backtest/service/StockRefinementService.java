package com.arghya.backtest.service;

import com.arghya.backtest.feign.client.StockEdgeClient;
import com.arghya.backtest.feign.model.stockedge.StockEdgeDelivery;
import com.arghya.backtest.feign.model.stockedge.StockEdgeFutureOptionInterest;
import com.arghya.backtest.feign.model.stockedge.StockEdgeSecurity;
import com.arghya.backtest.model.ChartInkBackTestCsv;
import com.arghya.backtest.model.StockAnalysisResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockRefinementService {

	private final StockEdgeClient stockEdgeClient;

	public List<StockAnalysisResultData> getAnalysisResults(List<ChartInkBackTestCsv> backTestData, LocalDate date) {
		List<StockAnalysisResultData> resultData = new ArrayList<>();
		backTestData.parallelStream().forEach(td -> {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate csvDate = LocalDate.parse(td.getDate().substring(0, 10), formatter);

				if (date.compareTo(csvDate) == 0) {
					List<StockEdgeSecurity> stockEdgeSecurities = stockEdgeClient.getMajorSecurities(td.getSymbol());
					log.debug("stockEdgeSecurities :: {}", stockEdgeSecurities);

					Long securityId = stockEdgeSecurities.get(0).getId();
					List<StockEdgeDelivery> stockEdgeDeliveries = stockEdgeClient
						.getAdjDeliveriesByTimeSpan(securityId);
					log.debug("stockEdgeDeliveries :: {}", stockEdgeDeliveries);

					StockEdgeSecurity stockEdgeSecurity = stockEdgeClient.getLatestSecurityInfo(securityId);
					log.debug("stockEdgeSecurity :: {}", stockEdgeSecurity);

					List<StockEdgeFutureOptionInterest> stockEdgeFutureOptionInterests = stockEdgeClient
						.getCumulativeFutureOpenInterest(stockEdgeSecurity.getDefaultListingID());
					log.debug("stockEdgeFutureOptionInterests :: {}", stockEdgeFutureOptionInterests);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		return resultData;
	}

}
