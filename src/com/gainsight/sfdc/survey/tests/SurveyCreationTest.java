package com.gainsight.sfdc.survey.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.TimeZone;

public class SurveyCreationTest extends BaseTest {
	String QUERY = "DELETE [SELECT Id FROM JBCXM__Survey__c WHERE JBCXM__Title__c  = 'test'];";


	@BeforeClass
	public void setUp() {
        userLocale = soql.getUserLocale();
        userTimezone = TimeZone.getTimeZone(soql.getUserTimeZone());
        basepage.login();

	}

    @Test
    public void sampleTest() {

    }


	@AfterClass
	public void tearDown() {
		basepage.logout();
	}


}
