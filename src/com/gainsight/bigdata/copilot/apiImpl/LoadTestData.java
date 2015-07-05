package com.gainsight.bigdata.copilot.apiImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.ObjectFields;
//import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.testdriver.Application;
import com.gainsight.util.MetaDataUtil;

public class LoadTestData extends NSTestBase{

	MetaDataUtil md=new MetaDataUtil();
	
	private static final String CoPilotDir = Application.basedir
			+ "/testdata/newstack/CoPilot";
	private static final String CleanUp = CoPilotDir
			+ "/CleanUps/CleanUpDatas.apex";
	private static final String Job_C_ContactObject2 = CoPilotDir
			+ "/Job/job_C_ContactObject2.txt";
	private static final String Job_Account = CoPilotDir
			+ "/Job/job_Account.txt";
	private static final String Job_CustomerInfo = CoPilotDir
			+ "/Job/job_CustomerInfo.txt";
	private static final String Job_Contact = CoPilotDir
			+ "/Job/job_Contact.txt";
	private static final String Job_Case = CoPilotDir
	+ "/Job/job_Case.txt";
	private static final String Job_C_ContactObject1 = CoPilotDir
			+ "/Job/job_C_ContactObject1.txt";
	/*private static final String Scorecard_Enable = CoPilotDir
			+ "/Job/job_C_ContactObject1.txt";*/
	
	private DataETL dataETL;
	
