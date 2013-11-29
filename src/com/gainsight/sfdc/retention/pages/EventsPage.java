package com.gainsight.sfdc.retention.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pojos.Event;
import com.gainsight.sfdc.retention.pojos.Task;

public class EventsPage extends RetentionBasePage {
    private final String READY_INDICATOR        = "calhomeBtn";
    private final String EVENT_CARD             = "//div[@class='event-card view-card event-card-maindiv']";
    private final String WEEKLABEL_SELECT       = "//select[@class='weekLabelsCls']";
    private final String MONTHLABEL_SELECT      = "//select[@class='monthLabelsCls']";
    private final String QUARTERLABEL_SELECT    = "//select[@class='quarterLabelsCls']";
    private final String HOME_BUTTON     = "calhomeBtn";
    private final String NEXT_BUTTON = "calNextBtn";
    private final String PREVIOUS_BUTTON = "calPrevBtn";
    private final String WEEK_BUTTON = "calWeekBtn";
    private final String MONTH_BUTTON = "calMonthBtn";
    private final String QUARTER_BUTTON = "calQuarterBtn";
    private final String AUTO_OWNER_LIST = "//li[@role='menuitem']";
    private final String ADD_EVENT_BUTTON = "//div[@class='ge-addEventCls btn']";
    private final String CUST_SEARCH_RESULT = "//div[contains(@id, 'CustomerSearchPanel')]";
    private final String CUST_NAME_INPUT = "//input[contains(@class, 'custComponentIdjbaraDummyCustomerName customer-name-text')]";
    private final String EVENT_TYPE_SELECT = "//select[@class='ga_eventTypeCls']";
    private final String EVENT_OWNER_INPUT = "//input[@class='eventOwnerIdInputCls ui-autocomplete-input']";
    private final String EVENT_SUBJECT_INPUT = "ga_subjectFld";
    private final String EVENT_SCHEDULEDATE_INPUT = "ga_sEventDateFld";
    private final String EVENT_STATUS_OPEN_RADIO = "eventOpen";
    private final String EVENT_STATUS_INPROGRESS_RADIO = "eventProgress";
    private final String EVENT_STATUS_COMPLETE_RADIO = "eventComplete";
    private final String EVENT_DESRP_INPUT = "ga_eventDescription";
    private final String TASK_ASSIGNE_INPUT = "//input[@class='Assigned__cInputCls userlookupCls taskParamControlDataInput ui-autocomplete-input']";
    private final String TASK_SUBJECT_INPUT = "//input[@class='Subject__cInputCls taskParamControlDataInput']";
    private final String TASK_DUEDATE_INPUT = "Date__cInputId";
    private final String TASK_PRIORITY_SELECT = "//select[@class='Priority__cInputCls taskParamControlDataInput']";
    private final String TASK_STATUS_SELECT = "//select[@class='Status__cInputCls taskParamControlDataInput']";
    private final String TASK_DESC_INPUT = "//textarea[@class='Description__cInputCls taskParamControlDataInput']";
    private final String TASK_ADD_BUTTON = "//button[@class='btn taskActionBtn taskSaveBtn']";
    private final String TASK_CANCEL_BUTTON = "//button[@class='btn taskActionBtn taskCancelBtn']";
    private final String EVENT_CREATE_BUTTON = "createButton";
    private final String EVENT_CANCEL_BUTTON = "//select[@class='ga_btnSecondary ga_eventClose']";
    private final String EVENT_UPDATE_BUTTON = "createButton";
    private final String ADD_TASK_BUTTON = "//input[@class='btn dummyAddTaskText dummyETAddText overlayBtn' and @value='+ Add Task']";
    private final String ADD_PLAYBOOK_BUTTON ="//input[@class='dummyAddPBText dummyETAddText btn']";
    private final String PLAYBOOK_SELECT = "//select[@class='loadPlaybookCls']";
    private final String CUST_SEARCH_IMG = "//img[@title='Customer Name Lookup']";
    private final String EVENT_REC_CHECK = "IsRecurrence";
    private final String EVENT_REC_DAILY_RADIO = "rectypeftd";
    private final String EVENT_REC_MONTHLY_RADIO = "rectypeftm";
    private final String EVENT_REC_YERALY_RADIO = "rectypefty";
    private final String EVENT_REC_WEEKLY_RADIO = "rectypeftw";
    private final String EVENT_REC_STARTDATE_INPUT = "ga_eSDateFld";
    private final String EVENT_REC_ENDDATE_INPUT = "ga_eEDateFld";
    private final String EVENT_REC_DAILY_EVERYWEEKDAY = "recdd0";
    private final String EVENT_REC_DAILY_EVERYNDAYS = "recdd1";
    private final String EVENT_REC_DAILY_EVERYNDAYS_INPUT = "di";
    private final String EVENT_REC_WEEK_RECURING_INPUT = "wi";
    private final String EVENT_REC_WEEK_SUN = "//div[@class='weekIntervalValuesCount']/input[@id='1']";
    private final String EVENT_REC_WEEK_MON = "//div[@class='weekIntervalValuesCount']/input[@id='2']";
    private final String EVENT_REC_WEEK_TUE = "//div[@class='weekIntervalValuesCount']/input[@id='4']";
    private final String EVENT_REC_WEEK_WED = "//div[@class='weekIntervalValuesCount']/input[@id='8']";
    private final String EVENT_REC_WEEK_THU = "//div[@class='weekIntervalValuesCount']/input[@id='16']";
    private final String EVENT_REC_WEEK_FRI = "//div[@class='weekIntervalValuesCount']/input[@id='32']";
    private final String EVENT_REC_WEEK_SAT = "//div[@class='weekIntervalValuesCount']/input[@id='64']";
    private final String EVENT_REC_MONTH_OP1 = "recmm0";
    private final String EVENT_REC_MONTH_OP2 = "recmm1";
    private final String EVENT_REC_MONTH_OP1_DAY_SELECT = "mdom";
    private final String EVENT_REC_MONTH_OP1_INPUT = "mint";
    private final String EVENT_REC_MONTH_OP2_SELECT = "mnins";
    private final String EVENT_REC_MONTH_OP2_DAY_SELECT = "mndow";
    private final String EVENT_REC_MONTH_OP2_INPUT = "mnint";
    private final String EVENT_REC_YEAR_OP1 = "recyy0";
    private final String EVENT_REC_YEAR_OP2 = "recyy1";
    private final String EVENT_REC_YEAR_OP1_MONTH_SELECT = "ymoy";
    private final String EVENT_REC_YEAR_OP1_DAY_SELECT = "ydom";
    private final String EVENT_REC_YEAR_OP2_OPTION_SELECT = "ynins";
    private final String EVENT_REC_YEAR_OP2_DAY_SELECT = "yndow";
    private final String EVENT_REC_YEAR_OP2_MONTH_SELECT = "ynmoy";
    private final String HIDE_FILTER_IMG = "//div[@class='clickToBringIn']/img[@title='Hide filters']";
    private final String SHOW_FILTER_IMG = "//div[@class='clickToBringIn']/img[@title='Show filters']";
    private final String EVENT_CLOSE_BUTTON = "//div[@class='ui-dialog-titlebar ui-widget-header ui-helper-clearfix']/a";
    private final String EVENT_REC_TABLE = "//table[@class='ga_recurringTable']";
    private final String EVENT_SEC_HEADER = "ge-sectionHeadDates";
    private final String TASK_CARD = "//div[@class='taskDetailsCls taskDetailsBorderCls']";
    private final String EVENT_TYPE_VALUE = "eventType";
    private final String EVENT_OWNER_VALUE = "eventAssigned";
    private final String EVENT_SUBJECT_VALUE = "eventSubject";
    private final String EVENT_STATUS_VALUE = "eventStatus";
    private final String EVENT_DATE_VALUE = "eventSDate";
    private final String EVENT_DESC_VALUE = "eventDescription";

