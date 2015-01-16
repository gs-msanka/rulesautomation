package com.gainsight.sfdc.survey.pages;

import java.util.ArrayList;

import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion.SurveyAllowedAnswer;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyQuestionPage extends SurveyDesignPage {

	private final String QUESTIONS_PAGE = "//a[contains(@class,'sub-menu-option  sub-opt-questions')]";
	private final String VERIFYDEFAULTPAGE_DISPLAYED = "//div[@class='page-tool-bar clearfix']/descendant::h3[@class='page-title']";
	private final String CREATE_PAGE = "//input[@class='gs-btn btn-save pull-right addqtn-dropdown add-page']";
	private final String CLICKON_QUESTIONICON = "//a[@class='page-up-down' and @title='Add question']//span[@class='add-qst-icon']";
	private final String QUESTION_DROPDOWNMENU = "//div[@class='qtntypes pull-right dropdown clearfix']";
	// "//div[@class='qtntypes pull-right dropdown clearfix']//ui[@class='dropdown-menu addqtn-dropdown-menu']//span[@class='top-arrow']"
	private final String MULTIPLECHOICE_SINGLESELECT = "//li//a[@data-label='Multiple choice' and @data-type ='MULTIPLE_CHOICE_QUESTION' and @data-control='CHECKBOX' and @data-multiple='true']//i[@class='mco']";
	private final String MULTIPLECHOICE_MULTISELECT = "//li//a[@data-label='Multiple choice' and @data-type ='MULTIPLE_CHOICE_QUESTION'  and @data-control='CHECKBOX' and @data-multiple='true']//i[@class='mco']";
	private final String DROPDOWN_SINGLESELECT = "//li//a[@data-label='Dropdown' and @data-type ='MULTIPLE_CHOICE_QUESTION' and @data-control='SELECT' and @data-multiple='true']//i[@class='dropdown']";
	private final String DROPDOWN_MULTISELECT = "//li//a[@data-label='Dropdown' and @data-type ='MULTIPLE_CHOICE_QUESTION' and @data-control='SELECT' and @data-multiple='true']//i[@class='dropdown']";
	private final String NPS_QUESTION = "//li//a[@data-label='NPS' and @data-type ='NPS_QUESTION' and @data-control='CHECKBOX' and @data-multiple='true']//i[@class='nps']";
	private final String CLICK_SHOWHEADER_CHECKBOX ="//span[@class='f-left line-height30 pull-left']//input[@type='checkbox']";
	private final String NPS_HEADER_DROPDOWN = "//select[@class='/following-sibling::/option[@value='%s']";
//	private final String SELECT_HEADER_TEXT ="//select[@class='form-control header-type-select pull-left mag-left10']//option[contains(@value, 'Text')]";
//	private final String SELECT_HEADER_SMILEY = "//select[@class='form-control header-type-select pull-left mag-left10']//option[contains(@value, 'Icon')]";
	private final String RATING_QUESTION = "//li//a[@data-label='Rating' and @data-type ='RATING_QUESTION' and @data-control='RATING' and @data-multiple='true']//i[@class='rating']";
	private final String RANKING_QUESTION = "//li//a[@data-label='Ranking' and @data-type ='RANKING_QUESTION' and @data-control='RANKING' and @data-multiple='true']//i[@class='ranking']";
	private final String SHORT_TEXT = "//li//a[@data-label='Short Text' and @data-type ='TEXT_QUESTION' and @data-control='TEXT_INPUT' and @data-multiple='']//i[@class='shrttxt']";
	private final String LONG_TEXT = "//li//a[@data-label='Long Text' and @data-type ='TEXT_QUESTION' and @data-control='TEXT_AREA' and @data-multiple='']//i[@class='longtxt']";
	private final String MATRIX_SINGLESELECT = "//li//a[@data-label='Matrix' and @data-type ='MATRIX_QUESTION' and @data-control='MATRIX' and @data-multiple='true']//i[@class='matrix']";
	private final String MATRIX_MULTISELECT = "//li//a[@data-label='Matrix' and @data-type ='MATRIX_QUESTION' and @data-control='MATRIX' and @data-multiple='true']//i[@class='matrix']";
	private final String QUESTION_TEXTAREA = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//textarea[@class='form-control inputstyle' and @placeholder ='Enter the question here.']";
	private final String ENTERSUBQUESTIONS_TEXTAREA ="//li[%d]/input[@class='form-control inputstyle-small pull-left' and @type = 'text' and @placeholder = 'Enter option']";
	private final String ENTERCHOICES_TEXTAREA = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//li[%d]/input[@class='form-control inputstyle-small pull-left' and @type = 'text' and @placeholder = 'Enter option']";
	private final String CLICKON_ADDCHOICE_ICON = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//div[@class='addmail option-add-delete']/descendant::a[@class='mailadd' and @data-action='ADD']";
	private final String CLICKON_REMOVECHOICE_ICON = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//div[@class='addmail option-add-delete']/descendant::a[@class='maildelete' and @data-action='DELETE']";
	private final String SETTINGS_ACTIVE_CHECKBOX = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//label[contains(text(),'Active')]/following-sibling::div/input";
	private final String SETTINGS_ALLOWCOMMENT_CHECKBOX = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//label[contains(text(),'Allow Comment')]/following-sibling::div/div/input";
	private final String SETTINGS_COMMENT_LABEL = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//input[@class='form-control' and @type='text' and @value='Comments']";
	// private final String SETTINGS_IMAGEUPLOAD = "";
	// private final String SETTINGS_IMAGECAPTION = "";
	private final String SETTINGS_ADDOTHERS_CHECKBOX = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//label[contains(text(),'Add others')]/following-sibling::div/div/input";
	private final String SETTINGS_OTHERSLABEL = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//input[@class='form-control' and @type='text' and @value='Other']";
	private final String SETTINGS_SINGLEANSWER_CHECKBOX = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//label[contains(text(),'Single Answer')]/following-sibling::div/input";
	private final String SETTINGS_ANSWER_REQUIREDCHECKBOX = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//label[contains(text(),'Answer Required')]/following-sibling::div/input";
	private final String SAVE_QUESTION = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//a[@class='custom-tt btn-xs gs-btn btn-save' and @title='Save']";
	private final String CANCEL_QUESTION = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//a[@class='custom-tt btn-xs gs-btn btn-cancel' and @title='Cancel']";
	private final String DUPLICATE_QUESTION = "//div[@class='qtn-div ui-draggable' and @data-order='%d']//a[@class='custom-tt custom-tt' and @title ='Duplicate Question']//span[@class='hduplicate']";
	private final String DELETE_QUESTION = "//a[@class='custom-tt' and @title='Delete']//span[@class='hdelete']";
	private final String EDIT_PAGEICON = "//a[@data-action='EDIT' and @title ='Edit']//span[@class='hedit']";
	private final String EDITPAGE_POPUP = "";
	private final String ENTER_PAGETITLE = "";
	private final String SAVE_PAGE = "";
	private final String CANCEL_PAGE = "";
	private final String DELETE_PAGE = "";

	public SurveyQuestionPage() {
		wait.waitTillElementDisplayed(QUESTIONS_PAGE, MIN_TIME, MAX_TIME);
	}

	public void SurveyDefaultPageVerification() {
		wait.waitTillElementDisplayed(VERIFYDEFAULTPAGE_DISPLAYED, MIN_TIME,
				MAX_TIME);
	}

	public void AddQuestionAndSave(SurveyQuestion ques,int quesNumber) {

		item.click(CLICKON_QUESTIONICON);
		
		// Select the type of question and Fill the question details basing on
		// type of question
		wait.waitTillElementDisplayed(QUESTION_DROPDOWNMENU, MIN_TIME, MAX_TIME);
		if(ques.getquestionType().equalsIgnoreCase("Multiple Choice - Single answer (Radio)")){
			item.click(MULTIPLECHOICE_SINGLESELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Multiple Choice - Multiple answers (Checkboxes)")){
			item.click(MULTIPLECHOICE_MULTISELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Multiple Choice - Single answer (dropdown menu)")){
			item.click(DROPDOWN_SINGLESELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Multiple Choice - Multiple answer (dropdown menu)")){
			item.click(DROPDOWN_MULTISELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("NPS - Single answer per row (radio)")){
			item.click(NPS_QUESTION);
			field.selectCheckbox(CLICK_SHOWHEADER_CHECKBOX);
	//		field.selectFromDropDown(NPS_HEADER_DROPDOWN,ques.getAllowedAnswers());
			
			waitTillNoLoadingIcon();
			//select smiley or textt
		}
		else if(ques.getquestionType().equalsIgnoreCase("Rating")){
			item.click(RATING_QUESTION);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Ranking")){
			item.click(RANKING_QUESTION);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Open Text - Single line")){
			item.click(SHORT_TEXT);
			waitTillNoLoadingIcon();
		}
		else if(ques.getquestionType().equalsIgnoreCase("Open Text - Comments")){
			item.click(LONG_TEXT);
			waitTillNoLoadingIcon();
			//row and column selection
		}
		else if(ques.getquestionType().equalsIgnoreCase("Matrix - Single answer per row radio")){
			item.click(MATRIX_SINGLESELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
			enterSubQuestions(ques.getsubQuestions(),quesNumber);
		}
		else if(ques.getquestionType().equalsIgnoreCase("Matrix - Multiple answer per row checkbox")){
			item.click(MATRIX_MULTISELECT);
			waitTillNoLoadingIcon();
			enterAllowedAnswers(ques.getAllowedAnswers(),quesNumber);
			enterSubQuestions(ques.getsubQuestions(),quesNumber);
		}
		field.clearAndSetText(String.format(QUESTION_TEXTAREA,quesNumber), ques.getQuestion());
		fillQuestionSettings(ques,quesNumber);
		item.click(String.format(SAVE_QUESTION,quesNumber));
		waitTillNoLoadingIcon();
	}
	
	
	public SurveyQuestionPage enterAllowedAnswers(ArrayList<SurveyQuestion.SurveyAllowedAnswer> choices,int quesNumber){
		for (int i = 0; i < choices.size(); i++) {
			field.clearAndSetText(
					String.format(ENTERCHOICES_TEXTAREA,quesNumber, i + 1), choices.get(i).getAnsValue());
			if(i==choices.size()-1) break;
			item.click(String.format(CLICKON_ADDCHOICE_ICON,quesNumber));
		}
		return this;
	}
	
	public SurveyQuestionPage enterSubQuestions(ArrayList<SurveyQuestion.SurveySubQuestions> subQues,int quesNumber){
		for (int i = 0; i < subQues.size(); i++) {
			field.clearAndSetText(
					String.format(ENTERSUBQUESTIONS_TEXTAREA,quesNumber, i + 1), subQues.get(i).getsubQuesValue());
			item.click(String.format(CLICKON_ADDCHOICE_ICON,quesNumber));
		}
		return this;
	}
	// Fill other settings
	// NPS = Active, CommentLabel, Image upload, Image caption, answer required
	// Ranking = Active, Allow comment, comment label, image upload, image caption
	// Rating = Active, Allow comment, comment label,Image upload, image caption, answer required
	// Short text = Active, Image upload, image caption and Answer required
	// long text = Active, Image upload, image caption and Answer required
	// MCQ = Active, Allow comments, comments label, Image upload, image caption, Add Others, Others label, Single answer, Answer required.
	// Drop down = Active, Allow comments, comments label, Image upload, image caption , Add Others, Others label, Single answer, Answer required.
	// Matrix = Active, Allow comment, comment label, image upload, image caption, single answer, answer required
	/*
	 * Active - all image & caption - all Comment checkbox - all except
	 * nps,shorttex & long text comment label - all except short and long text
	 * add others - only for mcq and drop down ans req - all except ranking
	 * single ans - mcq,dropdoqn,matrix
	 */
	public SurveyQuestionPage fillQuestionSettings(SurveyQuestion ques,int quesNumber) {
		if (ques.isActive())
			field.selectCheckBox(String.format(SETTINGS_ACTIVE_CHECKBOX,quesNumber));
		// image - TBD
		if (ques.isAllowComment()) {
			if (!ques.getquestionType().equalsIgnoreCase("nps"))
				field.selectCheckbox(String.format(SETTINGS_ALLOWCOMMENT_CHECKBOX,quesNumber));
			field.clearAndSetText(String.format(SETTINGS_COMMENT_LABEL,quesNumber),
					ques.getCommentLabel());
		}
		if (ques.isaddOthers()) {
			field.selectCheckbox(String.format(SETTINGS_ADDOTHERS_CHECKBOX,quesNumber));
			field.clearAndSetText(String.format(SETTINGS_OTHERSLABEL,quesNumber), ques.getotherLabel());
		}
		if (ques.isRequired())
			field.selectCheckbox(String.format(SETTINGS_ANSWER_REQUIREDCHECKBOX,quesNumber));
		if (ques.isSingleAnswer())
			field.selectCheckbox(String.format(SETTINGS_SINGLEANSWER_CHECKBOX,quesNumber));
		return this;
	}

	public void createPage() {
		item.click(CREATE_PAGE);
	}

}
