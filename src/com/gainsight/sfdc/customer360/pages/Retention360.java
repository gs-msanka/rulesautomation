package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.AlertsPage;
import com.gainsight.sfdc.retention.pages.EventsPage;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;

public class Retention360 extends Customer360Page {

    private static final String ADD_EVENT_BUTTON                = "//div[@class='gs_edit_icon']/a[text()='Add Event']";
    private static final String ADD_ALERT_BUTTON                = "//div[@class='gs_edit_icon']/a[text()='Add Alert']";
    private static final String EVENT_STATUS_OPEN_RADIO         = "//input[@class='ga_eventstatusFldCls' and @value='Open']";
    private static final String EVENT_STATUS_INPROGRESS_RADIO   = "//input[@class='ga_eventstatusFldCls' and @value='In Progress']";
    private static final String EVENT_STATUS_COMPLETE_RADIO     = "//input[@class='ga_eventstatusFldCls' and @value='Complete']";
    private static final String ALERT_SECTION_TAB               = "//ul[@class='alert_tab_nav']/li/a[text()='Alert']";
    private static final String EVENT_SECTION_TAB               = "//ul[@class='alert_tab_nav']/li/a[text()='Events']";
    private static final String LOADING_EVENTS_IMG              = "//div[contains(@class, 'gs-loader' and text()='Loading Events')]";
    private static final String LOADING_ALERTS_IMG              = "//div[contains(@class, 'gs-loader' and text()='Loading Alerts')]";
    EventsPage ePage;
    AlertsPage aPage;

    public Retention360(String val) {
        if(val != null && val.contains("Events Page")) {
            ePage = new EventsPage("360 Page");
            wait.waitTillElementDisplayed(ADD_EVENT_BUTTON, MAX_TIME, MIN_TIME);
        } else if(val != null && val.contains("Alerts Page")) {
            aPage = new AlertsPage("360 Page");
            wait.waitTillElementDisplayed(ADD_ALERT_BUTTON, MAX_TIME, MIN_TIME);
        }
    }

    private void movetoActiveIframe() {
        amtDateUtil.sleep(5);
        List<WebElement> elementList = element.getAllElement("//iframe");
        for(WebElement wEle : elementList) {
           if(wEle.isDisplayed()) {
               driver.switchTo().frame(wEle);
           }
        }
    }

    public void addEvent(HashMap<String, String> eventData, HashMap<String, String> taskData) {
        item.click(ADD_EVENT_BUTTON);
        movetoActiveIframe();
        fillEventForm(eventData);
        if(Boolean.valueOf(eventData.get("tasks"))) {
            ePage.fillTaskForm(taskData);
            ePage.clickOnSaveTask();
        }
        ePage.clickOnCreateEvent();
        element.switchToMainWindow();
    }

    public void fillEventForm(HashMap<String, String> eventData) {
        ePage.fillEventForm(eventData, false);
    }

    public void addEventTask(HashMap<String, String> taskData) {
        ePage.clickOnAddTask();
        ePage.fillTaskForm(taskData);
        ePage.clickOnSaveTask();
    }

    public void clickOnUpdateEvent() {
        ePage.clickOnUpdateEvent();
        element.switchToMainWindow();
        amtDateUtil.stalePause();
    }

    public void selectPlaybook(String pName) {
        int tasksCount = ePage.getAllTasks().size();
        ePage.selectPlaybook(pName);
        for(int i =0; i < 15; i ++) {
            int taskCountAfterPlaybook = ePage.getAllTasks().size();
            if(taskCountAfterPlaybook >tasksCount) {
                Report.logInfo("Playbook tasks loaded");
                break;
            } else {
                amtDateUtil.sleep(1);
            }
        }
    }

    private String buildeventXpath(HashMap<String, String> testData) {
        String elePath = "//div[@class='data_value' and contains(text(), '"+testData.get("schedule")+"')]" +
                "/parent::div/preceding-sibling::div/div[contains(@title, '"+testData.get("owner")+"')]" +
                "/parent::div/preceding-sibling::div[contains(text(), '"+testData.get("subject")+"')]" +
                "/preceding-sibling::div[contains(text(),'"+testData.get("type")+"')]" +
                "/parent::div[@class='events_card']";
        Report.logInfo("Event Xpath :" +elePath);
        return elePath;
    }

    public void waitForEventDisplay(HashMap<String, String> testData) {
        wait.waitTillElementDisplayed(buildeventXpath(testData), MIN_TIME, MAX_TIME);
    }

