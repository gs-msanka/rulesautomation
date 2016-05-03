package com.gainsight.bigdata.rulesengine.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.DataLoadConfigPojo;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects;
import com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo.LoadableObjects.DataLoadObject;
import com.gainsight.bigdata.rulesengine.pages.DataLoadConfiguration;
import com.gainsight.bigdata.rulesengine.pages.EditRulePage;
import com.gainsight.bigdata.rulesengine.pages.RulesConfigureAndDataSetup;
import com.gainsight.bigdata.rulesengine.pages.RulesManagerPage;
import com.gainsight.bigdata.rulesengine.pages.SetupRuleActionPage;
import com.gainsight.bigdata.rulesengine.pojo.RulesPojo;
import com.gainsight.bigdata.rulesengine.pojo.enums.ActionType;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CTAAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToFeatureAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.LoadToMileStoneAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.RuleAction;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.SetScoreAction;
import com.gainsight.bigdata.rulesengine.util.RulesEngineUtil;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.pageobject.core.Element;
import com.gainsight.sfdc.BaseSalesforceConnector;
import com.gainsight.sfdc.administration.pages.AdminScorecardSection;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.gsEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.rulesEngine.setup.RuleEngineDataSetup;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MongoDBDAO;
import com.gainsight.utils.Verifier;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by vmenon on 8/27/2015.
 */
public class CreateRuleTest extends BaseTest {

    private static final String CLEAN_UP_FOR_RULES = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/CleanUpForRules.apex";
    private static final String CREATE_ACCOUNTS_CUSTOMERS = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Create_Accounts_Customers.txt";
    private static final String LOAD_ACCOUNTS_JOB = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Jobs/Job_Accounts.txt";
    private RulesEngineUtil rulesEngineUtil = new RulesEngineUtil();
    MongoDBDAO mongoDBDAO = new MongoDBDAO(nsConfig.getGlobalDBHost(), Integer.valueOf(nsConfig.getGlobalDBPort()), nsConfig.getGlobalDBUserName(), nsConfig.getGlobalDBPassword(), nsConfig.getGlobalDBDatabase());
    private static final String CLEANUP_FEATURES = Application.basedir + "/testdata/newstack/RulesEngine/LoadToFeature/FeatureInsertion.apex";
    private static final String SET_USAGE_DATA_LEVEL_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/Set_Account_Level_Monthly.apex";
    private static final String SET_USAGE_DATA_MEASURE_FILE = Application.basedir + "/testdata/sfdc/rulesEngine/scripts/UsageData_Measures.apex";
    private static final String NUMERIC_SCHEME_FILE = Application.basedir + "/apex_scripts/scorecard/Scorecard_enable_numeric.apex";
    private static final String USAGE_DATA_FILE = "/testdata/newstack/RulesEngine/RulesUI-Data/UsageData_Monthly.csv";
    private static final String COLLECTION_MASTER = "collectionmaster";
    private static final String RULES_LOADABLE_OBJECT = "rulesLoadableObject";
    private static final String SCHEME = "Score";
    private static final String METRICS_CREATE_FILE =  Application.basedir + "/apex_scripts/scorecard/Create_ScorecardMetrics.apex";
    private static final String CLEANUP_DATA = Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-Scripts/Cleanup.apex";
    private ObjectMapper mapper = new ObjectMapper();
    TenantDetails tenantDetails = null;
    RulesConfigureAndDataSetup rulesConfigureAndDataSetup = new RulesConfigureAndDataSetup();
    NSTestBase nsTestBase = new NSTestBase();
    ReportManager reportManager = new ReportManager();
    DataETL dataETL = new DataETL();
    private RulesUtil rulesUtil = new RulesUtil();
    public List<String> collectionNames = new ArrayList<String>();
    private TenantManager tenantManager;
    private RulesManagerPage rulesManagerPage;
    private String rulesManagerPageUrl;
    private MongoDBDAO mongoConnection;
    


