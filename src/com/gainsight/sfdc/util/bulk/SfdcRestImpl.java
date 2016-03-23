package com.gainsight.sfdc.util.bulk;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;

/**
 * Created by Giribabu on 05/11/15.
 */
public class SfdcRestImpl {

    WebAction wa;
    Header header = new Header();

    public SfdcRestImpl(String sessionId) {
        wa = new WebAction();
        header.addHeader("Content-Type", "application/json; charset=UTF-8");
        header.addHeader("Accept", "application/json");
        header.addHeader("Authorization", "Bearer "+sessionId);
    }

    /**
     * Do a get request to SFDC rest api & return the reponse object.
     * @param uri - URL to fetch/request for.
     * @return ResponseObj
     * @throws Exception
     */
    public ResponseObj getFromSalesforce(String uri) throws Exception {
        Log.info("URL " +uri);
        if(uri ==null || uri.isEmpty()) {
            throw new RuntimeException("URL can't be empty or null.");
        }
        ResponseObj responseObj = wa.doGet(uri, header.getAllHeaders());
        Log.info("Response Obj : " +responseObj.getContent());
        return responseObj;
    }

    /**
     * Method to post data using salesForce Rest Api
     *
     * @param uri     - endPoint
     * @param payload - entity
     * @return
     * @throws Exception
     */
    public ResponseObj insertIntoSalesforce(String uri, String payload) throws Exception {
        ResponseObj responseObj = null;
        responseObj = wa.doPost(uri, header.getAllHeaders(), payload);
        Log.info("Response Obj : " + responseObj.getContent());
        return responseObj;
    }
}
