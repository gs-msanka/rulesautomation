package com.gainsight.sfdc.customer360.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import jxl.read.biff.BiffException;

import com.gainsight.sfdc.administration.pages.AdminCustomersTab;
import com.gainsight.sfdc.customer.pages.CustomersPage;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360SummaryWidget;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



public class Customer360SummaryWidgetTest extends BaseTest {

	
	final static String  TEST_DATA_FILE              =  "testdata/sfdc/c360Summary/SummaryWidget_TestData/TestData/SummaryWidgetTestdata.xls";
	private final String CREATE_ACCNT_CASES_SCRIPT   = Application.basedir+"/testdata/sfdc/c360Summary/SummaryWidget_TestData/Scripts/Create_AccountandCases.txt";
	private final String DEFAULT_SUMMARY_WIDGET1     =  Application.basedir+"/testdata/sfdc/c360Summary/SummaryWidget_TestData/Scripts/Create_DefaultSummaryWidgets1.txt";
	private final String SUMMARY_WIDGET2             =  Application.basedir+"/testdata/sfdc/c360Summary/SummaryWidget_TestData/Scripts/Create_SummaryWidgets2.txt";
	private final String DEFAULT_SUMMARY_WIDGET3     =  Application.basedir+"/testdata/sfdc/c360Summary/SummaryWidget_TestData/Scripts/Create_SummaryWidgets3.txt";
	
	@BeforeClass
	public void setUp() throws Exception {
        /*Log.info("Starting Customer 360 Summary Widgets module Test Cases...");
        metadataClient.createNumberField("JBCXM__CustomerInfo__c", new String[]{"CustPercentage"}, true);
        addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"JBCXM__CustPercentage__c"});
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InRegions", new String[]{"India", "America", "England", "France"});
        addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"JBCXM__InRegions__c"});
        metadataClient.createPickListField("JBCXM__CustomerInfo__c", fields, true);
        metadataClient.createCurrencyField("JBCXM__CustomerInfo__c", new String[]{"CurrencyField"});
       addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"JBCXM__CurrencyField__c"});
        metadataClient.createFields("JBCXM__CustomerInfo__c", new String[]{"IsActive"}, true, false, false);
        addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"JBCXM__IsActive__c"});*/
    //   sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCNT_CASES_SCRIPT));
     //   sfdc.runApexCode(getNameSpaceResolvedFileContents(DEFAULT_SUMMARY_WIDGET1)); 
        basepage.login();
	}
	
	
    
	/*@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "DefaultSummaryWidget")
    public void widgetRule(HashMap<String, String> testData){
    	
    	Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
    	Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
    	HashMap<String, String> widgetPnl = getMapFromData(testData.get("widgetPnl"));
    	
    	Set<String> itr = widgetPnl.keySet();
    	for(String key : itr){
    		String val = widgetPnl.get(key);
    		Assert.assertTrue(sumWidget.verifyWidgetPanel(key, val));
    		System.out.println("Key value is:"+ key+"value is :"+val);
    	}
   	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=2)
	
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "lfetPanelVali")
	public void leftRule1(HashMap<String, String> testData){
		
		Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
		Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
		HashMap<String, String> leftPnl = getMapFromData(testData.get("leftWidgetPnl"));
		
		Set<String> itr = leftPnl.keySet();
		for(String key : itr){
			String val = leftPnl.get(key);
			Assert.assertTrue(sumWidget.verifyLeftPanel(key, val));
			System.out.println("Key value is:"+ key+"value is :"+val);
		}
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=3)
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Widget2")
    public void widgetRule2(HashMap<String, String> testData){
		 sfdc.runApexCode(getNameSpaceResolvedFileContents(SUMMARY_WIDGET2));
    	Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
    	Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
    	HashMap<String, String> widgetPnl = getMapFromData(testData.get("widgetPnl"));
    	
    	Set<String> itr = widgetPnl.keySet();
    	for(String key : itr){
    		String val = widgetPnl.get(key);
    		Assert.assertTrue(sumWidget.verifyWidgetPanel(key, val));
    		System.out.println("Key value is:"+ key+"value is :"+val);
    	}
   	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=4)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "LeftPanel001")
	public void leftRule2(HashMap<String, String> testData){
		
		Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
		Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
		HashMap<String, String> leftPnl = getMapFromData(testData.get("leftWidgetPnl"));
		
		Set<String> itr = leftPnl.keySet();
		for(String key : itr){
			String val = leftPnl.get(key);
			Assert.assertTrue(sumWidget.verifyLeftPanel(key, val));
			System.out.println("Key value is:"+ key+"value is :"+val);
		}
	}
	
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=5 )
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Widget3")
    public void widgetRule3(HashMap<String, String> testData){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(DEFAULT_SUMMARY_WIDGET3));
    	Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
    	Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
    	HashMap<String, String> widgetPnl = getMapFromData(testData.get("widgetPnl"));
    	
    	Set<String> itr = widgetPnl.keySet();
    	for(String key : itr){
    		String val = widgetPnl.get(key);
    		Assert.assertTrue(sumWidget.verifyWidgetPanel(key, val));
    		System.out.println("Key value is:"+ key+"value is :"+val);
    	}
   	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=6)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "lfetPanelVali")
	public void leftRule3(HashMap<String, String> testData){
		
		Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
		Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
		HashMap<String, String> leftPnl = getMapFromData(testData.get("leftWidgetPnl"));
		
		Set<String> itr = leftPnl.keySet();
		for(String key : itr){
			String val = leftPnl.get(key);
			Assert.assertTrue(sumWidget.verifyLeftPanel(key, val));
			System.out.println("Key value is:"+ key+"value is :"+val);
		}
	}*/
	
	
	/*@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=6)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_Summary")
	public void editSummary(HashMap<String, String> testData){
		
		Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
		Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
		//sumWidget.editSummary(Ke);
		HashMap<String, String> edtSumry = getMapFromData(testData.get("edit_Summary_Values"));
		
		Set<String> itr = edtSumry.keySet();
		for(String key : itr){
			String val = edtSumry.get(key);
			System.out.println("Val is:" + val);
			Assert.assertTrue(sumWidget.editSummary(key, val));
			System.out.println("Key value is:"+ key+"value is :"+val);
		}
		
	}*/
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Edit_Summary")
    public  void editSummary(HashMap<String, String> testData) {
		
		Customer360Page c360 = basepage.clickOnC360Tab().searchCustomer(testData.get("Search_Customer"), false, false);
		Customer360SummaryWidget sumWidget = c360.goToSummaryWidgetSection();
		HashMap<String, String> edtSumry = getMapFromData(testData.get("edit_Summary_Values"));
		sumWidget.editSummary(edtSumry.get("Status"), edtSumry.get("Stage"), edtSumry.get("Comments"));
		
		/*Assert.assertTrue(Customer360SummaryWidget.isDataPresentInGrid(customerData.get("Customer") + "|" + customerData.get("Status") + "|" + customerData.get("Stage") + "|" + customerData.get("Comments")));
                testData.get("Comments")), "Checking Customer add successfully.");*/
    }
	

	/*@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
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
    }*/

	
@AfterClass
public void tearDown() {
	basepage.logout();
}

}
	
	
	
	
	
	
	
	