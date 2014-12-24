package com.gainsight.bigdata.rulesengine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.testdriver.Log;
import com.gainsight.testdriver.TestEnvironment;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class GSUtil {
    protected static TestEnvironment env = new TestEnvironment();
    public static Boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    public static SFDCUtil sfdc = new SFDCUtil();

    public static void sfdcLogin(Header header, WebAction wa)
            throws Exception {
        SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
        TestEnvironment env = new TestEnvironment();

        NSInfo nsinfo = NSUtil.fetchNewStackInfo(sfinfo, new Header());
        header.addHeader("appOrgId", sfinfo.getOrg());
        header.addHeader("appSessionId", sfinfo.getSessionId());
        header.addHeader("appUserId", sfinfo.getUserId());
        header.addHeader("Content-Type", "application/json");
        System.out.println("endpoint:" + sfinfo.getEndpoint());
        // "https://jbcxm.na10.visual.force.com"
        String SFInstance = sfinfo.getEndpoint().split("https://")[1]
                .split("\\.")[0];
        String OriginHeader = "";
        Boolean isPackaged = Boolean.valueOf(env
                .getProperty("sfdc.managedPackage"));
        if (isPackaged)
            OriginHeader = "https://jbcxm." + SFInstance + ".visual.force.com";
        else
            OriginHeader = "https://" + SFInstance + ".visual.force.com";

        System.out.println("OriginHeader value=" + OriginHeader);
        header.addHeader("Origin", OriginHeader);

    }

    public static ResponseObject convertToObject(String result)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseObject response = objectMapper.readValue(result,
                ResponseObject.class);
        return response;
    }

    /**
     * Method to remove the name space from the string "JBCXM__".
     *
     * @param str -The string where name space should be removed.
     * @return String - with name space removed.
     */
    public static String resolveStrNameSpace(String str) {
        String result = "";
        if (str != null && !isPackage) {
            result = str.replaceAll("JBCXM__", "").replaceAll("JBCXM\\.", "");
            Log.info(result);
            return result;
        } else {
            return str;
        }
    }

    //We should not use this method any more - sunand so changing the return signature to null
    public static SObject[] execute(String query) {
        //return soql.getRecords(GSUtil.resolveStrNameSpace(query));
    	return null;
    }

    public static void runApexCode(String fileName) {
        sfdc.runApexCodeFromFile(fileName, isPackage);
    }

    public static void runApexCodeByReplacingTemplateId(String fileName, String templateId) {
        String code = sfdc.getFileContents(fileName);
        code = code.replace("$templateId", templateId);
        if (!isPackage) {
            code = code.replace("JBCXM__", "");
        }
        sfdc.runApex(code);
    }

    public static void waitForCompletion(String ruleId, WebAction webAction, Header header) throws Exception {
        boolean flag = true;
        int maxWaitingTime = 300000;
        long startTime = System.currentTimeMillis();
        long executionTime = 0;
        while (flag && executionTime < maxWaitingTime) {
            Thread.sleep(10000);
            ResponseObj result = webAction.doGet(PropertyReader.nsAppUrl + "/api/async/process/?ruleId=" + ruleId + "", header.getAllHeaders());
            ResponseObject res = GSUtil.convertToObject(result.getContent());
            List<Object> data = (List<Object>) res.getData();
            Map<String, Object> map = (Map<String, Object>) data.get(0);
            if (map.get("status") != null) {
                String status = (String) map.get("status");
                if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("failed_while_processing")) {
                    flag = false;
                    if(!status.equalsIgnoreCase("completed")){
                    	Log.info("ruledID - "+ruleId+ " "+map.get("executionMessages"));
                    }
                }
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
    }

}
