package com.gainsight.sfdc.survey.pages;

import java.util.*;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion;
import com.gainsight.sfdc.survey.pojo.SurveyQuestion.SurveyAllowedAnswer;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
    private final String QUESTIONS_MENU_CSS     = "ul[class=dropdown-menu addqtn-dropdown-menu]";
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
    private final String QUESTION_TEXT_INPUT            = QUESTION_BLOCK+"/descendant::div[contains(id, '_qtn_entry')]/textarea[contains(@class, 'form-control')]";
    private final String SECTION_TITLE_INPUT            = QUESTION_BLOCK+"/descendant::h3[@class='section-title drag-handle']/input[@class='form-control']";
    private final String SECTION_TITLE_VIEW             = QUESTION_BLOCK+"/descendant::h3[@class='section-title drag-handle']";
    private final String QUESTION_TITLE_VIEW            = QUESTION_BLOCK+"/descendant::h3[@class='qtn-title drag-handle']";
    private final String NPS_SHOW_HEADERS_CHECKBOX_CSS  = "input[id$=_show_header_check][type=checkbox]";
    private final String NPS_HEADER_SELECT_CSS          = "select[class*='form-control header-type-select']";
    private final String RATING_SELECT_CSS              = "select[class=form-control]";
    private final String SHORT_TEXT_SIZE_SELECT_CSS     = "select[class='selectstyle select-size show']";
    private final String LONG_TEXT_ROWS_SELECT_CSS      = "select[class='selectstyle select-rows show']";
    private final String LONG_TEXT_COLS_SELECT_CSS      = "select[class='selectstyle select-cols show']";
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
        waitTillNoLoadingIcon();
	}

    public SurveyQuestionPage addNewPage() {
        item.click(PAGE_CREATE_BUTTON);
        Timer.sleep(3); //No loading icons are implemented.
        return this;
    }

    public WebElement getPageElement(SurveyQuestion surveyQuestion) {
        WebElement surQuestionsPageEle = element.getElement(String.format(PAGE_BLOCK, surveyQuestion.getPageId(), surveyQuestion.getPageId()));
        return surQuestionsPageEle;
    }

    public WebElement getQuestionElement(SurveyQuestion surveyQuestion) {
        WebElement surveyQuestionEle = getPageElement(surveyQuestion).findElement(By.xpath(String.format(QUESTION_BLOCK, surveyQuestion.getQuestionId())));
        return surveyQuestionEle;
    }

    public SurveyQuestionPage clickOnAddNewQuestion(SurveyQuestion surveyQuestion) {
        WebElement surQuestionsPageEle = element.getElement(String.format(PAGE_BLOCK, surveyQuestion.getPageId(), surveyQuestion.getPageId()));
        WebElement addQuestionEle = surQuestionsPageEle.findElement(By.cssSelector(ADD_QUESTION_CSS));
        addQuestionEle.click();
        waitTillElementDisplayed(surQuestionsPageEle, QUESTIONS_MENU_CSS, MIN_TIME, MAX_TIME);
        surQuestionsPageEle.findElement(By.cssSelector(String.format(QUESTION_TYPE_SELECT_CSS, surveyQuestion.getQuestionType()))).click();
        waitTillNoLoadingIcon();
        return this;
    }

    public void fillQuestionFormInfo(SurveyQuestion surveyQuestion) {
        WebElement surQuestionEle = getQuestionElement(surveyQuestion);
        surQuestionEle.findElement(By.cssSelector(QUESTION_TEXT_INPUT)).clear();
        surQuestionEle.findElement(By.cssSelector(QUESTION_TEXT_INPUT)).sendKeys(surveyQuestion.getQuestionText());

        //TO Mark Question Active / InActive
        WebElement activeCheckboxEle = surQuestionEle.findElement(By.cssSelector(ACTIVE_CHECKBOX_CSS));
        String attributeValue = activeCheckboxEle.getAttribute("CHECKED");
        if(attributeValue == null && surveyQuestion.isActive()) {
            activeCheckboxEle.click();
        } else if(attributeValue != null && !surveyQuestion.isActive()) {
            activeCheckboxEle.click();
        }

        if(surveyQuestion.getQuestionType() != "RANKING") {
            WebElement requiredCheckEle = surQuestionEle.findElement(By.cssSelector(REQUIRED_CHECKBOX_CSS));
            String attVal = activeCheckboxEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isRequired()) {
                requiredCheckEle.click();
            } else if(attVal != null && !surveyQuestion.isRequired()) {
                requiredCheckEle.click();
            }
        }

        if(surveyQuestion.getQuestionType().equals("CHECKBOX") || surveyQuestion.getQuestionType().equals("SELECT") || surveyQuestion.getQuestionType().equals("MATRIX")){
            WebElement singleSelect = surQuestionEle.findElement(By.cssSelector(SINGLE_ANS_CHECKBOX_CSS));
            String attVal = singleSelect.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isSingleAnswer()) {
                singleSelect.click();
            } else if(attVal != null && !surveyQuestion.isSingleAnswer()) {
                singleSelect.click();
            }
        }

        if(surveyQuestion.getQuestionType().equals("MATRIX") || surveyQuestion.getQuestionType().equals("RANKING")
                || surveyQuestion.getQuestionType().equals("RATING") || surveyQuestion.getQuestionType().equals("CHECKBOX")
                ||   surveyQuestion.getQuestionType().equals("SELECT")) {
            WebElement commentsEle = surQuestionEle.findElement(By.cssSelector(COMMENTS_CHECKBOX_CSS));
            String attVal = commentsEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isSingleAnswer()) {
                commentsEle.click();
            } else if(attVal != null && !surveyQuestion.isSingleAnswer()) {
                commentsEle.click();
            }
            WebElement commentsLabelEle = surQuestionEle.findElement(By.cssSelector(COMMENTS_INPUT_CSS));
            commentsLabelEle.clear();
            commentsLabelEle.sendKeys(surveyQuestion.getCommentsLabel());
        }

        if(surveyQuestion.getQuestionType().equals("CHECKBOX")  || surveyQuestion.getQuestionType().equals("SELECT")) {
            WebElement allowOtherEle = surQuestionEle.findElement(By.cssSelector(ADD_OTHERS_CHECKBOX_CSS));
            String attVal = allowOtherEle.getAttribute("CHECKED");
            if(attVal == null && surveyQuestion.isSingleAnswer()) {
                allowOtherEle.click();
            } else if(attVal != null && !surveyQuestion.isSingleAnswer()) {
                allowOtherEle.click();
            }
            WebElement commentsLabelEle = surQuestionEle.findElement(By.cssSelector(ADD_OTHERS_INPUT_CSS));
            commentsLabelEle.clear();
            commentsLabelEle.sendKeys(surveyQuestion.getCommentsLabel());
        }

        if(surveyQuestion.getQuestionType().equals("TEXT_AREA")) {
            surQuestionEle.findElement(By.cssSelector(LONG_TEXT_ROWS_SELECT_CSS)).sendKeys("10 Rows");
            surQuestionEle.findElement(By.cssSelector(LONG_TEXT_COLS_SELECT_CSS)).sendKeys("40 Columns");
        } else if(surveyQuestion.getQuestionType().equals("TEXT_INPUT")) {
            surQuestionEle.findElement(By.cssSelector(SHORT_TEXT_SIZE_SELECT_CSS)).sendKeys("60 Characters");
        } else if(surveyQuestion.getQuestionType().equals("RATING")) {
            surQuestionEle.findElement(By.cssSelector(RATING_SELECT_CSS)).sendKeys("5 Stars");
        } else if(surveyQuestion.getQuestionType().equals("NPS")) {
            surQuestionEle.findElement(By.cssSelector(NPS_SHOW_HEADERS_CHECKBOX_CSS)).click();
            surQuestionEle.findElement(By.cssSelector(NPS_HEADER_SELECT_CSS)).sendKeys("Text");
        } else if(surveyQuestion.getQuestionType().equals("CHECKBOX") || surveyQuestion.getQuestionType().equals("SELECT")
                || surveyQuestion.getQuestionType().equals("RANKING")) {
            for(SurveyQuestion.SurveyAllowedAnswer surveyAllowedAnswer : surveyQuestion.getAllowedAnswers()) {
                addAnsChoice(surQuestionEle, surveyAllowedAnswer.getAnswerText());
            }
            if(surveyQuestion.getQuestionType().equals("MATRIX")) {
                for(SurveyQuestion.SurveySubQuestions surveySubQuestion : surveyQuestion.getSubQuestions()) {
                    addSubQuestion(surQuestionEle, surveySubQuestion.getSubQuestionText());
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
    public SurveyQuestionPage addSection() {
        return this;
    }

    public SurveyQuestionPage addAnsChoice(WebElement QuestionEle, String ansText) {
        String ADD_CHOICE = ".//div[contains(@id,'_ans_entry')]/descendant::a[@class='mailadd' and @data-action='ADD' and @data-type='ANSWER']";
        WebElement addAnsChoiceEle = QuestionEle.findElement(By.xpath(ADD_CHOICE));
        addAnsChoiceEle.click();
        String LAST_CHOICE_QUESTION = "//div[contains(@id, '_sub_qtn_entry')]/ul/li[last()]/input";
        QuestionEle.findElement(By.xpath(LAST_CHOICE_QUESTION)).clear();
        QuestionEle.findElement(By.xpath(LAST_CHOICE_QUESTION)).sendKeys(ansText);
        return this;
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

    public SurveyQuestionPage addSubQuestion(WebElement QuestionEle, String subQuesText) {
        String ADD_SUB_QUESTION = ".//div[contains(@id,'_sub_qtn_entry')]/descendant::a[@class='mailadd' and @data-action='ADD']";
        WebElement addQueChoiceEle = QuestionEle.findElement(By.xpath(ADD_SUB_QUESTION));
        addQueChoiceEle.click();
        String LAST_SUB_QUESTION = "//div[contains(@id, '_sub_qtn_entry')]/ul/li[last()]/input";
        QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).clear();
        QuestionEle.findElement(By.xpath(LAST_SUB_QUESTION)).sendKeys(subQuesText);
        return this;
    }

    public SurveyQuestionPage removeSubQuestion(WebElement QuestionEle, String subQuesText) {
        if(subQuesText==null && subQuesText =="") {
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

    public boolean isQuestionPresent(SurveyQuestion surveyQuestion) {
        boolean result = false;

        return result;
    }



}
