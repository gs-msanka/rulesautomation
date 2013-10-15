package com.gainsight.sfdc.survey.pages;

public class AddQuestionsPage extends SurveyDesignPage {

	private final String READY_INDICATOR = "//select[@title='Question Type']";
	private final String QUESTION_TYPE = "//select[@class='jbaraQuestionTypeList']";
	private final String QUESTION_TEXTAREA = "//textarea[@id='dummyJbaraQuestionTitle']";

	
	private final String ADD_QUESTION = "//input[@value='Add Question']";
	private final String CANCEL_QUESTION = "//input[@value='Cancel']";
	
	public AddQuestionsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

	public void multiChoiceSingleAnswerRadioQuestion(String question) {

		driver.switchTo().frame("");
//		item.selectFromDropDown(QUESTION_TYPE,"Multiple Choice - Single answer (Radio)");

		item.setText(QUESTION_TEXTAREA, question);
		item.setText("//ul[@id='answerUlContainer']/li/input", "Answer1");
		item.setText("//ul[@id='answerUlContainer']/li[2]/input", "Answer2");
		item.click("//ul[@id='answerUlContainer']/li[2]/div/span[2]");
		item.setText("//ul[@id='answerUlContainer']/li[3]/input", "Answer3");
		item.click("id=chkAnswerOptionAllowOther");
		item.click("id=chkQuestionAllowComments");
		
	}

	public void multiChoiceMultipleAnswerCheckboxesQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Multiple Choice - Multiple answers (Checkboxes)");

	}

	public void multiChoiceSingleAnswerDropdownMenuQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Multiple Choice - Single answer (dropdown menu)");

	}

	public void multiChoiceMultipleAnswerDropdownMenuQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Multiple Choice - Multiple answer (dropdown menu)");

	}

	public void openTextSingleLineQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Open Text - Single line");

	}

	public void openTextCommentsQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Open Text - Comments");

	}

	public void matrixSingleAnswerPerRowRadioQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Matrix - Single answer per row (radio)");

	}
	public void matrixMultipleAnswersPerRowCheckboxesQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Matrix - Multiple answers per row (checkboxes)");

	}

	public void npsSinpleAnswerPerRowQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"NPS - Single answer per row (radio)");

	}
	
	public void rankingMultipleAnswerQuestion() {

		driver.switchTo().frame("");
		item.selectFromDropDown(QUESTION_TYPE,
				"Ranking- Multiple answer");

	}
	
	public SurveyDesignPage addQuestion(){
		
		item.click(ADD_QUESTION);
		return new SurveyDesignPage();
	}

	public SurveyDesignPage cancelQuestion(){
		
		item.click(CANCEL_QUESTION);
		return new SurveyDesignPage();
	}
}
