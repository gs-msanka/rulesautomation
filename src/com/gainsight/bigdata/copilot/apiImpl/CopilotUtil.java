package com.gainsight.bigdata.copilot.apiImpl;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.pojos.SmartList;
import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CopilotUtil extends NSTestBase {

	ResponseObj resp;
	String req = null;

	public JsonNode createSmartList(HashMap<String, String> testData) throws Exception {

		SmartList smList = new SmartList();

		smList.setName(testData.get("name"));
		smList.setDescription(testData.get("description"));
		smList.setType(testData.get("type"));
		smList.setStatus(testData.get("status"));

		SmartList.Stats stats = mapper.readValue(testData.get("stats"),SmartList.Stats.class);
		smList.setStats(stats);
		smList.setAutomatedRule(mapper.readValue(testData.get("automatedRule"),	SmartList.AutomatedRule.class));
		Log.info("automatedRule json is "+ smList.getAutomatedRule());
		smList.setRefreshList(testData.get("refreshList"));
		smList.setDataSourceType(testData.get("dataSourceType"));

		req = mapper.writeValueAsString(smList);
		Log.info("List Request is :" + req);
		resp = wa.doPost(ApiUrls.API_CREATE_SMARTLIST, header.getAllHeaders(),req);
		JsonNode jsonNode = mapper.readTree(resp.getContent());
		
		return jsonNode;
	}

	public JsonNode getListStats(String SmartListId) throws Exception {
		resp = wa.doGet(ApiUrls.API_CREATE_SMARTLIST + SmartListId,	header.getAllHeaders());
		JsonNode jsonNode = mapper.readTree(resp.getContent());
		JsonNode Data = jsonNode.get("data");
		List<JsonNode> JsonNodeList = Data.findValues("stats");
		return JsonNodeList.get(0);
	}

}
