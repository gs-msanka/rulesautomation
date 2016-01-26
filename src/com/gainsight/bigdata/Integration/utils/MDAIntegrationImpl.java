package com.gainsight.bigdata.Integration.utils;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.administration.pages.AdminIntegrationPage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Giribabu on 14/07/15.
 */
public class MDAIntegrationImpl
{
	ObjectMapper mapper = new ObjectMapper();
	WebAction wa = new WebAction();
	Header header;

	public MDAIntegrationImpl(Header header) {
		this.header = header;

	}

    /**
     * Check if the tenant is authorised on MDA (i.e OAuth enabled.)
     * @return true if already MDA is authorised already.
     */
    public boolean isMDAAuthorized() {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doGet(MDA_SFDC_ACCOUNT_EXISTS, header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    result = true;
                } else {
                    Log.info("Error Code " +nsResponseObj.getErrorCode());
                    Log.info("Error Description "+nsResponseObj.getErrorDesc());
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get mda sfdc auth info.. " ,e);
            throw  new RuntimeException("Failed to get mda sfdc auth info.. ", e);
        }
        Log.error("MDA Authorized : " +result);
        return result;
    }

    /**
     * Revokes MDA Authorization and deactivates all the connectors.
     * @return true - on successful revoke of MDA.
     */
    public boolean revokeMDAAuthorization() {
        boolean result = false;
        NsResponseObj nsResponseObj = null;
        try {
            Log.info("Revoking MDA Authorization...");
            ResponseObj responseObj = wa.doDelete(MDA_AUTH_REVOKE, header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    Log.info("Revoked MDA Authorization..., Now Disabling MixPanel, Google Analytics & SegmentIO connectors.");
                    if(deactivateConnector(UsageConnectorType.SEGMENT_IO) && deactivateConnector(UsageConnectorType.MIXPANEL)
                            && deactivateConnector(UsageConnectorType.GOOGLE_ANALYTICS)) {
                        result = true;
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Failed to revoke MDA...",  e);
        }
        return result;
    }

    public NsResponseObj getNsReponseObj(ResponseObj responseObj) throws IOException {
        if (responseObj.getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Status code is not 200 Ok.");
    }

    public boolean makeConnectorDefault(UsageConnectorType connectorType) {
        try {
            ResponseObj responseObj = wa.doPut(String.format(CONNECTOR_DEFAULT_PUT, connectorType), "{}", header.getAllHeaders());
            NsResponseObj nsResponseObj = getNsReponseObj(responseObj);
            return nsResponseObj.isResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deactivateConnector(UsageConnectorType connectorType) {
        try {
            ResponseObj responseObj = wa.doPut(String.format(CONNECTOR_DEACTIVATE_PUT, connectorType), "{}", header.getAllHeaders());
            NsResponseObj nsResponseObj = getNsReponseObj(responseObj);
            return nsResponseObj.isResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if MDA is authorised already, if not authorises MDA by opening up the browser.
     */
    public void authorizeMDA() {
        if(isMDAAuthorized()) {
            Log.info("MDA is authorised already...");
            return;
        }
        Log.info("Authorizing MDA Environment...");
        final Application env = new Application();
        env.start();
        URI uri = null;
        try {
            env.launchBrower();
            BasePage basePage = new BasePage();
            Log.info("Initializing Base Page : " + basePage);
            basePage.login().selectGainsightApplication().clickOnAdminTab();
            uri = new URI(Application.getDriver().getCurrentUrl());
            String hostName = "https://" + uri.getHost();
            Application.getDriver().get(hostName + "/apex/Integration");
            AdminIntegrationPage integrationPage = new AdminIntegrationPage();
            integrationPage = integrationPage.clickOnAuthorizeMDASection().authorizeMDA();
        } catch (URISyntaxException e) {
            Log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            env.stop();
        }

        Log.info("MDA Authorization done successfully.");
    }






}
