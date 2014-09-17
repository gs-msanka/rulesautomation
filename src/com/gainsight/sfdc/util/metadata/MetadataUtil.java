package com.gainsight.sfdc.util.metadata;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 11/09/14
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataUtil {

    private static CreateObjectAndFields createObjectAndFields;
    private static final String CONTACT_OBJECT = "CONTACT" ;

    public MetadataUtil() {
         createObjectAndFields = new CreateObjectAndFields();
    }

    public void createFieldsOnContact() {
        createObjectAndFields.createTextFields(CONTACT_OBJECT, new String[]{"Contact ExternalID"}, true, true, true, false, false);
        createObjectAndFields.createNumberField(CONTACT_OBJECT, new String[]{"NoOfReferrals", "NumForDate", "NumberField"},false);
        createObjectAndFields.createFields(CONTACT_OBJECT, new String[]{"Active"}, true, false, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InvolvedIn", new String[]{"Marketing", "Sales", "Forecast", "Finance", "Budget"});
        createObjectAndFields.createPickListField(CONTACT_OBJECT, fields, true);
        createObjectAndFields.createNumberField(CONTACT_OBJECT, new String[]{"DealCloseRate"}, true);
    }

    public void createFieldsOnAccount() {
        createObjectAndFields.createTextFields("Account", new String[]{"Data ExternalId"}, true, true, true, false, false);
        createObjectAndFields.createFields("Account", new String[]{"IsActive"}, true, false, false);
        createObjectAndFields.createDateField("Account", new String[]{"InputDate"}, false);
        createObjectAndFields.createDateField("Account", new String[]{"InputDateTime"}, true);
        createObjectAndFields.createNumberField("Account", new String[]{"AccPercentage"}, true);
        createObjectAndFields.createNumberField("Account", new String[]{"ActiveUsers"}, false);
        HashMap<String, String[]> fields = new HashMap<String, String[]>();
        fields.put("InRegions", new String[]{"India", "America", "England", "France", "Italy", "Germany", "Japan" , "China", "Australia", "Russia", "Africa", "Arab "});
        createObjectAndFields.createPickListField("Account", fields, true);
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
        createObjectAndFields.createFormulaFields("Account", fFields);
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
        createObjectAndFields.createFormulaFields("Account", fFields);
    }
}
