package com.gainsight.bigdata.rulesengine;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sforce.soap.metadata.*;
import com.sforce.soap.metadata.Error;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.gainsight.sfdc.util.datagen.DataETL;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class LoadToSFDC extends RulesUtil {

    private static final String PRE_CLEANUP = Application.basedir
            + "/testdata/newstack/RulesEngine/scripts/PreCleanup.apex";

    private static final String POST_CLEANUP = Application.basedir
            + "/testdata/newstack/RulesEngine/scripts/PostCleanup.apex";
    private static final String CLEANUP_PRE_TEST_RUN = Application.basedir
            + "/testdata/newstack/RulesEngine/scripts/CleanUpPreTestRun.apex";
    private static final String TEST_DATA_FILE                 =       "/testdata/newstack/RulesEngine/LoadToSFDC/LoadToSFDC.xls";
    private static final String LOAD_DATA_TO_SOURCE_JOB        =       env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_CustomSourceWeRules.txt";
    private static final String UPSERT_DATA_TO_SOURCE_JOB      =       env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Upsert_CustomWeRules.txt";
    private static final String UPDATE_DATA_TO_SOURCE_JOB      =       env.basedir+"/testdata/newstack/RulesEngine/jobs/Job_Update_CustomWeRules.txt";

    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";
    MetadataConnection metadataConnection;
    DataETL dataETL;
    ResponseObj result = null;

    @BeforeClass
    public void beforeClass() throws Exception {
        sfdc.connect();
        sfinfo = sfdc.fetchSFDCinfo();

        String[] dateFields = new String[]{"CDate1", "CDate2", "CDate3"};
        String[] dateTimeFields = new String[]{"CDateTime1", "CDateTime2", "CDateTime3"};
        String[] checkBoxFields = new String[]{"CBoolean1", "CBoolean2", "CBoolean3"};
        String[] textFields = new String[]{"CText1", "CText2", "CText3"};
        String[] externalIdFields = new String[]{"Data_ExternalId"};
        String[] textAreaFields = new String[]{"CTextArea1", "CTextArea2", "CTextArea3"};
        String[] numberFields = new String[]{"CNumber1", "CNumber2", "CNumber3"};
        String[] percentFields = new String[]{"CPercent1"};

        // Cleaning up both source and target fields and objects before re-creating.
        //Commented for now!
       /*
        String[] toDelete1 = new String[]{"CDate1__c","CDate2__c","CDate3__c","CDateTime1__c","CDateTime2__c","CDateTime3__c","CBoolean1__c","CBoolean2__c","CBoolean3__c"};
        String[] toDelete2 = new String[]{"CText1__c","CText2__c","CText3__c","CTextArea1__c","CTextArea2__c","CTextArea3__c","CNumber1__c", "CNumber2__c", "CNumber3__c","CPercent1__c"};
        String[] toDelete3 = new String[]{"cPicklist__c","cMultiPicklist__c","Account__c","Data_ExternalId__c"};

        deleteCustomObjectFields(sfdc, "CustomSourceWeRules__c", toDelete1);
        deleteCustomObjectFields(sfdc, "CustomSourceWeRules__c", toDelete2);
        deleteCustomObjectFields(sfdc, "CustomSourceWeRules__c", toDelete3);
        metadataClient.deleteCustomObject("CustomSourceWeRules__c");

        deleteCustomObjectFields(sfdc, "CustomWeRules__c", toDelete1);
        deleteCustomObjectFields(sfdc, "CustomWeRules__c", toDelete2);
        deleteCustomObjectFields(sfdc, "CustomWeRules__c", toDelete3);
        metadataClient.deleteCustomObject("CustomWeRules__c");
       */

        HashMap<String, String[]> fieldsMap = new HashMap<String, String[]>();
        fieldsMap.put("DATE", dateFields);
        fieldsMap.put("DATETIME", dateTimeFields);
        fieldsMap.put("TEXT", textFields);
        fieldsMap.put("TEXTAREA", textAreaFields);
        fieldsMap.put("NUMBER", numberFields);
        fieldsMap.put("PERCENT", percentFields);
        fieldsMap.put("BOOLEAN", checkBoxFields);

        HashMap<String, String[]> cPicklistFields = new HashMap<String, String[]>();
        cPicklistFields.put("cPicklist", new String[]{"CPL1", "CPL2", "CPL3"});

        HashMap<String, String[]> cMultiPicklistFields = new HashMap<String, String[]>();
        cMultiPicklistFields.put("cMultiPicklist", new String[]{"CMPL1", "CMPL2", "CMPL3", "CMPL4"});

        String[] customFields = ArrayUtils.add(ArrayUtils.addAll(cPicklistFields.keySet().toArray(new String[cPicklistFields.keySet().size()]), cMultiPicklistFields.keySet().toArray(new String[cMultiPicklistFields.keySet().size()])), "Account");

        // Create source object and its fields
        metadataClient.createCustomObject("CustomSourceWeRules");
        metadataClient.createMasterDetailRelationField("CustomSourceWeRules__c", "Account", "Account");
        metadataClient.createTextFields("CustomSourceWeRules__c", externalIdFields, true, true, true, false, false);
        createCustomFieldsAndAddPermissions("CustomSourceWeRules__c", fieldsMap);
        metadataClient.createPickListField("CustomSourceWeRules__c", cPicklistFields, false);
        metadataClient.createPickListField("CustomSourceWeRules__c", cMultiPicklistFields, true);
        metaUtil.addFieldPermissionsToUsers("CustomSourceWeRules__c", customFields, sfinfo);

        // Create custom object, fields and add their permissions.
        metadataClient.createCustomObject("CustomWeRules");
        createCustomFieldsAndAddPermissions("CustomWeRules__c", fieldsMap);
        metadataClient.createMasterDetailRelationField("CustomWeRules__c", "Account", "Account");
        metadataClient.createPickListField("CustomWeRules__c", cPicklistFields, false);
        metadataClient.createPickListField("CustomWeRules__c", cMultiPicklistFields, true);
        metaUtil.addFieldPermissionsToUsers("CustomWeRules__c", customFields, sfinfo);


        Map<String, Object> customObjInfo = new HashMap<String, Object>();
        customObjInfo.put("type", "SFDC");
        String fieldInfo = "[";
        for (String fldType : fieldsMap.keySet()) {
            for (String fieldName : fieldsMap.get(fldType)) {
                if ("NUMBER".equalsIgnoreCase(fldType) || "PERCENT".equalsIgnoreCase(fldType)) {
                    fldType = "DOUBLE";
                } else if (!("DATETIME".equalsIgnoreCase(fldType) || "DATE".equalsIgnoreCase(fldType) || "BOOLEAN".equalsIgnoreCase(fldType))) {
                    fldType = "STRING";
                }
                fieldInfo += "{name:" + fieldName + ",dataType:" + fldType + "},";
            }
        }
        fieldInfo += fieldInfo.substring(0, fieldInfo.length() - 1) + "]";
        customObjInfo.put("fields", fieldInfo);
        customObjInfo.put("objectLabel", "CustomWeRules Object");
        customObjInfo.put("objectName", "CustomWeRules__c");
        ObjectMapper mapper = new ObjectMapper();
        String configObjInfo = mapper.writeValueAsString(customObjInfo);
        String configData = "{\"type\":\"SFDC\",\"objectName\":\"CustomWeRules__c\",\"objectLabel\":\"CustomWeRules Object\",\"fields\":[{\"name\":\"CBoolean1__c\",\"dataType\":\"boolean\"},{\"name\":\"CBoolean2__c\",\"dataType\":\"boolean\"},{\"name\":\"CBoolean3__c\",\"dataType\":\"boolean\"},{\"name\":\"CDate1__c\",\"dataType\":\"date\"},{\"name\":\"CDate2__c\",\"dataType\":\"date\"},{\"name\":\"CDate3__c\",\"dataType\":\"date\"},{\"name\":\"CDateTime1__c\",\"dataType\":\"dateTime\"},{\"name\":\"CDateTime2__c\",\"dataType\":\"dateTime\"},{\"name\":\"CDateTime3__c\",\"dataType\":\"dateTime\"},{\"name\":\"CNumber1__c\",\"dataType\":\"double\"},{\"name\":\"CNumber2__c\",\"dataType\":\"double\"},{\"name\":\"CNumber3__c\",\"dataType\":\"double\"},{\"name\":\"CPercent1__c\",\"dataType\":\"double\"},{\"name\":\"CText1__c\",\"dataType\":\"string\"},{\"name\":\"CText2__c\",\"dataType\":\"string\"},{\"name\":\"CText3__c\",\"dataType\":\"string\"},{\"name\":\"CTextArea1__c\",\"dataType\":\"string\"},{\"name\":\"CTextArea2__c\",\"dataType\":\"string\"},{\"name\":\"CTextArea3__c\",\"dataType\":\"string\"},{\"name\":\"Id\",\"dataType\":\"string\"},{\"name\":\"Name\",\"dataType\":\"string\"},{\"name\":\"Account__c\",\"dataType\":\"string\"},{\"name\":\"cMultiPicklist__c\",\"dataType\":\"string\"},{\"name\":\"cPicklist__c\",\"dataType\":\"string\"},{\"name\":\"Data_ExternalId__c\",\"dataType\":\"string\"}]}";


        System.out.println("CustomObject config -->" + configData);
        try {
            saveCustomObjectInRulesConfig(configData);
        } catch (Exception e) {
            System.out.print("Exception " + e.getMessage());
            e.printStackTrace();
        }
        sfdc.runApexCode(getNameSpaceResolvedFileContents(PRE_CLEANUP));
        dataETL = new DataETL();
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_DATA_TO_SOURCE_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
    }

    @BeforeMethod
    public void beforeInsert() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_PRE_TEST_RUN));
    }

    @TestInfo(testCaseIds = {"GS-4694"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "InsertRule")
    public void insertOperation(HashMap<String, String> testData) throws Exception {
        // RulesUtil ru = new RulesUtil();
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        System.out.println("request:" + PropertyReader.nsAppUrl
                + "/api/eventrule/" + ruleId);
        result = wa.doPost(
                PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                + PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
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

        int trgtObjRecCount = sfdc.getRecordCount("SELECT Id FROM CustomWeRules__c where IsDeleted = false");
        int srcObjRecCount = sfdc
                .getRecordCount("SELECT Id FROM CustomSourceWeRules__c where isDeleted = false");
        Assert.assertEquals(srcObjRecCount, trgtObjRecCount);


    }

    @BeforeMethod
    public void beforeUpsert() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_PRE_TEST_RUN));
    }

    @TestInfo(testCaseIds = {"GS-4695"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UpsertRule")
    public void upsertOperation(HashMap<String, String> testData) throws Exception {
        dataETL = new DataETL();
        JobInfo jobInfo = mapper.readValue((new FileReader(UPSERT_DATA_TO_SOURCE_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        System.out.println("request:" + PropertyReader.nsAppUrl
                + "/api/eventrule/" + ruleId);
        result = wa.doPost(
                PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                + PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
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

        int srcObjRecCount = sfdc.getRecordCount("SELECT Id FROM CustomWeRules__c where IsDeleted = false");
        int trgtObjRecCount = sfdc
                .getRecordCount("SELECT Id FROM CustomSourceWeRules__c where IsDeleted = false");
        Assert.assertEquals(srcObjRecCount, trgtObjRecCount);


    }

    @BeforeMethod
    public void beforeUpdate() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_PRE_TEST_RUN));
    }

    @TestInfo(testCaseIds = {"GS-4696"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "UpdateRule")
    public void updateOperation(HashMap<String, String> testData) throws Exception {
        dataETL = new DataETL();
        JobInfo jobInfo = mapper.readValue((new FileReader(UPDATE_DATA_TO_SOURCE_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
        setupRule(testData);
        String RuleName = testData.get("Name");
        String ruleId = getRuleId(RuleName);
        System.out.println("request:" + PropertyReader.nsAppUrl
                + "/api/eventrule/" + ruleId);
        result = wa.doPost(
                PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId,
                header.getAllHeaders(), "{}");
        Log.info("Rule ID:" + ruleId + "\n Request URL"
                + PropertyReader.nsAppUrl + "/api/eventrule/" + ruleId
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

        int trgtObjRecCount  = sfdc.getRecordCount("SELECT Id FROM CustomWeRules__c where IsDeleted = false");
        int srcObjRecCount = sfdc
                .getRecordCount("SELECT Id FROM CustomSourceWeRules__c where IsDeleted = false");
        Assert.assertNotEquals(srcObjRecCount, trgtObjRecCount);
        Assert.assertEquals(true,srcObjRecCount > trgtObjRecCount);
    }





    @AfterClass
    public void afterClass() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(POST_CLEANUP));
    }
}