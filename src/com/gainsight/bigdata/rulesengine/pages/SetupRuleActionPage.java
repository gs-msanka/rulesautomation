package com.gainsight.bigdata.rulesengine.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.gainsight.bigdata.rulesengine.pojo.setupaction.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 9/3/2015.
 */
public class SetupRuleActionPage extends BasePage {

    private final String NOACTION_YET = "//p[contains(text(),'No action yet')]";
    private final String NOACTION_BUTTON = "//p[contains(text(),'No action yet')]/../input";
    private final String ACTION_BUTTON = "//input[contains(@value,'Save')]/../input[contains(@value,'Action')]";
    private final String SELECT_BUTTON = "//div[@class='action-type']//button";
    private final String PRIORITY_BUTTON = "//label[contains(text(),'Priority')]/following-sibling::div/select[contains(@class,'alertSeverity')]/following-sibling::button";
    private final String CTA_TYPE = "//label[contains(text(),'Priority')]/following-sibling::div/select[contains(@class,'alertType')]/following-sibling::button";
    private final String STATUS_BUTTON = "//label[contains(text(),'Status')]/following-sibling::div/select[contains(@class,'Status')]/following-sibling::button";
    private final String PLAYBOOK = "//label[contains(text(),'Status')]/following-sibling::div/select[contains(@class,'playBook')]/following-sibling::button";
    private final String REASON_BUTTON = "//label[contains(text(),'Reason')]/following-sibling::div/select[contains(@class,'Reason')]/following-sibling::button";
    private final String OWNERFIELD = "//label[contains(text(),'Reason')]/following-sibling::div/select[contains(@class,'OwnerField')]/following-sibling::button";
    private final String DUEDATE = "//div[@class='dueDate']/input";
    private final String DEFAULTOWNER = "//label[contains(text(),'Default Owner')]/following-sibling::div//input";
    private final String POSTUPDATE_BUTTON = "//select[contains(@class, 'comments_post_frequency')]/following-sibling::button";
    private final String COMMENTS = "//label[contains(text(),'Comments')]/../descendant::div[contains(@class,'form')]";
    private final String SAVE_BUTTON = "//input[@type='button' and @class='gs-btn btn-save']";
    private final String CREATE_CTA_RADIO_BUTTON = "//input[@value='create']";
    private final String CTA_NAME_INPUT = "//div[contains(@class, 'ctaName')]";
    private final String SCORECARD_COMMENTS = "//div[contains(@class, 'setup-action-body create-score-card')]/descendant::textarea[contains(@class, 'scorecardComment')]";
    private final String DUE_DATE_TYPE = "//select[contains(@class, 'due_date_type')]/following-sibling::button";

    private final String SHOWFIELD_LTM = "//label[contains(text(),'Date')]/..//input[@value='show_field']";
    private final String CONSTANT_SELECT_LTM = "//label[contains(text(),'Date')]/..//select[contains(@class,'constant')]/../button";
    private final String CONSTANT_SELECT_LTM_VALUE = "//label[contains(text(),'Date')]/..//select[contains(@class,'constant')]/..//input";
    private final String SHOEFIELD_SELECT_LTM = "//label[contains(text(),'Date')]/..//select[contains(@class,'show')]/../button";
    private final String MILESTONE_LTM = "//label[contains(text(),'Milestone')]/..//button";

    private final String PRODUCT_LTF = "//label[contains(text(),'Product')]/..//button";
    private final String FEATURE_LTF = "//label[contains(text(),'Feature')]/..//button";
    private final String LICENSED_SHOWFIELD_LTF = "//label[contains(text(),'Licensed')]/..//input[@value='show_field']";
    private final String LICENSED_CONSTANT_RADIO_BUTTON = "//input[@class='feature_lic_radio']";
    private final String LICENSED_CONSTANT_SELECT_LTF = "//label[contains(text(),'Licensed')]/..//select[contains(@class,'constant')]/../button";
    private final String ENABLED_SHOWFIELD_LTF = "//label[contains(text(),'Enabled')]/..//input[@value='show_field']";
    private final String ENABLED_SHOEFIELD_SELECT_LTF = "//label[contains(text(),'Enabled')]/..//select[contains(@class,'showfield')]/../button";
    private final String LICENCED_DROPDOWN_LOADTOFATURE = "//div[contains(@class, 'feature-lic-constant-block')]/descendant::select/following-sibling::button";
    private final String ENABLED_DROPDOWN_LOADTOFATURE = "//div[contains(@class, 'feature-enabled-constant')]/descendant::select/following-sibling::button";
    private final String ENABLED_CONSTANT_RADIO_BUTTON = "//input[@class='feature_enabled_radio']";

