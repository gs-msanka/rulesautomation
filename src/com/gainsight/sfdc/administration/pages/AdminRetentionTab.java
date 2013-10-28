package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.pages.BasePage;

public class AdminRetentionTab extends BasePage {
	
	private final String READY_INDICATOR      = "//div[@id='Administration-Retention']/descendant::input[@class='btn dummyAllAdminNewBtn']";
	private final String NEW_ALERT_TYPE       = "//div[@id='Administration-Retention']/descendant::input[@class='btn dummyAllAdminNewBtn']";
	private final String ALERT_TYPE_NAME      = "//span[contains(text(),'Alert Type')]/parent::h2/parent::div//following::div/input";   //input[@class='jbaraDummyAdminInputCtrl subjectInput nameInputClass']";
	private final String ATYPE_DISPLAY_ORDER  = "//span[contains(text(),'Alert Type')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String ATYPE_SYSTEM_NAME    = "//span[contains(text(),'Alert Type')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";    //input[@class='jbaraDummyAdminInputCtrl subjectInput systemNameInputClass']";
	private final String ATYPE_SHORT_NAME     = "//span[contains(text(),'Alert Type')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String INCLUDE_IN_WIDGET    = "//input[@class='jbaraDummyAdminCheckboxCtrl checkboxSystemDefinedInput']";
	private final String ACTIVE               = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	private final String SAVE                 = "//input[@class='btn dummyAllAdminSaveBtn']";
	
	private final String ALERT_SEVERITY_NAME   = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input";
	private final String SEVERITY_DISPLAYORDER = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String SEVERITY_SYSNAME      = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String SEVERITY_SHOTNAME     = "//span[contains(text(),'Alert Severity')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String SEVERITY_SAVE         = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String SEVERITY_CANCEL       = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	
	private final String ALERT_REASON_NAME    = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input";
	private final String AREASON_DISPLAYORDER = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String AREASON_SYSNAME      = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String AREASON_SHOTNAME     = "//span[contains(text(),'Alert Reason')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String AREASON_SAVE         = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[@value='Save']";
	
	private final String ALERT_STATUS_NAME    = "//span[contains(text(),'Alert Status')]/parent::h2/parent::div//following::div/input";
	private final String ASTATUS_DISPLAYORDER = "//span[contains(text(),'Alert Status')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String ASTATUS_SYSNAME      = "//span[contains(text(),'Alert Status')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String ASTATUS_SHOTNAME     = "//span[contains(text(),'Alert Status')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String ASTATUS_SAVE         = "//span[contains(text(),'Alert Status')]/parent::h2/parent::div//following::div/input[@value='Save']";
	
	private final String EVENT_NAME            = "//span[contains(text(),'Event Type')]/parent::h2/parent::div//following::div/input";
	private final String EVENT_DISPLAYORDER    = "//span[contains(text(),'Event Type')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String EVENT_SYSNAME         = "//span[contains(text(),'Event Type')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String EVENT_SHOTNAME        = "//span[contains(text(),'Event Type')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String EVENT_SAVE            = "//span[contains(text(),'Event Type')]/parent::h2/parent::div//following::div/input[@value='Save']";    
	
	private final String EVENT_TABLE_VALUES      = "j_id0:j_id14:j_id314:j_id324";
	private final String ASTATUS_TABLE_VLAUES    = "j_id0:j_id14:j_id285:j_id295";
	private final String ASEVERITY_TABLE_VALUES  = "j_id0:j_id14:j_id240:j_id250";
	private final String AREASON_TABLE_VALUES    = "j_id0:j_id14:j_id263:j_id273";
	private final String ALERT_TYPE_VALUES       = "j_id0:j_id14:j_id217:j_id227";
	
	private final String NEW_ALERT_SEVERITY      = "//h2[text()='Alert Severity']/parent::td/following-sibling::td/div/input[@value='New']";
	private final String NEW_ALERT_REASON        = "//h2[text()='Alert Reason']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String NEW_ALERT_STATUS        = "//h2[text()='Alert Status']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String NEW_EVENT_TYPE          = "//h2[text()='Events Type']/parent::td/following-sibling::td/div/input[@Value='New']";
	private final String EVENT_ACTIVE_CBOX       = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	
	private final String CONFIGURE_BUTTON      = "//input[@class='btn dummyTaskConfigure']";
	private final String SAVE_CONFIG           = "//input[@class='btn btnSaveClick']";
	private final String CANCEL_CONFIG         = "//input[@class='btn btnCancelClick']";
	private final String SALESFORCE_TASKS      = "//input[@class='rbtnSFTask']";
	private final String GAINSIGHT_TASKS       = "//input[@class='rbtnCSTask']";
	

