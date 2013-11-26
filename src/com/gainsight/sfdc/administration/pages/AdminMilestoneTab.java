package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminMilestoneTab extends BasePage {
	
	
	private final String READY_INDICATOR        = "//h2[text()='Milestones']/parent::td/following-sibling::td/div/input[@value='New']"; 
	private final String NEW_MILESTONE          = "//h2[text()='Milestones']/parent::td/following-sibling::td/div/input[@value='New']"; 
	private final String MILESTONE_NAME         = "//span[contains(text(),'Milestones')]/parent::h2/parent::div//following::div/input";
	private final String MILESTON_DISPLAYORDER  = "//span[contains(text(),'Milestones')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String MILESTONE_SYSNAME      = "//span[contains(text(),'Milestones')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String MILESTONE_SHOTNAME     = "//span[contains(text(),'Milestones')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String MILESTONE_SAVE         = "//span[contains(text(),'Milestones')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String MILESTONE_CANCEL       = "//span[contains(text(),'Milestones')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	private final String MILESTONE_ACTIVE       = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	private final String MSTONE_FORM_BLOCK      = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String MSTONE_FORM_NONE       = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String MSTONE_TEXT_PRESENT    = "//span[contains(text(),'Milestones')]";
	private final String MSTONE_TYPE_VALUES     = "j_id0:j_id14:j_id177:j_id187";
	private final String MSTONE_FROM_TITLE      = "//span[@class='DialogTitleClass']";
	
	public AdminMilestoneTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	
	public AdminMilestoneTab createMilestoneType(String name ,String displayOrder, String systemName, String shortName) {
		button.click(NEW_MILESTONE);
		fillAllFileds(name,displayOrder,systemName,shortName);
		return this;		
	}
	public boolean IsMilestoneTypePresent(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(MSTONE_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
	if(a != -1) {
			result = true;
		}return result;
	}
             
	public AdminMilestoneTab editMilestoneType(String s,String name ,String displayOrder,String shortName) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(name,displayOrder,shortName);
		return this;
	}
	public boolean IsMilestoneTypeEdited(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(MSTONE_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
	if(a != -1) {
			result = true;
		}return result;
	}
	          
	public AdminMilestoneTab deleteMilestoneType(String s) {
        wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;
	}
	public boolean IsMilestoneTypeDeleted(String values){
		Boolean result = false;
		WebElement ATtable2 =item.getElement(MSTONE_TYPE_VALUES);
		String tableId = ATtable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
	if(a != -1) {
			result = true;
		}return result;
	}
	                     
                               //Common Functionality to fill all the fields
public AdminMilestoneTab fillAllFileds(String name,String displayOrder,String systemName,String shortName) {
		wait.waitTillElementDisplayed(MSTONE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(MSTONE_TEXT_PRESENT);
		field.clearAndSetText(MILESTONE_NAME,name);
		field.clearAndSetText(MILESTON_DISPLAYORDER,displayOrder);
		field.clearAndSetText(MILESTONE_SYSNAME,systemName);
		field.clearAndSetText(MILESTONE_SHOTNAME,shortName);
		//field.selectCheckBox(MILESTONE_ACTIVE);
		button.click(MILESTONE_SAVE);
     wait.waitTillElementPresent(MSTONE_FORM_NONE, MIN_TIME, MAX_TIME);
     refreshPage();
     wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		return this;
	}
	public AdminMilestoneTab fillFewFileds(String name ,String displayOrder,String shortName) {
		  wait.waitTillElementDisplayed(MSTONE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(MSTONE_TEXT_PRESENT);
		field.clearAndSetText(MILESTONE_NAME,name);
		field.clearAndSetText(MILESTON_DISPLAYORDER,displayOrder);
		field.clearAndSetText(MILESTONE_SHOTNAME,shortName);
		button.click(MILESTONE_SAVE);
		 wait.waitTillElementPresent(MSTONE_FORM_NONE, MIN_TIME, MAX_TIME);
		 refreshPage();
	     wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
	return this;
	}
	
	
	
	

}
