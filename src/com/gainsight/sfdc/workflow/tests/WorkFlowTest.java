package com.gainsight.sfdc.workflow.tests;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import org.testng.annotations.*;

/**
 * Created by gainsight on 07/11/14.
 */
public class WorkFlowTest extends BaseTest {

    @BeforeClass
    public void setup() {
        basepage.login();
    }

    @Test
    public void sampleTest() {
        WorkflowPage workflowPage = basepage.clickOnWorkflowTab().clickOnListView();
        Report.logInfo("Working okay");
    }

    @AfterClass
    public void tearDown() {
        basepage.logout();
    }
}
