package com.gainsight.sfdc.customer360.test;

import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 24/12/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Alerts360Test extends BaseTest {
    String[] dirs = {"eventtests"};
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    private final String TEST_DATA_FILE = "testdata/sfdc/alerttests/Alert_360_Tests.xls";

    @BeforeClass
    public void setUp() {
        basepage.login();
    }

    /*
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "360_Alert_1")
    public void addAlert(HashMap<String , String> testData) {
        Customer360Page c360Page = basepage.clickOnC360Tab();
        Customer360Page c360Page = c360Tab.searchCustomer("", true);
        Retention360 ret = c360Page.clickOn360RetentionSection();
        if(testData.get("Alert_Data") != null) {
            ret.addAlert(getMapFromData(testData.get("Alert_Data")));
        } else {
            Assert.assertTrue(false, "Please check your data set");
        }
     Assert.assertTrue(ret.isAlertDisplayed(), "Checking is alert dispalyed");



    }
     */


    @AfterClass
    public void tearDown(){
        basepage.logout();
    }

}
