package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.HttpResponseObj;

public class GetAllReportsTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + ApiUrl.REPORT_GET_ALL;
	}

	@Test
	public void getSavedReports() throws Exception {
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}

	@AfterClass
	public void tearDown() {

	}
}
