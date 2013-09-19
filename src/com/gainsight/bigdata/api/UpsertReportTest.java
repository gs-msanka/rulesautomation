package com.gainsight.bigdata.api;

import java.io.File;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;

public class UpsertReportTest extends TestBase {

	String baseuri;
	String testDataBasePath = "./testdata/newstack/UpsertReportTestData/";
	String rid = "f4e1f1a7-c887-4972-93e5-d446d231b77e";
	
	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + "/upsertreport";
	}
	
	@Test
	public void upsertReport() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataBasePath + "UpsertReportInput1.txt")).toString();
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		rid = reportId.get("ReportId").toString();
		Report.logInfo(rid);
		Assert.assertNotNull(reportId, "Report ID not found.");
	}
	
	@Test
	public void upsertExistingReport() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataBasePath + "UpsertReportInput2.txt")).toString();
		rawBody = rawBody.replaceFirst("\"ReportId\":\"\"", "\"ReportId\":\""+rid+"\"");
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		Report.logInfo(reportId.get("ReportId").toString());
		Assert.assertNotNull(reportId, "Report ID not found.");
	}
	
	@Test
	public void upsertExistingReportWithEmptyReportId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataBasePath + "UpsertReportInput2.txt")).toString();
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		Report.logInfo(reportId.get("ReportId").toString());
		Assert.assertNull(reportId, "Report ID not found.");
	}
	
	@Test
	public void upsertReportWithEmptyRequest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = "";
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.getErrorCode().equals("600"), "Wrong Status Code being Sent");
	}
	
	@Test
	public void upsertReportWithInvalidJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = "sdgfhghjkml345678";
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.getErrorCode().equals("601"), "Wrong Status Code being Sent");
	}
	
	@Test
	public void upsertReportWithInvalidAuthHeader() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataBasePath + "UpsertReportInput2.txt")).toString();
		Report.logInfo(rawBody);
		
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@AfterClass
	public void tearDown() {
		
	}

}
