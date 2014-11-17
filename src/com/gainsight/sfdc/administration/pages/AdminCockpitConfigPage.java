package com.gainsight.sfdc.administration.pages;


import java.util.Iterator;
import java.util.Set;

import com.gainsight.sfdc.pages.BasePage;


public class AdminCockpitConfigPage extends BasePage {
	private static final String READY_INDICATOR="//div[@class='gs-cockpit-heading']/span[contains(text(),'Cockpit Configuration')]";
	private final String EDIT_TASK_MAPPING_BUTTON="//div[@class='gs-cockpit-buttons']/a[@class='btn-apply']";
	private final String SAVE_TASK_MAPPING_BUTTON="//div[@class='gs-cockpit-buttons']/a[@class='btn-save']";
	private final String SAVE_TASK_MAPPING_BUTTON_AFTER_SAVING="//div[@class='gs-cockpit-buttons']/a[@class='btn-save' and @style='display: none;']";

	public void AdminIntegrationPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AdminCockpitConfigPage editAndSaveTaskMapping(){
		item.click(EDIT_TASK_MAPPING_BUTTON);
		item.click(SAVE_TASK_MAPPING_BUTTON);
		wait.waitTillElementDisplayed(SAVE_TASK_MAPPING_BUTTON_AFTER_SAVING, MIN_TIME, MAX_TIME);
		return this;
	}
}
