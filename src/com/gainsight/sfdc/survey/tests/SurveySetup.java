package com.gainsight.sfdc.survey.tests;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.stringtemplate.v4.compiler.STParser.element_return;
import org.testng.Assert;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.survey.pages.SurveyQuestionPage;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.sforce.soap.partner.sobject.SObject;

public class SurveySetup extends BaseTest {
	
	private final static String PICK_LIST_QUERY  = "Select id, Name, JBCXM__Category__c, JBCXM__SystemName__c from JBCXM__PickList__c Order by JBCXM__Category__c, Name";
	private final static String CTA_TYPES_QUERY  = "Select id, Name, JBCXM__Type__c, JBCXM__DisplayOrder__c, JBCXM__Color__c from JBCXM__CTATypes__c";
	private static SFDCInfo sfdcInfo = SFDCUtil.fetchSFDCinfo();
	private RuleEngineDataSetup ruleEngineDataSetup;
	private static HashMap<String, String> suveyQus;
	private static HashMap<String, String> ctaTypesMap;
	private static HashMap<String, String> PickListMap;
	private static HashMap<String, String> playBook;
	private static HashMap<String, String> surveyAns;

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
    
    public void logicRules(SurveyQuestionPage surveyQuestionPage){
    	surveyQuestionPage.addLogicRules();
      
    }

