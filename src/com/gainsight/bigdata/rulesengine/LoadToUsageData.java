package com.gainsight.bigdata.rulesengine;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.webaction.WebAction;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class LoadToUsageData {
	private static final String rulesDir = TestEnvironment.basedir+ "/testdata/newstack/RulesEngine/LoadToUsageData/";
    private static final String UsageDataSync = rulesDir+"/UsageDataSync.apex";
    private static final String UsageDataSync1 = rulesDir+"/UsageDataSync1.apex";
    private static final String UsageDateSync = rulesDir+"/UsageDateSync.apex";
    private static final String UsageDateSync1 = rulesDir+"/UsageDateSync1.apex";
	private static final boolean isEnabled = false;
	static WebAction wa = new WebAction();

	public SFDCUtil sfdc = new SFDCUtil();
	public Header header = new Header();
	SOQLUtil soql = new SOQLUtil();

	// Work In Progress Need to optimize the code as we will proceed

	@BeforeClass
	public void beforeClass() throws Exception {

		LoginUtil.sfdcLogin(soql, header, wa);
		CreateObjectAndFields creatFields = new CreateObjectAndFields();

		String[] fields = { "FilesDownloaded","NoOfReportsRun" };
		creatFields.createNumberField("JBCXM__UsageData__c", fields, false);
		
	}

	@Test(enabled=isEnabled)
    // Its for UsageData sync with Account Id's only
    public void rulesUsageOne() throws Exception {
        sfdc.runApexCodeFromFile(UsageDataSync, true);
        sfdc.runApexCodeFromFile(UsageDataSync1, true);
        SObject[] rules = soql
                .getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDataSync'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = LoginUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            Thread.sleep(25000);
        }
        SObject[] rules1 = soql.getRecords("SELECT count(Id) FROM Account");
        SObject[] rules2 = soql
                .getRecords("SELECT count(Id) FROM JBCXM__UsageData__c");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    public void rulesUsageTwo() throws Exception {
        sfdc.runApexCodeFromFile(UsageDateSync, true);
        sfdc.runApexCodeFromFile(UsageDateSync1, true);
        SObject[] rules = soql
                .getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDateSync'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
                    + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = LoginUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            Thread.sleep(25000);
        }
        SObject[] rules1 = soql.getRecords(" Select count(Id) From Account Where ((Name LIKE 'B%')) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 = soql
                .getRecords("SELECT count(Id) FROM JBCXM__UsageData__c where FilesDownloaded__c=12345");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }
	
	@AfterClass
	public void afterClass() throws ConnectionException, InterruptedException {
		CreateObjectAndFields deleteFields = new CreateObjectAndFields();
		String[] fields = {"FilesDownloaded","NoOfReportsRun"};
		deleteFields.deletefields("JBCXM__UsageData__c", fields);
		soql = null;
	}
}
