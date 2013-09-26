package com.gainsight.bigdata.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class PropertyReader {

	public static String userName;
	public static String password;
	public static String stoken;
	public static String nsAppUrl;
	public static String baseDir;
	
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
		
		userName = p.getProperty("username");
		password = p.getProperty("password");
		stoken = p.getProperty("stoken");
		nsAppUrl = p.getProperty("ns_appurl");
	}
}
