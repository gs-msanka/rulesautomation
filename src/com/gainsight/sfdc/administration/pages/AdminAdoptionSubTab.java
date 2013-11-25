package com.gainsight.sfdc.administration.pages;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminAdoptionSubTab extends BasePage {
	
	private final String READY_INDICATOR      = "//h2[contains(text(),'Adoption Measures')]/parent::td/following-sibling::td/div/input[@value='New']";
	private final String NEW                  = "//h2[contains(text(),'Adoption Measures')]/parent::td/following-sibling::td/div/input[@value='New']";
	private final String AMEASURE_NAME        = "//span[contains(text(),'Adoption Measure Types')]/parent::h2/parent::div//following::div/input";
	private final String AMESURE_DISPLYORDER  = "//span[contains(text(),'Adoption Measure Types')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String AMESURE_SYSTEM_NAME  = "//span[contains(text(),'Adoption Measure Types')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String AMESURE_SHORT_NAME   = "//span[contains(text(),'Adoption Measure Types')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String SELECT_GROUPNAME     = "//select[contains(@class,'jbaraDummyAdminSelectCtrl')]";   //As of now out of scope   
	private final String INCLUDE_IN_ADOPTION  = "//input[@class='jbaraDummyAdminCheckboxCtrlAdoptionMeasure']";
	private final String INCLUDE_USAGETRACKER = "//input[@class='jbaraDummyAdminCheckboxCtrlUsageTracking']";
	private final String MEASURE_SAVE         = "//span[contains(text(),'Adoption Measure Types')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String MEASURE_CANCEL       = "//span[contains(text(),'Adoption Measure Types')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	private final String TABLE_VALUES_MESURE  = "j_id0:j_id14:j_id137:j_id147";
	private final String TEXT_PRESENT         = "//span[text()='Adoption Measure Types']";  
	
	private final String USAGE_CONFIG       = "//input[@class='btn dummyAdoptionConfig'] ";
	private final String EDIT               = "//input[@class='btn dummyBtnEditAggregationType']";
	private final String LIGHTBOX_TEXT      = "//h2[@id='InlineEditDialogTitle' and contains(text(),'Usage Data Configuration')]";
	private final String FORM_NONE          = "//div[contains(@class,'jbaraDummyAggregationTypeEditForm') and contains(@style,'display: none')]";
	private final String FORM_BLOCK         = "//div[contains(@class,'jbaraDummyAggregationTypeEditForm') and contains(@style,'display: block')]";
	private final String MESURE_FORM_BLOCK = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String MESURE_FORM_NONE  = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String ENABLE_ADV_USAGE   = "//input[@id='chkEnableAggregation']";
	private final String USER_LEVEL         = "//input[@id='rbtnUSERLEVEL']";
	private final String INSTANCE_LEVEL     = "//input[@id='rbtnINSTANCELEVEL']";
	private final String ACCOUNT_LEVEL_AGG  = "//input[@id='chkEnableInstanceAccount']";
	private final String ACCOUNT_LEVEL      = "//input[@id='rbtnACCOUNTLEVEL']";
	
	private final String LD_DATA_BY_MONTHLY = "//select[@id='weekMonthSelection']";
	private final String WEEK_STARTS_ON     = "//select[@id='weekStartSelection']";
	private final String WEEK_LABEL_BASED_ON= "//select[@id='weekLabelSelection']";
	private final String SAVE               = "//input[@class='btn dummySaveAggregationBtn']";
	private final String CANCEL             = "//input[@class='btn']";
	
	private final String COLUMN_MAP         = "//input[@class='btn dummyMeasureMapColumnBtn']";
	private final String MAP_AGGRE            = "//input[@class='btn dummyColumnAggregationNewBtn']";
	private final String LINKED_COL         = "//select[@class='ddColumnName']";
	private final String CONGIURE           = "//input[@class='btn licensedUsersSettingsBtn']";
	
	public AdminAdoptionSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	                      //Create New Adoption Measure
   public AdminAdoptionSubTab createAdoptionMeasure(String name, String displayOrder, String systemName, String shortName) {
		button.click(NEW);
		wait.waitTillElementDisplayed(MESURE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(TEXT_PRESENT);
		field.clearAndSetText(AMEASURE_NAME,name);
		field.clearAndSetText(AMESURE_DISPLYORDER, displayOrder);
		field.clearAndSetText(AMESURE_SYSTEM_NAME, systemName);
		field.clearAndSetText(AMESURE_SHORT_NAME, shortName);
		item.click(INCLUDE_IN_ADOPTION);
		item.click(INCLUDE_USAGETRACKER);
		button.click(MEASURE_SAVE);
		wait.waitTillElementPresent(MESURE_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
	return this;
	}
   public boolean isAdoptionMeasurePresent(String values){
		Boolean result = false;
		WebElement Measuretable =item.getElement(TABLE_VALUES_MESURE);
		String tableId = Measuretable.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
   
                 //Edit Adoption Measure
   public AdminAdoptionSubTab editAdoptionMeasure(String s,String name, String displayOrder, String shortName) {
	   wait.waitTillElementPresent("//span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a", MIN_TIME, MAX_TIME);
		item.click("//span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a");
		wait.waitTillElementDisplayed(MESURE_FORM_BLOCK, MIN_TIME, MAX_TIME);
	item.isElementPresent(TEXT_PRESENT);
		field.clearAndSetText(AMEASURE_NAME,name);
		field.clearAndSetText(AMESURE_DISPLYORDER, displayOrder);
		field.clearAndSetText(AMESURE_SHORT_NAME, shortName);
		button.click(MEASURE_SAVE);
		wait.waitTillElementPresent(MESURE_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
	return this;
	}
   public boolean isAdoptionMeasureEdited(String values){
		Boolean result = false;
		WebElement Measuretable =item.getElement(TABLE_VALUES_MESURE);
		String tableId = Measuretable.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
                                   //Delete Adoption Measure
   public AdminAdoptionSubTab deleteAdoptionMeasure(String s ) {
	   wait.waitTillElementPresent("//span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		refreshPage();
	return this;
	}
   public boolean isAdoptionMeasureDeleted(String values){
		Boolean result = false;
		WebElement Measuretable =item.getElement(TABLE_VALUES_MESURE);
		String tableId = Measuretable.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
       
      	/** This method will take care for Usage configuration..
   	 * This will internally call the selectDataGranularity , selectTimeGranularity methods ..  
   	 * 
   	 */
	public AdminAdoptionSubTab usageConfiguration(String UsageConfig, String Granularity , String WeekStartsOn , String WeekLabel ) {
		
		button.click(USAGE_CONFIG);
		wait.waitTillElementDisplayed(EDIT, MIN_TIME , MAX_TIME);
		button.click(EDIT);
		System.out.println("Clicked on Edit:--");
		wait.waitTillElementDisplayed(FORM_BLOCK,  MIN_TIME , MAX_TIME);
		           //Calling methods
		selectDataGranularity(UsageConfig);
		selectTimeGranularity(Granularity,WeekStartsOn, WeekLabel);
		return this;
	}
	
	private final String COLMAP_FORM_BLOCK = "//div[contains(@class,'jbaraDummyMeasureColumnMapForm') and contains(@style,'display: block')]";
	private final String COLMAP_FORM_NONE  = "//div[contains(@class,'jbaraDummyMeasureColumnMapForm') and contains(@style,'display: none')]";
	private final String CLOSE_PRESENT     = "//img[@onclick='jbaraCloseMeasureColumnMapForm()']/.";
	
	
/*	public AdminAdoptionSubTab measureCloumnMapping () {
		
		button.click(COLUMN_MAP);
		wait.waitTillElementDisplayed(COLMAP_FORM_BLOCK, MIN_TIME , MAX_TIME);
	if(item.isElementPresent(CLOSE_PRESENT)) {
		
		

		
		
		} 
		return this;	
	}
	public boolean isAdoptionMeasureColPresent(String values) {
		Boolean result = false;
		WebElement MeasureColtable =item.getElement("//table[@id='tblMeasureColumnForm']");
		String tableId = MeasureColtable.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
	*/
	/*private final String AGGRE_FORM_BLOCK   = "//div[contains(@class,'jbaraDummyColumnAggregationTypeForm') and contains(@style,'display: block')]";
	private final String AGGRE_FORM_NONE   = "//div[contains(@class,'jbaraDummyColumnAggregationTypeForm') and contains(@style,'display: none')]";
	private final String AGGRE_TEXT_PRESENT = "//img[@onclick='jbaraCloseColumnAggregationTypeForm()']";
	
	public AdminAdoptionSubTab columnAggregationType() { 
		button.click(MAP_AGGRE);
	wait.waitTillElementPresent(AGGRE_FORM_BLOCK, MIN_TIME , MAX_TIME);
    if(item.isElementPresent(AGGRE_TEXT_PRESENT)) {
    	
    	
    	
    }
		
		return null;
	}
	*/
	
	

	/** Data Granularity :Select the Load Usage Data:--User Level,Instance Level, Account Level  
	 * @param UsageConfig should be sent from Xl sheet.
	 */
  public AdminAdoptionSubTab selectDataGranularity(String UsageConfig) {
	if(item.isElementPresent(LIGHTBOX_TEXT)) {
			WebElement Cbox =item.getElement(ENABLE_ADV_USAGE);
			String Cboxres = Cbox.getAttribute("checked");
	   if(Cboxres == null) {
				item.click(ENABLE_ADV_USAGE);
				System.out.println("Selecting the element:--");
				} 			
		//Data Granularity :Select the Load Usage Data:--User Level
	     if(UsageConfig.equalsIgnoreCase("Userlevel")) {
			item.click(USER_LEVEL);
			System.out.println("User Level:--"+ UsageConfig);	
			}
			              //Load usage data by Instance
	     else if(UsageConfig.equalsIgnoreCase("InstanceLevel")) {
		     item.click(INSTANCE_LEVEL);
		     wait.waitTillElementDisplayed(ACCOUNT_LEVEL_AGG, MIN_TIME , MAX_TIME);    
		     item.click(ACCOUNT_LEVEL_AGG);
				}          //Load usage data by Account
		 else if(UsageConfig.equalsIgnoreCase("AccountLevel")) {
				System.out.println("Account Level");	
			item.click(ACCOUNT_LEVEL);
				System.out.println("Instance Level"+ UsageConfig);	
		} else {
			  System.out.println("No Test data to the select the Usage data");
			}		} else {
			System.out.println("No Light BOx Text Present Element Present:--"); }
		return this;
	} 
	

/** Data Granularity :Select Time Granularity:--Like Weekly/Monthly WeekStartson and Week Label based on(Start of the day) 
 * @param should be sent from xlsheet.
 */
 	 public AdminAdoptionSubTab selectTimeGranularity(String Granularity ,String WeekStartsOn, String WeekLabel) {
		  if(Granularity.equalsIgnoreCase("Month")) {
				field.selectFromDropDown(LD_DATA_BY_MONTHLY, Granularity);
					//Save 
				button.click(SAVE);
				wait.waitTillElementPresent(FORM_NONE,  MIN_TIME , MAX_TIME);
			  }
		  else if(Granularity.equalsIgnoreCase("Week")) {
			  field.selectFromDropDown(LD_DATA_BY_MONTHLY, Granularity);
			  //Week starts on
			field.selectFromDropDown(WEEK_STARTS_ON, WeekStartsOn);
			  //Week label based on
			field.selectFromDropDown(WEEK_LABEL_BASED_ON, WeekLabel);
			  //save
			  button.click(SAVE);
			  wait.waitTillElementPresent(FORM_NONE,  MIN_TIME , MAX_TIME);
		  } else {
			  System.out.println("No Match found to select Time Granularity");
		  }
		return this;
		  
		  }
	
	
	
}
