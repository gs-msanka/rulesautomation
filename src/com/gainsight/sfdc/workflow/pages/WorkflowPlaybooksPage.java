package com.gainsight.sfdc.workflow.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

import java.util.HashMap;

public class WorkflowPlaybooksPage extends BasePage {

	private final String READY_INDICATOR            = "//input[@class='gs-btn btn-add']";  
	private final String ADD_PLAYBOOK_BUTTON        = "//div[@class='add-playbtn-ctn']/input[@class='gs-btn btn-add']";	//Add Playbook on the left hand side
	private final String ADD_PLAYBOOK_NOPB_BUTTON   = "//div[@class='no-playbooxs-ctn']/input[@class='gs-btn btn-add']";	//Add Playbook in the middle of playbook page
	private final String SAVE_PLAYBOOK_BUTTON		= "//input[@class='gs-btn btn-save btn-save-playbook']";	//Save Playbook 
	private final String CANCEL_PLAYBOOK_BUTTON		= "//input[@class='gs-btn btn-cancel btn-cancel-playbook']";	//Cancel Playbook
	private final String PLAYBOOK_NAME_INPUT		= "//input[@class='form-control pb-subject']";	//Playbook Name
	private final String PLAYBOOK_COMMENTS_INPUT	= "//textarea[@class='form-control  pb-description']";	//Playbook Comments
	private final String RISK_PBTYPE_RADIO 			= "//input[@type='radio' and @value='Risk' and @name='playbook-type']";	//Risk Radio button
	private final String OPPORTUNITY_PBTYPE_RADIO 	= "//input[@type='radio' and @value='Opportunity' and @name='playbook-type']";	//Opportunity Radio button
	private final String EVENT_PBTYPE_RADIO 		= "//input[@type='radio' and @value='Event' and @name='playbook-type']";	//Event Radio button
	private final String ALL_PBTYPE_RADIO 			= "//input[@type='radio' and @value='All' and @name='playbook-type']";	//All Radio button
	private final String DUPLICATE_PLAYBOOK			= "//a[@title='Duplicate playbook']";	//Duplicate Playbook Icon
	private final String DUPLICATE_PLAYBOOK_NAMe	= "//input[@class='layout_popup_input']";		//Duplicate Playbook Name
	private final String SAVE_DUPLICATE_PLAYBOOK	= "//input[@class='gs-btn btn-save btn_save saveSummary']";		//Save Duplicate
	private final String EDIT_PLAYBOOK				= "//a[@title='Edit playbook']";	//Edit Playbook
	private final String DELETE_PLAYBOOK			= "//a[@title='Delete playbook']";	//Delete Playbook
	private final String SEARCH_PLAYBOOK_INPUT		= "//input[@class='form-control global-search']";	//Search Playbook 
	private final String SEARCH_TASK_INPUT			= "//input[@class='form-control global-search col-md-12 search-playbooks-tasks']";	//Search Task
	private final String ADD_PBTASK_BUTTON			= "//input[@class='gs-btn btn-add  btn-add-task']";	//Add Task
	private final String EDIT_PBTASK_BUTTON			= "//a[@title='Edit task']";		//Edit Task
	private final String DELETE_PBTASK_BUTTON		= "//a[@title='Delete task']";	//Delete Task
	private final String SUBJECT_PBTASK_INPUT		= "//input[@class='form-control Subject__cInputCls taskParamControlDataInput']";		//Task Subject
	private final String RELDATECOUNT_PBTASK_INPUT	= "//input[@class='form-control width40 Date__cInputCls taskParamControlDataInput']";	//Task Date
	private final String COMMENTS_PBTASK_INPUT		= "//textarea[@class='form-control Description__cInputCls taskParamControlDataInput']";	//Task Comments/Description
	private final String STATUS_PBTASK_BUTTON 		="//label[@class='Status__cClass taskFieldLbl ga-flabel col-sm-2 control-label']/parent::div/div/button/span";		//Task Status
	private final String STATUS_PBTASK_BUTTON_OPTION="//span[contains(text(),'%s')]";
	private final String PRIORITY_PBTASK_BUTTON		="//label[@class='Priority__cClass taskFieldLbl ga-flabel col-sm-2 control-label']/parent::div/div/button/span";	//Task Priority	
	private final String PRIORITY_PBTASK_BUTTON_OPTION="//span[contains(text(),'%s')]";
	private final String SAVE_PBTASK_BUTTON			="//input[@class='gs-btn btn-save btn-save-task']";		//Save Task
	private final String CANCEL_PBTASK_BUTTON		="//input[@class='gs-btn btn-cancel btn-cancel-task']";		//Cancel Task
	private final String SELECT_PLAYBOOK_PBLIST		="//li[contains(text(),'')]";  //xpath to find the Playbook element from the playbooks list - Provide the playbook name
	
	
	
