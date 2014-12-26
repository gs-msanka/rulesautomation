package com.gainsight.bigdata.connectors;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.pojo.Scheduler;
import com.gainsight.bigdata.connectors.pojo.UsageConfiguration;
import com.gainsight.sfdc.util.DateUtil;

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

	public GlobalMapping getGlobalMapping() {
		return globalMapping;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setProperties(String collectionId, String timeZone) {
		properties.put("collectionId", collectionId);
		properties.put("timeZone", timeZone);
	}

	public void setDefaultUsageConfig() {
		usageConfiguration = new UsageConfiguration();
	}

	public void setDefaultScheduler() {
		scheduler = new Scheduler();
		String startDate = DateUtil.addDays(Calendar.getInstance(), -10, "yyyy-MM-dd") + "T00:00:00.000";
		String endDate = DateUtil.addDays(Calendar.getInstance(), 0, "yyyy-MM-dd") + "T00:00:00.000";
		scheduler.saveAndRun(startDate, endDate);
	}
}
