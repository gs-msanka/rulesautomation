package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminTransactionsTab extends BasePage {
	
	private final String READY_INDICATOR      = "//div[@id='Administration-Transactions']/descendant::input[@class='btn dummyAllAdminNewBtn' and @type='button' and @value='New']";
	private final String NEW_BOOKINGTYPES     = "//div[@id='Administration-Transactions']/descendant::input[@class='btn dummyAllAdminNewBtn' and @type='button' and @value='New']";
	private final String BOOKINGT_NAME        = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input";
	private final String BOOKINGT_DISPLYORDER = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String BOOKINGT_SYSTEM_NAME = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String BOOKINGT_SHORT_NAME  = "//span[contains(text(),'Booking Type')]/parent::h2//following::tbody/tr[5]/td[2]/input";
    private final String BOOKINGTYPESELECT    = "//select[@class='mapToBookingTypeSelectClass']";
	private final String BOOKING_SAVE         = "//span[contains(text(),'Booking Type')]/parent::h2/parent::div//following::div/input[@value='Save']";
	private final String TABLE_VALUES_BTYPES  = "//table[contains(@id,'transactionBookingTypesPanel')]";
	private final String BOOKING_FORM_BLOCK   = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: block')]";
	private final String BOOKING_FORM_NONE    = "//div[contains(@class,'jbaraDummyAdminInputForm') and contains(@style,'display: none')]";
	private final String BOOKING_TEXT_PRESENT =  "//span[contains(text(),'Booking Type')]";
	
	                                   //Map Booking Types
	private final String CHECK_ACTIVATION     = "//table[@id='tableSortable']/tbody/tr[contains(.,'Activation')]//input";
	private final String SAVE_MAP_BUTTON      = "//div[@class='overlayDialog inlineEditDialog jbaraDummyAdminBookingTypesInputForm bPageBlock ui-draggable']/descendant::input[@class='btn dummyAllAdminSaveBtn']";
	private final String CHECK_SERVICES       = "//table[@id='tableSortable']/tbody/tr[contains(.,'Services')]//input";
	private final String CHECK_USERS          = "//table[@id='tableSortable']/tbody/tr[contains(.,'Users')]//input";
	private final String CHECK_SUBSCRIPTION   = "//table[@id='tableSortable']/tbody/tr[contains(.,'Subscription')]//input";
	private final String BOOKING_FORM_BLOCK_MAP    = "//div[contains(@class,'jbaraDummyAdminBookingTypesInputForm') and contains(@style,'display: block')]";
	private final String BOOKING_FORM_NONE_MAP    = "//div[contains(@class,'jbaraDummyAdminBookingTypesInputForm') and contains(@style,'display: none')]";
	
	                             //TRANSACTIONS LINE ITEMS
	private final String NEW_TRANS_LINE_ITEMS = "//td[contains(@id,'Transactions')]/div/input";
	private final String LINT_ITEM_NAME       = "//input[@class='jbaraDummyAdminTransactionTypeInputCtrl subjectInput']";
	private final String LINE_ITEM_TYPE       = "//select[@class='jbaraDummyAdminTransactionTypeSelectCtrl']";
	private final String TABLE_VALUES_LITEMS  = "//table[contains(@id,'Transactions')]";
	private final String SAVE_LITEMS          = "//input[@onclick='disableBtn(this);actionSaveAdminTransactionLine()']";
	private final String CANCEL_LITEMS        = "//input[@onclick='jbaraCloseAdminTransactionLineInputForm()' and @class='btn']";
	private final String TRANS_FORM_BLOCK     = "//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: block')]";
	private final String TRANS_FORM_NONE      = "//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm') and contains(@style,'display: none')]";
	private final String TRANS_TEXT_PRESENT   = "//h2[@id='InlineEditDialogTitle' and text()= 'Transaction Line Items']";
	                                    //CHURN REASON
	private final String NEW_CHURN_REASON     = "//td[contains(@id,'churnReasonAdminIdBlock')]/div/input";
	private final String CREASON_NAME         = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input";
	private final String CREASON_DISPORDER    = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'DisplayOrderInput')]";
	private final String CREASON_SYSTEM_NAME  = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[contains(@class,'systemNameInputClass')]";
	private final String CREASON_SHOT_NAME    = "//span[contains(text(),'Churn Reason')]/parent::h2//following::tbody/tr[5]/td[2]/input";
	private final String CREASON_SAVE         = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[@value='Save']";   
	private final String CREASON_CANCEL       = "//span[contains(text(),'Churn Reason')]/parent::h2/parent::div//following::div/input[@value='Cancel']";
	private final String CHURN_TEXT_PRESENT   = "//span[contains(text(),'Churn Reason')]";
	                                  //Edit Settings
	private final String CHURN_EDIT_SETTINGS      = "//td/div/input[@class='btn dummyAllAdminNewBtn' and @value='Edit Settings']";
	private final String CHURN_SETTINGS_DOWNSELL  = "//input[@class='jbaraDummyAdminCheckboxCtrl checkboxChurnInput']";
	private final String TABLE_VALUES_CHURN       ="//table[contains(@id,'churnReasonAdminIdBlock')]";
	
	public AdminTransactionsTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	                               //Create Booking Types
	public AdminTransactionsTab addBookingTypes(String name, String displayOrder,String systemName ,String shortName, String selectBookingType) { 
		item.click(NEW_BOOKINGTYPES);
		fillFieldsForBookingTypes(name, displayOrder, systemName, shortName, selectBookingType);
		return this;
	}               /**
	                *Validating whether the booking type is created with particular fields ..   
	             */
	public boolean isBookingTypePresent(String values){
		Boolean result = false;
		WebElement Booktable1 =item.getElement(TABLE_VALUES_BTYPES);
		String tableId = Booktable1.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
		                           //Edit Booking Types
	public AdminTransactionsTab editbookingType(String s, String name, String displayOrder,String shortName, String selectBookingType) {                       
		wait.waitTillElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFields(name,displayOrder,shortName, selectBookingType);
		return this;
	}
	public boolean isBookingTypeEdited(String values){
		Boolean result = false;
		WebElement Booktable1 =item.getElement(TABLE_VALUES_BTYPES);
		String tableId = Booktable1.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
                                //Map Booking Types   String activation, String services, String users, String subscription
	public AdminTransactionsTab mapBookingTypes(String name, String mapBookingType ) {
		wait.waitTillElementPresent("//td/span[text()='"+name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']");
		wait.waitTillElementDisplayed(BOOKING_FORM_BLOCK_MAP, MIN_TIME, MAX_TIME);
		  String[] allBooking =   mapBookingType.split("@");
		  
		for(String all: allBooking) {
			System.out.println("Elements in all:"+ all);
		if(all.equalsIgnoreCase("activation")) {
		item.click(CHECK_ACTIVATION);  
		}
		if(all.equalsIgnoreCase("services")) {
		item.click(CHECK_SERVICES);
		}
		if(all.equalsIgnoreCase("users")) {
			item.click(CHECK_USERS);  
			}
	if(all.equalsIgnoreCase("subscription")) {
			item.click(CHECK_SUBSCRIPTION);
	}
		}
		button.click(SAVE_MAP_BUTTON);
		wait.waitTillElementNotDisplayed(BOOKING_FORM_NONE_MAP, MIN_TIME, MAX_TIME);
		refreshPage();
		wait.waitTillElementPresent(NEW_BOOKINGTYPES, MIN_TIME, MAX_TIME);
		return this;
	}
	                                 //Delete Booking Types
	public AdminTransactionsTab deleteBookingTypes(String name) {
		wait.waitTillElementPresent("//td/span[text()='"+name+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+name+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']");
	    modal.accept(); 
	    wait.waitTillElementNotPresent("//td/span[text()='"+name+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']", MIN_TIME, MAX_TIME);
	    refreshPage();
		return this;
	}
	public boolean isBookingTypeDeleted(String values){
		Boolean result = false;
		WebElement Booktable1 =item.getElement(TABLE_VALUES_BTYPES);
		String tableId = Booktable1.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
                        	       //Create Transaction Line Items
	public AdminTransactionsTab addTransactionLinesItems(String name,String type) {
		wait.waitTillElementPresent(NEW_TRANS_LINE_ITEMS, MIN_TIME, MAX_TIME);
		item.click(NEW_TRANS_LINE_ITEMS);
		 wait.waitTillElementDisplayed(TRANS_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(TRANS_TEXT_PRESENT);
		     field.setTextField(LINT_ITEM_NAME,name);
		   field.setSelectField(LINE_ITEM_TYPE,type);
		   button.click(SAVE_LITEMS);
		   wait.waitTillElementPresent(TRANS_FORM_NONE, MIN_TIME, MAX_TIME);
		   refreshPage();
		   wait.waitTillElementPresent("//label[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
		   item.isElementPresent("//label[contains(text(),'"+name+"')]");
		   amtDateUtil.stalePause();
		return this;
	}                
	/**
     *Validating whether the Line item is created with particular fields ..   
  */
	public boolean isTransactionLineItemPresent(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_LITEMS);
		String tableId = Linetable2.getAttribute("Id");
		int rowNo  = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
		                               // Edit Transaction Line Items
	public AdminTransactionsTab editTransactionLineItem(String s,String name,String type) {
		   wait.waitTillElementPresent("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]", MIN_TIME, MAX_TIME);
			item.click("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]");
			wait.waitTillElementDisplayed(TRANS_FORM_BLOCK, MIN_TIME, MAX_TIME);
			if(item.isElementPresent(TRANS_TEXT_PRESENT)) {
			field.clearAndSetText(LINT_ITEM_NAME,name);
			field.setSelectField(LINE_ITEM_TYPE,type);
			button.click(SAVE_LITEMS);
			wait.waitTillElementPresent(TRANS_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
			wait.waitTillElementPresent("//label[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
			 amtDateUtil.stalePause();
			} return this;
	}
	public boolean isTransactionLineItemEdited(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_LITEMS);
		String tableId = Linetable2.getAttribute("Id");
		int rowNo  = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
	                     //Delete Transaction Line Items
	public AdminTransactionsTab deleteTransactionLineItem(String name) {
		wait.waitTillElementPresent("//td/label[contains(text(),'"+name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]", MIN_TIME, MAX_TIME);
		item.click("//td/label[contains(text(),'"+name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]");
		modal.accept();
		wait.waitTillElementNotPresent("//td/label[contains(text(),'"+name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]", MIN_TIME, MAX_TIME);
		refreshPage();
	return this;
	}
	public boolean isTransactionLineItemDeleted(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_LITEMS);
		String tableId = Linetable2.getAttribute("Id");
		int rowNo  = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
	                                        //--add Churn Reason
	public AdminTransactionsTab addChurnReason(String name, String displayOrder, String systemName , String shortName) {
        wait.waitTillElementPresent(NEW_CHURN_REASON, MIN_TIME, MAX_TIME);
		item.click(NEW_CHURN_REASON);
		wait.waitTillElementDisplayed(BOOKING_FORM_BLOCK, MIN_TIME, MAX_TIME);
	     item.isElementPresent(CHURN_TEXT_PRESENT);
		  field.clearAndSetText(CREASON_NAME,name);
		  field.clearAndSetText(CREASON_DISPORDER,displayOrder);
		  field.clearAndSetText(CREASON_SYSTEM_NAME,systemName);
		  field.clearAndSetText(CREASON_SHOT_NAME,shortName);  
		button.click(CREASON_SAVE);
	wait.waitTillElementPresent(BOOKING_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
    wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
    amtDateUtil.stalePause();
		return this;
	}	
		public boolean isChurnPresent(String values){
			Boolean result = false;
			WebElement Linetable2 =item.getElement(TABLE_VALUES_CHURN);
			String tableId = Linetable2.getAttribute("Id");
			int rowNo = table.getValueInListRow(tableId, values);
			if(rowNo != -1) {
				result = true;
			}
			return result;
		}
	                                        //Edit ChurnReason
	public AdminTransactionsTab editChurnReason(String s,String name, String displayOrder,String shortName) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed(BOOKING_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(CHURN_TEXT_PRESENT);
			field.clearAndSetText(CREASON_NAME,name);
			field.clearAndSetText(CREASON_DISPORDER,displayOrder);
			field.clearAndSetText(CREASON_SHOT_NAME,shortName);  
			button.click(CREASON_SAVE);
			wait.waitTillElementPresent(BOOKING_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
			wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
			 amtDateUtil.stalePause();
	return this;
	}
	public boolean isChurnEdited(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_CHURN);
		String tableId = Linetable2.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
	                                            //Delete Churn Reason
	public AdminTransactionsTab deleteChurnReason(String Name) {
		wait.waitTillElementPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		 wait.waitTillElementNotPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']", MIN_TIME, MAX_TIME);
		 refreshPage();
	return this;
	}
	public boolean isChurnDeleted(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(TABLE_VALUES_CHURN);
		String tableId = Linetable2.getAttribute("Id");
		int rowNo = table.getValueInListRow(tableId, values);
		if(rowNo != -1) {
			result = true;
		}
		return result;
	}
	
	public AdminTransactionsTab churnTabSettings(){
		item.click(CHURN_EDIT_SETTINGS);
		item.click(CHURN_SETTINGS_DOWNSELL);
		//button.click(SAVE);
		return this;
	}
	private AdminTransactionsTab fillFieldsForBookingTypes(String name, String displayOrder, String systemName, String shortName, String selectbookingtype) {
		wait.waitTillElementDisplayed(BOOKING_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(BOOKING_TEXT_PRESENT);			
		field.clearAndSetText(BOOKINGT_NAME,name);
		field.clearAndSetText(BOOKINGT_DISPLYORDER,displayOrder);
		field.clearAndSetText(BOOKINGT_SYSTEM_NAME,systemName);
		field.clearAndSetText(BOOKINGT_SHORT_NAME,shortName);
		field.selectFromDropDown(BOOKINGTYPESELECT,selectbookingtype);
		button.click(BOOKING_SAVE);
		wait.waitTillElementPresent(BOOKING_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
	   wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
	    item.isElementPresent("//span[contains(text(),'"+name+"')]");
	   amtDateUtil.stalePause();
		return this;
	}
	
	private AdminTransactionsTab fillFewFields(String name, String displayOrder, String shortName, String selectbookingtype) {
		wait.waitTillElementDisplayed(BOOKING_FORM_BLOCK, MIN_TIME, MAX_TIME);
		item.isElementPresent(BOOKING_TEXT_PRESENT);
		field.clearAndSetText(BOOKINGT_NAME,name);
		field.clearAndSetText(BOOKINGT_DISPLYORDER,displayOrder);
		field.clearAndSetText(BOOKINGT_SHORT_NAME,shortName);
		field.selectFromDropDown(BOOKINGTYPESELECT,selectbookingtype);
		button.click(BOOKING_SAVE);	
		wait.waitTillElementPresent(BOOKING_FORM_NONE, MIN_TIME, MAX_TIME);
		refreshPage();
	    wait.waitTillElementPresent("//span[contains(text(),'"+name+"')]", MIN_TIME, MAX_TIME);
	    amtDateUtil.stalePause();
 return this;
}
	}
