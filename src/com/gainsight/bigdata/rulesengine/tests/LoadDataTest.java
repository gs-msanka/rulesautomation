package com.gainsight.bigdata.rulesengine.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Class which contains test methods related to loading of testdata to different datastorages
 * which can be used for manual testing/Feature testing
 * Created by Abhilash Thaduka on 1/19/2016.
 */
public class LoadDataTest {

    private static final String SFDC_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt";
    private NSTestBase nsTestBase = new NSTestBase();
    private DataETL dataETL = new DataETL();
    private ObjectMapper mapper = new ObjectMapper();
    private BaseTest baseTest;
    private static final String CUSTOM_OBJECT_CLEANUP = "Delete [SELECT Id FROM C_Custom__c];";
    private RulesConfigureAndDataSetup rulesConfigureAndDataSetup;
    //   private BaseTest baseTest;

    @BeforeClass
    public void setup() throws Exception {
        nsTestBase.init();
/*        rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
        rulesConfigureAndDataSetup.createCustomObjectAndFields();*/
    }

    @Test(description = "loads data to sfdc with all datatypes")
    public void loadDataToSfdc() throws IOException {
    	NSTestBase.sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
    	JobInfo jobInfo = mapper.readValue(nsTestBase.getNameSpaceResolvedFileContents(SFDC_JOB), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @Test(description = "loads data to Mda with all datatypes available")
    public void loadDataToMda() throws IOException {
    	NSTestBase.sfdc.runApexCode(CUSTOM_OBJECT_CLEANUP);
        JobInfo jobInfo = mapper.readValue(NSTestBase.resolveStrNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Data_Into_CustomObject.txt"), JobInfo.class);
        dataETL.execute(jobInfo);
    }
}
