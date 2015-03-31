package com.gainsight.sfdc.sfWidgets.oppWidget.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccWidget_FeaturesPage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccountWidgetPage;

public class OpportunityPage extends BasePage {
	private final String READY_INDICATOR="//h2[text()='Opportunity Detail']";
	private final String FEATURES_TAB = "//a[@class='Features']";
	
	public OpportunityPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
	}
	public OppWidgetPage getOppWidget() {
		return new OppWidgetPage();
	}
	
	public OppWidgetPage switchToOppCSWidget(){
		element.switchToFrame("//iframe[@title='CustomerSuccessOpportunity']");
	System.out.println("In Opp wodget PAge");
		return new OppWidgetPage();	
	}

	
	
	
	
}
