package com.gainsight.sfdc.survey.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;
import java.util.List;

public class SurveyDesignPage extends SurveyBasePage{
	
	private final String READY_INDICATOR                ="//a[contains(text(),'Design')]";

    private final String DESIGN_SUB_TAB                 = "DesignTab";
    private final String REORDER_SUB_TAB                = "ReorderTab";
    private final String LOGIC_RULES_SUB_TAB            = "LogicRulesTab";
    private final String ALERT_RULES_SUB_TAB            = "AlertRulesTab";
    private final String PREVIEW_SUB_TAB                = "//span[@id='ulPreviewLink']/a[contains(text(), 'Preview')]";

    private final String BACK_LINK                      = "//a[contains(text(), 'Back')]";
    private final String FRAME_QUESTION_FORM            = "//iframe[contains(@src, 'apex/SurveyQuestionFrame')]";

    private final String NEW_QUESTION_BUTTON            = "//input[@value='New']";
    private final String QUESTION_TYPE_SELECT           = "//select[@class='jbaraQuestionTypeList' and @title='Question Type']";
    private final String QUESTION_TEXT                  = "dummyJbaraQuestionTitle";
    private final String ANSWER_ADD_BUTTON              = "//span[@class='ui-icon addIconClass answerOption_addIcon']";
    private final String ANSWER_REMOVE_BUTTON           = "//span[@class='ui-icon removeIconClass answerOption_removeIcon']";
    private final String SAVE_QUESTION_BUTTON           = "//input[@class='btn dummyQuestionSaveBtn' and @value='Add Question']";
    private final String CANCEL_BUTTON                  = "//input[@class='btn' and @value='Cancel']";
    private final String REQUIRED_CHECKBOX              = "answerRequired";
    private final String QUESTION_STATUS_CHEKBOX        = "questionStatus";
    private final String OTHERS_ANS_ENABLE_CHECKBOX     = "chkAnswerOptionAllowOther";
    private final String OTHERS_ANS_TEXT                = "answerOtherLabel";
    private final String ALLOW_COMMENTS_CHECKBOX        = "chkQuestionAllowComments";
    private final String ALLOW_COMMENTS_LABEL_TEXT      = "questionAllowCommentsLabel";

    private final String ANS_TEXT                       = "//li[@class='dummyAnswerItemLi']/input[@class='dummyAnswerText']";
    private final String MATRIX_ROW_TEXT                = "//input[@class='dummyMatrixRowText']";
    private final String MATRIX_ROW_CONTAINER           = "//ul[@id='matrixColumnUlContainer']/li[@class='dummySubQuestionLi']";
    private final String ANS_CONTAINER                  = "//ul[@id='answerUlContainer']/li[@class='dummyAnswerItemLi']";
    private final String NO_OF_ROWS_SELECT              = "//select[@class='dummyAnswerOpenCommentsRowSize']";
    private final String NO_OF_COLUMNS_SELECT           = "//select[@class='dummyAnswerOpenCommentsColumnsSize']";
    private final String FIELD_SIZE_SELECT              = "//select[@class='dummyAnswerOpenTextSize']";
    private final String NPS_SHOW_HEADERS_CHECKBOX      = "chkNPSEnableHeaders";
    private final String NPS_TEXT_HEADER_RADIO          = "//input[@class='rbtnHeaderByText']";
    private final String NPS_ICON_HEADER_RADIO          = "//input[@class='rbtnHeaderByIcon']";
    private String NPS_SELECTION                        = "//ul[@id='answerUlContainer']";
	
