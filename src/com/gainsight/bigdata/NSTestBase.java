package com.gainsight.bigdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.gainsight.bigdata.rulesengine.ResponseObject;
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
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class NSTestBase {

	protected SFDCInfo sfinfo;
	protected NSInfo nsinfo;
	protected WebAction wa;
	protected Header header;	
	protected static String basedir;
	protected String testDataBasePath;	
	protected ObjectMapper mapper = new ObjectMapper();
    public static SalesforceMetadataClient metadataClient;
    public static SalesforceConnector sfdc;
    public static final Application env = new Application();
    public static final Boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    public static MetaDataUtil metaUtil=new MetaDataUtil();

    public NSTestBase() {
		basedir = System.getenv("basedir");
		testDataBasePath = basedir + "/testdata/newstack";
	}
	
	@BeforeSuite
	public void init() {
		//Initializing Headers
		header = new Header();	
		wa = new WebAction();
		//Initializing SFDC Connection
		sfdc = new SalesforceConnector(PropertyReader.userName, PropertyReader.password + PropertyReader.stoken,
    			PropertyReader.partnerUrl, PropertyReader.sfdcApiVersion);
		sfdc.connect();
        metadataClient = SalesforceMetadataClient.createDefault(sfdc.getMetadataConnection());
		sfinfo = sfdc.fetchSFDCinfo();
		
		header.addHeader("Origin",sfinfo.getEndpoint());
		header.addHeader("Content-Type", "application/json");
		header.addHeader("appOrgId", sfinfo.getOrg());
		header.addHeader("appUserId", sfinfo.getUserId());
		header.addHeader("appSessionId", sfinfo.getSessionId());
		
		//header.addHeader("authToken", "3b135252-473a-43f1-a6b1-e78a4db53ddd");
		
		//sfdc.getAccessToken();
		System.out.println("SF INFO : " + sfinfo.toString());
		  
	}
		//createTenant()
	
	@Test
	public void sampleTest() {
		SObject s[] = sfdc.getRecords("select id, name from account limit 10");
		System.out.println("No of Records " +s.length);
		
	}	
	
	
	/**
	 * @param objName = the object from which we need the map
	 * @param fieldName = the field name that needs to be queried for - it will be the key in the hashmap
	 * @param shortCut = the shortCut for each object will be unique.in the test data we need to prepend the key with the shortcut
	 * @return
	 */
	public HashMap<String,String> getMapFromObject(String objName,String fieldName,String shortCut){
		    String Query         = "SELECT Id,"+fieldName+" from "+objName; 
			HashMap<String,String> objMap=new HashMap<String,String>();
		    SObject[] objRecords = sfdc.getRecords(resolveStrNameSpace(Query));
		    Log.info("Total Piclist Records : " +objRecords.length);
	        for(SObject sObject : objRecords) {
	            Log.info("ObjectName:"+objName+"..FieldName : "+sObject.getField(resolveStrNameSpace(fieldName)) + " - With Id : " +sObject.getId());
	            objMap.put(shortCut+"."+sObject.getField(fieldName).toString(), sObject.getId());
	        }
	        return objMap;
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
    public static String resolveStrNameSpace(String str) {
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
    
    public String getNameSpaceResolvedFileContents(String filePath) {
        return resolveStrNameSpace(FileUtil.getFileContents(filePath));
    }
	
  //key is of format : SFID:ObjectName:FieldName:FieldValue 
  //Handling only string type of fields for now
   /* public String getSFId(String key){
    	System.out.println("got key as:"+key);
    	String[] values=key.split(":");
    	System.out.println("Executing query:   select "+values[1]+" from "+values[2]+" where "+values[3]+"='"+values[4]+"'");
    	SObject[] records=sfdc.getRecords("select "+values[1]+" from "+values[2]+" where "+values[3]+"='"+values[4]+"'");
    	return records[0].getChild(values[1]).getValue().toString();
    }*/
    

	public  HashMap<String,String> getSFValues(HashMap<String, String> fAndV,
			String Key) {
		System.out.println("Key is : "+Key);
		String query="select ";
		String fromValues="";
		int i=0;
		Iterator iterator = fAndV.keySet().iterator();
		while(iterator.hasNext()){
			if(i==0) fromValues=fromValues+iterator.next();
			else fromValues=fromValues+","+iterator.next();
			i++;
		}
		//Key = ObjectName:WhereField:WhereValue
		String[] values=Key.split(":");
		query=query+fromValues+ " from "+values[0]+" where "+values[1]+" = '"+values[2]+"'";
		SObject[] records=sfdc.getRecords(query);
		System.out.println("QUERY"+query);
		Iterator newIterator=fAndV.keySet().iterator();
		while(newIterator.hasNext()){
		String key=(String) newIterator.next();
		fAndV.put(key, records[0].getChild(key).getValue().toString());
		}
        return fAndV;
	}
	
	public void updateNSURLInAppSettings(String NSURL) {
		System.out.println("setting ns url in app settings");
        sfdc.connect();
        sfdc.getRecordCount("select id from JBCXM__ApplicationSettings__c");
		sfdc.runApexCode(resolveStrNameSpace("JBCXM__ApplicationSettings__c appSet= [select id,JBCXM__NSURL__c from JBCXM__ApplicationSettings__c];"
                + "appSet.JBCXM__NSURL__c='" + NSURL + "';"
                + "update appSet;"));
        Log.info("NS URL Updated Successfully");
	}

	public static ResponseObject convertToObject(String result)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseObject response = objectMapper.readValue(result,
				ResponseObject.class);
		return response;
	}

}
