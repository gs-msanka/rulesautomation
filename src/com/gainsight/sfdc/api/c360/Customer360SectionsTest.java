package com.gainsight.sfdc.api.c360;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import us.monoid.web.BinaryResource;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import com.gainsight.bigdata.pojo.SFDCInfo;
import com.gainsight.bigdata.util.SFDCUtil;
import com.gainsight.sfdc.util.CompareJSON;
import com.gainsight.utils.DataProviderArguments;
import static us.monoid.web.Resty.*;

public class Customer360SectionsTest {
	private SFDCInfo sfinfo;
	private String endPoint, sessionid;
	Resty r;
	URI uri;
	private final String c360Path = "/services/apexrest/JBCXM/Customer360";
	private String accountQueryPath = "/services/data/v29.0/query/?q=SELECT+id+from+Account+where+name='%s'";
	private String renewalDate = "/services/data/v29.0/query/?q=SELECT+JBCXM__NextRenewalDate__c+from+JBCXM__CustomerInfo__c+where+JBCXM__Account__c='%s'";
	private String summaryJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getCustomerSummary\"}";
	private String scorecardJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getScorecardDataDetailsByCustomer\"}";
	private String transactionsJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getTransactionsByAccountId\"}";
	private String eventsJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getEventsByCustomer\"}";
	private String usageDataJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getUsageDataAndConfig\"}";
	private String accountAttrJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getAccountAttributesByCustomer\"}";
	private String surveysJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getRespondedParticpantsByAllSurveys\"}";
	private String featuresJSON = "{\"accountId\" : \"%s\",  \"actionType\" : \"getFeaturesByCustomer\"}";
	final String TEST_DATA_FILE = "testdata/sfdc/c360api/sections.xls";
	final String OPJSONS = "testdata/sfdc/c360api/OPJSONS";
	private final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

	@BeforeClass
	public void setup() throws URISyntaxException {
		sfinfo = SFDCUtil.fetchSFDCinfo();
		endPoint = sfinfo.getEndpoint();
		sessionid = sfinfo.getSessionId();
		uri = new URI(endPoint + c360Path);
		r = new Resty();
		r.withHeader("Content-Type", "application/json");
		r.withHeader("Authorization", "Bearer " + sessionid);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "summary")
	public void testSummarySection(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		updateSummaryFile(accName, getRenewalDays(accID));
		validateResponseJSON(summaryJSON, "summary", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "scorecard")
	public void testScorecard(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "scorecard", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "transactions")
	public void testTransactions(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "transactions", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "events")
	public void testEvents(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "events", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "usageData")
	public void testUsageData(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "usageData", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "accountAttr")
	public void testAccountAttr(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "accountAttr", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "surveys")
	public void testSurveys(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "surveys", accID, accName);
	}

	@Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
	@DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "features")
	public void testFeatures(HashMap<String, String> testData)
			throws Exception {
		String accName = testData.get("account");
		String accID = getAccountID(testData.get("account"));
		validateResponseJSON(summaryJSON, "features", accID, accName);
	}

	private void validateResponseJSON(String sectionJSON, String sectionName,
			String accID, String accName) throws Exception {
		BinaryResource result = r.bytes(uri,
				content(String.format(sectionJSON, accID)));
		File tempFile = File.createTempFile(accID + "_" + sectionName, ".json");
		File outFile = result.save(tempFile);
		accName = accName.replace(" ", "_");
		File inFile = new File(OPJSONS + "/" + accName + "_" + sectionName
				+ ".json");
		Assert.assertTrue(CompareJSON.compareJSONs(outFile, inFile, true));
		tempFile.delete();
	}

	private String getAccountID(String name) throws Exception {
		URI accURI = new URI(endPoint
				+ String.format(accountQueryPath, name.replace(" ", "+")));
		JSONResource result = r.json(accURI);
		System.out.println(result.toString());
		return (String) result.get("records[0].Id");
	}

	private long getRenewalDays(String accID) throws Exception {
		URI accURI = new URI(endPoint + String.format(renewalDate, accID));
		JSONResource result = r.json(accURI);
		String rDate = (String) result
				.get("records[0].JBCXM__NextRenewalDate__c");
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Date rd = ft.parse(rDate);
		return ((rd.getTime() - System.currentTimeMillis()) / MILLISECONDS_PER_DAY) + 1;
	}

	private void updateSummaryFile(String accName, long renewalDays)
			throws IOException {
		accName = accName.replace(" ", "_");
		String inFileName = OPJSONS + "/" + accName + "_"
				+ "summary_draft.json";
		String outFileName = OPJSONS + "/" + accName + "_" + "summary.json";
		String contents = getFileContents(inFileName);
		contents = String.format(contents, renewalDays);
		System.out.println(contents);
		FileWriter writer = new FileWriter(new File(outFileName));
		writer.write(contents);
		writer.close();
	}

	private String getFileContents(String fileName) {
		String code = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			code = stringBuilder.toString();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
}
