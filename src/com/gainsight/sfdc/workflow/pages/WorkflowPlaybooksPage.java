package com.gainsight.sfdc.workflow.pages;

import com.gainsight.pageobject.core.Report;

public class WorkflowPlaybooksPage {

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
		/*item.click(ADD_PLAYBOOK_BUTTON);
        wait.waitTillElementDisplayed(CREATE_BUTTON, MIN_TIME, MAX_TIME);
        fillPlaybookDetails(playbookData);
        fillTaskDetails(taskData);
        item.click(CREATE_BUTTON);
        amtDateUtil.stalePause();*/
    }
	
}
