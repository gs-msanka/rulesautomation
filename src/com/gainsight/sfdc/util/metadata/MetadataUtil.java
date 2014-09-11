package com.gainsight.sfdc.util.metadata;

import com.gainsight.pageobject.core.Report;

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

    public void createExtIdOnContact() {
        try {
            createObjectAndFields.createTextFields(CONTACT_OBJECT, new String[]{"Contact ExternalID"}, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create ext id field on account object :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void createFieldsOnContact() {

        try {
            createObjectAndFields.createNumberField(CONTACT_OBJECT, new String[]{"NoOfReferrals", "NumForDate", "NumberField"},false);
        } catch (Exception e) {
            Report.logInfo("Failed to create number fields :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        try {
            createObjectAndFields.createFields(CONTACT_OBJECT, new String[]{"Active"}, true, false, false);
        } catch (Exception e) {
            Report.logInfo("Failed to create Active fields :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        try {
            HashMap<String, String[]> fields = new HashMap<String, String[]>();
            fields.put("InvolvedIn", new String[]{"Marketing", "Sales", "Forecast", "Finance", "Budget"});
            createObjectAndFields.createPickListField(CONTACT_OBJECT, fields, true);
        } catch (Exception e) {
            Report.logInfo("Failed to create PickList fields :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        try {
            createObjectAndFields.createNumberField(CONTACT_OBJECT, new String[]{"DealCloseRate"}, true);
        } catch (Exception e) {
            Report.logInfo("Failed to create percentage fields :" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }






}