    public boolean isEventCardDisplayed(HashMap<String, String> testData) {
        boolean result = false;
        for(int i =0; i < 2; ++i) {
            if(item.isElementPresent(buildeventXpath(testData))) {
                result = true;
                break;
            } else {
                amtDateUtil.stalePause();
                Report.logInfo("Trying again to find the event card");
            }
        }
        return result;
    }

    public WebElement getEventCard(HashMap<String, String> testData) {
        String xpath = buildeventXpath(testData);
        WebElement event = element.getElement(xpath);
        return event;
    }

    public void changeEventStatus(HashMap<String, String> testData, String status) {
        WebElement event = getEventCard(testData);
        event.findElement(By.cssSelector("div.change_event_status")).click();
        wait.waitTillElementDisplayed("//div[@class='loadEventStatusDiv']", MIN_TIME, MAX_TIME);
        if(status != null) {
            if(status.equalsIgnoreCase("Open")) {
                item.click(EVENT_STATUS_OPEN_RADIO);
            } else if(status.equalsIgnoreCase("In Progress")) {
                item.click(EVENT_STATUS_INPROGRESS_RADIO);
            } else if(status.equalsIgnoreCase("Complete")) {
                item.click(EVENT_STATUS_COMPLETE_RADIO);
            }
        }
    }

    public void deleteAllEvents() {
        List<WebElement> eventList = element.getAllElement("//div[@class='events_card']");
        if(eventList !=null && eventList.size() >0) {
            for(WebElement event : eventList) {
                event.findElement(By.cssSelector("div.delete_icon_btn")).click();
                modal.accept();
            }
        Report.logInfo("Deleted All Events From 360 Page");
        } else {
            Report.logInfo("No Events Displayed");
        }
        amtDateUtil.stalePause();
    }



    public boolean isInfoMessageDisplayed() {
        String xPath = "//div[@class='events_content']/div[@class='noDataFound']";
        wait.waitTillElementDisplayed(xPath, MIN_TIME, MAX_TIME);
        boolean result = false;
        if(element.getElement(xPath).isDisplayed()) {
            String actValue = element.getText(xPath);

            String expValue =  "No Events Found";
            System.out.println(actValue);
            System.out.println(expValue);
            if(actValue.contains(expValue)){
                result = true;
            }
        }
        return result;
    }

    public boolean verifyEventStatus(HashMap<String, String> testData, String expStatus) {
        boolean result = false;
        String actStatus= null;
        WebElement event = getEventCard(testData);
        event.findElement(By.cssSelector("div.change_event_status")).click();
        wait.waitTillElementDisplayed("//div[@class='loadEventStatusDiv']", MIN_TIME, MAX_TIME);
        if(expStatus != null) {
            if(expStatus.equalsIgnoreCase("Open")) {
                actStatus = element.getElement(EVENT_STATUS_OPEN_RADIO).getAttribute("checked");
            } else if(expStatus.equalsIgnoreCase("In Progress")) {
                actStatus = element.getElement(EVENT_STATUS_INPROGRESS_RADIO).getAttribute("checked");
            } else if(expStatus.equalsIgnoreCase("Complete")) {
                actStatus = element.getElement(EVENT_STATUS_COMPLETE_RADIO).getAttribute("checked");
            }
        }
        if(actStatus != null & actStatus.equalsIgnoreCase("true")) {
            result = true;
        }
        return result;
    }

    public void deleteEvent(HashMap<String, String> testData) {
        WebElement event = getEventCard(testData);
        event.findElement(By.cssSelector("div.delete_icon_btn")).click();
        modal.accept();
        amtDateUtil.stalePause();
    }

    public void openEventCard(HashMap<String, String> eventData) {
        WebElement eCard = getEventCard(eventData);
        eCard.findElement(By.cssSelector("div.edit_icon.card_data_click")).click();
        Report.logInfo("Clicked on Event Card");
        movetoActiveIframe();
        ePage.waitforEventCardtoLoad();
    }

    public boolean isTaskDisplayed(HashMap<String, String> taskData) {
        boolean result = ePage.isTaskDisplayed(taskData);
        return result;
    }

    public void reloadPage() {
        refreshPage();
    }

    /////////////////////////////////////////////////////////////////
    /************Alerts Module Page Objects********/


    public Retention360 addAlert(HashMap<String, String> alertData) {
        item.click(ADD_ALERT_BUTTON);
        movetoActiveIframe();
        aPage.fillAlertForm(alertData, false);
        aPage.clickOnSaveAlert();
        element.switchToMainWindow();
        return this;
    }

