package com.gainsight.bigdata.copilot.apiImpl;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.copilot.smartlist.pojos.*;
import com.gainsight.bigdata.rulesengine.RulesUtil;
import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import com.sforce.soap.partner.sobject.SObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CopilotUtil extends NSTestBase {

	ResponseObj resp;
	String req = null;
	private static HashMap<String,String> userMap;
	
	public JsonNode createSmartList(HashMap<String, String> testData,String automatedRulePayLoad)
			throws Exception {

		String automatedRule = null;
		SmartList smList = new SmartList();
		smList.setName(testData.get("name"));
		smList.setDescription(testData.get("description"));
		smList.setType(testData.get("type"));
		smList.setStatus(testData.get("status"));
		Stats stats = mapper.readValue(testData.get("stats"), Stats.class);
		smList.setStats(stats);
			smList.setAutomatedRule(mapper.readValue(automatedRulePayLoad,
					AutomatedRule.class));
		Log.info("automatedRule json is " + mapper.writeValueAsString(smList));
		
		smList.setRefreshList(testData.get("refreshList"));
		smList.setDataSourceType(testData.get("dataSourceType"));

		req = mapper.writeValueAsString(smList);
		Log.info("List Request is :" + req);
		resp = wa.doPost(ApiUrls.API_CREATE_SMARTLIST, header.getAllHeaders(),
				req);
		JsonNode jsonNode = mapper.readTree(resp.getContent());

		return jsonNode;
	}
	
	public JsonNode createSmartList(HashMap<String, String> testData)
			throws Exception {

		String automatedRule = null;
		SmartList smList = new SmartList();
		smList.setName(testData.get("name"));
		smList.setDescription(testData.get("description"));
		smList.setType(testData.get("type"));
		smList.setStatus(testData.get("status"));
		Stats stats = mapper.readValue(testData.get("stats"), Stats.class);
		smList.setStats(stats);
		automatedRule = testData.get("automatedRule1")
				+ testData.get("automatedRule2");
		RulesUtil ru=new RulesUtil();
		automatedRule = getResolvedCriteria(automatedRule);
		smList.setAutomatedRule(mapper.readValue(automatedRule,
				AutomatedRule.class));
		Log.info("automatedRule json is " + mapper.writeValueAsString(smList));
		
		smList.setRefreshList(testData.get("refreshList"));
		smList.setDataSourceType(testData.get("dataSourceType"));

		req = mapper.writeValueAsString(smList);
		Log.info("List Request is :" + req);
		resp = wa.doPost(ApiUrls.API_CREATE_SMARTLIST, header.getAllHeaders(),
				req);
		JsonNode jsonNode = mapper.readTree(resp.getContent());

		return jsonNode;
	}

	//Temporary util to replace the USER_NAMES and USER_EMAILS in the trigger criteria.
	//Need to make it more robust to support not just usernames...but any type of customizations
	private String getResolvedCriteria(String string) {
		String Username = sfinfo.getUserFullName();
		String UserEmail = sfinfo.getUserEmail();
		string=string.replace("#USERNAME#", Username);
		string=string.replace("#USEREMAIL#", UserEmail);
		return string;
	}

	public JsonNode getListStats(String SmartListId) throws Exception {
		resp = wa.doGet(ApiUrls.API_CREATE_SMARTLIST + SmartListId,
				header.getAllHeaders());
		JsonNode jsonNode = mapper.readTree(resp.getContent());
		Log.info("Response is " +jsonNode);
		JsonNode Data = jsonNode.get("data");
		List<JsonNode> JsonNodeList = Data.findValues("stats");
		return JsonNodeList.get(0);
	}

}
