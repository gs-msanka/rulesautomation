package com.gainsight.sfdc.retention.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.AlertsPage;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    String userLocale = "en_IN";

    @BeforeClass
    public void setUp() {
        basepage.login();
        String script = "List<JBCXM__Alert__c> alertsList = [SELECT ID FROM JBCXM__Alert__c]; DELETE alertsList;";
        if(!isPackageInstance()) {
            script = removeNameSpace(script);
        }
        apex.runApex(script);
        userLocale = soql.getUserLocale();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Alert_Test_1")
    public  void addAlert(HashMap<String, String> testData) {
        AlertsPage alertsPage = basepage.clickOnRetentionTab().clickOnAlertsTab();
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        alertData.put("date", getDatewithFormat(0));
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
        alertData.put("date", getDatewithFormat(0));
        List<HashMap<String, String>> tasksList = new ArrayList<HashMap<String, String>>();
        try {
            for(int i=1; i < 20; i++) {
                HashMap<String, String> taskData = getMapFromData(testData.get("Task"+i));
                taskData.put("date", getDatewithFormat(i));
                tasksList.add(taskData);
            }
        } catch (Exception e) {
            Report.logInfo("Tasks added to list");
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

    public String getDatewithFormat(int i) {
        String date                 = null;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, i);
        if(userLocale.contains("en_US")) {
            DateFormat dateFormat   = new SimpleDateFormat("MM/dd/yyyy");
            date = dateFormat.format(c.getTime());

        } else if(userLocale.contains("en_IN")) {
            DateFormat dateFormat   = new SimpleDateFormat("dd/MM/yyyy");
            date = dateFormat.format(c.getTime());
        }
        Report.logInfo(String.valueOf(date));
        return date;
    }



    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
