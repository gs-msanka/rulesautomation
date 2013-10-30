package com.gainsight.sfdc.adoption.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.gainsight.pageobject.core.Report;

public class AdoptionAnalyticsPage extends AdoptionBasePage {
	
	/**
	 * Constructor waits for the ready element to be displayed in the page.
	 */
	public AdoptionAnalyticsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	/**
	 * Fills in deatils & clicks on view results for a customer usage data if org level configuration is weekly.
	 * @param cName
	 * @param instance
	 * @param measures
	 * @param tPeriod
	 * @param date
	 * @return
	 */
	public AdoptionAnalyticsPage displayCustWeeklyData(String cName, String instance, String measures, 
															String tPeriod, String date) {
		
		selectCustomer(cName);

		if(instance != null && !instance.isEmpty()) {
			if(isInstDropDownLoaded(instance)) {
				field.selectFromDropDown(INSTANCE_SELECT, instance);
			}
		}
		selectMeasures(measures);
		
		if(tPeriod != null && !tPeriod.isEmpty()) {
			field.selectFromDropDown(TIMEPERIOD_SELECT, tPeriod);
		}
		if(date != null && !date.isEmpty()) {
			field.clearAndSetText(ENDDATE_INPUT, date);
		}
		button.click(VIEWRESULTS_BUTTON);
		return this;
		
	}
	/**
	 * Fills the details for viewing the customer usage usage data if org level configuration is monthly.
	 * @param cName
	 * @param instance
	 * @param measures
	 * @param tPeriod
	 * @param month
	 * @param year
	 * @return
	 */
	public AdoptionAnalyticsPage displayCustMonthlyData(String cName, String instance, String measures, 
														String tPeriod, String month, String year) {
		
		selectCustomer(cName);
		if(instance != null && !instance.isEmpty()) {
			if(isInstDropDownLoaded(instance)) {
				field.selectFromDropDown(INSTANCE_SELECT, instance);
			}
		}
		selectMeasures(measures);
		if(tPeriod != null && !tPeriod.isEmpty()) {
			field.selectFromDropDown(TIMEPERIOD_SELECT, tPeriod);
		}
		if(month != null && !month.isEmpty()) {
			field.selectFromDropDown(MONTH_SELECT, month);
		}
		if(year != null && !year.isEmpty()) {
			field.selectFromDropDown(YEAR_SELECT, year);
		}
		button.click(VIEWRESULTS_BUTTON);
		return this;
	}
	
	/**
	 * Enters the customer name, lookup for the customer name, selects the customer.
	 * @param cName
	 */
	public void selectCustomer(String cName) {
		Report.logInfo("Selecting a customer to view adoption data for");
		item.clearAndSetText(CUSTOMERNAME_INPUT, cName);
		item.click(CUSTLOOKUP_IMG);
		wait.waitTillElementDisplayed("//a[contains(text(), '"+cName+"')]", MIN_TIME, MAX_TIME);
		item.click("//a[contains(text(), '"+cName+"')]");
		//item.clickLink("//a[text()='"+cName+"']");
		//wait.waitTillElementDisplayed(INSTANCE_SELECT, MIN_TIME, MAX_TIME);
		
		Report.logInfo("Selected the customer in adoption analytics page");
	}
	
