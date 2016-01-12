package com.gainsight.sfdc.reporting.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

public class ReportingSettingsUtils extends BasePage {

	private final String SETTINGS_BTN_XPATH = "//div[@title='Settings']";

	// Ranking related xpath's
	private final String RANKING_BTN_XPATH = "//label[@class='gs-rb-settings-label' and contains(text(),'Ranking')]";
	private final String CLICK_RANKING_FIELDNAME_XPATH = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Choose a field')]";
//	private final String SELECT_RANKING_FEILDNAME_XPATH = "//input[@type='radio' and @title='%s']/parent::label";
	private final String SELECT_RANKING_FEILDNAME_XPATH = "//input[@type='radio' and contains(@title,'%s')]/parent::label";
	private final String CLICK_RANKING_OPERATOR_XPATH = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Top')]";
	private final String SELECT_RANKING_OPERATOR_XPATH = "//input[@type='radio' and @title='%s']/parent::label";

	private final String RANKING_VALUE_XPATH = "//input[@type='text' and @class='form-control limit']";
	private final String RANKING_APPLY_BTN_XPATH = "//div[@class='gs-rb-applycancel']/div[contains(text(),'Apply')]";
	private final String RANKING_CANCEL_BTN_XAPTH = "//div[@class='gs-rb-applycancel']/div[contains(text(),'Cancel')]";
	private final String CLEAR_RANKING_BTN_XPATH = "//div[@class='gs-rb-report-close pull-right']";
	private final String RANKING_APPLY_BTN_POPUP = "//span[contains(text(),'Please wait while we crunch your data')]";

	private final String CHART_ICON = "//ul[contains(@class,'selectedType')]//span[@class='table-chart-icon']";
	private final String CHART_LABEL = "//span[@class='gs-rb-chart-label' and contains(text(),'%s')]";

	private final String EXPORTASEXCEL_BTN_XPATH = "//label[@class='gs-rb-settings-label' and contains(text(),'Export as Excel')]";
	private final String EXPORTASCSV_BTN_XPATH = "";
	private final String EXPORRASIMAGE_BTN_XPATH = "//label[@class='gs-rb-settings-label' and contains(text(),'Export as Image')]";
	private final String CHARTOPTIONS_BTN_XPATH = "//label[@class='gs-rb-settings-label' and contains(text(),'Chart options')]";

	/**
	 * This method is used to apply Ranking
	 * 
	 * @param fieldName
	 *            , Count of Account Name
	 * @param operator,
	 *            Top/Bottom
	 * @param value
	 *            , 5
	 * @return , true/false
	 * @throws InterruptedException
	 */
	public void applyRanking(String fieldName, String operator, String value) {
		Log.info("Apply Raking for field: " + fieldName + ",with Operator: " + operator + ",with value: " + value);
		item.click(SETTINGS_BTN_XPATH);
		item.click(RANKING_BTN_XPATH);
		applyRankingOnFieldName(fieldName);
		if (operator.contains("desc") || operator.contains("DESC")) {
			operator = "Top";
		} else {
			operator = "Bottom";
		}
		applyRankingOperator(operator);
		setRankingValue(value);
		item.click(RANKING_APPLY_BTN_XPATH);
		env.setTimeout(3);
		wait.waitTillElementNotDisplayed(RANKING_APPLY_BTN_POPUP, 1, 5);
		env.setTimeout(30);
	}

	private void applyRankingOnFieldName(String fieldName) {
		item.click(CLICK_RANKING_FIELDNAME_XPATH);
		item.click(String.format(SELECT_RANKING_FEILDNAME_XPATH, fieldName));
	}

	private void applyRankingOperator(String operator) {
		item.click(CLICK_RANKING_OPERATOR_XPATH);
		item.click(String.format(SELECT_RANKING_OPERATOR_XPATH, operator));
	}

	private void setRankingValue(String value) {
		item.setText(RANKING_VALUE_XPATH, value);
	}

	/**
	 * This method clears Ranking
	 */
	public void clearRanking() {
		Log.info("Clearing Ranking....");
		item.click(RANKING_BTN_XPATH);
		item.click(CLEAR_RANKING_BTN_XPATH);
		item.click(RANKING_APPLY_BTN_XPATH);
	}

	public void selectReportType(String chartType) {
		chartType = convertChartType(chartType);
		item.click(CHART_ICON);
		item.click(String.format(CHART_LABEL, chartType));
		env.setTimeout(3);
		wait.waitTillElementNotDisplayed(RANKING_APPLY_BTN_POPUP, 1, 15);
		env.setTimeout(30);
	}

	private String convertChartType(String chartType) {
		// TODO Auto-generated method stub
		if (chartType.contains("PIE")) {
			return "Pie";
		} else if (chartType.contains("STACKED_BAR")) {
			return "Stacked Bar";
		} else if (chartType.contains("STACKED_COLUMN")) {
			return "Stacked Column";
		} else if (chartType.contains("COLUMN_LINE")) {
			return "Column Line";
		} else if (chartType.contains("GRID") || chartType.contains("LIST")) {
			return "Table";
		} else if (chartType.contains("BAR")) {
			return "Bar";
		} else if (chartType.contains("COLUMN")) {
			return "Column";
		} else if (chartType.contains("BUBBLE")) {
			return "Bubble";
		} else if (chartType.contains("SCATTER")) {
			return "Scatter";
		} else if (chartType.contains("LINE")) {
			return "Line";
		} else if (chartType.contains("AREA")) {
			return "Area";
		}
		return chartType;
	}
}
