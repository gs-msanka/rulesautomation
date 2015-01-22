package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.tests.BaseTest;
import com.sforce.soap.partner.sobject.SObject;

public class SurveySetup extends BaseTest{
	
	//method to return Survey ID, i/p = Survey Name, object name = JBCXM__Survey__C 
	public String getSurveyID(String surveyName){
		
		SObject[] surveys = sfdc.getRecords("SELECT ID FROM JBCXM__SURVEY__C WHERE TITLE__C = "+surveyName);
		String surveyId=surveys[0].getId();
		return surveyId;
		
	}
	
	//method to return Survey QuestionID,i/p = Survey Name, object name = JBCXM__Survey__C
	public String getSurveyQuestionID(String surveyName,String questionTitle){
		SObject[] questions = sfdc.getRecords("SELECT ID FROM JBCXM__SurveyQuestion__c where JBCXM__SurveyMaster__c = "+getSurveyID(surveyName)+ " and JBCXM__Title__c = "+questionTitle);
		String questionID = questions[0].getId();
		return questionID;
	}
	
	//method to return Survey SubQuestionID,i/p = Survey Name and ParentQuestion object name = JBCXM__Survey__C
	
	

}
