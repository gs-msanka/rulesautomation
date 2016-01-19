package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;

/**
 * Class which contains test methods related to loading of testdata to different datastorages
 * which can be used for manual testing/Feature testing
 * Created by Abhilash Thaduka on 1/19/2016.
 */
public class LoadDataTest {


    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String CLEANUP_SCRIPT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private NSTestBase nsTestBase = new NSTestBase();
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private String rulesManagerPageUrl;
    private RulesManagerPage rulesManagerPage;
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;
    //   private BaseTest baseTest;

    @BeforeClass
    public void setup() throws Exception {
        // nsTestBase.sfdc.connect();
        nsTestBase.init();
        //    baseTest=new BaseTest();

        nsTestBase.sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
        nsTestBase.sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        JobInfo jobInfo = mapper.readValue(NSTestBase.resolveStrNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
    }
}
