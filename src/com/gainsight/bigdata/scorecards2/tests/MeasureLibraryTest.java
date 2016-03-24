package com.gainsight.bigdata.scorecards2.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.scorecards2.apis.ScorecardsApi;
import com.gainsight.bigdata.scorecards2.pojos.MeasureList;
import com.gainsight.bigdata.scorecards2.pojos.MeasureMappingPayload;
import com.gainsight.bigdata.scorecards2.pojos.SaveScorecardPayload;
import com.gainsight.bigdata.scorecards2.pojos.ScorecardsList;
import com.gainsight.bigdata.tenantManagement.pojos.TenantDetails;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.SqlUtil;
import com.gainsight.utils.annotations.TestInfo;
import com.sforce.soap.partner.sobject.SObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileReader;


/**
 * Created by nyarlagadda on 17/02/16.
 */


public class MeasureLibraryTest extends NSTestBase {

    ScorecardsApi scorecardsApi;
    TenantDetails tenantDetails;

    @BeforeClass
    public void setup() throws Exception {

        scorecardsApi = new ScorecardsApi(header);

        tenantDetails = tenantManager.getTenantDetail(sfdc.fetchSFDCinfo().getOrg(), null);
        tenantDetails = tenantManager.getTenantDetail(null, tenantDetails.getTenantId());
        Boolean details = tenantManager.enabledRedShiftWithDBDetails(tenantDetails);
        Assert.assertNotNull(details,"Enabling Redshift Failed.Check response log for more details");

        String host = env.getProperty("ns_redshift_host");
        String dbName = env.getProperty("ns_redshift_dbName");
        String username = env.getProperty("ns_redshift_userName");
        String password = env.getProperty("ns_redshift_password");

        SqlUtil sqlUtil = new SqlUtil(host, dbName, username, password);
        String newTenantId = tenantDetails.getTenantId().replaceAll("-", "");

        String scorecardTable = "scorecard_measure_" + newTenantId;
        Boolean existenceCheck = sqlUtil.isTableExists(scorecardTable);
        //To check if Scorecard Schema exists or creates new Schema
        if (!existenceCheck) {
            NsResponseObj nsResponseObj = scorecardsApi.initScorecardConfig("");
            Assert.assertNotNull(nsResponseObj, "Status code is not 200, so response object returned is null. Check response log for more details");
            Assert.assertTrue(nsResponseObj.isResult(),"Creating Scorecard Schema failed.Check response log for more info");
        }
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8327"})
    public void testEditMeasure() throws Exception {
        //To insert measures if measures are not already present
        testMeasureSave();
        MeasureList measureUpdate = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/MeasureSave.json")), MeasureList.class);
        NsResponseObj nsResponseObj = scorecardsApi.getAllMeasuresList();
        Assert.assertNotNull(nsResponseObj, "Status code is not 200,so response object returned is null.Check response log for more info");
        MeasureList[] measureList = mapper.convertValue(nsResponseObj.getData(), MeasureList[].class);
        String Id = measureList[0].getId();
        measureUpdate.setId(Id);
        measureUpdate.setName("FeedBack");
        measureUpdate.setDescription("Measure Updated Successfully");
        String payload = mapper.writeValueAsString(measureUpdate);
        NsResponseObj nsObj = scorecardsApi.updateMeasure(payload);
        Assert.assertNotNull(nsObj, "Status code is not 200,so response object returned is null.Check response log for more info");
        MeasureList actualValue = mapper.convertValue(nsObj.getData(), MeasureList.class);
        Assert.assertEquals(actualValue.getDescription(), measureUpdate.getDescription(), "Description did not match.Check response log for more info");
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8325"})
    public void testMeasureSave() throws Exception {

        MeasureList measureSave = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/MeasureSave.json")), MeasureList.class);
        String payload = mapper.writeValueAsString(measureSave);
        NsResponseObj result = scorecardsApi.saveMeasure(payload);
        Assert.assertNotNull(result, "Status code is not 200,so response object returned is null.Check response log for more info");
        MeasureList actualResult = mapper.convertValue(result.getData(), MeasureList.class);
        testAssertion(measureSave, actualResult);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8328"})
    public void testDeleteMeasure() throws Exception {
        //To insert measures if measures are not already present
        testMeasureSave();
        NsResponseObj deleteResObj = scorecardsApi.getAllMeasuresList();
        Assert.assertNotNull(deleteResObj, "Status code is not 200,so response object returned is null.Check response log for more info");
        MeasureList[] measureList = mapper.convertValue(deleteResObj.getData(), MeasureList[].class);
        for (MeasureList lst : measureList) {
            String measureId = lst.getId();
            NsResponseObj deleteMeasure = scorecardsApi.deleteMeasure(measureId);
            Assert.assertNotNull(deleteMeasure, "Status code is not 200,so response object returned is null.Check response log for more info");
            if (deleteMeasure.getErrorDesc() != null && (deleteMeasure.getErrorDesc().equals("Scorecard measure is in use."))) {
                Assert.assertEquals(deleteMeasure.getErrorDesc(), "Scorecard measure is in use.");
                Log.info("Measure cannot be deleted as," + deleteMeasure.getErrorDesc());
            } else {
                Assert.assertTrue(deleteMeasure.isResult(), "Delete Failed,response is not true.Check response log for more info");
            }
        }
    }

    @Test
    @TestInfo(testCaseIds = {"GS-230508"})
    public void testDeleteUsedMeasure() throws Exception {
        //To create measure
        testMeasureSave();
        String create_relationship = Application.basedir + "/apex_scripts/Relationships/GetRelationshipTypeId.apex";
        sfdc.runApexCode(getNameSpaceResolvedFileContents(create_relationship));
        SObject[] relId = sfdc.getRecords(resolveStrNameSpace("Select Id,Name From JBCXM__GSRelationshipType__c Where Name='Automated RelationshipType'"));
        String relationshipTypeId = relId[0].getId();
        SaveScorecardPayload scorecards = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/ScorecardSave.json")), SaveScorecardPayload.class);
        scorecards.setRelationshipTypeId(relationshipTypeId);
        String payload = mapper.writeValueAsString(scorecards);
        NsResponseObj nsResponseObj = scorecardsApi.saveScorecard(payload);
        Assert.assertNotNull(nsResponseObj, "Status code is not 200,so response object returned is null.Check response log for more info");
        ScorecardsList scorecardsList = mapper.convertValue(nsResponseObj.getData(), ScorecardsList.class);
        String scorecardId = scorecardsList.getId();
        NsResponseObj result = scorecardsApi.getAllMeasuresList();
        MeasureList[] measureList = mapper.convertValue(result.getData(), MeasureList[].class);
        String measureId = measureList[0].getId();
        MeasureMappingPayload[] measurePayload = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/MeasureMapping.json")), MeasureMappingPayload[].class);
        measurePayload[0].setScorecardId(scorecardId);
        measurePayload[0].getChildren().get(0).setMeasureId(measureId);
        measurePayload[0].getChildren().get(0).setScorecardId(scorecardId);
        String measureMappingPayload = mapper.writeValueAsString(measurePayload);
        NsResponseObj measureMapping = scorecardsApi.saveMeasureMapping(measureMappingPayload, scorecardId);
        Assert.assertNotNull(measureMapping, "Status code is not 200,so response object returned is null.Check response log for more info");

        NsResponseObj deleteMeasure = scorecardsApi.deleteMeasure(measureId);
        Assert.assertNotNull(deleteMeasure, "Status code is not 200,so response object returned is null.Check response log for more info");
        if (deleteMeasure.getErrorDesc() != null && (deleteMeasure.getErrorDesc().equals("Scorecard measure is in use."))) {
            Assert.assertEquals(deleteMeasure.getErrorDesc(), "Scorecard measure is in use.");
            Log.info("Measure cannot be deleted as," + deleteMeasure.getErrorDesc());
        } else {
            Assert.assertTrue(deleteMeasure.isResult(), "Delete Failed,response is not true.Check response log for more info");
        }
    }

    /**
     * Method which deletes objects from json
     *
     * @return
     * @paramjsonNode-JsonNode
     * @paramstringJson- list of objects to remove from node
     */
    public JsonNode removeObjectFromJsonNode(JsonNode jsonNode, String[] stringJson) {
        for (String str : stringJson) {
            ObjectNode obj = (ObjectNode) jsonNode;
            obj.remove(str);
        }
        return jsonNode;
    }

    /**
     * Method to perform assertions
     *
     * @paramactualResult- response jsondata
     * @paramexpectedResult- expected jsondata
     */
    public void testAssertion(MeasureList actualResult, MeasureList expectedResult) {
        Assert.assertEquals(actualResult.getName(), expectedResult.getName(), "Name did not match,response is not true.Check response log for more info");
        Assert.assertEquals(actualResult.getDescription(), expectedResult.getDescription(), "Description did not match,response is not true.Check response log for more info");
        Assert.assertEquals(actualResult.getInputType(), expectedResult.getInputType(), "InputType did not match,response is not true.Check response log for more info");
    }
}
