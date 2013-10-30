package com.gainsight.sfdc.retention.pages;

import java.util.Hashtable;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.excelUtil.TestUtil;
import com.gainsight.sfdc.excelUtil.Xls_Reader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class PlayBooksPage extends BasePage {

    private final String READY_INDICATOR = "//div[@class='ga-addPlaybookIcon btn']";
    private final String DELETE_PLAYBOOK = "//a[@title='Delete playbook']";
    private final String ADD_PLAYBOOK_BUTTON = "//div[@class='ga-addPlaybookIcon btn']";
    private final String ISALERT_PLAYBOOK_CHECK = "ga-alert";
    private final String ISEVENT_PLAYBOOK_CHECK = "ga-event";
    private final String PBNAME_INPUT = "//input[@class='ga-playbookName pbInputVal']";
    private final String PBDES_INPTU  = "//textarea[@class='ga-playbookDes pbInputVal']";
    private final String TASK_SUB_INPUT = "//input[@class='Subject__cInputCls taskParamControlDataInput']";
    private final String TASK_RELDATECOUNT_INPUT = "//input[@class='Date__cInputCls taskParamControlDataInput']";
    private final String TASK_PRIORITY_SELECT = "//select[@class='Priority__cInputCls taskParamControlDataInput']";
    private final String TASK_STATUS_SELECT = "//select[@class='Status__cInputCls taskParamControlDataInput']";
    private final String CREATE_BUTTON = "//input[@class='ga-btnPrimary ga-savePlaybook' and @type='button' and @value='Create']";
    private final String CANCEL_BUTTON = "//input[@class='ga-btnSecondary ga-cancelPlaybook' and @type='button' and @value ='Cancel']";
    private final String UPDATE_BUTTON = "//input[@class='ga-btnPrimary ga-savePlaybook' and @value='Update']";
    private final String ADD_TASK_BUTTON = "ga-addTaskIconId";
    private final String TASK_SERCH_INPUT = "//input[@class='ga-searchInput ga-searchTasks'";
    private final String TASK_SEARCH_CLEAR_BUTTON = "//input[@class='ga-clearBtn ga-searchTaskClearBtn']";
    private final String PB_EDIT_LINK =  "//a[@class='ga-icn ga-edit ga-editPlaybook']";



    /*
     * Constructor wait for the first element to be displayed in the page.
     */
    public PlayBooksPage() {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    /*
     * Adds playbook from the testdata sent.
     */
    public void addplaybook(Hashtable<String, String> playbookData, Hashtable<String, String> taskData) {
        Report.logInfo("Started Adding the playbook");
        item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(CREATE_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(playbookData);
        fillTaskDetails(taskData);
        item.click(CREATE_BUTTON);
        Report.logInfo("Completed Adding the playbook");
        amtDateUtil.stalePause();
    }

    /*
     * Fills the playbook form.
     */
    public void fillPlaybookDetails(Hashtable<String, String> testdata) {
        Report.logInfo("Started filling the playbook details.");
        if(testdata.get("alert") != null &&  testdata.get("alert").equalsIgnoreCase("true")){
            item.click(ISALERT_PLAYBOOK_CHECK);
        }
        if(testdata.get("event") != null  && testdata.get("event").equalsIgnoreCase("true")) {
            item.click(ISEVENT_PLAYBOOK_CHECK);
        }
        if(testdata.get("playbookname")!=null) {
            field.setTextField(PBNAME_INPUT, testdata.get("playbookname"));
        }
        if(testdata.get("description") != null) {
            field.setTextField(PBDES_INPTU, testdata.get("description"));
        }
        Report.logInfo("Finished filling the playbook details");
    }
    /*
     * Fills the task details.
     */
    public void fillTaskDetails(Hashtable<String, String> testdata) {
        Report.logInfo("Stated filling the task form details.");
        amtDateUtil.stalePause();
        if(testdata.get("subject") != null) {
            field.setTextField(TASK_SUB_INPUT, testdata.get("subject"));
        }
        if(testdata.get("date") != null) {
            field.setTextField(TASK_RELDATECOUNT_INPUT, testdata.get("date"));
        }
        if(testdata.get("priority") != null) {
            element.selectFromDropDown(TASK_PRIORITY_SELECT, testdata.get("priority"));
        }
        if(testdata.get("status") !=null) {
            element.selectFromDropDown(TASK_STATUS_SELECT, testdata.get("status"));
        }
        Report.logInfo("Fininshed filling the task form details.");
    }

    /*
     * Checks weather playbook exists in the left tree structure.
     */
    public boolean isplaybookpresent(String playbookname) {
        Report.logInfo("Started Checking for the playbook present :" +playbookname);
        boolean result = false;
        //result = field.isElementPresent("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        try {
            WebElement ele = element.getElement("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
            if(ele!=null) {
                if(ele.isDisplayed()) {
                    result =true;
                }
            }
        } catch(RuntimeException e) {
            result = false;
        }
        Report.logInfo("Finished Checking for playbook present & returning result :" +result);
        return result;
    }

    /*
     * Adds a task to the playbook.
     */
    public void addTask(Hashtable<String, String> testdata) {
        addTask(testdata, testdata.get("playbookname"));
    }

    /*
     * Adds a task to the playbook.
     */
    public void addTask(Hashtable<String, String> testdata, String playbookname) {
        Report.logInfo("Started adding the task on the playbook :" +playbookname);
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        item.click("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed("//div[@class='ga-content ga-contentDetails']/h4[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
        item.click(ADD_TASK_BUTTON);
        fillTaskDetails(testdata);
        item.click(CREATE_BUTTON);
        wait.waitTillElementDisplayed("//h4[@class='ga-fltl taskHeader' and" +
                "@title='"+testdata.get("subject")+"']", MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
        Report.logInfo("Finished adding task on the playbook :" +playbookname);
    }

    /*
     * Verifies the task present
     */
    public boolean isTaskPresent(Hashtable<String, String> testdata) {
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
            System.out.println("No of Tasks :" +noOfTasksinPlaybook());
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

    /*
     * Verifies the task present based on task subject only.
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

    /*
     * Deletes the task based on task subject.
     */
    public boolean deleteTask(String taskSubject, String playbookname) {
        Report.logInfo("Started deleting the task from playbook :" +playbookname);
        boolean result = false;
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        item.click("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        wait.waitTillElementDisplayed("//div[@class='ga-content ga-contentDetails']/h4[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
        List<WebElement> eleList = element.getAllElement("//h4[@class='ga-fltl taskHeader'and @title='"+taskSubject+"']/following-sibling::span/a[@title='Delete task']");
        if(eleList !=null && eleList.size() > 0) {
            System.out.println("No task found :" +noOfTasksinPlaybook());
            System.out.println("Deleting the First Task Found in the List");
            //item.click("//h4[@class='ga-fltl taskHeader'and @title='"+taskSubject+"']/following-sibling::span/a[@title='Delete task']");
            for(WebElement ele : eleList) {
                if(eleList.get(0).isDisplayed()) {
                    ele.click();
                    break;
                }
            }
            modal.accept();
            result = true;
        }
        Report.logInfo("Finished deleting the task from playbook & returing result : " +result);
        return result;
    }

    /*
     * Deletes the task based on test data of the task i.e. checking for status, priority.
     */
    public boolean deleteTask(Hashtable<String, String> testdata) {
        boolean result = false;
        //Yet to be implemented.
        return result;
    }

    /*
     * Deletes the playbook.
     */
    public boolean deletePlaybook(String playbookname) {
        Report.logInfo("Started Deleting the playbook");
        boolean result = false;
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        WebElement ele = element.getElement("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        if(ele.isDisplayed()) {
            item.click("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
            wait.waitTillElementDisplayed("//div[@class='ga-content ga-contentDetails']/h4[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
            wait.waitTillElementDisplayed(ADD_TASK_BUTTON, MIN_TIME, MAX_TIME);
            wait.waitTillElementDisplayed(DELETE_PLAYBOOK, MIN_TIME, MAX_TIME);
            item.click(DELETE_PLAYBOOK);
            modal.accept();
            amtDateUtil.stalePause();
            result = true;
        }
        Report.logInfo("Finished deleting the playbook & returing the result :" +result);
        return result;

    }

    /*
     * Count no of tasks on the playbook.
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

    /*
     * Verifies that the playbook is displayed under All playbook types.
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

    /*
     * Verifies that the playbook is displayed under Alert playbook type.
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

    /*
     * Verifies that the playbook is displayed under Event playbook type.
     */
    public boolean isEventPlaybook(String playbookname) {
        Report.logInfo("Strarted verifying the playbook "+playbookname+" is Event type of playbook");
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
