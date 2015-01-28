package com.gainsight.sfdc.customer360.pages;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;

public class Customer360Features extends Customer360Page{

	private final String EDIT_FEATURES_ICON="//a[text()='Edit Features']";
	private final String SAVE_BUTTON="//a[contains(@class, 'btn_save edit_features')]";
	public final String FEATURES_TABLE_HEADER="//table[@class='gs_features_grid gs_features_grid_header gs_features_display_header']";
	public final String FEATURES_TABLE_DATA="//table[@class='gs_features_grid gs_features_display']";

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

    private final String FEATURE_POPUP = "//div[contains(@class, 'featuresCls ui-draggable')]/descendant::div[@class='gs_features']";
    private final String FEATURE_ROW = FEATURE_POPUP+"/descendant::td[@title='%s']/ancestor::table/descendant::td[@title='%s']/ancestor::tbody/tr/td[@title='%s']";

    private final String FEATURE_TABLE = "//div[@class='gs_features_grid_row']/table[@class='gs_features_grid gs_features_display']";
    private final String FEATURE_TABLE_VIEW_ROW = FEATURE_TABLE+"/descendant::td[@title='%s']/ancestor::table/descendant::td[@title='%s']/ancestor::tbody/tr/td[@title='%s']";

    //

    public void clickOnEditFeatures() {
		item.click(EDIT_FEATURES_ICON);
		wait.waitTillElementDisplayed(FEATURE_POPUP, MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed(SAVE_BUTTON, MIN_TIME, MAX_TIME);
	}

    public void selectLicensed(String product,String feature){
        String xPath = String.format(FEATURE_ROW, feature, product, feature)+"/following-sibling::td/input[@class='licensed']";
        Log.info("Feature / Product Xpath : " +xPath);
        item.selectCheckBox(xPath);
	}
	
	public void selectEnabled(String product,String feature){
        String xPath = String.format(FEATURE_ROW, feature, product, feature)+"/following-sibling::td/input[@class='enabled']";
        Log.info("Feature / Product Xpath : " +xPath);
        item.selectCheckBox(xPath);
	}

	public void addComments(String product,String feature,String comment){
        String xPath = String.format(FEATURE_ROW, feature, product, feature)+"/following-sibling::td/div/input[@class='comments']";
        Log.info("Feature / Product Xpath : " +xPath);
        item.clearAndSetText(xPath, comment);
	}
	
	public Customer360Features clickOnSave(){
		item.click(SAVE_BUTTON);
        env.setTimeout(5);
        wait.waitTillElementNotPresent(FEATURE_POPUP, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
        Log.info("Clicked on Feature Save");
        return this;
	}
	
	public boolean isHeaderPresent() {
		wait.waitTillElementPresent(FEATURES_TABLE_HEADER, MIN_TIME, MAX_TIME);
		return true;
	}

    public boolean isHeaderColumnPresent(String header) {
        Log.info("Feature Header Path :" +FEATURES_TABLE_HEADER + "/thead/tr/th[text()='" + header + "']");
        return isElementPresentAndDisplay(By.xpath(FEATURES_TABLE_HEADER + "/thead/tr/th[text()='" + header + "']"));
    }
	
	public boolean isDataGridPresent() {
		wait.waitTillElementPresent(FEATURES_TABLE_DATA, MIN_TIME, MAX_TIME);
		return true;
	}

    // /following-sibling::td/div/img[@src='/img/checkbox_checked.gif']/ancestor::td[@class='featuresTd featuresTdCb']/following-sibling::td[@class='featuresTd featuresTdCb']

    public boolean checkFeatureRow(String product, String feature,
			String licensed, String enabled, String comments) {
        Log.info("Checking For Data In Feature Table");
        Log.info("Checking Product : " +product + " , - Feature : " +feature + " , - Licensed : "+licensed + ",  - Enabled : " +enabled);
        Log.info("With Comments : " +comments);
        String xpath = String.format(FEATURE_TABLE_VIEW_ROW, feature, product,feature);
        String unCheckedImg = "/img/checkbox_unchecked.gif";
        String checkedImg = "/img/checkbox_checked.gif";
        if(licensed.equalsIgnoreCase("Yes")) {
            xpath= xpath+"/following-sibling::td/div/img[@src='"+checkedImg+"']";
        } else {
            xpath= xpath+"/following-sibling::td/div/img[@src='"+unCheckedImg+"']";
        }
        xpath = xpath+"/ancestor::td[@class='featuresTd featuresTdCb']/following-sibling::td[@class='featuresTd featuresTdCb']";
        if(enabled.equalsIgnoreCase("Yes")) {
            xpath=xpath+"/div/img[@src='"+checkedImg+"']";
        } else {
            xpath=xpath+"/div/img[@src='"+unCheckedImg+"']";
        }

        Log.info("Feature xpath : " +xpath);
        env.setTimeout(1);
        boolean result = isElementPresentAndDisplay(By.xpath(xpath));
        env.setTimeout(30);
        return result;
	}
}
