package com.gainsight.sfdc.GSEmail.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.GSEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.tests.BaseTest;

public class GSEmailTests extends BaseTest {
	
	@BeforeClass
    public void setUp() {
       basepage.login();
    }
	
	@Test
	public void testConnection(){
		System.out.println("testing connection!");
		GSEmailSetup gse=new GSEmailSetup();
		basepage.login();
		/*gse.updateNSURLInAppSettings();
		gse.addNSURLToRemoteSiteSettings();
		gse.enableOAuthForOrg();
		Boolean oAuthEnabled=gse.validateOAuthEnabled();
		if(oAuthEnabled) System.out.println("oAuth Successfully Enabled");
		else System.out.println("oAuth not enabled still");
		*/
		gse.createSurveyWithGSEmail();
		gse.sendTestEmail();
		gse.deleteOldEmailActivityLogFromMongo();
	}
	
	 @AfterClass
	 public void tearDown() {
	        basepage.logout();
	 }
}
