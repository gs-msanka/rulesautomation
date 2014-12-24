package com.gainsight.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class PropertyReader {

	public static final String clientId;
	public static final String clientSecret;
	public static final String userName;
	public static final String password;
	public static final String stoken;
	public static final String nsAppUrl;
	public static final String baseDir;
	public static final String partnerUrl;
	public static final String sfdcApiVersion;
	
	static {
		baseDir = System.getProperty("basedir", ".");
		PropertyConfigurator.configure(baseDir + "/conf/log4j.properties");
		File confFile = new File(baseDir + "/conf/application.properties");
		Properties p = new Properties();
		try {
			p.load(new FileReader(confFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clientId = p.getProperty("sfdc.clientId");
		clientSecret = p.getProperty("sfdc.cilentSecret");
		userName = p.getProperty("sfdc.username");
		password = p.getProperty("sfdc.password");
		stoken = p.getProperty("sfdc.stoken");
		partnerUrl = p.getProperty("sfdc.soapUrl");
		sfdcApiVersion = p.getProperty("sfdc.apiVersion");
		nsAppUrl = p.getProperty("ns.appurl");
	}
}
