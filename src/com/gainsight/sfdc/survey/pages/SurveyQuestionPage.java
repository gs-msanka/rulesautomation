package com.gainsight.sfdc.survey.pages;

import java.util.*;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion.SurveyAllowedAnswer;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;
import com.sforce.soap.partner.sobject.SObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Created by gainsight on 05/12/14.
 */

/*Some Assumptions
    - No 2 Answer texts will be same in the same question.
 */
public class SurveyQuestionPage extends SurveyPage {

	//Top Most Elements
    private final String PAGE_CREATE_BUTTON     = "//input[contains(@class, 'add-page')]";
    private final String COLLAPSE_VIEW          = "//a[@data-action='COLLAPSE']";
    private final String EXPAND_VIEW            = "//a[@data-action='EXPAND']";
    private final String PREVIEW_BUTTON         = "//a[@data-action='PREVIEW']";

    //Page Level Selectors
    private final String PAGE_BLOCK             = "//div[contains(@class, 'page-break-ctn page') and (@data-page-id='%s' or @data-oder='%s')]";
    private final String QUESTIONS_MENU_CSS     = "ul[class='dropdown-menu addqtn-dropdown-menu']";
    private final String QUESTION_TYPE_SELECT_CSS  = "a[data-control=%s]";
    private final String ADD_SECTION_CSS        = "a[data-action=ADD_SECTION]";
    private final String ADD_QUESTION_CSS       = "a[data-action=ADD_QUESTION]";
    private final String EDIT_PAGE_CSS          = "a[data-action=EDIT]";
    private final String DELETE_PAGE_CSS        = "a[data-action='DELETE']";

    private final String PAGE_TITLE_INPUT               = "//input[@class='form-control br-page-title']";
    private final String PAGE_EDIT_FORM_SAVE_BUTTON     = "//button[contains(@class, 'btn-page-br-yes') and text()='Yes']";
    private final String PAGE_EDIT_FORM_CANCEL_BUTTON   = "//button[@class='gs-btn btn-cancel' and text()='Cancel']";

    //Question Edit View Selectors
    private final String QUESTION_BLOCK                 = ".//div[contains(@class, 'qtn-div') and @data-id='%s']";
    private final String QUESTION_TEXT_INPUT            = ".//div[contains(@id, '_qtn_entry')]/textarea[contains(@class, 'form-control')]";
    private final String SECTION_TITLE_INPUT            = ".//h3[@class='section-title drag-handle']/input[@class='form-control']";
    private final String SECTION_TITLE_VIEW             = ".//h3[@class='section-title drag-handle']";
    private final String QUESTION_TITLE_VIEW            = ".//h3[@class='qtn-title drag-handle']/a";
    private final String NPS_SHOW_HEADERS_CHECKBOX_CSS  = "input[id$=_show_header_check][type=checkbox]";
    private final String NPS_HEADER_SELECT_CSS          = "select[class*='form-control header-type-select']";
    private final String RATING_SELECT              = ".//div[contains(@id, 'ans_entry')]/select[@class='form-control']";
    private final String SHORT_TEXT_SIZE_SELECT     = ".//div[contains(@id, 'ans_entry')]/div/select[@class='selectstyle select-size show']";
    private final String LONG_TEXT_ROWS_SELECT      = ".//div[contains(@id, 'ans_entry')]/div/select[@class='selectstyle select-rows show']";
    private final String LONG_TEXT_COLS_SELECT      = ".//div[contains(@id, 'ans_entry')]/div/select[@class='selectstyle select-cols show']";
    private final String QUESTION_SURVEY_BRANCHING      = "a[class=qtn-link-a]";
    private final String ACTIVE_CHECKBOX_CSS        = "input[id$=_active][type=checkbox]";
    private final String COMMENTS_CHECKBOX_CSS      = "input[id$=_cmt_check][type=checkbox]";
    private final String REQUIRED_CHECKBOX_CSS      = "input[id$=_required][type=checkbox]";
    private final String SINGLE_ANS_CHECKBOX_CSS    = "input[id$=_multiple_check][type=checkbox]";
    private final String ADD_OTHERS_CHECKBOX_CSS    = "input[id$=_add_others]";
    private final String ADD_OTHERS_INPUT_CSS       = "input[id$=_add_others_lbl]";
    private final String COMMENTS_INPUT_CSS         = "input[id$=_cmt_lbl]";
    private final String IMG_LOAD_CSS               = "input[id$=_upload_img]";

    //Question Header Options
    private final String QUESTION_EDIT_CSS          = "a[data-action=EDIT]";
    private final String QUESTION_CLONE_CSS         = "a[data-action=DUPLICATE]";
    private final String QUESTION_DELETE_CSS        = "a[data-action=DELETE]";
    private final String QUESTION_SAVE_BUTTON_CSS   = "a[data-action=SAVE]";
    private final String QUESTION_CANCEL_BUTTON_CSS = "a[data-action=CANCEL]";

