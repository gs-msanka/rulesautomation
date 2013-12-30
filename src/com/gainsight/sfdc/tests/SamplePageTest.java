package com.gainsight.sfdc.tests;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;

public class SamplePageTest {

	@Test
	public void testFunction() {
	
		Report.logInfo("Starting the Sample Test....");
		Assert.assertFalse("Purposefully failing the test to check the testng plugin behaviour", true);
	}
}
