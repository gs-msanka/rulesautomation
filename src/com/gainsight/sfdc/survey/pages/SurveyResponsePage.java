package com.gainsight.sfdc.survey.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.tests.SurveySetup;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.survey.pojo.SurveyResponseAns;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.testdriver.Log;
import com.gainsight.pageobject.core.WebPage;

import static com.gainsight.sfdc.pages.Constants.*;


public class SurveyResponsePage extends WebPage {
	
	//SurveyResponseForm Webelements
	private final String SINGLE_RADIO_ANSWER_XPATH="//div[@class='preview-answr']/descendant::label[contains(., '%s')]/input";
	private final String SURVEY_FORM_SUBMIT_BUTTON="//div[@class='savebtns']/descendant::li/a[@data-action='SUBMIT']";
	private final String MATRIX_SINGLEANSWER_RADIO_XPATH="//table[contains(@class, 'matrix-table')]/descendant::tbody/descendant::td[contains(text(), '%s')]/ancestor::tr/descendant::div/descendant::input";
	private final String SINGLE_SELECT_ANSWER_XPATH="//div[@class='preview-answr']/descendant::select/option[contains(text(),'%s')]";
	private final String MATRIX_MULTISELECT_XPATH="//table[contains(@class, 'matrix-table')]/descendant::tbody/descendant::td[contains(text(), '%s')]/following-sibling::td/div[@class='text-center']/input";
	
	SurveySetup surveysetup=new SurveySetup();
	public String currentURL = null;

	
	public void openSurveyForm(SurveyCTARule surveyCTARule, HashMap<String, String> testData,SurveyResponseAns surveyAns) {
		currentURL=BasePage.getCurrentUrl();
		Log.info("Current url is " +currentURL);
		String temp = surveysetup.surveyURL(surveyCTARule, testData);
		Log.info("Survey participant url is " + temp);
		URL=temp;
		open();
		wait.waitTillElementDisplayed(SURVEY_FORM_SUBMIT_BUTTON, MIN_TIME,
				MAX_TIME);
		submitAnswer(surveyCTARule, testData, surveyAns );
	}
	
	public void submitAnswer(SurveyCTARule surveyCTARule,  HashMap<String, String> testData, SurveyResponseAns surveyAns) {
		if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("singleRadio")) {
			Log.info("Submitting the survey response for singleRadio - question type");
			// item.click(SINGLE_PICKLIST_ANSWER);
			item.click(String.format(SINGLE_RADIO_ANSWER_XPATH,
					surveyAns.getAns1()));
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("matrixSingleAns")) {
			Log.info("Submitting the survey response for Matrix single answer - question type");
			item.click(String.format(MATRIX_SINGLEANSWER_RADIO_XPATH,
					surveyAns.getAns1()));
			item.click(String.format(MATRIX_SINGLEANSWER_RADIO_XPATH,
					surveyAns.getAns2()));
			item.click(String.format(MATRIX_SINGLEANSWER_RADIO_XPATH,
					surveyAns.getAns3()));
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("singleSelect")) {
			Log.info("Submitting the survey response for Single Select - question type");
			item.click(String.format(SINGLE_SELECT_ANSWER_XPATH,
					surveyAns.getAns5()));
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("checkBox")) {
			Log.info("Submitting the survey response for CheckBox - question type");
			item.click(String.format(SINGLE_RADIO_ANSWER_XPATH,
					surveyAns.getAns1()));
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("multiSelect")) {
			Log.info("Submitting the survey response for MultiSelect - question type");
			item.click(String.format(SINGLE_SELECT_ANSWER_XPATH,
					surveyAns.getAns1()));
			item.click(String.format(SINGLE_SELECT_ANSWER_XPATH,
					surveyAns.getAns2()));
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals(
						"MatrixMultipleAnswers")) {
			Log.info("Submitting the survey response for Matrix MultipleAnswers - question type");
			List<WebElement> checkboxlist = element.getAllElement(String
					.format(MATRIX_MULTISELECT_XPATH, surveyAns.getAns1()));
			Log.info("CheckBox length is " + checkboxlist.size());
			for (WebElement loop : checkboxlist) {
				loop.click();
			}
		}
		item.click(SURVEY_FORM_SUBMIT_BUTTON);
		URL = currentURL;
		open(); /*Navigating back to the Gainsight home page*/
	}
}
