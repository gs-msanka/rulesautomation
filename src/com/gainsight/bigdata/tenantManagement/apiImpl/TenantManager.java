package com.gainsight.bigdata.tenantManagement.apiImpl;


import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.NsConfig;
import com.gainsight.util.SfdcConfig;
import com.gainsight.util.ConfigLoader;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gainsight.bigdata.urls.AdminURLs.*;
import static com.gainsight.sfdc.pages.Constants.INTERVAL_TIME;
import static com.gainsight.sfdc.pages.Constants.MAX_WAIT_TIME;

/**
 * Created by Giribabu on 07/05/15.
 * Create, Update, Delete of Tenants and Subject Areas.
 */
public class TenantManager {

    private final Application env = new Application();
    public SFDCInfo sfdcInfo;
    private SalesforceConnector sfConnector;
    private Header header = new Header();
    private WebAction wa = new WebAction();
    private ObjectMapper mapper = new ObjectMapper();
    private SfdcConfig sfdcConfig = ConfigLoader.getSfdcConfig();
    private NsConfig nsConfig = ConfigLoader.getNsConfig();

    /**
     * Logs in to tenant Management SFDC org and sets up the default headers required.
     */

    public static TenantManager tenantManager = null;

    public static TenantManager getInstance() {
        if(tenantManager ==null) {
            tenantManager = new TenantManager();
            return tenantManager;
        } else {
            return tenantManager;
        }
    }