	public AdminRetentionTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	                                  //Create Alert Type
	public AdminRetentionTab createAlertType(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {
		Report.logInfo("************In the Create Alert type method**********");
		button.click(NEW_ALERT_TYPE);
		fillAllFileds(Name,displayorder,systemname,shortname, includeinWidget);
		return this;		
	}
	public boolean IsAlertTypePresent(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(ALERT_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} System.out.println("****Result:***"+ result);
		return result;
	}
                //--Edit Alert type
	public AdminRetentionTab editAlertType(String s,String Name,String displayorder,String shortname, String includeinWidget) {
		Report.logInfo("**********************In the Edit Alert type method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
		return this;
	}
	           //--Delete Alert Type
	public AdminRetentionTab deleteAlertType(String s) {
        Report.logInfo("***********In the Delete Alert type method*********");
        wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
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
	                        //Create Alert Severity
	public AdminRetentionTab createAlertSeverity(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {  
		Report.logInfo("************In the createAlertSeverity method**********");
		wait.waitTillElementPresent(NEW_ALERT_SEVERITY, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_SEVERITY);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Severity')]")) {
			field.clearAndSetText(ALERT_SEVERITY_NAME,Name);
			field.clearAndSetText(SEVERITY_DISPLAYORDER,displayorder);
			field.clearAndSetText(SEVERITY_SYSNAME,systemname);
			field.clearAndSetText(SEVERITY_SHOTNAME,shortname);
			field.selectCheckBox(INCLUDE_IN_WIDGET);
			button.click(SEVERITY_SAVE);
	        wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
	        refreshPage();
	        wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("This is not an alert Severity light box:This is "+ item.getText("//span[@class='DialogTitleClass']")+"Window");
			button.click(SEVERITY_CANCEL);
		}
		return this;
	}
	public boolean IsAlertSeverityPresent(String values){
		Boolean result = false;
		WebElement AStable2 =item.getElement(ASEVERITY_TABLE_VALUES);
		String tableId = AStable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} System.out.println("****Result:***"+ result);
		return result;
	}
	                                                 //Edit Alert Severity
	public AdminRetentionTab editAlertSeverity(String s,String Name,String displayorder,String shortname, String includeinWidget) {
		Report.logInfo("************In the edit createAlertSeverity method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Severity')]")) {
			field.clearAndSetText(ALERT_SEVERITY_NAME,Name);
			field.clearAndSetText(SEVERITY_DISPLAYORDER,displayorder);
			field.clearAndSetText(SEVERITY_SHOTNAME,shortname);
			button.click(SEVERITY_SAVE);
			wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			refreshPage();
		    wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		   
		} else {
			System.out.println("This is not an alert Severity light box.This is:"+ item.getText("//span[@class='DialogTitleClass']")+"Window");
		}
		return this;
	}
	                                          //Delete Alert Severity
	public AdminRetentionTab deleteAlertSeverity(String s) {
		Report.logInfo("************In the delete  createAlertSeverity method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
			System.out.println("Unable to delete the element");
		}
		else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Alert Severity:**********");
	return this;
	}
	                                 //Create Alert Reason
	public AdminRetentionTab createAlertReason(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the createAlertReason method**********");
		wait.waitTillElementPresent(NEW_ALERT_REASON, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_REASON);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Reason')]")) {
		field.clearAndSetText(ALERT_REASON_NAME,Name);
		field.clearAndSetText(AREASON_DISPLAYORDER,displayorder);
		field.clearAndSetText(AREASON_SYSNAME,systemname);
		field.clearAndSetText(AREASON_SHOTNAME,shortname);
		button.click(AREASON_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
        wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("The is not an alert Reason light box.This is:"+ item.getText("//span[@class='DialogTitleClass']") +"Window");
		} 
		return this;	
	}
	public boolean IsAlertReasonPresent(String values){
		Boolean result = false;
		WebElement ARtable2 =item.getElement(AREASON_TABLE_VALUES);
		String tableId = ARtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		System.out.println("****Result:***"+ result);
		return result;
	}
	                                  //Edit Alert Reason
	public AdminRetentionTab editAlertReason(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createAlertReason method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Reason')]")) {
			field.clearAndSetText(ALERT_REASON_NAME,Name);
			field.clearAndSetText(AREASON_DISPLAYORDER,displayorder);
			field.clearAndSetText(AREASON_SHOTNAME,shortname);
			button.click(AREASON_SAVE);
			wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			refreshPage();
		    wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		   
		} else {
			System.out.println("The is not an alert Reason light box to edit.This is:"+ item.getText("//span[@class='DialogTitleClass']") +"Window");
		}
	return this;	
	}              	     //Delete Alert Reason
	public AdminRetentionTab deleteAlertReason(String s) {
		Report.logInfo("************In the delete createAlertReason method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
			System.out.println("Unable to delete the element");
		} else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Alert Reason:**********");
		return this;
	}
	                         //Create Alert Status
	public AdminRetentionTab createAlertStatus(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the  createAlertStatus method**********");
		wait.waitTillElementPresent(NEW_ALERT_STATUS, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_STATUS);
		if(item.isElementPresent("//span[contains(text(),'Alert Status')]")) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(ALERT_STATUS_NAME,Name);
		field.clearAndSetText(ASTATUS_DISPLAYORDER,displayorder);
		field.clearAndSetText(ASTATUS_SYSNAME,systemname);
		field.clearAndSetText(ASTATUS_SHOTNAME,shortname);
		button.click(ASTATUS_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		 refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME); 
		} else {
				System.out.println("The is not an alert Status light box:"+ item.getText("//span[@class='DialogTitleClass']") +"Window");
			}
		return this;
	}	
	public boolean IsAlertStatusPresent(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(ASTATUS_TABLE_VLAUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		System.out.println("****Result:***"+ result);
		return result;
	}
	                      //Edit Alert Status
	public AdminRetentionTab editAlertStatus(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createAlertStatus method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Status')]")) {
			field.clearAndSetText(ALERT_STATUS_NAME,Name);
			field.clearAndSetText(ASTATUS_DISPLAYORDER,displayorder);
			field.clearAndSetText(ASTATUS_SHOTNAME,shortname);
			button.click(ASTATUS_SAVE);
			wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			refreshPage();
		    wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		   
		} else {
			System.out.println("The is not an alert Status light box.This is :"+ item.getText("//span[@class='DialogTitleClass']")+"Window");
		}
		
		return this;
	}
           //Retention:-- Delete Alert Status
	public AdminRetentionTab deleteAlertStatus(String s) {
		Report.logInfo("************In the delete  createAlertStatus method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
			System.out.println("Element is still present");
		}
		else {
			System.out.println("Element got deleted");
		}
		return this;
	}
                                   //Task Configuration
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
	    if(item.isElementPresent("//span[contains(text(),'Event Type')]")) {
	    wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(EVENT_NAME,Name);
		field.clearAndSetText(EVENT_DISPLAYORDER,displayorder);
		field.clearAndSetText(EVENT_SYSNAME,systemname);
		field.clearAndSetText(EVENT_SHOTNAME,shortname);
		button.click(EVENT_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME); 
	    }  else {
			System.out.println("The is not an event light box. This is:"+ item.getText("//span[@class='DialogTitleClass']")+"Light box");
		} return this;
	}
	public boolean IsEventTypePresent(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(EVENT_TABLE_VALUES);
		System.out.println("***Tabel2 values:***" +ASTtable2 );
		String tableId = ASTtable2.getAttribute("Id");
		System.out.println("***table2 attribute id:***" + table);
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
                                    //Edit Event Type
	public AdminRetentionTab editEventType(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("************In the edit createEventType method**********");
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		  if(item.isElementPresent("//span[contains(text(),'Event Type')]")) {
			  wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
			    field.clearAndSetText(EVENT_NAME,Name);
				field.clearAndSetText(EVENT_DISPLAYORDER,displayorder);
				field.clearAndSetText(EVENT_SHOTNAME,shortname);
				button.click(EVENT_SAVE);
				wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
				refreshPage();
			     wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
			   
		  } else {
			  System.out.println("The is not an event light box. This is:"+ item.getText("//span[@class='DialogTitleClass']")+"Light box");
		  }
		  
		return this;
	}
                            	       //  Delete Event Type
	public AdminRetentionTab deleteEventType(String s) {
		Report.logInfo("************In the delete createEventType method**********");
		//element.takeScreenShot("test.png");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
       wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
       refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
			System.out.println("Element not deleted");
		} else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Event Type:**********");
		return this;
	}
	             //Common Functionality to fill all the fields
	public AdminRetentionTab fillAllFileds(String Name,String displayorder,String systemname,String shortname, String includeinWidget) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Alert Type')]")) {
		field.clearAndSetText(ALERT_TYPE_NAME,Name);
		field.clearAndSetText(ATYPE_DISPLAY_ORDER,displayorder);
		field.clearAndSetText(ATYPE_SYSTEM_NAME,systemname);
		field.clearAndSetText(ATYPE_SHORT_NAME,shortname);
		field.selectCheckBox(INCLUDE_IN_WIDGET);
		button.click(SAVE);
        wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
        refreshPage();
        wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
        System.out.println("**********End of Create Alert Type:**********");  
		} else {
			System.out.println("This is not an alert Type light box:"+ item.getText("//span[@class='DialogTitleClass']"));
		}
		return this;
	}
	public AdminRetentionTab fillFewFileds(String Name,String displayorder,String shortname) {
		  wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
	if(item.isElementPresent("//span[contains(text(),'Alert Type')]")) {
		field.clearAndSetText(ALERT_TYPE_NAME,Name);
		field.clearAndSetText(ATYPE_DISPLAY_ORDER,displayorder);
		field.clearAndSetText(ATYPE_SHORT_NAME,shortname);
		//wait.waitTillElementPresent(SAVE, MIN_TIME, MAX_TIME);
		button.click(SAVE);	
		 wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		 refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		  } else {
				System.out.println("This is not an alert Type light box:"+ item.getText("//span[@class='DialogTitleClass']"));
			}
		return this;
	}
}

