package com.gainsight.bigdata.rulesengine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.webaction.WebAction;
import com.sforce.soap.partner.sobject.SObject;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class rules {
	private static final String CustomerInfo = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/CustomerInfo.apex";
	private static final String CustomerInfo1 = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/CustomerInfo1.apex";
	private static final String LoadToCustomer = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/LoadToCustomer.apex";
	private static final String LoadToCustomer1 = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/LoadToCustomer1.apex";
	private static final String LoadtoCust_Picklist = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/LoadtoCust_Picklist.apex";
	private static final String LoadtoCust_Picklist1 = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/LoadtoCust_Picklist1.apex";
	private static final String WithAndOrConditionFilters = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/WithAndOrConditionFilters.apex";
	private static final String WithAndOrConditionFilters1 = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/WithAndOrConditionFilters1.apex";
	private static final String OCD_Today = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/OCD_Today.apex";
	private static final String OCD_Today1 = TestEnvironment.basedir
			+ "/testdata/newstack/RulesEngine/OCD_Today1.apex";
	static NSInfo nsinfo;
	static WebAction wa;
	protected SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
	protected TestEnvironment env = new TestEnvironment();
	private Boolean isPackaged =Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
	public static SFDCUtil sfdc = new SFDCUtil();
	public Header h;
	SOQLUtil soql = new SOQLUtil();

	/**
	 * @param args
	 * @throws Exception
	 * 
	 */
	
	//Work In Progress Need to optimize the code as we will proceed
	
	@BeforeClass
	public void beforeClass() throws Exception {
		nsinfo = NSUtil.fetchNewStackInfo(sfinfo, new Header());
		
		soql.login(env.getUserName(), env.getUserPassword(),
				env.getProperty("sfdc.stoken"));

		wa = new WebAction();
		h = new Header();
		h.addHeader("appOrgId", sfinfo.getOrg());
		h.addHeader("appSessionId", sfinfo.getSessionId());
		h.addHeader("appUserId", sfinfo.getUserId());
		h.addHeader("Content-Type", "application/json");
		System.out.println("endpoint:" + sfinfo.getEndpoint());
		//"https://jbcxm.na10.visual.force.com"
		String SFInstance=sfinfo.getEndpoint().split("https://")[1].split("\\.")[0];
		String OriginHeader="";
		if(isPackaged)
		OriginHeader	="https://jbcxm."+SFInstance+".visual.force.com";
		else
			OriginHeader	="https://"+SFInstance+".visual.force.com";
		
		System.out.println("OriginHeader value="+OriginHeader);
		h.addHeader("Origin", OriginHeader);
		
	}

	@AfterClass
	public void afterClass() {

	}

	// Its for CustomerInfo Sync when Checkbox Apply to Gainsight customers is
	// not enabled

	@Test
	public void rulesOne() throws Exception {

		sfdc.runApexCodeFromFile(CustomerInfo, true);
		System.out.print("Filename = " + CustomerInfo);
		sfdc.runApexCodeFromFile(CustomerInfo1, true);
		System.out.print("Filename =" + CustomerInfo);
		SObject[] rules = soql
				.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='CusotomerSync_Initially no customers'");
		for (SObject r : rules) {
			String rawBody = ("{}");
			HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
					+ "/eventrule" + "/" + r.getId() + "", rawBody,
					h.getAllHeaders());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(result.getContent());
			Assert.assertTrue(tree1.findValue("result").asBoolean());
			Assert.assertNotNull(tree1.findValue("requestId").isNull());
			Thread.sleep(10000);
		}
		SObject[] rules1 = soql.getRecords("SELECT count(Id) FROM Account");
		SObject[] rules2 = soql.getRecords("SELECT count(Id) FROM JBCXM__CustomerInfo__c");
		System.out.println(rules1[0].getChild("expr0").getValue());
		System.out.println(rules2[0].getChild("expr0").getValue());
		Assert.assertEquals(
				rules1[0].getChild("expr0").getValue()
						.equals(rules2[0].getChild("expr0").getValue()), true);
		}

	@Test
	// Load to customer with Account names starts with A and ASV=4545
	public void rulesTwo() throws Exception {
		sfdc.runApexCodeFromFile(LoadToCustomer, true);
		System.out.print("Filename = " + LoadToCustomer);
		sfdc.runApexCodeFromFile(LoadToCustomer1, true);
		System.out.print("Filename =" + LoadToCustomer1);
		SObject[] rules = soql
				.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='LoadToCustomer'");
		for (SObject r : rules) {
			String rawBody = ("{}");
			HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
					+ "/eventrule" + "/" + r.getId() + "", rawBody,
					h.getAllHeaders());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(result.getContent());
			Assert.assertTrue(tree1.findValue("result").asBoolean());
			Assert.assertNotNull(tree1.findValue("requestId").isNull());
			Thread.sleep(30000);
		}
		SObject[] rules1 = soql
				.getRecords("Select count(Id) From Account Where ((JBCXM__CustomerInfo__r.JBCXM__CustomerName__c LIKE 'A%')) AND JBCXM__CustomerInfo__c != null");
		SObject[] rules2 = soql
				.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c where JBCXM__CustomerName__c LIKE 'A%' and JBCXM__ASV__c=4545");
		System.out.println(rules1[0].getChild("expr0").getValue());
		System.out.println(rules2[0].getChild("expr0").getValue());
		Assert.assertEquals(
				rules1[0].getChild("expr0").getValue()
						.equals(rules2[0].getChild("expr0").getValue()), true);
		}

	
	@Test
	//Load to customer with picklist excludes all in where condition
    public void rulesThree() throws Exception {
		sfdc.runApexCodeFromFile(LoadtoCust_Picklist, true);
		System.out.print("Filename = " + LoadtoCust_Picklist);
		sfdc.runApexCodeFromFile(LoadtoCust_Picklist1, true);
		System.out.print("Filename =" + LoadtoCust_Picklist1);
		SObject[] rules = soql
				.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='LoadtoCust_Picklist'");
		for (SObject r : rules) {
			String rawBody = ("{}");
			HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
					+ "/eventrule" + "/" + r.getId() + "", rawBody,
					h.getAllHeaders());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(result.getContent());
			Assert.assertTrue(tree1.findValue("result").asBoolean());
			Assert.assertNotNull(tree1.findValue("requestId").isNull());
			Thread.sleep(30000);
		}
		SObject[] rules1 = soql
				.getRecords("Select count(Id) From Account Where ((Rating NOT IN ('Hot','Warm','Cold'))) AND JBCXM__CustomerInfo__c != null");
		SObject[] rules2 = soql
				.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c");
		System.out.println(rules1[0].getChild("expr0").getValue());
		System.out.println(rules2[0].getChild("expr0").getValue());
		Assert.assertEquals(
				rules1[0].getChild("expr0").getValue()
						.equals(rules2[0].getChild("expr0").getValue()), true);
		}

	@Test
	// In FIlters And+Or condition
    public void rulesFour() throws Exception {
		sfdc.runApexCodeFromFile(WithAndOrConditionFilters, true);
		System.out.print("Filename = " + WithAndOrConditionFilters);
		sfdc.runApexCodeFromFile(WithAndOrConditionFilters1, true);
		System.out.print("Filename =" + WithAndOrConditionFilters1);
		SObject[] rules = soql
				.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='WithAndOrConditionFilters'");
		for (SObject r : rules) {
			String rawBody = ("{}");
			HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
					+ "/eventrule" + "/" + r.getId() + "", rawBody,
					h.getAllHeaders());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(result.getContent());
			Assert.assertTrue(tree1.findValue("result").asBoolean());
			Assert.assertNotNull(tree1.findValue("requestId").isNull());
			Thread.sleep(30000);
		}
		SObject[] rules1 = soql
				.getRecords("Select Count(Id) From Account Where ((Id != null) AND ((IsDeleted != false) OR (JBCXM__CustomerInfo__r.Id != null))) AND JBCXM__CustomerInfo__c != null");
		SObject[] rules2 = soql
				.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c");
		System.out.println(rules1[0].getChild("expr0").getValue());
		System.out.println(rules2[0].getChild("expr0").getValue());
		Assert.assertEquals(
				rules1[0].getChild("expr0").getValue()
						.equals(rules2[0].getChild("expr0").getValue()), true);
		}

	@Test
	// Date Sync for Load to Customer with Today's date (In Where Account Name contains B)
    public void rulesFive() throws Exception {
		sfdc.runApexCodeFromFile(OCD_Today, true);
		System.out.print("Filename = " + OCD_Today);
		sfdc.runApexCodeFromFile(OCD_Today1, true);
		System.out.print("Filename =" + OCD_Today1);
		SObject[] rules = soql
				.getRecords("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='OCD_Today'");
		for (SObject r : rules) {
			String rawBody = ("{}");
			HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api"
					+ "/eventrule" + "/" + r.getId() + "", rawBody,
					h.getAllHeaders());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree1 = mapper.readTree(result.getContent());
			Assert.assertTrue(tree1.findValue("result").asBoolean());
			Assert.assertNotNull(tree1.findValue("requestId").isNull());
			Thread.sleep(30000);
		}
		SObject[] rules1 = soql
				.getRecords("Select Count(Id) From Account Where ((Name LIKE '%A%')) AND JBCXM__CustomerInfo__c != null ");
		SObject[] rules2 = soql
				.getRecords("SELECT Count(Id) FROM JBCXM__CustomerInfo__c where JBCXM__OriginalContractDate__c=TODAY and JBCXM__CustomerName__c Like'%A%'");
		System.out.println(rules1[0].getChild("expr0").getValue());
		System.out.println(rules2[0].getChild("expr0").getValue());
		Assert.assertEquals(
				rules1[0].getChild("expr0").getValue()
						.equals(rules2[0].getChild("expr0").getValue()), true);
		}
}
