package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Log;
import com.sforce.soap.partner.sobject.SObject;

import java.util.HashMap;

public class SurveySetup extends BaseTest {

    /**
     * Populate ths survey Id.
     * @param surveyProp
     */
	public String setSurveyId(SurveyProperties surveyProp){
        String query = resolveStrNameSpace("Select id, Name From JBCXM__Survey__c Where JBCXM__title__c = '" + surveyProp.getSurveyName() + "' order by createdDate desc limit 1");
        Log.info("Query to get survey ID : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of records returned : "+sObjects.length);
        String surveyId;
        if(sObjects.length >=1) {
            surveyId = sObjects[0].getId();
            Log.info("Survey Id : "+surveyId);
            surveyProp.setsId(surveyId);
        } else {
            throw new RuntimeException("No Survey Found with this name : " +surveyProp.getSurveyName());
        }
        return surveyId;
    }

    public String getRecentAddedPageId(SurveyProperties surveyProp) {
        String query = resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PageInfo__c Where JBCXM__Survey__c = '"+surveyProp.getsId()+"' AND Name= 'Untitled Page' order by createdDate desc limit 1");
        Log.info("Query to get page ID : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of records returned : "+sObjects.length);
        String pageId;
        if(sObjects.length >=1) {
            pageId = sObjects[0].getId();
            Log.info("Page Id : " +pageId);
        } else {
            throw new RuntimeException("No Survey Found with this name : " +surveyProp.getSurveyName());
        }
        return pageId;
    }

    public String getRecentAddedQuestionId(SurveyQuestion surQues) {
        String query = resolveStrNameSpace("Select id, Name, JBCXM__ParentQuestion__c, JBCXM__DisplayOrder__c, JBCXM__SurveyMaster__c, JBCXM__Title__c, JBCXM__Type__c From JBCXM__SurveyQuestion__c where " +
                "JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' order by createdDate desc limit 1 ");
        Log.info("Query to get survey question ID : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of records returned : "+sObjects.length);
        String questId;
        if(sObjects.length >=1) {
            questId = sObjects[0].getId();
            Log.info("Question Id : "+questId);
        } else {
            throw new RuntimeException("No Survey Question Found with this name : " +surQues.getQuestionText());
        }
        return questId;
    }

    public void getQuestionId(SurveyQuestion surQues) {
        String query = resolveStrNameSpace("Select id, Name, JBCXM__ParentQuestion__c, JBCXM__DisplayOrder__c, JBCXM__SurveyMaster__c, JBCXM__Title__c, JBCXM__Type__c From JBCXM__SurveyQuestion__c where " +
                            "JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"'  and JBCXM__Title__c='"+surQues.getQuestionText()+"' and JBCXM__Type__c = '"+surQues.getQuestionType()+"' order by createdDate desc limit 1 ");
        Log.info("Query to get survey question ID : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of records returned : "+sObjects.length);
        if(sObjects.length >=1) {
            String questId = sObjects[0].getId();
            Log.info("Question Id : "+questId);
            surQues.setQuestionId(questId);
        } else {
            throw new RuntimeException("No Survey Question Found with this name : " +surQues.getQuestionText());
        }
    }

    ////TODO - Comments Question Should be implemented.
    public void setSubQuestionsId(SurveyQuestion surQues) {
        if(surQues.getSubQuestions() ==null && surQues.getSubQuestions().size()==0) {
            throw new RuntimeException("No Sub Questions to populate the information.");
        }
        String query = resolveStrNameSpace("Select id, Name, JBCXM__ParentQuestion__c, JBCXM__DisplayOrder__c, JBCXM__SurveyMaster__c, JBCXM__Title__c, JBCXM__Type__c From JBCXM__SurveyQuestion__c where " +
                "JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' and JBCXM__ParentQuestion__c='"+surQues.getQuestionId()+"'  order by createdDate desc limit 1 ");
        Log.info("Query to get all the sub questions: " +query);
        SObject[] sObjects = sfdc.getRecords(query);
        if(!(sObjects.length>=1)) {
            throw new RuntimeException("No Sub Questions to populate Id information.");
        }
        HashMap<String, String> tempMap = new HashMap<>();
        for(SObject sObject : sObjects) {
            Log.info(sObject.getSObjectField(resolveStrNameSpace("JBCXM__Title__c")).toString());
            tempMap.put(sObject.getSObjectField(resolveStrNameSpace("JBCXM__Title__c")).toString(), sObject.getId());
        }
        for(SurveyQuestion.SurveySubQuestions subQues : surQues.getSubQuestions()) {
            if(tempMap.containsKey(subQues.getSubQuestionText())) {
                subQues.setsId(tempMap.get(subQues.getSubQuestionText()));
            } else {
                throw new RuntimeException("Following Sub Question Text is not found : " +subQues.getSubQuestionText());
            }

        }
    }

    //TODO - For Allow Others Should be implemented.
    public void setAnsChoicesId(SurveyQuestion surQues) {
        if(surQues.getAllowedAnswers() ==null && surQues.getAllowedAnswers().size()==0) {
            throw new RuntimeException("No Choice information to populate the information, Check your data");
        }
        String query = resolveStrNameSpace("Select Id, JBCXM__SurveyMaster__c, JBCXM__SurveyQuestion__c, JBCXM__Title__c, JBCXM__IsActive__c, JBCXM__DisplayOrder__c, JBCXM__AllowOtherLabel__c from JBCXM__SurveyAllowedAnswers__c " +
                            "Where JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' and JBCXM__SurveyQuestion__c='"+surQues.getQuestionId()+"' order by createdDate desc limit 1 ");
        Log.info("Query to get all the sub questions: " +query);
        SObject[] sObjects = sfdc.getRecords(query);
        if(!(sObjects.length>=1)) {
            throw new RuntimeException("No Sub Questions to populate Id information.");
        }
        HashMap<String, String> tempMap = new HashMap<>();
        for(SObject sObject : sObjects) {
            Log.info(sObject.getSObjectField(resolveStrNameSpace("JBCXM__Title__c")).toString());
            tempMap.put(sObject.getSObjectField(resolveStrNameSpace("JBCXM__Title__c")).toString(), sObject.getId());
        }
        for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer : surQues.getAllowedAnswers()) {
            if(tempMap.containsKey(surveyAllowedAnswer.getAnswerText())) {
                surveyAllowedAnswer.setsId(tempMap.get(surveyAllowedAnswer.getAnswerText()));
            } else {
                throw new RuntimeException("Following Answer Choice is not found : " +surveyAllowedAnswer.getAnswerText());
            }
        }
    }
	
    public SurveyQuestionPage createSurveyQuestion(SurveyQuestion surveyQuestion, SurveyQuestionPage surveyQuestionPage) {
        surveyQuestionPage.clickOnAddNewQuestion(surveyQuestion);
        surveyQuestion.setQuestionId(getRecentAddedQuestionId(surveyQuestion));
        surveyQuestionPage.fillQuestionFormInfo(surveyQuestion);
        surveyQuestionPage = surveyQuestionPage.clickOnSaveQuestion(surveyQuestionPage.getQuestionElement(surveyQuestion));
        return surveyQuestionPage;
    }
	

	
	

}
