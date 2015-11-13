package com.gainsight.sfdc.customer.pages;

import java.util.List;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.pages.Constants;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
            Timer.sleep(Constants.STALE_PAUSE);     //Require this as this testcase is failing quite often.
        }
        setCustomerNameFilterOnTag(customer);
        item.click(TAG_APPLY_BUTTON);
        Timer.sleep(2);
        waitForLoadingImagesNotPresent();
        return this;
    }

    /**
     * This is used for filtering if tag row is enabled.
     * @param customer
     */

    private void setCustomerNameFilterOnTag(String customer) {
        field.clearAndSetText("//div[@class='ui-state-default slick-headerrow-column l2 r2']/input[@type='text']", customer);
        driver.findElement(By.xpath("//div[@class='ui-state-default slick-headerrow-column l2 r2']/input[@type='text']")).sendKeys(Keys.ENTER);
        Timer.sleep(2);
        item.click("//a[contains(text(), '"+customer+"')]/parent::div/preceding-sibling::div[contains(@class, 'checkboxsel')]/input");
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
            field.setTextField(COMMENTS_INPUT, comments);
        }
    }

    private void setCustomerFilter(String custName) {
        field.clearAndSetText("//div[@class='ui-state-default slick-headerrow-column l1 r1']/input[@class='filter_input']", custName);
        Timer.sleep(2);
    }

    private int getNoOfCustomersRecords(String custName) {
        Timer.sleep(2);
        setCustomerFilter(custName);
        int recordCount = element.getElementCount("//div[@class='slick-cell l1 r1 slick-customer-format']/a[contains(text(), '"+custName+"')]");
        return recordCount;
    }

    public boolean isCustomerPresent(String customerName) {
        Timer.sleep(2);
        if(getNoOfCustomersRecords(customerName)>0) {
            return true;
        }
        return false;
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
            Timer.sleep(2);
            modal.accept();
            Timer.sleep(2);
            try {
                modal.accept();
                Log.info("Modal dialog present ,Customer can't be deleted");
            } catch (Exception e) { //need to change it to exact exception type
                Log.info("Modal dialog not present ,Customer can be deleted");
                status = true;
            }
        } else {
            throw new RuntimeException("Customer not found");
        }
        waitForLoadingImagesNotPresent();
        return status;
    }

    public boolean isDataPresentInGrid(String values) {
        Log.info("Data to Verify : " +values);
        boolean result = false;
        String[] cellValues = values.split("\\|");
        setCustomerNameFilter(cellValues[0].trim());
        WebElement ele = element.getElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-left']");
        List<WebElement> rows = ele.findElements(By.cssSelector("div[class*='ui-widget-content slick-row']"));
        Log.info("Rows :" +rows.size());
        int a=1;  boolean hasScroll = false;
        for(WebElement row : rows) {
            Log.info("Checking Row : " +row.getText());
            boolean inRowData = true;
            WebElement rightRow= null;
            try {
                element.getElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-right']");
                rightRow = element.getElement("//div[@class='grid-canvas grid-canvas-top grid-canvas-right']/div[contains(@class,'ui-widget-content slick-row')]["+a+"]");
                hasScroll = true;
                Log.info("Checking Row : "+rightRow.getText());
                Log.info("Grid has scroll bar");
            } catch (Exception e) {
                Log.info("Grid Doesn't have scroll bar");
            }
            List<WebElement> cells = null;
            if(hasScroll) {
                cells = rightRow.findElements(By.cssSelector("div[class*='slick-cell']"));
            } else {
                cells = row.findElements(By.cssSelector("div[class*='slick-cell']"));
            }

            Log.info("No of Cells :" +cells.size());
            int i=1;
            outerloop:
            for(String val : cellValues) {
                //i=1;
                boolean valTemp  =false;
                for(WebElement cell : cells) {
                    if(i==1) {
                        ++i;
                        if(!hasScroll) {
                            Log.info(cell.getText());
                            Log.info(String.valueOf(cell.getText().contains(val.trim())));
                            if(cell.getText().contains(val.trim())) { valTemp=true; break;}
                            if(cell.getText().contains(val.trim())) { break outerloop;}
                        } else {
                            if(row.getText().contains(val.trim())) { valTemp=true; break;}
                            if(row.getText().contains(val.trim())) { break outerloop;}
                        }
                    } else {
                        Log.info(val);
                        Log.info(cell.getText());
                        if(cell.getText().contains(val.trim())) {
                            valTemp = true;
                            Log.info("Value is found in cell");
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
            ++a;
        }
        return result;
    }

    private void setCustomerNameFilter(String customerName) {
        field.clearText(CUSTOMER_NAME_GIRD_FILTER_INPUT);
        if(customerName !=null) {
            field.setTextField(CUSTOMER_NAME_GIRD_FILTER_INPUT, customerName);
            driver.findElement(By.xpath(CUSTOMER_NAME_GIRD_FILTER_INPUT)).sendKeys(Keys.ENTER);
            Timer.sleep(2);
        }
    }



}
