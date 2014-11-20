package com.gainsight.bigdata.connectors.enums;

public class ConnConstants {
	public static enum Events {
		LOGIN("Logged in"), LOGOUT("Logged out"), RUN_REPORT("Run Report"), SAVE_REPORT("Report Save"),
		SAVE_CONFIG("Save Config"), CHROME("Chrome"), OPERA("Opera"), FIREFOX("Fireforx");

		String event;

		private Events(String event) {
			this.event = event;
		}

		public String getEvent() {
			return event;
		}
	}

	public static enum AggType {
		SUM("SUM"), AVG("AVG"), MAX("MAX"), MIN("MIN"), COUNT("COUNT"), DIST_COUNT("DISTINCT_COUNT");

		String aggType;

		private AggType(String aggType) {
			this.aggType = aggType;
		}

		public String getAggType() {
			return aggType;
		}
	}

	public static enum TrackerData {
		ALL_USERS("ALL_USERS"), ALL_EVENTS("ALL_EVENTS"),
		ACCOUNT_USAGE("ACCOUNT_USAGE"), EVENTS_BY_ACCOUNT("EVENTS_BY_ACCOUNT");
		String dataType;

		private TrackerData(String dataType) {
			this.dataType = dataType;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

	}
}
