package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AdoptionAnalyticsPage extends AdoptionBasePage {
    private final String READY_INDICATOR                    = "//div[@class=' m_ctn results-btn' and text()='Go']";

    private final String MISSING_TEXT                       = "//span[@class='clsToShowMissingDataMsg']";
    private final String ADOP_GRID                          = "gview_dynamicAdoptionTableList";
    private final String ADOP_CHART                         = "//div[@class='trend_data_viz']/div[@class='highcharts-container']";
    private final String PERIOD_WEEK_SELECT_BUTTON          = "//div[@class='upto_sel']/select[contains(@class,'no_of_weeks no_of_weeks_view')]/following-sibling::button";
    private final String PERIOD_MONTH_SELECT_BUTTON         = "//div[@class='upto_sel']/select[contains(@class,'no_of_month no_of_month_view')]/following-sibling::button";
    private final String CUSTOMER_NAME_INPUT                = "//input[@class='search_input search-field ui-autocomplete-input']";
    private final String MEASURE_SELECT_BUTTON              = "//div[@class='m_ctn gs_att_filtsmall']/select[contains(@class,'measure_view')]/following-sibling::button";
    private final String MONTH_SELECT__BUTTON               = "//div[@class='gs_att_filtsmall']/select[contains(@class,'month month_view')]/following-sibling::button";
    private final String YEAR_SELECT_BUTTON                 = "//div[@class='gs_att_filtsmall']/select[contains(@class,'year year_view')]/following-sibling::button";
    private final String GO_BUTTON                          = "//div[@class=' m_ctn results-btn' and text()='Go']";
    private final String WEEK_END_DATE_INPUT                = "//input[@class='calendar period']";
    private final String INSTANCE_SELECT_BUTTON             = "//div[@class='instance-list-dd']/select[@class='select-instance']/following-sibling::button";
    private final String CHECK_ALL_MEASURES                 = "//a[@class='ui-multiselect-all']/span[contains(text(), 'Check all')]";
    private final String UNCHECK_ALL_MEASURES               = "//a[@class='ui-multiselect-none']/span[contains(text(), 'Uncheck all')]";
    private final String AUTO_SELECT_LIST                   = "//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']";
    private final String SEARCH_MEASURE_INPUT               = "//div[@class='ui-multiselect-filter']/input[@type='search']";
    private final String DRILL_DOWN_MSG_DIV                  = "//div[@class='click-to-drilldown']";

    private String customerName;
    private String measureNames = "All Measures";
    private String forTimeMonthPeriod = "6 Months";
    private String forTimeWeekPeriod = "52 Weeks";
    private String weekLabelDate = "";
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

    private void selectCustomer(String customerName) {
        wait.waitTillElementDisplayed(CUSTOMER_NAME_INPUT, MIN_TIME, MAX_TIME);
        driver.findElement(By.xpath(CUSTOMER_NAME_INPUT)).clear();
        driver.findElement(By.xpath(CUSTOMER_NAME_INPUT)).sendKeys(customerName);
        driver.findElement(By.xpath(CUSTOMER_NAME_INPUT)).sendKeys(Keys.ENTER);
        wait.waitTillElementDisplayed(AUTO_SELECT_LIST, MIN_TIME, MAX_TIME);

        List<WebElement> eleList = element.getAllElement("//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']/li/a[contains(text(), '"+customerName+"')]");
        Report.logInfo(String.valueOf(eleList.size()));
        boolean customerSelected = false;
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                ele.click();
                customerSelected = true;
            }
        }
        if(!customerSelected) throw new RuntimeException("Unable to select customer (or) customer not found" );
    }

    //Files Downloaded, Page Views.
    private void selectMeasures(String measures) {
        item.click(MEASURE_SELECT_BUTTON);
        amtDateUtil.stalePause();
        getFirstDisplayedElement(UNCHECK_ALL_MEASURES).click();
        String[] args = measures.split("\\|");
        for(String str : args) {
            getFirstDisplayedElement(SEARCH_MEASURE_INPUT).clear();
            getFirstDisplayedElement(SEARCH_MEASURE_INPUT).sendKeys(str.trim());
            getFirstDisplayedElement("//span[contains(text(), '"+str.trim()+"')]/preceding-sibling::input").click();
        }
    }

    public AdoptionAnalyticsPage displayCustWeeklyData() {
        selectCustomer(customerName);

        if (instance != null && !instance.isEmpty()) {
            wait.waitTillElementDisplayed(INSTANCE_SELECT_BUTTON, MIN_TIME, MAX_TIME);
            item.click(INSTANCE_SELECT_BUTTON);
            selectValueInDropDown(instance);
        }
        selectMeasures(measureNames);

        if (forTimeWeekPeriod != null && !forTimeWeekPeriod.isEmpty()) {
            item.click(PERIOD_WEEK_SELECT_BUTTON);
            selectValueInDropDown(forTimeWeekPeriod);
        }
        if (weekLabelDate != null && !weekLabelDate.isEmpty()) {
            getFirstDisplayedElement(WEEK_END_DATE_INPUT).clear();
            getFirstDisplayedElement(WEEK_END_DATE_INPUT).sendKeys(weekLabelDate);
        }
        button.click(GO_BUTTON);
        amtDateUtil.stalePause();
        return this;

    }

    public AdoptionAnalyticsPage displayCustMonthlyData() {
        selectCustomer(customerName);
        if (instance != null && !instance.isEmpty()) {
            wait.waitTillElementDisplayed(INSTANCE_SELECT_BUTTON, MIN_TIME, MAX_TIME);
            item.click(INSTANCE_SELECT_BUTTON);
            selectValueInDropDown(instance);
        }
        selectMeasures(measureNames);
        if(forTimeMonthPeriod != null && forTimeMonthPeriod != "") {
            item.click(PERIOD_MONTH_SELECT_BUTTON);
            selectValueInDropDown(forTimeMonthPeriod);
        }
        if (month != null && !month.isEmpty()) {
            item.click(MONTH_SELECT__BUTTON);
            selectValueInDropDown(month);
        }
        if (year != null && !year.isEmpty()) {
            item.click(YEAR_SELECT_BUTTON);
            selectValueInDropDown(year);
        }
        button.click(GO_BUTTON);
        amtDateUtil.stalePause();
        return this;
    }


    //Verifies if the usage chart is displayed or not.
    public boolean isChartDisplayed() {
        boolean success = false;
        Report.logInfo("Checking Adoption chart is displayed");
        try {
            WebElement ele = element.getElement(ADOP_CHART);
            if (ele != null) {
                success = ele.isDisplayed();
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " + e.getLocalizedMessage());
        }
        Report.logInfo("Checked Adoption chart display returning result: " + success);
        return success;
    }

    //Verifies if the adoption grid below the usage data is displayed (Not grill-down grid).
    public boolean isGridDisplayed() {
        boolean success = false;
        Report.logInfo("Checking adoption grid is displayed in adoption analytics page.");
        try {
            WebElement ele = element.getElement(ADOP_GRID);
            if (ele != null) {
                success = ele.isDisplayed();
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " + e.getLocalizedMessage());
        }
        Report.logInfo("Checked Adoption chart display returning result: " + success);
        return success;
    }

    public boolean isMissingDataInfoDisplayed(String value) {
        boolean success = false;
        Report.logInfo("Checking Missing Data message info is displayed above the graph.");
        try {
            WebElement ele = element.getElement(MISSING_TEXT);
            if (ele != null) {
                if (ele.getText().contains(value)) {
                    success = true;
                }
            }
        } catch (RuntimeException e) {
            Report.logInfo("No Such Ele : " + e.getLocalizedMessage());
        }
        Report.logInfo("Checked  Missing Data message info is displayed, returning result: " + success);
        return success;
    }


    public boolean isDataPresentInGrid(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        List<String> values = new ArrayList<String>();
        for (String v : s.split("\\|")) {
            values.add(v.trim());
        }
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'dynamicAdoptionTableList')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        for (WebElement row : rows) {
            if (row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Actual Text : " + row.getText());
                Report.logInfo("Expected Text: " + values.toString());
            }
            for (String val : values) {
                System.out.println("Checking String :" + val);
                if (rowtext.contains(val)) {
                    result = true;
                } else {
                    result = false;
                    Report.logInfo("Matched : " + result);
                    break;
                }
            }
            if (result) {
                break;
            }
            index++;
        }
        System.out.println("The Number of actual Rows : " + rows.size());
        Report.logInfo("Checked the data in the grid & returning result :" + result);
        return result;
    }

    public boolean isDrillDownMsgDisplayed(String msg) {
        try {
             String actualText = element.getElement(DRILL_DOWN_MSG_DIV).getText();
             Report.logInfo("Actual text : " +actualText +" - Expected : " +msg);
             return actualText.toLowerCase().contains(msg.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            Report.logInfo(e.getLocalizedMessage());
            return false;
        }
    }



}