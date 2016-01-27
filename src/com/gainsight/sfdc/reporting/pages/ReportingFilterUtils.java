package com.gainsight.sfdc.reporting.pages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.processing.Filer;

import com.gainsight.bigdata.reportBuilder.pojos.ReportFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportFilterSFDC;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.reporting.enums.ConstantsOperator;
import com.gainsight.sfdc.util.DateUtil;

public class ReportingFilterUtils extends BasePage {

	public enum filterOPerators {
		INCLUDES, EXCLUDES, EQUALS, NOT_EQUALS, CONTAINS, DOESNOTCONTAINS, LESS_THAN, GREATER_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL, STARTSWITH, ENDSWITH
	};

	private final String FILTER_BTN_XPATH = "//div[@class='pull-left gs-rb-filterBlue-icon']";
	private final String FILTER_NAME_SELECT_XPATH = "//button[@type='button' and contains(@class,'gs-filter-lhs')]";
	private final String FILTER_OPERATOR_SELECT_XPATH = "//button[@type='button' and contains(@class,'gs-filter-operator')]";
	private final String FILTER_VALUE_XPATH = "//div[@class='gs-condition-rhs pull-left']";
	private final String FILTER_VALUE_NULL_XPATH = "//input[@data-control='NULL-CHECKBOX']";
	private final String FILTER_VALUE_LAST60DAYS = "//input[@value='%s']/following-sibling::span";
	private final String DATE_FILTER_SEARCH = "//div[contains(@class,'timeframeSelectDropdown')]//input[@placeholder='Search'and @type='text']";

	private final String SEARCH_FILTER_NAME_XPATH = "//div[contains(@class,'gs-filter-lhs')]//div[@class='ui-multiselect-filter']/input[@placeholder='Search'and @type='text']";
	private final String SEARCH_FILTER_NAME_XPATH_MDA = "//div[contains(@class,'gs-filter-lhs')]//div[@class='customTreeContainer']//input[@placeholder='Search...'and @type='text']";

	private final String CLICK_FILTER_NAME_XPATH = "//input[@type='radio' and contains(@value,'%s')]/following-sibling::span[normalize-space(text())='%s']";
	private final String CLICK_FILTER_NAME_XPATH_MDA = "//div[contains(@class,'ui-multiselect-hasfilter')]/div[@class='customTreeContainer']//li/a/span[text()='%s']";
	private final String CLICK_FILTER_NAME_XPATH_MDA_AGGREGATED = "//div[contains(@class,'ui-multiselect-hasfilter')]/div[@class='customTreeContainer']//li/a[text()='%s']";
	private final String CLICK_FILTER_OPERATOR_XPATH = "//input[@type='radio']/following-sibling::span[normalize-space(text())='%s']";

	private final String FILTER_APPLY_BTN_XPATH = "//div[contains(@class, 'gs-rb-filter-popup')]/div[@class='gs-rb-applycancel']/div[contains(@class,'btn-apply')]";
	private final String FILTER_CANCEL_BTN_XPATH = "//div[contains(@class, 'gs-rb-filter-popup')]/div[@class='gs-rb-applycancel']/div[contains(@class,'btn-cancel')]";

	private final String FILTER_APPLY_BTN_POPUP = "//span[contains(text(),'Please wait while we crunch your data')]";

	private final String FILTER_VALUE_DATE_XPATH = "//div[@class='conditionViewWrapper']//span[contains(text(),'--None--')]";
	private final String FILTER_AVALUE_DATE_XPATH = "//div[contains(@class,'timeframeSelectDropdown ')]//input[@placeholder='Search' and @type='text']";
	private final String FILTER_AVALUE_DATE_MULTISELECT = "//input[contains(@title,'%s')]/following-sibling::span";
	private final String FILTER_AVALUE_DATE_DROPDOWN_XPATH = "//select[@class='for-width']/following-sibling::button";
	private final String FILTER_AVALUE_DATE_BTW_START = "//input[@data-date='START']";
	private final String FILTER_AVALUE_DATE_BTW_END = "//input[@data-date='END']";
	private final String FILTER_AVALUE_DATE_BTW_START_MONTH = "//select[@id='calMonthPicker']";
	private final String FILTER_AVALUE_DATE_BTW_START_MONTH_NUMBER = "//option[@value='%s']";
	private final String FILTER_AVALUE_DATE_BTW_START_YEAR = "//select[@id='calYearPicker']";
	private final String FILTER_AVALUE_DATE_BTW_START_DATE = "//td[contains(@class,'week') and (not(contains(@class,'Month')) and text()='%s')]";
	
	private final String FILTER_AVALUE_DATE_CUSTOM_INPUT = "//input[@data-date='DATE']";
	private final String FILTER_AVALUE_DATE_FIT_XPATH = "//li/label/input[@value='%s']/following-sibling::span[contains(text(),'%s')]";
	private final String FILTER_AVALUE_DATE_INPUT_XPATH = "//input[@placeholder='Date']";

