package com.gainsight.sfdc.reporting.apiImpl;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;
import org.apache.commons.httpclient.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by JayaPrakash on 03/02/16.
 */
public class ReportingApiImpl {

    ObjectMapper mapper = new ObjectMapper();
    WebAction wa = new WebAction();
    Header header;
    public ReportingApiImpl(Header header) {
        this.header = header;

    }

    public NsResponseObj exportDashboardGetNsReponse(String payload) throws Exception{

        ResponseObj responseObj = wa.doPost(ApiUrls.API_REPORT_DASHBOARD_TEST_EMAIL, header.getAllHeaders(), payload);
        Log.info("Response Obj : " +responseObj.toString());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST
                || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //internal server should be removed once the bug in the product is fixed.
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to export dashboard - Unknown Error");
        }
        return nsResponseObj;
    }

    public NsResponseObj exportSuccessSnapshotGetNsReponse(String payload,String successsnapshotID) throws Exception{
        Log.info("Success Snapshot ID Provided to export: "+successsnapshotID);
        ResponseObj responseObj = wa.doPost(String.format(ApiUrls.API_REPORT_EXPORT_PPT, successsnapshotID), header.getAllHeaders(), payload);
        Log.info("Response Obj : " +responseObj.toString());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST
                || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //internal server should be removed once the bug in the product is fixed.
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to export Success Snapshot - Unknown Error");
        }
        return nsResponseObj;

    }

}
