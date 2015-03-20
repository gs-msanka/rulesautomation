package com.gainsight.bigdata.rulesengine;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.testdriver.Application;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class LoadToUsageData extends NSTestBase {
    private static final String rulesDir = Application.basedir + "/testdata/newstack/RulesEngine/LoadToUsageData/";
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
        sfdc.connect();
        String[] fields = {"FilesDownloaded", "NoOfReportsRun"};
        metadataClient.createNumberField(resolveStrNameSpace("JBCXM__UsageData__c"), fields, false);
    }

    @Test
    // Its for UsageData sync with Account Id's only
    public void rulesUsageOne() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UsageDataSync));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UsageDataSync1));
        SObject[] rules =sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDataSync'"));
        for (SObject r : rules) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId() + "", header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = RulesUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            RulesUtil.waitForCompletion(r.getId(), wa, header);
        }
        SObject[] rules1 =sfdc.getRecords(resolveStrNameSpace("SELECT count(Id) FROM Account"));
        SObject[] rules2 =sfdc.getRecords(resolveStrNameSpace("SELECT count(Id) FROM JBCXM__UsageData__c"));
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    public void rulesUsageTwo() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UsageDateSync));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(UsageDateSync1));
        SObject[] rules =sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='UsageDateSync'"));
        for (SObject r : rules) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId() + "", header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = RulesUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            RulesUtil.waitForCompletion(r.getId(), wa, header);
        }
        SObject[] rules1 =sfdc.getRecords(resolveStrNameSpace(" Select count(Id) From Account Where ((Name LIKE 'B%')) AND JBCXM__CustomerInfo__c != null"));
        SObject[] rules2 =sfdc.getRecords(resolveStrNameSpace("SELECT count(Id) FROM JBCXM__UsageData__c where FilesDownloaded__c=12345"));
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @AfterClass
    public void afterClass() {

    }
}
