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
	private final String ATYPE_FORM_BLOCK     = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String ATYPE_FORM_NONE      = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String ATYPE_TEXT_PRESENT   = "//span[contains(text(),'Alert Type')]";

	
	private final String ALERT_SEVERITY_NAME   = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input";
	private final String SEVERITY_DISPLAYORDER = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String SEVERITY_SYSNAME      = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String SEVERITY_SHOTNAME     = "//span[contains(text(),'Alert Severity')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String SEVERITY_SAVE         = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String SEVERITY_CANCEL       = "//span[contains(text(),'Alert Severity')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	private final String ASEVERITY_FORM_BLOCK  = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String ASEVERITY_FORM_NONE   = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String ASEVERITY_TEXT_PRESENT= "//span[contains(text(),'Alert Severity')]";
	
	private final String ALERT_REASON_NAME    = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input";
	private final String AREASON_DISPLAYORDER = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String AREASON_SYSNAME      = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String AREASON_SHOTNAME     = "//span[contains(text(),'Alert Reason')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String AREASON_SAVE         = "//span[contains(text(),'Alert Reason')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String AREASON_FORM_BLOCK   = "//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: block')]";
	private final String AREASON_FORM_NONE    = "//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: none')]";
	private final String AREASON_TEXT_PRESENT = "//span[contains(text(),'Alert Reason')]";
	
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
	private final String EVENT_FORM_BLOCK      = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String EVENT_FORM_NONE       = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String EVENT_TEXT_PRESENT    = "//span[contains(text(),'Event Type')]";
	
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
                     	/**
                             * The below Creates an Alert type,severity,Reason and Event type..   
                           */
	public AdminRetentionTab createAlertType(String name,String displayOrder,String systemName,String shortName, String includeinWidget) {
		button.click(NEW_ALERT_TYPE);
		fillAllFileds(name,displayOrder,systemName,shortName, includeinWidget);
		return this;		
	} 
	public boolean isAlertTypePresent(String values) {
		Boolean result = false;
		WebElement ATtable2 =item.getElement(ALERT_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		} return result;
	}
                                           //--Edit Alert type
	public AdminRetentionTab editAlertType(String s,String name,String displayOrder,String shortName, String includeinWidget) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(name,displayOrder,shortName);
		return this;
	}
	public boolean isAlertTypeEdited(String values) {
		Boolean result = false;
		WebElement ATtable2 =item.getElement(ALERT_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		} return result;
	}
	                              //--Delete Alert Type
	public AdminRetentionTab deleteAlertType(String s) {
        wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;
	}
	public boolean isAlertTypeDeleted(String values) {
		Boolean result = false;
		WebElement ATtable2 =item.getElement(ALERT_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		} return result;
	}
	                        //Create Alert Severity
	public AdminRetentionTab createAlertSeverity(String name,String displayOrder ,String systemName,String shortName, String includeinWidget) {  
		wait.waitTillElementPresent(NEW_ALERT_SEVERITY, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_SEVERITY);
		wait.waitTillElementDisplayed(ASEVERITY_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(ASEVERITY_TEXT_PRESENT);
			field.clearAndSetText(ALERT_SEVERITY_NAME,name);
			field.clearAndSetText(SEVERITY_DISPLAYORDER,displayOrder);
			field.clearAndSetText(SEVERITY_SYSNAME,systemName);
			field.clearAndSetText(SEVERITY_SHOTNAME,shortName);
			field.selectCheckBox(INCLUDE_IN_WIDGET);
			button.click(SEVERITY_SAVE);
	      wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
	        refreshPage();
	      wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
	public boolean isAlertSeverityPresent(String values){
		Boolean result = false;
		WebElement AStable2 =item.getElement(ASEVERITY_TABLE_VALUES);
		String tableId = AStable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} 
		return result;
	}
	                                                 //Edit Alert Severity
	public AdminRetentionTab editAlertSeverity(String s,String name, String displayOrder,String shortName, String includeinWidget) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
	  wait.waitTillElementDisplayed(ASEVERITY_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(ASEVERITY_TEXT_PRESENT);
			field.clearAndSetText(ALERT_SEVERITY_NAME,name);
			field.clearAndSetText(SEVERITY_DISPLAYORDER,displayOrder);
			field.clearAndSetText(SEVERITY_SHOTNAME,shortName);
			button.click(SEVERITY_SAVE);
	     wait.waitTillElementPresent(ASEVERITY_FORM_NONE, MIN_TIME, MAX_TIME);
		  refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
	public boolean isAlertSeverityEdited(String values){
		Boolean result = false;
		WebElement AStable2 =item.getElement(ASEVERITY_TABLE_VALUES);
		String tableId = AStable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} 
		return result;
	}
	                                          //Delete Alert Severity
	public AdminRetentionTab deleteAlertSeverity(String s) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
	return this;
	}
	public boolean isAlertSeverityDeleted(String values){
		Boolean result = false;
		WebElement AStable2 =item.getElement(ASEVERITY_TABLE_VALUES);
		String tableId = AStable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} 
		return result;
	}
	                        //Create Alert Reason
	public AdminRetentionTab createAlertReason(String name ,String displayOrder,String systemName,String shortName) {
		wait.waitTillElementPresent(NEW_ALERT_REASON, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_REASON);
		wait.waitTillElementDisplayed(AREASON_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(AREASON_TEXT_PRESENT);
		field.clearAndSetText(ALERT_REASON_NAME,name);
		field.clearAndSetText(AREASON_DISPLAYORDER,displayOrder);
		field.clearAndSetText(AREASON_SYSNAME,systemName);
		field.clearAndSetText(AREASON_SHOTNAME,shortName);
		button.click(AREASON_SAVE);
	wait.waitTillElementPresent(AREASON_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
      wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;	
	}
	public boolean isAlertReasonPresent(String values){
		Boolean result = false;
		WebElement ARtable2 =item.getElement(AREASON_TABLE_VALUES);
		String tableId = ARtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	                                  //Edit Alert Reason
	public AdminRetentionTab editAlertReason(String s,String name ,String displayOrder,String shortName) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed(AREASON_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(AREASON_TEXT_PRESENT);
			field.clearAndSetText(ALERT_REASON_NAME,name);
			field.clearAndSetText(AREASON_DISPLAYORDER,displayOrder);
			field.clearAndSetText(AREASON_SHOTNAME,shortName);
			button.click(AREASON_SAVE);
			wait.waitTillElementPresent(AREASON_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
		    wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
	return this;	
	}        
	public boolean isAlertReasonEdited(String values){
		Boolean result = false;
		WebElement ARtable2 =item.getElement(AREASON_TABLE_VALUES);
		String tableId = ARtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	                        //Delete Alert Reason
	public AdminRetentionTab deleteAlertReason(String s) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;
	}
	public boolean isAlertReasonDeleted(String values){
		Boolean result = false;
		WebElement ARtable2 =item.getElement(AREASON_TABLE_VALUES);
		String tableId = ARtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	                         //Create Alert Status
	public AdminRetentionTab createAlertStatus(String name ,String displayOrder,String systemName ,String shortName) {
		wait.waitTillElementPresent(NEW_ALERT_STATUS, MIN_TIME, MAX_TIME);
		button.click(NEW_ALERT_STATUS);
		item.isElementPresent("//span[contains(text(),'Alert Status')]");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(ALERT_STATUS_NAME,name);
		field.clearAndSetText(ASTATUS_DISPLAYORDER,displayOrder);
		field.clearAndSetText(ASTATUS_SYSNAME,systemName);
		field.clearAndSetText(ASTATUS_SHOTNAME,shortName);
		button.click(ASTATUS_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		 refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME); 
		return this;
	}	
	public boolean isAlertStatusPresent(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(ASTATUS_TABLE_VLAUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	                      //Edit Alert Status
	public AdminRetentionTab editAlertStatus(String s,String name,String displayOrder,String shortName) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		item.isElementPresent("//span[contains(text(),'Alert Status')]");
			field.clearAndSetText(ALERT_STATUS_NAME,name);
			field.clearAndSetText(ASTATUS_DISPLAYORDER,displayOrder);
			field.clearAndSetText(ASTATUS_SHOTNAME,shortName);
			button.click(ASTATUS_SAVE);
			wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			refreshPage();
		    wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		 return this;
	}
	public boolean isAlertStatusEdited(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(ASTATUS_TABLE_VLAUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
                                  //Delete Alert Status
	public AdminRetentionTab deleteAlertStatus(String s) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;	
	}
	public boolean isAlertStatusDeleted(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(ASTATUS_TABLE_VLAUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
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
	public AdminRetentionTab createEventType(String name ,String displayOrder,String systemName ,String shortName) {
	    button.click(NEW_EVENT_TYPE);
	    item.isElementPresent(EVENT_TEXT_PRESENT);
	    wait.waitTillElementDisplayed(EVENT_FORM_BLOCK, MIN_TIME, MAX_TIME);
		field.clearAndSetText(EVENT_NAME,name);
		field.clearAndSetText(EVENT_DISPLAYORDER,displayOrder);
		field.clearAndSetText(EVENT_SYSNAME,systemName);
		field.clearAndSetText(EVENT_SHOTNAME,shortName);
		button.click(EVENT_SAVE);
		wait.waitTillElementPresent(EVENT_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME); 
		 return this;
	}
	public boolean isEventTypePresent(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(EVENT_TABLE_VALUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
                                    //Edit Event Type
	public AdminRetentionTab editEventType(String s,String name,String displayOrder,String shortName) {
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		  item.isElementPresent(EVENT_TEXT_PRESENT);
			wait.waitTillElementDisplayed(EVENT_FORM_BLOCK, MIN_TIME, MAX_TIME);
			    field.clearAndSetText(EVENT_NAME,name);
				field.clearAndSetText(EVENT_DISPLAYORDER,displayOrder);
				field.clearAndSetText(EVENT_SHOTNAME,shortName);
				button.click(EVENT_SAVE);
			wait.waitTillElementPresent(EVENT_FORM_NONE, MIN_TIME, MAX_TIME);
				refreshPage();
			  wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
	public boolean isEventTypeEdited(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(EVENT_TABLE_VALUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
                            	       //  Delete Event Type
	public AdminRetentionTab deleteEventType(String s) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
       wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
       refreshPage();
		return this;
	}
	public boolean isEventTypeDeleted(String values){
		Boolean result = false;
		WebElement ASTtable2 =item.getElement(EVENT_TABLE_VALUES);
		String tableId = ASTtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		} return result;
	}
	             //Common Functionality to fill all the fields
		public AdminRetentionTab fillAllFileds(String name,String displayOrder,String systemName,String shortName, String includeinWidget) {
		wait.waitTillElementDisplayed(ATYPE_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(ATYPE_TEXT_PRESENT);
		field.clearAndSetText(ALERT_TYPE_NAME,name);
		field.clearAndSetText(ATYPE_DISPLAY_ORDER,displayOrder);
		field.clearAndSetText(ATYPE_SYSTEM_NAME,systemName);
		field.clearAndSetText(ATYPE_SHORT_NAME,shortName);
		field.selectCheckBox(INCLUDE_IN_WIDGET);
		button.click(SAVE);
        wait.waitTillElementPresent(ATYPE_FORM_NONE, MIN_TIME, MAX_TIME);
        refreshPage();
        wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
public AdminRetentionTab fillFewFileds(String name,String displayOrder,String shortName) {
  wait.waitTillElementDisplayed(ATYPE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(ATYPE_TEXT_PRESENT);
		field.clearAndSetText(ALERT_TYPE_NAME,name);
		field.clearAndSetText(ATYPE_DISPLAY_ORDER,displayOrder);
		field.clearAndSetText(ATYPE_SHORT_NAME,shortName);
		button.click(SAVE);	
		 wait.waitTillElementPresent(ATYPE_FORM_NONE, MIN_TIME, MAX_TIME);
		 refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}

}

