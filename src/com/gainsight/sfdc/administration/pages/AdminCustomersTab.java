package com.gainsight.sfdc.administration.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AdminCustomersTab extends BasePage{
	
	private final String NAME                 = "//input[@class='jbaraDummyAdminInputCtrl subjectInput nameInputClass']";
	private final String DISPLAY_ORDER        = "//input[@class='jbaraDummyAdminInputCtrl DisplayOrderInput']";
	private final String SYSTEM_NAME          = "//input[@class='jbaraDummyAdminInputCtrl subjectInput systemNameInputClass']";
	private final String SHORT_NAME           = "//input[@class='jbaraDummyAdminInputCtrl subjectInput shortNameInputClass']";
	private final String ACTIVE               = "//input[@class='jbaraDummyAdminCheckboxCtrlActive checkboxActiveInput']";
	private final String SAVE                 = "//input[@class='btn dummyAllAdminSaveBtn']";
	
	//private final String REVENUEBAND_CONFIG   = "//input[@class='btn dummyAllAdminNewBtn']";
	//private final String CSM_CONFIG           = "//input[@class='btn csmFieldSettingsBtn']";
	
	public AdminCustomersTab addStage(String name, String displayorder, String systemName, String shortName) {
		
		field.setSelectField(NAME, name);
		field.setTextField(DISPLAY_ORDER, displayorder);
		field.setTextField(SYSTEM_NAME, systemName);
		field.setTextField(SHORT_NAME, shortName);
		button.click(SAVE);
		return this;
	}
	
	
	public AdminCustomersTab editStage() {
		
		
		return this;
	}
	
	public AdminCustomersTab deleteStage() {
		
		return this;
	}
	
	
    public AdminCustomersTab revenueBandConfiguration() {
    	
    	
    	
    	return this;
    }
	
    
    public AdminCustomersTab doCSMConfigutaion() {
    	
    	
    	return this;
    }
    
    
    
}
