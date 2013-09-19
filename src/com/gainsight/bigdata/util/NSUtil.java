package com.gainsight.bigdata.util;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

import com.gainsight.bigdata.WebAction;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.SFDCInfo;
import com.gainsight.pageobject.core.Report;
import com.google.gson.Gson;

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

	public static NSInfo fetchNewStackInfo(SFDCInfo info) throws Exception {
		// TODO Auto-generated method stub
		wa= new WebAction();
		Gson gson = new Gson();
		String rawBody = gson.toJson(info);
		System.out.println("POST Request: " + rawBody);

		List<Header> h = new ArrayList<Header>();
		Header hr = new Header();
		hr.setName("Content-Type"); 
		hr.setValue("application/json");
		h.add(hr);
		
		HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/sflogin",
				rawBody, h);
		Report.logInfo(result.toString());
		
		NsResponseObj obj = gson.fromJson(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(),
				"Fetching Access Token failed : " + result);
		
		NSInfo ns = new NSInfo();
		ns.setAuthToken((String) obj.getData().get("acess_token"));
		ns.setTenantID((String) obj.getData().get("TenantId"));

		Report.logInfo("NS Info: " + ns.toString());
		return ns;
	}

}
