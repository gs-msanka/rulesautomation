package com.gainsight.sfdc.customer.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.Constants;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class CustomersPage extends CustomerBasePage {
    private final String READY_INDICATOR        = "//div[@class='gs-moreopt-btn']";

    private final String MORE_ICON              = "//div[@class='gs-moreopt-btn']";
    private final String NEW_CUSTOMER_LINK      = "//a[contains(text(), 'New Customer')]";
    private final String ACC_NAME_INPUT         = "//input[@placeholder='Enter customer name' and @name='search_text']";
    private final String AUTO_SELECT_LIST       = "//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']";
    private final String CUSTOMER_SAVE          = "//input[@class='save-customer btn-save' and @value='Save']";
    private final String COMMENTS_INPUT         = "//textarea[@class='commentArea']";
    private final String STAGE_SELECT_BUTTON    = "//select[@class='stageSelection']/following-sibling::button";
    private final String STATUS_SELECT_BUTTON   = "//select[@class='statusSelection']/following-sibling::button";
    private final String CUSTOMER_FORM_CANCEL_BUTTON        = "//input[@value='Cancel' and @class='cancel btn-cancel']";
    private final String CUSTOMER_NAME_GIRD_FILTER_INPUT    = "//div[@class='ui-state-default slick-headerrow-column l1 r1']/input[@type='text']";
    private final String UI_VIEW_BUTTON                     = "//select[@class='components_list']/following-sibling::button[@type='button']";
    private final String TAGS_OPTION                        = "//li[@action='tags']/a[contains(text(), 'Tags')]";
    private final String TAG_APPLY_BUTTON                   = "//input[@value='Apply' and @type='button']";
    private final String TAGS_INPUT                         = "//input[@value='Click here to add tags']";
    private final String TAG_SELECT                         = "//li[contains(@class, 'active-result') and contains(text(), '%s')]";
    private final String TAGS_LIST                          = "//div[@class='chosen-drop']";
    private final String EXPORT_OPTION                      = "//li[@action='export']/a[contains(text(), 'export')]";

    public CustomersPage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public CustomersPage addCustomer(String customerName, String status,
                                     String stage, String comments) {

        item.click(MORE_ICON);
        selectAccount(customerName);
        fillFields(status, stage, comments);
        item.click(CUSTOMER_SAVE);
        Timer.sleep(2);
        waitForLoadingImagesNotPresent();
        return this;
    }

    public CustomersPage applyTags(String customer, String[] tags) {
        item.click(MORE_ICON);
        wait.waitTillElementDisplayed(TAGS_OPTION, MIN_TIME, MAX_TIME);
        item.click(TAGS_OPTION);
        wait.waitTillElementDisplayed(TAGS_INPUT, MIN_TIME, MAX_TIME);
        for(String tag :tags) {
            item.click(TAGS_INPUT);
            wait.waitTillElementDisplayed(TAGS_LIST, MIN_TIME, MAX_TIME);
            item.click(String.format(TAG_SELECT, tag));
        }
        setCustomerNameFilterOnTag(customer);
        item.click(TAG_APPLY_BUTTON);
        waitForLoadingImagesNotPresent();
        return this;
    }

    /**
     * This is used for filtering if tag row is enabled.
     * @param customer
     */

    private void setCustomerNameFilterOnTag(String customer) {
        setCustomerFilter(customer);
        int index = -1;
        if(isScrollExists()) {
            item.click("//a[contains(text(), '" + customer + "')]/parent::div/preceding-sibling::div[contains(@class, 'checkboxsel')]/input");
        } else {
            List<WebElement> gridRows = element.getAllElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-right']/div[contains(@class, 'ui-widget-content slick-row')]");
            for(WebElement row : gridRows) {
                try {
                    element.getElement(row, "//a[contains(@href, 'customersuccess360') and text()='"+customer+"']").isDisplayed();
                    index++;
                    break;
                } catch (NoSuchElementException e) {
                    Log.error("Just ignore...");
                }
            }
            if(index != -1) {
                item.click("//div[@class='grid-canvas grid-canvas-top grid-canvas-left']/div["+index+1+"]/div/input");
            } else {
                throw new RuntimeException("Failed to check customer for applying tag.");
            }
        }
    }

    /**
     * Checks the customer search input box in slick grid has this is the only way to find if the grid has scroll or not.
     * @return
     */
    private boolean isScrollExists() {
        String CUSTOMER_SEARCH_DIV = "//div[@class='slick-headerrow-columns slick-headerrow-columns-left']/div[@gridcolid='Customer']";
        return isElementPresentAndDisplay(By.xpath(CUSTOMER_SEARCH_DIV));
    }

    private void setCustomerFilter(String custName) {
        String CUSTOMER_SEARCH_DIV;
        if(isScrollExists()) {
            CUSTOMER_SEARCH_DIV = "//div[@class='slick-headerrow-columns slick-headerrow-columns-left']/div[@gridcolid='Customer']";
        } else {
            CUSTOMER_SEARCH_DIV = "//div[@class='slick-headerrow-columns slick-headerrow-columns-right']/div[@gridcolid='Customer']";
        }
        item.clearAndSetText(CUSTOMER_SEARCH_DIV+"/input[@class='filter_input']", custName);
        driver.findElement(By.xpath(CUSTOMER_SEARCH_DIV+"/input[@class='filter_input']")).sendKeys(Keys.ENTER);
    }

    private void selectAccount(String customerName) {
        wait.waitTillElementDisplayed(NEW_CUSTOMER_LINK, MIN_TIME, MAX_TIME);
        item.click(NEW_CUSTOMER_LINK);
        wait.waitTillElementDisplayed(ACC_NAME_INPUT, MIN_TIME, MAX_TIME);
        driver.findElement(By.xpath(ACC_NAME_INPUT)).sendKeys(customerName);
        driver.findElement(By.xpath(ACC_NAME_INPUT)).sendKeys(Keys.ENTER);
        driver.findElement(By.xpath(ACC_NAME_INPUT)).sendKeys(Keys.ARROW_DOWN);
        wait.waitTillElementDisplayed(AUTO_SELECT_LIST, MIN_TIME, MAX_TIME);

        List<WebElement> eleList = element.getAllElement("//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']/li/a[contains(text(), '"+customerName+"')]");

        boolean customerSelected = false;
        for(WebElement ele : eleList) {
            if(ele.isDisplayed()) {
                ele.click();
                customerSelected = true;
            }
        }
        if(!customerSelected) throw new RuntimeException("Unable to select customer (or) customer not found" );
    }

    public void selectValueInDropDown(String value) {
        wait.waitTillElementDisplayed("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]", MIN_TIME, MAX_TIME);
        item.click("//input[contains(@title, '"+value+"')]/following-sibling::span[contains(text(), '"+value+"')]");
    }

    private void fillFields(String status, String stage, String comments) {
        if(stage !=null && stage !="") {
            item.click(STAGE_SELECT_BUTTON);
            selectValueInDropDown(stage);
        }

        if(status !=null && status !="") {
            item.click(STATUS_SELECT_BUTTON);
            selectValueInDropDown(status);
        }

        if(comments != null && comments !="") {
            field.clearAndSetText(COMMENTS_INPUT, comments);
        }
    }

    private WebElement getCustomerRow(String customerName, String values) {
        setCustomerFilter(customerName);
        WebElement ele = null;
        try{
            ele = element.getElement("//a[contains(text(), '"+customerName+"')]/parent::div/parent::div[contains(@class, 'ui-widget-content slick-row')]");
        } catch (RuntimeException e) {
            Log.info(e.getMessage());
        }
        return ele;
    }

    public CustomersPage selectUIView(String uiViewName) {
        item.click(UI_VIEW_BUTTON);
        selectValueInDropDown(uiViewName);
        waitForLoadingImagesNotPresent();
        return this;
    }

    public CustomersPage exportGrid() {
        item.click(MORE_ICON);
        wait.waitTillElementDisplayed(EXPORT_OPTION, MIN_TIME, MAX_TIME);
        item.click(EXPORT_OPTION);
        return this;
    }



    public CustomersPage editCustomer(String customerName, String status,
                                      String stage, String comments) {
        WebElement ele = getCustomerRow(customerName, null);

        if(ele!=null) {
            ele.findElement(By.cssSelector("div>a[data-action='EDIT']")).click();
            wait.waitTillElementDisplayed(ACC_NAME_INPUT, MIN_TIME, MAX_TIME);
            fillFields(status,stage,comments);
            item.click(CUSTOMER_SAVE);
            waitForLoadingImagesNotPresent();
        } else {
            throw new RuntimeException("Customer not found");
        }
        return this;
    }

    public boolean deleteCustomer(String customerName) {
        boolean status = false;
        WebElement ele = getCustomerRow(customerName, null);
        if(ele!=null) {
            ele.findElement(By.cssSelector("div>a[data-action='DELETE']")).click();
            modal.accept();
        } else {
            throw new RuntimeException("Customer not found");
        }
        waitForLoadingImagesNotPresent();
        return status;
    }

    public List<List<String>> getGridData() {
        List<List<String>>  tableData = new ArrayList<>();
        env.setTimeout(1);
        if(isScrollExists()) {
            String left = "//div[@class='grid-canvas grid-canvas-top grid-canvas-left']/div[contains(@class, 'ui-widget-content slick-row')]";
            List<WebElement> leftNav = element.getAllElement(left);
            for (WebElement leftEle : leftNav) {
                List<String> rowData = new ArrayList<>();
                String customerName = leftEle.findElement(By.cssSelector("a[href^='customersuccess360']")).getText();
                Log.info(customerName);
                rowData.add(customerName);
                tableData.add(rowData);
            }
        }

        String right = "//div[@class='grid-canvas grid-canvas-top grid-canvas-right']/div[contains(@class, 'ui-widget-content slick-row')]";
        List<WebElement> rightNav = element.getAllElement(right);
        int i=0;
        for(WebElement rightEle: rightNav) {
            List<WebElement> gridCells= rightEle.findElements(By.tagName("div"));
            for(WebElement gridCell: gridCells) {
                if(tableData.size()-1 != i) {
                    tableData.add(new ArrayList<String>());
                }
                List<String> rowData = tableData.get(i);
                String cellValue = gridCell.getText();
                Log.info("Cell Value :" +cellValue);
                rowData.add(cellValue);
            }
            i++;
        }
        env.setTimeout(30);
        return tableData;
    }

    public boolean isDataPresent(List<List<String>> gridData, String[] data) {
        for(List<String> rowData :  gridData) {
            if(rowData.containsAll(Arrays.asList(data))) {
                return true;
            }
        }
        Log.error("Data is not matched.");
        return false;
    }

    public void setCustomerNameFilter(String customerName) {
        field.clearText(CUSTOMER_NAME_GIRD_FILTER_INPUT);
        if(customerName !=null) {
            field.setTextField(CUSTOMER_NAME_GIRD_FILTER_INPUT, customerName);
            driver.findElement(By.xpath(CUSTOMER_NAME_GIRD_FILTER_INPUT)).sendKeys(Keys.ENTER);
            Timer.sleep(1);  //Grid need to be filtered.
        }
    }



}