    public EventsPage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public void waitTillEventCardsLoad() {
        amtDateUtil.stalePause();
        wait.waitTillElementNotDisplayed("//img[@class='waitingImage']", MIN_TIME, MAX_TIME);
        wait.waitTillElementNotDisplayed("//span[@class='waitingDescription']", MIN_TIME, MAX_TIME);
    }

    public void weekView() {
        item.click(WEEK_BUTTON);
        wait.waitTillElementDisplayed(WEEKLABEL_SELECT, MIN_TIME, MAX_TIME);
        waitTillEventCardsLoad();
    }

    public void monthView() {
        item.click(MONTH_BUTTON);
        wait.waitTillElementDisplayed(MONTHLABEL_SELECT, MIN_TIME, MAX_TIME);
        waitTillEventCardsLoad();
    }

    public void quarterView() {
        item.click(QUARTER_BUTTON);
        wait.waitTillElementDisplayed(QUARTERLABEL_SELECT, MIN_TIME, MAX_TIME);
        waitTillEventCardsLoad();
    }
    private void clickOnAddEvent() {
        button.click(ADD_EVENT_BUTTON);
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed(CUST_NAME_INPUT, MIN_TIME, MAX_TIME);
    }
    public void clickOnCloseEventCard( ) {
        item.click(EVENT_CLOSE_BUTTON);
        try {
            modal.accept();
        } catch(NoAlertPresentException e) {
            Report.logInfo(e.getLocalizedMessage());
        }
        wait.waitTillElementDisplayed(ADD_EVENT_BUTTON, MIN_TIME, MAX_TIME);
    }

    public void addEventandTask(HashMap<String, String> eventdata, HashMap<String, String> taskData) {
        Report.logInfo("Started adding the event & task on the event");
        int taskNo = 0;
        clickOnAddEvent();
        fillEventForm(eventdata);
        if(eventdata.get("recurring") != null && eventdata.get("recurring").equalsIgnoreCase("TRUE")) {
            recEventForm(eventdata.get("frequency"), eventdata.get("startdate"), eventdata.get("enddate"));
        }
        if(eventdata.get("tasks") != null && eventdata.get("tasks").equalsIgnoreCase("true") ) {
            addTask(taskData);
        }
        if(eventdata.get("playbook") != null && !eventdata.get("playbook").equalsIgnoreCase("NA")) {
            selectPlaybook(eventdata.get("playbook"));
        }
        clickOnCreateEvent();
        Report.logInfo("Finished adding the event & task on the event");
    }

