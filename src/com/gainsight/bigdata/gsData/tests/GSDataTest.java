package com.gainsight.bigdata.gsData.tests;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.gsData.pojos.COMMetadata;
import com.gainsight.bigdata.gsData.pojos.CollectionDependency;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.*;
import com.gainsight.util.Comparator;
import com.gainsight.utils.Verifier;
import com.gainsight.utils.annotations.TestInfo;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.apache.commons.httpclient.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Giribabu on 17/08/15.
 */
public class GSDataTest extends NSTestBase {

    GSDataImpl gsDataImpl = null;
    TenantDetails tenantDetails = null;
    String testDataFiles = testDataBasePath + "/gsData";
    final String TEST_DATA_FILE = "testdata/newstack/gsData/tests/COM_Tests.xls";

    Date date = Calendar.getInstance().getTime();
    private static DBStoreType dataBaseType = DBStoreType.MONGO;

    ReportManager reportManager = new ReportManager();
    DataETL dataETL = new DataETL();


    @Parameters("dbStoreType")
    @BeforeClass
    public void setUp(@Optional String dbStoreType) throws Exception {
        tenantManager = new TenantManager();
        dataBaseType = (dbStoreType==null || dbStoreType.isEmpty()) ? dataBaseType : DBStoreType.valueOf(dbStoreType);
        gsDataImpl = new GSDataImpl(header);
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        TenantInfo tenantInfo = gsDataImpl.getTenantInfo(sfinfo.getOrg());
        tenantDetails  =tenantManager.getTenantDetail(null, tenantInfo.getTenantId());
        if(dataBaseType.equals(DBStoreType.MONGO)) {
            if(tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
            }
        } else if(dataBaseType.equals(DBStoreType.REDSHIFT)) {
            if (!tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        }
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8098", "GS-8099", "GS-8100"})
    public void createCustomObject() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_1_" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection Info hasn't matched.");
        NsResponseObj nsResponseObj = gsDataImpl.createCustomObjectGetNsResponse(mapper.writeValueAsString(collectionInfo));
        Assert.assertEquals(nsResponseObj.getErrorCode(), MDAErrorCodes.SUBJECT_AREA_ALREADY_EXISTS.getGSCode(), "Subject name already exits error.");
        Assert.assertEquals(nsResponseObj.getErrorDesc(), "Subject Area Name Already in use please use a different name.");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7585", "GS-7587", "GS-7588", "GS-7589", "GS-7590", "GS-7586", "GS-8252"})
    public void createFieldsOnCustomObject() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_2_" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection Info hasn't matched.");

