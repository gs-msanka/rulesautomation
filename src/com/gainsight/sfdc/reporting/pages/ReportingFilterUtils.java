package com.gainsight.sfdc.reporting.pages;

import java.util.List;

import javax.annotation.processing.Filer;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;

public class ReportingFilterUtils extends BasePage {

	public enum filterOPerators {
		INCLUDES, EXCLUDES, EQUALS, NOT_EQUALS, CONTAINS, DOESNOTCONTAINS, LESS_THAN, GREATER_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL, STARTSWITH, ENDSWITH
	};

	private final String FILTER_BTN_XPATH = "//div[@class='pull-left gs-rb-filterBlue-icon']";
	private final String FILTER_NAME_SELECT_XPATH = "//button[@type='button' and contains(@class,'gs-filter-lhs')]";
	private final String FILTER_OPERATOR_SELECT_XPATH = "//button[@type='button' and contains(@class,'gs-filter-operator')]";
	private final String FILTER_VALUE_XPATH = "//div[@class='gs-condition-rhs pull-left']";

	private final String SEARCH_FILTER_NAME_XPATH = "//div[contains(@class,'gs-filter-lhs')]//div[@class='ui-multiselect-filter']/input[@placeholder='Search'and @type='text']";
	private final String SEARCH_FILTER_NAME_XPATH_MDA = "//div[contains(@class,'gs-filter-lhs')]//div[@class='customTreeContainer']//input[@placeholder='Search...'and @type='text']";

	private final String CLICK_FILTER_NAME_XPATH = "//input[@type='radio']/following-sibling::span[normalize-space(text())='%s']";
	private final String CLICK_FILTER_NAME_XPATH_MDA = "//div[contains(@class,'ui-multiselect-hasfilter')]/div[@class='customTreeContainer']//li/a/span[text()='%s']";
	private final String CLICK_FILTER_NAME_XPATH_MDA_AGGREGATED = "//div[contains(@class,'ui-multiselect-hasfilter')]/div[@class='customTreeContainer']//li/a[text()='%s']";
	private final String CLICK_FILTER_OPERATOR_XPATH = "//input[@type='radio']/following-sibling::span[normalize-space(text())='%s']";

	private final String FILTER_APPLY_BTN_XPATH = "//div[contains(@class, 'gs-rb-filter-popup')]/div[@class='gs-rb-applycancel']/div[contains(@class,'btn-apply')]";
	private final String FILTER_CANCEL_BTN_XPATH = "//div[contains(@class, 'gs-rb-filter-popup')]/div[@class='gs-rb-applycancel']/div[contains(@class,'btn-cancel')]";

	private final String FILTER_APPLY_BTN_POPUP = "//span[contains(text(),'Please wait while we crunch your data')]";

	private final String FILTER_VALUE_DATE_XPATH = "//div[@class='conditionViewWrapper']//span[contains(text(),'--None--')]";
	private final String FILTER_AVALUE_DATE_XPATH = "//div[contains(@class,'timeframeSelectDropdown ')]//input[@placeholder='Search' and @type='text']";
	private final String FILTER_AVALUE_DATE_FIT_XPATH = "//li/label/input[@value='%s']/following-sibling::span[contains(text(),'%s')]";
	private final String FILTER_AVALUE_DATE_INPUT_XPATH = "//input[@placeholder='Date']";

	private final String FILTER_UNLOCKED_BTN_XPATH = "//span[@class='gs-rb-field-name require-tooltip' and contains(@title,'Display Name :: %s')]/following-sibling::span[@class='gs-rb-unlock-icon' and contains(@data-id,'%s')]";
	private final String FILTER_LOCKED_BTN_XPATH = "//span[@class='gs-rb-field-name require-tooltip' and contains(@title,'Display Name :: %s')]/following-sibling::span[@class='gs-rb-lock-icon' and contains(@data-id,'%s')]";

	/**
	 * 
	 * This will add filter for sfdc data.
	 * 
	 * @param filterName
	 * @param filterOPerator
	 * @param filterValues
	 * @param filterType
	 */

	public boolean addFilter(String filterName, String filterOPerator, List<Object> filterValues, String filterType) {
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		boolean isSuccess = false;
		isSuccess = selectFilterName(filterName);
		if (isSuccess) {
			isSuccess = selectFilterOperator(filterOPerator);
			if (isSuccess) {
				isSuccess = selectFilterValues(filterOPerator, filterValues);
				if (isSuccess) {
					// Click Apply button
					item.click(FILTER_APPLY_BTN_XPATH);
					wait.waitTillElementNotDisplayed(FILTER_APPLY_BTN_POPUP, 0, 5);
					// Timer.sleep(5);
					if ((filterType).toUpperCase() == "LOCKED") {
						item.click(String.format(FILTER_LOCKED_BTN_XPATH, filterName, filterName));
					}
				} else {
					// Click Cancel button
					item.click(FILTER_CANCEL_BTN_XPATH);
				}
			}
		}
		return isSuccess;
	}

	/**
	 * 
	 * This will add filter for MDA data objects.
	 * 
	 * @param filterName
	 * @param filterOPerator
	 * @param filterValues
	 * @param filterType
	 * @throws InterruptedException
	 */

