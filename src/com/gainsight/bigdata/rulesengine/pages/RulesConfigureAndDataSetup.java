
package com.gainsight.bigdata.rulesengine.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.gainsight.utils.MongoUtil;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.ObjectFields;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.testdriver.Log;


/**
 * @author Abhilash Thaduka
 */
public class RulesConfigureAndDataSetup{

    RulesUtil rulesUtil = new RulesUtil();
    private static final String SCHEDULE_COLLECTION = "schedule";
    ObjectMapper mapper =new ObjectMapper();

    /**
     * Creates custom object and fields for rules ui automation in sfdc
     * @throws Exception
     */
    public void createCustomObjectAndFieldsInSfdc() throws Exception {
    	NSTestBase.metadataClient = SalesforceMetadataClient.createDefault(NSTestBase.sfdc.getMetadataConnection());
    	NSTestBase.metadataClient.createCustomObject("RulesSFDCCustom");
        String TextField[] = {"C_Text"}, NumberField[] = {"C_Number"}, Checkbox[] = {"C_Checkbox"}, Currency[] = {"C_Currency"}, Email[] = {"C_Email"}, Percent[] = {"C_Percent"}, Phone[] = {"C_Phone"}, Picklist_FieldName = "C_Picklist", Picklist_Values[] = {
                "Pvalue1", "Pvalue2", "Pvalue3"}, MultiPicklist_FieldName = "C_MultiPicklist", MultiPicklist_Values[] = {
                "MPvalue1", "MPvalue2", "MPvalue3"}, TextArea[] = {"C_TextArea"}, EncryptedString[] = {"C_EncryptedString"}, URL[] = {"C_URL"};
        HashMap<String, String[]> pickListFields = new HashMap<String, String[]>();
        pickListFields.put(Picklist_FieldName, Picklist_Values);
        HashMap<String, String[]> MultipickListFields = new HashMap<String, String[]>();
        MultipickListFields.put(MultiPicklist_FieldName, MultiPicklist_Values);
        String ReferenceTo = "User"; // Reference to User Object
        String ReleationShipName = "AccountS_AutomationS_Rules"; // Relation Name																// Name
        String C_Reference = "C_Reference";
        String LookupFieldName[] = {C_Reference}, Reference[] = {
                ReferenceTo, ReleationShipName};
        NSTestBase.metadataClient.createTextFields("RulesSFDCCustom__c", TextField, false, false, true, false, false);
        NSTestBase.metadataClient.createNumberField("RulesSFDCCustom__c", NumberField, false);
        NSTestBase.metadataClient.createFields("RulesSFDCCustom__c", Checkbox, true, false, false);
        NSTestBase.metadataClient.createCurrencyField("RulesSFDCCustom__c", Currency);
        NSTestBase.metadataClient.createEmailField("RulesSFDCCustom__c", Email);
        NSTestBase.metadataClient.createNumberField("RulesSFDCCustom__c", Percent, true);
        NSTestBase.metadataClient.createFields("RulesSFDCCustom__c", Phone, false, true, false);
        NSTestBase.metadataClient.createPickListField("RulesSFDCCustom__c", pickListFields, false);
        NSTestBase.metadataClient.createPickListField("RulesSFDCCustom__c", MultipickListFields, true);
        NSTestBase.metadataClient.createTextFields("RulesSFDCCustom__c", TextArea, false, false, false, true, false);
        NSTestBase.metadataClient.createEncryptedTextFields("RulesSFDCCustom__c", EncryptedString);
        NSTestBase.metadataClient.createFields("RulesSFDCCustom__c", URL, false, false, true);
        NSTestBase.metadataClient.createLookupField("RulesSFDCCustom__c", LookupFieldName, Reference);
        NSTestBase.metadataClient.createTextFields("RulesSFDCCustom__c", new String[]{"Data ExternalId"}, true, true, true, false, false);
        NSTestBase.metadataClient.createFields("RulesSFDCCustom__c", new String[]{"IsActive"}, true, false, false);
        NSTestBase.metadataClient.createDateField("RulesSFDCCustom__c", new String[]{"InputDate"}, false);
        NSTestBase.metadataClient.createDateField("RulesSFDCCustom__c", new String[]{"InputDateTime"}, true);
        NSTestBase.metadataClient.createNumberField("RulesSFDCCustom__c", new String[]{"AccPercentage"}, true);
        NSTestBase.metadataClient.createNumberField("RulesSFDCCustom__c", new String[]{"ActiveUsers"}, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InRegions", new String[]{"India", "America", "England",
                "France", "Italy", "Germany", "Japan", "China", "Australia",
                "Russia", "Africa", "Arab "});
        NSTestBase.metadataClient.createPickListField("Account", fields, true);
        //Field was getting creating on account object but permission are set on other object.
        NSTestBase.metaUtil.addFieldPermissionsToUsers("Account", NSTestBase.metaUtil.convertFieldNameToAPIName(fields.keySet().toArray(new String[fields.size()])), NSTestBase.sfdc.fetchSFDCinfo(), false);
		String[] permFields = new String[] { "Data ExternalId", "IsActive",
				"InputDate", "InputDateTime", "AccPercentage", "ActiveUsers",
				"C_Text", "C_Number", "C_Checkbox", "C_Currency", "C_Email",
				"C_Percent", "C_Phone", "C_Picklist", "C_MultiPicklist",
				"C_TextArea", "C_EncryptedString", "C_URL", "C_Reference" };
        NSTestBase.metaUtil.addFieldPermissionsToUsers("RulesSFDCCustom__c", NSTestBase.metaUtil.convertFieldNameToAPIName(permFields), NSTestBase.sfdc.fetchSFDCinfo(), true);
        
