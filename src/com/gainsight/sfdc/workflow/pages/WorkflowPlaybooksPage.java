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
	
	private final String DUPLICATE_PLAYBOOK			="//a[@title='Duplicate playbook']";	//Duplicate Playbook Icon
	private final String EDIT_PLAYBOOK				="//a[@title='Edit playbook']";	//Edit Playbook
	private final String DELETE_PLAYBOOK			="//a[@title='Delete playbook']";	//Delete Playbook
	
	private final String SEARCH_PLAYBOOK_INPUT		="//input[@class='form-control global-search']";	//Search Playbook 
	private final String SEARCH_TASK_INPUT			="//input[@class='form-control global-search col-md-12 search-playbooks-tasks']";	//Search Task
	
	private final String ADD_PBTASK_BUTTON			="//input[@class='gs-btn btn-add  btn-add-task']";	//Add Task
	//Edit Task
	//Delete Task
	//Task Subject
	//Task Priority
	//Task Date
	//Task Status
	//Task Description
}
