package com.gainsight.sfdc.retention.tests;

import com.gainsight.sfdc.retention.pages.AlertsPage;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AlertsTests extends BaseTest {
    String[] dirs = {"alerttests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    Calendar c = Calendar.getInstance();
    Boolean isAlertCreateScriptExecuted = false;
    private final String TEST_DATA_FILE = "testdata/sfdc/alerttests/Alert_Tests.xls";
    String PLAYBOOKS_DELETE_SCRIPT = "DELETE [SELECT ID FROM JBCXM__Playbook__c];";
    String ALERTS_DELETE_SCRIPT = "DELETE [SELECT ID FROM JBCXM__Alert__c];";
    String USER_SETUP_FILE = env.basedir+"/testdata/sfdc/eventtests/User_Update_Create_Script.txt";
    @BeforeClass
    public void setUp() {
        userLocale = soql.getUserLocale();
        apex.runApexCodeFromFile(resolveStrNameSpace(USER_SETUP_FILE));
        apex.runApex(resolveStrNameSpace(ALERTS_DELETE_SCRIPT));
        apex.runApex(resolveStrNameSpace(PLAYBOOKS_DELETE_SCRIPT));

        basepage.login();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Alert_Test_1")
    public  void addAlert(HashMap<String, String> testData) {
        AlertsPage alertsPage = basepage.clickOnRetentionTab().clickOnAlertsTab();
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        alertData.put("date", getDateWithFormat(0));
        alertsPage.addAlert(alertData);
        AlertCardLabel alertCardLabel = new AlertCardLabel();
        alertCardLabel.setLabel4("Renewal Date");
        alertCardLabel.setLabel3("Alert ASV");
        alertCardLabel.setLabel2("Reason");
        alertCardLabel.setLabel1("Type");
        alertData.put("Type", alertData.get("type"));
        alertData.put("Reason", alertData.get("reason"));
        alertsPage.groupExpandOrCollapse(alertData.get("status"), true);
        boolean alertPresent = alertsPage.isAlertDisplayed(alertCardLabel, alertData);
        Assert.assertTrue(alertPresent);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Alert_Test_2")
    public  void addTasksOnAlert(HashMap<String, String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        alertData.put("date", getDateWithFormat(0));
        List<HashMap<String, String>> tasksList = new ArrayList<HashMap<String, String>>();
        for(int a=1;a <= 20 ; a++ ) {
            if(testData.get("Task"+a) != null) {
                HashMap<String, String> taskData = getMapFromData(testData.get("Task"+a));
                taskData.put("date", getDateWithFormat(a));
                tasksList.add(taskData);
            }
        }
        AlertsPage alertsPage = basepage.clickOnRetentionTab().clickOnAlertsTab();
        alertsPage.addAlertandTasks(alertData, tasksList);
        alertsPage.groupExpandOrCollapse(alertData.get("status"), true);
        AlertCardLabel alertCardLabel = new AlertCardLabel();
        alertCardLabel.setLabel4("Renewal Date");
        alertCardLabel.setLabel3("Alert ASV");
        alertCardLabel.setLabel2("Reason");
        alertCardLabel.setLabel1("Type");
        alertData.put("Type", alertData.get("type"));
        alertData.put("Reason", alertData.get("reason"));
        Assert.assertTrue(alertsPage.isAlertDisplayed(alertCardLabel, alertData));
        alertsPage.openAlertCard(alertData, alertCardLabel);
        for(HashMap<String, String> taskData : tasksList) {
            Assert.assertEquals(true, alertsPage.isTaskDisplayed(taskData));
        }
        alertsPage.closeAlertForm();
    }





    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
