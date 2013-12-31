package com.gainsight.sfdc.customer360.test;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Retention360;
import com.gainsight.sfdc.retention.pojos.AlertCardLabel;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

public class Alerts360Test extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    private final String TEST_DATA_FILE = "testdata/sfdc/alerttests/Alert_360_Tests.xls";
    String customerName = "Galbreath Co";
    Customer360Page c360Page;
    Retention360 ret;


    @BeforeClass
    public void setUp() {
        basepage.login();
        c360Page    = basepage.clickOnC360Tab().searchCustomer(customerName, true);
        ret         = new Retention360("Alerts Page");
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
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        ret.isAlertDisplayed(alertData, alabel);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_2")
    public void deleteAlert(HashMap<String , String> testData) {
        HashMap<String, String> alertData = getMapFromData(testData.get("Alert"));
        AlertCardLabel alabel = new AlertCardLabel();
        alabel.setLabel5("Alert ASV");
        alabel.setLabel4("Reason");
        alabel.setLabel3("Type");
        alabel.setLabel2("Severity");
        alabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
        ret.deleteAlert(alertData, alabel);
        Assert.assertEquals(false, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert is deleted successfully");
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
        ret.addAlert(alertData);
        alertData.put(alabel.getLabel1(), alertData.get("status"));
        alertData.put(alabel.getLabel2(), alertData.get("severity"));
        alertData.put(alabel.getLabel3(), alertData.get("type"));
        alertData.put(alabel.getLabel4(), alertData.get("reason"));
        alertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(alertData,alabel), "Checking weather alert created successfully");
        ret.updateAlert(alertData, updatedAlertData, alabel);
        updatedAlertData.put(alabel.getLabel1(), alertData.get("status"));
        updatedAlertData.put(alabel.getLabel2(), alertData.get("severity"));
        updatedAlertData.put(alabel.getLabel3(), alertData.get("type"));
        updatedAlertData.put(alabel.getLabel4(), alertData.get("reason"));
        updatedAlertData.put(alabel.getLabel5(), alertData.get("asv"));
        Assert.assertEquals(true, ret.isAlertDisplayed(updatedAlertData,alabel), "Checking weather alert is updated successfully");
    }



    @AfterClass
    public void tearDown(){
        basepage.logout();
    }

}
