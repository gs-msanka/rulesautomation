package com.gainsight.bigdata.util;

import com.gainsight.pojo.Header;

import java.util.ArrayList;
import java.util.List;

public class DynamicHeadersTestData {
	
	String appOrgId = "appOrgId";
	String appUserId = "appUserId";
	String appSessionId = "appSessionId";
	String authToken = "authToken";
	String Origin = "Origin";
	
	List<Header> invalidHeadersList = new ArrayList<Header>();
	List<Header> validHeadersList = new ArrayList<Header>();
	
	public List<Header> getAllHeaderCombinationInvalid(Header h) {
		String[] headerNamesSessionType = { appOrgId, appSessionId, appUserId, Origin };
		String[] headerNamesAuthType = { authToken, Origin };
		Header headersSessionType = h.deepClone();
		Header headersAuthTokenType = h.deepClone();
		// Remove AuthToken from Session Type Headers
		headersSessionType.removeHeader(authToken);
		// Remove appOrgId, appSessionId, appUserId from AuthToken Type Headers
		headersAuthTokenType.removeHeader(appOrgId);
		headersAuthTokenType.removeHeader(appSessionId);
		headersAuthTokenType.removeHeader(appUserId);

		for (int i = 0; i < headerNamesSessionType.length; i++) {
			Header header = (Header) headersSessionType.deepClone();
			header.removeHeader(headerNamesSessionType[i]);
			invalidHeadersList.add(header);
		}

		for (int i = 0; i < headerNamesAuthType.length; i++) {
			Header header = (Header) headersAuthTokenType.deepClone();
			header.removeHeader(headerNamesAuthType[i]);
			invalidHeadersList.add(header);
		}
		return invalidHeadersList;
	}

	public List<Header> getAllHeaderCombinationValid(Header h) {
		Header headersSessionType = h.deepClone();
		Header headersAuthTokenType = h.deepClone();
		// Remove AuthToken from Session Type Headers
		headersSessionType.removeHeader(authToken);
		// Remove appOrgId, appSessionId, appUserId from AuthToken Type Headers
		headersAuthTokenType.removeHeader(appOrgId);
		headersAuthTokenType.removeHeader(appSessionId);
		headersAuthTokenType.removeHeader(appUserId);

		validHeadersList.add(headersSessionType);
		validHeadersList.add(headersAuthTokenType);
		return invalidHeadersList;
	}


}
