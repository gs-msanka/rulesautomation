package com.gainsight.bigdata.scorecards2.apis;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.scorecards2.pojos.Measure;
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
        if (header != null){
            this.header = header;
        }
        else{
            throw new RuntimeException("NullPointerException ");
        }
    }

    /**
     * CleansUp Scorecard Schema
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj cleanUpScorecardSchema(String payload) throws Exception {

        return doPostAndGetResponseObj(CLEANUP_SCORECARD_DATA, "Cleanup failed ", payload);
    }

    /**
     * Creates Scorecard Schema
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj initScorecardConfig(String payload) throws Exception {

        return doPostAndGetResponseObj(POST_INIT, "Init failed ", payload);
    }

    /**
     * Gets list of Measures
     *
     * @return
     * @throws Exception
     */
    public Measure[] getAllMeasuresList() throws Exception {
        NsResponseObj nsResponseObj = null;
        Measure[] measureList = null;
        ResponseObj responseObj = wa.doGet(GET_MEASURE_LIST, header.getAllHeaders());
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                measureList = mapper.convertValue(nsResponseObj.getData(), Measure[].class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while getting measures" + ex);
        }
        return measureList;
    }

    /**
     * Updates a Measure
     *
     * @param payload - Request payload
     * @return
     * @throws Exception
     */
    public NsResponseObj updateMeasure(String payload) throws Exception {

        return doPutAndGetResponseObj(MEASURE_UPDATE, "PUT Request Failed for Measure Update", payload);
    }

    /**
     * Saves a Measure
     *
     * @param payload - request payload
     * @return
     * @throws Exception
     */
    public NsResponseObj saveMeasure(String payload) throws Exception {

        return doPostAndGetResponseObj(MEASURE_SAVE, "POST Request Failed for Measure Save", payload);
    }

    /**
     * Deletes Measures from Measure Library
     *
     * @param measureId - which holds measure Id
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteMeasure(String measureId) throws Exception {

        return doDeleteAndGetResponseObj(MEASURE_DELETE, "Request to delete Measure failed", measureId);
    }

    /**
     * Gets all Scorecards
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


    /**
     * Saves a Scorecard
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj saveScorecard(String payload) throws Exception {

        return doPostAndGetResponseObj(POST_SCORECARDS_SAVE, "Request to save the Scorecard failed", payload);
    }

    /**
     * Saves Scorecard Measure Mapping
     *
     * @param payload
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj saveMeasureMapping(String payload, String scorecardId) throws Exception {

        NsResponseObj nsResponseObj = null;
        Log.info("URL = " + String.format(SCORECARD_MEASURE_MAP, scorecardId));
        ResponseObj responseObj = wa.doPost(String.format(SCORECARD_MEASURE_MAP, scorecardId), header.getAllHeaders(), payload);
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

    /**
     * Gets Scorecard Measure Mapping
     *
     * @param measureMapId
     * @return
     * @throws Exception
     */
    public NsResponseObj getScorecardMeasureConfig(String measureMapId) throws Exception {

        return doGetAndGetResponseObj(SCORECARD_MEASURE_MAP, "Request to get MeasureMapping failed", measureMapId);
    }

    /**
     * Deletes Scorecard
     *
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteScorecardMaster(String scorecardId) throws Exception {

        return doDeleteAndGetResponseObj(DELETE_SCORECARDS, "Request to delete Scorecard failed", scorecardId);
    }

    /**
     * Deletes Scorecard Measure Mapping
     *
     * @param scorecardId
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteScorecardMeasureConfig(String scorecardId) throws Exception {

        return doDeleteAndGetResponseObj(SCORECARD_MEASURE_MAP, "Request to delete MeasureMapping failed", scorecardId);
    }

    /**
     * Updates a Scorecard
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj updateScorecardMaster(String payload) throws Exception {

        return doPutAndGetResponseObj(PUT_SCORECARD_UPDATE, "Request to update Scorecard failed", payload);
    }

    /** Common Delete Method
     *
     */

    public NsResponseObj doDeleteAndGetResponseObj(String NS_URL, String errorMessage, String Id) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doDelete(NS_URL + Id, header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException(errorMessage + e);
        }
        return nsResponseObj;
    }

    /** Common Get Method
     *
     *
     * @param NS_URL
     * @param errorMessage
     * @param Id
     * @return
     * @throws Exception
     */
    public NsResponseObj doGetAndGetResponseObj(String NS_URL, String errorMessage, String Id) throws Exception {

        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doGet(String.format(NS_URL, Id), header.getAllHeaders());
        Log.info("ResponseObj " + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                Log.info("nsResponseObj = " + nsResponseObj);
            }
        } catch (Exception e) {
            throw new RuntimeException(errorMessage + e);
        }
        return nsResponseObj;
    }

    /** Common Post Method
     *
     *
     * @param NS_URL
     * @param errorMessage
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj doPostAndGetResponseObj(String NS_URL, String errorMessage, String payload) throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPost(NS_URL, header.getAllHeaders(), payload);
        Log.info("Response Obj" + responseObj);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException(errorMessage + ex);
        }
        return nsResponseObj;
    }

    /** Common Put Method
     *
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj doPutAndGetResponseObj(String NS_URL, String errorMessage, String payload) throws Exception {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = wa.doPut(NS_URL, payload, header.getAllHeaders());
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {

                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException(errorMessage + ex);
        }
        return nsResponseObj;
    }
}