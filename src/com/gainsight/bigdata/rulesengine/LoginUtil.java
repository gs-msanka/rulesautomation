package com.gainsight.bigdata.rulesengine;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.webaction.WebAction;

public class LoginUtil {

	public static void sfdcLogin(SOQLUtil soql, Header header, WebAction wa)
			throws Exception {
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		TestEnvironment env = new TestEnvironment();

		NSInfo nsinfo = NSUtil.fetchNewStackInfo(sfinfo, new Header());
		soql.login(env.getUserName(), env.getUserPassword(),
				env.getProperty("sfdc.stoken"));
		header.addHeader("appOrgId", sfinfo.getOrg());
		header.addHeader("appSessionId", sfinfo.getSessionId());
		header.addHeader("appUserId", sfinfo.getUserId());
		header.addHeader("Content-Type", "application/json");
		System.out.println("endpoint:" + sfinfo.getEndpoint());
		// "https://jbcxm.na10.visual.force.com"
		String SFInstance = sfinfo.getEndpoint().split("https://")[1]
				.split("\\.")[0];
		String OriginHeader = "";
		Boolean isPackaged = Boolean.valueOf(env
				.getProperty("sfdc.managedPackage"));
		if (isPackaged)
			OriginHeader = "https://jbcxm." + SFInstance + ".visual.force.com";
		else
			OriginHeader = "https://" + SFInstance + ".visual.force.com";

		System.out.println("OriginHeader value=" + OriginHeader);
		header.addHeader("Origin", OriginHeader);

	}

	public static ResponseObject convertToObject(String result)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseObject response = objectMapper.readValue(result,
				ResponseObject.class);
		return response;
	}
}
