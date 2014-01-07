package com.gainsight.sfdc.retention.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pojos.Alert;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import com.gainsight.sfdc.retention.pojos.Task;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import java.util.HashMap;
import java.util.List;

public class AlertsPage extends RetentionBasePage {
	private static final String READY_INDICATOR          = "//select[@class='jbaraDummyAlertUIViewsSelectControl']";
    private static final String ADD_ALERT_BUTTON         = "//a[contains(text(), 'Add Alert')]";
    private static final String SHOW_FILTER_IMG          = "//img[@title='Show filters']";
    private static final String HIDE_FILTER_IMG          = "//img[@title='Hide filters']";
    private static final String FILTER_SECTION_BUTTON    = "filter-section";
    private static final String EXPORT_BUTTON            = "//div[@class='export-btn btn']";
    private static final String LIST_VIEW_IMG            = "//img[@title='List view']";
    private static final String CARD_VIEW_IMG            = "//img[@title='Card view']";
    private static final String ANALYTICS_VIEW_IMG       = "//img[@title='Analytics view']";
    private static final String CUST_NAME_INPUT          = "//input[contains(@class,'custComponentIdjbaraDummyCustomerName customer-name-text')]";
    private static final String CUST_LOOKUP_SERCH_IMG    = "//img[@title='Customer Name Lookup']";
    private static final String SUBJECT_INPUT            = "jbaraAlertSubjectInput";
    private static final String SEVERITY_SELECT          = "//select[@class='entry-severity-select jbaraAlertSelectInput jbaraAlertSeverityInput']";
    private static final String DATE_INPUT               = "jbaraAlertDateInput";
    private static final String ASV_INPUT                = "jbaraAlertASVInput";
    private static final String TYPE_SELECT              = "//select[@class='entry-type-select jbaraAlertSelectInput jbaraAlertTypeInput']";
    private static final String REASON_SELECT            = "//select[@class='entry-reason-select jbaraAlertSelectInput jbaraAlertReasonInput']";
    private static final String STATUS_SELECT            = "//select[@class='entry-status-select jbaraAlertSelectInput jbaraAlertStatusInput']";
    private static final String COMMENT_INPUT            = "jbaraAlertCmtInput";
    private static final String ALERT_SAVE_ADD_TASK_BUTTON = "saveAlertAndAddTaskIdBtn";
    private static final String ALERT_SAVE_CLOSE         = "saveAlertAndCloseIdBtn";
    private static final String ALERT_EDIT_SAVE_CLOSE    = "editAndCloseAlertBtn";
    private static final String ALERT_FORM_CLOSE         = "//span[@title='Close']";
    private static final String ALERT_FORM_EDIT_ALERT_DETAILS = "//span[@class='dummyEditAlertInfoIconCls']";
    private static final String ADD_PLAYBOOK_BUTTON      = "//input[@class='dummyAddPBText dummyETAddText btn']";
    private static final String ADD_TASK_BUTTON          = "//input[@class='dummyAddTaskIconCls btn']";
    private static final String GS_TASK_ASSIGN_INPUT     = "//input[@class='Assigned__cInputCls userlookupCls taskParamControlDataInput ui-autocomplete-input']";
    private static final String GS_TASK_SUBJECT_INPUT    = "//input[@class='Subject__cInputCls taskParamControlDataInput']";
    private static final String GS_TASK_DATE_INPUT       = "Date__cInputId";
    private static final String GS_TASK_PRI0RITY_INPUT   = "//select[@class='Priority__cInputCls taskParamControlDataInput']";
    private static final String GS_TASK_STATUS_INPUT     = "//select[@class='Status__cInputCls taskParamControlDataInput']";
    private static final String TASK_SAVE_BUTTON         = "//button[@class='btn taskSaveBtn']";
    private static final String TASK_CANCEL_BUTTON       = "//button[@class='btn taskCancelBtn']";
    private static final String PLAYBOOK_SELECT          = "//select[@class='loadPlaybookCls']";
    private static final String TASK_CARD                = "//div[@class='taskItemCls']";
    private static final String CUST_SEARCH_RESULT_DIV   = "//div[contains(@id, 'CustomerSearchPanel')]";
    private static final String NO_TASK_PRESENT_MSG = "//div[@class='taskFirstTimeAddCls' and contains(text(), 'No tasks are added for this alert!')]";

