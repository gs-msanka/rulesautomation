
package com.gainsight.bigdata.rulesengine.pages;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;

import org.bson.Document;
import org.testng.Assert;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.ObjectFields;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDACollection;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;


/**
 * @author Abhilash Thaduka
 */
public class RulesConfigureAndDataSetup extends NSTestBase {

    private DataETL dataLoad = new DataETL();
    private Calendar calendar = Calendar.getInstance();
    RulesUtil rulesUtil = new RulesUtil();
    private static final String SCHEDULE_COLLECTION = "schedule";

    /**
     * Creates custom object and fields for rules ui automation in sfdc
     * @throws Exception
     */
    public void createCustomObjectAndFieldsInSfdc() throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        metadataClient.createCustomObject("RulesSFDCCustom");
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
        metadataClient.createTextFields("RulesSFDCCustom__c", TextField, false, false, true, false, false);
        metadataClient.createTextFields("RulesSFDCCustom__c", TextField, false, false, true, false, false);
        metadataClient.createNumberField("RulesSFDCCustom__c", NumberField, false);
        metadataClient.createFields("RulesSFDCCustom__c", Checkbox, true, false, false);
        metadataClient.createCurrencyField("RulesSFDCCustom__c", Currency);
        metadataClient.createEmailField("RulesSFDCCustom__c", Email);
        metadataClient.createNumberField("RulesSFDCCustom__c", Percent, true);
        metadataClient.createFields("RulesSFDCCustom__c", Phone, false, true, false);
        metadataClient.createPickListField("RulesSFDCCustom__c", pickListFields, false);
        metadataClient.createPickListField("RulesSFDCCustom__c", MultipickListFields, true);
        metadataClient.createTextFields("RulesSFDCCustom__c", TextArea, false, false, false, true, false);
        metadataClient.createEncryptedTextFields("RulesSFDCCustom__c", EncryptedString);
        metadataClient.createFields("RulesSFDCCustom__c", URL, false, false, true);
        metadataClient.createLookupField("RulesSFDCCustom__c", LookupFieldName, Reference);
        metadataClient.createTextFields("RulesSFDCCustom__c", new String[]{"Data ExternalId"}, true, true, true, false, false);
        metadataClient.createFields("RulesSFDCCustom__c", new String[]{"IsActive"}, true, false, false);
        metadataClient.createDateField("RulesSFDCCustom__c", new String[]{"InputDate"}, false);
        metadataClient.createDateField("RulesSFDCCustom__c", new String[]{"InputDateTime"}, true);
        metadataClient.createNumberField("RulesSFDCCustom__c", new String[]{"AccPercentage"}, true);
        metadataClient.createNumberField("RulesSFDCCustom__c", new String[]{"ActiveUsers"}, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InRegions", new String[]{"India", "America", "England",
                "France", "Italy", "Germany", "Japan", "China", "Australia",
                "Russia", "Africa", "Arab "});
        metadataClient.createPickListField("Account", fields, true);
        //Field was getting creating on account object but permission are set on other object.
        metaUtil.addFieldPermissionsToUsers("Account", metaUtil.convertFieldNameToAPIName(fields.keySet().toArray(new String[fields.size()])), sfinfo, false);
        ArrayList<HashMap<String, String>> fFields = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> fField1 = new HashMap<String, String>();
        fField1.put("Type", "CheckBox");
        fField1.put("Formula", "IsActive__c");
        fField1.put("FieldName", "FIsActive");
        fField1.put("Description", "Is Active Field");
        fField1.put("HelpText", "Is Active Field");
        fFields.add(fField1);
        HashMap<String, String> fField2 = new HashMap<String, String>();
        fField2.put("Type", "Currency");
        fField2.put("Formula", "AnnualRevenue");
        fField2.put("FieldName", "FCurrency");
        fField2.put("Description", "AnnualRevenue");
        fField2.put("HelpText", "Formula AnnualRevenue");
        fFields.add(fField2);
        HashMap<String, String> fField3 = new HashMap<String, String>();
        fField3.put("Type", "Date");
        fField3.put("Formula", "InputDate__c");
        fField3.put("FieldName", "FDate");
        fField3.put("Description", "Formula InputDate__c");
        fField3.put("HelpText", "Formula InputDate__c");
        fFields.add(fField3);
        HashMap<String, String> fField4 = new HashMap<String, String>();
        fField4.put("Type", "DateTime");
        fField4.put("Formula", "InputDateTime__c");
        fField4.put("FieldName", "FDateTime");
        fField4.put("Description", "Formula InputDateTime__c");
        fField4.put("HelpText", "Formula InputDateTime__c");
        fFields.add(fField4);
        HashMap<String, String> fField5 = new HashMap<String, String>();
        fField5.put("Type", "Number");
        fField5.put("Formula", "ActiveUsers__c");
        fField5.put("FieldName", "FNumber");
        fField5.put("Description", "Formula ActiveUsers__c");
        fField5.put("HelpText", " Formula ActiveUsers__c");
        fFields.add(fField5);
        HashMap<String, String> fField6 = new HashMap<String, String>();
        fField6.put("Type", "Percent");
        fField6.put("Formula", "AccPercentage__c");
        fField6.put("FieldName", "FPercent");
        fField6.put("Description", "Field AccPercentage__c");
        fField6.put("HelpText", "Field AccPercentage__c");
        fFields.add(fField6);
        HashMap<String, String> fField7 = new HashMap<String, String>();
        fField7.put("Type", "Text");
        fField7.put("Formula", "Name");
        fField7.put("FieldName", "FText");
        fField7.put("Description", "Formula Name");
        fField7.put("HelpText", "Formula Name");
        fFields.add(fField7);
        metadataClient.createFormulaFields("Account", fFields);
		String[] permFields = new String[] { "Data ExternalId", "IsActive",
				"InputDate", "InputDateTime", "AccPercentage", "ActiveUsers",
				"C_Text", "C_Number", "C_Checkbox", "C_Currency", "C_Email",
				"C_Percent", "C_Phone", "C_Picklist", "C_MultiPicklist",
				"C_TextArea", "C_EncryptedString", "C_URL", "C_Reference" };
		String[] permFieldsForAccountObject = new String[] { "FIsActive",
				"FCurrency", "FDate", "FDateTime", "InputDateTime",
				"AccPercentage", "ActiveUsers", "FNumber", "FPercent", "FText" };
        metaUtil.addFieldPermissionsToUsers("RulesSFDCCustom__c", metaUtil.convertFieldNameToAPIName(permFields), sfdc.fetchSFDCinfo(), true);
        metaUtil.addFieldPermissionsToUsers("Account", metaUtil.convertFieldNameToAPIName(permFieldsForAccountObject), sfdc.fetchSFDCinfo(), true);
        //	metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace("RulesSFDCCustom__c"), permFields,sfdc.fetchSFDCinfo(), true);
        String configData = "{\"type\":\"SFDC\",\"objectName\":\"RulesSFDCCustom__c\",\"objectLabel\":\"RulesSFDCCustom Object\",\"fields\":[{\"name\":\"C_Checkbox__c\",\"dataType\":\"boolean\"},{\"name\":\"InputDate__c\",\"dataType\":\"date\"},{\"name\":\"InputDateTime__c\",\"dataType\":\"dateTime\"},{\"name\":\"C_Number__c\",\"dataType\":\"double\"},{\"name\":\"C_Email__c\",\"dataType\":\"string\"},{\"name\":\"C_Text__c\",\"dataType\":\"string\"},{\"name\":\"C_TextArea__c\",\"dataType\":\"string\"},{\"name\":\"Data_ExternalId__c\",\"dataType\":\"string\"},{\"name\":\"C_Reference__c\",\"dataType\":\"string\"}]}";
        
        try {
            Log.info("Saving CustomWeRules object and fields info in MDA to load data using Load To SFDC action. Config Data: "
                    + configData);
            rulesUtil.saveCustomObjectInRulesConfig(configData);
        } catch (Exception e) {
            Log.error(
                    "Exception occurred while saving CustomWeRules object configuration in MDA ",
                    e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates MDA subject area with data for rule sui automation
     * @throws Exception
     */
    public void createMdaSubjectAreaWithData() throws Exception {
        DataLoadManager dataLoadManager = new DataLoadManager(sfinfo, getDataLoadAccessKey());
        JobInfo load = mapper.readValue(new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob.txt"), JobInfo.class);
        dataLoad.execute(load);
        String collectionName = "MONGO";
        Log.info("Collection Name : " + collectionName);
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionWithMesasuresSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        JobInfo loadTransform = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob1.txt"), JobInfo.class);
        File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform, calendar.getTime());
        DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(actualCollectionInfo);
        metadata.setCollectionName(actualCollectionInfo.getCollectionDetails().getCollectionName());
        String statusId = dataLoadManager.dataLoadManage(metadata, dataLoadFile);
        Assert.assertNotNull(statusId);
        dataLoadManager.waitForDataLoadJobComplete(statusId);
        LoadToMDACollection loadToMDACollection =new LoadToMDACollection();
        loadToMDACollection.setType("MDA"); 
        loadToMDACollection.setObjectName(actualCollectionInfo.getCollectionDetails().getCollectionId());
        loadToMDACollection.setObjectLabel(actualCollectionInfo.getCollectionDetails().getCollectionName());
        List<LoadToMDACollection.Field> fields = new ArrayList<>();
        LoadToMDACollection.Field field = null;
        for (CollectionInfo.Column column : actualCollectionInfo.getColumns()) {
            field = new LoadToMDACollection.Field();
            field.setDataType(column.getDatatype().toUpperCase());
            field.setName(column.getDbName());
            fields.add(field);
        }
        loadToMDACollection.setFields(fields);
        String payload = mapper.writeValueAsString(loadToMDACollection);
        try {
            rulesUtil.saveCustomObjectInRulesConfig(payload);
        } catch (Exception e) {
            Log.error("Exception occurred while saving  configuration in MDA ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an empty subject area
     * @throws Exception
     */
    public void createEmptySubjectArea() throws Exception {
        DataLoadManager dataLoadManager = new DataLoadManager(sfinfo, getDataLoadAccessKey());
        String collectionName = "EmptySubjectArea";
        Log.info("Collection Name : " + collectionName);
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(collectionName);
        String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
        Assert.assertNotNull(collectionId);
        CollectionInfo actualCollectionInfo = dataLoadManager.getCollectionInfo(collectionId);
        LoadToMDACollection loadToMDACollection =new LoadToMDACollection();
        loadToMDACollection.setType("MDA"); 
        loadToMDACollection.setObjectName(actualCollectionInfo.getCollectionDetails().getCollectionId());
        loadToMDACollection.setObjectLabel(actualCollectionInfo.getCollectionDetails().getCollectionName());
        List<LoadToMDACollection.Field> fields = new ArrayList<>();
        LoadToMDACollection.Field field = null;
        for (CollectionInfo.Column column : actualCollectionInfo.getColumns()) {
            field = new LoadToMDACollection.Field();
            field.setDataType(column.getDatatype().toUpperCase());
            field.setName(column.getDbName());
            fields.add(field);
        }
        loadToMDACollection.setFields(fields);
        String payload = mapper.writeValueAsString(loadToMDACollection);
        try {
            rulesUtil.saveCustomObjectInRulesConfig(payload);
        } catch (Exception e) {
            Log.error("Exception occurred while saving  configuration in MDA ", e);
            throw new RuntimeException(e);
        }
    }

   // TODO - WIP
    /**
     * Creates multiple(six) subject areas for a tenant in Mongo, RedShift databases based upon the iteration given.
     * @param tenantDetails
     * @param mongoDBDAO
     */
	public void createMultipleSubjectAreasForDataLoadConfiguration(TenantDetails tenantDetails, MongoDBDAO mongoDBDAO) throws Exception{
		DataLoadManager dataLoadManager = new DataLoadManager(sfinfo, getDataLoadAccessKey());
		JobInfo load = mapper.readValue(new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob.txt"), JobInfo.class);
		dataLoad.execute(load);
		try {
		for (int i = 0; i < 6; i++) {
				String collectionName = "SubjectArea" + " " + i;
				Log.info("Collection Name : " + collectionName);
			    CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/CollectionSchemaForDataLoadConfiguration.json")), CollectionInfo.class);
				collectionInfo.getCollectionDetails().setCollectionName(collectionName);
				String collectionId = dataLoadManager.createSubjectAreaAndGetId(collectionInfo);
				Assert.assertNotNull(collectionId);
				boolean iSRedShiftEnabled=false;
				if ((i > 2) && (!iSRedShiftEnabled)) {
					iSRedShiftEnabled = tenantManager.enabledRedShiftWithDBDetails(tenantDetails);
					Log.info("Is Redhisft Enabled ??? " + iSRedShiftEnabled);
					mongoDBDAO.updateCollectionDBStoreTypeByCollectionName(tenantDetails.getTenantId(), collectionName,DBStoreType.REDSHIFT);
				}
			    JobInfo loadTransform = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-Jobs/dataLoadJob1.txt"), JobInfo.class);
				File dataLoadFile = FileProcessor.getDateProcessedFile(loadTransform, calendar.getTime());
				DataLoadMetadata metadata = dataLoadManager.getDefaultDataLoadMetaData(collectionInfo);
				metadata.setCollectionName(collectionInfo.getCollectionDetails().getCollectionName());
				String statusId = dataLoadManager.dataLoadManage(metadata,dataLoadFile);
				Assert.assertNotNull(statusId);
				dataLoadManager.waitForDataLoadJobComplete(statusId);
			}
		} finally {
			tenantManager.disableRedShift(tenantDetails);
			mongoDBDAO.mongoUtil.closeConnection();
		}
	}
	
	public void updateTimeZoneInAppSettings(String timeZone) {
		if (sfdc.getRecordCount(resolveStrNameSpace("select id from JBCXM__ApplicationSettings__c")) > 0) {
		System.out.println("setting JBCXM__OrgTimeZone__c (TimeZone) in app settings");
		sfdc.runApexCode(resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__OrgTimeZone__c from JBCXM__ApplicationSettings__c];"
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
		MongoUtil mongoUtil = new MongoUtil(nsConfig.getSchedulerDBHost(), Integer.valueOf(nsConfig.getSchedulerDBPort()),
				nsConfig.getSchedulerDBUserName(), nsConfig.getSchedulerDBPassword(), nsConfig.getSchedulerDBDatabase());
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
		metadataClient.createCustomObject("C_Custom");

		List<String> externalID_Text = new ArrayList<String>();
		externalID_Text.add("rules_c_ExternalID");
		objField.setExternalID_Text(externalID_Text);

		List<String> textFields = new ArrayList<String>();
		textFields.add("rules_c_Text");
		objField.setTextFields(textFields);

		List<String> numberFields = new ArrayList<String>();
		numberFields.add("rules_c_Number");
		objField.setNumberFields(numberFields);

		List<String> checkBoxes = new ArrayList<String>();
		checkBoxes.add("rules_c_Checkbox");
		objField.setCheckBoxes(checkBoxes);

		List<String> Email = new ArrayList<String>();
		Email.add("rules_c_Email");
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
		objField.setDates(Date);

		List<String> DateTime = new ArrayList<String>();
		DateTime.add("rules_c_DateTime");
		objField.setDateTimes(DateTime);

		List<String> textArea = new ArrayList<String>();
		textArea.add("rules_c_TextArea");
		objField.setTextAreas(textArea);

		List<HashMap<String, String>> lookup = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> Customer_lookupListMap = new HashMap<String, String>();
		Customer_lookupListMap.put("Name", "C_lookup");
		Customer_lookupListMap.put("ReferenceTo", "Account");
		Customer_lookupListMap.put("ReleationShipName", "Custom_rulesUI_Automation");
		lookup.add(Customer_lookupListMap);
		objField.setLookups(lookup);
		metaUtil.createFieldsOnObject(sfdc, "C_Custom__c", objField);
	}
}
