package com.gainsight.bigdata;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class NSTestBase {

	protected SFDCInfo sfinfo;
	protected NSInfo nsinfo;
	protected WebAction wa;
	protected Header header;
	
	protected static String basedir;
	protected String testDataBasePath;
	protected String origin = "https://c.ap1.visual.force.com";
	protected ObjectMapper mapper = new ObjectMapper();
    public static SalesforceMetadataClient metadataClient;
    public SalesforceConnector sfdc;
    
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
		sfdc = new SalesforceConnector(PropertyReader.userName, PropertyReader.password + PropertyReader.stoken,
    			PropertyReader.partnerUrl, PropertyReader.sfdcApiVersion);
		sfdc.connect();
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
		sfinfo = sfdc.fetchSFDCinfo();
		//sfdc.getAccessToken();
		System.out.println("SF INFO : " + sfinfo.toString());
	//	createTenant();
	}
	
	@Test
	public void sampleTest() {
		SObject s[] = sfdc.getRecords("select id, name from account limit 10");
		System.out.println("No of Records " +s.length);
		
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
	
	
	/**
     * Method to remove the name space from the string "JBCXM__".
     *
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public String resolveStrNameSpace(String str) {
        return FileUtil.resolveNameSpace(str, PropertyReader.managedPackage ? PropertyReader.NAMESPACE : null);
        
    }
    public static String resolveNameSpace(String str, String nameSpace) {
        String result = "";
        if (str != null && nameSpace!=null && !nameSpace.equalsIgnoreCase("JBCXM")) {
            result = str.replaceAll("JBCXM__", nameSpace+"__").replaceAll("JBCXM\\.", nameSpace+".");
            Log.info(result);
            return result;
        } else {
            return str;
        }
    }
	
	
}
