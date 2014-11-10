package com.gainsight.sfdc.workflow.pages;


import org.openqa.selenium.By;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkflowPage extends WorkflowBasePage {

    private final String READY_INDICATOR  = "//div[@title='Add CTA']";
    private final String CALENDAR_VIEW_READY_INDICATOR = "//ul[@class='calendar-tab']";
    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image-64')]";

    //Header Page Elements
    private final String SHOW_CLOSED_CTA    = "//li[contains(@class, 'cta-stage baseFilter cta-stage-filter')]";
    private final String SHOW_IMP_CTA       = "//li[contains(@class, 'cta-flag baseFilter cta-flag-filter')]";
    private final String SHOW_SNOOZE_CTA    = "//li[contains(@class, 'cta-snooze baseFilter cta-snooze-filter')]";

    private final String TYPE_RISK          = "//div[@class='type cta-types-filter']/ul/li[@name='Risk']";
    private final String TYPE_OPPORTUNITY   = "//div[@class='type cta-types-filter']/ul/li[@name='Opportunity']";
    private final String TYPE_EVENT         = "//div[@class='type cta-types-filter']/ul/li[@name='Event']" ;

    private final String PRIORITY_CTA       = "//div[@class='priority cta-priority-filter']/ul/li[@name='%s']";
    private final String GROUP_BY           = "//select[@class='form-control cta-group-by']/following-sibling::button";
    private final String SORT_BY            = "//select[@class='form-control cta-sort-by']/following-sibling::button";

    //Form Page Elements
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
	private final String CREATE_FORM_SELECT_REASON="//ul/li/label/span[text()='%s']";
	private final String CREATE_FORM_DUE_DATE="//input[@class='form-control cta-dateCtrl']";
	private final String CREATE_FORM_COMMENTS="//textarea[@class='form-control strdescription']";
	private final String SAVE_CTA="//div[@class='modal-footer text-center']/button[@class='gs-btn btn-save']";
	private final String CREATE_RECURRING_EVENT="//div[@class='form-group clearfix cta-recurring-event']/input[@id='chkRecurring']";
    private final String CREATE_FORM_RECUR_TYPE="//div[@class='row']/div[@class='radio-inline']/input[@value='%s']";
    private final String CREATE_RECUR_EVERYWEEKDAY_EVENT="//div[@class='radio-inline']/input[@value='RecursEveryWeekDay']";
    private final String RECUR_EVENT_START_DATE="//input[@class='form-control date-input scheduler-event-start-datepick']";
    private final String RECUR_EVENT_END_DATE="//input[@class='form-control date-input scheduler-event-end-datepick']";
    private final String RECUR_WEEK_COUNT="//div[@class='schedulereventoptions-weekly']/div[@class='date-float']/input[@class='form-control width40 text-center weekly-recursevery-weekpick']";
    private final String RECUR_WEEKDAY="//div[@class='row']/label[@class='checkbox-inline']/input[@value='%s']";
    private enum WEEKDAY{Sun,Mon,Tue,Wed,Thu,Fri,Sat};

    //CTA Expanded View Elements


    public WorkflowPage() {
        waitForPageLoad();
    }

    public WorkflowPage(String view) {
        waitForPageLoad();
        wait.waitTillElementDisplayed(CALENDAR_VIEW_READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    private void waitForPageLoad() {
        Report.logInfo("Loading Cockpit Page");
        env.setTimeout(5);
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
		if(cta.isRecurring()){
		field.selectCheckbox(CREATE_RECURRING_EVENT);

            /*
			if(cta.getRecurringType().equals("Daily")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,cta.getRecurringType()));
				if(cta.getDailyRecurringInterval().equals("EveryWeekday"))
					item.click(CREATE_RECUR_EVERYWEEKDAY_EVENT);
				item.setText(RECUR_EVENT_START_DATE, cta.getRecurStartDate());
				item.setText(RECUR_EVENT_END_DATE, cta.getRecurEndDate());
			}
			else if (cta.getRecurringType().equals("Weekly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,cta.getRecurringType()));
				field.setText(RECUR_WEEK_COUNT, cta.getWeeklyRecurringInterval().split("_")[1]);
				field.selectCheckBox(String.format(RECUR_WEEKDAY,WEEKDAY.valueOf(cta.getWeeklyRecurringInterval().split("_")[2])));
			}
			else if(cta.getRecurringType().equals("Monthly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,cta.getRecurringType()));

			}
			else if(cta.getRecurringType().equals("Yearly")){
				item.click(String.format(CREATE_FORM_RECUR_TYPE,cta.getRecurringType()));

			}
			*/
		}
		button.click(SAVE_CTA);
        amtDateUtil.stalePause(); //In - Case, Should add wait logic here.
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

    public boolean isCTADisplayed(CTA cta) {
        try {
            WebElement wEle = driver.findElement(By.xpath(getCTAXPath(cta)));
            return wEle.isDisplayed();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Report.logInfo("CTA is not displayed / Present, Please check you xPath");
            Report.logInfo(e.getLocalizedMessage());
            return false;
        }
    }

    public String getCTAXPath(CTA cta) {
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

        xPath = xPath+"/following-sibling::span[@class='title-name workflow-cta-title' and contains(text(), '"+cta.getSubject()+"')]";
        xPath = xPath+"/ancestor::div[@class='pull-left']/div[@class='wf-account pull-left']";
        xPath = xPath+"/descendant::span[contains(text(), '"+cta.getCustomer()+"')]";
        xPath = xPath+"/ancestor::div[@class='pull-left']/div[@class='pull-left cta-score']";

        for(CTA.Attribute attribute : cta.getAttributes()) {
            //if(attribute.isInSummary()) {
                xPath = xPath+"/descendant::span[@title='"+attribute.getAttLabel()+"' and contains(text(), '"+attribute.getAttValue()+"')]";
                xPath = xPath+"/ancestor::div[@class='pull-left cta-score']";
            //}
        }

        xPath = xPath+"/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        xPath = xPath+"/descendant::div[@class='pull-right relative']";
        xPath = xPath+"/descendant::span[@class='task-no' and contains(text(), '"+cta.getTaskCount()+"')]/ancestor::div[contains(@class, 'gs-cta-head workflow-ctaitem')]";
        xPath = xPath+"/descendant::span[@class='cta-duedate' and contains(text(), '"+cta.getDueDate()+"')]/ancestor::div[conatins(@class, 'gs-cta-head workflow-ctaitem')]";
        if(cta.getAssignee() != null) {
            xPath = xPath+"/descendant::img[contains(@alt, '"+cta.getAssignee()+"')]";
            xPath = xPath+"/ancestor::div[@class='gs-cta']";
        } else {
            throw new RuntimeException("Assignee should be specified.");
        }
        Report.logInfo("CTA Path : " + xPath);
        return xPath;
    }
}
