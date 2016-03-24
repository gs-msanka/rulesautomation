package com.gainsight.bigdata.scorecards2.apis;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by nyarlagadda on 17/02/16.
 */
public class MeasureApi {
    private ObjectMapper mapper = new ObjectMapper();
    WebAction wa = new WebAction();
    Header header;

    public MeasureApi(Header header) {
        this.header = header;
    }

    /** Method to get list of Measures
     *
     * @return
     * @throws Exception
     */
    public NsResponseObj getAllMeasuresList() throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doGet(GET_MEASURE_LIST, header.getAllHeaders());
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while getting measures" + ex);
        }
        return nsResponseObj;
    }

    /** Method to Update a Measure
     *
     * @param payload - Request payload
     * @return
     * @throws Exception
     */
    public NsResponseObj updateMeasure(String payload) throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPut(MEASURE_UPDATE, payload, header.getAllHeaders());
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("PUT Request Failed for Update" + ex);
        }
        return nsResponseObj;
    }

    /** Method to save a Measure
     *
     * @param payload - request payload
     * @return
     * @throws Exception
     */
    public NsResponseObj saveMeasure(String payload) throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPost(MEASURE_SAVE, header.getAllHeaders(), payload);
        Log.info("Response Obj" + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("POST Request Failed for Save" + ex);
        }
        return nsResponseObj;
    }

    /** Method to delete Measures from Measure Library
     *
     * @param measureId - which holds measure Id
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteMeasure(String measureId) throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doDelete(MEASURE_DELETE + measureId, header.getAllHeaders());
        Log.info("Response Obj" + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("DELETE Request Failed" + ex);
        }
        return nsResponseObj;
    }
}
