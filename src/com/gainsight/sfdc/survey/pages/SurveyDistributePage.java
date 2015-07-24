package com.gainsight.sfdc.survey.pages;

import org.openqa.selenium.By;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyDistribution;
import com.gainsight.testdriver.Log;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyDistributePage extends SurveyBasePage{
	
	private final String DISTRIBUTE_PAGE ="";
	private final String CLICKON_LISTVIEW = "//li[@class='dist-tab active' and @data-action ='list']";
	private final String CLICKON_DISTRIBUTION_LIST_CIRCLE = "//div[@class='list-all icon-select']";
	private final String CLICKON_RESPONDED_CIRCLE = "//p[@class='sc-responded icon-select active' and @data-action='Responded']";
	private final String CLICKON_NOTRESPONDED_CIRCLE = "//p[@class='notresponded icon-select' and @data-action='Contacted']";
	private final String CLICKON_SCHEDULED_CIRCLE = "//p[@class='scheduled icon-select' and @data-action='Inschedule']";
	private final String CLICKON_TOBECONTACTED_CIRCLE = "//p[contains(@class, 'to-be-contacted icon-select') and @data-action='Notcontacted']";
	private final String CLICKON_UNDELIVERED_CIRCLE = "//div[@class='undelivered icon-select' and @data-action='Undelivered']";
	private final String CLICKON_SENDEMAIL_BUTTON ="//input[@class='gs-btn btn-add email-sent-btn' and @type='button' and @value='Send Email']";
	private final String CLICKON_EXPORT_BUTTON ="//li[@class='export-participants']/a[@class='export-icon custom-tt']";
	private final String CLICKON_FILTER_BUTTON ="//li[@class='filter-advanced' and @title='Filter']/a[@class='filter']";
	private final String SELECT_EMAIL_TEMPLATE ="//select[@class='email-templates form-control']/";
	private final String CLICKON_RESENDEMAIL_BUTTON ="//input[@class='gs-btn btn-add email-sent-btn']";
	private final String CLICKON_FROMEMAILADDRESS_DROPDOWN = "//select[@class='fromEmail form-control']";
	private final String CLICKON_SCHEDULE_BUTTON = "//input[@class='gs-btn btn-save pull-right' and @type='button' and @value='+ Create Schedule']";
	private final String SCHEDULEPOP_UP_DETAILS ="";
	private final String SCHEDULEPOP_UP_SHOWPARTICIPANTS ="";
	private final String SCHEDULE_NAME_TEXTAREA ="//input[@class='form-control schedule-title' and @type='text' and @placeholder='Enter schedule name here']";
	private final String SCHEDULE_TYPE_DROPDOWN ="//select[@class='form-control sel-schedule-type']";
	private final String SCHEDULE_EMAIL_TEMPLATE_DROPDOWN ="//select[@class='form-control email-templates']";
	private final String SCHEDULE_DATE ="//input[@class='form-control sche-dateCtrl' and @type='text']";
	private final String SCHEDULE_TIME_HRS="//select[@class='form-control sel-hours']";
	private final String SCHEDULE_TIME_MINUTES ="//select[@class='form-control sel-minutes']";
	private final String SCHEDULE_FROMEMAILADDRESS_DROPDOWN ="//select[@class='form-control sel-frm-address']";
	private final String SCHEDULE_NEXT_BUTTON ="//a[@class='btnmove btnnext  pcacs-tabs offset-none']";
	private final String SCHEDULE_PREVIOUS_BUTTON ="//span[@class='create-sch-preview']";
	private final String SCHEDULE_DONE_BUTTON ="//button[@class='gs-btn btn-save']";
	private final String SCHEDULE_CANCEL_BUTTON ="//button[@class='gs-btn btn-cancel']";
	private final String CLICKON_CALENDARVIEW = "//li[@class='dist-tab' and @data-action ='schedule']";
	private final String CALENDARVIEW_MONTH ="//button[@class='fc-month-button fc-button fc-state-default fc-corner-left fc-state-active']";
	private final String CALENDARVIEW_WEEK ="//button[@class='fc-agendaWeek-button fc-button fc-state-default']";
	private final String CALENDARVIEW_DAY ="//button[@class='fc-agendaDay-button fc-button fc-state-default fc-corner-right']";
	private final String CALENDARVIEW_PREVIOUS_ARROWMARK ="//button[@class='fc-prev-button fc-button fc-state-default fc-corner-left']";
	private final String CALENDARVIEW_NEXT_ARROWMARK ="//button[@class='fc-next-button fc-button fc-state-default fc-corner-right']";
	private final String CLICKON_CREATE_SCHEDULE ="//div[@class='col-md-2']/input[@value='+ Create Schedule']";
	private final String CLICKON_VIEW_SURVEYRESPONSE ="//span[@class='surveyParticipantResponsePreview']";
    private final String CONFIRMATION_MESSAGE_DIV  ="//div[@class='tab-pane active container-schedule']/descendant::div[@id='errorMsg']";
	private final String SELECT_CONTACTS_CHECKBOX ="//input[@id='cb_add-participants-tbl']";
	private final String EMAIL_CONFIRM_DILOG_TEXT ="//div[@class='modal_body']/div[@class='layout_popup_text']";
	private final String EMAIL_CONFIRM            ="//div[@class='modal_footer']/descendant::input[contains(@class, 'saveSummary')]";
	private final String CREATE_SCHEDULE_DIV      ="//div[contains(@class, 'ui-dialog-titlebar')]/span[text()='Create Schedule']";
	private final String TODAY_LINK_IN_CALENDER   ="Today";
	private final String CALENDER_CLICK           ="//div[@class='clearfix']/span[text()='Minutes']"; //Clicking somewhere on Div
	private final String SCHEDULE_CHECKBOX        ="//div[contains(@id, 'new-schedule-participants')]/input[@class='cbox']";
	private final String EMAIL_SENT_CONFIRMATION_DIV ="//[@class='errorMsg' and text()='%s']";
	
	
	
	public SurveyDistributePage(String  surveyName) {
		super(surveyName);
		wait.waitTillElementDisplayed(CLICKON_CREATE_SCHEDULE, MIN_TIME, MAX_TIME);
	}
	
	public void clickingToBeContacted() {
		item.click(CLICKON_TOBECONTACTED_CIRCLE);
		wait.waitTillElementDisplayed(CLICKON_TOBECONTACTED_CIRCLE, MIN_TIME, MAX_TIME);
		Timer.sleep(5); // Added since in linux box its not recognizing particular webelement.
		item.click(SELECT_CONTACTS_CHECKBOX);
	}

	public int getContactsCount() {
		String temptext = element.getElement(
				By.xpath("//a[@class='numcolor-5 mininum-add']/span"))
				.getText();
		Log.info("String Text is  : " + temptext);
		int Count = Integer.parseInt(temptext);
		Log.info("Count is " + Count);
		return Count;
	}

	public void sendEmail() {
		Timer.sleep(5);
		item.click(CLICKON_SENDEMAIL_BUTTON);
		wait.waitTillElementDisplayed(EMAIL_CONFIRM, MIN_TIME, MAX_TIME);
		item.click(EMAIL_CONFIRM);
		wait.waitTillElementDisplayed(CONFIRMATION_MESSAGE_DIV, MIN_TIME,
				MAX_TIME);
	}

	public void createSchedule(SurveyDistribution surveyDistribution){
		item.click(CLICKON_CREATE_SCHEDULE);
		wait.waitTillElementDisplayed(CREATE_SCHEDULE_DIV, MIN_TIME, MAX_TIME);
		field.setText(SCHEDULE_NAME_TEXTAREA,
				surveyDistribution.getScheduleName());
		element.selectFromDropDown(SCHEDULE_TYPE_DROPDOWN,
				surveyDistribution.getScheduleType());
		field.clearAndSetText(SCHEDULE_DATE, surveyDistribution.getScheduleDate());
		element.selectFromDropDown(SCHEDULE_TIME_HRS,
				surveyDistribution.getHours());
		element.selectFromDropDown(SCHEDULE_TIME_MINUTES,
				surveyDistribution.getMinutes());
		if (surveyDistribution.getScheduleType().equalsIgnoreCase("Resend")) {
			element.selectFromDropDown(SCHEDULE_EMAIL_TEMPLATE_DROPDOWN,
					surveyDistribution.getEmailTemplate());
		}
		item.click(SCHEDULE_NEXT_BUTTON);
		createScheduleNext();
	}
	
	public void createScheduleNext() {
		wait.waitTillElementDisplayed(SCHEDULE_DONE_BUTTON, MIN_TIME, MAX_TIME);
		wait.waitTillElementDisplayed(SCHEDULE_CHECKBOX, MIN_TIME, MAX_TIME);
		item.click(SCHEDULE_CHECKBOX);
		button.click(SCHEDULE_DONE_BUTTON);
		wait.waitTillElementNotDisplayed("//div[contains(@class, ' ui-corner-all ui-front ui-draggable')]", MIN_TIME, MAX_TIME);
	}

	public int getScheduledCount(){
		Timer.sleep(9);
		String temptext=element.getElement(By.xpath("//a[@class='numcolor-4 mininum-add']/span")).getText();
		Log.info("String Text is  : " + temptext);
		int Count=Integer.parseInt(temptext);
		System.out.println(Count);
		return Count;
	}
	public String getCurrentDateAndTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String currentTime = dateFormat.format(date);
		Log.info(dateFormat.format(date));
		return currentTime;
	}

	
}

