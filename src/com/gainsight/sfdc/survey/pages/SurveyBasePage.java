package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;

public class SurveyBasePage extends BasePage {


	private final String READY_INDICATOR = "";

	public SurveyBasePage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		
	}

	public SurveyPropertiesPage createSurvey(String surveyName, boolean isNew) {
        item.click("");
        if(isNew) {

        }
		//Save button click.
		return new SurveyPropertiesPage();
	}

	public SurveyBasePage clickOnDashboard() {
        //Click
        return this;
    }

    public SurveyBasePage clickOnDrafts() {
        //Click
        return this;
    }

    public SurveyBasePage clickOnPublish() {
        //Click
        return this;
    }

    public SurveyBasePage clickOnExpired() {
        //Click
        //Waiting.
        return this;
    }
}
