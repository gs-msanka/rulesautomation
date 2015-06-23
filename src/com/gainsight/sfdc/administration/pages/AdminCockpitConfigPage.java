package com.gainsight.sfdc.administration.pages;


import java.util.HashMap;
import com.gainsight.sfdc.pages.BasePage;


public class AdminCockpitConfigPage extends BasePage {
	private static final String READY_INDICATOR="//div[@class='gs-cockpit-heading']/span[contains(text(),'Cockpit Configuration')]";
	private final String EDIT_TASK_MAPPING_BUTTON="//div[@class='gs-cockpit-priority-mapping gs-cockpit-task-priority-mapping']/descendant::a[@class='btn-apply']";
	private final String SAVE_TASK_MAPPING_BUTTON="//div[@class='gs-cockpit-priority-mapping gs-cockpit-task-priority-mapping']/descendant::a[@class='btn-save']";
	private final String CTA_ASSOCIATE_OBJECT    = "//select[contains(@class, 'assc-obj-multiselect')]/following-sibling::button";


	public void AdminIntegrationPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

    public AdminCockpitConfigPage enableAutoSync() {
        String xpath = "//div[contains(text(), 'Auto-sync task to SFDC:')]/following-sibling::span[@class='gs-sync-switch gs_switch gs_off']";
        item.click(xpath);
        waitTillNoLoadingIcon();
        return this;
    }
    
    public AdminCockpitConfigPage disableAutoSync() {
        String xpath = "//div[contains(text(), 'Auto-sync task to SFDC:')]/following-sibling::span[@class='gs-sync-switch gs_switch gs_on']";
        item.click(xpath);
        waitTillNoLoadingIcon();
        return this;
    }
    
    public void waitTillNoLoadingIcon() {
        env.setTimeout(5);
        wait.waitTillElementNotPresent("div[contains(@class, 'gs-loader-image')]", MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

	public AdminCockpitConfigPage editAndSaveTaskMapping(){
		item.click(EDIT_TASK_MAPPING_BUTTON);
		item.click(SAVE_TASK_MAPPING_BUTTON);
        waitTillNoLoadingIcon();
		wait.waitTillElementDisplayed(EDIT_TASK_MAPPING_BUTTON, MIN_TIME, MAX_TIME);
		return this;
	}
	
	public void CTAdetailViewConfiguration(HashMap<String, String> testData) {
		waitTillNoLoadingIcon();
		if (!item
				.isElementPresent("//input[contains(@class, 'gs-asscobj-fieldname') and @objname='"
						+ testData.get("AssociateObject") + "']")) {
			wait.waitTillElementDisplayed(CTA_ASSOCIATE_OBJECT, MIN_TIME,
					MAX_TIME);
			item.click(CTA_ASSOCIATE_OBJECT);
			selectValueInDropDown(testData.get("AssociateObject"));
			item.click("//input[contains(@class, 'btn_save saveSummary')]");
			waitTillNoLoadingIcon();
		}
	}
}
