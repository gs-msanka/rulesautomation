package com.gainsight.bigdata.burp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.CollectionInfo.Columns;
import com.gainsight.bigdata.pojo.DimensionBrowserInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;

public class BurpSuiteTest extends NSTestBase {
	
	String tenantName = "AutTenant";
	CollectionInfo cinfo;
	List<Columns> colList = new ArrayList<CollectionInfo.Columns>();
	
	@BeforeClass
	public void setUp() throws Exception {
		// Need to fix this after functional test cases are up and running
		
		/*init();
		cinfo = new CollectionInfo();
		cinfo.setTenantName("DummyTenant");
		Columns col = cinfo.new Columns();
		col.setName("spid");
		col.setDatatype("");
		col.setHide(0);
		col.setIndexable(0);
		col.setColattribtype(0);
		
		colList = new ArrayList<CollectionInfo.Columns>();
		colList.add(col);
		cinfo.setColumns(colList);*/
	}
	
	@Test
	public void createTenant() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenant";
		TenantInfo info = new TenantInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Log.info(rawBody);
		ResponseObj result = wa.doPost(uri, header.getAllHeaders(), rawBody);
		Log.info(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void createCollection() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/createcollection/"+ /*nsinfo.getTenantID()*/  "/AutCollection1";
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Log.info(rawBody);
		ResponseObj result = wa.doPost(uri, header.getAllHeaders(), rawBody);
		Log.info(result.toString());
		JsonNode collectionId = mapper.readTree(result.getContent());
		Log.info(collectionId.get("CollectionID").toString());
		Assert.assertNotNull(collectionId, "Collection ID not found.");
	}

	@Test
	public void getCollection() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/getcollection/";//+ nsinfo.getTenantID();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode inputNode = mapper.readTree(mapper.writeValueAsString(colList));
		
		ResponseObj result = wa.doGet(uri, header.getAllHeaders());
		Log.info(result.toString());
		
		JsonNode node = mapper.readTree(result.getContent());
		JsonNode outputNode  = node.findValue("Columns");
		
		Log.info("PRINTING OUTPUT: "  + outputNode.toString());
		Assert.assertTrue(outputNode.equals(inputNode), "The Collection Response didn't Match");
	}
	
	@Test
	public void test_1Col_1Measure() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/preparereport";
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1col_1measure_input.txt"));
		Log.info("Request:\n"+rawBody);
		ResponseObj result = wa.doPost(uri, header.getAllHeaders(), rawBody);
		Log.info("Actual JSON:\n" + result.getContent());
		
		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1col_1measure_output.txt"));
		Log.info("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
	}
	
	@Test
	public void upsertReport() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/upsertreport";
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.readTree(new File(testDataBasePath + "UpsertReportInput1.txt")).toString();
		Log.info(rawBody);
		ResponseObj result = wa.doPost(uri, header.getAllHeaders(), rawBody);
		Log.info(result.toString());
		JsonNode reportId = mapper.readTree(result.getContent());
		String rid = reportId.get("ReportId").toString();
		Log.info(rid);
		Assert.assertNotNull(reportId, "Report ID not found.");
	}
	
	@Test
	public void getSavedReports() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/savedreports/";//+ nsinfo.getTenantID();
		ResponseObj result = wa.doGet(uri, header.getAllHeaders());
		Log.info(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void dimensionBrowser() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/dimenbrowser";
		DimensionBrowserInfo info = new DimensionBrowserInfo();
		/*info.setTenantId(nsinfo.getTenantID());
		info.setCollectionName("AutCollection1");*/
		info.setColumn("Year");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Log.info(rawBody);
		
		ResponseObj result = wa.doPost(uri, header.getAllHeaders(), rawBody);
		Log.info(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	private boolean jsonOutputCompare(String actual, String expected) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);
		
		if(tree1.equals(tree2)) {
			Log.info("Report output Matched");
			return true;
		}
		else {
			Log.info("Report output Did Not Match");
			return false;
		}
	}
}
