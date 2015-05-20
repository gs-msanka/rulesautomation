package com.gainsight.bigdata.dataload.tests;


import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.enums.MDAErrorCodes;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.http.ResponseObj;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Giribabu on 28/04/15.
 */
public class LoadDataToMDATest extends DataLoadManager {


    private final String TEST_DATA_FILE = "testdata/newstack/dataLoader/tests/DataLoaderTests.xls";
    private TenantDetails tenantDetails;

    @BeforeClass
    public void setup() {
        Assert.assertTrue(tenantAutoProvision(), "Tenant Auto-Provisioning..."); //Tenant Provision is mandatory step for data load progress.
        accessKey = getDataLoadAccessKey();
        tenantDetails = tenantManager.getTenantDetail(sfinfo.getOrg(), null);
    }

    @TestInfo(testCaseIds = {"GS-3626"})
    @Test
    public void mdaAuthorizeCheckingWithValidDetails() {
        NsResponseObj nsResponseObj = mdaDataLoadAuthenticate(sfinfo.getOrg(), accessKey, sfinfo.getUserName());
        Assert.assertTrue(nsResponseObj != null);
        Assert.assertTrue(nsResponseObj.isResult());
        Assert.assertNotNull(nsResponseObj.getData());
        HashMap<String, String> serverData = (HashMap<String, String>) mapper.convertValue(nsResponseObj.getData(), HashMap.class);
        Assert.assertTrue(serverData.containsKey("tenantName"));
        Assert.assertNotNull(serverData);
    }

    @TestInfo(testCaseIds = {"GS-3627"})
    @Test
    public void mdaAuthorizeCheckingWithInvalidAccessKey() throws IOException {
        String key = accessKey;
        accessKey = getDataLoadAccessKey(); //re-generating another accessKey to verify that old one is discarded.
        ResponseObj responseObj = mdaAuthenticate(sfinfo.getOrg(), key, sfinfo.getUserName());
        Assert.assertEquals(responseObj.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(nsResponseObj.getErrorCode(), MDAErrorCodes.UN_AUTHORIZED.getGSCode());
        Assert.assertEquals(nsResponseObj.getErrorDesc(), "Error occurred while authenticating.");
    }

    @TestInfo(testCaseIds = {"GS-3628"})
    @Test
    public void mdaAuthorizeCheckingWithInvalidLoginName() throws IOException {
        ResponseObj responseObj = mdaAuthenticate(sfinfo.getOrg(), accessKey, sfinfo.getUserName()+"Invalid");
        Assert.assertEquals(responseObj.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(nsResponseObj.getErrorCode(), MDAErrorCodes.UN_AUTHORIZED.getGSCode());
        Assert.assertEquals(nsResponseObj.getErrorDesc(), "Error occurred while authenticating.");
    }

    @TestInfo(testCaseIds = {"GS-3629"})
    @Test
    public void mdaAuthorizeCheckingWithInvalidOrgId() throws IOException {
        ResponseObj responseObj = mdaAuthenticate(tenantManager.sfdcInfo.getOrg(), accessKey, sfinfo.getUserName()); //Sending tenant management org SFDC ID which is not valid ID for the accessKey & UserName.
        Assert.assertEquals(responseObj.getStatusCode(), HttpStatus.SC_UNAUTHORIZED);
        NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        Assert.assertFalse(nsResponseObj.isResult());
        Assert.assertEquals(nsResponseObj.getErrorCode(), MDAErrorCodes.UN_AUTHORIZED.getGSCode());
        Assert.assertEquals(nsResponseObj.getErrorDesc(), "Error occurred while authenticating.");
    }

    @TestInfo(testCaseIds = {"GS-3630"})
    @Test
    public void mdaAuthorizeCheckingWith15DigitOrgId() {
        System.out.println(sfinfo.getOrg().substring(0,15));
        NsResponseObj nsResponseObj = mdaDataLoadAuthenticate(sfinfo.getOrg().substring(0,15), accessKey, sfinfo.getUserName());
        Assert.assertTrue(nsResponseObj != null);
        Assert.assertTrue(nsResponseObj.isResult());
        Assert.assertNotNull(nsResponseObj.getData());
        HashMap<String, String> serverData = (HashMap<String, String>) mapper.convertValue(nsResponseObj.getData(), HashMap.class);
        Assert.assertTrue(serverData.containsKey("tenantName"));
        Assert.assertNotNull(serverData);
    }
}
