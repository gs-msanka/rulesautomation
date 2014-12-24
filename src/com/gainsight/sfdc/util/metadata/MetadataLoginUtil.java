package com.gainsight.sfdc.util.metadata;

import com.gainsight.testdriver.TestEnvironment;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.LoginResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class MetadataLoginUtil {

	private String user_Name = "ggolla@gainsightautomation.com";
	private String password = "1234567jbxfFaIVveaQuTrt9g9S2l9QAxT";
	private String soap_Url = "https://login.salesforce.com/services/Soap/c/29.0";
	static MetadataConnection connection;
    static TestEnvironment env;
	
	
	/*final static String user_Name = "giri@gainsightbeta.com";
	final static String password = "1234567jbkrSKczrOa1ANjWvtsZfbBEOwV";
	final static String login_Url = "https://login.salesforce.com/services/Soap/c/29.0"; */

	
	public MetadataConnection login() throws ConnectionException {
        env = new TestEnvironment();
        user_Name = env.getUserName();
        password = env.getProperty("sfdc.password")+env.getProperty("sfdc.stoken");
        soap_Url = env.getProperty("sfdc.soapUrl");
		final LoginResult login = loginToSalesforce(user_Name, password, soap_Url);
		connection = createMetadataConnection(login);
		return connection;

	}
	
	private MetadataConnection createMetadataConnection(final LoginResult login) throws ConnectionException {
		ConnectorConfig config = new ConnectorConfig();
		config.setServiceEndpoint(login.getMetadataServerUrl());
		config.setSessionId(login.getSessionId());
		return new MetadataConnection(config);
	}

	private LoginResult loginToSalesforce(
		final String username,
		final String password,
		final String loginUrl) throws ConnectionException {
		final ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(loginUrl);
		config.setServiceEndpoint(loginUrl);
		config.setManualLogin(true);
		return (new EnterpriseConnection(config)).login(username, password);
	}
}