    private final String SCORE_MEASURE = "//label[contains(text(),'Measure')]/..//button";

    private final String EMAILSERVICE_BUTTON = "//label[contains(text(),'Email Service')]/..//select[contains(@class,'emailService')]/../button";
    private final String EMAILTEMPLATE_BUTTON = "//label[contains(text(),'Email Service')]/..//select[contains(@class,'emailTemplate')]/../button";
    private final String FROM_NAME = "//label[contains(text(),'From Name')]/..//select[contains(@class,'fromName')]/..//input";
    private final String FROM_EMAIL = "//label[contains(text(),'From Name')]/..//select[contains(@class,'fromAddress')]/..//input";
    private final String TO = "//label[contains(text(),'Reply-To')]/..//select[contains(@class,'recipient')]/../button";
    private final String REPLY_TO = "//label[contains(text(),'Reply-To')]/..//select[contains(@class,'replyto')]/../button";

    private final String TYPE_CCTA = "//div[@class='close_ctn']//label[contains(text(),'Type')]/following-sibling::div/select[contains(@class,'alertType')]/following-sibling::button";
    private final String REASON_CCTA = "//div[@class='close_ctn']//label[contains(text(),'Reason')]/following-sibling::div//button";
    private final String SOURCE_CCTA = "//div[@class='close_ctn']//label[contains(text(),'Source')]/following-sibling::div/select[contains(@class,'Source')]/following-sibling::button";
    private final String CTASTATUS_CCTA = "//div[@class='close_ctn']//label[contains(text(),'status')]/following-sibling::div//button";
    private final String CLOSECTA_COMMENTS = "//div[contains(@class, 'close_ctn')]/descendant::div[contains(@class, 'alertComment')]";
    private final String CLOSE_CTA_RADIO_BUTTON = "//input[@value='close' and @value='close']";
    private final String CLOSE_CTA_SETSTATUS = "//input[@title='%s' and @value='%s']";

    private final String FIELD_MAPPING_LTU1 = "//span[contains(text(),'%s') and ";
    private final String FIELD_MAPPING_LTU2 = "contains(text(),'%s')]/../following-sibling::div/select";

    private final String CRITERIA = "//div[contains(text(),'Criteria')]/following-sibling::a";
    private final String CRITERIA_SHOWFIELD = "//select[@class='field']/following-sibling::button";
    private final String CRITERIA_SHOWFIELD_OPERATOR = "//select[@class='operator']/following-sibling::button";
    private final String CRITERIA_SHOWFIELD_FORWIDTH = "//select[@data-control='FIELD-VALUE']/following-sibling::button";
    private final String CRITERIA_SHOWFIELD_FORWIDTH_VALUE = "//select[@data-control='FIELD-RIGHT']/following-sibling::button";
    private final String CRITERIA_SHOWFIELD_INPUT = "//input";
    private final String CRITERIA_SHOWFIELD_INPUT_DROPDOWN = "//select[@data-control='RULE-DATE' or @data-control='LIST']/following-sibling::button";

    private final String MEASURE_SCORE_SLIDER_CIRCLE = "//*[local-name() = 'svg' and namespace-uri()='http://www.w3.org/2000/svg']/*[local-name()='circle']";
    private final String SAVE_SCORECARD = "//div[@class='save-options clearfix']/descendant::a[@data-action='SAVE']";


    private final String FIELD_MAPPING_DESTINATION = "//option[contains(text(), '%s')]";
    private final String LOAD_TO_OBJECT = "//div[contains(@class, 'only-for-load-to-object')]/descendant::select/following-sibling::button";
    private final String LOAD_TO_OBJECT_OPERATION_TYPE = "//div[contains(@class, 'only-for-load-to-object')]/descendant::select[contains(@class, 'operation-type')]/following-sibling::button";

