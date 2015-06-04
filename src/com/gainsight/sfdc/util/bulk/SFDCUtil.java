package com.gainsight.sfdc.util.bulk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.SfdcConfig;
import com.gainsight.util.SfdcConfigLoader;
import com.sforce.soap.apex.ExecuteAnonymousResult;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SFDCUtil {

    private final String endPointURL = "https://login.salesforce.com/services/Soap/u/29.0";
    static PartnerConnection connection;
    SoapConnection soapConnection;
    //static Application env;
    static SfdcConfig sfdcConfig = SfdcConfigLoader.getConfig();

    public static void main(String[] args) {
    	SFDCUtil.fetchSFDCinfo();
    }
    
    /**
     * Fetching the Salesforce UserInfo along with session id.
     * @return
     */
    public static SFDCInfo fetchSFDCinfo() {
        Log.info("Fetching SalesForce Data");
        try {
            //env = new Application();
            
            ConnectorConfig config = new ConnectorConfig();
            config.setAuthEndpoint(sfdcConfig.getSfdcPartnerUrl());
            config.setUsername(sfdcConfig.getSfdcUsername());
            config.setPassword(sfdcConfig.getSfdcPassword() + sfdcConfig.getSfdcStoken());

            connection = Connector.newConnection(config);
            LoginResult loginResult = connection.login(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword() + sfdcConfig.getSfdcStoken());
            GetUserInfoResult userInfo = loginResult.getUserInfo();

            SFDCInfo info = new SFDCInfo();
            info.setOrg(userInfo.getOrganizationId());
            info.setUserId(userInfo.getUserId());
            info.setSessionId(config.getSessionId());
            info.setAuthEndPoint(config.getAuthEndpoint());
            info.setServiceEndPoint(config.getServiceEndpoint());
            info.setUserEmail(userInfo.getUserEmail());
            info.setUserName(userInfo.getUserName());
            info.setUserFullName(userInfo.getUserFullName());
            info.setUserCurrencySymbol(userInfo.getCurrencySymbol());
            info.setUserLocale(userInfo.getUserLocale());
            info.setUserTimeZone(userInfo.getUserTimeZone());
            String sept = config.getServiceEndpoint();
            sept = sept.substring(0, sept.indexOf(".com") + 4);
            info.setEndpoint(sept);

            System.out.println(info.toString());
            Log.info("SDCF Info:\n" + info.toString());
            return info;
        } catch (ConnectionException ce) {
            ce.printStackTrace();
            return null;
        }
    }

    public void runApexCodeFromFile(String fileName, boolean packageFlag) {
        String code = getFileContents(fileName);
        if (!packageFlag) {
            code = code.replace("JBCXM__", "");
        }
        runApex(code);
    }

    public void runApex(String apexCode) {
        try {
            if (login()) {
                System.out.println("Running Apex Code : " + apexCode);
                ExecuteAnonymousResult result = soapConnection
                        .executeAnonymous(apexCode);
                if (result.isCompiled()) {
                    if (result.isSuccess()) {
                        Log.info("Apex code excuted sucessfully");
                    } else {
                        throw new RuntimeException("Apex code execution failed :"
                                + result.getExceptionMessage());
                    }
                } else {
                    throw new RuntimeException("Apex code compilition failed :"
                            + result.getCompileProblem());
                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    public String getFileContents(String fileName) {
        String code = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            code = stringBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    private boolean login() {
        if (soapConnection != null) return true;
        else {
            //Application env = new Application();
            String username = sfdcConfig.getSfdcUsername();
            String password = sfdcConfig.getSfdcPassword();
            String securityToken = sfdcConfig.getSfdcStoken();
            return login(username, password, securityToken);
        }
    }
    public boolean login(String username, String password, String securityToken) {
        boolean success = false;
        if (soapConnection != null) success = true;
        else {
            try {
                ConnectorConfig config = new ConnectorConfig();
                config.setUsername(username);
                config.setPassword(password + securityToken);
                Log.info("AuthEndPoint: " + endPointURL);
                config.setAuthEndpoint(endPointURL);
                Connector.newConnection(config);
                ConnectorConfig soapConfig = new ConnectorConfig();
                soapConfig.setAuthEndpoint(config.getAuthEndpoint());
                soapConfig.setServiceEndpoint(config.getServiceEndpoint().replace(
                        "/u/", "/s/"));
                soapConfig.setSessionId(config.getSessionId());
                soapConnection = new SoapConnection(soapConfig);
                success = true;
            } catch (ConnectionException ce) {
                throw new RuntimeException("Error connecting to salesforce", ce);
            }
        }
        return success;
    }
}
