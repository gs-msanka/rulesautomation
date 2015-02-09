package com.gainsight.sfdc.customer360.pages;

import org.openqa.selenium.By;

import com.gainsight.testdriver.Log;

public class Customer360SummaryWidget extends Customer360Page {
	
	
	private final String READY_INDICATOR  = "//div[@class='gs_summary']";
	private final String EDIT_BUTTON      = "//a[@class='GSEditSummary']";
	private final String STATUS           =  "//table[@class='summary-table']/tbody/tr/td/button/span[@class='ui-icon ui-icon-triangle-2-n-s']";          //table[@class='summary-table']/tbody/tr/td/button/span";
    private final String STAGE            = "//table[@class='summary-table']/tbody/tr/td/following::tr/td/button/span";
	private final String COMMENTS         = "//textarea[@class='summaryComment']";
	private final String SAVE             = "//a[@class='btn_save saveSummary']";
	private final String FORM_BLOCK       = "//div[contains(@class,'ui-widget ui-widget-content') and contains(@style,'display: block')]";
	private final String FORM_NONE        = "//div[contains(@class,'ui-widget ui-widget-content') and contains(@style,'display: none')]";
	
	public Customer360SummaryWidget() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

	}
	  	
	public boolean verifyWidgetPanel(String wName, String wValue) {
		Log.info("Validating  Widget Panel Verification");

        //TODO - Please declare the locator's globally & use String.format(); to build xpath.
	String label  = item.getText("//div[contains(@class,'gs-sum-widgethead') and contains(text(),'"+wName+"')]");
	System.out.println("Value of label:"+label);
        //TODO - Please declare the locator's globally & use String.format(); to build xpath.
	String value = item.getText("//div[contains(@class,'gs-sum-widgethead') and contains(text(),'"+wName+"')]/following-sibling::div/div");
	System.out.println("Value of label:"+value);
        //TODO - Please declare the locator's globally & use String.format(); to build xpath.
		if(isElementPresentAndDisplay(By.xpath("//div[contains(@class,'gs-sum-widgethead') and contains(text(),'"+wName+"')]"))) {
			
			if(label.equals(wName) && value.equals(wValue)){
				Log.info("Widget and Value is correct");
			}else if(label.equals(wName) || value.equals(wValue)) {
				Log.info("Widget is Present but value is not correct");
			}else if(label.equals(wName)&& value.equals(wValue)){
			   Log.info("stage is correct and Value is not correct");
		   }
		 }else {
			Log.info("Widget is Present but value is not matching");
			Log.info("expected value is:"+wValue+" but actual value present here is"+ value );
			} 
		return true;
		}
		


public boolean verifyLeftPanel(String lName, String lValue) {
	Log.info("Validating  Widget Panel Verification");
    //TODO - Please declare the locator's globally & use String.format(); to build xpath.
	String label  = item.getText("//span[contains(@class,'gs-label-name') and contains(text(),'"+lName+"')]");
	
    //TODO - Please declare the locator's globally & use String.format(); to build xpath.
	String value = item.getText("//span[contains(@class,'gs-sum-value')and contains(text(),'"+lValue+"')]");

    //TODO - Please declare the locator's globally & use String.format(); to build xpath.
	if(isElementPresentAndDisplay(By.xpath("//span[contains(@class,'gs-label-name') and contains(text(),'"+lName+"')]"))) {
		if(label.equals(lName) && value.equals(lValue)){
			Log.info("Widget and Value is correct");
		}else if(label.equals(lName) || value.equals(lValue)) {
			Log.info("Widget is Present but value is not correct");
		}else if(label.equals(lName)&& value.equals(lValue)){
		   Log.info("stage is correct and Value is not correct");
	   }
	 }else {
		Log.info("Widget is Present but value is not matching");
		Log.info("expected value is:"+lValue+" but actual value present here is"+ value );
		} 
	return true;
	}

 
public boolean editSummary(String Status, String Stage, String Comments) {
	
	button.click(EDIT_BUTTON);
	wait.waitTillElementPresent(FORM_BLOCK, MIN_TIME , MAX_TIME);
	String text = item.getText("//span[contains(@class,'ui-dialog-title')]");
	item.clearAndSetText(COMMENTS, Comments);
	item.click("//table[@class='summary-table']/tbody/tr/td/button/span");
	item.click("//input[@title ='"+Status+"']//following-sibling::span[text()='"+Status+"']");
	item.click("//table[@class='summary-table']/tbody/tr/following::td/button/span");
	item.click("//input[@title ='"+Stage+"']//following-sibling::span[text()='"+Stage+"']");
	
	button.click(SAVE);
	return true;
	
}


	}



		
