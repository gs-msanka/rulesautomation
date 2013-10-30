package com.gainsight.sfdc.adoption.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pages.Customer360Page;


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
    private final String MEASURE_SELECT 		= "//select[@class='jbaraDummyAdoptionMeasureSelectControl min-width']"; //"//div[@class='newFilters']/div[2]/select";
    private final String GO_BUTTON 				= "//div[@class='newFilters']/div[2]/input[@value='View Results']";
    private final String DATAVIEW_SELECT 		= "//select[@class='jbaraDummyAdoptionDataViewSelectControl min-width']";
    private final String WEEK_PERIOD_SELECT		= "//select[@class='dummyJbaraWeeksPeriodsSelectionCntrl min-width']";
    private final String WEEKDATE_UPTO_INPUT	=	"//input[@class='jbaraAdoptionGridInputField min-width']";
    private final String ADVANCEDSEARCH_BUTTON	=	"//span[@class='dummygrdAdvancedSearch' and @title='Search']";
    private final String ASEARCH_SEARCH_BUTTON 	= "//input[@class='Search' and @type='button']";
    private final String ASEARCH_RESET_BUTTON 	= "//input[@class='Reset' and @type='button']";
    private final String ASEARCH_CLOSE_BUTTON 	= "//input[@class='Close' and @type='button']";
    private final String ASEARCH_CLOSE 			= "//a[@class='ui-dialog-titlebar-close ui-corner-all']/span[text()='close']";

    String month 		= "";
    String year 		= "";
    String measure 		= "";
    String byDataGran 	= "";
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
     * @param byDataGran
     */
    public void setByDataGran(String byDataGran) {
        this.byDataGran = byDataGran;
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
        field.selectFromDropDown(MONTH_SELECT, month);
        field.selectFromDropDown(YEAR_SELECT, year);
        field.setSelectField(MEASURE_SELECT, measure);
        if(byDataGran.isEmpty() == false && byDataGran.length() > 1 ) {
            field.selectFromDropDown(DATAVIEW_SELECT, byDataGran);
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
        field.selectFromDropDown(MEASURE_SELECT, measure);
        if(byDataGran != null && byDataGran.isEmpty() == false) {
            field.selectFromDropDown(DATAVIEW_SELECT, byDataGran);
        }
        field.selectFromDropDown(WEEK_PERIOD_SELECT, noOfWeeks);
        item.clearAndSetText(WEEKDATE_UPTO_INPUT, date);
        button.click(GO_BUTTON);
        Report.logInfo("Clicked to display weekly usage data.");
        return this;
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

    public boolean isDataPresentInGrid(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        //String s = "Test acc 1 | Test acc 1 -SandBox Instance | 19/02/2013 | 0 | 0 | 0% | 5 ";
        List<String> values = new ArrayList<String>();
        for(String v : s.split("\\|")) {
            values.add(v.trim());
        }
        boolean result = false;
        WebElement table = driver.findElement(By.id("adoptionTableList_IdOfJBaraStandardView"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        for(WebElement row : rows) {
            if(row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                System.out.println("Row Text : " +row.getText());
            }
            for(String val : values ) {
                System.out.println("Checking String :" +val);
                if(rowtext.contains(val)) {
                    result = true;
                    System.out.println("Matched : " +result);
                } else {
                    result = false;
                    System.out.println("Matched : " +result);
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

    public AdoptionAnalyticsPage navToUsageByCustIns(String cName, String Instance) {
        Report.logInfo("Click on view button to view single customers usage data");
        item.click("//td[text()='"+Instance+"']/preceding-sibling::td[@title='"+cName+"']/preceding-sibling::td/a[text()='View']");
        Report.logInfo("CLicked on view to view single customers usage data");
        return new AdoptionAnalyticsPage();
    }

    public Customer360Page navigateTo360(String cName) {
        Report.logInfo("Clicking on Customer link to navigate to 360 Page");
        item.click("//td[@title='"+cName+"']/a[text()='"+cName+"']");
        Report.logInfo("Clicked on customer Name & Navigating to 360 Page");
        return new Customer360Page();
    }


    public void applyGridCustStatusFilter(String filter) {
        Report.logInfo("Applying Customer status level filter on adotpion grid.");
        field.click(GRID_CUST_STATUS_IMG);
        wait.waitTillElementDisplayed(GRID_CUST_STATUS_BLOCK, MIN_TIME, MAX_TIME);
        if(filter==null) {
            filter = "All";
        }
        item.click("//div[@class='dummyDDFilterList' and contains(text(), '"+filter+"')]");
        Report.logInfo("Applied Customer status level filter on adotpion grid.");
    }

    public boolean exportGrid() {
        boolean result = false;
        Report.logInfo("Clicking to export the grid data");
        item.click(GRID_EXPORT_LINK);
        wait.waitTillElementDisplayed(EXPORT_SUCCESS_MSG, MIN_TIME, MAX_TIME);
        result = element.getElement(EXPORT_SUCCESS_MSG).isDisplayed();
        Report.logInfo("Clicked on export & returing the result : "+result);
        return result;
    }
    //To verify weather all field in form are displayed (Weekly configuration).
    public boolean isWeeklyFormEleDisplayed() {
        if(!item.getElement(MEASURE_SELECT).isDisplayed()) {
            return false;
        }
        if(!item.getElement(WEEK_PERIOD_SELECT).isDisplayed()) {
            return false;
        }
        if(!item.getElement(WEEKDATE_UPTO_INPUT).isDisplayed()) {
            return false;
        }
        return true;
    }
    //To verify weather all field in form are displayed (Monthly configuration).
    public boolean isMonthlyFormEleDisplayed() {
        if(!item.getElement(MEASURE_SELECT).isDisplayed()) {
            return false;
        }
        if(!item.getElement(MONTH_SELECT).isDisplayed()) {
            return false;
        }
        if(!item.getElement(YEAR_SELECT).isDisplayed()) {
            return false;
        }
        return true;
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


}