    @BeforeClass
    public void setUp() throws Exception {
		nsTestBase.init();
		rulesManagerPageUrl=visualForcePageUrl+"Rulesmanager";
		rulesManagerPage = new RulesManagerPage();
		rulesUtil.populateObjMaps();
	    nsTestBase.tenantAutoProvision();
        tenantManager= new TenantManager();
        GSEmailSetup gs=new GSEmailSetup();
        MDAIntegrationImpl integrationImpl = new MDAIntegrationImpl(NSTestBase.header);
        if(!integrationImpl.isMDAAuthorized()) {
            Log.info("MDA is not authorised, so authorizing now");
        gs.enableOAuthForOrg();
        }
        tenantDetails = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null);
        tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
        rulesConfigureAndDataSetup.createCustomObjectAndFieldsInSfdc();
        sfdc.runApexCode("Delete [SELECT Id FROM RulesSFDCCustom__c];");
        metaUtil.createExtIdFieldForScoreCards(sfdc);
        AdministrationBasePage administrationBasePage = basepage.clickOnAdminTab();
        AdminScorecardSection adminScorecardSection = administrationBasePage.clickOnScorecardSection();
        adminScorecardSection.enableScorecard();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(NUMERIC_SCHEME_FILE));
        runMetricSetup(METRICS_CREATE_FILE, SCHEME);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEAN_UP_FOR_RULES));
        metaUtil.createFieldsOnUsageData(sfdc);
        metaUtil.createFieldsForAccount(sfdc, sfdc.fetchSFDCinfo());
        metaUtil.createFieldsOnAccount(sfdc);
        metaUtil.createExtIdFieldOnAccount(sfdc);
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_FEATURES));
        // Updating timeZone to America/Los_Angeles in Application settings
        rulesConfigureAndDataSetup.updateTimeZoneInAppSettings("America/Los_Angeles");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCOUNTS_CUSTOMERS));
        JobInfo jobInfo = mapper.readValue((new FileReader(LOAD_ACCOUNTS_JOB)), JobInfo.class);
        dataETL.execute(jobInfo);
	}

    @BeforeMethod
    public void rulesCleanup() {
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CLEANUP_DATA));
    }

	// TestCase Id's mapped belongs to setscore and load to usage, other actions are covered seperately
	@TestInfo(testCaseIds = { "GS-5148", "GS-5155", "GS-9075","GS-4974" })
    @Test
    public void testAllActionsUsingNativeData() throws Exception {
    	rulesConfigureAndDataSetup.createDataLoadConfiguration();
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC1.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Check whether Rule ran successfully or not !");
        assertForAllActionsUsingSFDCData(rulesPojo);
    }

    @TestInfo(testCaseIds = { "GS-9068", "GS-4187", "GS-4239"})
    @Test
    public void testCTAActionWithCalculatedFieldsOnUsageDataObject() throws Exception {
        RuleEngineDataSetup ruleEngineDataSetup = new RuleEngineDataSetup();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_LEVEL_FILE));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(SET_USAGE_DATA_MEASURE_FILE));
        ruleEngineDataSetup.loadUsageData(dataETL, USAGE_DATA_FILE, false);
        RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/CTAActionWithCalculatedFields.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        Assert.assertTrue(rulesUtil.runRule(rulesPojo.getRuleName()), "Rule Created and Ran Successfully!");
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        for (RuleAction ruleAction : ruleActions) {
            JsonNode actionObject = ruleAction.getAction();
            CTAAction ctaAction = mapper.readValue(actionObject, CTAAction.class);
            Assert.assertTrue(rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(), sfdcInfo.getUserId(),
                    ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook()),
                    "verify whether cta action configured resulted in  correct cta or not");
        }
    }
	
    public void assertForAllActionsUsingSFDCData(RulesPojo rulesPojo) throws Exception {
        List<RuleAction> ruleActions = rulesPojo.getSetupActions();
        Verifier verifier = new Verifier();
        for (RuleAction ruleAction : ruleActions) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode actionObject = ruleAction.getAction();
            ActionType actionType = ruleAction.getActionType();
            SetupRuleActionPage setupRuleActionPage = new SetupRuleActionPage();
            int srcObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(setupRuleActionPage.queryString(ruleAction.getCriterias())));
            switch (actionType) {
                case CTA:
                    CTAAction ctaAction = objectMapper.readValue(actionObject, CTAAction.class);
                    verifier.verifyTrue((rulesUtil.isCTACreateSuccessfully(ctaAction.getPriority(), ctaAction.getStatus(),
                            sfdcInfo.getUserId(), ctaAction.getType(), ctaAction.getReason(), ctaAction.getComments(), ctaAction.getName(), ctaAction.getPlaybook())),
                            "verify whether cta action configured resulted in  correct cta or not");
                    Log.info("Verification done for cta");
                    break;
                case LoadToCustomers:
                    int trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false"));
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    break;
                case LoadToFeature:
                    LoadToFeatureAction loadToFeatureAction = objectMapper.readValue(actionObject, LoadToFeatureAction.class);
                    boolean licenced = false;
                    if (loadToFeatureAction.getLicensed().getUpdateType().equalsIgnoreCase("Licenced")) {
                        licenced = true;
                    } else {
                        licenced = false;
                    }
                    boolean enabled = false;
                    if (loadToFeatureAction.getEnabled().getUpdateType().equalsIgnoreCase("Enabled")) {
                        enabled = true;
                    } else {
                        enabled = false;
                    }
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace(
                            "Select JBCXM__Account__c,JBCXM__Comment__c,JBCXM__Enabled__c,JBCXM__Licensed__c from JBCXM__CustomerFeatures__c where JBCXM__Features__r.JBCXM__Feature__c='"
                                    + loadToFeatureAction.getFeature() + "' and JBCXM__Features__r.JBCXM__Product__c='" + loadToFeatureAction.getProduct() + "' and JBCXM__Enabled__c=" + enabled + " and JBCXM__Licensed__c=" + licenced + " and isDeleted=false"));
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    Log.info("Verification done for Load to feature");
                    break;
                case LoadToMileStone:
                    LoadToMileStoneAction loadToMileStoneAction = objectMapper.readValue(actionObject, LoadToMileStoneAction.class);
                    int loadToMilestoneRecords = sfdc.getRecordCount(
                            resolveStrNameSpace("SELECT Id FROM JBCXM__Milestone__c where IsDeleted = false and JBCXM__Milestone__r.name='" + loadToMileStoneAction.getSelectMilestone() + "'"));
                    Log.info("Load to milestone records count" + loadToMilestoneRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToMilestoneRecords);
                    break;
                case LoadToUsage:
                    int loadToUsageRecords = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__Account__c,JBCXM__Date__c,JBCXM__ExternalId__c,JBCXM__InstanceId__c,JBCXM__InstanceName__c,JBCXM__WeekLabel__c FROM JBCXM__UsageData__c where  isDeleted=false"));
                    Log.info("No of records for load to usage" + loadToUsageRecords);
                    verifier.verifyEquals(srcObjRecCount, loadToUsageRecords);
                    break;
                case SetScore:
                    SetScoreAction setScoreAction = objectMapper.readValue(actionObject, SetScoreAction.class);
                    SObject[] JBCXM__MetricId__c = sfdc
                            .getRecords(resolveStrNameSpace("SELECT Id FROM JBCXM__ScorecardMetric__c where Name = '" + setScoreAction.getSelectMeasure() + "'"));
                    int setScoreRecords = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id,JBCXM__CurComment__c,JBCXM__CurScoreId__c,JBCXM__MetricId__c FROM JBCXM__ScorecardFact__c where JBCXM__MetricId__c='" + JBCXM__MetricId__c[0].getField("Id").toString() + "' AND isDeleted=false"));
                    Log.info("No of records for set score" + setScoreRecords);
                    verifier.verifyEquals(srcObjRecCount, setScoreRecords);
                    break;
                case LoadToSFDCObject:
                    trgtObjRecCount = sfdc.getRecordCount(resolveStrNameSpace("SELECT Id FROM RulesSFDCCustom__c where IsDeleted = false "));
                    Log.info("Load to sfdc records count" + trgtObjRecCount);
                    verifier.verifyEquals(srcObjRecCount, trgtObjRecCount);
                    break;
                default:
                    break;
            }
        }
        verifier.assertVerification();
    }
	*
	 * TestCase to verify Gainsight package objects(JBCXM) are available under
	 * dataload configuration Dropdownlist or not.
	 * 
	 * This testcase will work in Beta or managed package only(Since, In Dev org
	 * Rules Team is not handling this case)
	 * @throws IOException 

	@TestInfo(testCaseIds = { "GS-9067" })
	@Test
	public void testGainsightObjectsArePresentInDataLoadConfigurationList() throws IOException {
		List<String> jbcxmObjects=BaseSalesforceConnector.getAllGainSightObjects(sfdc.getPartnerConnection());
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnConfigure();
		DataLoadConfiguration dataLoadConfiguration = new DataLoadConfiguration();
		dataLoadConfiguration.selectSourceObjectFromNativeData();
		dataLoadConfiguration.clickOnNativeObjectSelection();
		Element element = new Element();
		env.setTimeout(0);
		Verifier verifier = new Verifier();
		for (int i = 0; i < jbcxmObjects.size(); i++) {
			verifier.verifyFalse((element.isElementPresent("//li[contains(@class, 'ui-multiselect-option')]/descendant::input[@value='"+jbcxmObjects.get(i)+"']/following-sibling::span")),
					"Check whether Gainsight Package Objects are present under DataLoadConfiguration List !!!");
		}
		verifier.assertVerification();
	}
	
	@TestInfo(testCaseIds = { "GS-9066" })
	@Test
	public void testAdditionAndRemovalOFFieldsInDataLoadConfig() throws Exception{
		//Deleting sfdc RulesSFDCCustom__c object
		rulesUtil.deleteObjectInRulesConfig("RulesSFDCCustom__c", "SFDC");
		DataLoadConfigPojo dataLoadConfigPojo = mapper.readValue(
				new FileReader(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC7.json"),DataLoadConfigPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnConfigure();
		for (LoadableObjects loadableObject : dataLoadConfigPojo.getLoadableObjects()) {
			DataLoadConfiguration dataLoadConfiguration = new DataLoadConfiguration();
			dataLoadConfiguration.selectDataSource(loadableObject.getObjectType());
			for (DataLoadObject dataLoadObject : loadableObject.getDataLoadObject()) {
				dataLoadConfiguration.selectSourceObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.clickOnNativeObjectSelectionSymbol();
				dataLoadConfiguration.clickOnParticularObject(dataLoadObject.getObjectName());
				dataLoadConfiguration.selectFieldsFromList(dataLoadObject);
				dataLoadConfiguration.removeFieldsFromList(dataLoadObject);
				dataLoadConfiguration.clickOnSaveButton();
				int expectedList = dataLoadObject.getFields().size() - dataLoadObject.getRemoveFields().size();
				System.out.println("Expected Size is " + expectedList);
				Element element = new Element();
				List<WebElement> actualList = element.getAllElement("//select[contains(@class,'selectedFields fieldSelect')]/descendant::option");
				Log.info("Actual Selected Fileds List in UI is " + actualList.size());
				Assert.assertEquals(actualList.size(), expectedList,  "Check Dropdown list Size in UI with actual testdata");
			}
		}
	}
	
	@TestInfo(testCaseIds = { "GS-3158" })
	@Test
	public void dailyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC9.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");
	}
	
	@TestInfo(testCaseIds = { "GS-3159" })
	@Test
	public void weeklyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC10.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!"); 
	}
	
	@TestInfo(testCaseIds = { "GS-3160" })
	@Test
	public void monthlyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC11.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		} 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");   
	}
	
	@TestInfo(testCaseIds = { "GS-3161" })
	@Test
	public void yearlyScheduler() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir + "/testdata/newstack/RulesEngine/RulesUI-TestData/TC12.json"), RulesPojo.class);
        rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
        rulesPojo.getShowScheduler().setStartDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getStartDate()), 0, false)));
        rulesPojo.getShowScheduler().setEndDate((getDateWithFormat(Integer.valueOf(rulesPojo.getShowScheduler().getEndDate()), 0, false)));
        rulesManagerPage.clickOnAddRule();
        rulesEngineUtil.createRuleFromUi(rulesPojo);
        String ruleID=null;
        SObject[] result =sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__AutomatedAlertRules__c where JBCXM__LastRunResult__c=null order by CreatedDate desc limit 1"));
		if (result.length > 0) {
			 ruleID = (String) result[0].getField("Id");			
		}else {
			throw new RuntimeException("RuleID is not present, Please check Automated Alert Rules Object");
		}
	 
		String actualCronExpression = rulesConfigureAndDataSetup.getCronExpressionFromDb(tenantDetails.getTenantId(), ruleID);
		Assert.assertEquals(actualCronExpression, rulesPojo.getShowScheduler().getCronExpression(), "Cron Expression is not matching, Kindly check !!");
	}
	
	@TestInfo(testCaseIds = { "GS-3153" })
	@Test
	public void testRuleInactive() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC14.json"),RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.switchOffRuleByName(rulesPojo.getRuleName());
		Assert.assertTrue(rulesManagerPage.isRuleInActive(rulesPojo.getRuleName()), "Check whether rule is active or inactive!! ");
	}
	
	@TestInfo(testCaseIds = { "GS-4115" })
	@Test
	public void testCloningOFARule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC15.json"),RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.cloneARuleByName(rulesPojo.getRuleName(), rulesPojo.getRuleName()+ "CLONED");
		Assert.assertTrue(rulesManagerPage.isRuleInActive(rulesPojo.getRuleName()+ "CLONED"), "Check whether rule is cloned or not !!");
		SObject[] rule1Criteria=sfdc.getRecords(resolveStrNameSpace("select ID,JBCXM__TriggerCriteria__c from JBCXM__AutomatedAlertRules__c where Name='"+rulesPojo.getRuleName()+"'"));
		SObject[] rule2Criteria=sfdc.getRecords(resolveStrNameSpace("select ID,JBCXM__TriggerCriteria__c from JBCXM__AutomatedAlertRules__c where Name='"+rulesPojo.getRuleName()+ "CLONED"+"'"));
		Assert.assertEquals(rule1Criteria[0].getField("JBCXM__TriggerCriteria__c"), rule2Criteria[0].getField("JBCXM__TriggerCriteria__c"), "verify json data for autual rule and cloned rule");
	}
	
	@TestInfo(testCaseIds = { "GS-3152" })
	@Test
	public void testDeleteRule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC16.json"),RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.deleteRuleByName(rulesPojo.getRuleName());
		env.setTimeout(2);
		Assert.assertFalse(rulesManagerPage.isRulePresentByName(rulesPojo.getRuleName()), "Check whether rule is present or not in UI after deletion!! ");
		
	}

	@TestInfo(testCaseIds = { "GS-3151" })
	@Test
	public void testEditARule() throws Exception{
		RulesPojo rulesPojo = mapper.readValue(new File(Application.basedir+ "/testdata/newstack/RulesEngine/RulesUI-TestData/TC17.json"),RulesPojo.class);
		rulesManagerPage.openRulesManagerPage(rulesManagerPageUrl);
		rulesManagerPage.clickOnAddRule();
		rulesEngineUtil.createRuleFromUi(rulesPojo);
		EditRulePage editRulePage=new EditRulePage();
		editRulePage.clickOnRulesList();
		rulesManagerPage.editRuleByName(rulesPojo.getRuleName());
		Assert.assertTrue(rulesManagerPage.isEditRulePagePresent(), "Check whether clicking on edit rule lands on editrule page or not!!");
	}
}
