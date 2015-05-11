package com.gainsight.bigdata.tenantManagement.tests;

import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by gainsight on 08/05/15.
 */
public class TenantManagementTest {

    private final String TEST_DATA_FILE = "testdata/newstack/tenantManagement/tests/TenantMgtTests.xls";
    private TenantManager tenantManager;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public void setup() {
        tenantManager = new TenantManager();
    }

    @TestInfo(testCaseIds = {"GS-4271"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void createTenant(HashMap<String, String> testData) throws IOException {
        TenantDetails tenantDetails = mapper.readValue(testData.get("TenantDetails"), TenantDetails.class);
        Assert.assertTrue(tenantManager.createTenant(tenantDetails));
        Assert.assertNotNull(tenantDetails.getTenantId());
        Assert.assertTrue(tenantManager.deleteTenant(null, tenantDetails.getTenantId()));

    }

}
