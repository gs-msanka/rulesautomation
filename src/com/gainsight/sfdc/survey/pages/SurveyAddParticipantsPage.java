package com.gainsight.sfdc.survey.pages;

import org.testng.Assert;

import com.gainsight.testdriver.Log;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAddParticipantsPage extends SurveyBasePage {
	
	private final String ADD_PARTICIPANTS_PAGE = "//a[contains(@class,'sub-menu-option  sub-opt-addparticipants')]";
	private final String SEARCH = "//li[contains(@class,'1-search')]"; //Click on Search link
	private final String ADD_PARTICIPANTS = "//li[contains(@class,'2-add-participants')]"; //Click on Add Participants link
	
	//Objects under Search Section
	private final String LOAD_SURVEY_PARTICIPANTS_FROM = "//span[contains(text(),'Contact Object')]|//span[contains(text(),'Custom Object')]|//span[contains(text(),'CSV File')]";
	
	private final String SELECT_ROLE_CONTACT = "//input[@class='contact-type frmcontact' and @type='radio']";
	private final String SELECT_TITLE = "//span[@class = 'ui-multiselect-selected-label' and contains(text(),'Title')]";
	
	private final String SELECT_ROLE_ACCOUNT_CONTACT = "//input[@class='contact-type acc-contact-role' and @type='radio']";
	
	private final String SELECT_A_FIELD = "//span[@class = 'ui-multiselect-selected-label' and contains(text(),'Select a Field')]";
	private final String OPERATOR = "//span[@class = 'ui-multiselect-selected-label gs-rb-filter-' and contains(text(),'--None--')]";
	private final String VALUE = "//input[@class = 'for-width form-control text']";
	
	private final String ADVANCED_LOGIC = "//input[@class='form-control' and @placeholder = 'A and (B or C)']";
	
	private final String ADD_FILTER_ICON = "//a[@class='add' and @data-control='ADD_FIELD']";
	private final String DELETE_FILTER_ICON = "//a[@class='delete gs-rb-report-close' and @data-type='DELETE_FIELD']";
	
	//Elements when selected to load from Load Survey participants from Custom Object
	private final String SELECT_CUSTOM_OBJECT = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Select Object')]";
		
	//Elements when selected to load from Load Survey participants from CSV File
	private final String UPLOAD_CSV = "//span[@class='fileupload-new-inside' and contains(text(),'Upload CSV')]";
	
	private final String EXCLUDE_PARTICIPANTS = "//span[@class='ui-multiselect-selected-label' and contains(text(),'None')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Contacted')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Responded')]";
	private final String EXCLUDE_PARTICIPANTS_OPTIONS = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Select options')]";
	
	private final String SAVE_AND_SHOW_CONTACTS_BTN = "//a[contains(text(),'Save & Show Contacts')]";
	
	//Objects under Add Participants Section
	private final String SELECT_ALL = "//input[@id='isLoadAll']";
	
	private final String SELECT_ALL_DISPLAYED_IN_GRID = "//input[@id='cb_show-contacts-tbl']";
	private final String ADD_TO_DISTRIBUTION_LIST = "//a[contains(text(),'Add to Distribution List')]";
	
	
	private final String SUCCESSFUL_MSG = "//div[contains(text(),'Selected participants are added successfully')]";
	
	
	public SurveyAddParticipantsPage() {
		wait.waitTillElementPresent(ADD_PARTICIPANTS_PAGE, MIN_TIME, MAX_TIME);
	}
	
	//Method to add filters 
	public void addFilter(String fieldName,String Operator,String Value){
		item.selectFromDropDown(SELECT_A_FIELD, fieldName);
		item.selectFromDropDown(OPERATOR, Operator);
		field.setText(VALUE, Value);
		Log.info("Added filters");
	}
	
	//Method to navigate from Search page to Add Participants page
	public void navigateToAddParticipantsPage(){
		button.click(SAVE_AND_SHOW_CONTACTS_BTN);
		wait.waitTillElementPresent(ADD_TO_DISTRIBUTION_LIST, MIN_TIME, MAX_TIME);
		Log.info("Navigated to Add Participants page");
	}
	
	public void verifySuccessfulMessage(){
		Log.info("Verify the successful message after adding the contacts to the distribution list");
		wait.waitTillElementPresent(SUCCESSFUL_MSG, MIN_TIME, MAX_TIME);
		
	}
	
	
	
		
}
