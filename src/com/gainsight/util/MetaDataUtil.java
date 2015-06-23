package com.gainsight.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;

import sun.util.logging.resources.logging;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.bigdata.pojo.ObjectFields;

public class MetaDataUtil {
	public static SalesforceMetadataClient metadataClient ;
	 public static final Application env = new Application();
	public SfdcConfig sfdcConfig = ConfigLoader.getSfdcConfig();

	   
	   public void createFieldsOnAccount(SalesforceConnector sfdc) throws Exception {
		 metadataClient= SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
	    	String TextField[]={"C_Text"} , NumberField[]={"C_Number"} , Checkbox[]={"C_Checkbox"} , Currency[]={"C_Currency"} , Email[]={"C_Email"} , Percent[]={"C_Percent"} ,  Phone[]={"C_Phone"} , Picklist_FieldName="C_Picklist" , 
					Picklist_Values[]={"Pvalue1","Pvalue2","Pvalue3"} , MultiPicklist_FieldName="C_MultiPicklist", MultiPicklist_Values[]={"MPvalue1","MPvalue2","MPvalue3"} , TextArea[]={"C_TextArea"} , EncryptedString[]={"C_EncryptedString"} , URL[]={"C_URL"};
	    	HashMap<String, String[]> pickListFields=new HashMap<String, String[]>();
			pickListFields.put(Picklist_FieldName, Picklist_Values);
			
			HashMap<String, String[]> MultipickListFields=new HashMap<String, String[]>();
			MultipickListFields.put(MultiPicklist_FieldName, MultiPicklist_Values);
			String ReferenceTo="User";  //Reference to User Object
			String ReleationShipName="Acco2untS_AutomationS"; //Relation Name
		
			String C_Reference="C_Reference";
			String LookupFieldName[]={C_Reference} , Reference[]={ReferenceTo,ReleationShipName};
			
	    	metadataClient.createTextFields("Account", TextField, false, false, true, false, false);
			metadataClient.createTextFields("Account", TextField, false, false, true, false, false);
			metadataClient.createNumberField("Account", NumberField, false);
			metadataClient.createFields("Account", Checkbox, true, false, false);
			metadataClient.createCurrencyField("Account", Currency);  
			metadataClient.createEmailField("Account", Email);
			metadataClient.createNumberField("Account", Percent, true);
			metadataClient.createFields("Account", Phone, false, true, false);
			metadataClient.createPickListField("Account", pickListFields, false);
			metadataClient.createPickListField("Account", MultipickListFields, true);
			metadataClient.createTextFields("Account", TextArea, false, false, false, true, false); 
			metadataClient.createEncryptedTextFields("Account", EncryptedString); 
			metadataClient.createFields("Account", URL, false, false, true);
			metadataClient.createLookupField("Account", LookupFieldName, Reference);		
	        metadataClient.createTextFields("Account", new String[]{"Data ExternalId"}, true, true, true, false, false);
	        metadataClient.createFields("Account", new String[]{"IsActive"}, true, false, false);
	        metadataClient.createDateField("Account", new String[]{"InputDate"}, false);
	        metadataClient.createDateField("Account", new String[]{"InputDateTime"}, true);
	        metadataClient.createNumberField("Account", new String[]{"AccPercentage"}, true);
	        metadataClient.createNumberField("Account", new String[]{"ActiveUsers"}, false);
	        HashMap<String, String[]> fields = new HashMap<String, String[]>();
	        fields.put("InRegions", new String[]{"India", "America", "England", "France", "Italy", "Germany", "Japan" , "China", "Australia", "Russia", "Africa", "Arab "});
	        metadataClient.createPickListField("Account", fields, true);
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
	        metadataClient.createFormulaFields("Account", fFields);
	        fFields.clear();
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

	        String[] permFields = new String[]{"Data ExternalId", "IsActive", "InputDate", "InputDateTime",
	                                    "AccPercentage", "ActiveUsers", "InRegions", "FIsActive", "FCurrency", "FDate", "FDateTime", "FNumber", "FPercent", "FText","C_Text",
	                                    "C_Number","C_Checkbox","C_Currency","C_Email","C_Percent","C_Phone","C_Picklist","C_MultiPicklist","C_TextArea","C_EncryptedString",
	                                    "C_URL","C_Reference"};
	        addFieldPermissionsToUsers("Account", convertFieldNameToAPIName(permFields), sfdc.fetchSFDCinfo());
	    }
	 
