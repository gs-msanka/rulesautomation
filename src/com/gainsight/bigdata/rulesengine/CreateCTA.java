package com.gainsight.bigdata.rulesengine;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pojos.CTA;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

public class CreateCTA extends RulesUtil {

	private static final String rulesDir = Application.basedir
			+ "/testdata/newstack/RulesEngine/CreateCTA/";
	private static final String CreateCTACustomer = rulesDir
			+ "CreateCTACustomer.apex";
	private static final String CleanUpForRules = Application.basedir
			+ "/testdata/newstack/RulesEngine/scripts/CleanUpForRules.apex";
	private static final String CreateOwnerField = rulesDir
			+ "CreateOwnerField.apex";
	private static final String AssignValuesToStandardFields = rulesDir
			+ "AssignValuesToStandardFields.apex";
	private static final String AssignValuesToCustomFields = rulesDir
			+ "AssignValuesToCustomFields.apex";
	ResponseObj result = null;
	private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/CreateCTA/CreateCTAs.xls";
	String nsAppUrl = PropertyReader.nsAppUrl;

	@BeforeClass
	public void beforeClass() throws Exception {
		sfdc.connect();
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanUpForRules));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
		updateNSURLInAppSettings(env.getProperty("ns.appurl"));
		populateObjMaps();
	}

	// Create CTA : No Advance Criteria, No Playbook, No Token, No Owner Field.
	@TestInfo(testCaseIds="{GS-5572}")
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule1")
	public void Rule1(HashMap<String, String> testData) throws Exception {
		String ActInfo=testData.get("JBCXM__ActionInfo__c");
		testData.put("JBCXM__ActionInfo__c",ActInfo.replace("USER.Id", sfdc
				.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
				.getChild("CreatedById").getValue().toString()));
		 Assert.assertTrue(createAndRunRule(testData),"Rule Created and Ran Successfully!");
		 CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		 Assert.assertTrue(isCTACreateSuccessfully(cta.getPriority(),
				cta.getStatus(), cta.getCustomer(), cta.getType(),
				cta.getReason(), cta.getComments(), testData.get("Name"), null, false));
	}

	// Create CTA : No Advance Criteria, Yes Playbook, No Token, No Owner Field.
	@TestInfo(testCaseIds="{GS-5573}")
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule2")
	public void Rule2(HashMap<String, String> testData) throws Exception {
		String ActInfo=testData.get("JBCXM__ActionInfo__c");
		testData.put("JBCXM__ActionInfo__c",ActInfo.replace("USER.Id", sfdc
				.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
				.getChild("CreatedById").getValue().toString()));
		 Assert.assertTrue(createAndRunRule(testData),"Rule Created and Ran Successfully!");
		 CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		 Assert.assertTrue(isCTACreateSuccessfully(cta.getPriority(),
				cta.getStatus(), cta.getCustomer(), cta.getType(),
				cta.getReason(), cta.getComments(), testData.get("Name"), cta.getPlaybookName(), false));
	}

	// Create CTA : No Advance Criteria, Yes Playbook, No Token, Yes Owner Field.
	@TestInfo(testCaseIds="{GS-5574}")
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule3")
	public void Rule3(HashMap<String, String> testData) throws Exception {
		metadataClient.deleteFields("Account", new String[] { "C_Reference" });
		metadataClient.createLookupField("Account",
				new String[] { "C_Reference" }, new String[] { "User",
						"Acco2untS_AutomationS" });
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateOwnerField));
		String ActInfo=testData.get("JBCXM__ActionInfo__c");
		testData.put("JBCXM__ActionInfo__c",ActInfo.replace("USER.Id", sfdc
				.getRecords(resolveStrNameSpace("SELECT C_Reference__c FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
				.getChild("C_Reference__c").getValue().toString()));
		 Assert.assertTrue(createAndRunRule(testData),"Rule Created and Ran Successfully!");
		 CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		 Assert.assertTrue(isCTACreateSuccessfully(cta.getPriority(),
				cta.getStatus(), cta.getCustomer(), cta.getType(),
				cta.getReason(), cta.getComments(), testData.get("Name"), null, true));

	}

	// Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard Object), No Owner Field.
	// Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue,NumberOfEmployees, Description, OwnerId. (Token are only Standard fields from Account Object are taken here)
	@TestInfo(testCaseIds="{GS-5575,GS-4259}")
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule4")
	public void Rule4(HashMap<String, String> testData) throws Exception {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
		// Assign value to standard fields(Description,fax,Type,Annual
		// Revenue,Employees,website) in Account Object
		String ActInfo=testData.get("JBCXM__ActionInfo__c");
		testData.put("JBCXM__ActionInfo__c",ActInfo.replace("USER.Id", sfdc
				.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
				.getChild("CreatedById").getValue().toString()));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AssignValuesToStandardFields));
		 Assert.assertTrue(createAndRunRule(testData),"Rule Created and Ran Successfully!");
		
		// Verify if CTA created has Priority as HIGH, Status as Open, Assigned
		// as same value of Account createdby,Type as Risk, Reason as Product
		// Performance,Playbook as
		// "Drop in Usage and Comment as initialized below"
		HashMap<String, String> fieldsAndValues = new HashMap<String, String>();
		fieldsAndValues.put("Id", "");
		fieldsAndValues.put("Name", "");
		fieldsAndValues.put("Type", "");
		fieldsAndValues.put("Fax", "");
		fieldsAndValues.put("Website", "");
		fieldsAndValues.put("AnnualRevenue", "");
		fieldsAndValues.put("NumberOfEmployees", "");
		fieldsAndValues.put("Description", "");
		fieldsAndValues.put("OwnerId", "");

		getSFValues(fieldsAndValues, "Account:Name:Create CTA No Adv Criteria");
		String OwnerName = sfdc.getRecords("Select Name from User where Id='"
				+ fieldsAndValues.get("OwnerId") + "'")[0].getName().toString();
		// This CTA is assigned to AccountID ${Id} Account Name:${Name}
		// ${Description} ${Fax} ${Type} ${AnnualRevenue} ${NumberOfEmployees}
		// ${LastActivityDate} ${Website} ${OwnerId} ${Owner.Name}
		String Comment = "This CTA is assigned to AccountID "
				+ fieldsAndValues.get("Id") + " Account Name:"
				+ fieldsAndValues.get("Name") + " "
				+ fieldsAndValues.get("Description") + " "
				+ fieldsAndValues.get("Fax") + " "
				+ fieldsAndValues.get("Type") + " "
				+ fieldsAndValues.get("AnnualRevenue") + " "
				+ fieldsAndValues.get("NumberOfEmployees") + " NA" + " "
				+ fieldsAndValues.get("Website") + " "
				+ fieldsAndValues.get("OwnerId") + " " + OwnerName;
		// Comment=Comment.replaceAll(" ", "");

		// Verify if CTA created has Priority as HIGH, Status as Open, Assigned
		// as same value of Account createdby,Type as Risk, Reason as Product
		// Performance
		CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		cta.setComments(Comment);
		System.out.println(cta.getComments());
		Assert.assertTrue(isCTACreateSuccessfully(cta.getPriority(),
				cta.getStatus(), cta.getCustomer(), cta.getType(),
				cta.getReason(), cta.getComments(), testData.get("Name"),
				cta.getPlaybookName(), false));

	}

	// Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard
	// Object), Yes Owner Field.
	// Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue,
	// NumberOfEmployees, Description, OwnerId. (Token are only Standard fields
	// from Account Object are taken here)
	@TestInfo(testCaseIds="{GS-5576}")
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Rule5")
	public void Rule5(HashMap<String, String> testData) throws Exception {
		// Custom Fields to Delete from Account Object
		String FieldsToDelete[] = { "C_Text", "C_Number", "C_Checkbox",
				"C_Currency", "C_Email", "C_Percent", "C_Phone", "C_Picklist",
				"C_MultiPicklist", "C_TextArea" };
		String FieldsToDelete1[] = { "C_EncryptedString", "C_URL",
				"C_Reference" };
		// Delete Custom Fields
		metadataClient.deleteFields("Account", FieldsToDelete);
		metadataClient.deleteFields("Account", FieldsToDelete1);
		// Create Custom Fields
		metaUtil.createFieldsOnAccount(sfdc, sfinfo);
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateOwnerField));
		// Assign value to Custom
		// fields("C_Text__c","C_Number__c","C_Checkbox__c","C_Currency__c","C_Email__c","C_Percent__c","C_Phone__c","C_Picklist__c","C_MultiPicklist__c","C_TextArea__c","C_EncryptedString__c","C_URL__c")
		// in Account Object
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AssignValuesToCustomFields));
		// Rule Config
		String ActInfo=testData.get("JBCXM__ActionInfo__c");
		testData.put("JBCXM__ActionInfo__c",ActInfo.replace("USER.Id", sfdc
				.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
				.getChild("CreatedById").getValue().toString()));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AssignValuesToStandardFields));
		 Assert.assertTrue(createAndRunRule(testData),"Rule Created and Ran Successfully!");
		 CTA cta = mapper.readValue(testData.get("CTACriteria"), CTA.class);
		// Assigning Value to Comment which has all Token values
		HashMap<String, String> fAndV = new HashMap<String, String>();
		fAndV.put("Id", "");
		fAndV.put("C_Text__c", "");
		fAndV.put("C_Number__c", "");
		fAndV.put("C_Checkbox__c", "");
		fAndV.put("C_Currency__c", "");
		fAndV.put("C_Email__c", "");
		fAndV.put("C_Percent__c", "");
		fAndV.put("C_Phone__c", "");
		fAndV.put("C_Picklist__c", "");
		fAndV.put("C_MultiPicklist__c", "");
		fAndV.put("C_TextArea__c", "");
		fAndV.put("C_EncryptedString__c", "");
		fAndV.put("C_URL__c", "");
		fAndV.put("C_Reference__c", "");
		fAndV = getSFValues(fAndV, "Account:Name:Create CTA No Adv Criteria");
		// This CTA is assigned to AccountID ${Id} ${C_Checkbox__c}
		// ${C_Currency__c} ${C_Email__c} ${C_EncryptedString__c}
		// ${C_MultiPicklist__c} ${C_Number__c} ${C_Percent__c} ${C_Phone__c}
		// ${C_Picklist__c} ${C_Reference__c} ${C_Text__c} ${C_TextArea__c}
		// ${C_URL__c}
		String Comment = "This CTA is assigned to AccountID " + fAndV.get("Id")
				+ " " + fAndV.get("C_Checkbox__c") + " "
				+ fAndV.get("C_Currency__c") + " " + fAndV.get("C_Email__c")
				+ " " + fAndV.get("C_EncryptedString__c") + " "
				+ fAndV.get("C_MultiPicklist__c") + " "
				+ fAndV.get("C_Number__c") + " " + fAndV.get("C_Percent__c")
				+ " " + fAndV.get("C_Phone__c") + " "
				+ fAndV.get("C_Picklist__c") + " "
				+ fAndV.get("C_Reference__c") + " " + fAndV.get("C_Text__c")
				+ " " + fAndV.get("C_TextArea__c") + " "
				+ fAndV.get("C_URL__c");
		// Comment=Comment.replaceAll(" ", "");
		// Verify if CTA created has Priority as HIGH, Status as Open, Assigned
		// as same value of Account createdby,Type as Risk, Reason as Product
		// Performance
		cta.setComments(Comment); 
		Assert.assertTrue(isCTACreateSuccessfully(cta.getPriority(),
				cta.getStatus(), cta.getCustomer(), cta.getType(),
				cta.getReason(), cta.getComments(), testData.get("Name"), null, false));

	}
}
