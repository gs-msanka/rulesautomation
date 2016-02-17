package com.gainsight.sfdc.reporting.tests;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.util.config.NsConfig;
import com.gainsight.utils.config.ConfigProviderFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bson.Document;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.annotate.*;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.reportApiImpl.ReportManager;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

import com.gainsight.utils.MongoUtil;

import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

/**
 *
 * Contains methods for connecting db to get the backend JSON and JSON
 * comparision.
 */

public class MDAConnectBackend extends BaseTest {

    static Date date = Calendar.getInstance().getTime();
	NsConfig nsConfig = ConfigProviderFactory.getConfig(NsConfig.class);
	NSTestBase nsTestBase = new NSTestBase();

	/**
	 * This method will connect to DB to get the JSON, and compares that to inpt
	 * JSON.
	 * 
	 * @param tenantId
	 * @param reportInfo
	 * @param mongoUtil
	 * @param reportMaster
	 * @throws IOException
	 */

	public void connectDbAndCompareJSON(String tenantId, ReportInfo reportInfo, MongoUtil mongoUtil,
			ReportMaster reportMaster) {
		try {
			Document document = new Document();
			document.append("TenantId", tenantId);
			document.append("ReportInfo.reportName", reportInfo.getReportName());
			document.append("ReportInfo.SchemaName", reportInfo.getSchemaName());
			List<String> includeFields = new ArrayList<>();
			List<String> excludeFields = new ArrayList<>();
			includeFields.add("ReportInfo");
			includeFields.add("newReport");
			includeFields.add("reportMasterRequired");
			includeFields.add("format");
			includeFields.add("displayType");
			excludeFields.add("_id");
			excludeFields.add("ReportInfo.ReportId");
			Document document1 = mongoUtil.getFirstRecord("reportmaster", document, includeFields, excludeFields);
			String serverData = new ObjectMapper().writeValueAsString(document1).toString();
			Log.info(new ObjectMapper().writeValueAsString(document1).toString());
			ObjectMapper mapper = new ObjectMapper();

			mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			DataLoadManager dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());
			CollectionInfo collectionInfo = dataLoadManager.getCollection(reportInfo.getSchemaName());

			ReportMaster r = null;
			try {
				r = ReportManager.getDBNamesPopulatedReportMaster(reportMaster, collectionInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.info("Printing json________________" + new ObjectMapper().writeValueAsString(r));

			compareGivenJSON(serverData, new ObjectMapper().writeValueAsString(r));
		} catch (NumberFormatException e) {
			throw e;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}

    public void compareGivenJSON(String expectedJSON, String actualJSON) {
        JsonFluentAssert.assertThatJson(expectedJSON).when(Option.IGNORING_EXTRA_FIELDS).isEqualTo(actualJSON);
    }

    public void verifyData(String tenantId, ReportInfo reportInfo, MongoUtil mongoUtil,
                           String expectedData) throws IOException {
        Document document = new Document();
        document.append("TenantId", tenantId);
        document.append("ReportInfo.reportName", reportInfo.getReportName());
        document.append("ReportInfo.SchemaName", reportInfo.getSchemaName());
        List<String> includeFields = new ArrayList<>();
        List<String> excludeFields = new ArrayList<>();
        includeFields.add("ReportInfo");
        includeFields.add("newReport");
        includeFields.add("reportMasterRequired");
        includeFields.add("format");
        includeFields.add("displayType");
        excludeFields.add("_id");
        excludeFields.add("ReportInfo.ReportId");
        Document document1 = mongoUtil.getFirstRecord("reportmaster", document, includeFields, excludeFields);
        String serverData = new ObjectMapper().writeValueAsString(document1);
        DataLoadManager dataLoadManager = new DataLoadManager(sfdcInfo, nsTestBase.getDataLoadAccessKey());
        CollectionInfo collectionInfo = dataLoadManager.getCollection(reportInfo.getSchemaName());
        HashMap<String, String> displayDBNamesMap = ReportManager.getDisplayAndDBNamesMap(collectionInfo);
        ReportManager reportManager = new ReportManager();
        String actualData = reportManager.runReport(serverData);

        expectedData = MDAConnectBackend.parserJsonData(expectedData);

        String newString = expectedData.substring(expectedData.indexOf("{", 5) + 1, expectedData.indexOf("}"));
        String[] arrayString = newString.split(":");
        int length = arrayString.length;
        for (String field : arrayString) {
            if (length > 1) {
                if (length == arrayString.length) {
                    expectedData = expectedData.replaceAll(field.substring(1, field.length() - 1) + "\"", displayDBNamesMap.get(field.substring(field.indexOf("\"") + 1, field.length() - 1)) + "\"");
                } else {
                    field = field.substring(field.indexOf(",") + 1);
                    expectedData = expectedData.replaceAll(field.substring(1, field.length() - 1) + "\"", displayDBNamesMap.get(field.substring(field.indexOf("\"") + 1, field.length() - 1)) + "\"");
                }
                length--;
            }
        }

        compareGivenJSON(expectedData, actualData);
    }

    public static String parserJsonData(String inputJson) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(inputJson);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray jsonArray1 = new JsonArray();
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
            Boolean check = true;
            if (jsonObject1.get("Date") != null && ((jsonObject1.get("Date").toString().contains("-")) || jsonObject1.toString().contains("\"Date\":\"0\""))) {
                String value = jsonObject1.get("Date").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date");
                jsonObject2.addProperty("Date", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);
                check = false;

            }
            if (jsonObject1.get("Date1") != null && ((jsonObject1.get("Date1").toString().contains("-")) || jsonObject1.toString().contains("\"Date1\":\"0\""))) {
                String value = jsonObject1.get("Date1").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date1");
                jsonObject2.addProperty("Date1", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);
                check = false;
            }
            if (jsonObject1.get("Date2") != null && ((jsonObject1.get("Date2").toString().contains("-")) || jsonObject1.toString().contains("\"Date2\":\"0\""))) {
                String value = jsonObject1.get("Date2").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date2");
                jsonObject2.addProperty("Date2", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);
                check = false;
            }
            if (jsonObject1.get("Date3") != null && ((jsonObject1.get("Date3").toString().contains("-")) || jsonObject1.toString().contains("\"Date3\":\"0\""))) {
                String value = jsonObject1.get("Date3").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date3");
                jsonObject2.addProperty("Date3", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);
                check = false;
            }
            if (check) {
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonArray1.add(jsonObject2);
            }
        }
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("data", jsonArray1);
        return jsonObject1.toString();

    }
}
