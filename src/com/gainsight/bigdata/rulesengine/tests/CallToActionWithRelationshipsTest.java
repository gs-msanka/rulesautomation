package com.gainsight.bigdata.rulesengine.tests;

import java.io.FileReader;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;

import com.gainsight.testdriver.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Abhilash Thaduka on 1/18/2016.
 */
public class CallToActionWithRelationshipsTest extends BaseTest {

    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_DATA_INTO_CUSTOMOBJECT = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObjectForRelationshipCTA.txt";
    private static final String ENABLE_RELATIONSHIP = Application.basedir + "/apex_scripts/Relationships/EnableRelationship.apex";
    private static final String CREATE_RELATIONSHIP = Application.basedir + "/apex_scripts/Relationships/CreateRelationship.apex";
    private ObjectMapper mapper = new ObjectMapper();
    private RulesUtil rulesUtil = new RulesUtil();
    private String rulesManagerPageUrl;
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    private RulesManagerPage rulesManagerPage;
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;
    private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private DataETL dataETL = new DataETL();

    @BeforeClass
    public void setUp() throws Exception {
        basepage.login();
        sfdc.connect();
        nsTestBase.init();
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        rulesUtil.populateObjMaps();
        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ENABLE_RELATIONSHIP));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_RELATIONSHIP));
        JobInfo loadData = mapper.readValue((new FileReader(LOAD_DATA_INTO_CUSTOMOBJECT)), JobInfo.class);
        dataETL.execute(loadData);
    }

    @Test
    public void test1() {
        Log.info("done");

    }


}
