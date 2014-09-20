package com.gainsight.bigdata.util;

import java.util.ArrayList;
import java.util.List;

public class AdminUrl extends NSUrl {
	public static final String ADMIN = "/admin";
	public static final String BASE_COLLECTIONS = ADMIN + "/collections";
	public static final String BASE_TENANTS = ADMIN + "/tenants";
	public static final String BASE_TOKENS = ADMIN + "/tokens";
	public static final String BASE_ACT = ADMIN + "/activities";

	// ------------------- COLLECTION URLs -------------------
	public static final String COLLECTIONS_GET_ALL = BASE_COLLECTIONS + "/list";
	public static final String COLLECTIONS_CREATE = BASE_COLLECTIONS;
	// @URL_PARAM1: CollectionID
	public static final String COLLECTIONS_GET_DETAILS = BASE_COLLECTIONS + URL_PARAM1;
	public static final String COLLECTIONS_UPDATE = BASE_COLLECTIONS;
	// @URL_PARAM1: CollectionID
	public static final String COLLECTIONS_DELETE = BASE_COLLECTIONS + URL_PARAM1;
	// ------------------- END -------------------

	// ------------------- TENANTS URLs -------------------
	public static final String TENANTS_GET_ALL = BASE_TENANTS;
	public static final String TENANTS_ADD = BASE_TENANTS;
	// @URL_PARAM1: TenantID
	public static final String TENANTS_GET_DETAILS = BASE_TENANTS + URL_PARAM1;
	// @URL_PARAM1: TenantID
	public static final String TENANTS_UPDATE = BASE_TENANTS + URL_PARAM1;
	// @URL_PARAM1: TenantID
	public static final String TENANTS_DELETE = BASE_TENANTS + URL_PARAM1;
	// ------------------- END -------------------

	// ------------------- OTHER URLs -------------------
	public static final String TOKENS_GET_ALL_ACCESSKEYS = BASE_TOKENS;
	public static final String ACT_GET_ACTIVITY_LOG = BASE_ACT;
	// @URL_PARAM1: TenantID
	public static final String ACT_GET_TNENANT_ACTIVITY_LOG = BASE_ACT + "?tenantId=" + URL_PARAM1;
	// ------------------- END -------------------

	public static List<String> getApiList = new ArrayList<String>();
	public static List<String> postApiList = new ArrayList<String>();
	public static List<String> putApiList = new ArrayList<String>();
	public static List<String> deleteApiList = new ArrayList<String>();
	public static List<String> collectionApiList = new ArrayList<String>();

	public static void loadAdminUrls() {
		// Add Collection API URLs
		// postApiList.add(COLLECTIONS_GET_ALL);
		postApiList.add(COLLECTIONS_CREATE);
		getApiList.add(COLLECTIONS_GET_DETAILS);
		putApiList.add(COLLECTIONS_UPDATE);
		deleteApiList.add(COLLECTIONS_DELETE);

		// Add Tenant API URLs
		getApiList.add(TENANTS_GET_ALL);
		postApiList.add(TENANTS_ADD);
		getApiList.add(TENANTS_GET_DETAILS);
		putApiList.add(TENANTS_UPDATE);
		deleteApiList.add(TENANTS_DELETE);

		collectionApiList.add(COLLECTIONS_CREATE);
		collectionApiList.add(COLLECTIONS_GET_DETAILS);
		collectionApiList.add(COLLECTIONS_UPDATE);
		collectionApiList.add(COLLECTIONS_DELETE);

		// Add Other API URLs
		getApiList.add(TOKENS_GET_ALL_ACCESSKEYS);
		getApiList.add(ACT_GET_ACTIVITY_LOG);
		getApiList.add(ACT_GET_TNENANT_ACTIVITY_LOG);

	}

}
