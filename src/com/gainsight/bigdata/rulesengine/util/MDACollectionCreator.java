package com.gainsight.bigdata.rulesengine.util;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.concurrent.Callable;

/**
 * Created by msanka on 5/4/2016.
 */
public class MDACollectionCreator implements Callable<String> {

    private static final String GLOBAL_TEST_DATA_DIR = Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/GlobalTestData/";
    ObjectMapper mapper;
    File dataFile = null;
    String dbStoreType;
    String collectionSchemaFile;
    String[] validFields;
    GSDataImpl gsDataImpl = null;
    String collectionName= null;

    public MDACollectionCreator(){
        mapper = new ObjectMapper();
        gsDataImpl = new GSDataImpl(BaseTest.header);
    }


    public MDACollectionCreator(String collectionSchemaFile, String dbStoreType, File dataFile, String[] validFields){
        this();
        this.collectionSchemaFile = GLOBAL_TEST_DATA_DIR  + collectionSchemaFile;
        this.dbStoreType = dbStoreType;
        this.dataFile = dataFile;
        this.validFields = validFields;
    }
    public String call() throws Exception {
        return createMDACollectionAndLoadData();
    }

    public String createMDACollectionAndLoadData() throws Exception {
        Log.info("Creating MDA Collection for the schema file: "+ this.collectionSchemaFile.substring(this.collectionSchemaFile.lastIndexOf('/')+1));
        CollectionInfo collectionInfo = mapper.readValue(new FileReader(this.collectionSchemaFile), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(this.dbStoreType + Calendar.getInstance().getTime().getTime());
        String collectionId = gsDataImpl.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataImpl.getCollectionMaster(collectionId);
        collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, this.validFields,	DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataImpl.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj = gsDataImpl.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
        return collectionName;
    }
}
