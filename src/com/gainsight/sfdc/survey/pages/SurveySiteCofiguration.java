package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

public class SurveySiteCofiguration extends BasePage {
	
	
	private final String SETUP_LINK                         = "//div[@id='userNavMenu']/descendant::a[@title='Setup']";
	private final String FORCE_COM_LINK                     ="ForceCom_font";
	private final String SITES_LINK                         ="//div[@class='setupLeaf']/a[contains(@id, 'CustomDomain')]";
	private final String SITE_LABEL                         ="//th[contains(@class, 'dataCell')]/a";
	private final String EDIT_BUTTON                        ="//div[@class='pbHeader']/descendant::td[contains(@class, 'pbButton')]/descendant::input";
	private final String VISUAL_FORCE_PAGE_INPUT            ="//table[@class='detailList']/descendant::tr/descendant::label[contains(text(), 'Active Site')]/ancestor::th/following-sibling::td/descendant::span/input";
	private final String VISUAL_FORCE_SAVE                  ="//div[@class='pbBottomButtons']/descendant::input";
	private final String VISUAL_FORCEPAGE_EDIT              ="//div[contains(@id, 'pagessect')]/descendant::div[contains(@class, 'secondaryPalette')]/descendant::td[contains(@class, 'pbButton')]/input";
	private final String SURVEY_EMAILOPEN_VFPAGE_INPUT1     ="//select[@id='duel_select_1']/option[contains(text(), 'EmailOpen')]";
	private final String SURVEY_EMAILOPEN_VFPAGE_INPUT0     ="//select[@id='duel_select_0']/option[contains(text(), 'EmailOpen')]";
	private final String DROP_VFPAGE_LINK                   ="//div[contains(@class, 'zen-mbs')]/a";
	private final String SURVEY_SURVEYRESPONSE_VFPAGE_INPUT1="//select[@id='duel_select_1']/option[contains(text(), 'SurveyResponse')]";
	private final String VFPAGE_SAVE                        ="//div[@class='pbHeader']/descendant::td[@id='topButtonRow']/input[@name='save']";
	private final String SURVEY_SURVEYRESPONSE_VFPAGE_INPUT0="//select[@id='duel_select_0']/option[contains(text(), 'SurveyResponse')]";
	
	public void navigateToSetup(){
		item.click(USERNAVBUTTON);
		item.click(SETUP_LINK);
		wait.waitTillElementPresent(FORCE_COM_LINK, MIN_TIME, MAX_TIME);
		searchSitesInSetup();
	}

	public void searchSitesInSetup(){
		field.clearAndSetText("setupSearch", "Sites"); /*search input is passed here, as this never changes*/
		item.click(SITES_LINK);
		if (item.isElementPresent(SITE_LABEL)) {
			item.click(SITE_LABEL);
			wait.waitTillElementDisplayed(EDIT_BUTTON, MIN_TIME, MAX_TIME);
			addVisualForcePage();
		} else {
            Log.error("Site Label Not found, Plese Check");
            throw new RuntimeException("Site Label Not found, Plese Check");
		}
	}
	
	public void addVisualForcePage(){
		item.click(EDIT_BUTTON);
		field.clearAndSetText(VISUAL_FORCE_PAGE_INPUT, "SurveyResponse");
		item.click(VISUAL_FORCE_SAVE);
		wait.waitTillElementDisplayed(EDIT_BUTTON, MIN_TIME, MAX_TIME); 
		item.click(VISUAL_FORCEPAGE_EDIT);
		if (item.isElementPresent(SURVEY_EMAILOPEN_VFPAGE_INPUT1)) {
			Log.info("VisualForce page already added");
		} else {
			item.click(SURVEY_EMAILOPEN_VFPAGE_INPUT0);
			item.click(DROP_VFPAGE_LINK);
		}

		if (item.isElementPresent(SURVEY_SURVEYRESPONSE_VFPAGE_INPUT1)) {
			Log.info("VisualForce page already added");
			item.click(VFPAGE_SAVE);
		} else {
			item.click(SURVEY_SURVEYRESPONSE_VFPAGE_INPUT0);
			item.click(DROP_VFPAGE_LINK);
			item.click(VFPAGE_SAVE);
		}
	}
}
