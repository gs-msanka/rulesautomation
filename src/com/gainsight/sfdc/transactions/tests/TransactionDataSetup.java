package com.gainsight.sfdc.transactions.tests;


import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.dataLoad.loadDataFromFile;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class TransactionDataSetup {


    TestEnvironment env;
    static boolean isPackageInstance = false;
    static String resDir = "./resources/datagen/";
    static JobInfo jobInfo1;
    ObjectMapper mapper = new ObjectMapper();
    public TransactionDataSetup() {
        TestEnvironment env = new TestEnvironment();
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    }

    public static void main(String[] args) {
        TransactionDataSetup setup = new TransactionDataSetup();
        AccountDataSetup accSetup = new AccountDataSetup();
        accSetup.createExtIdFieldOnAccount();
        setup.createExtIdFieldsInTransactionModule();
        SFDCUtil sfdcUtil = new SFDCUtil();
        sfdcUtil.runApexCodeFromFile("./testdata/sfdc/transactions/scripts/TrailData_Populate_TransTypes.txt", isPackageInstance);
        try {
            setup.LoadTransData();
        } catch (IOException e) {
            Report.logInfo("Failed to Load Account's");
            e.printStackTrace();
        }
    }

    public void createExtIdFieldsInTransactionModule() {

        CreateObjectAndFields cObjFields = new CreateObjectAndFields();

        String transHeader_Obj      = "JBCXM__TransHeader__c";
        String transLines_Obj       = "JBCXM__TransLines__c";
        String transTypes_Obj       = "JBCXM__TransactionType__c";

        String[] transHeader_ExtId  = new String[]{"TransHeader ExternalID"};
        String[] transLines_ExtId   = new String[]{"TransLines ExternalID"};
        String[] transTypes_ExtId   = new String[]{"TransTypes ExternalID"};

        try {
            cObjFields.createTextFields(resolveNameSpace(transHeader_Obj), transHeader_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(resolveNameSpace(transLines_Obj), transLines_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(resolveNameSpace(transTypes_Obj), transTypes_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("*************Failed to create fields*****************");
            e.printStackTrace();
        }

    }

    public void LoadTransData() throws IOException {
        loadDataFromFile dataLoad = new loadDataFromFile();
        DataETL dataLoader = new DataETL();

        Report.logInfo("Cleaning Transaction Header");
        dataLoader.cleanUp(resolveNameSpace("JBCXM__TransHeader__c"), "TransHeader_ExternalID__c != null");
        dataLoader.cleanUp(resolveNameSpace("JBCXM__TransLines__c"), null);
        dataLoader.cleanUp(resolveNameSpace("JBCXM__CustomerInfo__c"), resolveNameSpace(" JBCXM__OriginalContractNumber__c = 'AUTO_SAMPLE_DATA'"));

        Report.logInfo("Loading Account Data");
        jobInfo1 = mapper.readValue(resolveNameSpace(resDir + "jobs/Job_Trans_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);
        //dataLoad.loadData("Account", "./testdata/sfdc/transactions/TrailData/Account.csv", "Data_ExternalID__c");

        Report.logInfo("Loading Order Transaction Map");
        dataLoader.cleanUp("JBCXM__OrderTransactionMap__c", null);
        dataLoad.loadData(resolveNameSpace("JBCXM__OrderTransactionMap__c"), "./testdata/sfdc/transactions/TrailData/OrderTransactionMap.csv", null);

        Report.logInfo("Loading First Transaction header File");
        dataLoad.loadData(resolveNameSpace("JBCXM__TransHeader__c"), "./testdata/sfdc/transactions/TrailData/TransHeader_Load1.csv", "TransHeader_ExternalID__c");

        Report.logInfo("Loading Second Transaction header File");
        dataLoad.loadData(resolveNameSpace("JBCXM__TransHeader__c"), "./testdata/sfdc/transactions/TrailData/TransHeader_Load2.csv", "TransHeader_ExternalID__c");

        Report.logInfo("Loading Transaction Lines");
        dataLoad.loadData(resolveNameSpace("JBCXM__TransLines__c"), "./testdata/sfdc/transactions/TrailData/TransLines.csv", null);

        Report.logInfo("Loading Customers Data");
        dataLoad.loadData(resolveNameSpace("JBCXM__CustomerInfo__c"), "./testdata/sfdc/transactions/TrailData/Customers.csv", null);

    }


    public String resolveNameSpace(String s) {
        if(!isPackageInstance) {
            return s.replaceAll("JBCXM__", "");
        }
        return s;
    }
}

