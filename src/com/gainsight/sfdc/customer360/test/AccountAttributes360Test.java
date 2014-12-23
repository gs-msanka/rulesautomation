package com.gainsight.sfdc.customer360.test;

import com.gainsight.sfdc.customer360.pages.Attributes;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by gainsight on 08/01/14.
 */
public class AccountAttributes360Test extends BaseTest {

    private final String TEST_DATA_FILE                 = "testdata/sfdc/accountattributes/data/AccAtt_360_Tests.xls";
    private final String ATTRIBUTES_UIVIEW_CREATE_FILE  = env.basedir+"/testdata/sfdc/accountattributes/scripts/Attributes_UIView_Create.txt";
    private final String ACCOUNT_CREATE_FILE            = env.basedir+"/testdata/sfdc/accountattributes/scripts/Account_Create.txt";
    private final String ATTRIBUTES_360_SEC_ENABLE_FILE = env.basedir+"/testdata/sfdc/accountattributes/scripts/Attributes_360Sec_Enable.txt";

    @BeforeClass
    public void setUp() {
        basepage.login();
        apex.runApexCodeFromFile(ACCOUNT_CREATE_FILE, isPackage);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ACC_ATT_1")
    public void uiViewNotConfMsgVerification(HashMap<String, String> testData) {
        apex.runApexCodeFromFile(ATTRIBUTES_360_SEC_ENABLE_FILE, isPackage);
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Attributes att = c360Page.clickOnAccAttributesSec();
        Assert.assertTrue(att.isNoUIViewConfMsgDisplayed(), "Verifying No UI configured message displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ACC_ATT_2")
    public void accAttributesFieldsOrder(HashMap<String, String> testData) {
        apex.runApexCodeFromFile(ATTRIBUTES_UIVIEW_CREATE_FILE, isPackage);
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Attributes att = c360Page.clickOnAccAttributesSec();
        String[]  expValues = testData.get("Values").split("\\|");
        Assert.assertTrue(att.isFieldsDisplayedInOrder(expValues), "Verifying the order of fields Displayed");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "ACC_ATT_3")
    public void accAttributesFieldValues(HashMap<String, String> testData) {
        apex.runApexCodeFromFile(ATTRIBUTES_UIVIEW_CREATE_FILE, isPackage);
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        Attributes att = c360Page.clickOnAccAttributesSec();
        HashMap<String, String> fieldDataMap  = getMapFromData(testData.get("Values"));
        Assert.assertTrue(att.isValuesForAccountAttDisplayed(fieldDataMap), "Checking the account attribute values");
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
