package com.gainsight.sfdc.tests;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.utils.TestDataHolder;

public class BaseTest {
	protected TestDataHolder testDataLoader = new TestDataHolder();
	String[] dirs = { "testdata", "sfdc" };
	public final String TEST_DATA_PATH_PREFIX = generatePath(dirs);
	TestEnvironment env = new TestEnvironment();
	public SOQLUtil soql = new SOQLUtil();
	public BasePage basepage;
	private final String DELETE_RECORDS = "Select id from TransHeader__c | Select id from CustomerInfo__c";
	private final String DELETE_RECORDS_NAMESPACE = "Select id from JBCXM__TransHeader__c | Select id from JBCXM__CustomerInfo__c";

	@BeforeSuite
	public void init() throws Exception {
		env.start();
		try {
			String deleteFlag = env.getProperty("deleteRecords");
			String namesapce = env.getProperty("managedPackage");
			String setAsDefaultApp = env.getProperty("setAsDefaultApp");
			String loadDefaultData = env.getProperty("loadDefaultData");
			if (deleteFlag != null && deleteFlag.equals("true")) {
				if (namesapce != null && namesapce.equals("true"))
					soql.deleteQuery(DELETE_RECORDS_NAMESPACE);
				else
					soql.deleteQuery(DELETE_RECORDS);
			}
			env.launchBrower();
			basepage = new BasePage();
			if (setAsDefaultApp != null && setAsDefaultApp.equals("true")) {
				basepage.login();
				basepage.setDefaultApplication("JBara");
				basepage.logout();
			}
			if (loadDefaultData != null && loadDefaultData.equals("true")) {
				basepage.login();
				basepage.loadDefaultData();
				basepage.logout();
			}
		} catch (Exception e) {
			env.stop();
			throw e;
		}
	}

	@AfterSuite
	public void fini() {
		env.stop();
	}

	@BeforeTest
	public void beInMainWindow() {
		basepage.beInMainWindow();
	}

	public String generatePath(String[] dirs) {
		String path = "";
		for (String dir : dirs) {
			path = path + dir + File.pathSeparator;
		}
		return path;
	}

	public String currencyFormat(String amt) {
		DecimalFormat moneyFormat = new DecimalFormat("$###,###");
		return moneyFormat.format(new Long(amt)).replace("$", "$ ");
	}

	public String getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		return sdf.format(cal.getTime());
	}

	public String getFormattedDate(String dateStr) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		return sdf.format(date);
	}

	public String getFormattedDate(String dateStr, int days)
			throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		return sdf.format(c.getTime());
	}

	public HashMap<String, String> getMapFromData(String data) {
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println(data);
		String[] dataArray = data.substring(1, data.length() - 1).split("\\|");
		for (String record : dataArray) {
			if (record != null) {
				System.out.println(record);
				String[] pair = record.split("\\:");
				hm.put(pair[0], pair[1]);
			}
		}
		return hm;
	}

	public Double calcMRR(int ASV) {
		return Math.ceil(ASV / 12.0);
	}

	public Double calcARPU(int ASV, int users) {
		return Math.ceil((ASV / 12.0) / users);
	}
}
