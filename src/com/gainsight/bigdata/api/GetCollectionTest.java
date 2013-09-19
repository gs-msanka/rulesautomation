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
import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.pojo.CollectionInfo.Columns;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;

public class GetCollectionTest extends TestBase {

	String baseuri;

	@BeforeClass
	public void setUp() throws Exception {
		init();
		baseuri = PropertyReader.nsAppUrl + "/getcollection/"+ nsinfo.getTenantID();
	}
	
	@Test
	public void getCollection() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		//TestData to Compare
		CollectionInfo cinfo = new CollectionInfo();
		cinfo.setTenantName("DummyTenantName");
		Columns col = cinfo.new Columns();
		col.setName("spid");
		col.setDatatype("");
		col.setHide(0);
		col.setIndexable(0);
		col.setColattribtype(0);
		
		List<Columns> colList = new ArrayList<CollectionInfo.Columns>();
		colList.add(col);
		cinfo.setColumns(colList);
		JsonNode inputNode = mapper.readTree(mapper.writeValueAsString(colList));
		
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		
		JsonNode node = mapper.readTree(result.getContent());
		JsonNode outputNode  = node.findValue("Columns");
		
		Report.logInfo("PRINTING OUTPUT: "  + outputNode.toString());
		Assert.assertTrue(outputNode.equals(inputNode), "The Collection Response didn't Match");
	}
	
	@Test
	public void getCollectionForInvalidTenantId() throws Exception {
		String uri = PropertyReader.nsAppUrl + "/getcollection/GarbageTenantID";
		HttpResponseObj result = wa.doGet(uri, h.getAllHeaders());
		Report.logInfo(result.toString());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(result.getContent());
		Report.logInfo(node.toString());
//		Report.logInfo("" + node.get(0).toString().isEmpty());
		Assert.assertNotNull(node.get(0), "No input Returned or even an error message returned");
	}
	
	@Test
	public void getCollectionForInvalidAuthHeader() throws Exception {
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", "AddingGarbage");
		HttpResponseObj result = wa.doGet(baseuri, h.getAllHeaders());
		Report.logInfo(result.toString());
		Assert.assertTrue(result.getContent().equals("Invalid AuthToken"), "Invalid Auth Header is accepted");
	}
	
	@AfterClass
	public void tearDown() {
		
	}
}
