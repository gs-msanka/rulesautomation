package com.gainsight.sfdc.customer.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.TimeZone;

public class CustomerTest extends BaseTest {

    private final String TEST_DATA_FILE             = "testdata/sfdc/customers/data/Customers_Data.xls";
    private final String STATE_PRESERVATION_QUERY   = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name='CustomersTab'];";
    private final String ACC_SETUP_SCRIPT           = env.basedir+"/testdata/sfdc/customers/scripts/Cust_Account_Create.txt";
    private final String UIVIEW_SETUP_SCRIPT           = env.basedir+"/testdata/sfdc/customers/scripts/UIView_Create.txt";

    @BeforeClass
    public void setUp() {
        Report.logInfo("Started Customers Test Cases");
        basepage.login();
        apex.runApex(resolveStrNameSpace(STATE_PRESERVATION_QUERY));
        apex.runApexCodeFromFile(ACC_SETUP_SCRIPT, isPackage);
        apex.runApexCodeFromFile(UIVIEW_SETUP_SCRIPT, isPackage);
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-1")
    public  void addCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        customersPage = customersPage.addCustomer(testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments"));
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|" +
                testData.get("Status") + "|" + testData.get("Stage") + "|" +
                testData.get("Comments")), "Checking Customer add successfully.");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-2")
    public  void editCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        HashMap<String, String> customerData = getMapFromData(testData.get("Customer"));
        HashMap<String, String> customerUpdatedData = getMapFromData(testData.get("Updated"));
        customersPage = customersPage.addCustomer(customerData.get("Customer"), customerData.get("Status"), customerData.get("Stage"), customerData.get("Comments"));
        Assert.assertTrue(customersPage.isDataPresentInGrid(customerData.get("Customer") + "|" + customerData.get("Status") + "|" + customerData.get("Stage") + "|" + customerData.get("Comments")));
        customersPage = customersPage.editCustomer(customerUpdatedData.get("Customer"), customerUpdatedData.get("Status"), customerUpdatedData.get("Stage"), customerUpdatedData.get("Comments"));
        Assert.assertTrue(customersPage.isDataPresentInGrid(customerUpdatedData.get("Customer") + "|" +
                customerUpdatedData.get("Status") + "|" + customerUpdatedData.get("Stage") + "|" +
                customerUpdatedData.get("Comments")), "Checking is customer details are updated.");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-3")
    public  void deleteCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        customersPage = customersPage.addCustomer(testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments"));
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|" +
                testData.get("Status") + "|" + testData.get("Stage") + "|" +
                testData.get("Comments")), "Checking customer added successfully.");
        customersPage.deleteCustomer(testData.get("Customer"));
        Assert.assertFalse(customersPage.isDataPresentInGrid(testData.get("Customer") + "|" + testData.get("Status") +
                "|" + testData.get("Stage") + "|" + testData.get("Comments")), "Checking is customer delete successful.");

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-4")
    public  void applySingleTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|"+arrayToString(tags)));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-5")
    public  void applyMultipleTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|"+arrayToString(tags)));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-6")
    public  void updateTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|"+arrayToString(tags)));
        customersPage = customersPage.applyTags(testData.get("Customer"), testData.get("UpdatedTags").split("\\|"));
        Assert.assertTrue(customersPage.isDataPresentInGrid(testData.get("Customer") + "|"+arrayToString(testData.get("UpdatedTags").split("\\|"))));
    }

    //@Test
    public void dataExportMessageCheck() {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView("Standard View");

    }


    private String arrayToString(String[] values) {
        String value = "";
        for(String s : values) {
            value +=s+";";
        }
        value = value.substring(0, value.length()-1);
        Report.logInfo("Value : "+value);
        return value;
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }

}
