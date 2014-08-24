package com.gainsight.bigdata.api;

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

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.HttpResponseObj;

public class PrepareReportApiTest extends TestBase {

	String uri;
	String testDataLoc = testDataBasePath + "/MatrixFormatterTestData/";

	@BeforeClass
	public void setUp() throws Exception {
		init();

		uri = PropertyReader.nsAppUrl + "/api/reports/run/preparation";
	}

	@Test
	public void test_2Row() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_2Mea() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "2mea_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "2mea_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_1Col_1Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "1col_1measure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "1col_1measure_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_1MeasureOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "1measureOnRow_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "1measureOnRow_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_1Row_1Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "1row_1measure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "1row_1measure_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_1Row_1MeasureOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "1row_1measureOnRow_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "1row_1measureOnRow_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_2Row_2Col_2Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_2col_2measure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_2col_2measure_output.txt"));
		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_2Row_2Measure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_2measure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "2row_2measure_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_3Row_1measure_sortOnMeasure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnMeasure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnMeasure_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_3Row_1measure_sortOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnRow_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnRow_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_3Row_2Col_2measure_sortOnRow() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_2col_2measure_sortOnRow_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_2col_2measure_sortOnRow_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@Test
	public void test_1Row_2measures_dimensionOnMeasure() throws Exception {
		String rawBody = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnMeasure_input.txt"));
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());

		String expectedJson = FileUtils.readFileToString(new File(testDataLoc
				+ "3row_1measure_sortOnMeasure_output.txt"));

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@AfterClass
	public void tearDown() {

	}

	private boolean jsonOutputCompare(String actual, String expected)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);

		if (tree1.equals(tree2)) {
			Report.logInfo("Report output Matched");
			return true;
		} else {
			Report.logInfo("Report output Did Not Match. Expected JSON:\n"
					+ expected + "\n Actual JSON:" + actual);
			return false;
		}
	}
}
