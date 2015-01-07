package com.gainsight.sfdc.workflow.tests;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.testdriver.Application;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPlaybooksPage;
import com.gainsight.sfdc.workflow.pojos.Playbook;
import com.gainsight.sfdc.workflow.pojos.PlaybookTask;
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
	        cleanPlaybooksData();
	        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
	        basepage.login();
	    }

	    private void cleanPlaybooksData() {
	        sfdc.runApexCode("delete [Select id from JBCXM__playbook__c];");
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
        //Add Tasks to Playbook
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks){
        pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task),"Task AddedSuccessfully");
        }
       //Update Risk Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedRiskPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb,updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Risk Playbook - updated");
        //Add more tasks
        ArrayList<Task> updatedTasks = mapper.readValue(testData.get("UpdatedTasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task updatedTask : updatedTasks){
        pbPage.addTask(updatedTask);
       Assert.assertTrue(pbPage.isTaskDisplayed(updatedTask),"Task Updated Successfully");
        }
        //Edit existing Task
        Task taskToEdit=mapper.readValue(testData.get("TaskToEdit"),Task.class);
        Task editTask=mapper.readValue(testData.get("EditTask"),Task.class);
        pbPage.editTask(taskToEdit, editTask);
        Assert.assertTrue(pbPage.isTaskDisplayed(editTask),"Task Editted Successfully");
        //Delete Existing Task
         Task deleteTask=mapper.readValue(testData.get("DeleteTask"),Task.class);
         pbPage.deleteTask(deleteTask);
         Assert.assertFalse(pbPage.isTaskDisplayed(deleteTask),"Task Deleted Successfully");
       //Delete Risk Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying Risk Playbook - deleted");
  }
    
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T2")
    public void crud_EventPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Event Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("EventPB"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook");
      //Add Tasks to Playbook
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks){
        pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task),"Task AddedSuccessfully");
        }
        //Update Event Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedEventPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Event Playbook - updated");
        //Add more tasks
        ArrayList<Task> updatedTasks = mapper.readValue(testData.get("UpdatedTasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task updatedTask : updatedTasks){
        pbPage.addTask(updatedTask);
       Assert.assertTrue(pbPage.isTaskDisplayed(updatedTask),"Task Updated Successfully");
        }
        //Edit existing Task
        Task taskToEdit=mapper.readValue(testData.get("TaskToEdit"),Task.class);
        Task editTask=mapper.readValue(testData.get("EditTask"),Task.class);
        pbPage.editTask(taskToEdit, editTask);
        Assert.assertTrue(pbPage.isTaskDisplayed(editTask),"Task Editted Successfully");
        //Delete Existing Task
         Task deleteTask=mapper.readValue(testData.get("DeleteTask"),Task.class);
         pbPage.deleteTask(deleteTask);
         Assert.assertFalse(pbPage.isTaskDisplayed(deleteTask),"Task Deleted Successfully");
        //Delete Event Playbook
        pbPage=pbPage.deletePlaybook(updatedPb);
        Assert.assertFalse(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook - deleted");
  }
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T3")
    public void crud_OpporPlaybook_WithTasks(HashMap<String, String> testData) throws IOException {
    	//Create Opportunity Playbook
    	WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("OpporPB"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        //Read from UI and Verify
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Opportunity Playbook");
      //Add Tasks to Playbook
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks){
        pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task),"Task AddedSuccessfully");
        }
        //Update Opportunity Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedOpporPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying Opportunity Playbook - updated");
        //Add more tasks
        ArrayList<Task> updatedTasks = mapper.readValue(testData.get("UpdatedTasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task updatedTask : updatedTasks){
        pbPage.addTask(updatedTask);
       Assert.assertTrue(pbPage.isTaskDisplayed(updatedTask),"Task Updated Successfully");
        }
        //Edit existing Task
        Task taskToEdit=mapper.readValue(testData.get("TaskToEdit"),Task.class);
        Task editTask=mapper.readValue(testData.get("EditTask"),Task.class);
        pbPage.editTask(taskToEdit, editTask);
        Assert.assertTrue(pbPage.isTaskDisplayed(editTask),"Task Editted Successfully");
        //Delete Existing Task
         Task deleteTask=mapper.readValue(testData.get("DeleteTask"),Task.class);
         pbPage.deleteTask(deleteTask);
         Assert.assertFalse(pbPage.isTaskDisplayed(deleteTask),"Task Deleted Successfully");
        //Delete Opportunity Playbook
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
      //Add Tasks to Playbook
        ArrayList<Task> tasks = mapper.readValue(testData.get("Tasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task task : tasks){
        pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task),"Task AddedSuccessfully");
        }
        //Update All Type Playbook
        Playbook updatedPb = mapper.readValue(testData.get("UpdatedAllPB"), Playbook.class);
        pbPage = pbPage.editPlaybook(pb, updatedPb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(updatedPb), "Verifying All Type Playbook - updated");
        //Add more tasks
        ArrayList<Task> updatedTasks = mapper.readValue(testData.get("UpdatedTasks"), new TypeReference<ArrayList<Task>>() {});
        for(Task updatedTask : updatedTasks){
        pbPage.addTask(updatedTask);
       Assert.assertTrue(pbPage.isTaskDisplayed(updatedTask),"Task Updated Successfully");
        }
        //Edit existing Task
        Task taskToEdit=mapper.readValue(testData.get("TaskToEdit"),Task.class);
        Task editTask=mapper.readValue(testData.get("EditTask"),Task.class);
        pbPage.editTask(taskToEdit, editTask);
        Assert.assertTrue(pbPage.isTaskDisplayed(editTask),"Task Editted Successfully");
        //Delete Existing Task
         Task deleteTask=mapper.readValue(testData.get("DeleteTask"),Task.class);
         pbPage.deleteTask(deleteTask);
         Assert.assertFalse(pbPage.isTaskDisplayed(deleteTask),"Task Deleted Successfully");
        //Delete All Type Playbook
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
        Task task1 = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task1);
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Task task2 = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task2);
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Task task3 = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task3);
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
        pb.setName(testData.get("Clone_PBName"));
        pbPage.duplicatePlaybook(pb.getName());
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T6")
    public void duplicate_OpportunityPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Opportunity Playbook");
        Task task1 = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task1);
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Task task2 = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task2);
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Task task3 = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task3);
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
        pb.setName(testData.get("Clone_PBName"));
        pbPage.duplicatePlaybook(pb.getName());
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T7")
    public void duplicate_EventPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying Event Playbook");
        Task task1 = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task1);
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Task task2 = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task2);
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Task task3 = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task3);
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
        pb.setName(testData.get("Clone_PBName"));
        pbPage.duplicatePlaybook(pb.getName());
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T8")
    public void duplicate_AllPlaybook(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying All Playbook");
        Task task1 = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task1);
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Task task2 = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task2);
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Task task3 = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task3);
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
        pb.setName(testData.get("Clone_PBName"));
        pbPage.duplicatePlaybook(pb.getName());
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb));
        Assert.assertTrue(pbPage.isTaskDisplayed(task1));
        Assert.assertTrue(pbPage.isTaskDisplayed(task2));
        Assert.assertTrue(pbPage.isTaskDisplayed(task3));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T9")
    public void errorMessageForMandatoryFields(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook1"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookErrorMsgDisplayed(testData.get("ErrorMsg1")));
        Assert.assertTrue(pbPage.isPlaybookErrorMsgDisplayed(testData.get("ErrorMsg2")));
        pb = mapper.readValue(testData.get("Playbook2"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookErrorMsgDisplayed(testData.get("ErrorMsg2")));
        pb = mapper.readValue(testData.get("Playbook3"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying playbook is displayed");
        Task task = mapper.readValue(testData.get("Task1"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskErrorMsgDisplayed(testData.get("ErrorMsg3")));
        Assert.assertTrue(pbPage.isTaskErrorMsgDisplayed(testData.get("ErrorMsg4")));
        task = mapper.readValue(testData.get("Task2"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskErrorMsgDisplayed(testData.get("ErrorMsg3")));
        task = mapper.readValue(testData.get("Task3"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
        task = mapper.readValue(testData.get("Task4"), Task.class);
        pbPage = pbPage.addTask(task);
        Assert.assertTrue(pbPage.isTaskDisplayed(task));
    }

    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "T10")
    public void duplicatePlaybookWithoutTasks(HashMap<String, String> testData) throws IOException {
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Playbook pb = mapper.readValue(testData.get("Playbook"), Playbook.class);
        pbPage = pbPage.addPlaybook(pb);
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying playbook is displayed");
        pb.setName(testData.get("Clone_PBName"));
        pbPage.duplicatePlaybook(pb.getName());
        Assert.assertTrue(pbPage.isPlaybookDisplayed(pb), "Verifying duplicated playbook is displayed");
    }

    @Test
    public void noPlaybooksMessage() {
        cleanPlaybooksData();
        WorkflowPlaybooksPage pbPage = basepage.clickOnWorkflowTab().clickOnPlaybooksTab();
        Assert.assertTrue(pbPage.noPlaybooksMessage());
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
