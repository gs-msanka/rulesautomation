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
    private final String READY_INDICATOR = "//div[@class='RuleContainer']";
    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image')]";
    private final String RULE_NAME = "//div/input[contains(@class,'rule-name')]";
    private final String RULE_DESCRIPTION = "//div/textarea[contains(@class,'rule-description')]";
    private final String RULE_NEXT = "//div/span[contains(@class,'btn-save')]";
    private final String RULE_CANCEL = "//div/span[contains(@class,'btn-cancel')]";
    private final String SETUP_RULE_LINK = "//li[@data-id ='SetupView']/a";
    private final String SETUP_ACTION_LINK = "//li[@data-id ='SetupActionView']/a";
    private final String SETUP_SCHEDULE_LINK = "//li[@data-id ='SetupScheduleView']/a";
    private final String SELECT_RULE_BUTTON = "//select[contains(@class, 'select-type')]/following-sibling::button";
    private final String RULES_LIST_VIEW = "//li[@data-id ='ListView']/a";
    private final String RULES_HEADER = "//div[contains(@class, 'gs-re-top-section')]";
    private final String RULE_FOR_ACCOUNT_TYPE = "rule-for-account";
    private final String RULE_FOR_RELATIONSHIP_TYPE = "rule-for-relationship";
    private final String RELATIONSHIP_TYPE = "//select[contains(@class, 'relationship-type')]/following-sibling::button";

    public EditRulePage() {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public void waitForPageLoad() {
        Log.info("Refreshing the Page");
        wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, TEN_SECONDS);
    }

    /**
     * Selects a rule type from the drop down
     * @param ruleType Rule type to select
     */
    public void selectRuleType(String ruleType) {
        try{
            item.click(SELECT_RULE_BUTTON);
            wait.waitTillElementDisplayed("//input[contains(@title, '\"+ruleType+\"')]/following-sibling::span[contains(text(), '\"+ruleType+\"')]", MIN_TIME, MAX_TIME);
            selectValueInDropDown(ruleType);
        }
        finally {
            env.setTimeout(20);
        }

    }

    /**
     * Method to select rulefor type like account or relationship
     *
     * @param ruleForType - ruleFor Type
     */
    public void selectRuleFor(String ruleForType) {
        try {
            env.setTimeout(1);
            if (element.isElementDisplayed("//label[(contains(text(),'Rule For'))]")) {
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
            if (element.isElementDisplayed("//label[(contains(text(),'Relationship Type'))]")) {
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