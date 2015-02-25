package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.survey.pojo.SurveyProperties;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAnalyzePage extends SurveyPage{
	
	public SurveyAnalyzePage(SurveyProperties surveyProp) {
        super(surveyProp.getSurveyName());
        waitTillNoLoadingIcon();
	}
	
}
