package com.gainsight.bigdata;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;


import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.TenantInfo;
import com.gainsight.http.Header;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeSuite;

import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.SalesforceMetadataClient;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.MetaDataUtil;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class NSTestBase {

    protected TenantInfo tenantInfo;
	protected SFDCInfo sfinfo;
	protected WebAction wa;
	protected Header header;
	protected static String basedir;
	protected String testDataBasePath;	
	protected ObjectMapper mapper = new ObjectMapper();
    public static SalesforceMetadataClient metadataClient;
    public static SalesforceConnector sfdc;
    public static final Application env = new Application();
    public static final Boolean isPackage = PropertyReader.managedPackage;
    public static MetaDataUtil metaUtil=new MetaDataUtil();
    private String accessKey;

    String Api_Tokens = "/api/tokens";
    String Admin_DataLoad_Authenticate = "/admin/dataload/authenticate";


    public NSTestBase() {
		basedir = System.getenv("basedir");
		testDataBasePath = basedir + "/testdata/newstack";
	}
	
	@BeforeSuite
	public void init() throws Exception {
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
        header.addHeader("authToken", "initialcall");
        accessKey = getDataLoadAccessToken();
    }

    public String getDataLoadAccessToken() throws Exception {
        Log.info("Getting Access Key");
        String accessKey;
        String postURL = PropertyReader.nsAppUrl+"/"+PropertyReader.nsApiVersion+Api_Tokens;
        System.out.println(postURL);
        ResponseObj responseObj =  wa.doPost(postURL, header.getAllHeaders(), "{}");

        NsResponseObj rs;
        try {
            rs = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to get Access Token.");
        }
        if(rs.isResult()) {
            HashMap<String, String> data = (HashMap < String, String >)rs.getData();
            accessKey = data.get("accessKey");
            Log.info("AccessKey : " +accessKey);
        } else {
            Log.info("Error Code :" +rs.getErrorCode());
            Log.info("Error Desc : " +rs.getErrorDesc());
            throw new RuntimeException("Failed to get Access Token.");
        }
        return accessKey;
    }

    public void updateTenantAuthToken() throws Exception {
        String postURL = PropertyReader.nsAppUrl+"/"+PropertyReader.nsApiVersion+Admin_DataLoad_Authenticate;
        Log.info("Authorise URL " +postURL);
        header.addHeader("accessKey", accessKey);
        header.addHeader("loginName", sfinfo.getUserName());
        ResponseObj responseObj=  wa.doGet(postURL, header.getAllHeaders());

        org.apache.http.Header[] aList =  responseObj.getAllHeaders();
        for(org.apache.http.Header a : aList) {
            if(a.getName() !=null && a.getName().equals("Authtoken")) {
                Log.info("Updated AuthToken Details");
                tenantInfo.setAuthToken(a.getValue());
            }
        }
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
	
	/**
     * Method to remove the name space from the string "JBCXM__".
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public static String resolveStrNameSpace(String str) {
        return FileUtil.resolveNameSpace(str, PropertyReader.managedPackage ? PropertyReader.NAMESPACE : null);
        
    }
    
    public String getNameSpaceResolvedFileContents(String filePath) {
        return resolveStrNameSpace(FileUtil.getFileContents(filePath));
    }

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
	
	public void updateNSURLInAppSettings(String nsURL) {
		Log.info("Setting NS URL in Application Settings");
        if(sfdc.getRecordCount("select id from JBCXM__ApplicationSettings__c") >0 )  {
            String apexCode = "List<JBCXM__ApplicationSettings__c> appSettings = [select Id, Name, JBCXM__NSURL__C, JBCXM__ISNSEnabled__c from JBCXM__Applicationsettings__c];\n" +
                                "if(appSettings.size() >0 ) {\n" +
                                "\tSystem.debug(Failed to Update Application Settings);\n" +
                                "} else {\n" +
                                "\tappSettings.get(0).JBCXM__NSURL__C = '"+nsURL+"';\n" +
                                "}\n" +
                                "update appSettings; ";
            sfdc.runApexCode(resolveStrNameSpace(apexCode));
            Log.info("NS URL Updated Successfully");
        } else {
            Log.error("Configure Gainsight Application to update NS URL");
            throw new RuntimeException("Configure Gainsight Application to update NS URL");
        }
	}

}
