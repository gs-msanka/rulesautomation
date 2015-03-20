package com.gainsight.bigdata.rulesengine;

import java.util.List;

import com.gainsight.testdriver.Application;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class LoadToCustomers extends NSTestBase{
    private static final String rulesDir = Application.basedir + "/testdata/newstack/RulesEngine/LoadToCustomers/";
    private static final String CustomerInfo = rulesDir + "CustomerInfo.apex";
    private static final String CustomerInfo1 = rulesDir + "CustomerInfo1.apex";
    private static final String LoadToCustomer = rulesDir + "LoadToCustomer.apex";
    private static final String LoadToCustomer1 = rulesDir + "LoadToCustomer1.apex";
    private static final String LoadtoCust_Picklist = rulesDir + "LoadtoCust_Picklist.apex";
    private static final String LoadtoCust_Picklist1 = rulesDir + "LoadtoCust_Picklist1.apex";
    private static final String WithAndOrConditionFilters = rulesDir + "WithAndOrConditionFilters.apex";
    private static final String WithAndOrConditionFilters1 = rulesDir + "WithAndOrConditionFilters1.apex";
    private static final String OCD_Today = rulesDir + "OCD_Today.apex";
    private static final String OCD_Today1 = rulesDir + "OCD_Today1.apex";
    private static final String CountCust = rulesDir + "CountCust.apex";
    private static final String CountCust1 = rulesDir + "CountCust1.apex";
    private static final String PreviewResults = rulesDir + "PreviewResults.apex";
    private static final boolean isEnabled = false;
    static WebAction wa = new WebAction();
    public Header header = new Header();
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

    // Work In Progress Need to optimize the code as we will proceed

    @BeforeClass
    public void beforeClass() throws Exception {
        sfdc.connect();
        LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
    }

    @Test
    // Its for CustomerInfo Sync when Checkbox Apply to Gainsight customers is
    // not enabled
    public void rulesOne() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CustomerInfo));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CustomerInfo1));
        SObject[] rules = sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='CusotomerSync_Initially no customers'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = RulesUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            RulesUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] res = sfdc.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
        SObject[] rules1 = sfdc.getRecords("SELECT count(Id) FROM Account");
        SObject[] rules2 = sfdc.getRecords("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // Load to customer with Account names starts with A and ASV=4545
    public void rulesTwo() throws Exception {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(LoadToCustomer));
        System.out.print("Filename = " + LoadToCustomer);
       sfdc.runApexCode(getNameSpaceResolvedFileContents(LoadToCustomer1));
        System.out.print("Filename =" + LoadToCustomer1);
        SObject[] rules =sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='LoadToCustomer'");
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
        SObject[] rules1 =sfdc.getRecords("Select count(Id) From Account Where ((JBCXM__CustomerInfo__r.JBCXM__CustomerName__c LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 =sfdc.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo_sfdc.getRecords(tomerName__c LIKE 'A%' and JBCXM__ASV__c=4545");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // Load to customer with picklist excludes all in where condition
    public void rulesThree() throws Exception {
       sfdc.runApexCode(getNameSpaceResolvedFileContents(LoadtoCust_Picklist));
        System.out.print("Filename = " + LoadtoCust_Picklist);
       sfdc.runApexCode(getNameSpaceResolvedFileContents(LoadtoCust_Picklist1));
        System.out.print("Filename =" + LoadtoCust_Picklist1);
        SObject[] rules =sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='LoadtoCust_Picklist'");
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
        SObject[] rules1 =sfdc.getRecords("Select count(Id) From Account Where ((Rating NOT IN ('Hot','Warm','Cold'))) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 =sfdc.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // In FIlters And+Or condition
    public void rulesFour() throws Exception {
       sfdc.runApexCode(getNameSpaceResolvedFileContents(WithAndOrConditionFilters));
        System.out.print("Filename = " + WithAndOrConditionFilters);
       sfdc.runApexCode(getNameSpaceResolvedFileContents(WithAndOrConditionFilters1));
        System.out.print("Filename =" + WithAndOrConditionFilters1);
        SObject[] rules =sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='WithAndOrConditionFilters'");
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
        SObject[] rules1 =sfdc.getRecords("Select Count(Id) From Account Where ((Id != null) AND ((IsDeleted != false) OR (JBCXM__CustomerInfo__r.Id != null))) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 =sfdc.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // Date Sync for Load to Customer with Today's date (In Where Account Name
    // contains B)
    public void rulesFive() throws Exception {
       sfdc.runApexCode(getNameSpaceResolvedFileContents(OCD_Today));
        System.out.print("Filename = " + OCD_Today);
       sfdc.runApexCode(getNameSpaceResolvedFileContents(OCD_Today1));
        System.out.print("Filename =" + OCD_Today1);
        SObject[] rules =sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='OCD_Today'");
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
        SObject[] rules1 =sfdc.getRecords("Select Count(Id) From Account Where ((Name LIKE '%A%')) AND JBCXM__CustomerInfo__c != null ");
        SObject[] rules2 =sfdc.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c where JBCXM__OriginalContractDate__c=TODAY and JBCXM__CustomerName__c Like'%A%'");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // Data Sync for load to customers with aggregation in setup rule and
    // advance criteria in setup actions.
    public void rulesSix() throws Exception {
       sfdc.runApexCode(getNameSpaceResolvedFileContents(CountCust));
        System.out.print("Filename = " + CountCust);
       sfdc.runApexCode(getNameSpaceResolvedFileContents(CountCust1));
        System.out.print("Filename =" + CountCust1);
        SObject[] rules =sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='CountCust'"));
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
        SObject[] rules1 =sfdc.getRecords("Select Count(Id) From Account Where ((Id != null) AND (JBCXM__CustomerInfo__r.Id != null)) AND JBCXM__CustomerInfo__c != null");
        SObject[] rules2 =sfdc.getRecords("SELECT count(Id) FROM JBCXM__CustomerInfo__c where JBCXM__MRR__c=1");
        System.out.println(rules1[0].getChild("expr0").getValue());
        System.out.println(rules2[0].getChild("expr0").getValue());
        Assert.assertEquals(
                rules1[0].getChild("expr0").getValue()
                        .equals(rules2[0].getChild("expr0").getValue()), true);
    }

    @Test
    // Preview Results
    public void rulesSeven() throws Exception {
       sfdc.runApexCode(getNameSpaceResolvedFileContents(PreviewResults));
        System.out.print("Filename = " + PreviewResults);
        SObject[] rules =sfdc.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='PreviewResults'");
        for (SObject r : rules) {
            String rawBody = "{\"numberOfRecords\": \"10\"}";
            System.out.println(PropertyReader.nsAppUrl + "/api" + "/eventrule/" + r.getId() + "/result");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "/result", header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = RulesUtil.convertToObject(result
                    .getContent());
            // Assert.assertEquals(ro.getData().size(), 10);
            List<Object> data = (List<Object>) responseObj.getData();
            Assert.assertTrue(data.size() <= 10);
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());

        }
    }

    @AfterClass
    public void afterClass() {
        //GSUtil.soql = null;
    }
}
