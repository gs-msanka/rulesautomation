package com.gainsight.sfdc.workflow.tests;
import com.gainsight.pageobject.core.Report;

import java.io.IOException;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;



import com.gainsight.pageobject.core.*;
import com.gainsight.sfdc.retention.pages.PlayBooksPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPlaybooksPage;
import com.gainsight.sfdc.workflow.pages.WorkflowBasePage;


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
    	WorkflowBasePage wfb = new WorkflowBasePage();
    	wfb.showSalesForceHeader();
        basepage.logout();
    }
    
    @Test
    public void addPlaybookTypeRisk() throws BiffException, IOException {
//    	System.out.println("IN testAllPlaybook method");
    	 HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                 TESTDATA_DIR + "PlaybookTests.xls", "Risk");
         WorkflowPlaybooksPage pbPage = basepage.clickonWorkflowTab().clickOnPlaybooksTab();
         HashMap<String, String> pbData = getMapFromData(testdata.get("AddPb"));
         HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
         pbPage.addplaybook(pbData, taskData);
         Assert.assertTrue(pbPage.isplaybookpresent(pbData.get("playbookname")));
         Assert.assertTrue(pbPage.isTaskPresent(taskData));
         Assert.assertTrue(pbPage.isPlaybookType(pbData.get("playbookname"),testdata.get("pbType")));
    }
    
    @Test
    public void editPlaybookTypeRisk() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Risk");
        
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> pbData = getMapFromData(testdata.get("EditPb"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("UpdatedPb"));
        pbPage.addplaybook(pbData, taskData);
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertTrue(pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
        Assert.assertTrue(pbPage.isAllPlaybook(updatedpbdata.get("playbookname")));
    }
    
    @Test
    public void deletePlaybookTypeRisk() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Risk");
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> pbData = getMapFromData(testdata.get("DeletePb"));
        String playbookname = pbData.get("playbookname");
        pbPage.addplaybook(pbData, taskData);
        pbPage.deletePlaybook(playbookname);
        Assert.assertFalse(pbPage.isplaybookpresent(playbookname));
    }
    
    @Test
    public void addPlaybookTypeEvent() throws BiffException, IOException {
//    	System.out.println("IN testAllPlaybook method");
    	 HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                 TESTDATA_DIR + "PlaybookTests.xls", "Event");
         WorkflowPlaybooksPage pbPage = basepage.clickonWorkflowTab().clickOnPlaybooksTab();
         HashMap<String, String> pbData = getMapFromData(testdata.get("AddPb"));
         HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
         pbPage.addplaybook(pbData, taskData);
         Assert.assertTrue(pbPage.isplaybookpresent(pbData.get("playbookname")));
         Assert.assertTrue(pbPage.isTaskPresent(taskData));
         Assert.assertTrue(pbPage.isPlaybookType(pbData.get("playbookname"),testdata.get("pbType")));
    }
    
    @Test
    public void editPlaybookTypeEvent() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Event");
        
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> pbData = getMapFromData(testdata.get("EditPb"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("UpdatedPb"));
        pbPage.addplaybook(pbData, taskData);
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertTrue(pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
        Assert.assertTrue(pbPage.isAllPlaybook(updatedpbdata.get("playbookname")));
    }
    
    @Test
    public void deletePlaybookTypeEvent() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Event");
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> pbData = getMapFromData(testdata.get("DeletePb"));
        String playbookname = pbData.get("playbookname");
        pbPage.addplaybook(pbData, taskData);
        pbPage.deletePlaybook(playbookname);
        Assert.assertFalse(pbPage.isplaybookpresent(playbookname));
    }
    
    @Test
    public void addPlaybookTypeOpportunity() throws BiffException, IOException {
//    	System.out.println("IN testAllPlaybook method");
    	 HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                 TESTDATA_DIR + "PlaybookTests.xls", "Opportunity");
         WorkflowPlaybooksPage pbPage = basepage.clickonWorkflowTab().clickOnPlaybooksTab();
         HashMap<String, String> pbData = getMapFromData(testdata.get("AddPb"));
         HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
         pbPage.addplaybook(pbData, taskData);
         Assert.assertTrue(pbPage.isplaybookpresent(pbData.get("playbookname")));
         Assert.assertTrue(pbPage.isTaskPresent(taskData));
         Assert.assertTrue(pbPage.isPlaybookType(pbData.get("playbookname"),testdata.get("pbType")));
    }
    
    @Test
    public void editPlaybookTypeOpportunity() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Opportunity");
        
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> pbData = getMapFromData(testdata.get("EditPb"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("UpdatedPb"));
        pbPage.addplaybook(pbData, taskData);
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertTrue(pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
        Assert.assertTrue(pbPage.isAllPlaybook(updatedpbdata.get("playbookname")));
    }
    
    @Test
    public void deletePlaybookTypeOpportunity() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "Opportunity");
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> pbData = getMapFromData(testdata.get("DeletePb"));
        String playbookname = pbData.get("playbookname");
        pbPage.addplaybook(pbData, taskData);
        pbPage.deletePlaybook(playbookname);
        Assert.assertFalse(pbPage.isplaybookpresent(playbookname));
    }
    
    @Test
    public void addPlaybookTypeAll() throws BiffException, IOException {
//    	System.out.println("IN testAllPlaybook method");
    	 HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                 TESTDATA_DIR + "PlaybookTests.xls", "All");
         WorkflowPlaybooksPage pbPage = basepage.clickonWorkflowTab().clickOnPlaybooksTab();
         HashMap<String, String> pbData = getMapFromData(testdata.get("AddPb"));
         HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
         pbPage.addplaybook(pbData, taskData);
         Assert.assertTrue(pbPage.isplaybookpresent(pbData.get("playbookname")));
         Assert.assertTrue(pbPage.isTaskPresent(taskData));
         Assert.assertTrue(pbPage.isPlaybookType(pbData.get("playbookname"),testdata.get("pbType")));
    }
    
    @Test
    public void editPlaybookTypeAll() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "All");
        
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> pbData = getMapFromData(testdata.get("EditPb"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("UpdatedPb"));
        pbPage.addplaybook(pbData, taskData);
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertTrue(pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
        Assert.assertTrue(pbPage.isAllPlaybook(updatedpbdata.get("playbookname")));
    }
    
    @Test
    public void deletePlaybookTypeAll() throws BiffException, IOException {
//    	System.out.println("IN testeditAllPlaybook method");
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "All");
        WorkflowPlaybooksPage pbPage = new WorkflowPlaybooksPage();
        HashMap<String, String> taskData = getMapFromData(testdata.get("AddTask"));
        HashMap<String, String> pbData = getMapFromData(testdata.get("DeletePb"));
        String playbookname = pbData.get("playbookname");
        pbPage.addplaybook(pbData, taskData);
        pbPage.deletePlaybook(playbookname);
        Assert.assertFalse(pbPage.isplaybookpresent(playbookname));
    }
}
