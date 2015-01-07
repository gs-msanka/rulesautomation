package com.gainsight.sfdc.workflow.pages;

import java.util.List;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.workflow.pojos.Playbook;
import com.gainsight.sfdc.workflow.pojos.PlaybookTask;
import com.gainsight.sfdc.workflow.pojos.Task;

public class WorkflowPlaybooksPage extends WorkflowBasePage {

    private final String READY_INDICATOR            = "//input[@class='gs-btn btn-add']";
    private final String ADD_PLAYBOOK_BUTTON        = "//div[@class='add-playbtn-ctn']/input[@class='gs-btn btn-add']";
    private final String SAVE_PLAYBOOK_BUTTON		= "//input[contains(@class, 'btn-save-playbook') and @value='Save']";
    private final String PLAYBOOK_NAME_INPUT		= "//input[@class='form-control pb-subject']";
    private final String PLAYBOOK_COMMENTS_INPUT	= "//textarea[@class='form-control  pb-description']";
    private final String PB_ADD_TASK_BUTTON			= "//input[contains(@class,'btn-add-task') and @type='button']";
    private final String TASK_OWNER_INPUT           = "//div[contains(@class, 'task-owner')]/descendant::input[@class='search_input ui-autocomplete-input']";
    private final String TASK_SUBJECT_INPUT		    = "//input[contains(@class, 'Subject__cInputCls taskParamControlDataInput')]";
    private final String TASK_DATE_INPUT	        = "//input[contains(@class, 'Date__cInputCls taskParamControlDataInput')]";
    private final String TASK_PRIORITY_INPUT        = "//select[contains(@class, 'Priority__cInputCls')]/following-sibling::button";
    private final String TASK_STATUS_INPUT          = "//select[contains(@class, 'Status__cInputCls')]/following-sibling::button";
    private final String TASK_SAVE_BUTTON           = "//input[contains(@class, 'btn-save-task') and @value='Save']";

    private final String PLAYBOOK_DUPLICATE         = "//ul[contains(@class, 'playbook-tools')]//a[contains(@class, 'clone')]";
    private final String PLAYBOOK_EDIT              = "//ul[contains(@class, 'playbook-tools')]//a[contains(@class, 'edit')]";
    private final String PLAYBOOK_DELETE            = "//ul[contains(@class, 'playbook-tools')]//a[contains(@class, 'delete')]";
    private final String PLAYBOOK_DUPLICATE_NAME    = "input1";
    private final String POPUP_YES_BUTTON           = "//input[@type='button' and @data-action='Yes']";
    private final String POPUP_SAVE_BUTTON          = "//input[@type='button' and @data-action='Save']";
    private final String POPUP_CANCEL_BUTTON        = "//input[@type='button' and @data-action='Cancel']";
    private final String POPUP_DELETE_BUTTON        = "//input[@type='button' and @data-action='Delete']";

    private final String PLAYBOOK_SEARCH_INPUT      = "//div[@class='playbook-search-ctn']/input[contains(@class, 'global-search')]";
    private final String TASK_SEARCH_INPUT          = "//input[contains(@class, 'search-playbooks-tasks')]";
    private final String ALL_BLOCK = "//div[@class='playbook-type']/h2[text()='All']";
    private final String PLAYBOOK_ERROR_MSG_BLOCK = "//div[contains(@class, 'playbook-error-message')]";
    private final String TASK_ERROR_MSG_BLOCK = "//div[contains(@class, 'task-error-message')]";
    private final String NO_PLAYBOOK_MSG = "//div[@class='no-playbooxs-ctn']/p";


    public WorkflowPlaybooksPage() {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }


    public WorkflowPlaybooksPage addPlaybook(Playbook pb) {
        item.click(ADD_PLAYBOOK_BUTTON);
        fillPlaybookDetails(pb);
        item.click(SAVE_PLAYBOOK_BUTTON);
        Timer.sleep(3);
        wait.waitTillElementDisplayed(ALL_BLOCK, MIN_TIME, MAX_TIME);
        return this;
    }
    
    public WorkflowPlaybooksPage clickOnSearchOutput(Playbook pb){
    	return this;
    }
    public void fillPlaybookDetails(Playbook pb) {
        wait.waitTillElementDisplayed(PLAYBOOK_NAME_INPUT, MIN_TIME, MAX_TIME);
        if(pb.getType() != null && pb.getType() != "") {
            item.click("//input[@type='radio' and @value='"+pb.getType()+"']");
        }
        if(pb.getName() != null) {
            field.clearAndSetText(PLAYBOOK_NAME_INPUT, pb.getName());
        }
        field.clearAndSetText(PLAYBOOK_COMMENTS_INPUT, pb.getComments());
    }

