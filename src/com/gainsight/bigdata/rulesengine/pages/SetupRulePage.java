package com.gainsight.bigdata.rulesengine.pages;

import org.testng.Assert;

import com.gainsight.bigdata.rulesengine.pojo.setuprule.CalculatedField;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 * Created by vmenon on 9/3/2015.
 */
public class SetupRulePage extends BasePage {
    private final String READY_INDICATOR = "//div[@class='RuleContainer']";
    private final String DATA_SOURCE_FORCE = "//input[@value='force']";
    private final String DATA_SOURCE_NEWSTACK = "//input[@value='new-stack']";
    private final String SOURCE_OBJECT_DROPDOWN_LOC = "//div[contains(@class,'obj-list')]/descendant::button";
    private final String SOURCE_OBJECT_LIST_SEARCH = "//div[contains(@class,'long_obj_list')]//input[@placeholder='Search']";
    private final String SOURCE_OBJECT_DRPDWN_ITEM_LOCATOR = "//li[@class='ui-multiselect-option']/label/input[@value='%s']";
    private final String SOURCE_FIELD_SEARCH = "//div[contains(@class, 'ui-multiselect-single')]/descendant::input[@placeholder='Search']";
    private final String FIELD_LIST_DRAG_ITEM = "//li[@class='list-group-item' and @data-parent='%s']/span[contains(@class,'field-name') and text()='%s']/../span[contains(@class,'pull-right')]";
    private final String ADVANCE_LOGIC_INPUT = "//div[@class='advanced-logic']/input";
    private final String NEXT_BUTTON = "//span[contains(@class, 'visual-query-build') and text()='Next']";
    private final String PRIVIEW_RESULTS_BUTTON = "//span[contains(@class, 'visual-show-results') and text()='Preview Results']";
    private final String LOADING_ICON = "//div[contains(@class, 'gs-loader-image')]";
    private final String CALCULATED_FILED_LINKTEXT = "Add Calculated Field";
    private final String OK_BUTTON_IN_CALCULATED_FIELD_DIV = "//div[@id='add_calculated_popup']/descendant::span[text()='Ok']";
    private final String FIELDS_IN_OBJECT = "//li[contains(@class, 'ui-multiselect-option')]/descendant::span[text()='%s']";

    private final String DRAG_INPUT1 = "//h2[contains(text(),'%s') and @class='submenu']/following-sibling::ul";
    private final String DRAG_INPUT2 = "//span[text()='%s']";
    private final String SHOW_FIELDS_DIV = "//div[contains(@class,'show-droppable')]";
    private final String ACTION_FIELDS_DIV = "//div[contains(@class,'filter-droppable')]";
    
    private final String ACTION_FILTER_OPERATOR = "//div[@class='advance-logic-place-holder']/following-sibling::div//span[contains(text(),'%s')]/../../../following-sibling::div"
    	    + "//select[@class='operator']/following-sibling::button";
    private final String ACTION_FILTER_SVALUE = "//div[@class='advance-logic-place-holder']/following-sibling::div//span[contains(text(),'%s')]/../../../following-sibling::div"
    	    + "//select[contains(@class,'for-width')]/following-sibling::button";
    private final String ACTION_FILTER_IVALUE = "//div[@class='advance-logic-place-holder']/following-sibling::div//span[contains(text(),'%s')]/../../../following-sibling::div"
    	    + "//input[contains(@class,'value-text')]";
    private final String GSCUSTOMERS_CHECKBOX = "//div[contains(text(),'Apply to Gainsight customers')]/preceding-sibling::input";
    
    
    private final String ADD_CALCULATED_FIELD_LINKTEXT = "Add Calculated Field";
    private final String ADD_CALCULATED_FIELD_DIVISION ="//div[@aria-describedby='add_calculated_popup']";
    private final String CALULATED_FIELD_NAME_INPUT = "//input[contains(@class, 'cal_field_name')]";
    private final String CALCULATION_TYPE_DROPDOWN = "//select[contains(@class, 'calc_select calc_type_select')]/following-sibling::button";
    private final String CALCULATION_DIFFERENCE_TYPE = "//label[text()='%s']";
    private final String TIMEIDENTIFIER = "//select[contains(@class, 'timeidentifier')]/following-sibling::button";
    private final String CALCULATED_FIELDS_SHOW_FIELD_RADIO_BUTTON_A_SECTION = "//label[@class='radio-inline']/descendant::input[@value='showfield' and @data-section='A']";
    private final String CALCULATED_FIELDS_AGGREGATION_RADIO_BUTTON_A_SECTION = "//label[@class='radio-inline']/descendant::input[@value='timebased' and @data-section='A']";
    private final String CALCULATED_FIELD_AGGREGATION_LIST_DROPDOWN_A_SECTION = "//select[contains(@class, 'calc_select lhs_aggList')]/following-sibling::button";
    private final String CALCULATED_FIELD_SOURCEFIELD_LIST_DROPDOWN_A_SECTION = "//select[contains(@class, 'calc_select lhs_sourceFldsList')]/following-sibling::button";
    private final String CALCULATED_FIELD_PERIODTYPE_LIST_DROPDOWN_A_SECTION = "//select[contains(@class, 'calc_select periodTypes_select_A')]/following-sibling::button";
    private final String CALCULATED_FIELD_NO_OF_PERIODS_A_SECTION = "//input[contains(@class, 'noofPeriods_A')]";
    private final String CALCULATED_FIELD_GRANULARITY_A_SECTION = "//select[contains(@class, 'calc_select granularity_select_A')]/following-sibling::button";
    private final String CALCULATED_FILED_ADJUST_MISSINGDATA_A_SECTION = "//div[contains(@class, 'lhs_calcConditionRow_timebased')]/descendant::input[contains(@class, 'gs-isAdjustForMissingData')]";
    private final String CALCULATED_FILED_SELECT_A_SECTION = "//select[contains(@class, 'calc_select lhs_selfieldList')]/following-sibling::button";
    
