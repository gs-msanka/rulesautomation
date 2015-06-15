package com.gainsight.sfdc.survey.pages;


import java.io.File;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebElement;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.survey.pojo.SurveyAddParticipants;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

/**
 * Created by gainsight on 05/12/14.
 */
public class SurveyAddParticipantsPage extends SurveyPage {
	
	//Objects under Search Section
	private final String LOAD_SURVEY_PARTICIPANTS_FROM      = "//p[@class='mb10 clearfix']/button";
	private final String SELECT_ROLE_CONTACT                = "//input[@class='contact-type frmcontact' and @type='radio']";
	private final String SELECT_TITLE                       = "//select[contains(@class, 'selectcontact')]/following-sibling::button";
	private final String SELECT_A_FIELD                     = "//label[contains(text(),'Role Field :')]//following-sibling::div//button[@type='button']";
	private final String OPERATOR                           = "//span[@class = 'ui-multiselect-selected-label gs-rb-filter-' and contains(text(),'--None--')]";
	private final String VALUE                              = "//input[@class = 'for-width form-control text']";	
	private final String SELECT_CUSTOM_OBJECT_LIST          = "//select[contains(@class, 'selectcustomobject')]/following-sibling::button";
	private final String CUSTOM_OBJECT_CONFIRMATION_DILOG   = "//input[contains(@class, 'saveSummary')]";
	private final String CLICK_CUSTOM_NAME                  = "//div[@class='form-group clearfix custom-option']/div[@class='col-sm-6 nopadding']/button";	
	//Elements when selected to load from Load Survey participants from CSV File
	private final String UPLOAD_CSV                         = "//div[contains(@class, 'fileupload-new')]/span";
	private final String EXCLUDE_PARTICIPANTS               = "//span[@class='ui-multiselect-selected-label' and contains(text(),'None')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Contacted')]|//span[@class='ui-multiselect-selected-label' and contains(text(),'Responded')]";
	private final String EXCLUDE_PARTICIPANTS_OPTIONS       = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Select options')]";
	private final String SAVE_AND_SHOW_CONTACTS_BTN         = "//a[contains(text(),'Save & Show Contacts')]";
	//Objects under Add Participants Section
	private final String SELECT_ALL = "//input[@id='isLoadAll']";
	
	private final String SELECT_ALL_DISPLAYED_IN_GRID          = "//input[@id='cb_show-contacts-tbl']";
	private final String SELECTCONTACTTOADD_CHCKBOX            = "//input[@id='jqg_show-contacts-tbl_'+%d]"; //Need Review - //input[@id='jqg_show-contacts-tbl_1']
	private final String Survey_TITLE                          = "//h3[@class='leftinner-title']/span[text()='%s']";
	
	//Elements in Filter Section
	private final String FIELD_SELECT                          = "//div[@class='gs-condition-lhs pull-left']/descendant::div[@class='gs_att_filtsmall']/button";
	private final String OPERATOR_SELECT_INPUT                 = "//div[contains(@class, 'custom-filter-view')]/descendant::select[@class='operator']/following-sibling::button";
	private final String ENTER_VALUE                           = "//div[contains(@class, 'gs-condition-rhs')]/descendant::div[contains(@class, 'gs_att_filtsmall')]/input[@data-control='TEXT']";
	private final String SEARCH_OPERATOR_FILTER                = "//div[@class='gs-condition-rhs pull-left']/descendant::input[@data-control='TEXT']";
	private final String CLICK_CUSTOM_EMAIL                    = "//select[contains(@class, 'custommail form-select')]/following-sibling::button";
	private final String CLICK_CUSTOM_ROLE                     = "//select[contains(@class, 'cusotmrole form-select')]/following-sibling::button";
	private final String CLICK_FILTER                          = "//div[contains(@class, 'custom-filter-view')]/descendant::select/following-sibling::button";
	private final String ENTER_SEARCH_VALUE                    = "//div[contains(@class, 'custom-filter-view')]/descendant::div[@class='conditionViewWrapper']/descendant::div[contains(@class, 'gs-condition-row')]/descendant::div[contains(@class, 'gs-condition-rhs')]/descendant::input";
	private final String ADD_TO_DISTRIBUTION_LIST              = "//a[contains(text(),'Add to Distribution List')]";
	private final String SUCCESSFUL_MSG                        = "//div[contains(text(),'Selected participants are added successfully')]";
	private final String SHOW_CONTACTS                         = "//div[@class='filter-add']/descendant::a";
	private final String SELECT_ALL_CONTACTS                   = "//input[contains(@id, 'show-contacts')]";
	private final String ADD_TO_DISTRIBUTION_LISTS             = "//div[contains(@class, 'gs-participants-header')]/descendant::div[@class='participant-load-all']/following-sibling::a";
	private final String OPERATOR_SELECT                       = "//select[contains(@class, 'operator')]/following-sibling::button";
	
