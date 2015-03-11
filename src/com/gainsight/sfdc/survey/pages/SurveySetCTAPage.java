package com.gainsight.sfdc.survey.pages;

import org.openqa.selenium.WebElement;

import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.survey.pojo.SurveyRuleProperties;
import com.gainsight.testdriver.Log;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveySetCTAPage extends BasePage {
	
	private final String ADD_NEW_RULE_BUTTON="//a[@class='gs-btn btn-add' and contains(text(),'Add New Rule')]";
	
	//Adding new Rule
	private final String NEW_AUTOMATED_RULE_BOX="//div[contains(@class,'logicrulebox logic-editmode clearfix show')//p[contains(@class='logic-title-edit') and contains(text(),'New Automated Rule')]";
	private final String DELETE_RULE_ICON="//div[@class='add-actions pull-right']//a[@original-title='Delete' and contains(text(),'DELETE')]";
	private final String DELETE_RULE_CONFIRM="//input[@type='button' and contains(@class,'gs-btn btn-save btn_save saveSummary')]";
	
	//Question and Answer selection for Rule
	private final String SELECT_QUESTION_DD="//div[contains(@class='emailadd pull-left question-set')]/select[@class='form-control sel-question']";
	private final String SELECT_QUESTION_VALUE="//div[@class='emailadd pull-left question-set']/select/option[contains(@value,'%s')]";
	private final String ADD_ANOTHER_QUESTION="//div[@class='addmail']/a[@class='mailadd add-question-button add-question-set' and contains(text(),'ADD')]";
	private final String DELETE_SELECTED_QUESTION="//div[@class='addmail']/a[@class='maildelete delete-question-set' and contains(text(),'DELETE')]";
	private final String SELECT_ANSWER="//select[@multiple class='form-control pull-left sel-answer']";
	private final String SELECT_SUBQUESTION="//select[@class='form-control pull-left sel-sub-question show']";
	
	//Advanced logic selection for Rule
	private final String ADD_ADVNC_LOGIC_COLLAPSED="//a[@data-toggle='collapse' and @class='btn-slide btn-slide-all collapsed']";
	private final String ADD_ADVNC_LOGIC_EXPANDED="//a[@data-toggle='collapse' and @class='btn-slide btn-slide-all']";
	private final String SELECT_FIELD="";
	private final String SELECT_OPERATOR="";
	private final String SELECT_VALUE="";
	private final String SET_RULE_FOR_FILTER_LOGIC="";
	private final String ADD_ANOTHER_FILTER="";
	private final String DELETE_FILTER="";
	
	//Set Action for Rule
	private final String ACTION_SELECT_CTA="//li[contains(@class,'alert-rule-tab') and @data-action='CTA']/a[text()='CTA']";
	private final String ACTION_SELECT_ALERT="//li[contains(@class,'alert-rule-tab') and @data-action='Alert']/a[text()='Alert']";
	
	//Set CTA properties
	private final String SET_CTA_PRIORITY_DD="//div[@class='rule-cta']//select[@class='alertSeverity form-select']";
	private final String SET_CTA_STATUS_DD="//div[@class='rule-cta']//select[@class='alertStatus form-select']";
	private final String SET_CTA_REASON_DD="//div[@class='rule-cta']//select[@class='alertReason form-select']";
	private final String SET_CTA_DUE_DATE="//div[@class='rule-cta']//div[@class='dueDate']/input[@class='form-control formControlDataInput']";
	private final String SET_CTA_CHATTER_UPDATE="//div[@class='rule-cta']//select[@class='comments_post_frequency form-select']";
	private final String SET_CTA_TYPE="//div[@class='rule-cta']//select[@class='alertType form-select']";
	private final String SET_CTA_PLAYBOOK="//div[@class='rule-cta']//select[@class='form-select playBook']";
	private final String SET_CTA_OWNER="//div[@class='rule-cta']//select[@class='taskOwnerField form-select']";
	private final String SET_CTA_DEFAULT_OWNER="//div[@class='rule-cta']//div[@class='gs_search_section']/input[@class='search_input form-control ui-autocomplete-input']";
	private final String SET_CTA_COMMENTS="//div[@class='rule-cta']//div[@class='form-control alertComment']";
	
	//Set Alert Properties
	private final String SET_ALERT_SEVERITY="//div[@class='rule-alert']//select[@class='alertSeverity form-select']";
	private final String SET_ALERT_TYPE="//div[@class='rule-alert']//select[@class='alertType form-select']";
	private final String SET_ALERT_PLAYBOOK="//div[@class='rule-alert']//select[@class='form-select playBook']";
	private final String SET_ALERT_STATUS="//div[@class='rule-alert']//select[@class='alertStatus form-select']";
	private final String SET_ALERT_REASON="//div[@class='rule-alert']//select[@class='alertStatus form-select']";
	private final String SET_ALERT_TASK_OWNER="//div[@class='rule-alert']//select[@class='taskOwnerField form-select']";
	private final String SET_ALERT_DEFAULT_TASKOWNER="//div[@class='rule-alert']//input[@class='search_input form-control ui-autocomplete-input']";
	
	//	Save Rule
	private final String SAVE_RULE="//input[@class='gs-btn btn-save save-editmode' and @value='Save']";
	private final String CANCEL_RULE="//input[@class='gs-btn btn-cancel cancel-editmode' and @value='Cancel']";
	
	//Rule verification
	private final String RULE_STATUS_SWITCH="//label[@class='onoffswitch-label']/span[@class='onoffswitch-switch']";
	private final String RULE_STATUS_ACTIVE="//div[@class='onoffswitch pull-left set-alert-cta']/input[@class='onoffswitch-checkbox rule-status' and @checked='checked']";
	private final String RULE_STATUS_INACTIVE="//div[@class='onoffswitch pull-left set-alert-cta']/input[@class='onoffswitch-checkbox rule-status' and @checked='']";
	private final String RULE_TITLE="//div[@class='display-rule-questions']"; //the entire rule with the question,sub question + answer combination
	private final String RULE_CONDITION="//div[@class='disp-filter-logic' and contains(text(),'Condition']/i/span[@class='badge']";
	private final String RULE_ACTIONS="//div[@class='display-rule-alert-header' and contains(text(),'Actions')]/following-sibling::div[@class='display-rule-alert-title' and contains(text(),'Create %s with the following propertis')]";
	private final String RULE_ACTION_CTA_Props="";
	private final String RULE_ACTION_ALERT_Props="";
	
	//Edit Rule
	private final String EDIT_RULE_ICON="";
	
	private final String RULE_SET_STATUS=""; //Active or inactive!
	
	public SurveySetCTAPage clickOnAddNewRule(){
		item.click(ADD_NEW_RULE_BUTTON);
		waitTillNoLoadingIcon();
		item.isElementPresent(NEW_AUTOMATED_RULE_BOX);
		return this;
	}
	
	public SurveySetCTAPage setQuestionAndAnswers(SurveyRuleProperties sProps){
		item.click(SELECT_QUESTION_DD);
		return this;
		
	}
	  public void selectValueInDropDown(String value) {
	        boolean selected = false;
	        for(WebElement ele : element.getAllElement("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]")) {
	            Log.info("Checking : "+ele.isDisplayed());
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
}