	 public void createFieldsOnObject(SalesforceConnector sfdc, String Object, ObjectFields objF) throws Exception {
		 metadataClient= SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
		 List<String> permFieldsList = new ArrayList<String>();
	    
		 if(objF.getLookups().size() > 0){
			 for (HashMap<String,String> hm : objF.getLookups()){
				 metadataClient.createLookupField(resolveStrNameSpace(Object), new String[]{hm.get("Name")}, new String[]{hm.get("ReferenceTo"),hm.get("ReleationShipName")});
				 permFieldsList.add(hm.get("Name"));
			 }
		 }
		 if(objF.getFormulaFieldsList().size() > 0){
			 metadataClient.createFormulaFields(resolveStrNameSpace(Object), objF.getFormulaFieldsList());
			 
			 for (HashMap<String,String> hm : objF.getFormulaFieldsList()){
				 permFieldsList.add(hm.get("FieldName"));
			 }
		 }
		 if(objF.getTextFields().size() > 0){
			 metadataClient.createTextFields(resolveStrNameSpace(Object), objF.getTextFields().toArray(new String[objF.getTextFields().size()]), false, false, true, false, false);
			 permFieldsList.addAll(objF.getTextFields());
		 }
		 if(objF.getNumberFields().size() > 0){
			 metadataClient.createNumberField(resolveStrNameSpace(Object), objF.getNumberFields().toArray(new String[objF.getNumberFields().size()]), false);
			 permFieldsList.addAll(objF.getNumberFields());
		 }
		 if(objF.getCheckBoxes().size() > 0){
			 metadataClient.createFields(resolveStrNameSpace(Object), objF.getCheckBoxes().toArray(new String[objF.getCheckBoxes().size()]), true, false, false);
			 permFieldsList.addAll(objF.getCheckBoxes());
		 }
		 if(objF.getCurrencies().size() > 0){
			 metadataClient.createCurrencyField(resolveStrNameSpace(Object), objF.getCurrencies().toArray(new String[objF.getCurrencies().size()]));
			 permFieldsList.addAll(objF.getCurrencies());
		 }
		 if(objF.getEmails().size() > 0){
			 metadataClient.createEmailField(resolveStrNameSpace(Object), objF.getEmails().toArray(new String[objF.getEmails().size()]));
			 permFieldsList.addAll(objF.getEmails());
		 }
		 if(objF.getPercents().size() > 0){
			 metadataClient.createNumberField(resolveStrNameSpace(Object), objF.getPercents().toArray(new String[objF.getPercents().size()]), true);
			 permFieldsList.addAll(objF.getPercents());
		 }
		 if(objF.getPhones().size() > 0){
			 metadataClient.createFields(resolveStrNameSpace(Object), objF.getPhones().toArray(new String[objF.getPhones().size()]), false, true, false);
			 permFieldsList.addAll(objF.getPhones());
		 }
		 if(objF.getPickLists().size() > 0){
			 for(HashMap<String,String[]> hmPickLists : objF.getPickLists()){
			 metadataClient.createPickListField(resolveStrNameSpace(Object), hmPickLists, false);
			 //permFieldsList.add(hmPickLists)
			 Log.info("PickList Field Name is " + hmPickLists.keySet().toString());
			 permFieldsList.add(hmPickLists.keySet().toString());			 
			 }
		 }
		 if(objF.getMultiPickLists().size() > 0){
			 for(HashMap<String,String[]> hmMultiPickLists : objF.getMultiPickLists()){
			 metadataClient.createPickListField(resolveStrNameSpace(Object),hmMultiPickLists,true);
			 Log.info("MultiPickList Field Name is " + hmMultiPickLists.keySet().toString());
			 permFieldsList.add(hmMultiPickLists.keySet().toString());
			 }
		 }
		 
		 if(objF.getAutoNumber().size() > 0){
			 metadataClient.createAutoNumberFields(resolveStrNameSpace(Object), objF.getAutoNumber().toArray(new String[objF.getAutoNumber().size()]));
			 permFieldsList.addAll(objF.getAutoNumber());
		 }
		 if(objF.getDates().size() > 0){
			 metadataClient.createDateField(resolveStrNameSpace(Object), objF.getDates().toArray(new String[objF.getDates().size()]), false);
			 permFieldsList.addAll(objF.getDates());
		 }
		 if(objF.getDateTimes().size() > 0){
			 metadataClient.createDateField(resolveStrNameSpace(Object), objF.getDateTimes().toArray(new String[objF.getDateTimes().size()]), true);
			 permFieldsList.addAll(objF.getDateTimes());
		 }
		 if(objF.getGeoLocation().size() > 0){
			 //SalesforceMetadataClient Doesn't have Geo Location to create a Field
		 }
		 if(objF.getTextArea_Long().size() > 0){
			//SalesforceMetadataClient Doesn't have Text Area Long to create a Field. Text Fields,Text Area, Text Rich are available.
		 }
		 if(objF.getTextArea_Rich().size() > 0){
			 metadataClient.createTextFields(resolveStrNameSpace(Object), objF.getTextArea_Rich().toArray(new String[objF.getTextArea_Rich().size()]), false, false, false, false, true);
			 permFieldsList.addAll(objF.getTextArea_Rich());
		 }
		 if(objF.getTextAreas().size() > 0){
			 metadataClient.createTextFields(resolveStrNameSpace(Object), objF.getTextAreas().toArray(new String[objF.getTextAreas().size()]), false, false, false, true, false);
			 permFieldsList.addAll(objF.getTextFields());
		 }
		 if(objF.getEncryptedStrings().size() > 0){
			 metadataClient.createEncryptedTextFields(resolveStrNameSpace(Object), objF.getEncryptedStrings().toArray(new String[objF.getEncryptedStrings().size()]));
			 permFieldsList.addAll(objF.getEncryptedStrings());
		 }

		 if(objF.getURLs().size() > 0){
			 metadataClient.createFields(resolveStrNameSpace(Object), objF.getURLs().toArray(new String[objF.getURLs().size()]), false, false, true);
			 permFieldsList.addAll(objF.getURLs());
		 }
		 addFieldPermissionsToUsers(resolveStrNameSpace(Object), convertFieldNameToAPIName(permFieldsList.toArray(new String[permFieldsList.size()])), sfdc.fetchSFDCinfo());
	 }
	 
		 
		 
		 
	    //metadataClient.createTextFields("Account", TextField, false, false, true, false, false);

