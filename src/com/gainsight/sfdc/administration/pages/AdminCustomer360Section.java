package com.gainsight.sfdc.administration.pages;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;

import com.gainsight.sfdc.pages.BasePage;

public class AdminCustomer360Section extends BasePage {

    private final String READY_INDICATOR = "//div[@class='apexp']//table[contains(@id,'CS360Section')]";
    private final String EDIT_LINK_SPONSOR = "//a[contains(@onclick,'SponsorTracking')]";
    private final String EDIT_FROM_SPONSORTRACKING = "//div[@id='FrameForEditing']//iframe";
    private final String EDIT_FROM_RELATEDLIST = ".//*[@id='reportForEditing']//iframe";
    private final String ENABLE_CHECKBOX = "//span[contains(@class,'checkbox')]";
    private final String CHECKED_CHECKBOX = "//span[@class,'checkbox-active']";
    private final String UNCHECKED_CHECKBOX = "//span[@class,'checkbox-normal']";
    private final String SAVE_SPONSOR_TRACKING = "//a[@class='btn-save saveSummary']";
    private final String EDIT_SPONSOR_TRACKING_IFRAME = "//iframe[contains(@src,'sponsortracking')]";
    private final String ADD_NEW_SECTION = "//html/body//div[@id='contentWrapper']//div/input[@value='Add new section']";
    private final String ADD_SECTION_SHOW_LABEL = "html/body//div[@class='gs-rb-cs360-popup-container']//span/input[@id='showLabel']";
    private final String ADD_SECTION_SOURCE = ".//div[@class='gs-related-list']/div[@class='gs-rb-select-reports']/div[@class='gs-rb-source-select']/button";
    private final String SAVE_RELATED_LIST = "//div[@class='gs-rb-cs360-container']/div[@class='modal_footer']/input[@value='Save']";
    private final String CHECKBOX_REPORT_RELATED_LIST = "//div[@class='gs-rb-choose-reportlist']/div/span[@class='checkbox-normal']";

    public AdminCustomer360Section() {
        Log.info("Admin Customer 360 Section Page Loading");
        wait.waitTillElementPresent(READY_INDICATOR, MIN_TIME, MAX_TIME);
    }

    public void EditSponsorTracking() {
        Log.info("Click on Edit link in Customer 360 Section");
        item.click(EDIT_LINK_SPONSOR);
        Timer.sleep(2);
    }

    public void EnableSponsorTracking() {
        if (item.isElementPresent(EDIT_FROM_SPONSORTRACKING)) {
            driver = driver.switchTo().frame(driver.findElement(By.xpath(EDIT_SPONSOR_TRACKING_IFRAME)));
            wait.waitTillElementPresent(ENABLE_CHECKBOX, 3, 10);
            /*
             * if(!(field.isSelected(ENABLE_CHECKBOX)))
             * item.click(ENABLE_CHECKBOX); item.click(SAVE_SPONSOR_TRACKING);
             */
            // Why above lines didn't work: driver.findelements(by.xpath or
            // id..whatever) If element is not present, throws Exception.
            try {
                if (field.isElementPresent(UNCHECKED_CHECKBOX))
                    item.click(UNCHECKED_CHECKBOX);
            } catch (Exception e) {
                Log.info("Element is already Checked");
            }

            item.click(SAVE_SPONSOR_TRACKING);
            Timer.sleep(2);
            driver = driver.switchTo().defaultContent();
            Log.info("Finished Admin Config...");
        } else
            Log.info("Sponsor Tracking Window to Enable is not Visible");

    }
    public void addNewSectionCS360() {
        Log.info("Click on new section in Customer 360 Section");
        item.click(ADD_NEW_SECTION);
        Timer.sleep(2);
    }

    public void EnableNewSectionForRelatedList(String labelName, String dropdownobj) {
        
        Timer.sleep(3);
        driver.switchTo().frame(driver.findElement(By.xpath(EDIT_FROM_RELATEDLIST)));
        System.out.println("switched to frame");
        Timer.sleep(2);
        element.setText(ADD_SECTION_SHOW_LABEL, labelName);
        element.selectFromDropDown(ADD_SECTION_SOURCE, dropdownobj);
        element.selectCheckBox(CHECKBOX_REPORT_RELATED_LIST);
        item.click(SAVE_RELATED_LIST);
        Timer.sleep(2);
        System.out.println("Save button clicked");
        driver = driver.switchTo().defaultContent();
        Log.info("Finished Admin Config...");

    }

    public void editRelatedList(String relatedlistname) {
        Log.info("Click on Edit link in Customer 360 Section for related list");
        String xPath = "//*[contains(text(),'" + relatedlistname
                + "')]/preceding-sibling::td/a[contains(text(),'Edit')]";
        wait.waitTillElementDisplayed(xPath, MIN_TIME, MAX_TIME);
        item.click(xPath);
        Timer.sleep(2);
    }

    public void saveRelatedList() {
        Timer.sleep(3);
        driver = driver.switchTo().frame(driver.findElement(By.xpath(EDIT_FROM_RELATEDLIST)));
        Timer.sleep(2);
        item.click(SAVE_RELATED_LIST);
        Timer.sleep(2);
        System.out.println("Save button clicked");
        driver = driver.switchTo().defaultContent();
        Log.info("Finished Admin Config...");

    }
}
