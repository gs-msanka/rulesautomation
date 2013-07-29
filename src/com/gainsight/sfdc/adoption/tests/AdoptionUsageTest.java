package com.gainsight.sfdc.adoption.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.adoption.pages.AdoptionBasePage;
import com.gainsight.sfdc.adoption.pages.AdoptionUsagePage;
import com.gainsight.sfdc.tests.BaseTest;

public class AdoptionUsageTest extends BaseTest {

	@BeforeClass
	public void setUp() {
		Report.logInfo("Starting Adoption Usage Test Case...");
		basepage.login();
	}

	@Test
	public void searchUsageData() {
		AdoptionBasePage base = basepage.clickOnAdoptionTab();
		AdoptionUsagePage usage = base.clickOnUsageSubTab();
		usage.setMonth("Jun");
		usage.setYear("2013");
		usage.setMeasure("Uploaded PO Value");
		usage.displayUsageData();
	}
	
	@AfterClass
	public void tearDown(){
		basepage.logout();
	}
}
