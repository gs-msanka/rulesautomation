package com.gainsight.bigdata.rulesengine.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.gainsight.pageobject.core.WebPage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 8/26/2015.
 */
public class RulesManagerPage extends BasePage {

    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image')]";
    private final String READY_INDICATOR = "//div[@class='Rules_homepage']";
    private final String RULES_LISTING_PAGE = "//div[@class='Rules_Manager']";
    private final String SEARCH_RULE_NAME_FIELD = "//input[@class='search-rule' and @placeholder='Filter by rule name']";
 //   private final String ADD_RULE_LINK = "//input[@value='+ Rule']";
    private final String RULE_NAME_LINK = "//span[contains(@class, 'gs-re-rule-name')]";
    private final String RULE_WITH_NAME = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']";
    private final String ACTIVE_RULES_CHECKBOX = "//span[contains(@class,'gs-re-active')]";
    private final String INACTIVE_RULES_CHECKBOX = "//span[contains(@class,'gs-re-inactive')]";
    private final String RUN_RULE = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']/ancestor::div[contains(@class, 're-rule-content')]/descendant::span[@title='Run rule']";
    private final String CONFIRMATION_BUTTON = "//input[contains(@class, 'saveSummary') and @data-action='Yes']";
	private final String SWITCH_ON_OFF_RULE = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']/following::div[contains(@class, 'pull-right gs-re-action-list')]/descendant::div[contains(@class, 'onoffswitch-ctn pull-left')]/descendant::label";
    private final String RULE_INACTIVE = "//span[contains(@class, 'gs-re-rule-name inactive') and normalize-space(text())='%s']";
    private final String RULES_CONFIGURE_LINK = "//div[contains(@class, 'configure-settings')]/descendant::span";
    private final String RULE_LISTING_ACTIONS = "//span[contains(@class, 'gs-re-rule-name') and (contains(text(),'%s'))]/following::span[@title='%s']";
    private final String CLONE_RULE_INPUT = "//div[contains(@class, 'template-saveAs-popup')]/descendant::input[@placeholder='Name your Rule']";
    private final String SAVE_OK_BUTTON = "//input[contains(@class, 'btn_save') and @data-action='OK']";

    
    private final String ADD_RULE_LINK = "//input[contains(@class, 'add-rule') and @value='+ Rule']";

    public RulesManagerPage() {

    }

    public void waitForPageLoad() {
        Log.info("Refreshing the Page");
        wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
    }

    /**
     * Clicks on the add rule page button
     * @return EditRulePage class object
     */
    public EditRulePage clickOnAddRule() {
        item.click(ADD_RULE_LINK);
        return new EditRulePage();
    }

    /**
     * Lists all the rules
     */
    public void getListOfAllRules() {
        //TODO: To be implemented
        throw new RuntimeException("Method not implemented");
    }

    /**
     * Searches for a rule
     * @param ruleNameToSearch
     */
    public void searchForRule(String ruleNameToSearch) {
        element.clearAndSetText(SEARCH_RULE_NAME_FIELD, ruleNameToSearch);
    }

    /**
     * Clicks on a rule with given name
     * @param ruleName Rule name to click on
     */
    public void clickOnRuleWithName(String ruleName) {
        String ruleNameLink = String.format(RULE_WITH_NAME, ruleName);
        item.click(ruleNameLink);
    }

    /**
     * Switches off the rule with given name
     * @param ruleName rule name to switch off
     */
    public void switchOffRuleByName(String ruleName) {
		String ruleNameOFF = String.format(SWITCH_ON_OFF_RULE, ruleName);
		Log.info("Rule xpath is" + " " + ruleNameOFF);
		item.click(String.format(RULE_WITH_NAME, ruleName));
		wait.waitTillElementDisplayed("//div[@name='"+ruleName+"']/following-sibling::div[contains(@class, 'details-cnt')]", MIN_TIME, MAX_TIME);
		JavascriptExecutor executor = (JavascriptExecutor)Application.getDriver();
		executor.executeScript("arguments[0].click();", element.getElement(ruleNameOFF));
		wait.waitTillElementDisplayed(
				"//div[contains(@class, 'layout_popup ui-dialog-content ui-widget-content')]", MIN_TIME, MAX_TIME);
		item.click(CONFIRMATION_BUTTON);
		waitForPageLoad();
    }

    /**
     * Switches on a rule with given name
     * @param ruleName rule name to switch on
     */
    public void switchOnRuleByName(String ruleName) {
        String ruleNameON = String.format(SWITCH_ON_OFF_RULE, ruleName);
        Log.info("Rule xpath is" + " " + ruleNameON);
        item.click(ruleNameON);
        waitForPageLoad();
    }

    /**
     * Check if a given rule is on of off
     * @param ruleName Rule name of the rule to check
     * @return true if the rule is on else false
     */
    public boolean isRuleOn(String ruleName) {
        String isRuleActive = String.format(RULE_INACTIVE, ruleName);
        Log.info("Rule xpath is" + " " + isRuleActive);
        element.isElementPresent(isRuleActive);
        return false;
    }

