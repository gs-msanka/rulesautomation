package com.gainsight.sfdc.customer360.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.customer360.pages.Customer360Scorecard;
import com.gainsight.sfdc.customer360.pages.Workflow360Page;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.sfdc.workflow.tests.WorkFlowTest;
import com.gainsight.utils.DataProviderArguments;

public class Workflow360Tests extends WorkFlowTest{
	
	private final String TEST_DATA_FILE         = "testdata/sfdc/workflow/tests/WorkFlow_Test_360.xls";
    private final String CREATE_USERS_SCRIPT    = TestEnvironment.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";
    private final String CLEANUP_SCRIPT = "Delete [Select id from JBCXM__CTA__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];"+
                                        "Delete [select id from JBCXM__CSTask__c where JBCXM__Account__c in (select id from Account where Name='CTA Account 360')];";
    ObjectMapper mapper                         = new ObjectMapper();
    private HashMap<Integer, String> weekDayMap = new HashMap<>();
    @BeforeClass
    public void setup() {
       super.setup();
    }
    
    @BeforeMethod
    public void clearCTAsForThisAccount(){
    	apex.runApex(resolveStrNameSpace(CLEANUP_SCRIPT));
    }
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "CTA1")
    public void createRiskCTA_in360(HashMap<String, String> testData) throws IOException {
	     CTA cta = mapper.readValue(testData.get("CTA"), CTA.class);
	     Customer360Page customer360Page = basepage.clickOnC360Tab().searchCustomer(cta.getCustomer(), false, false);
	     Workflow360Page workflow360 = customer360Page.goToCockpitSection();
         cta.setDueDate(getDateWithFormat(Integer.valueOf(cta.getDueDate()), 0, false));
         cta.setAssignee(sfinfo.getUserFullName());
         workflow360.createCTA(cta);
         //Assert.assertTrue(workflow360.isCTADisplayed(cta), "Verifying risk CTA is created");
    }

}
