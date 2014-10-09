package com.gainsight.bigdata.util;

import java.util.ArrayList;
import java.util.List;

public class ApiUrl extends NSUrl {

	// ------------------- BASE URLS -------------------
	public static final String API = "/api";
	public static final String BASE_REPORTS = API + "/reports";
	public static final String BASE_ACCOUNTS = API + "/accounts";
	public static final String BASE_TENANTS = API + "/tenants";
	public static final String BASE_MIXPANEL = API + "/mixpanel";
	public static final String BASE_TOKENS = API + "/tokens";
	public static final String BASE_DS = API + "/datascience";
	public static final String BASE_COLLECTIONS = API + "/collections";

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
	public static final String COLLECTIONS_GET_ALL = BASE_COLLECTIONS + "/all";
	// @URL_PARAM1: CollectionID
	public static final String COLLECTIONS_GET = BASE_COLLECTIONS + URL_PARAM1;
	// @URL_PARAM1: Collection Name
	public static final String COLLECTIONS_GET_DIMENSION = BASE_COLLECTIONS + URL_PARAM1 + "/dimensions";
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

	// --------------- DATA SCIENCE URLs-----------------
	public static final String DS_SAVE_CONFIG = BASE_DS + "/configuration";
	public static final String DS_GET_CONFIG = BASE_DS + "/configuration" + URL_PARAM1;
	public static final String DS_UPDATE_CONFIG = BASE_DS + "/configuration" + URL_PARAM1;
	public static final String DS_DELTE_CONFIG = BASE_DS + "/configuration" + URL_PARAM1;
	public static final String DS_GET_ALL_CONFIG = BASE_DS + "/configuration";
	// @URL_PARAM1:dataModelID
	public static final String DS_SAVE_N_EXEC = BASE_DS + URL_PARAM1 + "/saveandexecute";
	// @URL_PARAM1:dataModelID
	public static final String DS_REMOVE_SCHEDULE = BASE_DS + "/configuration" + URL_PARAM1 + "/removeschedule";
	// @URL_PARAM1:dataModelID
	public static final String DS_UPDATE_OUTPUT = BASE_DS + "/configuration" + URL_PARAM1 + "/output";
	public static final String DS_REMOVE_AUTHTOKEN = BASE_DS + "/done";
	// ------------------- END -------------------

	public static List<String> getApiList = new ArrayList<String>();
	public static List<String> postApiList = new ArrayList<String>();
	public static List<String> putApiList = new ArrayList<String>();
	public static List<String> deleteApiList = new ArrayList<String>();

	public static void loadApiUrls() {

		// Add Report API URLs
		getApiList.add(REPORT_GET_ALL);
		getApiList.add(REPORT_GET);
		postApiList.add(REPORT_RUN);
		putApiList.add(REPORT_SAVE);
		deleteApiList.add(REPORT_DELETE);
		postApiList.add(REPORT_EXPORT_XSL);

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

		// Add Data Science API URLs
		postApiList.add(DS_SAVE_CONFIG);
		getApiList.add(DS_GET_CONFIG);
		putApiList.add(DS_UPDATE_CONFIG);
		deleteApiList.add(DS_DELTE_CONFIG);
		getApiList.add(DS_GET_ALL_CONFIG);
		postApiList.add(DS_SAVE_N_EXEC);
		deleteApiList.add(DS_REMOVE_SCHEDULE);
		putApiList.add(DS_UPDATE_OUTPUT);
		postApiList.add(DS_REMOVE_AUTHTOKEN);
	}
}