	/**
     * Constructor of the page, Waits for the ready indicator to be present on the page.
     */
    public WorkflowPlaybooksPage() {
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
    	wait.waitTillElementDisplayed(ADD_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
        item.click(ADD_PLAYBOOK_BUTTON);
        Report.logInfo("Clicked on Add Playbook button.");
        wait.waitTillElementDisplayed(SAVE_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(playbookData);
        fillTaskDetails(taskData);
        amtDateUtil.stalePause();
    }
    
    /**
     * Checks for the playbook name in the tree hierarchy.
     * @param playbookname - Name of the Playbook to search for.
     * @return boolean - true if Playbook found, false Playbook not found. 
     */
    public boolean isplaybookpresent(String playbookname) {
        Report.logInfo("Started Checking for the playbook present :" +playbookname);
        boolean result = false;
        //List<WebElement> eleList = element.getAllElement("//td[@class='standartTreeRow']/span[text()='"+playbookname+"']");
        result = element.isElementPresent("//li[contains(text(),'"+playbookname+"')]"); 
        Report.logInfo("Finished Checking for playbook present & returning result :" +result);
        return result;
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
//        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+testdata.get("playbookname")+"']", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed("//li[contains(text(),'"+testdata.get("playbookname")+"')]", MIN_TIME, MAX_TIME);
        item.click("//li[contains(text(),'"+testdata.get("playbookname")+"')]");
        wait.waitTillElementDisplayed(ADD_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
        /*String eleString  = "//h4[@class='ga-fltl taskHeader' and" +
                "@title='"+testdata.get("subject")+"']"+
                "/parent::div/following-sibling::"+
                "div/div[@class='ga-value' and text()='"+testdata.get("status")+"']"+
                "/parent::div/following-sibling::"+
                "div/div/div[@class='ga-value' and text()='"+testdata.get("priority")+"']";*/
        String eleString = "//h4[contains(text(),'"+testdata.get("subject") +"')]"
        		+ "/parent::div/following-sibling::ul/li/div[@class='tasks-value' and text()='"+testdata.get("status")+"']"
        		+ "/parent::li/following-sibling::li/div[@class='tasks-value' and text()='"+testdata.get("priority")+"']"
        		+ "/ancestor::div[contains(@id,'taskId')]";
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

    /**
     * Searches weather the Playbook is of ALL type. 
     * @param playbookname - Playbook Name.
     * @return true - All Playbook, else false. 
     */
    public boolean isAllPlaybook(String playbookname) {
        Report.logInfo("Strarted verifying the playbook "+playbookname+" is All type of playbook");
        boolean result = false;
        try {
            /*String eleString = "//span[text()='Alert']/ancestor::tr/following-sibling"+
                    "::tr/descendant::td[@class='standartTreeRow']" +
                    "/span[text()='"+playbookname+"']";*/
        	
        	String eleString = "//h2[text()='Risk']/following-sibling::ul[@class='playbook-Risk']/li[text()='"+playbookname+"']";            
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
     * Fills the Playbook form.
     * @param testdata - a HashMap where all the data needed a Playbook exists
     * @return void
     */
    public void fillPlaybookDetails(HashMap<String, String> testdata) {
       Report.logInfo("Started filling the playbook details.");
       if(testdata.get("pbType")!=null){
    	   Report.logInfo("Adding Playbook type as: "+testdata.get("pbType"));
           item.click("//input[@type='radio' and @value='"+testdata.get("pbType")+"' and @name='playbook-type']");
       }
       if(testdata.get("playbookname")!=null) {
           field.clearAndSetText(PLAYBOOK_NAME_INPUT, testdata.get("playbookname"));
       }
       if(testdata.get("description") != null) {
           field.clearAndSetText(PLAYBOOK_COMMENTS_INPUT, testdata.get("description"));
       }
       item.click(SAVE_PLAYBOOK_BUTTON);	//Calling Save button
    }
    
    /**
     * Fills the Task form details.
     * @param testdata - a HashMap comprising of tasks data.
     * @return void
     */
    public void fillTaskDetails(HashMap<String, String> testdata) {
        Report.logInfo("Started filling the task form details.");
        item.click(ADD_PBTASK_BUTTON);
        if(testdata.get("subject") != null) {
            field.clearAndSetText(SUBJECT_PBTASK_INPUT, testdata.get("subject"));
        }
        if(testdata.get("date") != null) {
            field.clearAndSetText(RELDATECOUNT_PBTASK_INPUT, testdata.get("date"));
        }
        if(testdata.get("priority") != null) {
            //element.selectFromDropDown(TASK_PRIORITY_SELECT, testdata.get("priority"));
        	element.click(PRIORITY_PBTASK_BUTTON);
        	System.out.println("selecting option:"+String.format(PRIORITY_PBTASK_BUTTON_OPTION, testdata.get("priority")));
			driver.findElement(By.xpath(String.format(PRIORITY_PBTASK_BUTTON_OPTION,testdata.get("priority")))).click();
//        	element.selectFromDropDown(PRIORITY_PBTASK_BUTTON, testdata.get("priority"));
        	//element.
            
        }
        if(testdata.get("status") !=null) {
//            element.selectFromDropDown(STATUS_PBTASK_BUTTON, testdata.get("status"));
        	element.click(STATUS_PBTASK_BUTTON);
        	System.out.println("selecting option:"+String.format(STATUS_PBTASK_BUTTON_OPTION, testdata.get("status")));
			driver.findElement(By.xpath(String.format(STATUS_PBTASK_BUTTON_OPTION,testdata.get("status")))).click();
//        	element.selectFromDropDown(STATUS_PBTASK_BUTTON, testdata.get("status"));
        }
        
      //Calling Save button
        item.click(SAVE_PBTASK_BUTTON);
        Report.logInfo("Fininshed filling the task form details.");
    }
    
    /**
     * Opens the Playbook by searching the tree hierarchy 
     * @param playbookname - Name of the Playbook to open.
     * @return void
     */
    public void openPlaybook(String playbookname) {
    	Report.logInfo("Started opening playbook");
    	wait.waitTillElementDisplayed("//li[contains(text(),'"+playbookname+"')]", MIN_TIME, MAX_TIME);
    	item.click("//li[contains(text(),'"+playbookname+"')]");
        wait.waitTillElementDisplayed(ADD_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
//        wait.waitTillElementDisplayed("//div[@class='ga-content ga-contentDetails']/h4[text()='"+playbookname+"']", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed("//div[@class='playbooks-data']/h3[contains(text(),'"+playbookname+"']", MIN_TIME, MAX_TIME);
    }
    
    /**
     * Searches for the Playbook in tree hierarchy & deletes the Playbook
     * @param playbookname - Name of the Playbook to delete.
     * @return void
     */
    public void deletePlaybook(String playbookname) {
        Report.logInfo("Started Deleting the playbook");
        openPlaybook(playbookname);
        wait.waitTillElementDisplayed(DELETE_PLAYBOOK, MIN_TIME, MAX_TIME);
        item.click(DELETE_PLAYBOOK);
        WorkflowBasePage wfb = new WorkflowBasePage();
    	wfb.wf_modal_accept();
      //span[@class='ui-dialog-title' and contains(text(),'Confirm')]
      //input[@value='Yes']
      //input[@class='gs-btn btn-cancel btn_cancel cancelSummary' and @value='Cancel']
        amtDateUtil.stalePause();
        playbookname = "//div[@class='playbooks-data']/h3[contains(text(),'"+playbookname+"']";
        wait.waitTillElementNotPresent(playbookname, MIN_TIME, MAX_TIME);
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
        wait.waitTillElementDisplayed(EDIT_PLAYBOOK, MIN_TIME, MAX_TIME);
        item.click(EDIT_PLAYBOOK);
        wait.waitTillElementDisplayed(SAVE_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(testdata);
        item.click(SAVE_PLAYBOOK_BUTTON);
        wait.waitTillElementNotDisplayed(SAVE_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
        Report.logInfo("Finished editing the playbook.");
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
     * @param oldtaskdata - a HashMap comprising of tasks data.
     * @param newtaskdata - a HashMap comprising updated task data.
     * @return void
     */
    public void editTask(HashMap<String, String> oldtaskdata, HashMap<String, String> newtaskdata) {
        Report.logInfo("Started editing the Task");
        getTask(oldtaskdata);	//Clicks the edit icon for the respective task
        wait.waitTillElementDisplayed(SAVE_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
        fillTaskDetails(newtaskdata);
        item.click(SAVE_PBTASK_BUTTON);
        wait.waitTillElementNotDisplayed(SAVE_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
        amtDateUtil.stalePause();
        Report.logInfo("Update the task");
    }
    
    /**
     * Searches on all tasks with the data sent & returns the Task card a WebElement.
     * @param testdata - a HashMap comprising of Task data.
     * @return WebElement - Task Card.
     */
    public void getTask(HashMap<String, String> testdata) {
        String expsubject = testdata.get("subject");
        item.click("//h4[contains(text(),'"+expsubject+"')]/parent::div/ul/li/a[contains(text(),'Edit')]");
        
       /* 
        String expstatus = testdata.get("status");
        String exppriority = testdata.get("priority");
        String expdate = testdata.get("date");
        WebElement task = null;
        amtDateUtil.stalePause();
        List<WebElement> eleList = element.getAllElement("//div[@class='ga-badge ga-typeSupport playbook-tasks']");
        System.out.println(eleList.size());
        if(eleList != null && eleList.size() > 0) {
            for(WebElement ele : eleList) {
                if(ele.isDisplayed()) {
                    String subject = ele.findElement(By.cssSelector("h4.ga-fltl.taskHeader")).getText().trim();
//                    String subject = ele.findElement(By.xpath("//h4[contains(text(),'"+testdata.get("subject")+"')]/parent::div/following-sibling::ul/li/div[@class='tasks-value' and text()='In Progress']"));
                    String status = ele.findElement(By.xpath("//h4[contains(text(),'"+testdata.get("subject")+"')]/parent::div/following-sibling::ul/li/div[@class='tasks-value' and text()='"+testdata.get("status")+"']")).getText().trim();
//                    String status = ele.findElement(By.cssSelector("div.ga-value")).getText().trim();
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
        return task;*/
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
    	String expsubject = testdata.get("subject");
        item.click("//h4[contains(text(),'"+expsubject+"')]/parent::div/ul/li/a[contains(text(),'Delete')]");
        amtDateUtil.stalePause();
        WorkflowBasePage wfb = new WorkflowBasePage();
    	wfb.wf_modal_accept();
     
        String task = "//h4[contains(text(),'"+expsubject+"')]";
        wait.waitTillElementNotPresent(task, MIN_TIME, MAX_TIME);
    }
    
    public void duplicatePlaybook(){
    	//Add Playbook with tasks //Select a playbook   	
    	//Click on duplicate playbook icon and provide name
    	//Verify the playbook and tasks added
    	
    }
    
    /**
     * Search Playbook.
     * @param testdata - a HashMap comprising of Task data.
     * @return void
     */
    public void searchPlaybook(HashMap<String, String> testdata){
    	String playbookname = testdata.get("playbookname");
    	Report.logInfo("Started searching playbook :" +playbookname);
    	wait.waitTillElementDisplayed(ADD_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
    	Report.logInfo("Started searching the playbook details.");
    	if(playbookname!=null){
    		driver.findElement(By.xpath(READY_INDICATOR)).sendKeys(playbookname);
            driver.findElement(By.xpath(READY_INDICATOR)).sendKeys(Keys.ENTER);
    	}
//    	openPlaybook(playbookname);	//Call this method in test
    	//Verify the playbook in the playbook list
//    	isplaybookpresent(playbookname); //call this method in test
    	}
    
    /**
     * Search Task.
     * @param testdata - a HashMap comprising of Task data.
     * @return void
     */
    public void searchTask(HashMap<String, String> testdata){
    	String taskSubject = testdata.get("subject");
    	Report.logInfo("Started searching task :" +taskSubject);
    	wait.waitTillElementDisplayed(ADD_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
    	Report.logInfo("Started searching the task details.");
    	if(taskSubject!=null){
    		driver.findElement(By.xpath(READY_INDICATOR)).sendKeys(taskSubject);
            driver.findElement(By.xpath(READY_INDICATOR)).sendKeys(Keys.ENTER);
    	}
//    	isTaskPresent(taskData);//call this method in test
    }

}
   