        //Adding a String field.
        String sFieldName = "Instance Id_"+date.getTime();
        CollectionInfo.Column column = CollectionUtil.createStringColumn(sFieldName);
        actualCollectionInfo.getColumns().add(column);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, sFieldName)), "Column information doesn't match, " + sFieldName);

        //Adding a Boolean field.
        String bFieldName = "Closed_"+date.getTime();
        column = CollectionUtil.createBooleanColumn(bFieldName);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, bFieldName)), "Column information doesn't match, " + bFieldName);

        //Adding a number field.
        String numberFieldName = "PageViews_"+date.getTime();
        column = CollectionUtil.createNumberColumn(numberFieldName, 0);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, numberFieldName)), "Column information doesn't match, " + numberFieldName);

        //Adding a number field with decimal places.
        String numberFieldName1 = "Average Page Load_"+date.getTime();
        column = CollectionUtil.createNumberColumn(numberFieldName1, 2);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, numberFieldName1)), "Column information doesn't match, " + numberFieldName1);

        //Adding a Date Field
        String dFieldName = "Event Date_"+date.getTime();
        column = CollectionUtil.createDateColumn(dFieldName, false);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, dFieldName)), "Column information doesn't match, " + dFieldName);

        //Adding a Date Time Field.
        String dTFieldName = "Event Time_"+date.getTime();
        column = CollectionUtil.createDateColumn(dTFieldName, true);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, dTFieldName)), "Column information doesn't match, " + dTFieldName);

        String duplicateField  = numberFieldName;
        column = CollectionUtil.createNumberColumn(numberFieldName, 3);
        actualCollectionInfo.getColumns().add(column);
        nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertFalse(nsResponseObj.isResult(), "Collection update should fail.");
        Assert.assertEquals(MDAErrorCodes.DUPLICATE_DISPLAY_NAME.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("Duplicate display name.", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7574", "GS-7593"})
    public void createCustomObjectWithHiddenFields() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_3_" + date.getTime());
        String fieldName = "Hidden_InstanceID";
        CollectionInfo.Column column = CollectionUtil.createStringColumn(fieldName);
        column.setHidden(true);
        collectionInfo.getColumns().add(column);
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection Info hasn't matched.");
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, fieldName)), "Column info didn't match, " + fieldName);

        //Adding hidden field after the object is created.
        String fieldName1 = "Hidden_PageViews";
        column = CollectionUtil.createNumberColumn(fieldName1, 3);
        column.setHidden(true);
        actualCollectionInfo.getColumns().add(column);

        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, fieldName1)), "Column info didn't match, " + fieldName1);
    }

    //This is partially covered test i.e. SFDCMappings, formula, lookup fields will be converted separately.
    @Test
    @TestInfo(testCaseIds = {"GS-8101", "GS-7604", "GS-7605"})
    public void createCustomObjectWithMoreThan100Fields() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_4_" + date.getTime());
        String[] stringFields = new String[]{"AccountSource", "BillingAddress", "Industry",
                "Parent", "Rating", "Regions", "ShippingAddress", "SicDesc", "TickerSymbol", "Type", "Website", "Account_ExternalId__c",
                "CSM_Manager__c", "CreatedByName", "CreatedById", "ModifiedByName", "ModifiedById"};
        for(String fieldName : stringFields) {
            collectionInfo.getColumns().add(CollectionUtil.createStringColumn(fieldName));
        }
        String[] dateFields = new String[]{"Week", "Month", "Year", "SLAExpirationDate__c"};
        for(String fieldName : dateFields) {
            collectionInfo.getColumns().add(CollectionUtil.createDateColumn(fieldName, false));
        }
        String[] dateTimeFields = new String[]{"CreatedDateTime", "ModifiedDateTime", "EventDateTime", "LastReferencedDate", "LastActivityDate"};
        for(String fieldName : dateTimeFields) {
            collectionInfo.getColumns().add(CollectionUtil.createDateColumn(fieldName, true));
        }

        String[] booleanField = new String[] {"Deleted", "Closed-Won", "isValid"};
        for(String fieldName : booleanField) {
            collectionInfo.getColumns().add(CollectionUtil.createBooleanColumn(fieldName));
        }

        String[] numberFields = new String[] {"# Page Views", "% of Users", "$Amount", "~ OutReaches Triggered", "Active User's",
                "Double's User's","Double\"s Measure\"s", "Measure\"s", "Files Downloaded", "Page Views", "Page Visits", "Report's Run",
                "Rule's Run", "Schedule's Created", "Annual Revenue", "ARR", "MRR", "Average Revenue / User"};

        for(String fieldName : numberFields) {
            collectionInfo.getColumns().add(CollectionUtil.createNumberColumn(fieldName, (int) ((Math.random()*10)/2)));
        }
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection Info hasn't matched.");

        List<String> measureFields = new ArrayList<>();
        for(int i=1; i<= 70; i++) {
            String measureName = "Measure - "+i;
            measureFields.add(measureName);
        }

        List<CollectionInfo.Column> columnList = new ArrayList<>();
        for(String fieldName : measureFields) {
            CollectionInfo.Column column = CollectionUtil.createNumberColumn(fieldName, (int) ((Math.random()*10)/2));
            actualCollectionInfo.getColumns().add(column);
            columnList.add(column);
        }

        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);

        Map<String, CollectionInfo.Column> columnMap = CollectionUtil.getDisplayNameColumnsMap(actualCollectionInfo);
        for(CollectionInfo.Column column : columnList) {
            Assert.assertTrue(columnMap.containsKey(column.getDisplayName()), column.getDisplayName()+" - Column/Field doesn't exists in the collection.");
            Assert.assertTrue(CollectionUtil.verifyColumn(column, columnMap.get(column.getDisplayName())), "Column info didn't match, " + column.getDisplayName());
        }
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7591", "GS-8102", "GS-7573", "GS-7578", "GS-7600"})
    public void createFieldsWithSFDCMapping() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t1/T1CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_5_" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        if(dataBaseType.equals(DBStoreType.MONGO)) {
            CollectionUtil.getColumnByDisplayName(collectionInfo, "Account Id").setIndexed(true);  //If object is created on mongo then field that's mapped to SFDC_ACCOUNT will have index created automatically.
        }

        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection information not matched.");

        List<CollectionInfo.Column> columnList = mapper.readValue(new File(testDataFiles + "/tests/t1/T1Columns.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {});
        actualCollectionInfo.getColumns().addAll(columnList);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);

        Verifier verifier = new Verifier();
        for(CollectionInfo.Column column : columnList) {
            verifier.verifyTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, column.getDisplayName())), column.getDisplayName()+" Properties Not Matched.");
        }
        verifier.assertVerification();
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7594", "GS-8128", "GS-8129", "GS-8130", "GS-8131", "GS-7570"})
    public void createFormulaFieldsOnMongo() throws Exception {
        if(dataBaseType != DBStoreType.MONGO) {
            throw new SkipException("This test case can only run on when MONGO is the data store.");
        }
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_6_" + date.getTime());
        CollectionUtil.getColumnByDisplayName(collectionInfo, "FilesDownloaded").setDecimalPlaces(0); //Settings decimal places to zero.
        CollectionUtil.getColumnByDisplayName(collectionInfo, "NoofReportsRun").setDecimalPlaces(0);  //Settings decimal places to zero.

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        collectionInfo.getCollectionDetails().setDataStoreType(DBStoreType.MONGO.name());
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info details not matched.");

        List<CollectionInfo.Column> columnList = mapper.readValue(new File(testDataFiles + "/tests/t2/T2Columns.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });
        actualCollectionInfo.getColumns().addAll(columnList);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);

        Verifier verifier = new Verifier();
        for(CollectionInfo.Column column : columnList) {
            verifier.verifyTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, column.getDisplayName())), column.getDisplayName()+" Properties Not Matched.");
        }
        verifier.assertVerification();

        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountId", "FilesDownloaded", "NoofReportsRun", "Description"}, DataLoadOperationType.INSERT);
        File file = new File(testDataFiles+"/tests/t2/T2CollectionData.csv");
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), file), "Data Validation Failed.");

        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), file);
        Assert.assertTrue(nsResponseObj1.isResult(), "Data load Response should not be false");

        File expectedFile = new File(testDataFiles+"/tests/t2/T2ExpectedData.csv");
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo, new String[]{"AccountId", "Description", "FilesDownloaded", "NoofReportsRun", "ADD", "SUBSTRACT", "MULTIPLE", "DIVIDE"})), actualCollectionInfo);
        List<Map<String, String>> expectedData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(expectedFile))), actualCollectionInfo);
        Log.debug(mapper.writeValueAsString(actualData));
        Log.debug(mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.debug(mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size(), "No of Un Matched Records should be 0");

        COMMetadata comMetadata = GSDataImpl.getSimpleFilterMetadata("CollectionDetails.CollectionID", "string", new String[]{collectionId}, new String[]{"CollectionDetails", "Columns"});
        List<HashMap<String, Object>> collectionDetails = gsDataImpl.getCustomObjectsDetails(mapper.writeValueAsString(comMetadata));
        Assert.assertEquals(1, collectionDetails.size(), "One Record should be returned.");
        Assert.assertEquals(20, collectionDetails.get(0).get("TotalRecordCount"));
        Assert.assertEquals("false", collectionDetails.get(0).get("isAllowedInMDAJoin").toString(), "MDA Joins allowed should be true.");

    }

    @Test
    @TestInfo(testCaseIds = {"GS-7595", "GS-8113", "GS-8114", "GS-8115", "GS-8116", "GS-7620", "GS-7571", "GS-7578"})
    public void createFormulaFieldsOnRedshift() throws Exception {
        if(dataBaseType != DBStoreType.REDSHIFT) {
          throw new SkipException("This test case can only run when REDSHIFT is enabled / databaseType = RedShift.");
        }
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_7_" + date.getTime());
        CollectionUtil.getColumnByDisplayName(collectionInfo, "FilesDownloaded").setDecimalPlaces(0); //Settings decimal places to zero.
        CollectionUtil.getColumnByDisplayName(collectionInfo, "NoofReportsRun").setDecimalPlaces(0);  //Settings decimal places to zero.

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        collectionInfo.getCollectionDetails().setDataStoreType(DBStoreType.REDSHIFT.name());
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info details not matched.");

        List<CollectionInfo.Column> columnList = mapper.readValue(new File(testDataFiles + "/tests/t3/T3Columns.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {
        });

        actualCollectionInfo.getColumns().addAll(columnList);
        CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo, null);
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);

        Verifier verifier = new Verifier();
        for(CollectionInfo.Column column : columnList) {
            verifier.verifyTrue(CollectionUtil.verifyColumn(column, CollectionUtil.getColumnByDisplayName(actualCollectionInfo, column.getDisplayName())), column.getDisplayName()+" Properties Not Matched.");
        }
        verifier.assertVerification();

        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountId", "FilesDownloaded", "NoofReportsRun", "Description"}, DataLoadOperationType.INSERT);
        File file = new File(testDataFiles+"/tests/t3/T3CollectionData.csv");
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), file), "Data Validation Failed.");

        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), file);
        Assert.assertTrue(nsResponseObj1.isResult(), "Data load Response should not be false");

        File expectedFile = new File(testDataFiles+"/tests/t3/T3ExpectedData.csv");
        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createTabularReport(actualCollectionInfo, new String[]{"AccountId", "Description", "FilesDownloaded", "NoofReportsRun", "ADD", "SUBSTRACT", "MULTIPLE", "DIVIDE"})), actualCollectionInfo);
        List<Map<String, String>> expectedData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(expectedFile))), actualCollectionInfo);
        Log.debug(mapper.writeValueAsString(actualData));
        Log.debug(mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.debug(mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size(), "No of Un Matched Records should be 0");

        COMMetadata comMetadata = GSDataImpl.getSimpleFilterMetadata("CollectionDetails.CollectionID", "string", new String[]{collectionId}, new String[]{"CollectionDetails", "Columns"});
        List<HashMap<String, Object>> collectionDetails = gsDataImpl.getCustomObjectsDetails(mapper.writeValueAsString(comMetadata));
        Assert.assertEquals(1, collectionDetails.size(), "One Record should be returned.");
        Assert.assertEquals(20, collectionDetails.get(0).get("TotalRecordCount"));
        Assert.assertEquals("true", collectionDetails.get(0).get("isAllowedInMDAJoin").toString(), "MDA Joins allowed should be true.");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7598"})
    public void createLookupFieldsOnRedshift() throws Exception {
        if(dataBaseType != DBStoreType.REDSHIFT) {
            new SkipException("Can't be executed on redshift.");
        }
        CollectionInfo account = mapper.readValue(new File(testDataFiles + "/tests/t4/T4Account.json"), CollectionInfo.class);
        account.getCollectionDetails().setCollectionName("GSAccount_" + date.getTime());
        CollectionInfo user = mapper.readValue(new File(testDataFiles + "/tests/t4/T4User.json"), CollectionInfo.class);
        user.getCollectionDetails().setCollectionName("GSUser_" + date.getTime());
        CollectionInfo usageData = mapper.readValue(new File(testDataFiles + "/tests/t4/T4UsageData.json"), CollectionInfo.class);
        usageData.getCollectionDetails().setCollectionName("GSUsageData_" + date.getTime());

        String userCollectionId = gsDataImpl.createCustomObject(user);
        Assert.assertNotNull(userCollectionId, "User Collection Id should not be null.");
        CollectionInfo actualUserCollection = gsDataImpl.getCollectionMaster(userCollectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(user, actualUserCollection), "User CollectionInfo didn't match.");

        //Setting up the required lookup details on account collection.
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(account, "User Id"), actualUserCollection, "Id");
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(account, "User Name"), actualUserCollection, "Name");

        String accCollectionId = gsDataImpl.createCustomObject(account);
        Assert.assertNotNull(accCollectionId, "Collection Id should not be null.");
        CollectionInfo actualAccountCollection = gsDataImpl.getCollectionMaster(accCollectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(account, actualAccountCollection), "Account CollectionInfo didn't match.");

        //Setting up the required lookup details on usage data collection.
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(usageData, "Account Id"), actualAccountCollection, "Id");
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(usageData, "Account Name"), actualAccountCollection, "Name");
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(usageData, "User Id"), actualUserCollection, "Id");
        CollectionUtil.setLookUpDetails(CollectionUtil.getColumnByDisplayName(usageData, "User Name"), actualUserCollection, "Name");

        String usageCollectionId = gsDataImpl.createCustomObject(usageData);
        Assert.assertNotNull(usageCollectionId, "Collection Id should not be null.");
        CollectionInfo actualUsageCollection = gsDataImpl.getCollectionMaster(usageCollectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(usageData, actualUsageCollection), "Usage Data CollectionInfo didn't match.");

        //Now update the User Object base field display name.
        CollectionUtil.getColumnByDisplayName(actualUserCollection, "Id").setDisplayName("Id - Changed");
        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualUserCollection));
        Assert.assertTrue(nsResponseObj.isResult(), "User Object Update Failed.");

        //Now update the Account object base field display name.
        CollectionUtil.getColumnByDisplayName(actualAccountCollection, "Name").setDisplayName("Account Name - Changed.");
        //Changing the user object display name also.
        actualAccountCollection.getCollectionDetails().setCollectionName(actualAccountCollection.getCollectionDetails().getCollectionName() + "-Changed");
        NsResponseObj nsResponseObj1 = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualAccountCollection));
        Assert.assertTrue(nsResponseObj1.isResult(), "Account Object Update Failed.");

        //Updating the expected UsageData object "Account Name" field with the proper look up details.
        CollectionInfo.Column usageObjCol1 = CollectionUtil.getColumnByDisplayName(actualUsageCollection, "Account Name");
        usageObjCol1.getLookupDetail().setColumnDisplayName("Account Name - Changed.");
        usageObjCol1.getLookupDetail().setCollectionName(actualAccountCollection.getCollectionDetails().getCollectionName());
        CollectionUtil.getColumnByDisplayName(actualUsageCollection, "Account Id").getLookupDetail().setCollectionName(actualAccountCollection.getCollectionDetails().getCollectionName());

        //Updating the expected UsageData object "User Id" field with the proper look up details.
        CollectionUtil.getColumnByDisplayName(actualUsageCollection, "User Id").getLookupDetail().setColumnDisplayName("Id - Changed");


        //Fetch the new details for usage data object.
        CollectionInfo usageCollection = gsDataImpl.getCollectionMaster(usageCollectionId);
        Log.info("Checking the final update usage data collection info...");
        System.out.println(mapper.writeValueAsString(actualUsageCollection));
        System.out.println(mapper.writeValueAsString(usageCollection));
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(actualUsageCollection, usageCollection));
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7621"})
    public void verifyCURL() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_8_" + date.getTime());
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection Id should not null.");

        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        String cUrl = gsDataImpl.getCURL(collectionId);
        Assert.assertNotNull(cUrl);
        //TODO - Need to implement best way to assert the CURL generated.
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7625"})
    public void verifyDuplicateHeaderInCSVFileHeader() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_9_" + date.getTime());
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        NsResponseObj nsResponseObj = gsDataImpl.getDataLoadMappingNsResponse(mapper.writeValueAsString(actualCollectionInfo), new File(testDataFiles + "/tests/t5/T5CollectionData.csv"));
        Assert.assertFalse(nsResponseObj.isResult(), "Server should return false.");
        Assert.assertEquals(MDAErrorCodes.DUPLICATE_HEADERS.getGSCode(), nsResponseObj.getErrorCode(), "Error code not matched.");
        Assert.assertEquals("Duplicate headers are not allowed", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7624"})
    public void sameCSVHeaderMappedToTwoDifferentColumns() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_10_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        HashMap<String, String> displayDBMap = CollectionUtil.getDisplayAndDBNamesMap(actualCollectionInfo);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountId", "AccountName", "Active", "Date", "PageViews"}, DataLoadOperationType.INSERT);

        String field1 = "AccountId";
        String field2 = "AccountName";
        for(DataLoadMetadata.Mapping mapping : metadata.getMappings()) {
            if(mapping.getSource().equals(field2)) {
                mapping.setTarget(displayDBMap.get(field1));
                break;
            }
        }

        ResponseObj responseObj = gsDataImpl.sendDataForValidation(mapper.writeValueAsString(metadata), new File(testDataFiles + "/tests/t6/T6Collection.csv"));
        Log.info("Response Obj : " +responseObj.toString());
        Assert.assertEquals(responseObj.getStatusCode(),HttpStatus.SC_BAD_REQUEST);

        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult(), " Result should be false.");
        Assert.assertEquals(MDAErrorCodes.INVALID_MAPPING.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("Invalid mapping info.", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7626"})
    public void verifyMoreThanOneMBFileError() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());

        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_11_" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        ResponseObj responseObj = gsDataImpl.sendDataForValidation(mapper.writeValueAsString(CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT)), new File(testDataFiles + "/tests/t7/T7CollectionData.csv"));
        Log.info("Response Obj : " + responseObj.toString());
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, responseObj.getStatusCode());
        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(MDAErrorCodes.FILE_SIZE_EXCEEDED_ONE_MB.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("Input file exceeded max file size of 1Mb", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7628"})
    public void dataLoadToFormulaFieldShouldNotBeAllowed() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_12_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        if(dataBaseType.equals(DBStoreType.REDSHIFT))  {
            List<CollectionInfo.Column> columnList = mapper.readValue(new File(testDataFiles + "/tests/t8/T8RedShiftFormulaFields.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {
            });
            actualCollectionInfo.getColumns().addAll(columnList);
            CollectionUtil.tokenizeCalculatedExpression(actualCollectionInfo);
        } else if(dataBaseType.equals(DBStoreType.MONGO)) {
            List<CollectionInfo.Column> columnList = mapper.readValue(new File(testDataFiles + "/tests/t8/T8MongoFormulaFields.json"), new TypeReference<ArrayList<CollectionInfo.Column>>() {
            });
            actualCollectionInfo.getColumns().addAll(columnList);
        }

        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Collection update failed");
        Assert.assertEquals("Update CollectionMaster successful", nsResponseObj.getData());
        actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);

        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountName", "ADD", "PageViews", "PageVisits", "FilesDownloaded", "NoofReportsRun"}, DataLoadOperationType.INSERT);
        ResponseObj responseObj = gsDataImpl.sendDataForValidation(mapper.writeValueAsString(metadata), new File(testDataFiles + "/tests/t8/T8CollectionData.csv"));
        Log.info("Response Obj : " + responseObj.getContent());
        Assert.assertEquals(responseObj.getStatusCode(),HttpStatus.SC_BAD_REQUEST);

        NsResponseObj nsResponseObj1 = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj1.isResult());
        Assert.assertEquals(MDAErrorCodes.FORMULA_FIELD_SHOULD_NOT_BE_MAPPED.getGSCode(), nsResponseObj1.getErrorCode());
        Assert.assertEquals("Formula Field cannot be mapped", nsResponseObj1.getErrorDesc());
    }


    @Test
    @TestInfo(testCaseIds = {"GS-7649", "GS-7648", "GS-8119", "GS-8120"})
    public void verifyDependentReportsAndRules() throws Exception {
        //TODO - Rules need to be implemented.
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_13_" + date.getTime());

        //If SFDC account id map is not set, then collection will not be shown up in rules.
        String mapping = "{\"SFDC\":{\"dataType\":\"string\",\"key\":\"SFDC_ACCOUNT\"}}";
        CollectionInfo.MappingsSFDC mappingsSFDC = mapper.readValue(mapping, CollectionInfo.MappingsSFDC.class);
        CollectionInfo.Column c = CollectionUtil.getColumnByDisplayName(collectionInfo, "AccountId");
        c.setMappings(mappingsSFDC);
        c.setIndexed(dataBaseType.MONGO == DBStoreType.MONGO ? true : false);

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        //Creating a report & building its dependency.
        String reportName = "REPORT CREATED FROM COM TO TEST DEPENDENCY - 1 -"+date.getTime();
        ReportMaster reportMaster = reportManager.createTabularReportMaster(actualCollectionInfo, null);
        reportMaster.getReportInfo().get(0).setReportName(reportName);
        String reportId  = reportManager.saveReport(mapper.writeValueAsString(reportMaster));
        Assert.assertNotNull(reportId);
        CollectionDependency collectionDependency = mapper.readValue("{\"entityType\":\"REPORT\", \"entityId\": \"" + reportId + "\", \"entityName\": \"" + reportName + "\"}", CollectionDependency.class);

        //Creating one more report & building its dependency.
        String reportName1 = "REPORT CREATED FROM COM TO TEST DEPENDENCY - 2 -"+date.getTime();
        ReportMaster reportMaster1 = reportManager.createTabularReportMaster(actualCollectionInfo, null);
        reportMaster1.getReportInfo().get(0).setReportName(reportName1);
        String reportId1  = reportManager.saveReport(mapper.writeValueAsString(reportMaster1));
        Assert.assertNotNull(reportId1);
        CollectionDependency collectionDependency1 = mapper.readValue("{\"entityType\":\"REPORT\", \"entityId\": \"" + reportId1 + "\", \"entityName\": \"" + reportName1 + "\"}", CollectionDependency.class);

        List<CollectionDependency> dependencies = gsDataImpl.getCollectionMasterReferences(collectionId);
        Assert.assertNotNull(dependencies, "Failed to get Dependencies.");
        Assert.assertTrue(CollectionUtil.isDependencyExists(dependencies, collectionDependency), "Collection Dependency Not Found.");
        Assert.assertTrue(CollectionUtil.isDependencyExists(dependencies, collectionDependency1), "Collection Dependency Not Found.");

        Assert.assertTrue(reportManager.deleteReport(reportId), "Delete of report failed.");
        List<CollectionDependency> dependencies1 = gsDataImpl.getCollectionMasterReferences(collectionId);
        Assert.assertNotNull(dependencies1, "Failed to get Dependencies.");
        Assert.assertFalse(CollectionUtil.isDependencyExists(dependencies1, collectionDependency), "Collection Dependency Should not be Found.");
        Assert.assertTrue(CollectionUtil.isDependencyExists(dependencies1, collectionDependency1), "Collection Dependency Not Found.");
    }


    @Test
    @TestInfo(testCaseIds = {"GS-8277"})
    public void verifyAutoInferMapping() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_14_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        ResponseObj responseObj = gsDataImpl.getDataLoadMapping(mapper.writeValueAsString(actualCollectionInfo), new File(testDataFiles + "/tests/t9/T9CollectionData.csv"));
        Assert.assertEquals(HttpStatus.SC_OK, responseObj.getStatusCode());
        JsonNode responseData = mapper.readTree(responseObj.getContent());
        JsonNode data = responseData.get("data");
        Assert.assertNotNull(data, "Data should not found in reponse");

        HashMap<String, String> expDisplayDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(actualCollectionInfo);

        Set<String> actualCSVColumns = mapper.convertValue(data.get("csvColumns"), HashSet.class);
        Set<String> actualCollectionMasterColumns = mapper.convertValue(data.get("collectionMasterColumns"), HashSet.class);
        HashMap<String, String> actualDisplayDBNamesMap = mapper.convertValue(data.get("displayNameToDbNameMap"), HashMap.class);
        String actualAutoInferJson = data.get("autoInferFieldList").toString();

        Assert.assertNotNull(actualCSVColumns, "CSV Columns Key Not found");
        Assert.assertNotNull(actualCollectionMasterColumns, "Collection Master columns key not found in data.");
        Assert.assertNotNull(actualDisplayDBNamesMap, "Display DB Name Map not found in data.");
        Assert.assertNotNull(actualAutoInferJson, "Acutal Auto Infer Key not found in data.");

        String[] expectedCSVColumns = new String[]{"AccountId", "AccountName", "Description", "Active", "eventtimestamp", "Date", "PageViews", "PageVisits"};
        Assert.assertTrue(actualCSVColumns.containsAll(Arrays.asList(expectedCSVColumns)), "CSV Header's not matched.");
        Assert.assertTrue(actualCollectionMasterColumns.containsAll(expDisplayDBNamesMap.keySet()));
        Assert.assertTrue(expDisplayDBNamesMap.equals(actualDisplayDBNamesMap), "Display and DB Names not Matched.");

        String expectedAutoInferJson = "[{\"source\":\"AccountId\",\"target\":\"AccountId\",\"sourceFieldType\":\"field\"},{\"source\":\"AccountName\",\"target\":\"AccountName\",\"sourceFieldType\":\"field\"},{\"source\":\"Description\",\"target\":\"Description\",\"sourceFieldType\":\"field\"},{\"source\":\"Active\",\"target\":\"Active\",\"sourceFieldType\":\"field\"},{\"source\":\"Date\",\"target\":\"Date\",\"sourceFieldType\":\"field\"},{\"source\":\"EventTimeStamp\",\"target\":\"eventtimestamp\",\"sourceFieldType\":\"field\"},{\"source\":\"PageViews\",\"target\":\"PageViews\",\"sourceFieldType\":\"field\"},{\"source\":\"PageVisits\",\"target\":\"PageVisits\",\"sourceFieldType\":\"field\"}]";
        JsonFluentAssert.assertThatJson(actualAutoInferJson).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expectedAutoInferJson);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7622", "GS-7635", "GS-7641", "GS-7642", "GS-7643", "GS-7644", "GS-7645", "GS-8096", "GS-8097", "GS-8120"})
    public void loadDataToMDA() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_15_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        JobInfo jobInfo = mapper.readValue(new File(testDataFiles + "/tests/t10/T10DateProcess.json"), JobInfo.class);
        dataETL.execute(jobInfo);

        File file = new File(Application.basedir+"/"+jobInfo.getDateProcess().getOutputFile());
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), file));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), file);
        Assert.assertTrue(nsResponseObj.isResult());

        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo)), actualCollectionInfo);
        List<Map<String, String>> expectedData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(file))), actualCollectionInfo);
        Log.debug(mapper.writeValueAsString(actualData));
        Log.debug(mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.debug(mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size(), "No of Un Matched Records should be 0");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7623", "GS-7629", "GS-7630", "GS-7631", "GS-7632", "GS-7633", "GS-7634", "GS-8118"})
    public void failedRecordsVerification() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_16_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"AccountId", "AccountName", "Active", "Date", "EventTimeStamp", "PageViews", "Description"}, DataLoadOperationType.INSERT);
        File file = new File(testDataFiles+"/tests/t11/T11CollectionData.csv");
        File resultFile = new File(testDataFiles+"/process/t11/T11FailedRecords.csv");
        resultFile.getParentFile().mkdirs();
        gsDataImpl.validateDataAndDownloadFailures(mapper.writeValueAsString(metadata), file, resultFile);

        List<Map<String, String>> actualData = Comparator.getParsedCsvData(new CSVReader(new FileReader(resultFile)));
        List<Map<String, String>> expectedData = Comparator.getParsedCsvData(new CSVReader(new FileReader(testDataFiles + "/tests/t11/T11EXPFailedRecords.csv")));
        Log.debug("Actual Data : " +mapper.writeValueAsString(actualData));
        Log.debug("Expected Data : " + mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.error("Diff : " + mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size(), "No of diff records should be zero.");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7639"})
    public void csvHavingBlankHeader() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_17_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        NsResponseObj nsResponseObj = gsDataImpl.getDataLoadMappingNsResponse(mapper.writeValueAsString(actualCollectionInfo), new File(testDataFiles + "/tests/t12/T12CollectionData.csv"));
        Assert.assertFalse(nsResponseObj.isResult(), "NS Result should be null.");
        Assert.assertEquals(MDAErrorCodes.NO_HEADERS.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("The input CSV file does not contain headers, please add headers.", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7592"})
    public void verifyDisplayNameMandatoryWhileCreatingField() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        CollectionUtil.getColumnByDisplayName(collectionInfo, "AccountId").setDisplayName(""); //Removing display name.
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_18_" + date.getTime());

        NsResponseObj nsResponseObj = gsDataImpl.createCustomObjectGetNsResponse(mapper.writeValueAsString(collectionInfo));
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(MDAErrorCodes.COLUMN_DISPLAY_NAME_NULL.getGSCode(), nsResponseObj.getErrorCode());
        Assert.assertEquals("Display name of Column cannot be empty", nsResponseObj.getErrorDesc());
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7602"})
    public void changeOfDisplayNamesOfFields() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/global/CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_19_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        HashMap<String, CollectionInfo.Column> columnHashMap = CollectionUtil.getDisplayNameColumnsMap(actualCollectionInfo);

        //Change the display of String field - Account Name
        columnHashMap.get("AccountName").setDisplayName("Account Name - Changed");
        //Change the display of Boolean field - Active
        columnHashMap.get("Active").setDisplayName("Is Customer");
        //Change the display of Date field - Date
        columnHashMap.get("Date").setDisplayName("ActivedDate");
        //Change the display of Number field - PageVisits
        columnHashMap.get("PageVisits").setDisplayName("PageVisits - Changed");

        NsResponseObj nsResponseObj = gsDataImpl.updateCustomObjectGetNsResponse(mapper.writeValueAsString(actualCollectionInfo));
        Assert.assertTrue(nsResponseObj.isResult(), "Update of collection failed.");

        CollectionInfo latestCollectionMaster = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(actualCollectionInfo, latestCollectionMaster));
    }

    @Test
    @TestInfo(testCaseIds = {"GS-7636"})
    public void verifyAllSupportedDateFormat() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue(new File(testDataFiles + "/tests/t13/T13CollectionInfo.json"), CollectionInfo.class);
        //To Run on  different DB's - MONGO / REDSHIFT
        collectionInfo.getCollectionDetails().setDataStoreType(dataBaseType.name());
        collectionInfo.getCollectionDetails().setCollectionName("GS_COM_20_" + date.getTime());

        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection creation failed.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        Assert.assertTrue(CollectionUtil.verifyCollectionInfo(collectionInfo, actualCollectionInfo), "Collection info hasn't matched.");

        File loadFile = new File(testDataFiles+"/tests/t13/T13CollectionData.csv");
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), loadFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), loadFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Data Load Failed.");

        List<Map<String, String>> actualData = ReportManager.getProcessedReportData(reportManager.runReportLinksAndGetData(reportManager.createDynamicTabularReport(actualCollectionInfo)), actualCollectionInfo);
        List<Map<String, String>> expectedData = ReportManager.populateDefaultBooleanValue(com.gainsight.util.Comparator.getParsedCsvData(new CSVReader(new FileReader(testDataFiles+"/tests/t13/T13ExpectedData.csv"))), actualCollectionInfo);
        Log.debug(mapper.writeValueAsString(actualData));
        Log.debug(mapper.writeValueAsString(expectedData));

        List<Map<String, String>> diffData = Comparator.compareListData(expectedData, actualData);
        Log.debug(mapper.writeValueAsString(diffData));
        Assert.assertEquals(0, diffData.size(), "No of Un Matched Records should be 0");
    }


    //TODO - In order to verify list of custom object, no of records, filter on custom objects with different values COM Landing screen.
    //@Test - WIP
    public void createCustomObjects() throws IOException {
        String a = "Ut Quam Company\n" +
                "Sit Industries\n" +
                "Eu Limited\n" +
                "Sed Molestie Sed Associates\n" +
                "Lacus Corporation\n" +
                "Sodales Associates\n" +
                "Ut Incorporated\n" +
                "Magna Nec Company\n" +
                "Primis In Faucibus Corp.\n" +
                "Sed Malesuada Incorporated\n" +
                "Morbi Accumsan Laoreet Foundation\n" +
                "Varius Et Euismod PC\n" +
                "Nec Metus Consulting\n" +
                "Consectetuer Ipsum Limited\n" +
                "Est LLC\n" +
                "Vulputate Nisi PC";
        String collectionMaster = "./testdata/newstack/connectors/dataApi/data/CollectionInfo.json";
        CollectionInfo collectionInfo = mapper.readValue(new File(collectionMaster), CollectionInfo.class);
        for(int i=0 ; i < a.split("\n").length ; i++) {
            System.out.println(a.split("\n")[i]);
            collectionInfo.getCollectionDetails().setCollectionName(a.split("\n")[i] + i + 1);

        }
    }
}