    /**
     * Creates a permission set on the org with name "GS_Automation_Permission" & assigns to all the system admins & licensed users.
     * Code deployment is done for this feature to work.
     * Please check out ----- packageUtil.deployPermissionSetCode();
     *
     * Issues : if NULL POINTER Exception is found then the Fields send may not be found is SFDC (or) check the field API Name.
     *
     * @param object - Full Object API Name
     * @param fields - Array of fields.
     * @throws Exception - Connection exception, Runtime Exception if status is failed.
     */
    public void addFieldPermissionsToUsers(String object, String[] fields, SFDCInfo sfinfo) throws Exception {
        WebAction webAction = new WebAction();
        Header header = new Header();
        header.addHeader("Authorization", "Bearer " + sfinfo.getSessionId());
        header.addHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String[]> objMap = new HashMap<>();
        objMap.put(object, fields);
        Map<String, Object> payLoad1 = new HashMap<>();
        payLoad1.put("modulename", "GS_Auto_Permissions");
        List<Object> tmp = new ArrayList<Object>();
        tmp.add(objMap);
        payLoad1.put("data", tmp);
        Map<String, Object> payLoad = new HashMap<>();
        payLoad.put("params", mapper.writeValueAsString(payLoad1));
        Log.info(mapper.writeValueAsString(payLoad));
        ResponseObj responseObj = webAction.doPost(sfinfo.getEndpoint() + "/services/apexrest/GSAutomation/orgInfo/", header.getAllHeaders(), mapper.writeValueAsString(payLoad));
        Map<String, Object> resContent = new HashMap<>();
        resContent = mapper.readValue(responseObj.getContent(), resContent.getClass());
        if (!resContent.get("status").toString().equalsIgnoreCase("Success")) {
            Log.error("Failed to create field permissions");
            Log.error(responseObj.getContent());
            throw new RuntimeException(resContent.get("errMsg").toString());
        }
        Log.info("Field permissions added successfully.");
    }

    public void createExtIdFieldOnAccount(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String[] fields = new String[]{"Data ExternalId"};
        metadataClient.createTextFields("Account", fields, true, true, true, false, false);
        addFieldPermissionsToUsers("Account", convertFieldNameToAPIName(fields), sfdc.fetchSFDCinfo());
    }

