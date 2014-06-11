package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class AdoptionUsagePage extends AdoptionBasePage {



    private final String EXPORT_SUCCESS_MSG = "exportApexMsgId";
    private final String GRID_EXPORT_LINK = "//div[@id='AdoptionUsageGridNavigationBar']/div/a[@title='export']";
    private final String GRID_CUST_STATUS_BLOCK = "//div[@class='dummyDDStatusFilterList ui-corner-bottom']";
    private final String GRID_CUST_STATUS_IMG = "//img[@class='jbarahideListButton dummyHideChurnAnalyticIcon' and @title='Select to filter']";
    private final String WARNING_MESSAGE = "//div[@class='additionalCountSection']";
    private final String ADOPTION_GRID = "containerAdoptionTableList";
    //private final String READY_INDICATOR = "//div[@id='Adoption-Usage']";
    private final String PIN_ICON = "//img[@id='pinIcon']";
    private final String MONTH_SELECT = "//div[@class='JbaraMonthlyFilter hideForOldAdoption']/select";
    private final String YEAR_SELECT = "//div[@class='JbaraMonthlyFilter changeMyFloat']/select";
    //private final String MEASURE_SELECT 		= "//select[@class='jbaraDummyAdoptionMeasureSelectControl min-width']";
    //private final String GO_BUTTON = "//div[@class='newFilters']/div[2]/input[@value='View Results']";
    private final String DATAVIEW_SELECT = "//select[@class='jbaraDummyAdoptionDataViewSelectControl min-width']";
    private final String WEEK_PERIOD_SELECT = "//select[@class='dummyJbaraWeeksPeriodsSelectionCntrl min-width']";
    private final String WEEKDATE_UPTO_INPUT = "//input[@class='jbaraAdoptionGridInputField min-width']";
    private final String ADVANCEDSEARCH_BUTTON = "//span[@class='dummygrdAdvancedSearch' and @title='Search']";
    private final String ASEARCH_SEARCH_BUTTON = "//input[@class='Search' and @type='button']";
    private final String ASEARCH_RESET_BUTTON = "//input[@class='Reset' and @type='button']";
    private final String ASEARCH_CLOSE_BUTTON = "//input[@class='Close' and @type='button']";
    private final String ASEARCH_CLOSE = "//a[@class='ui-dialog-titlebar-close ui-corner-all']/span[text()='close']";
    private final String UIVIEW_SELECT = "//select[@class='jbaraDummyAdoptionUIViewsSelectControl']";
    private final String MEASURE_SELECT = "//div[@class='newMeasureSelector']";
    private final String MEASURE_DISPLAY_DIV = "//div[@class='gs_mult_drop hide']";
    private final String MEASURE_SEARCH_INPUT = "//input[@class='singleSearchMeasureText']";


    String READY_INDICATOR1         = "//div[@id='aGrid_view1']/center[text()='No views configured']";
    String READY_INDICATOR2         = "//div[@class='results-btn' and text()='Go']";
    String GO_BUTTON                = "//div[@class='results-btn' and text()='Go']";
    String NO_VIEW_INFO_DIV         = "//div[@id='aGrid_view1']/center[contains(text(), 'No views configured')]";
    String UI_VIEW_SELECT_BUTTON    = "//select[@class='components_list']/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
    String MEASURE_SELECT_BUTTON    = "//select[@class='measure']/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
    String MONTH_PERIOD_BUTTON      = "//select[@class='modern-select-box period']/following-sibling::button";
    String MONTH_SELECT_BUTTON      = "//select[@class='modern-select-box month']/following-sibling::button";
    String YEAR_SELECT_BUTTON       = "//select[@class='year modern-select-box']/following-sibling::button";
    String AGG_SELECT_BUTTON        = "//select[@class='adoptionAggregationLevel modern-select-box']/following-sibling::button";
    String SPARK_LINES_CHECKBOX     = "//div[contains(text(), 'Show Sparklines')]/input[@type='checkbox']";
    String FILTER_BUTTON            = "//a[@data-action='FILTER']";
    String MORE_BUTTON              = "//div[@class='gs-moreopt-btn']";
    String NO_DATA_FOUND_DIV        = "//div[@class='jbaraInfoMessageClassMain']/div[@class='noDataFound' and text()='No Data Found']";

    String DROP_DOWNS_DIV ="//div[@class='ui-multiselect-menu ui-widget ui-widget-content ui-corner-all ui-multiselect-single']";

    String UNCHECK_ALL_MEASURES = "//a[@class='ui-multiselect-none']/span[contains(text(), 'Uncheck all')]";
    String SEARCH_MEASURE_INPUT = "//div[@class='ui-multiselect-filter']/input[@type='search']";



    String uiView           = "";
    String measure          = "";
    String noOfWeeks        = "";
    String noOfMonths       = "";
    String month            = "";
    String year             = "";
    String dataGranularity  = "";
    String date             = "";

    public AdoptionUsagePage() {
        try {
            wait.waitTillElementPresent(READY_INDICATOR2, MIN_TIME, MAX_TIME);
        } catch (ElementNotFoundException e) {
            wait.waitTillElementPresent(READY_INDICATOR1, MIN_TIME, MAX_TIME);
        }
    }

    public void setNoOfMonths(String noOfMonths) {
        this.noOfMonths = noOfMonths;
    }

    public void setUiView(String uiView) {
        this.uiView = uiView;
    }
    /**
     * No of weeks i.e. 3 Weeks, 6 Weeks, 9 Weeks, 12 Weeks.
     *
     * @param noOfWeeks
     */

    public void setNoOfWeeks(String noOfWeeks) {
        this.noOfWeeks = noOfWeeks;
    }

    /**
     * Data granularity is "By Account", "By Instance".
     *
     * @param dataGranularity
     */
    public void setDataGranularity(String dataGranularity) {
        this.dataGranularity = dataGranularity;
    }

    /**
     * Month value Format is like Apr for April, Jan for January
     *
     * @param month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Year value format 2013, 2012
     *
     * @param year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Measure name which are configurable.
     *
     * @param measure
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private void selectValueInDropDown(String value) {
        wait.waitTillElementDisplayed("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]", MIN_TIME, MAX_TIME);
        item.click("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]");
    }

    /**
     * Displays month level data.
     * @return this.
     */
    public AdoptionUsagePage displayMonthlyUsageData() {

        Report.logInfo("Displaying Monthly usage Data");

        if(measure !=null && measure != "") {
            selectMeasures(measure);
        }

        if(noOfMonths != null && noOfMonths != "") {
            item.click(MONTH_PERIOD_BUTTON);
            selectValueInDropDown(noOfMonths);
        }
        if(month != null && month != "") {
            item.click(MONTH_SELECT_BUTTON);
            selectValueInDropDown(month);
        }
        if(year != null && year != "") {
            item.click(YEAR_SELECT_BUTTON);
            selectValueInDropDown(year);
        }
        if(dataGranularity != null && dataGranularity != "") {
          item.click(AGG_SELECT_BUTTON);
            selectValueInDropDown(dataGranularity);
        }

        WebElement wle = element.getElement(By.xpath("//div[@class='sparks-check']/input"));
        Report.logInfo(wle.getCssValue("value"));
        Report.logInfo(wle.getCssValue("type"));
        Report.logInfo(wle.getCssValue("checked"));
        Report.logInfo(wle.getCssValue("style"));
        Report.logInfo(wle.getAttribute("value"));
        Report.logInfo(wle.getAttribute("type"));
        Report.logInfo(wle.getAttribute("checked"));
        Report.logInfo(wle.getAttribute("style"));
        item.click(GO_BUTTON);

        return this;
    }

    //Files Downloaded, Page Views.
    private void selectMeasures(String mesaures) {
        item.click(MEASURE_SELECT_BUTTON);
        wait.waitTillElementDisplayed(UNCHECK_ALL_MEASURES, MIN_TIME,MAX_TIME);
        item.click(UNCHECK_ALL_MEASURES);
        String[] args = mesaures.split(",");
        for(String str : args) {
            field.clearText(SEARCH_MEASURE_INPUT);
            amtDateUtil.stalePause();
            field.setTextField(SEARCH_MEASURE_INPUT, str);
            amtDateUtil.stalePause();
            item.click("//span[contains(text(), '"+str+"')]/preceding-sibling::input[@title='"+str+"']");
        }
    }

    public AdoptionUsagePage selectUIView(String viewName) {
        if(viewName != null && viewName!= "") {
            item.click(UI_VIEW_SELECT_BUTTON);
            selectValueInDropDown(viewName);
        }
        return new AdoptionUsagePage();
    }

    public boolean exportGrid() {
        //Your request to export data is added to the queue and you will receive an email notification upon completion.
        boolean result = false;
        Report.logInfo("Clicking to export the grid data");
        try {
            item.click(MORE_BUTTON);
            String EXPORT_LINK = "//li[@action='export']/a[contains(text(), 'Export')]";
            String EXPORT_MSG_ELE = "//div[@class='message infoM3 exportDisplayMessage']/span[@class='exportMsg']";
            wait.waitTillElementDisplayed(EXPORT_LINK, MIN_TIME, MAX_TIME);
            item.click(EXPORT_LINK);
            wait.waitTillElementDisplayed(EXPORT_MSG_ELE, MIN_TIME, MAX_TIME);
            String exportMsg = element.getElement(EXPORT_MSG_ELE).getText();
            if(exportMsg.contains("Your request to export data is added to the queue and you will receive an email notification upon completion")) {
                result = true;
            }
        } catch (Exception e) {
            Report.logInfo("*** Export Failed ***");
        }
        return result;
    }











    /////////////////////////////











    /////////////////////////////
    /**
     * Displays week level data.
     *
     * @return
     */
    public AdoptionUsagePage displayWeeklyUsageData() {
        Report.logInfo("Displaying Weekly usage Data");
        //field.selectFromDropDown(MEASURE_SELECT, measure);
        selectMeasure(measure);
        if (dataGranularity != null && dataGranularity.isEmpty() == false) {
            field.selectFromDropDown(DATAVIEW_SELECT, dataGranularity);
        }
        field.selectFromDropDown(WEEK_PERIOD_SELECT, noOfWeeks);
        if (date != null && !date.equals("")) {
            item.clearAndSetText(WEEKDATE_UPTO_INPUT, date);
        }
        button.click(GO_BUTTON);
        Report.logInfo("Clicked to display weekly usage data.");
        return this;
    }



    public boolean isAdoptionGridDisplayed() {
        Report.logInfo("Verifying is Adoption Data grid displayed");
        boolean success = false;
        wait.waitTillElementDisplayed(ADOPTION_GRID, MIN_TIME, MAX_TIME);
        if (isElementPresentAndDisplay(By.id(ADOPTION_GRID))) {
            success = true;
        }
        Report.logInfo("Verified adoption Data grid display & returing the result : " + success);
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
                    "adoptionTableList_IdOfJBaraStandardView", cName + " | " + values);
            if (customerRownNo != -1) {
                status = true;
                break;
            }
            sleep(2);
            attemptNo++;
        }
        Report.logInfo("Verified data present in grid & returning result : " + status);
        return status;
    }

    public boolean isMissingDataInfoDisplayed() {
        Report.logInfo("Checking Adoption data missing text is displayed on the screen");
        boolean result = false;
        if (field.isElementPresent(WARNING_MESSAGE)) {
            result = true;
        }
        Report.logInfo("Checked Adoption data missing text is dispalyed & returning : " + result);
        return result;
    }

    public boolean isGridHeaderMapped(String hRowsText) {
        Report.logInfo("Checking the header of the grid for values in-puted.");
        //String hRowsText = "Customer | Instancesasd | Renewal Date";
        boolean result = false;
        List<String> columnHeaders = new ArrayList<String>();
        for (String s : hRowsText.split(" \\|")) {
            columnHeaders.add(s.trim());
        }

        WebElement table = driver.findElement(By.xpath("//table[@class='ui-jqgrid-htable']"));
        List<WebElement> dummyRows = table.findElements(By.tagName("tr"));
        List<WebElement> hRows = new ArrayList<WebElement>();
        for (WebElement row : dummyRows) {
            if (row.getAttribute("role").equalsIgnoreCase("rowheader")) {
                hRows.add(row);
            }
        }
        if (hRows.size() > 0) {
            System.out.println("No of Header Rows :" + hRows.size());
            List<WebElement> cols = null;
            String dyString = null;
            WebElement row = hRows.get(0);
            cols = row.findElements(By.tagName("th"));
            if (cols.size() > 0) {
                String s = null;
                for (WebElement col : cols) {
                    s = col.getText().trim();
                    dyString += s;
                }
                for (String s1 : columnHeaders) {
                    System.out.println("Header text compared :" + s1);
                    if (dyString.contains(s1)) {
                        result = true;
                    } else {
                        result = false;
                        break;
                    }
                }
            }
        }
        Report.logInfo("Checked the header of grid for values inputed, completed & returing : " + result);
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
        for (String v : s.split("\\|")) {
            values.add(v.trim());
        }
        field.clearAndSetText("gs_cl", values.get(0).toString());
        amtDateUtil.stalePause();
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'adoptionTableList_IdOf')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        int a = 0;
        for (WebElement row : rows) {
            ++a;
            if (row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Actual Text : " + row.getText());
                Report.logInfo("Expected Text :" + values.toString());
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
        Report.logInfo("Matched in row : " + a);
        return result;
    }


    public AdoptionAnalyticsPage clickOnViewUsage(String s) {
        Report.logInfo("Checking Weather data is displayed in the grid");
        List<String> values = new ArrayList<String>();
        for (String v : s.split("\\|")) {
            values.add(v.trim());
        }
        field.clearAndSetText("gs_cl", values.get(0).toString());
        amtDateUtil.stalePause();
        boolean result = false;
        WebElement table = element.getElement("//table[contains(@id,'adoptionTableList_IdOf')]");
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int index = 0;
        String rowtext = null;
        int a = 0;
        for (WebElement row : rows) {
            ++a;
            if (row.getAttribute("role").equalsIgnoreCase("row")) {
                rowtext = row.getText();
                Report.logInfo("Row Text : " + row.getText());
            }
            for (String val : values) {
                System.out.println("Checking String :" + val);
                if (rowtext.contains(val)) {
                    result = true;
                    Report.logInfo("Matched : " + result);
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
        Report.logInfo("Matched in row : " + a);
        item.click("//table[contains(@id, 'adoptionTableList_IdOf')]/descendant::tr[@id='" + a + "']/td/atext()='View'");
        return new AdoptionAnalyticsPage();
    }

    public AdoptionAnalyticsPage navToUsageByCust(String cName, String instance) {
        Report.logInfo("Click on view button to view single customers usage data");
        if (instance != null) {
            item.click("//td[text()='" + instance + "']/preceding-sibling::td[@title='" + cName + "']/preceding-sibling::td/a[text()='View']");
        } else {
            item.click("//td[@title='" + cName + "']/preceding-sibling::td[@title='View']/a[text()='View']");
        }

        Report.logInfo("CLicked on view to view single customers usage data");
        return new AdoptionAnalyticsPage();
    }

    public Customer360Page navigateTo360(String cName) {
        Report.logInfo("Clicking on Customer link to navigate to 360 Page");
        item.click("//td[@title='" + cName + "']/a[text()='" + cName + "']");
        Report.logInfo("Clicked on customer Name & Navigating to 360 Page");
        return new Customer360Page();
    }



    //To verify weather all field in form are displayed (Weekly configuration).
    public boolean isWeeklyFormEleDisplayed() {
        try {
            if (item.getElement(MEASURE_SELECT).isDisplayed() && item.getElement(WEEK_PERIOD_SELECT).isDisplayed() &&
                    item.getElement(WEEKDATE_UPTO_INPUT).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Some exception*** " + e.getLocalizedMessage());
            return false;
        }
        return false;
    }

    //To verify weather all field in form are displayed (Monthly configuration).
    public boolean isMonthlyFormEleDisplayed() {
        try {
            if (item.getElement(MEASURE_SELECT).isDisplayed() && item.getElement(MONTH_SELECT).isDisplayed() &&
                    item.getElement(YEAR_SELECT).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("***Some exception*** " + e.getLocalizedMessage());
            return false;
        }
        return false;
    }

    //To verify data granularity selection drop-down is displayed.
    public boolean isDataGranularitySelectionDisplayed() {
        if (!item.getElement(DATAVIEW_SELECT).isDisplayed()) {
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




    public AdoptionUsagePage selectCustomersView(String value) {
        String s = "//div[@class='dummyDDStatusFilterHead ui-corner-all']";
        item.click(s);
        String s1 = "//div[@class='dummyDDFilterList']";
        wait.waitTillElementDisplayed(s1, MIN_TIME, MAX_TIME);
        String s2 = "//div[@class='dummyDDFilterList' and contains(text(), '" + value + "')]";
        item.click(s2);
        return this;
    }


    private void selectMeasure(String measure) {
        item.click(MEASURE_SELECT);
        wait.waitTillElementDisplayed(MEASURE_DISPLAY_DIV, MIN_TIME, MAX_TIME);
        field.setTextField(MEASURE_SEARCH_INPUT, measure);
        amtDateUtil.stalePause();
        item.click("//ul[@class='gs_mult_results']/li[contains(text(), '" + measure + "')]");
    }


}
