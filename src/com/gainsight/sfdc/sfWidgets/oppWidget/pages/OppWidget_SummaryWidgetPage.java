package com.gainsight.sfdc.sfWidgets.oppWidget.pages;
import org.openqa.selenium.By;

import com.gainsight.testdriver.Log;

public class OppWidget_SummaryWidgetPage extends OppWidgetPage {


	private final String READY_INDICATOR  = "//div[@class='gs_summary']";
	private final String EDIT_BUTTON      = "//a[@class='GSEditSummary']";
	private final String STATUS           =  "//table[@class='summary-table']/tbody/tr/td/button/span[@class='ui-icon ui-icon-triangle-2-n-s']";          //table[@class='summary-table']/tbody/tr/td/button/span";
    private final String STAGE            = "//table[@class='summary-table']/tbody/tr/td/following::tr/td/button/span";
	private final String COMMENTS         = "//textarea[@class='summaryComment']";
	private final String SAVE             = "//a[@class='btn_save saveSummary']";
	private final String FORM_BLOCK       = "//div[contains(@class,'ui-widget ui-widget-content') and contains(@style,'display: block')]";
	private final String FORM_NONE        = "//div[contains(@class,'ui-widget ui-widget-content') and contains(@style,'display: none')]";
	private final String STATUS_DROP_DOWN = "//table[@class='summary-table']/tbody/tr/td/button/span";
	private final String STAGE_DROP_DOWN  = "//table[@class='summary-table']/tbody/tr/following::td/button/span";
	private final String WNAME_TEXT       = "//div[contains(@class,'gs-sum-widgethead') and contains(text(),'%s')]";
	private final String WVALUE_TEXT      = "//div[contains(@class,'gs-sum-widgethead') and contains(text(),'%s')]/following-sibling::div/div";
	private final String LNAME            = "//span[contains(@class,'gs-label-name') and contains(text(),'%s')]";
	private final String IVALUE           = "//span[contains(@class,'gs-sum-value')and contains(text(),'%s')]";
	
	public OppWidget_SummaryWidgetPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

	}
	  	
	public boolean verifyWidgetPanel(String wName, String wValue) {
		Log.info("Validating  Widget Panel Verification");
		String label	= String.format(WNAME_TEXT, wName);
		 item.getText(label);
		 
		 String value	= String.format(WVALUE_TEXT, wName);
		 item.getText(value);
		 
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
			} return true;
		}
		
   public boolean verifyLeftPanel(String lName, String lValue) {
	Log.info("Validating  Widget Panel Verification");
	String label = String.format(LNAME, lName);
	 item.getText(label);
	String value = String.format(IVALUE, lValue);
	item.getText(value);
			
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
			} return true;
	}

 
public boolean editSummary(String Status, String Stage, String Comments) {
	
	button.click(EDIT_BUTTON);
	wait.waitTillElementPresent(FORM_BLOCK, MIN_TIME , MAX_TIME);
	item.clearAndSetText(COMMENTS, Comments);
	item.click(STATUS_DROP_DOWN);
	item.click("//input[@title ='"+Status+"']//following-sibling::span[text()='"+Status+"']");
	item.click(STAGE_DROP_DOWN);
	item.click("//input[@title ='"+Stage+"']//following-sibling::span[text()='"+Stage+"']");
	button.click(SAVE);
	return true;
	
}


	}


	

