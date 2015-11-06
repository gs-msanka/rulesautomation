package com.gainsight.bigdata.dataload.apiimpl;

import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.gainsight.bigdata.urls.ApiUrls.*;
import static com.gainsight.sfdc.pages.Constants.*;

/**
 * Created by Giribabu on 13/05/15.
 */
public class DataLoadManager  {

    public Header headers = new Header();
    ObjectMapper mapper = new ObjectMapper();
    WebAction wa = new WebAction();


    public DataLoadManager(SFDCInfo sfdcInfo, String accessKey) {
        headers.addHeader("Content-Type", "application/json");
        headers.addHeader("accessKey", accessKey);
        headers.addHeader("appOrgId", sfdcInfo.getOrg());
        headers.addHeader("loginName", sfdcInfo.getUserName());
    }

    /**
     * Performs MDA authentication used via data load tool.
     *
     * @param sfOrgId - Salesforce Organisation ID - 15 (or) 18 digits.
     * @param accessKey - AccessKey to load data.
     * @param loginName - Salesforce User Login Name.
     * @return NSResponseObj - if authentication is successful with the given params and NULL on failure.
     */
    public NsResponseObj mdaDataLoadAuthenticate(String sfOrgId, String accessKey, String loginName) {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = mdaAuthenticate(sfOrgId, accessKey, loginName);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            } else {
                Log.error("Authentication Failure.");
            }
        } catch (Exception e) {
            Log.error("Failed to parse", e);
            throw new RuntimeException("Failed to parse"+e.getLocalizedMessage());
        }
        return nsResponseObj;
    }

    /**
     * Does salesforce authentication with the supplied details.
     *
     * @param sfOrgId - Salesforce Org Id.
     * @param accessKey - AccessKey generated from MDA.
     * @param loginName - user login name.
     * @return - Response Object.
     */
    public ResponseObj mdaAuthenticate(String sfOrgId, String accessKey, String loginName) {
        Header head = new Header();
        head.addHeader("appOrgId", NSUtil.convertSFID_15TO18(sfOrgId));
        head.addHeader("accessKey", accessKey);
        head.addHeader("loginName", loginName);
        try {
            ResponseObj responseObj = wa.doGet(DATA_LOAD_AUTHENTICATE, head.getAllHeaders());
            Log.info("Response Obj : " +responseObj.toString());
            return responseObj;
        } catch (Exception e) {
            Log.error("Failed to perform authentication.", e);
            throw new RuntimeException("Failed to perform authentication");
        }
    }

    /**
     * Creates a collection/Subject area in MDA.
     *
     * @param collectionInfo - Collection Schema/Table Schema to create a subject area.
     * @return - NSResponse Object.
     */
    public NsResponseObj createSubjectArea(CollectionInfo collectionInfo) {
        Log.info("Creating Subject Area...");
        if (collectionInfo == null) {
            throw new RuntimeException("Collection Info should not be NULL");
        }
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = createSubjectAreaGetResponseObj(collectionInfo);
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception e) {
            Log.error("Failed to create subject area ", e);
            throw new RuntimeException("Failed to create subject area " + e.getLocalizedMessage());
        }
        Log.info("Returning after subject create.");
        return nsResponseObj;
    }

    public ResponseObj createSubjectAreaGetResponseObj(CollectionInfo collectionInfo) throws Exception {
        ResponseObj responseObj = wa.doPost(DATA_LOAD_COLLECTIONS, headers.getAllHeaders(), mapper.writeValueAsString(collectionInfo));
        Log.info("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Creates a collection and returns the collection Id.
     *
     * @param collectionInfo - Collection to be created.
     * @return collection id on success, null on failure.
     */
    public String createSubjectAreaAndGetId(CollectionInfo collectionInfo ){
        String collectionId = null;
        NsResponseObj nsResponseObj = createSubjectArea(collectionInfo);
        if(nsResponseObj.isResult()) {
            CollectionInfo.CollectionDetails colDetails = getCollectionDetail(nsResponseObj.getData());
            collectionId = colDetails.getCollectionId();
        }
        Log.info("Collection Id : " +collectionId);
        return collectionId;
    }

    /**
     * Gets the data load job details.
     *
     * @param jobId - JobId/StatusId - to check the status.
     * @return JOB Result.
     */
    public DataLoadStatusInfo getDataLoadJobStatus(String jobId) {
        Log.info("Get Data Load Job Status : " +jobId);
        if (jobId == null) {
            throw new RuntimeException("Job Id is mandatory");
        }
        DataLoadStatusInfo statusInfo = null;
        try {
            ResponseObj responseObj = wa.doGet(DATA_LOAD_STATUS + jobId, headers.getAllHeaders());
            Log.info("Response Obj : " +responseObj.toString());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    statusInfo = mapper.convertValue(nsResponseObj.getData(), DataLoadStatusInfo.class);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get job status", e);
            throw new RuntimeException("Failed to get job status" + e.getLocalizedMessage());
        }
        Log.info("Returning Status Info." +statusInfo.toString());
        return statusInfo;
    }

    /**
     * Waits for MAX_NO_OF_REQUESTS*10 sec for the Job to complete
     *
     * @param jobId - JobId/StatusId to wait for its completion i.e status != IN_PROGRESS.
     */
    public boolean waitForDataLoadJobComplete(final String jobId) {
        if(jobId == null || jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should not be null");
        }
        Log.info("Wait for the "+jobId + " to complete...");
        boolean result = CommonWait.waitForCondition(MAX_WAIT_TIME, INTERVAL_TIME, new ExpectedCommonWaitCondition<Boolean>() {
            @Override
            public Boolean apply() {
                return isdataLoadJobDone(jobId);
            }
        });
        return result;
    }

    /**
     * Returns true id job is completed, failed, partial success else false.
     * @param jobId - Job Id to check the status.
     * @return
     */
    public boolean isdataLoadJobDone(String jobId) {
        boolean result = false;
        DataLoadStatusInfo statusInfo = getDataLoadJobStatus(jobId);
        if(statusInfo == null) {
            throw new RuntimeException("Failed to get Data Load Job Status : " +jobId);
        }
        if(statusInfo.getStatusType().equals(DataLoadStatusType.COMPLETED)
                                    || statusInfo.getStatusType().equals(DataLoadStatusType.FAILED)
                                    || statusInfo.getStatusType().equals(DataLoadStatusType.PARTIAL_SUCCESS)) {
            result = true;
            Log.info("Data Load Job : " +jobId + " is complete.");
        }
        if(!result) {
            Log.info("Data Load Job status is not completed : " +jobId);
            Log.info(("Status of Job : " +statusInfo.getStatusType()));
        }

        return result;
    }

    public boolean isdataLoadJobCompleted(String jobId) {
        boolean result = false;
        DataLoadStatusInfo statusInfo = getDataLoadJobStatus(jobId);
        if(statusInfo == null) {
            throw new RuntimeException("Failed to get Data Load Job Status : " +jobId);
        }
        if(statusInfo.getStatusType().equals(DataLoadStatusType.COMPLETED)) {
            result = true;
            Log.info("Data Load Job : " +jobId + " is complete.");
        }
        if(!result) {
            Log.info("Data Load Job status is not Success : " +jobId);
            Log.info("Status of Job : " +statusInfo.getStatusType());
            Log.info("Error Message : " +statusInfo.getMessage());
        }
        return result;
    }


    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param DLMetadata - DataLoadMetadata - Used for data load mapping purpose.
     * @param filePath - File to load.
     * @return - Status/JobID after the data load.
     */
    public String dataLoadManage(DataLoadMetadata DLMetadata, String filePath) {
        if(DLMetadata==null || filePath==null) {
            throw new RuntimeException("Meta data & file path is mandatory.");
        }
        return dataLoadManage(DLMetadata, new File(filePath));
    }

    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param metadata - metadata/schema i.e. CSV file header to collection field mapping - used to load the CSV file
     * @param filePath - File path to be loaded to MDA.
     * @return - Status/JobID after the data load.
     */
    public String dataLoadManage(String metadata, String filePath) {
        if(metadata==null || filePath==null) {
            throw new RuntimeException("Meta data & file path is mandatory.");
        }
        return dataLoadManage(metadata, new File(filePath));
    }

    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param DLMetadata - DataLoadMetadata - Used for data load mapping purpose.
     * @param file - File to load.
     * @return - Status/JobID of the data load.
     */
    public String dataLoadManage(DataLoadMetadata DLMetadata, File file) {
        if(DLMetadata==null || file==null || !file.exists()) {
            throw new RuntimeException("Metadata & file should not be null...");
        }
        String metadata = null;
        try {
            metadata = mapper.writeValueAsString(DLMetadata);
        } catch (IOException e) {
            Log.error("Failed to parser metadata.", e);
            throw new RuntimeException("Failed to parse metadata" +e);
        }
        return dataLoadManage(metadata, file);
    }


    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param metadata - metadata/schema i.e. CSV file header to collection field mapping - used to load the CSV file
     * @param file - File to be loaded to MDA.
     * @return - Status/JobID after the data load.
     */

    public String dataLoadManage(String metadata, File file) {
        String jobId = null;
        try {
            ResponseObj responseObj = dataLoadManageGetResponseObject(metadata, file);
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    HashMap<String, String> serverData = (HashMap<String, String>)mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                    Log.info("Status ID : " + serverData.get("statusId"));
                    jobId = serverData.get("statusId");
                } else {
                    throw new RuntimeException("Failed to load data");
                }
            }
        } catch (Exception e) {
            Log.error("Failed to load data", e);
            throw new RuntimeException("Failed to load data, "+e.getLocalizedMessage());
        }
        Log.info("Data Load Successful, returning job Id " +jobId);
        return jobId;
    }

    public ResponseObj dataLoadManageGetResponseObject(String metadata, File file) throws Exception {
        Log.info("Started Loading Data to MDA...");
        if(metadata==null || file==null) {
            throw new RuntimeException("Metadata & file are not null");
        }

        headers.removeHeader("Content-Type");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        StringBody value = new StringBody(metadata, ContentType.APPLICATION_JSON);
        builder.addPart("metadata", value);

        FileBody body = new FileBody(file, ContentType.MULTIPART_FORM_DATA);
        builder.addPart("file", body);
        HttpEntity reqEntity = builder.build();
        try {
            ResponseObj responseObj = wa.doPost(DATA_LOAD_IMPORT, headers.getAllHeaders(), reqEntity);
            Log.info("Response Obj : " + responseObj.toString());
            return responseObj;
        }finally {
            headers.addHeader("Content-Type","application/json");
        }
    }


    /**
     * Exports all the failed records as string with new line as line separator and with headers.
     *
     * @param statusId - The StatusId(JobId) to retrieve the failed records
     * @return StringContent returned by server.
     */
    public String exportFailedRecords(String statusId) {
        String result= null;
        try {
            ResponseObj responseObj = wa.doGet(DATA_LOAD_EXPORT_FAILURES + statusId, headers.getAllHeaders());
            Log.info("Response Obj : " + responseObj.toString());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                  result = responseObj.getContent();
            }
        } catch (Exception e) {
            Log.error("Failed to export data for status ID :" +statusId);
            throw new RuntimeException("Failed to export data for status ID :"+e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * Exports all the failed records to the CSV file provided.
     *
     * @param statusId - The StatusId(JobId) to retrieve the failed records
     * @param file - Writes to the file specified.
     */
    public void exportFailedRecords(String statusId, File file) {
        Log.info("Started Exporting Failed records...");
        String data = exportFailedRecords(statusId);
        if(file.exists()) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(file), ',', '"', '\\', "\n");
                List<String[]> rows = new ArrayList<>();
                String[] cols;
                for(String row : data.split("\\n")) {
                    cols = row.split(",");
                    rows.add(cols);
                }
                writer.writeAll(rows);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new RuntimeException("File not found" +file.getAbsolutePath());
        }
    }

    /**
     * Gets all the Collections for a tenant.
     *
     * @return - List of Collection Schema (Subject Area) of a Tenant.
     */
    public List<CollectionInfo> getAllCollections() {
        Log.info("Getting All the Collections...");
        List<CollectionInfo> collectionInfoList = new ArrayList<>();
        try {
            ResponseObj responseObj = wa.doGet(DATA_LOAD_GET_COLLECTIONS_ALL, headers.getAllHeaders());
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);

            HashMap<String, List<CollectionInfo>> collectionInfoMap = mapper.convertValue(nsResponseObj.getData(), new TypeReference<HashMap<String, ArrayList<CollectionInfo>>>() {});

            if(collectionInfoMap != null && collectionInfoMap.containsKey("Collections")) {
                collectionInfoList = collectionInfoMap.get("Collections");
            }
        } catch (Exception e) {
            Log.error("Failed to get collections..");
            throw new RuntimeException("Failed to get collections..");
        }
        Log.info("Total No of Collections... "+collectionInfoList.size());
        return collectionInfoList;
    }

    /**
     * Gets the schema of a subject area / Collection.
     *
     * @param subjectAreaName - The SubjectArea/Collection name for which the collection schema need to be retrieved.
     * @return - Subject/Collection schema.
     */
    public CollectionInfo getCollection(String subjectAreaName) {
        Log.info("Getting Single Collection..." +subjectAreaName);
        if(subjectAreaName ==null && subjectAreaName.equals("")) {
            throw new RuntimeException("Subject Area is Required.");
        }
        List<CollectionInfo> collectionInfoList = getAllCollections();
        Log.info("No OF Collections ... " +collectionInfoList.size());
        for(CollectionInfo collectionInfo : collectionInfoList) {
            if(subjectAreaName.equals(collectionInfo.getCollectionDetails().getCollectionName())) {
                return collectionInfo;
            }
        }
        return null;
    }

    /**
     * Gets the schema of a subject area / Collection.
     *
     * @param collectionId  - The Collection Id for which the collection schema need to be retrieved.
     * @return - Subject/Collection schema.
     */
    public CollectionInfo getCollectionInfo(String collectionId) {
        Log.info("Get Collection Schema...");
        try {
            ResponseObj responseObj = wa.doGet(APP_API_GET_COLLECTION+collectionId, headers.getAllHeaders());
            Log.info("Response Obj : " +responseObj.toString());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    CollectionInfo collectionInfo = mapper.convertValue(nsResponseObj.getData(), CollectionInfo.class);
                    return collectionInfo;
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get collection schema..."+e.getLocalizedMessage());
            throw new RuntimeException("Failed to get collection schema...", e);
        }
        return null;
    }

    /**
     * Fetches the failed records for the job id.
     *
     * @param jobId - Job Id to get failed records.
     * @return - List of String - data rows.
     */
    public List<String[]> getFailedRecords(String jobId) {
        Log.info("Fetching Failed records...");
        List<String[]> dataList = new ArrayList<>();
        if(jobId == null) {
            throw new RuntimeException("Job Id is Mandatory");
        }
        try {
            ResponseObj responseObj = wa.doGet(DATA_LOAD_EXPORT_FAILURES + jobId, headers.getAllHeaders());
            if(responseObj.getContent()!=null || responseObj.getContent()!="") {
                for(String rowData : responseObj.getContent().split("\n")) {
                    String[] row = rowData.split(",");  //We are splitting by "," this may result in inappropriate values if there's "," in the data(Please use with caution).
                    dataList.add(row);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to fetch records " ,e);
            throw new RuntimeException("Failed to fetch records " +e.getLocalizedMessage());
        }
        return dataList;
    }

    /**
     * Initiates Clear all collection.
     *
     * @param collectionName - Collection Name / Subject Area Name.
     * @param sourceType
     * @param targetType
     * @return JOB ID of the operation triggered.
     */
    public String clearAllCollectionData(String collectionName, String sourceType, String targetType) {
        DataLoadMetadata metadata = new DataLoadMetadata();
        metadata.setSourceType(sourceType);
        metadata.setTargetType(targetType);
        metadata.setHeaderRow(false);
        metadata.setCollectionName(collectionName);
        metadata.setClearOperation("CLEAR_ALL_DATA");
        metadata.setDbNameUsed(false);

        return clearAllCollectionData(metadata);
    }

    /**
     * Initiates Clear all Collection, returns the Job Id.
     *
     * @param metadata - Metadata / payload to clear all data.
     * @return - JOB Id.
     */
    public String clearAllCollectionData(DataLoadMetadata metadata) {
        Log.info("Clearing All Collection Data...");

        String jobId = null;
        try {
            headers.removeHeader("Content-Type");
            Log.info(mapper.writeValueAsString(metadata));
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            StringBody value = new StringBody(mapper.writeValueAsString(metadata), ContentType.APPLICATION_JSON);
            builder.addPart("metadata", value);

            HttpEntity reqEntity = builder.build();

            ResponseObj responseObj = wa.doPost(DATA_LOAD_IMPORT, headers.getAllHeaders(), reqEntity);
            Log.info("Response Obj : " +responseObj.toString());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    HashMap<String, String> serverData = (HashMap<String, String>) mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                    Log.info("Status ID : " + serverData.get("statusId"));
                    jobId = serverData.get("statusId");
                }
            }
        } catch (Exception e) {
            Log.error("Clear All Collection Data Failed.", e);
        } finally {
            headers.addHeader("Content-Type", "application/json");
        }
        return jobId;
    }

    /**
     * Verifies the Collection Columns, Column Data Type and checks DB Name is not null in actual collection.
     *
     * @param expected - Expected Collection Info.
     * @param actual - Actual Collection Info.
     */
    public static boolean verifyCollectionInfo(CollectionInfo expected, CollectionInfo actual) {
        if(!expected.getCollectionDetails().getCollectionName().equals(actual.getCollectionDetails().getCollectionName())) {
            throw new RuntimeException("Collection Name doesn't match, Expected Collection Name : " +
                    expected.getCollectionDetails().getCollectionName()+" Found Collection Name : "+
                    actual.getCollectionDetails().getCollectionName());
        }
        if(expected.getColumns().size() != actual.getColumns().size() ) {
            throw new RuntimeException("No of Columns doesn't match, Expected no of columns : "+
                    expected.getColumns().size()+ " Found no of columns : "+
                    actual.getColumns().size());
        }

        boolean result = false;
        for(CollectionInfo.Column expColumn : expected.getColumns()) {
            for(CollectionInfo.Column actualColumn : actual.getColumns()) {
                if(expColumn.getDisplayName().equals(actualColumn.getDisplayName())) {
                    if(actualColumn.getDbName()==null) {
                        throw new RuntimeException("DB Found empty on Column - " +actualColumn.getDisplayName());
                    }
                    if(expColumn.getDatatype().equals(actualColumn.getDatatype())) {
                        result = true;
                        break;
                    }
                }
            }
            if(!result) {
                throw new RuntimeException("Column Data Type Not Matched, Expected Data Type : "+expColumn.getDatatype() +
                " on Column " + expColumn.getDisplayName());
            }
            result =false;
        }
        return true;
    }

    /**
     * Covert the object to a readily usable POJO class.
     *
     * @param content - Object that's returned after subject area creation in ResponseObj.getData() method.
     * @return - Collection Detail Pojo populated.
     */
    public CollectionInfo.CollectionDetails getCollectionDetail (Object content) {
        HashMap<String, String> serverCollectionData = mapper.convertValue(content, HashMap.class);
        CollectionInfo.CollectionDetails colDetails = new CollectionInfo.CollectionDetails();
        colDetails.setCollectionId(serverCollectionData.get("collectionId"));
        colDetails.setDbCollectionName(serverCollectionData.get("dbCollectionName"));
        return colDetails;
    }

    /**
     * Trims the string fields to 250 characters and appends "...".
     * @param dataList - Data List to trim.
     * @param collectionInfo
     */
    public static void trimStringDataColumns(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        if (dataList == null || collectionInfo == null) {
            throw new IllegalArgumentException("DataList, Collection Info Should not be null.");
        }
        Map<String, Integer> columnsToTrim = new HashMap<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            if(column.getDatatype().equalsIgnoreCase("String")) {
                Log.info("Column : " +column.getDisplayName());
                columnsToTrim.put(column.getDisplayName(), column.getMaxLength());
            }
        }
        Log.info("Total String Columns Found : " + columnsToTrim.size());
        if(columnsToTrim.size() == 0) {
            Log.info("No String Fields Found to trim.");
            return;
        }

        for(Map<String , String> data : dataList) {
            for(String key : columnsToTrim.keySet()) {
                if(data.get(key) != null && data.get(key).length() > columnsToTrim.get(key)) {
                    data.put(key, data.get(key).substring(0, columnsToTrim.get(key)-3)+"...");
                }
            }
        }
    }

    /**
     * Clears all the data in the collection and deletes the collection.
     * @param tenantId - Tenant Id
     * @param collectionsIdsToDelete - Collection Id to delete.
     */
    public void deleteAllCollections(List<String> collectionsIdsToDelete, String tenantId) {
        Log.info("Total no of collection to delete : " + collectionsIdsToDelete.size());
        if(collectionsIdsToDelete == null || collectionsIdsToDelete.size() ==0) {
            Log.info("No Collections To Delete");
            return;
        }
        Map<String, CollectionInfo.CollectionDetails> collectionInfoMap = new HashMap<>();
        for(CollectionInfo collectionInfo : getAllCollections() ) {
            collectionInfoMap.put(collectionInfo.getCollectionDetails().getCollectionId(), collectionInfo.getCollectionDetails());
        }
        for(String colId : collectionsIdsToDelete) {
            if(collectionInfoMap.containsKey(colId)) {
                String jobId = clearAllCollectionData(collectionInfoMap.get(colId).getCollectionName(), "FILE", collectionInfoMap.get(colId).getDataStoreType());
                waitForDataLoadJobComplete(jobId);
                if(TenantManager.getInstance().deleteSubjectArea(tenantId, colId)) {
                    Log.info("Collection Deleted Successfully " +colId);
                } else {
                    Log.error("Failed to delete collection id : "+colId);
                }
            } else {
                Log.error("Collection Id Doesn't Exists to delete : " +colId);
            }
        }
        Log.info("Deleted all collection data & collections");
    }


    /**
     * Clears all the data in the collection and deletes the collection.
     * @param tenantId - Tenant Id
     * @param collectionDetailList - CollectionDetails.
     */
    public void deleteAllCollections(String tenantId, List<CollectionInfo.CollectionDetails> collectionDetailList) {
        Log.info("Total no of collection to delete : " + collectionDetailList.size());
        if(tenantId == null || collectionDetailList == null) {
            throw new RuntimeException("Tenant Id, Collection Details List should not be null.");
        }
        for(CollectionInfo.CollectionDetails collectionDetail : collectionDetailList) {
                String jobId = clearAllCollectionData(collectionDetail.getCollectionName(), "FILE", collectionDetail.getDataStoreType());
                if(waitForDataLoadJobComplete(jobId)) {
                    TenantManager.getInstance().deleteSubjectArea(tenantId, collectionDetail.getCollectionId());
                } else {
                    throw new RuntimeException("Data deleting is not completed. i.e. data load job is still running then the max wait time. ");
                }
            }
        Log.info("Deleted all collection data & collections");
    }

    /**
     * Returns the metadata that to be used for loading the data via data load api.
     * @param collectionInfo - CollectionInfo Pojo class
     * @return metadata to be used while loading data.
     */
    public DataLoadMetadata getDefaultDataLoadMetaData(CollectionInfo collectionInfo) {
        DataLoadMetadata metadata = new DataLoadMetadata();
        metadata.setCollectionName(collectionInfo.getCollectionDetails().getCollectionName());
        metadata.setDataLoadOperation(DataLoadOperationType.INSERT.name());


        List<DataLoadMetadata.Mapping> mappings = new ArrayList<>();
        DataLoadMetadata.Mapping mapping = null;
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            mapping = new DataLoadMetadata.Mapping();
            mapping.setSource(column.getDisplayName());
            mapping.setTarget(column.getDisplayName());
            mappings.add(mapping);
        }
        metadata.setMappings(mappings);
        return metadata;
    }


    /**
     * Add the data load DTO mapping for fields.
     * When you are using display names as mapping then this helps.
     * @param metadata
     * @param keys
     */
    public static void addMapping(DataLoadMetadata metadata, String[] keys) {
        if(metadata ==null || keys ==null) {
            throw new IllegalArgumentException("metadata or keys can't be null");
        }
        if(metadata.getMappings()==null) {
            metadata.setMappings(new ArrayList<DataLoadMetadata.Mapping>());
        }
        for(String key : keys) {
            DataLoadMetadata.Mapping mapping = new DataLoadMetadata.Mapping();
            mapping.setSource(key);
            mapping.setTarget(key);
            metadata.getMappings().add(mapping);
        }
    }

}




