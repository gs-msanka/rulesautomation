package com.gainsight.bigdata.api;

import java.io.File;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.HttpResponseObj;

public class SaveOrUpdateReportsTest extends TestBase {

	String baseuri;
	String testDataLoc = testDataBasePath + "/UpsertReportTestData/";
	String rid = "750dc4c2-d2f1-4b28-bf3a-80893c49e183";
	long epoch = System.currentTimeMillis() / 1000;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + ApiUrl.REPORT_SAVE;
	}

	@Test
	public void saveReport() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataLoc + "UpsertReportInput1.txt")).toString();
		rawBody = rawBody.replaceFirst("\"reportName\":\"\"", "\"reportName\":\"New Report" + epoch + "\"");
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPut(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		JsonNode data = reportId.findPath("data");
		rid = data.get("ReportId").toString();
		Report.logInfo(rid);
		Assert.assertNotNull(reportId, "Report ID not found.");
	}

	@Test
	public void saveAsReport() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataLoc + "UpsertReportInput2.txt")).toString();
		rawBody = rawBody.replaceFirst("\"ReportId\":\"\"", "\"ReportId\":\"" + rid + "\"");
		rawBody = rawBody.replaceFirst("Automation Report2", "RenamedReport" + epoch);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPut(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		JsonNode data = reportId.findPath("data");
		Report.logInfo(data.get("ReportId").toString());
		Assert.assertNotNull(reportId, "Report ID not found.");
	}

	@Test
	public void updateReport() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataLoc + "UpsertReportInput2.txt")).toString();
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPut(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		JsonNode data = reportId.findPath("data");
		Report.logInfo(data.get("ReportId").toString());
		Assert.assertNotNull(reportId, "Report ID not found.");
	}

	@Test
	public void upsertExistingReportWithEmptyReportId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataLoc + "UpsertReportInput2.txt")).toString();
		rawBody = rawBody.replaceFirst("cf286c13-2371-436f-a9b9-85636968717a", "");
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPut(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		Report.logInfo(reportId.get("result").toString());
		Assert.assertEquals(reportId.get("result").toString(), "false", "Report ID found instead of an Error");
	}

	@Test
	public void upsertReportWithEmptyRequest() throws Exception {
		String rawBody = "";
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPut(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertNotEquals(result.getStatusCode(), 200, "Response was 200 OK");
	}

	@AfterClass
	public void tearDown() {

	}

}
