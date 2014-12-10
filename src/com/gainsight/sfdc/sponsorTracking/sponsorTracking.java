package com.gainsight.sfdc.sponsorTracking;

import java.net.UnknownHostException;
import java.util.HashMap;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.sfdc.administration.pages.AdminIntegrationPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.webaction.WebAction;

public class sponsorTracking extends BasePage{
	protected TestEnvironment env = new TestEnvironment();
	WebAction wa = new WebAction();
	private Boolean isPackaged =Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
	private static final String EXISTS_CALL = "/api/accounts/SFDC/exists";
	private static final String SEARCH_SPONSOR = "/api/sponsor/search";
	
	static BaseTest bt = new BaseTest();
	
	public static sponsorTracking init(){
		return new sponsorTracking();
	}
	
	
	public void updateNSURLInAppSettings() {
		String NSURL = env.getProperty("ns.appurl");
		System.out.println("setting ns url in app settings");
		bt.soql.getRecordCount(bt.resolveStrNameSpace("select id from JBCXM__ApplicationSettings__c"));
	
		bt.apex.login(env.getUserName(), env.getUserPassword(),env.getProperty("sfdc.stoken"));
		
		//bt.apex.runApex(bt.resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
			//																	+ "appSet.JBCXM__NSURL__c='"	+ NSURL+ "';"
				//															+ "update appSet;"));
		
		String printapex=bt.resolveStrNameSpace("JBCXM__ApplicationSettings__c AppSett=[Select JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
				+ "if (AppSett.JBCXM__NSURL__c == '"+NSURL+"') System.debug('Success');"
				+ "else { AppSett.JBCXM__NSURL__c='"+NSURL+"'; update AppSett;	}");
		Report.logInfo(printapex);
				
		
		bt.apex.runApex(printapex);
		Report.logInfo("updateNSURLInAppSettings DONE");
			
	}
	
	public void enableOAuthForOrg() {
		AdministrationBasePage admin = clickOnAdminTab();
		AdminIntegrationPage integ = admin.clickOnIntegrationLink();
		integ.clickOnEnableGSMDP();
		amtDateUtil.stalePause();
		amtDateUtil.stalePause();
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
			HttpResponseObj httpResp= wa.doGet(uri, hdrs.getAllHeaders());
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
			Report.logInfo("Headers Passing are : "+hdrs.toString());
			String rawBody="{\"firstName\": \""+ReqParam.get("FirstName")+"\",\"lastName\": \""+ReqParam.get("LastName")+"\",\"email\": \""+ReqParam.get("Email")+"\","
					+ "\"title\": \""+ReqParam.get("Title")+"\",\"company\":\""+ReqParam.get("Name")+"\"}";
			Report.logInfo("Request is :"+rawBody);
			HttpResponseObj httpResp= wa.doPost(uri, rawBody, hdrs.getAllHeaders());
			Report.logInfo("statuscode is =" + httpResp.getStatusCode());
			Report.logInfo("Content Length:"+httpResp.getContentLength());
			String result=httpResp.getContent();
			Report.logInfo("result:"+result);
			
			
			
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