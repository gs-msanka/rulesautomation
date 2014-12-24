package com.gainsight.sfdc.pages;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.accounts.pages.AccountsPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.churn.pages.ChurnPage;
import com.gainsight.sfdc.customer.pages.CustomerBasePage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.helpers.AmountsAndDatesUtil;
import com.gainsight.sfdc.helpers.Transactions;
import com.gainsight.sfdc.opportunities.pages.OpportunitiesPage;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.transactions.pages.TransactionsBasePage;
import com.gainsight.sfdc.widgets.AccountWidget.pages.AccountWidgetPage;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;
import com.gainsight.testdriver.TestEnvironment;

/**
 * Base Class to hold all the Top Level Navigations
 * 
 * @author gainsight1
 * 
 */
public class BasePage extends WebPage implements Constants {

	private final String READY_INDICATOR    = "//div[@id='userNavButton']";

    private final String LOADING_IMG            = "//div[contains(text(), 'gs-loadingMsg gs-loader-container')]";
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
    private final String LOADING_ICON       = "//div[contains(@class, 'gs-loader-image')]";
	private final String LOADING_ICON_360 = "//div[@class='gs-loadingMsg gs-loader-container-64' and contains(@style,'display: block;')]";
    private final String SEARCH_LOADING     = "//div[@class='base_filter_search_progress_icon']";
    public Transactions transactionUtil     = new Transactions();
	public AmountsAndDatesUtil amtDateUtil  = new AmountsAndDatesUtil();


	public BasePage login() {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.setTextField("username", TestEnvironment.get().getUserName());
		field.setTextField("password", TestEnvironment.get().getUserPassword());
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		return this;
	}

	public BasePage login(String username, String pwd) {
		if(!driver.getCurrentUrl().contains("login")){
			driver.get(env.getDefaultUrl());
		}
		field.setTextField("username", username);
		field.setTextField("password", pwd);
		button.click("Login");
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
		return this;
	}

	public BasePage logout() {
		element.switchToMainWindow();
		item.click("userNavButton");
		item.click("//a[text()='Logout']");
		return this;
	}

	public void beInMainWindow() {
		element.switchToMainWindow();
	}
	
	public AccountWidgetPage gotoAccountPageWithId(String Id){
		driver.get(env.getDefaultUrl()+"/"+Id);
		element.switchToFrame("//iframe[@title='CustomerSuccess']");
		return new AccountWidgetPage();
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
        amtDateUtil.stalePause();
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
	public OpportunitiesPage clickOnOpportunitiesTab() {
		if (!field.isElementPresent(OPPORTUNITIES_TAB)) {
			item.click(ALL_TABS);
		}
		item.click(OPPORTUNITIES_TAB);
		return new OpportunitiesPage();
	}

	public AccountsPage clickOnAccountsTab() {
		clickOnTab(ACCOUNTS_TAB);
		return new AccountsPage();
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

	public void setDefaultApplication(String appName) {
		item.click("userNavButton");
		link.clickLink("Setup");
		wait.waitTillElementPresent("//a[text()='Manage Users']", MIN_TIME, MAX_TIME);
		link.clickLink("Manage Users");
		link.clickLink("Profiles");
		wait.waitTillElementPresent("//a[contains(.,'System Administrator')]",MIN_TIME, MAX_TIME);
		item.click("//td[@id='bodyCell']//tr[contains(.,'System Administrator')]//a[contains(.,'Edit')]");
		wait.waitTillElementPresent("//input[@title='Save']", MIN_TIME, MAX_TIME);
		item.click("//input[@title='"+appName+" Default']");
        field.selectFromDropDown(String.format(TAB_SELECT, "Accounts"),"Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Administration"),"Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Engagement"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Churn"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Customers"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "NPS"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Actions"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Survey"), "Default On");
		field.selectFromDropDown(String.format(TAB_SELECT, "Transactions"), "Default On");
        field.selectFromDropDown(String.format(TAB_SELECT, "Insights"), "Default On");
        field.selectFromDropDown(String.format(TAB_SELECT, "Gainsight"), "Default On");
        field.selectFromDropDown(String.format(TAB_SELECT, "Cockpit"), "Default On");
        field.selectFromDropDown(String.format(TAB_SELECT, "Customer Success 360"), "Default On");
        field.selectFromDropDown(String.format(TAB_SELECT, "Cockpit"), "Default On");
		item.click("//input[@title='Save']");
		wait.waitTillElementPresent("//a[contains(.,'System Administrator')]", MIN_TIME, MAX_TIME);
	}