	public boolean addFilterMDA(String aggFilterName, String filterDataType, String filterName, String filterOPerator,
			List<Object> filterValues, String filterType) {

		filterOPerator = modifyFilterOperator(filterOPerator);
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		boolean isSuccess = false;
		isSuccess = selectFilterNameMda(filterName, aggFilterName);
		if (isSuccess) {
			isSuccess = selectFilterOperator(filterOPerator);
			if (isSuccess) {
				if (filterDataType.contains("date")) {
					isSuccess = selectFilterValuesForDate(filterOPerator, filterValues);
				} else {
					isSuccess = selectFilterValues(filterOPerator, filterValues);
				}

				if (isSuccess) {
					// Click Apply button
					item.click(FILTER_APPLY_BTN_XPATH);
					wait.waitTillElementNotDisplayed(FILTER_APPLY_BTN_POPUP, 1, 5);
					if ((filterType).toUpperCase() == "LOCKED") {
						item.click(String.format(FILTER_LOCKED_BTN_XPATH, filterName, filterName));
					}
				} else {
					// Click Cancel button
					item.click(FILTER_CANCEL_BTN_XPATH);
				}
			}
		}
		return isSuccess;
	}

	/**
	 * 
	 * @param filterName
	 * @param filterOPerator
	 * @param filterValues
	 * @param filterType
	 * @throws InterruptedException
	 */

	public boolean addAggregateFilter(String filterName, String filterOPerator, List<Object> filterValues,
			String filterType) throws InterruptedException {
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		boolean isSuccess = false;
		isSuccess = selectFilterNameMda(filterName, null);
		if (isSuccess) {
			isSuccess = selectFilterOperator(filterOPerator);
			if (isSuccess) {
				isSuccess = selectFilterValues(filterOPerator, filterValues);
				if (isSuccess) {
					// Click Apply button
					item.click(FILTER_APPLY_BTN_XPATH);
					wait.wait(5);
					if ((filterType).toUpperCase() == "LOCKED") {
						item.click(String.format(FILTER_LOCKED_BTN_XPATH, filterName, filterName));
					}
				} else {
					// Click Cancel button
					item.click(FILTER_CANCEL_BTN_XPATH);
				}
			}
		}
		return isSuccess;
	}

	private boolean selectFilterName(String filterName) {
		try {
			// Click filter name
			item.click(FILTER_NAME_SELECT_XPATH);
			item.setText(SEARCH_FILTER_NAME_XPATH, filterName);
			item.click(String.format(CLICK_FILTER_NAME_XPATH, filterName));
			return true;
		} catch (Exception e) {
			return false;

		}
	}

	private boolean selectFilterNameMda(String filterName, String aggFilterName) {
		try {
			// Click filter name
			item.click(FILTER_NAME_SELECT_XPATH);
			item.setText(SEARCH_FILTER_NAME_XPATH_MDA, filterName);
			if (aggFilterName != null) {
				filterName = aggFilterName;
				item.click(String.format(CLICK_FILTER_NAME_XPATH_MDA_AGGREGATED, filterName));
			} else {
				item.click(String.format(CLICK_FILTER_NAME_XPATH_MDA, filterName));
			}

			return true;
		} catch (Exception e) {
			return false;

		}
	}

	private boolean selectFilterOperator(String filterOperator) {
		try {
			// Click filter operator
			item.click(FILTER_OPERATOR_SELECT_XPATH);
			item.click(String.format(CLICK_FILTER_OPERATOR_XPATH, filterOperator.toLowerCase().replace("_", " ")));
			return true;
		} catch (Exception e) {
			return false;

		}
	}

	private boolean selectFilterValues(String filterOperator, List<Object> filterValues) {
		try {
			// Click filter values
			if (filterOperator == filterOPerators.INCLUDES.name()
					|| filterOperator == filterOPerators.EXCLUDES.name()) {
				for (int i = 0; i < filterValues.size(); i++) {
					item.selectCheckBox(
							String.format("//input[@type='checkbox' and @title='%s']", filterValues.get(i)));
				}
			} else {
				item.setText(FILTER_VALUE_XPATH + "/div/input", filterValues.get(0).toString());
			}
			return true;
		} catch (Exception e) {
			return false;

		}
	}

	private boolean selectFilterValuesForDate(String filterOperator, List<Object> filterValues) {
		try {
			item.click(FILTER_VALUE_DATE_XPATH);
			if (filterOperator.contains("equals")) {

				item.setText(FILTER_AVALUE_DATE_XPATH, filterValues.get(0).toString());
				item.click(String.format(FILTER_AVALUE_DATE_FIT_XPATH, filterValues.get(0).toString().toUpperCase(),
						filterValues.get(0).toString()));

			} else {
				item.click(String.format(FILTER_AVALUE_DATE_FIT_XPATH, filterValues.get(0).toString().toUpperCase(),
						filterValues.get(0).toString()));
			}

			if (filterValues.get(0).toString().contains("Custom")) {
				item.setText(FILTER_AVALUE_DATE_INPUT_XPATH, "");
			}

			return true;
		} catch (Exception e) {
			return false;

		}
	}

	private String modifyFilterOperator(String filterOperator) {

		if (filterOperator.contains("EQ")) {
			filterOperator = "equals";
			return filterOperator;
		} else if (filterOperator.contains("GT")) {
			filterOperator = "greater than";
			return filterOperator;
		} else if (filterOperator.contains("STARTSWITH")) {
			filterOperator = "starts with";
			return filterOperator;
		} else

			return filterOperator;
	}

}
