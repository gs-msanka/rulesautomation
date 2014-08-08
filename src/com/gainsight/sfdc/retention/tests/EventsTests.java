package com.gainsight.sfdc.retention.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.EventsPage;
import com.gainsight.sfdc.retention.pojos.Event;
import com.gainsight.sfdc.tests.BaseTest;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EventsTests extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    Calendar c = Calendar.getInstance();
    Boolean isEventCreateScriptExecuted = false;

    String EVENT_PICKLIST_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Event_PickList_Setup_Script.txt";
    String USER_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";
    String PLAYBOOK_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Playbooks_Create_Script.txt";
    String EVENT_OBJECT = "JBCXM__CSEvent__c";
    String TASKS_OBJECT = "JBCXM__CSTask__c";

    @BeforeClass
    public void setUp() {
        userLocale           = soql.getUserLocale();
        apex.runApex(resolveStrNameSpace("DELETE [SELECT ID FROM JBCXM__CSEvent__c LIMIT 8000];"));
        apex.runApex(resolveStrNameSpace("DELETE [SELECT ID FROM JBCXM__CSTask__c LIMIT 8000];"));
        basepage.login();
        apex.runApexCodeFromFile(EVENT_PICKLIST_SETUP_FILE, isPackageInstance());
        apex.runApexCodeFromFile(USER_SETUP_FILE);
        apex.runApexCodeFromFile(PLAYBOOK_SETUP_FILE, isPackageInstance());
    }

    @AfterMethod
    private void refresh() {
        basepage.refreshPage();
    }

    @Test
    public void Event_07() {
        if(!isEventCreateScriptExecuted) {
            createEventsFromScript();
        }
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
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_030");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(365));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));

        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursYearly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_31() throws IOException, BiffException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_031");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(300));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));

        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursYearlyNth' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_33() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_033");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.changeStatus(eventData, "In Progress");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "In Progress"), "Verifying wether the status of the event changed to In progress.");
        eventsPage.changeStatus(eventData, "Complete");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "Complete"), "Verifying the status of event is complete or not");
    }

    @Test
    public void Event_32() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_032");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(2));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        eventsPage.deleteEvent(eventData);
        Assert.assertFalse(eventsPage.isEventDisplayed(eventData), "Verifying weather Event Deleted successfully.");
    }

    @Test
    public void Event_29() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_029");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(60));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursMonthlyNth' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount;
        recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_28() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_028");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(40));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursMonthly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_27() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_027");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(3));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursWeekly' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_26() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_026");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(4));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursDaily' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_25() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_025");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        eventData.put("startdate", getDateWithFormat(0));
        eventData.put("enddate", getDateWithFormat(1));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        Assert.assertTrue(eventsPage.verifyisRecurringEvent(eventData));
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursEveryWeekDay' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount = getQueryRecordCount(resolveStrNameSpace(query));
        Assert.assertEquals(1, recordCount);
    }

    @Test
    public void Event_24() throws BiffException, IOException {
        EventsPage eventsPage                       = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        List<HashMap<String, String>> taskDataList  =  new ArrayList<HashMap<String, String>>();
        HashMap<String, String> testData            = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_024");
        HashMap<String, String> eventData           = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData            = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        HashMap<String, String> task1               = getMapFromData(testData.get("task1"));
        task1.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        taskDataList.add(task1);
        eventsPage.addTask(eventData, taskDataList);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData), "Checking weather the event is present.");
        eventsPage.openEventCard(eventData);
        Assert.assertTrue(eventsPage.isTaskDisplayed(task1), "Checking weather task is created successfully");
        eventsPage.deleteTask(task1);
        eventsPage.clickOnUpdateEvent(eventData);
        basepage.refreshPage(); //stale
        eventsPage.openEventCard(eventData);
        Assert.assertFalse(eventsPage.isTaskDisplayed(task1), "Checking weather task deletion successful");
        eventsPage.clickOnCloseEventCard();
    }

    @Test
    public void Event_18() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_018");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        HashMap<String, String> task1       = getMapFromData(testData.get("task1"));
        task1.put("date", getDateWithFormat(1));
        HashMap<String, String> task2       = getMapFromData(testData.get("task2"));
        task2.put("date", getDateWithFormat(2));
        HashMap<String, String> task3       = getMapFromData(testData.get("task3"));
        task3.put("date", getDateWithFormat(3));
        HashMap<String, String> task4       = getMapFromData(testData.get("task4"));
        task4.put("date", getDateWithFormat(4));
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
    public void Event_17() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_017");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        HashMap<String, String> task1       = getMapFromData(testData.get("task1"));
        task1.put("date", getDateWithFormat(0));
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
    public void Event_16() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_016");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.changeStatus(eventData, "In Progress");
        Assert.assertTrue(eventsPage.verifyEventCardStatus(eventData, "In Progress"));
    }

    @Test
    public void Event_04() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_004");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.changeStatus(eventData, "Complete");
        eventsPage.deleteEvent(eventData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
    }

    @Test
    public void Event_03() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_003");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
        eventsPage.addEventandTask(eventData, taskData);
        Assert.assertTrue(eventsPage.isEventDisplayed(eventData));
        eventsPage.deleteEvent(eventData);
        Assert.assertFalse(eventsPage.isEventDisplayed(eventData));
    }

    @Test
    public void Event_01() throws BiffException, IOException {
        EventsPage eventsPage               = basepage.clickOnRetentionTab().clickOnEventsTab();
        eventsPage.waitTillEventCardsLoad();
        HashMap<String, String> testData    = testDataLoader.getDataFromExcel(TESTDATA_DIR+"EventsTests.xls", "Event_001");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0));
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
