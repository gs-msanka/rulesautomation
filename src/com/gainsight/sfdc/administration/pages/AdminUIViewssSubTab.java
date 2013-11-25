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
	private final String AVAILABLE_FIELDS      = "ddColumnSelectorList";
	private final String VIEW_NAME             = "//input[@ class='jbaraDummyUIViewInputCtrl viewNameInput']";
	private final String FILTER_SELECT_FIELDS  = "//select[@class='ddFilterFields']";
	private final String FILTER_SELECT_OPERATOR= "//select[@class='dummyOperatorFields ddOperatorClass']";
	private final String FILTER_VALUE          = "//input[@class='Name filterFieldClass']";
	private final String FILTER_VALUE_SELECT   = "//select[@class='AccountSource filterFieldClass']";
	
	//private final String
	//private final String
	//private final String
	
	
	
	public AdminUIViewssSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	
	public AdminUIViewssSubTab selectTabName(String tabName, String ViewName, String fieldName, 
			                                              String selectffield, String foperator , String fvalue) {
		System.out.println("selectffield has these values---1:" +selectffield);
		button.click(NEW);
		System.out.println("Clicked on NEW" );
		wait.waitTillElementPresent(WAIT_PAGE_LOAD, MIN_TIME, MAX_TIME);
		verifyandSelectTabName(tabName, ViewName, fieldName);
		specifyFilterCriteria(selectffield, foperator ,fvalue);
		return this;
		}		
		
	
	
	public AdminUIViewssSubTab verifyandSelectTabName(String tabName, String ViewName, String fieldName  ) {
			                                  
	 String[] SelectTabs = {"Customers 360", "NPS", "Survey Participants", "survey Details Report" };  
		for(String Nmechck:SelectTabs ) {
			System.out.println("Namecheck items are:--"+Nmechck);
			if(Nmechck.contains(tabName)) {
				 System.out.println("In the if block tab name is :--"+tabName);
				 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
				 
				//selectAvailableFields(fieldName);
			 return this;
			}
	 	}
			 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
			 field.clearAndSetText(VIEW_NAME, ViewName);
			// specifyFilterCriteria(selectfield, foperator ,fvalue, selectfvalue);
		return this;		
	}
	
	
	public AdminUIViewssSubTab specifyFilterCriteria(String selectffield, String foperator , String fvalue) {
		
		System.out.println("se;ectffield has these values:" +selectffield);
		field.selectFromDropDown(FILTER_SELECT_FIELDS, selectffield);
		
		
		
		wait.waitTillElementDisplayed(FILTER_SELECT_OPERATOR, MIN_TIME, MAX_TIME);
		field.selectFromDropDown(FILTER_SELECT_OPERATOR, foperator);//equals, contains,lessthan...
		if(item.isElementPresent(FILTER_VALUE)) {
		field.selectFromDropDown(FILTER_VALUE, fvalue);
		} else {
			
			//field.selectFromDropDown(FILTER_VALUE_SELECT, selectfvalue);
		} return this;	
	}
	
	
	
	
	
	
public AdminUIViewssSubTab selectAvailableFields(String fieldName ) {
		
		boolean result = false;
		Select s = new Select(item.getElement(AVAILABLE_FIELDS));
		List<WebElement> avblefilds = s.getOptions();
		for(WebElement webEle : avblefilds) {
   if(webEle.getText().equalsIgnoreCase(fieldName)) {
		result = true;
	  if(result == true) {
			field.selectFromDropDown(AVAILABLE_FIELDS, fieldName);//selecting existing field Name
		    } 
		}
		return this;
	}
		return this;
}
	
	

}
