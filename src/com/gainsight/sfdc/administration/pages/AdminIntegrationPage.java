package com.gainsight.sfdc.administration.pages;

import java.util.Iterator;
import java.util.Set;

import com.gainsight.sfdc.pages.BasePage;

public class AdminIntegrationPage extends BasePage {
	
	private static final String READY_INDICATOR="//a[contains(@text,'Gainsight Matrix Data Platform')]";
	private static final String SIDE_SECTION="//div[@class='form-group']";
	private static final String OAUTH_ENABLE="//div[@class='onoffswitch-switch']";
	private static final String AUTHORIZE="//button[@id='btnAuthorize']";
	private static final String AUTHORIZE_DISABLED = "//button[@id='btnAuthorize' and @disabled='disabled']";
	private static final String REVOKE="//button[@id='btnRevoke']";
	private static final String MARKETO_ENABLE="";
	private static final String GSEMAIL_ENABLE="//div[@class='gs-cta-head EMAIL_SERVICE']//div[@class='onoffswitch-switch']";
	private static final String GSEMAIL_ON="//button[@class='btnEnable gs-btn btn-add' and @apptype='EMAIL_SERVICE']";
	private static final String GSEMAIL_OFF="//button[@class='btnDisable gs-btn btn-cancel' and @action='DISABLE']";
	private static final String GSEMAIL_SECTION="//div[@class='data-title' and text()='Gainsight Email Service']";
	
	public void AdminIntegrationPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public void clickOnEnableGSMDP(){
		item.click(OAUTH_ENABLE);
		wait.waitTillElementDisplayed(SIDE_SECTION, MIN_TIME, MAX_TIME);
	}
	
	public void enableGSEmail(){
		item.click(GSEMAIL_ENABLE);
		wait.waitTillElementDisplayed(GSEMAIL_SECTION, MIN_TIME, MAX_TIME);
		item.click(GSEMAIL_ON);
		
	}
	public void clickOnAuthorize(){
		
		if(item.isElementPresent(AUTHORIZE_DISABLED))
			System.out.println("OAuth already enabled");
		else
		{
			item.click(AUTHORIZE);		
			Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
			Iterator<String> itererator = windowId.iterator();   
			String mainWinID = itererator.next();
			String oauthWinID = itererator.next();
			driver.switchTo().window(oauthWinID);
			System.out.println(driver.getTitle());
			wait.waitTillElementDisplayed("//h2[contains(text(),'dev_app')]", MIN_TIME, MAX_TIME);
			item.click("//input[@value=' Allow ']");
			if(item.isElementPresent("//p[contains(text(),'Authorization successful')]"))
			{
				item.click("//input[@value='Close']");
				driver.switchTo().window(mainWinID);
			}
			else{
				System.out.println("oAuth Failed!!!..check settings");
			}
		}
	}
	public void clickOnRevoke(){
		if(item.isElementPresent(AUTHORIZE_DISABLED))
			item.click(REVOKE);
		else
			System.out.println("Not yet authorized");
	}
}
