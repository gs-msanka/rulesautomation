package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.By;

import com.gainsight.sfdc.pages.BasePage;

public class AdminCustomer360Section extends BasePage{
	
	private final String READY_INDICATOR        = "//div[@class='apexp']//table[contains(@id,'CS360Section')]";
	private final String EDIT_LINK_SPONSOR	    = "//a[contains(@onclick,'SponsorTracking')]";
	private final String EDIT_SPONSOR_TRACKING  = "//div[@id='FrameForEditing']//iframe";
	private final String ENABLE_CHECKBOX		= "//span[contains(@class,'checkbox')]";
	private final String CHECKED_CHECKBOX		= "//span[@class,'checkbox-active']";
	private final String UNCHECKED_CHECKBOX		= "//span[@class,'checkbox-normal']";
	private final String SAVE_SPONSOR_TRACKING  = "//a[@class='btn-save saveSummary']"; 
	private final String EDIT_SPONSOR_TRACKING_IFRAME  = "//iframe[contains(@src,'sponsortracking')]";
	
	public AdminCustomer360Section() {
        Log.info("Admin Customer 360 Section Page Loading");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public void EditSponsorTracking(){
		Log.info("Click on Edit link in Customer 360 Section");
		item.click(EDIT_LINK_SPONSOR);
		amtDateUtil.stalePause();		
	}
	
	public void EnableSponsorTracking(){
		if(item.isElementPresent(EDIT_SPONSOR_TRACKING)){
			driver=driver.switchTo().frame(driver.findElement(By.xpath(EDIT_SPONSOR_TRACKING_IFRAME)));
			wait.waitTillElementPresent(ENABLE_CHECKBOX, 3, 10);
			/*if(!(field.isSelected(ENABLE_CHECKBOX)))
				item.click(ENABLE_CHECKBOX);
			item.click(SAVE_SPONSOR_TRACKING);*/
			//Why above lines didn't work: driver.findelements(by.xpath or id..whatever) If element is not present, throws Exception.
			try{
				if(field.isElementPresent(UNCHECKED_CHECKBOX))
					item.click(UNCHECKED_CHECKBOX);
			}
			catch(Exception e){
				Log.info("Element is already Checked");
			}
				
			item.click(SAVE_SPONSOR_TRACKING);
			amtDateUtil.stalePause();
			driver=driver.switchTo().defaultContent();
			Log.info("Finished Admin Config...");
		}
		else
			Log.info("Sponsor Tracking Window to Enable is not Visible");
		
	}
	

}