    private final String CALCULATED_FIELDS_AGGREGATION_RADIO_BUTTON_B_SECTION = "//label[@class='radio-inline']/descendant::input[@value='timebased' and @data-section='B']";
    private final String CALCULATED_FIELD_AGGREGATION_LIST_DROPDOWN_B_SECTION = "//select[contains(@class, 'calc_select rhs_aggList')]/following-sibling::button";
    private final String CALCULATED_FIELD_SOURCEFIELD_LIST_DROPDOWN_B_SECTION = "//select[contains(@class, 'calc_select rhs_sourceFldsList')]/following-sibling::button";
    private final String CALCULATED_FIELD_PERIODTYPE_LIST_DROPDOWN_B_SECTION = "//select[contains(@class, 'calc_select periodTypes_select_B')]/following-sibling::button";
    private final String CALCULATED_FIELD_NO_OF_PERIODS_B_SECTION = "//input[contains(@class, 'noofPeriods_B')]";
    private final String CALCULATED_FIELD_GRANULARITY_B_SECTION = "//select[contains(@class, 'calc_select granularity_select_B')]/following-sibling::button";
    private final String CALCULATED_FILED_ADJUST_MISSINGDATA_B_SECTION = "//div[contains(@class, 'rhs_calcConditionRow_timebased')]/descendant::input[contains(@class, 'gs-isAdjustForMissingData')]";
    private final String CALCULATED_FILED_SELECT_B_SECTION = "//select[contains(@class, 'calc_select rhs_selfieldList')]/following-sibling::button";
    private final String CALCULATED_FIELDS_SHOW_FIELD_RADIO_BUTTON_B_SECTION = "//label[@class='radio-inline']/descendant::input[@value='showfield' and @data-section='B']";
    private final String CALCULATED_FIELDS_SAVE_BUTTON = "//span[contains(@class, 'btn-save') and text()='Ok']";
    

    public SetupRulePage() {
        Log.info("Dummy Constructor");
    }

    public void dropDownOFSourceObjectSelection() {
        item.click(SOURCE_OBJECT_DROPDOWN_LOC);
    }

    public void waitForPageLoad() {
        Log.info("Waiting for the page to load");
        wait.waitTillElementNotDisplayed(LOADING_ICON, MIN_TIME, MAX_TIME);
    }

    public void selectSourceObjectFromNativeData() {
        item.click(DATA_SOURCE_FORCE);
    }

    public void selectSourceObjectFromMatrixData() {
        item.click(DATA_SOURCE_NEWSTACK);
        wait.waitTillElementNotDisplayed("//label[contains(@class, 'loading-spin')]", MIN_TIME, MAX_TIME);
    }

    public void enterAdvanceLogic(String advanceLogic) {
        field.clearAndSetText(ADVANCE_LOGIC_INPUT, advanceLogic);
    }

    public SetupRuleActionPage clickOnNext() {
        item.click(NEXT_BUTTON);
        waitForPageLoad();
        return new SetupRuleActionPage();
    }

    public void clickToPreviewResults() {
        item.click(PRIVIEW_RESULTS_BUTTON);
        waitForPageLoad();
    }

    public void addCalculatedField() {
        link.clickLink(CALCULATED_FILED_LINKTEXT);
        wait.waitTillElementDisplayed("//div[contains(@class, 'ui-widget-content')]", MIN_TIME, MAX_TIME);
    }

    public void clickOnOKButton() {
        item.click(OK_BUTTON_IN_CALCULATED_FIELD_DIV);

    }
    
