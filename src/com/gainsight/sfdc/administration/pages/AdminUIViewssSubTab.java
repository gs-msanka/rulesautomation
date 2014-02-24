package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.sfdc.pages.BasePage;

public class AdminUIViewssSubTab extends BasePage {
	
	
	
	private final String READY_INDICATOR       = "//input[@class='btn uiViewNewBtn']";
	private final String NEW                   = "//input[@class='btn uiViewNewBtn']";
	private final String UI_VIEW_CANCEL        = "//input[@class='btn btnCancelClick']";
	private final String WAIT_PAGE_LOAD        = "//div[@class='bPageBlock brandSecondaryBrd bEditBlock secondaryPalette']";
	private final String SELECT_DROPDOWN       = "//select[@class='ddTabsList']";
	private final String AVAILABLE_FIELDS      = "//select[@id='ddColumnSelectorList' and @multiple='multiple']"; //"ddColumnSelectorList";
	private final String VIEW_NAME             = "//input[@ class='jbaraDummyUIViewInputCtrl viewNameInput']";
	private final String FILTER_SELECT_FIELDS  = "//select[@class='ddFilterFields']";
	private final String FILTER_SELECT_OPERATOR= "//select[@class='dummyOperatorFields ddOperatorClass']";
	private final String FILTER_VALUE          = "//input[@class='Name filterFieldClass']";
	
	private final String FILTER_VALUE_TEXT     = "//td[@class='tdFilterValue othertds']//input";
	private final String MULTI_FILTER_VALUE    = "//select[@multiple='multiple']";
	private final String FILTER_VALUE_SELECT   = "//td[@class='tdFilterValue othertds']//select";
	
	//private final String
	//private final String
	//private final String
	
	
	
	public AdminUIViewssSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	
	public AdminUIViewssSubTab selectTabName(String tabName, String ViewName,  
			                                              String selectffield, String foperator , String fvalue,String sctFieldName) {
		button.click(NEW);
		System.out.println("Clicked on NEW" );
		wait.waitTillElementPresent(WAIT_PAGE_LOAD, MIN_TIME, MAX_TIME);
		verifyandSelectTabName(tabName, ViewName, selectffield, foperator , fvalue, sctFieldName);
		return this;
		}		
		
	public AdminUIViewssSubTab verifyandSelectTabName(String tabName, String ViewName,String selectffield, String foperator , String fvalue ,String sctFieldName) {             
	 String[] SelectTabs = {"Customer360", "NPS","Survey Detail Report", "Survey Participants"};  
		for(String Nmechck:SelectTabs ) {
			if(Nmechck.contains(tabName)) {
				 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
				 selectAvailableFields(sctFieldName);
			 return this;
			}
	 	}
			 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
			 field.clearAndSetText(VIEW_NAME, ViewName);
			 System.out.println("After the View name:--");
			 specifyFilterCriteria(selectffield, foperator ,fvalue);
			 selectAvailableFields(sctFieldName);
		return this;		
	}
	
	public AdminUIViewssSubTab specifyFilterCriteria(String selectffield, String foperator , String fvalue) {
		field.selectFromDropDown(FILTER_SELECT_FIELDS, selectffield);
		wait.waitTillElementDisplayed(FILTER_SELECT_OPERATOR, MIN_TIME, MAX_TIME);
		System.out.println("foperator value is:--"+foperator);
		field.selectFromDropDown(FILTER_SELECT_OPERATOR, foperator);//equals, contains,lessthan...
		
	if(item.isElementPresent(MULTI_FILTER_VALUE)) {
			System.out.println("Fvalue is:--"+fvalue);
		       field.selectFromDropDown(MULTI_FILTER_VALUE, fvalue);
		} else if(item.isElementPresent(FILTER_VALUE_TEXT)) {
			System.out.println("Fvalue is:--"+fvalue);
		     field.selectFromDropDown(FILTER_VALUE_TEXT, fvalue);
		} else {
			 field.selectFromDropDown(FILTER_VALUE_SELECT, fvalue);
		} return this;	
	}
	
		
public AdminUIViewssSubTab selectAvailableFields(String sctFieldName ) {
	System.out.println("****In selectAvailableFields*****");
	System.out.println("splits.size: " + sctFieldName);
	 String[] allsctFieldNames= sctFieldName.split(",");
	 System.out.println("splits.size: " + allsctFieldNames.length);
	 for(int k=0;k<allsctFieldNames.length;k++){
		 field.selectFromDropDown(AVAILABLE_FIELDS, allsctFieldNames[k]);//selecting existing field Name
	     button.click("//img[@class='rightArrowIcon']");
	 }
	button.click("//input[@class='btnSaveClick btn']");
		return this;
}
	


}