    private final String POP_DELETE_YES  = "//input[@class='gs-btn btn-save btn_save saveSummary' and @value='Yes']";
    private final String LINK_ICON       ="//ul[@class='radiolist']/li[1]/div[contains(@class, 'attatchicon')]/span[@class='glyphicon glyphicon-link']";
    private final String LOGIC_RULES_DIV ="//div[contains(@class, 'ui-dialog-titlebar')]/span[text()='Link Question']";
    private final String LINK_CHECKBOX   ="//div[contains(@class, 'i-checks pull-left')]/label/input[@type='checkbox']";
    private final String LINK_SAVE_BUTTON="//div[contains(@class, 'text-center')]/button[text()='Save']";
    private final String LOGIC_ATTACH_LINK="//div[@class='qtn-body']/descendant::label[contains(., 'Cochin')]/ancestor::li[@class='clearfix']/div[contains(@class, 'attatchicon')]/span";
    private final String BRANCHING_IN_FIRST_PAGE="//div[contains(@class, 'ui-draggable')]/descendant::div[@class='qtn-link']/a";
    private final String BRANCHING_ICON="//div[contains(@class, 'logicrulepopup')]/descendant::div[contains(@class, 'logic-rule')]/div[@class='col-sm-8']/select";
    private final String BRANCHING_SAVE_ON_POPUP="//div[contains(@class, 'modal-footer')]/button[text()='Save']";
    private final String LOGIC_ATTACHED_ICON="//ul[@class='radiolist']/descendant::div[contains(@class, 'attached')]";
    private final String SECTION_HEADER="//a[@data-action='ADD_SECTION']";
    private final String EDIT_HEADER="//div[contains(@class, 'header-edit-tools')]/descendant::a[@data-action='EDIT']/span";
    private final String SECTION_HEADER_TEXTINPUT="//div[contains(@class, 'section-bar')]/descendant::input";


    //Miscellaneous
    private final static Set<String> supportedQuesTypes;
    static {
        supportedQuesTypes = new HashSet<String>();
        supportedQuesTypes.add("CHECKBOX");
        supportedQuesTypes.add("SELECT");
        supportedQuesTypes.add("NPS");
        supportedQuesTypes.add("RATING");
        supportedQuesTypes.add("RANKING");
        supportedQuesTypes.add("TEXT_INPUT");
        supportedQuesTypes.add("TEXT_AREA");
        supportedQuesTypes.add("MATRIX");
    }

	public SurveyQuestionPage(SurveyProperties surveyProp) {
        super(surveyProp.getSurveyName());
        wait.waitTillElementDisplayed(PREVIEW_BUTTON, MIN_TIME, MAX_TIME);
        waitTillNoLoadingIcon();
	}

    public SurveyQuestionPage clickOnExpandView() {
        item.click(EXPAND_VIEW);
        Timer.sleep(2);
        return this;
    }

    public SurveyQuestionPage clickOnCollapseView() {
        item.click(COLLAPSE_VIEW);
        Timer.sleep(2);
        return this;
    }

    public WebElement getPageElement(SurveyQuestion surveyQuestion) {
        WebElement surQuestionsPageEle = element.getElement(String.format(PAGE_BLOCK, surveyQuestion.getPageId(), surveyQuestion.getPageId()));
        return surQuestionsPageEle;
    }

    public WebElement getPageElement(String pageTitle, String pageId) {
        String PAGE_TITLE_PATH = String.format("//h3[@class='page-title' and text()='%s']/ancestor::div[contains(@class, 'page-break-ctn')]", pageTitle);
        if(pageId!=null) {
            PAGE_TITLE_PATH = String.format("//h3[@class='page-title' and text()='%s']/ancestor::div[contains(@class, 'page-break-ctn') and (@data-page-id='%s' or data-order='%s')]", pageTitle, pageId, pageId);
        }
        return getWebElement(By.xpath(PAGE_TITLE_PATH));
    }

    public WebElement getQuestionElement(final SurveyQuestion surveyQuestion) {
		WebElement surveyQuestionEle;
		CommonWait.waitForCondition(MAX_TIME, MIN_TIME,
				new ExpectedCommonWaitCondition<Boolean>() {
					@Override
					public Boolean apply() {
						return isQuestionExists(surveyQuestion);
					}
				});
		surveyQuestionEle = getPageElement(surveyQuestion).findElement(
				By.xpath(String.format(QUESTION_BLOCK,
						surveyQuestion.getQuestionId())));
		return surveyQuestionEle;
    }
    
	public boolean isQuestionExists(SurveyQuestion surveyQuestion) {
		boolean result = false;
		WebElement surveyQuestionEle = getPageElement(surveyQuestion)
				.findElement(
						By.xpath(String.format(QUESTION_BLOCK,
								surveyQuestion.getQuestionId())));
		if (surveyQuestionEle.isDisplayed()) {
			result = true;
		}
		surveyQuestionEle.isDisplayed();
		return result;
	}

