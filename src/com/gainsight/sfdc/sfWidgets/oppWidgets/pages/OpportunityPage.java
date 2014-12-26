package com.gainsight.sfdc.sfWidgets.oppWidgets.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.pages.CustomerSuccessPage;

public class OpportunityPage extends BasePage {
	private final String READY_INDICATOR="//h2[text()='Opportunity Detail']";
	
	public OpportunityPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
	}
	public CustomerSuccessPage getCustomerSuccessSection() {
		return new CustomerSuccessPage();
	}

}
