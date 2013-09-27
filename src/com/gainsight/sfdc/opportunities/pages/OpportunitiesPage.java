package com.gainsight.sfdc.opportunities.pages;

import com.gainsight.sfdc.pages.BasePage;

public class OpportunitiesPage extends BasePage{
	private final String READY_INDICATOR="//h3[text()='Recent Opportunities']";
	
	public OpportunitiesPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}
	
	public OpportunityPage selectRecentOpportunity(String name){
		item.click("//a[text()='"+name+"']");
		return new OpportunityPage();
	}

}
