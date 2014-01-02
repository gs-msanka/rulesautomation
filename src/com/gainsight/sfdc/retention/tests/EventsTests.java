package com.gainsight.sfdc.retention.tests;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gainsight.sfdc.retention.pojos.Event;
import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.EventsPage;
import com.gainsight.sfdc.tests.BaseTest;

public class EventsTests extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    Calendar c = Calendar.getInstance();
    Boolean isEventCreateScriptExecuted = false;

    String EVENT_PICKLIST_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Event_PickList_Setup_Script.txt";
    String USER_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";
    String PLAYBOOK_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Playbooks_Create_Script.txt";
    @BeforeClass
    public void setUp() {
        basepage.login();
        userLocale           = soql.getUserLocale();
        apex.runApexCodeFromFile(EVENT_PICKLIST_SETUP_FILE, isPackageInstance());
        //apex.runApexCodeFromFile(USER_SETUP_FILE);
        apex.runApexCodeFromFile(PLAYBOOK_SETUP_FILE, isPackageInstance());
    }

    @Test
    public void Event_07() {
        if(!isEventCreateScriptExecuted) {
            createEventsFromScript();
        }
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        eventsPage.applyFilter("Sprint Planning");
        Assert.assertTrue(eventsPage.isFiltersOn("Sprint Planning"));
        List<Event> eventList = eventsPage.getAllEvents();
        for(Event e : eventList) {
                  Assert.assertEquals("Sprint Planning", e.getType());
        }
        eventsPage.clearAllFilters();
    }

    @Test
    public void Event_30() throws IOException, BiffException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_030");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(365));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));

        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursYearly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_31() throws IOException, BiffException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_031");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(300));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));

        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursYearlyNth' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_033() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_033");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.changeStatus(eventData, "In Progress");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "In Progress"), "Verifying wether the status of the event changed to In progress.");
        eventsPage.changeStatus(eventData, "Complete");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "Complete"), "Verifying the status of event is complete or not");
    }

    @Test
    public void Event_032() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_032");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(2));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        eventsPage.deleteEvent(eventData);
        Assert.assertFalse(eventsPage.isEventDisplayed(eventData), "Verifying weather Event Deleted successfully.");
    }

    @Test
    public void Event_029() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_029");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(60));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursMonthlyNth' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_028() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_028");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(40));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursMonthly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        int recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_027() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_027");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(3));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursWeekly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        int recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_026() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_026");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(4));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursDaily' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        int recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_025() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_025");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        eventData.put("startdate", getDatewithFormat(0));
        eventData.put("enddate", getDatewithFormat(1));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursEveryWeekDay' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        int recordCount = getQueryRecordCount(query);
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_024() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage                       = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        List<HashMap<String, String>> taskDataList  =  new ArrayList<HashMap<String, String>>();
        HashMap<String, String> testData            = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_024");
        HashMap<String, String> eventData           = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData            = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        HashMap<String, String> task1               = getMapFromData(testData.get("task1"));
        task1.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        taskDataList.add(task1);
        eventsPage.addTask(eventData, taskDataList);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.openEventCard(eventData);
        Assert.assertTrue(eventsPage.isTaskDisplayed(task1), "Checking weather task is created successfully");
        eventsPage.deleteTask(task1);
        eventsPage.clickOnUpdateEvent(eventData);
        eventsPage.openEventCard(eventData);
        Assert.assertFalse(eventsPage.isTaskDisplayed(task1), "Checking weather task deletion successful");
        eventsPage.clickOnCloseEventCard();
    }

    @Test
    public void Event_018() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_018");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        HashMap<String, String> task1       = getMapFromData(testData.get("task1"));
        task1.put("date", getDatewithFormat(1));
        HashMap<String, String> task2       = getMapFromData(testData.get("task2"));
        task2.put("date", getDatewithFormat(2));
        HashMap<String, String> task3       = getMapFromData(testData.get("task3"));
        task3.put("date", getDatewithFormat(3));
        HashMap<String, String> task4       = getMapFromData(testData.get("task4"));
        task4.put("date", getDatewithFormat(4));
        List<HashMap<String, String>> taskDataList = new ArrayList<HashMap<String, String>>();
        taskDataList.add(task1);
        taskDataList.add(task2);
        taskDataList.add(task3);
        taskDataList.add(task4);
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.addTask(eventData, taskDataList);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.openEventCard(eventData);
        Assert.assertTrue(eventsPage.isTaskDisplayed(task1));
        Assert.assertTrue(eventsPage.isTaskDisplayed(task2));
        Assert.assertTrue(eventsPage.isTaskDisplayed(task3));
        Assert.assertTrue(eventsPage.isTaskDisplayed(task4));
        eventsPage.clickOnCloseEventCard();
    }

    @Test
    public void Event_017() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_017");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        HashMap<String, String> task1       = getMapFromData(testData.get("task1"));
        task1.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        List<HashMap<String, String>> taskDataList =  new ArrayList<HashMap<String, String>>();
        taskDataList.add(task1);
        eventsPage.addTask(eventData, taskDataList);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.openEventCard(eventData);
        Assert.assertTrue(eventsPage.isTaskDisplayed(task1));
        eventsPage.clickOnCloseEventCard();
    }

    @Test
    public void Event_016() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_016");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.changeStatus(eventData, "In Progress");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "In Progress"));
    }

    @Test
    public void Event_004() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_004");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.changeStatus(eventData, "Complete");
        eventsPage.deleteEvent(eventData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
    }

    @Test
    public void Event_003() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_003");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.deleteEvent(eventData);
        Assert.assertFalse(eventsPage.isEventDisplayed(eventData));
    }

    @Test
    public void Event_001() throws BiffException, IOException {
        basepage.refreshPage();
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_001");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDatewithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDatewithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
    }

    public String getCalendarCurrentMonthandYear() {
        String monthYear;
        Calendar c      = Calendar.getInstance();
        DateFormat df   = new SimpleDateFormat("MMM-yyyy");
        monthYear       = df.format(c.getTime());
        return monthYear;
    }
    public String getCalendarCurrentWeek() {
        String week     = "Current week  (";
        Calendar c      = Calendar.getInstance();
        int year        = c.get(Calendar.YEAR);
        c.set(Calendar.YEAR, year-1);
        c.add(Calendar.YEAR,1) ;
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // Print dates of the current week starting on Monday
        //DateFormat df = new SimpleDateFormat("EEE dd/MM/yyyy");
        DateFormat df  = new SimpleDateFormat("dd-MMM");
        week            =  week+df.format(c.getTime())+"  ";
        c.add(Calendar.DATE,6);
        week            =  week+df.format(c.getTime())+")";
        return week;
    }

    public String getCalendarCurrentQuarter() {
        String quar     ="";
        Calendar c      = Calendar.getInstance();
        int month       = c.get(Calendar.MONTH);
        if(month <3 ) {
            quar        = "Q1-"+Calendar.YEAR;
        } else if(month >=3 && month < 6) {
            quar        = "Q2-"+Calendar.YEAR;
        } else if(month >=6 && month < 9) {
            quar        = "Q3-"+Calendar.YEAR;
        } else if(month >= 9) {
            quar        = "Q4-"+Calendar.YEAR;
        }
        return quar;
    }

    public void createEventsFromScript() {
        String file = env.basedir+"/testdata/sfdc/eventtests/Event_Create_Script.txt";
        Report.logInfo("File :" +file);
        apex.runApexCodeFromFile(file, isPackageInstance());
        isEventCreateScriptExecuted = true;
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
