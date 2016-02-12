package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.FieldMapping;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToSFDCAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.DBStoreType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class LoadToSfdcTest extends BaseTest {

    private ObjectMapper mapper = new ObjectMapper();
    private NSTestBase nsTestBase = new NSTestBase();
    ReportManager reportManager = new ReportManager();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    Date date = Calendar.getInstance().getTime();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    GSDataImpl gsDataImpl = null;
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;
    RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();


    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        basepage.login();
        nsTestBase.init();
        tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.MONGO.name())) {
            Assert.assertTrue(tenantManager.disableRedShift(tenantDetails));
        } else if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.REDSHIFT.name())) {
            Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
        }
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        rulesConfigureAndDataSetup.createDataLoadConfiguration();
    }


    @Test()
    public void loadToSfdcInsertAndUpdateOperation() throws Exception {
        // Creating collection with data as source collection
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("LoadToSfdc-MDA" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();

        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult());

        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-Input.json"), RulesPojo.class);
        LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
        for (FieldMapping fields1 : loadToSFDCAction.getFieldMappings()) {
            fields1.setSourceObject(collectionName);
        }
        rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        Log.debug("Updated Pojo/json object is" + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Creating another collection with data as source collection for upsert condition
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("GS-3965-Collection2" + date.getTime());
        String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();

        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-DataloadJob.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj1.isResult());

        RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3965/GS-3965-Input.json"), RulesPojo.class);
        LoadToSFDCAction loadToSFDCAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
        for (FieldMapping fields1 : loadToSFDCAction1.getFieldMappings()) {
            fields1.setSourceObject(collectionName1);
        }
        rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction1, JsonNode.class));
        rulesPojo1.getSetupRule().setSelectObject(collectionName1);
        rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
        Log.debug("Updated Pojo/json object is " + mapper.writeValueAsString(rulesPojo1));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo1);
    }


    @Test()
    public void loadToSfdcInsertAndUpsertOperation() throws Exception {
        // Creating collection with data as source collection
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("LoadToSfdc-MDA-Upsert" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();

        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult());

        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3963/GS-3963-Input.json"), RulesPojo.class);
        LoadToSFDCAction loadToSFDCAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
        for (FieldMapping fields1 : loadToSFDCAction.getFieldMappings()) {
            fields1.setSourceObject(collectionName);
        }
        rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction, JsonNode.class));
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        Log.debug("Updated Pojo/json object is" + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Creating another collection with data as source collection for upsert condition
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("GS-3965-Collection2-Upsert" + date.getTime());
        String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();

        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-DataloadJob.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj1.isResult());

        RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3984/GS-3984-Input.json"), RulesPojo.class);
        LoadToSFDCAction loadToSFDCAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToSFDCAction.class);
        for (FieldMapping fields1 : loadToSFDCAction1.getFieldMappings()) {
            fields1.setSourceObject(collectionName1);
        }
        rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToSFDCAction1, JsonNode.class));
        rulesPojo1.getSetupRule().setSelectObject(collectionName1);
        rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
        Log.debug("Updated Pojo/json object is " + mapper.writeValueAsString(rulesPojo1));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo1);
    }
}
