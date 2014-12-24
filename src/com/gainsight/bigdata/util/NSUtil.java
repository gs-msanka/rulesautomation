package com.gainsight.bigdata.util;

import org.testng.Assert;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;

public class NSUtil {

	static enum requestMethodType {
		post, get
	}

	static enum httpStatus {
		code_200(200), code_300(300), code_400(400), code_500(500);

		private int statusCode;

		private httpStatus(int code) {
			statusCode = code;
		}

		public int getCode() {
			return statusCode;
		}
	}

	static WebAction wa;

	
	public static NSInfo fetchNewStackInfo(SFDCInfo info, Header hr) throws Exception {
		// TODO Auto-generated method stub
		wa= new WebAction();
		NSInfo ns = new NSInfo();

		//Header hr = new Header();
		hr.addHeader("Content-Type", "application/json");
		hr.addHeader("Origin", "https://ap1.visual.force.com");
		hr.addHeader("appOrgId", info.getOrg());
		hr.addHeader("appUserId", info.getUserId());
		hr.addHeader("appSessionId", info.getSessionId());
		//hr.addHeader("authToken", "initialcall");
				
		ResponseObj result = wa.doGet(PropertyReader.nsAppUrl + "/api/reports/all", 
				hr.getAllHeaders());
		org.apache.http.Header[] resHeaders = result.getAllHeaders();
		for (int i = 0; i < resHeaders.length; i++) {
			if(resHeaders[i].getName().equalsIgnoreCase("authToken")){
				ns.setAuthToken((String)resHeaders[i].getValue());
				hr.addHeader("authToken", (String)resHeaders[i].getValue());
				break;
			}
		}	
		
		Assert.assertNotNull(ns.getAuthToken(), "authToken is not set");
		Log.info("NS Info: " + ns.toString());
		return ns;
	}

}
