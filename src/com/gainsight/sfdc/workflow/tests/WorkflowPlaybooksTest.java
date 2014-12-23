package com.gainsight.sfdc.workflow.tests;
import com.gainsight.pageobject.core.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gainsight.sfdc.workflow.pojos.Playbook;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.utils.DataProviderArguments;
import jxl.read.biff.BiffException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPlaybooksPage;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;


public class WorkflowPlaybooksTest extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    String[] dirs = { "wfplaybooktests" };
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    String PLAYBOOK_OBJECT = "JBCXM__Playbook__c";
    private final String TEST_DATA_FILE         = "./testdata/sfdc/workflow/tests/PlaybookTests.xls";

    @BeforeClass
    public void setUp() {
        Report.logInfo("Starting Playbook Test Case...");
        apex.runApex("delete [Select id from JBCXM__playbook__c];");
        basepage.login();
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void addPlaybooks(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Risk-Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        pb = mapper.readValue(testData.get("Opportunity-Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Opportunity Playbook");
        pb = mapper.readValue(testData.get("Event-Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook");
        pb = mapper.readValue(testData.get("All-Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying All Playbook");
    }


    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void addUpdateRiskPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        Playbook pb1 = mapper.readValue(testData.get("UpdatedPB1"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, pb1);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb1), "Verifying Playbook Updated details");
        Playbook pb2 = mapper.readValue(testData.get("UpdatedPB2"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb1, pb2);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb2), "Verifying Playbook Updated details");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void addAndDeletePlaybooks(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        List<Playbook> pbList = mapper.readValue(testData.get("Playbooks"), new TypeReference<ArrayList<Playbook>>() {});
        for(Playbook pb : pbList) {
            pbPage = pbPage.addPlaybook(pb);
            Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        }
        for(Playbook pb : pbList) {
            pbPage.deletePlaybook(pb);
            Assert.assertFalse(pbPage.isPlaybookDisplayed(pb));
        }

    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void addPlaybookAndTasks(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        List<Task> taskList = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task :taskList) {
            pbPage = pbPage.addTask(task);
            Assert.assertTrue(pbPage.isTaskDisplayed(task));
        }
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
