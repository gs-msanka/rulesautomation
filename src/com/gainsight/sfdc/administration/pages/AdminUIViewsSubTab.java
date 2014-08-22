package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.By;

import com.gainsight.sfdc.pages.BasePage;

public class AdminUIViewsSubTab extends BasePage {
	
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
	private final String MULTI_FILTER_VALUE    = "//select[@multiple='multiple']";//select[contains(@multiple,'multiple')]
	private final String FILTER_VALUE_SELECT   = "//td[@class='tdFilterValue othertds']//select";
	
	private final String SPECFY_REPORT_PARM    = "//select[@class='ddReportParamFields']";
	private final String RPARMS_LABEL          = "//input[@class='selectedReportParamLabel']"; 
	private final String RPARMS_OPERATOR       = "//div[@ id='divReportParamSelector']//table//tbody//td[3]//select";
	
	private final String RPARMS_VALUE_SELECT   = "//div[@ id='divReportParamSelector']//table//tbody//td[4]//select";
	private final String RPRAMS_VALUE_TEXT     = "//div[@ id='divReportParamSelector']//table//tbody//td[4]//input";  

    private final String FILTER_ADD_ROWS       = "//a[@id='fAddRowLink']";
	private final String FILTER_REMOVE_ROWS    = "//span[@id='fRemoveRowLinkGreyed']";

	public AdminUIViewsSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	
	public AdminUIViewsSubTab selectTabName(String tabName, String ViewName,
			                                       String selectffield, String foperator , String fvalue,
			                                          String sctFieldName,String selectRfield, String rpOperator ,
			                                           String rpvalue) {
		button.click(NEW);
		System.out.println("Clicked on NEW" );
		wait.waitTillElementPresent(WAIT_PAGE_LOAD, MIN_TIME, MAX_TIME);     
		verifyandSelectTabName(tabName, ViewName, selectffield, foperator , fvalue, sctFieldName,
			                      	 selectRfield, rpOperator ,rpvalue);
		return this;
		}		
		
	public AdminUIViewsSubTab verifyandSelectTabName(String tabName, String ViewName,String selectffield, String foperator ,
			                                                              String fvalue ,String sctFieldName,
			                                               String selectRfield, String rpOperator , String rpvalue) {             
	 String[] SelectTabs = {"Customer360", "NPS","Survey Detail Report", "Survey Participants"};  
		for(String Nmechck:SelectTabs ) {
			if(Nmechck.contains(tabName)) {
				 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
				 selectAvailableFields(sctFieldName);
			 return this;
			}}
			 field.selectFromDropDown(SELECT_DROPDOWN, tabName);
			 field.clearAndSetText(VIEW_NAME, ViewName);
			 System.out.println("After the View name:--");
			 specifyFilterCriteria(selectffield, foperator ,fvalue);
			 specifyReportParams(selectRfield,rpOperator,rpvalue);
			 selectAvailableFields(sctFieldName);
		return this;		
	}
	                             //Filter Criteria code
	public AdminUIViewsSubTab specifyFilterCriteria(String selectffield, String foperator ,String fvalue) {
		field.selectFromDropDown(FILTER_SELECT_FIELDS, selectffield);
		wait.waitTillElementDisplayed(FILTER_SELECT_OPERATOR, MIN_TIME, MAX_TIME);
		System.out.println("foperator value is:--"+foperator);
		field.selectFromDropDown(FILTER_SELECT_OPERATOR, foperator); //equals, contains,lessthan...
		              // To Select a value	
	if(isElementPresentAndDisplay(By.xpath(FILTER_VALUE_TEXT))) {
		item.clearText(FILTER_VALUE_TEXT);
		item.setTextByKeys(FILTER_VALUE_TEXT, fvalue); 
		} else if(isElementPresentAndDisplay(By.xpath(FILTER_VALUE_SELECT))) {
			System.out.println("splits.size: " + fvalue);
			 String[] allfvalue= fvalue.split(",");
			 System.out.println("splits.size: " + allfvalue.length);
			 for(int r=0;r<allfvalue.length;r++){
				 field.selectFromDropDown(FILTER_VALUE_SELECT, allfvalue[r]);
	}} 
		return this;
	}	
	                         // Report Params code.
	public AdminUIViewsSubTab specifyReportParams(String selectRfield, String rpOperator , String rpvalue) {
		field.selectFromDropDown(SPECFY_REPORT_PARM, selectRfield);
		wait.waitTillElementDisplayed(RPARMS_OPERATOR, MIN_TIME, MAX_TIME);
		System.out.println("rpOperator value is:--"+rpOperator);
		field.selectFromDropDown(RPARMS_OPERATOR, rpOperator);//equals, contains,lessthan...
		
		if(isElementPresentAndDisplay(By.xpath(RPARMS_VALUE_SELECT))) {
			System.out.println("splits.size: " + rpvalue);
			 String[] allrpvalue= rpvalue.split(",");
			 System.out.println("splits.size: " + allrpvalue.length);
			 for(int r=0;r<allrpvalue.length;r++){
				 field.selectFromDropDown(RPARMS_VALUE_SELECT, allrpvalue[r]);//selecting existing field Name
	}} else {
		item.clearText(RPRAMS_VALUE_TEXT);
		item.clearAndSetText(RPRAMS_VALUE_TEXT, rpvalue);
	}
	return this;	
	}
	
public AdminUIViewsSubTab selectAvailableFields(String sctFieldName ) {
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



























