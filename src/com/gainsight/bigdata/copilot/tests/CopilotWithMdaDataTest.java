package com.gainsight.bigdata.copilot.tests;

import com.gainsight.bigdata.copilot.apiImpl.CopilotAPIImpl;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.gsData.apiImpl.GSDataImpl;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.MongoUtil;
import com.gainsight.utils.annotations.TestInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sforce.soap.partner.sobject.SObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.util.*;

import java.io.File;

/**
 * Created by Abhilash Thaduka on 4/19/2016.
 */
public class CopilotWithMdaDataTest extends CopilotTestUtils {

    private MongoDBDAO mongoDBDAO;
    private MongoDBDAO schemaDbInstance;
    private MongoUtil dataDbInstance;
    private TenantDetails.DBDetail schemaDBDetails = null;
    private TenantDetails.DBDetail dataDBDetails = null;
    private String collectionName;
    private TenantDetails tenantDetails = null;
    private String storageType = null;

    @BeforeClass
    @Parameters("dbStoreType")
    public void setUp(@Optional String dbStoreType) throws Exception {
        copilotAPI = new CopilotAPIImpl(header);
        gsDataAPI = new GSDataImpl(header);
        tenantInfo = gsDataAPI.getTenantInfo(sfinfo.getOrg());
        /*        createCustomFields();
        cleanAndGenerateData();*/
        storageType = dbStoreType;
        mongoDBDAO = MongoDBDAO.getGlobalMongoDBDAOInstance();
        schemaDBDetails = mongoDBDAO.getSchemaDBDetail(tenantInfo.getTenantId());

        dataDBDetails = mongoDBDAO.getDataDBDetail(tenantInfo.getTenantId());
        if (schemaDBDetails == null || schemaDBDetails.getDbServerDetails() == null || schemaDBDetails.getDbServerDetails().get(0) == null) {
            throw new RuntimeException("DB details are not correct, please check it.");
        }
        Log.info("Connecting to schema db.......................");
        schemaDbInstance = new MongoDBDAO(schemaDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, schemaDBDetails.getDbServerDetails().get(0).getUserName(), schemaDBDetails.getDbServerDetails().get(0).getPassword(), schemaDBDetails.getDbName());
        Log.info("Connecting to data db........................");
        dataDbInstance = new MongoUtil(dataDBDetails.getDbServerDetails().get(0).getHost().split(":")[0], 27017, dataDBDetails.getDbServerDetails().get(0).getUserName(), dataDBDetails.getDbServerDetails().get(0).getPassword(), dataDBDetails.getDbName());
        tenantDetails = tenantManager.getTenantDetail(null, tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null).getTenantId());
        if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Redshift")) {
            Log.info("Storage type passed is RedShift");
            if (!tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(tenantManager.enabledRedShiftWithDBDetails(tenantDetails));
            }
        } else if (StringUtils.isNotBlank(dbStoreType) && dbStoreType.equalsIgnoreCase("Mongo")) {
            Log.info("Storage type passed is Mongo");
            if (tenantDetails.isRedshiftEnabled()) {
                Assert.assertTrue(mongoDBDAO.disableRedshift(tenantInfo.getTenantId()));
            }
        }
        loadDataToMda();
    }

    @TestInfo(testCaseIds = {"GS-4633"})
    @Test(description = "Validating AccountStrategy with Email Logs collection as base object with E2E Scenario")
    public void testAccountStrategyEmailLogsAsBaseObject() throws Exception {
        String dbCollectionName = schemaDbInstance.getDbCollectionName(tenantInfo.getTenantId(), "Email Logs");
        Assert.assertTrue(dataDbInstance.removeMany(dbCollectionName, new Document()), "Deletion of records failed !!");
        SObject[] accounts = sfdc.getRecords("SELECT Id,Name FROM Account where Name like 'Gallo Ernst & Julio Winery' and  isdeleted=false");

        List<Document> documents = Lists.newArrayList();
        // Here Email Logs db names are fixed in json as they are constant
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"aid\":\"" + accounts[0].getId() + "\",\"an\":\"Gallo Ernst & Julio Winery\"}"));
        dataDbInstance.insertDocuments(dbCollectionName, documents);

        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName("Email Logs");
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("Account Id"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("Account Name"));
        valuesMap.put("EmailAddress", displayAndDBNamesMap.get("Email Address"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("Opened On"));
        String payload = "{\"name\":\"PowerList Created with Email Logs Collection\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${EmailAddress}\\\",\\\"fieldName\\\":\\\"${EmailAddress}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Email Address\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_EMAIL\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Account Name\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst & Julio Winery\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"CONDITIONAL\",\"actionInfo\":\"{\\\"actionType\\\":\\\"CONDITIONAL\\\",\\\"trueCase\\\":[{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_CONTACTS_IN_ACCOUNT\\\",\\\"identifierType\\\":\\\"ACCOUNT_MAPPING\\\",\\\"recipientFieldName\\\":\\\"Contact.Email\\\"}],\\\"queries\\\":[{\\\"externalIdentifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"uniqueName\\\":\\\"${AccountID}\\\",\\\"parentObj\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"string\\\",\\\"aggregation\\\":\\\"\\\"},\\\"lookUpFieldInfos\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Email\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_EMAIL\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"EMAIL\\\",\\\"isCreateable\\\":true,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":true,\\\"isRichText\\\":false,\\\"isRequired\\\":false},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Name\\\",\\\"fieldName\\\":\\\"Name\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Full Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"STRING\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Id\\\",\\\"fieldName\\\":\\\"Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"ID\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"AccountId\\\",\\\"fieldName\\\":\\\"AccountId\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"REFERENCE\\\",\\\"groupable\\\":false,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Account ID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isExternalCriteria\\\":true,\\\"isReferenceField\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"fieldName\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"isReferenceField\\\":true,\\\"isExternalCriteria\\\":true,\\\"isJoinField\\\":true}],\\\"identifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"uniqueName\\\":\\\"Id\\\",\\\"parentObj\\\":\\\"Account\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"aggregation\\\":\\\"\\\"},\\\"query\\\":\\\"SELECT Contact.Email,Contact.Id,Contact.AccountId,Contact.Name,Contact.Account.Id,Contact.Account.Name FROM Contact WHERE Contact.Account.Id IN (%values%)\\\"}],\\\"additionalCriteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"Contact\\\",\\\"field\\\":\\\"Contact.Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"objectName\\\":\\\"Contact\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"label\\\":\\\"Email\\\",\\\"isExternalCriteria\\\":true},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"automation.gainsighttest.com\\\"}}],\\\"whereLogic\\\":\\\"A\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 15, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/Account_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 15, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4637"})
    @Test(description = "Validating AccountStrategy with Mda custom collection as base object with E2E Scenario")
    public void testAccountStrategyWithMdaCollection() throws Exception {
        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName(collectionName);
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID, Collection name in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("CollectionName", collectionName);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("AccountID"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("AccountName"));
        valuesMap.put("ContactName", displayAndDBNamesMap.get("ContactName"));
        valuesMap.put("EmailAddressField", displayAndDBNamesMap.get("EmailAddressField"));
        valuesMap.put("CustomNumber1", displayAndDBNamesMap.get("CustomNumber1"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("CustomDate1"));
        String payload = "{\"name\":\"Account strategy powerList using Mda collection\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B AND C\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountName\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactName}\\\",\\\"fieldName\\\":\\\"${ContactName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"ContactName\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"AccountName\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst & Julio Winery\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${EmailAddressField}\\\",\\\"fieldName\\\":\\\"${EmailAddressField}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"EmailAddressField\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"automation\\\"}},{\\\"alias\\\":\\\"C\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${CustomNumber1}\\\",\\\"fieldName\\\":\\\"${CustomNumber1}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"label\\\":\\\"CustomNumber1\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"gt\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"590\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"CONDITIONAL\",\"actionInfo\":\"{\\\"actionType\\\":\\\"CONDITIONAL\\\",\\\"trueCase\\\":[{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_CONTACTS_IN_ACCOUNT\\\",\\\"identifierType\\\":\\\"ACCOUNT_MAPPING\\\",\\\"recipientFieldName\\\":\\\"Contact.Email\\\"}],\\\"queries\\\":[{\\\"externalIdentifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"uniqueName\\\":\\\"${AccountID}\\\",\\\"parentObj\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"string\\\",\\\"aggregation\\\":\\\"\\\"},\\\"lookUpFieldInfos\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Email\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_EMAIL\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"EMAIL\\\",\\\"isCreateable\\\":true,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":true,\\\"isRichText\\\":false,\\\"isRequired\\\":false},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Name\\\",\\\"fieldName\\\":\\\"Name\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Full Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"STRING\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Id\\\",\\\"fieldName\\\":\\\"Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"ID\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"AccountId\\\",\\\"fieldName\\\":\\\"AccountId\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"REFERENCE\\\",\\\"groupable\\\":false,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Account ID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isExternalCriteria\\\":true,\\\"isReferenceField\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"fieldName\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"isReferenceField\\\":true,\\\"isExternalCriteria\\\":true,\\\"isJoinField\\\":true}],\\\"identifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"uniqueName\\\":\\\"Id\\\",\\\"parentObj\\\":\\\"Account\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"aggregation\\\":\\\"\\\"},\\\"query\\\":\\\"SELECT Contact.Email,Contact.Account.Name,Contact.Id,Contact.AccountId,Contact.Name,Contact.Account.Id FROM Contact WHERE Contact.Account.Id IN (%values%)\\\"}],\\\"additionalCriteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"Contact\\\",\\\"field\\\":\\\"Contact.Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"objectName\\\":\\\"Contact\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"label\\\":\\\"Email\\\",\\\"isExternalCriteria\\\":true},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"auto\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"Contact\\\",\\\"field\\\":\\\"Contact.Account.Name\\\",\\\"fieldName\\\":\\\"Account.Name\\\",\\\"objectName\\\":\\\"Contact\\\",\\\"fieldType\\\":\\\"TEXT\\\",\\\"label\\\":\\\"Account ID Name\\\",\\\"isExternalCriteria\\\":true},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst & Julio Winery\\\"}}],\\\"whereLogic\\\":\\\"A AND B\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 15, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/AccountStrategyWithMdaCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 150, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4634"})
    @Test(description = "Validating Contact Strategy with Email Logs collection")
    public void testContactStrategyWithEmailLogsCollection() throws Exception {
        String dbCollectionName = schemaDbInstance.getDbCollectionName(tenantInfo.getTenantId(), "Email Logs");
        Assert.assertTrue(dataDbInstance.removeMany(dbCollectionName, new Document()), "Deletion of records failed !!");
        SObject[] contact = sfdc.getRecords("SELECT Id,Name,AccountId,Account.Name FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Azharuddin%' and  isdeleted=false limit 1");

        List<Document> documents = Lists.newArrayList();
        // Here Email Logs db names are fixed in json as they are constant
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"cid\":\"" + contact[0].getId() + "\",\"aid\":\"" + contact[0].getField("AccountId") + "\",\"cn\":\"Azharuddin Gallo Khan\",\"an\":\"Gallo Ernst & Julio Winery\"}"));
        dataDbInstance.insertDocuments(dbCollectionName, documents);

        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName("Email Logs");
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("Account Id"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("Account Name"));
        valuesMap.put("ContactID", displayAndDBNamesMap.get("Contact Id"));
        valuesMap.put("ContactName", displayAndDBNamesMap.get("Contact Name"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("Opened On"));
        String payload = "{\"name\":\"Contact Strategy - PowerList Created with Email Logs Collection - GS-4634 - Automated By Abhilash\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"cid\\\",\\\"fieldName\\\":\\\"cid\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Contact Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isRecipientField\\\":true,\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Account Name\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst & Julio Winery\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${ContactName}\\\",\\\"fieldName\\\":\\\"${ContactName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Contact Name\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Azharuddin\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"CONDITIONAL\",\"actionInfo\":\"{\\\"actionType\\\":\\\"CONDITIONAL\\\",\\\"trueCase\\\":[{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_CONTACTS\\\",\\\"identifierType\\\":\\\"CONTACT_MAPPING\\\",\\\"recipientFieldName\\\":\\\"Contact.Email\\\"}],\\\"queries\\\":[{\\\"externalIdentifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"cid\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"string\\\",\\\"aggregation\\\":\\\"\\\"},\\\"lookUpFieldInfos\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Email\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_EMAIL\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"EMAIL\\\",\\\"isCreateable\\\":true,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":true,\\\"isRichText\\\":false,\\\"isRequired\\\":false},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Name\\\",\\\"fieldName\\\":\\\"Name\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Full Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"STRING\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Id\\\",\\\"fieldName\\\":\\\"Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"ID\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"AccountId\\\",\\\"fieldName\\\":\\\"AccountId\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"REFERENCE\\\",\\\"groupable\\\":false,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Account ID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isExternalCriteria\\\":true,\\\"isReferenceField\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"fieldName\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"isReferenceField\\\":true,\\\"isExternalCriteria\\\":true,\\\"isJoinField\\\":true}],\\\"identifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"uniqueName\\\":\\\"Id\\\",\\\"parentObj\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"aggregation\\\":\\\"\\\"},\\\"query\\\":\\\"SELECT Contact.Copilot_Contact_AutoNumber__c,Contact.Id,Contact.Email,Contact.AccountId,Contact.Name,Contact.Account.Name,Contact.Account.Id FROM Contact WHERE Contact.Id IN (%values%)\\\"}],\\\"additionalCriteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"Contact\\\",\\\"field\\\":\\\"Contact.Copilot_Contact_AutoNumber__c\\\",\\\"fieldName\\\":\\\"Copilot_Contact_AutoNumber__c\\\",\\\"objectName\\\":\\\"Contact\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Copilot_Contact_AutoNumber\\\",\\\"isExternalCriteria\\\":true},\\\"operator\\\":\\\"gt\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"500\\\"}}],\\\"whereLogic\\\":\\\"A\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/ContactStrategyWithEmailLogsCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4637"})
    @Test(description = "Validating Contact Strategy with Email Logs collection and more filter conditions")
    public void testContactStrategyWithEmailLogsCollectionAndMoreFilters() throws Exception {
        String dbCollectionName = schemaDbInstance.getDbCollectionName(tenantInfo.getTenantId(), "Email Logs");
        Assert.assertTrue(dataDbInstance.removeMany(dbCollectionName, new Document()), "Deletion of records failed !!");
        SObject[] contact1 = sfdc.getRecords("SELECT Id,Name,AccountId,Account.Name FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Azharuddin%' and  isdeleted=false limit 1");
        SObject[] contact2 = sfdc.getRecords("SELECT Id,Name,AccountId,Account.Name FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Hari%' and  isdeleted=false limit 1");

        List<Document> documents = Lists.newArrayList();
        // Here Email Logs db names are fixed in json as they are constant
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"cid\":\"" + contact1[0].getId() + "\",\"aid\":\"" + contact1[0].getField("AccountId") + "\",\"cn\":\"Azharuddin Gallo Khan\",\"an\":\"Gallo Ernst & Julio Winery\",\"nopen\":3,\"nclk\":2}"));
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"cid\":\"" + contact2[0].getId() + "\",\"aid\":\"" + contact2[0].getField("AccountId") + "\",\"cn\":\"Hari babu Gallo Vadlamudi\",\"an\":\"Gallo Ernst & Julio Winery\",\"nopen\":0,\"nclk\":0}"));
        dataDbInstance.insertDocuments(dbCollectionName, documents);

        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName("Email Logs");
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("Account Id"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("Account Name"));
        valuesMap.put("ContactID", displayAndDBNamesMap.get("Contact Id"));
        valuesMap.put("ContactName", displayAndDBNamesMap.get("Contact Name"));
        valuesMap.put("NOpened", displayAndDBNamesMap.get("Open Count"));
        valuesMap.put("NClicked", displayAndDBNamesMap.get("Click Count"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("Opened On"));
        String payload = "{\"name\":\"Contact Strategy with Email Logs collection and more filter conditions - Automated By Abhilash\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B AND C\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactID}\\\",\\\"fieldName\\\":\\\"${ContactID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Contact Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isRecipientField\\\":true,\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactName}\\\",\\\"fieldName\\\":\\\"${ContactName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Contact Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${NClicked}\\\",\\\"fieldName\\\":\\\"${NClicked}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"dataType\\\":\\\"number\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Click Count\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":1,\\\"originalDataType\\\":\\\"NUMBER\\\",\\\"decimalPlaces\\\":0},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${NOpened}\\\",\\\"fieldName\\\":\\\"${NOpened}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"dataType\\\":\\\"number\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Open Count\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":1,\\\"originalDataType\\\":\\\"NUMBER\\\",\\\"decimalPlaces\\\":0},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${NClicked}\\\",\\\"fieldName\\\":\\\"${NClicked}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"label\\\":\\\"Click Count\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"ne\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"0\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${NOpened}\\\",\\\"fieldName\\\":\\\"${NOpened}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"label\\\":\\\"Open Count\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"ne\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"0\\\"}},{\\\"alias\\\":\\\"C\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Account Name\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"SMARTLIST\",\"actionInfo\":\"{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_CONTACTS\\\",\\\"identifierType\\\":\\\"CONTACT_MAPPING\\\",\\\"queries\\\":[{\\\"externalIdentifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"string\\\",\\\"aggregation\\\":\\\"\\\"},\\\"lookUpFieldInfos\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Email\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_EMAIL\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"EMAIL\\\",\\\"isCreateable\\\":true,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":true,\\\"isRichText\\\":false,\\\"isRequired\\\":false},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Name\\\",\\\"fieldName\\\":\\\"Name\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Full Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"STRING\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Id\\\",\\\"fieldName\\\":\\\"Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"ID\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"AccountId\\\",\\\"fieldName\\\":\\\"AccountId\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"REFERENCE\\\",\\\"groupable\\\":false,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Account ID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isExternalCriteria\\\":true,\\\"isReferenceField\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"fieldName\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"isReferenceField\\\":true,\\\"isExternalCriteria\\\":true,\\\"isJoinField\\\":true}],\\\"identifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"uniqueName\\\":\\\"Id\\\",\\\"parentObj\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"aggregation\\\":\\\"\\\"},\\\"query\\\":\\\"SELECT Contact.Id,Contact.Email,Contact.AccountId,Contact.Name,Contact.Account.Name,Contact.Account.Id FROM Contact WHERE Contact.Id IN (%values%)\\\"}],\\\"recipientFieldName\\\":\\\"Contact.Email\\\",\\\"additionalCriteria\\\":[],\\\"whereLogic\\\":\\\"\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/ContactStrategyWithEmailLogsCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4638"})
    @Test(description = "Validating Contact Strategy with Mda collection")
    public void testContactStrategyWithMdaCollection() throws Exception {
        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName(collectionName);
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID, Collection name in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("CollectionName", collectionName);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("AccountID"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("AccountName"));
        valuesMap.put("ContactID", displayAndDBNamesMap.get("ContactID"));
        valuesMap.put("CustomNumber1", displayAndDBNamesMap.get("CustomNumber1"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("CustomDate1"));
        String payload = "{\"name\":\"Contact Strategy with Mda collection -Automated by Abhilash\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountName\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactID}\\\",\\\"fieldName\\\":\\\"${ContactID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"ContactID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isRecipientField\\\":true,\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${CustomNumber1}\\\",\\\"fieldName\\\":\\\"${CustomNumber1}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"dataType\\\":\\\"number\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"CustomNumber1\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":1,\\\"originalDataType\\\":\\\"NUMBER\\\",\\\"decimalPlaces\\\":0},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"AccountName\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"startsWith\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"Gallo Ernst & Julio Winery\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${CustomNumber1}\\\",\\\"fieldName\\\":\\\"${CustomNumber1}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"NUMBER\\\",\\\"label\\\":\\\"CustomNumber1\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"gt\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"NUMBER\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"595\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"SMARTLIST\",\"actionInfo\":\"{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_CONTACTS\\\",\\\"identifierType\\\":\\\"CONTACT_MAPPING\\\",\\\"queries\\\":[{\\\"externalIdentifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"string\\\",\\\"aggregation\\\":\\\"\\\"},\\\"lookUpFieldInfos\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Email\\\",\\\"fieldName\\\":\\\"Email\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"EMAIL\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Email\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_EMAIL\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"EMAIL\\\",\\\"isCreateable\\\":true,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":true,\\\"isRichText\\\":false,\\\"isRequired\\\":false},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Name\\\",\\\"fieldName\\\":\\\"Name\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Full Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"STRING\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Id\\\",\\\"fieldName\\\":\\\"Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_USER\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isSortable\\\":true,\\\"isGroupable\\\":true,\\\"originalDataType\\\":\\\"ID\\\",\\\"isCreateable\\\":false,\\\"precision\\\":0,\\\"decimalPlaces\\\":\\\"0\\\",\\\"relationshipName\\\":\\\"\\\",\\\"isFormulaField\\\":false,\\\"isUpdateable\\\":false,\\\"isRichText\\\":false,\\\"isRequired\\\":true},\\\"isExternalCriteria\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"AccountId\\\",\\\"fieldName\\\":\\\"AccountId\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"REFERENCE\\\",\\\"groupable\\\":false,\\\"objectName\\\":\\\"Contact\\\",\\\"label\\\":\\\"Account ID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isExternalCriteria\\\":true,\\\"isReferenceField\\\":true},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Account.Id\\\",\\\"fieldName\\\":\\\"Contact.Account.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"STRING\\\",\\\"fieldType\\\":\\\"ID\\\",\\\"isReferenceField\\\":true,\\\"isExternalCriteria\\\":true,\\\"isJoinField\\\":true}],\\\"identifier\\\":{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"Contact.Id\\\",\\\"entity\\\":\\\"Contact\\\",\\\"uniqueName\\\":\\\"Id\\\",\\\"parentObj\\\":\\\"Contact\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"aggregation\\\":\\\"\\\"},\\\"query\\\":\\\"SELECT Contact.Id,Contact.Email,Contact.AccountId,Contact.Name,Contact.Account.Name,Contact.Account.Id FROM Contact WHERE Contact.Id IN (%values%)\\\"}],\\\"recipientFieldName\\\":\\\"Contact.Email\\\",\\\"additionalCriteria\\\":[],\\\"whereLogic\\\":\\\"\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 5, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/ContactStrategyWithMdaCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 5, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4639"})
    @Test(description = "Validating Email Strategy with Mda custom collection as base object")
    public void testEmailStrategyWithMdaCollection() throws Exception {
        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName(collectionName);
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID, Collection name in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("CollectionName", collectionName);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("AccountID"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("AccountName"));
        valuesMap.put("EmailAddressField", displayAndDBNamesMap.get("EmailAddressField"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("CustomDate1"));
        valuesMap.put("ContactName", displayAndDBNamesMap.get("ContactName"));
        String payload = "{\"name\":\"Email Strategy - GS-4639\",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountID\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"AccountName\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${EmailAddressField}\\\",\\\"fieldName\\\":\\\"${EmailAddressField}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"EmailAddressField\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isRecipientField\\\":true,\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${ContactName}\\\",\\\"fieldName\\\":\\\"${ContactName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"ContactName\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_USER_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${EmailAddressField}\\\",\\\"fieldName\\\":\\\"${EmailAddressField}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"EmailAddressField\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"gallovivek@automation.gainsighttest.com\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"AccountName\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"gal\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"SMARTLIST\",\"actionInfo\":\"{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_EMAIL_ADDRESSES\\\",\\\"identifierType\\\":\\\"CONTACT_MAPPING\\\",\\\"recipientFieldName\\\":\\\"${EmailAddressField}\\\",\\\"additionalCriteria\\\":[],\\\"whereLogic\\\":\\\"\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 1, 1);

        //Creating email template & verifying the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/EmailStrategyWithMdaCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    @TestInfo(testCaseIds = {"GS-4635"})
    @Test(description = "Validating Email Strategy with Email Logs collection")
    public void testEmailStrategyWithEmailLogsCollectionAndMoreFilters() throws Exception {
        String dbCollectionName = schemaDbInstance.getDbCollectionName(tenantInfo.getTenantId(), "Email Logs");
        Assert.assertTrue(dataDbInstance.removeMany(dbCollectionName, new Document()), "Deletion of records failed !!");
        SObject[] contact1 = sfdc.getRecords("SELECT Id,Name,AccountId,Account.Name,Email FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Azharuddin%' and  isdeleted=false limit 1");
        SObject[] contact2 = sfdc.getRecords("SELECT Id,Name,AccountId,Account.Name,Email FROM Contact where Account.Name='Gallo Ernst & Julio Winery' and Name like '%Hari%' and  isdeleted=false limit 1");

        List<Document> documents = Lists.newArrayList();
        // Here Email Logs db names are fixed in json as they are constant
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"cid\":\"" + contact1[0].getId() + "\",\"aid\":\"" + contact1[0].getField("AccountId") + "\",\"cn\":\"Azharuddin Gallo Khan\",\"an\":\"Gallo Ernst & Julio Winery\",\"nopen\":3,\"nclk\":2,\"e\":\"" + contact1[0].getField("Email") + "\"}"));
        documents.add(Document.parse("{\"uc\":\"Campaigns\",\"cid\":\"" + contact2[0].getId() + "\",\"aid\":\"" + contact2[0].getField("AccountId") + "\",\"cn\":\"Hari babu Gallo Vadlamudi\",\"an\":\"Gallo Ernst & Julio Winery\",\"nopen\":0,\"nclk\":0,\"e\":\"" + contact2[0].getField("Email") + "\"}"));
        dataDbInstance.insertDocuments(dbCollectionName, documents);

        CollectionInfo collectionInfo = gsDataAPI.getCollectionMasterByName("Email Logs");
        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        HashMap<String, String> displayAndDBNamesMap = CollectionUtil.getDisplayAndDBNamesMap(collectionInfo);

        // Replacing db Names and collection ID in the payload
        Map<String, String> valuesMap = Maps.newHashMap();
        valuesMap.put("collectionId", collectionId);
        valuesMap.put("AccountID", displayAndDBNamesMap.get("Account Id"));
        valuesMap.put("AccountName", displayAndDBNamesMap.get("Account Name"));
        valuesMap.put("TimeIdentifier", displayAndDBNamesMap.get("Opened On"));
        valuesMap.put("EmailAddress", displayAndDBNamesMap.get("Email Address"));
        String payload = "{\"name\":\"GS-4635 - Email Strategy with Email Logs collection -  Automated By Abhilash \",\"description\":\"\",\"status\":\"ACTIVE\",\"stats\":{\"contactCount\":0,\"customerCount\":0},\"automatedRule\":{\"relatedId\":\"\",\"ruleType\":\"CONDITIONAL\",\"description\":\"\",\"status\":true,\"triggerCriteria\":\"[{\\\"whereLogic\\\":\\\"A AND B\\\",\\\"refField\\\":null,\\\"timeIdentifier\\\":\\\"${TimeIdentifier}\\\",\\\"select\\\":[{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountID}\\\",\\\"fieldName\\\":\\\"${AccountID}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Id\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"isAccountIdRelatedField\\\":true,\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Account Name\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"properties\\\":{\\\"SFDC\\\":{\\\"keys\\\":[\\\"SFDC_ACCOUNT_NAME\\\"]}},\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_ACCOUNT_NAME\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"},{\\\"type\\\":\\\"field\\\",\\\"field\\\":\\\"${EmailAddress}\\\",\\\"fieldName\\\":\\\"${EmailAddress}\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"dataType\\\":\\\"string\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"groupable\\\":true,\\\"objectName\\\":\\\"${collectionId}\\\",\\\"label\\\":\\\"Email Address\\\",\\\"alias\\\":\\\"\\\",\\\"aggregation\\\":\\\"\\\",\\\"isRecipientField\\\":true,\\\"meta\\\":{\\\"isAccessible\\\":true,\\\"isFilterable\\\":true,\\\"isGroupable\\\":true,\\\"colattribtype\\\":0,\\\"originalDataType\\\":\\\"STRING\\\",\\\"decimalPlaces\\\":0,\\\"mappings\\\":{\\\"SFDC\\\":{\\\"key\\\":\\\"SFDC_EMAIL\\\",\\\"dataType\\\":\\\"string\\\"}}},\\\"collectionId\\\":\\\"${collectionId}\\\"}],\\\"calculatedFields\\\":[],\\\"collectionId\\\":\\\"${collectionId}\\\",\\\"criteria\\\":[{\\\"alias\\\":\\\"A\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${AccountName}\\\",\\\"fieldName\\\":\\\"${AccountName}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Account Name\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"gal\\\"}},{\\\"alias\\\":\\\"B\\\",\\\"left\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"field\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"entity\\\":\\\"${collectionId}\\\",\\\"field\\\":\\\"${EmailAddress}\\\",\\\"fieldName\\\":\\\"${EmailAddress}\\\",\\\"objectName\\\":\\\"${collectionId}\\\",\\\"fieldType\\\":\\\"STRING\\\",\\\"label\\\":\\\"Email Address\\\",\\\"isExternalCriteria\\\":false},\\\"operator\\\":\\\"contains\\\",\\\"right\\\":{\\\"keys\\\":[],\\\"type\\\":\\\"value\\\",\\\"valueType\\\":\\\"STRING\\\",\\\"isNull\\\":false,\\\"value\\\":\\\"gallovadlamudi\\\"}}]}]\",\"sourceType\":\"CONDITIONAL\",\"actionDetails\":[{\"actionType\":\"SMARTLIST\",\"actionInfo\":\"{\\\"params\\\":{\\\"areaName\\\":\\\"SMARTLIST\\\"},\\\"actionType\\\":\\\"SMARTLIST\\\",\\\"recipientStrategy\\\":\\\"SPECIFIC_EMAIL_ADDRESSES\\\",\\\"identifierType\\\":\\\"CONTACT_MAPPING\\\",\\\"recipientFieldName\\\":\\\"${EmailAddress}\\\",\\\"additionalCriteria\\\":[],\\\"whereLogic\\\":\\\"\\\"}\"}],\"triggerUsageOn\":\"ACCOUNTLEVEL\"},\"refreshList\":false,\"dataSourceType\":\"NEW-STACK\"}";
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        String actualPayload = sub.replace(payload);
        Log.info("Modified payload is " + actualPayload);

        // creating and asserting smartList
        SmartList actualSmartList = createAndValidateSmartList(mapper.readValue(actualPayload, SmartList.class), 1, 1);

        //Create email template & verify the same.
        EmailTemplate actualEmailTemplate = createAndValidateEmailTemplate(mapper.readValue(new File(testDataDir + "test/EmailLogsBaseObject/EmailTemplate.json"), EmailTemplate.class));

        //replacing tokens in outreach
        String outReachPayload = sub.replace(FileUtils.readFileToString(new File(testDataDir + "test/EmailLogsBaseObject/EmailStrategyWithEmailLogsCollection_Outreach.json")));

        //Create out reach & verify the same.
        OutReach actualOutReach = createAndTriggerOutreach(mapper.readValue(outReachPayload, OutReach.class), actualSmartList, actualEmailTemplate);
        verifyOutReachExecutionHistory(actualOutReach.getStatusId(), 1, 0);
        Assert.assertEquals(actualOutReach.getLastRunStatus(), "SUCCESS", "Outreach Run Status is not Success");

        // verifying the email template use in outreach.
        verifyTemplateUsedInOutreach(actualEmailTemplate, actualOutReach);

        //Deleting outreach, email template, smart list.
        Assert.assertTrue(copilotAPI.deleteOutReach(actualOutReach.getCampaignId()), "Outreach deletion failed.");
        Assert.assertTrue(copilotAPI.deleteEmailTemplate(actualEmailTemplate.getTemplateId()), "Email template deletion failed.");
        Assert.assertTrue(copilotAPI.deleteSmartList(actualSmartList.getSmartListId()), "APowerList list deletion failed.");
    }

    private void loadDataToMda() throws Exception {
        CollectionInfo collectionInfo = mapper.readValue((new File(testDataDir + "globalMdaTestData/CollectionSchema.json")), CollectionInfo.class);
        collectionInfo.getCollectionDetails().setCollectionName(storageType + "=======" + UUID.randomUUID());
        String collectionId = gsDataAPI.createCustomObject(collectionInfo);
        Assert.assertNotNull(collectionId, "Collection ID should not be null.");
        CollectionInfo actualCollectionInfo = gsDataAPI.getCollectionMaster(collectionId);
        collectionName = actualCollectionInfo.getCollectionDetails().getCollectionName();
        dataETL.execute(mapper.readValue((new File(testDataDir + "globalMdaTestData/DataloadJob.txt")), JobInfo.class));
        File dataFile = new File(testDataDir + "globalMdaTestData/Mda_Data_Final.csv");
        DataLoadMetadata metadata = CollectionUtil.getDBDataLoadMetaData(actualCollectionInfo, DataLoadOperationType.INSERT);
        Assert.assertTrue(gsDataAPI.isValidDataProvided(mapper.writeValueAsString(metadata), dataFile), "Data is not valid");
        NsResponseObj nsResponseObj = gsDataAPI.loadDataToMDA(mapper.writeValueAsString(metadata), dataFile);
        Assert.assertTrue(nsResponseObj.isResult(), "Data is not loaded, please check log for more details");
    }

    @AfterClass
    private void tearDown() {
        if (mongoDBDAO != null && schemaDbInstance != null && dataDbInstance != null) {
            dataDbInstance.closeConnection();
            mongoDBDAO.mongoUtil.closeConnection();
            schemaDbInstance.mongoUtil.closeConnection();
        }
    }
}
