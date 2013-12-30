package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.AlertsPage;
import com.gainsight.sfdc.retention.pages.EventsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;

public class Retention360 extends Customer360Page {

    private static final String ADD_EVENT_BUTTON = "//div[@class='gs_edit_icon']/a[text()='Add Event']";
    private static final String ADD_ALERT_BUTTON = "//div[@class='gs_edit_icon']/a[text()='Add Alert']";
    private static final String READY_INDICATOR = "//div[@class='gs_section_title']/h1[text()='Retention']";
    String EVENT_STATUS_OPEN_RADIO = "//input[@class='ga_eventstatusFldCls' and @value='Open']";
    String EVENT_STATUS_INPROGRESS_RADIO = "//input[@class='ga_eventstatusFldCls' and @value='In Progress']";
    String EVENT_STATUS_COMPLETE_RADIO = "//input[@class='ga_eventstatusFldCls' and @value='Complete']";
    String ALERT_SECTION_TAB = "//ul[@class='alert_tab_nav']/li/a[text()='Alert']";
    String EVENT_SECTION_TAB = "//ul[@class='alert_tab_nav']/li/a[text()='Events']";
    String LOADING_EVENTS_IMG = "//div[contains(@class, 'gs-loader' and text()='Loading Events')]";
    EventsPage ePage;
    AlertsPage aPage;

    public Retention360(String val) {
        wait.waitTillElementDisplayed(READY_INDICATOR, MIN_TIME, MAX_TIME);
        if(val != null && val.contains("Events Page")) {
            ePage = new EventsPage("360 Page");
        } else if(val != null && val.contains("Alerts Page")) {
            ePage = new EventsPage("360 Page");
        }
    }

    public void clickOnAlertSubTab() {
        wait.waitTillElementDisplayed(ALERT_SECTION_TAB, MIN_TIME, MAX_TIME);
        item.click(ALERT_SECTION_TAB);
    }

    public void clickOnEventSubTab() {
        wait.waitTillElementDisplayed(EVENT_SECTION_TAB, MIN_TIME, MAX_TIME);
        item.click(EVENT_SECTION_TAB);
        wait.waitTillElementNotPresent(LOADING_EVENTS_IMG, MIN_TIME, MAX_TIME);
    }

    public int getlistofIframes() {
        List<WebElement> wEleList = driver.findElements(By.tagName("iframe"));
        int a =0;
        if(wEleList  !=null) {
            a = wEleList.size();
        }
        return a;
    }

    private void movetoActiveIframe() {
        List<WebElement> elementList = element.getAllElement("//iframe");
        for(WebElement wEle : elementList) {
           if(wEle.isDisplayed()) {
               driver.switchTo().frame(wEle);
           }
        }
    }

    public void addEvent(HashMap<String, String> eventData, HashMap<String, String> taskData) {
        clickOnEventSubTab();
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

    public void addTask(HashMap<String, String> taskData) {
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
                "/preceding-sibling::div[text()='"+testData.get("type")+"']" +
                "/parent::div[@class='events_card']";
        return elePath;
    }

    public void waitForEventDisplay(HashMap<String, String> testData) {
        wait.waitTillElementDisplayed(buildeventXpath(testData), MIN_TIME, MAX_TIME);
    }

    public boolean isEventCardDisplayed(HashMap<String, String> testData) {
        boolean result = false;
        for(int i =0; i < 2; ++i) {
            List<WebElement> eventList = element.getAllElement(buildeventXpath(testData));
            if(eventList!= null & eventList.size() > 0 ) {
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
        amtDateUtil.sleep(10);
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


     public void addAlert(HashMap<String, String> alertData) {
            item.click(ADD_ALERT_BUTTON);
            movetoActiveIframe();
            aPage.addAlert(alertData);
        }


        public boolean isAlertDisplayed(HashMap<String, String> alertData) {
                  return false;

        }

        private String buildAlertXpath(HashMap<String, String> alertData) {
            return null;
        }






}
