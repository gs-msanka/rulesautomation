package com.gainsight.bigdata.TouchHub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.ObjectFields;
import com.gainsight.util.MetaDataUtil;

public class LoadTestData extends NSTestBase{

	MetaDataUtil md=new MetaDataUtil(); 	
	
	@Test
	public void createCustomFields() throws Exception {
		
		ObjectFields objField=new ObjectFields();
		
		//createCustomObject
		metadataClient.createCustomObject("C_ContactObject");
		metadataClient.createCustomObject("C_ContactObject2");		
			
		List<HashMap<String, String>> lookupList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> lookupListMap1=new HashMap<String, String>();
			lookupListMap1.put("Name", "p_c_Contact");		
			lookupListMap1.put("ReferenceTo", "Contact");
			lookupListMap1.put("ReleationShipName", "Contact_Automation2");
		lookupList.add(lookupListMap1);
		HashMap<String, String> lookupListMap2=new HashMap<String, String>();	
			lookupListMap2.put("Name", "p_c_User");
			lookupListMap2.put("ReferenceTo", "User");
			lookupListMap2.put("ReleationShipName", "User_Automation2");
		lookupList.add(lookupListMap2);
		HashMap<String, String> lookupListMap3=new HashMap<String, String>();	
			lookupListMap3.put("Name", "p_c_Account");
			lookupListMap3.put("ReferenceTo", "Account");
			lookupListMap3.put("ReleationShipName", "Account_Automation2");
		lookupList.add(lookupListMap3);
		objField.setLookups(lookupList);
			
		
		List<HashMap<String, String>> formulaFieldsList= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> formulafieldsMap=new HashMap<String, String>();
			formulafieldsMap.put("Type", "Number");		
			formulafieldsMap.put("Formula", "NumberOfEmployees");
			formulafieldsMap.put("FieldName", "p_c_Formula");
			formulafieldsMap.put("Description", "Sample Description");
			formulafieldsMap.put("HelpText", "Sample HelpText");	
		formulaFieldsList.add(formulafieldsMap);
		objField.setFormulaFieldsList(formulaFieldsList);
		
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
		
		md.createFieldsOnObject(sfdc, sfinfo, "JBCXM__CustomerInfo__c", objField);
		
	}
	
}
