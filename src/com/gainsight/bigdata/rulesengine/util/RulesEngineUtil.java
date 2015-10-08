package com.gainsight.bigdata.rulesengine.util;

import com.gainsight.bigdata.rulesengine.pages.NewRulePage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRulePage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.enums.ActionType;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.*;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.CalculatedField;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.FilterFields;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.SetupData;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.SetupRulePojo;
import com.gainsight.bigdata.rulesengine.pojo.setuprule.ShowFields;
import com.gainsight.sfdc.tests.BaseTest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by vmenon on 9/15/2015.
 *
 * Contains utils for rules engine automation.
 */
public class RulesEngineUtil  extends BaseTest{


    /**
     * Creates a rule on the ui based on the json input configuration file path
     * @param jsonFilePath Path of the json configuration file
     */
    public void createRuleFromUi(String jsonFilePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RulesPojo rulesPojo = mapper.readValue(new File(jsonFilePath), RulesPojo.class);
            createRuleFromUi(rulesPojo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a rule on the ui based on the input configuration file path
     * @param rulesPojo RulesPojo configuration object for creation of the rule.
     */
    public void createRuleFromUi(RulesPojo rulesPojo ) {
        NewRulePage newRulePage = new NewRulePage();
        newRulePage.enterRuleDetailsAndClickNext(rulesPojo);
        int i=1;
		if ((rulesPojo.getSetupActions().size() == 1)
				&& (rulesPojo.getSetupActions().get(0).getActionType().name()
						.contains("LoadToCustomers") && rulesPojo.getSetupRule().getDataSource().equalsIgnoreCase("Native"))) {
            setUpRule(rulesPojo.getSetupRule(), true);
           } else {
        	   setUpRule(rulesPojo.getSetupRule(), false);
           }
          
        if(rulesPojo.getSetupActions()!=null && !rulesPojo.getSetupActions().isEmpty()){
            SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
            List<RuleAction> ruleActions = rulesPojo.getSetupActions();
            for(RuleAction ruleAction : ruleActions) {
                createRuleActionOnUi(ruleAction, setupRuleActionPage, i);
                setupRuleActionPage.clickOnActionCollapse(i);
                i++;
            }
            setupRuleActionPage.saveRule();
        }
    }

    /**
     * Creates a rule action on the ui using the given rule action object provided. This method should be used for creationg of the 1st action on the page.
     * @param ruleAction RuleAction class object containing the action to be created configuration
     * @param setupRuleActionPage SetupRuleActionPage object
     */
    public void createRuleActionOnUi(RuleAction ruleAction, SetupRuleActionPage setupRuleActionPage) {
        createRuleActionOnUi(ruleAction, setupRuleActionPage, 1);
    }

    /**
     * Creates a rule action on the ui using the given rule action object provided.
     * @param ruleAction RuleAction class object containing the action to be created configuration
     * @param setupRuleActionPage SetupRuleActionPage object
     * @param i Number of the action that is being created for a particular rule. for ex. You are creating a rule with 4 actions then for 1st action this value will be 1 for 2nd action 2 and so on.
     */
    public void createRuleActionOnUi(RuleAction ruleAction, SetupRuleActionPage setupRuleActionPage, int i){
        ObjectMapper objectMapper = new ObjectMapper();
        ActionType actionType = ruleAction.getActionType();
        JsonNode actionObject = ruleAction.getAction();
        try {
            switch (actionType) {
                case CTA:
                    CTAAction ctaAction = objectMapper.readValue(actionObject, CTAAction.class);
                    ctaAction.setDefaultOwner(sfdcInfo.getUserFullName());
                    setupRuleActionPage.createCTA(ctaAction, i);
                    break;
                case LoadToCustomers:
                    LoadToCustomersAction loadToCustomersAction = objectMapper.readValue(actionObject, LoadToCustomersAction.class);
                    setupRuleActionPage.createLoadToCustomers(loadToCustomersAction, i);
                    break;
                case LoadToFeature:
                    LoadToFeatureAction loadToFeatureAction = objectMapper.readValue(actionObject, LoadToFeatureAction.class);
                    setupRuleActionPage.createLoadToFeature(loadToFeatureAction, i);
                    break;
                case LoadToMileStone:
                    LoadToMileStoneAction loadToMileStoneAction = objectMapper.readValue(actionObject,LoadToMileStoneAction.class);
                    setupRuleActionPage.createLoadToMileStone(loadToMileStoneAction, i);
                    break;
                case LoadToUsage:
                    LoadToUsageAction loadToUsageAction = objectMapper.readValue(actionObject, LoadToUsageAction.class);
                    setupRuleActionPage.createLoadToUsage(loadToUsageAction, i);
                    break;
                case SendEmail:
                    SendEmailAction sendEmailAction = objectMapper.readValue(actionObject, SendEmailAction.class);
                    setupRuleActionPage.createSendEmail(sendEmailAction);
                    break;
                case SetScore:
                    SetScoreAction setScoreAction = objectMapper.readValue(actionObject, SetScoreAction.class);
                    setupRuleActionPage.createSetScore(setScoreAction, i);
                    break;
                case LoadToSFDCObject:
                	LoadToSFDCAction loadToSFDCAction = objectMapper.readValue(actionObject, LoadToSFDCAction.class);
                    setupRuleActionPage.loadToSfdcObject(loadToSFDCAction, i);
                    break;
                case LoadToMDASubjectArea:
                	LoadToMDAAction loadToMDAAction = objectMapper.readValue(actionObject, LoadToMDAAction.class);
                    setupRuleActionPage.loadToMdaCollection(loadToMDAAction, i);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		int j = 1;
		for (Criteria criteria : ruleAction.getCriterias()) {
			setupRuleActionPage.addCriteria(criteria, i, j);
			j++;
		}
	}

    /**
     * Method to setup a rule on rules ui page. Selects the source object , drag & drop show/filter fields, add advanced logic and add calculated fields.
     * @param setupRulePojo
     * @param uncheckCustomers
     */
    public void setUpRule(SetupRulePojo setupRulePojo, Boolean uncheckCustomers ) {
		SetupRulePage setupRulePage = new SetupRulePage();
		setupRulePage.selectDataSource(setupRulePojo.getDataSource());
		setupRulePage.selectSourceObject(setupRulePojo.getSelectObject());
		for (SetupData setupData : setupRulePojo.getSetupData()) {
			String sourceObject = setupData.getSourceObject();
			for (ShowFields showField : setupData.getShowFields()) {
				if (setupRulePojo.getDataSource().equalsIgnoreCase("Native")) {
					setupRulePage.dragAndDropFieldsToShowArea(sourceObject,showField.getFieldName());
				} else {
					setupRulePage.dragAndDropFieldsToShowAreaForMatrixData(showField.getFieldName());
				}
			}
			for (FilterFields filterField : setupData.getFilterFields()) {
				if (setupRulePojo.getDataSource().equalsIgnoreCase("Native")) {
					setupRulePage.dragAndDropFieldsToActionsForNativeData(sourceObject, filterField.getFieldName(), filterField.getOperator(), filterField.getValue());
				} else {
					setupRulePage.dragAndDropFieldsToActionsForMatrixData(sourceObject, filterField.getFieldName(),filterField.getOperator(), filterField.getValue());
				}
			}
		}
		setupRulePage.enterAdvanceLogic(setupRulePojo.getAdvancedLogic());
		setupRulePage.selectTimeIdentifier(setupRulePojo.getTimeIdentifier());
		if (setupRulePojo.getCalculatedFields() != null && !setupRulePojo.getCalculatedFields().isEmpty()) {
			for (CalculatedField calculatedField : setupRulePojo.getCalculatedFields()) {
				setupRulePage.fillCalculatedFields(calculatedField);
			}
		}
		if (uncheckCustomers) {
			setupRulePage.unCheckApplyToGSCustomers();
		}
		setupRulePage.clickOnNext();
	}
}
