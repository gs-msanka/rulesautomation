package com.gainsight.sfdc.jmeter;

import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;

public class TransactionSetup extends BaseTest {
	@Test
	public void setup() throws Exception {
		try {
			Log.info("Starting Acceptance Test Case...");
			apex.runApexCodeFromFile(env.basedir
					+ "/apex_scripts/jmeter/transactions_setup.apex",
					isPackage);
		} catch (Exception e) {
			Log.info(e.getMessage());
			throw e;
		}
	}
}
