package com.gainsight.bigdata.report;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.WebAction;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.pojo.SFDCInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.bigdata.util.SFDCUtil;
import com.gainsight.pageobject.core.Report;

public class PrepareReportApiTest {

	private NSInfo nsinfo;
	private WebAction wa;
	private Header h;
	String uri;
	String testDataBasePath = "./testdata/newstack/MatrixFormatterTestData/";

	@BeforeClass
	public void setUp() throws Exception {
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		nsinfo = NSUtil.fetchNewStackInfo(sfinfo);
		wa = new WebAction();

		h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", nsinfo.getAuthToken());

		uri = PropertyReader.nsAppUrl + "/preparereport";
	}
	
	@Test
	public void test_1Col_1Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1col_1measure_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());
		
		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1col_1measure_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}

	@Test
	public void test_1Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1measure_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1measure_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}
	
	@Test
	public void test_1MeasureOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1measureOnRow_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1measureOnRow_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}
	
	@Test
	public void test_1Row_1Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1row_1measure_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1row_1measure_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}
	
	@Test
	public void test_1Row_1MeasureOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "1row_1measureOnRow_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "1row_1measureOnRow_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}
	
	@Test
	public void test_2Row_2Col_2Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "2row_2col_2measure_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "2row_2col_2measure_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}
	
	@Test
	public void test_2Row_2Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataBasePath
				+ "2row_2measure_input.txt"));
		Report.logInfo("Request:\n"+rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo("Actual JSON:\n" + result.getContent());

		String expectedJson = FileUtils.readFileToString(new File(
				testDataBasePath + "2row_2measure_output.txt"));
		Report.logInfo("Expected JSON:\n" + expectedJson);
		
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Json Output did not match the expected report. " +
				"It happens in 2 conditions either the format is wrong or the values are wrong");
		
	}

	@AfterClass
	public void tearDown() {

	}

	private boolean jsonOutputCompare(String actual, String expected) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);
		
		if(tree1.equals(tree2)) {
			Report.logInfo("Report output Matched");
			return true;
		}
		else {
			Report.logInfo("Report output Did Not Match");
			return false;
		}
	}
}
