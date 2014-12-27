package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;

public class GetAllReportsTest extends NSTestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		baseuri = PropertyReader.nsAppUrl + ApiUrl.REPORT_GET_ALL;
	}

	@Test
	public void getSavedReports() throws Exception {
		ResponseObj result = wa.doGet(baseuri, header.getAllHeaders());
		Log.info(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}

	@AfterClass
	public void tearDown() {

	}
}
