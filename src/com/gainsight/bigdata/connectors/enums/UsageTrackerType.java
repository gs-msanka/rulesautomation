package com.gainsight.bigdata.connectors.enums;

public enum UsageTrackerType {
	ACTIVITY_TRACKER("ACTIVITY_TRACKER"),
	ALL_USERS("ALL_USERS"),
	ACCOUNT_USAGE("ACCOUNT_USAGE"),
	ALL_EVENTS("ALL_EVENTS"),
	EVENTS_BY_ACCOUNT("EVENTS_BY_ACCOUNT");

	private final String value;

	private UsageTrackerType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return ""+ value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
	
}
