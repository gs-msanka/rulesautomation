package com.gainsight.bigdata.rulesengine;

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
    private static final String rulesDir = TestEnvironment.basedir + "/testdata/newstack/RulesEngine/LoadToUsageData/";
    private static final String UsageDataSync = rulesDir + "/UsageDataSync.apex";
    private static final String UsageDataSync1 = rulesDir + "/UsageDataSync1.apex";
    private static final String UsageDateSync = rulesDir + "/UsageDateSync.apex";
    private static final String UsageDateSync1 = rulesDir + "/UsageDateSync1.apex";
    private static final boolean isEnabled = false;
    static WebAction wa = new WebAction();
    public Header header = new Header();

    // Work In Progress Need to optimize the code as we will proceed

    @BeforeClass
    public void beforeClass() throws Exception {

        GSUtil.sfdcLogin(header, wa);
        CreateObjectAndFields creatFields = new CreateObjectAndFields();

        String[] fields = {"FilesDownloaded", "NoOfReportsRun"};
        creatFields.createNumberField(GSUtil.resolveStrNameSpace("JBCXM__UsageData__c"), fields, false);

    }

    @Test(enabled = isEnabled)
    // Its for UsageData sync with Account Id's only
    public void rulesUsageOne() throws Exception {
        GSUtil.runApexCode(UsageDataSync);
        GSUtil.runApexCode(UsageDataSync1);
        SObject[] rules = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDataSync'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
        }
        SObject[] rules1 = GSUtil.execute("SELECT count(Id) FROM Account");
        SObject[] rules2 = GSUtil.execute("SELECT count(Id) FROM JBCXM__UsageData__c");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    public void rulesUsageTwo() throws Exception {
        GSUtil.runApexCode(UsageDateSync);
        GSUtil.runApexCode(UsageDateSync1);
        SObject[] rules = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDateSync'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
        }
        SObject[] rules1 = GSUtil.execute(" Select count(Id) From Account Where ((Name LIKE 'B%')) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 = GSUtil.execute("SELECT count(Id) FROM JBCXM__UsageData__c where FilesDownloaded__c=12345");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @AfterClass
    public void afterClass() throws ConnectionException, InterruptedException {
        CreateObjectAndFields deleteFields = new CreateObjectAndFields();
        String[] fields = {"FilesDownloaded", "NoOfReportsRun"};
        deleteFields.deletefields(GSUtil.resolveStrNameSpace("JBCXM__UsageData__c"), fields);
        GSUtil.soql = null;
    }
}
