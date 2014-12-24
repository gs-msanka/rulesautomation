package com.gainsight.sfdc.tests;

import org.junit.Assert;
import org.testng.annotations.Test;

public class SamplePageTest {

	@Test
	public void testFunction() {
	
		Log.info("Starting the Sample Test....");
		Assert.assertFalse("Purposefully failing the test to check the testng plugin behaviour", true);
	}
}
