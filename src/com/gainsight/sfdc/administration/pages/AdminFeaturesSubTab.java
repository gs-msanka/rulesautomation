package com.gainsight.sfdc.administration.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.sfdc.pages.BasePage;

public class AdminFeaturesSubTab extends BasePage{

	
	private final String READY_INDICATOR       = "//h2[text()='Features']/parent::td/following-sibling::td/div/input[@value='New']";
	private final String FEATURES_NEW          = "//h2[text()='Features']/parent::td/following-sibling::td/div/input[@value='New']";
	private final String FEATURE_FORM_BLOCK    = "//div[contains(@class,'jbaraDummyAdminFeatureInputForm') and contains(@style,'display: block')]";
	private final String FEATURE_FORM_NONE     = "//div[contains(@class,'jbaraDummyAdminFeatureInputForm') and contains(@style,'display: none')]";
	private final String FEATURE_TEXT_PRESENT  = "//h2[@id='InlineEditDialogTitle' and text()='Feature']";
	private final String FEATURE_NAME          = "//input[@class='jbaraDummyAdminFeatureInputCtrl featureInput']";
	private final String FEATURE_SELECT_GRUP   = "//select[@class='jbaraDummyAdminFeatureSelectCtrl']";//No in scope now
	private final String FEATURE_SYSTEMNAME    = "//input[@class='jbaraDummyAdminFeatureInputCtrl systemNameInput']";
	private final String FEATUR_SAVE           = "//input[@onclick='disableBtn(this);actionSaveAdminFeature()']";
	private final String FEATURE_CANCEL        = "//input[@onclick='jbaraCloseAdminFeatureInputForm()']";
	private final String FEATURE_TABLE_VALUES  = "j_id0:j_id14:j_id200:j_id209";
	
	public AdminFeaturesSubTab() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	  
	
	public AdminFeaturesSubTab createFeatureType(String Name, String systemname , String productName) {
		
		button.click(FEATURES_NEW);
		wait.waitTillElementDisplayed(FEATURE_FORM_BLOCK, MIN_TIME, MAX_TIME);
		if(item.isElementPresent(FEATURE_TEXT_PRESENT)) {
			field.clearAndSetText(FEATURE_NAME, Name);
			productSelection(productName);
			field.clearAndSetText(FEATURE_SYSTEMNAME, systemname);
			button.click(FEATUR_SAVE);
			wait.waitTillElementPresent(FEATURE_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
			wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("Text Missmatch with the light box:--");
		} return this;
	}
	
	public boolean IsFeatureTypePresent(String values){
		Boolean result = false;
		WebElement Linetable2 =item.getElement(FEATURE_TABLE_VALUES);
		String tableId = Linetable2.getAttribute("Id");
		int a = table.getValueInListRow(tableId, values);
		if(a != -1) {
			result = true;
		}
		return result;
	}
	
	public AdminFeaturesSubTab editFeatureType(String s, String Name, String systemname , String productName) {
		
		item.click("//td/span[contains(text(),'"+s+"')]/parent::td/preceding-sibling::td/a[text()='Edit']");
		wait.waitTillElementDisplayed(FEATURE_FORM_BLOCK, MIN_TIME, MAX_TIME);
		if(item.isElementPresent(FEATURE_TEXT_PRESENT)) {
			field.clearAndSetText(FEATURE_NAME, Name);
			productSelection(productName);
			field.clearAndSetText(FEATURE_SYSTEMNAME, systemname);
			button.click(FEATUR_SAVE);
			wait.waitTillElementPresent(FEATURE_FORM_NONE, MIN_TIME, MAX_TIME);
			refreshPage();
			wait.waitTillElementPresent("//span[contains(text(),'"+Name+"')]", MIN_TIME, MAX_TIME);
		} else {
			System.out.println("Text Missmatch with the light box:--");
		} return this;
	}
	
	public AdminFeaturesSubTab deleteFeatureType(String Name) {
		
		item.click("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Delete']");
		modal.accept();
		wait.waitTillElementNotPresent("//td/span[contains(text(),'"+Name+"')]/parent::td/preceding-sibling::td/a[text()='Edit']", MIN_TIME, MAX_TIME);
		refreshPage();
		return this;
	}
	
	
	/** If product name exists, then this will select the existing product name   
	 * @param if product name doesn't exists, then it will create the product name.
	 */
	public AdminFeaturesSubTab productSelection(String productName ) {
		
		boolean result = false;
		Select s = new Select(item.getElement(FEATURE_SELECT_GRUP));
		List<WebElement> opList = s.getOptions();
		for(WebElement webEle : opList) {
   if(webEle.getText().equalsIgnoreCase(productName)) {
		result = true;
	 } if(result == true) {
			field.selectFromDropDown(FEATURE_SELECT_GRUP, productName);//selecting existing product value
		 } else {
			item.click("//span[@class='jbaraDummyAddNewProductName']");//Plus mark
			field.setTextField("//input[@class='theHiddenProductName']", productName); //Product Name should be given in the text box
		        }
		}
		return this;
	}
	
	
}
