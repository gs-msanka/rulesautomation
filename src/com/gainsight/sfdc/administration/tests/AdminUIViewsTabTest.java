package com.gainsight.sfdc.administration.tests;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.administration.pages.AdminCustomersTab;
import com.gainsight.sfdc.administration.pages.AdminUIViewssSubTab;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class AdminUIViewsTabTest extends BaseTest {

	String[] dirs = { "acceptancetests" };
	//private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
		//	+ generatePath(dirs);
	final String TEST_DATA_FILE = "testdata/sfdc/Administration/AdminUIViewsTestdata.xls";
	
	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting  Test Case...");
		/*apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/AlertUI-Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Customer_Tab_ UI_Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Acnt_Attributes_UI_Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/NPS_UI_View", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Churn_UI_Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Transaction_UI_Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Survey_Detail_Report_UI_Views", isPackageInstance());
		apex.runApexCodeFromFile(env.basedir+"/apex_scripts/UI_Views/Survey_Participants_UI_Views", isPackageInstance());*/
		basepage.login();
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel",priority=1)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Add_Fields")
	public void testAdminAddUIViews(HashMap<String, String> testData) throws BiffException, IOException {
		selectTabName(testData.get("addAvailableFields"));
	}
	private AdminUIViewssSubTab selectTabName(String testData) {
		HashMap<String, String> data = getMapFromData1(testData);
		String tabName = data.get("tabName");
		String ViewName =data.get("ViewName");
		String sctFieldName = data.get("sctFieldName");
		String selectffield = data.get("selectffield");
		System.out.println("selectffield has these values in test:" +selectffield);
		String foperator =data.get("foperator");
		String fvalue = data.get("fvalue");
		String selectRfield = data.get("selectRfield");
		//String rLabel = data.get("rLabel");
		String rpOperator =data.get("rpOperator");
		String rpvalue = data.get("rpvalue");
		
		AdminUIViewssSubTab adUIview = basepage.clickOnAdminTab().clickOnUIViewssettingsSubTab();
		adUIview.selectTabName(tabName,ViewName,selectffield,foperator,fvalue,sctFieldName,
				                    selectRfield, rpOperator , rpvalue); 
		return adUIview;
		 
       
		
	}
                 //	selectfvalue
	public HashMap<String, String> getMapFromData1(String data) {
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println(data);
		String[] dataArray = data.substring(1, data.length() - 1).split("\\|");
		for (String record : dataArray) {
			if (record != null) {
				System.out.println(record);
				String[] pair = record.split("\\&");
				hm.put(pair[0], pair[1]);
			}
		}
		return hm;
	}

	
	
	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
	
	
	
}




