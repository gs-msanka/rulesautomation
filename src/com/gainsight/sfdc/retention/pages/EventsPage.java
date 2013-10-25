package com.gainsight.sfdc.retention.pages;

public class EventsPage extends RetentionBasePage {

    private final String READY_INDICATOR = "calhomeBtn";
	public EventsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

}
