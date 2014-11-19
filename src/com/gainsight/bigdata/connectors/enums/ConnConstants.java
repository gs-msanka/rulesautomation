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
		SUM("SUM"), AVG("AVG"), MAX("MAX"),	MIN("MIN"), COUNT("COUNT"), DIST_COUNT("DISTINCT_COUNT");

		String aggType;

		private AggType(String aggType) {
			this.aggType = aggType;
		}

		public String getAggType() {
			return aggType;
		}
	}
}
