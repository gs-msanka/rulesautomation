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
	String actionType = "RUN_NOW";

	public void save(String startDate, String endDate) {
		this.actionType = "SAVE";
		this.type = "RUN_NOW";
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void saveAndRun(String startDate, String endDate) {

		this.actionType = "RUN_NOW";
		this.type = "RUN_NOW";
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/* Don't user this service. This require some more parameters */
	public void schedule(String startDate, String endDate) {
		this.actionType = "SAVE";
		this.type = "SCHEDULER";
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/* Don't user this service. This require some more parameters */
	public void scheduleAndRun(String startDate, String endDate) {
		this.actionType = "RUN_NOW";
		this.type = "SCHEDULER";
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
