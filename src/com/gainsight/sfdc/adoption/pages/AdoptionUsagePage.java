package com.gainsight.sfdc.adoption.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;


public class AdoptionUsagePage extends AdoptionBasePage {
    private final String READY_INDICATOR1         = "//div[@id='aGrid_view1']/center[text()='No views configured']";
    private final String READY_INDICATOR2         = "//div[@class='results-btn' and text()='Go']";

    private final String ADOPTION_GRID            = "//div[contains(@class, 'home-page-slick-grid ui-widget slickgrid')]";
    private final String GO_BUTTON                = "//div[@class='results-btn' and text()='Go']";
    private final String UI_VIEW_SELECT_BUTTON    = "//select[@class='components_list']/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
    private final String MEASURE_SELECT_BUTTON    = "//select[@class='measure']/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
    private final String PERIOD_SELECT_BUTTON      = "//select[@class='modern-select-box period']/following-sibling::button";
    private final String MONTH_SELECT_BUTTON      = "//select[@class='modern-select-box month']/following-sibling::button";
    private final String YEAR_SELECT_BUTTON       = "//select[@class='year modern-select-box']/following-sibling::button";
    private final String AGG_SELECT_BUTTON        = "//select[@class='adoptionAggregationLevel modern-select-box']/following-sibling::button";
    private final String MORE_BUTTON              = "//div[@class='gs-moreopt-btn']";
    private final String UNCHECK_ALL_MEASURES     = "//a[@class='ui-multiselect-none']/span[contains(text(), 'Uncheck all')]";
    private final String SEARCH_MEASURE_INPUT     = "//div[@class='ui-multiselect-filter']/input[@type='search']";
    private final String WEEK_DATE_INPUT          = "calendar_view4";
    private final String CUSTOMER_NAME_GIRD_FILTER_INPUT = "//div[@class='ui-state-default slick-headerrow-column l0 r0']/input[@type='text']";
    private final String SPARK_LINES_CHECKBOX     = "//div[contains(text(), 'Show Sparklines')]/input[@type='checkbox']";
    private final String FILTER_BUTTON            = "//a[@data-action='FILTER']";
    private final String NO_VIEW_INFO_DIV         = "//div[@id='aGrid_view1']/center[contains(text(), 'No views configured')]";
    private final String NO_DATA_FOUND_DIV        = "//div[@class='jbaraInfoMessageClassMain']/div[@class='noDataFound' and text()='No Data Found']";

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

    public void setDate(String weekDate) {
        this.weekDate = weekDate;
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
            item.click(PERIOD_SELECT_BUTTON);
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
        amtDateUtil.stalePause();
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
            field.clearAndSetText(WEEK_DATE_INPUT, weekDate);
        }
        if(dataGranularity != null && dataGranularity != "") {
            item.click(AGG_SELECT_BUTTON);
            selectValueInDropDown(dataGranularity);
        }
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
        try {
            if(viewName != null && viewName!= "") {
                item.click(UI_VIEW_SELECT_BUTTON);
                selectValueInDropDown(viewName);
            } else {
                throw new RuntimeException("Please Specify UI-View to select");
            }
        } catch (Exception e) {
            throw new RuntimeException("UI-View is not found : "+viewName);
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
            Report.logInfo("*** Export Failed ***" +e.getMessage());
        }
        return result;
    }

    public boolean isDataPresentInGrid(String values) {
        amtDateUtil.sleep(5);
        boolean result = false;


        String[] cellValues = values.split("\\|");
        setCustomerNameFilter(cellValues[0]);
        WebElement ele = element.getElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-left']");

        List<WebElement> rows = ele.findElements(By.cssSelector("div[class*='ui-widget-content slick-row']"));
        System.out.println("Rows :" +rows.size());


        for(WebElement row : rows) {
            boolean inRowData = true;
            List<WebElement> cells = row.findElements(By.cssSelector("div[class^='slick-cell']"));
            int i=1;
            outerloop:
            for(String val : cellValues) {
                //i=1;
                boolean valTemp  =false;
                for(WebElement cell : cells) {
                    if(i==1) {
                        ++i;
                        Report.logInfo(cell.getText());
                        Report.logInfo(String.valueOf(cell.getText().contains(val.trim())));
                        if(cell.getText().contains(val.trim())) { valTemp=true; break;}
                        if(cell.getText().contains(val.trim())) { break outerloop;}
                    } else {
                        Report.logInfo(val);
                        Report.logInfo(cell.getText());
                        if(cell.getText().contains(val.trim())) {
                            valTemp = true;
                            Report.logInfo("Value is found in cell");
                            break;
                        }
                    }
                }
                if(!valTemp) {
                    inRowData = false;
                    break;
                }
            }
            if(inRowData) {
                result = true;
            }
            if(result) {
                break;
            }
        }
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

    public Customer360Page navigateTo360(String custName) {
        Report.logInfo("Clicking on Customer link to navigate to 360 Page");
        setCustomerNameFilter(custName);
        item.click("//div[@class='slick-cell l0 r0 slick-customer-format']/a[contains(text(), '"+custName+"')]");
        return new Customer360Page();
    }

    private void setCustomerNameFilter(String custName) {
        field.clearText(CUSTOMER_NAME_GIRD_FILTER_INPUT);
        amtDateUtil.stalePause();
        if(custName !=null) {
            field.setTextField(CUSTOMER_NAME_GIRD_FILTER_INPUT, custName);
            amtDateUtil.stalePause();
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

    public boolean isGridHeaderMapped(String hRowsText) {
        Report.logInfo("Checking the header of the grid for values in-puted.");
        //String hRowsText = "Customer | InstanceID | Renewal Date";
        boolean result = false;
        return result;
    }


}
