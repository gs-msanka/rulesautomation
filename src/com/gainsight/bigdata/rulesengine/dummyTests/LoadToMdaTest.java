package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.FieldMapping;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMDACollection;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by Abhilash Thaduka on 2/4/2016.
 */
public class LoadToMdaTest extends BaseTest {

    private ObjectMapper mapper = new ObjectMapper();
    private NSTestBase nsTestBase = new NSTestBase();
    private RulesUtil rulesUtil = new RulesUtil();
    ReportManager reportManager = new ReportManager();
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    Date date = Calendar.getInstance().getTime();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    GSDataImpl gsDataImpl = null;
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;


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
    }

    @Test
    public void loadToMdaInsertAndUpdateScenarioUsingMdaData() throws Exception {

        // Creating collection with data as source collection
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS-3977-MDA" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();

        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult());

        // Creating collection with no data for destination
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo2.getCollectionDetails().setCollectionName("GS-3977EmptyCollection-" + date.getTime());
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        // Setting collection permisssion in ruleslodable object
        LoadToMDACollection loadToMDACollection = new LoadToMDACollection();
        loadToMDACollection.setType("MDA");
        loadToMDACollection.setObjectName(actualCollectionInfo2.getCollectionDetails().getCollectionId());
        loadToMDACollection.setObjectLabel(actualCollectionInfo2.getCollectionDetails().getCollectionName());
        List<LoadToMDACollection.Field> fields = new ArrayList<>();
        LoadToMDACollection.Field field = null;
        for (CollectionInfo.Column column : actualCollectionInfo2.getColumns()) {
            field = new LoadToMDACollection.Field();
            field.setDataType(column.getDatatype().toUpperCase());
            field.setName(column.getDbName());
            fields.add(field);
        }
        loadToMDACollection.setFields(fields);
        rulesUtil.saveCustomObjectInRulesConfig(mapper.writeValueAsString(loadToMDACollection));

        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-Input.json"), RulesPojo.class);
        LoadToMDAAction loadToMDAAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
        loadToMDAAction.setObjectName(collectionName2);
        for (FieldMapping fields1 : loadToMDAAction.getFieldMappings()) {
            fields1.setSourceObject(collectionName);
        }
        rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction, JsonNode.class));
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        String demo = mapper.writeValueAsString(rulesPojo);
        Log.debug("Updated Pojo is" + demo);

        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Creating another collection with data as source collection for upsert condition
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("GS-3978-MDA" + date.getTime());
        String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();

        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-DataloadJob.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj1.isResult());

        RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3978-MdaData/GS-3978-Input.json"), RulesPojo.class);
        LoadToMDAAction loadToMDAAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
        loadToMDAAction1.setObjectName(collectionName2);
        for (FieldMapping fields1 : loadToMDAAction1.getFieldMappings()) {
            fields1.setSourceObject(collectionName1);
        }
        rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction1, JsonNode.class));
        rulesPojo1.getSetupRule().setSelectObject(collectionName1);
        rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
        String demo1 = mapper.writeValueAsString(rulesPojo1);
        Log.debug("Updated Pojo is" + demo1);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo1);
    }

    @Test
    public void loadToMdaUpsertScenarioUsingMdaData() throws Exception {

        // Creating collection with data as source collection
        CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName("GS-3979-MDA-3979" + date.getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        String collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-DataloadJob.txt")), JobInfo.class);
        File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile));
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult());

        // Creating collection with no data for destination
        CollectionInfo collectionInfo2 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo2.getCollectionDetails().setCollectionName("GS-3979EmptyCollection-3979" + date.getTime());
        String collectionId2 = gsDataImpl.createCustomObject(collectionInfo2);
        Assert.assertNotNull(collectionId2, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo2 = gsDataImpl.getCollectionMaster(collectionId2);
        String collectionName2 = actualCollectionInfo2.getCollectionDetails().getCollectionName();
        // Setting collection permisssion in ruleslodable object
        LoadToMDACollection loadToMDACollection = new LoadToMDACollection();
        loadToMDACollection.setType("MDA");
        loadToMDACollection.setObjectName(actualCollectionInfo2.getCollectionDetails().getCollectionId());
        loadToMDACollection.setObjectLabel(actualCollectionInfo2.getCollectionDetails().getCollectionName());
        List<LoadToMDACollection.Field> fields = new ArrayList<>();
        LoadToMDACollection.Field field = null;
        for (CollectionInfo.Column column : actualCollectionInfo2.getColumns()) {
            field = new LoadToMDACollection.Field();
            field.setDataType(column.getDatatype().toUpperCase());
            field.setName(column.getDbName());
            fields.add(field);
        }
        loadToMDACollection.setFields(fields);
        rulesUtil.saveCustomObjectInRulesConfig(mapper.writeValueAsString(loadToMDACollection));

        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3977-MdaData/GS-3977-Input.json"), RulesPojo.class);
        LoadToMDAAction loadToMDAAction = mapper.readValue(rulesPojo.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
        loadToMDAAction.setObjectName(collectionName2);
        for (FieldMapping fields1 : loadToMDAAction.getFieldMappings()) {
            fields1.setSourceObject(collectionName);
        }
        rulesPojo.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction, JsonNode.class));
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        String demo = mapper.writeValueAsString(rulesPojo);
        Log.debug("Updated Pojo is" + demo);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);

        // Creating another collection with data as source collection for upsert condition
        CollectionInfo collectionInfo1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-CollectionSchema.json")), CollectionInfo.class);
        collectionInfo1.getCollectionDetails().setCollectionName("GS-3979-MDA2-3979" + date.getTime());
        String collectionId1 = gsDataImpl.createCustomObject(collectionInfo1);
        Assert.assertNotNull(collectionId1, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo1 = gsDataImpl.getCollectionMaster(collectionId1);
        String collectionName1 = actualCollectionInfo1.getCollectionDetails().getCollectionName();

        JobInfo loadTransform1 = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-DataloadJob.txt")), JobInfo.class);
        File dataFile1 = FileProcessor.getDateProcessedFile(loadTransform1, date);
        DataLoadMetadata metadata1 = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo1, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata1), dataFile1));
        NsResponseObj nsResponseObj1 = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata1), dataFile1);
        Assert.assertTrue(nsResponseObj1.isResult());

        RulesPojo rulesPojo1 = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-3979-MdaData/GS-3979-Input.json"), RulesPojo.class);
        LoadToMDAAction loadToMDAAction1 = mapper.readValue(rulesPojo1.getSetupActions().get(0).getAction(), LoadToMDAAction.class);
        loadToMDAAction1.setObjectName(collectionName2);
        for (FieldMapping fields1 : loadToMDAAction1.getFieldMappings()) {
            fields1.setSourceObject(collectionName1);
        }
        rulesPojo1.getSetupActions().get(0).setAction(mapper.convertValue(loadToMDAAction1, JsonNode.class));
        rulesPojo1.getSetupRule().setSelectObject(collectionName1);
        rulesPojo1.getSetupRule().getSetupData().get(0).setSourceObject(collectionName1);
        String demo1 = mapper.writeValueAsString(rulesPojo1);
        Log.debug("Updated Pojo is" + demo1);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo1);
    }
}