	@BeforeTest
	public void cleanUp() {
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanUp));
	}
	
	@Test
	public void createCustomFields() throws Exception {
		
		ObjectFields objField=new ObjectFields();
		
		//createCustomObject
		metadataClient.createCustomObject("C_ContactObject");
		metadataClient.createCustomObject("C_ContactObject2");
		
		List<HashMap<String, String>> formulaFieldsList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> formulafieldsMap=new HashMap<String, String>();
			formulafieldsMap.put("Type", "Number");		
			formulafieldsMap.put("Formula", "NumberOfEmployees");
			formulafieldsMap.put("FieldName", "p_c_Formula");
			formulafieldsMap.put("Description", "Sample Description");
			formulafieldsMap.put("HelpText", "Sample HelpText");	
		formulaFieldsList.add(formulafieldsMap);
		objField.setFormulaFieldsList(formulaFieldsList);
		
		List<String> externalID_Text = new ArrayList<String>();
		externalID_Text.add("p_c_ExternalID");
		objField.setExternalID_Text(externalID_Text);
		
		List<String> textFields = new ArrayList<String>();
		textFields.add("p_c_Text");
		objField.setTextFields(textFields);
		
		List<String> numberFields = new ArrayList<String>();
		numberFields.add("p_c_Number");                           
		objField.setNumberFields(numberFields);
		
		List<String> checkBoxes = new ArrayList<String>();
		checkBoxes.add("p_c_Checkbox");	
		objField.setCheckBoxes(checkBoxes);
		
		List<String> Currency = new ArrayList<String>();
		Currency.add("p_c_Currency");
		objField.setCurrencies(Currency);
		
		List<String> Email = new ArrayList<String>();
		Email.add("p_c_Email");
		objField.setEmails(Email);
		
		List<String> Percent = new ArrayList<String>();
		Percent.add("p_c_Percent");
		objField.setPercents(Percent);
		
		List<String> Phone = new ArrayList<String>();
		Phone.add("p_c_PhoneNumber");
		objField.setPhones(Phone);
		
		
		List<HashMap<String,String[]>> pickLists= new ArrayList<HashMap<String,String[]>>();
		HashMap<String,String[]> hmPickList1 = new HashMap<String,String[]>();
		hmPickList1.put("p_c_Picklist", new String[]{"Pvalue1","Pvalue2","Pvalue3"});
		pickLists.add(hmPickList1);
		objField.setPickLists(pickLists);
		
		List<HashMap<String,String[]>> multiPickLists= new ArrayList<HashMap<String,String[]>>();
		HashMap<String,String[]> hmMultiPickList1 = new HashMap<String,String[]>();
		hmMultiPickList1.put("p_c_MultiPicklist", new String[]{"MPvalue1","MPvalue2","MPvalue3"});
		multiPickLists.add(hmMultiPickList1);
		objField.setMultiPickLists(multiPickLists);
		
		List<String> autoNumber = new ArrayList<String>();
		autoNumber.add("p_c_AutoNumber");
		objField.setAutoNumber(autoNumber);
		
		List<String> Date = new ArrayList<String>();
		Date.add("p_c_Date");
		objField.setDates(Date);
		
		List<String> DateTime = new ArrayList<String>();
		DateTime.add("p_c_DateTime");
		objField.setDateTimes(DateTime);
		
		List<String> geoLocation = new ArrayList<String>();
		geoLocation.add("p_c_GeoLocation");
		objField.setGeoLocation(geoLocation);
		
		List<String> textArea_Long = new ArrayList<String>();
		textArea_Long.add("p_c_TextAreaLong");
		objField.setTextArea_Long(textArea_Long);
		
		List<String> textArea_Rich = new ArrayList<String>();
		textArea_Rich.add("p_c_TextAreaRich");
		objField.setTextArea_Rich(textArea_Rich);		
		
		List<String> textArea = new ArrayList<String>();
		textArea.add("p_c_TextArea");
		objField.setTextAreas(textArea);
		
		List<String> encryptedString = new ArrayList<String>();
		encryptedString.add("p_c_TextEncrypted");
		objField.setEncryptedStrings(encryptedString);
		
		List<String> URL = new ArrayList<String>();
		URL.add("p_c_URL");
		objField.setURLs(URL);
		
		md.createFieldsOnObject(sfdc, "Account", objField);
		objField.clearFormulaFieldsList();  //  formulafieldsMap.put("Formula", "NumberOfEmployees");  "NumberOfEmployees" is present only in Account.
		md.createFieldsOnObject(sfdc, "JBCXM__CustomerInfo__c", objField);
		md.createFieldsOnObject(sfdc, "C_ContactObject__c", objField);		
		md.createFieldsOnObject(sfdc, "Contact", objField);
		md.createFieldsOnObject(sfdc, "Case", objField);
		
		
		ObjectFields Customer_ObjectFields=new ObjectFields();
		
		List<HashMap<String, String>> Customer_formulaFieldsList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> Customer_formulafieldsMap=new HashMap<String, String>();
			Customer_formulafieldsMap.put("Type", "Number");		
			Customer_formulafieldsMap.put("Formula", "p_c_Number__c");
			Customer_formulafieldsMap.put("FieldName", "p_c_Formula");
			Customer_formulafieldsMap.put("Description", "Sample Description");
			Customer_formulafieldsMap.put("HelpText", "Sample HelpText");	
		Customer_formulaFieldsList.add(Customer_formulafieldsMap);
		Customer_ObjectFields.setFormulaFieldsList(Customer_formulaFieldsList);
		
		List<HashMap<String, String>> Customer_lookupList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> Customer_lookupListMap1=new HashMap<String, String>();
			Customer_lookupListMap1.put("Name", "p_c_Contact");		
			Customer_lookupListMap1.put("ReferenceTo", "Contact");
			Customer_lookupListMap1.put("ReleationShipName", "Contact_Automation31");
		Customer_lookupList.add(Customer_lookupListMap1);
		HashMap<String, String> Customer_lookupListMap2=new HashMap<String, String>();	
			Customer_lookupListMap2.put("Name", "p_c_User");
			Customer_lookupListMap2.put("ReferenceTo", "User");
			Customer_lookupListMap2.put("ReleationShipName", "User_Automation31");
		Customer_lookupList.add(Customer_lookupListMap2);
		HashMap<String, String> Customer_lookupListMap3=new HashMap<String, String>();
			Customer_lookupListMap3.put("Name", "p_c_customobj_contact");		
			Customer_lookupListMap3.put("ReferenceTo", "C_ContactObject__c");
			Customer_lookupListMap3.put("ReleationShipName", "ContactObject_Automation31");
		Customer_lookupList.add(Customer_lookupListMap3);		
		HashMap<String, String> Customer_lookupListMap4=new HashMap<String, String>();
			Customer_lookupListMap4.put("Name", "p_c_JBCXMobj_Score");		
			Customer_lookupListMap4.put("ReferenceTo", "JBCXM__ScoringSchemeDefinition__c");
			Customer_lookupListMap4.put("ReleationShipName", "p_c_JBCXMobj_Score_Automation31");
		Customer_lookupList.add(Customer_lookupListMap4);		
		
		Customer_ObjectFields.setLookups(Customer_lookupList);
		md.createFieldsOnObject(sfdc, "JBCXM__CustomerInfo__c", Customer_ObjectFields);
		
		
		ObjectFields C_ContactObject_ObjectFields=new ObjectFields();
		
		List<HashMap<String, String>> C_ContactObject_lookupList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> C_ContactObject_lookupListMap1=new HashMap<String, String>();
			C_ContactObject_lookupListMap1.put("Name", "p_c_Contact");		
			C_ContactObject_lookupListMap1.put("ReferenceTo", "Contact");
			C_ContactObject_lookupListMap1.put("ReleationShipName", "Contact_Automation2");
		C_ContactObject_lookupList.add(C_ContactObject_lookupListMap1);
		HashMap<String, String> C_ContactObject_lookupListMap2=new HashMap<String, String>();	
			C_ContactObject_lookupListMap2.put("Name", "p_c_User");
			C_ContactObject_lookupListMap2.put("ReferenceTo", "User");
			C_ContactObject_lookupListMap2.put("ReleationShipName", "User_Automation2");
		C_ContactObject_lookupList.add(C_ContactObject_lookupListMap2);
		HashMap<String, String> C_ContactObject_lookupListMap3=new HashMap<String, String>();	
			C_ContactObject_lookupListMap3.put("Name", "p_c_Account");
			C_ContactObject_lookupListMap3.put("ReferenceTo", "Account");
			C_ContactObject_lookupListMap3.put("ReleationShipName", "Account_Automation2");
		C_ContactObject_lookupList.add(C_ContactObject_lookupListMap3);
		HashMap<String, String> C_ContactObject_lookupListMap4=new HashMap<String, String>();
			C_ContactObject_lookupListMap4.put("Name", "p_c_customobj_contact2");		
			C_ContactObject_lookupListMap4.put("ReferenceTo", "C_ContactObject2__c");
			C_ContactObject_lookupListMap4.put("ReleationShipName", "ContactObject2_Automation2");
		C_ContactObject_lookupList.add(C_ContactObject_lookupListMap4);		
		HashMap<String, String> C_ContactObject_lookupListMap5=new HashMap<String, String>();
			C_ContactObject_lookupListMap5.put("Name", "p_c_JBCXMobj_Score");		
			C_ContactObject_lookupListMap5.put("ReferenceTo", "JBCXM__ScoringSchemeDefinition__c");
			C_ContactObject_lookupListMap5.put("ReleationShipName", "p_c_JBCXMobj_Score_Automation2");
		C_ContactObject_lookupList.add(C_ContactObject_lookupListMap5);		
	
	C_ContactObject_ObjectFields.setLookups(C_ContactObject_lookupList);
	md.createFieldsOnObject(sfdc, "C_ContactObject__c", C_ContactObject_ObjectFields);
	
	
	ObjectFields Account_ObjectFields=new ObjectFields();
	
	List<HashMap<String, String>> Account_lookupList= new ArrayList<HashMap<String, String>>();
	HashMap<String, String> Account_lookupListMap1=new HashMap<String, String>();
		Account_lookupListMap1.put("Name", "p_c_Contact");		
		Account_lookupListMap1.put("ReferenceTo", "Contact");
		Account_lookupListMap1.put("ReleationShipName", "Contact_Automation1");
	Account_lookupList.add(Account_lookupListMap1);
	HashMap<String, String> Account_lookupListMap2=new HashMap<String, String>();	
		Account_lookupListMap2.put("Name", "p_c_User");
		Account_lookupListMap2.put("ReferenceTo", "User");
		Account_lookupListMap2.put("ReleationShipName", "User_Automation1");
	Account_lookupList.add(Account_lookupListMap2);	
	HashMap<String, String> Account_lookupListMap4=new HashMap<String, String>();
		Account_lookupListMap4.put("Name", "p_c_customobj_contact");		
		Account_lookupListMap4.put("ReferenceTo", "C_ContactObject__c");
		Account_lookupListMap4.put("ReleationShipName", "ContactObject_Automation1");
	Account_lookupList.add(Account_lookupListMap4);		
	HashMap<String, String> Account_lookupListMap5=new HashMap<String, String>();
		Account_lookupListMap5.put("Name", "p_c_JBCXMobj_Score");		
		Account_lookupListMap5.put("ReferenceTo", "JBCXM__ScoringSchemeDefinition__c");
		Account_lookupListMap5.put("ReleationShipName", "p_c_JBCXMobj_Score_Automation1");
	Account_lookupList.add(Account_lookupListMap5);		

	Account_ObjectFields.setLookups(Account_lookupList);
	md.createFieldsOnObject(sfdc, "Account", Account_ObjectFields);
	
	ObjectFields Contact_ObjectFields=new ObjectFields();
	
	List<HashMap<String, String>> Contact_lookupList= new ArrayList<HashMap<String, String>>();	
	HashMap<String, String> Contact_lookupListMap2=new HashMap<String, String>();	
		Contact_lookupListMap2.put("Name", "p_c_User");
		Contact_lookupListMap2.put("ReferenceTo", "User");
		Contact_lookupListMap2.put("ReleationShipName", "User_Automation4");
	Contact_lookupList.add(Contact_lookupListMap2);
	HashMap<String, String> Contact_lookupListMap3=new HashMap<String, String>();	
		Contact_lookupListMap3.put("Name", "p_c_Case");
		Contact_lookupListMap3.put("ReferenceTo", "Case");
		Contact_lookupListMap3.put("ReleationShipName", "Case_Automation4");
	Contact_lookupList.add(Contact_lookupListMap3);
	HashMap<String, String> Contact_lookupListMap4=new HashMap<String, String>();
		Contact_lookupListMap4.put("Name", "p_c_customobj_contact");		
		Contact_lookupListMap4.put("ReferenceTo", "C_ContactObject__c");
		Contact_lookupListMap4.put("ReleationShipName", "ContactObject_Automation4");
	Contact_lookupList.add(Contact_lookupListMap4);		
	HashMap<String, String> Contact_lookupListMap5=new HashMap<String, String>();
		Contact_lookupListMap5.put("Name", "p_c_JBCXMobj_Score");		
		Contact_lookupListMap5.put("ReferenceTo", "JBCXM__ScoringSchemeDefinition__c");
		Contact_lookupListMap5.put("ReleationShipName", "p_c_JBCXMobj_Score_Automation4");
	Contact_lookupList.add(Contact_lookupListMap5);		

	Contact_ObjectFields.setLookups(Contact_lookupList);
	md.createFieldsOnObject(sfdc, "Contact", Contact_ObjectFields);
	
	ObjectFields Case_ObjectFields=new ObjectFields();
	
	List<HashMap<String, String>> Case_lookupList= new ArrayList<HashMap<String, String>>();
	HashMap<String, String> Case_lookupListMap1=new HashMap<String, String>();
		Case_lookupListMap1.put("Name", "p_c_Contact");		
		Case_lookupListMap1.put("ReferenceTo", "Contact");
		Case_lookupListMap1.put("ReleationShipName", "Contact_Automation5");
	Case_lookupList.add(Case_lookupListMap1);
	HashMap<String, String> Case_lookupListMap2=new HashMap<String, String>();	
		Case_lookupListMap2.put("Name", "p_c_User");
		Case_lookupListMap2.put("ReferenceTo", "User");
		Case_lookupListMap2.put("ReleationShipName", "User_Automation5");
	Case_lookupList.add(Case_lookupListMap2);
	HashMap<String, String> Case_lookupListMap4=new HashMap<String, String>();
		Case_lookupListMap4.put("Name", "p_c_customobj_contact");		
		Case_lookupListMap4.put("ReferenceTo", "C_ContactObject__c");
		Case_lookupListMap4.put("ReleationShipName", "ContactObject_Automation5");
	Case_lookupList.add(Case_lookupListMap4);		
	HashMap<String, String> Case_lookupListMap5=new HashMap<String, String>();
		Case_lookupListMap5.put("Name", "p_c_JBCXMobj_Score");		
		Case_lookupListMap5.put("ReferenceTo", "JBCXM__ScoringSchemeDefinition__c");
		Case_lookupListMap5.put("ReleationShipName", "p_c_JBCXMobj_Score_Automation5");
	Case_lookupList.add(Case_lookupListMap5);		

	Case_ObjectFields.setLookups(Case_lookupList);
	md.createFieldsOnObject(sfdc, "Case", Case_ObjectFields);		
	}

	@Test
	public void generateData() throws Exception {
		CopilotDataSetup dataSetup = new CopilotDataSetup();
		dataETL = new DataETL();
		dataSetup.loadToObject(dataETL, Job_C_ContactObject2);
		dataSetup.loadToObject(dataETL, Job_Account);
		dataSetup.loadToObject(dataETL, Job_CustomerInfo);
		dataSetup.loadToObject(dataETL, Job_Contact);
		dataSetup.loadToObject(dataETL, Job_Case);
		dataSetup.loadToObject(dataETL, Job_C_ContactObject1);
	}
}