	public boolean isInstDropDownLoaded(String instName) {
		boolean result = false;
        wait.waitTillElementDisplayed(INSTANCE_SELECT, MIN_TIME, MAX_TIME);
		String[] instList = instName.split("\\|");
		Select s = new Select(item.getElement(INSTANCE_SELECT));
		List<WebElement> opList = s.getOptions();
        String dropDownValue = null;
		for(WebElement wE : opList) {
             dropDownValue += wE.getText().trim() +" | ";
        }
        for(String str : instList) {
            if(dropDownValue.contains(str.trim())) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
		return result;
	}
	
	public boolean isInstDropDownLoaded() {
		boolean result = false;
		wait.waitTillElementDisplayed(INSTANCE_SELECT, MIN_TIME, MAX_TIME);
		Select s = new Select(item.getElement(INSTANCE_SELECT));
		List<WebElement> opList = s.getOptions();
		if(opList.size() > 1) {
			result = true;
		}
		return result;
	}
	/**
	 * Selects the list of measure provided measure values should be "|" separated.
	 * @param measures
	 */
	public void selectMeasures(String measures) {
		Report.logInfo("Selecting Measure to view the adoption data.");
		item.click(MEASURE_SELECT);
		wait.waitTillElementDisplayed(MEASURESELECT_BLOCK, MIN_TIME, MAX_TIME);
		String ch = driver.findElement(By.xpath("//li[text()='All Measures']/child::input")).getAttribute("checked");
		if(ch == null) {
			if(measures.equalsIgnoreCase("All Measures")) {
				item.click("//li[text()='All Measures']/child::input");
			} else {
				item.click("//li[text()='All Measures']/child::input");
				sleep(1); //Need a time frame for child checkbooks to get effected.
				item.click("//li[text()='All Measures']/child::input");
				for(String m : measures.split("\\|")) {
					item.click("//li[text()='"+m.trim()+"']/child::input");
					Report.logInfo("Seleted the Measure name: " +m.trim());
				}
			}
			
		} else {
			if(!measures.equalsIgnoreCase("All Measures")) {
				item.click("//li[text()='All Measures']/child::input");
				sleep(1); //Need a time frame for child checkbooks to get effected.
				for(String m : measures.split("\\|")) {
					item.click("//li[text()='"+m.trim()+"']/child::input");
					Report.logInfo("Seleted the Measure name: " +m.trim());
				}
			}
			
		}
		Report.logInfo("Selected the above measures to view adoption data");
	}
	
	public AdoptionAnalyticsPage clickOnAddAlert() {
		button.click(ADDALERT_BUTTON);
		return this;
	}
	
	//Verifies if the usage chart is displayed or not.
	public boolean isChartDisplayed() {
		boolean success = false;
		Report.logInfo("Checking Adoption chart is displayed");
		if(field.isElementPresent(ADOP_CHART)) {
			success = true;
		}
		Report.logInfo("Checked Adoption chart display returning result: " +success);
		return success;
	}
	
	//Verifies if the adoption grid below the usage data is displayed (Not grill-down grid).
	public boolean isGridDispalyed() {
		boolean success = false;
		Report.logInfo("Checking aoption grid is displayed in adoption analytics page.");
		wait.waitTillElementDisplayed(ADOP_CHART, MIN_TIME, MAX_TIME);
		if(field.isElementPresent(ADOP_GRID)){
			success = true;
		}
		Report.logInfo("Checked adoption grid appearence in adoption analytics & returning result: "+success);
		return success;
	}
	
	public boolean isMissingDataInfoDisplayed() {
		boolean success = false;
		Report.logInfo("Checking Missing Data message info is displayed above the graph.");
		if(field.isElementPresent(MISSING_TEXT)) {
			success = true;
		}
		Report.logInfo("Checked adoption missing data message on screen & returning result: " +success);
		return success;
	}
	
	private final String MISSING_TEXT 			= "//span[@class='clsToShowMissingDataMsg']";
	private final String ADOP_GRID			    = "adoptionDataGridView";
	private final String ADOP_CHART				= "userAdoptionChartId";
	private final String READY_INDICATOR 		= "selectionFilterControls";
	private final String CUSTOMERNAME_INPUT 	= "//div[@class='requiredInput']/input";
	private final String MEASURE_SELECT	 		= "//a[@class='measureSelectorLink measureSwitch newMeasureDD']";
	private final String TIMEPERIOD_SELECT 		= "//select[@class='jbaraDummyPeriodSelectControl min-width']";
	private final String MONTH_SELECT 			= "//select[@class='jbaraDummyMonSelectControl min-width']";
	private final String YEAR_SELECT 			= "//select[@class='jbaraDummyYearSelectControl min-width']";
	private final String VIEWRESULTS_BUTTON 	= "btnUsageGraphGoBtn";
	private final String ADDALERT_BUTTON		= "//input[@class='btn dummyAllAlertNewBtn']";
	private final String ENDDATE_INPUT			= "//input[@class='min-width jbaraDateInputAnalytics']";
	private final String INSTANCE_SELECT		= "//select[@class='jbaraDummyInstanceSelectControl classToHandeInstance']";
	private final String CUSTLOOKUP_IMG			= "//img[@alt='Customer Name Lookup' and @title='Customer Name Lookup']";
	private final String MEASURESELECT_BLOCK	= "measureSelectorContainer";
	private final String NOADOPTION_MSG         = "AdoptionDataNotAvailable";
	

}