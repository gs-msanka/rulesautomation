package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.workflow.pojos.CTA;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyRuleProperties;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveySetCTAPage extends SurveyPage {
	
	private final String ADD_NEW_RULE_BUTTON    = "//a[@class='gs-btn btn-add' and contains(text(), 'Rule') and contains(text(), 'Add New')]";
	
	//Adding new Rule
	private final String NEW_AUTOMATED_RULE_BOX = "//p[@class='logic-title-edit' and contains(text(), 'New Automated Rule')]/ancestor::div[@class='alert-rule']";
	private final String DELETE_RULE_ICON       = ".//a[@original-title='Delete' and contains(text(),'DELETE')]";
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
    private final String SAVE_RULE      = "//input[@class='gs-btn btn-save save-editmode' and @value='Save']";
    private final String CANCEL_RULE    = ".//input[@class='gs-btn btn-cancel cancel-editmode' and @value='Cancel']";

    //Question And Answers Area
    private final String LAST_QUES_ANS_DIV      = ".//div[contains(@class, 'question-answer-set')][last()]";
    private final String ADD_QUESTION_ICON      = ".//a[contains(@class, 'add-question-set') and text()='ADD']";
    private final String REMOVE_QUESTION_ICON   = ".//a[contains(@class, 'delete-question-set') and text()='DELETE']";
    private final String QUESTION_SELECT        = ".//select[contains(@class, 'sel-question')]";
    private final String SUB_QUES_SELECT        = ".//select[contains(@class, 'sel-sub-question')]";
    private final String ANSWER_SELECT          = ".//select[contains(@class, 'sel-answer')]";

    private final String PRIORITY_XPATH          = ".//label[contains(text(),'Priority')]/following-sibling::div";
    private final String TYPE_XPATH              = ".//label[contains(text(),'Type')]/following-sibling::div";
    private final String STATUS_XPATH            = ".//label[contains(text(),'Status')]/following-sibling::div";
    private final String REASON_XPATH            = ".//label[contains(text(),'Reason')]/following-sibling::div";
    private final String DATE_XPATH              = ".//label[contains(text(),'Due Date')]/following-sibling::div";
    private final String PLAYBOOK_XPATH         = ".//label[contains(text(),'Playbook')]/following-sibling::div";
    private final String OWNER_FIELD_XPATH      = ".//label[contains(text(),'Owner field')]/following-sibling::div";
    private final String CTA_ON_CARD_VIEW         ="//div[@class='pull-left alerts']/a";
    private final String SET_CTA_PAGE             ="//a[@ref-link='setalerts']";
    private final String NO_RULES_DIV             ="//div[contains(@class, 'no-rule-to-display')]";

	//Edit Rule
	private final String EDIT_RULE_ICON         ="//a[contains(@class, 'showeditmode')]";
	private final String DELETE_ICON_LINKTEXT   ="DELETE";
	
	//Advance Logic WebElements
	private final String ADVANCE_LOGIC_LINK      ="//a[contains(text(),'Advanced Logic')]";
	private final String ADVANCE_LOGIC_FIELD     ="//select[@class='field']/following-sibling::button";
	private final String ADVANCE_LOGIC_OPERATOR  ="//div[contains(@class, 'gs-condition-operator')]/descendant::select/following-sibling::button";
	private final String ADVANCE_LOGIC_INPUT     ="//div[contains(@class, 'value-select')]/descendant::input";
	private final String ADVANCE_CONDITION_DIV   ="//div[@class='disp-filter-criteria']/p";
	
	private final String RULE_SET_STATUS=""; //Active or inactive!

    public SurveySetCTAPage(SurveyProperties surveyProp) {
        super(surveyProp.getSurveyName());
        wait.waitTillElementDisplayed(ADD_NEW_RULE_BUTTON, MIN_TIME, MAX_TIME);
        Log.info("Set CTA Page Loaded Successfully");
    }
    
	public SurveySetCTAPage() {
		System.out.println("Dummy Constructor - Survey SetCTAPage");
	}

	public SurveySetCTAPage clickOnAddNewRule(){
		item.click(ADD_NEW_RULE_BUTTON);
		wait.waitTillElementDisplayed(NEW_AUTOMATED_RULE_BOX, MIN_TIME, MAX_TIME);
        return this;
	}

    public WebElement getRecentAddedRuleEle() {
        return element.getElement(NEW_AUTOMATED_RULE_BOX);
    }


	public SurveySetCTAPage addRule(SurveyCTARule surveyCTARule) {
		clickOnAddNewRule();
		WebElement ruleEle = getRecentAddedRuleEle();
		setCTACriteria(ruleEle, surveyCTARule.getCta(),
				surveyCTARule.getPlaybook(), surveyCTARule.getOwnerField(),
				surveyCTARule.getChatterUpdate());
		setQuestionAndAnswerCriteria(ruleEle,
				surveyCTARule.getSurveyQuestions());
		if (surveyCTARule.getAdvanceValue() == ""
				|| surveyCTARule.getAdvanceValue() == null) {
			ruleEle.findElement(By.xpath(SAVE_RULE)).click();
		} else {
			setAdvancedFilter(surveyCTARule);
			ruleEle.findElement(By.xpath(SAVE_RULE)).click();
		}
		Timer.sleep(5);
		return this;
	}


    public void setCTACriteria(WebElement ruleEle, CTA cta, String playbook, String ownerField, String chatterUpdate) {
        ruleEle.findElement(By.xpath(SET_CTA_SECTION_HEADER)).click();
        ruleEle.findElement(By.xpath(SET_CTA_PRIORITY_DD)).click();
        selectValueInDropDown(cta.getPriority());
        ((JavascriptExecutor)Application.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element.getElement(SAVE_RULE)); 
        Log.info("scrolled till end of page");
        ruleEle.findElement(By.xpath(SET_CTA_STATUS_DD)).click();
        selectValueInDropDown(cta.getStatus());
        ruleEle.findElement(By.xpath(SET_CTA_REASON_DD)).click();
        selectValueInDropDown(cta.getReason());
        ruleEle.findElement(By.xpath(SET_CTA_TYPE)).click();
        selectValueInDropDown(cta.getType());
        selectOwner(ruleEle, cta.getAssignee());
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
            ruleEle.findElement(By.xpath(SET_CTA_OWNER)).click();
            selectValueInDropDown(ownerField);
        }
        if(cta.getComments() !=null) {
            ruleEle.findElement(By.xpath(SET_CTA_COMMENTS)).clear();
            ruleEle.findElement(By.xpath(SET_CTA_COMMENTS)).sendKeys(cta.getComments());
        }
        if(chatterUpdate !=null && chatterUpdate!="") {
            ruleEle.findElement(By.xpath(SET_CTA_CHATTER_UPDATE)).click();
            selectValueInDropDown(chatterUpdate);
        }
    }

    private void selectOwner(WebElement ruleEle, String ownerName) {
        Log.error("Selecting Default Owner");
        ruleEle.findElement(By.xpath(SET_CTA_DEFAULT_OWNER)).click();
        ruleEle.findElement(By.xpath(SET_CTA_DEFAULT_OWNER)).sendKeys(ownerName);
        ruleEle.findElement(By.xpath(SET_CTA_DEFAULT_OWNER)).sendKeys(Keys.ENTER);
        wait.waitTillElementNotDisplayed("//div[@class='search_progress_icon']", MIN_TIME, MAX_TIME);
        ruleEle.findElement(By.xpath(".//li[@class='ui-menu-item' and @role = 'presentation']/a[contains(text(),'"+ownerName+"')]")).click();
        Log.error("Selected Default Owner");
    }

    public boolean verifyCTACriteria(WebElement ruleEle, SurveyCTARule ctaRule) {
        CTA cta = ctaRule.getCta();
        String dueDate = (cta.getDueDate()==null ||cta.getDueDate()=="") ? "5" : cta.getDueDate();
        WebElement ele = ruleEle.findElement(By.xpath(PRIORITY_XPATH));
        System.out.println(ele.getAttribute("text"));
        System.out.println(ele.getText());
        System.out.println(ruleEle.findElement(By.xpath(PRIORITY_XPATH)).getText());
        String a = ruleEle.findElement(By.xpath(PRIORITY_XPATH)).getText();
        System.out.println(ele.getCssValue("text"));
        System.out.println(ele.getAttribute("class"));
        System.out.println(ele.getAttribute("value"));
        String priority = ruleEle.findElement(By.xpath(PRIORITY_XPATH)).getText().trim();
        String type = ruleEle.findElement(By.xpath(TYPE_XPATH)).getText().trim();
        String status = ruleEle.findElement(By.xpath(STATUS_XPATH)).getText().trim();
        String reason = ruleEle.findElement(By.xpath(REASON_XPATH)).getText().trim();
        Log.info("Expected Priority : "+cta.getPriority());
        Log.info("Actual Priority   : "+priority);
        Log.info("Expected Type     : "+cta.getType());
        Log.info("Actual Type       : "+type);
        Log.info("Expected Status   : "+cta.getStatus());
        Log.info("Actual Status     : "+status);
        Log.info("Expected Reason   : "+cta.getReason());
        Log.info("Actual Reason     : "+reason);

        if(!(cta.getType().equals(type) && cta.getReason().equals(reason) && cta.getStatus().equals(status) && cta.getPriority().equals(priority))) {
            Log.error("One of cta parameters type, reason, status, priority not matched");
            return false;
        }

        if(ctaRule.getPlaybook()!=null && ctaRule.getPlaybook()!="") {
            String actualPlaybook = ruleEle.findElement(By.xpath(PLAYBOOK_XPATH)).getText().trim();
            if(!ctaRule.getPlaybook().equals(actualPlaybook)) {
                Log.error("Playbook Details not matched");
                return false;
            }
        }

        if(ctaRule.getOwnerField()!=null && ctaRule.getOwnerField()!="") {
            String actualOwnerField = ruleEle.findElement(By.xpath(OWNER_FIELD_XPATH)).getText().trim();
            if(!(ctaRule.getOwnerField().equals(actualOwnerField))) {
                Log.error("Owner Field Not Matched");
                return false;
            }
        }

        String actualDate = ruleEle.findElement(By.xpath(DATE_XPATH)).getText().trim();
        if(actualDate!=null ) {
            return actualDate.contains(dueDate);
        } else {
            return false;
        }
    }

    private boolean verifyRuleQuestions(WebElement ruleEle, List<SurveyQuestion> surveyQuestions) {
        String QUESTION_DIV = ".//div[@class='display-rule-questions']";
        List<WebElement> questions  = ruleEle.findElement(By.xpath(QUESTION_DIV)).findElements(By.tagName("p"));
        int temp=0;
        for(SurveyQuestion surveyQuestion : surveyQuestions) {
            String actualText = questions.get(temp).getText().trim();
            actualText = actualText.substring(1, actualText.length());
            String expectedText = null;
            if(surveyQuestion.getQuestionType().equals("MATRIX")) {
                expectedText = "If answer to the question : \" " + surveyQuestion.getQuestionText() + " - (" + surveyQuestion.getPageTitle() + ") \" with Sub-Question ";
                expectedText += "\" "+surveyQuestion.getSubQuestions().get(0).getSubQuestionText()+" \" is ";
            } else {
                expectedText = "If answer to the question : \" " + surveyQuestion.getQuestionText() + " - (" + surveyQuestion.getPageTitle() + ") \" is ";
            }
            for (SurveyQuestion.SurveyAllowedAnswer allowedAnswer : surveyQuestion.getAllowedAnswers()) {
                expectedText += allowedAnswer.getAnswerText() + ", ";
            }
            expectedText = expectedText.trim().substring(0, expectedText.length() - 2);

            Log.info("Expected Text : " +expectedText);
            Log.info("Actual Text   : " + actualText);
            if(!actualText.contains(expectedText) && !expectedText.contains(actualText)) {
                Log.error("Question Text not Matched");
                return false;
            }
        }
        Log.info("Question Text Matched");
        return true;
    }

    public boolean verifyRule(SurveyCTARule surveyCTARule) {
        String RULE_DIV = "//div[@class='survey-alert-ctn']/div[@class='alert-rule']";
        Log.info("Checking for all the rules");
        boolean ctaResult = false;
        boolean questionResult = false;
        System.out.println(element.getAllElement(RULE_DIV).size());
        for(WebElement ele : element.getAllElement(RULE_DIV)) {
            ctaResult = verifyCTACriteria(ele, surveyCTARule);
            if(ctaResult) {
                Log.info("Found the criteria match, Checking the questions");
                questionResult = verifyRuleQuestions(ele, surveyCTARule.getSurveyQuestions());
            }
            if(ctaResult && questionResult) {
                Log.info("CTA & Question information matched");
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

	public void setAdvancedFilter(SurveyCTARule surveyCTARule) {
		if (surveyCTARule.getAdvanceField() != null
				|| surveyCTARule.getAdvanceField() != "") {
			item.click(ADVANCE_LOGIC_LINK);
			item.click(ADVANCE_LOGIC_FIELD);
			selectValueInDropDown(surveyCTARule.getAdvanceField());
		}
		if (surveyCTARule.getAdvanceOperator() != null
				|| surveyCTARule.getAdvanceOperator() != "") {
			item.click(ADVANCE_LOGIC_OPERATOR);
			selectValueInDropDown(surveyCTARule.getAdvanceOperator());
		}
		if (surveyCTARule.getAdvanceValue() != null
				|| surveyCTARule.getAdvanceValue() != "") {
			field.clearAndSetText(ADVANCE_LOGIC_INPUT,
					surveyCTARule.getAdvanceValue());
		}
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
        for(SurveyQuestion.SurveyAllowedAnswer allowedAnswer : surveyQuestion.getAllowedAnswers()) {
            temp.add(allowedAnswer.getAnswerText());
        }
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
        throw new RuntimeException("Unable to Find the Question With Given Text, "+quesTitle);
    }
    
	public void clickOnCTACardView() {
		Log.info("Clicking CTA on Card View");
		item.click(CTA_ON_CARD_VIEW);
		wait.waitTillElementDisplayed(ADD_NEW_RULE_BUTTON, MIN_TIME, MAX_TIME);
	}

	public boolean isCTAPageVisible() {
		return element.getElement(SET_CTA_PAGE).isDisplayed();
	}

	public String getNoRulesText() {
		String temp = element.getElement(NO_RULES_DIV).getText().trim();
		Log.info("Text Message is" + " " + temp);
		return temp;
	}
	
	public void deleteExistingRule() {
		link.click(DELETE_ICON_LINKTEXT);
		item.click(DELETE_RULE_CONFIRM);
	}
	
	public boolean getAdvancedCondtion(SurveyCTARule surveyCTARule) {
		String temp = element.getElement(ADVANCE_CONDITION_DIV).getText();
		Log.info("message is" + " " + temp);
		String concatText = surveyCTARule.getAdvanceField() + " "
				+ surveyCTARule.getAdvanceOperator() + " "
				+ surveyCTARule.getAdvanceValue();
		Log.info(concatText);
		return temp.contains(concatText);
	}
	
}
