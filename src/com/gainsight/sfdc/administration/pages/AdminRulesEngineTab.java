package com.gainsight.sfdc.administration.pages;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import jxl.read.biff.BiffException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;


import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.salesforce.pages.CreateSalesforceUsers;

public class AdminRulesEngineTab extends BasePage {
	
	private final String READY_INDICATOR      = "//div[@class='addRulesSection btn']";
	private final String TEXT                 = "//div[contains(text(),'No Rules Set')]";
	private final String SORT                 = "//select[@class='sortingDD']";
	private final String NEW_RULE            = "//div[@class='addRulesSection btn']/a";
	private final String RULE_TITLE           = "//input[@class='jbaraAlertSubjectInput']";
	private final String SELECT_SEVERITY      = "//select[@id='jbaraAlertSeverityInput']";
	private final String SELECT_ATYPE         = "//select[@id='jbaraAlertTypeInput']";
	private final String SELECT_ASTATUS       = "//select[@id='jbaraAlertStatusInput']";
	private final String SELECT_AREASON       = "//select[@id='jbaraAlertReasonInput']";
	private final String COMMENTS             = "//textarea[@id='measureCommentSection']";
	private final String SELECT_PLAYBOOK      = "//select[@id='jbaraAlertPlaybookInput']";
	private final String SELECT_TASK_OWNER    = "//select[@id='jbaraTaskOwnerField']";
	private final String DEFAULT_TASK_OWNER   = "//input[@id='jbaraTaskDefaultUserName']";
	private final String CANCEL               = "//div[@class='btnSaveCancel cancelBtn']";
	private final String SAVE                 = "//div[@class='btnSaveCancel saveBtn']";   
	private final String SET_CRITERIA         = "//a[@class='jbaraOverlayForRules']";
	private final String BASED_ON_USAGE       = "//input[@id='ruleRadio']";
	private final String BASED_ON_CUST_FIELDS = "//input[@id='filterRadio']";
	private final String SELECT_ADOP_MEASURES = "mainMileStoneId";
	private final String SELECT_PARITY        = "//select[@class='ddOperator previewClass']";
	private final String PERCENTAGE           = "//input[@class='ddSelectPercentage ddSelectPercentageAverage previewClass']";
	private final String APPLY_ADD_NEXT       = "//div[@class='btnSaveCancel btnApplyAndAdd']";
	private final String APPLY_CLOSE          = "//div[@class='btnSaveCancel btnApplyAndClose']"; 
	private final String SUM_CHECKBOX         = "//input[@class='usageRulesConfigInputCls trendvaluebased SUM']";
	private final String SUM_SELECT_MONTHS    = "//select[@class='averageTrailingMonths monthsSelect inputMonths previewClass']";
	private final String SUM_VALUE            = "//select[@class='averageTrailingMonths monthsSelect inputMonths previewClass']/following::span/input";
	private final String EDIT_RULE            = "//div[@data-name='"+RULE_TITLE+"']/div/span[@class='ruleEditCls editIcon ruleOperationsCls']";
	private final String DELETE_RULE          = "//span[@class='ruleDeleteCls deleteIcon ruleOperationsCls']/a";
	
	public AdminRulesEngineTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	public AdminRulesEngineTab createNewRule(String ruleTitle, String selectSeverity, String alertType,
			                                String status, String reason, String selectPlaybook, 
			                             String taskOwner,String defaultTaskOwner,String selectActivity,
			                           String selectParity,String percent, String selectMonths ,String  sumValue, 
		                             String firstName, String lastName, String email, String userLicense, String role) { 
                                                                             
		item.click(NEW_RULE);
		wait.waitTillElementPresent(RULE_TITLE, MIN_TIME, MAX_TIME);
		System.out.println("Clicked on New Rule");
		item.clearAndSetText(RULE_TITLE, ruleTitle);
		field.selectFromDropDown(SELECT_SEVERITY, selectSeverity);
		field.selectFromDropDown(SELECT_ATYPE, alertType);
		wait.waitTillElementPresent(SELECT_ASTATUS, MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_ASTATUS, status);
		wait.waitTillElementPresent(SELECT_AREASON, MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_AREASON, reason);
		field.selectFromDropDown(SELECT_PLAYBOOK, selectPlaybook);
		field.selectFromDropDown(SELECT_TASK_OWNER, taskOwner);
	    ownerSelect(defaultTaskOwner);
		item.click(SET_CRITERIA);
		wait.waitTillElementDisplayed("//div[@class='mainRuleBody ui-dialog-content ui-widget-content']", MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_ADOP_MEASURES, selectActivity);
		field.selectFromDropDown(SELECT_PARITY, selectParity);
		
		                          /*Since Equals,Not equals,Less than and Greater than has 
		                                              * same Trend,so validating only equals  
		                                    *    else part will cover-- Dropped By and Increased By.  */                          
		if(selectParity.equalsIgnoreCase("Equals")) {
			System.out.println("This is the for Equals operator...");
			item.click(SUM_CHECKBOX);
			field.selectFromDropDown(SUM_SELECT_MONTHS, selectMonths);
			item.clearAndSetText(SUM_VALUE, sumValue);
		} else {
			item.clearAndSetText(PERCENTAGE, percent);
		}
		button.click(APPLY_CLOSE);
		wait.waitTillElementPresent(SAVE, MIN_TIME, MAX_TIME);
	    button.click(SAVE);
		return this;
	}
	
