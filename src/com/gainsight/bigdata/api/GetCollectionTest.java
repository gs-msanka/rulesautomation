package com.gainsight.bigdata.api;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
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

public class GetCollectionTest extends TestBase {

	String baseuri;
	String testDataLoc = testDataBasePath;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + ApiUrl.COLLECTIONS_GET;
		baseuri = ApiUrl.setURLParam(baseuri, "8d405860-d6ec-46a1-8401-438ff0f635f4");
	}

	@Test
	public void getCollection() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		String expectedJson = mapper.readTree(new File(testDataLoc + "/collectionmaste.txt")).toString();

		Assert.assertTrue(jsonOutputCompare(result.getContent(), expectedJson),
				"Expected and Actual Json not match. Format/Values are wrong");
	}

	@AfterClass
	public void tearDown() {

	}

	private boolean jsonOutputCompare(String actual, String expected)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual).get("data");
		JsonNode tree2 = mapper.readTree(expected).get("data");
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
