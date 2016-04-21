package com.gainsight.bigdata.gsData.apiImpl;

import com.gainsight.bigdata.gsData.pojos.COMFilters;
import com.gainsight.bigdata.gsData.pojos.COMMetadata;
import com.gainsight.bigdata.gsData.pojos.CollectionDependency;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.ws.commons.util.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gainsight.bigdata.urls.ApiUrls.*;


/**
 * Created by Giribabu on 17/08/15.
 */
public class GSDataImpl {

    ObjectMapper mapper = new ObjectMapper();
    WebAction wa = new WebAction();
    Header header;
    public GSDataImpl(Header header) {
        this.header = header;

    }

    /**
     * Get the Tenant information like Tenantid, Tenant Name, is redshift enabled etc.
     * @param orgId - Org id to fetch the details.
     * @return - TenantInfo all the properties.
     * @throws Exception - if request to server fails/ Org id not found.
     */
    public TenantInfo getTenantInfo(String orgId) throws Exception {
        if(orgId == null) {
            throw new IllegalArgumentException("Org id can't be null.");
        }
        Log.info("Getting Tenant Information with org Id...");
        ResponseObj responseObj = wa.doGet(TENANT_INFO_LITE + orgId, header.getAllHeaders());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                TenantInfo tenantInfo = mapper.convertValue(nsResponseObj.getData(), TenantInfo.class);
                Log.info("Tenant Id : " +tenantInfo.getTenantId());
                return tenantInfo;
            }
        }
        throw new RuntimeException("No Tenant Information found with the supplied org id Or ." +responseObj.toString());
    }

    public CollectionInfo getCollectionMasterByName(String collectionName) throws IOException {
        COMMetadata metadata = mapper.readValue("{\"limit\":1,\"skip\":0,\"whereAdvanceFilter\":{\"filters\":[{\"dbName\":\"CollectionDetails.CollectionName\",\"alias\":\"A\",\"dataType\":\"string\",\"filterOperator\":\"EQ\",\"logicalOperator\":\"AND\",\"filterValues\":[\""+collectionName+"\"]}],\"expression\":\"A\"},\"includeFields\":[\"CollectionDetails\",\"CollectionDescription\",\"createdByName\",\"createdDate\",\"modifiedByName\",\"modifiedDate\",\"Columns\"]}", COMMetadata.class);
        NsResponseObj nsResponseObj = getCustomObjectsDetailsNsResponse(metadata);
        if(nsResponseObj.isResult()) {
            List<HashMap<String, Object>> response = null;
            if(nsResponseObj != null && nsResponseObj.isResult() && nsResponseObj.getData()!= null) {
                response = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<HashMap<String, Object>>>() {});
                if(response != null && response.size() >= 0 && response.get(0).containsKey("collectionMaster")) {
                    Object serverResponse = response.get(0).get("collectionMaster");
                    CollectionInfo collectionInfo = mapper.convertValue(serverResponse, CollectionInfo.class);
                    return collectionInfo;
                }
            }
        }
        throw new RuntimeException("Failed to get Collection master with collection name :" +collectionName);
    }

    /**
     * Get the custom objects details.
     * @param metadata - Filter to filter the objects.
     * @return - NsResponseObj
     */
    public NsResponseObj getCustomObjectsDetailsNsResponse(COMMetadata metadata) {
        String payload = null;
        try {
            payload = mapper.writeValueAsString(metadata);
        } catch (IOException e) {
            Log.error("Failed to parse", e);
            throw new RuntimeException(e);
        }
        return getCustomObjectsDetailsNsResponse(payload);
    }

    /**
     * Get the custom objects with details.
     * @param payload - Filter criteria.
     * @return
     */
    public NsResponseObj getCustomObjectsDetailsNsResponse(String payload) {
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = wa.doPost(COLLECTION_DETAILS_POST, header.getAllHeaders(), payload);
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception e) {
            Log.error("Failed to get the collection details.", e);
            throw new RuntimeException(e);
        }
        return nsResponseObj;
    }

    /**
     * Get Custom Object details.
     * @param metadata - Filter.
     * @return List of objects with their properties.
     */
    public List<HashMap<String, Object>> getCustomObjectsDetails(COMMetadata metadata) {
        String payload = null;
        try {
            payload = mapper.writeValueAsString(metadata);
        } catch (IOException e) {
            Log.error("Failed to parse", e);
            throw new RuntimeException(e);
        }
        return getCustomObjectsDetails(payload);
    }

    /**
     * Get Custom object details.
     * @param payload - Filters.
     * @return - List of objects with their properties.
     */
    public List<HashMap<String, Object>> getCustomObjectsDetails(String payload) {
        NsResponseObj nsResponseObj = getCustomObjectsDetailsNsResponse(payload);
        List<HashMap<String, Object>> results = null;
        if(nsResponseObj != null && nsResponseObj.isResult() && nsResponseObj.getData()!= null) {
            results = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<HashMap<String, Object>>>() {
            });
        } else {
            Log.error("No results fetched.");
        }
        return results;
    }

    /**
     * Get Collection info of a single collection.
     * @param collectionId - Collection Id to retrive the details.
     * @param showHidden - Weather to retrive hidden fields or not.
     * @param resolveLookup - Weather to resolve look up details like - Name of looked up object & display name for lookedup field.
     * @return - Nsresponse Object.
     * @throws Exception
     */
    public NsResponseObj getCollectionMasterDetailsNsResponse(String collectionId, boolean showHidden, boolean resolveLookup) throws Exception {
        String payload = "{\"collectionId\":\""+collectionId+"\", \"showHidden\": "+showHidden+", \"resolveLookup\": "+resolveLookup+"}";
        ResponseObj responseObj = wa.doPost(COLLECTION_DETAIL, header.getAllHeaders(), payload);
        Log.info("Response Obj :  " +responseObj.toString());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to get Collection Details");
        }
        return nsResponseObj;
    }

    /**
     * Get Collection info of a single collection.
     * @param collectionId - Collection Id to retrive the details.
     * @return - CollectionDetails and its properties.
     * @throws Exception
     */
    public HashMap<String, Object> getCollectionMasterDetails(String collectionId) throws Exception {
        NsResponseObj nsResponseObj  = getCollectionMasterDetailsNsResponse(collectionId, true, true);
        if(nsResponseObj == null && !nsResponseObj.isResult()) {
            throw new RuntimeException("NS Reponse Object is null or result is false.");
        }
        return mapper.convertValue(nsResponseObj.getData(), new TypeReference<HashMap<String, Object>>(){});
    }

    /**
     * Get Collection info of a single collection.
     * @param collectionId - Collection id to retrive the details.
     * @return
     * @throws Exception
     */
    public CollectionInfo getCollectionMaster(String collectionId) throws Exception {
        CollectionInfo collectionInfo = null;
        HashMap<String, Object> customObjectDetails = getCollectionMasterDetails(collectionId);
        if(customObjectDetails !=null && customObjectDetails.containsKey("collectionMaster")) {
            collectionInfo = mapper.convertValue(customObjectDetails.get("collectionMaster"), CollectionInfo.class);
        } else {
            Log.error("collectionMaster key is not found in the collection details retrived.");
        }
        return collectionInfo;
    }

    /**
     * To retrive all the depencies of a collection.
     * @param customObjectDetails
     * @return
     */
    public List<CollectionDependency>  getCollectionMasterReferences(HashMap<String, Object> customObjectDetails) {
        List<CollectionDependency> referenceList = null;
        if(customObjectDetails !=null && customObjectDetails.containsKey("collectionMasterReferences")) {
            referenceList = mapper.convertValue(customObjectDetails.get("collectionMasterReferences"), new TypeReference<ArrayList<CollectionDependency>>(){});
        } else {
            Log.error("collectionMasterReferences key is not found in the collection details retrived.");
        }
        return referenceList;
    }

    /**
     * Get the collection dependency.
     * @param collectionId - Collection to fetch the dependency.
     * @return - List of all dependent entities.
     * @throws Exception
     */
    public List<CollectionDependency>  getCollectionMasterReferences(String collectionId) throws Exception {
        HashMap<String, Object> customObjectDetails =  getCollectionMasterDetails(collectionId);
        return getCollectionMasterReferences(customObjectDetails);
    }

    /**
     * Create custom object.
     * @param collectionInfo - Payload to create a custom object.
     * @return - Collection Id.
     * @throws Exception
     */
    public String createCustomObject(CollectionInfo collectionInfo) throws Exception {
        return createCustomObject(mapper.writeValueAsString(collectionInfo));
    }

    /**
     * Create a custom object.
     * @param payload - Payload to create a custom object.
     * @return - Collection Id.
     * @throws Exception
     */
    public String createCustomObject(String payload) throws Exception {
        String collectionId = null;
        NsResponseObj nsResponseObj = createCustomObjectGetNsResponse(payload);
        if(nsResponseObj.getData() !=null) {
            HashMap<String, String> response = mapper.convertValue(nsResponseObj.getData(), new TypeReference<HashMap<String, String>>(){});
            collectionId = response.get("collectionId");
        } else {
            Log.error("Data in response Object is null.");
        }
        Log.info("Collection Id "+collectionId);
        return collectionId;
    }

    /**
     *
     * @param payload - Custom object metadata.
     * @return - NsReposnse object after creating the custom object.
     * @throws Exception
     */
    public NsResponseObj createCustomObjectGetNsResponse(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(ApiUrls.COLLECTION_CREATE_OR_UPDATE, header.getAllHeaders(), payload);
        Log.info("Response Obj : " +responseObj.toString());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST
                || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //internal server should be removed once the bug in the product is fixed.
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to create / update custom object.");
        }
        return nsResponseObj;
    }

    /**
     * Update Custom object
     * @param payload - Custom object updated schema.
     * @return
     * @throws Exception
     */
    public NsResponseObj updateCustomObjectGetNsResponse(String payload) throws Exception {
        ResponseObj responseObj = wa.doPut(ApiUrls.COLLECTION_CREATE_OR_UPDATE, payload, header.getAllHeaders());
        Log.info("Response Obj : " +responseObj.toString());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST ||
                responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else  {
            throw new RuntimeException("Response Obj :" +responseObj.toString());
        }
        return nsResponseObj;
    }

    /**
     * Verify if the data provided is valid or not.
     * @param metadata - Data load Mapping.
     * @param file - File to verify.
     * @return true if data is valid & false is data is not valid.
     * @throws IOException
     */
    public boolean isValidDataProvided(String metadata, File file) throws IOException {
        boolean result = false;
        ResponseObj responseObj = sendDataForValidation(metadata, file);
        if(responseObj!=null && responseObj.getStatusCode()==HttpStatus.SC_OK ) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            result = nsResponseObj.isResult();
        }
        Log.info("Returning Data Validation With : " +result);
        return result;
    }

    /**
     * Send data to validation and download the failure results.
     * @param metadata - data load mapping.
     * @param file - File to send for validations.
     * @param resultFile - writes the output results to following file.
     * @throws IOException
     */
    public void validateDataAndDownloadFailures(String metadata, File file, File resultFile) throws IOException {
        ResponseObj responseObj = sendDataForValidation(metadata, file);
        if(responseObj != null && responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(!nsResponseObj.isResult() && nsResponseObj.getData() != null
                    && MDAErrorCodes.VALIDATION_FAILED.getGSCode().equals(nsResponseObj.getErrorCode())) {
                byte[] decode = Base64.decode((String) nsResponseObj.getData());
                FileUtils.writeByteArrayToFile(resultFile, decode);
                Log.info("Finished Downloading file : " + resultFile);
            } else {
                Log.error("No data found to download.");
                throw new RuntimeException("Response content should not be null.");
            }
        } else {
            throw new RuntimeException("Http status code should be 200 OK.");
        }
    }

    /**
     * Sends data for validation.
     * @param metadata - Data load mapping.
     * @param file - File to upload.
     * @return -Response object.
     */
    public ResponseObj sendDataForValidation(String metadata, File file) {
        if(metadata ==null || file == null || !file.exists()) {
            throw new RuntimeException("Metadata, File should be present.");
        }
        Log.info("Started sending data to MDA...");
        Log.info("MetaData : " +metadata);
        header.removeHeader("Content-Type");
        ResponseObj responseObj =null;
        try {
            responseObj = wa.doPost(COLLECTION_DATA_VALIDATE, header.getAllHeaders(), buildHttpEntity(metadata, "metadata", file));
            Log.info("Response Obj : " +responseObj.toString());
        } catch (Exception e) {
            Log.error("Failed to send data", e);
            throw new RuntimeException("Failed to load data, "+e);
        } finally {
            header.addHeader("Content-Type","application/json");
        }
        Log.info("Data sent to MDA.");

        return responseObj;
    }

    /**
     * Loads data to MDA.
     * @param metadata - data load mapping.
     * @param file - file to upload.
     * @return - NsResponseObject
     */
    public NsResponseObj loadDataToMDA(String metadata, File file) {
        Log.info("Started sending data to MDA...");
        header.removeHeader("Content-Type");
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = wa.doPost(COLLECTION_DATA_LOAD, header.getAllHeaders(), buildHttpEntity(metadata, "metadata", file));
            Log.info("Response Obj : "+responseObj.toString());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception e) {
            Log.error("Failed to send data", e);
            throw new RuntimeException("Failed to load data, "+e);
        } finally {
            header.addHeader("Content-Type","application/json");
        }
        Log.info("Data loaded to MDA.");
        return nsResponseObj;
    }


    /**
     * Build Http form entity.
     * @param metadata - data load mapping.
     * @param name - mapping key name.
     * @param file - File to be included in form data.
     * @return Http build entity.
     */
    public static HttpEntity buildHttpEntity(String metadata, String name,  File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        StringBody value = new StringBody(metadata, ContentType.APPLICATION_JSON);
        builder.addPart(name, value);

        FileBody body = new FileBody(file, ContentType.MULTIPART_FORM_DATA);
        builder.addPart("file", body);
        HttpEntity httpEntity = builder.build();
        return httpEntity;
    }

    /**
     * Gets the dataload mapping - auto infer.
     * @param collectionMaster - Collection master schema
     * @param file - File to get the data load mapping.
     * @return - NsResponse Object.
     */
    public NsResponseObj getDataLoadMappingNsResponse(String collectionMaster, File file) {
        Log.info("Started sending data to MDA...");
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = getDataLoadMapping(collectionMaster, file);
            if(responseObj.getStatusCode()==HttpStatus.SC_OK || responseObj.getStatusCode()==HttpStatus.SC_BAD_REQUEST) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception e) {
            Log.error("Failed to send data for mapping", e);
            throw new RuntimeException("Failed to send data for mapping, "+e);
        }
        return nsResponseObj;
    }

    /**
     * Gets dataload mappung - auto infer.
     * @param collectionMaster - Collection master schema.
     * @param file - File to get the auto infer details.
     * @return
     */
    public ResponseObj getDataLoadMapping(String collectionMaster, File file) {
        Log.info("Started sending data to MDA...");
        header.removeHeader("Content-Type");
        ResponseObj responseObj = null;
        try {
            responseObj = wa.doPost(COLLECTION_DATA_MAPPING, header.getAllHeaders(), buildHttpEntity(collectionMaster, "collectionMaster", file));
            Log.info("Respone Obj : " +responseObj.toString());
        } catch (Exception e) {
            Log.error("Failed to send data for mapping", e);
            throw new RuntimeException("Failed to send data for mapping, "+e);
        } finally {
            header.addHeader("Content-Type","application/json");
        }
        return responseObj;
    }

    /**
     * Get the collection id of collection with name
     * @param collectionName - Collection name to retrive the collection id.
     * @return - Colleciton id.
     */
    public String getCollectionId(String collectionName) {
        String collectionId = null;
        COMMetadata metadata = getSimpleFilterMetadata("CollectionDetails.CollectionName", "string", new String[]{collectionName}, new String[]{"CollectionDetails"});
        List<HashMap<String, Object>>  response = getCustomObjectsDetails(metadata);
        if(response != null && response.size() >= 0 && response.get(0).containsKey("collectionMaster")) {
            Object serverResponse = response.get(0).get("collectionMaster");
            CollectionInfo collectionInfo = mapper.convertValue(serverResponse, CollectionInfo.class);
            collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        } else {
            Log.error("Failed to collection collection id, check the logs above.");
        }
        return collectionId;
    }

    /**
     * Get DB collection name of collection with name
     * @param collectionName - Collection name to retrive the collection id.
     * @return - DB Collection Name.
     */
    public String getCollectionDBName(String collectionName) {
        String dbName = null;
        COMMetadata metadata = getSimpleFilterMetadata("CollectionDetails.CollectionName", "string", new String[]{collectionName}, new String[]{"CollectionDetails"});
        List<HashMap<String, Object>>  response = getCustomObjectsDetails(metadata);
        if(response != null && response.size() >= 0 && response.get(0).containsKey("collectionMaster")) {
            Object serverResponse = response.get(0).get("collectionMaster");
            CollectionInfo collectionInfo = mapper.convertValue(serverResponse, CollectionInfo.class);
            dbName = collectionInfo.getCollectionDetails().getDbCollectionName();
        } else {
            Log.error("Failed to collection collection id, check the logs above.");
        }
        return dbName;
    }

    /**
     * Builds the base metadata filter.
     * @param dbName - DBname to filter. collection master key fields.
     * @param dataType - Data type.
     * @param value - values.
     * @param includeFields  - Fields to be retrived .
     * @return - Metadata filter.
     */
    public static COMMetadata getSimpleFilterMetadata(String dbName, String dataType, Object[] value, String[] includeFields) {
        COMMetadata metadata = new COMMetadata();
        metadata.setIncludeFields(includeFields);
        metadata.setLimit(1);
        COMFilters comFilter = new COMFilters();
        comFilter.setDbName(dbName);
        comFilter.setAlias("A");
        comFilter.setDataType(dataType);
        comFilter.setFilterOperator("EQ");
        comFilter.setLogicalOperator("AND");
        comFilter.setFilterValues(value);
        COMMetadata.WhereAdvanceFilter whereAdvanceFilter = new COMMetadata.WhereAdvanceFilter();
        whereAdvanceFilter.setExpression("A");
        whereAdvanceFilter.setFilters(new COMFilters[]{comFilter});
        metadata.setWhereAdvanceFilter(whereAdvanceFilter);
        return metadata;
    }

    /**
     * Fetch the collection CURL of a collection.
     * @param collectionId
     * @return
     * @throws Exception
     */
    public String getCURL(String collectionId) throws Exception {
        if(collectionId ==null) {
            throw new RuntimeException("Collection should not be null.");
        }
        String cURL = null;

        ResponseObj responseObj = wa.doGet(String.format(ApiUrls.COLLECTION_CURL_GENERATE, collectionId), header.getAllHeaders());
        Log.info("ReponseObj : "+responseObj.getContent());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
          NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                cURL = mapper.writeValueAsString(nsResponseObj.getData());
            }
        }
        if(cURL==null) {
            throw new RuntimeException("Failed to generate cURL");
        }
        return cURL;
    }

    /**
     * Load large files to mda ASYNC.
     * @param metadata - data load mapping
     * @param file - File to load.
     * @return - ResponseObject.
     * @throws Exception
     */
    public ResponseObj loadDataToMDAAsync(String metadata, File file) throws Exception {
        try {
            header.removeHeader("Content-Type");
            HttpEntity httpEntity = buildHttpEntity(metadata, "metadata", file);
            ResponseObj responseObj = wa.doPost(COLLECTION_DATA_ASYNC_IMPORT, header.getAllHeaders(), httpEntity);
            Log.info("Response Data : " +responseObj.toString());
            return responseObj;
        } finally {
            header.addHeader("Content-Type", "application/json");
        }
    }

    /**
     * Load large data files to MDA via async.
     * @param metadata - Metadata mapping.
     * @param file -File to be updated.
     * @return - Status id of the upload request.
     * @throws Exception
     */
    public String loadDataToMDAAsyncAndGetStatusId(String metadata, File file) throws Exception {
        String statusId = null;
        ResponseObj responseObj = loadDataToMDAAsync(metadata, file);
        if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                HashMap<String, String> serverData = (HashMap<String, String>)mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                Log.info("Status ID : " + serverData.get("statusId"));
                statusId = serverData.get("statusId");
            } else {
                throw new RuntimeException("Failed to load data");
            }
        }
        Log.info("Data Load Status ID : " +statusId);
        return statusId;
    }
}
