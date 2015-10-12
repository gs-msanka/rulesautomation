package com.gainsight.bigdata.rulesengine.pages;

import com.gainsight.sfdc.pages.BasePage;
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
    private final String SWITCH_ON_OFF_RULE = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']/ancestor::div[contains(@class, 're-rule-content')]/descendant::input[contains(@class, 'onoffswitch')]";
    private final String RULE_INACTIVE = "//span[contains(@class, 'gs-re-rule-name inactive') and normalize-space(text())='%s']";
    private final String DELETE_RULE_LINK = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']/ancestor::div[contains(@class, 're-rule-content')]/descendant::span[@title='Delete']";
    private final String EDIT_RULE_LINK = "//span[contains(@class, 'gs-re-rule-name') and normalize-space(text())='%s']/ancestor::div[contains(@class, 're-rule-content')]/descendant::span[@title='Edit']";
    
    
    private final String ADD_RULE_LINK = "//input[contains(@class, 'add-rule') and @value='+ Rule']";

    public RulesManagerPage() {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
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
     * Switches off th rule with given name
     * @param ruleName rule name to switch off
     */
    public void switchOffRuleByName(String ruleName) {
        String ruleNameOFF = String.format(SWITCH_ON_OFF_RULE, ruleName);
        Log.info("Rule xpath is" + " " + ruleNameOFF);
        item.click(ruleNameOFF);
        wait.waitTillElementDisplayed("//div[contains(@class, 'layout_popup ui-dialog-content ui-widget-content')]",
                MIN_TIME, MAX_TIME);
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
        item.click(INACTIVE_RULES_CHECKBOX);
    }

    /**
     * Delete a rule by given name
     * @param ruleName Rule name ot delete
     */
    public void deleteRuleByName(String ruleName) {
        String ruleNameToDelete = String.format(DELETE_RULE_LINK, ruleName);
        wait.waitTillElementDisplayed("//div[contains(@class, 'layout_popup ui-dialog-content ui-widget-content')]",
                MIN_TIME, MAX_TIME);
        item.click(ruleNameToDelete);
        item.click(CONFIRMATION_BUTTON);
        waitForPageLoad();
    }

    /**
     * Clicks on the edit option for a given rule
     * @param ruleName Rule name to edit
     * @return EditRulePage object after clciking on the dit button
     */
    public EditRulePage editRuleByName(String ruleName) {
        String ruleNameToEdit = String.format(EDIT_RULE_LINK, ruleName);
        Log.info("Rule xpath is" + " " + ruleNameToEdit);
        item.click(ruleNameToEdit);
        return new EditRulePage();

    }
}
