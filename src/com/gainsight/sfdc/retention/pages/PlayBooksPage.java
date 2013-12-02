package com.gainsight.sfdc.retention.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class PlayBooksPage extends BasePage {

    private final String READY_INDICATOR            = "//div[@class='ga-addPlaybookIcon btn']";
    private final String DELETE_PLAYBOOK            = "//a[@title='Delete playbook']";
    private final String ADD_PLAYBOOK_BUTTON        = "//div[@class='ga-addPlaybookIcon btn']";
    private final String ISALERT_PLAYBOOK_CHECK     = "ga-alert";
    private final String ISEVENT_PLAYBOOK_CHECK     = "ga-event";
    private final String PBNAME_INPUT               = "//input[@class='ga-playbookName pbInputVal']";
    private final String PBDES_INPTU                = "//textarea[@class='ga-playbookDes pbInputVal']";
    private final String TASK_SUB_INPUT             = "//input[@class='Subject__cInputCls taskParamControlDataInput']";
    private final String TASK_RELDATECOUNT_INPUT    = "//input[@class='Date__cInputCls taskParamControlDataInput']";
    private final String TASK_PRIORITY_SELECT       = "//select[@class='Priority__cInputCls taskParamControlDataInput']";
    private final String TASK_STATUS_SELECT         = "//select[@class='Status__cInputCls taskParamControlDataInput']";
    private final String CREATE_BUTTON              = "//input[@class='ga-btnPrimary ga-savePlaybook' and @type='button' and @value='Create']";
    private final String CANCEL_BUTTON              = "//input[@class='ga-btnSecondary ga-cancelPlaybook' and @type='button' and @value ='Cancel']";
    private final String UPDATE_BUTTON              = "//input[@class='ga-btnPrimary ga-savePlaybook' and @value='Update']";
    private final String ADD_TASK_BUTTON            = "ga-addTaskIconId";
    private final String TASK_SERCH_INPUT           = "//input[@class='ga-searchInput ga-searchTasks'";
    private final String TASK_SEARCH_CLEAR_BUTTON   = "//input[@class='ga-clearBtn ga-searchTaskClearBtn']";
    private final String PB_EDIT_LINK               =  "//a[@class='ga-icn ga-edit ga-editPlaybook']";

    /**
     * Constructor of the page, Waits for the ready indicator to be present on the page.
     */
    public PlayBooksPage() {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    /**
     * Adds Playbook for the user on supplied paramters.  
     * <pre>
     * {@code
     * 	1. Clicks on "Add Playbook" button.
     * 	2. Fills the Playbook details.
     * 	3. Fills the Event details.	
     * 	4. Clicks on create button.
     * }
     * @param playbookData - a HashMap where all the data needed a Playbook exists.
     * @param taskData - a HashMap where  all the data needed a Task exists.
     * @return void
     * </pre>
     */
    public void addplaybook(HashMap<String, String> playbookData, HashMap<String, String> taskData) {
        item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(CREATE_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(playbookData);
        fillTaskDetails(taskData);
        item.click(CREATE_BUTTON);
        amtDateUtil.stalePause();
    }

    /**
     * Fills the Playbook form.
     * @param testdata - a HashMap where all the data needed a Playbook exists
     * @return void
     */
    public void fillPlaybookDetails(HashMap<String, String> testdata) {
        Report.logInfo("Started filling the playbook details.");
        if(testdata.get("alert") != null &&  testdata.get("alert").equalsIgnoreCase("true")){
            String isChecked = element.getElement(ISALERT_PLAYBOOK_CHECK).getAttribute("checked");
            if(isChecked == null) {
                item.click(ISALERT_PLAYBOOK_CHECK);
            }
        } else if(testdata.get("alert").equalsIgnoreCase("false")) {
            String isChecked = element.getElement(ISALERT_PLAYBOOK_CHECK).getAttribute("checked");
            if(isChecked != null) {
                item.click(ISALERT_PLAYBOOK_CHECK);
            }
        }
        if(testdata.get("event") != null  && testdata.get("event").equalsIgnoreCase("true")) {
            String isChecked = element.getElement(ISEVENT_PLAYBOOK_CHECK).getAttribute("checked");
            if(isChecked == null) {
                item.click(ISEVENT_PLAYBOOK_CHECK);
            }
        } else if(testdata.get("event") != null  && testdata.get("event").equalsIgnoreCase("false")) {
            String isChecked = element.getElement(ISEVENT_PLAYBOOK_CHECK).getAttribute("checked");
            if(isChecked != null) {
                item.click(ISEVENT_PLAYBOOK_CHECK);
            }
        }
        if(testdata.get("playbookname")!=null) {
            field.clearAndSetText(PBNAME_INPUT, testdata.get("playbookname"));
        }
        if(testdata.get("description") != null) {
            field.clearAndSetText(PBDES_INPTU, testdata.get("description"));
        }
        Report.logInfo("Finished filling the playbook details");
    }

    /**
     * Checks for the playbook name in the tree hierarchy.
     * @param playbookname - Name of the Playbook to search for.
     * @return boolean - true if Playbook found, false Playbook not found. 
     */
    public boolean isplaybookpresent(String playbookname) {
        Report.logInfo("Started Checking for the playbook present :" +playbookname);
        boolean result = false;
        List<WebElement> eleList = element.getAllElement("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        if(eleList != null && eleList.size() >0) {
            result =true;
        }
        Report.logInfo("Finished Checking for playbook present & returning result :" +result);
        return result;
    }

    /**
     * Opens the Playbook by searching the tree hierarchy 
     * @param playbookname - Name of the Playbook to open.
     * @return void
     */
    public void openPlaybook(String playbookname) {
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        item.click("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        wait.waitTillElementDisplayed("//div[@class='ga-content ga-contentDetails']/h4[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
    }

    /**
     * Searches for the Playbook in tree hirerachy & deletes the Playbook
     * @param playbookname - Name of the Playbook to delete.
     * @return void
     */
    public void deletePlaybook(String playbookname) {
        Report.logInfo("Started Deleting the playbook");
        openPlaybook(playbookname);
        wait.waitTillElementDisplayed(DELETE_PLAYBOOK, MIN_TIME, MAX_TIME);
        item.click(DELETE_PLAYBOOK);
        modal.accept();
        amtDateUtil.stalePause();
        Report.logInfo("Finished deleting the playbook");
    }

    /**
     * Edits the existing Playbook.
     * @param oldplaybookname - Name of the Playbook to edit.
     * @param testdata - Playbook data.
     */
    public void editPlaybook(String oldplaybookname, HashMap<String, String> testdata) {
        Report.logInfo("Started editing the Playbook");
        openPlaybook(oldplaybookname);
        wait.waitTillElementDisplayed(PB_EDIT_LINK, MIN_TIME, MAX_TIME);
        item.click(PB_EDIT_LINK);
        wait.waitTillElementDisplayed(UPDATE_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(testdata);
        item.click(UPDATE_BUTTON);
        wait.waitTillElementNotDisplayed(UPDATE_BUTTON, MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
        Report.logInfo("Finished editing the playbook.");
    }

    /**
     * Fills the Task form details.
     * @param testdata - a HashMap comprising of tasks data.
     * @return void
     */
    public void fillTaskDetails(HashMap<String, String> testdata) {
        Report.logInfo("Stated filling the task form details.");
        if(testdata.get("subject") != null) {
            field.clearAndSetText(TASK_SUB_INPUT, testdata.get("subject"));
        }
        if(testdata.get("date") != null) {
            field.clearAndSetText(TASK_RELDATECOUNT_INPUT, testdata.get("date"));
        }
        if(testdata.get("priority") != null) {
            element.selectFromDropDown(TASK_PRIORITY_SELECT, testdata.get("priority"));
        }
        if(testdata.get("status") !=null) {
            element.selectFromDropDown(TASK_STATUS_SELECT, testdata.get("status"));
        }
        Report.logInfo("Fininshed filling the task form details.");
    }

    /**
     * Checks for the task in the Playbook
     * @param testdata
     * @return boolean  - true - task present, false - if task not present.
     */
    public boolean isTaskPresent(HashMap<String, String> testdata) {
        Report.logInfo("Verifying the task is dispalyed");
        boolean result = false;
        List<WebElement> eleList = null;
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+testdata.get("playbookname")+"']", MIN_TIME, MAX_TIME);
        item.click("//td[@class='standartTreeRow']/span[text()='"+testdata.get("playbookname")+"']");
        wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
        String eleString  = "//h4[@class='ga-fltl taskHeader' and" +
                "@title='"+testdata.get("subject")+"']"+
                "/parent::div/following-sibling::"+
                "div/div[@class='ga-value' and text()='"+testdata.get("status")+"']"+
                "/parent::div/following-sibling::"+
                "div/div/div[@class='ga-value' and text()='"+testdata.get("priority")+"']";
        System.out.println("Element : " +eleString);
        eleList = element.getAllElement(eleString);
        if(eleList != null && eleList.size() > 0) {
            System.out.println("No of Tasks :" +eleList.size());
            for(WebElement ele : eleList) {
                if(ele.isDisplayed()) {
                    result = true;
                    break;
                }
            }
        }
        Report.logInfo("Finished verifying the task displayed & returning result :" +result);
        return result;
    }

    public boolean isTaskDisplayed(HashMap<String, String> testdata) {
        boolean result = false;
        WebElement ele = getTask(testdata);
        if(ele != null) {
            result = true;
        }
        return result;
    }

    /**
     * Adds a Task to the Playbook.
     * @param testdata
     * @return void
     */
    public void addTask(HashMap<String, String> testdata) {
        wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
        item.click(ADD_TASK_BUTTON);
        wait.waitTillElementDisplayed(CREATE_BUTTON, MIN_TIME, MAX_TIME);
        fillTaskDetails(testdata);
        item.click(CREATE_BUTTON);
        wait.waitTillElementDisplayed("//h4[@class='ga-fltl taskHeader' and" +
                "@title='"+testdata.get("subject")+"']", MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
    }

    /**
     * Adds a Task to the Playbook Specified.
     * @param testdata - a HashMap comprising of Task data 
     * @param playbookname - Name of the Playbook on which task should be added. 
     */
    public void addTask(HashMap<String, String> testdata, String playbookname) {
        Report.logInfo("Started adding the task on the playbook :" +playbookname);
        openPlaybook(playbookname);
        addTask(testdata);
        Report.logInfo("Finished adding task on the playbook :" +playbookname);
    }

    /**
     * Verify weather the task is present or not.
     * @param taskSubject
     * @return true - task present, false - task not present
     */
    public boolean isTaskPresent(String taskSubject) {
        Report.logInfo("Started verifying the task present based on subject : " +taskSubject);
        boolean result = false;
        try{
            WebElement ele = element.getElement("//h4[@class='ga-fltl taskHeader' and" +
                    "@title='"+taskSubject+"']");
            if(ele!= null) {
                if(ele.isDisplayed()) {
                    result = true;
                }
            }
        } catch(RuntimeException e) {
            result = false;
        }
        Report.logInfo("Finished verifying the task present and returing with result: " +result );
        return result;
    }

    /**
     * Deletes Task from the Playbook.
     * @param testdata - a HashMap comprising of Task data.
     * @param playbookname - a Playbook name on which the task should be deleted.
     * @return void
     */
    public void deleteTask(HashMap<String, String> testdata, String playbookname) {
        Report.logInfo("Started deleting the task from playbook :" +playbookname);
        openPlaybook(playbookname);
        deleteTask(testdata);
        Report.logInfo("Finished deleting the task from playbook");
    }

    /**
     * Deletes the Task.
     * @param testdata - a HashMap comprising of Task data.
     * @return void
     */
    public void deleteTask(HashMap<String, String> testdata) {
        WebElement task = getTask(testdata);
        WebElement taskDeleteIcon = task.findElement(By.cssSelector("a.ga-icn.ga-ignore.ga-ignoreTaskItem"));
        taskDeleteIcon.click();
        modal.accept();
        amtDateUtil.stalePause();
    }

    /**
     * Edits the Task of the Playbook specified.
     * @param oldtaskdata - a HashMap comprising Task data.
     * @param newtaskdata - a HashMap comprising updated task data.
     * @param playbookname - Playbook Name.
     * @return void
     */
    public void editTask(HashMap<String, String> oldtaskdata, HashMap<String, String> newtaskdata, String playbookname) {
        openPlaybook(playbookname);
        editTask(oldtaskdata, newtaskdata);
    }

    /**
     * Edits the Task.
     * @param oldtaskdata - a HashMap comprsing of tasks data.
     * @param newtaskdata - a HashMap comprising updated task data.
     * @return void
     */
    public void editTask(HashMap<String, String> oldtaskdata, HashMap<String, String> newtaskdata) {
        Report.logInfo("Started editing the Playbook");
        WebElement task = getTask(oldtaskdata);
        WebElement taskEditIcon = task.findElement(By.cssSelector("a.ga-icn.ga-edit.ga-editTaskItem"));
        taskEditIcon.click();
        wait.waitTillElementDisplayed(UPDATE_BUTTON, MIN_TIME, MAX_TIME);
        fillTaskDetails(newtaskdata);
        item.click(UPDATE_BUTTON);
        wait.waitTillElementNotDisplayed(UPDATE_BUTTON, MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
        Report.logInfo("Update the task");
    }
    /**
     * Searches on all tasks with the data sent & returns the Task card a WebElement.
     * @param testdata - a HashMap comprising of Task data.
     * @return WebElement - Task Card.
     */
    public WebElement getTask(HashMap<String, String> testdata) {
        String expsubject = testdata.get("subject");
        String expstatus = testdata.get("status");
        String exppriority = testdata.get("priority");
        String expdate = testdata.get("date");
        WebElement task = null;
        amtDateUtil.stalePause();
        List<WebElement> eleList = element.getAllElement("//div[@class='ga-badge ga-typeSupport']");
        System.out.println(eleList.size());
        if(eleList != null && eleList.size() > 0) {
            for(WebElement ele : eleList) {
                if(ele.isDisplayed()) {
                    String subject = ele.findElement(By.cssSelector("h4.ga-fltl.taskHeader")).getText().trim();
                    String status = ele.findElement(By.cssSelector("div.ga-value")).getText().trim();
                    String priorityanddate = ele.findElement(By.className("ga-data")).getText().trim();
                    if(subject.equalsIgnoreCase(expsubject) && status.equalsIgnoreCase(expstatus)
                            && priorityanddate.contains(exppriority) && priorityanddate.contains(expdate)) {
                        task = ele;
                        Report.logInfo("SUCCESS: Found the task");
                        break;
                    }
                }
            }
            if(task ==null) {
                Report.logInfo("FAIL: Task is not found in playbook");
            }
        } else {
            Report.logInfo("FAIL: No Tasks where found for playbook :");
        }
        return task;
    }

    /**
     * Counts the number of Tasks in the Playbook.
     * @return int - count of tasks.
     */
    public int noOfTasksinPlaybook() {
        Report.logInfo("Started counting the number of tasks in a playbook.");
        int count = 0;
        List<WebElement> eleList = element.getAllElement("//div[@class='ga-badge ga-typeSupport' and contains(@id, 'taskId')]");
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                count++;
            }
        }
        Report.logInfo("Finished counting the number of tasks in the playbook & returing result :" +count);
        return count;
    }

    /**
     * Searches weather the Playbook is of ALL type. 
     * @param playbookname - Playbook Name.
     * @return true - All Playbook, else false. 
     */
    public boolean isAllPlaybook(String playbookname) {
        Report.logInfo("Strarted verifying the playbook "+playbookname+" is All type of playbook");
        boolean result = false;
        try {
            String eleString = "//span[text()='Alert']/ancestor::tr/following-sibling"+
                    "::tr/descendant::td[@class='standartTreeRow']" +
                    "/span[text()='"+playbookname+"']";
            System.out.println("Play book Path = " +eleString);
            wait.waitTillElementDisplayed(eleString, MIN_TIME, MAX_TIME);
            WebElement ele = element.getElement(eleString);

            if(ele !=null) {
                if(ele.isDisplayed()) {
                    result = true;
                }
            }
        } catch(RuntimeException e) {
            result =false;
        }
        Report.logInfo("Finished verifying playbook & resurting result :" +result);
        return result;
    }

    /**
     * Searches weather the Playbook is of Alert type. 
     * @param playbookname - Playbook Name.
     * @return true - Alert Playbook, else false. 
     */
    public boolean isAlertPlaybook(String playbookname) {
        Report.logInfo("Strarted verifying the playbook "+playbookname+" is Alert type of playbook");
        boolean result = false;
        try {
            String eleString = "//span[text()='Alert']/ancestor::tr/following-sibling"+
                    "::tr/descendant::td[@class='standartTreeRow']" +
                    "/span[text()='"+playbookname+"']";
            System.out.println("Play book Path = " +eleString);
            wait.waitTillElementDisplayed(eleString, MIN_TIME, MAX_TIME);
            WebElement ele = element.getElement(eleString);
            if(ele !=null) {
                if(ele.isDisplayed()) {
                    result = true;
                }
            }
        } catch(RuntimeException e) {
            result =false;
        }
        Report.logInfo("Finished verifying playbook & resurting result :" +result);
        return result;
    }

    /**
     * Searches weather the Playbook is of Event type. 
     * @param playbookname - Playbook Name.
     * @return true - Event Playbook, else false. 
     */
    public boolean isEventPlaybook(String playbookname) {
        Report.logInfo("Started verifying the playbook "+playbookname+" is Event type of playbook");
        boolean result = false;
        try {
            String eleString = "//span[text()='Alert']/ancestor::tr/following-sibling"+
                    "::tr/descendant::td[@class='standartTreeRow']" +
                    "/span[text()='"+playbookname+"']";
            System.out.println("Play book Path = " +eleString);
            wait.waitTillElementDisplayed(eleString, MIN_TIME, MAX_TIME);
            WebElement ele = element.getElement(eleString);
            if(ele !=null) {
                if(ele.isDisplayed()) {
                    result = true;
                }
            }
        } catch(RuntimeException e) {
            result =false;
        }
        Report.logInfo("Finished verifying playbook & resurting result :" +result);
        return result;
    }
}
