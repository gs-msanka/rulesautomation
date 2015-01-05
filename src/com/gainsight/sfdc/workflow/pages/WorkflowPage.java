package com.gainsight.sfdc.workflow.pages;


import java.util.ArrayList;
import java.util.List;

import com.gainsight.pageobject.util.Timer;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.testdriver.Log;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkflowPage extends WorkflowBasePage {

	private final String READY_INDICATOR  = "//div[@title='Add CTA']";
    private final String CALENDAR_VIEW_READY_INDICATOR = "//ul[@class='calendar-tab']";


    //Header Page Elements
    private final String SHOW_CLOSED_CTA    = "//li[contains(@class, 'cta-stage baseFilter cta-stage-filter')]";
    private final String HIDE_CLOSED_CTA    = "//li[starts-with(@class, 'cta-stage') and contains(@class, 'active')]";
    private final String SHOW_IMP_CTA       = "//li[contains(@class, 'cta-flag baseFilter cta-flag-filter')]";
    private final String HIDE_FLAG_CTA      = "//li[starts-with(@class, 'cta-flag') and contains(@class, 'active')]";
    private final String SHOW_SNOOZE_CTA    = "//li[contains(@class, 'cta-snooze baseFilter cta-snooze-filter')]";
    private final String HIDE_SNOOZE_CTA    = "//li[starts-with(@class, 'cta-snooze') and contains(@class, 'active')]";

    private final String TYPE_CTA           = "//div[@class='type cta-types-filter']/descendant::li[starts-with(@class, 'cta-types') and @name='%s']";
    private final String TYPE_CTA_ACTIVE    = "//div[@class='type cta-types-filter']/descendant::li[starts-with(@class, 'cta-types') and contains(@class, 'active') and @name='%s']";
    private final String PRIORITY_CTA       = "//div[@class='priority cta-priority-filter']/ul/li[@name='%s' and contains(@class, 'cta-priority')]";
    private final String PRIORITY_CTA_ACTIVE= "//div[@class='priority cta-priority-filter']/ul/li[@name='%s' and contains(@class, 'cta-priority') and contains(@class, 'active')]";

    private final String OWNER              = "//div[@class='wf-owner-search']/span/label[contains(@class, 'cta-username')]";
    private final String OWNER_SEARCH       = "//div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::input[@type='text' and @name='search_text']";
    private final String OWNER_SELECT       = "//div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::label[contains(text(), '%s')]";
    private final String OWNER_ALL          = "//div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::a[@id='All']/label[contains(text(), 'All Owners')]";
    private final String GROUP_BY           = "//select[@class='form-control cta-group-by']/following-sibling::button";
    private final String SORT_BY            = "//select[@class='form-control cta-sort-by']/following-sibling::button";
    private final String FILTER             = "//a[@class='dashboard-filter-btn']";

    //Popup Page Elements
    private final String OK_ACTION          = "//input[@data-action='Ok' and contains(@class, 'btn_save')]";
    private final String SAVE_ACTION        = "//input[@data-action='Yes' and contains(@class, 'btn_save')]";
    private final String CANCEL_ACTION      = "//input[@data-action='Cancel' and contains(@class, 'btn_cancel')]";
    private final String DELETE_ACTION      = "//input[@data-action='Delete' and contains(@class, 'btn-save')]";

    //CTA Form Page Elements
    private final String CREATE_CTA_ICON        = "//a[@class='dashboard-addcta-btn more-options cta-create-btn']";
    private final String CREATE_RISK_LINK       = "//a[@data-action='RISK']";
    private final String CREATE_OPPOR_LINK      = "//a[@data-action='OPPORTUNITY']";
    private final String CREATE_EVENT_LINK      = "//a[@data-action='EVENT']";
    private final String RISK_CTA_FORM_TITLE    = "//span[text()='Add Risk']";
    private final String OPPO_CTA_FORM_TITLE    = "//span[text()='Add Opportunity']";
    private final String EVENT_CTA_FORM_TITLE   = "//span[text()='Add Event']";
    private final String CREATE_FORM_SUBJECT    = "//input[@class='form-control cta-subject']";
    private final String CREATE_FORM_CUSTOMER   = "//input[@class='form-control strcustomer ui-autocomplete-input']";
	private final String CREATE_FORM_REASON     = "//div[@class='col-md-9']/button/span[@class='ui-icon ui-icon-triangle-2-n-s']";// "//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']/span[@class='ui-icon ui-icon-triangle-2-n-s']";
	private final String CREATE_FORM_SELECT_REASON  = "//ul/li/label/span[text()='%s']";
	private final String CREATE_FORM_DUE_DATE       = "//input[@class='form-control cta-dateCtrl']";
	private final String CREATE_FORM_COMMENTS       = "//div[@class='form-control strdescription']";
	private final String ASSIGN_TO_ME		="//a[@class='owner-assign-tome']";
	private final String TASK_ASSIGN_TO_ME="//div[@class='taskForm']//a[@class='owner-assign-tome']";
	private final String SAVE_CTA                   = "//div[@class='modal-footer text-center']/button[@class='gs-btn btn-save']";
	private final String CREATE_RECURRING_EVENT     = "//div[@class='form-group clearfix cta-recurring-event']/input[@id='chkRecurring']";
    private final String CREATE_FORM_RECUR_TYPE     = "//div[@class='row']/label[@class='radio-inline']/input[@value='%s']";
    private final String CREATE_RECUR_EVERYnDAYS    = "//label[@class='radio-inline']/input[@value='RecursDaily']";
    private final String CREATE_RECUR_DAILY_INTERVAL    = "//div[@class='date-float']/input[@class='form-control width40 text-center daily-every-daynumpick']";
    private final String RECUR_EVENT_START_DATE         = "//input[@class='form-control date-input scheduler-event-start-datepick']";
    private final String RECUR_EVENT_END_DATE           = "//input[@class='form-control date-input scheduler-event-end-datepick']";
    private final String RECUR_WEEK_COUNT               = "//div[@class='date-float']/input[@class='form-control width40 text-center weekly-recursevery-weekpick']";
    private final String RECUR_WEEKDAY                  = "//div[@class='row']/label[@class='checkbox-inline']/input[@value='%d']";
    private final String TO_SELECT_RECUR_DAY_OF_MONTH   = "//div[@class='date-float']/button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_DAY_OF_MONTH             = "//li[contains(@class,'ui-multiselect-option')]/label[contains(@class,'ui-corner-all')]/span[contains(text(),'%s')]";
    private final String RECUR_MONTH_INTERVAL           = "//div[@class='date-float']/input[@class='form-control width40 text-center monthly-onday-ofevery-monthpick']";
    private final String RECUR_MONTHLY_BY_WEEKDAY       = "//label[@class='radio-inline']/input[@value='RecursMonthlyNth']";
    private final String TO_SELECT_WEEK_NUMBER          = "//select[@class='form-control1 monthly-onthe-daynumpick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_WEEK_NUMBER_OF_MONTH     = "//label[@class='ui-corner-all']/input[@title='%s']/following-sibling::span";
    private final String TO_SELECT_RECUR_WEEK_OF_MONTH  = "//select[@class='form-control1 monthly-onthe-daypick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_WEEK_OF_MONTH            = "//li[contains(@class,'ui-multiselect-option')]//span[contains(text(),'%s')]";
    private final String RECUR_MONTHLY_INTERVAL_BYWEEK  = "//div[@class='date-float']/input[@class='form-control width40 text-center monthly-onthe-ofevery-monthpick']";

    private final String RECURS_YEARLY_INPUT                    = "//input[@value='RecursYearly' and @name='Yearly-on']";
    private final String RECURS_YEARLY_NTH_INPUT                = "//input[@value='RecursYearlyNth' and @name='Yearly-on']";
    private final String RECURS_YEARLY_ON_DAY_NUM_PICK          = "//select[contains(@class, 'yearly-onthe-daynumpick')]/following-sibling::button";
    private final String RECURS_YEARLY_ON_DAY_PICK              = "//select[contains(@class, 'yearly-onthe-daypick')]/following-sibling::button";
    private final String RECURS_YEARLY_MONTH_PICK               = "//select[contains(@class, 'yearly-onthe-monthpick')]/following-sibling::button";
    private final String RECUR_YEARLY_BYMONTH_TO_SELECT_MONTH   = "//select[@class='form-control1 yearly-onevery-monthpick']/following-sibling::button";
    private final String RECUR_YEARLY_BYMONTH_TO_SELECT_DATE    = "//select[@class='form-control1 yearly-onevery-daynumpick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_YEARLY_STARTDATE_SELECT          = "//select[@class='sel-start-year']/following-sibling::button";
    private final String RECUR_YEARLY_ENDDATE_SELECT            = "//select[@class='sel-end-year']/following-sibling::button";
    private enum WEEKDAY{Sun,Mon,Tue,Wed,Thu,Fri,Sat};

    //CTA Expanded View Elements
    private final String EXP_VIEW_HEADER                = "//div[@class='wf-details-header']";
    private final String EXP_VIEW_CTA_MORE_OPTIONS      = "//a[@class='more-edit more-options']";
    private final String EXP_VIEW_ADD_NEW_TASK          = "//a[@data-action='ADD_TASK']/span[@class='add']";
    private final String EXP_VIEW_APPLY_PLAYBOOK        = "//a[@data-action='APPLY_PlAYBOOK']/span[text()='Apply Playbook']";
    private final String EXP_VIEW_REPLACE_PLAYBOOK      = "//a[@data-action='APPLY_PlAYBOOK']/span[text()='Replace Playbook']";
    private final String EXP_VIEW_DELETE_CTA            = "//a[@data-action='DELETE_CTA' and contains(text(), 'Delete %s')]";
    private final String DETAILED_FORM                  = "//div[@class='widget workflow-details' and contains(@style, 'opacity: 1;')]";
    private final String EXP_VIEW_SUBJECT_INPUT         = "//input[contains(@class, 'editblue_title_input cta-title')]";
    private final String EXP_VIEW_REASON_BUTTON         = "//select[@class='select-cta-reason cockpit-multiselect']/following-sibling::button";
    private final String EXP_VIEW_PRIORITY_BUTTON       = "//select[@class='cta-select-priority cockpit-multiselect']/following-sibling::button";
    private final String EXP_VIEW_STATUS_BUTTON         = "//select[@class='cta-select-stage cockpit-multiselect']/following-sibling::button";
    private final String EXP_VIEW_CUSTOMER              = "//div[@class='wf-ac-details']/descendant::label[contains(@class, 'cta-accountname')]";
    private final String EXP_VIEW_TYPE                  = "//div[@class='wf-ac-details']/descendant::label[contains(@class, 'cta-accounttype')]";
    private final String EXP_VIEW_COMMENTS_DIV          = "//div[@class='cta-comments']/div[contains(@class, 'cta-comments-textarea')]";
    private final String EXP_VIEW_DUE_DATE_INPUT        = "frmDateCtrl";
    private final String EXP_VIEW_SNOOZE                = "//ul[@class='panal-tools']/descendant::a[contains(@class, 'wf-snooze')]";
    private final String EXP_VIEW_SET_SNOOZE_DATE       = "//input[@class='form-control cta-snooze-input']";
	private final String EXP_VIEW_SNOOZE_REASON_BUTTON  = "//select[@class='gs-snooze-reason cockpit-multiselect']/following-sibling::button";
    private final String EXP_VIEW_MILESTONE             = "//ul[@class='panal-tools']/descendant::a[contains(@class, 'landmark')]";
    private final String EXP_VIEW_ASSIGNEE              = "//div[@class='workflow-cta-details']/descendant::div[@class='wf-owner-search']/span/label[contains(@class, 'cta-username')]";
    private final String EXP_VIEW_ASSIGNEE_SEARCH_INPUT = "//div[@class='wf-details-header']/descendant::div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::input[@name='search_text']";
    private final String EXP_VIEW_ASSIGNEE_SELECT       = "//div[@class='wf-details-header']/descendant::div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::label[contains(text(), '%s')]";
    private final String CTA_EXP_SLIDE_ICON             = "//div[@class='cta-detail-set']//div[@class='slide-icon']";
    
    private final String VIEW_TASKS             = "//div[contains(@class,'task require-tooltip workflow-taskscnt  task-hyper')]";
    private final String DESYNCED_ICON          = "//div[contains(@class,'task require-tooltip task-sync')]/span[@class='glyphicon glyphicon-refresh']";
    private final String SYNCED_ICON            = "//div[contains(@class,'task require-tooltip task-sync  active')]/span[@class='glyphicon glyphicon-refresh']";
    private final String DESYNC_BUT_KEEP_INSF   = "//div[@class='modal_footer']/input[@data-action='Keep']";
    private final String DESYNC_AND_DELETE      = "//div[@class='modal_footer']/input[@data-action='Delete']";
    
    //Task Expanded View Elements
    private final String TASK_EXP_ASSIGNEE          = "//div[@class='wf-details-header']/descendant::label[@class='task-username']";
    private final String TASK_EXP_ASSIGNEE_SEARCH   = "//div[@class='task-detail-set']/descendant::div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::input[@name='search_text']";
    private final String TASK_EXP_ASSIGNEE_SELECT   = "//div[@class='task-detail-set']/descendant::div[@class='gs-dropdown gs-dropdown-profile pull-left open']/descendant::label[contains(text(), '%s')]";
    private final String TASK_EXP_SUBJECT           = "//input[contains(@class, 'editblue_title_input task-title')]";
    private final String TASK_EXP_PRIORITY          = "//select[contains(@class, 'task-select-priority')]/following-sibling::button";
    private final String TASK_EXP_STATUS            = "//select[contains(@class, 'task-select-status')]/following-sibling::button";
    private final String TASK_COMMENTS              = "//div[contains(@class, 'task-description-textarea')]";
    private final String TASK_DUE_DATE              = "task-date-id";
    private final String TASK_EXP_SLIDE_ICON        = "//div[@class='task-detail-set']/div[@class='slide-icon']";
    private final String TASK_EXP_MORE_OPTIONS      = "//div[@class='task-detail-set']/descendant::a[contains(@class, 'more-options')]";
    private final String TASK_EXP_EDIT_OPTION       = "//div[@class='task-detail-set']/descendant::a[@data-action='EDIT_TASK']";
    private final String TASK_EXP_DELETE_OPTION     = "//div[@class='task-detail-set']/descendant::a[@data-action='DELETE_TASK']";

    //Filter View Elements
    private final String FILTER_TITLE   = "//span[@class='ui-dialog-title']";
    private final String FILTER_ADD     = "//div[@data-filter='ADD_ADV_FILTER']";
    private final String FILTER_APPLY   = "//input[@data-action='APPLY_CHANGES']";
    private final String FILTER_CANCEL  = "//input[@data-action='CANCEL']";

	//Task Form Elements
    private final String SELECT_MORE_OPTIONS	    = "//a[@class='more-edit more-options']";
    private final String ADD_NEW_TASK			    = "//a[@data-action='ADD_TASK']/span[@class='add']";
    private final String ADD_TASK_POPUP_TITLE	    = "//span[text()='Add Task']";
    private final String EDIT_TASK_POPUP_TITLE		="//span[text()='Edit Task']";
    private final String ADD_ASSIGNEE_TO_TASK	    = "//input[@class='search_input form-control ui-autocomplete-input']";
    private final String SELECT_ASSIGNEE_FOR_TASK   = "//div[@class='gs_searchform']/descendant::label[contains(text(),'%s')]";
    private final String ADD_SUBJECT_TO_TASK		= "//input[@class='Subject__c form-control']";
    private final String ADD_DATE_TO_TASK			= "//input[@class='Date__c form-control gs-calendar']";
    private final String TO_SELECT_PRIORITY_IN_TASK = "//select[@class='Priority__c form-control form-select']/following-sibling::button";
    private final String TO_SELECT_STATUS_IN_TASK	= "//select[@class='Status__c form-control form-select']/following-sibling::button";
    private final String SAVE_TASK		            = "//div[@class='taskForm']//button[contains(text(),'Save')]";
    private final String TASK_TITLE_TO_VERIFY		= "//span[@class='title-name workflow-task-title' and contains(text(),'%s')]";
    
    //Playbook From Elements
    private final String PLAYBOOK_SELECT = "//select[@class='playbook-list']/following-sibling::button";
    private final String PLAYBOOK_APPLY  = "//button[contains(@class, 'btn-save') and text()='Apply']";
    private final String PLAYBOOK_REPLACE  = "//button[contains(@class, 'btn-save') and text()='Replace']";
    private final String PLAYBOOK_CANCEL = "//button[contains(@class, 'btn-cancel') and text()='Cancel']";

    //Calendar Page Elements
    String CALENDER_VIEW_SELECT         = "//ul[@class='calendar-tab']/descendant::a[@data-type='%s']";
    String CALENDER_DIR_LEFT            = "//div[@class='calendar-ctn']/div[@data-direction='LEFT']";
    String CALENDER_DIR_RIGHT           = "//div[@class='calendar-ctn']/div[@data-direction='RIGHT']";

    public WorkflowPage() {
        waitForPageLoad();
    }
    
    
    public WorkflowPage(String view) {
    	super(view);
        if(view.equals("Calendar")) 
        	{
            	waitForPageLoad();
        		wait.waitTillElementDisplayed(CALENDAR_VIEW_READY_INDICATOR, MIN_TIME, MAX_TIME);
        	}
        else if(view.equals("360 Page")) Log.info("Landed in Customer 360 Cockpit section");
        else if(view.equals("Account Widget")) Log.info("landed from Account Widget");
    }

    private void waitForPageLoad() {
        Log.info("Loading Cockpit Page");
        waitTillNoLoadingIcon();
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        Log.info("Cockpit Page Loaded Successfully");
    }


    public WorkflowPage createCTA(CTA cta){
    	item.click(CREATE_CTA_ICON);
    	if(cta.getType().equals("Risk"))
    	{
            Log.info("Adding CTA of Type - RISK");
    		item.click(CREATE_RISK_LINK);
    		wait.waitTillElementDisplayed(RISK_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);

    	}
    	if(cta.getType().equals("Opportunity")){
            Log.info("Adding CTA of Type - Opportunity");
    		item.click(CREATE_OPPOR_LINK);
    		wait.waitTillElementDisplayed(OPPO_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);

    	}
    	if(cta.getType().equals("Event")){
            Log.info("Adding CTA of Type - Event");
    		item.click(CREATE_EVENT_LINK);
    		wait.waitTillElementDisplayed(EVENT_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);
    	}
    	return this;
	}
    
    private void fillAndSaveCTAForm(CTA cta) {
        Log.info("Started Filling CTA Form");
		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		if(!cta.isFromCustomer360orWidgets()) selectCustomer(cta.getCustomer());
		item.click(ASSIGN_TO_ME);
		item.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON, cta.getReason()));
		field.clearAndSetText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		//field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		if(cta.isRecurring()) {
            fillAndSaveRecurringEventCTAForm(cta);
        }
        button.click(SAVE_CTA);
        Log.info("Clicked on Save CTA");
        if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        else {
        	env.setTimeout(1);
            wait.waitTillElementNotPresent("//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]", MIN_TIME, MAX_TIME);
            env.setTimeout(30);
        }
       if(!cta.isFromCustomer360orWidgets()) waitTillNoSearchIcon();
	}

	private void fillAndSaveRecurringEventCTAForm(CTA cta) {
        Log.info("Starting to fill recurring event part");
    	field.selectCheckBox(CREATE_RECURRING_EVENT);
		CTA.EventRecurring recurProperties=cta.getEventRecurring();
			if(recurProperties.getRecurringType().equals("Daily")){
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				if(!recurProperties.getDailyRecurringInterval().equals("EveryWeekday")){
					item.click(CREATE_RECUR_EVERYnDAYS);
					field.clearAndSetText(CREATE_RECUR_DAILY_INTERVAL,recurProperties.getDailyRecurringInterval());
				}				
			}
			else if (recurProperties.getRecurringType().equals("Weekly")){
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				field.clearAndSetText(RECUR_WEEK_COUNT, recurProperties.getWeeklyRecurringInterval().split("_")[0]);
				field.selectCheckBox(String.format(RECUR_WEEKDAY,WEEKDAY.valueOf(recurProperties.getWeeklyRecurringInterval().split("_")[1]).ordinal()+1));			
			}
			else if(recurProperties.getRecurringType().equals("Monthly")){
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				if(recurProperties.getMonthlyRecurringInterval().startsWith("Day")){
				item.click(TO_SELECT_RECUR_DAY_OF_MONTH);
				item.click(String.format(RECUR_DAY_OF_MONTH, recurProperties.getMonthlyRecurringInterval().split("_")[1]));
				field.clearAndSetText(RECUR_MONTH_INTERVAL, recurProperties.getMonthlyRecurringInterval().split("_")[2]);
				}
				else if(recurProperties.getMonthlyRecurringInterval().startsWith("Week")){
					item.click(RECUR_MONTHLY_BY_WEEKDAY);
					item.click(TO_SELECT_WEEK_NUMBER);
					System.out.println("dummy");
                    selectValueInDropDown(recurProperties.getMonthlyRecurringInterval().split("_")[1]);
					item.click(TO_SELECT_RECUR_WEEK_OF_MONTH);
					item.click(String.format(RECUR_WEEK_OF_MONTH,recurProperties.getMonthlyRecurringInterval().split("_")[2]));
					item.clearAndSetText(RECUR_MONTHLY_INTERVAL_BYWEEK,recurProperties.getMonthlyRecurringInterval().split("_")[3]);
				}					
			}
			else if(recurProperties.getRecurringType().equals("Yearly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
                String exp[] = recurProperties.getYearlyRecurringInterval().split("_");
				if (exp[0].equalsIgnoreCase("Day")){
                    item.click(RECURS_YEARLY_NTH_INPUT);
                    item.click(RECURS_YEARLY_ON_DAY_NUM_PICK);
                    selectValueInDropDown(exp[1]);
                    item.click(RECURS_YEARLY_ON_DAY_PICK);
                    selectValueInDropDown(exp[2]);
                    item.click(RECURS_YEARLY_MONTH_PICK);
                    selectValueInDropDown(exp[3]);

				} else {
                    item.click(RECURS_YEARLY_INPUT);
                    item.click(RECUR_YEARLY_BYMONTH_TO_SELECT_MONTH);
                    selectValueInDropDown(exp[0]);
                    item.click(RECUR_YEARLY_BYMONTH_TO_SELECT_DATE);
                    selectValueInDropDown(exp[1]);
                }

                item.click(RECUR_YEARLY_STARTDATE_SELECT);
                selectValueInDropDown(recurProperties.getRecurStartDate());
                item.click(RECUR_YEARLY_ENDDATE_SELECT);
                selectValueInDropDown(recurProperties.getRecurEndDate());
				}
		    Timer.sleep(2); //In - Case, Should add wait logic here.
        Log.info("Completed Recurring event form");
    }

	public WorkflowPage addTaskToCTA(CTA cta,List<Task> tasks){
		expandCTAView(cta);
		for(Task task : tasks){
			item.click(SELECT_MORE_OPTIONS);
			item.click(ADD_NEW_TASK);
			wait.waitTillElementDisplayed(ADD_TASK_POPUP_TITLE, MIN_TIME, MAX_TIME);
            selectTaskOwner(task.getAssignee().trim());
			item.setText(ADD_SUBJECT_TO_TASK, task.getSubject());
			item.clearAndSetText(ADD_DATE_TO_TASK, task.getDate());
			item.click(ADD_TASK_POPUP_TITLE);  //just clicking on the form because the date picker is not vanishing and not able to set the priority
			item.click(TO_SELECT_PRIORITY_IN_TASK);
			selectValueInDropDown(task.getPriority());
			item.click(TO_SELECT_STATUS_IN_TASK);
			selectValueInDropDown(task.getStatus());
			item.click(SAVE_TASK);
			cta.setTaskCount(cta.getTaskCount()+1);
			wait.waitTillElementPresent(String.format(TASK_TITLE_TO_VERIFY, task.getSubject()), MIN_TIME, MAX_TIME);
            if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
            else waitTillNoLoadingIcon_360();
		}
        collapseCTAView();
		return this;
	}


    private void selectTaskOwner(String owner) {
        Log.info("Selecting Task Owner : " +owner);
        boolean selected = false;
        for(int i=0; i< 3; i++) {
            item.clearAndSetText(ADD_ASSIGNEE_TO_TASK, owner);
            driver.findElement(By.xpath(ADD_ASSIGNEE_TO_TASK)).sendKeys(Keys.ENTER);
            for(WebElement ele : element.getAllElement("//li[@class='ui-menu-item']/a/label[contains(text(), '"+owner+"')]")) {
                if(ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                    return;
                }
            }
            Timer.sleep(2);
        }
        if(!selected) {
            throw new RuntimeException("Unable to select owner");
        }
        Log.info("Selected Task Owner Successfully: " +owner);
    }
	
	public WorkflowPage editTasks(CTA cta,Task newTask,Task oldTask){
		//item.click(VIEW_TASKS);
		expandTaskView(oldTask);
		item.click(TASK_EXP_MORE_OPTIONS);
		item.click(TASK_EXP_EDIT_OPTION);
		wait.waitTillElementDisplayed(EDIT_TASK_POPUP_TITLE, MIN_TIME, MAX_TIME);
        if(!oldTask.getAssignee().equalsIgnoreCase(newTask.getAssignee())) {
            selectTaskOwner(newTask.getAssignee());
        }
        item.clearAndSetText(ADD_SUBJECT_TO_TASK, newTask.getSubject());
        item.clearAndSetText(ADD_DATE_TO_TASK, newTask.getDate());
        item.click(EDIT_TASK_POPUP_TITLE);  //just clicking on the form because the date picker is not vanishing and not able to set the priority
        item.click(TO_SELECT_PRIORITY_IN_TASK);
        selectValueInDropDown(newTask.getPriority());
        item.click(TO_SELECT_STATUS_IN_TASK);
        selectValueInDropDown(newTask.getStatus());
        item.click(SAVE_TASK);
        wait.waitTillElementPresent(String.format(TASK_TITLE_TO_VERIFY, newTask.getSubject()), MIN_TIME, MAX_TIME);
        if(!oldTask.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        waitTillNoLoadingIcon_360();
		return this;
	}
	
    private void selectCustomer(String cName) {
        Log.info("Selecting Customer : " +cName);
        boolean selected = false;
        for(int i=0; i< 3; i++) {
            item.clearAndSetText(CREATE_FORM_CUSTOMER, cName);
            driver.findElement(By.xpath(CREATE_FORM_CUSTOMER)).sendKeys(Keys.ENTER);
            for(WebElement ele : element.getAllElement("//li[@class='ui-menu-item']/a/label[contains(text(), '"+cName+"')]")) {
                if(ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                    return;
                }
            }
            Timer.sleep(2);
        }
        if(!selected) {
            throw new RuntimeException("Unable to select owner");
        }
        Log.info("Selected Customer Successfully: " +cName);
    }

    
    public void createMilestoneForCTA(CTA cta){
    	expandCTAView(cta);
    	item.click(EXP_VIEW_MILESTONE);
        Timer.sleep(5);
    }
    
    public WorkflowPage snoozeCTA(CTA cta){
    	expandCTAView(cta);
    	item.click(EXP_VIEW_SNOOZE);
    	item.clearAndSetText(EXP_VIEW_SET_SNOOZE_DATE, cta.getSnoozeDate());
    	item.click(EXP_VIEW_SNOOZE_REASON_BUTTON);
    	selectValueInDropDown(cta.getSnoozeReason());
    	item.click(EXP_VIEW_HEADER); //click somewhere else
        Timer.sleep(5);
        return this;
    }

    public WorkflowPage updateCTADetails(CTA ExpectedCta, CTA newCta) {
        expandCTAView(ExpectedCta);
        if(!ExpectedCta.getAssignee().equalsIgnoreCase(newCta.getAssignee())) {
            boolean status = false;
            wait.waitTillElementDisplayed(EXP_VIEW_ASSIGNEE, MIN_TIME, MAX_TIME);
            item.click(EXP_VIEW_ASSIGNEE);
            wait.waitTillElementDisplayed(EXP_VIEW_ASSIGNEE_SEARCH_INPUT, MIN_TIME, MAX_TIME);
            field.clearText(EXP_VIEW_ASSIGNEE_SEARCH_INPUT);
            field.setText(EXP_VIEW_ASSIGNEE_SEARCH_INPUT, newCta.getAssignee().trim());
            driver.findElement(By.xpath(EXP_VIEW_ASSIGNEE_SEARCH_INPUT)).sendKeys(Keys.ENTER);
            if(!ExpectedCta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
            else waitTillNoLoadingIcon_360();
            wait.waitTillElementDisplayed(String.format(EXP_VIEW_ASSIGNEE_SELECT, newCta.getAssignee()), MIN_TIME, MAX_TIME);
            for(WebElement ele : element.getAllElement(String.format(EXP_VIEW_ASSIGNEE_SELECT, newCta.getAssignee()))){
                if(ele.isDisplayed()) {
                    ele.click();
                    status = true;
                    break;
                }
            }
            if(!status) {
                throw new RuntimeException("Failed to change the assignee");
            }
        }
        if(newCta.getDueDate() != null && !newCta.getDueDate().equalsIgnoreCase(ExpectedCta.getDueDate())) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String a = "j$('#"+EXP_VIEW_DUE_DATE_INPUT+"').val(\""+newCta.getDueDate()+"\").trigger(\"change\")" ;
            js.executeScript(a);
        }
        if(newCta.getSubject() !=null) {
            item.click(EXP_VIEW_SUBJECT_INPUT);
            field.clearText(EXP_VIEW_SUBJECT_INPUT);
            new Actions(driver).moveToElement(element.getElement(EXP_VIEW_SUBJECT_INPUT)).build().perform();
            field.setText(EXP_VIEW_SUBJECT_INPUT, newCta.getSubject());
            new Actions(driver).moveToElement(element.getElement(EXP_VIEW_SUBJECT_INPUT)).sendKeys(Keys.ENTER).build().perform();

            WebElement elements = element.getElement(EXP_VIEW_SUBJECT_INPUT);
            JavascriptLibrary javascript = new JavascriptLibrary();
            javascript.callEmbeddedSelenium(driver, "triggerEvent", elements, "blur");
        }

        if(newCta.getPriority() != null) {
            item.click(EXP_VIEW_PRIORITY_BUTTON);
            selectValueInDropDown(newCta.getPriority());
        }

        if(newCta.getStatus() != null) {
            item.click(EXP_VIEW_STATUS_BUTTON);
            selectValueInDropDown(newCta.getStatus());
        }

        if(newCta.getReason() != null) {
            item.click(EXP_VIEW_REASON_BUTTON);
            selectValueInDropDown(newCta.getReason());
        }
        Timer.sleep(2);
        collapseCTAView();
        return this;
    }
    
    public WorkflowPage updateCTAStatus_toClosedLost(CTA cta) {
        expandCTAView(cta);
        item.click(EXP_VIEW_STATUS_BUTTON);
       selectValueInDropDown("Closed Lost");
        Timer.sleep(2);
        collapseCTAView();
        return this;
    }


    public boolean isCTADisplayed_WithScore(CTA cta,String scheme){
    	String scoredCTAXpath;
    	if(scheme.equals("Color"))
    		scoredCTAXpath = getCTAXPath(cta)+"/descendant::span[@class='health-score-text' and @style='background-color:"+cta.getScoreOfCustomer()+"']";
    	else
    		scoredCTAXpath=getCTAXPath(cta)+"/descendant::span[@class='health-score-text' and contains(text(),'"+cta.getScoreOfCustomer()+"')]";
    	item.click(scoredCTAXpath);
    	return isCTAExpandedViewLoaded(cta);
    }
    
    public boolean isCTADisplayed(CTA cta) {
        if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        env.setTimeout(2);
        try {
            List<WebElement> webElements = driver.findElements(By.xpath(getCTAXPath(cta)));
            Log.info("NUmber of elements :" +webElements.size());
            System.out.println();
            for(WebElement ele : webElements) {
                if(ele.isDisplayed()) {
                    return true;
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.info("CTA is not displayed / Present, Please check your XPath (or) CTA Data");
            Log.info(e.getLocalizedMessage());
            return false;
        }
        Log.info("CTA is not displayed");
        env.setTimeout(30);
        return false;

    }

    public WorkflowPage collapseCTAView() {
        Log.info("Collapsing CTA View");
        item.click(CTA_EXP_SLIDE_ICON);
        Timer.sleep(2);
        Log.info("CTA View Collapsed");
        return this;
    }
    public WorkflowPage expandCTAView(CTA cta) {
        Log.info("Expanding CTA");
        String xPath = getCTAXPath(cta)+ "/descendant::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        item.click(xPath);
        if(!isCTAExpandedViewLoaded(cta)) {
            throw new RuntimeException("CTA expand view failed to load");
        }
        Log.info("CTA expanded view loaded successfully");
        return this;
    }
    public WorkflowPage expandTaskView(Task task) {
        Log.info("Expanding Task View");
        String xPath = getTaskXPath(task)+"/div[@class='gs-cta-head child-task workflow-ctataskitem']";
        item.click(xPath);
        if(!isTaskExpandedViewLoaded(task)) {
            throw new RuntimeException("Task expand view failed to load");
        }
        Log.info("Task View Expanded Successfully");
        return this;
    }

    public WorkflowPage collapseTaskView() {
        item.click(TASK_EXP_SLIDE_ICON);
        Timer.sleep(2);
        return this;
    }

    public boolean verifyTaskDetails(Task task) {
        expandTaskView(task);
        String xpath = TASK_EXP_ASSIGNEE;
        if(!element.getText(xpath).trim().equalsIgnoreCase(task.getAssignee())) {
            Log.info("CTA is not assigned to right user.");
            return false;
        }
        String dueDate = element.getElement(TASK_DUE_DATE).getAttribute("value");
        if(dueDate == null || !dueDate.trim().equalsIgnoreCase(task.getDate())) {
            Log.info("CTA due data is not correct");
            return false;
        }
        collapseTaskView();
        return true;
    }

   public boolean verifyCTADetails(CTA cta) {
       Log.info("Verifying CTA Details in expanded view");
        expandCTAView(cta);
        String xpath = EXP_VIEW_ASSIGNEE;
        if(!element.getText(xpath).trim().equalsIgnoreCase(cta.getAssignee())) {
            Log.info("CTA is not assigned to right user.");
            return false;
        }
        String dueDate = element.getElement(EXP_VIEW_DUE_DATE_INPUT).getAttribute("value");
        if(dueDate == null || !dueDate.trim().equalsIgnoreCase(cta.getDueDate())) {
            Log.info("CTA due data is not correct");
            return false;
        }

        List<CTA.Attribute> attributes = cta.getAttributes();
        for(int i=1; i <= attributes.size(); i++) {
            //table[@class='wf-score-table cta-dynamic-fields-table']/tbody/tr[1]/td
            System.out.println("//table[@class='wf-score-table cta-dynamic-fields-table']/tbody/tr" +
                    "/th[contains(text(), '"+attributes.get(i-1).getAttLabel()+"')]" +
                    "/following-sibling::td[contains(text(), '"+attributes.get(i-1).getAttValue()+"')]");
            if(!element.getElement("//table[@class='wf-score-table cta-dynamic-fields-table']/tbody/tr" +
                    "/th[contains(text(), '"+attributes.get(i-1).getAttLabel()+"')]" +
                    "/following-sibling::td[contains(text(), '"+attributes.get(i-1).getAttValue()+"')]").isDisplayed()) {
                Log.info("Data in the table didn't match");
                return false;
            }
        }
        return true;
    }

    public boolean isTaskExpandedViewLoaded(Task task) {
        Log.info("Verifying task expanded view is loaded");
        wait.waitTillElementDisplayed(DETAILED_FORM, MIN_TIME, MAX_TIME);
        Task expViewTask = new Task();
        for(int i=0; i<5; i++) {
            expViewTask.setSubject(element.getElement(TASK_EXP_SUBJECT).getAttribute("value").trim());
            expViewTask.setPriority(element.getText(TASK_EXP_PRIORITY));
            expViewTask.setStatus(element.getText(TASK_EXP_STATUS));

            if(!task.getPriority().equalsIgnoreCase(expViewTask.getPriority()))
                Log.info("Priority not matched");
            if(!task.getStatus().equalsIgnoreCase(expViewTask.getStatus()))
                Log.info("Status not matched");
            if(task.getSubject().equalsIgnoreCase(expViewTask.getSubject()))
                Log.info("Subject not matched");

            if(task.getPriority().equalsIgnoreCase(expViewTask.getPriority()) &&
                    task.getStatus().equalsIgnoreCase(expViewTask.getStatus()) &&
                    task.getSubject().equalsIgnoreCase(expViewTask.getSubject())) {
                return true;
            }  else {
                Log.info("Waiting for Task Details to Load");
                Timer.sleep(2);
            }
        }
        Log.info("Task expand mode is not loaded properly.");
        return false;
    }

    public boolean isCTAExpandedViewLoaded(CTA cta) {
        Log.info("Verify CTA expanded view loaded successfully.");
        wait.waitTillElementDisplayed(DETAILED_FORM, MIN_TIME, MAX_TIME);
        CTA expViewCta = new CTA();
        for(int i=0; i< 5; i++) {
            try {
            	if(!cta.isFromCustomer360orWidgets())
            	{
            		expViewCta.setCustomer(element.getText(EXP_VIEW_CUSTOMER).trim());
            	}
                expViewCta.setDueDate(element.getText(EXP_VIEW_DUE_DATE_INPUT).trim());
                expViewCta.setType(element.getText(EXP_VIEW_TYPE).trim());
                expViewCta.setPriority(element.getText(EXP_VIEW_PRIORITY_BUTTON).trim());
                expViewCta.setStatus(element.getText(EXP_VIEW_STATUS_BUTTON).trim());
                expViewCta.setReason(element.getText(EXP_VIEW_REASON_BUTTON).trim());
                expViewCta.setSubject(element.getElement(EXP_VIEW_SUBJECT_INPUT).getAttribute("value").trim());
                
                if(!cta.getCustomer().trim().equalsIgnoreCase(expViewCta.getCustomer())&&!cta.isFromCustomer360orWidgets()) {
                    Log.info("Expected Value : " +cta.getCustomer());
                    Log.info("Actual Value : " +expViewCta.getCustomer());
                    Log.info("Customer Name not matched.");
                } else if (cta.getType().trim().equalsIgnoreCase(expViewCta.getType())){
                    Log.info("Expected Value :" +cta.getType()+"a");
                    Log.info("Actual Value :" +expViewCta.getType()+"a");
                    Log.info("Type not matched.");
                } else if(cta.getPriority().trim().equalsIgnoreCase(expViewCta.getPriority())) {
                    Log.info("Expected Value : " +cta.getPriority());
                    Log.info("Actual Value : " +expViewCta.getPriority());
                    Log.info("Priority not matched.");
                } else if(cta.getStatus().trim().equalsIgnoreCase(expViewCta.getStatus())) {
                    Log.info("Expected Value : " +cta.getStatus());
                    Log.info("Actual Value : " +expViewCta.getStatus());
                    Log.info("Status not matched.");
                } else if(cta.getReason().trim().equalsIgnoreCase(expViewCta.getReason())) {
                    Log.info("Expected Value : " +cta.getReason());
                    Log.info("Actual Value : " +expViewCta.getReason());
                    Log.info("Reason not matched.");
                }
                if(!cta.isFromCustomer360orWidgets()){
                	if(cta.getCustomer().trim().equalsIgnoreCase(expViewCta.getCustomer())  &&
                        cta.getType().trim().equalsIgnoreCase(expViewCta.getType()) &&
                        cta.getPriority().trim().equalsIgnoreCase(expViewCta.getPriority()) &&
                        cta.getStatus().trim().equalsIgnoreCase(expViewCta.getStatus()) &&
                        cta.getReason().trim().equalsIgnoreCase(expViewCta.getReason())) 
                		return true;
                }
                else if(cta.isFromCustomer360orWidgets()){
                	if(cta.getType().trim().equalsIgnoreCase(expViewCta.getType()) &&
                            cta.getPriority().trim().equalsIgnoreCase(expViewCta.getPriority()) &&
                            cta.getStatus().trim().equalsIgnoreCase(expViewCta.getStatus()) &&
                            cta.getReason().trim().equalsIgnoreCase(expViewCta.getReason())) 
                    		return true;               	
                }
                else {
                    Log.info("Waiting for Event Details to Load");
                    Timer.sleep(2);
                }
            } catch (Exception e) {
                Log.info("Trying Again to check card loaded successfully.");
            }
        }
        Log.info("CTA expand mode is not loaded properly.");
        return false;
    }

    private String getCTAXPath(CTA cta) {
        Log.info("Generating CTA XPath");
        String xPath = "//div[@class='gs-cta']";
        xPath = xPath+"/descendant::div[@class='title-ctn pull-left']";
        xPath =(cta.isClosed() && !cta.getStatus().equals("Closed Lost")) ? xPath+"/span[@class='check-data ctaCheckBox require-tooltip active']" :
                xPath+"/span[@class='check-data ctaCheckBox require-tooltip']";
        xPath = cta.isImp() ? xPath+"/following-sibling::span[@class='glyphicon glyphicon-bookmark cta-flag require-tooltip' and contains(@style, 'color:#fc8744')]" :
                xPath+"/following-sibling::span[@class='glyphicon glyphicon-bookmark cta-flag require-tooltip']";
        if(cta.getPriority() != null) {
            xPath = xPath+"/following-sibling::span[@class='wf-priority cta-priority' and contains(text(), '"+cta.getPriority().substring(0, 1)+"')]";
        } else {
            throw new RuntimeException("Priority should be specified.") ;
        }

        xPath = cta.isClosed() ?xPath+"/following-sibling::span[@class='title-name workflow-cta-title' and contains(text(), '"+cta.getSubject()+"') and contains(@style,'text-decoration: line-through;')]":
        				xPath+"/following-sibling::span[@class='title-name workflow-cta-title' and contains(text(), '"+cta.getSubject()+"')]" ;
        				
        xPath = xPath+"/ancestor::div[@class='pull-left']/div[@class='wf-account pull-left']";
       if(!cta.isFromCustomer360orWidgets()) {
    	   xPath = xPath+"/descendant::span[contains(text(), '"+cta.getCustomer()+"')]";
       
        xPath = xPath+"/ancestor::div[@class='pull-left']/div[@class='pull-left cta-score']";
        
        for(CTA.Attribute attribute : cta.getAttributes()) {
            if(attribute.isInSummary()) {
                xPath = xPath+"/descendant::span[@title='"+attribute.getAttLabel()+"' and contains(text(), '"+attribute.getAttValue()+"')]";
                xPath = xPath+"/ancestor::div[@class='pull-left cta-score']";
             }
        	}
       }

        xPath = xPath+"/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        xPath = xPath+"/descendant::div[@class='pull-right relative']";
        xPath = xPath+"/descendant::span[@class='task-no' and contains(text(), '"+cta.getTaskCount()+"')]";
        xPath = xPath+"/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        String color = "";
        if(cta.getType() != null) {
            color = cta.getType().equals("Risk") ? "#f45655" : cta.getType().equals("Event") ? "#f0ac41" : "#42b899";
        } else {
            throw  new RuntimeException("CTA Type is mandatory");
        }
        xPath = xPath+"/descendant::span[@class='cta-duedate'  and contains(@style, 'background:"+color+"') and contains(text(), '"+cta.getDueDate()+"')]/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        if(cta.getAssignee() != null) {
            xPath = xPath+"/descendant::img[contains(@alt, '"+cta.getAssignee()+"')]";
            xPath = xPath+"/ancestor::div[@class='gs-cta']";
        } else {
            throw new RuntimeException("Assignee should be specified.");
        }
        Log.info("CTA Path : " + xPath);
        return xPath;
    }

    private static String getTaskXPath(Task task) {
        Log.info("Generating Task XPath");
        String xPath = "//div[@class='gs-cta-item']";
        if(task.getStatus().equalsIgnoreCase("Open")) {
            xPath = xPath+"/descendant::div[@class='title-ctn pull-left']/span[@class='check-data taskCheckBox require-tooltip']" +
                    "/following-sibling::span[@class='wf-priority' and contains(text(), '"+task.getPriority().substring(0,1)+"')]" +
                    "/following-sibling::span[contains(@class, 'workflow-task-title') and contains(text(), '"+task.getSubject().trim()+"')]";
        } else {
            xPath = xPath+"/descendant::div[@class='title-ctn pull-left']/span[@class='check-data taskCheckBox require-tooltip active']" +
                    "/following-sibling::span[@class='wf-priority' and contains(text(), '"+task.getPriority().substring(0,1)+"')]" +
                    "/following-sibling::span[contains(@class, 'workflow-task-title') and contains(@style, 'line-through;') and contains(text(), '"+task.getSubject().trim()+"')]";
        }
        xPath = xPath+"/ancestor::div[@class='gs-cta-item']";
        xPath = xPath+"/descendant::div[contains(@class, 'pull-right')]/div[@class='date']/span[contains(text(), '"+task.getDate()+"')]";
        if(task.getAssignee() != null) {
            xPath = xPath+"/ancestor::div[contains(@class, 'pull-right')]/div[contains(@class, 'task-assignee')]/img[contains(@alt, '"+task.getAssignee()+"')] ";
        } else {
            throw new RuntimeException("Task Owner is Mandatory");
        }
        xPath=xPath+"/ancestor::div[@class='gs-cta-item']";
        System.out.println("Task XPath : " + xPath);
        return xPath;
    }

    public boolean isTaskDisplayed(Task task) {
        env.setTimeout(3);
        List<WebElement> elements = element.getAllElement(getTaskXPath(task));
        if(elements.size() > 0) {
            for(WebElement ele : elements) {
                if(ele.isDisplayed()) {
                    return true;
                }
            }
        }
        Log.info("Task is not displayed");
        env.setTimeout(30);
        return false;
    }

    public WorkflowPage showCTATasks(CTA cta) {
        String eventXPath = getCTAXPath(cta);
        item.click(eventXPath+"/descendant::div[contains(@class, 'workflow-taskscnt')]");
        String eventTaskBody = eventXPath+"descendant::div[@class='gs-cta-body']";
        wait.waitTillElementDisplayed(eventTaskBody, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage closeCTA(CTA cta, boolean hasOpenTasks) {
        String xPath = getCTAXPath(cta)+"/descendant::span[@class='check-data ctaCheckBox require-tooltip']";
        item.click(xPath);
        if(hasOpenTasks) {
            wait.waitTillElementDisplayed(SAVE_ACTION, MIN_TIME, MAX_TIME);
            item.click(SAVE_ACTION);
        }
        Timer.sleep(2);
        return this;
    }

    public WorkflowPage openCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks) {
        String xPath = getCTAXPath(cta)+"/descendant::span[@class='check-data ctaCheckBox require-tooltip active']";
        item.click(xPath);
        Timer.sleep(2);
        if(hasTasks){
        	for(Task task : tasks){
        		openORCloseTask(task);
        	}
        }
        return this;
    }

    public WorkflowPage openORCloseTask(Task task) {
        String xPath = getTaskXPath(task)+"/descendant::span[contains(@class, 'check-data taskCheckBox')]";
        item.click(xPath);
        Timer.sleep(2);
        return this;
    }

    public WorkflowPage flagCTA(CTA cta) {
        String xPath = getCTAXPath(cta)+"/descendant::span[contains(@class, 'glyphicon glyphicon-bookmark cta-flag')]";
        item.click(xPath);
        Timer.sleep(2);
        if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        else waitTillNoLoadingIcon_360();
        cta.setImp(true);
        return this;
    }

    public boolean verifyClosedCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks){
    	if(!hasTasks){
            return verifyCTADetails(cta);
        } else{
    		item.click(VIEW_TASKS);
            if(!verifyCTADetails(cta)) {
                return false;
            }
    		for(Task task :tasks ) {
                if(!isTaskDisplayed(task)) {
                    return false;
                }
                if(!verifyTaskDetails(task)) {
                    return false;
                }
            }
            return true;
    	}
    }
    
    public WorkflowPage updateTaskDetails(Task ExpectedTask, Task newTask) {
        expandTaskView(ExpectedTask);
        if(!ExpectedTask.getAssignee().equalsIgnoreCase(newTask.getAssignee())) {
            boolean status = false;
            wait.waitTillElementDisplayed(TASK_EXP_ASSIGNEE, MIN_TIME, MAX_TIME);
            item.click(TASK_EXP_ASSIGNEE);
            field.setTextByKeys(TASK_EXP_ASSIGNEE_SEARCH, newTask.getAssignee());
            waitTillNoLoadingIcon();
            wait.waitTillElementDisplayed(String.format(TASK_EXP_ASSIGNEE_SELECT, newTask.getAssignee()), MIN_TIME, MAX_TIME);
            for(WebElement ele : element.getAllElement(String.format(TASK_EXP_ASSIGNEE_SELECT, newTask.getAssignee()))) {
                if(ele.isDisplayed()) {
                    ele.click();
                    status = true;
                    break;
                }
            }
            if(!status) {
                throw new RuntimeException("Failed to change the assignee");
            }

            item.click(String.format(TASK_EXP_ASSIGNEE_SELECT, newTask.getAssignee()));
        }
        if(newTask.getDate() != null && !newTask.getDate().equalsIgnoreCase(ExpectedTask.getDate())) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String a = "j$('#"+TASK_DUE_DATE+"').val(\""+newTask.getDate()+"\").trigger(\"change\")" ;
            js.executeScript(a);
        }
        if(newTask.getSubject() !=null) {
            try {
                item.click(TASK_EXP_SUBJECT);
                item.clearText(TASK_EXP_SUBJECT);
                new Actions(driver).moveToElement(element.getElement(TASK_EXP_SUBJECT)).build().perform();
                field.setText(TASK_EXP_SUBJECT, newTask.getSubject());
                new Actions(driver).moveToElement(element.getElement(TASK_EXP_SUBJECT)).sendKeys(Keys.ENTER).build().perform();

                WebElement elements = element.getElement(TASK_EXP_SUBJECT);
                JavascriptLibrary javascript = new JavascriptLibrary();
                javascript.callEmbeddedSelenium(driver, "triggerEvent", elements, "blur");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if(newTask.getPriority() != null) {
            item.click(TASK_EXP_PRIORITY);
            selectValueInDropDown(newTask.getPriority());
        }
        if(newTask.getStatus() !=null) {
            item.click(TASK_EXP_STATUS);
            selectValueInDropDown(newTask.getStatus());
        }
        Timer.sleep(2);
        return this;
    }
    
    
    public WorkflowPage deleteTask(Task task) {
        expandTaskView(task);
        item.click(TASK_EXP_MORE_OPTIONS);
        wait.waitTillElementDisplayed(TASK_EXP_DELETE_OPTION, MIN_TIME, MAX_TIME);
        item.click(TASK_EXP_DELETE_OPTION);
        wait.waitTillElementDisplayed(DELETE_ACTION, MIN_TIME, MAX_TIME);
        item.click(DELETE_ACTION);
        Timer.sleep(2);
        if(!task.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        else waitTillNoLoadingIcon_360();
        return this;
    }

    public WorkflowPage deleteCTA(CTA cta) {
        expandCTAView(cta);
        item.click(EXP_VIEW_CTA_MORE_OPTIONS);
        item.click(String.format(EXP_VIEW_DELETE_CTA, cta.getType()));
        item.click(DELETE_ACTION);
        Timer.sleep(2);
        if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        else waitTillNoLoadingIcon_360();
        return this;
    }

    public WorkflowPage changeAssigneeView(String assignee) {
        item.click(OWNER);
        Timer.sleep(2);
        wait.waitTillElementDisplayed(OWNER_SEARCH, MIN_TIME, MAX_TIME);
        if(assignee != null) {
            boolean status = false;
            field.clearAndSetText(OWNER_SEARCH, assignee);
           // waitTillNoLoadingIcon();
            for(WebElement ele : element.getAllElement(String.format(OWNER_SELECT, assignee))){
                if(ele.isDisplayed()) {
                    ele.click();
                    status = true;
                    break;
                }
            }
            if(!status) {
                throw new RuntimeException("Failed to Change Assignee View ");
            }
        } else {
            item.click(OWNER_ALL);
        }
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    public WorkflowPage applyPlayBook(CTA cta, String playBookName, List<Task> tasks,boolean isApply) {
        expandCTAView(cta);
        item.click(EXP_VIEW_CTA_MORE_OPTIONS);
        if(isApply) item.click(EXP_VIEW_APPLY_PLAYBOOK);
        else item.click(EXP_VIEW_REPLACE_PLAYBOOK);
        Timer.sleep(2);
        if(!cta.isFromCustomer360orWidgets()) waitTillNoLoadingIcon();
        else waitTillNoLoadingIcon_360();
        item.click(PLAYBOOK_SELECT);
        selectValueInDropDown(playBookName);
        if(!cta.isFromCustomer360orWidgets())waitTillNoLoadingIcon();
        else waitTillNoLoadingIcon_360();
        applyOwnersToTasksInPlaybook(tasks);
        if(isApply) {
            item.click(PLAYBOOK_APPLY);
            cta.setTaskCount(tasks.size()+cta.getTaskCount());
        } else {
            item.click(PLAYBOOK_REPLACE);
        }
        Timer.sleep(5);
        return this;
    }

    private void applyOwnersToTasksInPlaybook(List<Task> tasks) {
        for(Task task : tasks) {
            String path = "//h4[contains(text(), '"+task.getSubject().replace("\\\"", "'")+"')]" +
                    "/ancestor::div[contains(@class, 'playbook-task')]/descendant::input[@name='search_text']";
            field.clearAndSetText(path, task.getAssignee());
            driver.findElement(By.xpath(path)).sendKeys(Keys.ENTER);
            //
            if(!task.isFromCustomer360orWidgets())  waitTillNoLoadingIcon();
            else waitTillNoLoadingIcon_360();
            //wait.waitTillElementDisplayed("//li[@class='ui-menu-item' and @role='presentation']/a[contains(text(), '"+task.getAssignee()+"')]", MIN_TIME, MAX_TIME);
            boolean selected = false;
            for(WebElement ele : element.getAllElement("//li[@class='ui-menu-item' and @role='presentation']/a[contains(text(), '"+task.getAssignee()+"')]")) {
                if(ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                }
            }
            if(!selected) {
                throw new RuntimeException("Unable to select owner");
            }
        }
    }

	public WorkflowPage syncTasksToSF(CTA cta,Task task) {
		//item.click(VIEW_TASKS);
		item.click(String.format(DESYNCED_ICON, task.getSubject()));
		wait.waitTillElementDisplayed(SYNCED_ICON, MIN_TIME, MAX_TIME);
		return this;
	}
	
	public WorkflowPage deSyncTaskFromSF(CTA cta,Task task, boolean keepInSF){
		item.click(SYNCED_ICON);
		if(keepInSF) item.click(DESYNC_BUT_KEEP_INSF);
		else item.click(DESYNC_AND_DELETE);
		wait.waitTillElementDisplayed(DESYNCED_ICON, MIN_TIME, MAX_TIME);
		return this;	
	}
	
    public WorkflowPage showClosedCTA() {
    	if(element.isElementPresent(HIDE_CLOSED_CTA)) return this;
    	else{
        item.click(SHOW_CLOSED_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(HIDE_CLOSED_CTA, MIN_TIME, MAX_TIME );
        return this;
    	}
    }

    public WorkflowPage hideClosedCTA() {
        item.click(HIDE_CLOSED_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(SHOW_CLOSED_CTA, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage showFlaggedCTA() {
        item.click(SHOW_IMP_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(HIDE_FLAG_CTA, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage disabledFlaggedCTAView() {
        item.click(HIDE_FLAG_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(SHOW_IMP_CTA, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage showSnoozeCTA() {
        item.click(SHOW_SNOOZE_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(HIDE_SNOOZE_CTA, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage hideSnoozeCTA() {
        item.click(HIDE_SNOOZE_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(SHOW_SNOOZE_CTA, MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage selectCTATypeFilter(String type) {
        item.click(String.format(TYPE_CTA, type));
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(String.format(TYPE_CTA_ACTIVE, type), MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage unSelectCTATypeFilter( String type) {
        item.click(String.format(TYPE_CTA_ACTIVE, type));
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(String.format(TYPE_CTA, type), MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage selectCTAPriorityFilter(String priority) {
        item.click(String.format(PRIORITY_CTA, priority));
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(String.format(PRIORITY_CTA_ACTIVE, priority), MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage unSelectCTAPriorityFilter(String priority) {
        item.click(String.format(PRIORITY_CTA_ACTIVE, priority));
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(String.format(PRIORITY_CTA, priority), MIN_TIME, MAX_TIME);
        return this;
    }

    public WorkflowPage selectGroupBy(String val) {
        item.click(GROUP_BY);
        selectValueInDropDown(val);
        waitTillNoSearchIcon();
        return this;
    }

    public WorkflowPage selectSortBy(String val) {
        item.click(SORT_BY);
        selectValueInDropDown(val);
        waitTillNoSearchIcon();
        return this;
    }
    
    public boolean  isOverDueCTADisplayed(CTA cta) {
        String xPath = getCTAXPath(cta)+"/descendant::div[contains(@class,'cta-overdue widget-alert')]";
        item.click(xPath);
        return  isCTAExpandedViewLoaded(cta);
    }

    public int countOfCTASInGroup(String groupName, CTA cta) {
        int count;
        if(groupName == null && cta ==null)
            throw new RuntimeException("Either groupName (or) cta is mandatory");
        if(groupName !=null && groupName.equalsIgnoreCase("Opportunity")) {
            groupName =  "Opportunities";
        }
        if(cta != null && groupName != null) {
            String xPath = getCTAXPath(cta)+"/ancestor::div[@class='gs-wf-group']/div[@class='gs-wf-group-head']" +
                    "/a[contains(text(), '"+groupName+"')]/span[contains(@class, 'grouped-ctas-count')]";
            Log.info("Group Xpath :" +xPath);
            count = Integer.valueOf(element.getText(xPath).trim());
            Log.info("No of CTA's in Group :" +count);
            return count;
        } else if(groupName != null){
            String xPath = "//div[@class='gs-wf-group-head']/a[contains(text(), '"+groupName+"')]/span[contains(@class, 'grouped-ctas-count')]" ;
            Log.info("Group Xpath :" +xPath);
            count = Integer.valueOf(element.getText(xPath).trim());
            Log.info("No of CTA's in Group :" +count);
            return count;
        } else {
            String xPath = getCTAXPath(cta)+"/ancestor::div[@class='gs-wf-group']/div[@class='gs-wf-group-head']" +
                    "/a/span[contains(@class, 'grouped-ctas-count')]";
            Log.info("Group Xpath :" +xPath);
            count = Integer.valueOf(element.getText(xPath).trim());
            Log.info("No of CTA in Group :" +count);
            return count;
        }
    }

    public boolean isCTADisplayedInGroup(String groupName, CTA cta) {
        boolean status = false;
        try {
            if(cta != null && groupName != null) {
                if(groupName.equalsIgnoreCase("Opportunity")) {
                    groupName =  "Opportunities";
                }
                String xPath = getCTAXPath(cta)+"/ancestor::div[@class='gs-wf-group']/div[@class='gs-wf-group-head']" +
                        "/a[contains(text(), '"+groupName+"')]";
                Log.info("Group Xpath :" +xPath);
                status = element.getElement(xPath).isDisplayed();
                Log.info("CTA Display Status " +status);
                return status;
            } else {
                throw new RuntimeException("Both CTA, GroupName are mandatory");
            }
        } catch (Exception e) {
            Log.info(e.getLocalizedMessage());
            Log.info("CTA is not displayed");
            e.printStackTrace();
        }
        return status;
    }

    /**
     *
     * @param cta
     * @param task
     * @return
     */
    public boolean isTaskDisplayedUnderCTA(CTA cta, Task task) {
        boolean status = false;

        String ctaXpath = getCTAXPath(cta);
        String taskXpath = getTaskXPath(task);
        String xpath = ctaXpath+"/div[@class='gs-cta-body']"+getTaskXPath(task);
        Log.info("Xpath of CTA, TASK : " +cta);
        try {
            if(element.getElement(ctaXpath).isDisplayed()) {
                status = element.getElement(xpath).isDisplayed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            Log.info("CTA (Or) Task is not displayed");
        }
        return status;
    }

    /**
     *
     * @param view - DAILY, WEEKLY, MONTHLY.
     * @return
     */
    public WorkflowPage selectCalendarView(String view) {
        item.click(String.format(CALENDER_VIEW_SELECT, view));
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    /**
     *
     * @param month - Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
     * @param year - 2014, 2015, 2016.....
     * @return
     */
    public WorkflowPage selectCalendarMonth(String month, int year) {
        String xPath = "//div[@class='calendar-content cal-row']/descendant::div[@class='month' and starts-with(text(), '"+month+"') and contains(text(), '"+year+"')]";
        Log.info("Selecting month : " +xPath);
        item.click(xPath);
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    /**
     *
     * @param day - 1,2,3....
     * @param month - Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
     * @param weekDay - Mon, Tue, Wed, Thu, Fri, Sat, Sun
     * @return
     */
    public WorkflowPage selectCalendarDay(int day, String month, String weekDay) {
        String xPath = "//div[@class='calendar-content cal-row']/descendant::div[@class='cell-lbl wf-date' and contains(text(), '"+day+"')]" +
                "/following-sibling::div[@class='wf-week-month']/span[contains(text(), '"+month.toUpperCase()+"')]" +
                "/following-sibling::span[contains(text(), '"+weekDay.toUpperCase()+"')]";
        Log.info("Selecting day : " +xPath);
        item.click(xPath);
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    /**
     *
     * @param day - 1,2,3...
     * @param week - 1,2,3...
     * @param month - Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
     * @return
     */
    public WorkflowPage selectCalendarWeek(int day, int week, String month) {
        String xPath = "//div[@class='calendar-content cal-row']/descendant::div[@class='wk' and contains(text(), 'Week "+week+"')]" +
                "/following-sibling::div[@class='wk-month']/span[contains(text(), '"+month+"') and contains(text(), '"+day+"')]";
        Log.info("Selecting week : " +xPath);
        item.click(xPath);
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    public WorkflowPage clickOnCalenderArrow(boolean right) {
        if(right) {
            item.click(CALENDER_DIR_RIGHT);
        } else {
            item.click(CALENDER_DIR_LEFT);
        }
        Timer.sleep(2);
        return this;
    }




}
