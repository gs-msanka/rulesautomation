package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;

import java.util.HashMap;


public class Customer360Milestones extends Customer360Page {

    private final String LOADING_IMG                = "//div[contains(text(), 'gs-loadingMsg gs-loader-container')]";
    private final String NO_MILESTONES_MSG          = "//div[contains(@class,'noMilestone noDataFound') and contains(text(), 'No Milestones')]";
    private final String MILESTONES_SUB_TAB         = "//li[@data-tabname='Milestones']/a[contains(.,'Milestones')]";
    private final String ADD_MILESTONES             = "//a[@class='addNewMilestone']";
    private final String DATE_FIELD                 = "//input[@id='DateId']";
    private final String MILESTONE_DROP_BOX         = "//select[@id='MilestoneOptions']/following-sibling::button";
    private final String OPPORTUNITY_DROP_BOX       = "//select[@id='OppOptions']/following-sibling::button";
    private final String COMMENT_FIELD              = "//textarea[@id='CommentsId']";
    private final String SAVE_BUTTON                = "//a[@class='btn_save']";
    private final String CLOSE_BUTTON               = "//a[@class='btn_cancel']";
    private final String CLOSE_X                    = "//button[@title='close']/span[@title='Close']";
    private final String MILESTONES_TABLE           = "//div[@id='Milestones']/descendant::table[contains(@class,'gs_milestones_grid')]";
    private final String MILESTONES_TABLE_DATA_GRID = MILESTONES_TABLE+"/tbody";
    private final String MILESTONES_ADD_POPUP       = "//div[@class='ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix']/span[contains(text(), 'Add Milestone')]";
    private final String MILESTONES_EDIT_POPUP      = "//div[@class='ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix']/span[contains(text(), 'Edit Milestone')]";
    private final String MILESTONE_ROW_CHECK        = MILESTONES_TABLE+"/tbody/tr[%d]";
    private final String NO_OF_ROWS                 = "//table[@class='gs_features_grid gs_milestones_grid']/tbody/tr";
    private final String MILESTONE_ROW              = "//table[@class='gs_features_grid gs_milestones_grid']"+
                                                            "/tbody/tr/td[1][contains(.,'%s')]"+
                                                            "/following-sibling::td[1]/span[@style='background-color:%s;']"+
                                                            "/following-sibling::span[contains(.,'%s')]/parent::td"+
                                                            "/following-sibling::td[1][contains(.,'%s')]"+
                                                            "/following-sibling::td[1][contains(.,'%s')]";


    public Customer360Milestones gotoMilestonesSubTab(){
        item.click(MILESTONES_SUB_TAB);
        wait.waitTillElementDisplayed(ADD_MILESTONES, MIN_TIME, MAX_TIME);
        waitForLoadingImagesNotPresent();
        return this;
    }

    public void addMilestone(HashMap<String, String> testData) {
        item.click(ADD_MILESTONES);
        wait.waitTillElementDisplayed(MILESTONES_ADD_POPUP, MIN_TIME, MAX_TIME);
        if(testData.get("Date") != null) {
            setDateInField(testData.get("Date"));
        }
        if(testData.get("Milestone") !=null) {
            selectMileStone(testData.get("Milestone"));
        }
        if(testData.get("Opportunity") != null) {
            selectOpportunityForMilestone(testData.get("Opportunity"));
        }
        if(testData.get("Comments") != null) {
            addComments(testData.get("Comments"));
        }
        clickOnSave();
        waitForLoadingImagesNotPresent();
    }

    private void waitForLoadingImagesNotPresent() {
        env.setTimeout(5);
        wait.waitTillElementNotPresent(LOADING_IMG, MIN_TIME, MAX_TIME);
        env.setTimeout(30);
    }

    private void setDateInField(String date){
        item.clearAndSetText(DATE_FIELD, date);
    }

