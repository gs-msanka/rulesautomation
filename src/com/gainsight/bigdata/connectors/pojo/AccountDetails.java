package com.gainsight.bigdata.connectors.pojo;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;

public class AccountDetails {

	@JsonProperty("usageConfiguration")
	UsageConfiguration usageConfiguration;
	@JsonProperty("schedulerDetails")
	SchedulerDetails schedulerDetails;
	@JsonProperty("globalMapping")
	JsonNode globalMapping;
	@JsonProperty("writeToSFDC")
	boolean writeToSFDC;

	public AccountDetails() {
		usageConfiguration = new UsageConfiguration();
		schedulerDetails = new SchedulerDetails();
		globalMapping = null;
		writeToSFDC = false;
	}

	public UsageConfiguration getUsageConfiguration() {
		return usageConfiguration;
	}

	public void setUsageConfiguration(UsageConfiguration usageConfiguration) {
		this.usageConfiguration = usageConfiguration;
	}

	public SchedulerDetails getSchedulerDetails() {
		return schedulerDetails;
	}

	public void setSchedulerDetails(SchedulerDetails schedulerDetails) {
		this.schedulerDetails = schedulerDetails;
	}

	public JsonNode getGlobalMapping() {
		return globalMapping;
	}

	public void setGlobalMapping(JsonNode globalMapping) {
		this.globalMapping = globalMapping;
	}

	public class UsageConfiguration {
		@JsonProperty("weekType")
		String weekType;
		@JsonProperty("day")
		String day;
		@JsonProperty("frequency")
		String frequency;
		@JsonProperty("configType")
		String configType;

		public UsageConfiguration() {
			weekType = "START";
			day = "MONDAY";
			frequency = "MONTHLY";
			configType = "ACCOUNTLEVEL";
		}

		public String getWeekType() {
			return weekType;
		}

		public void setWeekType(String weekType) {
			this.weekType = weekType;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public String getConfigType() {
			return configType;
		}

		public void setConfigType(String configType) {
			this.configType = configType;
		}

	}

	public class SchedulerDetails {
		@JsonProperty("type")
		String type;
		@JsonProperty("startDate")
		String startDate;
		@JsonProperty("endDate")
		String endDate;
		@JsonProperty("weekType")
		String weekType;
		@JsonProperty("day")
		String day;

		SchedulerDetails() {
			type = "RUN_NOW";
			startDate = "2014-10-08T00:00:00.000";
			endDate = "2014-10-14T00:00:00.000";
			weekType = "START";
			day = "MONDAY";
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getWeekType() {
			return weekType;
		}

		public void setWeekType(String weekType) {
			this.weekType = weekType;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

	}
}
