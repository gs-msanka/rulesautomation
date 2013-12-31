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
        AlertCardLabel alertCardLabel = new AlertCardLabel();
        alertCardLabel.setLabel5("Alert ASV");
        alertCardLabel.setLabel4("Reason");
        alertCardLabel.setLabel3("Type");
        alertCardLabel.setLabel2("Severity");
        alertCardLabel.setLabel1("Status");
        alertData.put("date", getDatewithFormat(0));
        if(testData.get("Alert") != null) {
            ret.addAlert(alertData);
        } else {
            Assert.assertTrue(false, "Please check your data set");
        }
     ret.isAlertDisplayed(alertData, alertCardLabel);
    }



    @AfterClass
    public void tearDown(){
        basepage.logout();
    }

}
