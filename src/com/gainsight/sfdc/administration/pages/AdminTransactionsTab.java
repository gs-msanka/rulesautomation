package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminTransactionsTab extends BasePage {
	
	private final String READY_INDICATOR      ="//div[@id='Administration-Transactions']/descendant::input[@class='btn dummyAllAdminNewBtn' and @type='button' and @value='New']";
	private final String NEW_BOOKINGTYPES     = "//div[@id='Administration-Transactions']/descendant::input[@class='btn dummyAllAdminNewBtn' and @type='button' and @value='New']";
	private final String BOOKINGT_NAME        = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input";
	private final String BOOKINGT_DISPLYORDER = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String BOOKINGT_SYSTEM_NAME = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String BOOKINGT_SHORT_NAME  = "//span[contains(text(),'Booking Type')]/parent::h2//following::tbody/tr[5]/td[2]/input";
    private final String BOOKINGTYPESELECT    = "//select[@class='mapToBookingTypeSelectClass']";
	private final String BOOKING_SAVE         = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String TABLE_VALUES_BTYPES  = "//table[contains(@id,'transactionBookingTypesPanel')]";
	
	         //Map Booking Types
	private final String CHECK_ACTIVATION     = "//table[@id='tableSortable']/tbody/tr[contains(.,'Activation')]//input";
	private final String SAVE_MAP_BUTTON      = "//div[@class='overlayDialog inlineEditDialog jbaraDummyAdminBookingTypesInputForm bPageBlock ui-draggable']/descendant::input[@class='btn dummyAllAdminSaveBtn']";
	private final String CHECK_SERVICES       = "//table[@id='tableSortable']/tbody/tr[contains(.,'Services')]//input";
	
	                             //TRANSACTIONS LINE ITEMS
	private final String NEW_TRANS_LINE_ITEMS = "//td[contains(@id,'Transactions')]/div/input";
	private final String LINT_ITEM_NAME       = "//input[@class='jbaraDummyAdminTransactionTypeInputCtrl subjectInput']";
	private final String LINE_ITEM_TYPE       = "//select[@class='jbaraDummyAdminTransactionTypeSelectCtrl']";
	private final String TABLE_VALUES_LITEMS  = "//table[contains(@id,'Transactions')]";
	private final String SAVE_LITEMS          = "//input[@onclick='disableBtn(this);actionSaveAdminTransactionLine()']";
	private final String CANCEL_LITEMS        = "//input[@onclick='jbaraCloseAdminTransactionLineInputForm()' and @class='btn']";
	
	                                    //CHURN REASON
	private final String NEW_CHURN_REASON     = "//td[contains(@id,'churnReasonAdminIdBlock')]/div/input";
	private final String CREASON_NAME         = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input";
	private final String CREASON_DISPORDER    = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String CREASON_SYSTEM_NAME  = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String CREASON_SHOT_NAME    = "//span[contains(text(),'Churn Reason')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String CREASON_SAVE         = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[@value='Save']";   
	private final String CREASON_CANCEL       = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	
	                                  //Edit Settings
	private final String CHURN_EDIT_SETTINGS      = "//td/div/input[@class='btn dummyAllAdminNewBtn' and @value='Edit Settings']";
	private final String CHURN_SETTINGS_DOWNSELL  = "//input[@class='jbaraDummyAdminCheckboxCtrl checkboxChurnInput']";
	private final String TABLE_VALUES_CHURN       ="//table[contains(@id,'churnReasonAdminIdBlock')]";
	
	public AdminTransactionsTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	                               //Create Booking Types
	public AdminTransactionsTab addBookingTypes(String Name, String displayorder,String systemname ,String shortname, String bookingtypeselect) {       
		Report.logInfo("*************In the addBookingTypes method***********************");
		wait.waitTillElementPresent(NEW_BOOKINGTYPES, MIN_TIME, MAX_TIME);
		item.click(NEW_BOOKINGTYPES);
		fillFieldsForBookingTypes(Name, displayorder, systemname ,shortname, bookingtypeselect);
		return this;
	}               //Validation
	public boolean isBookingTypePresent(String values){
		Boolean result = false;
		WebElement Booktable1 =item.getElement(TABLE_VALUES_BTYPES);
		String tableId = Booktable1.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
		                           //Edit Booking Types
	public AdminTransactionsTab editbookingType(String s, String Name, String displayorder,String shortname) {                       
		Report.logInfo("*************In the Edit addBookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFields(Name,displayorder,shortname);
		return this;
	}
	                                //Map Booking Types
	public AdminTransactionsTab mapBookingTypes(String Name) {
		Report.logInfo("*************In the map addBookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+Name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+Name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminBookingTypesInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		item.click(CHECK_ACTIVATION);
		item.click(CHECK_SERVICES);
		button.click(SAVE_MAP_BUTTON);
		wait.waitTillElementNotDisplayed("//div[contains(@class,'jbaraDummyAdminBookingTypesInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent(NEW_BOOKINGTYPES, MIN_TIME, MAX_TIME);
		return this;
	}
	                                 //Delete Booking Types
	public AdminTransactionsTab deleteBookingTypes(String s) {
		Report.logInfo("*************In the Delete BookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']");
	    modal.accept(); 
	    wait.waitTillElementNotPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']", MIN_TIME, MAX_TIME);
	    refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']")) {
			System.out.println("Unable to delete the element");
		} else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Booking Type:**********");
		return this;
	}
                        	       //Create Transaction Line Items
	public AdminTransactionsTab addTransactionLinesItems(String Name,String type) {
		Report.logInfo("*************In the  addTransactionLinesItems method***********************");
		wait.waitTillElementPresent(NEW_TRANS_LINE_ITEMS, MIN_TIME, MAX_TIME);
		item.click(NEW_TRANS_LINE_ITEMS);
		 wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//h2[@id='InlineEditDialogTitle' and text()= 'Transaction Line Items']")) {
		     field.setTextField(LINT_ITEM_NAME,Name);
		   field.setSelectField(LINE_ITEM_TYPE,type);
		   button.click(SAVE_LITEMS);
		   wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		   refreshPage();
		   wait.waitTillElementPresent("//label[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		      System.out.println("****End of Transaction Line Items****");
		} else {
			System.out.println("This is not Transactions Line Items light box. This is :"+ item.getText("//span[@class='DialogTitleClass']")+"LightBox");
		}
		return this;
	}                
	                                 //validation of the table
	public boolean isTransactionLineItemPresent(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_LITEMS);
		String tableId = Linetable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		System.out.println("****Result:***"+ result);
		return result;
	}
		                               // Edit Transaction Line Items
	public AdminTransactionsTab editTransactionLineItem(String s,String Name,String type) {
		Report.logInfo("*************In the edit  addTransactionLinesItems method***********************");
		   wait.waitTillElementPresent("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]", MIN_TIME, MAX_TIME);
			item.click("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]");
			wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
			if(item.isElementPresent("//h2[@id='InlineEditDialogTitle' and text()= 'Transaction Line Items']")) {
			field.clearAndSetText(LINT_ITEM_NAME,Name);
			field.setSelectField(LINE_ITEM_TYPE,type);
			button.click(SAVE_LITEMS);
			//wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			wait.waitTillElementPresent("//label[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
			refreshPage();
			 System.out.println("End of Edit Trans line Item");
			} else {
				System.out.println("This is not Transactions Line Items light box. This is :"+ item.getText("//span[@class='DialogTitleClass']")+"LightBox");
			} return this;
	}
	                                         //Delete Transaction Line Items
	public AdminTransactionsTab deleteTransactionLineItem(String Name) {
		Report.logInfo("*************In the delete  addTransactionLinesItems method***********************");
		wait.waitTillElementPresent("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]", MIN_TIME, MAX_TIME);
		item.click("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]");
		modal.accept();
		wait.waitTillElementNotPresent("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]", MIN_TIME, MAX_TIME);
		refreshPage();
		Report.logInfo("Checking whether the element is deleted");
		if(item.isElementPresent("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]")) {
			System.out.println("Unable to delete the element");
		} else {
			System.out.println("Element got deleted");
		}
		System.out.println("**********End of Delete Alert Type:**********");
		return this;
	}
	                                        //--add Churn Reason
	public AdminTransactionsTab addChurnReason(String Name, String displayorder,
            String systemname , String shortname) {
		Report.logInfo("*************In the   addChurnReason method***********************");
        wait.waitTillElementPresent(NEW_CHURN_REASON, MIN_TIME, MAX_TIME);
		item.click(NEW_CHURN_REASON);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Churn Reason')]")) {
		field.clearAndSetText(CREASON_NAME,Name);
		field.clearAndSetText(CREASON_DISPORDER,displayorder);
		field.clearAndSetText(CREASON_SYSTEM_NAME,systemname);
		field.clearAndSetText(CREASON_SHOT_NAME,shortname);  
		button.click(CREASON_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
        wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("This is not add churn Reason light box. This is :"+ item.getText("//span[@class='DialogTitleClass']")+"LightBox");	
		}return this;
	}	
		public boolean IsChurnPresent(String values){
			Boolean result = false;
			WebElement Linetable2 =item.getElement(TABLE_VALUES_CHURN);
			String tableId = Linetable2.getAttribute("Id");
			int a = table.getValueInListRow(tableId, values);
			if(a != -1) {
				result = true;
			}
			System.out.println("****Result:***"+ result);
			return result;
		}
	                                        //Edit ChurnReason
	public AdminTransactionsTab editChurnReason(String s,String Name, String displayorder,String shortname) {
		Report.logInfo("*************In the Edit  ChurnReason method***********************");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Churn Reason')]")) {
			field.clearAndSetText(CREASON_NAME,Name);
			field.clearAndSetText(CREASON_DISPORDER,displayorder);
			field.clearAndSetText(CREASON_SHOT_NAME,shortname);  
			button.click(CREASON_SAVE);
			wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
			refreshPage();
			//wait.waitTillElementPresent("//label[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("This is not add churn Reason light box. This is :"+ item.getText("//span[@class='DialogTitleClass']")+"LightBox");
			button.click(CREASON_CANCEL);
		}
	return this;
	}
	                                            //Delete Churn Reason
	public AdminTransactionsTab deleteChurnReason(String Name) {
		Report.logInfo("*************In the Delete addChurnReason method***********************");
		wait.waitTillElementPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		 wait.waitTillElementNotPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		 refreshPage();
			Report.logInfo("Checking whether the element is deleted");
			if(item.isElementPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']")) {
				System.out.println("Unable to delete the element");
			} else {
				System.out.println("Element got deleted");
			}
			System.out.println("**********End of Delete Alert Reason:**********");
	return null;
	}
	public AdminTransactionsTab churnTabSettings(){
		item.click(CHURN_EDIT_SETTINGS);
		item.click(CHURN_SETTINGS_DOWNSELL);
		//button.click(SAVE);
		return null;
	}
	private AdminTransactionsTab fillFieldsForBookingTypes(String Name, String displayorder, String systemname, String shortname, String bookingtypeselect) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Booking Type')]")) {			
		field.clearAndSetText(BOOKINGT_NAME,Name);
		field.clearAndSetText(BOOKINGT_DISPLYORDER,displayorder);
		field.clearAndSetText(BOOKINGT_SYSTEM_NAME,systemname);
		field.clearAndSetText(BOOKINGT_SHORT_NAME,shortname);
		field.selectFromDropDown(BOOKINGTYPESELECT,bookingtypeselect);
		button.click(BOOKING_SAVE);
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
	    wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("This is not an alert Type light box:"+ item.getText("//span[@class='DialogTitleClass']"));
		}
		return this;
	}
	private AdminTransactionsTab fillFewFields(String Name, String displayorder, String shortname) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]", MIN_TIME, MAX_TIME);
		if(item.isElementPresent("//span[contains(text(),'Booking Type')]")) {
		field.clearAndSetText(BOOKINGT_NAME,Name);
		field.clearAndSetText(BOOKINGT_DISPLYORDER,displayorder);
		field.clearAndSetText(BOOKINGT_SHORT_NAME,shortname);
		button.click(BOOKING_SAVE);	
		wait.waitTillElementPresent("//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]", MIN_TIME, MAX_TIME);
		refreshPage();
	    wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("This is not Booking Type light box. This is :"+ item.getText("//span[@class='DialogTitleClass']")+"LightBox");
		}
 return this;
}}
