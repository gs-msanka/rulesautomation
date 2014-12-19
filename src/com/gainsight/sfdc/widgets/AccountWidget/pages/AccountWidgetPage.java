package com.gainsight.sfdc.widgets.AccountWidget.pages;

import com.gainsight.sfdc.pages.BasePage;

public class AccountWidgetPage extends BasePage{
	
	private final String COCKPIT_SUBTAB="//a[@class='Cockpit']";
	private final String COCKPIT_INDICATOR="//div[@class='gs_section_title']/h1[contains(text(),'Cockpit')]";
	
	public AccountWidget_CockpitTab_Page gotoCockpitSubTab(){
		item.click(COCKPIT_SUBTAB);
		wait.waitTillElementDisplayed(COCKPIT_INDICATOR, MIN_TIME,MAX_TIME);
		return new AccountWidget_CockpitTab_Page();
	}
}
