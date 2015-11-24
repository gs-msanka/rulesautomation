package com.gainsight.sfdc.reporting.pages;

import com.gainsight.sfdc.reporting.constants.XPathConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Log;

/**
 *
 * Contains utils for reporting base page.
 */
public class ReportingBasePage extends BasePage {

	ReportingSettingsUtils reportingSettingsUtils = new ReportingSettingsUtils();
	ReportingFilterUtils reportingFilterUtils = new ReportingFilterUtils();
	private String type = "FORCE";

	public ReportingBasePage() {
		// wait.waitTillElementPresent(XPathConstants.getXPath("READY_INDICATOR"),
		// MIN_TIME, MAX_TIME);
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @param objName
	 *            , Calls To Action
	 */
	public void selectObjectFromSource(String objName) {

		Log.info("Selecting Object from Source: " + objName);
		item.click(XPathConstants.getXPath("CLICKONDATASOURCE"));

		item.setText(XPathConstants.getXPath("SEARCHDATASOURCE"), objName);
		wait.waitTillElementDisplayed(
				String.format(XPathConstants.getXPath("CLICKSEARCHEDOBJECTNAME"), objName, objName), 0, 2);
		item.click(String.format(XPathConstants.getXPath("CLICKSEARCHEDOBJECTNAME"), objName, objName));
		wait.waitTillElementNotDisplayed(XPathConstants.getXPath("WAITTOLOADFILEDSFOROBJECT"), 1, 5);
		WebElement ele = element
				.getElement(By.xpath(String.format(XPathConstants.getXPath("VERIFYSELECTEDOBJECTNAME"), objName)));
		if (ele.isDisplayed()) {
			Log.info(objName + "Object Selected successfully");
		}
	}

	/**
	 * 
	 * @param fieldName
	 * @param ObjName
	 */
	public void addShowMeField(String fieldName, String ObjName) {
		Log.info("Adding field into Show Me section: " + fieldName);
		item.click(XPathConstants.getXPath("ADDSHOWME"));
		item.setText(XPathConstants.getXPath("SEARCHSHOWMEField"), fieldName);
		item.click(String.format(XPathConstants.getXPath("CLICKSEARCHSHOWMEFIELd"), fieldName, ObjName));
	}

	/**
	 * 
	 * @param fieldName
	 */
	public void addShowMeFieldMDA(String fieldName) {

		Log.info("Adding field into Show Me section: " + fieldName);
		item.click(XPathConstants.getXPath("ADDSHOWME"));
		item.setText(XPathConstants.getXPath("SEARCHSHOWMEFieldMDA"), fieldName);
		item.click(String.format(XPathConstants.getXPath("CLICKSEARCHSHOWMEFIELdMDA"), fieldName));
	}

	/**
	 *
	 * @param oldDisplayName
	 * @param newDisplayName
	 * @param aggregation
	 * @param decimalPlaces
	 */
	public void showMeFieldSettings(String oldDisplayName, String newDisplayName, String aggregation,
			String decimalPlaces) {
		Log.info("Changing field display name and aggregation for: " + oldDisplayName);
		item.click(String.format(XPathConstants.getXPath("SHOWMESETTINGS"), oldDisplayName));

		if ((newDisplayName != "") || (newDisplayName != null)) {
			item.clearAndSetText(XPathConstants.getXPath("FIELDDISPLAYNAME"), newDisplayName);
		}
		// Need to write a little bit logic here
		if (aggregation != null) {
			item.clearAndSetText(XPathConstants.getXPath("AGGREGATION"), aggregation);
		}
		if (decimalPlaces != null) {
			item.clearAndSetText(XPathConstants.getXPath("DISPLAY"), decimalPlaces);
		}

	}

	public void addByField(String fieldName, String ObjName) {
		Log.info("Adding field into By section: " + fieldName);
		item.click(XPathConstants.getXPath("ADDBY"));
		item.setText(XPathConstants.getXPath("SEARCHBYFIELD"), fieldName);
		wait.waitTillElementDisplayed(String.format(XPathConstants.getXPath("CLICKSEARCHBYFIELD"), fieldName, ObjName),
				0, 2);
		item.click(String.format(XPathConstants.getXPath("CLICKSEARCHBYFIELD"), fieldName, ObjName));

	}

	public void addByFieldMDA(String fieldName, String ObjName) {
		Log.info("Adding field into By section: " + fieldName);
		item.click(XPathConstants.getXPath("ADDBY"));
		item.setText(XPathConstants.getXPath("SEARCHSHOWMEFieldMDA"), fieldName);
		wait.waitTillElementDisplayed(
				String.format(XPathConstants.getXPath("CLICKSEARCHSHOWMEFIELdMDA"), fieldName, ObjName), 0, 2);
		item.click(String.format(XPathConstants.getXPath("CLICKSEARCHSHOWMEFIELdMDA"), fieldName, ObjName));

	}

	/**
	 *
	 * @param oldDisplayName
	 * @param newDisplayName
	 */
	public void byFieldSettings(String oldDisplayName, String newDisplayName) {

		Log.info("Changing field display name for: " + oldDisplayName);
		item.click(String.format(XPathConstants.getXPath("SHOWMESETTINGS"), oldDisplayName));

		if ((newDisplayName != "") || (newDisplayName != null)) {
			item.clearAndSetText(XPathConstants.getXPath("FIELDDISPLAYNAME"), newDisplayName);
		}
	}

	/**
	 * 
	 * @param reportName
	 */
	public void saveReport(String reportName) {
		Log.info("Saving the Report : " + reportName);
		if (reportName != "") {
			item.setText(XPathConstants.getXPath("REPORTNAME"), reportName);
			element.getElement(XPathConstants.getXPath("REPORTNAME")).sendKeys(Keys.TAB);
			/*
			 * if (element.getElement(XPathConstants.getXPath("REPORTNAME")).
			 * isDisplayed()) {
			 * element.getElement(XPathConstants.getXPath("REPORTNAME")).
			 * sendKeys(Keys.TAB); }
			 */

		}
		item.click(XPathConstants.getXPath("SAVEBUTTON_XPATH"));

	}

	/**
	 * Run the report by clickig on Apply button
	 */
	public void runReport() {
		Log.info("Clicking on apply button to run the report");
		if (item.isElementPresent(XPathConstants.getXPath("APPLY_BTN_XPATH"))) {
			item.click(XPathConstants.getXPath("APPLY_BTN_XPATH"));
		}

	}

	/**
	 * 
	 * @param reportName
	 */
	public void saveAsReport(String reportName) {

		Log.info("Save As the Report : " + reportName);
		item.click(XPathConstants.getXPath("SAVEAS_BTN_XPATH"));
		item.clearAndSetText(XPathConstants.getXPath("SAVEAS_TXT_XPATH"), reportName);
		item.click(XPathConstants.getXPath("SAVE_SAVEAS_BTN_XPATH"));
		// Timer.sleep(2);
	}

	/**
	 * This method deletes the report
	 * 
	 * @param reportName
	 */
	public void deleteReport(String reportName) {
		Log.info("Deleting the report: " + reportName);
		item.click(XPathConstants.getXPath("DELETE_BTN_XPATH"));
		item.click(XPathConstants.getXPath("DELETE_YES_BTN_XPATH"));
	}

	/**
	 * This method loads the report from the Repository
	 * 
	 * @param reportName
	 * @return true/fasle
	 */
	public void loadReportFromRepo(String reportName) {
		Log.info("Loading the report from Rep: " + reportName);
		item.click(XPathConstants.getXPath("REPOSITORY_BTN_XPATH"));
		item.clearAndSetText(XPathConstants.getXPath("REPOSITORY_SEARCH_TXT_XPATH"), reportName);
		item.click(String.format(XPathConstants.getXPath("CLICK_SEARCHREPORTNAME_REPOSITORY"), reportName));
	}

	/**
	 * @param rulesUrl
	 */
	public void openReportingPage(String rulesUrl) {
		URL = rulesUrl;
		open();
		wait.waitTillElementDisplayed(XPathConstants.getXPath("READY_INDICATOR"), MIN_TIME, MAX_TIME);

	}

}

// Follow Xpath naming conventions
// Selecting Object from Data Source
// Adding fields to Show Me section
// Adding fields to By Section
// Saving the Report
// Adding Where Filters
// Adding Having Filters
// Apply Ranking
// Apply Chart Options
// Save as Report and methods for RB icons
// Reload the reports from Repo
// Change the Chart Type
// Validate the Saved report
// Validate the info messages pop up like, save SAve as , when clicked on show
// me with out object
// Method best practices
/// **
// The Description of the method to explain what the method does
// @param the parameters used by the method
// @return the value returned by the method
// @throws what kind of exception does this method throw
// */
