package com.gainsight.sfdc.customer360.pages;
import com.gainsight.pageobject.util.Timer;
import org.openqa.selenium.By;

public class Customer360Features extends Customer360Page{

	private final String EDIT_FEATURES_ICON="//a[text()='Edit Features']";
	private final String SAVE_BUTTON="//a[contains(@class, 'btn_save edit_features')]";
	public final String FEATURES_TABLE_HEADER="//table[@class='gs_features_grid gs_features_grid_header gs_features_display_header']";
	public final String FEATURES_TABLE_DATA="//table[@class='gs_features_grid gs_features_display']";
	public final String EDIT_FEATURES_TABLE="//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']";
	protected final String FEATURE_ROW_WITH_ROWSPAN="//table[@class='gs_features_grid gs_features_display']"+
													"/tbody/tr/td[contains(.,'%s') and @rowspan=%d]"+
													"/following-sibling::td[1][contains(.,'%s')]"+
													"/following-sibling::td[1]/div/img[@src='%s']/parent::div/parent::td"+
													"/following-sibling::td[1]/div/img[@src='%s']/parent::div/parent::td"+
													"/following-sibling::td[1]/div[contains(.,'%s')]";
	protected final String FEATURE_ROW_WITHOUT_ROWSPAN="//table[@class='gs_features_grid gs_features_display']"+
													   "/tbody/tr/td[contains(.,'%s') and @rowspan=%d]"+
													   "/parent::tr/following-sibling::tr/"+
			                                           "td[1][contains(.,'%s')]"+
			                                           "/following-sibling::td[1]/div/img[@src='%s']/parent::div/parent::td"+
			                                           "/following-sibling::td[1]/div/img[@src='%s']/parent::div/parent::td"+
			                                           "/following-sibling::td[1]/div[contains(.,'%s')]";
	
	public void clickOnEditFeatures()
	{
		item.click(EDIT_FEATURES_ICON);
		Timer.sleep(2);
	}
	
	public void selectLicensed(String product,String feature){
		Timer.sleep(2);
		//table[@class='gs_features_grid gs_features_grid_dialog gs_features_edit']/tbody/tr[contains(.,'P1')]/td[contains(.,'Feature1')]/following-sibling::td[1]
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[1]/input[@class='licensed']")).click();
	}
	
	public void selectEnabled(String product,String feature){
		Timer.sleep(2);
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[2]/input[@class='enabled']")).click();
	}

	public void addComments(String product,String feature,String comment){
		Timer.sleep(2);
		driver.findElement(By.xpath(EDIT_FEATURES_TABLE+"/tbody//td[contains(.,'"+feature+"')]/following-sibling::td[3]/div/input[@class='comments']")).sendKeys(comment);
		//field.clearAndSetText(,comment);
	}
	
	public void clickOnSave(){
		Timer.sleep(5);
		item.click(SAVE_BUTTON);
	}
	
	public boolean isHeaderPresent()
	{
		wait.waitTillElementPresent(FEATURES_TABLE_HEADER, MIN_TIME, MAX_TIME);
		return true;
	}
	
	public boolean isDataGridPresent()
	{
		wait.waitTillElementPresent(FEATURES_TABLE_DATA, MIN_TIME, MAX_TIME);
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
