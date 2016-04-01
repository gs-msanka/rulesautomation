package com.gainsight.sfdc.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.util.AmountsUtil;
import com.gainsight.sfdc.sfWidgets.accWidget.pages.AccountPage;
import com.gainsight.sfdc.sfWidgets.oppWidget.pages.OpportunityPage;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.util.config.SfdcConfig;
import com.gainsight.util.config.SfdcConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.transactions.pages.Transactions;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;

/**
 * Base Class to hold all the Top Level Navigations
 * 
 * @author gainsight1
 * 
 */
public class BasePage extends WebPage implements Constants {

	public final String USERNAVBUTTON       = "userNavButton";
    public final String SETUP_LINK          = "//a[@title='Setup' and text()='Setup']";
    private final String APP_DROPDOWN       = "tsidLabel";
    private final String LOADING_IMG        = "//div[contains(text(), 'gs-loadingMsg gs-loader-container')]";
	private final String OPPORTUNITIES_TAB  = "//img[@title='Opportunities']";
	private final String ALL_TABS           = "//img[@title='All Tabs']";
	private final String ACCOUNTS_TAB       = "//a[@title='Accounts Tab']";
	private final String DEFAULT_APP_RADIO  = "//td[text()='%s']/following-sibling::td//input[@type='radio']";
	private final String TAB_SELECT         = "//td[contains(@class,'labelCol') and contains(.,'%s')]//following-sibling::td//select";
	private final String C360_TAB           = "//a[contains(@title,'Customer Success 360')]";
    private final String CUSTOMER_TAB       = "//a[contains(@title,'Customers Tab')]";
    private final String TRANSACTIONS_TAB   = "//a[contains(@title,'Transactions Tab')]";
    private final String RETENTION_TAB      = "//a[contains(@title, 'Retention Tab')]";
    private final String CHURN_TAB          = "//a[contains(@title,'Churn Tab')]";
    private final String ADOPTION_TAB       = "//a[contains(@title,'Engagement Tab')]";
    private final String SURVEY_TAB         = "//a[contains(text(),'Survey')]";
    private final String ADMINISTRATION_TAB = "//a[contains(@title,'Administration')]";
    private final String WORKFLOW_TAB		= "//a[contains(@title, 'Cockpit Tab')]";
    private final String MORE_TABS          = "MoreTabs_Tab";
    private final String MORE_TABS_LIST     = "MoreTabs_List";
    protected final String LOADING_ICON       = "//div[contains(@class, 'gs-loader-image')]";
	private final String LOADING_ICON_360 = "//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]";
    private final String SEARCH_LOADING     = "//div[@class='base_filter_search_progress_icon']";

	public Transactions transactionUtil     = new Transactions();
	public AmountsUtil amtUtil  = new AmountsUtil();
    SfdcConfig sfdcConfig = ConfigProviderFactory.getConfig(SfdcConfig.class);;

	public BasePage login() {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.clearAndSetText("username", sfdcConfig.getSfdcUsername());
		field.clearAndSetText("password", sfdcConfig.getSfdcPassword());
		item.doubleClick("Login");
        try {
            wait.waitTillElementPresent(USERNAVBUTTON, MIN_TIME, MAX_TIME);
        } catch (Exception e) {
            Log.info("Trying to clicking on continue in on schedule screen.");
            if(isTextPresent("Scheduled Maintenance Notification")) {
                item.click("//a[@class='continue' and text()='Continue']");
                wait.waitTillElementPresent(USERNAVBUTTON, MIN_TIME, MAX_TIME);
            } else {
                Log.error("Login Failed");
                throw new RuntimeException("Login Failed");
            }
        }

		return this;
	}

	public BasePage login(String username, String pwd) {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.setTextField("username", username);
		field.setTextField("password", pwd);
		button.click("Login");
		wait.waitTillElementPresent(USERNAVBUTTON, MIN_TIME, MAX_TIME);
		return this;
	}

	public BasePage logout() {
		element.switchToMainWindow();
        wait.waitTillElementPresent("userNavButton", MIN_TIME, MAX_TIME);
		item.click("userNavButton");
		item.click("//a[text()='Logout']");
		return this;
	}

	public void beInMainWindow() {
		element.switchToMainWindow();
	}

    public BasePage selectGainsightApplication() {
        String appName = "Gainsight";
        if(appName.equals(element.getText(APP_DROPDOWN).trim())) {
            Log.info("Gainsight App is selected already...");
        } else {
            item.click(APP_DROPDOWN);
            item.click("//a[@class='menuButtonMenuLink' and contains(text(), '"+appName+"')]");
            Log.info("Gainsight App Selected.");
        }
        return this;
    }


	public OpportunityPage gotoOpportunityPageWithId(String Id){
		driver.get(env.getDefaultUrl() + "/" + Id);
		return new OpportunityPage();
	}
	// Start of Top Level Navigation
	public CustomerBasePage clickOnCustomersTab() {
        clickOnTab(CUSTOMER_TAB);
		return new CustomerBasePage();
	}

	public TransactionsBasePage clickOnTransactionTab() {
        clickOnTab(TRANSACTIONS_TAB);
		return new TransactionsBasePage();
	}

	public AdoptionBasePage clickOnAdoptionTab() {
        clickOnTab(ADOPTION_TAB);
        Timer.sleep(2);
		return new AdoptionBasePage();
	}

    public WorkflowBasePage clickOnWorkflowTab(){
    	clickOnTab(WORKFLOW_TAB);
    	return new WorkflowBasePage();
    }

	public SurveyBasePage clickOnSurveyTab() {
        clickOnTab(SURVEY_TAB);
		return new SurveyBasePage();
	}

