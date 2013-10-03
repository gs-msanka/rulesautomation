package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.DimensionBrowserInfo;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;

public class DimensionBrowserTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + "/dimenbrowser";
	}
	
	@Test
	public void dimensionBrowser() throws Exception {
		DimensionBrowserInfo info = new DimensionBrowserInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setTenantId(nsinfo.getTenantID());
		info.setCollectionName("AutCollection1");
		info.setColumn("Year");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void dimensionBrowserWithInvalidTenantId() throws Exception {
		DimensionBrowserInfo info = new DimensionBrowserInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setTenantId("Invalid Tenant ID");
		info.setCollectionName("Angies.UsageData");
		info.setColumn("Year");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void dimensionBrowserWithInvalidColName() throws Exception {
		DimensionBrowserInfo info = new DimensionBrowserInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setTenantId(nsinfo.getTenantID());
		info.setCollectionName("Angies.InvalidCollectionName");
		info.setColumn("Year");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void dimensionBrowserWithInvalidColumn() throws Exception {
		DimensionBrowserInfo info = new DimensionBrowserInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setTenantId(nsinfo.getTenantID());
		info.setCollectionName("Angies.UsageData");
		info.setColumn("InvalidColumn");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void dimensionBrowserWithInvalidAuthHeader() throws Exception {
		DimensionBrowserInfo info = new DimensionBrowserInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setTenantId(nsinfo.getTenantID());
		info.setCollectionName("Angies.UsageData");
		info.setColumn("InvalidColumn");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		h.addHeader("Origin", origin);
		
		HttpResponseObj result = wa.doPost(baseuri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@AfterClass
	public void tearDown() {
		
	}
}