    public void updateAlert(HashMap<String, String> alertData, HashMap<String, String> updatedAlertData, AlertCardLabel alabel) {
        openAlertCardEditMode(alertData, alabel);
        aPage.fillAlertForm(updatedAlertData, false);
        aPage.clickOnEditAlertClose();
        element.switchToMainWindow();
    }

    public boolean isAlertDisplayed(HashMap<String, String> alertData, AlertCardLabel alertCardLabel) {
        boolean result = false;
        amtDateUtil.stalePause();
        String alert = buildAlertXpath(alertData, alertCardLabel);
        if(item.isElementPresent(alert)) {
            result = true;
            Report.logInfo("Alert Found");
        } else {
            Report.logInfo("Alert Not Found");
        }
        return result;
    }

    public void openAlertCardEditMode(HashMap<String, String> alertData, AlertCardLabel alabel) {
        String alertXpath = buildAlertXpath(alertData, alabel);
        String alertEditIcon = alertXpath+"/div[@class='edit_icon card_data_click' and @title='Edit']";
        item.click(alertEditIcon);
        movetoActiveIframe();
        aPage.waitTillAlertCardLoaded();
    }

    public Retention360 addTaksOnAlert(List<HashMap<String, String>> taskDataList) {
        for(HashMap<String, String> taskData : taskDataList) {
            aPage.addTask(taskData);
        }
        closeAlertView();
        return this;
    }

    public void closeAlertView() {
        aPage.closeAlertForm();
        element.switchToMainWindow();
    }

    public void deleteAlert(HashMap<String, String> alertData, AlertCardLabel alabel) {
        String alertXpath = buildAlertXpath(alertData, alabel);
        String deleteIcon = alertXpath+"/div[@class='delete_icon_btn' and @title='Delete']";
        item.click(deleteIcon);
        modal.accept();
    }

    public boolean isAlertTaskDisplayed(HashMap<String, String> taskData) {
        return aPage.isTaskDisplayed(taskData);
    }

    private String buildAlertXpath(HashMap<String, String> alertData, AlertCardLabel alabel) {
        String  xPath = "//";
        if(alabel.getLabel5() != null && alabel.getLabel5() != "") {
            xPath += "div[@class='data_label' and contains(text(),'"+alabel.getLabel5()+"')]/following-sibling::div[@class='data_value' and contains(text(),'"+alertData.get(alabel.getLabel5())+"')]" +
                    "/parent::div/preceding-sibling::div[@class='card_data card_data_click']";
        }
        if(alabel.getLabel4() != null && alabel.getLabel5() != "") {
            xPath += "/div[@class='data_label' and contains(text(),'"+alabel.getLabel4()+"')]/following-sibling::div[@class='data_value' and contains(text(),'"+alertData.get(alabel.getLabel4())+"')]" +
                    "/parent::div/preceding-sibling::div[@class='card_data card_data_click']";
        }
        if(alabel.getLabel3() != null && alabel.getLabel3() != "") {
            xPath += "/div[@class='data_label' and contains(text(),'"+alabel.getLabel3()+"')]/following-sibling::div[@class='data_value' and contains(text(),'"+alertData.get(alabel.getLabel3())+"')]" +
                    "/parent::div/preceding-sibling::div[@class='card_data card_data_click']";
        }
        if(alabel.getLabel2() != null && alabel.getLabel2() != "") {
            xPath +="/div[contains(@class, 'data_label') and contains(text(),'"+alabel.getLabel2()+"')]/following-sibling::div[@class='data_value' and contains(text(),'"+alertData.get(alabel.getLabel2())+"')]" +
                    "/parent::div/preceding-sibling::div[@class='card_data card_data_click']";
        }
        if(alabel.getLabel1() != null && alabel.getLabel1() != "") {
            xPath += "/div[@class='data_label' and contains(text(),'"+alabel.getLabel1()+"')]/following-sibling::div[@class='data_value' and contains(text(),'"+alertData.get(alabel.getLabel1())+"')]";
        }
        xPath += "/parent::div[@class='card_data card_data_click']/preceding-sibling::div[@class='alert_title card_data_click' and contains(text(), '"+alertData.get("subject")+"')]";
        xPath += "/parent::div[@class='alert_card']";
        Report.logInfo("Alert Xpath :" +xPath);
        return xPath;
    }








}
