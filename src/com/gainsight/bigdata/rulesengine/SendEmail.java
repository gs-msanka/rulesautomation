package com.gainsight.bigdata.rulesengine;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.http.Header;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.TestEnvironment;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * Created by sparava on 11/17/14.
 */
public class SendEmail {
    public WebAction webAction = new WebAction();
    public Header header = new Header();
//    public SOQLUtil soql;
    public String rulesDir = TestEnvironment.basedir + "/testdata/newstack/RulesEngine/SendEmail/";
    public String GSEmailAccountStrategy = rulesDir + "GSEmailAccountStrategy.apex";
    public String GSEmailAccountStrategy1 = rulesDir + "GSEmailAccountStrategy1.apex";
    public String GSEmailContactStrategy = rulesDir + "GSEmailContactStrategy.apex";
    public String GSEmailContactStrategy1 = rulesDir + "GSEmailContactStrategy1.apex";
    public String GSEmailEmailStrategy = rulesDir + "GSEmailEmailStrategy.apex";
    public String GSEmailEmailStrategy1 = rulesDir + "GSEmailEmailStrategy1.apex";
    public String Contacts = rulesDir + "Contacts.apex";
    public String AutomatedAlertRulesObjectName = "JBCXM__AutomatedAlertRules__c";
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";
    public String templateId = "";

/*    @BeforeClass
    public void beforeClass() throws Exception {
        GSUtil.runApexCode(Contacts);
        GSUtil.sfdcLogin(header, webAction);
//        soql = GSUtil.soql;
        AutomatedAlertRulesObjectName = GSUtil.resolveStrNameSpace(AutomatedAlertRulesObjectName);
        LastRunResultFieldName = GSUtil.resolveStrNameSpace(LastRunResultFieldName);
//        SObject[] templates = soql.getRecords("SELECT Id FROM EmailTemplate where Name='Gainsight sample template - measure below threshold'");
//        templateId = (String) templates[0].getChild("Id").getValue();
    }

    @Test
    public void testGSEmailAccountStrategy() throws Exception {
        GSUtil.runApexCode(GSEmailAccountStrategy);
        GSUtil.runApexCodeByReplacingTemplateId(GSEmailAccountStrategy1, templateId);

        SObject[] rules = soql
                .getRecords("select Id,Name from " + AutomatedAlertRulesObjectName + " where Name='Send Email Account Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), webAction, header);
            SObject[] res = soql
                    .getRecords("select " + LastRunResultFieldName + " from " + AutomatedAlertRulesObjectName + " where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
    }


    @Test
    public void testGSEmailContactStrategy() throws Exception {
        GSUtil.runApexCode(GSEmailContactStrategy);
        GSUtil.runApexCodeByReplacingTemplateId(GSEmailContactStrategy1, templateId);

        SObject[] rules = soql
                .getRecords("select Id,Name from " + AutomatedAlertRulesObjectName + " where Name='Send Email Contact Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), webAction, header);

            SObject[] res = soql
                    .getRecords("select " + LastRunResultFieldName + " from " + AutomatedAlertRulesObjectName + " where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
    }

    @Test
    public void testGSEmailEmailStrategy() throws Exception {
        GSUtil.runApexCode(GSEmailEmailStrategy);
        GSUtil.runApexCodeByReplacingTemplateId(GSEmailEmailStrategy1, templateId);

        SObject[] rules = soql
                .getRecords("select Id,Name from " + AutomatedAlertRulesObjectName + " where Name='Send Email Email Strategy'");
        for (SObject r : rules) {
            String rawBody = ("{}");
            HttpResponseObj result = webAction.doPost(PropertyReader.nsAppUrl + "/api"
                            + "/eventrule" + "/" + r.getId() + "", rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), webAction, header);

            SObject[] res = soql
                    .getRecords("select " + LastRunResultFieldName + " from " + AutomatedAlertRulesObjectName + " where Id='" + r.getId() + "'");
            for (SObject obj : res) {
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
    }

    @AfterClass
    public void afterClass() throws ConnectionException, InterruptedException {
        soql = null;
    }
*/
}
