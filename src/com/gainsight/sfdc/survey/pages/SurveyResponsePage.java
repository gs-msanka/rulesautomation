package com.gainsight.sfdc.survey.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.tests.SurveySetup;
import com.gainsight.sfdc.survey.pojo.SurveyCTARule;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.testdriver.Log;
import com.gainsight.pageobject.core.WebPage;

import static com.gainsight.sfdc.pages.Constants.*;


public class SurveyResponsePage extends WebPage {
	
	//SurveyResponseForm Webelements
	private final String SINGLE_PICKLIST_ANSWER="//div[@class='pull-left']/descendant::input";
	private final String SURVEY_FORM_SUBMIT_BUTTON="//div[@class='savebtns']/descendant::li/a[@data-action='SUBMIT']";
	private final String MATRIX_ANSWER_RADIO1="//div[@class='text-center']/input";
	private final String MATRIX_ANSWER_RADIO2="//table[contains(@class, 'matrix-table')]/descendant::tbody/descendant::td[contains(text(), 'Value')]/ancestor::tr/descendant::div/descendant::input";
	private final String MATRIX_ANSWER_RADIO3="//table[contains(@class, 'matrix-table')]/descendant::tbody/descendant::td[contains(text(), 'Purchase')]/ancestor::tr/descendant::div/descendant::input";
	private final String SINGLE_SELECT_ANSWER_XPATH="//div[@class='preview-answr']/select";
	private final String CHECKBOX_TYPE_XPATH="//div[@class='pull-left']/descendant::input[@type='checkbox']";
	private final String MULTI_SELECT_OPTION_XPATH1="//div[@class='preview-answr']/descendant::select/option[contains(text(),'Sales')]";
	private final String MULTI_SELECT_OPTION_XPATH2="//div[@class='preview-answr']/descendant::select/option[contains(text(),'Installation')]";
	private final String MATRIX_MULTISELECT_XPATH="//table[contains(@class, 'matrix-table')]/descendant::tbody/descendant::td[contains(text(), 'Engage')]/following-sibling::td/div[@class='text-center']/input";
	SurveySetup surveysetup=new SurveySetup();

	
	public void openSurveyForm(CTA cta, SurveyCTARule surveyCTARule) {
		String temp = surveysetup.surveyURL(surveyCTARule);
		Log.info("Survey participant url is " + temp);
		BasePage.open(temp);
		wait.waitTillElementDisplayed(SURVEY_FORM_SUBMIT_BUTTON, MIN_TIME,
				MAX_TIME);
		submitAnswer(cta, surveyCTARule);
	}
	
	public void submitAnswer(CTA cta, SurveyCTARule surveyCTARule) {
		if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("singlePicklist")) {
			Log.info("Submitting the survey response for singlepicklist - question type");
			item.click(SINGLE_PICKLIST_ANSWER);
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("matrixSingleAns")) {
			Log.info("Submitting the survey response for Matrix single answer - question type");
			item.click(MATRIX_ANSWER_RADIO1);
			item.click(MATRIX_ANSWER_RADIO2);
			item.click(MATRIX_ANSWER_RADIO3);
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("singleSelect")) {
			Log.info("Submitting the survey response for Single Select - question type");
			element.selectFromDropDown(SINGLE_SELECT_ANSWER_XPATH,
					"Any time of the day on Weekends");
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("checkBox")) {
			Log.info("Submitting the survey response for CheckBox - question type");
			element.selectCheckBox(CHECKBOX_TYPE_XPATH);
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals("multiSelect")) {
			Log.info("Submitting the survey response for MultiSelect - question type");
			item.click(MULTI_SELECT_OPTION_XPATH1);
			item.click(MULTI_SELECT_OPTION_XPATH2);
		} else if (surveyCTARule.getQuestionType() != null
				&& surveyCTARule.getQuestionType().equals(
						"MatrixMultipleAnswers")) {
			Log.info("Submitting the survey response for Matrix MultipleAnswers - question type");
			List<WebElement> checkboxlist = element
					.getAllElement(MATRIX_MULTISELECT_XPATH);
			Log.info("CheckBox length is " + checkboxlist.size());
			for (WebElement loop : checkboxlist) {
				loop.click();
			}
		}
		item.click(SURVEY_FORM_SUBMIT_BUTTON);
		BasePage.navigateBack();
	}
}
