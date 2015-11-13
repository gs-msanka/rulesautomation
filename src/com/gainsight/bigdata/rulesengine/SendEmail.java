package com.gainsight.bigdata.rulesengine;

import java.util.HashMap;

import com.gainsight.sfdc.gsEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.utils.DataProviderArguments;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public class SendEmail extends RulesUtil {
	public WebAction webAction = new WebAction();
	public Header header = new Header();
	public String rulesDir = Application.basedir
			+ "/testdata/newstack/RulesEngine/SendEmail/";
	public String GSEmailContactStrategy = rulesDir
			+ "GSEmailContactStrategy.apex";
	public String GSEmailContactStrategy1 = rulesDir
			+ "GSEmailContactStrategy1.apex";
	public String GSEmailEmailStrategy = rulesDir + "GSEmailEmailStrategy.apex";
	public String GSEmailEmailStrategy1 = rulesDir
			+ "GSEmailEmailStrategy1.apex";
	public String Contacts = rulesDir + "Contacts.apex";
	public String AutomatedAlertRulesObjectName = "JBCXM__AutomatedAlertRules__c";
	public String LastRunResultFieldName = "JBCXM__LastRunResult__c";
	public String templateId = "";
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/SendEmail/SendEmail.xls";
	ResponseObj result = null;

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(Contacts));
		LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
		// Enable GS Email service - from Admin-Integration page
		updateNSURLInAppSettings(env.getProperty("ns.appurl"));
		BaseTest baseTest = new BaseTest();
		baseTest.init();
		BasePage basepage = new BasePage();
		basepage.login();
		GSEmailSetup gsEmail = new GSEmailSetup();
		gsEmail.enableOAuthForOrg();
		gsEmail.enableGSEmailInAdmin();
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
	public void testGSEmailAccountStrategy(HashMap<String, String> testData)
			throws Exception {
		RulesUtil ru = new RulesUtil();
		ru.setupRule(testData);
		String ruleId = getRuleId(testData.get("JBCXM__AutomatedAlertRules__c"));
		Log.info("request:" + nsConfig.getNsURl()
				+ "/api/eventrule/" + ruleId);
		result = wa.doPost(
                nsConfig.getNsURl() + "/api/eventrule/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ nsConfig.getNsURl() + "/api/eventrule/" + ruleId
				+ "\n Request rawBody:{}");
		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='"
						+ ruleId + "'")[0].getChild("JBCXM__LastRunResult__c")
				.getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);
	}

	/*
	 * @Test public void testGSEmailContactStrategy() throws Exception {
	 * GSUtil.runApexCode(GSEmailContactStrategy);
	 * GSUtil.runApexCodeByReplacingTemplateId(GSEmailContactStrategy1,
	 * templateId);
	 * 
	 * SObject[] rules = soql .getRecords("select Id,Name from " +
	 * AutomatedAlertRulesObjectName +
	 * " where Name='Send Email Contact Strategy'"); for (SObject r : rules) {
	 * String rawBody = ("{}"); HttpResponseObj result =
	 * webAction.doPost(PropertyReader.nsAppUrl + "/api" + "/eventrule" + "/" +
	 * r.getId() + "", rawBody, header.getAllHeaders()); ResponseObject
	 * responseObj = GSUtil.convertToObject(result .getContent());
	 * Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
	 * Assert.assertNotNull(responseObj.getRequestId());
	 * GSUtil.waitForCompletion(r.getId(), webAction, header);
	 * 
	 * SObject[] res = soql .getRecords("select " + LastRunResultFieldName +
	 * " from " + AutomatedAlertRulesObjectName + " where Id='" + r.getId() +
	 * "'"); for (SObject obj : res) {
	 * Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
	 * Assert.assertEquals("success",
	 * obj.getChild(LastRunResultFieldName).getValue
	 * ().toString().toLowerCase()); } } }
	 * 
	 * @Test public void testGSEmailEmailStrategy() throws Exception {
	 * GSUtil.runApexCode(GSEmailEmailStrategy);
	 * GSUtil.runApexCodeByReplacingTemplateId(GSEmailEmailStrategy1,
	 * templateId);
	 * 
	 * SObject[] rules = soql .getRecords("select Id,Name from " +
	 * AutomatedAlertRulesObjectName +
	 * " where Name='Send Email Email Strategy'"); for (SObject r : rules) {
	 * String rawBody = ("{}"); HttpResponseObj result =
	 * webAction.doPost(PropertyReader.nsAppUrl + "/api" + "/eventrule" + "/" +
	 * r.getId() + "", rawBody, header.getAllHeaders()); ResponseObject
	 * responseObj = GSUtil.convertToObject(result .getContent());
	 * Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
	 * Assert.assertNotNull(responseObj.getRequestId());
	 * GSUtil.waitForCompletion(r.getId(), webAction, header);
	 * 
	 * SObject[] res = soql .getRecords("select " + LastRunResultFieldName +
	 * " from " + AutomatedAlertRulesObjectName + " where Id='" + r.getId() +
	 * "'"); for (SObject obj : res) {
	 * Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue());
	 * Assert.assertEquals("success",
	 * obj.getChild(LastRunResultFieldName).getValue
	 * ().toString().toLowerCase()); } } }
	 */
}
