package com.gainsight.bigdata.report;

import java.io.File;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.util.PropertyReader;

public class RestTester {

	static NSInfo nsinfo;
	static WebAction wa;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		init();
		
		doReportFuzzTest();
		
		destroy();
		
	}

	private static void init() throws Exception {
		// TODO Auto-generated method stub
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		nsinfo= NSUtil.fetchNewStackInfo(sfinfo, new Header());
		wa = new WebAction();
	}

	private static void doReportFuzzTest() throws Exception {
		// TODO Auto-generated method stub
		String rawBody = "{\"TenantId\":\"ea745732-af3d-4216-b68d-f10375348c66\",\"ReportInfo\":[{\"SchemaName\":\"Angies.UsageData\",\"limit\":10,\"Type\":\"adhoc\",\"Dimensions\":[{\"col\":\"Category\",\"axis\":\"column\"},{\"col\":\"CurrentReviews\",\"axis\":\"measure\",\"agg_func\":\"sum\"}]}],\"FormatOfReport\":\"matrix\"}";
		Header h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", nsinfo.getAuthToken());
		ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/preparereport", h.getAllHeaders(), rawBody);
		System.out.println(result.getContent());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(new File("./testdata/newstack/MatrixFormatterTestData/1col_1measure_output.txt"));
		JsonNode tree2 = mapper.readTree(result.getContent());
		
		if(tree1.equals(tree2))
			System.out.println("Equal");
		else
			System.out.println("Not Equal");
	}
	
	private static boolean jsonFormatCompare(String actual, String expected) {
		return true;
	}
	
	private static void destroy() {
		// TODO Auto-generated method stub
		
	}
}
