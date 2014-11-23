package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.ConnConstants;

public class UsageTracker {
	@JsonProperty("usageTrackerType")
	String usageTrackerType;
	@JsonProperty("frequency")
	String frequency;
	@JsonProperty("accountId")
	String accountId;
	@JsonProperty("numberOfDays")
	int numberOfDays;

	public UsageTracker() {
		frequency = "DAY";
		numberOfDays = 7;
	}

	public UsageTracker(ConnConstants.TrackerData usageTrackerType, String accountId) {
		this();
		this.usageTrackerType = usageTrackerType.getDataType();
		this.accountId = accountId;
	}
}
