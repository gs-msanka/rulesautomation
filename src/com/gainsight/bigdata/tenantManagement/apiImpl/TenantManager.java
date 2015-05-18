package com.gainsight.bigdata.tenantManagement.apiImpl;


import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.bigdata.urls.AdminURLs;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gainsight.bigdata.urls.AdminURLs.*;

/**
 * Created by Giribabu on 07/05/15.
 * Create, Update, Delete of Tenants & Subject Areas.
 */
public class TenantManager {

    private SFDCInfo sfdcInfo;
    private SalesforceConnector sfConnector;
    private Header header = new Header();
    private WebAction wa = new WebAction();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Logs in to tenant Management SFDC org & sets up the default headers required.
     */
    public TenantManager() {
        sfConnector = new SalesforceConnector(PropertyReader.tenantMgtUserName, PropertyReader.tenantMgtPassword + PropertyReader.tenantMgtSecurityToken,
                PropertyReader.partnerUrl, PropertyReader.sfdcApiVersion);
        if (!sfConnector.connect()) {
            throw new RuntimeException("Failed to Connect to salesforce - Check your admin credentials.");
        }
        sfdcInfo = sfConnector.fetchSFDCinfo();
        header.addHeader("Origin", sfdcInfo.getEndpoint());
        header.addHeader("Content-Type", "application/json");
        header.addHeader("appOrgId", sfdcInfo.getOrg());
        header.addHeader("appSessionId", sfdcInfo.getSessionId());
        header.addHeader("appUserId", sfdcInfo.getUserId());
        String authToken = getMDAAuthToken();
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
                System.out.println(responseObj.getContent());
                org.apache.http.Header[] headers = responseObj.getAllHeaders();
                for (org.apache.http.Header h : headers) {
                    if (h.getName() != null && h.getName().equals("Authtoken")) {
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
     * Gets all the tenants if SFOrgId is present & gets the with tenant id, gets all other tenant related information.
     *
     * @param sfOrgId  - Salesforce Organization Id.
     * @param tenantId - MDA environment tenant Id.
     * @return TenantDetails if tenant exists & NULL if tenant doesn't exits.
     */
    public TenantDetails getTenantDetail(String sfOrgId, String tenantId) {
        TenantDetails tenantDetail = null;
        Log.info("Getting All Tenant Details to get one Tenant Detail...");
        sfOrgId = NSUtil.convertSFID_15TO18(sfOrgId);
        String url = ADMIN_TENANTS;

        if (tenantId != null) {
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
            try {
                ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
                if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                    NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                    if (nsResponseObj.isResult()) {
                        List<TenantDetails> tenantDetails = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<TenantDetails>>() {
                        });
                        for (TenantDetails tD : tenantDetails) {
                            if (tD.getExternalTenantID() != null && tD.getExternalTenantID().equals(sfOrgId)) {
                                tenantDetail = tD;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("Some Exception in request" + e);
                throw new RuntimeException("Failed to get Tenant Details");
            }
        }
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
                List<CollectionInfo> collectionInfoList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<CollectionInfo>>() {
                });
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
        if (tenantId == null || collectionId == null) {
            Log.info("Both Tenant Id, Collection Id are Mandatory");
            throw new RuntimeException("Both Tenant Id, Collection Id are Mandatory");
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
        if (tenantId == null || collectionId == null) {
            Log.info("Both Tenant Id, Collection Id are Mandatory");
            throw new RuntimeException("Both Tenant Id, Collection Id are Mandatory");
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

    //TODO
    /**
     * Pending - AccessToken, Activity Log, Test Database Connection.
     */


}
