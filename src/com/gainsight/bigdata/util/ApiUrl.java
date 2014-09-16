package com.gainsight.bigdata.util;

import java.util.ArrayList;
import java.util.List;

public final class ApiUrl extends NSUrl {
	
	// ------------------- BASE URLS -------------------
	public static final String API = "/api";
	public static final String BASE_REPORTS = API + "/reports";
	public static final String BASE_ACCOUNTS = API + "/accounts";
	public static final String BASE_TENANTS = API + "/tenants";
	public static final String BASE_MIXPANEL = API + "/mixpanel";
	public static final String BASE_TOKENS = API + "/tokens";

	// ------------------- REPORT URLs -------------------
	public static final String REPORT_GET_ALL = BASE_REPORTS + "/all";
	// @URL_PARAM1: ReportID
	public static final String REPORT_GET = BASE_REPORTS + URL_PARAM1;
	public static final String REPORT_RUN = BASE_REPORTS + "/run/preparation";
	public static final String REPORT_SAVE = BASE_REPORTS;
	// @URL_PARAM1: ReportID
	public static final String REPORT_DELETE = BASE_REPORTS + URL_PARAM1;
	public static final String REPORT_EXPORT_XSL = BASE_REPORTS + "/run/export";
	// ------------------- END -------------------

	// ------------------- COLLECTION URLs -------------------
	public static final String COLLECTIONS_GET_ALL = API + NS_COL_GET_ALL;
	public static final String COLLECTIONS_GET = API + NS_COL_GET;
	public static final String COLLECTIONS_GET_DIMENSION = API + NS_COL_GET_DIMENSION;
	// ------------------- END -------------------

	// ------------------- ACCOUNT URLs -------------------
	public static final String ACC_GET_ALL = BASE_ACCOUNTS + "/supported";
	public static final String ACC_CHECK_GS_ENABLED = BASE_ACCOUNTS + "/SFDC/exists";
	public static final String ACC_DISABLE_GS_CNCTR = BASE_ACCOUNTS + "/sfdc?accountType=SFDC";
	// @URL_PARAM1: Connector TYPE
	public static final String ACC_GET_OF_CNCTR = BASE_ACCOUNTS + "/integrations" + URL_PARAM1;
	public static final String ACC_GET_DETAILS = BASE_ACCOUNTS + URL_PARAM1;
	public static final String ACC_GET_DISTINCT_EVENTS = BASE_ACCOUNTS + URL_PARAM1 + "/events" + URL_PARAM2
			+ "/distinct";
	// @URL_PARAM1: Connector TYPE, URL_PARAM2: true/false
	public static final String ACC_ONOFF_CONNCTOR = BASE_ACCOUNTS + "/accountType" + URL_PARAM1 + "/default"
			+ URL_PARAM2;
	public static final String ACC_CREATE_MIXPANEL_PROJ = BASE_ACCOUNTS + "/mixpanel/create";
	// @URL_PARAM1: AccountID
	public static final String ACC_DATA_FETCH_MIXPANEL = BASE_MIXPANEL + "/accounts" + URL_PARAM1 + "/fetch";
	// @URL_PARAM1: AccountID
	public static final String ACC_DELETE_MIXPANEL_PROJ = BASE_ACCOUNTS + "/accountId" + URL_PARAM1;
	// @URL_PARAM1: AccountID
	public static final String DELETE_SEGMENT_PROJ = BASE_TOKENS + "/integrations/SEGMENT_IO/projects" + URL_PARAM1;
	public static final String CREATE_SEGMENT_PROJ = BASE_TOKENS + "/v2";
	// ------------------- END -------------------

	// --------------- TENANT URLs-----------------
	public static final String TENANT_AUTO_PROVISIONING = BASE_TENANTS + "/provision";
	// ------------------- END -------------------

	public static List<String> getApiList = new ArrayList<>();
	public static List<String> postApiList = new ArrayList<>();
	public static List<String> putApiList = new ArrayList<>();
	public static List<String> deleteApiList = new ArrayList<>();

	public static void getAllApiUrlsWithReqType() {

		// Add Report API URLs
		getApiList.add(REPORT_GET_ALL);
		getApiList.add(REPORT_GET);
		postApiList.add(REPORT_RUN);
		putApiList.add(REPORT_SAVE);
		deleteApiList.add(REPORT_DELETE);
		// postApiList.add(REPORT_EXPORT_XSL);

		// Add Collection API URLs */
		getApiList.add(COLLECTIONS_GET_ALL);
		getApiList.add(COLLECTIONS_GET);
		postApiList.add(COLLECTIONS_GET_DIMENSION);

		// Add Account API URLs
		getApiList.add(ACC_GET_ALL);
		getApiList.add(ACC_CHECK_GS_ENABLED);
		deleteApiList.add(ACC_DISABLE_GS_CNCTR);
		getApiList.add(ACC_GET_OF_CNCTR);
		getApiList.add(ACC_GET_DETAILS);
		getApiList.add(ACC_GET_DISTINCT_EVENTS);
		putApiList.add(ACC_ONOFF_CONNCTOR);
		postApiList.add(ACC_CREATE_MIXPANEL_PROJ);
		postApiList.add(ACC_DATA_FETCH_MIXPANEL);
		deleteApiList.add(ACC_DELETE_MIXPANEL_PROJ);
		deleteApiList.add(DELETE_SEGMENT_PROJ);
		postApiList.add(CREATE_SEGMENT_PROJ);
	}
}
