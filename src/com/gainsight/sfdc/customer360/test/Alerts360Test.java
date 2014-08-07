package com.gainsight.sfdc.customer360.test;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Retention360;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Alerts360Test extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    private final String TEST_DATA_FILE = "testdata/sfdc/alerttests/Alert_360_Tests.xls";
    String ALERT_SETUP_SCRIPT_FILE = env.basedir+"/testdata/sfdc/alerttests/Alert_360_Setup_Script.txt";
    String ALERT_OBJECT = "JBCXM__Alert__c";
    String ALERTS_DELETE_SCRIPT = "DELETE [SELECT ID FROM JBCXM__Alert__c];";
    String PLAYBOOKS_DELETE_SCRIPT = "DELETE [SELECT ID FROM JBCXM__Playbook__c];";
    String PLAYBOOK_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/Playbooks_Create_Script.txt";
    String USER_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";

    @BeforeClass
    public void setUp() throws IOException {
        basepage.login();
        userLocale = soql.getUserLocale();
        DataETL dataETL = new DataETL();
        apex.runApexCodeFromFile(USER_SETUP_FILE);
        dataETL.cleanUp(resolveStrNameSpace(ALERT_OBJECT), null);
        apex.runApex(resolveStrNameSpace(PLAYBOOKS_DELETE_SCRIPT));
        apex.runApexCodeFromFile(PLAYBOOK_SETUP_FILE, isPackageInstance());
        apex.runApexCodeFromFile(ALERT_SETUP_SCRIPT_FILE, isPackageInstance());

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_1")
    public void addAlert(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret = ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertTrue(ret.isAlertDisplayed(alertData, alabel), "Checking alert creation is successful");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_2")
    public void deleteAlert(HashMap<String , String> testData) {
        apex.runApex(resolveStrNameSpace(ALERTS_DELETE_SCRIPT));
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData, alabel), "Checking weather alert created successfully");
        ret.deleteAlert(alertData, alabel);
        Assert.assertEquals(false, ret.isAlertDisplayed(alertData, alabel), "Checking weather alert is deleted successfully");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_3")
    public void updateAlerts(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        HashMap<String, String> updatedAlertData = getMapFromData(testData.get("Updated_Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData, alabel), "Checking weather alert created successfully");
        ret.updateAlert(alertData, updatedAlertData, alabel);
        updatedAlertData.put(alabel.getLabel1(), updatedAlertData.get("status"));
        updatedAlertData.put(alabel.getLabel2(), updatedAlertData.get("severity"));
        updatedAlertData.put(alabel.getLabel3(), updatedAlertData.get("type"));
        updatedAlertData.put(alabel.getLabel4(), updatedAlertData.get("reason"));
        updatedAlertData.put(alabel.getLabel5(), updatedAlertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(updatedAlertData,alabel), "Checking weather alert is updated successfully");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_4")
    public void addTasksOnAlert(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        List<HashMap<String, String>> taskDataList = new ArrayList<HashMap<String, String>>();
        AlertCardLabel alabel = new AlertCardLabel();
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret.addAlert(alertData);
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData, alabel), "Checking weather alert created successfully");
        ret.openAlertCardEditMode(alertData, alabel);
        for(int a=1;a <= 20 ; a++ ) {
            if(testData.get("Task"+a) != null) {
                HashMap<String, String> taskData = getMapFromData(testData.get("Task"+a));
                taskData.put("date", getDatewithFormat(a));
                taskDataList.add(taskData);
            }
        }
        ret.addTaksOnAlert(taskDataList);
        ret.openAlertCardEditMode(alertData, alabel);
        for(HashMap<String, String> taskData : taskDataList)  {
            Assert.assertTrue(ret.isAlertTaskDisplayed(taskData));
        }
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_5")
    public void closeAlert(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        HashMap<String, String> updatedAlertData = getMapFromData(testData.get("Updated_Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
        ret.updateAlert(alertData, updatedAlertData, alabel);
        updatedAlertData.put(alabel.getLabel1(), updatedAlertData.get("status"));
        updatedAlertData.put(alabel.getLabel2(), updatedAlertData.get("severity"));
        updatedAlertData.put(alabel.getLabel3(), updatedAlertData.get("type"));
        updatedAlertData.put(alabel.getLabel4(), updatedAlertData.get("reason"));
        updatedAlertData.put(alabel.getLabel5(), updatedAlertData.get("asv"));
        Assert.assertEquals(false, ret.isAlertDisplayed(updatedAlertData,alabel), "Check to find closed alert is displayed in 360 page");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_6")
    public void addTasksOnAlertFromPlaybook(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        List<HashMap<String, String>> taskDataList = new ArrayList<HashMap<String, String>>();
        AlertCardLabel alabel = new AlertCardLabel();
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        ret.addAlert(alertData);
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
        ret.openAlertCardEditMode(alertData, alabel);
        for(int a=1;a <= 20 ; a++ ) {
            if(testData.get("Task"+a) != null) {
                HashMap<String, String> taskData = getMapFromData(testData.get("Task"+a));
                taskData.put("date", getDatewithFormat(a));
                taskDataList.add(taskData);
            }
        }
        ret.selectAlertPlaybook(testData.get("Playbook"));
        ret.closeAlertView();
        ret.openAlertCardEditMode(alertData, alabel);
        for(HashMap<String, String> taskData : taskDataList)  {
            Assert.assertTrue(ret.isAlertTaskDisplayed(taskData));
        }
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_7")
    public void informationMessageCheck(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        String script = "SELECT id FROM JBCXM__Alert__c WHERE JBCXM__Account__r.Name LIKE '"+alertData.get("customer")+"'";
        soql.deleteQuery(resolveStrNameSpace(script));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        Assert.assertTrue(ret.isAlertInfoMsgDisplayed(), "Checking No Alerts Found Message Display");
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_8")
    public void deleteAlerts(HashMap<String , String> testData) {
        apex.runApex(resolveStrNameSpace(ALERTS_DELETE_SCRIPT));
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(alertData.get("customer"), false, false);
        Retention360 ret         = c360Page.clickOnRetAlertsSec();
        Assert.assertTrue(ret.isAlertInfoMsgDisplayed(), "Checking No Alerts Found Message Display");
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
        ret.deleteAlert(alertData, alabel);
        Assert.assertEquals(false, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert deleted successfully");
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
