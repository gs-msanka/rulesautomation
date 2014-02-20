package com.gainsight.sfdc.retention.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.retention.pages.PlayBooksPage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import jxl.read.biff.BiffException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaybooksTests extends BaseTest {
    String[] dirs = { "playbooktests" };
    private final String TESTDATA_DIR = TEST_DATA_PATH_PREFIX
            + generatePath(dirs);
    String PLAYBOOK_OBJECT = "JBCXM__Playbook__c";

    @BeforeClass
    public void setUp() {
        Report.logInfo("Starting Playbook Test Case...");
        basepage.login();
        DataETL dataETL = new DataETL();
        try {
            dataETL.cleanUp(PLAYBOOK_OBJECT, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_001");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("taskdetails"));
        pbPage.addplaybook(pbData, taskData);
        Assert.assertEquals(true, pbPage.isplaybookpresent(pbData.get("playbookname")));
        Assert.assertEquals(true, pbPage.isTaskPresent(taskData));
        Assert.assertEquals(true, pbPage.isAllPlaybook(pbData.get("playbookname")));
    }

    @Test(dependsOnMethods="testAllPlaybook")
    public void testeditAllPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_001");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("updatedplaybookdetails"));
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertEquals(true, pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
        Assert.assertEquals(true, pbPage.isEventPlaybook(updatedpbdata.get("playbookname")));
    }

    @Test(dependsOnMethods="testeditAllPlaybook")
    public void testDeleteAllPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_001");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("updatedplaybookdetails"));
        String playbookname = pbData.get("playbookname");
        pbPage.deletePlaybook(playbookname);
        Assert.assertEquals(false, pbPage.isplaybookpresent(playbookname));
    }

    @Test
    public void testAlertPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_002");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("taskdetails"));
        pbPage.addplaybook(pbData, taskData);
        Assert.assertEquals(true, pbPage.isplaybookpresent(pbData.get("playbookname")));
        Assert.assertEquals(true, pbPage.isTaskPresent(taskData));
        Assert.assertEquals(true, pbPage.isAlertPlaybook(pbData.get("playbookname")));
    }

    @Test(dependsOnMethods="testAlertPlaybook")
    public void testeditAlertPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_002");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("updatedplaybookdetails"));
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertEquals(true, pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
    }

    @Test(dependsOnMethods="testeditAlertPlaybook")
    public void testDeleteAlertPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_002");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("updatedplaybookdetails"));
        String playbookname = pbData.get("playbookname");
        pbPage.deletePlaybook(playbookname);
        Assert.assertEquals(false, pbPage.isplaybookpresent(playbookname));
    }

    @Test
    public void testEventPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_003");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> taskData = getMapFromData(testdata.get("taskdetails"));
        pbPage.addplaybook(pbData, taskData);
        Assert.assertEquals(true, pbPage.isplaybookpresent(pbData.get("playbookname")));
        Assert.assertEquals(true, pbPage.isTaskPresent(taskData));
        Assert.assertEquals(true, pbPage.isEventPlaybook(pbData.get("playbookname")));
    }

    @Test(dependsOnMethods="testEventPlaybook")
    public void testeditEventPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_003");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String, String> updatedpbdata = getMapFromData(testdata.get("updatedplaybookdetails"));
        pbPage.editPlaybook(pbData.get("playbookname"), updatedpbdata);
        Assert.assertEquals(true, pbPage.isplaybookpresent(updatedpbdata.get("playbookname")));
    }

    @Test(dependsOnMethods="testeditEventPlaybook")
    public void testDeleteEventPlaybook() throws BiffException, IOException {
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_003");
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> pbData = getMapFromData(testdata.get("updatedplaybookdetails"));
        String playbookname = pbData.get("playbookname");
        pbPage.deletePlaybook(playbookname);
        Assert.assertEquals(false, pbPage.isplaybookpresent(playbookname));
    }

    @Test
    public void testAddTasksForPlaybook() throws BiffException, IOException {
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        List<HashMap<String, String>> taskDataList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_004");
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String,String> taskData = getMapFromData(testdata.get("taskdetails"));
        taskDataList.add(getMapFromData(testdata.get("task1")));
        taskDataList.add(getMapFromData(testdata.get("task2")));
        taskDataList.add(getMapFromData(testdata.get("task3")));
        taskDataList.add(getMapFromData(testdata.get("task4")));
        pbPage.addplaybook(pbData, taskData);
        Assert.assertEquals(true, pbPage.isplaybookpresent(pbData.get("playbookname")));
        Assert.assertEquals(true, pbPage.isTaskDisplayed(taskData));
        Assert.assertEquals(true, pbPage.isAllPlaybook(pbData.get("playbookname")));
        for(HashMap<String, String> tData : taskDataList) {
            pbPage.addTask(tData);
            Assert.assertEquals(true, pbPage.isTaskDisplayed(taskData));
        }
    }

    @Test(dependsOnMethods="testAddTasksForPlaybook")
    public void testEditTask() throws BiffException, IOException {
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_004");
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        HashMap<String,String> oldtaskdata = getMapFromData(testdata.get("task1"));
        HashMap<String, String> newtaskdata = getMapFromData(testdata.get("updatedtaskdetails"));
        pbPage.openPlaybook(pbData.get("playbookname"));
        pbPage.editTask(oldtaskdata, newtaskdata);
        Assert.assertEquals(true, pbPage.isTaskDisplayed(newtaskdata));
    }

    @Test(dependsOnMethods ={"testAddTasksForPlaybook"})
    public void testDeleteTask() throws BiffException, IOException {
        PlayBooksPage pbPage = basepage.clickOnRetentionTab().clickOnPlaybooksTab();
        HashMap<String, String> testdata =  testDataLoader.getDataFromExcel(
                TESTDATA_DIR + "PlaybookTests.xls", "RET_004");
        HashMap<String,String> taskData = getMapFromData(testdata.get("taskdetails"));
        HashMap<String, String> pbData = getMapFromData(testdata.get("playbookdetails"));
        System.out.println(pbData.get("playbookname"));
        pbPage.deleteTask(taskData, pbData.get("playbookname"));
        Assert.assertEquals(false, pbPage.isTaskDisplayed(taskData));
    }

    @AfterClass
    public void tearDown(){
        basepage.logout();

    }
}
