package com.gainsight.sfdc.customer360Page.pages;
import com.gainsight.sfdc.customer.pages.Customer360Page;

public class Customer360Features extends Customer360Page{

	private final String EDIT_FEATURES_ICON="//a[text()='Edit Features']";
	private final String SAVE_BUTTON="//a[@class='btn_save edit_features']";
	public final String FEATURES_TABLE_HEADER="//table[@class='gs_features_grid gs_features_grid_header gs_features_display_header']";
	public final String FEATURES_TABLE_DATA="//table[@class='gs_features_grid gs_features_display']";
	public final String EDIT_FEATURES_TABLE="//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']";
	public void clickOnEditFeatures()
	{
		item.click(EDIT_FEATURES_ICON);
	}
	
	public void selectLicensed(String product,String feature){
		//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']/tbody/tr[contains(.,'P1')]/td[contains(.,'Feature1')]/following-sibling::td[1]
		item.selectCheckBox("//tbody/tr[contains(.,'"+product+"')]/td[contains(.,'"+feature+"')]/following-sibling::td[1]/input[@class='licensed']");
	}
	
	public void selectEnabled(String product,String feature){
		item.selectCheckBox(EDIT_FEATURES_TABLE+"/tbody/tr[contains(.,'"+product+"')]/td[contains(.,'"+feature+"')]/following-sibling::td[2]/input[@class='enabled']");
	}
	
	public void clickOnSave(){
		item.click(SAVE_BUTTON);
	}
	
	public void addComments(String product,String feature,String comment){
		field.clearAndSetText(EDIT_FEATURES_TABLE+"/tbody/tr[contains(.,'"+product+"')]/td[contains(.,'"+feature+"')]/following-sibling::td[3]/input[@class='comments']",comment);
	}
	
	public boolean isHeaderPresent()
	{
		wait.waitTillElementPresent(FEATURES_TABLE_HEADER, 1,30);
		return true;
	}
	
	public boolean isDataGridPresent()
	{
		wait.waitTillElementPresent(FEATURES_TABLE_DATA, 1,30);
		return true;
	}

	public boolean checkFeatureForProduct(String product,String feature) {
		System.out.println("Prod:"+product+",feature:"+feature);
		//For the given product check if the given feature exists in the table row
		
		//form an xpath for the feature basing on the product and verify if the element is present
		wait.waitTillElementDisplayed(FEATURES_TABLE_DATA+"/tbody/tr/td[contains(.,'"+feature+"')]", MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean checkLicensedForProduct(String product, String license) {
		System.out.println("Prod:"+product+",license:"+license);
		String img="";
		if(license.equals("Yes"))
		{
			img="/img/checkbox_checked.gif";
		}
		else if(license.equals("No"))
		{
			img="/img/checkbox_unchecked.gif";
		}
		//table[@class='gs_features_grid gs_features_display']/tbody/tr[contains(.,'P1')]/td/img[@src='/img/checkbox_checked.gif']
		wait.waitTillElementDisplayed(FEATURES_TABLE_DATA+"/tbody/tr[contains(.,'"+product+"')]/td[3]/img[@src='"+img+"']", MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean checkEnabledForProduct(String product, String enabled) {
		System.out.println("Prod:"+product+",enabled:"+enabled);
		String img="";
		if(enabled.equals("Yes"))
		{
			img="/img/checkbox_checked.gif";
		}
		else if(enabled.equals("No"))
		{
			img="/img/checkbox_unchecked.gif";
		}
		wait.waitTillElementDisplayed(FEATURES_TABLE_DATA+"/tbody/tr[contains(.,'"+product+"')]/td[4]/img[@src='"+img+"']", MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean checkCommentsForProduct(String product, String comments) {
		System.out.println("Prod:"+product+",comments:"+comments);
		//table[@class='gs_features_grid gs_features_display']/tbody/tr[contains(.,'P1')]/td[contains(.,'A good feature of Product1')]
		wait.waitTillElementDisplayed(FEATURES_TABLE_DATA+"/tbody/tr[contains(.,'"+product+"')]/td[5][contains(.,'"+comments+"')]", MIN_TIME, MAX_TIME);
		return true;
	}

	public boolean checkProductWithRowspan(String product, int rowspan) {
		System.out.println("Prod:"+product+", rowspan:"+rowspan);
		//table[@class='gs_features_grid gs_features_display']/tbody/tr/td[contains(.,'P2') and @rowspan=2] 
		wait.waitTillElementDisplayed(FEATURES_TABLE_DATA+"/tbody/tr/td[contains(.,'"+product+"') and @rowspan="+rowspan+"]", MIN_TIME, MAX_TIME);
		return true;
	}
	
}
