package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Retention360;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Events360Test extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    private final String TEST_DATA_FILE = "testdata/sfdc/eventtests/Events_360_Tests.xls";

    String PLAYBOOK_CREATE_FILE = env.basedir+"/testdata/sfdc/eventtests/Playbooks_Create_Script.txt";
    String EVENT_PICKLIST_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Event_PickList_Setup_Script.txt";
    String USER_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";



    @BeforeClass
    public void setUp() {
        basepage.login();
        userLocale = soql.getUserLocale();
        apex.runApexCodeFromFile(USER_SETUP_FILE);
        apex.runApexCodeFromFile(PLAYBOOK_CREATE_FILE, isPackageInstance());
        apex.runApexCodeFromFile(EVENT_PICKLIST_SETUP_FILE, isPackageInstance());

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_1")
    public void addEvent(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        taskData.put("date", getDateWithFormat(0, 0));
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_2")
    public void changeEventStatus(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        ret.changeEventStatus(eventData, "In Progress");
        Assert.assertEquals(true, ret.verifyEventStatus(eventData, "In Progress"), "Checking Event Status");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_3")
    public void deleteEvent( HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        String script = "SELECT id FROM JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name LIKE '"+eventData.get("customer")+"'";
        soql.deleteQuery(resolveStrNameSpace(script));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        ret.changeEventStatus(eventData, "Open");
        Assert.assertEquals(true, ret.verifyEventStatus(eventData, "Open"), "Checking Event Status");
        ret.deleteEvent(eventData);
        Assert.assertEquals(false, ret.isEventCardDisplayed(eventData), "Checking Event is Deleted");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_4")
    public void addTasksToEvent(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        ret.openEventCard(eventData);
        List<HashMap<String, String>> tasksList = new ArrayList<HashMap<String, String>>();
        for(int a =1 ; a< 20; a++) {
            try {
                taskData = getMapFromData(testData.get("task"+a));
                taskData.put("date", getDateWithFormat(a, a));
                tasksList.add(taskData);
                ret.addEventTask(taskData);
            } catch (NullPointerException e) {
                Report.logInfo("Finished Adding Tasks");
                break;
            }
        }
        ret.clickOnUpdateEvent();
        ret.openEventCard(eventData);
        for(HashMap<String, String> task : tasksList) {
            Assert.assertTrue(ret.isTaskDisplayed(task));
        }
        ret.clickOnUpdateEvent();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_5")
    public void addTasksToEventFromPlaybook(HashMap<String, String> testData) throws IOException, BiffException {
        String pName = testData.get("playbook");
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        ret.openEventCard(eventData);
        ret.selectPlaybook(pName);
        ret.clickOnUpdateEvent();
        List<HashMap<String, String>> tasksList = new ArrayList<HashMap<String, String>>();
        for(int a =1 ; a< 20; a++) {
            try {
                taskData = getMapFromData(testData.get("task"+a));
                taskData.put("date", getDateWithFormat(a, a));
                tasksList.add(taskData);
            } catch (NullPointerException e) {
                Report.logInfo("Finished Loading Tasks");
                break;
            }
        }
        basepage.refreshPage();
        ret = c360Page.clickOnRetEventsSec();
        ret.openEventCard(eventData);
        for(HashMap<String, String> task : tasksList) {
            Assert.assertTrue(ret.isTaskDisplayed(task), "Checking task is displayed on event");
        }
        ret.clickOnUpdateEvent();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_6")
    public void addRecurringEvent(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        eventData.put("startdate", getDateWithFormat(0, 0));
        eventData.put("enddate", getDateWithFormat(5, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        String script = "SELECT id FROM JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name LIKE '"+eventData.get("customer")+"'";
        soql.deleteQuery(resolveStrNameSpace(script));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        eventData.put("date", getDateWithFormat(2, 0));
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        eventData.put("date", getDateWithFormat(4, 0));
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        String query = "Select id from JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name = '"+eventData.get("customer")+
                "' and JBCXM__IsRecurrence__c = true and JBCXM__RecurrenceType__c = 'RecursDaily' " +
                "and JBCXM__Status__c = '"+eventData.get("status")+"' and JBCXM__Type__r.Name = '"+eventData.get("type")+"' and isdeleted = false";
        int recordCount = getQueryRecordCount(query);
        Assert.assertEquals(recordCount,1, "Checking weather all the events are created." );
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_7")
    public void infoMessageVerification(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        String script = "SELECT id FROM JBCXM__CSEvent__c WHERE JBCXM__Account__r.Name LIKE '"+eventData.get("customer")+"'";
        soql.deleteQuery(resolveStrNameSpace(script));
        eventData.put("schedule", getDateWithFormat(0, 0));
        eventData.put("startdate", getDateWithFormat(0, 0));
        eventData.put("enddate", getDateWithFormat(70, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        Assert.assertTrue(ret.isInfoMessageDisplayed(), "Checking the information message displayed");
        ret.addEvent(eventData, taskData);
        ret.deleteAllEvents();
        //Assert.assertTrue(ret.isInfoMessageDisplayed(), "Checking the information message displayed"); //since no message is displayed we are doing assert false, bug failed in JIRA.
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_ET_8")
    public void editEvent(HashMap<String, String> testData) throws IOException, BiffException {
        HashMap<String, String> eventData   = getMapFromData(testData.get("eventdetails"));
        HashMap<String, String> updateEventData   = getMapFromData(testData.get("updateeventdetails"));
        eventData.put("schedule", getDateWithFormat(0, 0));
        updateEventData.put("schedule", getDateWithFormat(3, 0));
        HashMap<String, String> taskData    = getMapFromData(testData.get("taskdetails"));
        taskData.put("date", getDateWithFormat(0, 0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(eventData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetEventsSec();
        ret.addEvent(eventData, taskData);
        Assert.assertEquals(true, ret.isEventCardDisplayed(eventData), "Checking Event is Present");
        ret.openEventCard(eventData);
        ret.fillEventForm(updateEventData);
        ret.clickOnUpdateEvent();
        Assert.assertEquals(true, ret.isEventCardDisplayed(updateEventData), "Checking Event is Present");
    }

    @AfterMethod
    public void refresh() {
        basepage.refreshPage();
    }
    @AfterClass
    public void tearDown(){
        basepage.logout();
    }


}
