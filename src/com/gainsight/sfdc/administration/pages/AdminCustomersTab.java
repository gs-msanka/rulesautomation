package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminCustomersTab extends BasePage {
	
	
	private final String NEW_CUSTOMERS        = "//input[@class='btn dummyAllAdminNewBtn']";
	private final String STAGE_TEXT           =  "//span[contains(text(),'Stage')]";
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
	//private final String REVENUEBAND_CONFIG   = "//input[@class='btn dummyAllAdminNewBtn']";
	//private final String CSM_CONFIG           = "//input[@class='btn csmFieldSettingsBtn']";
	
	public AdminCustomersTab addNewStage(String name, String displayorder, String systemName, String shortName, String active) {
		Report.logInfo("*************In addStage********");
		wait.waitTillElementPresent(NEW_CUSTOMERS, MIN_TIME, MAX_TIME);
		button.click(NEW_CUSTOMERS);
		fillFields( name, displayorder, systemName, shortName, active); 
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
	public AdminCustomersTab editStage(String s,String name, String displayorder, String shortName ) {
		Report.logInfo("*************In EditStage********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFields(name, displayorder, shortName); 
		return this;
	}
	public boolean isEditStagePresent(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(TABLE_VALUES_STAGE);
		System.out.println("***Tabel2 values:***" +ATtable2 );
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		System.out.println("***table2 attribute id:***" + table);
		if(a != -1) {
			result = true;
		} return result;
	}
	                  //Delete Stage
	public AdminCustomersTab deleteStage(String s) {
		Report.logInfo("*************In DeleteStage********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
			System.out.println("Unable to delete the element");
		}
		else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Alert Type:**********");
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
    public AdminCustomersTab fillFields(String name, String displayorder, String systemName, String shortName , String active) {
    	wait.waitTillElementDisplayed(FROM_BLOCK ,MIN_TIME , MAX_TIME);
    	if(item.isElementPresent(STAGE_TEXT)) {
		field.clearAndSetText(STAGE_NAME, name);
		field.clearAndSetText(STAGE_DISPLAY_ORDER, displayorder);
		field.clearAndSetText(STAGE_SYSTEM_NAME, systemName);
		field.clearAndSetText(STAGE_SHORT_NAME, shortName);
		item.click(ACTIVE);
		button.click(STAGE_SAVE);
		wait.waitTillElementPresent(FORM_NONE ,MIN_TIME , MAX_TIME);
		refreshPage();
		//wait.waitTillElementPresent("//span[contains(text(),'"+STAGE_NAME+"')]", MIN_TIME, MAX_TIME);
    	} else {
    		System.out.println("This is not an Stage light box:This is "+ item.getText("//span[@class='DialogTitleClass']")+"Window");
			button.click(STAGE_CANCEL);
    	}
		return this;
    }
    public AdminCustomersTab fillFewFields(String name, String displayorder, String shortName ) {
    	wait.waitTillElementDisplayed(FROM_BLOCK ,MIN_TIME , MAX_TIME);
    	if(item.isElementPresent(STAGE_TEXT)) {
		field.clearAndSetText(STAGE_NAME, name);
		field.clearAndSetText(STAGE_DISPLAY_ORDER, displayorder);
		field.clearAndSetText(STAGE_SHORT_NAME, shortName);
		button.click(STAGE_SAVE);
		wait.waitTillElementPresent(FORM_NONE ,MIN_TIME , MAX_TIME);
		refreshPage();
		//wait.waitTillElementPresent("//span[contains(text(),'"+STAGE_NAME+"')]", MIN_TIME, MAX_TIME);
    	} else {
    		System.out.println("This is not an Stage light box:This is "+ item.getText("//span[@class='DialogTitleClass']")+"Window");
			button.click(STAGE_CANCEL);
    	}
		return this;
    }
    
}

