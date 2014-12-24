package com.gainsight.sfdc.salesforce.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import jxl.read.biff.BiffException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.salesforce.pages.CreateSalesforceUsers;
import com.gainsight.sfdc.tests.BaseTest;

public class CreateSalesforceUsersTest extends BaseTest {
	
	
	String[] dirs = { "acceptancetests" };
	private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
			+ generatePath(dirs);

	@BeforeClass
	public void setUp() {
		Log.info("Starting  Test Case...");
		basepage.login();
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
	}
	
	
	@Test(priority=1)                    	// Create New User
	public void testSalesforceUsersTest() throws BiffException, IOException {
		HashMap<String, String> testData = testDataLoader.getDataFromExcel(
				TESTDATA_DIR + "AdministrationTestdata.xls", "salesforceUsers");
		createUsers(testData.get("createSalesforceUsers"));
	}
	
	private CreateSalesforceUsers createUsers(String testData) {
		HashMap<String, String> data = getMapFromData(testData);
		
		String firstName = data.get("firstName");
		String lastName = data.get("lastName");
		//String aliasName = data.get("aliasName");
		String email = data.get("email");
		//String userName = data.get("userName");
		//String nickName = data.get("nickName");
		String userLicense = data.get("userLicense");
		String role = data.get("role");
	//	String profile = data.get("profile");
		CreateSalesforceUsers crtfrceUsr =  basepage.clickOnAdminTab().clickOnSetup();	   //new CreateSalesforceUsers();
		crtfrceUsr.createUsers(firstName, lastName, email, 
			                        userLicense, role );
		return crtfrceUsr;
	}
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


	

}
