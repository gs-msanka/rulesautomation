package com.gainsight.sfdc.customer360.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gainsight.testdriver.Log;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.RelatedList360;
import com.gainsight.sfdc.customer360.pages.SalesforceRecordForm;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class Relatedlisttests extends BaseTest {
    private final String TEST_DATA_FILE                 = "testdata/sfdc/relatedlist/data/RelatedList_360.xls";
    private final String CONTACT_SCRIPT_FILE            = env.basedir+"/testdata/sfdc/relatedlist/scripts/Contact_RelatedList_View_Setup.txt";
    private final String UI_VIEW_SCRIPT_FILE1           = env.basedir+"/testdata/sfdc/relatedlist/scripts/UI_View_Setup1.txt";
    private final String UI_VIEW_SCRIPT_FILE2           = env.basedir+"/testdata/sfdc/relatedlist/scripts/UI_View_Setup2.txt";
    private final String USER_CREATE_UPDATE             = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";
    boolean taskScriptCreated = false;

    @BeforeClass
    public void setUp() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CONTACT_SCRIPT_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(USER_CREATE_UPDATE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UI_VIEW_SCRIPT_FILE1));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UI_VIEW_SCRIPT_FILE2));
        basepage.login();
    }

    public void createEventsFromScript() {
        String file = env.basedir+"/testdata/sfdc/eventtests/Event_Create_Script.txt";
        Log.info("File :" + file);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(file));
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
                Log.info(data);
            }
        }
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
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
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        Assert.assertTrue(sal.verifyRecordAddIsDisplayed(testData.get("ObjectId")));
        rLPage.closeWindow();

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_4")
    public void stdContactViewFunc(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.viewRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_5")
    public void stdContactEditViewFunc(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.editRecord(relatedListName, testData.get("Values"));
        Assert.assertTrue(salesPage.verifyRecordEditViewIsDisplayed(testData.get("ObjectId")), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_3")
    public void custObjEditVerification(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.editRecord(relatedListName, testData.get("Values1"));
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        SObject[] a = sfdc.getRecords(resolveStrNameSpace(query));
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Log.info("Object ID : " +objectId);
        } else {
            Log.info("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(salesPage.verifyRecordEditViewIsDisplayed(objectId), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_4")
    public void custObjViewVerification(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        SalesforceRecordForm salesPage = rLPage.viewRecord(relatedListName, testData.get("Values1"));
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        SObject[] a = sfdc.getRecords(resolveStrNameSpace(query));
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Log.info("Object ID : " +objectId);
        } else {
            Log.info("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(salesPage.verifyRecordViewIsDisplayed(objectId), "Verifying the Page Url is contact record view or not");
        rLPage.closeWindow();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_360_6")
    public void standObjNoDataInfoVerif(HashMap<String, String> testData) {
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        rLPage = rLPage.selectUIView(relatedListName, testData.get("UIView"));
        Assert.assertTrue(rLPage.isNoDataMsgDisplayed(relatedListName));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_1")
    public void custTasksAddFunc(HashMap<String, String> testData) {
        if(!taskScriptCreated) {
            createEventsFromScript();
        }
        String relatedListName = testData.get("Section");
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        SalesforceRecordForm sal = rLPage.clickOnAdd(relatedListName);
        String query = "SELECT ID FROM "+testData.get("ObjectId")+"";
        SObject[] a = sfdc.getRecords(resolveStrNameSpace(query));
        String objectId = "";
        if(a != null && a.length >0) {
            objectId = String.valueOf(a[0].getId()).substring(0,3);
            Log.info("Object ID : " +objectId);
        } else {
            Log.info("Failed to query data record");
            Assert.assertTrue(false, "Error With Data Configuration.");
        }
        Assert.assertTrue(sal.verifyRecordAddIsDisplayed(objectId));
        rLPage.closeWindow();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "RL_CUST_GSTASK_2")
    public void customObjTasksDataVerification(HashMap<String, String> testData) {
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
                Log.info(data);
            }
        }
        Customer360Page cPage  = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        RelatedList360 rLPage = cPage.clickOnRelatedListSec(relatedListName);
        Assert.assertTrue(rLPage.isTableHeadersExisting(colHeaders, relatedListName), "verifying the table headers");
        for(String data : dataList) {
            Assert.assertTrue(rLPage.isDataDisplayed(relatedListName, data), "Verification of Data in table");
        }
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