    public void addTabsToApplication(String appName, String tabs) {
        item.click("userNavButton");
        link.clickLink("Setup");
        wait.waitTillElementDisplayed("DevTools_icon", MIN_TIME, MAX_TIME);
        item.click("DevTools_icon");
        wait.waitTillElementDisplayed("TabSet_font", MIN_TIME, MAX_TIME);
        item.click("TabSet_font");
        wait.waitTillElementDisplayed("//table[@class='list']/descendant::a[contains(text(), '"+appName+"')]", MIN_TIME, MAX_TIME);
        item.click("//table[@class='list']/descendant::a[contains(text(), '"+appName+"')]");
        wait.waitTillElementDisplayed("//input[@title='Edit' and @name ='edit']", MIN_TIME, MAX_TIME);
        item.click("//input[@title='Edit' and @name ='edit']");
        wait.waitTillElementDisplayed("//input[@type='submit' and @name='save']", MIN_TIME, MAX_TIME);
        Select multiDropDown = new Select(element.getElement("duel_select_0"));
        String[] tabsList = tabs.split(",");
        env.setTimeout(1);
        for(String tabName : tabsList) {
            try {
                multiDropDown.selectByVisibleText(tabName.trim());
                item.click("//img[@class='rightArrowIcon' and @title='Add']");
            } catch (Exception e) {
                Log.info("The Following tab is not available : " +tabName + " (or) added to application already");
            }
        }
        env.setTimeout(30);
        item.click("overwrite_user_setting");
        item.click("//input[@type='submit' and @name='save']");
        wait.waitTillElementDisplayed("//input[@title='Edit' and @name ='edit']", MIN_TIME, MAX_TIME);
    }

	public void loadDefaultData() throws URISyntaxException {
        String APPLICATION_SELECTION = "tsidButton";
        String APP_DROP_DOWN = "tsidMenu";
        wait.waitTillElementDisplayed(APPLICATION_SELECTION, MIN_TIME, MAX_TIME);
        item.click(APPLICATION_SELECTION);
        wait.waitTillElementDisplayed(APP_DROP_DOWN, MIN_TIME, MAX_TIME);
        String a = "//a[contains(@class, 'menuButtonMenuLink') and contains(text(), 'Gainsight')]";
        try{
            item.click(a);
            wait.waitTillElementDisplayed(CUSTOMER_TAB, MIN_TIME, MAX_TIME);
        } catch (Exception e) {
            String a1 = "//span[@id='tsidLabel' and contains(text(), 'Gainsight')]";
            if(element.isElementPresent(a1)) {
                Log.info("Gainsight application is not available to select");
            }
        }
        clickOnCustomersTab();
		URI uri=new URI(driver.getCurrentUrl());
		String hostName="https://"+uri.getHost();
		driver.get(hostName+"/apex/loadsetupdata");
		String intializeButton="//input[@value='INITIALIZE']";
        env.setTimeout(2);
		if(element.isElementPresent(intializeButton)){
			item.click(intializeButton);
			wait.waitTillElementDisplayed("loadSetupDatatable", MIN_TIME, MAX_TIME);
		}
		driver.get(hostName+"/apex/loadsampledata");
        String loadButton="//input[@value='Load']";
        try {
            String DELETE_BUTTON = "//input[@class='btn' and contains(@value, 'Go')]";
            wait.waitTillElementDisplayed(DELETE_BUTTON, MIN_TIME,MAX_TIME);
            item.click(DELETE_BUTTON);
            Log.info("Deleting the sample data");
            amtDateUtil.sleep(15);
            wait.waitTillElementDisplayed(loadButton, MIN_TIME, 2*MAX_TIME);
            Log.info("Sample data delete completed.");
        } catch(Exception e) {
            Log.info("Sample data is not loaded in to org.");
        }
		if(element.isElementPresent(loadButton)){
			item.click(loadButton);
			//wait.waitTillElementDisplayed("loadSampleDatatable", MIN_TIME, MAX_TIME);
			wait.waitTillElementDisplayed("//input[@class='btn' and contains(@value, 'Go')]", MIN_TIME, MAX_TIME);
		}
        driver.get(hostName+"/apex/Customers");
        wait.waitTillElementDisplayed("//a[@data-tab='CUSTOMERS']", MIN_TIME, MAX_TIME);
        env.setTimeout(30);


	}
	
	public  void waitTillNoLoadingIcon() {
        env.setTimeout(1);
        wait.waitTillElementNotPresent(LOADING_ICON, MIN_TIME, MAX_TIME);
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
        Log.info("Element Identifier : " +identifier);
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
}