    public WorkflowPlaybooksPage addTask(Task task) {
        item.click(PB_ADD_TASK_BUTTON);
        fillTaskDetails(task);
        item.click(TASK_SAVE_BUTTON);
        Timer.sleep(4);
        return this;
    }

    private void fillTaskDetails(Task task) {
        wait.waitTillElementDisplayed(TASK_SAVE_BUTTON, MIN_TIME, MAX_TIME);
        if(task.getAssignee() != null && task.getAssignee() != "") {
        	           selectTaskOwner(task.getAssignee());
                }
                if(task.getSubject() != null) {
                    field.clearAndSetText(TASK_SUBJECT_INPUT, task.getSubject());
                }
                if(task.getDate() != null) {
                    field.clearAndSetText(TASK_DATE_INPUT, task.getDate());
                }
                if(task.getPriority() != null) {
                            item.click(TASK_PRIORITY_INPUT);
                            selectValueInDropDown(task.getPriority());
                        }
                        if(task.getStatus() != null) {
                            item.click(TASK_STATUS_INPUT);
                            selectValueInDropDown(task.getStatus());
                        }
    }


    public void selectTaskOwner(String owner) {
        Log.info("Selecting Task Owner : " + owner);
        Timer.sleep(3);
        boolean selected = false;
        for(int i=0; i< 3; i++) {
            item.clearAndSetText(TASK_OWNER_INPUT, owner);
            driver.findElement(By.xpath(TASK_OWNER_INPUT)).sendKeys(Keys.ENTER);
            for(WebElement ele : element.getAllElement("//li[@class='ui-menu-item']/a[contains(text(), '"+owner+"')]")) {
                if(ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                    Log.info("Selected Task Owner Successfully: " +owner);
                    return;
                }
            }
            Timer.sleep(2);
        }
        if(!selected) {
            throw new RuntimeException("Unable to select owner");
        }
    }

    public boolean isPlaybookDisplayed(Playbook pb) {
        env.setTimeout(2);
        boolean result = isElementPresentAndDisplay(By.xpath(getPlaybookXPath(pb)));
        env.setTimeout(30);
        return result;
    }

    private String getPlaybookXPath(Playbook pb) {
        String xpath =  "//div[@class='playbook-type']/h2[text()='"+pb.getType()+"']/following-sibling::ul/li[contains(text(), '"+pb.getName()+"')]";
        Log.info("Playbook Path : " +xpath);
        return xpath;
    }

    public WorkflowPlaybooksPage expandPlaybookView(Playbook pb) {
        item.click(getPlaybookXPath(pb));
        wait.waitTillElementDisplayed("//div[@class='playbooks-data']/h3[contains(text(), '"+pb.getName()+"')]", MIN_TIME, MAX_TIME);
        Timer.sleep(2);
        return this;
    }