    public SurveyQuestionPage clickOnAddNewQuestion(SurveyQuestion surveyQuestion) {
        WebElement surQuestionsPageEle = getPageElement(surveyQuestion);
        WebElement addQuestionEle = surQuestionsPageEle.findElement(By.cssSelector(ADD_QUESTION_CSS));
        addQuestionEle.click();
        waitTillElementDisplayed(surQuestionsPageEle, QUESTIONS_MENU_CSS, MIN_TIME, MAX_TIME);
        surQuestionsPageEle.findElement(By.cssSelector(String.format(QUESTION_TYPE_SELECT_CSS, surveyQuestion.getQuestionType()))).click();
        waitTillNoLoadingIcon();
        return this;
    }

   /* private void waitTillElementDisplayed(WebElement surQuestionsPageEle,
			String qUESTIONS_MENU_CSS2, int minTime, int maxTime) {
		// TODO Auto-generated method stub
		
	} */

	public void fillQuestionFormInfo(SurveyQuestion surveyQuestion) {
        WebElement surQuestionEle = getQuestionElement(surveyQuestion);
        Log.info("Entering Question Title");
        surQuestionEle.findElement(By.xpath(QUESTION_TEXT_INPUT)).clear();
        surQuestionEle.findElement(By.xpath(QUESTION_TEXT_INPUT)).sendKeys(surveyQuestion.getQuestionText());

        //TO Mark Question Active / InActive
        Log.info("Making question Active / InActive");
        WebElement activeCheckboxEle = surQuestionEle.findElement(By.cssSelector(ACTIVE_CHECKBOX_CSS));
        String attributeValue = activeCheckboxEle.getAttribute("CHECKED");
        if(attributeValue == null && surveyQuestion.isActive()) {
            Log.info("Making Question Active");
            activeCheckboxEle.click();
        } else if(attributeValue != null && !surveyQuestion.isActive()) {
            Log.info("Making Question De-Active");
            activeCheckboxEle.click();
        }

        Log.info("Making question Required / Not Required");
        if(!surveyQuestion.getQuestionType().equals("RANKING")) {
            WebElement requiredCheckEle = surQuestionEle.findElement(By.cssSelector(REQUIRED_CHECKBOX_CSS));
            String attVal = requiredCheckEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isRequired()) {
                Log.info("Marking question as required");
                requiredCheckEle.click();
            } else if(attVal != null && !surveyQuestion.isRequired()) {
                Log.info("Marking question as not required");
                requiredCheckEle.click();
            }
        }

