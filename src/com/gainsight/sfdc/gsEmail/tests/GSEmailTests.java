package com.gainsight.sfdc.gsEmail.tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.sfdc.gsEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.MongoUtil;

public class GSEmailTests extends NSTestBase {
	private static GSEmailSetup gse;
	String NSURL;
	MongoUtil mUtil;
	BasePage basepage = new BasePage();

	@BeforeClass
	public void setUp() throws Exception {
		gse = new GSEmailSetup();
		basepage.login();
		mUtil = new MongoUtil();
		NSURL = env.getProperty("ns.appurl");
		updateNSURLInAppSettings(NSURL);
		// addNSURLToRemoteSiteSettings();
	}

	@Test
	public void testOAuthWithCorrectURL() {
		System.out.println("testing oAuth with Correct NS URL!");
		gse.enableOAuthForOrg();
		Boolean oAuthEnabled = gse.validateOAuthEnabled();
		Assert.assertTrue(oAuthEnabled, "oAuth Successfully Enabled");
	}

	@Test
	public void testOauthWrongNSURL() {
		updateNSURLInAppSettings("https://afladsjfklas.app.com");
		// should fail at this point..as the integration page would not load
		// Assert.assertFalse(
		// gse.enableOAuthForOrg(),"oAuth Failed due to wrong NS URL");
	}

	@Test
	public void testMandrillAccountCreation() throws Exception {
		updateNSURLInAppSettings(NSURL);
		gse.enableOAuthForOrg();
		gse.updateAccessKeyInApplicationSettingForGSEmail();
		HashMap<String, String> records = new HashMap<String, String>();
		HashMap<String, String> fieldDetails = new HashMap<String, String>();
		records.put("ExternalTenantID", sfinfo.getOrg());
		fieldDetails.put("TenantId", "");
		fieldDetails.put("TenantName", "");
		//mUtil.getFieldValueFromDoc("tenantmaster", records, fieldDetails);
		Assert.assertTrue(
				gse.checkSubAccountInMandrill(
						env.getProperty("mandrill.APIKey"),
						fieldDetails.get("TenantId"),
						fieldDetails.get("TenantName")),
				"Account registration successful in mandrill!");
	}

	@AfterClass
	public void tearDown() {
		basepage.logout();
	}
}