    private final String NEWRULE_PART1 = "//div[contains(@class,'setup-action-ctn')]/div[";
    private final String NEWRULE_PART2 = "]";
    
    // "//div[contains(@class, 'form-control alertComment')]/following-sibling::div[@class='showFieldList']/descendant::span[text()='%s']";
    private final String COMMENTS_TOKENS_DIV = "//div[contains(@class, 'form-control alertComment')]/following-sibling::div[@class='showFieldList']/descendant::span[text()='%s']";

    /**
     * Selects the given action type
     *
     * @param actionType
     */
    public void selectActionType(String actionType) {
        item.click(SELECT_BUTTON);
        selectValueInDropDown(actionType);
    }

    /**
     * Clicks on the action button. Code handles whether any rules exists or not
     */
    public void clickOnActionButton() {
        try {
            env.setTimeout(0);
            if (element.getElement(NOACTION_YET).isDisplayed()) {
                item.click(NOACTION_BUTTON);
            }
        } catch (Exception e) {
            item.click(ACTION_BUTTON);
        } finally {
            env.setTimeout(MAX_ELEMENT_WAIT);
        }
    }

    /**
     * Click on the action collapse the action for which the index i is given.
     * @param index
     */
    public void clickOnActionCollapse(int index) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + index + "]//a[@class='collapseIcon']";
        element.getElement(xpath).click();
    }

    /**
     * Click on the action collapse the action for which the index i is given.
     */
    public void clickOnActionCollapse() {
        clickOnActionCollapse(1);
    }


    public void createCTA(CTAAction ctaAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        if (!ctaAction.isCtaUpsert()) {
        	clickOnActionButton();
            item.click(xpath + SELECT_BUTTON);
            selectValueInDropDown("Call To Action");
		}
        item.click(xpath + CREATE_CTA_RADIO_BUTTON);
        field.clearAndSetText(CTA_NAME_INPUT, ctaAction.getName());
        item.click(xpath + PRIORITY_BUTTON);
        selectValueInDropDown(ctaAction.getPriority());
        item.click(xpath + CTA_TYPE);
        selectValueInDropDown(ctaAction.getType());
        item.click(xpath + STATUS_BUTTON);
        selectValueInDropDown(ctaAction.getStatus());
        if (!ctaAction.getPlaybook().isEmpty()) {
        item.click(xpath + PLAYBOOK);
        selectValueInDropDown(ctaAction.getPlaybook());
        }
        item.click(xpath + REASON_BUTTON);
        selectValueInDropDown(ctaAction.getReason());
        field.clearAndSetText(xpath + DUEDATE, ctaAction.getDueDate());
        
        if (ctaAction.getOwnerField()!=null || !ctaAction.getOwnerField().isEmpty()) {
			item.click(xpath+OWNERFIELD);
			selectValueInDropDown(ctaAction.getOwnerField(), true);
		}
        
        if (!ctaAction.getChatterUpdate().isEmpty()) {
			item.click(xpath+POSTUPDATE_BUTTON);
			 selectValueInDropDown(ctaAction.getChatterUpdate(), true);
		}
        if (ctaAction.getDueDateType()!=null || !ctaAction.getDueDateType().isEmpty()) {
			item.click(DUE_DATE_TYPE);
			selectValueInDropDown(ctaAction.getDueDateType(), true);
		}
        selectTaskOwner(ctaAction.getDefaultOwner(), i);
		List<String> tokenList = Arrays.asList(ctaAction.getComments().split(","));
		for (String token : tokenList) {
			if (token.startsWith("@")) {
				element.setText(COMMENTS, token);
				item.click(String.format(COMMENTS_TOKENS_DIV, token.substring(token.indexOf("@") + 1)));
			} else {
				element.setText(xpath + COMMENTS, token);
			}
		}
        wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
    }

    public void createLoadToFeature(LoadToFeatureAction loadToFeatureAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to Feature");
        item.click(xpath + PRODUCT_LTF);
        selectValueInDropDown(loadToFeatureAction.getProduct());
        item.click(xpath + FEATURE_LTF);
        selectValueInDropDown(loadToFeatureAction.getFeature());

        if (loadToFeatureAction.getLicensed().getType().equalsIgnoreCase("Constant")) {
            item.click(xpath + LICENSED_CONSTANT_RADIO_BUTTON);
            item.click(xpath + LICENCED_DROPDOWN_LOADTOFATURE);
            selectValueInDropDown(loadToFeatureAction.getLicensed().getUpdateType());
        } else {
            item.click(xpath + LICENSED_SHOWFIELD_LTF);
            item.click(xpath + LICENCED_DROPDOWN_LOADTOFATURE);
            selectValueInDropDown(loadToFeatureAction.getLicensed().getUpdateType());
        }

        if (loadToFeatureAction.getEnabled().getType().equalsIgnoreCase("Constant")) {
            item.click(xpath + ENABLED_CONSTANT_RADIO_BUTTON);
            item.click(xpath + ENABLED_DROPDOWN_LOADTOFATURE);
            selectValueInDropDown(loadToFeatureAction.getEnabled().getUpdateType());
        } else {
            item.click(xpath + ENABLED_SHOWFIELD_LTF);
            item.click(xpath + ENABLED_DROPDOWN_LOADTOFATURE);
            selectValueInDropDown(loadToFeatureAction.getEnabled().getUpdateType());
        }
        element.clearAndSetText(xpath + COMMENTS, loadToFeatureAction.getComments());
    }

    public void fillLoadToUsage(String object, String field, String mapField) {
        String fieldMapping = String.format(FIELD_MAPPING_LTU1, object);
        fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, field);
        item.click(fieldMapping);
        selectValueInDropDown(mapField);
    }

    public void fillLoadToCustomers(String object, String field, String mapField) {
        String fieldMapping = String.format(FIELD_MAPPING_LTU1, object);
        fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, field);
        item.click(fieldMapping);
        selectValueInDropDown(mapField);
    }

    public void saveRule() {
        item.click(SAVE_BUTTON);
    }

    private void selectTaskOwner(String owner, int j) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + j + "]";
        Log.info("Selecting Task Owner : " + owner);
        boolean selected = false;
        for (int i = 0; i < 3; i++) {
            item.clearAndSetText(xpath + DEFAULTOWNER, owner);
            element.getElement((xpath + DEFAULTOWNER)).sendKeys(Keys.ENTER);
            Timer.sleep(3);
            for (WebElement ele : element.getAllElement("//li[@class='ui-menu-item' and @role = 'presentation']/a[contains(text(),'" + owner + "')]")) {
                if (ele.isDisplayed()) {
                    ele.click();
                    selected = true;
                    return;
                }
            }
            Timer.sleep(2);
        }
        if (!selected) {
            throw new RuntimeException("Unable to select owner");
        }
        Log.info("Selected Task Owner Successfully: " + owner);
    }


    public void createLoadToCustomers(LoadToCustomersAction loadToCustomersAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to Customers");
        List<FieldMapping> ruleActions = loadToCustomersAction.getFieldMappings();
        for (FieldMapping fieldMappingObject : ruleActions) {
            String fieldMapping = String.format(FIELD_MAPPING_LTU1, fieldMappingObject.getSourceObject());
            fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, fieldMappingObject.getSourceField());
            item.click(xpath + fieldMapping);
            String fieldMappingDestination = String.format(FIELD_MAPPING_DESTINATION, fieldMappingObject.getDestination());
            item.click(xpath + fieldMapping + fieldMappingDestination);
        }
    }


    public void createLoadToMileStone(LoadToMileStoneAction loadToMileStoneAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to Milestone");
        if (loadToMileStoneAction.getMilestoneDate().getType().contains("Show Field")) {
            item.click(xpath + SHOWFIELD_LTM);
            item.click(xpath + SHOEFIELD_SELECT_LTM);
            selectValueInDropDown(loadToMileStoneAction.getMilestoneDate().getDateField());
        } else {
            item.click(xpath + CONSTANT_SELECT_LTM);
            selectValueInDropDown(loadToMileStoneAction.getMilestoneDate().getDateField());
            element.setText(NEWRULE_PART1 + i + NEWRULE_PART2
                    + CONSTANT_SELECT_LTM_VALUE, loadToMileStoneAction.getMilestoneDate().getDateFieldValue());
        }
        item.click(xpath + MILESTONE_LTM);
        selectValueInDropDown(loadToMileStoneAction.getSelectMilestone());
        element.setText(xpath + COMMENTS, loadToMileStoneAction.getComments());
    }

    public void loadToSfdcObject(LoadToSFDCAction loadToSFDCAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to SFDC Object");
        item.click(xpath + LOAD_TO_OBJECT);
        selectValueInDropDown(loadToSFDCAction.getObjectName());
        item.click(xpath + LOAD_TO_OBJECT_OPERATION_TYPE);
        selectValueInDropDown(loadToSFDCAction.getOperation());
        List<FieldMapping> ruleActions = loadToSFDCAction.getFieldMappings();
        for (FieldMapping fieldMappingObject : ruleActions) {
            String fieldMapping = String.format(FIELD_MAPPING_LTU1, fieldMappingObject.getSourceObject());
            fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, fieldMappingObject.getSourceField());
            item.click(xpath + fieldMapping);
            String fieldMappingDestination = String.format(FIELD_MAPPING_DESTINATION, fieldMappingObject.getDestination());
            item.click(xpath + fieldMapping + fieldMappingDestination);
        }
    }

    public void loadToMdaCollection(LoadToMDAAction loadToMDAAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to MDA Subject Area");
        item.click(xpath + LOAD_TO_OBJECT);
        selectValueInDropDown(loadToMDAAction.getObjectName());
        item.click(xpath + LOAD_TO_OBJECT_OPERATION_TYPE);
        selectValueInDropDown(loadToMDAAction.getOperation());
        List<FieldMapping> ruleActions = loadToMDAAction.getFieldMappings();
        for (FieldMapping fieldMappingObject : ruleActions) {
            String fieldMapping = String.format(FIELD_MAPPING_LTU1, fieldMappingObject.getSourceObject());
            fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, fieldMappingObject.getSourceField());
            item.click(xpath + fieldMapping);
            String fieldMappingDestination = String.format(FIELD_MAPPING_DESTINATION, fieldMappingObject.getDestination());
            item.click(xpath + fieldMapping + fieldMappingDestination);
        }
    }


    public void createSendEmail(SendEmailAction sendEmailAction) {
        //TODO: To be implemented
        throw new RuntimeException("Method not yet implemented.");
    }

    public void createLoadToUsage(LoadToUsageAction loadToUsageAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Load to Usage");
        List<FieldMapping> ruleActions = loadToUsageAction.getFieldMappings();
        for (FieldMapping fieldMappingObject : ruleActions) {
            String fieldMapping = String.format(FIELD_MAPPING_LTU1, fieldMappingObject.getSourceObject());
            fieldMapping = fieldMapping + String.format(FIELD_MAPPING_LTU2, fieldMappingObject.getSourceField());
            item.click(xpath + fieldMapping);
            String fieldMappingDestination = String.format(FIELD_MAPPING_DESTINATION, fieldMappingObject.getDestination());
            item.click(xpath + fieldMapping + fieldMappingDestination);
        }
    }

    public void createSetScore(SetScoreAction setScoreAction, int i) {
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
        clickOnActionButton();
        item.click(xpath + SELECT_BUTTON);
        selectValueInDropDown("Set Score");
        item.click(xpath + SCORE_MEASURE);
        selectValueInDropDown(setScoreAction.getSelectMeasure());
        item.clearAndSetText(xpath + SCORECARD_COMMENTS, setScoreAction.getComments());
        item.click(xpath + "//div[contains(@class, 'overallscore')]/descendant::select/following-sibling::button");
        selectValueInDropDown(setScoreAction.getSetScoreFrom());
    }

    public void addCriteria(Criteria criteria, int r, int c) {
    	String fieldName=criteria.getShowField();
    	if (criteria.getShowField().startsWith("lookup_")) {
    		fieldName=criteria.getShowField().substring(7);
		}
        String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + r + "]//div[@class='criteria-container'][" + c + "]";
        String criteriaButton = "//div[contains(@class,'setup-action-ctn')]/div[" + r + "]";
        item.click(criteriaButton + CRITERIA);
        item.click(xpath + CRITERIA_SHOWFIELD);
        selectValueInDropDown(fieldName);
        item.click(xpath + CRITERIA_SHOWFIELD_OPERATOR);
        selectValueInDropDown(criteria.getOperator());
        if (criteria.getField().equalsIgnoreCase("field")) {
            item.click(xpath + CRITERIA_SHOWFIELD_FORWIDTH);
            selectValueInDropDown("field");
            item.click(xpath + CRITERIA_SHOWFIELD_FORWIDTH_VALUE);
            selectValueInDropDown(criteria.getValue());
        } else {
            // item.click(xpath + CRITERIA_SHOWFIELD_FORWIDTH);
            // selectValueInDropDown("value");
            if (criteria.getValue().startsWith("input_")) {
                item.clearAndSetText(xpath + CRITERIA_SHOWFIELD_INPUT, criteria.getValue().substring(6));
            } else {
                item.click(xpath + CRITERIA_SHOWFIELD_INPUT_DROPDOWN);
                selectValueInDropDown(criteria.getValue(), true);
            }
        }
    }

    public String queryString(List<Criteria> criterias) {
        String str = "";
        String showField = "";
        String operator = "";
        String value = "";
        for (Criteria criteria : criterias) {
            if (!criteria.getField().contains("field")) {
                showField = criteria.getShowField()
                        .substring((criteria.getShowField().indexOf(":")) + 2).replaceAll(" ", "_");
                showField = showField + "__c";
                operator = getOperator(criteria.getOperator());
                value = getValue(criteria.getField(), criteria.getValue());
                str = str + showField + "   " + operator + "  " + value + " AND  ";
            }
        }
        str = "SELECT Id, Name   FROM " + " Account " + " Where " + str
                + " isDeleted=false";
        return str;
    }

    public String getOperator(String operator) {
        String symbol = "";
        if (operator.contains("equals")) {
            symbol = "=";
        } else if (operator.contains("less or equal")) {
            symbol = "<=";
        } else if (operator.contains("greater or equal")) {
            symbol = ">=";
        }
        return symbol;
    }

    public String getValue(String field, String value) {
        String symbol = "";
        if (value.contains("Date")) {
            Date date = new Date();
            symbol = DateUtil.getFormattedDate(date, "yyyy-MM-dd");
        } else if (field.contains("field")) {
            symbol = value.substring((value.indexOf(":")) + 2);
        } else if (value.contains("input")) {
            symbol = value.substring(6);
        } else {
            symbol = "'" + value + "'";
        }
        return symbol;
    }
    
    public void closeCTA(CloseCtaAction closeCta, int i) {
    	String xpath = "//div[contains(@class,'setup-action-ctn')]/div[" + i + "]";
		item.click(xpath + CLOSE_CTA_RADIO_BUTTON);
		item.click(xpath + TYPE_CCTA);
		selectValueInDropDown(closeCta.getType(), true);
		item.click(xpath + REASON_CCTA);
		selectValueInDropDown(closeCta.getReason(), true);
		item.click(xpath + SOURCE_CCTA);	
		if (closeCta.getSource()!=null) {
			List<String> sources=Arrays.asList(closeCta.getSource().split(","));
			Log.info("Number of sources selected is " +sources.size());
			for (String source : sources) {
				item.click(String.format(CLOSE_CTA_SETSTATUS, source, source));
			}		
		}
		item.click(xpath + CTASTATUS_CCTA);
		selectValueInDropDown(closeCta.getSetCtaStatusTo(), true);
		element.clearAndSetText(xpath + CLOSECTA_COMMENTS, closeCta.getComments());
		wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
    }
    
    public void tokenComments(String tokens){
    	List<String> tokenList=Arrays.asList(tokens.split(","));
        for (String token : tokenList) {
			if (token.startsWith("@")) {
				element.setText(COMMENTS, token);
				item.click(String.format(COMMENTS_TOKENS_DIV, token.substring(token.indexOf("@")+1)));
			}
		}
    	
    }
}
