package com.gainsight.sfdc.survey.pages;

import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.workflow.pojos.CTA;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyRuleProperties;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveySetCTAPage extends SurveyPage {
	
	private final String ADD_NEW_RULE_BUTTON="//a[@class='gs-btn btn-add' and contains(text(),'Add New Rule')]";
	
	//Adding new Rule
	private final String NEW_AUTOMATED_RULE_BOX = "//div[@class='alert-rule']/descendant::p[@class='logic-title-edit' and contains(tetx()='New Automated Rule')]";
	private final String DELETE_RULE_ICON       = "//div[@class='add-actions pull-right']//a[@original-title='Delete' and contains(text(),'DELETE')]";
	private final String DELETE_RULE_CONFIRM    = "//input[@type='button' and contains(@class,'gs-btn btn-save btn_save saveSummary')]";

	//Advanced logic selection for Rule
	private final String ADV_LOGIC_COLLAPSED    = "//a[@data-toggle='collapse' and @class='btn-slide btn-slide-all collapsed']";
	private final String ADV_LOGIC_EXPANDED     = "//a[@data-toggle='collapse' and @class='btn-slide btn-slide-all']";

	//Set CTA properties
    private final String SET_CTA_SECTION_HEADER = ".//li[contains(@class, 'alert-rule-tab') and @data-action='CTA']/a";
	private final String SET_CTA_PRIORITY_DD    = ".//select[@class='alertSeverity form-select']/following-sibling::button";
	private final String SET_CTA_STATUS_DD      = ".//select[@class='alertStatus form-select']/following-sibling::button";
	private final String SET_CTA_REASON_DD      = ".//select[@class='alertReason form-select']/following-sibling::button";
    private final String SET_CTA_TYPE           = ".//select[@class='alertType form-select']/following-sibling::button";
    private final String SET_CTA_PLAYBOOK       = ".//select[@class='form-select playBook']/following-sibling::button";
    private final String SET_CTA_OWNER          = ".//select[@class='taskOwnerField form-select']/following-sibling::button";
    private final String SET_CTA_CHATTER_UPDATE = ".//select[@class='comments_post_frequency form-select']/following-sibling::button";
    private final String SET_CTA_DUE_DATE       = ".//div[@class='dueDate']/input[contains(@class, 'formControlDataInput')]";
    private final String SET_CTA_DEFAULT_OWNER  = ".//div[@class='gs_search_section']/input[@class='search_input form-control ui-autocomplete-input']";
    private final String SET_CTA_COMMENTS       = ".//div[@class='form-control alertComment']";

    //Save Rule
    private final String SAVE_RULE      = ".//input[@class='gs-btn btn-save save-editmode' and @value='Save']";
    private final String CANCEL_RULE    = ".//input[@class='gs-btn btn-cancel cancel-editmode' and @value='Cancel']";

    //Question And Answers Area
    private final String LAST_QUES_ANS_DIV      = ".//div[contains(@class, 'question-answer-set')][last()]";
    private final String ADD_QUESTION_ICON      = ".//a[contains(@class, 'add-question-set') and text()='ADD']";
    private final String REMOVE_QUESTION_ICON   = ".//a[contains(@class, 'delete-question-set') and text()='DELETE']";
    private final String QUESTION_SELECT        = ".//select[contains(@class, 'sel-question')]";
    private final String SUB_QUES_SELECT        = ".//select[contains(@class, 'sel-sub-question')]";
    private final String ANSWER_SELECT          = ".//select[contains(@class, 'sel-answer')]";


	//Edit Rule
	private final String EDIT_RULE_ICON="";
	
	private final String RULE_SET_STATUS=""; //Active or inactive!

    public SurveySetCTAPage() {
        wait.waitTillElementDisplayed(ADD_NEW_RULE_BUTTON, MIN_TIME, MAX_TIME);
        Log.info("Set CTA Page Loaded Successfully");
    }

	public SurveySetCTAPage clickOnAddNewRule(){
		item.click(ADD_NEW_RULE_BUTTON);
		waitTillNoLoadingIcon();
		wait.waitTillElementDisplayed(NEW_AUTOMATED_RULE_BOX, MIN_TIME, MAX_TIME);
		return this;
	}

    public void setCTACriteria(WebElement ruleEle, CTA cta, String playbook, String ownerField, String chatterUpdate) {
        ruleEle.findElement(By.xpath(SET_CTA_SECTION_HEADER)).click();
        ruleEle.findElement(By.xpath(SET_CTA_PRIORITY_DD)).click();
        selectValueInDropDown(cta.getPriority());
        ruleEle.findElement(By.xpath(SET_CTA_STATUS_DD)).click();
        selectValueInDropDown(cta.getStatus());
        ruleEle.findElement(By.xpath(SET_CTA_REASON_DD)).click();
        selectValueInDropDown(cta.getReason());
        ruleEle.findElement(By.xpath(SET_CTA_TYPE)).click();
        selectValueInDropDown(cta.getType());
        ruleEle.findElement(By.xpath(SET_CTA_OWNER)).clear();
        ruleEle.findElement(By.xpath(SET_CTA_OWNER)).sendKeys(cta.getDueDate());
        //TODO - owner search pending
        if(cta.getDueDate() != null) {
            ruleEle.findElement(By.xpath(SET_CTA_DUE_DATE)).clear();
            ruleEle.findElement(By.xpath(SET_CTA_DUE_DATE)).sendKeys(cta.getDueDate());
        }
        if(playbook!=null && playbook!="") {
            ruleEle.findElement(By.xpath(SET_CTA_PLAYBOOK)).click();
            selectValueInDropDown(playbook);
        }
        if(ownerField!=null && ownerField!="") {
            ruleEle.findElement(By.xpath(SET_CTA_DEFAULT_OWNER)).click();
            selectValueInDropDown(ownerField);
        }
        if(cta.getComments() !=null) {
            ruleEle.findElement(By.xpath(SET_CTA_COMMENTS)).clear();
            ruleEle.findElement(By.xpath(SET_CTA_COMMENTS)).sendKeys(cta.getDueDate());
        }
        if(chatterUpdate !=null && chatterUpdate!="") {
            ruleEle.findElement(By.xpath(SET_CTA_CHATTER_UPDATE)).click();
            selectValueInDropDown(chatterUpdate);
        }
    }

    public boolean verifyCTACriteria(WebElement ruleEle, CTA cta, String playbook, String ownerField) {
        String ctaXPath = ".//div[@class='alertmsg display-rule-alert']" +
                "/descendant::label[contains(text(),'Priority')]/following-sibling::div[text()='"+cta.getPriority()+"']/ancestor::div[@class='alertmsg display-rule-alert']" +
                "/descendant::label[contains(text(),'Type')]/following-sibling::div[text()='"+cta.getType()+"']/ancestor::div[@class='alertmsg display-rule-alert']" +
                "/descendant::label[contains(text(),'Status')]/following-sibling::div[text()='"+cta.getStatus()+"']/ancestor::div[@class='alertmsg display-rule-alert']" +
                "/descendant::label[contains(text(),'Reason')]/following-sibling::div[text()='"+cta.getReason()+"']/ancestor::div[@class='alertmsg display-rule-alert']";
        if(playbook!=null) {
            ctaXPath+="/descendant::label[contains(text(),'Playbook')]/following-sibling::div[text()='"+playbook+"']/ancestor::div[@class='alertmsg display-rule-alert']";
        }
        if(ownerField!=null) {
            ctaXPath+="/descendant::label[contains(text(),'Owner field')]/following-sibling::div[text()='"+ownerField+"']/ancestor::div[@class='alertmsg display-rule-alert']";
        }
        String dueDate = (cta.getDueDate()==null ||cta.getDueDate()=="") ? "5" : cta.getDueDate();
            ctaXPath+="/descendant::label[contains(text(),'Due Date')]/following-sibling::div[text()='"+dueDate+"']/ancestor::div[@class='alertmsg display-rule-alert']";

        Log.info("CTA XPath = " +ctaXPath);
        return isElementPresentAndDisplayed(ruleEle, ctaXPath);
    }

    private boolean verifyRuleQuestions(WebElement ruleEle, List<SurveyQuestion> surveyQuestions) {
        boolean result = true;
        for(SurveyQuestion surveyQuestion : surveyQuestions) {
            String questionXpath = ".//p[contains(text(), 'If answer to the question : \" "+surveyQuestion.getQuestionText()+" - ("+surveyQuestion.getPageTitle()+") \" is ')]";
            String ansText="";
            for(SurveyQuestion.SurveyAllowedAnswer allowedAnswer: surveyQuestion.getAllowedAnswers()) {
                ansText+= allowedAnswer.getAnswerText()+",";
            }
            ansText=ansText.substring(0, ansText.length()-1);
            questionXpath+="/span[contains(text(), '"+ansText+"')]";
            if(!isElementPresentAndDisplayed(ruleEle, questionXpath)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public boolean verifyRule(SurveyCTARule surveyCTARule) {
        String RULE_DIV = "//div[@class='survey-alert-ctn']/div[@class='alert-rule']";
        Log.info("Checking for all the rules");
        boolean ctaResult = false;
        boolean questionResult = false;
        for(WebElement ele : element.getAllElement(RULE_DIV)) {
            ctaResult = verifyCTACriteria(ele, surveyCTARule.getCta(), surveyCTARule.getPlaybook(), surveyCTARule.getOwnerField());
            if(ctaResult) {
                Log.info("Found the criteria match, Checking the questions");
                questionResult = verifyRuleQuestions(ele, surveyCTARule.getSurveyQuestions());
            }
            if(ctaResult && questionResult) {
                return true;
            }
            questionResult = false;
        }
        return false;
    }

    public void setQuestionAndAnswerCriteria(WebElement ruleEle, List<SurveyQuestion> surveyQuestions) {
        boolean flag = false;
        for(SurveyQuestion surQues : surveyQuestions) {
            if(flag) {
                ruleEle.findElement(By.xpath(ADD_QUESTION_ICON)).click();
            }
            flag=true;
            fillQuestionAndAnswer(ruleEle, surQues);
        }
    }

    //TODO - Action Item.
    public void setAdvancedFilter() {

    }

    private void fillQuestionAndAnswer(WebElement ruleEle, SurveyQuestion surveyQuestion) {
        WebElement quesEle = getRecentAddedQuestionEle(ruleEle);
        List<String> temp = new ArrayList<>();
        temp.add(surveyQuestion.getQuestionText()+ " - (" + surveyQuestion.getPageTitle() + ")");
        selectValueFromDropDown(quesEle.findElement(By.xpath(QUESTION_SELECT)),temp);
        if(surveyQuestion.getQuestionType().equals("MATRIX")) {
            temp.clear();
            temp.add(surveyQuestion.getSubQuestions().get(0).getSubQuestionText());
            selectValueFromDropDown(quesEle.findElement(By.xpath(SUB_QUES_SELECT)), temp);
        }
        temp.clear();
        temp.add(surveyQuestion.getAllowedAnswers().get(0).getAnswerText());
        selectValueFromDropDown(quesEle.findElement(By.xpath(ANSWER_SELECT)), temp);
    }

    private WebElement getRecentAddedQuestionEle(WebElement ruleEle) {
        Log.info("Returning the Latest Added Question/Answer DIV");
        return ruleEle.findElement(By.xpath(LAST_QUES_ANS_DIV));
    }

    private WebElement getQuestionEle(WebElement ruleEle, String quesTitle) {
        for(WebElement wEle : ruleEle.findElements(By.xpath(QUESTION_SELECT))){
            Select dropDown = new Select(wEle);
            String selectValue = dropDown.getFirstSelectedOption().toString().trim();
            if(selectValue.contains(quesTitle)) {
                WebElement e = ruleEle.findElement(By.xpath("../.."));
                return e;
            }
        }
        throw new RuntimeException("Unable to Find the Question With Given Text");
    }



}