        ObjectFields objField = new ObjectFields();
		List<HashMap<String, String>> lookup = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> Customer_lookupListMap = new HashMap<String, String>();
		Customer_lookupListMap.put("Name", "custom_lookup");
		Customer_lookupListMap.put("ReferenceTo", "Account");
		Customer_lookupListMap.put("ReleationShipName", "Custom_rulesengineUI_Automation");
		lookup.add(Customer_lookupListMap);
		objField.setLookups(lookup);
		NSTestBase.metaUtil.createFieldsOnObject(NSTestBase.sfdc, "RulesSFDCCustom__c", objField);
    }
    
    /** creates dataLoad Configuration for Object "RulesSFDCCustom__c"
     * 
     */
    public void createDataLoadConfiguration(){	
		String configData = "{\"type\":\"SFDC\",\"fields\":[{\"name\":\"custom_lookup__c\",\"dataType\":\"STRING\"},{\"name\":\"AccPercentage__c\",\"dataType\":\"DOUBLE\"},{\"name\":\"ActiveUsers__c\",\"dataType\":\"DOUBLE\"},{\"name\":\"C_Checkbox__c\",\"dataType\":\"BOOLEAN\"},{\"name\":\"C_Currency__c\",\"dataType\":\"DOUBLE\"},{\"name\":\"C_Email__c\",\"dataType\":\"STRING\"},{\"name\":\"C_EncryptedString__c\",\"dataType\":\"STRING\"},{\"name\":\"C_MultiPicklist__c\",\"dataType\":\"STRING\"},{\"name\":\"C_Number__c\",\"dataType\":\"DOUBLE\"},{\"name\":\"C_Percent__c\",\"dataType\":\"DOUBLE\"},{\"name\":\"C_Phone__c\",\"dataType\":\"STRING\"},{\"name\":\"C_Picklist__c\",\"dataType\":\"STRING\"},{\"name\":\"C_Reference__c\",\"dataType\":\"STRING\"},{\"name\":\"C_Reference__c\",\"dataType\":\"STRING\"},{\"name\":\"C_Text__c\",\"dataType\":\"STRING\"},{\"name\":\"C_TextArea__c\",\"dataType\":\"STRING\"},{\"name\":\"C_URL__c\",\"dataType\":\"STRING\"},{\"name\":\"Data_ExternalId__c\",\"dataType\":\"STRING\"},{\"name\":\"Id\",\"dataType\":\"STRING\"},{\"name\":\"InputDate__c\",\"dataType\":\"DATE\"},{\"name\":\"InputDateTime__c\",\"dataType\":\"DATETIME\"},{\"name\":\"IsActive__c\",\"dataType\":\"BOOLEAN\"},{\"name\":\"Name\",\"dataType\":\"STRING\"},{\"name\":\"OwnerId\",\"dataType\":\"STRING\"},{\"name\":\"OwnerId\",\"dataType\":\"STRING\"}],\"objectName\":\"RulesSFDCCustom__c\",\"objectLabel\":\"RulesSFDCCustom Object\"}";
		try {
			Log.info("Saving CustomWeRules object and fields info in MDA to load data using Load To SFDC action. Config Data: "
					+ configData);
			rulesUtil.deleteObjectInRulesConfig("RulesSFDCCustom__c", "SFDC");
			rulesUtil.saveCustomObjectInRulesConfig(configData);
		} catch (Exception e) {
			Log.error(
					"Exception occurred while saving CustomWeRules object configuration in MDA ", e);
			throw new RuntimeException(e);
		}
	}
	
	public void updateTimeZoneInAppSettings(String timeZone) {
		if (NSTestBase.sfdc.getRecordCount(NSTestBase.resolveStrNameSpace("select id from JBCXM__ApplicationSettings__c")) > 0) {
		System.out.println("setting JBCXM__OrgTimeZone__c (TimeZone) in app settings");
		NSTestBase.sfdc.runApexCode(NSTestBase.resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__OrgTimeZone__c from JBCXM__ApplicationSettings__c];"
                + "appSet.JBCXM__OrgTimeZone__c='"+timeZone+"';" + "update appSet;"));
		Log.info("America/Los_Angeles Timezone Updated Successfully");
		}else {
			throw new RuntimeException("Configure Gainsight Application to update TimeZone");
		}
	}
	
	
	/**
	 * Method to get cronExpression from Scheduler Db
	 * @param tenantID
     * @param jobIdentifier property from scheduler Db
	 * @return cronExpression
	 * @throws IOException 
	 */
	
	public String getCronExpressionFromDb(String tenantID, String jobIdentifier)throws Exception {
		MongoUtil mongoUtil = new MongoUtil(NSTestBase.nsConfig.getSchedulerDBHost(), Integer.valueOf(NSTestBase.nsConfig.getSchedulerDBPort()),
				NSTestBase.nsConfig.getSchedulerDBUserName(), NSTestBase.nsConfig.getSchedulerDBPassword(), NSTestBase.nsConfig.getSchedulerDBDatabase());
		String cronExpression = null;
		try {
			Document whereQuery = new Document();
			whereQuery.put("tenantId", tenantID);
			whereQuery.put("jobIdentifier", jobIdentifier);
			Document collection = mongoUtil.getFirstRecord(SCHEDULE_COLLECTION, whereQuery);
			cronExpression = (String) collection.get("cronExpression");
			Log.info(cronExpression);
		} finally {
			mongoUtil.closeConnection();
		}
		return cronExpression;
	}
	

	/** Method which creates custom object "C_Custom" and fields using metadata api
	 * @throws Exception
	 */
	public void createCustomObjectAndFields() throws Exception {
		ObjectFields objField = new ObjectFields();
		// creating CustomObject
		NSTestBase.metadataClient.createCustomObject("C_Custom");

		List<String> externalID_Text = new ArrayList<String>();
		externalID_Text.add("rules_c_ExternalID");
		objField.setExternalID_Text(externalID_Text);

		List<String> textFields = new ArrayList<String>();
		textFields.add("rules_c_Text");
		textFields.add("Custom_Text2");
		textFields.add("Custom_Aggregation_Name");
		textFields.add("Custom_Instance_Name");
		textFields.add("Custom_Instance_Id");
		objField.setTextFields(textFields);

		List<String> numberFields = new ArrayList<String>();
		numberFields.add("rules_c_Number");
		numberFields.add("Custom_Number2");
		numberFields.add("Custom_Number3");
		numberFields.add("Custom_Number4");
		objField.setNumberFields(numberFields);

		List<String> checkBoxes = new ArrayList<String>();
		checkBoxes.add("rules_c_Checkbox");
		checkBoxes.add("Custom_Checkbox2");
		objField.setCheckBoxes(checkBoxes);

        List<String> Email = new ArrayList<String>();
        Email.add("rules_c_Email");
        Email.add("customEmail2");
        Email.add("customEmail3");
        Email.add("customEmail4");
        Email.add("customEmail5");
        Email.add("customEmail6");
        objField.setEmails(Email);

		List<String> Percent = new ArrayList<String>();
		Percent.add("rules_c_Percent");
		objField.setPercents(Percent);

		List<HashMap<String, String[]>> pickLists = new ArrayList<HashMap<String, String[]>>();
		HashMap<String, String[]> hmPickList1 = new HashMap<String, String[]>();
		hmPickList1.put("rules_c_Picklist", new String[] { "Pvalue1", "Pvalue2", "Pvalue3" });
		pickLists.add(hmPickList1);
		objField.setPickLists(pickLists);

		List<HashMap<String, String[]>> multiPickLists = new ArrayList<HashMap<String, String[]>>();
		HashMap<String, String[]> hmMultiPickList1 = new HashMap<String, String[]>();
		hmMultiPickList1.put("rules_c_MultiPicklist", new String[] {
				"MPvalue1", "MPvalue2", "MPvalue3" });
		multiPickLists.add(hmMultiPickList1);
		objField.setMultiPickLists(multiPickLists);

        List<String> Date = new ArrayList<String>();
        Date.add("rules_c_Date");
        Date.add("Custom_Date2");
        Date.add("Custom_Date3");
        Date.add("Custom_Date4");
        Date.add("Custom_Date5");
        Date.add("Custom_Date6");
        objField.setDates(Date);

		List<String> DateTime = new ArrayList<String>();
        DateTime.add("rules_c_DateTime");
        DateTime.add("Custom_DateTime2");
        DateTime.add("Custom_DateTime3");
        DateTime.add("Custom_DateTime4");
        DateTime.add("Custom_DateTime5");
        DateTime.add("Custom_DateTime6");
        objField.setDateTimes(DateTime);

        List<String> Currency = new ArrayList<String>();
        Currency.add("Custom_Currency");
        Currency.add("Custom_CurrencyField2");
        Currency.add("Custom_CurrencyField3");
        Currency.add("Custom_CurrencyField4");
        Currency.add("Custom_CurrencyField5");
        Currency.add("Custom_CurrencyField6");
        objField.setCurrencies(Currency);

		List<String> textArea = new ArrayList<String>();
		textArea.add("rules_c_TextArea");
		objField.setTextAreas(textArea);

        List<String> phone = new ArrayList<String>();
        phone.add("rules_phone");
        phone.add("customPhone2");
        phone.add("customPhone3");
        phone.add("customPhone4");
        phone.add("customPhone5");
        phone.add("customPhone6");
        objField.setPhones(phone);

        List<String> URL = new ArrayList<String>();
        URL.add("rules_URL");
        URL.add("customUrl2");
        URL.add("customUrl3");
        URL.add("customUrl4");
        URL.add("customUrl5");
        URL.add("customUrl6");
        objField.setURLs(URL);

		List<HashMap<String, String>> lookup = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> Customer_lookupListMap = new HashMap<String, String>();
		Customer_lookupListMap.put("Name", "C_lookup");
		Customer_lookupListMap.put("ReferenceTo", "Account");
		Customer_lookupListMap.put("ReleationShipName", "Custom_rulesUI_Automation");
		lookup.add(Customer_lookupListMap);

        HashMap<String, String> relationShipLookup = new HashMap<String, String>();
        relationShipLookup.put("Name", "Relationship_lookup");
        relationShipLookup.put("ReferenceTo", NSTestBase.resolveStrNameSpace("JBCXM__GSRelationship__c"));
        relationShipLookup.put("ReleationShipName", "Relationshiplookup_rulesUI_Automation");
        lookup.add(relationShipLookup);
        objField.setLookups(lookup);
        NSTestBase.metaUtil.createFieldsOnObject(NSTestBase.sfdc, "C_Custom__c", objField);

    }

    /**
     * Method to create lookup on relationship object from  RulesSFDCCustom__c object
     *
     * @throws Exception
     */
    public void createlookupOnRelationshipObject() throws Exception {
        // Creating lookup on relationship object from RulesSFDCCustom__c object (related object)
        List<HashMap<String, String>> relatedLookup = new ArrayList<HashMap<String, String>>();
        ObjectFields objectFields = new ObjectFields();
        HashMap<String, String> related_Lookup = new HashMap<String, String>();
        related_Lookup.put("Name", "Relationship_lookup");
        related_Lookup.put("ReferenceTo", NSTestBase.resolveStrNameSpace("RulesSFDCCustom__c"));
        related_Lookup.put("ReleationShipName", "Relationshiplookup_rulesUI_Automation");
        relatedLookup.add(related_Lookup);
        objectFields.setLookups(relatedLookup);
        NSTestBase.metaUtil.createFieldsOnObject(NSTestBase.sfdc, NSTestBase.resolveStrNameSpace("JBCXM__GSRelationship__c"), objectFields);
    }
}