    public void selectSourceObject(String sourceObject) {
		dropDownOFSourceObjectSelection();
		field.clearAndSetText(SOURCE_FIELD_SEARCH, sourceObject);
		String fieldNameXpath = String.format(FIELDS_IN_OBJECT, sourceObject);
		item.click(fieldNameXpath);
		return;
	}
    
	public void selectDataSource(String dataSource) {
		if (dataSource.equalsIgnoreCase("Native")) {
			selectSourceObjectFromNativeData();
		} else {
			selectSourceObjectFromMatrixData();
		}
	}
	
	public SetupRulePage selectTimeIdentifier(String timeIdentifier){
		item.click(TIMEIDENTIFIER);
		selectValueInDropDown(timeIdentifier, true);
		return this;
	} 

    public void dragAndDropFieldsToShowArea(String object, String field) {
        String sourceXpath = String.format(DRAG_INPUT1, object);
        sourceXpath = sourceXpath + String.format(DRAG_INPUT2, field);
        element.dragAndDrop(sourceXpath, SHOW_FIELDS_DIV);

    }
    
	public void unCheckApplyToGSCustomers() {
		if (element.getElement(GSCUSTOMERS_CHECKBOX).getAttribute("checked").contains("true")) {
			item.click(GSCUSTOMERS_CHECKBOX);
		}
	}


    public void dragAndDropFieldsToActionsForNativeData(String object, String fields, String operator, String value) {
        String sourceXpath = String.format(DRAG_INPUT1, object);
        sourceXpath = sourceXpath + String.format(DRAG_INPUT2, fields);
        element.dragAndDrop(sourceXpath, ACTION_FIELDS_DIV);     
		String filterOperator = String.format(ACTION_FILTER_OPERATOR, object+ "::" + fields);
		item.click(filterOperator);
		selectValueInDropDown(operator);
		if (value.startsWith("select_")) {
			value = value.substring(7);
			String filterSValue = String.format(ACTION_FILTER_SVALUE, object+ "::" + fields);
			item.click(filterSValue);
			selectValueInDropDown(value, true);
		} else if (value.startsWith("input_")) {
			value = value.substring(6);
			String filterIValue = String.format(ACTION_FILTER_IVALUE, object+ "::" + fields);
			field.clearAndSetText(filterIValue, value);
		}
	}
    
    public void dragAndDropFieldsToShowAreaForMatrixData(String field) {
        String sourceXpath = "//div[contains(@class, 'gs-rb-schema-tree-wrapper')]/descendant::a/descendant::span[text()='"+field+"']";
        element.dragAndDrop(sourceXpath, SHOW_FIELDS_DIV);

    }
    
    public void dragAndDropFieldsToActionsForMatrixData(String object, String fields, String operator, String value) {
    	String sourceXpath = "//div[contains(@class, 'gs-rb-schema-tree-wrapper')]/descendant::a/descendant::span[text()='"+fields+"']";
		element.dragAndDrop(sourceXpath, ACTION_FIELDS_DIV);
		String filterOperator = String.format(ACTION_FILTER_OPERATOR, fields);
		item.click(filterOperator);
		selectValueInDropDown(operator);
		if (value.startsWith("select_")) {
			value = value.substring(7);
			String filterSValue = String.format(ACTION_FILTER_SVALUE, fields);
			item.click(filterSValue);
			selectValueInDropDown(value, true);
		} else if (value.startsWith("input_")) {
			value = value.substring(6);
			String filterIValue = String.format(ACTION_FILTER_IVALUE, fields);
			field.clearAndSetText(filterIValue, value);
		}
	}
    
	public SetupRulePage clickOnCalculatedField() {
		link.click(ADD_CALCULATED_FIELD_LINKTEXT);
		Log.debug("Waiting till the caulcated Div appears on UI");
		wait.waitTillElementDisplayed(ADD_CALCULATED_FIELD_DIVISION, MIN_TIME, MAX_TIME);
		return this;
	}
	