	private final String FILTER_UNLOCKED_BTN_XPATH = "//span[@class='gs-rb-field-name require-tooltip' and contains(@title,'Display Name :: %s')]/following-sibling::span[@class='gs-rb-unlock-icon' and contains(@data-id,'%s')]";
	private final String FILTER_LOCKED_BTN_XPATH = "//span[@class='gs-rb-field-name require-tooltip' and contains(@title,'Display Name :: %s')]/following-sibling::span[@class='gs-rb-lock-icon' and contains(@data-id,'%s')]";
	private final String WHERE_FILTER_EXPRESSION = "//div[contains(@class,'nonagg')]/input";
	private final String HAVING_FILTER_EXPRESSION = "//div[contains(@class,'-agg')]/input";

	/**
	 * This will add the filter to sfdc objects.
	 * 
	 * @param reportFilter
	 */
	public void addFilter(ReportFilterSFDC reportFilter) {
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		String filedName = reportFilter.getFieldLabel();
		if (reportFilter.getAggregation() != null) {
			filedName = reportFilter.getAggregation().substring(0, 1)
					+ reportFilter.getAggregation().substring(1).toLowerCase() + " of " + filedName;
		}
		selectFilterName(reportFilter.getObjectName(), filedName);

		selectFilterOperator(ConstantsOperator.SFDCOperator.valueOf(reportFilter.getOperator()).getSFDCOperator());
		selectFilterValuesSFDC(reportFilter.getValue().toString());
		// Click Apply button
		item.click(FILTER_APPLY_BTN_XPATH);
		env.setTimeout(1);
		wait.waitTillElementNotDisplayed(FILTER_APPLY_BTN_POPUP, 1, 15);
		env.setTimeout(30);
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

	public void addFilterMDA(String aggFilterName, String filterDataType, String filterName, String filterOPerator,
			List<Object> filterValues, String filterType, ReportFilter reportFilter) {

		filterOPerator = modifyFilterOperator(filterOPerator);
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		selectFilterNameMda(filterName, aggFilterName);
		selectFilterOperator(filterOPerator);
		if ((filterDataType.contains("date")) && (reportFilter.getAggregateFunction()==null)) {
			selectFilterValuesForDate(filterOPerator, filterValues, reportFilter);
		} else {
			selectFilterValues(filterOPerator, filterValues);
		}
		// Click Apply button
		item.click(FILTER_APPLY_BTN_XPATH);
		env.setTimeout(1);
		wait.waitTillElementNotDisplayed(FILTER_APPLY_BTN_POPUP, 1, 15);
		env.setTimeout(30);
		if ((filterType).toUpperCase() == "LOCKED") {
			item.click(String.format(FILTER_LOCKED_BTN_XPATH, filterName, filterName));
		}
	}

	/**
	 * 
	 * @param filterName
	 * @param filterOPerator
	 * @param filterValues
	 * @param filterType
	 * @throws InterruptedException
	 */

	public void addAggregateFilter(String filterName, String filterOPerator, List<Object> filterValues,
			String filterType) throws InterruptedException {
		// open filter pop up
		item.click(FILTER_BTN_XPATH);
		selectFilterNameMda(filterName, null);
		selectFilterOperator(filterOPerator);
		selectFilterValues(filterOPerator, filterValues);
		// Click Apply button
		item.click(FILTER_APPLY_BTN_XPATH);
		wait.wait(5);
		if ((filterType).toUpperCase() == "LOCKED") {
			item.click(String.format(FILTER_LOCKED_BTN_XPATH, filterName, filterName));

		}
	}

	private void selectFilterName(String objectName, String filterName) {
		// Click filter name
		item.click(FILTER_NAME_SELECT_XPATH);
		item.setText(SEARCH_FILTER_NAME_XPATH, filterName);
		item.click(String.format(CLICK_FILTER_NAME_XPATH, objectName ,filterName));
	}

	private void selectFilterNameMda(String filterName, String aggFilterName) {
		// Click filter name
		item.click(FILTER_NAME_SELECT_XPATH);
		item.setText(SEARCH_FILTER_NAME_XPATH_MDA, filterName);
		if (aggFilterName != null) {
			filterName = aggFilterName;
			item.click(String.format(CLICK_FILTER_NAME_XPATH_MDA_AGGREGATED, filterName));
		} else {
			item.click(String.format(CLICK_FILTER_NAME_XPATH_MDA, filterName));
		}
	}

	private void selectFilterOperator(String filterOperator) {
		item.click(FILTER_OPERATOR_SELECT_XPATH);
		item.click(String.format(CLICK_FILTER_OPERATOR_XPATH, filterOperator.toLowerCase().replace("_", " ")));
	}

	private void selectFilterValues(String filterOperator, List<Object> filterValues) {
		if (filterValues.size() == 0) {
			item.click(FILTER_VALUE_NULL_XPATH);
		} else if (filterOperator == filterOPerators.INCLUDES.name()
				|| filterOperator == filterOPerators.EXCLUDES.name()) {
			for (int i = 0; i < filterValues.size(); i++) {
				item.selectCheckBox(String.format("//input[@type='checkbox' and @title='%s']", filterValues.get(i)));
			}
		} else {
			item.setText(FILTER_VALUE_XPATH + "/div/input", filterValues.get(0).toString());
		}
	}

	private void selectFilterValuesSFDC(String filterValues) {
		item.setText(FILTER_VALUE_XPATH + "/div/input", filterValues);
	}

	private void selectFilterValuesForDate(String filterOperator, List<Object> filterValues,
			ReportFilter reportFilter) {
	    Date date = new Date();
        DateUtil dateUtil = new DateUtil();
        Calendar cal = Calendar.getInstance();
        Calendar calnextWeek = Calendar.getInstance();
        calnextWeek.add(Calendar.DATE, +7);
        Date nextWeek = calnextWeek.getTime();
		item.click(FILTER_VALUE_DATE_XPATH);
		if (filterOperator.contains("equals")) {

			if (reportFilter.getTimeFunction() == null) {
				selectValueInDropDown("Custom");
				item.click(FILTER_VALUE_NULL_XPATH);
			} else if (reportFilter.getTimeFunction().contains("LAST")) {
				String filterValue = getFiletrValue(reportFilter.getTimeFunction());
				item.setText(DATE_FILTER_SEARCH, filterValue);
				item.click(String.format(FILTER_VALUE_LAST60DAYS, reportFilter.getTimeFunction().toString()));
			} else if (reportFilter.getTimeFunction().equalsIgnoreCase("Custom")) {
				selectValueInDropDown("Custom");
				//		item.setText(FILTER_AVALUE_DATE_CUSTOM_INPUT, filterValues.get(0).toString());
				item.setText(FILTER_AVALUE_DATE_CUSTOM_INPUT, dateUtil.getFormattedDate(date, "MM/dd/yyyy"));
			} else {
				String relativeTime = convertToRelativeTime(reportFilter.getTimeFunction());
				item.setText(FILTER_AVALUE_DATE_XPATH, relativeTime);
				item.click(String.format(FILTER_AVALUE_DATE_MULTISELECT, relativeTime));
			}

        } else if (filterOperator.contains("between")) {
            selectValueInDropDown("Custom");
            item.click(FILTER_AVALUE_DATE_BTW_START);
            item.click(FILTER_AVALUE_DATE_BTW_START_MONTH);
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_MONTH_NUMBER, cal.get(Calendar.MONTH)));
            item.click(FILTER_AVALUE_DATE_BTW_START_YEAR);
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_MONTH_NUMBER, cal.get(Calendar.YEAR)));
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_DATE, cal.get(Calendar.DAY_OF_MONTH)));
            item.click(FILTER_AVALUE_DATE_BTW_END);
            item.click(FILTER_AVALUE_DATE_BTW_END);
            item.click(FILTER_AVALUE_DATE_BTW_START_MONTH);
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_MONTH_NUMBER, calnextWeek.get(Calendar.MONTH)));
            item.click(FILTER_AVALUE_DATE_BTW_START_YEAR);
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_MONTH_NUMBER, calnextWeek.get(Calendar.YEAR)));
            item.click(String.format(FILTER_AVALUE_DATE_BTW_START_DATE, calnextWeek.get(Calendar.DAY_OF_MONTH)));
            item.click(FILTER_AVALUE_DATE_BTW_END);
            List<Object> ints = new ArrayList<Object>();
            ints.add(0, dateUtil.getFormattedDate(date, "MM-dd-yyyy"));
            ints.add(1, dateUtil.getFormattedDate(nextWeek, "MM-dd-yyyy"));
            reportFilter.setFilterValues(ints);

        } else {
            selectValueInDropDown("Custom");
            item.setText(FILTER_AVALUE_DATE_CUSTOM_INPUT, filterValues.get(0).toString());
        }
    }

	private String getFiletrValue(String timeFunction) {
		return timeFunction.replaceAll("_"," ");
	}

	private String convertToRelativeTime(String timeFunction) {
		if (timeFunction.contains("LAST_7_DAYS")) {
			return "Last 7 Days";
		}
		return timeFunction;
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
		} else if (filterOperator.contains("BTW")) {
			filterOperator = "between";
			return filterOperator;
		} else if (filterOperator.contains("NE")) {
			filterOperator = "not equals";
			return filterOperator;
		} else if (filterOperator.contains("LTE")) {
			filterOperator = "less or equal";
			return filterOperator;
		} else {
			return filterOperator;
		}
	}

	public void addWhereExpression(String whereExpression) {
		item.clearAndSetText(WHERE_FILTER_EXPRESSION, whereExpression);
	}

	public void addHavingExpression(String havingExpression) {
		item.clearAndSetText(HAVING_FILTER_EXPRESSION, havingExpression);
	}
}
