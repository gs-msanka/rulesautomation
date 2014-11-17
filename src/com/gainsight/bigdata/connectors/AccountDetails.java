package com.gainsight.bigdata.connectors;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.pojo.Scheduler;
import com.gainsight.bigdata.connectors.pojo.UsageConfiguration;

public class AccountDetails {
	@JsonProperty("globalMapping")
	GlobalMapping globalMapping;
	@JsonProperty("schedulerDetails")
	Scheduler scheduler;
	@JsonProperty("usageConfiguration")
	UsageConfiguration usageConfiguration;
	@JsonProperty("writeToSFDC")
	boolean writeToSFDC = false;
	@JsonProperty("properties")
	Map<String, String> properties;
	@JsonProperty("accountType")
	String accountType;
	@JsonProperty("displayName")
	String displayName;

	public AccountDetails() {
		globalMapping = new GlobalMapping();
		properties = new HashMap<String, String>();
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setProperties(String collectionId, String timeZone) {
		properties.put("collectionId", collectionId);
		properties.put("timeZone", timeZone);
	}

	public void setUsageConfig() {
		usageConfiguration = new UsageConfiguration();
	}

	public void addScheduler() {
		scheduler = new Scheduler();
		scheduler.schedule("RUN_NOW", "2014-11-10T00:00:00.000", "2014-11-14T00:00:00.000");
	}

}