	public SurveyDesignPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}

    public SurveyBasePage clickOnBack() {
        item.click(BACK_LINK);
        return new SurveyBasePage();
    }

	public QuestionsReorderPage clickOnReorder(){
        item.click(REORDER_SUB_TAB);
		return new QuestionsReorderPage();
	}
	
	public LogicRulesPage clickOnLoginRules(){
        item.click(LOGIC_RULES_SUB_TAB);
		return new LogicRulesPage();
	}
	
	public AlertRulesPage clickOnAlertRules(){
        item.click(ALERT_RULES_SUB_TAB);
		return new AlertRulesPage();
	}

    public PreviewSurveyPage clickOnPreview() {
        item.click(PREVIEW_SUB_TAB);
        return new PreviewSurveyPage();
    }

    public SurveyDesignPage addSurveyQuestion(HashMap<String, String> testData) {
        String xpath = "//li[contains(@class,'spaceBetweenQuestions questionelement')]";
        int count = element.getElementCount(xpath);
        item.click(NEW_QUESTION_BUTTON);
        element.switchToFrame(FRAME_QUESTION_FORM);
        item.selectFromDropDown(QUESTION_TYPE_SELECT, testData.get("QuestionType"));
        if(testData.get("Question") != null) {
            item.clearAndSetText(QUESTION_TEXT, testData.get("Question"));
        }
        if(testData.get("QuestionType").equalsIgnoreCase("Matrix - Multiple answers per row (checkboxes)")
                || testData.get("QuestionType").equalsIgnoreCase("Matrix - Single answer per row (radio)")) {
            fillMatrixAnsForm(testData.get("RowAnswers"), testData.get("ColumnAnswers"));
        } else if(testData.get("QuestionType").equalsIgnoreCase("NPS - Single answer per row (radio)")) {
            fillNPS(testData.get("Answers"));
        } else if(testData.get("QuestionType").equalsIgnoreCase("Open Text - Comments")) {
            fillCommentAns(testData.get("Answers"), true);
        } else if(testData.get("QuestionType").equalsIgnoreCase("Open Text - Single line")) {
            fillCommentAns(testData.get("Answers"), false);
        } else {
            fillAnswers(testData.get("Answers"));
        }
        setQuestionStatus(testData.get("Active"));
        if(!testData.get("QuestionType").equalsIgnoreCase("Ranking- Multiple answer")) {
            setRequired(testData.get("Required"));
        }
        enableComments(testData.get("AllowComments"));
        item.click(SAVE_QUESTION_BUTTON);
        amtDateUtil.stalePause();
        element.switchToMainWindow();
        refreshPage();
        return this;
    }

    public boolean isQuestionPresent(HashMap<String, String> testData) {
        if(testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Single answer (Radio)") || testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Multiple answers (Checkboxes)")) {
            return isRadioCheckQuesDisplayed(testData);
        } else if(testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Single answer (dropdown menu)") || testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Multiple answer (dropdown menu)")) {
            return isDropDownQuesDisplayed(testData);
        } else if(testData.get("QuestionType").equalsIgnoreCase("Open Text - Single line") || testData.get("QuestionType").equalsIgnoreCase("Open Text - Comments")) {
            return isCommentQuesDisplayed(testData);
        } else if(testData.get("QuestionType").equalsIgnoreCase("Matrix - Single answer per row (radio)") || testData.get("QuestionType").equalsIgnoreCase("Matrix - Multiple answers per row (checkboxes)")) {
            return isMatrixQuesDisplayed(testData);
        } else if(testData.get("QuestionType").equalsIgnoreCase("NPS - Single answer per row (radio)")) {
            return isNPSQuesDisplayed(testData);
        } else if(testData.get("QuestionType").equalsIgnoreCase("Ranking- Multiple answer")) {
            return isRankingQuestionDisplayed(testData);
        }
        Report.logInfo("Question is not displayed, Problem with data set");
        return false;
    }

    public boolean isRadioCheckQuesDisplayed(HashMap<String, String> testData) {
        List<WebElement> elementList = driver.findElements(By.xpath(xpathOfRadioCheckQues(testData)));
        if(elementList == null || elementList.size() < 1) {
            return false;
        } else {
            return elementList.get(0).isDisplayed();
        }
    }

    private String xpathOfRadioCheckQues(HashMap<String, String> testData) {
        String questionType = testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Single answer (Radio)") ? "radio" : "checkbox";
        String xPath = "//li[@class='spaceBetweenQuestions questionelement']";
        xPath = xPath+"/descendant::div[@class='questHeader' and contains(text(), '"+testData.get("Question")+"')]/following-sibling::div[@class='qustBody']/descendant::";
        String[] ans = splitValues(testData.get("Answers"));
        if(ans != null && ans.length > 0) {
            for(int i=0; i<ans.length;i++) {
                if(i==0)
                    xPath = xPath+"li/input[@type='"+questionType+"']/following-sibling::label[contains(text(), '"+ans[i].trim()+"')]";
                else {
                    xPath =xPath+"/parent::li/following-sibling::li/input[@type='"+questionType+"']/following-sibling::label[contains(text(), '"+ans[i].trim()+"')]";
                }
            }
            xPath = xPath+"/ancestor::li[@class='spaceBetweenQuestions questionelement']";
        }  else {
            Report.logInfo("Data entered is not appropriate");
        }
        Report.logInfo("Xpath : " +xPath);
        return xPath;
    }

    public boolean isDropDownQuesDisplayed(HashMap<String, String> testData) {
        boolean found = false;
        String xPath = "//li[@class='spaceBetweenQuestions questionelement']";
        xPath = xPath+"/descendant::div[@class='questHeader' and contains(text(), '"+testData.get("Question")+"')]/following-sibling::div[@class='qustBody']";
        String[] ans = splitValues(testData.get("Answers"));
        if(element.getElement(xPath).isDisplayed()) {
            Report.logInfo("Question is displayed, checking for option is the select");
            xPath = xPath+ (testData.get("QuestionType").equalsIgnoreCase("Multiple Choice - Single answer (dropdown menu)") ? "/select[@class='dummySelectClass']" : "/select[@class='dummySelectClass' and @multiple='multiple']");
            Select dropDown = new Select(element.getElement(xPath));
            List<WebElement> options = dropDown.getOptions();
            for(int i=0; i<ans.length; i++) {
                 found = false;
                for(WebElement wEle : options) {
                    if(wEle.getText().equalsIgnoreCase(ans[i].trim())) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    Report.logInfo("Option doesn't exists in drop-down : "+ans[i]);
                    return false;
                }
            }
        } else {
            Report.logInfo("Question is not displayed.");
            return false;
        }
        return found;
    }


    public boolean isRankingQuestionDisplayed(HashMap<String, String> testData) {
        List<WebElement> elementList = driver.findElements(By.xpath(xPathOfRankingQues(testData)));
        if(elementList == null || elementList.size() < 1) {
            return false;
        } else {
            return elementList.get(0).isDisplayed();
        }
    }

    private String xPathOfRankingQues(HashMap<String, String> testData) {
        String xPath = "//li[@class='spaceBetweenQuestions questionelement']/descendant::div[@class='questHeader' and contains(text(), '";
        xPath=xPath+testData.get("Question")+"')]/following-sibling::div[@class='qustBody']/ol[@class='answersClass rankingAnswer ui-sortable']/";
        String[] values = splitValues(testData.get("Answers"));
        if(values != null) {
            for(int i=0; i< values.length; i++) {
                if(i==0) {
                    xPath=xPath+"li[@class='displayRankingAnswerLi' and contains(text(), '"+values[i].trim()+"')]";
                } else {
                    xPath=xPath+"/following-sibling::li[@class='displayRankingAnswerLi' and contains(text(), '"+values[i].trim()+"')]";
                }
            }
            xPath=xPath+"/ancestor::li[@class='spaceBetweenQuestions questionelement']";
        } else {
            Report.logInfo("Failed to build xpath");
        }
        Report.logInfo("XPath :" +xPath);
        return xPath;
    }

    public boolean isCommentQuesDisplayed(HashMap<String, String> testData) {
        List<WebElement> elementList = driver.findElements(By.xpath(xPathOfCommentQues(testData)));
        if(elementList == null || elementList.size() < 1) {
            return false;
        } else {
            return elementList.get(0).isDisplayed();
        }
    }

    private String xPathOfCommentQues(HashMap<String, String> testData) {
        String xPath = "//li[@class='spaceBetweenQuestions questionelement']" +
                "/descendant::div[@class='questHeader' and contains(text(), '"+
                testData.get("Question")+"')]/following-sibling::div[@class='qustBody']";

        if(testData.get("QuestionType").equalsIgnoreCase("Open Text - Single line")) {
            xPath=xPath+"/input[@type='text']";
        } else {
            xPath=xPath+"/textarea[@class='textareawidth']";
        }
        xPath =xPath+"/ancestor::li[@class='spaceBetweenQuestions questionelement']";
        Report.logInfo("XPath : " +xPath);
        return xPath;
    }

    public boolean isNPSQuesDisplayed(HashMap<String, String> testData) {
        List<WebElement> elementList = element.getAllElement(xPathOfNPSQues(testData));
        if(elementList == null || elementList.size() < 1) {
            return false;
        } else {
            return elementList.get(0).isDisplayed();
        }
    }

    private String xPathOfNPSQues(HashMap<String, String> testData) {
        String xPath = "//li[@class='spaceBetweenQuestions questionelement']" +
                "/descendant::div[@class='questHeader' and contains(text(), '"+
                testData.get("Question")+"')]/following-sibling::div[@class='qustBody qustBodyNPS']/table[@class='answersClass']";
        String[] values = splitValues(testData.get("Answers"));
        if(values !=null && values[0].equalsIgnoreCase("True")) {
            if(values[1].equalsIgnoreCase("Icon")) {
                xPath=xPath+"/thead/tr/th[@class='SadBorder' and @colspan='7']" +
                        "/following-sibling::th[@class='PlainBorder' and @colspan='2']" +
                        "/following-sibling::th[@class='SmileBorder']" +
                        "/ancestor::table[@class='answersClass']";
            } else {
                xPath=xPath+"/thead/tr/th[@class='SadBorder' and @colspan='7' and contains(text(), 'Not likely at all')]" +
                        "/following-sibling::th[@class='PlainBorder' and @colspan='2' and contains(text(), 'Neutral')]" +
                        "/following-sibling::th[@class='SmileBorder' and @colspan='2' and contains(text(), 'Extremely likely')]" +
                        "/ancestor::table[@class='answersClass']";
            }
        } else {
            xPath = xPath+"/tbody/tr/td[contains(text(), '0')]/input[@type='radio']";
            for(int i=1; i < 11;i++) {
                xPath=xPath+"/parent::td/following-sibling::td[contains(text(), '"+i+"')]/input[@type='radio']";
            }
        }
        xPath=xPath+"/ancestor::li[@class='spaceBetweenQuestions questionelement']";
        Report.logInfo("Xpath :" +xPath);
        return xPath;
    }

    public boolean isMatrixQuesDisplayed(HashMap<String, String> testData) {
        List<WebElement> elementList = element.getAllElement(xPathOfMatrixQues(testData));
        if(elementList == null || elementList.size() < 1) {
            return false;
        } else {
            return elementList.get(0).isDisplayed();
        }
    }

    private String xPathOfMatrixQues(HashMap<String, String> testData) {
        String xPath = "//li[@class='spaceBetweenQuestions questionelement nonResponsiveMatrix']" +
                "/descendant::div[@class='questHeader' and contains(text(), '"+testData.get("Question")+"')]" +
                "/following-sibling::div[@class='qustBody']/descendant::table[@class='answersClass answersClassNonResponsive']";
        String[] rows = splitValues(testData.get("RowAnswers"));
        String[] cols = splitValues(testData.get("ColumnAnswers"));
        for(int i=0; i< cols.length;i++) {
            if(i==0) {
                xPath=xPath+"/tbody/tr/td[contains(text(), '"+cols[i].trim()+"')]";
            }  else {
                xPath=xPath+"/following-sibling::td[contains(text(), '"+cols[i].trim()+"')]";
            }

        }
        xPath=xPath+"/parent::tr/following-sibling::tr[contains(@class,'matrixAnsRows')]";
        for(int i=0; i<rows.length; i++) {
            if(i==0) {
                xPath=xPath+"/td[contains(text(), '"+rows[i].trim()+"')]" +
                        "/following-sibling::td[@class='matrixRadioTd']" +
                        "/descendant::input[@type='radio']/ancestor::tr[contains(@class,'matrixAnsRows')]";
            } else {
                xPath=xPath+"/following-sibling::tr[contains(@class,'matrixAnsRows')]/td[contains(text(),'"+rows[i].trim()+"')]/parent::tr";
            }
        }
        xPath=xPath+"/ancestor::li[contains(@class,'spaceBetweenQuestions')]";
        if(testData.get("QuestionType").equalsIgnoreCase("Matrix - Multiple answers per row (checkboxes)")) {
            xPath=xPath.replaceAll("Radio", "Check").replaceAll("radio", "checkbox");
        }
        Report.logInfo("XPath :" +xPath);
        return xPath;
    }

    private void waitForQuestionAdded(int count) {
        String xpath = "//li[contains(@class,'spaceBetweenQuestions questionelement')]";
        int noOfQuestions = element.getElementCount(xpath);
        for(int i=0; i<15; i++) {
            if(count < element.getElementCount(xpath)){
                break;
            } else {
                amtDateUtil.stalePause();
            }
        }
    }

    private void enableOthers(String value) {
        String[] values =splitValues(value);
        if(values!=null && values[0].equalsIgnoreCase("true")) {
            allowOthers(values[1]);
        }
    }
    private void enableComments(String value) {
        String[] values = splitValues(value);
        if(values[0].equalsIgnoreCase("True")) {
            allowComments(values[1]);
        }
    }
    private void setRequired(String value) {
        if(value != null && value.equalsIgnoreCase("True")) {
            makeRequired();
        } else if(value != null && value.equalsIgnoreCase("False")) {
            makeUnRequired();
        }
    }

    private void setQuestionStatus(String value) {
        if(value != null && value.equalsIgnoreCase("True")) {
            makeActive();
        } else if(value != null && value.equalsIgnoreCase("False")) {
            deActive();
        }
    }

    private void allowOthers(String label) {
        item.click(OTHERS_ANS_ENABLE_CHECKBOX);
        if(label != null && label.equalsIgnoreCase("NA")) {
            field.clearText(OTHERS_ANS_TEXT);
            field.setText(OTHERS_ANS_TEXT, label);
        }
    }

    private void allowComments(String label) {
        item.click(ALLOW_COMMENTS_CHECKBOX);
        if(label != null && label.equalsIgnoreCase("NA")) {
            field.clearText(ALLOW_COMMENTS_LABEL_TEXT);
            field.setText(ALLOW_COMMENTS_LABEL_TEXT, label);
        }
    }
    private void makeActive() {
        String status = element.getElement(QUESTION_STATUS_CHEKBOX).getAttribute("checked");
        if(status == null || status.equalsIgnoreCase("unchecked")) {
            element.click(QUESTION_STATUS_CHEKBOX);
        }
    }

    private void deActive() {
        String status = element.getElement(QUESTION_STATUS_CHEKBOX).getAttribute("checked");
        if(status != null && status.equalsIgnoreCase("checked")) {
            element.click(QUESTION_STATUS_CHEKBOX);
        }
    }

    private void makeRequired() {
        String status = element.getElement(REQUIRED_CHECKBOX).getAttribute("checked");
        if(status == null || status.equalsIgnoreCase("unchecked")) {
            element.click(REQUIRED_CHECKBOX);
        }
    }

    private void makeUnRequired() {
        String status = element.getElement(REQUIRED_CHECKBOX).getAttribute("checked");
        if(status != null && status.equalsIgnoreCase("checked")) {
            element.click(REQUIRED_CHECKBOX);
        }
    }

    private void fillMatrixAnsForm(String row, String col) {
        String[] rows = splitValues(row);
        if(rows !=null) {
            int count = element.getElementCount(MATRIX_ROW_CONTAINER);
            if(rows.length < count) {
                for(; rows.length <= count; count--) {
                    removeMatrixRow();
                }
            }
            for(int i =1; i <=rows.length;i++) {
                if(i>count) {
                    addMatrixRow();
                }
                field.clearAndSetText(MATRIX_ROW_CONTAINER+"["+i+"]/input[@class='dummyMatrixRowText']", rows[i-1].trim());
            }
        }
        fillAnswers(col);
    }

    private void addMatrixRow() {
        String MATRIX_ROW_CONTAINER = "//ul[@id='matrixColumnUlContainer']/li[@class='dummySubQuestionLi']";
        int count = element.getElementCount(MATRIX_ROW_CONTAINER);
        item.click(MATRIX_ROW_CONTAINER+"["+count+"]"+"/div/span[@class='ui-icon addIconClass matrixColumn_addIcon']");
        for (int i=0; i< 15; ++i) {
            if(count + 1 == element.getElementCount(MATRIX_ROW_CONTAINER)) {
                break;
            } else {
                amtDateUtil.stalePause();
            }
        }
    }

    private void removeMatrixRow() {
        String MATRIX_ROW_CONTAINER = "//ul[@id='matrixColumnUlContainer']/li[@class='dummySubQuestionLi']";
        int count = element.getElementCount(MATRIX_ROW_CONTAINER);
        item.click(MATRIX_ROW_CONTAINER+"["+count+"]"+"/div/span[@class='ui-icon removeIconClass matrixColumn_removeIcon']");
        for (int i=0; i< 15; ++i) {
            if(count + 1 == element.getElementCount(MATRIX_ROW_CONTAINER)) {
                break;
            } else {
                amtDateUtil.stalePause();
            }
        }
    }

    private void fillAnswers(String ans) {
        String[] values = splitValues(ans);
        if(values != null) {
            int count = element.getElementCount(ANS_CONTAINER);
            if(values.length < count) {
                for( ; values.length <= count ; count--) {
                    removeAnsORMatrixColumn();
                }
            }
            for(int i=1; i<= values.length; i++) {
                if(i>count) {
                    addAnsORMatrixColumn();
                }
                field.clearAndSetText(ANS_CONTAINER+"["+i+"]/input[@class='dummyAnswerText']",values[i-1].trim());
            }
        }
    }

    private void fillNPS(String ans) {
        String[] values = splitValues(ans);
        String s =    NPS_SELECTION;
        if(Boolean.valueOf(values[0])) {
            item.click(NPS_SHOW_HEADERS_CHECKBOX);
            amtDateUtil.stalePause();
            if(values[1].equalsIgnoreCase("Icon")) {
                item.click(NPS_ICON_HEADER_RADIO);
                amtDateUtil.stalePause();

                for(int i=1; i < 13; i++) {
                    s = NPS_SELECTION+"/li[@class='dummyAnswerItemLi']["+i+"]/span/select[@class='dummyGroupHeaderIcon']";
                    if(i<=7) {
                        item.selectFromDropDown(s, "Sad");
                    } else if(i >7 && i < 10) {
                        item.selectFromDropDown(s, "Plain");
                    } else if(i >=10 && i < 12){
                        item.selectFromDropDown(s, "Smile");
                    }
                }
            } else {
                for(int i=1; i < 12; i++) {
                    s = NPS_SELECTION+"/li[@class='dummyAnswerItemLi']["+i+"]/span/select[@class='dummyGroupHeaderText']";
                    if(i<=7) {
                        item.selectFromDropDown(s, "Not likely at all");
                    } else if(i >7 && i < 10) {
                        item.selectFromDropDown(s, "Neutral");
                    } else if(i >=10 && i < 12){
                        item.selectFromDropDown(s, "Extremely likely");
                    }
                }
            }

        }
    }

    private void fillCommentAns(String values, boolean isMultiLineComment) {
        if(isMultiLineComment) {
            String[] s = splitValues(values);
                item.selectFromDropDown(NO_OF_ROWS_SELECT, s[0].trim());
                item.selectFromDropDown(NO_OF_COLUMNS_SELECT, s[1].trim());
        } else {
            item.selectFromDropDown(FIELD_SIZE_SELECT, values.trim());
        }
    }

    private void addAnsORMatrixColumn() {
        int count = element.getElementCount(ANS_CONTAINER);
        item.click(ANS_CONTAINER+"["+count+"]"+"/div/span[@class='ui-icon addIconClass answerOption_addIcon']");
        for (int i=0; i < 15; i++) {
            if(count + 1 == element.getElementCount(ANS_CONTAINER)) {
                break;
            } else {
                amtDateUtil.stalePause();
            }
        }
    }

    private void removeAnsORMatrixColumn() {
        int count = element.getElementCount(ANS_CONTAINER);
        item.click(ANS_CONTAINER+"["+count+"]"+"/div/span[@class='ui-icon removeIconClass answerOption_removeIcon']");
        for (int i=0; i<15; i++) {
            if(count - 1 == element.getElementCount(ANS_CONTAINER)) {
                break;
            } else {
                amtDateUtil.stalePause();
            }
        }
    }

    private String[] splitValues(String values) {
        if(values !=null) {
            return values.trim().substring(1,values.length()-1).split("\\~");
        }
        return null;

    }
	
}
