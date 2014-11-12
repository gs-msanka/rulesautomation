package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

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

	public UsageTracker(String usageTrackerType, String accountId) {
		this();
		this.usageTrackerType = usageTrackerType;
		this.accountId = accountId;
	}

	public String getUsageTrackerType() {
		return usageTrackerType;
	}

	public void setUsageTrackerType(String usageTrackerType) {
		this.usageTrackerType = usageTrackerType;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

}
