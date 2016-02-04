package com.gainsight.bigdata.rulesengine.dummyTests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.FileProcessor;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.DBStoreType;
import com.gainsight.util.MongoDBDAO;
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
public class CalculatedFieldsAndMeasuresTestUsingMongoAsSourceData extends BaseTest {

    private NSTestBase nsTestBase = new NSTestBase();
    private ObjectMapper mapper = new ObjectMapper();
    private DataETL dataETL = new DataETL();
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    GSDataImpl gsDataImpl = null;
    private Date date = Calendar.getInstance().getTime();
    private TenantDetails tenantDetails = null;
    private TenantManager tenantManager;
    MongoDBDAO mongoDBDAO = null;
    String collectionName;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setup(@Optional String dbStoreType) throws Exception {
        basepage.login();
        nsTestBase.init();
        tenantManager = new TenantManager();
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (dbStoreType != null && dbStoreType.equalsIgnoreCase(DBStoreType.MONGO.name())) {
            Assert.assertTrue(tenantManager.disableRedShift(tenantDetails), "Redshift is not disbaled, kindly check credentials");
        }
        rulesManagerPageUrl = visualForcePageUrl + "Rulesmanager";
        rulesManagerPage = new RulesManagerPage();
        gsDataImpl = new GSDataImpl(NSTestBase.header);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt"));
        // Loading testdata at class level in setup
        boolean isLoadTestDataGlobally = true;
        if (isLoadTestDataGlobally) {
            CollectionInfo collectionInfo = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/CollectionSchemaWithMongoCalculatedMeasures.json")), CollectionInfo.class);
            collectionInfo.getCollectionDetails().setCollectionName(dbStoreType + date.getTime());
            String collectionId = gsDataImpl.createCustomObject(collectionInfo);
            Assert.assertNotNull(collectionId, "Collection ID should not be null.");

            CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
            collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
            dataETL.execute(mapper.readValue(resolveNameSpace(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob3.txt"), JobInfo.class));
            JobInfo loadTransform = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/DataloadJob.txt")), JobInfo.class);
            File dataFile = FileProcessor.getDateProcessedFile(loadTransform, date);
            DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, new String[]{"ID", "AccountName", "CustomDate1", "CustomNumber1", "CustomNumber2", "CustomNumberWithDecimals1", "CustomNumberWithDecimals2"}, DataLoadOperationType.INSERT);
            Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
            NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
            Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        }
    }

    @Test(dataProvider = "testData")
    public void testCalculatedFieldsUsingMongoAsSourceData(String fileName) throws Exception {
        Log.info("Creating rule with testdata " + fileName);
        RulesPojo rulesPojo = mapper.readValue(new File(fileName), RulesPojo.class);
        rulesPojo.getSetupRule().setSelectObject(collectionName);
        rulesPojo.getSetupRule().getSetupData().get(0).setSourceObject(collectionName);
        Log.debug("updated input testdata/pojo is " + mapper.writeValueAsString(rulesPojo));
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
    }

    @DataProvider(name = "testData")
    public Object[][] getDataFromDataProvider() {

        return new Object[][]{
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4200/GS-4200-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4236/GS-4236-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4048/GS-4048-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4045/GS-4045-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4046/GS-4046-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4237/GS-4237-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4238/GS-4238-Input-Mongo.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input-Mongo1.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4204/GS-4204-Input-Mongo2.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input-Mongo1.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4239/GS-4239-Input-Mongo2.json"},
                {Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/GS-4234/GS-4234-Input-Mongo.json"}
        };
    }
}
