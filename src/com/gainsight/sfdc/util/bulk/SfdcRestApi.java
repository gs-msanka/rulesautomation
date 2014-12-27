package com.gainsight.sfdc.util.bulk;

import java.net.URLEncoder;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.http.WebAction;

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
	 * @param sObject
	 * @throws Exception
	 */
	public static int countOfRecordsForASObject(String sObject) throws Exception {
		// TODO Auto-generated method stub
		String query = "Select COUNT(id) from " + sObject;
		Log.info("SFDC Query : " + query);
		query = URLEncoder.encode(query, "UTF-8");
		query_url = query_url + query;
		Log.info("SFDC URL : " + query_url);
		
		Header h = new Header();
		h.addHeader("Authorization", "Bearer " + info.getSessionId());
		ResponseObj resp = wa.doGet(query_url, h.getAllHeaders());
		String json = resp.getContent();
		Log.info("Count of Records of Query : " + json);
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
			Log.info("Response from the SFDC Rest URI - " + json);
			return -1;
		}
	}
}
