package com.arghya.backtest.feign.client;

import com.arghya.backtest.feign.model.CandleStickResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "upstoxClient", url = "${feign.upstoxClient.url}", path = "/v2/historical-candle")
public interface UpStoxClient {

	@GetMapping(value = "/{instrumentKey}/{interval}/{toDate}/{fromDate}")
	CandleStickResponse getCandleStickData(@PathVariable("instrumentKey") String instrumentKey,
			@PathVariable("interval") String interval, @PathVariable("toDate") String toDate,
			@PathVariable("fromDate") String fromDate);

}
