package com.gainsight.bigdata.rulesengine.pages;

import org.openqa.selenium.Keys;

import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 8/26/2015.
 */
public class EditRulePage extends BasePage {
    private final String READY_INDICATOR = "//div[contains(@class, 'rulesViewStack')]";
    private final String LOADING_ICON = "//div[contains(@class, 'RulesMainCntr gs gs-panel gs-container ready')]";
    private final String RULE_NAME = "//div/input[contains(@class,'rule-name')]";
    private final String RULE_DESCRIPTION = "//div/textarea[contains(@class,'rule-description')]";
    private final String RULE_NEXT = "//div/input[@value='Next']";
    private final String RULE_CANCEL = "//div/input[@value='Cancel']";
    private final String SETUP_RULE_LINK = "//div[contains(@class,'ruleSetupStepsWizard')]/ul/descendant::span[contains(@class,'step-id') and text()='2']";
    private final String SETUP_ACTION_LINK = "//li[@data-id ='SetupActionView']/a";
    private final String SETUP_SCHEDULE_LINK = "//li[@data-id ='SetupScheduleView']/a";
    private final String SELECT_RULE_BUTTON = "//select[contains(@class, 'select-type')]/following-sibling::button";
    private final String RULES_LIST_VIEW = "//li/a[@title ='Go to Rules List']";
    private final String RULES_HEADER = "//ul[contains(@class, 'gs-tabs')]";
    private final String RULE_FOR_ACCOUNT_TYPE = "rule-for-account";
    private final String RULE_FOR_RELATIONSHIP_TYPE = "rule-for-relationship";
    private final String RELATIONSHIP_TYPE = "//select[contains(@class, 'relationship-type')]/following-sibling::button";

    public EditRulePage() {
        wait.waitTillElementDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
    }

    public void waitForPageLoad() {
        Log.info("Refreshing the Page");
        wait.waitTillElementDisplayed(LOADING_ICON, MIN_TIME, TEN_SECONDS);
    }

    /**
     * Selects a rule type from the drop down
     * @param ruleType Rule type to select
     */
    public void selectRuleType(String ruleType) {
        item.click(SELECT_RULE_BUTTON);
        selectValueInDropDown(ruleType);
    }

    /**
     * Method to select rulefor type like account or relationship
     *
     * @param ruleForType - ruleFor Type
     */
    public void selectRuleFor(String ruleForType) {
        try {
            env.setTimeout(1);
            if (element.isElementDisplayed("//div[(contains(text(),'Rule For'))]")) {
                if ("Relationship".equalsIgnoreCase(ruleForType)) {
                    item.click(RULE_FOR_RELATIONSHIP_TYPE);
                } else {
                    item.click(RULE_FOR_ACCOUNT_TYPE);
                }
            }
        } finally {
            env.setTimeout(30);
        }
    }

    /**
     * Method to select relationship type from the list of relationships available
     *
     * @param relationshipType - RelationshipType
     */
    public void selectRelationShipType(String relationshipType) {
        try {
            env.setTimeout(1);
            if (element.isElementDisplayed("//div[(contains(text(),'Relationship Type'))]")) {
                item.click(RELATIONSHIP_TYPE);
                wait.waitTillElementDisplayed("//input[contains(@title, '" + relationshipType + "')]/following-sibling::span[contains(text(), '" + relationshipType + "')]", MIN_TIME, MAX_TIME);
                selectValueInDropDown(relationshipType);
            }
        } finally {
            env.setTimeout(30);
        }
    }

    /**
     * Enter a rule name
     * @param ruleName
     */
    public void enterRuleName(String ruleName) {
    	wait.waitTillElementDisplayed(RULE_NAME, MIN_TIME, MAX_TIME);
        field.clearAndSetText(RULE_NAME, ruleName);
        element.getElement(RULE_NAME).sendKeys(Keys.ENTER);
    }

    /**
     * Enters a rule description
     * @param description
     */
    public void enterRuleDescription(String description) {
        field.clearAndSetText(RULE_DESCRIPTION, description);
    }

    /**
     * Click on next button
     */
    public void clickOnNext() {
        item.click(RULE_NEXT);
        waitForPageLoad();
    }

    /**
     * Clicks on cancel button
     */
    public void clickOnCancel() {
        item.click(RULE_CANCEL);
    }

    /**
     * Enters a rule details based on given arguments and click on next
     * @param ruleType
     * @param ruleName
     * @param description
     */
    public void enterRuleDetailsAndClickNext(String ruleType, String ruleName, String description) {
        selectRuleType(ruleType);
        enterRuleName(ruleName);
        enterRuleDescription(description);
        clickOnNext();
        waitForPageLoad();
    }

    /**
     * Enters rule details based on the given RulesPojo object and click on next
     * @param rulesPojo
     */
    public void enterRuleDetailsAndClickNext(RulesPojo rulesPojo){
        selectRuleType(rulesPojo.getRuleType());
        selectRuleFor(rulesPojo.getRuleFor());
        selectRelationShipType(rulesPojo.getRelationshipType());
        enterRuleName(rulesPojo.getRuleName());
        enterRuleDescription(rulesPojo.getRuleDescription());
        clickOnNext();
    }

    /**
     * Clciks on setup rule link
     * @return
     */
    public SetupRulePage clickOnSetupRule() {
        item.click(SETUP_RULE_LINK);
        return new SetupRulePage();
    }

    /**
     * Clicks on schedule rule page link
     * @return
     */
    public RulesSchedulerPage clickOnScheduleRule() {
        item.click(SETUP_SCHEDULE_LINK);
        return new RulesSchedulerPage();
    }

    /**
     * Clicks on setup rule action page link
     * @return
     */
    public SetupRuleActionPage clickOnSetupRuleAction() {
        item.click(SETUP_ACTION_LINK);
        return new SetupRuleActionPage();
    }
    
    /**
     * Clicks on RulesList Screen link
     */
    public void clickOnRulesList(){
		item.click(RULES_LIST_VIEW);
		wait.waitTillElementDisplayed(RULES_HEADER, MIN_TIME, MAX_TIME);
	}

}