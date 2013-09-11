package com.gainsight.bigdata.api;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.WebAction;
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.SFDCInfo;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.bigdata.util.SFDCUtil;
import com.gainsight.pageobject.core.Report;

public class SalesForceLoginTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		baseuri = PropertyReader.nsAppUrl + "/sflogin";
		sfinfo = SFDCUtil.fetchSFDCinfo();
		wa = new WebAction();

		h = new Header();
		h.addHeader("Content-Type", "application/json");
	}
	
	@Test
	public void sfLogin() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(sfinfo);
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertTrue(obj.isResult(),
				"Fetching Access Token failed : " + result);
	}
	
	@Test
	public void sfLoginWithInvalidJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String rawBody = "asdasd";
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
		
		Assert.assertTrue(obj.getErrorCode().equals("600"), "Wrng Error Code Returned : " + result);
	}
	
	@Test
	public void sfLoginWithInvalidInput() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SFDCInfo sfinfo = new SFDCInfo();
		sfinfo.setOrg("InvalidOrgId");
		sfinfo.setUserId("InvalidUserId");
		sfinfo.setEndpoint("InvalidEndpoint");
		sfinfo.setSessionId("InvalidSessionId");
		String rawBody = mapper.writeValueAsString(sfinfo);
		
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
	}
	
	@Test
	public void sfLoginWithMissingOrgId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SFDCInfo sfinfo = new SFDCInfo();
		sfinfo.setUserId("InvalidUserId");
		sfinfo.setEndpoint("InvalidEndpoint");
		sfinfo.setSessionId("InvalidSessionId");
		String rawBody = mapper.writeValueAsString(sfinfo);
		
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
		
		Assert.assertTrue(obj.getErrorCode().equals("601"), "Missing Org Id is not Recognized : " + result);
	}
	
	@Test
	public void sfLoginWithMissingUserId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SFDCInfo sfinfo = new SFDCInfo();
		sfinfo.setOrg("InvalidOrgId");
		sfinfo.setEndpoint("InvalidEndpoint");
		sfinfo.setSessionId("InvalidSessionId");
		String rawBody = mapper.writeValueAsString(sfinfo);
		
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
		
		Assert.assertTrue(obj.getErrorCode().equals("604"), "Missing User Id is not Recognized : " + result);
	}
	
	@Test
	public void sfLoginWithMissingSessionId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		SFDCInfo sfinfo = new SFDCInfo();
		sfinfo.setOrg("InvalidOrgId");
		sfinfo.setUserId("InvalidUserId");
		sfinfo.setEndpoint("InvalidEndpoint");
		String rawBody = mapper.writeValueAsString(sfinfo);
		
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
		
		Assert.assertTrue(obj.getErrorCode().equals("603"), "Missing User Id is not Recognized : " + result);
	}
	
	@Test
	public void sfLoginWithInvalidSessionId() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		sfinfo.setSessionId("InvalidSessionId");
		String rawBody = mapper.writeValueAsString(sfinfo);
		
		HttpResponseObj result = wa.doPost(baseuri,	rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result should return false : " + result);
	}
	
	@AfterClass
	public void tearDown() {
		
	}

}
