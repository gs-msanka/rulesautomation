package com.gainsight.bigdata;

import java.util.HashMap;
import java.util.Map;

import com.gainsight.sfdc.SalesforceMetadataClient;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeSuite;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.OAuthSalesforceConnector;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;

public class NSTestBase {

	protected SFDCInfo sfinfo;
	protected NSInfo nsinfo;
	protected WebAction wa;
	protected Header header;
	
	protected String basedir;
	protected String testDataBasePath;
	protected String origin = "https://c.ap1.visual.force.com";
	protected ObjectMapper mapper = new ObjectMapper();
    public static SalesforceMetadataClient metadataClient;
	OAuthSalesforceConnector sfdc;
	
	public NSTestBase() {
		basedir = System.getenv("basedir");
		testDataBasePath = basedir + "/testdata/newstack";
	}
	
	@BeforeSuite
	public void init() {

		//Initializing Headers
		header = new Header();
		header.addHeader("Origin", origin);
		wa = new WebAction();
		//Initializing SFDC Connection
		sfdc = new OAuthSalesforceConnector(PropertyReader.clientId, PropertyReader.clientId, 
				PropertyReader.userName, PropertyReader.password + PropertyReader.stoken, 
				PropertyReader.sfdcApiVersion);
		sfdc.connect();
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
		sfinfo = sfdc.fetchSFDCinfo();
		//sfdc.getAccessToken();
		createTenant();
	}

	private void createTenant() {
		header.addHeader("Content-Type", "application/json");
		Map<String, Object> contentMap = new HashMap<>();
		Map<String, String> auth = new HashMap<>();
		auth.put("appOrgId", sfinfo.getOrg());
		auth.put("appUserId", sfinfo.getUserId());
		auth.put("appSessionId", sfinfo.getSessionId());
		contentMap.put("auth_content", auth);
		try {
			String authJson = mapper.writeValueAsString(contentMap);
			String provisionUrl = PropertyReader.nsAppUrl + "/v1.0/api/provision";
			
			ResponseObj responseObj = wa.doPost(provisionUrl, header.getAllHeaders(), new StringEntity(authJson));
			if(responseObj.getStatusCode() == 200) {
				JsonNode node = mapper.readTree(responseObj.getContent());
				if(!node.get("result").asBoolean()) throw new RuntimeException("Unable to Provision Tenant");
			}
			
			Log.info("Tenant Provisioned");
		}
		catch(Exception e) {
			Log.error("Tenant Creation Failed : " + e.getLocalizedMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	
}
