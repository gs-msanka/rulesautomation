package com.gainsight.sfdc.util.bulk;

import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SFDCUtil {

//	private final String endPointURL = "https://login.salesforce.com/services/Soap/u/28.0";
	static PartnerConnection connection;
	static TestEnvironment env;
	
	/**
	 * Fetching the Salesforce UserInfo along with session id.
	 * @return
	 */
	public static SFDCInfo fetchSFDCinfo() {
		Report.logInfo("Fetching SalesForce Data");
		try {
			env = new TestEnvironment();
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(env.getUserName());
			config.setPassword(env.getUserPassword() + env.getProperty("sfdc.stoken"));

			connection = Connector.newConnection(config);
			GetUserInfoResult userInfo = connection.getUserInfo();
			
			SFDCInfo info = new SFDCInfo();
			info.setOrg(userInfo.getOrganizationId());
			info.setUserId(userInfo.getUserId());
			info.setSessionId(config.getSessionId());
			String sept = config.getServiceEndpoint();
			sept = sept.substring(0, sept.indexOf(".com") + 4);
			info.setEndpoint(sept);
			
			Report.logInfo("SDCF Info:\n" + info.toString());
			return info;
		} catch (ConnectionException ce) {
			ce.printStackTrace();
			return null;
		}
	}
}