    private TenantManager() {
        sfConnector = new SalesforceConnector(nsConfig.getSfdcUsername(), nsConfig.getSfdcPassword() + nsConfig.getSfdcStoken(),
                sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
        if (!sfConnector.connect()) {
            throw new RuntimeException("Failed to Connect to salesforce - Check your admin credentials.");
        }
        sfdcInfo = sfConnector.fetchSFDCinfo();
        header.addHeader("Origin", sfdcInfo.getEndpoint());
        header.addHeader("Content-Type", "application/json");
        header.addHeader("appOrgId", sfdcInfo.getOrg());
        header.addHeader("appSessionId", sfdcInfo.getSessionId());
        header.addHeader("appUserId", sfdcInfo.getUserId());
        
		String authToken = CommonWait.waitForCondition(MAX_WAIT_TIME, INTERVAL_TIME, new ExpectedCommonWaitCondition<String>() {
					@Override
					public String apply() {
						return getMDAAuthToken();
					}
				});
        if (authToken == null || authToken == "") {
            throw new RuntimeException("Failed to generate auth token");
        }
        header.removeHeader("authToken");
        header.addHeader("authToken", authToken);
    }

    /**
     * To create a new tenant in MDA environment.
     *
     * @param tenantDetails - Tenant Related Information for creating a new Tenant.
     * @return - true if tenant creation is successful and updates the TenantId in tenantDetails if tenant already exists.
     */
    public boolean createTenant(TenantDetails tenantDetails) {
        Log.info("Creating Tenant...");
        boolean result = false;
        tenantDetails.setExternalTenantID(NSUtil.convertSFID_15TO18(tenantDetails.getExternalTenantID()));
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_TENANTS, header.getAllHeaders(), mapper.writeValueAsString(tenantDetails));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    HashMap<String, String> data = (HashMap<String, String>) nsResponseObj.getData();
                    if (data.get("TenantId") != null) {
                        result = true;
                        tenantDetails.setTenantId(data.get("TenantId"));
                    }
                } else {
                    Log.info(nsResponseObj.getErrorCode() + " ::: " + nsResponseObj.getErrorDesc());
                }
            } else if (responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (MDAErrorCodes.TENANT_ALREADY_EXIST.getGSCode().equals(nsResponseObj.getErrorCode())) {
                    Log.info(nsResponseObj.getErrorDesc());
                    tenantDetails.setTenantId(getTenantDetail(tenantDetails.getExternalTenantID(), null).getTenantId());
                }
            }
        } catch (Exception e) {
            Log.error("Failed to create tenant");
        }
        return result;
    }

    /**
     * Update the Tenant Details in MDA environment.
     *
     * @param tenantDetails - Updates the Tenant with the provided information.
     * @return - true if tenant update is successful.
     */
    public boolean updateTenant(TenantDetails tenantDetails) {
        Log.info("Updating the Tenant...");
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPut(ADMIN_TENANTS + "/" + tenantDetails.getTenantId(), mapper.writeValueAsString(tenantDetails), header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    result = true;
                } else {
                    Log.error(nsResponseObj.getErrorCode() + " ::: " + nsResponseObj.getErrorDesc());
                }
            }
        } catch (Exception e) {
            Log.error("Failed to update the tenant ", e);
            throw new RuntimeException("Failed to update the tenant " +e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * If tenant Id is null, will get the tenant id with provided SFOrg Id.
     *
     * @param sfOrgId  - Salesforce Organization Id (15 or 18)
     * @param tenantId - Tenant Id - to uniquely identify the tenant in MDA Environment.
     * @return True - If tenantId / sfOrgId is found.
     */
    public boolean deleteTenant(String sfOrgId, String tenantId) {
        Log.info("Deleting Tenant...");
        boolean result = false;
        String url = ADMIN_TENANTS;
        if (tenantId != null) {
            url = url + "/" + tenantId;
        } else {
            TenantDetails tenantDetail = getTenantDetail(sfOrgId, null);
            if (tenantDetail != null && tenantDetail.getTenantId() != null) {
                url = url + "/" + tenantDetail.getTenantId();
            }
        }
        Log.info("Delete Tenant URL " + url);
        try {
            ResponseObj responseObj = wa.doDelete(url, header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    Log.info("Tenant Deleted Successfully");
                    result = true;
                }
            } else {
                Log.error("Failed to deleted Tenant");
            }
        } catch (Exception e) {
            Log.error("Failed to deleted Tenant", e);
        }


        return result;
    }

    /**
     * Makes the Initial Call to MDA & returns the auth token that's returned by MDA in headers.
     *
     * @return - AuthToken - Which can be used for further communication with MDA, NULL(or) runtime exception on failure.
     */
    private String getMDAAuthToken() {
        Log.info("Setting MDA Auth Token...");
        String authToken = null;
        header.removeHeader("authToken");
        header.addHeader("authToken", "initialcall");
        try {
            ResponseObj responseObj = wa.doGet(ADMIN_TENANTS, header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                org.apache.http.Header[] headers = responseObj.getAllHeaders();
                for (org.apache.http.Header h : headers) {
                    if (h.getName() != null && h.getName().equalsIgnoreCase("authToken")) {
                        authToken = h.getValue();
                        Log.info("Auth Token :" + authToken);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get Auth Token : ", e);
            throw new RuntimeException("Failed to get Auth Token");
        }
        return authToken;
    }

    /**
     * Checks if the tenant exists on MDA Environment or not.
     *
     * @param sfOrgId  - Salesforce Organization Id.
     * @param tenantId - MDA Tenant Id.
     * @return True if tenant ID/SFOrg ID exists, false if tenant doesn't exists.
     */
    public boolean isTenantPresent(String sfOrgId, String tenantId) {
        Log.info("Verifying Tenant Present...");
        TenantDetails tenantDetail = getTenantDetail(sfOrgId, tenantId);
        if (tenantDetail != null) {
            Log.info("Found Tenant, Tenant Org Id ::: " + tenantDetail.getExternalTenantID() + " , Tenant Id ::: " + tenantDetail.getTenantId());
            return true;
        }
        return false;
    }

    /**
     * Gets all the tenants if SFOrgId is present and gets the with tenant id, gets all other tenant related information.
     *
     * @param sfOrgId  - Salesforce Organization Id.
     * @param tenantId - MDA environment tenant Id.
     * @return TenantDetails if tenant exists and NULL if tenant doesn't exits.
     */
    public TenantDetails getTenantDetail(String sfOrgId, String tenantId) {
        TenantDetails tenantDetail = null;
        String url = ADMIN_TENANTS;

        if (tenantId != null) {
            Log.info("Getting Tenant Details : " +tenantId);
            url = ADMIN_TENANTS + "/" + tenantId;
            try {
                ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
                if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                    NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                    tenantDetail = mapper.convertValue(nsResponseObj.getData(), TenantDetails.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get Tenant Information");
            }
        } else {
            if(sfOrgId ==null || sfOrgId == "") {
                throw new IllegalArgumentException("Salesforce Org Id should not be null.");
            }
            sfOrgId = NSUtil.convertSFID_15TO18(sfOrgId);
            try {
                Log.info("Getting All Tenant Details to get one Tenant Detail...");
                ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
                if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                    NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                    if (nsResponseObj.isResult()) {
                        List<TenantDetails> tenantDetails = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<TenantDetails>>() {});
                        for (TenantDetails tD : tenantDetails) {
                            if (tD.getExternalTenantID() != null && tD.getExternalTenantID().equals(sfOrgId)) {
                                tenantDetail = tD;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("Some Exception in request " + e);
                throw new RuntimeException("Failed to get Tenant Details " +e.getLocalizedMessage());
            }
        }
        Log.info("Tenant ID : " +tenantDetail.getTenantId());
        Log.info("Org Id : " +tenantDetail.getExternalTenantID());
        return tenantDetail;
    }

    /**
     * Creates a new subject area for the tenant.
     *
     * @param tenantId - Tenant Id on which the subject area should be created.
     * @param payload  - Actual payload i.e. Subject Area Metadata.
     * @return - on success returns collection details, else returns NULL.
     */
    public CollectionInfo.CollectionDetails createSubjectArea(String tenantId, String payload) {
        Log.info("Creating Subject Area for tenant : "+tenantId);
        if (tenantId == null || tenantId.equals("") || payload == null) {
            Log.error("Tenant ID, payload are mandatory");
            throw new RuntimeException("Tenant ID, payload are mandatory");
        }
        header.addHeader("contextTenantId", tenantId);
        CollectionInfo.CollectionDetails collectionDetails = null;
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_COLLECTIONS, header.getAllHeaders(), payload);
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    HashMap<String, String> data = (HashMap<String, String>)mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                    collectionDetails = new CollectionInfo.CollectionDetails();
                    collectionDetails.setDbCollectionName(data.get("dbCollectionName"));
                    collectionDetails.setCollectionId(data.get("collectionId"));
                }
            } else if (responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                Log.error("Failed to create subject area");
            }
        } catch (Exception e) {
            Log.error("Failed while creating subject area" + e);
            throw new RuntimeException("Failed creating subject area" + e);
        } finally {
            header.removeHeader("contextTenantId");
        }
        return collectionDetails;
    }

    /**
     *  Creates a new Subject area for the tenant.
     * @param tenantId - Tenant Id of MDA Env.
     * @param collectionInfo - The Collection/Subject schema/metadata/
     * @return CollectionDetails - on success, null on failure.
     */
    public CollectionInfo.CollectionDetails createSubjectArea(String tenantId, CollectionInfo collectionInfo) {
        if (tenantId == null || tenantId.equals("") || collectionInfo == null) {
            Log.error("Tenant ID, collectionInfo are mandatory");
            throw new RuntimeException("Tenant ID, collectionInfo are mandatory");
        }
        CollectionInfo.CollectionDetails collectionDetails= null;
        String payload = "";
        try {
            payload = mapper.writeValueAsString(collectionInfo);
            Log.info("Collection Schema :" +payload);
            collectionDetails = createSubjectArea(tenantId, payload);
        } catch (IOException e) {
            Log.error("Failed while creating subject area" + e);
            throw new RuntimeException("Failed creating subject area" + e);
        }
        return collectionDetails;
    }

    /**
     * Gets all the subject area's of a tenant.
     *
     * @param tenantId      - MDA Environment Tenant Id.
     * @param fieldsToQuery - JSON object to query required fields.
     * @return - List of all the subject area for a tenant.
     */
    public List<CollectionInfo> getAllSubjectAreas(String tenantId, String fieldsToQuery) {
        Log.info("Getting All Subject Areas for a tenant Id " +tenantId);
        if (tenantId == null && tenantId.equals("")) {
            Log.error("Tenant ID is mandatory");
            throw new RuntimeException("Tenant Id mandatory");
        }
        header.addHeader("contextTenantId", tenantId);
        fieldsToQuery = "{\"includeFields\":\"id,CollectionDetails,createdByName,createdDate,modifiedByName,modifiedDate,TenantId\"}";
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_POST_COLLECTIONS_LIST, header.getAllHeaders(), fieldsToQuery);
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                List<CollectionInfo> collectionInfoList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<CollectionInfo>>() {});
                Log.info("No of Subject Areas Retrieved : " + collectionInfoList.size());
                return collectionInfoList;
            }
        } catch (Exception e) {
            Log.error("Failed to fetch all the subject areas.", e);
            throw new RuntimeException("Failed to get all subject areas, " + e);
        } finally {
            header.removeHeader("contextTenantId");
        }
        return new ArrayList<CollectionInfo>();
    }

    /**
     * Gets the the schema of a subject area.
     *
     * @param tenantId     - MDA Environment TenantID.
     * @param collectionId - Collection ID(Subject Area ID) to get the schema.
     * @return Returns the schema (collectionInfo) of the Subject area of a tenant.
     */
    public CollectionInfo getSubjectAreaMetadata(String tenantId, String collectionId) {
        Log.info("Getting Subject Area / Collection Schema for Tenant Id :" +tenantId + "And Collection Id "+collectionId);
        if (tenantId == null || collectionId == null) {
            Log.info("Both Tenant Id, Collection Id are Mandatory");
            throw new IllegalArgumentException("Both Tenant Id, Collection Id are Mandatory");
        }
        CollectionInfo collectionInfo = null;
        header.addHeader("contextTenantId", tenantId);
        try {
            ResponseObj responseObj = wa.doGet(ADMIN_COLLECTIONS + "/" + collectionId, header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    Log.info("Retrieved Collection Metadata Successfully");
                    collectionInfo = mapper.convertValue(nsResponseObj.getData(), CollectionInfo.class);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to fetch Collection Metadata", e);
            throw new RuntimeException("Failed to fetch Collection Metadata" + e);
        } finally {
            header.removeHeader("contextTenantId");
        }
        return collectionInfo;
    }

    /**
     * Gets the subject area on MDA env.
     *
     * @param tenantId     - MDA Environment TenantId.
     * @param collectionId - CollectionId (Subject Area ID) to delete.
     * @return - True of subject are deletion, false/Run time exception on failure.
     */
    public boolean deleteSubjectArea(String tenantId, String collectionId) {
        Log.info("Deleting Subject Area / Collection Id : " +collectionId + " For Tenant Id : " +tenantId);
        if (tenantId == null || collectionId == null) {
            Log.info("Both Tenant Id, Collection Id are Mandatory");
            throw new IllegalArgumentException("Both Tenant Id, Collection Id are Mandatory");
        }
        boolean result = false;
        header.addHeader("contextTenantId", tenantId);
        try {
            ResponseObj responseObj = wa.doDelete(ADMIN_COLLECTIONS + "/" + collectionId, header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    result = true;
                    Log.info("Collection Deleted SuccessFully");
                }
            }
        } catch (Exception e) {
            Log.error("Collection Delete Failed");
            throw new RuntimeException("Failed to delete Collection");
        } finally {
            header.removeHeader("contextTenantId");
        }
        return result;
    }

    /**
     * Updates the schema of a collection/Subject area.
     *
     * @param tenantId       - MDA Tenant ID
     * @param collectionInfo - Collection Info to Update.
     * @return true on successful collection update, false/RunTime Exception on failure.
     */
    public boolean updateSubjectArea(String tenantId, CollectionInfo collectionInfo) {
        Log.info("Updating Subject Area of a tenant : " +tenantId);
        if (tenantId == null || collectionInfo == null) {
            Log.info("Both Tenant Id, collectionInfo are Mandatory");
            throw new RuntimeException("Both Tenant Id, collectionInfo are Mandatory");
        }
        boolean result = false;
        header.addHeader("contextTenantId", tenantId);
        try {
            ResponseObj responseObj = wa.doPut(ADMIN_COLLECTIONS, mapper.writeValueAsString(collectionInfo), header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    result = true;
                    Log.info("Collection Updated SuccessFully");
                }
            }
        } catch (Exception e) {
            Log.error("Collection updated Failed");
            throw new RuntimeException("Failed to update collection");
        } finally {
            header.removeHeader("contextTenantId");
        }
        return result;
    }

    /**
     * Checks the DB details & returns the success   / failure.
     *
     * @param dbDetail - DBDetail Pojo - Data base details.
     * @param dbDetailName - type of db - schemadb, datadb, redshift, postgres.
     * @return true on connection test success.
     * @throws Exception
     */
    public boolean testDBDetails(TenantDetails.DBDetail dbDetail, String dbDetailName) {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_TEST_CONNECTION, header.getAllHeaders(),  "{\""+dbDetailName+"\": "+mapper.writeValueAsString(dbDetail)+"}");
            if(responseObj !=null && responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    List<HashMap<String, Object>> resultSet = mapper.convertValue(nsResponseObj.getData(),   new TypeReference<ArrayList<HashMap<String, Object>>>() {});
                    if(resultSet != null && resultSet.size() > 0 && Boolean.valueOf(resultSet.get(0).get("success").toString())) {
                        result = true;
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Parsing failed ", e);
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Checks the redshift data base details for the correctness.
     *
     * @param dbDetail - Data base details to check if they are correct.
     * @return -  true if the provided details are valid.
     * @throws Exception - unable to contact server
     */
    public boolean testRedShiftDBDetails(TenantDetails.DBDetail dbDetail) {
        return testDBDetails(dbDetail, "redshiftDBDetail");
    }

    /**
     * Checks the Postgres DB data base details for the correctness.
     *
     * @param dbDetail - Data base details to check if they are correct.
     * @return -  true if the provided details are valid.
     * @throws Exception - unable to contact server
     */
    public boolean testPostgresDBDetails(TenantDetails.DBDetail dbDetail) {
        return testDBDetails(dbDetail, "postgresDBDetail");
    }

    /**
     * Checks the Mongo Schema DB data base details for the correctness.
     *
     * @param dbDetail - Data base details to check if they are correct.
     * @return -  true if the provided details are valid.
     * @throws Exception - unable to contact server
     */
    public boolean testSchemaDBDetails(TenantDetails.DBDetail dbDetail) {
        return testDBDetails(dbDetail, "schemaDBDetail");
    }

    /**
     * Checks the Mongo Data DB data base details for the correctness.
     *
     * @param dbDetail - Data base details to check if they are correct.
     * @return -  true if the provided details are valid.
     * @throws Exception - unable to contact server
     */
    public boolean testDataDBDetails(TenantDetails.DBDetail dbDetail) {
        return testDBDetails(dbDetail, "dataDBDetail");
    }

    /**
     * Enables redshift for the tenant by reading the properties file.
     *
     * @param tenantDetails - Tenant Details of a tenant.
     * @throws IOException - If some thing fails.
     */
    public boolean enabledRedShiftWithDBDetails(TenantDetails tenantDetails) throws IOException {
        TenantDetails.DBDetail db = new TenantDetails.DBDetail();
        db.setDbName(env.getProperty("ns_redshift_dbName"));
        db.setSslEnabled(Boolean.valueOf(env.getProperty("ns_redshift_sslEnabled")));
        TenantDetails.DBServerDetail serverDetail = new TenantDetails.DBServerDetail();
        serverDetail.setHost(env.getProperty("ns_redshift_host"));
        serverDetail.setUserName(env.getProperty("ns_redshift_userName"));
        serverDetail.setPassword(env.getProperty("ns_redshift_password"));
        List<TenantDetails.DBServerDetail> dbl = new ArrayList<>();
        dbl.add(serverDetail);
        db.setDbServerDetails(dbl);
        return enableRedShift(tenantDetails, db);
    }

    /**
     * if useExisting is true reads the exists tenant DB details and test the db details,
     * if they are correct, updates the tenant details,
     * else checks the newly passed credentials & updates the tenant details.
     *
     * @param tenantDetails - Tenant Details for which the data base details need to be updated.
     * @param dbDetail - DBDetails that need to be updated.
     * @return - true in case of successful db tenant db details update else false.
     */
    public boolean enableRedShift(TenantDetails tenantDetails, TenantDetails.DBDetail dbDetail) {
        Log.info("Enabling Redshift with db details...");
        boolean result = false;
        tenantDetails.setRedshiftEnabled(true);
        if(dbDetail !=null && testRedShiftDBDetails(dbDetail)) {
            tenantDetails.setRedshiftDBDetail(dbDetail);
            result = updateTenant(tenantDetails);
        } else {
            Log.error("DB Details are not correct, please correct them.");
        }
        return result;
    }

    public boolean enableRedShift(TenantDetails tenantDetails) {
        Log.info("Enabling Redshift..");
        tenantDetails.setRedshiftEnabled(true);
        return updateTenant(tenantDetails);
    }

    /**
     * Disable redshift for the tenant.
     *
     * @param tenantDetails
     * @return
     */
    public boolean disableRedShift(TenantDetails tenantDetails) {
        Log.info("Disabling Redshift...");
        tenantDetails.setRedshiftEnabled(false);
        return updateTenant(tenantDetails);
    }

    public boolean updatePostgresDBDetails(TenantDetails tenantDetails) {
        TenantDetails.DBDetail db = new TenantDetails.DBDetail();
        db.setDbName(env.getProperty("ns_postgres_dbName"));
        db.setSslEnabled(Boolean.valueOf(env.getProperty("ns_postgres_sslEnabled")));
        TenantDetails.DBServerDetail serverDetail = new TenantDetails.DBServerDetail();
        serverDetail.setHost(env.getProperty("ns_postgres_host"));
        serverDetail.setUserName(env.getProperty("ns_postgres_userName"));
        serverDetail.setPassword(env.getProperty("ns_postgres_password"));
        List<TenantDetails.DBServerDetail> dbl = new ArrayList<>();
        dbl.add(serverDetail);
        db.setDbServerDetails(dbl);
        return updatePostgresDBDetails(tenantDetails, db);
    }

    public boolean updatePostgresDBDetails(TenantDetails tenantDetails, TenantDetails.DBDetail dbDetail) {
        Log.info("Updating postgres db details...");
        boolean result = false;
        if(dbDetail !=null && testPostgresDBDetails(dbDetail)) {
            tenantDetails.setPostgresDBDetail(dbDetail);
            result = updateTenant(tenantDetails);
        } else {
          Log.error("DB Details are not correct, Please correct them.");
        }
        return false;
    }



    //TODO
    /**
     * Pending - AccessToken, Activity Log, Test Database Connection.
     */


}