    public void verifyQuestionDisplayed(SurveyQuestionPage surveyQuestionPage, SurveyQuestion surQues) {
        WebElement surQuesEle = surveyQuestionPage.getQuestionElement(surQues);
        Assert.assertTrue(surveyQuestionPage.isQuestionTitleDisplayed(surQues), "Checking question title");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionType(surQuesEle, surQues) , "Checking question type");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionStatus(surQuesEle, surQues) , "Checking question status");
        Assert.assertTrue(surveyQuestionPage.verifyQuestionRequired(surQuesEle, surQues) , "Checking question mandatory");
        Assert.assertTrue(surveyQuestionPage.verifySurveyQuestionAnswers(surQuesEle, surQues) , "Checking answers");
    }
    
    public void create_Custom_Object_For_Addparticipants() throws Exception{
		metadataClient.createCustomObject("EmailCustomObjct");
		String TextField[] = { "Dis Name", "Dis Role" };
		String Email[] = { "Dis Email" };
		String C_Reference = "C_Reference";
		String ReferenceTo = "Account"; // Reference to User Object
		String ReleationShipName = "Accountss_AutomationnS"; // Relation Name
		String LookupFieldName[] = { C_Reference }, Reference[] = {
				ReferenceTo, ReleationShipName };
		metadataClient.createTextFields("EmailCustomObjct__c", TextField,
				false, false, true, false, false);
		metadataClient.createEmailField("EmailCustomObjct__c", Email);
		metadataClient.createLookupField("EmailCustomObjct__c",
				LookupFieldName, Reference);
		metaUtil.createExtIdFieldForCustomObject(sfdc, sfinfo);
	}
    
	public int getRecordCountFromContactObject() {
		int count = sfdc
				.getRecordCount("SELECT Id,name FROM Contact where isDeleted=false");
		Log.info("Count from Object is" + count);
		return count;
	}
    
	public int getRecordCountFromContactObjectWithFilterCond() {
		int count = sfdc
				.getRecordCount(resolveStrNameSpace(("SELECT Id,name FROM Contact where  email like '%gainsighttest.com%'and isDeleted=false")));
		Log.info("Count from contact object is" + count);
		return count;
	}
    
	public void updateNSURLInAppSettings(String NSURL) {
		System.out.println("setting ns url in app settings");
		sfdc.getRecordCount("select id from JBCXM__ApplicationSettings__c");
		sfdc.runApexCode(resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
				+ "appSet.JBCXM__NSURL__c='" + NSURL + "';" + "update appSet;"));
		Log.info("NS URL Updated Successfully");
	}

	public boolean getBranchingField(SurveyQuestion surveyQuestion) {
		Log.info("fetching Records from JBCXM__SurveyQuestion__c Object");
		Timer.sleep(5);
		SObject[] jsondata = sfdc
				.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,JBCXM__HasRules__c FROM JBCXM__SurveyQuestion__c where JBCXM__Type__c='"+surveyQuestion.getQuestionType()+"' order by createdDate desc limit 1"));
		System.out.println(sfdc.getRecords("SELECT Id,JBCXM__Title__c,JBCXM__HasRules__c FROM JBCXM__SurveyQuestion__c where JBCXM__Type__c='"+surveyQuestion.getQuestionType()+"' order by createdDate desc limit 1"));
		boolean result = false;
		if (jsondata.length > 0) {
			String sTemp = (String) jsondata[0].getField("JBCXM__HasRules__c");
			result = Boolean.valueOf(sTemp);
		}
		return result;
	}
	
	public boolean getDependentField() {
		Log.info("fetching Records from JBCXM__SurveyQuestion__c Object");
		Timer.sleep(5);
		SObject[] jsondata = sfdc
				.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__Title__c,JBCXM__Dependent__c FROM JBCXM__SurveyQuestion__c order by createdDate desc limit 1"));
		boolean result = false;
		if (jsondata.length > 0) {
			String sTemp = (String) jsondata[0].getField("JBCXM__Dependent__c");
			result = Boolean.valueOf(sTemp);
		}
		return result;
	}
    
	public void setupRule(HashMap<String, String> testData) {
		// Create Rule in AutomatedAlertHandler
		String apexCodeForRuleCreation = "JBCXM__AutomatedAlertRules__c rule=new JBCXM__AutomatedAlertRules__c(Name='"
				+ testData.get("Name")
				+ "',"
				+ "JBCXM__ruleType__c='"
				+ testData.get("JBCXM__ruleType__c")
				+ "',JBCXM__SourceType__c='"
				+ testData.get("JBCXM__SourceType__c")
				+ "',JBCXM__AdvanceCriteria__c='"
				+ testData.get("JBCXM__AdvanceCriteria__c")
				+ "',JBCXM__PlayBookIds__c='"
				+ getIdResolvedString(testData.get("JBCXM__PlayBookIds__c"))
				+ "',JBCXM__TaskDefaultOwner__c='"
				+ testData.get("JBCXM__TaskDefaultOwner__c")
				+ "',JBCXM__TriggerCriteria__c='"
				+ getIdResolvedString(testData.get("JBCXM__TriggerCriteria__c"))
				+ "',JBCXM__AlertCriteria__c='"
				+ getIdResolvedString(testData.get("JBCXM__AlertCriteria__c"))
				+ "');" + "insert rule;";
		Log.info("Creating Rule...." + apexCodeForRuleCreation);
		sfdc.runApexCode(resolveStrNameSpace(apexCodeForRuleCreation));
	}

	public HashMap<String, String> getMapFromObject(String objName,
			String fieldName, String shortCut) {
		String Query = "SELECT Id," + fieldName + " from " + objName;
		HashMap<String, String> objMap = new HashMap<String, String>();
		SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace(Query));
		Log.info("Total Records : " + objRecords.length);
		for (SObject sObject : objRecords) {
			Log.info("ObjectName:" + objName + "..FieldName : "
					+ sObject.getField(resolveStrNameSpace(fieldName))
					+ " - With Id : " + sObject.getId());
			objMap.put(shortCut + "." + sObject.getField(fieldName).toString(),
					sObject.getId());
		}

		for (Entry<String, String> entry : objMap.entrySet()) {
			Log.info("Key : " + entry.getKey() + " Value : " + entry.getValue());

		}
		return objMap;
	}
    
	private String getIdResolvedString(String string) {
		string = replaceSystemNameInRule(
				replaceSystemNameInRule(
						replaceSystemNameInRule(
								replaceSystemNameInRule(
										replaceSystemNameInRule(string,
												playBook), suveyQus),
								ctaTypesMap), PickListMap), surveyAns);
		return string;
	}
	
	public static String replaceSystemNameInRule(String text,
			HashMap<String, String> replacements) {
		Pattern pattern = Pattern.compile("#(.+?)#");
		return replaceStringWithTokens(text, pattern, replacements);
	}
    
	public static String replaceStringWithTokens(String text, Pattern pattern,
			HashMap<String, String> replacements) {
		Matcher matcher = pattern.matcher(text);
		// populate the replacements map ...
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
			Log.info("Found " + matcher.group());
			String replacement = replacements.get(matcher.group().substring(1,
					matcher.group().length() - 1));
			Log.info("replacement : " + replacement);
			builder.append(text.substring(i, matcher.start()));
			if (replacement == null) {
				builder.append(matcher.group(0));
			} else {
				builder.append(replacement);
			}
			i = matcher.end();
		}
		builder.append(text.substring(i, text.length()));
		Log.info("Replaced String : " + builder.toString());
		return builder.toString();
	}
   
	public void populateObjMaps() {
		suveyQus = getMapFromObject("JBCXM__SurveyQuestion__c",
				"JBCXM__Title__c", "SQ");
		ctaTypesMap = getMapFromObject("JBCXM__CTATypes__c", "JBCXM__Type__c",
				"CT");
		PickListMap = getMapFromObject("JBCXM__PickList__c",
				"JBCXM__SystemName__c", "PL");
		playBook = getMapFromObject("JBCXM__Playbook__c", "Name", "PB");
		surveyAns = getMapFromObject("JBCXM__SurveyAllowedAnswers__c",
				"JBCXM__Title__c", "SA");

	}
	
	public String surveyURL(SurveyCTARule surveyCTARule, HashMap<String, String> testData) {
		Log.info("Fetching Records");
		SObject[] jsondata = sfdc
				.getRecords(resolveStrNameSpace("SELECT Id,JBCXM__DisplayName__c,JBCXM__SurveyTitle__c,JBCXM__SurveyURL__c,JBCXM__SurveyId__c, JBCXM__Token__c FROM JBCXM__SurveyParticipant__c where JBCXM__DisplayName__c='"
						+ testData.get("participantName")
						+ "' order by createddate desc limit 1"));
		String concatUrl = null;
		if (jsondata.length > 0) {
			String sTemp1 = (String) jsondata[0]
					.getField(resolveStrNameSpace("JBCXM__SurveyURL__c"));
			String sTemp2 = (String) jsondata[0].getField(resolveStrNameSpace("JBCXM__SurveyId__c"));
			String sTemp3 = (String) jsondata[0].getField(resolveStrNameSpace("JBCXM__Token__c"));
			String concatUrl1 = sTemp1 + "?surveyId=" + sTemp2
					+ "&participantId=" + sTemp3;
			Log.info("Survey url is " + concatUrl1);
			concatUrl = concatUrl1;
		}
		return concatUrl;
	}
	
	public String surveySiteURL() {
		SObject siteObject[] = sfdc
				.getRecords(resolveStrNameSpace("SELECT Name, Status, Subdomain FROM Site where Status='Active' Limit 1"));
		String publishURL = null;
		if (siteObject.length > 0) {
			String siteURL = "http://"
					+ siteObject[0].getField(resolveStrNameSpace("Subdomain"))
					+ ".force.com/";
			Log.info(" Site url is " + siteURL);
			publishURL = siteURL;
		} else {
			throw new RuntimeException("Site Not Found");
		}
		return publishURL;
	}
	
	public int getCountFromSurveyParticipantObject(SurveyProperties surveyProp) {
		int count = sfdc
				.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyProp) + "' and isDeleted=false")));
		Log.info("Count from SurveyParticipant__c object is " + count);
		return count;
	}

	public int getCountfromCustomObject() {
		WebDriverWait wait = new WebDriverWait(Application.getDriver(), 30);
		return wait.until(new ExpectedCondition<Integer>() {
			@Override
			public Integer apply(WebDriver d) {
				return sfdc
						.getRecordCount("SELECT Id,Name FROM EmailCustomObjct__c where isDeleted=false");
			}
		});
	}
	
/*	public int getCountFromSurveyParticipantsObject(SurveyProperties surveyProp) {
		int count = sfdc
				.getRecordCount(resolveStrNameSpace(("SELECT Id FROM JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c='"
						+ setSurveyId(surveyProp) + "' and isDeleted=false and JBCXM__Sent__c=true")));
		Log.info("Count from SurveyParticipant__c object is " + count);
		return count;
	}*/
	
}