	public void fillCalculatedFields(CalculatedField calculatedField){
		clickOnCalculatedField();
		element.clearAndSetText(CALULATED_FIELD_NAME_INPUT,calculatedField.getFieldName());
		item.click(CALCULATION_TYPE_DROPDOWN);
		selectValueInDropDown(calculatedField.getCalculationType().getType());
		if (calculatedField.getCalculationType().name().equalsIgnoreCase("COMPARISON")) {
			item.click(String.format(CALCULATION_DIFFERENCE_TYPE,calculatedField.getCalculateDifferenceType()));
			if (calculatedField.getFieldAConfig().getCalculatedFieldType().name().equals("AGGREGATION")) {
				item.click(CALCULATED_FIELDS_AGGREGATION_RADIO_BUTTON_A_SECTION);
				item.click(CALCULATED_FIELD_AGGREGATION_LIST_DROPDOWN_A_SECTION);
				selectValueInDropDown(calculatedField.getFieldAConfig().getAggregation().getAggregationCalculation(), true);
				item.click(CALCULATED_FIELD_SOURCEFIELD_LIST_DROPDOWN_A_SECTION);
				selectValueInDropDown(calculatedField.getFieldAConfig().getAggregation().getSourceField(), true);
				item.click(CALCULATED_FIELD_PERIODTYPE_LIST_DROPDOWN_A_SECTION);
				selectValueInDropDown(calculatedField.getFieldAConfig().getAggregation().getPeriodType(), true);
				element.clearAndSetText(CALCULATED_FIELD_NO_OF_PERIODS_A_SECTION,calculatedField.getFieldAConfig().getAggregation().getNoOfPeriods());
				item.click(CALCULATED_FIELD_GRANULARITY_A_SECTION);
				selectValueInDropDown(calculatedField.getFieldAConfig().getAggregation().getGranularity(), true);
				if (calculatedField.getFieldAConfig().getAggregation().isAdjustForMissingData()) {
					item.click(CALCULATED_FILED_ADJUST_MISSINGDATA_A_SECTION);
				} else {
					Log.info("Boolean is not checked - Not Adjusting For Missing data");
				}
			} else {
				item.click(CALCULATED_FIELDS_SHOW_FIELD_RADIO_BUTTON_A_SECTION);
				item.click(CALCULATED_FILED_SELECT_A_SECTION);
				selectValueInDropDown(calculatedField.getFieldAConfig().getShowField(), true);
			}
			if (calculatedField.getFieldBConfig().getCalculatedFieldType().name().equals("AGGREGATION")) {
				item.click(CALCULATED_FIELDS_AGGREGATION_RADIO_BUTTON_B_SECTION);
				item.click(CALCULATED_FIELD_AGGREGATION_LIST_DROPDOWN_B_SECTION);
				selectValueInDropDown(calculatedField.getFieldBConfig().getAggregation().getAggregationCalculation(), true);
				item.click(CALCULATED_FIELD_SOURCEFIELD_LIST_DROPDOWN_B_SECTION);
				selectValueInDropDown(calculatedField.getFieldBConfig().getAggregation().getSourceField(), true);
				item.click(CALCULATED_FIELD_PERIODTYPE_LIST_DROPDOWN_B_SECTION);
				selectValueInDropDown(calculatedField.getFieldBConfig().getAggregation().getPeriodType(), true);
				element.clearAndSetText(CALCULATED_FIELD_NO_OF_PERIODS_B_SECTION,calculatedField.getFieldBConfig().getAggregation().getNoOfPeriods());
				item.click(CALCULATED_FIELD_GRANULARITY_B_SECTION);
				selectValueInDropDown(calculatedField.getFieldBConfig().getAggregation().getGranularity(), true);
				if (calculatedField.getFieldAConfig().getAggregation().isAdjustForMissingData()) {
					item.click(CALCULATED_FILED_ADJUST_MISSINGDATA_B_SECTION);
				} else {
					Log.info("Boolean is not checked - Not Adjusting For Missing data");
				}
			} else {
				item.click(CALCULATED_FIELDS_SHOW_FIELD_RADIO_BUTTON_B_SECTION);
				item.click(CALCULATED_FILED_SELECT_B_SECTION);
				selectValueInDropDown(calculatedField.getFieldBConfig().getShowField(), true);
			}
		} else {
			item.click(CALCULATED_FIELD_AGGREGATION_LIST_DROPDOWN_A_SECTION);
			selectValueInDropDown(calculatedField.getAggregationConfig().getAggregationCalculation(), true);
			item.click(CALCULATED_FIELD_SOURCEFIELD_LIST_DROPDOWN_A_SECTION);
			selectValueInDropDown(calculatedField.getAggregationConfig().getSourceField(), true);
			item.click(CALCULATED_FIELD_PERIODTYPE_LIST_DROPDOWN_A_SECTION);
			selectValueInDropDown(calculatedField.getAggregationConfig().getPeriodType(), true);
			element.clearAndSetText(CALCULATED_FIELD_NO_OF_PERIODS_A_SECTION,calculatedField.getAggregationConfig().getNoOfPeriods());
			item.click(CALCULATED_FIELD_GRANULARITY_A_SECTION);
			selectValueInDropDown(calculatedField.getAggregationConfig().getGranularity(), true);
			if (calculatedField.getAggregationConfig().isAdjustForMissingData()) {
				item.click(CALCULATED_FILED_ADJUST_MISSINGDATA_A_SECTION);
			} else {
				Log.info("Boolean is not checked - Not Adjusting For Missing data");
			}
		}
		item.click(CALCULATED_FIELDS_SAVE_BUTTON);
		Assert.assertTrue(element
				.getElement("//span[contains(@class, 'visual-send-results') and text()='Email Results']").isDisplayed());
	}
}