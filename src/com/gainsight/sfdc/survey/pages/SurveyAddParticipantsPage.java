package com.gainsight.sfdc.survey.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.testdriver.Log;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAddParticipantsPage extends SurveyPage {
	
	//Objects under Search Section
	private final String LOAD_SURVEY_PARTICIPANTS_FROM      = "//p[@class='mb10 clearfix']/button";
	private final String CONTACTS_LINK                      ="//div[contains(@class,'ui-multiselect-menu')]/ul[contains(@class,'ui-multiselect-checkboxes')]/li[contains(@class,'ui-multiselect')]/label/span[text()='Contact Object']";
	private final String SEARCH_TITLE                       ="//div[contains(@class,'ui-multiselect-menu')]/div[1]/div[@class='ui-multiselect-filter']/input";	
	private final String SELECT_ROLE_CONTACT                = "//input[@class='contact-type frmcontact' and @type='radio']";
	private final String SELECT_TITLE                       = "//span[@class = 'ui-multiselect-selected-label' and contains(text(),'Title')]";
	private final String SELECT_A_FIELD                     = "//label[contains(text(),'Role Field :')]//following-sibling::div//button[@type='button']";
	private final String OPERATOR                           = "//span[@class = 'ui-multiselect-selected-label gs-rb-filter-' and contains(text(),'--None--')]";
	private final String VALUE                              = "//input[@class = 'for-width form-control text']";	
	private final String SELECT_CUSTOM_OBJECT_LIST          ="//div[@class='userroles participants-roles']/p[@class='mb10 clearfix']/button";
	private final String SELECT_CUSTOM_FROM_LIST            ="//div[contains(@class,'ui-multiselect-menu')]/ul[@class='ui-multiselect-checkboxes ui-helper-reset']/li[2]/label[contains(@for,'option-1')]/span[text()='Custom Object']";
	private final String CLICK_CUSTOM_OBJECT                ="//div[@class='custom-object-properties']/div[@class='form-group clearfix']/div[@class='col-sm-6 nopadding']/button[contains(@class,'ui-multiselect')]";
	private final String ENTER_CUSTOM_OBJECT                ="//div[13]/div[contains(@class,'ui-multiselect-hasfilter')]/div[@class='ui-multiselect-filter']/input[@type='text']";
	private final String CLICK_CUSTOM_NAME                  ="//div[@class='form-group clearfix custom-option']/div[@class='col-sm-6 nopadding']/button";
	private final String ENTER_CUSTOM_NAME                  ="//div[14]/div[contains(@class,'ui-widget-header ui-corner-all')]/div[@class='ui-multiselect-filter']/input[@type='text']";
	//Elements when selected to load from Load Survey participants from Custom Object
	private final String SELECT_CUSTOM_OBJECT               = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Select Object')]";	
	//Elements when selected to load from Load Survey participants from CSV File
	private final String UPLOAD_CSV                         = "//span[@class='fileupload-new-inside' and contains(text(),'Upload CSV')]";
	private final String EXCLUDE_PARTICIPANTS               = "//span[@class='ui-multiselect-selected-label' and contains(text(),'None')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Contacted')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Responded')]";
	private final String EXCLUDE_PARTICIPANTS_OPTIONS       = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Select options')]";
	private final String SAVE_AND_SHOW_CONTACTS_BTN         = "//a[contains(text(),'Save & Show Contacts')]";
	private final String TITLE_SEARCH                       ="//ul[contains(@class, 'ui-multiselect')]/li[24]/label/span[text()='Title']";
	//Objects under Add Participants Section
	private final String SELECT_ALL = "//input[@id='isLoadAll']";
	
	private final String SELECT_ALL_DISPLAYED_IN_GRID          = "//input[@id='cb_show-contacts-tbl']";
	private final String SELECTCONTACTTOADD_CHCKBOX            = "//input[@id='jqg_show-contacts-tbl_'+%d]"; //Need Review - //input[@id='jqg_show-contacts-tbl_1']
	private final String Survey_TITLE                          =  "//h3[@class='leftinner-title']/span[text()='%s']";
	
	//Elements in Filter Section
	private final String FIELD_SELECT                          ="//div[@class='gs-condition-lhs pull-left']/descendant::div[@class='gs_att_filtsmall']/button";
	private final String FIELD_ENTER                           ="//div[contains(@class, 'gs-filter-lhs')]/descendant::div[contains(@class,'ui-widget-header')]/div/input";
	private final String FIELD_CLICK                           ="//div[contains(@class, 'gs-filter-lhs ui-multiselect-single')]/ul[contains(@class, 'ui-multiselect-checkboxes')]/li[18]/label/span";
	private final String ENTER_FILTER                          ="//body/div[21]/div[1]/div[@class='ui-multiselect-filter']/input[@type='text']";
	private final String OPERATOR_SELECT                       ="//div[contains(@class, 'gs-condition-operator pull-left')]/div/button";
	private final String OPERATOR_CLICK                        ="//li[contains(@class, ' gs-rb-filter-c')]/label/span";
	private final String SELECT_CUSTOM_ROLE                    ="//div[16]/ul[contains(@class,'ui-multiselect')]/li[8]/label/span";
	private final String ENTER_VALUE                           ="//div[contains(@class, 'gs-condition-rhs')]/descendant::div[contains(@class, 'gs_att_filtsmall')]/input[@data-control='TEXT']";
	
	private final String ENTER_OPERATOR_FILTER                 ="//div[contains(@class, 'gs-filter-lhs')]/descendant::div[contains(@class,'ui-widget-header')]/div/input";
	private final String CLICK_OPERATOR_FILTER                 ="//div[contains(@class, 'gs-filter-lhs')]/ul[contains(@class, 'ui-multiselect-checkboxes')]/li[4]/label/span";
	private final String SELECT_FILTER                         ="//body/div[21]/ul[contains(@class,'ui-multiselect')]/li[4]/label[@class='ui-corner-all']/span";
	private final String SEARCH_OPERATOR_FILTER                ="//div[contains(@class, 'gs-condition-rhs')]/descendant::div[contains(@class, 'gs_att_filtsmall')]/input[@data-control='TEXT']";
	private final String SELECT_CUSTOM_NAME                    ="//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/li[7]/label/span[text()='Dis Name']";
	private final String CLICK_CUSTOM_EMAIL                    ="//div[contains(@class,'custom-obj-ctn')]/div[@class='custom-object-properties']/div[4]/div[@class='col-sm-6 nopadding']/button";
	private final String ENTER_CUSTOM_EMAIL                    ="//div[15]/div[contains(@class,'ui-widget-header ui-corner-all')]/div[@class='ui-multiselect-filter']/input[@type='text']";
	private final String SELECT_CUSTOM_EMAIL                   ="//div[15]/ul[contains(@class,'ui-multiselect')]/li[6]/label/span";
	private final String CLICK_CUSTOM_ROLE                     ="//div[contains(@class,'custom-obj-ctn')]/div[@class='custom-object-properties']/div[5]/div[@class='col-sm-6 nopadding']/button";
	private final String ENTER_CUSTOM_ROLE                     ="//div[16]/div[contains(@class,'ui-widget-header ui-corner-all')]/div[@class='ui-multiselect-filter']/input[@type='text']";
	private final String CLICK_FILTER                           ="//div[@class='custom-filter-view']/div/div[@class='conditionViewWrapper']/div[contains(@id, 'view')]/div[2]/div[@class='gs_att_filtsmall']/button";
	private final String ENTER_SEARCH_VALUE                    ="//div[@class='custom-filter-ctn claerfix']/descendant::div[@class='custom-filter-view']/div/descendant::div[@class='conditionViewWrapper']/div[@class='gs-condition-row clearfix']/div[4]/div[@class='gs_att_filtsmall value-select']/input";
	private final String ADD_TO_DISTRIBUTION_LIST                = "//a[contains(text(),'Add to Distribution List')]";
	private final String SUCCESSFUL_MSG                         = "//div[contains(text(),'Selected participants are added successfully')]";

	
	public SurveyAddParticipantsPage(String  surveyName) {
		super(surveyName);
		Timer.sleep(5);
		//wait.waitTillElementPresent(Survey_TITLE, MIN_TIME, MAX_TIME);
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
	
	public void loadFromContactObj(SurveyAddParticipants surveyparts){
		if(surveyparts.getLoadParticipantsFrom().equalsIgnoreCase("Contact Object")){
			Log.info("Selecting participants From Standard Object");
			item.click(LOAD_SURVEY_PARTICIPANTS_FROM);
			item.click(CONTACTS_LINK);
			
		if(surveyparts.getSelectRole().equalsIgnoreCase("Contact")){
			item.click(SELECT_ROLE_CONTACT);
			item.click(SELECT_TITLE);
			field.clearAndSetText(SEARCH_TITLE, surveyparts.getSelectRoleField());
			item.click(TITLE_SEARCH);
			//ContactFilterConditions(surveyparts);
		}
			
		}
	}
    
	public void ContactFilterConditions(SurveyAddParticipants surveyparts){
		Log.info("Selecting Filter Conditions");
		button.click(FIELD_SELECT);
		if (surveyparts.getEmailSearch().equalsIgnoreCase("gainsight.com")) {
			field.setText(FIELD_ENTER, surveyparts.getSelectRoleField());
			item.click(FIELD_CLICK);
			button.click(OPERATOR_SELECT);
			item.click(OPERATOR_CLICK);
			field.setText(ENTER_VALUE, surveyparts.getEmailSearch());
		}
		else {
		    field.setText(ENTER_OPERATOR_FILTER, surveyparts.getFilter());
		    item.click(CLICK_OPERATOR_FILTER);
		    Log.info("Entering Account Name/Others From Pojo Class");
		    field.setText(SEARCH_OPERATOR_FILTER, surveyparts.getValue());
	     }
		loadParticipants();
	}
	
	public void loadFromCustomObject(SurveyAddParticipants surveyparts){
		
		if(surveyparts.getLoadParticipantsFrom().equalsIgnoreCase("Custom Object")){
		Log.info("Selecting Custom Object");
		item.click(SELECT_CUSTOM_OBJECT_LIST); //click custom obj dropdown
		item.click(SELECT_CUSTOM_FROM_LIST); //select custom obj frm list
		item.click(CLICK_CUSTOM_OBJECT);  // clicking Select Custom object
		Log.info("Selecting Custom Object from Pojo Class");
		field.setText(ENTER_CUSTOM_OBJECT, surveyparts.getcustomobjectname());	
		item.click("//body/div[13]/ul/li[16]/label/span");
		Timer.sleep(5);
		CustomObjectProperties(surveyparts);
		
		}

	}
	
	public void CustomObjectProperties(SurveyAddParticipants surveyparts){
		Log.info("Selecting Name");
		item.click(CLICK_CUSTOM_NAME);
		field.setText(ENTER_CUSTOM_NAME, surveyparts.getDisplayName());
		item.click(SELECT_CUSTOM_NAME); // selecting name
		button.click(CLICK_CUSTOM_EMAIL); //clicking email
		Log.info("Selecting Email");
		field.setText(ENTER_CUSTOM_EMAIL, surveyparts.getDisplayEmail());
		item.click(SELECT_CUSTOM_EMAIL); // selecting Email
		Log.info("Slecting Role From List");
		button.click(CLICK_CUSTOM_ROLE);
		field.setText(ENTER_CUSTOM_ROLE, surveyparts.getDisplayRole());
		item.click(SELECT_CUSTOM_ROLE);
		//CustomFilterConditions(addPtp);
	    }
	
	public void CustomFilterConditions(SurveyAddParticipants surveyparts){
		 Log.info("Selecting Filter Conditions");
		 button.click(CLICK_FILTER);
		 field.setText(ENTER_FILTER, surveyparts.getFilter());
		 item.click(SELECT_FILTER);
		 field.setText(ENTER_SEARCH_VALUE, surveyparts.getValue());
		 loadParticipants();
		 
	}    
	
	public void loadParticipants() {
		 Log.info("Clicking on Show Contacts");
		 link.click("Save & Show Contacts");
		 item.click("//input[contains(@id, 'show-contacts')]");
		 Log.info("Adding to Distribution List");
		 link.click("+ Add to Distribution List");
		 //waitTillNoLoadingIcon();
		 Log.info("Waiting till contacts added to the List");
		 wait.waitTillElementDisplayed(SUCCESSFUL_MSG, MIN_TIME, MAX_TIME);
		 Timer.sleep(5);
	}
	
    public String getMessage(){
    	String result=element.getElement(By.xpath("//div[contains(@class, 'gs-add-participants')]/div")).getText();
        System.out.println(result);
		return result;
    }
}