	public ChurnPage clickOnChurnTab() {
        clickOnTab(CHURN_TAB);
		return new ChurnPage();
	}
	public Customer360Page clickOnC360Tab() {
		clickOnTab(C360_TAB);
        return new Customer360Page();
	}

	 public AccountPage gotoAccountPageWithId(String accID){
	        driver.get(env.getDefaultUrl() + "/" + accID);
	        return new AccountPage();
	    }

    private void clickOnTab(String xpath) {
        try {
            WebElement wEle = element.getElement(xpath);
            if(wEle.isDisplayed()) {
                wEle.click();
            }
        } catch (Exception e) {
            item.click(MORE_TABS);
            wait.waitTillElementDisplayed(MORE_TABS_LIST, MIN_TIME, MAX_TIME);
            item.click(xpath);
        }
    }

	public AdministrationBasePage clickOnAdminTab() {
		clickOnTab(ADMINISTRATION_TAB);
		return new AdministrationBasePage();
	}

	public void setFilter(String filterFiledName, String value) {
		field.setTextByKeys("//input[@name='" + filterFiledName + "']", value);
	}

    public void switchToMainWindow() {
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Log.info("Moving to Main window : " + windows.size());
        driver.switchTo().window(aa.get(0));
    }

	public  void waitTillNoLoadingIcon() {
        env.setTimeout(1);
        wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

	public void waitForNoLoadingIconDisplayed() {
		env.setTimeout(1);
		wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
		env.setTimeout(30);
	}

    public void waitTillNoSearchIcon() {
        env.setTimeout(1);
        wait.waitTillElementNotDisplayed(SEARCH_LOADING, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

    public void waitTillNoLoadingIcon_360(){
          	env.setTimeout(1);
              wait.waitTillElementNotPresent(LOADING_ICON_360, MIN_TIME, MAX_TIME);
              env.setTimeout(30);
    }
	public void goBack() {
		driver.navigate().back();

	}
	public void enterDate(String identifier, String date) {
		//field.click(identifier);
		//field.click("//td[@class='weekday']");
		field.clearAndSetText(identifier, date);
	}

    public WebElement getFirstDisplayedElement(String identifier) {
        Log.info("Element Identifier : " + identifier);
        List<WebElement> elements = element.getAllElement(identifier);
        //Log.info("Total Number of Elements :" +elements.size());
        for(WebElement ele : elements) {
            //Log.info("Element displayed : "+ele.isDisplayed() );
            if(ele.isDisplayed()) {
                return ele;
            }
        }
        return null;
    }

    public void waitForLoadingImagesNotPresent() {
        env.setTimeout(2);
        wait.waitTillElementNotPresent(LOADING_IMG, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

    /**
     * Selects a value in drop down searched by the given value. Elements  matching the selector are identified from top of the page.
     * These elements are then iterated and checked whether they are displayed. The first element found as diplayed is selected.
     * @param value Value that needs to be searched and selected
     * @param reverseSelect set it to true in case you want to reverse elemenet selection procedure. If set true the first element from the last that is displayed will be selected.
     *                      This option is faster if you know that the first element from the last is the one that you are looking for.
     */
    public void selectValueInDropDown(String value, boolean reverseSelect) {
        String selector = "//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]";
		List<WebElement> listElement = element.getAllElement(selector);
		boolean selected = false;

        if(reverseSelect) {
            Collections.reverse(listElement);
        }

		for (WebElement ele : listElement) {
            Log.info("Checking : "+ele.isDisplayed());
            if(ele.isDisplayed()) {
                ele.click();
                selected = true;
                break;
            }
        }
        if(selected != true) {
            throw new RuntimeException("Unable to select element : //input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]" );
        }
    }

    /**
     * Selects a value in drop down searched by the given value. Elements  matching the selector are identified from top of the page.
     * These elements are then iterated and checked whether they are displayed. The first element found as diplayed is selected.
     * @param value Value that needs to be searched and selected
     */
    public void selectValueInDropDown(String value) {
        selectValueInDropDown(value, false);
    }

    public BasePage setSiteActiveHomePage(String siteActiveHomePage) {
        item.click(USERNAVBUTTON);
        item.click(SETUP_LINK);
        wait.waitTillElementDisplayed("DevToolsIntegrate_icon", MIN_TIME, MAX_TIME);
        item.click("DevToolsIntegrate_icon");
        wait.waitTillElementDisplayed("CustomDomain_font", MIN_TIME, MAX_TIME);
        item.click("CustomDomain_font");
        wait.waitTillElementDisplayed("//a[@class='actionLink' and contains(@title, 'Edit') and text()='Edit']", MIN_TIME, MAX_TIME);
        item.click("//a[@class='actionLink' and contains(@title, 'Edit') and text()='Edit']");
        wait.waitTillElementDisplayed("thePage:theForm:thePageBlock:thePageBlockSection:IndexPageSec:IndexPage", MIN_TIME, MAX_TIME);
        wait.waitTillElementDisplayed("thePage:theForm:thePageBlock:thePageBlockButtons:bottom:save", MIN_TIME, MAX_TIME);
        field.clearAndSetText("thePage:theForm:thePageBlock:thePageBlockSection:IndexPageSec:IndexPage", siteActiveHomePage);
        item.click("thePage:theForm:thePageBlock:thePageBlockButtons:bottom:save");
        wait.waitTillElementDisplayed("thePage:form:thePageBlock:pageBlockButtons:edit", MIN_TIME, MAX_TIME);
        return this;
    }

	public static void open(String url) {
		Application.getDriver().get(url);
		Log.info("Opening" + " " + url);
	}

	public static void navigateBack() {
		Application.getDriver().navigate().back();
	}

	public static String getCurrentUrl(){
		return Application.getDriver().getCurrentUrl();
	}
}