    public void fillEventForm(HashMap<String, String> testdata) {
        Report.logInfo("Filling in the events field values.");
        wait.waitTillElementDisplayed(CUST_NAME_INPUT, MIN_TIME, MAX_TIME);
        if(testdata.get("customer") != null) {
            field.setTextField(CUST_NAME_INPUT, testdata.get("customer"));
            item.click(CUST_SEARCH_IMG);
            wait.waitTillElementDisplayed(CUST_SEARCH_RESULT, MIN_TIME, MAX_TIME);
            item.click("//a[contains(text(), '"+testdata.get("customer").trim()+"')]");
        }
        if(testdata.get("type") != null) {
            field.selectFromDropDown(EVENT_TYPE_SELECT, testdata.get("type").trim());
        }
        if(testdata.get("owner") != null) {
            field.clearAndSetText(EVENT_OWNER_INPUT, testdata.get("owner"));
            ownerSelect(testdata.get("owner"));
        }
        if(testdata.get("subject") != null) {
            wait.waitTillElementDisplayed(EVENT_SUBJECT_INPUT, MIN_TIME, MAX_TIME);
            field.setTextField(EVENT_SUBJECT_INPUT, testdata.get("subject"));
        }
        if(testdata.get("schedule") != null) {
            field.clearAndSetText(EVENT_SCHEDULEDATE_INPUT, testdata.get("schedule"));
        }

        if(testdata.get("status").equalsIgnoreCase("Open")) {
            String ch = item.getElement(EVENT_STATUS_OPEN_RADIO).getAttribute("checked");
            if(ch == null) {
                item.click(EVENT_STATUS_OPEN_RADIO);
            }
        } else if(testdata.get("status").equalsIgnoreCase("In Progress")) {
            String ch = item.getElement(EVENT_STATUS_INPROGRESS_RADIO).getAttribute("checked");
            if(ch == null) {
                item.click(EVENT_STATUS_INPROGRESS_RADIO);
            }
        } else if(testdata.get("status").equalsIgnoreCase("Complete")) {
            String ch = item.getElement(EVENT_STATUS_COMPLETE_RADIO).getAttribute("checked");
            if(ch == null) {
                item.click(EVENT_STATUS_COMPLETE_RADIO);
            }
        }

        if(testdata.get("description") != null ) {
            field.setTextField(EVENT_DESRP_INPUT, testdata.get("description"));
        }
        Report.logInfo("Filled values of event form");
    }

