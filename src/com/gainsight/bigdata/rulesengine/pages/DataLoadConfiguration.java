package com.gainsight.bigdata.rulesengine.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects.DataLoadObject;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects.Field;
import com.gainsight.sfdc.pages.BasePage;

public class DataLoadConfiguration extends BasePage {

	private final String DATA_SOURCE_FORCE = "//input[@value='force']";
	private final String DATA_SOURCE_NEWSTACK = "//input[@value='new-stack']";
	private final String SOURCE_OBJECT_DROPDOWN_LOC = "//div[contains(@class,'obj-list')]/descendant::button";
	private final String SOURCE_FIELD_SEARCH = "//div[contains(@class, 'ui-multiselect-single')]/descendant::input[@placeholder='Search']";
	private final String FIELDS_IN_OBJECT = "//li[contains(@class, 'ui-multiselect-option')]/descendant::span[text()='%s']";
	private final String OBJECT_IN_NATIVE = "//a[contains(@class,'gs-btn-flat js-add pull-left')]/../button";
	private final String OBJECT_IN_NATIVE_SYMBOL = "//a[contains(@class,'gs-btn-flat js-add pull-left')]/i";
	private final String ACTUAL_OBJECT = "//ul[contains(@class, 'trgt-obj-list')]//a[@data-obj-label='%s']";
	private final String MOVE_RIGHT = "//a[contains(@class,'moveright')]";
	private final String MOVE_LEFT = "//a[contains(@class,'moveleft')]";
	private final String SAVE = "//a[contains(text(),'Save')]";
	private final String RULES_CONFIGUREPAGE_DIV = "//div[contains(@class, 'configure_container')]";
	private final String SELECTED_FILEDS_LIST = "//select[contains(@class,'selectedFields fieldSelect')]";

	public DataLoadConfiguration() {
		wait.waitTillElementDisplayed(RULES_CONFIGUREPAGE_DIV, MIN_TIME, MAX_TIME);
	}

    /**
     * Selects DataSource such as Native or Matrix
     * @param dataSource
     */
	public void selectDataSource(String dataSource) {
		if (dataSource.equalsIgnoreCase("Native")) {
			selectSourceObjectFromNativeData();
		} else {
			selectSourceObjectFromMatrixData();
		}
	}

    /**
     * Clicks on Native object Dropdown
     */
	public void selectSourceObjectFromNativeData() {
		item.click(DATA_SOURCE_FORCE);
	}

    /**
     * Clicks on Matrix object Dropdown
     */
	public void selectSourceObjectFromMatrixData() {
		item.click(DATA_SOURCE_NEWSTACK);
		wait.waitTillElementNotDisplayed(
				"//label[contains(@class, 'loading-spin')]", MIN_TIME, MAX_TIME);
	}

    /**
     * Selects exact objects which needs to ass permission for dataload Confoguration
     */
	public void selectSourceObject(String sourceObject) {
		clickOnNativeObjectSelection();
		field.clearAndSetText(SOURCE_FIELD_SEARCH, sourceObject);
		String fieldNameXpath = String.format(FIELDS_IN_OBJECT, sourceObject);
		item.click(fieldNameXpath);
		return;
	}

	public void dropDownOFSourceObjectSelection() {
		item.click(SOURCE_OBJECT_DROPDOWN_LOC);
	}

    /**
     * clicks on native object selection
     */
	public void clickOnNativeObjectSelection() {
		item.click(OBJECT_IN_NATIVE);
	}

	public void clickOnNativeObjectSelectionSymbol() {
		item.click(OBJECT_IN_NATIVE_SYMBOL);
	}

    /**
     * click on particular object in dataload configuration
     * @param object on which it needs to be clicked
     */
	public void clickOnParticularObject(String object) {
		String fieldNameXpath = String.format(ACTUAL_OBJECT, object);
		item.click(fieldNameXpath);
	}

    /**
     * clicks on save button in dataload configuration
     */
	public DataLoadConfiguration clickOnSaveButton() {
		item.click(SAVE);
		return this;
	}

    /**
     * Adds fields from an object in dataload configuration
     * @param object for which fields needs to be added
     */
	public void selectFieldsFromList(DataLoadObject dataLoadObject) {
		String totalFields = "";
		for (Field field : dataLoadObject.getFields()) {
			totalFields = totalFields + field.getFieldName() + ", ";
		}
		Select select = new Select(
				element.getElement("//select[contains(@class,'availableFields')]"));
		List<WebElement> allOptions = select.getOptions();
		for (WebElement webElement : allOptions) {
			if (totalFields.contains(webElement.getText())) {
				webElement.click();
			}
		}
		item.click(MOVE_RIGHT);
	}
	
	
    /**
     * Removes fields from an object in dataload configuration
     * @param object from which fields needs to be removed
     */
	public void removeFieldsFromList(DataLoadObject dataLoadObject) {
		String totalFields = "";
		for (Field field : dataLoadObject.getRemoveFields()) {
			totalFields = totalFields + field.getFieldName() + ", ";
		}
		Select select = new Select(element.getElement(SELECTED_FILEDS_LIST));
		List<WebElement> allOptions = select.getOptions();
		for (WebElement webElement : allOptions) {
			if (totalFields.contains(webElement.getText())) {
				webElement.click();
			}
		}
		item.click(MOVE_LEFT);
	}
}
