package com.gainsight.sfdc.customer360.pages;
import org.openqa.selenium.By;

import com.gainsight.sfdc.customer360.pages.Customer360Page;

public class Customer360Features extends Customer360Page{

	private final String EDIT_FEATURES_ICON="//a[text()='Edit Features']";
	private final String SAVE_BUTTON="//a[@class='btn_save edit_features']";
	public final String FEATURES_TABLE_HEADER="//table[@class='gs_features_grid gs_features_grid_header gs_features_display_header']";
	public final String FEATURES_TABLE_DATA="//table[@class='gs_features_grid gs_features_display']";
	public final String EDIT_FEATURES_TABLE="//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']";
	protected final String FEATURE_ROW_WITH_ROWSPAN="//table[@class='gs_features_grid gs_features_display']"+
													"/tbody/tr/td[contains(.,'%s') and @rowspan=%d]"+
													"/following-sibling::td[1][contains(.,'%s')]"+
													"/following-sibling::td[1]/img[@src='%s']/parent::td"+
													"/following-sibling::td[1]/img[@src='%s']/parent::td"+
													"/following-sibling::td[1][contains(.,'%s')]";
	protected final String FEATURE_ROW_WITHOUT_ROWSPAN="//table[@class='gs_features_grid gs_features_display']"+
													   "/tbody/tr/td[contains(.,'%s') and @rowspan=%d]"+
													   "/parent::tr/following-sibling::tr/"+
			                                           "td[1][contains(.,'%s')]"+
			                                           "/following-sibling::td[1]/img[@src='%s']/parent::td"+
			                                           "/following-sibling::td[1]/img[@src='%s']/parent::td"+
			                                           "/following-sibling::td[1][contains(.,'%s')]";
	
	public void clickOnEditFeatures()
	{
		item.click(EDIT_FEATURES_ICON);
		amtDateUtil.stalePause();
	}
	
	public void selectLicensed(String product,String feature){
		amtDateUtil.stalePause();
		//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']/tbody/tr[contains(.,'P1')]/td[contains(.,'Feature1')]/following-sibling::td[1]
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[1]/input[@class='licensed']")).click();
	}
	
	public void selectEnabled(String product,String feature){
		amtDateUtil.stalePause();
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[2]/input[@class='enabled']")).click();
	}

	public void addComments(String product,String feature,String comment){
		amtDateUtil.stalePause();
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[3]/input[@class='comments']")).sendKeys(comment);
		//field.clearAndSetText(,comment);
	}
	
	public void clickOnSave(){
		amtDateUtil.stalePause();
		amtDateUtil.stalePause();
		item.click(SAVE_BUTTON);
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

	public boolean checkFeatureRow(String Product, String Feature,
			String Licensed, String Enabled, String Comments,int rowspan,boolean order) {
		String eimg="";
		if(Enabled.equals("Yes")) 	eimg="/img/checkbox_checked.gif";
		else if(Enabled.equals("No")) eimg="/img/checkbox_unchecked.gif";
		
		String limg="";
		if(Licensed.equals("Yes"))  limg="/img/checkbox_checked.gif";
		else if(Licensed.equals("No")) limg="/img/checkbox_unchecked.gif";
		
		if(order)
		return item.isElementPresent(String.format(FEATURE_ROW_WITH_ROWSPAN,Product,rowspan,Feature,limg,eimg,Comments));
		else
		return item.isElementPresent(String.format(FEATURE_ROW_WITHOUT_ROWSPAN,Product,rowspan,Feature,limg,eimg,Comments));

	}
}
