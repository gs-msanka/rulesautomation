package com.gainsight.sfdc.reporting.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

public class ReportingSettingsUtils extends BasePage {

	private final String SETTINGS_BTN_XPATH = "//div[@title='Settings']";

	// Ranking related xpath's
	private final String RANKING_BTN_XPATH = "//label[@class='gs-rb-settings-label' and contains(text(),'Ranking')]";
	private final String CLICK_RANKING_FIELDNAME_XPATH = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Choose a field')]";
	private final String SELECT_RANKING_FEILDNAME_XPATH = "//input[@type='radio' and @title='%s']/parent::label";
	private final String CLICK_RANKING_OPERATOR_XPATH = "//span[@class='ui-multiselect-selected-label' and contains(text(),'Top')]";
	private final String SELECT_RANKING_OPERATOR_XPATH = "//input[@type='radio' and @title='%s']/parent::label";

	private final String RANKING_VALUE_XPATH = "//input[@type='text' and @class='form-control limit']";
	private final String RANKING_APPLY_BTN_XPATH = "//div[@class='gs-rb-applycancel']/div[contains(text(),'Apply')]";
	private final String RANKING_CANCEL_BTN_XAPTH = "//div[@class='gs-rb-applycancel']/div[contains(text(),'Cancel')]";
	private final String CLEAR_RANKING_BTN_XPATH = "//div[@class='gs-rb-report-close pull-right']";
	private final String RANKING_APPLY_BTN_POPUP = "//span[contains(text(),'Please wait while we crunch your data')]";

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
	public boolean applyRanking(String fieldName, String operator, String value) {
		Log.info("Apply Raking for field: " + fieldName + ",with Operator: " + operator + ",with value: " + value);
		item.click(SETTINGS_BTN_XPATH);
		boolean isRankingApplied = false;
		item.click(RANKING_BTN_XPATH);
		isRankingApplied = applyRankingOnFieldName(fieldName);
		if (isRankingApplied) {
			if (operator.contains("desc")) {
				operator = "Top";
			} else {
				operator = "Bottom";
			}
			isRankingApplied = applyRankingOperator(operator);
			if (isRankingApplied) {
				isRankingApplied = setRankingValue(value);
				if (isRankingApplied) {
					item.click(RANKING_APPLY_BTN_XPATH);
					wait.waitTillElementNotDisplayed(RANKING_APPLY_BTN_POPUP, 1, 5);
				} else {
					item.click(RANKING_CANCEL_BTN_XAPTH);
				}
			}
		}
		return isRankingApplied;
	}

	private boolean applyRankingOnFieldName(String fieldName) {
		try {
			item.click(CLICK_RANKING_FIELDNAME_XPATH);
			item.click(String.format(SELECT_RANKING_FEILDNAME_XPATH, fieldName));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private boolean applyRankingOperator(String operator) {
		try {
			item.click(CLICK_RANKING_OPERATOR_XPATH);
			item.click(String.format(SELECT_RANKING_OPERATOR_XPATH, operator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean setRankingValue(String value) {
		try {
			item.setText(RANKING_VALUE_XPATH, value);
			return true;
		} catch (Exception e) {
			return false;
		}

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

}
