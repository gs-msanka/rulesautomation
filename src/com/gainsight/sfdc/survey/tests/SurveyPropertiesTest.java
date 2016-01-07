package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.Integration.utils.MDAIntegrationImpl;
import com.gainsight.sfdc.gsEmail.setup.GSEmailSetup;
import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

public class SurveyPropertiesTest extends SurveySetup{
	
	private final String TEST_DATA_FILE = "testdata/sfdc/survey/tests/SurveyProperties_Test.xls";
	private final String SURVEYDATA_CLEANUP = "Delete [SELECT Id,Name,JBCXM__Title__c FROM JBCXM__Survey__c];";
	private final String CREATE_ACCS=env.basedir+"/testdata/sfdc/survey/scripts/Create_Accounts_For_CompleteAnonymousSurvey.txt";
	ObjectMapper mapper = new ObjectMapper();
	GSEmailSetup gs=new GSEmailSetup();
	NSTestBase ns=new NSTestBase();

	@BeforeClass
	public void setUp() throws Exception {
		Log.info("Adding properties in Survey Properties Tab");
		sfdc.connect();
		basepage.login();
		metaUtil.createExtIdFieldOnAccount(sfdc);
		metaUtil.createExtIdFieldOnContacts(sfdc);
		sfdc.runApexCode(resolveStrNameSpace(SURVEYDATA_CLEANUP));
		updateNSURLInAppSettings(nsConfig.getNsURl());
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_ACCS));
		ns.init();
		ns.tenantAutoProvision();
        MDAIntegrationImpl integrationImpl = new MDAIntegrationImpl(NSTestBase.header);
        if(!integrationImpl.isMDAAuthorized()) {
            Log.info("MDA is not authorised, so authorizing now");
        gs.enableOAuthForOrg();
        }
		gs.updateAccessKeyInApplicationSettingForGSEmail();
	}
    
	@TestInfo(testCaseIds={"GS-2662","GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet1")
	public void testNonAnonymousSurvey(HashMap<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");
	}
	
	@TestInfo(testCaseIds={"GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet2")
	public void testPartialAnonymousSurvey(HashMap<String, String> testData) throws JsonParseException, JsonMappingException, IOException{
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");

	}
	
	@TestInfo(testCaseIds={"GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet3")
	public void testCompleteAnonymousSurvey(HashMap<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");	
	}
	
	@TestInfo(testCaseIds={"GS-3200","GS-2662","GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet4")
	public void testGSEmailNonAnonymousSurvey(HashMap<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");
	}
	
	@TestInfo(testCaseIds={"GS-3200","GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet5")
	public void testGSEmailPartialAnonymousSurvey(HashMap<String, String> testData) throws JsonParseException, JsonMappingException, IOException{
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");

	}
	
	@TestInfo(testCaseIds={"GS-3200","GS-2667","GS-2668","GS-2669"})
	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel", enabled=true)
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "Sheet6")
	public void testGSEmailCompleteAnonymousSurvey(HashMap<String, String> testData)
			throws IOException {
		SurveyBasePage surBasePage = basepage.clickOnSurveyTab();
		SurveyProperties surveyPropData = mapper.readValue(
				testData.get("SurveyProp"), SurveyProperties.class);
		surveyPropData.setStartDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getStartDate()), 0, false));
		surveyPropData.setEndDate(getDateWithFormat(
				Integer.valueOf(surveyPropData.getEndDate()), 0, false));
		SurveyPropertiesPage surPropPage = surBasePage.createSurvey(
				surveyPropData, true);
		surPropPage.updateSurveyProperties(surveyPropData);
		Assert.assertEquals(surPropPage.getPropertiesMessage(),
				"Survey properties saved successfully.");	
	}
}