    private void selectMileStone(String Milestone){
        item.click(MILESTONE_DROP_BOX);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+Milestone+"')]");
    }

    private void selectOpportunityForMilestone(String Opportunity){
        item.click(OPPORTUNITY_DROP_BOX);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+Opportunity+"')]");
    }

    private void addComments(String Comments){
        item.clearAndSetText(COMMENT_FIELD, Comments);
    }

    private void clickOnSave(){
        item.click(SAVE_BUTTON);
        amtDateUtil.stalePause();
    }

    public boolean isMilestonePresent(HashMap<String, String> testData) {
        String milestone = getMilestoneXpath(testData);
        return isElementPresentAndDisplay(By.xpath(milestone));
    }

    public boolean checkMilestoneRow(String date,String Color,String Milestone,String Opportunity,String Comments) {
        return item.isElementPresent(String.format(MILESTONE_ROW, date,Color,Milestone,Opportunity,Comments));
    }

    public void deleteMilestone(HashMap<String, String> testData){
        String milestone = getMilestoneXpath(testData);
        String milestoneDelete = milestone+"/td[@class='delete-icon']";
        item.click(milestoneDelete);
        driver.switchTo().alert().accept();
        amtDateUtil.stalePause();
        waitForLoadingImagesNotPresent();
    }

    private String getMilestoneXpath(HashMap<String, String> testData) {
        String xPath = MILESTONES_TABLE+"/descendant::td[contains(text(), '"+testData.get("Date")+"')]";
        xPath += "/following-sibling::td/span[contains(text(), '"+testData.get("Milestone")+"')]";
        if(testData.get("MilestoneColor") !=null) {
            xPath += "/preceding-sibling::span[contains(@style, '"+testData.get("MilestoneColor")+"')]";
        }
        xPath += "/parent::td";
        if(testData.get("Opportunity") !=null) {
            xPath +="/following-sibling::td[contains(text(), '"+testData.get("Opportunity")+"')]";
        }

        if(testData.get("Comments") !=null) {
            xPath +="/following-sibling::td[contains(text(), '"+testData.get("Comments")+"')]";
        }
        xPath +="/parent::tr";
        Report.logInfo("Mile Stone Xpath in table for given data : " +xPath);
        return xPath;
    }

    /**
     * Edits the milestone & validates if form data is correctly loaded & then update with new info
     * @param oldTestData - Milestone to edit.
     * @param newTestData - Milestone updated information.
     * throws run time exception is form data is not correct.
     */
    public void editMileStone(HashMap<String, String> oldTestData, HashMap<String, String> newTestData) {
        String milestone = getMilestoneXpath(oldTestData);
        milestone += "/td[@class='edit-icon']";
        item.click(milestone);
        wait.waitTillElementDisplayed(MILESTONES_EDIT_POPUP, MIN_TIME, MAX_TIME);
        amtDateUtil.sleep(5);
        HashMap<String, String> formData = getMilestoneFormData();
        if(!oldTestData.get("Date").equals(formData.get("Date")) && !oldTestData.get("Milestone").equals(formData.get("Milestone")) &&
                (!oldTestData.get("Opportunity").equals(formData.get("Opportunity")) || oldTestData.get("Opportunity") == null ) &&
                (!oldTestData.get("Comments").equalsIgnoreCase(formData.get("Comments")) || oldTestData.get("Comments") == null)) {
            throw new RuntimeException("Form data of milestone is not loaded appropriately.");
        } else {
            setDateInField(newTestData.get("Date"));
            selectMileStone(newTestData.get("Milestone"));
            selectOpportunityForMilestone(newTestData.get("Opportunity"));
            addComments(newTestData.get("Comments"));
            clickOnSave();
            amtDateUtil.stalePause();
            waitForLoadingImagesNotPresent();
        }
    }

    /**
     * Generates the milestone form data that in popup.
     * @return milestone data as key value pairs.
     */
    private HashMap<String, String> getMilestoneFormData() {
        HashMap<String, String> formData =  new HashMap<String, String>();
        Report.logInfo("Date : " +field.getTextFieldValue(DATE_FIELD));
        formData.put("Date", item.getText(DATE_FIELD));
        Report.logInfo("Milestone : " +item.getText(MILESTONE_DROP_BOX+"/span[@class='ui-multiselect-selected-label']"));
        formData.put("Milestone", item.getText(MILESTONE_DROP_BOX+"/span[@class='ui-multiselect-selected-label']"));
        Report.logInfo("Opportunity : " +item.getText(OPPORTUNITY_DROP_BOX+"/span[@class='ui-multiselect-selected-label']"));
        formData.put("Opportunity", item.getText(OPPORTUNITY_DROP_BOX+"/span[@class='ui-multiselect-selected-label']"));
        formData.put("Comments", field.getTextFieldValue(COMMENT_FIELD));
        return formData;
    }

    public boolean isNoMilestoneMessagePresent() {
        amtDateUtil.stalePause();
        waitForLoadingImagesNotPresent();
        return isElementPresentAndDisplay(By.xpath(NO_MILESTONES_MSG));
    }

    public boolean isHeaderPresent(){
        amtDateUtil.stalePause();
        wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
        return true;
    }

    public boolean isHeaderItemPresent(String columnName){
        wait.waitTillElementDisplayed(MILESTONES_TABLE+ "/thead/tr/th[text()='"+ columnName+ "']",MIN_TIME,MAX_TIME);
        return true;
    }

    public boolean isMsTableDataPresent(){
        Report.logInfo("xpath of table data-->"+MILESTONES_TABLE_DATA_GRID);
        wait.waitTillElementDisplayed(MILESTONES_TABLE_DATA_GRID, MIN_TIME, MAX_TIME);
        return true;
    }

    public void clickOnCloseButton(){
        item.click(CLOSE_BUTTON);
        wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
    }

    public void clickOnCloseX(){
        item.click(CLOSE_X);
        wait.waitTillElementDisplayed(MILESTONES_TABLE, MIN_TIME, MAX_TIME);
    }

    public boolean isRowPresentAfterDelete(int row)
    {
        wait.waitTillElementNotPresent(String.format(MILESTONE_ROW_CHECK, row), MIN_TIME, MAX_TIME);
        return true;
    }

    public int getCurrentNoOfRows() {
        return element.getElementCount(NO_OF_ROWS);
    }
}
