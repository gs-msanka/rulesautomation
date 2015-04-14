package com.gainsight.bigdata.rulesengine;

/**
 * Created by pawel on 10/4/15.
 */
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.testng.Assert;
import org.testng.annotations.*;


import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.ResponseObj;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;

public class LoadToFeature extends NSTestBase {
    private static final String CleanupFeatures=Application.basedir+"/testdata/newstack/RulesEngine/LoadToFeature/FeatureInsertion.apex";
    private final String TEST_DATA_FILE="/testdata/newstack/RulesEngine/LoadToFeature/LoadToFeature.xls";
    ResponseObj result=null;

    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

    @BeforeClass
    public void beforeClass() throws Exception {
        sfdc.connect();
        LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
    }
    @BeforeTest
    public void cleanUp() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CleanupFeatures));
    }
   //Work In Progress
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Feature1")
    public void Feature1(HashMap<String,String> testData) throws Exception {
        RulesUtil ru=new RulesUtil();
        ru.setupRule(testData);
        String ruleId=getSFId(testData.get("JBCXM__AutomatedAlertRules__c"));
        System.out.println("request:"+PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId);
        result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +ruleId, header.getAllHeaders(),"{}");
        Log.info("Rule ID:" + ruleId+"\n Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId+"\n Request rawBody:{}");

        ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        RulesUtil.waitForCompletion(ruleId, wa, header);

        String LRR = getSFId("SFID:JBCXM__LastRunResult__c:JBCXM__AutomatedAlertRules__c:Id:"+ruleId+"");
        Assert.assertEquals("SUCCESS", LRR);


    }

    @AfterClass
    public void afterClass() {
        //GSUtil.soql = null;
    }


}
