package com.gainsight.bigdata.rulesengine;

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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by sparava on 11/17/14.
 */
public class SendEmail {
    public WebAction webAction = new WebAction();
    public SFDCUtil sfdc = new SFDCUtil();
    public Header header = new Header();
    public SOQLUtil soql = new SOQLUtil();
    public String rulesDir = TestEnvironment.basedir + "/testdata/newstack/RulesEngine/SendEmail/";
    public String GSEmailAccountStrategy = rulesDir + "GSEmailAccountStrategy.apex";
    public String GSEmailAccountStrategy1 = rulesDir + "GSEmailAccountStrategy1.apex";
    public String GSEmailContactStrategy = rulesDir + "GSEmailContactStrategy.apex";
    public String GSEmailContactStrategy1 = rulesDir + "GSEmailContactStrategy1.apex";
    public String GSEmailEmailStrategy = rulesDir + "GSEmailEmailStrategy.apex";
    public String GSEmailEmailStrategy1 = rulesDir + "GSEmailEmailStrategy1.apex";
    public String Contacts = rulesDir + "Contacts.apex";

    @BeforeClass
    public void beforeClass() throws Exception {
    	sfdc.runApexCodeFromFile(Contacts, true);
        LoginUtil.sfdcLogin(soql, header, webAction);
    }

    @Test
    public void testGSEmailAccountStrategy() throws Exception {
        sfdc.runApexCodeFromFile(GSEmailAccountStrategy, true);
        sfdc.runApexCodeFromFile(GSEmailAccountStrategy1, true);

        SObject[] rules = soql
                .getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='Send Email Account Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = LoginUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            Thread.sleep(20000);

            SObject[] res = soql
                    .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild("JBCXM__LastRunResult__c").getValue());
                Assert.assertEquals("success", obj.getChild("JBCXM__LastRunResult__c").getValue().toString().toLowerCase());
            }
        }
    }


    @Test
    public void testGSEmailContactStrategy() throws Exception {
        sfdc.runApexCodeFromFile(GSEmailContactStrategy, true);
        sfdc.runApexCodeFromFile(GSEmailContactStrategy1, true);

        SObject[] rules = soql
                .getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='Send Email Contact Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = LoginUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            Thread.sleep(20000);

            SObject[] res = soql
                    .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild("JBCXM__LastRunResult__c").getValue());
                Assert.assertEquals("success", obj.getChild("JBCXM__LastRunResult__c").getValue().toString().toLowerCase());
            }
        }
    }

    @Test
    public void testGSEmailEmailStrategy() throws Exception {
        sfdc.runApexCodeFromFile(GSEmailEmailStrategy, true);
        sfdc.runApexCodeFromFile(GSEmailEmailStrategy1, true);

        SObject[] rules = soql
                .getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='Send Email Email Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = LoginUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            Thread.sleep(20000);

            SObject[] res = soql
                    .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild("JBCXM__LastRunResult__c").getValue());
                Assert.assertEquals("success", obj.getChild("JBCXM__LastRunResult__c").getValue().toString().toLowerCase());
            }
        }
    }
    @AfterClass
	public void afterClass() throws ConnectionException, InterruptedException {
		soql = null;
	}

}
