package com.gainsight.bigdata.rulesengine;

import java.io.IOException;

import com.gainsight.pageobject.core.Report;
import com.sforce.soap.partner.sobject.SObject;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.utils.SOQLUtil;
import com.gainsight.webaction.WebAction;

public class GSUtil {
    protected static TestEnvironment env = new TestEnvironment();
    public static Boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
    public static SOQLUtil soql = new SOQLUtil();


    public static void sfdcLogin(Header header, WebAction wa)
            throws Exception {
        SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
        TestEnvironment env = new TestEnvironment();

        NSInfo nsinfo = NSUtil.fetchNewStackInfo(sfinfo, new Header());
        soql.login(env.getUserName(), env.getUserPassword(),
                env.getProperty("sfdc.stoken"));
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
            Report.logInfo(result);
            return result;
        } else {
            return str;
        }
    }

    public static SObject[] execute(String query) {
        return soql.getRecords(GSUtil.resolveStrNameSpace(query));
    }
}
