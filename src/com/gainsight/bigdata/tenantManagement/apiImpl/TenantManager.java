package com.gainsight.bigdata.tenantManagement.apiImpl;


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
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gainsight on 07/05/15.
 */
public class TenantManager implements AdminURLs {

    private SFDCInfo sfdcInfo;
    private SalesforceConnector sfConnector;
    private Header header = new Header();
    private WebAction wa = new WebAction();
    private ObjectMapper mapper = new ObjectMapper();

    public TenantManager() {
        sfConnector = new SalesforceConnector(PropertyReader.tenantMgtUserName, PropertyReader.tenantMgtPassword + PropertyReader.tenantMgtSecurityToken,
                PropertyReader.partnerUrl, PropertyReader.sfdcApiVersion);
        if(!sfConnector.connect()) {
            throw new RuntimeException("Failed to Connect to salesforce - Check your admin credentials.");
        }
        sfdcInfo = sfConnector.fetchSFDCinfo();
        header.addHeader("Origin",sfdcInfo.getEndpoint());
        header.addHeader("Content-Type", "application/json");
        header.addHeader("appOrgId", sfdcInfo.getOrg());
        header.addHeader("appSessionId", sfdcInfo.getSessionId());
        header.addHeader("appUserId", sfdcInfo.getUserId());
        header.addHeader("authToken", "initialcall");
        String authToken = getMDAAuthToken();
        Assert.assertTrue((authToken!=null && !authToken.equals("")), "Verifying Auth Token Generation");
        header.addHeader("authToken", authToken);
    }

    /**
     * If tenant already exists, it re-populates the Tenant details from server.
     * @param tenantDetails - Details to create a tenant
     * @return true - if tenant creation successful
     */
    public boolean createTenant(TenantDetails tenantDetails) {
        Log.info("Creating Tenant...");
        boolean result = false;
        tenantDetails.setExternalTenantID(NSUtil.convertSFID_15TO18(tenantDetails.getExternalTenantID()));

        try {
            ResponseObj responseObj = wa.doPost(ADMIN_TENANTS, header.getAllHeaders(), mapper.writeValueAsString(tenantDetails));
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    HashMap<String, String> data = (HashMap<String, String>)nsResponseObj.getData();
                    if(data.get("TenantId") != null) {
                        result = true;
                        tenantDetails.setTenantId(data.get("TenantId"));
                    }
                } else {
                    Log.info(nsResponseObj.getErrorCode() + " ::: " +nsResponseObj.getErrorDesc());
                }
                //This should not be bad request.
            } else if(responseObj.getStatusCode()==HttpStatus.SC_BAD_REQUEST) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(MDAErrorCodes.TENANT_ALREADY_EXIST.getGSCode().equals(nsResponseObj.getErrorCode())) {
                    Log.info(nsResponseObj.getErrorDesc());
                    tenantDetails = getTenantDetail(tenantDetails.getExternalTenantID(), null);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to create tenant");
        }
        return result;
    }

    /**
     *
     * @param tenantDetails
     * @return
     */
    public boolean updateTenant(TenantDetails tenantDetails) {
        Log.info("Updating the Tenant...");
        boolean result = false;
        String url = ADMIN_TENANTS+"/"+tenantDetails.getTenantId();
        Log.info(url);
        try {
            ResponseObj responseObj = wa.doPut(url, mapper.writeValueAsString(tenantDetails), header.getAllHeaders());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    result = true;
                } else {
                    Log.info(nsResponseObj.getErrorCode() + " ::: " +nsResponseObj.getErrorDesc());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Deletes the tenant.
     * @param sfOrgId
     * @return
     */
    public boolean deleteTenant(String sfOrgId, String tenantId) {
        Log.info("Deleting Tenant...");
        boolean result = false;
        String url = ADMIN_TENANTS;
        if(tenantId!=null) {
            url = url+"/"+tenantId;
        } else {
            TenantDetails tenantDetail = getTenantDetail(sfOrgId, null);
            if (tenantDetail != null && tenantDetail.getTenantId() != null) {
                url = url + "/" + tenantDetail.getTenantId();
            }
        }
        Log.info("Delete Tenant URL " +url);
        try {
            ResponseObj responseObj = wa.doDelete(url, header.getAllHeaders());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
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
     * @return - AuthToken - Which can be used for further communication with MDA.
     */
    private String getMDAAuthToken() {
        Log.info("Setting MDA Auth Token...");
        String authToken = "";
        String url = ADMIN_TENANTS;
        Log.info(url);
        try {
            ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
            if(responseObj.getStatusCode()== HttpStatus.SC_OK) {
                System.out.println(responseObj.getContent());
                org.apache.http.Header[] headers =  responseObj.getAllHeaders();
                for(org.apache.http.Header h : headers) {
                    if(h.getName() !=null && h.getName().equals("Authtoken")) {
                        authToken = h.getValue();
                        Log.info("Updated AuthToken Details");
                        Log.info("Auth Token :" +authToken);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get Auth Token : ", e);
            throw new RuntimeException("Failed to get Auth Token");
        }
        return authToken;
    }

    public boolean isTenantPresent(String sfOrgId, String tenantId) {
        Log.info("Verifying Tenant Present...");
        TenantDetails tenantDetail = getTenantDetail(sfOrgId, tenantId);
        if(tenantDetail!=null) {
            Log.info("Found Tenant, Tenant Org Id ::: " +tenantDetail.getExternalTenantID() +" , Tenant Id ::: " +tenantDetail.getTenantId());
            return true;
        }
        return false;
    }

    /*

     */
    public TenantDetails getTenantDetail(String sfOrgId, String tenantId) {
        TenantDetails tenantDetail = null;
        Log.info("Getting All Tenant Details to get one Tenant Detail...");
        sfOrgId = NSUtil.convertSFID_15TO18(sfOrgId);
        String url = ADMIN_TENANTS;

        if(tenantId!=null) {
            url = ADMIN_TENANTS+"/"+tenantId;
            try {
                ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
                if(responseObj.getStatusCode()== HttpStatus.SC_OK) {
                    NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                    tenantDetail = mapper.convertValue(nsResponseObj.getData(), TenantDetails.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get Tenant Information");
            }
        }
        else {
            try {
                ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
                if(responseObj.getStatusCode()== HttpStatus.SC_OK) {
                    NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                    if (nsResponseObj.isResult()) {
                        List<TenantDetails> tenantDetails = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<TenantDetails>>() {});
                        for(TenantDetails tD : tenantDetails) {
                            if(tD.getExternalTenantID()!=null && tD.getExternalTenantID().equals(sfOrgId)) {
                                tenantDetail = tD;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("Some Exception in request" +e);
                throw new RuntimeException("Failed to get Tenant Details");
            }
        }
        return tenantDetail;
    }








}
