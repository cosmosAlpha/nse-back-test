package com.arghya.backtest.controller;

import com.arghya.backtest.model.ChartInkBackTestCsv;
import com.arghya.backtest.model.StockAnalysisResultData;
import com.arghya.backtest.model.StockData;
import com.arghya.backtest.service.StockRefinementService;
import com.arghya.backtest.service.StockService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StockController {

	private final StockService stockService;

	private final StockRefinementService stockRefinementService;

	@GetMapping("/")
	public String home(Model model) {
		return "index";
	}

	@GetMapping("/back-test")
	public String backTest(Model model) {
		return "back-test";
	}

	@GetMapping("/refine-stocks")
	public String refine(Model model) {
		return "refine-stocks";
	}

	@PostMapping("/run-back-test")
	public String uploadCSVFile(@RequestParam("chartInkBackTestCsv") MultipartFile file,
			@RequestParam("numOfDays") int numOfDays, @RequestParam("targetPercent") int targetPercent, Model model) {
		StopWatch watch = new StopWatch();
		watch.start();
		long recordProcessed = 0;
		if (file.isEmpty()) {
			model.addAttribute("message", "Please select a CSV file to upload.");
			model.addAttribute("status", false);
			watch.stop();
			log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
					recordProcessed);
		}
		else {

			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
				CsvToBean<ChartInkBackTestCsv> csvToBean = new CsvToBeanBuilder(reader)
					.withType(ChartInkBackTestCsv.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

				List<ChartInkBackTestCsv> backTestData = csvToBean.parse();
				recordProcessed = backTestData.size();
				List<StockAnalysisResultData> stockAnalysisResultData = stockService.getAnalysisResults(backTestData,
						numOfDays, targetPercent);
				model.addAttribute("data", stockAnalysisResultData);
				List<StockAnalysisResultData> stockAnalysisResultDataTargetAchieved = stockAnalysisResultData.stream()
					.filter(StockAnalysisResultData::isTargetAchieved)
					.toList();
				model.addAttribute("accuracy",
						(stockAnalysisResultDataTargetAchieved.size() * 100) / stockAnalysisResultData.size());
				watch.stop();
				log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
						recordProcessed);
				model.addAttribute("info", String.format("Time taken for calculation is : %s seconds for %s records",
						watch.getTotalTimeSeconds(), recordProcessed));
				model.addAttribute("fileName", file.getOriginalFilename());
				model.addAttribute("days", numOfDays);
				model.addAttribute("targetPercent", targetPercent);
				return "back-test-result";
			}
			catch (Exception ex) {
				model.addAttribute("message", "An error occurred while processing the CSV file.");
				model.addAttribute("status", false);
				ex.printStackTrace();
				watch.stop();
				log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
						recordProcessed);
			}
		}
		return "back-test";

	}

	@PostMapping("/run-refinement")
	public String runRefinement(@RequestParam("chartInkBackTestCsv") MultipartFile file,
			@RequestParam("date") LocalDate date, Model model) {
		StopWatch watch = new StopWatch();
		watch.start();
		long recordProcessed = 0;
		if (file.isEmpty()) {
			model.addAttribute("message", "Please select a CSV file to upload.");
			model.addAttribute("status", false);
			watch.stop();
			log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
					recordProcessed);
		}
		else {

			try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
				CsvToBean<ChartInkBackTestCsv> csvToBean = new CsvToBeanBuilder(reader)
					.withType(ChartInkBackTestCsv.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

				List<ChartInkBackTestCsv> backTestData = csvToBean.parse();
				// backTestData.removeAll(backTestData.stream().filter(v -> v.))
				recordProcessed = backTestData.size();
				List<StockAnalysisResultData> stockAnalysisResultData = stockRefinementService
					.getAnalysisResults(backTestData, date);
				model.addAttribute("data", stockAnalysisResultData);
				watch.stop();
				log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
						recordProcessed);
				model.addAttribute("info", String.format("Time taken for calculation is : %s seconds for %s records",
						watch.getTotalTimeSeconds(), recordProcessed));
				model.addAttribute("fileName", file.getOriginalFilename());
				return "back-test-result";
			}
			catch (Exception ex) {
				model.addAttribute("message", "An error occurred while processing the CSV file.");
				model.addAttribute("status", false);
				ex.printStackTrace();
				watch.stop();
				log.info("Time taken for calculation is : {} seconds for {} records", watch.getTotalTimeSeconds(),
						recordProcessed);
			}
		}
		return "back-test";

	}

}
