package com.gainsight.sfdc.retention.pages;

public class AlertsPage extends RetentionBasePage {
	private final String READY_INDICATOR = "//select[@class='jbaraDummyAlertUIViewsSelectControl']";
	
	public AlertsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
}