    public void ownerSelect(String ownerName) {
        Report.logInfo("Started selecting the owner of event");
        for(int i =0; i<15; i++) {
            List<WebElement> eleList = element.getAllElement("//li[@class='ui-menu-item' and @role='menuitem']");
            boolean isOwnerDisplayed = false;
            for(WebElement e : eleList) {
                if(e.isDisplayed()) {
                    isOwnerDisplayed = true;
                    break;
                } else {
                    amtDateUtil.sleep(1);
                }
            }
            if(isOwnerDisplayed) {
                break;
            }
        }
        WebElement wEle = null;
        List<WebElement> eleList = element.getAllElement("//a[contains(@class, 'ui-corner-all')]");
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()){
                String s = ele.getText();
                System.out.println("AccText :" +s);
                System.out.println("Exp Text:" +ownerName);
                if(s.contains(ownerName)){
                    wEle = ele;
                    break;
                }
            }
        }
        if(wEle != null) {
            Actions builder = new Actions(driver);
            builder.moveToElement(wEle);
            builder.click(wEle);
            Action selectedAction = builder.build();
            selectedAction.perform();
            Report.logInfo("Finished selecting the owner for event");
        } else {
            Report.logInfo("FAIL: Failed to select the owner for the event");
        }
    }

    public void fillTaskForm(HashMap<String, String> taskData) {
        Report.logInfo("Started filling the task form on Task card.");
        wait.waitTillElementDisplayed(TASK_ASSIGNE_INPUT, MIN_TIME, MAX_TIME);
        if(taskData.get("assignee") != null) {
            field.setTextField(TASK_ASSIGNE_INPUT, taskData.get("assignee"));
            ownerSelect(taskData.get("assignee"));
        }
        if(taskData.get("subject") != null) {
            field.setText(TASK_SUBJECT_INPUT, taskData.get("subject"));
        }
        if(taskData.get("date") != null) {
            field.clearText(TASK_DUEDATE_INPUT);
            field.setText(TASK_DUEDATE_INPUT, taskData.get("date"));
        }
        if(taskData.get("priority") != null) {
            field.setSelectField(TASK_PRIORITY_SELECT, taskData.get("priority"));
        }
        if(taskData.get("status") != null) {
            field.setSelectField(TASK_STATUS_SELECT, taskData.get("status"));
        }
		/*
		if(taskData.get("desc") != null) {
			field.setTextField(TASK_DESC_INPUT, taskData.get("desc"));
		}
		*/
        Report.logInfo("Completed filling the task form on event card.");
    }

    public void addTask(HashMap<String, String> testData) {
        Report.logInfo("Started adding a task on the event.");
        clickOnAddTask();
        fillTaskForm(testData);
        clickOnSaveTask();
        wait.waitTillElementDisplayed("//div[@class='taskDetailsCls taskDetailsBorderCls']/h4[text()='"+testData.get("subject")+"']",
                MIN_TIME, MAX_TIME);
        Report.logInfo("Finished adding a task on the event");
    }

    public void addTask(HashMap<String, String> eventData, List<HashMap<String, String>> taskDataList) {
        if(eventData != null) {
            openEventCard(eventData);
            if(taskDataList != null && taskDataList.size() >0 ) {
                for(HashMap<String, String> taskData : taskDataList) {
                    addTask(taskData);
                }
                clickOnUpdateEvent(eventData);
                Report.logInfo("Finished adding tasks");
                amtDateUtil.stalePause();
            } else {
                Report.logInfo("No Tasks Sent for adding");
            }
        }
    }

    public void clickOnAddTask() {
        item.click(ADD_TASK_BUTTON);
        wait.waitTillElementDisplayed(TASK_ASSIGNE_INPUT, MIN_TIME, MAX_TIME);
        Report.logInfo("Clicked on Add-Task button.");
    }

    public void selectPlaybook(String pbName) {
        Report.logInfo("Selecting a Playbook on a event card");
        item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(PLAYBOOK_SELECT, MIN_TIME, MAX_TIME);
        item.selectFromDropDown(PLAYBOOK_SELECT, pbName);
        Report.logInfo("Playbook selection on event card is completed.");
        amtDateUtil.sleep(5);
    }

    public void clickOnSaveTask() {
        Report.logInfo("CLicking on add task button.");
        item.click(TASK_ADD_BUTTON);
        Report.logInfo("Clicked on add task button.");
    }

    public void clickOnCreateEvent() {
        Report.logInfo("Clicking on saving event.");
        button.click(EVENT_CREATE_BUTTON);
        Report.logInfo("Clicked on saving the event");
    }

    public void clickOnUpdateEvent(HashMap<String, String> eventdata) {
        Report.logInfo("Clicking on updating event.");
        button.click(EVENT_UPDATE_BUTTON);
        amtDateUtil.stalePause();
        String xpath = "//div[@class='data-value scheduledDateValCls' "
                + "and contains(text(), '"+eventdata.get("schedule")+"')]/parent::div"
                + "/preceding-sibling::div/div[contains(@title,'"+eventdata.get("owner")+"')]"
                + "/parent::div/preceding-sibling::div[@title='"+eventdata.get("subject")+"']"
                + "/preceding-sibling::div[text()='"+eventdata.get("type")+"']"
                + "/preceding-sibling::div[@title='"+eventdata.get("customer")+"']"
                + "/parent::div[@class='event-card-body view-card']"
                + "/parent::div[@class='event-card view-card event-card-maindiv']";
        wait.waitTillElementDisplayed(xpath, MIN_TIME, MAX_TIME);
        Report.logInfo("Clicked on Updating the event");
    }

    private boolean isErrMsgDisplayed(String s) {
        boolean result = false;
        WebElement wEle = item.getElement("//li[@class='dummyCustomErrors' and contains(text(), '"+s+"')]");
        if(wEle != null) {
            if(wEle.isDisplayed()) {
                result = true;
            }
        }
        wEle = item.getElement("//img[@alt='FATAL' and @title='FATAL']");
        if(wEle !=null){
            if(wEle.isDisplayed()) {
                result = true;
            }
        }
        return result;
    }


    public int noOfEventCards() {
        int count = 0;
        List<WebElement> eleList = element.getAllElement(EVENT_CARD);
        if(eleList != null) {
            count = eleList.size();
            Report.logInfo("Total Cards Found: "+count);
        } else {
            Report.logInfo("No events Find");
        }
        return count;
    }

    public void clickOnWeekLabel() {
        Report.logInfo("Clicking on to View Event in WEEK");
        button.click(WEEK_BUTTON);
        wait.waitTillElementDisplayed(WEEKLABEL_SELECT, MIN_TIME, MAX_TIME);
    }

    public void clickOnMonthLabel() {
        Report.logInfo("Clicking on to View Event in MONTH");
        button.click(MONTH_BUTTON);
        wait.waitTillElementDisplayed(MONTHLABEL_SELECT, MIN_TIME, MAX_TIME);
    }
    public void clickOnQuarterLabel() {
        Report.logInfo("Clicking on to View Event in YEAR");
        wait.waitTillElementDisplayed(QUARTERLABEL_SELECT, MIN_TIME, MAX_TIME);
        button.click(QUARTER_BUTTON);
    }

    public boolean verifyWeekLevelDropdown(String expValue) {
        boolean result = false;
        try {
            Select s = (Select) element.getElement(WEEKLABEL_SELECT);
            String actualValue  = s.getFirstSelectedOption().getText().trim();
            if(expValue != null) {
                if(expValue.equalsIgnoreCase(actualValue)) {
                    result = true;
                    Report.logInfo("SUCCESS: Found the expected value");
                }
            } else {
                Report.logInfo("FAIL: Please send the expected value");
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed in verifying Weeklevel drop-down: " +e.getLocalizedMessage());
        }
        return result;
    }

    public boolean verifyMonthLevelDropdown(String expValue) {
        boolean result = false;
        try {
            Select s = (Select) element.getElement(MONTHLABEL_SELECT);
            String actualValue  = s.getFirstSelectedOption().getText().trim();
            if(expValue != null) {
                if(expValue.equalsIgnoreCase(actualValue)) {
                    result = true;
                    Report.logInfo("SUCCESS: Found the expected value");
                }
            } else {
                Report.logInfo("FAIL: Please send the expected value");
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed in verifying MonthLevel drop-down: " +e.getLocalizedMessage());
        }

        return result;
    }

    public boolean verifyQuarterLevelDropdown(String expValue) {
        boolean result = false;
        try {
            Select s = (Select) element.getElement(QUARTERLABEL_SELECT);
            String actualValue  = s.getFirstSelectedOption().getText().trim();
            if(expValue != null) {
                if(expValue.equalsIgnoreCase(actualValue)) {
                    result = true;
                    Report.logInfo("SUCCESS: Found the expected value");
                }
            } else {
                Report.logInfo("FAIL: Please send the expected value");
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed in verifying MonthLevel drop-down: " +e.getLocalizedMessage());
        }
        return result;
    }

    public boolean verifyLayoutHeaderText(String expvalue) {
        Report.logInfo("Started Checking for the layout header text");
        boolean result = false;
        String actualValue = element.getText("//div[@id='ge-sectionHeadDates']");
        System.out.println("actual :" +actualValue);
        System.out.println("Exp : " +expvalue);
        if(expvalue.equalsIgnoreCase(actualValue)) {
            result = true;
        } else if(expvalue.contains(actualValue)) { //Time being need to change appropriately.
            result = true;
        }
        Report.logInfo("Finished cheking for the text & returing result :" +result);
        return result;
    }

    public boolean isEventDisplayed(HashMap<String, String> testdata) {
        boolean result = false;
        amtDateUtil.stalePause();
            WebElement event = getEventInCardLayout(testdata);
            if(event != null) {
                result = true;
            }
        return result;
    }

    public WebElement getTaskElement(HashMap<String, String> testdata) {
        WebElement task = null;
        //Need modification has the xpath specified is not acceptable.
        String taskEle = "//div[@class= 'ga_value statusValue taskStatusCls' and text()='"+testdata.get("status")+"']"
                + "/parent::div[@class='ga_fltr']"
                + "/preceding-sibling::div[@class='ga_data statusPriorityContainer']"
                + "/div/div[@class='ga_value' and text()='"+testdata.get("priority")+"']"
                + "/ancestor::div[@class='ga_data statusPriorityContainer']"
                + "/preceding-sibling::div[@class='ga_data']/p[contains(text(), '"+testdata.get("assignee")+"')]"
                + "/ancestor::div[@class='ga_data']/preceding-sibling::h4[text()='"+testdata.get("subject")+"']"
                + "/parent::div[@class='taskDetailsCls taskDetailsBorderCls']";
        Report.logInfo("Searching Task:" +taskEle);
        try  {
            if(element.getElement(taskEle).isDisplayed()) {
                task = element.getElement(taskEle);
            }
        } catch(RuntimeException e) {
            Report.logInfo("Task Not Found:"+e.getLocalizedMessage());
        }
        return task;
    }

    public boolean isTaskDisplayed(HashMap<String, String> testdata) {
        boolean result = false;
        WebElement task = getTaskElement(testdata);
        if(task != null) {
            result = true;
        }
        return result;
    }

    public boolean isTaskDisplayeddummy(Task task) {
        boolean result = false;
        Report.logInfo("Started verifying the task present");
        for(Task t : getAllTasks()) {
            if(t.getOwner().contains(task.getOwner()) && t.getSubject().equalsIgnoreCase(task.getSubject())
                    && t.getPriority().equalsIgnoreCase(task.getPriority())
                    && t.getStatus().equalsIgnoreCase(task.getStatus())) {
                result = true;
            }
        }

        Report.logInfo("Finished verifying the task display & returning result :" +result);
        return result;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<Task>();
        List<WebElement> eleList = element.getAllElement(TASK_CARD);
        System.out.println(eleList.size());
        for(WebElement ele : eleList) {
            Task t = new Task();
            t.setSubject(ele.findElement(By.className("ga_fltl")).getText().trim());
            t.setOwner(ele.findElement(By.className("ga_data")).getText().trim());
            t.setPriority(ele.findElement(By.className("ga_value")).getText().trim());
            t.setStatus(ele.findElement(By.cssSelector("div.ga_value.statusValue.taskStatusCls")).getText().trim());
            taskList.add(t);
        }
        System.out.println("No of Tasks on Event :" +taskList.size());
        return taskList;
    }

    public void clickOnTaskEdit(HashMap<String, String> testdata) {
        WebElement Task = getTaskElement(testdata);
        WebElement taskEditIcon = Task.findElement(By.cssSelector("a.ga_icn.ga_edit"));
        taskEditIcon.click();
        wait.waitTillElementDisplayed(TASK_ADD_BUTTON, MIN_TIME, MAX_TIME);
        item.click(TASK_CANCEL_BUTTON);
        amtDateUtil.stalePause();
    }

    public void deleteTask(HashMap<String, String> testdata) {
        WebElement Task = getTaskElement(testdata);
        if(Task != null) {
            WebElement taskDeleteIcon = Task.findElement(By.cssSelector("a.ga_icn.ga_ignore"));
            taskDeleteIcon.click();
            modal.accept();
            amtDateUtil.stalePause();
        } else {
            Report.logInfo("Delete Task Failed as no task found");
        }

    }
    public void deleteEvent(HashMap<String, String> testdata) {
        Report.logInfo("Started deleting the Event");
        WebElement event = getEventInCardLayout(testdata);
        if(event != null) {
            WebElement eventDeleteIcon = event.findElement(By.cssSelector("a.deleteIconList.ge-icn.ge-edit.ge-deleteEvent"));
            if(eventDeleteIcon != null) {
                eventDeleteIcon.click();
                modal.accept();
                Report.logInfo("Finished deleting the Event");
            } else {
                Report.logInfo("Deletion of event failed.");
            }
        } else {
            Report.logInfo("Event Is not found & so could not delete");
        }
    }

    public void waitforEventCardDisplay(HashMap<String, String> testdata) {
        String elePath = "//div[@class='data-value scheduledDateValCls' "
                + "and contains(text(), '"+testdata.get("schedule")+"')]/parent::div"
                + "/preceding-sibling::div/div[contains(@title,'"+testdata.get("owner")+"')]"
                + "/parent::div/preceding-sibling::div[@title='"+testdata.get("subject")+"']"
                + "/preceding-sibling::div[text()='"+testdata.get("type")+"']"
                + "/preceding-sibling::div[@title='"+testdata.get("customer")+"']"
                + "/parent::div[@class='event-card-body view-card']"
                + "/parent::div[@class='event-card view-card event-card-maindiv']";
        wait.waitTillElementDisplayed(elePath, MIN_TIME, MAX_TIME);

    }
    public WebElement getEventInCardLayout(HashMap<String, String> testdata) {
        WebElement eventCard = null;
        for(int i =0; i<2;i++) {
            String elePath = "//div[@class='data-value scheduledDateValCls' "
                    + "and contains(text(), '"+testdata.get("schedule")+"')]/parent::div"
                    + "/preceding-sibling::div/div[contains(@title,'"+testdata.get("owner")+"')]"
                    + "/parent::div/preceding-sibling::div[@title='"+testdata.get("subject")+"']"
                    + "/preceding-sibling::div[text()='"+testdata.get("type")+"']"
                    + "/preceding-sibling::div[@title='"+testdata.get("customer")+"']"
                    + "/parent::div[@class='event-card-body view-card']"
                    + "/parent::div[@class='event-card view-card event-card-maindiv']";
            List<WebElement> eventCardsList = element.getAllElement(elePath);
            if(eventCardsList  != null && eventCardsList.size() >0) {
                eventCard = eventCardsList.get(0);
                Report.logInfo("Found the event Card");
                break;
            } else {
                amtDateUtil.stalePause();
                Report.logInfo("Event card is not found");
            }
        }
        return eventCard;
    }

    public void waitforEventCardtoLoad() {
        boolean tasksDisplayed = false;
        boolean eventDataLoaded = false;
        for(int i=0; i< 2 ; i++) {
            try {
                if(!eventDataLoaded) {
                    String eventType = element.getElement("eventType").getText();
                    if(eventType == null || eventType.length() <=1) {
                        amtDateUtil.sleep(1);
                        Report.logInfo("Event details are not loaded still.");
                        continue;
                    } else {
                        eventDataLoaded = true;
                    }
                }
                if(!tasksDisplayed) {
                    List<WebElement> eleList = element.getAllElement("//div[@class='taskDetailsCls taskDetailsBorderCls']");
                    if(eleList.size() ==0) {
                        amtDateUtil.sleep(1);
                        Report.logInfo("Tasks on Event card are not loaded still");
                    } else {
                        tasksDisplayed = true;
                    }
                }
                if(tasksDisplayed && eventDataLoaded) {
                    Report.logInfo("Event Card loaded successfully");
                    break;
                }
            } catch(RuntimeException e) {
                Report.logInfo("Run time Exception");
                Report.logInfo("Wait for event card load failed:" +e.getLocalizedMessage());
            }
        }
    }

    public void openEventCard(HashMap<String, String> testdata) {
        Report.logInfo("Started opening the event card");
        try {
            WebElement eventCard = getEventInCardLayout(testdata);
            if(eventCard != null) {
                eventCard.click();
                waitforEventCardtoLoad();
                amtDateUtil.stalePause();
                Report.logInfo("Event card found & opened the card successfully");
            } else {
                Report.logInfo("Event Card is not found, failed to open the event card");
            }
        } catch(StaleElementReferenceException e ) {
            amtDateUtil.sleep(10);
            WebElement eventCard = getEventInCardLayout(testdata);
            if(eventCard != null) {
                eventCard.click();
                waitforEventCardtoLoad();
                amtDateUtil.stalePause();
                Report.logInfo("Event card found & opened the card successfully");
            } else {
                Report.logInfo("Event Card is not found, failed to open the event card");
            }
        }

    }

    public void openEventCardEditMode(HashMap<String, String> testdata) {
        WebElement eventCard = getEventInCardLayout(testdata);
        WebElement editIcon = eventCard.findElement(By.cssSelector("a.ge-icn.ge-edit.ge-editEvent"));
        editIcon.click();
    }

    public void changeStatus(HashMap<String, String> testdata, String status) {
        WebElement eventCard = getEventInCardLayout(testdata);
        WebElement statusChageIcon = eventCard.findElement(By.cssSelector("a.ge-arrow.eventStatusCls"));
        statusChageIcon.click();
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed("//table[@class='loadEventStatusDiv']", MIN_TIME, MAX_TIME);
        if(status.trim().equalsIgnoreCase("Open")) {
            item.click("//input[@id='Open' and @value='Open']");
        } else if(status.trim().equalsIgnoreCase("In Progress")) {
            item.click("//input[@id='Progress' and @value='In Progress']");
        } else if(status.trim().equalsIgnoreCase("Complete")) {
            item.click("//input[@id='Complete' and @value='Complete']");
        }
        //item.click(EVENT_SEC_HEADER);
        amtDateUtil.stalePause();

    }

    public boolean verifyEventCardStatus(HashMap<String, String> testdata, String status) {
        boolean result = false;
        WebElement eventCard = getEventInCardLayout(testdata);
        WebElement statusChageIcon = eventCard.findElement(By.cssSelector("a.ge-arrow.eventStatusCls"));
        statusChageIcon.click();
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed("//table[@class='loadEventStatusDiv']", MIN_TIME, MAX_TIME);
        if(status.trim().equalsIgnoreCase("Open")) {
            WebElement openStatus_Radio = element.getElement("//input[@id='Open' and @value='Open']");
            String isChecked = openStatus_Radio.getAttribute("checked");
            if(isChecked.equals("true")){
                result = true;
            }
        } else if(status.trim().equalsIgnoreCase("In Progress")) {
            WebElement inProgressStatus_Radio = element.getElement("//input[@id='Progress' and @value='In Progress']");
            String isChecked = inProgressStatus_Radio.getAttribute("checked");
            if(isChecked.equals("true")){
                result = true;
            }
        } else if(status.trim().equalsIgnoreCase("Complete")) {
            WebElement completeStatus_Radio = element.getElement("//input[@id='Complete' and @value='Complete']");
            String isChecked = completeStatus_Radio.getAttribute("checked");
            if(isChecked.equals("true")){
                result = true;
            }
        }
        item.click(EVENT_SEC_HEADER);
        return result;
    }

    public List<Event> getAllEvents() {
        amtDateUtil.sleep(5);
        List<Event> eventsList = new ArrayList<Event>();
        List<WebElement> eleList = element.getAllElement(EVENT_CARD);
        System.out.println(eleList.size());
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                Event t = new Event();
                t.setCustomer(ele.findElement(By.cssSelector("div.eventType-value.customer-value")).getText().trim());
                t.setType(ele.findElement(By.cssSelector("div.card-data.event-type-value")).getText().trim());
                t.setSubject(ele.findElement(By.cssSelector("div.card-data.view-card")).getText().trim());
                t.setOwner(ele.findElement(By.cssSelector("span.boldusername")).getText().trim());
                t.setScDate(ele.findElement(By.cssSelector("div.data-value.scheduledDateValCls")).getText().trim());
                t.setEventStatusMsg(ele.findElement(By.cssSelector("div.eventStatusTextCls.newEventTypeColor")).getText().trim());
                eventsList.add(t);
            }
        }
        System.out.println("No of Events Displayed :" +eventsList.size());
        return eventsList;
    }

    public void showFilters() {
        if(element.isElementPresent(SHOW_FILTER_IMG)) {
            Report.logInfo("Displaying the filters");
            item.click(SHOW_FILTER_IMG);
            wait.waitTillElementDisplayed(HIDE_FILTER_IMG, MIN_TIME, MAX_TIME);
            Report.logInfo("Filters displayed");
        } else {
            Report.logInfo("Filters are already hidden");
        }
    }

    public void hideFilters() {
        if(element.isElementPresent(HIDE_FILTER_IMG)) {
            Report.logInfo("Closing the filters");
            item.click(HIDE_FILTER_IMG);
            wait.waitTillElementDisplayed(SHOW_FILTER_IMG, MIN_TIME, MAX_TIME);
            Report.logInfo("Closed the filters successfully");
        } else {
            Report.logInfo("Filters are already hidden");
        }
    }

    public void clearAllFilters() {
        Report.logInfo("Swithcing off all the filters");
        showFilters();
        List<WebElement> filterButtonList = element.getAllElement("//div[@class='filter-check-options filter-check-on']");
        Report.logInfo("Total Filters to clear:" +filterButtonList.size());
        for(WebElement ele : filterButtonList) {
            ele.click();
        }
        filterButtonList = element.getAllElement("//div[@class='filter-check-options filter-check-on']");
        if(filterButtonList.size() > 0) {
            Report.logInfo("All the filters are not switched off");
        } else {
            Report.logInfo("Switched all the filters");
        }
        hideFilters();
    }

    public boolean isFiltersDisplayed() {
        boolean result = false;
        String FILTERSHEADER = "tabFilterID";
        try {
            if(element.getElement(FILTERSHEADER).isDisplayed() && element.getElement(HIDE_FILTER_IMG).isDisplayed()) {
                result = true;
                Report.logInfo("Filters are displayed.");
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed to verify is Filters displayed");
        }
        return result;
    }

    public void applyFilter(String testdata) {
        // "Open | In Progress | SprintPlanning";
        showFilters();
        String[] s = testdata.split("\\|");
        int noOfFilterstoApply = s.length;
        try {
            for(String s1 : s) {
                WebElement ele = element.getElement("//div[@class='filter-content' and @title='"+s1.trim()+"']"
                        + "/following-sibling::div[contains(@class, 'filter-check-options')]");
                ele.click();
            }
            amtDateUtil.stalePause();
            List<WebElement> filterButtonList = element.getAllElement("//div[@class='filter-check-options filter-check-on']");
            Report.logInfo("Total Filters Applied :" +filterButtonList.size());
            if(filterButtonList.size() == noOfFilterstoApply) {
                Report.logInfo(" Event filters are applied");
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed to Apply filters:" +e.getLocalizedMessage());
        }
        amtDateUtil.stalePause();
        hideFilters();
    }

    public boolean isFiltersOn(String testdata) {
        boolean result = false;
        //"Open | In Progress | SprintPlanning";
        String[] s = testdata.split("\\|");
        Report.logInfo("No of Filters should be On :" +s.length);
        showFilters();
        try {
            List<WebElement> filterButtonList = element.getAllElement("//div[@class='filter-check-options filter-check-on']"
                    + "/preceding-sibling::div[@class='filter-content']");
            System.out.println("Total Filters Applied :" +filterButtonList.size());
            String s1 = "";
            for(WebElement ele : filterButtonList) {
                s1 += ele.getAttribute("title") + " | ";
            }
            for(String s2 : testdata.split("\\|")) {
                if(s1.contains(s2)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
            if(result) {
                if(filterButtonList.size() == s.length) {
                    Report.logInfo("filters supplied & filters verified count is same");
                }
            }
        } catch(RuntimeException e) {
            Report.logInfo("Failed in checking wether filters are applied:" +e.getLocalizedMessage());
        }
        return result;
    }

    public void recEventForm(String testData, String startDate, String endDate) {
        item.click(EVENT_REC_CHECK);
        wait.waitTillElementDisplayed(EVENT_REC_TABLE, MIN_TIME, MAX_TIME);
        Report.logInfo("Started on filling recurring event");
        if(testData != null && !testData.equalsIgnoreCase("NA")) {
            String[] values = null;
            String[] invalues = null;
            values = testData.split("@");
            if(values.length ==3) {
                if(values[0].trim().contains("Daily")) {
                    item.click(EVENT_REC_DAILY_RADIO);
                    if((values[1]).trim().equalsIgnoreCase("1")) {
                        item.click(EVENT_REC_DAILY_EVERYWEEKDAY);
                    } else if((values[1]).trim().equalsIgnoreCase("2")) {
                        item.click(EVENT_REC_DAILY_EVERYNDAYS);
                        field.clearAndSetText(EVENT_REC_DAILY_EVERYNDAYS_INPUT, values[2].trim());
                    }
                } else if(values[0].trim().contains("Weekly")) {
                    item.click(EVENT_REC_WEEKLY_RADIO);
                    field.clearAndSetText(EVENT_REC_WEEK_RECURING_INPUT, values[1].trim());
                    invalues = values[2].trim().split(",");
                    Report.logInfo("Selecting Week days :" +invalues);
                    for(String s : invalues) {
                        if(s.trim().equalsIgnoreCase("Sun")) {
                            item.click(EVENT_REC_WEEK_SUN);
                        } else if(s.trim().equalsIgnoreCase("Mon")) {
                            item.click(EVENT_REC_WEEK_MON);
                        } else if(s.trim().equalsIgnoreCase("Tue")) {
                            item.click(EVENT_REC_WEEK_TUE);
                        } else if(s.trim().equalsIgnoreCase("Wed")) {
                            item.click(EVENT_REC_WEEK_WED);
                        } else if(s.trim().equalsIgnoreCase("Thu")) {
                            item.click(EVENT_REC_WEEK_THU);
                        } else if(s.trim().equalsIgnoreCase("Fri")) {
                            item.click(EVENT_REC_WEEK_FRI);
                        } else if(s.trim().equalsIgnoreCase("Sat")) {
                            item.click(EVENT_REC_WEEK_SAT);
                        }
                    }
                } else if(values[0].trim().contains("Monthly")) {
                    item.click(EVENT_REC_MONTHLY_RADIO);
                    if((values[1]).trim().equalsIgnoreCase("1")) {
                        item.click(EVENT_REC_MONTH_OP1);
                        invalues = values[2].trim().split(",");
                        if(invalues.length ==2) {
                            field.selectFromDropDown(EVENT_REC_MONTH_OP1_DAY_SELECT, invalues[0].trim());
                            field.clearAndSetText(EVENT_REC_MONTH_OP1_INPUT, invalues[1].trim());
                        } else {
                            Report.logInfo("FAIL : Your monthly data format(1) is not Good");
                        }
                    } else if((values[1]).trim().equalsIgnoreCase("2")) {
                        item.click(EVENT_REC_MONTH_OP2);
                        invalues = values[2].trim().split(",");
                        if(invalues.length == 3) {
                            field.selectFromDropDown(EVENT_REC_MONTH_OP2_SELECT, invalues[0].trim());
                            field.selectFromDropDown(EVENT_REC_MONTH_OP2_DAY_SELECT, invalues[1].trim());
                            field.setText(EVENT_REC_MONTH_OP2_INPUT, invalues[2].trim());
                        } else {
                            Report.logInfo("FAIL : Your monthly data format(2) is not Good");
                        }
                    }
                } else if(values[0].trim().contains("Yearly")) {
                    item.click(EVENT_REC_YERALY_RADIO);
                    if((values[1]).trim().equalsIgnoreCase("1")) {
                        item.click(EVENT_REC_YEAR_OP1);
                        invalues = values[2].trim().split(",");
                        if(invalues.length ==2) {
                            field.selectFromDropDown(EVENT_REC_YEAR_OP1_MONTH_SELECT, invalues[0].trim());
                            field.selectFromDropDown(EVENT_REC_YEAR_OP1_DAY_SELECT, invalues[1].trim());
                        } else {
                            Report.logInfo("FAIL : Your monthly data format(1) is not Good");
                        }
                    } else if((values[1]).trim().equalsIgnoreCase("2")) {
                        item.click(EVENT_REC_YEAR_OP2);
                        invalues = values[2].trim().split(",");
                        if(invalues.length ==3) {
                            field.selectFromDropDown(EVENT_REC_YEAR_OP2_OPTION_SELECT, invalues[0].trim());
                            field.selectFromDropDown(EVENT_REC_YEAR_OP2_DAY_SELECT, invalues[1].trim());
                            field.selectFromDropDown(EVENT_REC_YEAR_OP2_MONTH_SELECT, invalues[2].trim());
                        } else {
                            Report.logInfo("FAIL : Your monthly data format(2) is not Good");
                        }
                    }
                }
            } else {
                Report.logInfo("Please check the data set as per format");
            }

        }
        field.clearAndSetText(EVENT_REC_STARTDATE_INPUT, startDate);
        field.clearAndSetText(EVENT_REC_ENDDATE_INPUT, endDate);
        Report.logInfo("Finished on filling recurring event form");
    }

    public boolean verifyEventDetails(HashMap<String, String> eventdata, List<HashMap<String, String>> tasksList) {
        boolean result = false;
        openEventCard(eventdata);
        if(verifyEventDetails(eventdata) && verifyTaskDetails(tasksList)) {
            result = true;
        }
        return result;
    }

    public Event getEventDetails() {
        Event event = new Event();
        try {
            event.setType(element.getElement(EVENT_TYPE_VALUE).getText().trim());
            event.setOwner(element.getElement(EVENT_OWNER_VALUE).getText().trim());
            event.setSubject(element.getElement(EVENT_SUBJECT_VALUE).getText().trim());
            event.setScDate(element.getElement(EVENT_DATE_VALUE).getText().trim());
            event.setStatus(element.getElement(EVENT_STATUS_VALUE).getText().trim());
        } catch(RuntimeException e) {
            Report.logInfo("Failed in retriving Event details:" +e.getLocalizedMessage());
        }

        return event;
    }

    private boolean verifyEventDetails(HashMap<String, String> eventdata) {
        boolean result = false;
        String expType = eventdata.get("type").trim();
        String expOwner = eventdata.get("owner").trim();
        String expStatus = eventdata.get("status").trim();
        String expSCDate = eventdata.get("date").trim();
        Event event = getEventDetails();
        if(expType.equalsIgnoreCase(event.getType()) && expOwner.contains(expOwner)
                && expStatus.equalsIgnoreCase(event.getStatus()) && expSCDate.equalsIgnoreCase(event.getScDate())) {
            result = true;
            Report.logInfo("SUCCESS: Event Details Matched");
        } else {
            Report.logInfo("FAIL : Event Details didn't match");
        }
        return result;
    }

    private boolean verifyTaskDetails(List<HashMap<String, String>> tasksList) {
        boolean result = false;
        for(HashMap<String, String> task : tasksList) {
            result = isTaskDisplayed(task);
            if(!result) {
                Report.logInfo("All Tasks are not displayed");
                break;
            }
        }
        return result;
    }

    public boolean verifyHeader(String testdata) {
        boolean result = false;
        //String headerText = element.getText(EVENT_SEC_HEADER);
        //Need to implement
        return result;
    }

    public boolean verifyisRecurringEvent(HashMap<String, String> eventdata) {
        boolean result = false;
        openEventCard(eventdata);
        amtDateUtil.stalePause();
        String recText = element.getText("//span[@class='recurrsDateLblCls']");
        //Good logic need to be implemented for differentiating between daily, weekly, monthly, yearly.
        if(recText.contains("Occurs")) {
            result = true;
        }
        return result;
    }
}
