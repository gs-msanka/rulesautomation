package com.gainsight.bigdata.TouchHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.GSUtil;
import com.gainsight.bigdata.rulesengine.ResponseObject;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class LoadTestData extends NSTestBase{
		
	private static final String rulesDir = Application.basedir+"/testdata/newstack/RulesEngine/CreateCTA/";    	
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";
    ResponseObj result=null;
    BaseTest bt=new BaseTest();
	
	@BeforeClass
    public void beforeClass() throws Exception {
        //LastRunResultFieldName = GSUtil.resolveStrNameSpace(LastRunResultFieldName);		
    }	
	
	//Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard Object), Yes Owner Field. 
	//Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, OwnerId. (Token are only Standard fields from Account Object are taken here)
	@Test
	public void CreateCustomFields() throws Exception {

		//String RuleName="Create CTA No Adv Criteria",Comment=null,Priority = null,Status= null,Type= null,Reason= null,Playbook= null; //Declaring fields to use in CTA		
		//String Id = null, C_Text = null, C_Number = null, C_Checkbox = null, C_Currency = null, C_Email = null, C_Percent = null, C_Phone = null, C_Picklist = null, C_MultiPicklist = null,C_TextArea = null,C_EncryptedString=null,C_URL=null,C_Reference=null;//Declaring fields to use in Account Object
		
		String ReferenceTo_User="User";  //Reference to User Object
		String ReleationShipName_User="User_Automation2"; //Relation Name
		String User_Reference[]={ReferenceTo_User,ReleationShipName_User};
		
		//Custom Fields to Delete from Account Object
		String FieldsToDelete1[]={"p_c_AutoNumber__c","p_c_Checkbox__c","p_c_Contact__c","p_c_Currency__c","p_c_Date__c"};
		String FieldsToDelete2[]={"p_c_DateTime__c","p_c_Formula__c","p_c_GeoLocation__c","p_c_MultiPicklist__c","p_c_Number__c"};
		String FieldsToDelete3[]={"p_c_Percent__c","p_c_PhoneNumber__c","p_c_Picklist__c","p_c_Text__c","p_c_TextArea__c"};
		String FieldsToDelete4[]={"p_c_TextAreaLong__c","p_c_TextAreaRich__c","p_c_TextEncrypted__c","p_c_URL__c","p_c_User__c"};
		String FieldsToDelete5[]={"p_c_Email__c"};
				
		String TextField[]={"p_c_Text"} , NumberField[]={"p_c_Number"} , Checkbox[]={"p_c_Checkbox"} , Currency[]={"p_c_Currency"} , Email[]={"p_c_Email"} , Percent[]={"p_c_Percent"} ,  Phone[]={"p_c_PhoneNumber"} , Picklist_FieldName="p_c_Picklist" , 
				AutoNumber[]={"p_c_AutoNumber"}, Lookup_Contact[]={"p_c_Contact"},	Date[]={"p_c_Date"}, DateTime[]={"p_c_DateTime"}, Formula[]={"p_c_Formula"}, GeoLocation[]={"p_c_GeoLocation"},
				TextAreaLong[]={"p_c_TextAreaLong"}, TextAreaRich[]={"p_c_TextAreaRich"}, Lookup_User[]={"p_c_User"},
				Picklist_Values[]={"Pvalue1","Pvalue2","Pvalue3"} , MultiPicklist_FieldName="p_c_MultiPicklist", MultiPicklist_Values[]={"MPvalue1","MPvalue2","MPvalue3"} , TextArea[]={"p_c_TextArea"} , EncryptedString[]={"p_c_TextEncrypted"} , URL[]={"p_c_URL"};
		
		String ReferenceTo_Contact="Contact";  //Reference to User Object
		String ReleationShipName_Contact="Contact_Automation2"; //Relation Name
		String Contact_Reference[]={ReferenceTo_Contact,ReleationShipName_Contact};
		
		List<HashMap<String, String>> FormulaFieldsList;
		FormulaFieldsList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> formulafieldsMap=new HashMap<String, String>();
		formulafieldsMap.put("Type", "Number");		
		formulafieldsMap.put("Formula", "NumberOfEmployees");
		formulafieldsMap.put("FieldName", Formula[0]);
		formulafieldsMap.put("Description", "Sample Description");
		formulafieldsMap.put("HelpText", "Sample HelpText");	
		FormulaFieldsList.add(formulafieldsMap);
		
		HashMap<String, String[]> pickListFields=new HashMap<String, String[]>();
		pickListFields.put(Picklist_FieldName, Picklist_Values);
		
		HashMap<String, String[]> MultipickListFields=new HashMap<String, String[]>();
		MultipickListFields.put(MultiPicklist_FieldName, MultiPicklist_Values);
			
		
		//Delete Custom Fields
		metadataClient.deleteFields("JBCXM__CustomerInfo__c",FieldsToDelete1);
		metadataClient.deleteFields("JBCXM__CustomerInfo__c",FieldsToDelete2);
		metadataClient.deleteFields("JBCXM__CustomerInfo__c",FieldsToDelete3);
		metadataClient.deleteFields("JBCXM__CustomerInfo__c",FieldsToDelete4);
		metadataClient.deleteFields("JBCXM__CustomerInfo__c",FieldsToDelete5);
		
		//CreateCTA
		metadataClient.createAutoNumberFields("JBCXM__CustomerInfo__c", AutoNumber);
		metadataClient.createFields("JBCXM__CustomerInfo__c", Checkbox, true, false, false);
		metadataClient.createLookupField("JBCXM__CustomerInfo__c", Lookup_Contact, Contact_Reference);
		metadataClient.createCurrencyField("JBCXM__CustomerInfo__c", Currency);
		metadataClient.createDateField("JBCXM__CustomerInfo__c", Date, false);
		metadataClient.createDateField("JBCXM__CustomerInfo__c", DateTime, true);		
		metadataClient.createFormulaFields("JBCXM__CustomerInfo__c", FormulaFieldsList);
		metadataClient.createPickListField("JBCXM__CustomerInfo__c", MultipickListFields, true);		
		metadataClient.createNumberField("JBCXM__CustomerInfo__c", NumberField, false);
		metadataClient.createNumberField("JBCXM__CustomerInfo__c", Percent, true);
		metadataClient.createFields("JBCXM__CustomerInfo__c", Phone, false, true, false);
		metadataClient.createPickListField("JBCXM__CustomerInfo__c", pickListFields, false);
		metadataClient.createTextFields("JBCXM__CustomerInfo__c", TextField, false, false, true, false, false);
		metadataClient.createTextFields("JBCXM__CustomerInfo__c", TextArea, false, false, false, true, false);
		metadataClient.createTextFields("JBCXM__CustomerInfo__c", TextAreaRich, false, false, false, false, true);
		metadataClient.createEncryptedTextFields("JBCXM__CustomerInfo__c", EncryptedString);
		metadataClient.createFields("JBCXM__CustomerInfo__c", URL, false, false, true);
		metadataClient.createLookupField("JBCXM__CustomerInfo__c", Lookup_User, User_Reference);
		metadataClient.createEmailField("JBCXM__CustomerInfo__c", Email);
		
		
		//Permission
		addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"p_c_AutoNumber__c","p_c_Checkbox__c","p_c_Contact__c","p_c_Currency__c","p_c_Date__c"});
		addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"p_c_DateTime__c","p_c_Formula__c","p_c_GeoLocation__c","p_c_MultiPicklist__c","p_c_Number__c"});
		addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"p_c_Percent__c","p_c_PhoneNumber__c","p_c_Picklist__c","p_c_Text__c","p_c_TextArea__c"});
		addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"p_c_TextAreaLong__c","p_c_TextAreaRich__c","p_c_TextEncrypted__c","p_c_URL__c","p_c_User__c"});
		addFieldPermissionsToUsers("JBCXM__CustomerInfo__c", new String[]{"p_c_Email__c"});
	}
	
	@AfterClass
    public void afterClass() {
        
    }
	
}
