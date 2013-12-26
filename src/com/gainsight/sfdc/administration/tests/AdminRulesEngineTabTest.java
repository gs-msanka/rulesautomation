package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminRulesEngineTab;
import com.gainsight.sfdc.administration.pages.AdminTransactionsTab;
import com.gainsight.sfdc.retention.pages.PlayBooksPage;
import com.gainsight.sfdc.salesforce.pages.CreateSalesforceUsers;
import com.gainsight.sfdc.tests.BaseTest;

public class AdminRulesEngineTabTest extends BaseTest {
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		basepage.login();
	}
	
	
	@Test(priority=1)                         //Create New Rules    
	public void testAdminNewRuleTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminRulesEngineTab");
		createNewRule(testData);
	}
	
	private AdminRulesEngineTab createNewRule(HashMap<String, String> testData) {
		HashMap<String, String> data = getMapFromData(testData.get("createRuleForEqualsWithUsers"));
		
		String firstName = data.get("firstName");
		String lastName = data.get("lastName");
		String email = data.get("email");
		String userLicense = data.get("userLicense");
		String role = data.get("role");
		/*CreateSalesforceUsers crtUsers = new CreateSalesforceUsers();
		crtUsers.createUsers(firstName, lastName, email, userLicense, role);
		
		
		PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
       HashMap<String, String> pbData = getMapFromData(testData.get("playbookdetails"));
        HashMap<String, String> taskData = getMapFromData(testData.get("taskdetails"));
        pbPage.addplaybook(pbData, taskData);
       Assert.assertEquals(true, pbPage.isplaybookpresent(pbData.get("playbookname")));
       Assert.assertEquals(true, pbPage.isTaskPresent(taskData));
       Assert.assertEquals(true, pbPage.isAllPlaybook(pbData.get("playbookname")));
	    */    
	    String ruleTitle = data.get("ruleTitle");
		String selectSeverity = data.get("selectSeverity");
		String alertType = data.get("alertType");
		String status = data.get("status");
		String reason = data.get("reason");
		String selectPlaybook = data.get("selectPlaybook");
		String taskOwner = data.get("taskOwner");
		String defaultTaskOwner = data.get("defaultTaskOwner");
		String selectActivity = data.get("selectActivity");
		String selectParity = data.get("selectParity");
	    String percent     = data.get("percent");
	    String selectMonths = data.get("selectMonths");
	    String  sumValue   = data.get("sumValue");
	    //String rName = data.get("rName");
		AdminRulesEngineTab adRulsEng = basepage.clickOnAdminTab().clickOnRulesEngineSubTab();	
		adRulsEng.createNewRule(ruleTitle, selectSeverity, alertType, status , reason, 
				         selectPlaybook, taskOwner, defaultTaskOwner , selectActivity, selectParity, percent ,
				                   selectMonths,sumValue, firstName,lastName,email,userLicense,role);
		String crtRuleEng = ruleTitle+"|"+ selectSeverity +"|"+ selectPlaybook +"|"+ reason;
		//Assert.assertEquals(true, adRulsEng.isAlertRuleCreated(crtRuleEng,ruleTitle));
		return adRulsEng;
	}
	    @Test(priority=2)                         //Edit  Rules    
	public void testAdminEditRuleTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminRulesEngineTab");
		editAlertRules(testData.get("editRuleForEqualsWithUsers"));
	}
	private AdminRulesEngineTab editAlertRules(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
	
		String ruleTitle = data.get("ruleTitle");
		String selectSeverity = data.get("selectSeverity");
		String alertType = data.get("alertType");
		String status = data.get("status");
		String reason = data.get("reason");
		String selectPlaybook = data.get("selectPlaybook");
		String taskOwner = data.get("taskOwner");
		String selectActivity = data.get("selectActivity");
		String selectParity = data.get("selectParity");
	    String percent     = data.get("percent");
	    String selectMonths = data.get("selectMonths");
	    String  sumValue   = data.get("sumValue");
		AdminRulesEngineTab adRulsEng = basepage.clickOnAdminTab().clickOnRulesEngineSubTab();	
		adRulsEng.editAlertRules(ruleTitle, selectSeverity, alertType, status , reason, 
				         selectPlaybook, taskOwner, selectActivity, selectParity, percent ,
				                   selectMonths,sumValue );//,firstName,lastName,email,userLicense,role);
		return adRulsEng;
	}
	@Test(priority=3)                         //delete  Rules    
	public void testAdmindeleteRuleTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "AdminRulesEngineTab");
		deleteAlertRules(testData.get("deleteAlertRule"));
	}
	
	private AdminRulesEngineTab deleteAlertRules(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		String ruleTitle = data.get("ruleTitle");
		
		AdminRulesEngineTab adRulsEng = basepage.clickOnAdminTab().clickOnRulesEngineSubTab();	
		adRulsEng.deleteAlertRules(ruleTitle);
		return adRulsEng;
	
	}
	
			
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
