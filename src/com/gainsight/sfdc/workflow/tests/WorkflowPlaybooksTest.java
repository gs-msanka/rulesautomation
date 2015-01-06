package com.gainsight.sfdc.workflow.tests;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gainsight.testdriver.Application;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPlaybooksPage;
import com.gainsight.sfdc.workflow.pojos.Playbook;
import com.gainsight.sfdc.workflow.pojos.Task;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;


public class WorkflowPlaybooksTest extends BaseTest {
    ObjectMapper mapper = new ObjectMapper();
    String PLAYBOOK_OBJECT = "JBCXM__Playbook__c";
    private final String TEST_DATA_FILE         = "./testdata/sfdc/workflow/tests/PlaybookTests.xls";
    private final String CREATE_USERS_SCRIPT    = Application.basedir+"/testdata/sfdc/workflow/scripts/CreateUsers.txt";

    @BeforeClass
    public void setUp() {
        Log.info("Starting Playbook Test Case...");
        sfdc.runApexCode("delete [Select id from JBCXM__playbook__c];");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
        basepage.login();
    }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T1")
    public void crud_RiskPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Risk Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("RiskPB"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);

        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        
        //Update Risk Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedRiskPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb,updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Risk Playbook - updated");

        //Delete Risk Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook - deleted");
  }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void crud_EventPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Risk Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("EventPB"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);

        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook");
        
        //Update Risk Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedEventPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Event Playbook - updated");

        //Delete Risk Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook - deleted");
  }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void crud_OpporPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Risk Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("OpporPb"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);

        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Opportunity Playbook");
        
        //Update Risk Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedOpporPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Opportunity Playbook - updated");

        //Delete Risk Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying Opportunity Playbook - deleted");
  }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T4")
    public void crud_AllPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Risk Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("AllPB"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);

        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying All Type Playbook");
        
        //Update Risk Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedAllPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying All Type Playbook - updated");

        //Delete Risk Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying All Type Playbook - deleted");
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T5")
    public void duplicate_RiskPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        Task task = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void duplicate_OpportunityPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        Task task = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void duplicate_EventPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        Task task = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void duplicate_AllPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook");
        Task task = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
    }
 
    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
