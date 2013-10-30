package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;

public class SavedReportTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + "/savedreports/"+ nsinfo.getTenantID()+"/ALL";
	}
	
	@Test
	public void getSavedReports() throws Exception {
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void getSavedReportsWithInvalidTenantId() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/savedreports/InvalidTenantId/ALL";
		HttpResponseObj result = wa.doGet(uri, h.getAllHeaders());
		Report.logInfo(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void getSavedReportsWithInvalidAuthHeader() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/savedreports/InvalidTenantId/ALL";
		
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		h.addHeader("Origin", origin);
		
		HttpResponseObj result = wa.doGet(uri, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@AfterClass
	public void tearDown() {
		
	}
}
