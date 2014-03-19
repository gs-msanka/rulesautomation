package com.gainsight.sfdc.rulesEngine.tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.sfdc.rulesEngine.tests.*;
public class RulesEngineTests extends BaseTest {
	private static final String TEST_DATA_FILE = "";
	RuleEngineDataSetup rSetup= new RuleEngineDataSetup();

	@BeforeClass
	public void setUp() {
		//1. load usage data
		basepage.login();
		try{
				rSetup.loadUsageDataForRulesEngine("AccountMonthly");
				//rSetup.createRulesForRulesEngine();
		}catch (Exception ex){
			System.out.println(ex.getLocalizedMessage());
		}
		
		//2. Create Rules From Backend
		
	}
	 
	@AfterMethod
	private void refresh() {
	        basepage.refreshPage();
	    }
	 
	@Test//(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", priority = 1)
	//@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "M1")
	public void verifyDataFromExcel() {
		//Trigger Ruless
		//Run Validations
		//Assert 
	}
}
