package com.gainsight.sfdc.workflow.pages;


import org.openqa.selenium.By;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.workflow.pojos.CTA;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkflowPage extends WorkflowBasePage {

    private final String READY_INDICATOR  = "//div[@title='Add CTA']";
    private final String CALENDAR_VIEW_READY_INDICATOR = "//ul[@class='calendar-tab']";
    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image-64')]";
    private final String CREATE_CTA_ICON="//a[@class='dashboard-addcta-btn more-options cta-create-btn']";
    private final String CREATE_RISK_LINK="//a[@data-action='RISK']";
    private final String CREATE_OPPOR_LINK="//a[@data-action='OPPORTUNITY']";
    private final String CREATE_EVENT_LINK="//a[@data-action='EVENT']";
    private final String RISK_CTA_FORM_TITLE="//span[text()='Add Risk']";
    private final String OPPO_CTA_FORM_TITLE="//span[text()='Add Opportunity']";
    private final String EVENT_CTA_FORM_TITLE="//span[text()='Add Event']";
    private final String CREATE_FORM_SUBJECT="//input[@class='form-control cta-subject']";
    private final String CREATE_FORM_CUSTOMER="//input[@class='form-control strcustomer ui-autocomplete-input']";
    private final String CREATE_FORM_CUST_LIST="//div[@class='wf-dd-profile']/ul/li[@class='ui-menu-item']";//"//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']";
	private final String CREATE_FORM_REASON ="//div[@class='col-md-9']/button/span[@class='ui-icon ui-icon-triangle-2-n-s']";// "//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']/span[@class='ui-icon ui-icon-triangle-2-n-s']";
	private final String CREATE_FORM_REASON_OPPOR="//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all ui-state-active ui-state-hover']/span[@class='ui-icon ui-icon-triangle-2-n-s']";
	private final String CREATE_FORM_SELECT_REASON="//ul/li/label/span[text()='%s']";
	private final String CREATE_FORM_DUE_DATE="//input[@class='form-control cta-dateCtrl']";
	private final String CREATE_FORM_COMMENTS="//textarea[@class='form-control strdescription']";
	private final String SAVE_CTA="//div[@class='modal-footer text-center']/button[@class='gs-btn btn-save']";
	private final String CREATE_RECURRING_EVENT="//div[@class='form-group clearfix cta-recurring-event']/input[@id='chkRecurring']";
    private final String CREATE_FORM_RECUR_TYPE="//div[@class='radio-inline']/input[@value='%s']";
    private final String CREATE_RECUR_EVERYWEEKDAY_EVENT="//div[@class='radio-inline']/input[@value='RecursEveryWeekDay']";
    private final String RECUR_EVENT_START_DATE="//input[@class='form-control date-input scheduler-event-start-datepick']";
    private final String RECUR_EVENT_END_DATE="//input[@class='form-control date-input scheduler-event-end-datepick']";
    
    public WorkflowPage() {
        waitForPageLoad();
    }

    public WorkflowPage(String view) {
        waitForPageLoad();
        wait.waitTillElementDisplayed(CALENDAR_VIEW_READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    private void waitForPageLoad() {
        Report.logInfo("Loading Cockpit Page");
        env.setTimeout(10);
        wait.waitTillElementNotPresent(LOADING_ICON, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        Report.logInfo("Cockpit Page Loaded Successfully");
    }
    
    public void createCTA(CTA cta){
    	Report.logInfo("Adding CTA of Type - RISK");
    	item.click(CREATE_CTA_ICON);
    	if(cta.getType().equals("Risk"))
    	{
    		item.click(CREATE_RISK_LINK);
    		fillAndSaveRiskCTAForm(cta);
    	}
    	if(cta.getType().equals("Opportunity")){
    		item.click(CREATE_OPPOR_LINK);
    		fillAndSaveOpporCTAForm(cta);
    	}
    	if(cta.getType().equals("Event")){
    		item.click(CREATE_EVENT_LINK);
    		fillAndSaveEventCTAForm(cta);
    	}
	}

	private void fillAndSaveEventCTAForm(CTA cta) {
		wait.waitTillElementDisplayed(EVENT_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		setCustomer(cta.getCustomer());
		button.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON,cta.getReason()));
		field.setText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		if(cta.getIsRecurring()){
		field.selectCheckbox(CREATE_RECURRING_EVENT);
			if(cta.getRecurringType().equals("Daily")){
				
			}
			else if (cta.getRecurringType().equals("Weekly")){
				
			}
			else if(cta.getRecurringType().equals("Monthly")){
				
			}
			else if(cta.getReurringType().equals("Yearly")){
				
			}
		}
		button.click(SAVE_CTA);	
		}

	private void fillAndSaveOpporCTAForm(CTA cta) {
		wait.waitTillElementDisplayed(OPPO_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		setCustomer(cta.getCustomer());
		item.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON,cta.getReason()));
		field.setText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		button.click(SAVE_CTA);
	}

	private void fillAndSaveRiskCTAForm(CTA cta) {
		wait.waitTillElementDisplayed(RISK_CTA_FORM_TITLE, MIN_TIME, MAX_TIME);
		field.clearAndSetText(CREATE_FORM_SUBJECT, cta.getSubject());
		setCustomer(cta.getCustomer());
		button.click(CREATE_FORM_REASON);
		item.click(String.format(CREATE_FORM_SELECT_REASON,cta.getReason()));
		field.setText(CREATE_FORM_DUE_DATE, cta.getDueDate());
		field.setText(CREATE_FORM_COMMENTS, cta.getComments());
		button.click(SAVE_CTA);		
	}
	  private void setCustomer(String custName) {
	        field.setText(CREATE_FORM_CUSTOMER, custName);
	       // field.setTextByKeys(CREATE_FORM_CUSTOMER, "\r");
	        amtDateUtil.stalePause();
	        driver.findElement(By.xpath("//li[@class='ui-menu-item']/a/label[contains(text(),'"+custName+"')]")).click();
	    }
}