	private final String CSV_UPLOAD_BROWSE_BUTTON              = "//div[contains(@class, 'fileUpload gs-btn btn-add')]/input";
	private final String CSV_UPLOAD_DIV                        = "//div[@class='csv-field-inputs-ctn']";
	private final String CSV_CONTACT_WITH_ID_RADIOBUTTON       = "//div[@class='csv-field-inputs-ctn']/descendant::input[@class='contact-id']";
	private final String CONTACT_ID_SELECTION                  = "//select[contains(@class, 'csv-contact')]/following-sibling::button";
	private final String CONTACT_EMAIL_SELECTION               = "//select[contains(@class, 'csv-mail')]/following-sibling::button";
	private final String CONTACT_ROLE_SELECTION                = "//select[contains(@class, 'csv-role')]/following-sibling::button";
	private final String CONTACT_NAME_SELECTION                = "//select[contains(@class, 'csv-name')]/following-sibling::button";
	
	
	private final String CONTACTS_CSV = Application.basedir+"/testdata/sfdc/survey/SurveyContactsWithIDS.csv";

	
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
	
	public void loadFromContactObject(SurveyAddParticipants surveyParticipants) {
		if (surveyParticipants.getLoadParticipantsFrom().equalsIgnoreCase(
				"Contact Object")) {
			Log.info("Selecting participants From Standard Object");
			wait.waitTillElementDisplayed(LOAD_SURVEY_PARTICIPANTS_FROM,
					MIN_TIME, MAX_TIME);
			item.click(LOAD_SURVEY_PARTICIPANTS_FROM);
			selectValueInDropDown(surveyParticipants.getLoadParticipantsFrom());
			if (surveyParticipants.getSelectRole().equalsIgnoreCase("Contact")) {
				item.click(SELECT_ROLE_CONTACT);
				item.click(SELECT_TITLE);
				selectValueInDropDown(surveyParticipants.getSelectRoleField());
			}
		}
	}
    
	public void filterConditions(SurveyAddParticipants surveyParticipants){
		Log.info("Selecting Filter Conditions");
		button.click(FIELD_SELECT);
		if (surveyParticipants.getSearchFilter().equalsIgnoreCase("QA")) {
			selectValueInDropDown(surveyParticipants.getFilter());
			button.click(OPERATOR_SELECT);
			selectValueInDropDown(surveyParticipants.getOperator());
			field.setText(ENTER_VALUE, surveyParticipants.getSearchFilter());
		} else {
			selectValueInDropDown(surveyParticipants.getFilter());
			button.click(OPERATOR_SELECT);
			selectValueInDropDown(surveyParticipants.getOperator());
			Log.info("Entering Account Name/Others From Pojo Class");
			field.setText(SEARCH_OPERATOR_FILTER, surveyParticipants.getValue());
		}
		loadParticipants();
	}
	
	public void loadFromCustomObject(SurveyAddParticipants surveyParticipants){
		if (surveyParticipants.getLoadParticipantsFrom().equalsIgnoreCase(
				"Custom Object")) {
			Log.info("Selecting Custom Object");
			wait.waitTillElementDisplayed(LOAD_SURVEY_PARTICIPANTS_FROM,
					MIN_TIME, MAX_TIME);
			item.click(LOAD_SURVEY_PARTICIPANTS_FROM);
			selectValueInDropDown(surveyParticipants.getLoadParticipantsFrom());
			wait.waitTillElementDisplayed(SELECT_CUSTOM_OBJECT_LIST, MIN_TIME,
					MAX_TIME);
			item.click(SELECT_CUSTOM_OBJECT_LIST);
			Log.info("Selecting Custom Object");
			selectValueInDropDown(surveyParticipants.getCustomObjectName());
			if (element.isElementPresent(CUSTOM_OBJECT_CONFIRMATION_DILOG)) {
				item.click(CUSTOM_OBJECT_CONFIRMATION_DILOG);
			}
			customObjectProperties(surveyParticipants);
		}
	}
	
