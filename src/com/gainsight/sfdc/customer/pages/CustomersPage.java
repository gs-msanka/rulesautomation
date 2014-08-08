package com.gainsight.sfdc.customer.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import java.util.*;


public class CustomersPage extends CustomerBasePage {
    private final String READY_INDICATOR    = "//div[@class='gs-moreopt-btn']";


    private final String CUSTOMER_EDIT_LINK = "//table[@id='customerList_IdOfJBaraStandardView']//tr[%d]//a[text()='Edit']";
    private final String CUSTOMER_DEL_LINK  = "//table[@id='customerList_IdOfJBaraStandardView']//tr[%d]//a[text()='Delete']";
    private final String CUSTOMER_TABLE     = "//table[contains(@id,'customerList_IdOf') and @class='ui-jqgrid-btable']";
    private final String STATUS_FIELD       = "//td[text()='Status: ']/following-sibling::td//select";
    private final String STAGE_FIELD        = "//td[text()='Stage: ']/following-sibling::td//select";
    private final String CUSTOMER_NAME_FIELD = "CustomerLink";


    private final String MORE_ICON            = "//div[@class='gs-moreopt-btn']";
    private final String NEW_CUSTOMER_LINK    = "//a[contains(text(), 'New Customer')]";
    private final String ACC_NAME_INPUT       = "//input[@placeholder='Enter customer name' and @name='search_text']";
    private final String AUTO_SELECT_LIST     = "//ul[@class='ui-autocomplete ui-front ui-menu ui-widget ui-widget-content ui-corner-all']";
    private final String CUSTOMER_SAVE        = "//input[@class='save-customer btn-save' and @value='Save']";
    private final String COMMENTS_INPUT       ="//textarea[@class='commentArea']";
    private final String STAGE_SELECT_BUTTON  = "//select[@class='stageSelection']/following-sibling::button";
    private final String STATUS_SELECT_BUTTON = "//select[@class='statusSelection']/following-sibling::button";
    private final String CUSTOMER_FORM_CANCEL_BUTTON = "//input[@value='Cancel' and @class='cancel btn-cancel']";

    public CustomersPage() {
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public CustomersPage addCustomer(String customerName, String status,
                                     String stage, String comments) {

        item.click(MORE_ICON);
        selectAccount(customerName);
        fillFields(status, stage, comments);
        item.click(CUSTOMER_SAVE);
        return this;
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

    private void selectValueInDropDown(String value) {
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
        amtDateUtil.stalePause();
    }

    private int getNoOfCustomersRecords(String custName) {
        amtDateUtil.stalePause();
        setCustomerFilter(custName);
        int recordCount = element.getElementCount("//div[@class='slick-cell l1 r1 slick-customer-format']/a[contains(text(), '"+custName+"')]");
        return recordCount;
    }

    public boolean isCustomerPresent(String customerName) {
        amtDateUtil.stalePause();
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
            Report.logInfo(e.getMessage());
        }
        return ele;
    }



    public CustomersPage editCustomer(String customerName, String status,
                                      String stage, String comments) {
        WebElement ele = getCustomerRow(customerName, null);

        if(ele!=null) {
            ele.findElement(By.cssSelector("div>a[data-action='EDIT']")).click();
            wait.waitTillElementDisplayed(ACC_NAME_INPUT, MIN_TIME, MAX_TIME);
            fillFields(status,stage,comments);
            item.click(CUSTOMER_SAVE);

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
            amtDateUtil.stalePause();
            modal.accept();
            amtDateUtil.stalePause();
            try {
                modal.accept();
                Report.logInfo("Modal dialog present ,Customer can't be deleted");
            } catch (Exception e) { //need to change it to exact exception type
                Report.logInfo("Modal dialog not present ,Customer can be deleted");
                status = true;
            }
        } else {
            throw new RuntimeException("Customer not found");
        }
        return status;
    }





}
