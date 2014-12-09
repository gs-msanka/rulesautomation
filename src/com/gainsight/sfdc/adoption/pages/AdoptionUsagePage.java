package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AdoptionUsagePage extends AdoptionBasePage {
    private final String READY_INDICATOR1         = "//div[@id='aGrid_view1']/center[text()='No views configured']";
    private final String READY_INDICATOR2         = "//div[@class='results-btn' and text()='Go']";

    private final String ADOPTION_GRID            = "//div[contains(@class, 'home-page-slick-grid ui-widget slickgrid')]";
    private final String GO_BUTTON                = "//div[@class='results-btn' and text()='Go']";
    private final String UI_VIEW_SELECT_BUTTON    = "//select[@class='components_list']/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
    private final String MEASURE_SELECT_BUTTON    = "//select[@class='measure']/following-sibling::button";
    private final String PERIOD_SELECT_BUTTON      = "//select[@class='modern-select-box period']/following-sibling::button";
    private final String MONTH_SELECT_BUTTON      = "//select[@class='modern-select-box month']/following-sibling::button";
    private final String YEAR_SELECT_BUTTON       = "//select[@class='year modern-select-box']/following-sibling::button";
    private final String AGG_SELECT_BUTTON        = "//select[@class='adoptionAggregationLevel modern-select-box']/following-sibling::button";
    private final String MORE_BUTTON              = "//div[@class='gs-moreopt-btn']";
    private final String UNCHECK_ALL_MEASURES     = "//a/span[contains(text(), 'Uncheck all')]";
    private final String CHECK_ALL_MEASURES       = "//a[@class='ui-multiselect-all']";
    private final String SEARCH_MEASURE_INPUT     = "//div[@class='ui-multiselect-filter']/input[@type='search']";
    private final String WEEK_DATE_INPUT          = "//input[@class='calendar period']";
    private final String CUSTOMER_NAME_GIRD_FILTER_INPUT = "//div[@class='ui-state-default slick-headerrow-column l0 r0']/input[@type='text']";
    private final String SPARK_LINES_CHECKBOX     = "//div[contains(text(), 'Show Sparklines')]/input[@type='checkbox']";
    private final String FILTER_BUTTON            = "//a[@data-action='FILTER']";
    private final String NO_VIEW_INFO_DIV         = "//div[@id='aGrid_view1']/center[contains(text(), 'No views configured')]";
    private final String NO_DATA_FOUND_DIV        = "//div[@class='jbaraInfoMessageClassMain']/div[@class='noDataFound' and text()='No Data Found']";
    private final String LOADING_ICON             = "//div[contains(@class, 'gs-loader-image')]";

    String uiView           = "";
    String measure          = "";
    String noOfWeeks        = "";
    String noOfMonths       = "";
    String month            = "";
    String year             = "";
    String dataGranularity  = "";
    String weekDate             = "";

    public AdoptionUsagePage() {
        try {
            wait.waitTillElementPresent(UI_VIEW_SELECT_BUTTON, MIN_TIME, MAX_TIME);
        } catch (Exception e) {
            wait.waitTillElementPresent(READY_INDICATOR1, MIN_TIME, MAX_TIME);
        }
    }

    public void setNoOfMonths(String noOfMonths) {
        this.noOfMonths = noOfMonths;
    }

    public void setUiView(String uiView) {
        this.uiView = uiView;
    }

    public void setNoOfWeeks(String noOfWeeks) {
        this.noOfWeeks = noOfWeeks;
    }

    public void setDataGranularity(String dataGranularity) {
        this.dataGranularity = dataGranularity;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setDate(String weekDate) {
        this.weekDate = weekDate;
    }

    public AdoptionUsagePage displayMonthlyUsageData() {

        Report.logInfo("Displaying Monthly usage Data");

        if(measure !=null && measure != "") {
            selectMeasures(measure);
        }

        if(noOfMonths != null && noOfMonths != "") {
            item.click(PERIOD_SELECT_BUTTON);
            selectValueInDropDown(noOfMonths);
            Report.logInfo("No of Months Selected : " +noOfMonths);
        }
        if(month != null && month != "") {

            item.click(MONTH_SELECT_BUTTON);
            selectValueInDropDown(month);
            Report.logInfo("Month Selected : " +month);
        }

        if(year != null && year != "") {
            item.click(YEAR_SELECT_BUTTON);
            selectValueInDropDown(year);
            Report.logInfo("Year Selected : "+year);
        }
        if(dataGranularity != null && dataGranularity != "") {
          item.click(AGG_SELECT_BUTTON);
            selectValueInDropDown(dataGranularity);
            Report.logInfo("Data Granularity Selected : " +dataGranularity);
        }
        try {
            env.setTimeout(1);
            WebElement wle = element.getElement(By.xpath("//div[@class='sparks-check']/input"));
            if(Boolean.valueOf(wle.getAttribute("checked"))) {
                wle.click();
            }
        } catch (Exception e) {
            Report.logInfo("Failed to unCheck/Check spark lines" +e.getLocalizedMessage());
        }
        env.setTimeout(30);
        item.click(GO_BUTTON);
        waitTillNoLoadingIcon();
        return this;
    }


    public AdoptionUsagePage displayWeeklyUsageData() {
        if(measure !=null && measure != "") {
            selectMeasures(measure);
        }
        if(noOfWeeks != null && noOfWeeks != "") {
            item.click(PERIOD_SELECT_BUTTON);
            selectValueInDropDown(noOfWeeks);
        }
        if(weekDate != null && weekDate != "") {
            Report.logInfo("Entering "+weekDate+" in "+WEEK_DATE_INPUT);
            getFirstDisplayedElement(WEEK_DATE_INPUT).clear();
            getFirstDisplayedElement(WEEK_DATE_INPUT).sendKeys(weekDate);

        }
        if(dataGranularity != null && dataGranularity != "") {
            item.click(AGG_SELECT_BUTTON);
            selectValueInDropDown(dataGranularity);
        }
        item.click(GO_BUTTON);
        waitTillNoLoadingIcon();
        return this;
    }

    //Files Downloaded, Page Views.
    private void selectMeasures(String measures) {
        Report.logInfo("Selecting measures : " +measures);
        item.click(MEASURE_SELECT_BUTTON);
        getFirstDisplayedElement(UNCHECK_ALL_MEASURES).click();
        String[] args = measures.split("\\|");
        for(String str : args) {
            getFirstDisplayedElement(SEARCH_MEASURE_INPUT).clear();
            getFirstDisplayedElement(SEARCH_MEASURE_INPUT).sendKeys(str.trim());
            getFirstDisplayedElement("//span[contains(text(), '"+str.trim()+"')]/preceding-sibling::input").click();
        }
    }

    public AdoptionUsagePage selectUIView(String viewName) {
        Report.logInfo("Selecting UI View : "+viewName);
        if(viewName != null && viewName!= "") {
            item.click(UI_VIEW_SELECT_BUTTON);
            selectValueInDropDown(viewName);
            waitTillNoLoadingIcon();
        } else {
            throw new RuntimeException("Please Specify UI-View to select");
        }
        return this;
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
            Report.logInfo("*** Export Failed ***" +e.getMessage());
        }
        return result;
    }

    public boolean gridHasScrollBar() {
        boolean hasScroll = false;
        try {
            hasScroll = element.getElement("//div[@class='slick-pane slick-pane-top slick-pane-right']").isDisplayed();
            Report.logInfo("Grid has scroll bar");
        } catch (Exception e) {
            Report.logInfo("Grid Doesn't have scroll bar");
        }
        return hasScroll;
    }



    public boolean isDataPresentInGrid(String value) {
        boolean result = false;
        String[] values = value.split("\\|");
        setCustomerNameFilter(values[0].trim());
        Report.logInfo("Expected Data : " +value);
        env.setTimeout(2);
        if(gridHasScrollBar()) {
            List<WebElement> leftRows = element.getAllElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-left']/div[contains(@class, 'ui-widget-content slick-row')]");
            for(int i=0; i< leftRows.size(); i++) {
                List<String> actualRowText = new ArrayList<>();
                String actualText = leftRows.get(i).getText();
                if(actualText.contains(values[0])) {
                    actualRowText.add(actualText);
                    WebElement rightRow = element.getElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-right']/div[contains(@class, 'ui-widget-content slick-row')]["+(i+1)+"]");
                    List<WebElement> cells = rightRow.findElements(By.cssSelector("div[class*='slick-cell']"));
                    for(WebElement cell : cells) {
                        actualRowText.add(cell.getText().trim());
                    }
                    Report.logInfo("Actual Data : "+actualRowText);
                    if(actualRowText.containsAll(Arrays.asList(values))) {
                        result = true;
                        break;
                    }
                }
            }
        } else {
            List<WebElement> rows = element.getAllElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-left']/div[contains(@class, 'ui-widget-content slick-row')]");
            for(WebElement row : rows) {
                String rowText = row.getText();
                Report.logInfo("Actual Row Text : "+rowText.replaceAll("\n", ", "));
                List<WebElement> cells = row.findElements(By.cssSelector("div[class*='slick-cell']"));
                List<String> actualRowText = new ArrayList<>();
                for(WebElement cell : cells) {
                    actualRowText.add(cell.getText().trim());
                }
                Report.logInfo("Actual Data : "+actualRowText);
                if(actualRowText.containsAll(Arrays.asList(values))) {
                    result = true;
                    break;
                }
            }
        }
        env.setTimeout(30);
        return result;
    }

    public boolean isAdoptionGridDisplayed() {
        boolean success = false;
        try {
            wait.waitTillElementDisplayed(ADOPTION_GRID, MIN_TIME, MAX_TIME);
            if (isElementPresentAndDisplay(By.xpath(ADOPTION_GRID))) {
                success = true;
            }
        } catch (Exception e ) {
            Report.logInfo(e.getMessage());
            Report.logInfo("Adoption Grid is not displayed");
        }
        return success;
    }

    public Customer360Page navigateTo360(String cName) {
        Report.logInfo("Clicking on Customer link to navigate to 360 Page");
        setCustomerNameFilter(cName);
        item.click("//div[@class='slick-cell l0 r0 slick-customer-format']/a[contains(text(), '"+cName+"')]");
        return new Customer360Page();
    }

    private void setCustomerNameFilter(String cName) {
        Report.logInfo("Filtering By Customer : "+cName);
        field.clearText(CUSTOMER_NAME_GIRD_FILTER_INPUT);
        if(cName !=null) {
            field.setTextField(CUSTOMER_NAME_GIRD_FILTER_INPUT, cName);
            driver.findElement(By.xpath(CUSTOMER_NAME_GIRD_FILTER_INPUT)).sendKeys(Keys.ENTER);
            amtDateUtil.stalePause();
            waitTillNoLoadingIcon();
        } else {
            throw new RuntimeException("Customer name is mandatory");
        }
    }

    //To verify weather all field in form are displayed (Weekly configuration).
    public boolean isWeeklyFormEleDisplayed() {
        try {
         WebElement ele = element.getElement(PERIOD_SELECT_BUTTON);
            if(ele.getText().contains("Week") && element.getElement(WEEK_DATE_INPUT).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("Weekly data selection view is not displayed");
            return false;
        }
        return false;
    }

    //To verify weather all field in form are displayed (Monthly configuration).
    public boolean isMonthlyFormEleDisplayed() {
        try {
            WebElement ele = element.getElement(PERIOD_SELECT_BUTTON);
            if(ele.getText().contains("Month") && element.getElement(MONTH_SELECT_BUTTON).isDisplayed()
                        && element.getElement(YEAR_SELECT_BUTTON).isDisplayed()) {
                return true;
            }
        } catch (Exception e) {
            Report.logInfo("Monthly data selection view is not displayed");
            return false;
        }
        return false;
    }

    //To verify data granularity selection drop-down is displayed.
    public boolean isDataGranularitySelectionDisplayed() {
        if (!item.getElement(AGG_SELECT_BUTTON).isDisplayed()) {
            return false;
        }
        return true;
    }

    //Need to be implemented keeping this more generic, that fits over entire application grid search.
    public AdoptionUsagePage advancedSearch(List<HashMap<String, String>> filtersList) {
        return this;
    }

    public boolean isGridHeaderMapped(String value) {
        Report.logInfo("Checking the header of the grid for values entered.");
        boolean result = false;
        String[] values = value.split("\\|");
        Report.logInfo("Expected Data : " +value);
        env.setTimeout(2);
        List<String> actualHeaderColumns = new ArrayList<>();
        if(gridHasScrollBar()) {
             //Verifying first element i.e. customer.
             if(element.isElementPresent("//div[contains(@class, 'slick-header-columns slick-header-columns-left')]/div[contains(@class, 'slick-header-column')]/span[contains(text(), '"+values[0]+"')]")) {
                for(String s : Arrays.copyOfRange(values, 1, values.length)) {
                    if(!element.isElementPresent("//div[contains(@class, 'slick-header-columns slick-header-columns-right')]/div[contains(@class, 'slick-header-column')]/span[@class='slick-column-name' and contains(text(), '"+s.trim()+"')]")) {
                        return false;
                    }
                }
                result = true;
             }
        } else {
            WebElement header = element.getElement("//div[contains(@class, 'slick-header-columns slick-header-columns-left')]");
            for(WebElement ele : header.findElements(By.tagName("div"))) {
                actualHeaderColumns.add(ele.getText().trim());
            }
            Report.logInfo("Actual Data : " +actualHeaderColumns);
            result = actualHeaderColumns.containsAll(Arrays.asList(values));
        }
        return result;
    }



}
