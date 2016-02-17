package com.gainsight.sfdc.customer360.test;

import java.util.HashMap;

import com.gainsight.sfdc.sponsorTracking.SponsorTracking;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.administration.pages.AdminCustomer360Section;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.customer360.pages.SponsorTracking360;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;

public class Customer360SponsorTest extends BaseTest {	
	
	private final String SPONSOR_FILE = Application.basedir+"/apex_scripts/sponsorTracking/SponsorTracking.apex";
	private final String TEST_DATA_FILE = "testdata/sfdc/cs360SponsorTracking/SponsorTrackingTestData.xls";
	private SponsorTracking360 sponsorTracking360; 
	private SponsorTracking sp_api;
	@BeforeClass
	public void setup() throws InterruptedException{
		Log.info("Starting Customer 360 Sponsor Tracking module Test Cases...");
		basepage.login();
		Thread.sleep(20000);
		AdministrationBasePage adm=basepage.clickOnAdminTab();
		AdminCustomer360Section c360sec=adm.clickOnC360TabAdmin();		
		c360sec.editSponsorTracking();
		c360sec.enableSponsorTracking();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SPONSOR_FILE));
		basepage.clickOnC360Tab();
		sponsorTracking360 = SponsorTracking360.init();
		sp_api= SponsorTracking.initialize();
	}
	
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "SPT-1")
	public void TrackUsingEmail(HashMap<String, String> testData) {
		HashMap<String, String> SponsorDetails = getMapFromData(testData.get("SponsorDetails"));
	    //basepage.clickOnC360Tab();		
	    sp_api.updateNSURLInAppSettings();
		if(sp_api.validateOAuthEnabled())
		 Log.info("Validation success is : OAuth Enabled");
		sponsorTracking360.searchSponsor(SponsorDetails.get("CustomerName"),SponsorDetails.get("ContactName"),SponsorDetails.get("Email"),sp_api);
	}
}
