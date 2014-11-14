package com.gainsight.sfdc.workflow.pages;


import com.gainsight.sfdc.workflow.pojos.Task;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.commands.SelectFrame;
import com.sforce.soap.metadata.Workflow;
import com.sforce.soap.partner.sobject.SObject;

import org.openqa.selenium.By;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkflowPage extends WorkflowBasePage {

	private final String READY_INDICATOR  = "//div[@title='Add CTA']";
    private final String CALENDAR_VIEW_READY_INDICATOR = "//ul[@class='calendar-tab']";
    private final String LOADING_ICON       = "//div[contains(@class, 'gs-loader-image')]";
    private final String SEARCH_LOADING     = "//div[@class='base_filter_search_progress_icon']";

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

    private final String OWNER              = "//div[@class='wf-owner-search']";
    private final String OWNER_SEARCH       = "//div[@class='wf-dd-profile']/descendant::input[@type='text' and @name='search_text']";
    private final String OWNER_SELECT       = "//div[@class='wf-dd-profile']/descendant::label[contains(text(), '%s')]";
    private final String OWNER_ALL          = "//div[@class='wf-dd-profile']/descendant::a[@id='All']";
    private final String GROUP_BY           = "//select[@class='form-control cta-group-by']/following-sibling::button";
    private final String SORT_BY            = "//select[@class='form-control cta-sort-by']/following-sibling::button";
    private final String FILTER             = "//a[@class='dashboard-filter-btn']";

    //Popup Page Elements
    private final String OK_ACTION          = "//input[@data-action='Ok' and contains(@class, 'btn_save')]";
    private final String SAVE_ACTION        = "//input[@data-action='Yes' and contains(@class, 'btn_save')]";
    private final String CANCEL_ACTION      = "//input[@data-action='Cancel' and contains(@class, 'btn_cancel')]";
    private final String DELETE_ACTION      = "//input[@data-action='Delete' and contains(@class, 'btn_save')]";

    //CTA Form Page Elements
    private final String CREATE_CTA_ICON    = "//a[@class='dashboard-addcta-btn more-options cta-create-btn']";
    private final String CREATE_RISK_LINK   = "//a[@data-action='RISK']";
    private final String CREATE_OPPOR_LINK  = "//a[@data-action='OPPORTUNITY']";
    private final String CREATE_EVENT_LINK  = "//a[@data-action='EVENT']";
    private final String RISK_CTA_FORM_TITLE    = "//span[text()='Add Risk']";
    private final String OPPO_CTA_FORM_TITLE    = "//span[text()='Add Opportunity']";
    private final String EVENT_CTA_FORM_TITLE   = "//span[text()='Add Event']";
    private final String CREATE_FORM_SUBJECT    = "//input[@class='form-control cta-subject']";
    private final String CREATE_FORM_CUSTOMER   = "//input[@class='form-control strcustomer ui-autocomplete-input']";
	private final String CREATE_FORM_REASON     = "//div[@class='col-md-9']/button/span[@class='ui-icon ui-icon-triangle-2-n-s']";// "//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']/span[@class='ui-icon ui-icon-triangle-2-n-s']";
	private final String CREATE_FORM_SELECT_REASON  = "//ul/li/label/span[text()='%s']";
	private final String CREATE_FORM_DUE_DATE       = "//input[@class='form-control cta-dateCtrl']";
	private final String CREATE_FORM_COMMENTS       = "//textarea[@class='form-control strdescription']";
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
    private final String RECUR_WEEK_NUMBER_OF_MONTH     = "//label[@class='ui-corner-all']/input[@value='%s']/following-sibling::span";
    private final String TO_SELECT_RECUR_WEEK_OF_MONTH  = "//select[@class='form-control1 monthly-onthe-daypick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_WEEK_OF_MONTH            = "//li[contains(@class,'ui-multiselect-option')]//span[contains(text(),'%s')]";
    private final String RECUR_MONTHLY_INTERVAL_BYWEEK  = "//div[@class='date-float']/input[@class='form-control width40 text-center monthly-onthe-ofevery-monthpick']";
  
    private final String RECUR_YEARLY_BYMONTH_TO_SELECT_MONTH   = "//select[@class='form-control1 yearly-onevery-monthpick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_YEARLY_BYMONTH                   = "//input[@value='%s']/following-sibling::span";
    private final String RECUR_YEARLY_BYMONTH_TO_SELECT_DATE    = "//select[@class='form-control1 yearly-onevery-daynumpick']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_YEARLY_BYMONTH_DATE              = "//input[@value='2']/following-sibling::span[contains(text(),'2')]";
    private final String RECUR_YEARLY_STARTDATE_SELECT          = "//select[@class='sel-start-year']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_YEARLY_STARTDATE                 = "//span[contains(text(),'%s')]";
    private final String RECUR_YEARLY_ENDDATE_SELECT            = "//select[@class='sel-end-year']/following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
    private final String RECUR_YEARLY_ENDDATE                   = "//span[contains(text(),'%s')]";
    private enum WEEKDAY{Sun,Mon,Tue,Wed,Thu,Fri,Sat};

    //CTA Expanded View Elements
    private final String EXP_VIEW_HEADER ="//div[@class='wf-details-header']";
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
    private final String EXP_VIEW_SET_SNOOZE_DATE = "//input[@class='form-control cta-snooze-input']";
	private final String EXP_VIEW_SNOOZE_REASON_BUTTON = "//select[@class='gs-snooze-reason cockpit-multiselect']/following-sibling::button";
    private final String EXP_VIEW_MILESTONE             = "//ul[@class='panal-tools']/descendant::a[contains(@class, 'landmark')]";
    private final String EXP_VIEW_ASSIGNEE              = "//div[@class='workflow-cta-details']/descendant::div[@class='wf-owner-search']";
    private final String EXP_VIEW_ASSIGNEE_SEARCH_INPUT = "//div[@class='wf-details-header']/descendant::input[@name='search_text']";
    private final String EXP_VIEW_ASSIGNEE_SELECT       = "//div[@class='wf-details-header']/descendant::div[@class='wf-dropdown-menu']/descendant::label[contains(text(), '%s')]";
    private final String CTA_EXP_SLIDE_ICON             = "//div[@class='cta-detail-set']//div[@class='slide-icon']";
    
    private final String VIEW_TASKS="//div[contains(@class,'task require-tooltip workflow-taskscnt  task-hyper')]";
    
    //Task Expanded View Elements
    private final String TASK_EXP_ASSIGNEE          = "//div[@class='wf-details-header']/descendant::label[@class='task-username']";
    private final String TASK_EXP_ASSIGNEE_SEARCH   = "//div[@class='task-detail-set']/descendant::input[@name='search_text']";
    private final String TASK_EXP_ASSIGNEE_SELECT   = "//div[@class='task-detail-set']/descendant::div[@class='wf-dropdown-menu']/descendant::label[contains(text(), '%s')]";
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
    private final String SELECT_MORE_OPTIONS		="//a[@class='more-edit more-options']";
    private final String ADD_NEW_TASK					="//a[@data-action='ADD_TASK']/span[@class='add']";
    private final String ADD_TASK_POPUP_TITLE	="//span[text()='Add Task']";
    private final String ADD_ASSIGNEE_TO_TASK	="//input[@class='search_input form-control ui-autocomplete-input']";
    private final String SELECT_ASSIGNEE_FOR_TASK="//div[@class='gs_searchform']/descendant::label[contains(text(),'%s')]";
    private final String ADD_SUBJECT_TO_TASK		="//input[@class='Subject__c form-control']";
    private final String ADD_DATE_TO_TASK			="//input[@class='Date__c form-control gs-calendar']";
    private final String TO_SELECT_PRIORITY_IN_TASK    		= "//select[@class='Priority__c form-control form-select']/following-sibling::button";
    private final String TO_SELECT_STATUS_IN_TASK	 = "//select[@class='Status__c form-control form-select']/following-sibling::button";
    private final String SAVE_TASK		="//div[@class='taskForm']//button[contains(text(),'Save')]";
    private final String TASK_TITLE_TO_VERIFY		="//span[@class='title-name workflow-task-title' and contains(text(),'%s')]";
    
    //Playbook From Elements
    private final String PLAYBOOK_SELECT = "//select[@class='playbook-list']/following-sibling::button";
    private final String PLAYBOOK_APPLY  = "//button[contains(@class, 'btn-save') and text()='Apply']";
    private final String PLAYBOOK_CANCEL = "//button[contains(@class, 'btn-cancel') and text()='Cancel']";


    public WorkflowPage() {
        waitForPageLoad();
    }

    public WorkflowPage(String view) {
        waitForPageLoad();
        wait.waitTillElementDisplayed(CALENDAR_VIEW_READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    private void waitForPageLoad() {
        Report.logInfo("Loading Cockpit Page");
        waitTillNoLoadingIcon();
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        Report.logInfo("Cockpit Page Loaded Successfully");
    }

    private void waitTillNoLoadingIcon() {
        env.setTimeout(1);
        wait.waitTillElementNotPresent(LOADING_ICON, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

    private void waitTillNoSearchIcon() {
        env.setTimeout(1);
        wait.waitTillElementNotDisplayed(SEARCH_LOADING, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

    public void createCTA(CTA cta){
    	Report.logInfo("Adding CTA of Type - RISK");
    	item.click(CREATE_CTA_ICON);
    	if(cta.getType().equals("Risk"))
    	{
    		item.click(CREATE_RISK_LINK);
    		wait.waitTillElementDisplayed(RISK_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);

    	}
    	if(cta.getType().equals("Opportunity")){
    		item.click(CREATE_OPPOR_LINK);
    		wait.waitTillElementDisplayed(OPPO_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);

    	}
    	if(cta.getType().equals("Event")){
    		item.click(CREATE_EVENT_LINK);
    		wait.waitTillElementDisplayed(EVENT_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
    		fillAndSaveCTAForm(cta);

    	}
	}
    
    private void fillAndSaveCTAForm(CTA cta) {
		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		setCustomer(cta.getCustomer());
		item.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON,cta.getReason()));
		field.setText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		if(cta.isRecurring()) fillAndSaveRecurringEventCTAForm(cta);
		else		button.click(SAVE_CTA);
	}

	private void fillAndSaveRecurringEventCTAForm(CTA cta) {

		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		setCustomer(cta.getCustomer());
		button.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON,cta.getReason()));
		field.setText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		if(cta.isRecurring()){
		field.selectCheckbox(CREATE_RECURRING_EVENT);
		CTA.EventRecurring recurProperties=cta.getEventRecurring();
			if(recurProperties.getRecurringType().equals("Daily")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				if(!recurProperties.getDailyRecurringInterval().equals("EveryWeekday")){
					item.click(CREATE_RECUR_EVERYnDAYS);
					field.clearAndSetText(CREATE_RECUR_DAILY_INTERVAL,recurProperties.getDailyRecurringInterval());
				}
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
			}
			else if (recurProperties.getRecurringType().equals("Weekly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				field.clearAndSetText(RECUR_WEEK_COUNT, recurProperties.getWeeklyRecurringInterval().split("_")[1]);
				field.selectCheckBox(String.format(RECUR_WEEKDAY,WEEKDAY.valueOf(recurProperties.getWeeklyRecurringInterval().split("_")[2]).ordinal()+1));
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
			}
			else if(recurProperties.getRecurringType().equals("Monthly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				if(recurProperties.getMonthlyRecurringInterval().startsWith("Day")){
				item.click(TO_SELECT_RECUR_DAY_OF_MONTH);
				item.click(String.format(RECUR_DAY_OF_MONTH, recurProperties.getMonthlyRecurringInterval().split("_")[1]));
				field.clearAndSetText(RECUR_MONTH_INTERVAL, recurProperties.getMonthlyRecurringInterval().split("_")[3]);
				}
				else if(recurProperties.getMonthlyRecurringInterval().startsWith("Week")){
					item.click(RECUR_MONTHLY_BY_WEEKDAY);
					item.click(TO_SELECT_WEEK_NUMBER);
					item.click(String.format(RECUR_WEEK_NUMBER_OF_MONTH, recurProperties.getMonthlyRecurringInterval().split("_")[1]));
					item.click(TO_SELECT_RECUR_WEEK_OF_MONTH);
					item.click(String.format(RECUR_WEEK_OF_MONTH,recurProperties.getMonthlyRecurringInterval().split("_")[2]));
					item.clearAndSetText(RECUR_MONTHLY_INTERVAL_BYWEEK,recurProperties.getMonthlyRecurringInterval().split("_")[4]);
				}
				item.setText(RECUR_EVENT_START_DATE, recurProperties.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, recurProperties.getRecurEndDate());
				
			}
			else if(recurProperties.getRecurringType().equals("Yearly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,recurProperties.getRecurringType()));
				if (recurProperties.getYearlyRecurringInterval().startsWith("Week")){
					
				}
				else{
					item.click(RECUR_YEARLY_BYMONTH_TO_SELECT_MONTH);
					for(WebElement ele : driver.findElements(By.xpath(String.format(RECUR_YEARLY_BYMONTH, recurProperties.getYearlyRecurringInterval().split("_")[0]))))
					 if(ele.isDisplayed()) ele.click();
					
					item.click(RECUR_YEARLY_BYMONTH_TO_SELECT_DATE);
					item.click(String.format(RECUR_YEARLY_BYMONTH_DATE,recurProperties.getYearlyRecurringInterval().split("_")[1]));
				}
				item.click(RECUR_YEARLY_STARTDATE_SELECT);
				item.click(String.format(RECUR_YEARLY_STARTDATE, recurProperties.getRecurStartDate()));
				
				item.click(RECUR_YEARLY_ENDDATE_SELECT);
				item.click(String.format(RECUR_YEARLY_ENDDATE, recurProperties.getRecurEndDate()));
					
				}
		}
		button.click(SAVE_CTA);
        amtDateUtil.stalePause(); //In - Case, Should add wait logic here.
		}

	public void addTaskToCTA(CTA cta,List<Task> tasks){
		expandCTAView(cta);
		for(Task task : tasks){
			
			item.click(SELECT_MORE_OPTIONS);
			item.click(ADD_NEW_TASK);
			wait.waitTillElementDisplayed(ADD_TASK_POPUP_TITLE, MIN_TIME, MAX_TIME);
			item.clearAndSetText(ADD_ASSIGNEE_TO_TASK, task.getAssignee());
			driver.findElement(By.xpath(ADD_ASSIGNEE_TO_TASK)).sendKeys(Keys.ENTER);
			waitTillNoLoadingIcon();
			System.out.println("clicking on ....."+String.format(SELECT_ASSIGNEE_FOR_TASK,task.getAssignee()));
			//item.click(String.format(SELECT_ASSIGNEE_FOR_TASK,task.getAssignee()));
			   boolean selected = false;
	            for(WebElement ele : element.getAllElement("//li[@class='ui-menu-item']/a/label[contains(text(), '"+task.getAssignee()+"')]")) {
	                if(ele.isDisplayed()) {
	                    ele.click();
	                    selected = true;
	                }
	            }
	            if(!selected) {
	                throw new RuntimeException("Unable to select owner");
	            }
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
		}
	}
    private void setCustomer(String custName) {
        field.setText(CREATE_FORM_CUSTOMER, custName);
        amtDateUtil.stalePause();
        driver.findElement(By.xpath("//li[@class='ui-menu-item']/a/label[contains(text(),'"+custName+"')]")).click();
    }
    
    public void createMilestoneForCTA(CTA cta){
    	expandCTAView(cta);
    	item.click(EXP_VIEW_MILESTONE);
    }
    
    public void snoozeCTA(CTA cta){
    	expandCTAView(cta);
    	item.click(EXP_VIEW_SNOOZE);
    	//
    	item.clearAndSetText(EXP_VIEW_SET_SNOOZE_DATE, cta.getSnoozeDate());
    	item.click(EXP_VIEW_SNOOZE_REASON_BUTTON);
    	selectValueInDropDown(cta.getSnoozeReason());
    	item.click(EXP_VIEW_HEADER); //click somewhere else
        amtDateUtil.stalePause();
    }
    
    public boolean verifySnoozeCTA(CTA cta){
    	item.click(SHOW_SNOOZE_CTA);
    	if(isCTADisplayed(cta)) return true;
    	else return false;
    }

    public WorkflowPage updateCTADetails(CTA oldCta, CTA newCta) {
        expandCTAView(oldCta);
        if(!oldCta.getAssignee().equalsIgnoreCase(newCta.getAssignee())) {
            item.click(EXP_VIEW_ASSIGNEE);
            amtDateUtil.stalePause();
            field.clearAndSetText(EXP_VIEW_ASSIGNEE_SEARCH_INPUT, newCta.getAssignee());
            driver.findElement(By.xpath(EXP_VIEW_ASSIGNEE_SEARCH_INPUT)).sendKeys(Keys.ENTER);
            waitTillNoLoadingIcon();
            item.click(String.format(EXP_VIEW_ASSIGNEE_SELECT, newCta.getAssignee()));
        }
        if(newCta.getDueDate() != null && !newCta.getDueDate().equalsIgnoreCase(oldCta.getDueDate())) {
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
        amtDateUtil.stalePause();
        collapseCTAView();
        return this;
    }

    public void selectValueInDropDown(String value) {
        boolean selected = false;
        for(WebElement ele : element.getAllElement("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]")) {
            Report.logInfo("Checking : "+ele.isDisplayed());
            if(ele.isDisplayed()) {
                ele.click();
                selected = true;
                break;
            }
        }
        if(selected != true) {
            throw new RuntimeException("Unable to select element : //input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]" );
        }
    }


    public boolean isCTADisplayed(CTA cta) {
        waitTillNoLoadingIcon();
        env.setTimeout(5);
        try {
            List<WebElement> webElements = driver.findElements(By.xpath(getCTAXPath(cta)));
            Report.logInfo("NUmber of elements :" +webElements.size());
            System.out.println();
            for(WebElement ele : webElements) {
                if(ele.isDisplayed()) {
                    return true;
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Report.logInfo("CTA is not displayed / Present, Please check your XPath (or) CTA Data");
            Report.logInfo(e.getLocalizedMessage());
            return false;
        }
        Report.logInfo("CTA is not displayed");
        env.setTimeout(30);
        return false;

    }

 public WorkflowPage collapseCTAView() {
        item.click(CTA_EXP_SLIDE_ICON);
        amtDateUtil.stalePause();
        return this;
    }
    public WorkflowPage expandCTAView(CTA cta) {
        String xPath = getCTAXPath(cta)+ "/descendant::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        item.click(xPath);
        if(!isCTAExpandedViewLoaded(cta)) {
            throw new RuntimeException("CTA expand view failed to load");
        }
        return this;
    }
  public WorkflowPage expandTaskView(Task task) {
        String xPath = getTaskXPath(task)+"/div[@class='gs-cta-head child-task workflow-ctataskitem']";
        item.click(xPath);
        if(!isTaskExpandedViewLoaded(task)) {
            throw new RuntimeException("Task expand view failed to load");
        }
        return this;
    }

    public WorkflowPage collapseTaskView() {
        item.click("TASK_EXP_SLIDE_ICON");
        amtDateUtil.stalePause();
        return this;
    }
	
   public boolean verifyCTADetails(CTA cta) {
        expandCTAView(cta);
        String xpath = EXP_VIEW_ASSIGNEE+"/descendant::label[contains(@class, 'cta-username')]";
        if(!element.getText(xpath).trim().equalsIgnoreCase(cta.getAssignee())) {
            Report.logInfo("CTA is not assigned to right user.");
            return false;
        }
        String dueDate = element.getElement(EXP_VIEW_DUE_DATE_INPUT).getAttribute("value");
        if(dueDate == null || !dueDate.trim().equalsIgnoreCase(cta.getDueDate())) {
            Report.logInfo("CTA due data is not correct");
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
                Report.logInfo("Data in the table didn't match");
                return false;
            }
        }
        return true;
    }

    public boolean isTaskExpandedViewLoaded(Task task) {
        wait.waitTillElementDisplayed(DETAILED_FORM, MIN_TIME, MAX_TIME);
        Task expViewTask = new Task();
        for(int i=0; i<5; i++) {
            expViewTask.setSubject(element.getElement(TASK_EXP_SUBJECT).getAttribute("title").trim());
            expViewTask.setPriority(element.getText(TASK_EXP_PRIORITY));
            expViewTask.setStatus(element.getText(TASK_EXP_STATUS));

            if(!task.getPriority().equalsIgnoreCase(expViewTask.getPriority()))
                Report.logInfo("Priority not matched");
            if(!task.getStatus().equalsIgnoreCase(expViewTask.getStatus()))
                Report.logInfo("Status not matched");
            if(task.getSubject().equalsIgnoreCase(expViewTask.getSubject()))
                Report.logInfo("Subject not matched");

            if(task.getPriority().equalsIgnoreCase(expViewTask.getPriority()) &&
                    task.getStatus().equalsIgnoreCase(expViewTask.getStatus()) &&
                    task.getSubject().equalsIgnoreCase(expViewTask.getSubject())) {
                return true;
            }  else {
                Report.logInfo("Waiting for Task Details to Load");
                amtDateUtil.stalePause();
            }
        }
        Report.logInfo("Task expand mode is not loaded properly.");
        return false;
    }

    public boolean isCTAExpandedViewLoaded(CTA cta) {
        wait.waitTillElementDisplayed(DETAILED_FORM, MIN_TIME, MAX_TIME);
        CTA expViewCta = new CTA();
        for(int i=0; i< 5; i++) {
            expViewCta.setDueDate(element.getText(EXP_VIEW_DUE_DATE_INPUT).trim());
            expViewCta.setCustomer(element.getText(EXP_VIEW_CUSTOMER).trim());
            expViewCta.setType(element.getText(EXP_VIEW_TYPE).trim());
            expViewCta.setPriority(element.getText(EXP_VIEW_PRIORITY_BUTTON).trim());
            expViewCta.setStatus(element.getText(EXP_VIEW_STATUS_BUTTON).trim());
            expViewCta.setReason(element.getText(EXP_VIEW_REASON_BUTTON).trim());
            expViewCta.setSubject(element.getElement(EXP_VIEW_SUBJECT_INPUT).getAttribute("value").trim());

            if(cta.getCustomer().trim().equalsIgnoreCase(expViewCta.getCustomer())  &&
                    cta.getType().trim().equalsIgnoreCase(expViewCta.getType()) &&
                    cta.getPriority().trim().equalsIgnoreCase(expViewCta.getPriority()) &&
                    cta.getStatus().trim().equalsIgnoreCase(expViewCta.getStatus()) &&
                    cta.getReason().trim().equalsIgnoreCase(expViewCta.getReason())) {
                return true;
            } else {
                Report.logInfo("Waiting for Event Details to Load");
                amtDateUtil.stalePause();
            }
        }
        Report.logInfo("CTA expand mode is not loaded properly.");
        return false;
    }

    private String getCTAXPath(CTA cta) {
        String xPath = "//div[@class='gs-cta']";
        xPath = xPath+"/descendant::div[@class='title-ctn pull-left']";
        xPath = cta.isClosed() ? xPath+"/span[@class='check-data ctaCheckBox require-tooltip active']" :
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
        xPath = xPath+"/descendant::span[contains(text(), '"+cta.getCustomer()+"')]";
        xPath = xPath+"/ancestor::div[@class='pull-left']/div[@class='pull-left cta-score']";

        for(CTA.Attribute attribute : cta.getAttributes()) {
            if(attribute.isInSummary()) {
                xPath = xPath+"/descendant::span[@title='"+attribute.getAttLabel()+"' and contains(text(), '"+attribute.getAttValue()+"')]";
                xPath = xPath+"/ancestor::div[@class='pull-left cta-score']";
            }
        }

        xPath = xPath+"/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        xPath = xPath+"/descendant::div[@class='pull-right relative']";
        xPath = xPath+"/descendant::span[@class='task-no' and contains(text(), '"+cta.getTaskCount()+"')]/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
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
        Report.logInfo("CTA Path : " + xPath);
        return xPath;
    }

    private static String getTaskXPath(Task task) {
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
        env.setTimeout(5);
        List<WebElement> elements = element.getAllElement(getTaskXPath(task));
        if(elements.size() > 0) {
            for(WebElement ele : elements) {
                if(ele.isDisplayed()) {
                    return true;
                }
            }
        }
        Report.logInfo("Task is not displayed");
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
        amtDateUtil.stalePause();
        return this;
    }

    public WorkflowPage openCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks) {
        String xPath = getCTAXPath(cta)+"/descendant::span[@class='check-data ctaCheckBox require-tooltip active']";
        item.click(xPath);
        amtDateUtil.stalePause();
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
        amtDateUtil.stalePause();
        return this;
    }

    public WorkflowPage flagCTA(CTA cta) {
        String xPath = getCTAXPath(cta)+"/descendant::span[contains(@class, 'glyphicon glyphicon-bookmark cta-flag')]";
        item.click(xPath);
        amtDateUtil.stalePause();
        waitTillNoLoadingIcon();
        cta.setImp(true);
        return this;
    }
    
    public boolean verifyImpCTA(CTA cta){
    	item.click(SHOW_IMP_CTA);
    	if(verifyCTADetails(cta))   	return true;
    	return false;
    	
    }
    
    public boolean verifyClosedCTA(CTA cta,boolean hasTasks,ArrayList<Task> tasks){
    	item.click(SHOW_CLOSED_CTA);
    	if(!hasTasks){
    	if(verifyCTADetails(cta)) return true;
    	return false;
    	}
    	else{
    		item.click(VIEW_TASKS);
    		boolean allTasksClosed=true;
    		for(Task task :tasks )
    			if(!isTaskDisplayed(task)) allTasksClosed=false;
    		
    		return allTasksClosed;
    	}
    }
    
    public WorkflowPage updateTaskDetails(Task oldTask, Task newTask) {
        expandTaskView(oldTask);
        if(!oldTask.getAssignee().equalsIgnoreCase(newTask.getAssignee())) {
            item.click(TASK_EXP_ASSIGNEE);
            field.setTextByKeys(TASK_EXP_ASSIGNEE_SEARCH, newTask.getAssignee());
            waitTillNoLoadingIcon();
            item.click(String.format(TASK_EXP_ASSIGNEE_SELECT, newTask.getAssignee()));
        }
        if(newTask.getDate() != null && !newTask.getDate().equalsIgnoreCase(oldTask.getDate())) {
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
        amtDateUtil.stalePause();
        return this;
    }

    public WorkflowPage deleteTask(Task task) {
        expandTaskView(task);
        item.click(TASK_EXP_MORE_OPTIONS);
        wait.waitTillElementDisplayed(TASK_EXP_DELETE_OPTION, MIN_TIME, MAX_TIME);
        item.click(TASK_EXP_DELETE_OPTION);
        wait.waitTillElementDisplayed(DELETE_ACTION, MIN_TIME, MAX_TIME);
        item.click(DELETE_ACTION);
        amtDateUtil.stalePause();
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkflowPage deleteCTA(CTA cta) {
        expandCTAView(cta);
        item.click(EXP_VIEW_CTA_MORE_OPTIONS);
        item.click(String.format(EXP_VIEW_DELETE_CTA, cta.getType()));
        item.click(DELETE_ACTION);
        amtDateUtil.stalePause();
        waitTillNoLoadingIcon();
        return this;
    }

    public WorkflowPage changeAssigneeView(String assignee) {
        item.click(OWNER);
        amtDateUtil.stalePause();
        if(assignee != null) {
            field.clearAndSetText(OWNER_SEARCH, assignee);
            waitTillNoLoadingIcon();
            item.click(String.format(OWNER_SELECT, assignee));
        } else {
            item.click(OWNER_ALL);
        }
        waitTillNoLoadingIcon();
        waitTillNoSearchIcon();
        return this;
    }

    public WorkflowPage applyPlayBook(CTA cta, String playBookName, List<Task> tasks) {
        expandCTAView(cta);
        item.click(EXP_VIEW_CTA_MORE_OPTIONS);
        item.click(EXP_VIEW_APPLY_PLAYBOOK);
        amtDateUtil.stalePause();
        waitTillNoLoadingIcon();
        item.click(PLAYBOOK_SELECT);
        selectValueInDropDown(playBookName);
        waitTillNoLoadingIcon();
        applyOwnersToTasksInPlaybook(tasks);
        item.click(PLAYBOOK_APPLY);
        amtDateUtil.sleep(5);
        return this;
    }

    private void applyOwnersToTasksInPlaybook(List<Task> tasks) {
        for(Task task : tasks) {
            //h4[contains(text(), 'Schedule call with Admin and/or Power User to identify reason for drop')]/ancestor::div[contains(@class, 'playbook-task')]/descendant::input[@name='search_text']
            String path = "//h4[contains(text(), '"+task.getSubject()+"')]" +
                    "/ancestor::div[contains(@class, 'playbook-task')]/descendant::input[@name='search_text']";
            field.clearAndSetText(path, task.getAssignee());
            driver.findElement(By.xpath(path)).sendKeys(Keys.ENTER);
            waitTillNoLoadingIcon();
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

	public void syncTasksToSF(ArrayList<Task> tasks) {
		// TODO Auto-generated method stub
		
	}

	public boolean areTasksSyncedToSF(ArrayList<Task> tasks) {
		// TODO Auto-generated method stub
		return false;
	}

    public WorkflowPage showClosedCTA() {
        item.click(SHOW_CLOSED_CTA);
        waitTillNoSearchIcon();
        wait.waitTillElementDisplayed(HIDE_CLOSED_CTA, MIN_TIME, MAX_TIME );
        return this;
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


}
