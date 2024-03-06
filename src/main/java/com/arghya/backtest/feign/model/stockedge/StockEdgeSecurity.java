package com.arghya.backtest.feign.model.stockedge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEdgeSecurity {

	@JsonProperty("ID")
	private Long id;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("IsMyStock")
	private boolean isMyStock;

	@JsonProperty("Slug")
	private String slug;

	@JsonProperty("DefaultListingID")
	private Long defaultListingID;

}