    private static final String SUBJECT_DISPLAY     = "subject-view";
    private static final String SEVERITY_DISPLAY    = "severity-view";
    private static final String REASON_DISPLAY      = "reason-view";
    private static final String STATUS_DISPLAY      = "status-view";
    private static final String TYPE_DISPLAY        = "type-view";
    private static final String DATE_DISPLAY        = "date-view";
    private static final String ASV_DISPLAY         = "asv-view";
    private static final String COMMENTS_DISPLAY    = "comments-view";





    public AlertsPage(String pageName) {
        super(pageName);
    }

	public AlertsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);

	}

    public void addAlert(HashMap<String, String> alertData) {
        item.click(ADD_ALERT_BUTTON);
        wait.waitTillElementDisplayed(CUST_NAME_INPUT, MIN_TIME, MAX_TIME);
        fillAlertForm(alertData, true);
        clickOnSaveAlert();
    }

    public void clickOnSaveAlert() {
        item.click(ALERT_SAVE_CLOSE);
    }
    public void clickOnEditAlertClose() {
        item.click(ALERT_EDIT_SAVE_CLOSE);
    }

    //customer , asv, date, subject, severity, type, reason, status, comment
    public void fillAlertForm(HashMap<String, String> alertData, boolean isCustomerReq) {
        if(alertData.get("customer") != null && isCustomerReq) {
            selectCustomer(alertData.get("customer"));
        }
        if(alertData.get("subject") != null) {
            field.clearAndSetText(SUBJECT_INPUT, alertData.get("subject"));
        }
        if(alertData.get("severity") != null) {
            field.selectFromDropDown(SEVERITY_SELECT, alertData.get("severity"));
        }
        if(alertData.get("date") != null) {
            field.clearAndSetText(DATE_INPUT, alertData.get("date"));
        }
        if(alertData.get("asv") != null) {
            amtDateUtil.stalePause();
            field.clearAndSetText(ASV_INPUT, alertData.get("asv"));
        }
        if(alertData.get("type") != null) {
            field.selectFromDropDown(TYPE_SELECT, alertData.get("type"));
        }
        if(alertData.get("reason") != null) {
            field.selectFromDropDown(REASON_SELECT, alertData.get("reason"));
        }
        if(alertData.get("status") != null) {
            field.selectFromDropDown(STATUS_SELECT, alertData.get("status"));
        }
        if(alertData.get("comment") != null) {
            field.setText(COMMENT_INPUT, alertData.get("comment"));
        }
    }

    private void selectCustomer(String cName) {
        field.setText(CUST_NAME_INPUT, cName);
        item.click(CUST_LOOKUP_SERCH_IMG);
        wait.waitTillElementDisplayed(CUST_SEARCH_RESULT_DIV, MIN_TIME, MAX_TIME);
        item.click("//a[contains(text(),'"+cName+"')]");
    }

    //owner, subject, date, priority, status.
    public void addTask(HashMap<String, String> taskData) {
        item.click(ADD_TASK_BUTTON);
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed(GS_TASK_ASSIGN_INPUT, MIN_TIME, MAX_TIME);
        fillTaskForm(taskData);
        item.click(TASK_SAVE_BUTTON);
        //wait.waitTillElementDisplayed(taskXPath(taskData), MIN_TIME, MAX_TIME);
    }

    private void fillTaskForm(HashMap<String, String> taskData) {
        if(taskData.get("owner") != null) {
            ownerSelect(taskData.get("owner"));
        }
        if(taskData.get("subject") != null) {
            field.setText(GS_TASK_SUBJECT_INPUT, taskData.get("subject"));
        }
        if(taskData.get("date") != null) {
            field.setText(GS_TASK_DATE_INPUT, taskData.get("date"));
        }
        if(taskData.get("priority") != null) {
            field.selectFromDropDown(GS_TASK_PRI0RITY_INPUT, taskData.get("priority"));
        }
        if(taskData.get("status") != null) {
            field.selectFromDropDown(GS_TASK_STATUS_INPUT, taskData.get("status"));
        }
    }

    public void ownerSelect(String ownerName) {
        Report.logInfo("Started selecting the owner:");
        for(int i =0; i<15; i++) {
            List<WebElement> eleList = element.getAllElement("//li[@class='ui-menu-item' and @role='menuitem']");
            Report.logInfo("No of Owners :" +eleList.size());
            boolean autoSuggestionDisplayed = false;
            for(WebElement e : eleList) {
                if(e.isDisplayed()) {
                    autoSuggestionDisplayed = true;
                    break;
                }
            }
            if(autoSuggestionDisplayed) {
                Report.logInfo("Auto Owner Suggestion list displayed");
                break;
            } else {
                Report.logInfo("Auto Suggestion List is not displayed");
                amtDateUtil.stalePause();
            }

        }
        WebElement wEle  = null;
        List<WebElement> eleList = element.getAllElement("//li[@class='ui-menu-item']/a[contains(@class, 'ui-corner-all')]");
        Report.logInfo("Owner List Count : " +eleList.size());
        int count =0;
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                count++;
                Report.logInfo("Actual text :" +ele.getText());
                Report.logInfo("Exp text :" +ele.getText());
                if(ele.getText().contains(ownerName.trim())) {
                    wEle = ele;
                    Report.logInfo("Owner Found");
                    break;
                }
            }
        }
        Report.logInfo("Owner List Displayed Count : " +count);
        wEle.click();
    }

    public int countOfTasks() {
        int count=0;
        List<WebElement> taskList = element.getAllElement(TASK_CARD);
        if(taskList != null && taskList.size() > 0) {
            count = taskList.size();
        }
        return count;
    }

    public boolean isTaskDisplayed(HashMap<String, String> taskData) {
        boolean result = false;
        amtDateUtil.sleep(5);
        List<WebElement> taskslist = element.getAllElement(taskXPath(taskData));
        if(taskslist.size() >0) {
            result = true;
        }
        return result;
    }

    public boolean isAlertDisplayed( AlertCardLabel alertCardLabel, HashMap<String, String> testData) {
        boolean result = false;
        String xpath = buildAlertXpath(alertCardLabel, testData);
        List<WebElement> eleList = element.getAllElement(xpath);
        if(eleList.size() >0) {
            result = true;
        }
        return result;
    }

    private String taskXPath(HashMap<String, String> taskData) {
        String xpath = "//span[@class='taskTitleCls' and contains(text(), '"+taskData.get("subject")+"')]" +
                "/following-sibling::div/div/span[@class='taskDataCls' and contains(text(), '"+taskData.get("owner")+"')]" +
                "/following-sibling::span[@class='taskDataCls' and contains(text(), '"+taskData.get("date")+"')]" +
                "/parent::div/following-sibling::div[@class='showMoreDetailsCls']" +
                "/div/descendant::span[@class='taskDataCls']/b[contains(text(),'"+taskData.get("priority")+"')]" +
                "/parent::span/following-sibling::span/span[contains(text(), '"+taskData.get("status")+"')]" +
                "/ancestor::div[@class='taskItemCls']";
        Report.logInfo("xpath of Task : " +xpath);
        return xpath;
    }

    private WebElement getTaskCard(HashMap<String, String> taskData) {
        WebElement ele = element.getElement(taskXPath(taskData));
        return ele;
    }

    public WebElement getAlertCard(AlertCardLabel alertCardLabel, HashMap<String, String> alertData) {
        WebElement wEle = null;
        wEle = element.getElement(buildAlertXpath(alertCardLabel, alertData));
        return wEle;
    }

    public boolean isAlertDisplayedBasedOnStatus(AlertCardLabel alertLabel, HashMap<String, String> alertData) {
        boolean result = false;
        String xPath = buildAlertXpath(alertLabel, alertData);
        xPath += "/ancestor::div[@class='card-group']/div/div[@class='group-title' and contains(text(), '"+alertData.get("status")+"')]";
        Report.logInfo(xPath);
        List<WebElement> eleList = element.getAllElement(xPath);
        if(eleList != null && eleList.size() >0) {
            result= true;
        }
        return result;
    }

    public void groupExpandOrCollapse(String groupName, boolean expand) {
        String s = "//div[@class='group-title' and contains(text(), '"+groupName+"')]" +
                "/ancestor::div[@class='card-group']/div[@class='group-resize']/div[contains(@class,'resize-handle-up')]";
        String s1 = "//div[@class='group-title' and contains(text(), '"+groupName+"')]" +
                "/ancestor::div[@class='card-group']/div[@class='group-resize']/div[contains(@class,'resize-handle-down')]";
        if(expand) {
            item.click(s1);
            wait.waitTillElementDisplayed(s, MIN_TIME, MAX_TIME);
        } else {
            item.click(s);
            wait.waitTillElementDisplayed(s1, MIN_TIME, MAX_TIME);
        }
        amtDateUtil.stalePause();
    }

    public void closeAlertForm()  {
        item.click(ALERT_FORM_CLOSE);
    }
    public String buildAlertXpath(AlertCardLabel alertLabel, HashMap<String, String> alertData) {
        String xPath = "//";
        if(alertLabel.getLabel5()!=null && alertLabel.getLabel5() != "") {
            xPath = "div[@class='data-label' and contains(text(),'"+alertLabel.getLabel5()+"')]" +
                    "/following-sibling::div[@class='data-value' and contains(text(),";
            if(alertData.get(alertLabel.getLabel5()) != null) {
                xPath += "'"+alertData.get(alertLabel.getLabel5())+"')]";
            } else {
                xPath += "':')]";
            }
            xPath += "/parent::div/preceding-sibling::div[@class='card-data view-card']/";
        }
        if(alertLabel.getLabel4() != null && alertLabel.getLabel4() != "") {
            String s = alertLabel.getLabel4();
            String temp = alertData.get(alertLabel.getLabel4());
            xPath += "div[@class='data-label' and contains(text(),'"+alertLabel.getLabel4()+"')]" +
                    "/following-sibling::div[@class='data-value' and contains(text(),";
            if(alertData.get(alertLabel.getLabel4()) != null) {
                xPath += "'"+alertData.get(alertLabel.getLabel4())+"')]";
            } else {
                xPath += "':')]";
            }
            xPath += "/parent::div/preceding-sibling::div[@class='card-data view-card']/";
        }
        if(alertLabel.getLabel3() != null && alertLabel.getLabel3() != "") {
            xPath += "div[@class='data-label' and contains(text(),'"+alertLabel.getLabel3()+"')]" +
                    "/following-sibling::div[@class='data-value' and contains(text(),";
            if(alertData.get(alertLabel.getLabel3()) != null) {
                xPath += "'"+alertData.get(alertLabel.getLabel3())+"')]";
            } else {
                xPath += "':')]";
            }
            xPath += "/parent::div/preceding-sibling::div[@class='card-data view-card']/";
        }
        if(alertLabel.getLabel2() != null && alertLabel.getLabel2() != "") {
            xPath += "div[@class='data-label' and contains(text(),'"+alertLabel.getLabel2()+"')]" +
                    "/following-sibling::div[@class='data-value' and contains(text(),";
            if(alertData.get(alertLabel.getLabel2()) != null) {
                xPath += "'"+alertData.get(alertLabel.getLabel2())+"')]";
            } else {
                xPath += "':')]";
            }
            xPath += "/parent::div/preceding-sibling::div[@class='card-data view-card']/";
        }
        if(alertLabel.getLabel1() != null && alertLabel.getLabel1() != "") {
            xPath += "div[@class='data-label' and contains(text(),'"+alertLabel.getLabel1()+"')]" +
                    "/following-sibling::div[@class='data-value' and contains(text(),";
            if(alertData.get(alertLabel.getLabel1()) != null) {
                xPath += "'"+alertData.get(alertLabel.getLabel1())+"')]";
            } else {
                xPath += "':')]";
            }
        }
        xPath += "/parent::div/preceding-sibling::div[@class='card-data view-card' and contains(text()," +
                " '"+alertData.get("subject")+"')]";

        xPath += "/preceding-sibling::div[contains(text()," +
                " '"+alertData.get("customer")+"')]";

        xPath += "/parent::div[@class='alert-card-body view-card']/parent::div[@class='alert-card view-card draggable ']";
        Report.logInfo("The Alert Xpath " +xPath);
        return xPath;
    }



    public void addTaskOnAlert(HashMap<String, String> alertData, List<HashMap<String, String>> taskDataList, AlertCardLabel alertCardLabel) {
        openAlertCard(alertData, alertCardLabel);
        for(HashMap<String, String> taskData : taskDataList) {
            addTask(taskData);
        }
        item.click(ALERT_FORM_CLOSE);
    }

    public void openAlertCard(HashMap<String, String> alertData, AlertCardLabel alertCardLabel) {
        WebElement alertCard = getAlertCard(alertCardLabel, alertData);
        if(alertCard != null) {
            alertCard.click();
        }
        waitTillAlertCardLoaded();
    }

    public void selectPlaybook(String pName) {
        item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(PLAYBOOK_SELECT, MIN_TIME, MAX_TIME);
        item.selectFromDropDown(PLAYBOOK_SELECT, pName);
    }

    public void addAlertandTasks(HashMap<String, String> alertData, List<HashMap<String, String>> taskDataList) {
        item.click(ADD_ALERT_BUTTON);
        wait.waitTillElementDisplayed(SUBJECT_INPUT, MIN_TIME, MAX_TIME);
        fillAlertForm(alertData, true);
        item.click(ALERT_SAVE_ADD_TASK_BUTTON);
        for(HashMap<String, String> taskData : taskDataList) {
            addTask(taskData);
        }
        item.click(ALERT_FORM_CLOSE);
    }

    //under construction.
    //High | Downsell | Product Issues
    public void applyfilters(String values) {
        String[] filters = values.split("\\|");
        for(String s: filters) {
            item.click("//div[@class='filter-content' and text()='"+s.trim()+"']");
        }

    }

    public void waitTillAlertCardLoaded() {
        boolean tasksDisplayed = false;
        boolean alertDataLoaded = false;
        for(int i=0; i< 5 ; i++) {
            try {
                if(!alertDataLoaded) {
                    String eventDate = "";
                    try {
                        eventDate = field.getTextFieldValue(SUBJECT_INPUT);
                    } catch (Exception e) {
                        eventDate = field.getText(SUBJECT_DISPLAY);
                    }
                    if(eventDate == null || eventDate.length() <=1) {
                        amtDateUtil.sleep(1);
                        Report.logInfo("Alert details are not loaded still.");
                        continue;
                    } else {
                        alertDataLoaded = true;
                    }
                }

                if(!tasksDisplayed) {
                    try {
                        if(isElementPresentAndDisplay(By.xpath(NO_TASK_PRESENT_MSG))) {
                            tasksDisplayed = true;
                        }
                    }catch (Exception e) {
                        Report.logInfo("Message not displayed");
                    }
                }
                if(!tasksDisplayed) {
                    try {
                        if(isElementPresentAndDisplay(By.xpath(TASK_CARD))) {
                            tasksDisplayed = true;
                        }
                    } catch (Exception e) {
                        Report.logInfo("No Tasks");
                    }
                }
                if(tasksDisplayed && alertDataLoaded) {
                    Report.logInfo("Alert Card loaded successfully");
                    break;
                }
            } catch(RuntimeException e) {
                Report.logInfo("Run time Exception");
                Report.logInfo("Wait for Alert card load failed:" +e.getLocalizedMessage());
            }
        }
    }


}
