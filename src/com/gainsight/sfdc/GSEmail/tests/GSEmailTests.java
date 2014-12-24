package com.gainsight.sfdc.GSEmail.tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.GSEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.MongoUtil;

public class GSEmailTests extends BaseTest {
	private static GSEmailSetup gse;
	 String NSURL ;
	 MongoUtil mUtil;
	@BeforeClass
    public void setUp() {
		gse=new GSEmailSetup();
        basepage.login();        
        mUtil= new MongoUtil();
        NSURL  = env.getProperty("ns.appurl");
        gse.updateNSURLInAppSettings(NSURL);
		gse.addNSURLToRemoteSiteSettings();
    }
	
	@Test
	public void testOAuthWithCorrectURL(){
		System.out.println("testing oAuth with Correct NS URL!");       
		gse.enableOAuthForOrg();
		Boolean oAuthEnabled=gse.validateOAuthEnabled();
		Assert.assertTrue(oAuthEnabled ,"oAuth Successfully Enabled");
	}
	
	@Test
	public void testOauthWrongNSURL(){
		 gse.updateNSURLInAppSettings("https://afladsjfklas.app.com");        
	     //should fail at this point..as the integration page would not load		
		Assert.assertFalse( gse.enableOAuthForOrg(),"oAuth Failed due to wrong NS URL");
	}
	
	@Test
	public void testMandrillAccountCreation() throws Exception{
		gse.updateNSURLInAppSettings(NSURL);
		gse.enableOAuthForOrg();
		gse.createSurveyWithGSEmail();
		HashMap<String,String> records=new HashMap<String,String>();
		HashMap<String,String> fieldDetails=new HashMap<String,String>();
		
		records.put("ExternalTenantID", sfinfo.getOrg());
		fieldDetails.put("TenantId", "");
		fieldDetails.put("TenantName", "");
		mUtil.getFieldValueFromDoc("tenantmaster", records, fieldDetails);
		Assert.assertTrue(gse.checkSubAccountInMandrill(env.getProperty("mandrill.APIKey"), fieldDetails.get("TenantId"), fieldDetails.get("TenantName")),"Account registration successful in mandrill!");
	}
	
	
	@Test
	public void testSendEmailSingleRecipient(){
		gse.updateNSURLInAppSettings(NSURL);
		gse.enableOAuthForOrg();
		gse.createSurveyWithGSEmail();
		gse.sendTestEmail();
		gse.deleteOldEmailActivityLogFromMongo();	
	}
	
	 @AfterClass
	 public void tearDown() {
	        basepage.logout();
	 }	
}
