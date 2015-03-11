package com.gainsight.sfdc.survey.pages;

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
	private final String CLICKON_TOBECONTACTED_CIRCLE = "//p[@class='to-be-contacted icon-select' and @data-action='Notcontacted']";
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
	private final String CLICKON_CREATED_SCHEDULE ="";
	private final String CLICKON_EDIT_SCHEDULE ="";
	private final String CLICKON_DELETE_SCHEDULE ="";
	private final String CLICKON_ALLOWINTERNAL_SUBMISSION ="";
	private final String CLICKON_VIEW_SURVEYRESPONSE ="//span[@class='surveyParticipantResponsePreview']";
	private final String SELECT_PARTICIPANTS_FROMGRID ="";
	private final String SELECT_ALLPARTICIPANTS_FROMGRID ="";
	private final String REMOVE_PARTICIPANTS_FROMSCHEDULE ="";	
	
}
