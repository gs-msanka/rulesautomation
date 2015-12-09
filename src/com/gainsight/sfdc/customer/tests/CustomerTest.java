package com.gainsight.sfdc.customer.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class CustomerTest extends BaseTest {

    private final String TEST_DATA_FILE             = "testdata/sfdc/customers/data/Customers_Data.xls";
    private final String STATE_PRESERVATION_QUERY   = "DELETE [SELECT ID FROM JBCXM__StatePreservation__c Where Name='CustomersTab'];";
    private final String ACC_SETUP_SCRIPT           = Application.basedir+"/testdata/sfdc/customers/scripts/Cust_Account_Create.txt";
    private final String UI_VIEW_SETUP_SCRIPT       = Application.basedir+"/testdata/sfdc/customers/scripts/UIView_Create.txt";

    @BeforeClass
    public void setUp() {
        Log.info("Started Customers Test Cases");
        basepage.login();
        sfdc.runApexCode(resolveStrNameSpace(STATE_PRESERVATION_QUERY));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACC_SETUP_SCRIPT));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UI_VIEW_SETUP_SCRIPT));
    }

    @TestInfo(testCaseIds={"GS-58"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-1")
    public  void addCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        customersPage = customersPage.addCustomer(testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments"));
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(),
                new String[]{testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments")}), "Checking is customer details are updated.");
    }

    @TestInfo(testCaseIds={"GS-64"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-2")
    public  void editCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        HashMap<String, String> customerData = getMapFromData(testData.get("Customer"));
        HashMap<String, String> customerUpdatedData = getMapFromData(testData.get("Updated"));
        customersPage = customersPage.addCustomer(customerData.get("Customer"), customerData.get("Status"), customerData.get("Stage"), customerData.get("Comments"));
        customersPage.setCustomerNameFilter(customerData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(),
                new String[]{customerData.get("Customer"), customerData.get("Status"), customerData.get("Stage"), customerData.get("Comments")}), "Checking is customer details are updated.");

        customersPage = customersPage.editCustomer(customerUpdatedData.get("Customer"), customerUpdatedData.get("Status"), customerUpdatedData.get("Stage"), customerUpdatedData.get("Comments"));
        customersPage.setCustomerNameFilter(customerUpdatedData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(),
                new String[]{customerUpdatedData.get("Customer"), customerUpdatedData.get("Status"), customerUpdatedData.get("Stage"), customerUpdatedData.get("Comments")}), "Checking is customer details are updated.");
    }

    @TestInfo(testCaseIds={"GS-66"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-3")
    public  void deleteCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        customersPage = customersPage.addCustomer(testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments"));
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(),
                new String[]{testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments")}), "Checking is customer details are updated.");
        customersPage.deleteCustomer(testData.get("Customer"));
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertFalse(customersPage.isDataPresent(customersPage.getGridData(),
                new String[]{testData.get("Customer"), testData.get("Status"), testData.get("Stage"), testData.get("Comments")}), "Checking is customer details are updated.");
    }

    @TestInfo(testCaseIds={"GS-5096"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-4")
    public  void applySingleTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(), new String[]{testData.get("Customer"), arrayToString(tags)}), "Checking is customer details are updated.");
    }

    @TestInfo(testCaseIds={"GS-5097"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-5")
    public  void applyMultipleTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(), new String[]{testData.get("Customer"), arrayToString(tags)}), "Checking is customer details are updated.");
    }

    @TestInfo(testCaseIds={"GS-5098"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "TC-6")
    public  void updateTagToCustomer(HashMap<String, String> testData) {
        CustomersPage customersPage = basepage.clickOnCustomersTab().clickOnCustomersSubTab();
        customersPage = customersPage.selectUIView(testData.get("UIView"));
        String[] tags = testData.get("Tags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags);

        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(), new String[]{testData.get("Customer"), arrayToString(tags)}), "Checking is customer details are updated.");

        String[] tags1 = testData.get("UpdatedTags").split("\\|");
        customersPage = customersPage.applyTags(testData.get("Customer"), tags1);
        customersPage.setCustomerNameFilter(testData.get("Customer"));
        Assert.assertTrue(customersPage.isDataPresent(customersPage.getGridData(), new String[]{testData.get("Customer"), arrayToString(tags)+";"+arrayToString(tags1)}), "Checking is customer details are updated.");

    }

    private String arrayToString(String[] values) {
        String value = "";
        for(String s : values) {
            value +=s+";";
        }
        value = value.substring(0, value.length() - 1);
        Log.info("Value : " + value);
        return value;
    }
    @AfterClass
    public void tearDown() {
        basepage.logout();
    }

}
