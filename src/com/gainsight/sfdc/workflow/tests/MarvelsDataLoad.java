package com.gainsight.sfdc.workflow.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.util.config.SfdcConfig;
import com.gainsight.utils.config.ConfigProviderFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.gainsight.testdriver.Application.basedir;

/**
 * Created by skunchu on 28/03/16.
 */
public class MarvelsDataLoad {
    private final String LOAD_SETUP_DATA_SCRIPT_FOR_MARVELSDATALOADER = "JBCXM.CEHandler.loadSetupData();";
    private final String CREATE_ACCOUNT_CUSTOMER = basedir
            + "/testdata/sfdc/workflow/scripts/Account_And_Customer_Creation.txt";
    private final String CREATE_PLAYBOOK = basedir
            + "/testdata/sfdc/workflow/scripts/Playbook_Creation.txt";
    private final String CREATE_PLAYBOOKTASK_OBJECTIVE = basedir
            + "/testdata/sfdc/workflow/scripts/Playbooktaskcreation_Objective.txt";
    private final String CREATE_PLAYBOOKTASK_RISK = basedir
            + "/testdata/sfdc/workflow/scripts/Playbooktaskcreation_Risk.txt";
    private final String CREATE_PLAYBOOKTASK_OPPORTUNITY = basedir
            + "/testdata/sfdc/workflow/scripts/Playbooktaskcreation_Opportunity.txt";
    private final String CREATE_PLAYBOOKTASK_EVENT = basedir
            + "/testdata/sfdc/workflow/scripts/Playbooktaskcreation_Event.txt";
    private final String CREATE_RELATIONSHIP = basedir
            + "/testdata/sfdc/workflow/scripts/Create_Relationship_for_All_Account.txt";
    private final String CREATE_RELATIONSHIP_CTA = basedir
            + "/testdata/sfdc/workflow/scripts/Create_CTA_and_Tasks_for_Relationship_CTA.txt";
    private final String CREATE_SUCCESSPLAN_TYPE_AND_CATEGORY = basedir
            + "/testdata/sfdc/workflow/scripts/Success_plan_type_and_category.txt";
    private final String CREATE_SUCCESSPLAN_TEMPLATE = basedir
            + "/testdata/sfdc/workflow/scripts/Success_plan_template.txt";
    private final String CREATE_OBJECTIVE_AND_TASK_IN_TEMPLATE  = basedir
            + "/testdata/sfdc/workflow/scripts/Objective_and_task_in_template.txt";
    private final String CREATE_CTA_FOR_SUCCESSPLAN = basedir
            + "/testdata/sfdc/workflow/scripts/SP_with_CTA_and_Task_creation_for_all_stages.txt";
    private final String CREATE_NORMAL_CTA = basedir
            + "/testdata/sfdc/workflow/scripts/Normal_CTA_And_Task.txt";
    public static SalesforceConnector sfdc;
    public static SfdcConfig sfdcConfig = ConfigProviderFactory.getConfig(SfdcConfig.class);
    NSTestBase nsTestBase=null;

    @BeforeClass
    public void loadAccount_And_Customer() throws Exception{

        sfdc = new SalesforceConnector(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword()+ sfdcConfig.getSfdcStoken(),
                sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
        nsTestBase=new NSTestBase();
        sfdc.connect();
        sfdc.runApexCode(nsTestBase.resolveStrNameSpace(LOAD_SETUP_DATA_SCRIPT_FOR_MARVELSDATALOADER));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_ACCOUNT_CUSTOMER));

    }



    @Test(priority = 0)
    public void loadPlaybook_And_Tasks(){
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_PLAYBOOK));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_PLAYBOOKTASK_OBJECTIVE));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_PLAYBOOKTASK_RISK));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_PLAYBOOKTASK_OPPORTUNITY));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_PLAYBOOKTASK_EVENT));
    }

    @Test(priority = 1)
    public void loadRelationship_RelationshipCTAs_And_CSTasks(){
       sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_RELATIONSHIP));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_RELATIONSHIP_CTA));
    }

    @Test(priority = 2)
    public void loadSuccessPlans() {
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_SUCCESSPLAN_TYPE_AND_CATEGORY));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_SUCCESSPLAN_TEMPLATE));
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_OBJECTIVE_AND_TASK_IN_TEMPLATE));
       sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_CTA_FOR_SUCCESSPLAN));

    }
    @Test(priority = 3)
    public void loadNormalCTA() {
        sfdc.runApexCode(nsTestBase.getNameSpaceResolvedFileContents(CREATE_NORMAL_CTA));
    }

    @Test
    public void loadSurveys() {

    }
}
