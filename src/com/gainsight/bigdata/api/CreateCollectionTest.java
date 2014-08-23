package com.gainsight.bigdata.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.CollectionInfo.CollectionDetails;
import com.gainsight.bigdata.pojo.CollectionInfo.Columns;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class CreateCollectionTest extends TestBase {

	String baseuri;
	String tenantName = "DummTenant";
	CollectionInfo cinfo;
	List<Columns> colList = new ArrayList<CollectionInfo.Columns>();

	@BeforeClass
	public void setUp() throws Exception {
		init();
		h.addHeader("contextTenantId", "ad0ea8a1-049d-4f52-8a10-2bb2c99a9176");
		long epoch = System.currentTimeMillis();
		baseuri = PropertyReader.nsAppUrl + "/admin/collections";

		cinfo = new CollectionInfo();
		CollectionDetails colDetails = cinfo.new CollectionDetails();
		colDetails.setCollectionName("DummyCollection" + epoch);
		cinfo.setCollectionDetails(colDetails);
		Columns col = cinfo.new Columns();
		col.setDisplayName("dim1");
		col.setDatatype("string");
		col.setColattribtype(0);

		colList = new ArrayList<CollectionInfo.Columns>();
		colList.add(col);
		cinfo.setColumns(colList);
	}

	@Test
	public void createCollection() throws Exception {
		String uri = baseuri;

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		JsonNode collectionId = mapper.readTree(result.getContent());
		Report.logInfo(collectionId.get("data").get("collectionId").toString());
		Assert.assertNotNull(collectionId, "Collection ID not found.");
	}

	@Test
	public void createCollectionWithExistingCollectionName() throws Exception {
		String uri = baseuri + "/AutCollection1";

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode collectionId = mapper.readTree(result.getContent());
		Assert.assertNull(collectionId, "Collection ID should be Empty found.");
	}

	@Test
	public void createCollectionWithDummyTenantId() throws Exception {
		String uri = PropertyReader.nsAppUrl
				+ "/createcollection/DummyTenantID/AutCollection1";

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		NsResponseObj obj = mapper.readValue(result.getContent(),
				NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : "
				+ result.getContent());
	}

	@Test
	public void createCollectionWithImproperInput() throws Exception {
		String uri = baseuri + "/AutImproperInput";

		CollectionInfo cinfo = new CollectionInfo();
		// cinfo.setTenantName("DummyTenantName");
		Columns col = cinfo.new Columns();
		col.setDisplayName("spid");
		col.setColattribtype(0);

		List<Columns> colList = new ArrayList<CollectionInfo.Columns>();
		colList.add(col);
		cinfo.setColumns(colList);

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Report.logInfo(rawBody);
		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode collectionId = mapper.readTree(result.getContent());
		Assert.assertNull(collectionId, "Collection ID should be Empty found.");
	}

	@Test
	public void createCollectionWithInvalidAuthHeader() throws Exception {
		String uri = baseuri + "/AutIvalidAuthHeader";

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Report.logInfo(rawBody);
		// Invalid Auth Token
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		h.addHeader("Origin", origin);

		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"),
				"Invalid Auth Header is accepted");
	}

	@Test
	public void createCollectionWithInvalidContentType() throws Exception {
		String uri = baseuri + "/AutInvalidContentType";

		ObjectMapper mapper = new ObjectMapper();
		String rawBody = mapper.writeValueAsString(cinfo);
		Report.logInfo(rawBody);
		// Invalid Request Content Type
		Header h = new Header();
		h.addHeader("Content-Type", "text/plain");
		h.addHeader("authToken", nsinfo.getAuthToken());
		h.addHeader("Origin", origin);

		HttpResponseObj result = wa.doPost(uri, rawBody, h.getAllHeaders());
		Report.logInfo(result.toString());
		JsonNode collectionId = mapper.readTree(result.getContent());
		Assert.assertNull(collectionId, "Collection ID should be Empty found.");
	}

	@AfterClass
	public void tearDown() {

	}
}
