package com.gainsight.bigdata.api;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.CollectionInfo.Columns;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;

public class GetCollectionTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + "/admin/collections/";
	}
	
	@Test
	public void getCollection() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		
		//TestData to Compare
		CollectionInfo cinfo = new CollectionInfo();
		cinfo.setCollectionName("SampleCollection");
		Columns col = cinfo.new Columns();
		col.setDisplayName("Name");
		col.setDatatype("string");
		col.setHidden(false);
		col.setIndexable(0);
		col.setColattribtype(0);
		
		List<Columns> colList = new ArrayList<CollectionInfo.Columns>();
		colList.add(col);
		cinfo.setColumns(colList);
		JsonNode inputNode = mapper.readTree(mapper.writeValueAsString(cinfo));
		
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		JsonNode node = mapper.readTree(result.getContent());
		JsonNode outputNode  = node.findValue("Columns");
		
		Assert.assertNotNull(outputNode, "No Data Found for Columns");
		Report.logInfo("PRINTING OUTPUT: "  + outputNode.toString());
		Assert.assertTrue(outputNode.equals(inputNode), "The Collection Response didn't Match");
	}
	
	@Test
	public void getCollectionForInvalidTenantId() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/getcollection/asdasdas/ALL";
		Report.logInfo(uri);
		HttpResponseObj result = wa.doGet(uri, h.getAllHeaders());
		Report.logInfo(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		NsResponseObj obj = mapper.readValue(result.getContent(), NsResponseObj.class);
		Assert.assertFalse(obj.isResult(), "Result Returned was true : " + result.getContent());
	}
	
	@Test
	public void getCollectionForInvalidAuthHeader() throws Exception {
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		h.addHeader("Origin", origin);
		
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@AfterClass
	public void tearDown() {
		
	}
}
