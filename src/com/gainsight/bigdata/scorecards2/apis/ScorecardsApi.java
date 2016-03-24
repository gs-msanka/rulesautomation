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
 * Created by nyarlagadda on 23/02/16.
 */
public class ScorecardsApi {

    ObjectMapper mapper = new ObjectMapper();
    WebAction wa = new WebAction();
    Header header;

    public ScorecardsApi(Header header) {
        this.header = header;
    }

    /** CleansUp Scorecard Schema
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj cleanUpScorecardSchema(String payload) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPost(CLEANUP_SCORECARD_DATA, header.getAllHeaders(), payload);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cleanup failed " + ex);
        }
        return nsResponseObj;
    }

    /** Creates Scorecard Schema
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj initScorecardConfig(String payload) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPost(POST_INIT, header.getAllHeaders(), payload);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cleanup failed " + ex);
        }
        return nsResponseObj;
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

    /** Get all Scorecards
     *
     * @return
     * @throws Exception
     */
    public NsResponseObj getScorecardsList() throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doGet(GET_SCORECARDS_LIST, header.getAllHeaders());
        Log.info("responseObj = " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed" + e);
        }
        return nsResponseObj;
    }


    /** To Save Scorecard Master
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj saveScorecard(String payload) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPost(POST_SCORECARDS_SAVE, header.getAllHeaders(), payload);
        Log.info("responseObj = " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request to save the Scorecard failed");
        }
        return nsResponseObj;
    }

    /** To Save Scorecard Measure Config
     *
     * @param payload
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj saveMeasureMapping(String payload, String scorecardId) throws Exception {

        NsResponseObj nsResponseObj = null;
        Log.info("URL = " + String.format(SCORECARD_MEASURE_MAP, scorecardId));
        ResponseObj responseObj = wa.doPost(String.format(SCORECARD_MEASURE_MAP,scorecardId), header.getAllHeaders(), payload);
        Log.info("responseObj = " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed = " + e);
        }
        return nsResponseObj;
    }

    /** To get Scorecard Measure Config
     *
     * @param measureMapId
     * @return
     * @throws Exception
     */
    public NsResponseObj getScorecardMeasureConfig(String measureMapId) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doGet(String.format(SCORECARD_MEASURE_MAP,measureMapId), header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed = " + e);
        }
        return nsResponseObj;
    }

    /** To Delete Scorecard Measure Config
     *
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteScorecardMaster(String scorecardId) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doDelete(DELETE_SCORECARDS+scorecardId, header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed = " + e);
        }
        return nsResponseObj;
    }

    /** To Delete Scorecard Measure Mapping
     *
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteScorecardMeasureConfig(String scorecardId) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doDelete(String.format(SCORECARD_MEASURE_MAP,scorecardId), header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed = " + e);
        }
        return nsResponseObj;
    }

    /** To Update Scorecards
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj updateScorecardMaster(String payload) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPut(PUT_SCORECARD_UPDATE, payload, header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Request Failed = " + e);
        }
        return nsResponseObj;
    }
}


