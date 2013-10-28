package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.pages.BasePage;

public class AdminTransactionsTab extends BasePage {
	
	private final String NEW_BOOKINGTYPES     = "//div[@id='Administration-Transactions']/descendant::input[@class='btn dummyAllAdminNewBtn' and @type='button' and @value='New']";
	private final String NAME                 = "//input[@class='jbaraDummyAdminInputCtrl subjectInput nameInputClass']";
	private final String DISPLAY_ORDER        = "//input[@class='jbaraDummyAdminInputCtrl DisplayOrderInput']";
	private final String SYSTEM_NAME          = "//input[@class='jbaraDummyAdminInputCtrl subjectInput systemNameInputClass']";
	private final String SHORT_NAME           = "//input[@class='jbaraDummyAdminInputCtrl subjectInput shortNameInputClass']";
    private final String BOOKINGTYPESELECT    = "//select[@class='mapToBookingTypeSelectClass']";
	private final String SAVE                 = "//input[@class='btn dummyAllAdminSaveBtn']";
	private final String TABLE_VALUES_BTYPES  = "//table[contains(@id,'transactionBookingTypesPanel')]/tbody";
	             //Map Booking Types
	private final String CHECK_ACTIVATION     = "//table[@id='tableSortable']/tbody/tr[contains(.,'Activation')]//input";
	private final String SAVE_MAP_BUTTON      = "//div[@class='overlayDialog inlineEditDialog jbaraDummyAdminBookingTypesInputForm bPageBlock ui-draggable']/descendant::input[@class='btn dummyAllAdminSaveBtn']";
	private final String CHECK_SERVICES       = "//table[@id='tableSortable']/tbody/tr[contains(.,'Services')]//input";
	//TRANSACTIONS LINE ITEMS
	private final String NEW_TRANS_LINE_ITEMS = "//td[contains(@id,'Transactions')]/div/input";
	private final String LINT_ITEM_NAME       = "//input[@class='jbaraDummyAdminTransactionTypeInputCtrl subjectInput']";
	private final String LINE_ITEM_TYPE       = "//select[@class='jbaraDummyAdminTransactionTypeSelectCtrl']";
	private final String NEW_CHURN_REASON     = "//td[contains(@id,'churnReasonAdminIdBlock')]/div/input";
	private final String TABLE_VALUES_LITEMS  = "//table[contains(@id,'Transactions')]/tbody";
	//Edit Settings
	private final String CHURN_EDIT_SETTINGS      = "//td/div/input[@class='btn dummyAllAdminNewBtn' and @value='Edit Settings']";
	private final String CHURN_SETTINGS_DOWNSELL  = "//input[@class='jbaraDummyAdminCheckboxCtrl checkboxChurnInput']";
	private final String TABLE_VALUES_CHURN       ="//table[contains(@id,'churnReasonAdminIdBlock')]/tbody";
	
