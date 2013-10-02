package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminRetentionTab extends BasePage{
	
	private final String NEW_ALERT_TYPE       = "//div[@id='Administration-Retention']/descendant::input[@class='btn dummyAllAdminNewBtn']";
	private final String NAME                 = "//input[@class='jbaraDummyAdminInputCtrl subjectInput nameInputClass']";
	private final String DISPLAY_ORDER        = "//input[@class='jbaraDummyAdminInputCtrl DisplayOrderInput']";
	private final String SYSTEM_NAME          = "//input[@class='jbaraDummyAdminInputCtrl subjectInput systemNameInputClass']";
	private final String SHORT_NAME           = "//input[@class='jbaraDummyAdminInputCtrl subjectInput shortNameInputClass']";
	private final String INCLUDE_IN_WIDGET    = "//input[@class='jbaraDummyAdminCheckboxCtrl checkboxSystemDefinedInput']";
	private final String ACTIVE               = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	private final String SAVE                 = "//input[@class='btn dummyAllAdminSaveBtn']";
	
	private final String EVENT_TABLE_VALUES      = "j_id0:j_id14:j_id314:j_id324";
	private final String ASTATUS_TABLE_VLAUES    ="j_id0:j_id14:j_id285:j_id295";
	private final String ASEVERITY_TABLE_VALUES  ="j_id0:j_id14:j_id240:j_id250";
	private final String AREASON_TABLE_VALUES    ="j_id0:j_id14:j_id263:j_id273";
	
	private final String NEW_ALERT_SEVERITY      = "//h2[text()='Alert Severity']/parent::td/following-sibling::td/div/input[@value='New']";
	private final String NEW_ALERT_REASON        = "//h2[text()='Alert Reason']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String NEW_ALERT_STATUS        = "//h2[text()='Alert Status']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String NEW_EVENT_TYPE          = "//h2[text()='Events Type']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String EVENT_ACTIVE_CBOX       = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	
	private final String CONFIGURE_BUTTON      = "//input[@class='btn dummyTaskConfigure']";
	private final String SAVE_CONFIG           ="//input[@class='btn btnSaveClick']";
	private final String CANCEL_CONFIG         ="//input[@class='btn btnCancelClick']";
	private final String SALESFORCE_TASKS      = "//input[@class='rbtnSFTask']";
	private final String GAINSIGHT_TASKS       = "//input[@class='rbtnCSTask']";
	
	
	          //Admin:--Retention:--Create Alert Type
	public AdminRetentionTab createAlertType(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {
		Report.logInfo("************In the Create Alert type method**********");
		button.click(NEW_ALERT_TYPE);
		fillAllFileds(Name,displayorder,systemname,shortname, includeinWidget);
		sleep(6);
		System.out.println("**********End of Create Alert Type:**********");  
		return this;	
	}
	public boolean IsAlertTypePresent(String values){
		//WebElement table =	driver.findElement(By.xpath("TABLE_VALUES"));
		WebElement table =item.getElement("j_id0:j_id14:j_id217:j_id227");
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("**********rowText vlaues:**********" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("************************Values exists************************" +result);
			return result;
		}	
	
                //Admin:--Retention:--Edit Alert type
	public AdminRetentionTab editAlertType(String s,String Name,String displayorder,String shortname, String includeinWidget) {
		Report.logInfo("**********************In the Edit Alert type method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
			System.out.println("**********End of Edit Alert Type:**********"); 
		return this;
	}
	           //Admin:--Retention:--Delete Alert Type
	public AdminRetentionTab deleteAlertType(String s) {
        Report.logInfo("***********In the Delete Alert type method*********");
        wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		sleep(2);
		modal.accept();
		System.out.println("**********End of Delete Alert Type:**********");
		return this;
	}
	        //Admin:--Retention:--Create Alert Severity
	public AdminRetentionTab createAlertSeverity(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {  
		Report.logInfo("************In the createAlertSeverity method**********");
		wait.waitTillElementPresent(NEW_ALERT_SEVERITY, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_SEVERITY);
		fillAllFileds(Name,displayorder,systemname,shortname, includeinWidget);
		sleep(7);
		return this;
	}
	public boolean IsAlertSeverityPresent(String values) {
		WebElement table =item.getElement(ASEVERITY_TABLE_VALUES);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("**********rowText vlaues:**********" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("************************Values exists************************" +result);
			return result;
		}	
	         //Admin:--Retention:--Edit Alert Severity
	public AdminRetentionTab editAlertSeverity(String s,String Name,String displayorder,String shortname, String includeinWidget) {
		Report.logInfo("************In the edit createAlertSeverity method**********");
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
		return this;
	}
	
	       //Admin:--Retention:--Delete Alert Severity
	public AdminRetentionTab deleteAlertSeverity(String s) {
		Report.logInfo("************In the delete  createAlertSeverity method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		sleep(2);
		modal.accept();
		return this;
	}
	
	       //Admin:--Retention:--Create Alert Reason
	public AdminRetentionTab createAlertReason(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the createAlertReason method**********");
		wait.waitTillElementPresent(NEW_ALERT_REASON, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_REASON);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);
		sleep(7);
		return this;	
	}
	public boolean IsAlertReasonPresent(String values) {
		WebElement table =item.getElement(AREASON_TABLE_VALUES);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("**********rowText vlaues:**********" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("************************Values exists************************" +result);
			return result;
		}	
	
	        //Admin:--Retention:--Edit Alert Reason
	public AdminRetentionTab editAlertReason(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createAlertReason method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
	
	return this;	
	}
	     //Admin:--Retention:--Delete Alert Reason
	public AdminRetentionTab deleteAlertReason(String s) {
		Report.logInfo("************In the delete createAlertReason method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		return this;
	}
	
	   //Admin:--Retention:--Create Alert Status
	public AdminRetentionTab createAlertStatus(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the  createAlertStatus method**********");
		
		wait.waitTillElementPresent(NEW_ALERT_STATUS, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_STATUS);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);
		sleep(7);
		return this;
	}
	public boolean IsAlertStatusPresent(String values) {
		//WebElement table =	driver.findElement(By.xpath("TABLE_VALUES"));
		WebElement table =item.getElement(ASTATUS_TABLE_VLAUES);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("**********rowText vlaues:**********" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("************************Values exists************************" +result);
			return result;
		}	
	          //Admin:--Retention:--Edit Alert Status
	public AdminRetentionTab editAlertStatus(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createAlertStatus method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
		return this;
	}
           //Admin:--Retention:-- Delete Alert Status
	public AdminRetentionTab deleteAlertStatus(String s) {
		Report.logInfo("************In the delete  createAlertStatus method**********");
		
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		sleep(10);
		modal.accept();
		return this;
	}
	
            //Admin:--Retention:-- Task Configuration
	public AdminRetentionTab taskConfiguration()
	{
		button.click(CONFIGURE_BUTTON);
		wait.waitTillElementPresent(SALESFORCE_TASKS, MIN_TIME, MAX_TIME);
		item.click(SALESFORCE_TASKS);
		//item.click(GAINSIGHT_TASKS);		
		button.click(SAVE_CONFIG);
		//button.click(CANCEL_CONFIG);
		return this;
	}
                            //Create Event Type
	public AdminRetentionTab createEventType(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the  createEventType method**********");
	    button.click(NEW_EVENT_TYPE);
	    wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);
		wait.waitTillElementDisplayed("//td/span[contains(text(),'"+systemname+"')]", MIN_TIME, MAX_TIME);
		sleep(7);
		return this;
	}
	public boolean IsEventTypePresent(String values) {
		WebElement table =item.getElement(EVENT_TABLE_VALUES);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("rowText vlaues:" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        		result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("Values exists" +result);
			return result;
		}	
                    //Admin:--Retention:--Edit Event Type
	public AdminRetentionTab editEventType(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createEventType method**********");
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed(NAME, MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);
		//stalePause();
		//wait.waitTillElementDisplayed("//td/span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
	       //Admin:--Retention:-- Delete Event Type
	public AdminRetentionTab deleteEventType(String s) {
		Report.logInfo("************In the delete createEventType method**********");
		element.takeScreenShot("test.png");
		Report.logInfo("In Delete Event type");
		item.click("//a[contains(@title,'Administration')]");
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		sleep(2);
		modal.accept();
		return this;
	}
	             //Common Functionality to fill all the fields
	public AdminRetentionTab fillAllFileds(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {
		  wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);
		field.selectCheckBox(INCLUDE_IN_WIDGET);
		button.click(SAVE);
		return this;
	}
	public AdminRetentionTab fillFewFileds(String Name,String displayorder,String shortname) {
		  wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SHORT_NAME,shortname);
		wait.waitTillElementPresent(SAVE, MIN_TIME, MAX_TIME);
		button.click(SAVE);	
		return this;
	}
}

