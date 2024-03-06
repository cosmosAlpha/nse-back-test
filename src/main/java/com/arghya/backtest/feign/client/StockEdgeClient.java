package com.arghya.backtest.feign.client;

import com.arghya.backtest.config.FeignConfig;
import com.arghya.backtest.feign.model.CandleStickResponse;
import com.arghya.backtest.feign.model.stockedge.StockEdgeDelivery;
import com.arghya.backtest.feign.model.stockedge.StockEdgeFutureOptionInterest;
import com.arghya.backtest.feign.model.stockedge.StockEdgeSecurity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "stockEdgeClient", url = "${feign.stockEdgeClient.url}", path = "/api",
		configuration = FeignConfig.class)
public interface StockEdgeClient {

	@GetMapping(value = "/SecurityDashboardApi/GetMajorSecurities?term={symbol}&page=1&pageSize=10&lang=en")
	List<StockEdgeSecurity> getMajorSecurities(@PathVariable("symbol") String symbol);

	@GetMapping(value = "/SecurityDashboardApi/GetLatestSecurityInfo/{ID}?lang=en")
	StockEdgeSecurity getLatestSecurityInfo(@PathVariable("ID") Long id);

	@GetMapping(value = "/SecurityDashboardApi/GetAdjDeliveriesByTimeSpan/{ID}?timeSpan=1&lang=en")
	List<StockEdgeDelivery> getAdjDeliveriesByTimeSpan(@PathVariable("ID") Long id);

	@GetMapping(value = "/ListingDashboardApi/GetCumulativeFutureOpenInterest/{ID}?lang=en")
	List<StockEdgeFutureOptionInterest> getCumulativeFutureOpenInterest(@PathVariable("ID") Long id);

}
