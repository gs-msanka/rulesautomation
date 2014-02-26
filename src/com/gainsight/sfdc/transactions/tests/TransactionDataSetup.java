package com.gainsight.sfdc.transactions.tests;


import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.accounts.tests.AccountDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TransactionDataSetup {

    static boolean isPackageInstance = false;


    ObjectMapper mapper = new ObjectMapper();
    static TestEnvironment env = new TestEnvironment();
    static String resDir = env.basedir+"/resources/datagen/";
    static BaseTest  baseTest = new BaseTest();
    public TransactionDataSetup() {
        isPackageInstance = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));

    }

    public static void main(String[] args) throws IOException {
        TransactionDataSetup setup = new TransactionDataSetup();
        AccountDataSetup accSetup = new AccountDataSetup();
        accSetup.createExtIdFieldOnAccount();
        setup.createExtIdFieldsInTransactionModule();
        setup.LoadTransData();
    }

    /**
     * Creates external Id fields on all the transaction module objects.
     */
    public void createExtIdFieldsInTransactionModule() {
        CreateObjectAndFields cObjFields = new CreateObjectAndFields();
        String transHeader_Obj      = baseTest.resolveStrNameSpace("JBCXM__TransHeader__c");
        String transLines_Obj       = baseTest.resolveStrNameSpace("JBCXM__TransLines__c");
        String transTypes_Obj       = baseTest.resolveStrNameSpace("JBCXM__TransactionType__c");

        String[] transHeader_ExtId  = new String[]{"TransHeader ExternalID"};
        String[] transLines_ExtId   = new String[]{"TransLines ExternalID"};
        String[] transTypes_ExtId   = new String[]{"TransTypes ExternalID"};

        try {
            cObjFields.createTextFields(transHeader_Obj, transHeader_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(transLines_Obj, transLines_ExtId, true, true, true, false, false);
            cObjFields.createTextFields(transTypes_Obj, transTypes_ExtId, true, true, true, false, false);
        } catch (Exception e) {
            Report.logInfo("*************Failed to create fields*****************");
            e.printStackTrace();
        }

    }

    /**
     * Cleans up customers, transaction setup data.
     * Updates the transaction types via script.
     * Updates & Inserts the accounts that are need for loading transactions data.
     * Inserts Order transaction Map.
     * Inserts transaction headers.
     * Upserts transaction headers with look up relation ship filled.
     * Inserts transaction line items.
     * Inserts Customer Info records
     * @throws IOException
     */
    public void LoadTransData() throws IOException {
        DataETL dataLoader = new DataETL();

        Report.logInfo("**Performing Clean up operation for Transaction Module Objects**");
        dataLoader.cleanUp("JBCXM__TransHeader__c", null);
        dataLoader.cleanUp("JBCXM__TransLines__c", null);
        dataLoader.cleanUp("JBCXM__CustomerInfo__c", null);
        dataLoader.cleanUp("JBCXM__OrderTransactionMap__c", null);

        SFDCUtil sfdcUtil = new SFDCUtil();
        sfdcUtil.runApexCodeFromFile(env.basedir+"/testdata/sfdc/transactions/scripts/TrailData_Populate_TransTypes.txt", isPackageInstance);

        Report.logInfo("**Started Loading Accounts**");
        JobInfo jobInfo1 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_Accounts.txt"), JobInfo.class);
        dataLoader.execute(jobInfo1);

        Report.logInfo("**Started Loading Order Transaction Map**");
        JobInfo jobInfo2 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_OrderTransMap.txt"), JobInfo.class);
        dataLoader.execute(jobInfo2);

        Report.logInfo("Loading First Transaction header File");
        JobInfo jobInfo3 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_TransHeader_Load1.txt"), JobInfo.class);
        dataLoader.execute(jobInfo3);

        Report.logInfo("Loading Second Transaction header File");
        JobInfo jobInfo4 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_TransHeader_Load2.txt"), JobInfo.class);
        dataLoader.execute(jobInfo4);

        Report.logInfo("Loading Transaction Lines");
        JobInfo jobInfo5 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_TransLines.txt"), JobInfo.class);
        dataLoader.execute(jobInfo5);

        Report.logInfo("Loading Customers Data");
        JobInfo jobInfo6 = mapper.readValue(new File(resDir + "jobs/Job_Trail_Trans_Customers.txt"), JobInfo.class);
        dataLoader.execute(jobInfo6);
    }
}

