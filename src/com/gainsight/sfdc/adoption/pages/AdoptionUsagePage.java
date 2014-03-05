package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;


public class AdoptionUsagePage extends AdoptionBasePage {
    private final String EXPORT_SUCCESS_MSG		= "exportApexMsgId";
    private final String GRID_EXPORT_LINK		= "//div[@id='AdoptionUsageGridNavigationBar']/div/a[@title='export']";
    private final String GRID_CUST_STATUS_BLOCK = "//div[@class='dummyDDStatusFilterList ui-corner-bottom']";
    private final String GRID_CUST_STATUS_IMG 	= "//img[@class='jbarahideListButton dummyHideChurnAnalyticIcon' and @title='Select to filter']";
    private final String WARNING_MESSAGE 		= "//div[@class='additionalCountSection']";
    private final String ADOPTION_GRID	 		= "containerAdoptionTableList";
    private final String READY_INDICATOR 		= "//div[@id='Adoption-Usage']";
    private final String PIN_ICON 				= "//img[@id='pinIcon']";
    private final String MONTH_SELECT 			= "//div[@class='JbaraMonthlyFilter hideForOldAdoption']/select";
    private final String YEAR_SELECT 			= "//div[@class='JbaraMonthlyFilter changeMyFloat']/select";
    //private final String MEASURE_SELECT 		= "//select[@class='jbaraDummyAdoptionMeasureSelectControl min-width']";
    private final String GO_BUTTON 				= "//div[@class='newFilters']/div[2]/input[@value='View Results']";
    private final String DATAVIEW_SELECT 		= "//select[@class='jbaraDummyAdoptionDataViewSelectControl min-width']";
    private final String WEEK_PERIOD_SELECT		= "//select[@class='dummyJbaraWeeksPeriodsSelectionCntrl min-width']";
    private final String WEEKDATE_UPTO_INPUT	= "//input[@class='jbaraAdoptionGridInputField min-width']";
    private final String ADVANCEDSEARCH_BUTTON	= "//span[@class='dummygrdAdvancedSearch' and @title='Search']";
    private final String ASEARCH_SEARCH_BUTTON 	= "//input[@class='Search' and @type='button']";
    private final String ASEARCH_RESET_BUTTON 	= "//input[@class='Reset' and @type='button']";
    private final String ASEARCH_CLOSE_BUTTON 	= "//input[@class='Close' and @type='button']";
    private final String ASEARCH_CLOSE 			= "//a[@class='ui-dialog-titlebar-close ui-corner-all']/span[text()='close']";
    private final String UIVIEW_SELECT          = "//select[@class='jbaraDummyAdoptionUIViewsSelectControl']";
    private final String MEASURE_SELECT = "//div[@class='newMeasureSelector']";
    private final String MEASURE_DISPLAY_DIV = "//div[@class='gs_mult_drop hide']";
    private final String MEASURE_SEARCH_INPUT = "//input[@class='singleSearchMeasureText']";

    String month 		= "";
    String year 		= "";
    String measure 		= "";
    String dataGranularity 	= "";
    String noOfWeeks	= "";
    String date			= "";



    /**
     * No of weeks i.e. 3 Weeks, 6 Weeks, 9 Weeks, 12 Weeks.
     * @param noOfWeeks
     */

    public void setNoOfWeeks(String noOfWeeks) {
        this.noOfWeeks = noOfWeeks;
    }

    /**
     * Data granularity is "By Account", "By Instance".
     * @param dataGranularity
     */
    public void setDataGranularity(String dataGranularity) {
        this.dataGranularity = dataGranularity;
    }



    public AdoptionUsagePage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    /**
     * Month value Format is like Apr for April, Jan for January
     * @param month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Year value format 2013, 2012
     * @param year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Measure name which are configurable.
     * @param measure
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    /**
     * Displays month level data.
     * @return this.
     */
    public AdoptionUsagePage displayMonthlyUsageData() {
        Report.logInfo("Displaying Monthly usage Data");
        button.click(PIN_ICON);
        Select select = new Select(driver.findElement(By.xpath(MONTH_SELECT)));
        select.selectByValue(month);
        //field.selectFromDropDown(MONTH_SELECT, month);
        field.selectFromDropDown(YEAR_SELECT, year);
        selectMeasure(measure);
        if(dataGranularity.isEmpty() == false && dataGranularity.length() > 1 ) {
            field.selectFromDropDown(DATAVIEW_SELECT, dataGranularity);
        }
        button.click(GO_BUTTON);
        Report.logInfo("Clicking on to display monthly usage data.");
        return this;
    }