    public WorkflowPlaybooksPage editPlaybook(Playbook pb, Playbook newPB) {
        expandPlaybookView(pb);
        item.click(PLAYBOOK_EDIT);
        fillPlaybookDetails(newPB);
        item.click(SAVE_PLAYBOOK_BUTTON);
        Timer.sleep(4);
        wait.waitTillElementDisplayed(ALL_BLOCK, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPlaybooksPage duplicatePlaybook(String pName) {
        item.click(PLAYBOOK_DUPLICATE);
        wait.waitTillElementDisplayed(PLAYBOOK_DUPLICATE_NAME, MIN_TIME, MAX_TIME);
        field.clearAndSetText(PLAYBOOK_DUPLICATE_NAME, pName);
        item.click(POPUP_SAVE_BUTTON);
        Timer.sleep(5);
        wait.waitTillElementDisplayed(ALL_BLOCK, MIN_TIME, MAX_TIME);
        return this;
    }

    public boolean isTaskDisplayed(Task task) {
        String xPath = "//h4[contains(text(), '"+task.getSubject()+"')]/ancestor::li[contains(@class,  'ga-badge ga-typeSupport playbook-tasks')]";
        List<WebElement> taskList = element.getAllElement(xPath);
        if(taskList.size() ==0) {
            Log.info("No Tasks found with subject : "+task.getSubject());
            return false;
        }
        String status, priority, date = null;
        for(WebElement tEle : taskList) {
            status = tEle.findElement(By.xpath(getTaskPropXpath("Status"))).getText().trim();
            priority = tEle.findElement(By.xpath(getTaskPropXpath("Priority"))).getText().trim();
            if(task.getDate()!=null) {
                date = tEle.findElement(By.xpath(getTaskPropXpath("Date"))).getText().trim();
            }
            Log.info("Expected Task Properties - Status : "+task.getStatus() +", -- priority : "+task.getPriority() +", -- date : "+task.getDate());
            Log.info("Actual Task Properties - Status : "+status +", -- priority : "+priority +", -- date : "+date);
            if(task.getStatus().equalsIgnoreCase(status) && task.getPriority().equalsIgnoreCase(priority)
                    && (task.getDate() != null ? date.contains(task.getDate()) :  true )) {
                Log.info("Task Found");
                return true;
            }
        }
        Log.info("Task not found");
        return false;
    }


    private String getTaskPropXpath(String prop) {
        String s = ".//div[@class='tasks-label' and contains(text(), '"+prop+"')]/following-sibling::div[contains(@class,'tasks-value')]";
        return s;
    }

    public WorkflowPlaybooksPage deletePlaybook(Playbook pb) {
        expandPlaybookView(pb);
        item.click(PLAYBOOK_DELETE);
        item.click(POPUP_DELETE_BUTTON);
        return this;
    }

    public WorkflowPlaybooksPage editTask(Task task, Task newTask) {
        String s = "//div[@class='header']/h4[contains(text(),'"+task.getSubject()+"')]/following-sibling::ul[contains(@class,'playbook-tools')]/descendant::a[contains(@class, 'edit') and @title='Edit task']";
        item.click(s);
        fillTaskDetails(newTask);
        item.click(TASK_SAVE_BUTTON);
        Timer.sleep(2);
        return this;
    }

    public WorkflowPlaybooksPage deleteTask(Task task) {
        String s = "//div[@class='header']/h4[contains(text(),'"+task.getSubject()+"')]/following-sibling::ul[contains(@class,'playbook-tools')]/descendant::a[contains(@class, 'delete') and @title='Delete task']";
        item.click(s);
        item.click(POPUP_YES_BUTTON);
        Timer.sleep(2);
        return this;
    }

    private String getTaskXpath(Task task)  {
        String s = "//h4[contains(text(), '"+task.getSubject()+"')]" +
                "/ancestor::div[contains(@class, 'playbook-tasks')]/ul[@class='playbook-tasks-list']" +
                "/li/div[@class='tasks-label' and contains(text(), 'Status')]" +
                "/following-sibling::div[@class='tasks-value' and contains(text(), '"+task.getStatus()+"')]" +
                "/ancestor::ul/li/div[@class='tasks-label' and contains(text(), 'Priority')]" +
                "/following-sibling::div[contains(@class, 'tasks-value') and contains(text(), '"+task.getPriority()+"')]" +
                "/ancestor::div[contains(@class, 'playbook-tasks')]";
        Log.info("Task Xpath : " +s);
        return s;
    }

    public WorkflowPlaybooksPage searchPlaybooks(String name) {
        field.clearAndSetText(PLAYBOOK_SEARCH_INPUT, name);
        driver.findElement(By.xpath(PLAYBOOK_SEARCH_INPUT)).sendKeys(Keys.ENTER);
        Timer.sleep(2);
        return this;
    }

    public WorkflowPlaybooksPage searchTasks(String task) {
        field.clearAndSetText(TASK_SEARCH_INPUT, task);
        driver.findElement(By.xpath(TASK_SEARCH_INPUT)).sendKeys(Keys.ENTER);
        Timer.sleep(2);
        return this;
    }
    public boolean noPlaybooksMessage() {
        String actualText = element.getText(NO_PLAYBOOK_MSG).trim();
        String expectedText = "No Playbooks are defined. Create your first one now.";
        Log.info("Actual : " +actualText);
        Log.info("Expected : " +expectedText);
        return expectedText.equalsIgnoreCase(actualText);
    }

    public boolean isPlaybookErrorMsgDisplayed(String message) {
        Log.info("Checking playbook error messages are displayed.");
        String xPath = PLAYBOOK_ERROR_MSG_BLOCK+"/div[text()='"+message+"']";
        return isElementPresentAndDisplay(By.xpath(xPath));
    }

    public boolean isTaskErrorMsgDisplayed(String message) {
        Log.info("Checking playbook error messages are displayed.");
        String xPath = TASK_ERROR_MSG_BLOCK+"/div[text()='"+message+"']";
        return isElementPresentAndDisplay(By.xpath(xPath));
    }
}


