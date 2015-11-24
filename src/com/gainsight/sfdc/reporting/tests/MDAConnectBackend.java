package com.gainsight.sfdc.reporting.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.gainsight.util.ConfigLoader;
import com.gainsight.util.NsConfig;
import com.gainsight.utils.MongoUtil;

import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

/**
 *
 * Contains methods for connecting db to get the backend JSON and JSON
 * comparision.
 */

public class MDAConnectBackend extends BaseTest {

	NsConfig nsConfig = ConfigLoader.getNsConfig();
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

			ReportMaster r = ReportManager.getDBNamesPopulatedReportMaster(reportMaster, collectionInfo);

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
}
