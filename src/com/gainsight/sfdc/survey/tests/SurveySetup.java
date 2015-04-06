package com.gainsight.sfdc.survey.tests;

import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

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

    public String getQuestionType(SurveyQuestion surveyQuestion) {
        String expectedQuestionType = null;
        if(surveyQuestion.getQuestionType().equals("CHECKBOX")) {
            if(surveyQuestion.isSingleAnswer()) {
                expectedQuestionType = "Radio";
            } else {
                expectedQuestionType = "Checkbox";
            }
        } else if(surveyQuestion.getQuestionType().equals("SELECT")) {
            if(surveyQuestion.isSingleAnswer()) {
                expectedQuestionType = "SingleSelect";
            } else {
                expectedQuestionType = "MultiSelect";
            }
        } else if(surveyQuestion.getQuestionType().equals("TEXT_INPUT")) {
            expectedQuestionType = "Text";
        } else if(surveyQuestion.getQuestionType().equals("TEXT_AREA")) {
            expectedQuestionType = "Comment";
        } else if(surveyQuestion.getQuestionType().equals("MATRIX")) {
            if (surveyQuestion.isSingleAnswer()) {
                expectedQuestionType = "MatrixSingleAnswer";
            } else {
                expectedQuestionType = "MatrixMultipleAnswers";
            }
        } else if(surveyQuestion.getQuestionType().equals("RATING"))   {
            expectedQuestionType = "Rating";
        } else if(surveyQuestion.getQuestionType().equals("RANKING")) {
            expectedQuestionType = "Ranking";
        } else if(surveyQuestion.getQuestionType().equals("NPS")) {
            expectedQuestionType = "NPS";
        }
        Log.info("Question Type :" +expectedQuestionType);
        if(expectedQuestionType==null) {
            throw new RuntimeException("Question Type Not Found : " +surveyQuestion.getQuestionType());
        }
        return expectedQuestionType;
    }

    public void setQuestionId(SurveyQuestion surQues) {
        String query = resolveStrNameSpace("Select id, Name, JBCXM__ParentQuestion__c, JBCXM__DisplayOrder__c, JBCXM__SurveyMaster__c, JBCXM__Title__c, JBCXM__Type__c From JBCXM__SurveyQuestion__c where " +
                            "JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' and JBCXM__IsActive__c = "+surQues.isActive()+" and JBCXM__isRequired__c ="+surQues.isRequired()+" and JBCXM__PageInfo__c ='"+surQues.getPageId()+"' and JBCXM__Type__c = '"+getQuestionType(surQues)+"' order by createdDate desc");
        Log.info("Query to get survey question ID : "+query);
        SObject[] sObjects = sfdc.getRecords(query);
        Log.info("No of records returned : "+sObjects.length);
        boolean flag = false;
        for(SObject surQuesObj  : sObjects) {
            String questionText = surQuesObj.getField(resolveStrNameSpace("JBCXM__Title__c")).toString();
            System.out.println("Expected Question Text : "+surQues.getQuestionText().toLowerCase());
            System.out.println("Actual Question Text : " +questionText.toLowerCase());
            if(questionText.toLowerCase().contains(surQues.getQuestionText().toLowerCase())) {
                flag = true;
                String questId = surQuesObj.getId();
                Log.info("Question Id : "+questId);
                surQues.setQuestionId(questId);
            } else {
                System.out.println("Question Not Matched");
            }

        }
        if(!flag){
            throw new RuntimeException("No Survey Question Found with this name : " +surQues.getQuestionText());
        }
    }

    ////TODO - Comments Question Should be implemented.
    public void setSubQuestionsId(SurveyQuestion surQues) {
        if(surQues.getSubQuestions() ==null && surQues.getSubQuestions().size()==0) {
            throw new RuntimeException("No Sub Questions to populate the information.");
        }
        String query = resolveStrNameSpace("Select id, Name, JBCXM__ParentQuestion__c, JBCXM__DisplayOrder__c, JBCXM__SurveyMaster__c, JBCXM__Title__c, JBCXM__Type__c From JBCXM__SurveyQuestion__c where " +
                "JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' and JBCXM__ParentQuestion__c='"+surQues.getQuestionId()+"'  order by createdDate desc");
        Log.info("Query to get all the sub questions: " +query);
        SObject[] sObjects = sfdc.getRecords(query);
        if(!(sObjects.length>=1)) {
            throw new RuntimeException("No Sub Questions to populate Id information.");
        }
        HashMap<String, String> tempMap = new HashMap<>();
        for(SObject sObject : sObjects) {
            Log.info(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString());
            tempMap.put(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString(), sObject.getId());
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
        if(surQues.getQuestionType().equals("RATING") || surQues.getQuestionType().equals("RANKING")
                ||surQues.getQuestionType().equals("TEXT_AREA") || surQues.getQuestionType().equals("TEXT_INPUT")
                || surQues.getQuestionType().equals("NPS")) {
            Log.error("No Answer ID's are supported currently, To be added");
            return;
        }
        if(surQues.getAllowedAnswers() ==null && surQues.getAllowedAnswers().size()==0) {
            throw new RuntimeException("No Choice information to populate the information, Check your data");
        }
        String query = resolveStrNameSpace("Select Id, JBCXM__SurveyMaster__c, JBCXM__SurveyQuestion__c, JBCXM__Title__c, JBCXM__IsActive__c, JBCXM__DisplayOrder__c, JBCXM__AllowOtherLabel__c from JBCXM__SurveyAllowedAnswers__c " +
                            "Where JBCXM__SurveyMaster__c='"+surQues.getSurveyProperties().getsId()+"' and JBCXM__SurveyQuestion__c='"+surQues.getQuestionId()+"' ");
        Log.info("Query to get all the answers : " +query);
        SObject[] sObjects = sfdc.getRecords(query);
        if(!(sObjects.length>=1)) {
            throw new RuntimeException("No Sub Questions to populate Id information.");
        }
        HashMap<String, String> tempMap = new HashMap<>();
        for(SObject sObject : sObjects) {
            if(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")) !=null) {
                Log.info(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString());
                tempMap.put(sObject.getField(resolveStrNameSpace("JBCXM__Title__c")).toString(), sObject.getId());
            }
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
        setAnsChoicesId(surveyQuestion);
        if(surveyQuestion.getQuestionType().equalsIgnoreCase("MATRIX")) {
            setSubQuestionsId(surveyQuestion);
        }
        return surveyQuestionPage;
    }

    public void verifyQuestionDisplayed(SurveyQuestionPage surveyQuestionPage, SurveyQuestion surQues) {
        WebElement surQuesEle = surveyQuestionPage.getQuestionElement(surQues);
        Assert.assertTrue(surveyQuestionPage.isQuestionTitleDisplayed(surQues), "Checking question title");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionType(surQuesEle, surQues) , "Checking question type");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionStatus(surQuesEle, surQues) , "Checking question status");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionRequired(surQuesEle, surQues) , "Checking question mandatory");
        Assert.assertTrue(surveyQuestionPage.verifySurveyQuestionAnswers(surQuesEle, surQues) , "Checking answers");
    }
    
    public void Create_Custom_Obj_For_Addparticipants() throws Exception{
    	
        metadataClient.createCustomObject("EmailCustomObjct");
        String TextField[]={"Dis Name", "Dis Role"};
        String Email[]={"Dis Email"};
        String C_Reference="C_Reference";
        String ReferenceTo="Account";  //Reference to User Object
        String ReleationShipName="Accountss_AutomationnS"; //Relation Name
        String LookupFieldName[]={C_Reference} , Reference[]={ReferenceTo,ReleationShipName};
        metadataClient.createTextFields("EmailCustomObjct__c", TextField, false, false,true, false, false);
        metadataClient.createEmailField("EmailCustomObjct__c", Email);
        metadataClient.createLookupField("EmailCustomObjct__c", LookupFieldName, Reference );
        metaUtil.createExtIdFieldForCustomObject(sfdc, sfinfo);
    }
    
    
    
}