    /**
     * Displays week level data.
     * @return
     */
    public AdoptionUsagePage displayWeeklyUsageData() {
        Report.logInfo("Displaying Weekly usage Data");
        //field.selectFromDropDown(MEASURE_SELECT, measure);
        selectMeasure(measure);
        if(dataGranularity != null && dataGranularity.isEmpty() == false) {
            field.selectFromDropDown(DATAVIEW_SELECT, dataGranularity);
        }
        field.selectFromDropDown(WEEK_PERIOD_SELECT, noOfWeeks);
        if(date != null && !date.equals("")) {
            item.clearAndSetText(WEEKDATE_UPTO_INPUT, date);
        }
        button.click(GO_BUTTON);
        Report.logInfo("Clicked to display weekly usage data.");
        return this;
    }


    private void selectMeasure(String measure) {
        item.click(MEASURE_SELECT);
        wait.waitTillElementDisplayed(MEASURE_DISPLAY_DIV, MIN_TIME, MAX_TIME);
        field.setTextField(MEASURE_SEARCH_INPUT, measure);
        amtDateUtil.stalePause();
        item.click("//ul[@class='gs_mult_results']/li[contains(text(), '"+measure+"')]");
    }

    public boolean isAdoptionGridDisplayed() {
        Report.logInfo("Verifying is Adoption Data grid displayed");
        boolean success = false;
        wait.waitTillElementDisplayed(ADOPTION_GRID, MIN_TIME, MAX_TIME);
        if(isElementPresentAndDisplay(By.id(ADOPTION_GRID))) {
            success = true;
        }
        Report.logInfo("Verified adoption Data grid display & returing the result : "+success);
        return success;
    }

    public boolean isWeeklyDataDisplayed() {
        boolean success = false;
        return success;
    }

    public boolean isMonthlyDataDisplayed() {
        boolean success = false;
        return success;
    }

    public boolean isAccountLevelDataDisplayed() {
        boolean success = false;
        return success;
    }

    public boolean isInstanceLevelDataDisplayed() {
        boolean success = false;
        return success;
    }

    public boolean isCustomerDataPresent(String cName, String values) {
        Report.logInfo("Verifying is data present in grid.");
        int attemptNo = 1;
        boolean status = false;
        setFilter("gs_cl", cName);
        //stalePause();
        while (attemptNo < 4) {
            int customerRownNo = table.getValueInListRow(
                    "adoptionTableList_IdOfJBaraStandardView", cName + " | " +values);
            if (customerRownNo != -1) {
                status = true;
                break;
            }
            sleep(2);
            attemptNo++;
        }
        Report.logInfo("Verified data present in grid & returning result : "+status);
        return status;
    }

    public boolean isMissingDataInfoDisplayed() {
        Report.logInfo("Checking Adoption data missing text is displayed on the screen");
        boolean result = false;
        if(field.isElementPresent(WARNING_MESSAGE)) {
            result = true;
        }
        Report.logInfo("Checked Adoption data missing text is dispalyed & returning : "+result);
        return result;
    }

    public boolean isGridHeaderMapped(String hRowsText) {
        Report.logInfo("Checking the header of the grid for values in-puted.");
        //String hRowsText = "Customer | Instancesasd | Renewal Date";
        boolean result = false;
        List<String> columnHeaders = new ArrayList<String>();
        for(String s : hRowsText.split(" \\|")) {
            columnHeaders.add(s.trim());
        }

        WebElement table = driver.findElement(By.xpath("//table[@class='ui-jqgrid-htable']"));
        List<WebElement> dummyRows = table.findElements(By.tagName("tr"));
        List<WebElement> hRows = new ArrayList<WebElement>();
        for(WebElement row : dummyRows) {
            if(row.getAttribute("role").equalsIgnoreCase("rowheader")) {
                hRows.add(row);
            }
        }
        if(hRows.size() > 0) {
            System.out.println("No of Header Rows :" +hRows.size());
            List<WebElement> cols = null;
            String dyString = null;
            WebElement row =  hRows.get(0);
            cols = row.findElements(By.tagName("th"));
            if(cols.size() > 0) {
                String s = null;
                for(WebElement col : cols) {
                    s = col.getText().trim();
                    dyString += s;
                }
                for(String s1 : columnHeaders) {
                    System.out.println("Header text compared :" +s1);
                    if(dyString.contains(s1)) {
                        result = true;
                    } else {
                        result = false;
                        break;
                    }
                }
            }
        }
        Report.logInfo("Checked the header of grid for values inputed, completed & returing : " +result);
        return result;
    }

    public void clearGirdFilter() {
        wait.waitTillElementDisplayed("gs_cl", MIN_TIME, MAX_TIME);
        field.clearText("gs_cl");
        amtDateUtil.stalePause();
    }

