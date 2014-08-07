package com.gainsight.sfdc.workflow.pages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

import java.util.HashMap;

public class WorkflowPlaybooksPage extends BasePage {

	private final String READY_INDICATOR            = "//input[@class='gs-btn btn-add']";  
	private final String ADD_PLAYBOOK_BUTTON        = "//input[@class='gs-btn btn-add']";	//Add Playbook 
	private final String SAVE_PLAYBOOK_BUTTON		= "//input[@class='gs-btn btn-save btn-save-playbook']";	//Save Playbook 
	private final String CANCEL_PLAYBOOK_BUTTON		= "//input[@class='gs-btn btn-cancel btn-cancel-playbook']";	//Cancel Playbook
	private final String PLAYBOOK_NAME_INPUT		= "//input[@class='form-control pb-subject']";	//Playbook Name
	private final String PLAYBOOK_COMMENTS_INPUT	= "//textarea[@class='form-control  pb-description']";	//Playbook Comments
	private final String RISK_PBTYPE_RADIO 			= "//input[@type='radio' and @value='Risk' and @name='playbook-type']";	//Risk Radio button
	private final String OPPORTUNITY_PBTYPE_RADIO 	= "//input[@type='radio' and @value='Opportunity' and @name='playbook-type']";	//Opportunity Radio button
	private final String EVENT_PBTYPE_RADIO 		= "//input[@type='radio' and @value='Event' and @name='playbook-type']";	//Event Radio button
	private final String ALL_PBTYPE_RADIO 			= "//input[@type='radio' and @value='All' and @name='playbook-type']";	//All Radio button
	private final String DUPLICATE_PLAYBOOK			= "//a[@title='Duplicate playbook']";	//Duplicate Playbook Icon
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
	private final String PRIORITY_PBTASK_BUTTON		="//label[@class='Priority__cClass taskFieldLbl ga-flabel col-sm-2 control-label']/parent::div/div/button/span";	//Task Priority	
	
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
    	item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(SAVE_PLAYBOOK_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(playbookData);
        fillTaskDetails(taskData);
        item.click(SAVE_PLAYBOOK_BUTTON);
        amtDateUtil.stalePause();
    }
    
    /**
     * Checks for the playbook name in the tree hierarchy.
     * @param playbookname - Name of the Playbook to search for.
     * @return boolean - true if Playbook found, false Playbook not found. 
     */
    public boolean isplaybookpresent_wf(String playbookname) {
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
     * Checks for the task in the Playbook
     * @param testdata
     * @return boolean  - true - task present, false - if task not present.
     */
    public boolean isTaskPresent_wf(HashMap<String, String> testdata) {
        Report.logInfo("Verifying the task is dispalyed");
        boolean result = false;
        List<WebElement> eleList = null;
        wait.waitTillElementDisplayed("//td[@class='standartTreeRow']/span[text()='"+testdata.get("playbookname")+"']", MIN_TIME, MAX_TIME);
        item.click("//td[@class='standartTreeRow']/span[text()='"+testdata.get("playbookname")+"']");
        wait.waitTillElementDisplayed(ADD_PBTASK_BUTTON, MIN_TIME, MAX_TIME);
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

    /**
     * Searches weather the Playbook is of ALL type. 
     * @param playbookname - Playbook Name.
     * @return true - All Playbook, else false. 
     */
    public boolean isAllPlaybook_wf(String playbookname) {
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
     * Fills the Playbook form.
     * @param testdata - a HashMap where all the data needed a Playbook exists
     * @return void
     */
    public void fillPlaybookDetails(HashMap<String, String> testdata) {
       Report.logInfo("Started filling the playbook details.");
        
        Report.logInfo("Finished filling the playbook details");
    }
    
    /**
     * Fills the Task form details.
     * @param testdata - a HashMap comprising of tasks data.
     * @return void
     */
    public void fillTaskDetails(HashMap<String, String> testdata) {
        Report.logInfo("Stated filling the task form details.");
        if(testdata.get("subject") != null) {
            field.clearAndSetText(SUBJECT_PBTASK_INPUT, testdata.get("subject"));
        }
        if(testdata.get("date") != null) {
            field.clearAndSetText(RELDATECOUNT_PBTASK_INPUT, testdata.get("date"));
        }
        if(testdata.get("priority") != null) {
            //element.selectFromDropDown(TASK_PRIORITY_SELECT, testdata.get("priority"));
        	element.click(PRIORITY_PBTASK_BUTTON);
        	//element.
            
        }
        if(testdata.get("status") !=null) {
            element.selectFromDropDown(STATUS_PBTASK_BUTTON, testdata.get("status"));
        }
        Report.logInfo("Fininshed filling the task form details.");
    }
    
    public void editPlaybook_wf(){
    	
    }
//    clonePlaybook
//    deletePlaybook
//    addTask
//    editTask
//    deleteTask
//    searchPlaybook
//    searchTask
    
 
}
