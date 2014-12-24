package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;

public class AdminCustomersTab extends BasePage {
	
	
	private final String NEW_CUSTOMERS        = "//input[@class='btn dummyAllAdminNewBtn']";
	private final String STAGE_TEXT           = "//span[contains(text(),'Stage')]";
	private final String STAGE_NAME           = "//span[contains(text(),'Stage')]/parent::h2/parent::div//following::div/input";
	private final String STAGE_DISPLAY_ORDER  = "//span[contains(text(),'Stage')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String STAGE_SYSTEM_NAME    = "//span[contains(text(),'Stage')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String STAGE_SHORT_NAME     = "//span[contains(text(),'Stage')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String ACTIVE               = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	private final String STAGE_SAVE           = "//span[contains(text(),'Stage')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String STAGE_CANCEL         = "//span[contains(text(),'Stage')]/parent::h2/parent::div//following::div/input[@value='Cancel']"; 
	
	private final String FROM_BLOCK           = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String FORM_NONE            = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	
	private final String REVENUE_CONFIG        = "//td[contains(@id,'revenueBandBlock')]";
	private final String CSM_CONFIG            = "//input[@class='btn csmFieldSettingsBtn']";
	private final String TABLE_VALUES_STAGE    = "j_id0:j_id14:j_id16:j_id26";
	private final String REVENUEBAND_CONFIG   = "//input[@class='btn dummyAllAdminNewBtn']";
	
	
	public AdminCustomersTab addNewStage(String name, String displayOrder, String systemName, String shortName, String active) {
		wait.waitTillElementPresent(NEW_CUSTOMERS, MIN_TIME, MAX_TIME);
		button.click(NEW_CUSTOMERS);
		fillFields( name, displayOrder, systemName, shortName, active); 
		return this;
	}
	public boolean isStagePresent(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(TABLE_VALUES_STAGE);
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	                                    //Edit Stage
	public AdminCustomersTab editStage(String s,String name, String displayOrder, String shortName ) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFields(name, displayOrder, shortName); 
		return this;
	}
		                  //Delete Stage
	public AdminCustomersTab deleteStage(String s) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;
	}
	
    public AdminCustomersTab revenueBandConfiguration() {
    	
    	button.click(REVENUE_CONFIG);
    	
    	return this;
    }
	
    
    public AdminCustomersTab doCSMConfigutaion() {
    	
    	button.click(CSM_CONFIG);
    	return this;
    }
                      //Common Method
    public AdminCustomersTab fillFields(String name, String displayOrder, String systemName, String shortName , String active) {
    	wait.waitTillElementDisplayed(FROM_BLOCK ,MIN_TIME , MAX_TIME);
		field.clearAndSetText(STAGE_NAME, name);
		field.clearAndSetText(STAGE_DISPLAY_ORDER, displayOrder);
		field.clearAndSetText(STAGE_SYSTEM_NAME, systemName);
		field.clearAndSetText(STAGE_SHORT_NAME, shortName);
		button.click(STAGE_SAVE);
		wait.waitTillElementPresent(FORM_NONE ,MIN_TIME , MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
    }
    public AdminCustomersTab fillFewFields(String name, String displayOrder, String shortName ) {
    	wait.waitTillElementDisplayed(FROM_BLOCK ,MIN_TIME , MAX_TIME);
		field.clearAndSetText(STAGE_NAME, name);
		field.clearAndSetText(STAGE_DISPLAY_ORDER, displayOrder);
		field.clearAndSetText(STAGE_SHORT_NAME, shortName);
		button.click(STAGE_SAVE);
		wait.waitTillElementPresent(FORM_NONE ,MIN_TIME , MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
    }
    
}

