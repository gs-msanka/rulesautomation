package com.gainsight.sfdc.sfWidgets.oppWidget.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccWidget_CockpitPage;

/**
 * Created by gainsight on 26/12/14.
 */
public class OppWidgetPage extends BasePage {

	 private final String COCKPIT_SUBTAB="//a[@class='Cockpit']";
	private final String COCKPIT_INDICATOR="//div[@class='gs_section_title']/h1[contains(text(),'Cockpit')]";
	private final String FEATURES_TAB = "//a[@class='Features']";
	  	
	public OppWidget_CockpitPage gotoCockpitSubTab(){
        item.click(COCKPIT_SUBTAB);
        wait.waitTillElementDisplayed(COCKPIT_INDICATOR, MIN_TIME,MAX_TIME);
        return new OppWidget_CockpitPage();
    }
	
	public OppWidget_FeaturesPage selectOppFeaturesSubTab() {
        item.click(FEATURES_TAB);
        return new OppWidget_FeaturesPage();
    }
}
