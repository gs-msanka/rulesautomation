package com.gainsight.bigdata.util;

import com.gainsight.bigdata.pojo.SFDCInfo;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

@Deprecated
public class SFDCUtil {

	private final String endPointURL = "https://login.salesforce.com/services/Soap/u/28.0";
	static PartnerConnection connection;
    static SoapConnection soapConnection;
	
	public static SFDCInfo fetchSFDCinfo() {
		// TODO Auto-generated method stub
		try {
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(PropertyReader.userName);
			config.setPassword(PropertyReader.password + PropertyReader.stoken);

			connection = Connector.newConnection(config);
			GetUserInfoResult userInfo = connection.getUserInfo();
			
			SFDCInfo info = new SFDCInfo();
			info.setOrg(userInfo.getOrganizationId());
			info.setUserId(userInfo.getUserId());
			info.setSessionId(config.getSessionId());
			String sept = config.getServiceEndpoint();
			sept = sept.substring(0, sept.indexOf(".com") + 4);
			info.setEndpoint(sept);
			
			System.out.println("SDCF Info:\n" + info.toString());
			return info;
		} catch (ConnectionException ce) {
			ce.printStackTrace();
			return null;
		}
	}


}