	public void customObjectProperties(SurveyAddParticipants surveyParticipants){
		Log.info("Selecting Name from Custom Object");
		item.click(CLICK_CUSTOM_NAME);
		selectValueInDropDown(surveyParticipants.getDisplayName());
		item.click(CLICK_CUSTOM_EMAIL);
		Log.info("Selecting Email from Custom Object");
		selectValueInDropDown(surveyParticipants.getDisplayEmail());
		Log.info("Selecting Role from Custom Object");
		item.click(CLICK_CUSTOM_ROLE);
		selectValueInDropDown(surveyParticipants.getDisplayRole());
	}
	
	public void customFilterConditions(SurveyAddParticipants surveyParticipants){
		Log.info("Selecting Filter Conditions");
		item.click(CLICK_FILTER);
		selectValueInDropDown(surveyParticipants.getFilter());
		item.click(OPERATOR_SELECT_INPUT);
		selectValueInDropDown(surveyParticipants.getOperator());
		field.setText(ENTER_SEARCH_VALUE, surveyParticipants.getValue());
		loadParticipants();
	}
	
	public void loadParticipants() {
		Log.info("Clicking on Show Contacts");
		item.click(SHOW_CONTACTS);
		wait.waitTillElementDisplayed(ADD_TO_DISTRIBUTION_LISTS, MIN_TIME,
				MAX_TIME);
		item.click(SELECT_ALL_CONTACTS);
		Log.info("Adding to Distribution List");
		item.click(ADD_TO_DISTRIBUTION_LISTS);
		Log.info("Waiting till contacts added to the List");
		wait.waitTillElementDisplayed(SUCCESSFUL_MSG, MIN_TIME, MAX_TIME);
	}

    public String getMessage(){
    	String result=element.getElement(By.xpath("//div[contains(@class, 'gs-add-participants')]/div")).getText();
        System.out.println(result);
		return result;
    }
    
    public void loadContactsFromCSV(SurveyAddParticipants surveyParticipants){
		if (surveyParticipants.getLoadParticipantsFrom().equalsIgnoreCase(
				"CSV File")) {
			Log.info("Selecting Custom Object");
			wait.waitTillElementDisplayed(LOAD_SURVEY_PARTICIPANTS_FROM,
					MIN_TIME, MAX_TIME);
			item.click(LOAD_SURVEY_PARTICIPANTS_FROM);
			selectValueInDropDown(surveyParticipants.getLoadParticipantsFrom());
			element.getElement(UPLOAD_CSV).click();
			List<WebElement> frameList = Application.getDriver().findElements(
					By.tagName("iframe"));
			Log.info("Number of frames are" + " " + frameList.size());
			try {
				Application.getDriver().switchTo().frame(2);
			} catch (NoSuchFrameException e) {
				e.getStackTrace();
			}
			File file = new File(CONTACTS_CSV);
			String absolutePath = file.getAbsolutePath().replace("\\", "\\\\");
			Log.info(absolutePath);
			field.setText(CSV_UPLOAD_BROWSE_BUTTON, absolutePath);
			Application.getDriver().switchTo().defaultContent();
			wait.waitTillElementDisplayed(CSV_UPLOAD_DIV, MIN_TIME, MAX_TIME);
		}
	}
    
    public void contactsFromCSVWithID(SurveyAddParticipants surveyParticipants){
       //Timer.sleep(10);
		item.click(CSV_CONTACT_WITH_ID_RADIOBUTTON);
		wait.waitTillElementDisplayed(CONTACT_ID_SELECTION, MIN_TIME, MAX_TIME);
		item.click(CONTACT_ID_SELECTION);
		Log.info("Selecting contact ID from the List");
		selectValueInDropDownList(surveyParticipants.getContactID());
		item.click(CONTACT_EMAIL_SELECTION);
		Log.info("Selecting contact Email from the List");
		selectValueInDropDownList(surveyParticipants.getContactEmail());
		item.click(CONTACT_ROLE_SELECTION);
		Log.info("Selecting contact Role from the List");
		selectValueInDropDownList(surveyParticipants.getContactRole());
		item.click(CONTACT_NAME_SELECTION);
		Log.info("Selecting contact Name from the List");
		selectValueInDropDownList(surveyParticipants.getContactName());
		item.click(ADD_TO_DISTRIBUTION_LIST);
    }
}
