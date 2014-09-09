package com.gainsight.sfdc.util.metadata;

import com.sforce.soap.metadata.*;
import com.sforce.ws.ConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class CreateObjectAndFields {

    String MANIFEST_FILE = "./src/package.xml";
    String ZIP_FILE = "./zipFiles/Sample.zip";
    boolean isPackageInstance = true;
    static MetadataConnection metadataConnection=null;

    public CreateObjectAndFields() {
        MetadataLoginUtil metaConn = new MetadataLoginUtil();
        try {
            metadataConnection = metaConn.login();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        CreateObjectAndFields objVar = new CreateObjectAndFields();
        String ObjName = "Account";
        String[] numberFields = new String[]{"Page Views", "Page Visits", "No of Report Run", "Files Downloaded"};

        HashMap<String, String[]> pickListFields =  new HashMap<String, String[]>();
        String[] values1 = new String[]{"Low NPS Score", "Sprint Planning", "Sprint Retrospection", "More Cases Opened"};
        pickListFields.put("Custom PickList1", values1);
        pickListFields.put("Custom PickList2", values1);

        HashMap<String, String[]> multiPickListFields =  new HashMap<String, String[]>();
        String[] values2 = new String[]{"Low NPS Score", "Sprint Planning", "Monthly Business Review",
                "Quarterly Business Review", "Weekly Business Review",
                "Sprint Review", "Sprint Retrospection", "Customer On-Boarding", "Survey Response Event"};
        multiPickListFields.put("Custom MultiPick1", values2);
        multiPickListFields.put("Custom MultiPick2", values2);


        String[] checkBoxFields 	= new String[]{"Check Box Field 11", "Check Box Field 12", "Check Box Field 13"};
        String[] currencyFields 	= new String[]{"Currency 1", "Currency 2", "Currency 3"};
        String[] dateFields 		= new String[]{"Date 1", "Date 2", "Date 3"};
        String[] dateTimeFields 	= new String[]{"Date Time 1", "Date Time 2", "Date Time 3"};
        String[] percentageFields 	= new String[]{"Percentage 1", "Percentage 2", "Percentage 3"};
        String[] urlFields 			= new String[]{"URL 1", "URL 2", "URL 3"};
        String[] phoneFields 		= new String[]{"Phone 1", "Phone 2", "Phone 3"};
        String[] textFields 		= new String[]{"Text 1", "Text 2", "Text 3"};
        String[] textAreaFields 	= new String[]{"Text Area 1", "Text Area 2", "Text Area 3"};
        String[] textRichFields 	= new String[]{"Text Rich 1", "Text Rich 2", "Text Area 3"};

        List<HashMap<String, String>> formulafieldsList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapper = new HashMap<String, String>();
        mapper.put("Type", "CheckBox");
        mapper.put("Formula", "CONTAINS(Name, 'a')");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula Check");
        mapper.put("HelpText", "TRUE if name of account contains 'a'");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "Currency");
        mapper.put("Formula", "AnnualRevenue * LEN(Name)");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula Currency");
        mapper.put("HelpText", "Contains Annual Revenue * 100 as value");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "Date");
        mapper.put("Formula", "TODAY()+LEN(NAME)");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula Date");
        mapper.put("HelpText", "Values = Today + Length of name");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "DateTime");
        mapper.put("Formula", "NOW()-LEN(Name)");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula DateTime");
        mapper.put("HelpText", "Values = Today-no days(account legth)");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "Number");
        mapper.put("Formula", "IF(AnnualRevenue == null && AnnualRevenue < 100 ,  30 , "
                + "IF(LEN( Name ) > 30  &&   AnnualRevenue > 500 , (AnnualRevenue * (LEN( Name )-25)), "
                + "(AnnualRevenue * (LEN( Name )))))");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula Number");
        mapper.put("HelpText", "Values = AnnualRevenue * 50");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "Percent");
        mapper.put("Formula", "IF(AnnualRevenue == null && AnnualRevenue < 100 ,  30 , "
                + "IF(LEN( Name ) > 30  &&   AnnualRevenue > 500 , AnnualRevenue / (AnnualRevenue * (LEN( Name )-25)), "
                + "AnnualRevenue / (AnnualRevenue * (LEN( Name )))))"); //IF( CONTAINS( Name , 'a') , 'Account Name Contains a', 'Account Name Contains a')
        mapper.put("Description", "This fiels is created from script");
        mapper.put("FieldName", "Formula Percent");
        mapper.put("HelpText", "Values = AnnualRevenue / (AnnualRevenue * 10)");
        formulafieldsList.add(mapper);
        mapper = new HashMap<String, String>();
        mapper.put("Type", "Text");
        mapper.put("Formula", "IF( CONTAINS( Name , 'a') , 'Account Name Contains a', 'Account Name doesnt Contains a')");
        mapper.put("Description", "This field is created from script");
        mapper.put("FieldName", "Formula Text");
        mapper.put("HelpText", "Values ");
        formulafieldsList.add(mapper);
		/*objVar.createFormulaFields(ObjName, formulafieldsList);
		objVar.createCustomObject(metadataConnection, name);
		objVar.deleteCustomObject(metadataConnection, name);
		objVar.retrivePackageFile(objVar, metadataConnection, name);
		objVar.updateObject(objVar, metadataConnection, name);
		objVar.createCurrencyField(metadataConnection, ObjName, currencyFields);
		objVar.createDateField(metadataConnection, ObjName, dateFields, false);
		objVar.createDateField(metadataConnection, ObjName, dateTimeFields, true);
		objVar.createNumberField(metadataConnection, ObjName, percentageFields, true);
		objVar.createNumberField(metadataConnection, ObjName, numberFields, false);
		objVar.createFields(metadataConnection, ObjName, checkBoxFields, true, false, false);
		objVar.createFields(metadataConnection, ObjName, urlFields, false, false, true);
		objVar.createFields(metadataConnection, ObjName, phoneFields, false, true, false);

		objVar.createPickListField(metadataConnection, ObjName, pickListFields, false);
		objVar.createPickListField(metadataConnection, ObjName, multiPickListFields, true);

		objVar.createTextFields(metadataConnection, ObjName, textFields, true, false, false);
		objVar.createTextFields(metadataConnection, ObjName, textFields, false, true, false);
		objVar.createTextFields(metadataConnection, ObjName, textRichFields, false, false, true);
        objVar.deletefields(metadataConnection, ObjName, checkBoxFields); */
    }



    public String removeNameSpace(String str) {
        return str.replaceAll("JBCXM__", "");
    }

    public void createFormulaFields(String objName, List<HashMap<String, String>> formulafieldsList) throws Exception {
        Metadata[] metadata = new Metadata[formulafieldsList.size()];
        int i=0;
        for(HashMap<String, String> testData : formulafieldsList) {
            CustomField custField = new CustomField();
            if(testData.get("Type").equals("CheckBox")) {
                custField.setType(FieldType.Checkbox);
            } else if(testData.get("Type").equals("Currency")) {
                custField.setType(FieldType.Currency);
                custField.setPrecision(2);
                custField.setScale(18);
            } else if(testData.get("Type").equals("Date")) {
                custField.setType(FieldType.Date);
            } else if(testData.get("Type").equals("DateTime")) {
                custField.setType(FieldType.DateTime);
            }  else if(testData.get("Type").equals("Number")) {
                custField.setType(FieldType.Number);
                custField.setPrecision(2);
                custField.setScale(18);
            } else if(testData.get("Type").equals("Percent")) {
                custField.setType(FieldType.Percent);
                custField.setPrecision(2);
                custField.setScale(18);
            } else if(testData.get("Type").equals("Text")) {
                custField.setType(FieldType.Text);
            }
            custField.setFormula(testData.get("Formula"));
            custField.setLabel(testData.get("FieldName"));
            custField.setFullName(objName+"."+testData.get("FieldName").trim().replaceAll(" ", "_")+"__c");
            custField.setDescription(testData.get("Description"));
            custField.setInlineHelpText(testData.get("HelpText"));
            metadata[i]=custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }





    /**
     * Make sure not more than 10 objects are referenced in one call - salesforce limitation.
     * @param objName
     * @param fields
     * @throws com.sforce.ws.ConnectionException
     * @throws InterruptedException
     */
    public void deletefields(String objName, String[] fields) throws ConnectionException, InterruptedException {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            custField.setFullName(objName+"."+field.trim().replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        AsyncResult[] ars = metadataConnection.delete(metadata);
        long waitTimeMilliSecs = 1000;
        for(i =0; i < ars.length; i++) {
            while (!ars[i].isDone()) {
                Thread.sleep(waitTimeMilliSecs);
                // double the wait time for the next iteration
                //waitTimeMilliSecs *= 2;
                ars = metadataConnection.checkStatus(new String[] { (ars[i]).getId() });
                System.out.println("Status of field : "+ars[i].getMessage()+" & Status is: " + ars[i].getState());
            }
        }
        System.out.println(" Job Done Boss!!!!!!!");
    }

    public void createFields(String objName, String[] fields, boolean isCheckBox, boolean isPhone, boolean isUrl) throws Exception {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            if(isCheckBox) {
                custField.setType(FieldType.Checkbox);
                custField.setDefaultValue("false");
            } else if(isPhone) {
                custField.setType(FieldType.Phone);
            } else if(isUrl) {
                custField.setType(FieldType.Url);
            }
            custField.setLabel(field);
            custField.setFullName(objName+"."+field.trim().replaceAll(" ", "_")+"__c");
            custField.setDescription("This field is created from metadata api script");

            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void createPickListField(String objName, HashMap<String, String[]> pickListFields, boolean isMutipickList) throws Exception {
        Metadata[] metadata = new Metadata[pickListFields.size()];
        int i=0;
        Iterator itr = pickListFields.keySet().iterator();
        String[] pkValues = null;
        String fieldName;
        while(itr.hasNext()) {
            fieldName = (String) itr.next();
            pkValues = pickListFields.get(fieldName);
            CustomField custField = new CustomField();
            if(isMutipickList) {
                custField.setType(FieldType.MultiselectPicklist);
                custField.setVisibleLines(4);
            } else {
                custField.setType(FieldType.Picklist);
            }

            custField.setLabel(fieldName);
            custField.setFullName(objName+"."+fieldName.trim().replaceAll(" ", "_")+"__c");
            custField.setDescription("This field is created from meta data script");
            Picklist p = new Picklist();
            PicklistValue[] pkValueArray = new PicklistValue[pkValues.length];
            int j=0;
            for(String value : pkValues) {
                PicklistValue picklistValue = new PicklistValue();
                picklistValue.setFullName(value.trim());
                pkValueArray[j]=picklistValue;
                j++;
            }
            p.setPicklistValues(pkValueArray);
            custField.setPicklist(p);
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void createTextFields(String objName, String[] fields, boolean isExternalID, boolean isUnique, boolean isTextField, boolean isTextArea, boolean isTextRich) throws Exception {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            if(isTextField) {
                custField.setType(FieldType.Text);
                custField.setLength(250);
            } else if(isTextArea) {
                custField.setType(FieldType.TextArea);
            } else if(isTextRich) {
                custField.setType(FieldType.Html);
                custField.setLength(32768);
                custField.setVisibleLines(10);
            }
            if(isExternalID) {
                custField.setExternalId(true);
            }
            if(isUnique) {
                custField.setUnique(true);
                custField.setCaseSensitive(false);
            }
            custField.setLabel(field);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void createCurrencyField(String objName, String[] fields) throws Exception {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            custField.setType(FieldType.Currency);
            custField.setLabel(field);
            custField.setScale(2);
            custField.setPrecision(18);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void createDateField(String objName, String[] fields, boolean isDateTime) throws Exception {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            if(isDateTime) {
                custField.setType(FieldType.DateTime);
            } else {
                custField.setType(FieldType.Date);
            }
            custField.setLabel(field);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void createAndCheckStatus(Metadata[] metadata) throws Exception {
        AsyncResult[] ars = metadataConnection.create(metadata);
        String[] id = new String[ars.length];
        int j =0;
        boolean iserror = false;
        for(AsyncResult ar : ars) {
            id[j]=ar.getId();
            ++j;
        }
        long waitTimeMilliSecs = 1000;
        Thread.sleep(waitTimeMilliSecs);
        for(int i =0; i < id.length; i++) {
            do {
                ars = metadataConnection.checkStatus(new String[] { id[i]});
            }
            while (!ars[0].isDone());

            if(ars[0].getMessage() != null ) {
                System.out.println("Status of field : "+ars[0].getMessage()+" & Status is: " + ars[0].getState());
                iserror = true;
            }
            if(iserror) {
                System.out.println("job Done With Error's !!!!!!!");
            } else {
                System.out.println("job Done !!!!!!!");
            }
        }


    }

    public void createNumberField(String objName, String[] fields, boolean isPercentage) throws Exception {
        Metadata[] metadata = new Metadata[fields.length];
        int i=0;
        for(String field : fields) {
            CustomField custField = new CustomField();
            if(isPercentage) {
                custField.setType(FieldType.Percent);
            } else {
                custField.setType(FieldType.Number);
            }
            custField.setLabel(field);
            custField.setScale(2);
            custField.setPrecision(18);
            custField.setFullName(objName+"."+field.replaceAll(" ", "_")+"__c");
            metadata[i] = custField;
            i++;
        }
        createAndCheckStatus(metadata);
    }

    public void deleteCustomObject(String name) throws ConnectionException, InterruptedException {
        CustomObject co = new CustomObject();
        co.setFullName(name + "__c");
        AsyncResult[] ars = metadataConnection.delete(new Metadata[]{co});
        AsyncResult asyncResult = ars[0];

        long waitTimeMilliSecs = 1000;
        while (!asyncResult.isDone()) {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            asyncResult = metadataConnection.checkStatus(new String[] {asyncResult.getId()})[0];
            System.out.println("Status is: " + asyncResult.getState());
        }
    }

    public void createCustomObject(String name) throws ConnectionException, InterruptedException {
        CustomObject co = new CustomObject();
        co.setFullName(name + "__c");
        co.setDeploymentStatus(DeploymentStatus.Deployed);
        co.setDescription("Created by the Metadata API");
        co.setEnableActivities(true);
        co.setLabel(name + " Object");
        co.setPluralLabel(co.getLabel() + "s");
        co.setSharingModel(SharingModel.ReadWrite);
        CustomField nf = new CustomField();
        nf.setType(FieldType.Text);
        nf.setLabel("Name");
        co.setNameField(nf);

        AsyncResult[] ars = metadataConnection.create(new Metadata[]{co});
        AsyncResult asyncResult = ars[0];

        long waitTimeMilliSecs = 1000;
        while (!asyncResult.isDone()) {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            asyncResult = metadataConnection.checkStatus(new String[] {asyncResult.getId()})[0];
            System.out.println("Status is: " + asyncResult.getState());
        }

    }
     
    public void isRemoteSitePresent(String url){
    	RemoteSiteSetting rss= new RemoteSiteSetting();
    	
    }
    
    public void createRemoteSiteSetting(String url) throws Exception{
    	RemoteSiteSetting[] rss= new RemoteSiteSetting[1];
    	rss[0]=new RemoteSiteSetting();
    	rss[0].setDescription("ns url for testing");
    	rss[0].setDisableProtocolSecurity(false);
    	rss[0].setFullName("NS");
    	rss[0].setIsActive(true);
    	rss[0].setUrl(url);
    	createAndCheckStatus(rss);    	
    }
}