	/*public boolean isAlertRuleCreated(String values,String ruleTitle) {
		
			Boolean result = false;
			WebElement ATtable2 =item.getElement("//div[@class='eachRuleWrapper' and @data-name='"+ruleTitle+"']");
			String ruledta = ATtable2.getAttribute("index");
		String index =	item.getText(ruledta);
			System.out.println("The  index values are :"+ index);
			
			return result;
			
		}*/

	                        //Edit Alert Rules	
	public AdminRulesEngineTab editAlertRules(String ruleTitle, String selectSeverity, String alertType,
                                                  String status, String reason,String selectPlaybook,  
                                                       String taskOwner, String selectActivity,String selectParity,
                                                            String percent, String selectMonths ,String sumValue) {
		
		item.click("//div[@data-name='"+ruleTitle+"']/div/span[@class='ruleEditCls editIcon ruleOperationsCls']");          
		wait.waitTillElementDisplayed("//div/h1[@class='pageType noSecondHeader']", MIN_TIME, MAX_TIME);
		item.clearAndSetText(RULE_TITLE, ruleTitle);
		field.selectFromDropDown(SELECT_SEVERITY, selectSeverity);
		field.selectFromDropDown(SELECT_ATYPE, alertType);
		wait.waitTillElementPresent(SELECT_ASTATUS, MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_ASTATUS, status);
		wait.waitTillElementPresent(SELECT_AREASON, MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_AREASON, reason);
		item.click(SET_CRITERIA);
		wait.waitTillElementDisplayed("//div[@class='mainRuleBody ui-dialog-content ui-widget-content']", MIN_TIME, MAX_TIME);
		field.selectFromDropDown(SELECT_ADOP_MEASURES, selectActivity);
		field.selectFromDropDown(SELECT_PARITY, selectParity);
		
		                  /*Since Equals,Not equals,Less than and Greater than has  
		                                               *  same Trend,so validating only equals  
		                             *    else part will cover-- Dropped By and Increased By.  */                          
		if(selectParity.equalsIgnoreCase("Equals")) {
			System.out.println("This is the for Equals operator...");
			item.click(SUM_CHECKBOX);
			field.selectFromDropDown(SUM_SELECT_MONTHS, selectMonths);
			item.clearAndSetText(SUM_VALUE, sumValue);
		} else {
			item.clearAndSetText(PERCENTAGE, percent);
		}
		button.click(APPLY_CLOSE);
		wait.waitTillElementPresent(SAVE, MIN_TIME, MAX_TIME);
	    button.click(SAVE);
		return this;
	}
		
	
	public AdminRulesEngineTab deleteAlertRules(String ruleTitle){
		item.click("//div[@data-name='"+ruleTitle+"']/div/span[@class='ruleDeleteCls deleteIcon ruleOperationsCls']");
		modal.accept();
		wait.waitTillElementDisplayed(NEW_RULE, MIN_TIME, MAX_TIME);
		return this;
	}
	
	      /*   Selecting the default task owner.Here need WebElement to 
	                                 * store the userid's and select one among them 
	                                                                     */
	public void ownerSelect(String ownerName) {
        field.setTextField(DEFAULT_TASK_OWNER, ownerName);
        amtDateUtil.sleep(4); 
        wait.waitTillElementDisplayed("//li[@class='ui-menu-item' and @role='menuitem']", MIN_TIME, MAX_TIME);
        Report.logInfo("Started selecting the owner of event");
        WebElement wEle = null;
        List<WebElement> eleList = element.getAllElement("//a[contains(@class, 'ui-corner-all')]");
        for(WebElement ele : eleList) {
                if(ele.isDisplayed()){
                        String s = ele.getText();
                        System.out.println("AccText :" +s);
                        System.out.println("Exp Text:" +ownerName);
                        if(s.contains(ownerName)){
                                wEle = ele;
                                
                                break;
                        }
                }
        }
        if(wEle != null) {
                Actions builder = new Actions(driver);
                builder.moveToElement(wEle);
                builder.click(wEle);
                Action s = builder.build();
                s.perform();
                Report.logInfo("Finished selecting the owner for event");
        } else {
                Report.logInfo("FAIL: Failed to select the owner for the event");
        }
       
        
}

}