	//Create Booking Types
	public AdminTransactionsTab addBookingTypes(String Name, String displayorder,String systemname , String shortname, String bookingtypeselect) {
			                    
		Report.logInfo("*************In the  addBookingTypes method***********************");
		wait.waitTillElementPresent(NEW_BOOKINGTYPES, MIN_TIME, MAX_TIME);
		item.click(NEW_BOOKINGTYPES);
		fillFieldsForBookingTypes(Name, displayorder, systemname ,shortname, bookingtypeselect);
	    sleep(7);        
		return this;
	}
                 //Validation 
	public boolean IsBookingTypePresent(String values){
		//WebElement table =	driver.findElement(By.xpath("TABLE_VALUES"));
		WebElement table =item.getElement(TABLE_VALUES_BTYPES);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("rowText vlaues:" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("***************Values exists**************" +result);
			return result;
		}
		                    //Edit Booking Types
	public AdminTransactionsTab editbookingType(String s, String Name, String displayorder,String shortname) {                       
		Report.logInfo("*************In the Edit addBookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);
		return this;
	}
	                         //Map Booking Types
	public AdminTransactionsTab mapBookingTypes(String Name) {
		Report.logInfo("*************In the map addBookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+Name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+Name+"']/parent::td/preceding-sibling::td/span/a[text()='Map']");
		 wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminBookingTypesInputForm')]", MIN_TIME, MAX_TIME);
		item.click(CHECK_ACTIVATION);
		item.click(CHECK_SERVICES);
		button.click(SAVE_MAP_BUTTON);
		return this;
	}
	                 //Delete Booking Types
	public AdminTransactionsTab deleteBookingTypes(String s) {
		Report.logInfo("*************In the Delete addBookingTypes method***********************");
		wait.waitTillElementPresent("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']", MIN_TIME, MAX_TIME);
		item.click("//td/span[text()='"+s+"']/parent::td/preceding-sibling::td/span/a[text()='Delete']");
		acceptAlert();
		return this;
	}
	public void acceptAlert() {
		int i=0;
		while(i++< 5) {
			try {
				modal.accept();
				break;
			} catch(NoAlertPresentException e) {
				Report.logInfo("No Alert found, sleep for 1 sec");
				sleep(1);
				continue;
			}
		}
		
	}
                        	//Create Transaction Line Items
	public AdminTransactionsTab addTransactionLinesItems(String Name,String type) {
		Report.logInfo("*************In the  addTransactionLinesItems method***********************");
		wait.waitTillElementPresent(NEW_TRANS_LINE_ITEMS, MIN_TIME, MAX_TIME);
		item.click(NEW_TRANS_LINE_ITEMS);
		 wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm')]", MIN_TIME, MAX_TIME);
		field.setTextField(LINT_ITEM_NAME,Name);
		field.setSelectField(LINE_ITEM_TYPE,type);
		button.click("//span[3]/div/div[2]/div/div[4]/input");
		sleep(7);
		return this;
	}
	public boolean isTransactionLineItemPresent(String values) {
		WebElement table =item.getElement(TABLE_VALUES_LITEMS);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("rowText vlaues:" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("Values exists" +result);
			return result;
		}
		             // Edit Transaction Line Items
	public AdminTransactionsTab editTransactionLineItem(String s,String Name,String type) {
		Report.logInfo("*************In the edit  addTransactionLinesItems method***********************");
		    wait.waitTillElementPresent("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]", MIN_TIME, MAX_TIME);
			item.click("//td/label[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Edit')]");
			wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminTransactionLineInputForm')]", MIN_TIME, MAX_TIME);
			field.clearAndSetText(LINT_ITEM_NAME,Name);
			field.setSelectField(LINE_ITEM_TYPE,type);
			button.click("//span[3]/div/div[2]/div/div[4]/input");
			//stalePause();
		return this;
	}
	                 //Delete Transaction Line Items
	public AdminTransactionsTab deleteTransactionLineItem(String Name) {
		Report.logInfo("*************In the delete  addTransactionLinesItems method***********************");
		wait.waitTillElementPresent("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]", MIN_TIME, MAX_TIME);
		item.click("//td/label[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[contains(text(),'Delete')]");
		modal.accept();
		return this;
	}
	                     //Admin:--add Churn Reason
	public AdminTransactionsTab addChurnReason(String Name, String displayorder,
            String systemname , String shortname) {
		Report.logInfo("*************In the   addChurnReason method***********************");
        wait.waitTillElementPresent(NEW_CHURN_REASON, MIN_TIME, MAX_TIME);
		item.click(NEW_CHURN_REASON);
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);  
		button.click(SAVE);
		sleep(7);
		return this;
	}
	public boolean IsChurnPresent(String values){
		//WebElement table =	driver.findElement(By.xpath("TABLE_VALUES"));
		WebElement table =item.getElement(TABLE_VALUES_CHURN);
	        List<WebElement> tableRows = table.findElements(By.tagName("tr"));
	        System.out.println("Table row Values are:" +tableRows);
	     
	        Boolean result = false;
	        for(WebElement we : tableRows) {
	        	String rowText = we.getText();
	        	System.out.println("rowText vlaues:" +rowText);     	
	        	for(String val : values.split("\\|") ) {
	        		if(rowText.contains(val)) {
	        			result = true;
	        		} else {
	        			result = false;
	        			break;
	        		}
	        	}
	        	if(result) {
     			break;
     		  }
	        }
	        System.out.println("Values exists" +result);
			return result;
		}         
	                         //Edit ChurnReason
	public AdminTransactionsTab editChurnReason(String s,String Name, String displayorder,String shortname) {
		Report.logInfo("*************In the Edit  addChurnReason method***********************");
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		fillFewFields(Name,displayorder,shortname);               
	return this;
	}
	          //Delete Churn Reason
	public AdminTransactionsTab deleteChurnReason(String Name) {
		Report.logInfo("*************In the Delete addChurnReason method***********************");
		item.click("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();	
	return null;
	}
	public AdminTransactionsTab churnTabSettings(){
		item.click(CHURN_EDIT_SETTINGS);
		item.click(CHURN_SETTINGS_DOWNSELL);
		button.click(SAVE);
		//stalePause();
		return null;
	}
	private void fillFieldsForBookingTypes(String Name, String displayorder, String systemname, String shortname, String bookingtypeselect) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SYSTEM_NAME,systemname);
		field.clearAndSetText(SHORT_NAME,shortname);
		field.selectFromDropDown(BOOKINGTYPESELECT,bookingtypeselect);
		button.click(SAVE);
		//stalePause();
	}
	private void fillFewFields(String Name, String displayorder, String shortname) {
		wait.waitTillElementDisplayed("//div[contains(@class,'jbaraDummyAdminInputForm')]", MIN_TIME, MAX_TIME);
		field.clearAndSetText(NAME,Name);
		field.clearAndSetText(DISPLAY_ORDER,displayorder);
		field.clearAndSetText(SHORT_NAME,shortname);
		button.click(SAVE);	
		//stalePause();
	}
}