        Log.info("Marking single select / multi select");
        if(surveyQuestion.getQuestionType().equals("CHECKBOX") || surveyQuestion.getQuestionType().equals("SELECT") || surveyQuestion.getQuestionType().equals("MATRIX")){
            WebElement singleSelect = surQuestionEle.findElement(By.cssSelector(SINGLE_ANS_CHECKBOX_CSS));
            String attVal = singleSelect.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isSingleAnswer()) {
                Log.info("Checking Single Select");
                singleSelect.click();
            } else if(attVal != null && !surveyQuestion.isSingleAnswer()) {
                Log.info("Un-Checking Single Select");
                singleSelect.click();
            }
        }

        Log.info("Allow Comments & Comments Label");
        if(surveyQuestion.getQuestionType().equals("MATRIX") || surveyQuestion.getQuestionType().equals("RANKING")
                || surveyQuestion.getQuestionType().equals("RATING") || surveyQuestion.getQuestionType().equals("CHECKBOX")
                ||   surveyQuestion.getQuestionType().equals("SELECT") || surveyQuestion.getQuestionType().equals("NPS")) {
            WebElement commentsEle = surQuestionEle.findElement(By.cssSelector(COMMENTS_CHECKBOX_CSS));
            String attVal = commentsEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isAllowComments()) {
                commentsEle.click();
                if(surveyQuestion.getCommentsLabel() != null) {
                    WebElement commentsLabelEle = surQuestionEle.findElement(By.cssSelector(COMMENTS_INPUT_CSS));
                    commentsLabelEle.clear();
                    commentsLabelEle.sendKeys(surveyQuestion.getCommentsLabel());
                }
            } else if(attVal != null && !surveyQuestion.isAllowComments()) {
                commentsEle.click();
            }
        }

        Log.info("Marking/Un-Marking allowing others");
        if(surveyQuestion.getQuestionType().equals("CHECKBOX")  || surveyQuestion.getQuestionType().equals("SELECT")) {
            WebElement allowOtherEle = surQuestionEle.findElement(By.cssSelector(ADD_OTHERS_CHECKBOX_CSS));
            String attVal = allowOtherEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isAddOther()) {
                allowOtherEle.click();
                WebElement othersLabelEle = surQuestionEle.findElement(By.cssSelector(ADD_OTHERS_INPUT_CSS));
                othersLabelEle.clear();
                othersLabelEle.sendKeys(surveyQuestion.getOtherLabel());
            } else if(attVal != null && !surveyQuestion.isAddOther()) {
                allowOtherEle.click();
            }
        }



        if(surveyQuestion.getQuestionType().equals("TEXT_AREA")) {
            if(!(surveyQuestion.getAllowedAnswers().size() >1)) {
                throw new RuntimeException("Answer options should not be null");
            }
            Select dropdown = new Select(surQuestionEle.findElement(By.xpath(LONG_TEXT_ROWS_SELECT)));
            String s = surveyQuestion.getAllowedAnswers().get(0).getAnswerText();
            System.out.println("******************** ::"+s);
            dropdown.selectByValue(s.split(" ")[0].trim());
            dropdown = new Select(surQuestionEle.findElement(By.xpath(LONG_TEXT_COLS_SELECT)));
            s = surveyQuestion.getAllowedAnswers().get(1).getAnswerText();
            System.out.println("******************** ::"+s);
            dropdown.selectByValue(s.split(" ")[0].trim());
        }

        else if(surveyQuestion.getQuestionType().equals("TEXT_INPUT")) {
            if(surveyQuestion.getAllowedAnswers().size()!=1) {
                throw new RuntimeException("Answer options should not be null");
            }
            Select dropdown = new Select(surQuestionEle.findElement(By.xpath(SHORT_TEXT_SIZE_SELECT)));
            String s = surveyQuestion.getAllowedAnswers().get(0).getAnswerText();
            System.out.println("******************** ::"+s);
            dropdown.selectByValue(s.split(" ")[0].trim());
        }

        else if(surveyQuestion.getQuestionType().equals("RATING")) {
            if(surveyQuestion.getAllowedAnswers().size() !=1) {
                throw new RuntimeException("Answer options should not be null");
            }
            Select dropdown = new Select(surQuestionEle.findElement(By.xpath(RATING_SELECT)));
            String s = surveyQuestion.getAllowedAnswers().get(0).getAnswerText();
            System.out.println("******************** ::"+s);
            System.out.println(s.split(" ")[0].trim());
            for(WebElement ele : dropdown.getOptions()) {
                System.out.println(ele.getAttribute("value"));
                System.out.println(ele.getText());
            }
            dropdown.selectByValue(s.split(" ")[0].trim());
        }

        else if(surveyQuestion.getQuestionType().equals("NPS")) {
        	item.click("//input[@class='header-smiley-check']");
        }

        else if(surveyQuestion.getQuestionType().equals("CHECKBOX") || surveyQuestion.getQuestionType().equals("SELECT")
                || surveyQuestion.getQuestionType().equals("RANKING") || surveyQuestion.getQuestionType().equals("MATRIX")) {
            boolean flag = false;
            for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer : surveyQuestion.getAllowedAnswers()) {
                if(!flag) {
                    fillAnsChoice(surQuestionEle, surveyAllowedAnswer.getAnswerText());
                    flag=true;
                } else {
                    addAnsChoice(surQuestionEle, surveyAllowedAnswer.getAnswerText());
                }
            }
            if(surveyQuestion.getQuestionType().equals("MATRIX")) {
                flag = false;
                for(SurveyQuestion.SurveySubQuestions surveySubQuestion : surveyQuestion.getSubQuestions()) {
                    if(!flag) {
                        fillSubQuestion(surQuestionEle, surveySubQuestion.getSubQuestionText());
                        flag=true;
                    } else {
                        addSubQuestion(surQuestionEle, surveySubQuestion.getSubQuestionText());
                    }
                }
            }
        }
    }

    public SurveyQuestionPage clickOnSaveQuestion(WebElement surveyQuestionEle) {
        surveyQuestionEle.findElement(By.cssSelector(QUESTION_SAVE_BUTTON_CSS)).click();
        waitTillNoLoadingIcon();
        return this;
    }

    //TODO - Take up at last
	public SurveyQuestionPage addSection(SurveyQuestion surQus) {
		Log.info("Adding Section Header");
		item.click(SECTION_HEADER);
		field.click(SECTION_HEADER_TEXTINPUT);
		field.setText(SECTION_HEADER_TEXTINPUT, surQus.getSectionHeaders());
    	item.click(COLLAPSE_VIEW); /*Clicking somewhere on screen to save section header*/
        return this;
    }
    
	public String getSectionAttribute() {
		wait.waitTillElementDisplayed(SECTION_HEADER_TEXTINPUT, MIN_TIME, MAX_TIME);
		String attribute = element.getElement(SECTION_HEADER_TEXTINPUT)
				.getAttribute("value");
		Log.info("Attribute value is" + attribute);
		return attribute;
	}

    public SurveyQuestionPage addAnsChoice(WebElement QuestionEle, String ansText) {
        String ADD_ANS_CHOICE = ".//div[contains(@id,'_ans_entry')]/descendant::a[@class='mailadd' and @data-action='ADD']";
        WebElement addAnsChoiceEle = QuestionEle.findElement(By.xpath(ADD_ANS_CHOICE));
        addAnsChoiceEle.click();
        fillAnsChoice(QuestionEle, ansText);
        return this;
    }

    private void fillAnsChoice(WebElement QuestionEle, String ansText) {
        String LAST_ANSWER_CHOICE = ".//div[contains(@id, '_ans_entry')]/ul/li[last()]/input";
        QuestionEle.findElement(By.xpath(LAST_ANSWER_CHOICE)).clear();
        QuestionEle.findElement(By.xpath(LAST_ANSWER_CHOICE)).sendKeys(ansText);
    }

    public SurveyQuestionPage addSubQuestion(WebElement QuestionEle, String subQuesText) {
        String ADD_SUB_QUESTION = ".//div[contains(@id,'_sub_qtn_entry')]/descendant::a[@class='mailadd' and @data-action='ADD']";
        WebElement addQueChoiceEle = QuestionEle.findElement(By.xpath(ADD_SUB_QUESTION));
        addQueChoiceEle.click();
        fillSubQuestion(QuestionEle, subQuesText);
        return this;
    }

    private void fillSubQuestion(WebElement QuestionEle, String subQuesText) {
        String LAST_SUB_QUESTION = ".//div[contains(@id, '_sub_qtn_entry')]/ul/li[last()]/input";
        QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).clear();
        QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).sendKeys(subQuesText);

    }

    public SurveyQuestionPage removeAnsChoice(WebElement QuestionEle, String ansText) {
        if(ansText==null && ansText =="") {
            String LAST_SUB_QUESTION = ".//div[contains(@id, '_ans_entry')]/ul/li[last()]/descendant::a[@data-action='DELETE' and @data-type='ANSWER']";
            QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).click();
            waitTillNoLoadingIcon();
        } else {
            String ANS_CHOICE = ".//div[contains(@id, '_ans_entry')]/ul/li/input[@value='"+ansText+"']/following-sibling::div/a[@data-action='DELETE' and @data-type='ANSWER']";
            try {
                QuestionEle.findElement(By.xpath(ANS_CHOICE)).click();
                waitTillNoLoadingIcon();
            } catch (NoSuchElementException e) {
                throw new RuntimeException("Sub Question doesn't exists, to delete : "+ansText);
            }
        }
        return this;
    }

    public SurveyQuestionPage updateAnsChoice(WebElement QuestionEle, String oldAnsText, String newAnsText) {
        String ANS_CHOICE = ".//div[contains(@id, '_ans_entry')]/ul/li/input[@value='"+oldAnsText+"']";
        try {
            QuestionEle.findElement(By.xpath(ANS_CHOICE)).clear();
            QuestionEle.findElement(By.xpath(ANS_CHOICE)).sendKeys(newAnsText);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Sub Question doesn't exists, to update : "+oldAnsText);
        }
        return this;
    }


    public SurveyQuestionPage removeSubQuestion(WebElement QuestionEle, String subQuesText) {
        if(subQuesText==null && subQuesText.equals("")) {
            String LAST_SUB_QUESTION = ".//div[contains(@id, '_sub_qtn_entry')]/ul/li[last()]/descendant::a[@data-action='DELETE' and @data-type='SUB_QUESTION']";
            QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).click();
            waitTillNoLoadingIcon();
        } else {
            String SUB_QUESTION = ".//div[contains(@id, '_sub_qtn_entry')]/ul/li/input[@value='"+subQuesText+"']/following-sibling::div/a[@data-action='DELETE' and @data-type='SUB_QUESTION']";
            try {
                QuestionEle.findElement(By.xpath(SUB_QUESTION)).click();
                waitTillNoLoadingIcon();
            } catch (NoSuchElementException e) {
                throw new RuntimeException("Sub Question doesn't exists, to delete : "+subQuesText);
            }
        }
        return this;
    }

    public SurveyQuestionPage updateSubQuestion(WebElement QuestionEle, String oldQuesText, String newQuesText) {
        String SUB_QUESTION = ".//div[contains(@id, '_sub_qtn_entry')]/ul/li/input[@value='"+oldQuesText+"']";
        try {
            QuestionEle.findElement(By.xpath(SUB_QUESTION)).clear();
            QuestionEle.findElement(By.xpath(SUB_QUESTION)).sendKeys(newQuesText);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Sub Question doesn't exists, to update : "+oldQuesText);
        }
        return this;
    }


    public boolean isQuestionTitleDisplayed(SurveyQuestion surveyQuestion) {
        WebElement surveyQuestionEle = getQuestionElement(surveyQuestion);
        String questionText = surveyQuestionEle.findElement(By.xpath(QUESTION_TITLE_VIEW)).getText();
        Log.info("Actual Question Text : "+questionText);
        Log.info("Expected Question Text : "+surveyQuestion.getQuestionText());
        if(questionText.toLowerCase().contains(surveyQuestion.getQuestionText().toLowerCase())) {
            Log.info("Survey Question Text matched.");
            return true;
        }
        Log.info("Survey Question Text Not matched.");
        return false;
    }

    public boolean verifyQuestionType(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        String questionType = surveyQuestionEle.findElement(By.xpath(".//div[contains(@class,'qtype-set')]/ul/li[contains(text(), 'Question Type')]/span")).getText();
        Log.info("Actual Question Type : "+questionType);
        String expectedQuestionText = getQuestionType(surveyQuestion);
        if(questionType !=null && questionType.toUpperCase().contains(expectedQuestionText.toUpperCase())) {
            Log.info("Type Matched.");
            return true;
        }
        Log.info("Type Not Matched.");
        return false;
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

    public boolean verifyQuestionStatus(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        String QUE_STATUS_CSS = ".//div[contains(@class, 'qtype-set')]/ul/li[contains(text(), 'Active')]/i[@class='glyphicon glyphicon-%s-circle']";
        if (surveyQuestion.isActive()) {
            return isElementPresentAndDisplayed(surveyQuestionEle, String.format(QUE_STATUS_CSS, "ok"));
        } else {
            return isElementPresentAndDisplayed(surveyQuestionEle, String.format(QUE_STATUS_CSS, "ban"));
        }
    }

    public boolean verifyQuestionRequired(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        String QUE_REQUIRED_CSS = ".//div[contains(@class, 'qtype-set')]/ul/li[contains(text(), 'Answer required')]/i[contains(@class, 'glyphicon-%s-star')]";
        if (surveyQuestion.isRequired()) {
            return isElementPresentAndDisplayed(surveyQuestionEle, String.format(QUE_REQUIRED_CSS, "yes"));
        } else {
            return isElementPresentAndDisplayed(surveyQuestionEle, String.format(QUE_REQUIRED_CSS, "no"));
        }
    }

    public boolean verifySurveyQuestionAnswers(WebElement surQuesEle, SurveyQuestion surveyQuestion) {
        String questionType = getQuestionType(surveyQuestion);
        boolean result = false;
        if(questionType.equalsIgnoreCase("Checkbox")) {
            result = verifyCheckBoxQuestionAnswers(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("Radio")) {
            result = verifyRadioQuestionAnswers(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("SingleSelect") || questionType.equalsIgnoreCase("MultiSelect")) {
            result = verifySelectQuestionAnswers(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("Text")) {
            result = verifyTextQuestion(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("Comment")) {
            result = verifyTextAreaQuestion(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("MatrixSingleAnswer") || questionType.equalsIgnoreCase("MatrixMultipleAnswers")) {
            result = verifyMatrixQuestionAnswers(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("NPS")) {
            result = verifyNPSQuestion(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("Rating")) {
            result = verifyRatingQuestionAnswer(surQuesEle, surveyQuestion);
        } else if(questionType.equalsIgnoreCase("Ranking")) {
            result = verifyRankingQuestionAnswer(surQuesEle, surveyQuestion);
        }
        return result;
    }

    private boolean verifyCheckBoxQuestionAnswers(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        boolean result = false;
        String ansXPath = ".//div[@class='qtn-answer-ctn']/descendant::label[contains(., '%s')]/input[@type='checkbox']";
        for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer: surveyQuestion.getAllowedAnswers()) {
            if(!isElementPresentAndDisplayed(surveyQuesEle, String.format(ansXPath, surveyAllowedAnswer.getAnswerText(), surveyAllowedAnswer.getsId()))) {
                Log.error("Ans Text : "+surveyAllowedAnswer.getAnswerText());
                result = false;
                break;
            }
            result = true;
        }
        return result;
    }

    private boolean verifyRadioQuestionAnswers(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        boolean result = false;
        String ansXPath = ".//div[@class='qtn-answer-ctn']/descendant::label[contains(., '%s')]/input[@type='radio']";
        for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer: surveyQuestion.getAllowedAnswers()) {
            if(!isElementPresentAndDisplayed(surveyQuesEle, String.format(ansXPath, surveyAllowedAnswer.getAnswerText(), surveyAllowedAnswer.getsId()))) {
                Log.error("Ans Text : "+surveyAllowedAnswer.getAnswerText());
                result = false;
                break;
            }
            result = true;
        }
        return result;
    }

    private boolean verifySelectQuestionAnswers(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        boolean result = true;
        String ansXpath = ".//div[@class='qtn-answer-ctn']/descendant::select[contains(@class, 'form-control')]";
        Select dropdown = new Select(surveyQuestionEle.findElement(By.xpath(ansXpath)));
        Set<String> actualAnsText = new HashSet<>();
        for(WebElement wEle : dropdown.getOptions()) {
            actualAnsText.add(wEle.getText().trim());
            Log.info("Actual Ans text : " +wEle.getText().trim());
        }
        for(SurveyAllowedAnswer allowedAnswer : surveyQuestion.getAllowedAnswers()) {
            String expectedText = allowedAnswer.getAnswerText().trim();
            Log.info("Expected Ans text : " +expectedText);
            result = actualAnsText.contains(expectedText);
            if(!result) {
                Log.info(expectedText + "is not found in the answers");
                return result;
            }
        }
        return result;
    }

    private boolean verifyTextQuestion(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        String maxLength = surveyQuestion.getAllowedAnswers().get(0).getAnswerText().split(" ")[0].trim();
        String ansXPath = ".//div[@class='qtn-answer-ctn']/descendant::input[@class='form-control single-textbox' and @maxlength='"+maxLength+"']";
        return isElementPresentAndDisplayed(surveyQuesEle, ansXPath);
    }

    private boolean verifyTextAreaQuestion(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        String cols = surveyQuestion.getAllowedAnswers().get(1).getAnswerText().split(" ")[0].trim();
        String rows = surveyQuestion.getAllowedAnswers().get(0).getAnswerText().split(" ")[0].trim();
        String ansXPath = ".//div[@class='qtn-answer-ctn']/descendant::textarea[@class='tmdt-txt' and @cols='"+cols+"' and @rows = '"+rows+"']";
        return isElementPresentAndDisplayed(surveyQuesEle, ansXPath);
    }

    private boolean verifyRatingQuestionAnswer(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        String ansXpath = ".//div[@class='qtn-answer-ctn']/div[@class='preview-answr rating-star']/descendant::div[@class='br-widget']/a";
        int noOfStars = surveyQuesEle.findElements(By.xpath(ansXpath)).size();
        Log.info("No of Actual Starts Displayed : " +noOfStars);
        int expectedNoOfStarts = Integer.valueOf(surveyQuestion.getAllowedAnswers().get(0).getAnswerText().split(" ")[0].trim());
        Log.info("Expected Stars : "+expectedNoOfStarts);
        return (expectedNoOfStarts ==noOfStars);
    }

    private boolean verifyRankingQuestionAnswer(WebElement surveyQuesEle , SurveyQuestion surveyQuestion) {
        boolean result = false;
        String ansXPath = ".//div[@class='qtn-answer-ctn']/descendant::ul[@class='answr-sortable ui-sortable']/li[contains(., '%s')]";
        for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer: surveyQuestion.getAllowedAnswers()) {
            if(!isElementPresentAndDisplayed(surveyQuesEle, String.format(ansXPath, surveyAllowedAnswer.getAnswerText(), surveyAllowedAnswer.getsId()))) {
                Log.error("Ans Text : "+surveyAllowedAnswer.getAnswerText());
                result = false;
                break;
            }
            result = true;
        }
        return result;
    }

    private boolean verifyMatrixQuestionAnswers(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        boolean result = true;
        String ansXpath = ".//div[@class='preview-answr']/descendant::table/thead/tr/th[%s]";
        int noOfAnswers = surveyQuestion.getAllowedAnswers().size();
        int i=1;
        Log.info("Checking Allowed Answers Text");
        for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer: surveyQuestion.getAllowedAnswers()) {
            String actualText = surveyQuestionEle.findElement(By.xpath(String.format(ansXpath, i+1))).getText();
            String expectedText = surveyAllowedAnswer.getAnswerText();
            Log.info(actualText);
            Log.info(expectedText);
            if(!expectedText.equalsIgnoreCase(actualText)) {
                Log.info("Answer Text Not Found : "+expectedText);
                return false;
            }
            ++i;
        }

        Log.info("Checking Sub Questions & Answers Types (Radio/Checkbox)");
        String subQuestionXPath = ".//div[@class='preview-answr']/descendant::table/tbody/tr";
        String radioBoxXPath = ".//input[@type='radio']";
        String checkboxXPath = ".//input[@type='checkbox']";
        List<WebElement> wEleList = surveyQuestionEle.findElements(By.xpath(subQuestionXPath));
        i=0;
        int count;
        for(SurveyQuestion.SurveySubQuestions surveySubQuestion: surveyQuestion.getSubQuestions()) {
            int j=0; count=0;
            for(WebElement wEle : wEleList.get(i).findElements(By.tagName("td"))) {
                if(j==0) {
                    String actualText = wEle.getText();
                    String expectedText = surveySubQuestion.getSubQuestionText();
                    Log.info("Expected Text : " +expectedText);
                    Log.info("Actual Text : " +actualText);
                    if(!expectedText.equalsIgnoreCase(actualText)) {
                        Log.info("Sub Question Not Found :"+expectedText);
                        return false;
                    }
                    ++j;
                } else {
                    count++;
                    if(!isElementPresentAndDisplayed(wEle, surveyQuestion.isSingleAnswer() ? radioBoxXPath : checkboxXPath )) {
                        Log.info("Question Type is not Matched");
                        return false;
                    }
                }
            }
            ++i;
            if(count!=noOfAnswers) {
                Log.info("Total No of Answers Options not matched;");
                return false;
            }
        }
        return result;
    }

    private boolean verifyNPSQuestion(WebElement surveyQuestionEle, SurveyQuestion surveyQuestion) {
        String ansXpath = ".//table[@class='previewlike']/descendant::input[@type='radio']";
        Log.info("Verifying Answer Options");
        int actualAnsCount = surveyQuestionEle.findElements(By.xpath(ansXpath)).size();
        if(actualAnsCount!=11) {
            Log.info("NPS Question Should have 11 Options");
            return false;
        }
        if(surveyQuestion.getAllowedAnswers().size()==1) {
            String expected = surveyQuestion.getAllowedAnswers().get(0).getAnswerText();
            if("Smiley".equalsIgnoreCase(expected)) {
                String xPath1 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='7']/span[@class='nps-sad-icon']";
                String xPath2 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='2']/span[@class='nps-neutral-icon']";
                String xPath3 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='2']/span[@class='nps-happy-icon']";
                if(!(isElementPresentAndDisplayed(surveyQuestionEle, xPath1) || isElementPresentAndDisplayed(surveyQuestionEle, xPath2)
                        || isElementPresentAndDisplayed(surveyQuestionEle, xPath3))) {
                    Log.info("Smiley Header's Not NPS Matched");
                    return false;
                }
            } else {
                String xPath1 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='7' and contains(text(), 'Not Likely')]";
                String xPath2 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='2' and contains(text(), 'Neutral')]";
                String xPath3 =  ".//table[@class='previewlike']/tbody/tr/td[@colspan='2' and contains(text(), 'Extremely Likely')]";
                if(!(isElementPresentAndDisplayed(surveyQuestionEle, xPath1) || isElementPresentAndDisplayed(surveyQuestionEle, xPath2)
                        || isElementPresentAndDisplayed(surveyQuestionEle, xPath3))) {
                    Log.info("Text Header's Not NPS Matched");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPagePresent(String pageTitle, String pageId) {
        if(pageTitle==null) {
            throw new RuntimeException("Page Title is mandatory");
        }
        String PAGE_TITLE_PATH = "//div[contains(@class, 'page')]/descendant::h3[@class='page-title' and text()='%s']";
        if(pageId!=null) {
            PAGE_TITLE_PATH = "//div[contains(@class, 'page') and (@data-page-id='%s' or data-order='%s')]/descendant::h3[@class='page-title' and text()='%s']";
        }
        boolean result = isElementPresentAndDisplay(By.xpath(PAGE_TITLE_PATH));
        Log.info("Page Displayed : " +result);
        return result;
    }

    public SurveyQuestionPage updatePageTitle(String pageId, String oldPageTitle, String newPageTitle) {
        if(isPagePresent(oldPageTitle, pageId)) {
            WebElement wEle = getPageElement(oldPageTitle, pageId);
            wEle.findElement(By.cssSelector(EDIT_PAGE_CSS)).click();
            wait.waitTillElementDisplayed(PAGE_TITLE_INPUT, MIN_TIME, MAX_TIME);
            item.click(PAGE_TITLE_INPUT);
            field.clearAndSetText(PAGE_TITLE_INPUT, newPageTitle);
            item.click(PAGE_EDIT_FORM_SAVE_BUTTON);
            Timer.sleep(1);
            waitTillNoLoadingIcon();
        } else {
            throw new RuntimeException("Page not found");
        }
        return this;
    }

    public SurveyQuestionPage addNewPage() {
        Log.info("Adding New Page");
        int noExistingPages = element.getAllElement("//div[@class='page-break-ctn']").size();
        item.click(PAGE_CREATE_BUTTON);
        for(int i=0; i<3; i++) {
            if(noExistingPages+1==element.getAllElement("//div[@class='page-break-ctn']").size()) {
                break;
            } else {
                Log.info("Waiting for page element to be added to dom");
                Timer.sleep(2);
            }
        }
        return this;
    }

    public SurveyQuestionPage deletePage(String pageTile, String pageId) {
        if(isPagePresent(pageTile, pageId)) {
            WebElement pageEle = getPageElement(pageTile, pageId);
            pageEle.findElement(By.cssSelector(DELETE_PAGE_CSS)).click();
            wait.waitTillElementDisplayed(POP_DELETE_YES, MIN_TIME, MAX_TIME);
            item.click(POP_DELETE_YES);
            waitTillNoLoadingIcon();
            return this;
        } else {
            Log.error("No Page Found to Delete");
            throw new RuntimeException("No Page Found to Delete");
        }
    }

    public SurveyQuestionPage deleteQuestion(SurveyQuestion surveyQuestion) {
        WebElement surveyQuesEle = getQuestionElement(surveyQuestion);
        surveyQuesEle.findElement(By.cssSelector(QUESTION_DELETE_CSS)).click();
        wait.waitTillElementDisplayed(POP_DELETE_YES, MIN_TIME, MAX_TIME);
        item.click(POP_DELETE_YES);
        waitTillNoLoadingIcon();
        return this;
    }
    public void addLogicRules(){
    	item.click(LINK_ICON);
    	wait.waitTillElementDisplayed(LOGIC_RULES_DIV, MIN_TIME, MAX_TIME);
    	item.click(LINK_CHECKBOX);
    	button.click(LINK_SAVE_BUTTON);
    }
    
	public boolean existsElement() {
		Log.info("Verifying Logic Rule");
		return element.getElement(LOGIC_ATTACHED_ICON).isDisplayed();
	}

	public boolean verifyAttachLink() {
		Timer.sleep(3);
		Log.info("Verifying Logic Rule");
		return element.getElement(LOGIC_ATTACH_LINK).isDisplayed();
	}
	
	public void addBranching(SurveyQuestion surQus) {
		item.click(BRANCHING_IN_FIRST_PAGE);
		element.selectFromDropDown(BRANCHING_ICON, surQus.getPageTitle());
		item.click(BRANCHING_SAVE_ON_POPUP);
	}
}
