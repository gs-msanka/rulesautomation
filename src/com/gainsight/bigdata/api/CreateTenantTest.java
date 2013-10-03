package com.gainsight.bigdata.api;

import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;

public class CreateTenantTest extends TestBase{

	String uri;
	String tenantName = "AutTenant";
	
	@BeforeClass
	public void setUp() throws Exception {
		init();
	}
	
	@Test
	public void createTenant() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantFromPgm";
		TenantInfo info = new TenantInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void createTenantWithExistingTenantNameWithDummyOrg() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenant";
		TenantInfo info = new TenantInfo();
//		info.setTenantId(UUID.randomUUID().toString());
		info.setExternalTenantID("dummyOrgId");
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void createTenantWithExisitingTenantNameAndOrg() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenant";
		TenantInfo info = new TenantInfo();
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was false : " + result.getContent());
	}
	
	@Test
	public void createTenantWithExisitingOrgAndDiffTenantName() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantDifferent";
		TenantInfo info = new TenantInfo();
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void createTenantWithImproperInput() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantWithImproperInput";
		TenantInfo info = new TenantInfo();
		info.setTenantId(UUID.randomUUID().toString());
		info.setExternalTenantID(sfinfo.getOrg());
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
//		Assert.assertNotEquals(result.getStatusCode(), 200, "API is not accounting for Missing ExternalTenantName in the API and the record is created in Mongo");
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void createTenantWithWrongInput() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantWithWrongInput";
		String rawBody = "Dummy Text";
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertNotEquals(result.getStatusCode(), 200, "Failed to set proper error status code for this API with request having improper input");
		/*ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());*/
	}
	
	@Test
	public void createTenantWithInvalidAuthHeader() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantInvalidAuthToken";
		TenantInfo info = new TenantInfo();
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		h.addHeader("Origin", origin);
		
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@Test
	public void createTenantWithInvalidContentType() throws Exception {
		uri = PropertyReader.nsAppUrl + "/createtenant/AutTestTenantInvalidContentType";
		TenantInfo info = new TenantInfo();
		info.setExternalTenantID(sfinfo.getOrg());
		info.setExternalTenantName("AutExternalTenantName");
		
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(info);
		Report.logInfo(rawBody);
		
		Header h = new Header();
		h.addHeader("Content-Type", "text/plain");
		h.addHeader("authToken", nsinfo.getAuthToken());
		h.addHeader("Origin", origin);
		
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertNotEquals(result.getStatusCode(), 200, "Content Type verification is not done");
/*		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());*/
	}
	
	@AfterClass
	public void tearDown() {
		
	}
}
