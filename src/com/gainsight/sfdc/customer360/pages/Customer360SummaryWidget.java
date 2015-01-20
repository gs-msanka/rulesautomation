package com.gainsight.sfdc.customer360.pages;

import org.openqa.selenium.By;
import com.gainsight.testdriver.Log;

public class Customer360SummaryWidget extends Customer360Page {
	
	
	private final String READY_INDICATOR       = "//div[@class='gs_summary']";
	

	public Customer360SummaryWidget() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

	}
	  	
	public boolean verifyWidgetPanel(String wName, String wValue) {
		Log.info("Validating  Widget Panel Verification");
		
	String label  = item.getText("//div[contains(@class,'gs-sum-widgethead') and contains(text(),'"+wName+"')]");
	System.out.println("Value of label:"+label);
	String value = item.getText("//div[contains(@class,'gs-sum-widgethead') and contains(text(),'"+wName+"')]/following-sibling::div/div");
	System.out.println("Value of label:"+value);
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
	
	String label  = item.getText("//span[contains(@class,'gs-label-name') and contains(text(),'"+lName+"')]");
	System.out.println("Value of label:"+label);
	String value = item.getText("//span[contains(@class,'gs-sum-value')and contains(text(),'"+lValue+"')]");
	System.out.println("Value of label:"+value);
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


	}



		