    /**
     * Creates field on Contact object  & Assigns permissions to those fields.
     *
     * @param sfdc - Salesforce Connector.
     * @throws Exception - When failed to create fields / add permissions to the created fields.
     */
    public void createExtIdFieldOnContacts(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String[] fields = new String[]{"Contact ExternalId"};
        metadataClient.createTextFields("Contact", fields, true, true, true, false, false);
        addFieldPermissionsToUsers("Contact", convertFieldNameToAPIName(fields), sfdc.fetchSFDCinfo());
    }

    public void createFieldsOnContact(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        metadataClient.createTextFields("Contact", new String[]{"Contact ExternalID"}, true, true, true, false, false);
        metadataClient.createNumberField("Contact", new String[]{"NoOfReferrals", "NumForDate", "NumberField"}, false);
        metadataClient.createFields("Contact", new String[]{"Active"}, true, false, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InvolvedIn", new String[]{"Marketing", "Sales", "Forecast", "Finance", "Budget"});
        metadataClient.createPickListField("Contact", fields, true);
        metadataClient.createNumberField("Contact", new String[]{"DealCloseRate"}, true);
        String[] permField = new String[]{"Contact ExternalID", "NoOfReferrals", "NumForDate",
                "NumberField", "Active", "InvolvedIn", "DealCloseRate"};
        addFieldPermissionsToUsers("Contact", convertFieldNameToAPIName(permField), sfdc.fetchSFDCinfo());
    }

    /**
     * Creates fields on ScorecardMetric__c & Assigns permissions to those fields.
     *
     * @param sfdc - Salesforce Connector.
     * @throws Exception - When failed to create fields / add permissions to the created fields.
     */
    public void createExtIdFieldForScoreCards(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String Scorecard_Metrics = "JBCXM__ScorecardMetric__c";
        String[] SCMetric_ExtId = new String[]{"SCMetric ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(Scorecard_Metrics), SCMetric_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(Scorecard_Metrics), convertFieldNameToAPIName(SCMetric_ExtId), sfdc.fetchSFDCinfo());
    }

    public void createExtIdFieldForCustomObject(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String EmailCustomObj = "EmailCustomObjct__c";
        String[] CusObj_ExtId = new String[]{"CusObj ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(EmailCustomObj), CusObj_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(EmailCustomObj), convertFieldNameToAPIName(CusObj_ExtId), sfdc.fetchSFDCinfo());
        String[] Allfields = {"Dis_Email__c", "Dis_Name__c", "Dis_Role__c", "C_Reference__c"};
        addFieldPermissionsToUsers(resolveStrNameSpace(EmailCustomObj), Allfields, sfdc.fetchSFDCinfo());
        String[] fields = new String[]{"Data ExternalId"};
        metadataClient.createTextFields("Account", fields, true, true, true, false, false);
        addFieldPermissionsToUsers("Account", convertFieldNameToAPIName(fields), sfdc.fetchSFDCinfo());
    }

    /**
     * Creates Fields on USER Object & Assigns permissions to those fields.
     *
     * @param sfdc - Salesforce Connector.
     * @throws Exception - When failed to create fields / add permissions to the created fields.
     */
    public void createExtIdFieldOnUser(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String UserObj = "User";
        String[] user_ExtId = new String[]{"User ExternalId"};
        metadataClient.createTextFields(resolveStrNameSpace(UserObj), user_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(UserObj, convertFieldNameToAPIName(user_ExtId), sfdc.fetchSFDCinfo());
    }

    /**
     * Creates Fields on CTA__c Object  & Assigns permissions to those fields.
     *
     * @param sfdc - Salesforce Connection.
     * @throws Exception - When failed to create fields / add permissions to the created fields.
     */
    public void createExternalIdFieldOnCTA(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String CtaObj = "JBCXM__CTA__c";
        String[] Cta_ExtId = new String[]{"CTA ExternalID"};
        metadataClient.createTextFields(resolveStrNameSpace(CtaObj), Cta_ExtId, true, true, true, false, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(CtaObj), convertFieldNameToAPIName(Cta_ExtId), sfdc.fetchSFDCinfo());
    }

    /**
     * Created Fields on UsageData__c Object & Assigns permissions to those fields.
     *
     * @param sfdc - Salesforce Connector
     * @throws Exception - When failed to create fields / add permissions to the created fields.
     */
    public void createFieldsOnUsageData(SalesforceConnector sfdc) throws Exception {
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
        String object = "JBCXM__Usagedata__c";
        String[] numberFields1 = new String[]{"Page Views", "Page Visits", "No of Report Run", "Files Downloaded"};
        String[] numberFields2 = new String[]{"Emails Sent Count", "Leads", "No of Campaigns", "DB Size", "Active Users"};
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields1, false);
        metadataClient.createNumberField(resolveStrNameSpace(object), numberFields2, false);
        addFieldPermissionsToUsers(resolveStrNameSpace(object), convertFieldNameToAPIName(ArrayUtils.addAll(numberFields1, numberFields2)), sfdc.fetchSFDCinfo());
    }

    //same method is used by rules engine test cases also.
	    public void createFieldsForAccount(SalesforceConnector sfdc,SFDCInfo sfinfo) throws Exception {
			metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
			String object = "Account";
			String[] numberField = new String[]{"Number Auto"};
			String[] currency = new String[]{"Currency Auto"};
			String[] checkbox = new String[]{"Boolean Auto", "Boolean Auto1"};
			String[] date = new String[]{"Date Auto", "Date Auto1"};
			String[] dateTime = new String[]{"DateTime Auto"};
			String[] email = new String[]{"Email Auto"};
			String[] percent = new String[]{"Percent Auto"};
			String[] url = new String[]{"URL Auto"};
			HashMap<String, String[]> pick = new HashMap<String, String[]>();
			pick.put("PickList Auto", new String[]{"Excellent", "Vgood", "Good", "Average", "Poor", "Vpoor"});
			HashMap<String, String[]> multipickList = new HashMap<String, String[]>();
			multipickList.put("MultiPicklist Auto", new String[]{"MPL1", "MPL2"});
			metadataClient.createNumberField(resolveStrNameSpace(object), numberField, false);
			metadataClient.createCurrencyField(resolveStrNameSpace(object), currency);
			metadataClient.createFields(object, checkbox, true, false, false);
			metadataClient.createDateField(object, date, false);
			metadataClient.createDateField(object, dateTime, true);
			metadataClient.createEmailField(object, email);
			metadataClient.createNumberField(object, percent, true);
			metadataClient.createFields(object, url, false, false, true);
			metadataClient.createPickListField(object, pick, false);
			metadataClient.createPickListField(object, multipickList, true);
			String[] targetArray = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(numberField, currency), checkbox), date), dateTime), email), percent), url);
			targetArray = ArrayUtils.addAll(targetArray, pick.keySet().toArray(new String[pick.keySet().size()]));
			targetArray = ArrayUtils.addAll(targetArray, multipickList.keySet().toArray(new String[multipickList.keySet().size()]));
			addFieldPermissionsToUsers(resolveStrNameSpace(object), convertFieldNameToAPIName(targetArray), sfinfo);

		}

	 //Delete Account metadata Rules Engine
	 public void deleteAccountMetadata(SalesforceConnector sfdc){
		 try{
			 metadataClient= SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
			 String object = "Account";
			 String FieldsToDelete1[]={"Boolean_Auto__c","Boolean_Auto1__c","Currency_Auto__c","Date_Auto__c","Date_Auto1__c","DateTime_Auto__c"};
			 String FieldsToDelete2[]={"Email_Auto__c","MultiPicklist_Auto__c","Number_Auto__c","Percent_Auto__c","PickList_Auto__c","URL_Auto__c"};

			 metadataClient.deleteFields(object,FieldsToDelete1);
			 metadataClient.deleteFields(object,FieldsToDelete2);
		 }
		 catch(Exception e){
			 e.printStackTrace();
			 System.out.println("Exception is "+e.getMessage());
		 }
	 }

    /**
     * Replaces / Removes name space provided.
     *
     * @param str - String to replace/remove name space like JBCXM.
     * @return - names space removed string.
     */
    public String resolveStrNameSpace(String str) {
        return FileUtil.resolveNameSpace(str, sfdcConfig.getSfdcManagedPackage() ? sfdcConfig.getSfdcNameSpace() : null);
    }

    /**
     * Generates API names for the fields supplied by replacing space with "_" * appending __c at end.
     *
     * @param args - List of fields to be converted to API names.
     * @return - Field API names by replacing all the spaces with _ & appending __c.
     */
    public String[] convertFieldNameToAPIName(String[] args) {
        Log.info("Converting Field Names to API names....");
        String[] temp = new String[args.length];
        int i = 0;
        for (String s : args) {
            temp[i] = s.replaceAll(" ", "_") + "__c";
            Log.info("Field Name : " + temp[i]);
            i++;
        }
        Log.info("Converted Field Names to API names.");
        return temp;
    }
}
