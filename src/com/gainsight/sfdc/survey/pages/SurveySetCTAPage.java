package com.gainsight.sfdc.survey.pages;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveySetCTAPage {
	
	private final String ADD_NEW_RULE_BUTTON="//a[@class='gs-btn btn-add' and contains(text(),'Add New Rule')]";
	
	//Adding new Rule
	private final String NEW_AUTOMATED_RULE_BOX="//div[contains(@class,'logicrulebox logic-editmode clearfix show')//p[contains(@class='logic-title-edit') and contains(text(),'New Automated Rule')]";
	private final String DELETE_RULE_ICON="";
	private final String DELETE_RULE_CONFIRM="";
	
	//Question and Answer selection for Rule
	private final String SELECT_QUESTION_DD="";
	private final String SELECT_QUESTION_VALUE="";
	private final String ADD_ANOTHER_QUESTION="";
	private final String DELETE_SELECTED_QUESTION="";
	private final String SELECT_ANSWER="";
	private final String SELECT_SUBQUESTION="";
	
	//Advanced logic selection for Rule
	private final String ADD_ADVNC_LOGIC_COLLAPSED="";
	private final String ADD_ADVNC_LOGIC_EXPANDED="";
	
	//Set Action for Rule
	private final String ACTION_SELECT_CTA="";
	private final String ACTION_SELECT_ALERT="";
	
	//Set CTA properties
	private final String SET_CTA_PRIORITY_DD="";
	private final String SET_CTA_STATUS_DD="";
	private final String SET_CTA_REASON_DD="";
	private final String SET_CTA_DUE_DATE="";
	private final String SET_CTA_CHATTER_UPDATE="";
	private final String SET_CTA_TYPE="";
	private final String SET_CTA_PLAYBOOK="";
	private final String SET_CTA_OWNER="";
	private final String SET_CTA_DEFAULT_OWNER="";
	private final String SET_CTA_COMMENTS="";
	private final String SET_CTA__SAVE="";
	private final String SET_CTA_CANCEL="";
	
	//Set Alert Properties
	private final String SET_ALERT_SEVERITY="";
	private final String SET_ALERT_TYPE="";
	private final String SET_ALERT_PLAYBOOK="";
	private final String SET_ALERT_STATUS="";
	private final String SET_ALERT_REASON="";
	private final String SET_ALERT_TASK_OWNER="";
	private final String SET_ALERT_DEFAULT_TASKOWNER="";
	private final String SET_ALERT_SAVE="";
	private final String SET_ALERT_CANCEL="";
	
	//Rule verification
	private final String RULE_STATUS="";
	private final String RULE_TITLE=""; //the entire rule with the question,sub question + answer combination
	private final String RULE_CONDITION="";
	private final String RULE_ACTIONS="";
	private final String RULE_ACTION_CTA_Props="";
	private final String RULE_ACTION_ALERT_Props="";
	
	//Edit Rule
	private final String EDIT_RULE_ICON="";
	
	private final String RULE_SET_STATUS=""; //Active or inactive!
	
	
}