    /**
     * Runs a rule with given name
     * @param ruleName Rule name of the rule to run
     */
    public void runRulesWithName(String ruleName) {
        String runRule = String.format(RUN_RULE, ruleName);
        Log.info("Rule Name is" + " " + runRule);
        item.click(runRule);

    }

    /**
     * Click on active rule checkbox
     */
    public void clickOnActiveRules() {
        item.click(ACTIVE_RULES_CHECKBOX);
    }

    /**
     * Click on Inactive rule checkbox
     */
    public void clickOnInActiveRules() {
    	wait.waitTillElementPresent(INACTIVE_RULES_CHECKBOX, MIN_TIME, MAX_TIME);
        item.click(INACTIVE_RULES_CHECKBOX);
    }

    /**
     * Delete a rule by given name
     * @param ruleName Rule name to delete
     */
    public void deleteRuleByName(String ruleName) {
		String ruleNameToDelete = String.format(RULE_LISTING_ACTIONS, ruleName, "Delete");
		item.click(String.format(RULE_WITH_NAME, ruleName));
		wait.waitTillElementDisplayed("//div[@name='"+ruleName+"']/following-sibling::div[contains(@class, 'details-cnt')]", MIN_TIME, MAX_TIME);
		JavascriptExecutor executor = (JavascriptExecutor)Application.getDriver();
		executor.executeScript("arguments[0].click();", element.getElement(ruleNameToDelete));
		wait.waitTillElementDisplayed(
				"//div[contains(@class, 'layout_popup ui-dialog-content ui-widget-content')]", MIN_TIME, MAX_TIME);
		item.click(CONFIRMATION_BUTTON);
		wait.waitTillElementDisplayed(ADD_RULE_LINK, MIN_TIME, WAIT_FOR_A_MINUTE);
    }

    /**
     * Clicks on the edit option for a given rule
     * @param ruleName Rule name to edit
     * @return EditRulePage object after clicking on the edit button
     */
    public EditRulePage editRuleByName(String ruleName) {
		String ruleNameToEdit = String.format(RULE_LISTING_ACTIONS, ruleName, "Edit");
		Log.info("Rule xpath is" + " " + ruleNameToEdit);
		item.click(String.format(RULE_WITH_NAME, ruleName));
		wait.waitTillElementDisplayed("//div[@name='"+ruleName+"']/following-sibling::div[contains(@class, 'details-cnt')]", MIN_TIME, MAX_TIME);
		JavascriptExecutor executor = (JavascriptExecutor)Application.getDriver();
		executor.executeScript("arguments[0].click();", element.getElement(ruleNameToEdit));	
		return new EditRulePage();
    }

    /**
     * Clicks on the configure option in rulesmanager page
     * @return DataLoadConfiguration object after clicking on configure link
     */
    public DataLoadConfiguration clickOnConfigure(){
    	item.click(RULES_CONFIGURE_LINK);
    	return new DataLoadConfiguration();
    }
    
    /**
     * checks whether rule is inactive or not
     * @param ruleName Rule name 
     * @return true if rule is inactive else false
     */
    public boolean isRuleInActive(String ruleName){
    	return element.getElement(String.format(RULE_INACTIVE, ruleName)).isDisplayed();	
    }
    
    /**
     * Clicks on clone option by ruleName
     * @param ruleName Rule name to click on
     * @param ruleName Rule name to clone
     */
    public void cloneARuleByName(String ruleName, String newRuleName) {
		String cloneRuleLink = String.format(RULE_LISTING_ACTIONS, ruleName, "Clone");
		Log.info("Rule xpath is" + " " + cloneRuleLink);
		item.click(String.format(RULE_WITH_NAME, ruleName));
		wait.waitTillElementDisplayed("//div[@name='"+ruleName+"']/following-sibling::div[contains(@class, 'details-cnt')]", MIN_TIME, MAX_TIME);
		JavascriptExecutor executor = (JavascriptExecutor)Application.getDriver();
		executor.executeScript("arguments[0].click();", element.getElement(cloneRuleLink));
		wait.waitTillElementDisplayed(CLONE_RULE_INPUT, MIN_TIME, MAX_TIME);
		element.clearAndSetText(CLONE_RULE_INPUT, newRuleName);
		item.click(SAVE_OK_BUTTON);
		clickOnInActiveRules();
		waitForPageLoad();
    }
    
    /**
     * Method to check whether particular rule is present or not UI
     * @param ruleName Rule name
     * @return true if rule is inactive else false 
     */
    public boolean isRulePresentByName(String ruleName){
    	return element.isElementPresent(String.format(RULE_WITH_NAME, ruleName));
	}
    
    /**
     * Method to check whether editRule page is present or not
     * @param ruleName Rule name
     * @return true if page is present else false
     */
    public boolean isEditRulePagePresent(){
    	 return element.isElementPresent(READY_INDICATOR);
	}
    
	/**
	 * @param rulesUrl
	 */
	public void openRulesManagerPage(String rulesUrl) {
		URL = rulesUrl;
		open();
		wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);

	}
}
