package com.gainsight.bigdata.rulesengine;

import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.ExcelDataProvider;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pawel on 22/7/15.
 */
public class LoadToMilestone extends RulesUtil {
    private final String TEST_DATA_FILE = "/testdata/newstack/RulesEngine/LoadToMilestone/LoadToMilestone.xls";
    private final String LOAD_ACCOUNTS_JOB=env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Accounts.txt";
    private final String TEST_DATA_FILE1 = "/testdata/newstack/RulesEngine/LoadToCustomers/LoadToCustomers.xls";
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

    @BeforeClass
    public void beforeClass() throws Exception {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        sfdc.connect();
        metaUtil.createFieldsForAccount(sfdc, sfinfo);
        LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
        ObjectMapper mapper = new ObjectMapper();
        dataETL=new DataETL();
        JobInfo jobInfo= mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        LastRunResultFieldName = resolveStrNameSpace(LastRunResultFieldName);
        updateNSURLInAppSettings(nsConfig.getNsURl());
        List<HashMap<String, String>> testDataList = ExcelDataProvider.getDataFromExcel(Application.basedir + TEST_DATA_FILE1, "loadToCustomers1");
        if(testDataList.size()>0) {
            loadToCustomers(testDataList.get(0));
        }
        else {
            throw new RuntimeException("Do it again...");
        }
        populateObjMaps();

    }

    @TestInfo(testCaseIds = {"GS-5753"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "loadToMilestone1")
    public void loadToMilestone1(HashMap<String, String> testData) throws Exception {
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        Log.info("request:" + ApiUrls.APP_API_EVENTRULE + "/" + ruleId);
        result = wa.doPost(ApiUrls.APP_API_EVENTRULE +"/"+ ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                +ApiUrls.APP_API_EVENTRULE +"/"+ ruleId
                + "\n Request rawBody:{}");
        ResponseObject responseObj = convertToObject(result
                .getContent());
        Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
        Assert.assertNotNull(responseObj.getRequestId());
        waitForCompletion(ruleId, wa, header);
        String LRR = sfdc
                .getRecords("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
                        + RuleName + "'")[0]
                .getChild("JBCXM__LastRunResult__c").getValue().toString();
        Assert.assertEquals("SUCCESS", LRR);
        int rules1 = sfdc.getRecordCount("Select Name, Boolean_Auto1__c, Id, PickList_Auto__c, Percent_Auto__c, Date_Auto__c, DateTime_Auto__c, MultiPicklist_Auto__c From Account Where ((Name LIKE 'F%') AND ((Boolean_Auto1__c = true) OR (PickList_Auto__c IN ('Excellent','Vgood','Good','Average','Poor','Vpoor')) OR (Percent_Auto__c != 0))) AND JBCXM__CustomerInfo__c != null");
        Log.info(""+rules1);
        int rules2 = sfdc.getRecordCount("SELECT Id, Name, JBCXM__Account__c, JBCXM__Comment__c, JBCXM__CreatedDate__c, JBCXM__Customer__c, JBCXM__Date__c, JBCXM__Milestone__r.name FROM JBCXM__Milestone__c where JBCXM__Milestone__r.name='Training'");
        Log.info(""+rules2);
        Assert.assertEquals(rules1,rules2);

    }
}
