package com.gainsight.sfdc.sponsorTracking;

import java.net.UnknownHostException;
import java.util.HashMap;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.administration.pages.AdminIntegrationPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.testdriver.Log;

public class SponsorTracking extends BaseTest {
	WebAction wa = new WebAction();
	private Boolean isPackaged = sfdcConfig.getSfdcManagedPackage();
	private static final String EXISTS_CALL = "/api/accounts/SFDC/exists";
	private static final String SEARCH_SPONSOR = "/api/sponsor/search";

	public static SponsorTracking initialize(){
		return new SponsorTracking();
	}
	

    //Please review this method. commented some code for time being
	public void updateNSURLInAppSettings() {
		String NSURL = env.getProperty("ns.appurl");
		System.out.println("setting ns url in app settings");
		sfdc.getRecordCount(resolveStrNameSpace("select id from JBCXM__ApplicationSettings__c"));
	
		//bt.sf.login(env.getUserName(), env.getUserPassword(),env.getProperty("sfdc.stoken"));
		
		//bt.apex.runApex(bt.resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
			//																	+ "appSet.JBCXM__NSURL__c='"	+ NSURL+ "';"
				//															+ "update appSet;"));
		
		String printapex=resolveStrNameSpace("JBCXM__ApplicationSettings__c AppSett=[Select JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
                + "if (AppSett.JBCXM__NSURL__c == '" + NSURL + "') System.debug('Success');"
                + "else { AppSett.JBCXM__NSURL__c='" + NSURL + "'; update AppSett;	}");
		Log.info(printapex);
				
		
		sfdc.runApexCode(printapex);
		Log.info("updateNSURLInAppSettings DONE");
			
	}
	
	public void enableOAuthForOrg() {
		AdministrationBasePage admin = basepage.clickOnAdminTab();
		AdminIntegrationPage integ = admin.clickOnIntegrationLink();
		integ.clickOnEnableGSMDP();
		Timer.sleep(2);
		Timer.sleep(2);
		integ.clickOnAuthorize();
	}

	public boolean validateOAuthEnabled() {
		Header hdrs = new Header();
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		String endPoint = env.getProperty("ns.appurl");
		String sessionid = sfinfo.getSessionId();
		String orgId = sfinfo.getOrg();
		String userId = sfinfo.getUserId();
		try {

			hdrs.addHeader("Content-Type", "application/json");
			hdrs.addHeader("appOrgId", orgId);
			hdrs.addHeader("appUserId", userId);
			hdrs.addHeader("appSessionId", sessionid);
			System.out.println("endpoint:" + sfinfo.getEndpoint());
			String SFInstance=sfinfo.getEndpoint().split("https://")[1].split("\\.")[0];
			String OriginHeader="";
			if(isPackaged)
			OriginHeader	="https://jbcxm."+SFInstance+".visual.force.com";
			else
				OriginHeader	="https://"+SFInstance+".visual.force.com";
			
			System.out.println("OriginHeader value="+OriginHeader);
			hdrs.addHeader("Origin", OriginHeader);
			String uri = endPoint + EXISTS_CALL;
			ResponseObj httpResp= wa.doGet(uri, hdrs.getAllHeaders());
			System.out.println("response==" + httpResp.getContent());
			
			if (httpResp.toString().contains("\"result\":true"))
				return true;
			else
				return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("error while validating oauth");
			return false;
		}
	}
	
	public Boolean SearchAPI(HashMap<String,Object> ReqParam) {
		
		Header hdrs = new Header();
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		String endPoint = env.getProperty("ns.appurl");
		String sessionid = sfinfo.getSessionId();
		String orgId = sfinfo.getOrg();
		String userId = sfinfo.getUserId();
	    try {
	    	hdrs.addHeader("appOrgId", orgId);
	    	hdrs.addHeader("appSessionid", sessionid);	 
	    	hdrs.addHeader("appUserId", userId);
			hdrs.addHeader("Content-Type", "application/json");
			hdrs.addHeader("Origin", "https://jbcxm.na10.visual.force.com");
			
			//hdrs.addHeader("accessKey", AccessKey );
			System.out.println("endpoint:" + sfinfo.getEndpoint());
			
			String uri = endPoint + SEARCH_SPONSOR;
			Log.info("Headers Passing are : " + hdrs.toString());
			String rawBody="{\"firstName\": \""+ReqParam.get("FirstName")+"\",\"lastName\": \""+ReqParam.get("LastName")+"\",\"email\": \""+ReqParam.get("Email")+"\","
					+ "\"title\": \""+ReqParam.get("Title")+"\",\"company\":\""+ReqParam.get("Name")+"\"}";
			Log.info("Request is :"+rawBody);
            //Please review, check if every thing is fine here.
			ResponseObj httpResp= wa.doPost(uri, hdrs.getAllHeaders(), rawBody);
			Log.info("statuscode is =" + httpResp.getStatusCode());
			Log.info("Content Length:"+httpResp.getContentLength());
			String result=httpResp.getContent();
			Log.info("result:"+result);
			
			
			
			//.split("\"result\":")[1].split(",")[0];
			
			if(httpResp.getStatusCode()==200 && result.contains("true") )
				return true;
			else
				return false;					
			
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
		}
	    catch(Exception ex){
	    	ex.printStackTrace();
	    	System.out.println("Search Failed!!");
	    	return false;
	    }
	
	}
	
	
	
	
	
}