    public boolean isDataPresentInGrid(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        List<String> values = new ArrayList<String>();
        for(String v : s.split("\\|")) {
            values.add(v.trim());
        }
        field.clearAndSetText("gs_cl", values.get(0).toString());
        amtDateUtil.stalePause();
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'adoptionTableList_IdOf')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        int a =0;
        for(WebElement row : rows) {
            ++a;
            if(row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Actual Text : " + row.getText());
                Report.logInfo("Expected Text :" +values.toString());
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
        Report.logInfo("Matched in row : " +a);
        return result;
    }


    public AdoptionAnalyticsPage clickOnViewUsage(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        List<String> values = new ArrayList<String>();
        for(String v : s.split("\\|")) {
            values.add(v.trim());
        }
        field.clearAndSetText("gs_cl", values.get(0).toString());
        amtDateUtil.stalePause();
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'adoptionTableList_IdOf')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        int a =0;
        for(WebElement row : rows) {
            ++a;
            if(row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Row Text : " +row.getText());
            }
            for(String val : values ) {
                System.out.println("Checking String :" +val);
                if(rowtext.contains(val)) {
                    result = true;
                    Report.logInfo("Matched : " +result);
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
        Report.logInfo("Matched in row : " +a);
        item.click("//table[contains(@id, 'adoptionTableList_IdOf')]/descendant::tr[@id='"+a+"']/td/atext()='View'");
        return new AdoptionAnalyticsPage();
    }

    public AdoptionAnalyticsPage navToUsageByCust(String cName, String instance) {
        Report.logInfo("Click on view button to view single customers usage data");
        if(instance != null) {
            item.click("//td[text()='"+instance+"']/preceding-sibling::td[@title='"+cName+"']/preceding-sibling::td/a[text()='View']");
        } else {
            item.click("//td[@title='"+cName+"']/preceding-sibling::td[@title='View']/a[text()='View']");
        }

        Report.logInfo("CLicked on view to view single customers usage data");
        return new AdoptionAnalyticsPage();
    }

    public Customer360Page navigateTo360(String cName) {
        Report.logInfo("Clicking on Customer link to navigate to 360 Page");
        item.click("//td[@title='"+cName+"']/a[text()='"+cName+"']");
        Report.logInfo("Clicked on customer Name & Navigating to 360 Page");
        return new Customer360Page();
    }

    public boolean exportGrid() {
        boolean result = false;
        Report.logInfo("Clicking to export the grid data");
        try  {
            item.click(GRID_EXPORT_LINK);
            wait.waitTillElementDisplayed(EXPORT_SUCCESS_MSG, MIN_TIME, MAX_TIME);
            result = element.getElement(EXPORT_SUCCESS_MSG).isDisplayed();
        } catch (Exception e) {
            Report.logInfo("*** Some Exception Got Created ***");
        }
        return result;
    }
    //To verify weather all field in form are displayed (Weekly configuration).
    public boolean isWeeklyFormEleDisplayed() {
        try  {
            if(item.getElement(MEASURE_SELECT).isDisplayed() && item.getElement(WEEK_PERIOD_SELECT).isDisplayed() &&
                    item.getElement(WEEKDATE_UPTO_INPUT).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Some exception*** " +e.getLocalizedMessage());
            return false;
        }
        return false;
    }
    //To verify weather all field in form are displayed (Monthly configuration).
    public boolean isMonthlyFormEleDisplayed() {
        try  {
            if(item.getElement(MEASURE_SELECT).isDisplayed() && item.getElement(MONTH_SELECT).isDisplayed() &&
                    item.getElement(YEAR_SELECT).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Some exception*** " +e.getLocalizedMessage());
            return false;
        }
        return false;
    }
    //To verify data granularity selection drop-down is displayed.
    public boolean isDataGranularitySelectionDisplayed() {
        if(!item.getElement(DATAVIEW_SELECT).isDisplayed()) {
            return false;
        }
        return true;
    }


    //Need to be implemented keeping this more generic, that fits over entire application grid search.
    public AdoptionUsagePage advancedSearch() {
        button.click(ADVANCEDSEARCH_BUTTON);
        wait.waitTillElementDisplayed(ASEARCH_SEARCH_BUTTON, MIN_TIME, MAX_TIME);
        return this;
    }


    public AdoptionUsagePage selectUIView(String viewName) {
        field.selectFromDropDown(UIVIEW_SELECT, viewName);
        return this;
    }

    public AdoptionUsagePage selectCustomersView(String value) {
        String s = "//div[@class='dummyDDStatusFilterHead ui-corner-all']";
        item.click(s);
        String s1 = "//div[@class='dummyDDFilterList']";
        wait.waitTillElementDisplayed(s1, MIN_TIME, MAX_TIME);
        String s2 = "//div[@class='dummyDDFilterList' and contains(text(), '"+value+"')]";
        item.click(s2);
        return this;
    }



}
