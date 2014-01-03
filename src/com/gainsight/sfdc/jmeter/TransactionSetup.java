package com.gainsight.sfdc.jmeter;

import org.testng.annotations.Test;
import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;

public class TransactionSetup extends BaseTest {
	@Test
	public void setup() throws Exception {
		try {
			Report.logInfo("Starting Acceptance Test Case...");
			apex.runApexCodeFromFile(env.basedir
					+ "/apex_scripts/jmeter/transactions_setup.apex",
					isPackageInstance());
		} catch (Exception e) {
			Report.logInfo(e.getMessage());
			throw e;
		}
	}
}
