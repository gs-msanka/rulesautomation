package com.gainsight.bigdata.Integration.utils;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.administration.pages.AdminIntegrationPage;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Giribabu on 14/07/15.
 */
public class MDAIntegrationImpl extends NSTestBase {

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
                    Log.info(nsResponseObj.getErrorCode());
                    Log.info(nsResponseObj.getErrorDesc());
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
                    if(deActivateGoogleAnalyticsConnector() && deActivateMixPanelConnector()
                            && deActivateSegmentIOConnector()) {
                        result = true;
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Failed to revoke MDA...",  e);
        }
        return result;
    }

    /**
     * Deactivates Google Analytics Connector.
     * @return true on successful de-activation of Google analytics connector..
     */
    public boolean deActivateGoogleAnalyticsConnector() {
        Log.info("Disabling Google Analytics Connector...");
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPut(GA_DEACTIVATE_PUT,  "{}", header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    Log.info("Disabled Google Analytics Connector.");
                    result = true;
                }
            } else {
                throw new RuntimeException("Failed to de-active google analytics");
            }
        } catch (Exception e) {
            Log.error("Failed ", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Deactivates SegmentIO Connector.
     * @return true on successful de-activation of SegmentIO connector.
     */
    public boolean deActivateSegmentIOConnector() {
        Log.info("Disabling SegmentIO Connector...");
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPut(SEGMENT_DEACTIVATE_PUT,  "{}", header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    Log.info("Disabled SegmentIO Connector.");
                    result = true;
                }
            } else {
                throw new RuntimeException("Failed to de-active SegmentIO");
            }
        } catch (Exception e) {
            Log.error("Failed ", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Deactivates MixPanel Connector.
     * @return true on successful de-activation of MixPanel Connector.
     */
    public boolean deActivateMixPanelConnector() {
        Log.info("Disabling MixPanel Connector...");
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPut(MIX_PANEL_DEACTIVATE_PUT,  "{}", header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    Log.info("Disabled MixPanel Connector.");
                    result = true;
                }
            } else {
                throw new RuntimeException("Failed to de-active MixPanel");
            }
        } catch (Exception e) {
            Log.error("Failed ", e);
            throw new RuntimeException(e);
        }
        return result;
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
