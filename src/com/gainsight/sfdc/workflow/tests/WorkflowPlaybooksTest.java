package com.gainsight.sfdc.workflow.tests;
import com.gainsight.pageobject.core.Report;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


//import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.PlayBooksPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPlaybooksPage;

public class WorkflowPlaybooksTest extends BaseTest {
	String[] dirs = { "wfplaybooktests" };
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    String PLAYBOOK_OBJECT = "JBCXM__Playbook__c";
    
    @BeforeClass
    public void setUp() {
        Report.logInfo("Starting Playbook Test Case...");
        apex.runApex(resolveStrNameSpace("DELETE [SELECT ID FROM JBCXM__Playbook__c LIMIT 8000];"));
        basepage.login();
    }
    
    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
    
    @Test
    public void testAllPlaybook_wf() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_001");
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("taskdetails"));
        pbPage.addplaybook_wf(pbData, taskData);
        Assert.assertEquals(true, pbPage.isplaybookpresent_wf(pbData.get("playbookname")));
        Assert.assertEquals(true, pbPage.isTaskPresent_wf(taskData));
        Assert.assertEquals(true, pbPage.isAllPlaybook_wf(pbData.get("playbookname")));
    }
    
    
}
