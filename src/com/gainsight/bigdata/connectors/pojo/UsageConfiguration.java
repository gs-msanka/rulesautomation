package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class UsageConfiguration {
	@JsonProperty("configType")
	String configType = "ACCOUNTLEVEL";
	@JsonProperty("frequency")
	String frequency = "WEEKLY";
	@JsonProperty("weekType")
	String weekType = "START";
	@JsonProperty("day")
	String day = "MONDAY";

	public void config(String weekType, String day) {
		this.weekType = weekType;
		this.day = day;
	}
}
