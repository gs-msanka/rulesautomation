package com.gainsight.sfdc.jmeter;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;

public class TransactionSetup extends BaseTest {
	@Test
	public void setup() throws Exception {
		try {
			Log.info("Starting Acceptance Test Case...");
            sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir+ "/apex_scripts/jmeter/transactions_setup.apex"));
		} catch (Exception e) {
			Log.info(e.getMessage());
			throw e;
		}
	}
}
