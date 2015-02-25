package com.gainsight.sfdc.survey.pages;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.testdriver.Log;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAddParticipantsPage extends SurveyPage {
	
	private static final String SELECT_PARTICIPANT = "//table[@id='show-contacts-tbl']/tbody/tr/td[@aria-describedby='show-contacts-tbl_Account.Name' and @title='%s']/parent::tr/td[@aria-describedby='show-contacts-tbl_Name' and @title='%s']/parent::tr/td[@aria-describedby='show-contacts-tbl_Email' and @title='%s']/parent::tr/td[@aria-describedby='show-contacts-tbl_cb']/input[@type='checkbox']";
	private final String ADD_PARTICIPANTS_PAGE = "//a[contains(@class,'sub-menu-option  sub-opt-addparticipants')]";
	private final String SEARCH = "//li[contains(@class,'1-search')]"; //Click on Search link
	private final String ADD_PARTICIPANTS = "//li[contains(@class,'2-add-participants')]"; //Click on Add Participants link
	
	//Objects under Search Section
	private final String LOAD_SURVEY_PARTICIPANTS_FROM = "//label[contains(text(),'Load Survey participants from :')]//following-sibling::button/span[@class='ui-icon ui-icon-triangle-2-n-s']";
	
	private final String SELECT_ROLE_CONTACT = "//input[@class='contact-type frmcontact' and @type='radio']";
	private final String SELECT_TITLE = "//span[@class = 'ui-multiselect-selected-label' and contains(text(),'Title')]";
	
	private final String SELECT_ROLE_ACCOUNT_CONTACT = "//input[@class='contact-type acc-contact-role' and @type='radio']";
	
	private final String SELECT_A_FIELD = "//label[contains(text(),'Role Field :')]//following-sibling::div//button[@type='button']";
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
	private final String SELECTCONTACTTOADD_CHCKBOX = "//input[@id='jqg_show-contacts-tbl_'+%d]"; //Need Review - //input[@id='jqg_show-contacts-tbl_1']
	private final String Survey_TITLE                   = "//h3[@class='leftinner-title']/span[text()='%s']";
	
	//String.format(RETRIEVE_CONTACT_ROW,Account Name,Name,Role,Email)
	private final String RETRIEVE_CONTACT_ROW = "//table[@id='show-contacts-tbl']"+
										"/tbody/tr/td[@title='%s']"+
										"/following-sibling::td[@title='%s']"+
										"/following-sibling::td[@title='%s']"+
										"/following-sibling::td[@title='%s']";
	
	private final String ADD_TO_DISTRIBUTION_LIST = "//a[contains(text(),'Add to Distribution List')]";
	
	
	private final String SUCCESSFUL_MSG = "//div[contains(text(),'Selected participants are added successfully')]";

	
	public SurveyAddParticipantsPage(SurveyProperties surveyProp) {
		wait.waitTillElementPresent(String.format(Survey_TITLE, surveyProp.getSurveyName()), MIN_TIME, MAX_TIME);
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
	
	public void verifyDataInParticipantDetailsGrid(){
			
	}
	
	public void loadFromContactObj(SurveyAddParticipants addPtp,SurveyProperties sData){
		if(addPtp.getLoadParticipantsFrom().equalsIgnoreCase("Contact Object")){
			item.click(LOAD_SURVEY_PARTICIPANTS_FROM);
			selectValueInDropDown(addPtp.getLoadParticipantsFrom());
		}
		if(addPtp.getSelectRole().equalsIgnoreCase("Contact")){
			item.click(SELECT_ROLE_CONTACT);
			item.click(SELECT_A_FIELD);
			selectValueInDropDown(addPtp.getSelectRoleField());
		}
		else	item.click(SELECT_ROLE_ACCOUNT_CONTACT);
		if(addPtp.getAdvancedLogic()!=null || addPtp.getAdvancedLogic()!=""){
			
		}
		if(addPtp.getExcludeParticipants_Type()!=""){
			item.click(EXCLUDE_PARTICIPANTS);
			selectValueInDropDown(addPtp.getExcludeParticipants_Type());
			item.click(EXCLUDE_PARTICIPANTS_OPTIONS);
			selectValueInDropDown(addPtp.getExcludePtp_Survey());
		}
		button.click(SAVE_AND_SHOW_CONTACTS_BTN);
		if(addPtp.isLoadAll()) loadAllParticipants();
		else loadSelectedParticipants(addPtp);
	}

	private void loadSelectedParticipants(SurveyAddParticipants addPtp) {
		List<SurveyAddParticipants.ParticipantDetails> participantsList=addPtp.getParticipantsList();
		for(SurveyAddParticipants.ParticipantDetails ptp : participantsList){
			item.click(String.format(SELECT_PARTICIPANT,ptp.getAccName(),ptp.getName(),ptp.getEmail()));
		}
		button.click(ADD_TO_DISTRIBUTION_LIST);
		waitTillNoLoadingIcon();
		wait.waitTillElementDisplayed(SUCCESSFUL_MSG, MIN_TIME, MAX_TIME);		
	}

	public void loadAllParticipants() {
		item.click(SELECT_ALL);
		button.click(ADD_TO_DISTRIBUTION_LIST);
		waitTillNoLoadingIcon();
		wait.waitTillElementDisplayed(SUCCESSFUL_MSG, MIN_TIME, MAX_TIME);
	}
	
	
		
}
