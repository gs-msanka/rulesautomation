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

	
	public AdminMilestoneTab createMilestoneType(String Name,String displayorder,String systemname,String shortname) {
		Report.logInfo("************In the Create Alert type method**********");
		button.click(NEW_MILESTONE);
		fillAllFileds(Name,displayorder,systemname,shortname);
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
                //--Edit Alert type
	public AdminMilestoneTab editMilestoneType(String s,String Name,String displayorder,String shortname) {
		Report.logInfo("**************In the Edit Alert type method**********");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFileds(Name,displayorder,shortname);
		return this;
	}
	           //--Delete Alert Type
	public AdminMilestoneTab deleteMilestoneType(String s) {
        Report.logInfo("***********In the Delete Alert type method*********");
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
		} System.out.println("**End of Delete Alert Type:**");
		return this;
	}
	                     
                               //Common Functionality to fill all the fields
public AdminMilestoneTab fillAllFileds(String Name,String displayorder,String systemname,String shortname) {
		wait.waitTillElementDisplayed(MSTONE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	if(item.isElementPresent(MSTONE_TEXT_PRESENT)) {
		field.clearAndSetText(MILESTONE_NAME,Name);
		field.clearAndSetText(MILESTON_DISPLAYORDER,displayorder);
		field.clearAndSetText(MILESTONE_SYSNAME,systemname);
		field.clearAndSetText(MILESTONE_SHOTNAME,shortname);
		//field.selectCheckBox(MILESTONE_ACTIVE);
		button.click(MILESTONE_SAVE);
     wait.waitTillElementPresent(MSTONE_FORM_NONE, MIN_TIME, MAX_TIME);
     refreshPage();
   //  wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
     System.out.println("**End of Create Alert Type:**");  
   } else {
			System.out.println("Milestone light box Text not present:"+ item.getText(MSTONE_FROM_TITLE));
		}
		return this;
	}
	public AdminMilestoneTab fillFewFileds(String Name,String displayorder,String shortname) {
		  wait.waitTillElementDisplayed(MSTONE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	if(item.isElementPresent(MSTONE_TEXT_PRESENT)) {
		field.clearAndSetText(MILESTONE_NAME,Name);
		field.clearAndSetText(MILESTON_DISPLAYORDER,displayorder);
		field.clearAndSetText(MILESTONE_SHOTNAME,shortname);
		button.click(MILESTONE_SAVE);
		 //wait.waitTillElementPresent(MSTONE_FORM_NONE, MIN_TIME, MAX_TIME);
	     wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
	     refreshPage();
	 } else {
				System.out.println("Milestone light box Text not present:"+ item.getText(MSTONE_FROM_TITLE));
			}
		return this;
	}
	
	
	
	

}
