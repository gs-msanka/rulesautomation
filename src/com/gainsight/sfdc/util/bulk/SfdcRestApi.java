package com.gainsight.sfdc.util.bulk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.webaction.WebAction;

public class SfdcRestApi {

	static String query_url;
	static SFDCInfo info;
	static WebAction wa;
	
	static {
		wa = new WebAction();
		info = SFDCUtil.fetchSFDCinfo();
		query_url = info.getEndpoint() + "/services/data/v29.0/query/?q=";
	}

	public static void main(String args[]) {
		try {
			System.out.println(countOfRecordsForASObject("Account"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static int countOfRecordsForASObject(String sObject) throws Exception {
		// TODO Auto-generated method stub
		String query = "Select COUNT(id) from " + sObject;
		Report.logInfo("SFDC Query : " + query);
		query = URLEncoder.encode(query, "UTF-8");
		query_url = query_url + query;
		Report.logInfo("SFDC URL : " + query_url);
		
		Header h = new Header();
		h.addHeader("Authorization", "Bearer " + info.getSessionId());
		HttpResponseObj resp = wa.doGet(query_url, h.getAllHeaders());
		String json = resp.getContent();
		Report.logInfo("Count of Records of Query : " + json);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(json);
		JsonNode records = node.get("records");
		try {
			//return Integer.parseInt(node.get("expr0").toString());
			for(final JsonNode rec : records) {
				return Integer.parseInt(rec.get("expr0").toString());
			}
			return -1;
		}
		catch (NumberFormatException e) {
			Report.logInfo("Response from the SFDC Rest URI - " + json);
			return -1;
		}
	}
}
