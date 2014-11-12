package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class Scheduler {
	@JsonProperty("type")
	String type = "RUN_NOW";
	@JsonProperty("startDate")
	String startDate;
	@JsonProperty("endDate")
	String endDate;
	@JsonProperty("weekType")
	String weekType = "START";
	@JsonProperty("day")
	String day = "MONDAY";

	public void schedule(String type, String startDate, String endDate) {
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
