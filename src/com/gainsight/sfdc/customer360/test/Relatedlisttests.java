package com.gainsight.sfdc.customer360.test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer360.pages.Customer360Page;

import com.gainsight.sfdc.customer360.pages.RelatedList360;
import com.gainsight.sfdc.customer360.pages.SalesforceRecordForm;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

import com.sforce.soap.partner.sobject.SObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Relatedlisttests extends BaseTest {
    private final String TEST_DATA_FILE = "testdata/sfdc/relatedlist/data/RelatedList_360.xls";
    private final String CONTACT_SCRIPT_FILE = env.basedir+"/testdata/sfdc/relatedlist/scripts/Contact_RelatedList_View_Setup.txt";
    private final String EVENT_TASKS_CREATE_FILE = env.basedir+"/testdata/sfdc/eventtests/Event_Create_Script.txt";
    private final String EVENT_PICKLIST_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Event_PickList_Setup_Script.txt";
    private final String UI_VIEW_SCRIPT_FILE = env.basedir+"/testdata/sfdc/relatedlist/scripts/UI_View_Setup.txt";
    private final String USER_CREATE_UPDATE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";
    boolean taskScriptCreated = false;

    @BeforeClass
    public void setUp() {
        basepage.login();
        apex.runApexCodeFromFile(CONTACT_SCRIPT_FILE, isPackageInstance());
        apex.runApexCodeFromFile(USER_CREATE_UPDATE,isPackageInstance());
        apex.runApexCodeFromFile(EVENT_PICKLIST_SETUP_FILE, isPackageInstance());
        apex.runApexCodeFromFile(EVENT_TASKS_CREATE_FILE, isPackageInstance());
        apex.runApexCodeFromFile(UI_VIEW_SCRIPT_FILE, isPackageInstance());

    }

    public void createEventsFromScript() {
        String file = env.basedir+"/testdata/sfdc/eventtests/Event_Create_Script.txt";
        Report.logInfo("File :" +file);
        apex.runApexCodeFromFile(file, isPackageInstance());
        taskScriptCreated = true;
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_1")
    public void stdContactDataVerification(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        HashMap<String, String> colHeaders = getMapFromData(testData.get("TableHeader"));
        List<String> dataList =new ArrayList<String>();
        for(int i =1; i <= 10; i++) {
            if(testData.get("TableRow" + i) !=null) {
                String data = testData.get("TableRow" + i);
                dataList.add(data);
                Report.logInfo(data);
            }
        }
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, relatedListName), "verifying the table headers");
        for(String data : dataList) {
            Assert.assertTrue(rLPage.isDataDisplayed(relatedListName, data), "Verification of Data is table");
        }
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_3")
    public void stdContactAddFunc(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        Assert.assertTrue(sal.verifyRecordAddIsDisplayed(testData.get("ObjectId")));

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_4")
    public void stdContactViewFunc(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.viewRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_5")
    public void stdContactEditViewFunc(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.editRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordEditViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_1")
    public void custTasksAddFunc(HashMap<String, String> testData) {
        if(!taskScriptCreated) {
            createEventsFromScript();
        }
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        SObject[] a = soql.getRecords(query);
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Report.logInfo("Object ID : " +objectId);
        } else {
            Report.logInfo("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(sal.verifyRecordAddIsDisplayed(objectId));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_2")
    public void custObjTasksDataVerif(HashMap<String, String> testData) {
        if(!taskScriptCreated) {
            createEventsFromScript();
        }
        String relatedListName = testData.get("Section");
        HashMap<String, String> colHeaders = getMapFromData(testData.get("TableHeader"));
        List<String> dataList =new ArrayList<String>();
        for(int i =1; i <= 10; i++) {
            if(testData.get("TableRow" + i) !=null) {
                String data = testData.get("Values" + i);
                dataList.add(data);
                Report.logInfo(data);
            }
        }
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, relatedListName), "verifying the table headers");
        for(String data : dataList) {
            Assert.assertTrue(rLPage.isDataDisplayed(relatedListName, data), "Verification of Data in table");
        }
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_3")
    public void custObjEditVerif(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        SalesforceRecordForm salesPage = rLPage.editRecord(relatedListName, testData.get("Values1"));
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        SObject[] a = soql.getRecords(query);
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Report.logInfo("Object ID : " +objectId);
        } else {
            Report.logInfo("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(salesPage.verifyRecordEditViewIsDisplayed(objectId), "Verifying the Page Url is contact record view or not");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_4")
    public void custObjViewVerif(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        SalesforceRecordForm salesPage = rLPage.viewRecord(relatedListName, testData.get("Values1"));
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        if(!isPackageInstance()) {
            query = removeNameSpace(query);
        }
        SObject[] a = soql.getRecords(query);
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Report.logInfo("Object ID : " +objectId);
        } else {
            Report.logInfo("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(salesPage.verifyRecordViewIsDisplayed(objectId), "Verifying the Page Url is contact record view or not");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_6")
    public void standObjNoDataInfoVerif(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), true);
        RelatedList360 rLPage = cPage.clickOnRealtedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        Assert.assertTrue(rLPage.isNoDataMsgDisplayed(relatedListName));
    }

    @AfterMethod
    public void beInMainWindow() {
        basepage.switchToMainWindow();
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
