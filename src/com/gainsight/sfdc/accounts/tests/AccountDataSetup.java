package com.gainsight.sfdc.accounts.tests;

import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;


public class AccountDataSetup {


    public AccountDataSetup() {

    }

    public static void main(String[] args) {
        AccountDataSetup setUp = new AccountDataSetup();
        setUp.createExtIdFieldOnAccount();

    }

    public void createExtIdFieldOnAccount() {
        String obj_Name = "Account";
        String[] field_Name = new String[]{"Data ExternalID"};
        CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        try {
            cObjFields.createTextFields(obj_Name, field_Name,true, true, true, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
