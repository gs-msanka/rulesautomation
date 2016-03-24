package com.gainsight.bigdata.scorecards2.tests;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.scorecards2.apis.MeasureApi;
import com.gainsight.bigdata.scorecards2.pojos.MeasureList;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;


/**
 * Created by nyarlagadda on 17/02/16.
 */


public class MeasureLibraryTest extends NSTestBase {
    MeasureApi measureApi;

    @BeforeClass
    public void setup() {
        measureApi = new MeasureApi(header);
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8327"})
    public void testEditMeasure() throws Exception {
        //To insert measures if measures are not already present
        testMeasureSave();

        MeasureList measureUpdate = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/MeasureSave.json")), MeasureList.class);
        NsResponseObj nsResponseObj = measureApi.getAllMeasuresList();
        if (nsResponseObj != null) {
            MeasureList[] measureList = mapper.convertValue(nsResponseObj.getData(), MeasureList[].class);
            String Id = measureList[0].getId();
            measureUpdate.setId(Id);
            measureUpdate.setName("Feed Back");
            measureUpdate.setDescription("Measure Updated Successfully");
            String payload = mapper.writeValueAsString(measureUpdate);
            NsResponseObj nsObj = measureApi.updateMeasure(payload);
            MeasureList actualValue = mapper.convertValue(nsObj.getData(), MeasureList.class);
            Assert.assertEquals(actualValue.getDescription(), measureUpdate.getDescription(), "Description did not match");
        }
        //To validate existing measures
        NsResponseObj listResponseObj = measureApi.getAllMeasuresList();
        if (nsResponseObj != null) {
            String responseJson = mapper.writeValueAsString(listResponseObj.getData());
            JsonNode rootNode = mapper.readTree(responseJson);
            String[] removeObjectsList = {"id", "deleted", "createdBy", "createdAt", "modifiedBy", "modifiedAt", "tenantId", "active", "defaultRollup"};
            for (JsonNode node : rootNode) {
                removeObjectFromJsonNode(node, removeObjectsList);
            }
            Log.info("Actual Value " + rootNode.toString());
            String expectedJson = FileUtils.readFileToString(new File(Application.basedir + "/testdata/newstack/Scorecards2/data/ExpectedMeasureList.json"));
            Log.info("Expected Value " + expectedJson);
            JsonFluentAssert.assertThatJson(rootNode.toString()).isEqualTo(expectedJson);
        }
    }

    @Test
    @TestInfo(testCaseIds = {"GS-8325"})
    public void testMeasureSave() throws Exception {
        //To delete existing Measures before insert
        NsResponseObj nsResponseObj = measureApi.getAllMeasuresList();
        MeasureList[] measureList = mapper.convertValue(nsResponseObj.getData(), MeasureList[].class);
        for (MeasureList lst : measureList) {
            String measureId = lst.getId();
            NsResponseObj result = measureApi.deleteMeasure(measureId);
            Assert.assertTrue(result.isResult(), "Deletion Failed");
        }
        MeasureList measureSave = mapper.readValue((new FileReader(Application.basedir + "/testdata/newstack/Scorecards2/data/MeasureSave.json")), MeasureList.class);
        String payload = mapper.writeValueAsString(measureSave);
        NsResponseObj result = measureApi.saveMeasure(payload);

        if (result != null) {
            MeasureList actualResult = mapper.convertValue(result.getData(), MeasureList.class);
            testAssertion(measureSave, actualResult);
        }
        measureSave.setName("Adoption");
        measureSave.setInputType("CALCULATED");
        String payload2 = mapper.writeValueAsString(measureSave);
        NsResponseObj result2 = measureApi.saveMeasure(payload2);

        if (result2 != null) {
            MeasureList actualResult2 = mapper.convertValue(result2.getData(), MeasureList.class);
            testAssertion(measureSave, actualResult2);
        }
        measureSave.setName("Usage Data");
        measureSave.setInputType("MANUAL");
        String payload3 = mapper.writeValueAsString(measureSave);
        NsResponseObj result3 = measureApi.saveMeasure(payload3);

        if (result3 != null) {
            MeasureList actualResult3 = mapper.convertValue(result3.getData(), MeasureList.class);
            testAssertion(measureSave, actualResult3);
        }
        measureSave.setName("Growth");
        measureSave.setInputType("MANUAL");
        String payload4 = mapper.writeValueAsString(measureSave);
        NsResponseObj result4 = measureApi.saveMeasure(payload4);
        if (result4 != null) {
            MeasureList actualResult4 = mapper.convertValue(result4.getData(), MeasureList.class);
            testAssertion(measureSave, actualResult4);
        }

    }

    @Test
    @TestInfo(testCaseIds = {"GS-8328"})
    public void testDeleteMeasure() throws Exception {
        //To insert measures if measures are not already present
        testMeasureSave();
        NsResponseObj nsResponseObj = measureApi.getAllMeasuresList();
        MeasureList[] measureList = mapper.convertValue(nsResponseObj.getData(), MeasureList[].class);
        for (MeasureList lst : measureList) {
            String measureId = lst.getId();
            NsResponseObj result = measureApi.deleteMeasure(measureId);
            Assert.assertTrue(result.isResult(), "Deletion Failed");
        }
    }

    /**
     * Method which deletes objects from json
     *
     * @param jsonNode   - JsonNode
     * @param stringJson - list of objects to remove from node
     * @return
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
     * @param actualResult   - response json data
     * @param expectedResult - expected json data
     */
    public void testAssertion(MeasureList actualResult, MeasureList expectedResult) {
        Assert.assertEquals(actualResult.getName(), expectedResult.getName(), "Name did not match");
        Assert.assertEquals(actualResult.getDescription(), expectedResult.getDescription(), "Description did not match");
        Assert.assertEquals(actualResult.getInputType(), expectedResult.getInputType(), "Input Type did not match");
    }
}