package com.gainsight.sfdc.sfWidgets.oppWidgets.pages;

import com.gainsight.sfdc.pages.BasePage;

import java.net.URI;
import java.net.URISyntaxException;

public class OpportunitiesPage extends BasePage{
	private final String READY_INDICATOR="//h3[text()='Recent Opportunities']";
	
	public OpportunitiesPage(){
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);		
	}

	public OpportunityPage goToOpportunityPage(String oppID){
        URI currentURL;
        try {
            currentURL = new URI(driver.getCurrentUrl());
            String accURL="https://" + currentURL.getHost()+"/"+oppID;
            driver.navigate().to(accURL);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to navigate to account page");
        }
        return new OpportunityPage();
	}
}
