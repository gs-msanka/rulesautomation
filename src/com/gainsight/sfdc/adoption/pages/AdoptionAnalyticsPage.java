package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class AdoptionAnalyticsPage extends AdoptionBasePage {


    private final String MISSING_TEXT 			= "//span[@class='clsToShowMissingDataMsg']";
    private final String ADOP_GRID			    = "adoptionDataGridView";
    private final String ADOP_CHART				= "userAdoptionChartId";
    private final String READY_INDICATOR 		= "selectionFilterControls";
    private final String CUSTOMERNAME_INPUT 	= "//div[@class='requiredInput']/input";
    //private final String MEASURE_SELECT	 		= "//a[@class='measureSelectorLink measureSwitch newMeasureDD']";
    private final String TIMEPERIOD_SELECT 		= "//select[@class='jbaraDummyPeriodSelectControl min-width']";
    private final String MONTH_SELECT 			= "//select[@class='jbaraDummyMonSelectControl min-width']";
    private final String YEAR_SELECT 			= "//select[@class='jbaraDummyYearSelectControl min-width']";
    private final String VIEWRESULTS_BUTTON 	= "btnUsageGraphGoBtn";
    private final String ADD_ALERT_BUTTON		= "//input[@class='btn dummyAllAlertNewBtn']";
    private final String ENDDATE_INPUT			= "//input[@class='min-width jbaraDateInputAnalytics']";
    private final String INSTANCE_SELECT		= "//select[@class='jbaraDummyInstanceSelectControl classToHandeInstance']";
    private final String CUSTLOOKUP_IMG			= "//img[@alt='Customer Name Lookup' and @title='Customer Name Lookup']";
    private final String MEASURESELECT_BLOCK	= "measureSelectorContainer";
    private final String NOADOPTION_MSG         = "AdoptionDataNotAvailable";
    private final String MEASURE_SELECT = "//span[@class='measureSelectorLink measureSwitch newMeasureDD']";
    private final String MEASURE_SEARCH_INPUT = "//input[@class='searchMeasureText']";
    private final String ALL_MEASURES = "//span[contains(text(), 'All Measures')]/preceding-sibling::input[@class='measureSwitch allMeasureCheckBox']";
    private final String UNGROUP_ALL_MEASURES = "//input[@class='ungroupMeaures' and @type='checkbox']";

    private String customerName;
    private String measureNames         = "All Measures";
    private String forTimeMonthPeriod   = "6 Months";
    private String forTimeWeekPeriod    = "52 Weeks";
    private String weekLabelDate        = "";
    private String month;
    private String year;

    public void setWeekLabelDate(String weekLabelDate) {
        this.weekLabelDate = weekLabelDate;
    }

    public void setForTimeWeekPeriod(String forTimeWeekPeriod) {

        this.forTimeWeekPeriod = forTimeWeekPeriod;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    private String instance;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getForTimeMonthPeriod() {
        return forTimeMonthPeriod;
    }

    public void setForTimeMonthPeriod(String forTimeMonthPeriod) {
        this.forTimeMonthPeriod = forTimeMonthPeriod;
    }

    public String getMeasureNames() {
        return measureNames;
    }

    public void setMeasureNames(String measureNames) {
        this.measureNames = measureNames;
    }

    /**
	 * Constructor waits for the ready element to be displayed in the page.
	 */
	public AdoptionAnalyticsPage() {
		wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
	}
	
	/**
	 * Fills in deatils & clicks on view results for a customer usage data if org level configuration is weekly.
	 * @return
	 */
	public AdoptionAnalyticsPage displayCustWeeklyData() {
		selectCustomer(customerName);
		if(instance != null && !instance.isEmpty()) {
			if(isInstDropDownLoaded(instance)) {
				field.selectFromDropDown(INSTANCE_SELECT, instance);
			}
		}
		selectMeasures(measureNames);
		
		if(forTimeWeekPeriod != null && !forTimeWeekPeriod.isEmpty()) {
			field.selectFromDropDown(TIMEPERIOD_SELECT, forTimeWeekPeriod);
		}
		if(weekLabelDate != null && !weekLabelDate.isEmpty()) {
			field.clearAndSetText(ENDDATE_INPUT, weekLabelDate);
		}
		button.click(VIEWRESULTS_BUTTON);
		return this;
		
	}
	/**
	 * Fills the details for viewing the customer usage usage data if org level configuration is monthly
	 * @return
	 */
	public AdoptionAnalyticsPage displayCustMonthlyData() {
		
		selectCustomer(customerName);
		if(instance != null && !instance.isEmpty()) {
			if(isInstDropDownLoaded(instance)) {
				field.selectFromDropDown(INSTANCE_SELECT, instance);
			}
		}
		selectMeasures(measureNames);
	    field.selectFromDropDown(TIMEPERIOD_SELECT, forTimeMonthPeriod);
		if(month != null && !month.isEmpty()) {
            Select select = new Select(driver.findElement(By.xpath(MONTH_SELECT)));
            select.selectByValue(month);
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
	

	/**
	 * Selects the list of measure provided measure values should be "|" separated.
	 * @param measures
	 */
	public void selectMeasures(String measures) {
        String MEASURE_DIV = "//div[@class='gs_mult_drop hide usage360MeasureSelectorStyles']";
		Report.logInfo("Selecting Measure to view the adoption data.");
		item.click(MEASURE_SELECT);
		wait.waitTillElementDisplayed(MEASURE_DIV, MIN_TIME, MAX_TIME);
        if(measures.equalsIgnoreCase("All Measures")) {
            String text = element.getElement(ALL_MEASURES).getAttribute("checked");
            if(text == null || text.equalsIgnoreCase("unchecked")) {
                item.click(ALL_MEASURES);
                amtDateUtil.stalePause();
            }
        } else {
            item.click(UNGROUP_ALL_MEASURES);
            amtDateUtil.stalePause();
            for(String measure : measures.split(",")) {
                String measure_xpath = "//li[text()='"+measure.trim()+"']/input[@type='checkbox']";
                String text = element.getElement(measure_xpath).getAttribute("checked");
                if(text == null || text.equalsIgnoreCase("unchecked")) {
                    item.click(measure_xpath);
                }
            }
        }
	}
	
	public AdoptionAnalyticsPage clickOnAddAlert() {
		button.click(ADD_ALERT_BUTTON);
		return this;
	}
	
	//Verifies if the usage chart is displayed or not.
	public boolean isChartDisplayed() {
		boolean success = false;
		Report.logInfo("Checking Adoption chart is displayed");
        try {
            WebElement ele = element.getElement(ADOP_CHART);
            if(ele != null) {
                success = ele.isDisplayed();
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " +e.getLocalizedMessage());
        }
        Report.logInfo("Checked Adoption chart display returning result: " +success);
		return success;
	}
	
	//Verifies if the adoption grid below the usage data is displayed (Not grill-down grid).
	public boolean isGridDispalyed() {
		boolean success = false;
		Report.logInfo("Checking adoption grid is displayed in adoption analytics page.");
		try {
            WebElement ele = element.getElement(ADOP_GRID);
            if(ele != null) {
                success = ele.isDisplayed();
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " +e.getLocalizedMessage());
        }
        Report.logInfo("Checked Adoption chart display returning result: " +success);
        return success;
	}
	
	public boolean isMissingDataInfoDisplayed(String value) {
		boolean success = false;
		Report.logInfo("Checking Missing Data message info is displayed above the graph.");
		try {
            WebElement ele = element.getElement(MISSING_TEXT);
            if(ele != null) {
                if(ele.getText().contains(value)) {
                    success =true;
                }
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " +e.getLocalizedMessage());
        }
        Report.logInfo("Checked  Missing Data message info is displayed, returning result: " +success);
        return success;
	}


    public boolean isNoAdoptionDataMsgDisplayed() {
        boolean result = false;
        try {
            WebElement ele = element.getElement(NOADOPTION_MSG);
            if(ele != null) {
                if(ele.isDisplayed()) {
                    if(ele.getText().contains("No Adoption data found for "+customerName+"")) {
                        result = true;
                    }
                }
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " +e.getLocalizedMessage());
        }
        return result;
    }

    public boolean isDataPresentInGrid(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        List<String> values = new ArrayList<String>();
        for(String v : s.split("\\|")) {
            values.add(v.trim());
        }
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'dynamicAdoptionTableList')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        for(WebElement row : rows) {
            if(row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Actual Text : " +row.getText());
                Report.logInfo("Expected Text: " +values.toString());
            }
            for(String val : values ) {
                System.out.println("Checking String :" +val);
                if(rowtext.contains(val)) {
                    result = true;
                } else {
                    result = false;
                    Report.logInfo("Matched : " +result);
                    break;
                }
            }
            if(result) {
                break;
            }
            index++;
        }
        System.out.println("The Number of actual Rows : " +rows.size());
        Report.logInfo("Checked the data in the grid & returning result :" +result);
        return result;
    }

    public boolean isSelectedCustomerName(String cName) {
        try {
            if(element.getElement(CUSTOMERNAME_INPUT).getAttribute("value").contains(cName)) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Some Exception *** " +e.getLocalizedMessage());
        }
        return false;
    }

    public boolean verifySelectedInstanceValue(String value) {
        try {
            Select instanceSelect = new Select(element.getElement(INSTANCE_SELECT));
            if(instanceSelect.getFirstSelectedOption().getText().equalsIgnoreCase(value)) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Som Exception***" +e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
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

    public boolean isInsDropDownDisplayed() {
        try {
            return element.getElement(INSTANCE_SELECT).isDisplayed();

        } catch (Exception e) {
            Report.logInfo("***Some Exception*** " +e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }


}