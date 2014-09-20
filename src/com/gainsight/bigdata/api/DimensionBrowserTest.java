package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.DimensionBrowserInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.HttpResponseObj;

public class DimensionBrowserTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + ApiUrl.COLLECTIONS_GET_DIMENSION;
		baseuri = ApiUrl.setURLParam(baseuri, "QATestData");
	}

	@Test
	public void dimensionBrowser() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String expected = "{\"companyname\":[{\"_id\":\"Gainsight\"},{\"_id\":\"Jbara\"},{\"_id\":\"NewStack\"}]}";
		DimensionBrowserInfo info = new DimensionBrowserInfo();
		info.setColumn("companyname");

		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);

		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		String response = mapper.readTree(result.toString().replaceFirst("content : ", "")).get("data").toString();
		Assert.assertEquals(response, expected);
	}

	@Test
	public void dimensionBrowserWithInvalidCollection() throws Exception {
		String uri = PropertyReader.nsAppUrl + ApiUrl.COLLECTIONS_GET_DIMENSION;
		uri = ApiUrl.setURLParam(baseuri, "InvalidCollection");
		DimensionBrowserInfo info = new DimensionBrowserInfo();

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);

		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}

	@Test
	public void dimensionBrowserWithInvalidColumn() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		DimensionBrowserInfo info = new DimensionBrowserInfo();
		info.setColumn("InvalidColumn");

		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);

		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}

	@AfterClass
	public void tearDown() {

	}
}
