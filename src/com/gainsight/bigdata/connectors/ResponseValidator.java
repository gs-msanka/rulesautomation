package com.gainsight.bigdata.connectors;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;

import com.gainsight.bigdata.connectors.enums.ConnConstants;
import com.gainsight.bigdata.connectors.pojo.UsageTracker;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.webaction.WebAction;

public class ResponseValidator {
	WebAction wa = new WebAction();
	Header h = null;

	public void jsonOutputCompare(String actual, String expected)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree1 = mapper.readTree(actual);
		JsonNode tree2 = mapper.readTree(expected);

		if (tree1.equals(tree2)) {
			Report.logInfo("Report output Matched");
		} else {
			System.out.println("Report output Did Not Match. Expected JSON:\n"
					+ expected + "\n Actual JSON:" + actual);

			Assert.fail("Report output Did Not Match. Expected JSON:\n" +
					expected + "\n Actual JSON:" + actual);

		}
	}

	public void validateReport(String reportRunUrl, Header header, String reportRequest, String expectedResponse)
			throws Exception {
		int counter = 1;
		this.h = header;
		HttpResponseObj result = wa.doPost(reportRunUrl, reportRequest, h.getAllHeaders());
		while (result.getStatusCode() == 503) {
			if (counter++ > 3) {
				Report.logInfo("Max trails for runnning report exceeded.");
				break;
			}
			result = wa.doPost(reportRunUrl, reportRequest, h.getAllHeaders());
		}
		if (result.getStatusCode() != 200) {
			Report.logInfo("Request:" + reportRequest + "\n Response:" + result.getContent());
			Assert.fail("Something gone wrong while running report");
		}
		jsonOutputCompare(result.getContent(), expectedResponse);
	}

	public void validateUsageTrackerData(String url, Header h, String accountID, Map<String, String> testData)
			throws Exception {

		// Validate ALL_EVENTS
		String allEventsActual = getUsageTrackerData(url, accountID, ConnConstants.TrackerData.ALL_EVENTS);
		String allEventsExpected = testData.get(ConnConstants.TrackerData.ALL_EVENTS.getDataType());
		jsonOutputCompare(allEventsActual, allEventsExpected);

		// Validate ALL_USERS
		String allUsersActual = getUsageTrackerData(url, accountID, ConnConstants.TrackerData.ALL_USERS);
		String allUsersExpected = testData.get(ConnConstants.TrackerData.ALL_USERS.getDataType());
		jsonOutputCompare(allUsersActual, allUsersExpected);

		// Validate EVENTS_BY_ACCOUNT
		String eventsByAccountActual = getUsageTrackerData(url, accountID, ConnConstants.TrackerData.EVENTS_BY_ACCOUNT);
		String eventsByAccountExpected = testData.get(ConnConstants.TrackerData.EVENTS_BY_ACCOUNT.getDataType());
		jsonOutputCompare(eventsByAccountActual, eventsByAccountExpected);

		// Validate ACCOUNT_USAGE
		String usageByAccountActual = getUsageTrackerData(url, accountID, ConnConstants.TrackerData.ACCOUNT_USAGE);
		String usageByAccountExpected = testData.get(ConnConstants.TrackerData.ACCOUNT_USAGE.getDataType());
		jsonOutputCompare(usageByAccountActual, usageByAccountExpected);
	}

	public String getUsageTrackerData(String usageTrakcerUrl, String accountID, ConnConstants.TrackerData tracker)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		UsageTracker ut = new UsageTracker(tracker, accountID);
		HttpResponseObj result = wa.doPost(usageTrakcerUrl, mapper.writeValueAsString(ut), h.getAllHeaders());
		JsonNode resContent = mapper.readTree(result.getContent());
		return resContent.get("data").toString();
	}

}
