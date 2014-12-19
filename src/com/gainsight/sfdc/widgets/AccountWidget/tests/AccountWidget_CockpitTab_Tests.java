package com.gainsight.sfdc.widgets.AccountWidget.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.widgets.AccountWidget.pages.AccountWidget_CockpitTab_Page;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;

public class AccountWidget_CockpitTab_Tests extends BaseTest {
	
    ObjectMapper mapper                         = new ObjectMapper();
    private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test_AccWidget.xls";
    private final String CREATE_USERS_SCRIPT    = TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CREATE_ACCOUNTS_CUSTOMERS=TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/Create_Accounts_Customers_For_CTA.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c];"+
                                        "Delete [select id from JBCXM__CSTask__c];"+
                                        "Delete [select id from Task];"+
                                        "Delete [Select id from JBCXM__StatePreservation__c];"+
                                        "Delete [Select id from JBCXM__Milestone__c];";
	 @BeforeClass
	    public void setup() {
	        sfinfo= SFDCUtil.fetchSFDCinfo();
	        userLocale = sfinfo.getUserLocale();
	        userTimezone = TimeZone.getTimeZone(sfinfo.getUserTimeZone());
	        basepage.login();
	        isPackage = isPackageInstance();
	   }
	 	
	 @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
	    public void createRiskCTA(HashMap<String, String> testData) throws IOException {
	        CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
		 	SObject[] accId=soql.getRecords("select id from Account where Name='"+cta.getCustomer()+"'");
	        AccountWidget_CockpitTab_Page accWfPage = basepage.gotoAccountPageWithId(accId[0].getId()).gotoCockpitSubTab();
	        cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
	        cta.setAssignee(sfinfo.getUserFullName());
	        cta.setFromWidgets(true);
	        accWfPage.createCTA(cta);
	        Assert.assertTrue(accWfPage.isCTADisplayed(cta), "Verifying risk CTA is created");
	    }
}
