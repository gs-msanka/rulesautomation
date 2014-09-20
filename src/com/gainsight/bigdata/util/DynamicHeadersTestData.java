package com.gainsight.bigdata.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.gainsight.pojo.Header;

public class DynamicHeadersTestData {

	String appOrgId = "appOrgId";
	String appUserId = "appUserId";
	String appSessionId = "appSessionId";
	String authToken = "authToken";
	String Origin = "Origin";
	String contextTenantId = "contextTenantId";
	String reqTypeAuthToken = "authToken";
	String reqTypeSession = "session";
	String reqTypeAdmin = "admin";

	List<Header> invalidHeadersList = new ArrayList<>();
	List<Header> validHeadersList = new ArrayList<>();
	public String[] invalidHeaderType = new String[] { appOrgId, appSessionId, appUserId, Origin, authToken, Origin };

	String[] headerNamesSessionType = { appOrgId, appSessionId, appUserId, Origin };
	String[] headerNamesAuthType = { authToken, Origin };

	public List<Header> getHeadersInvalid(Header h) {
		Header headersSessionType = h.deepClone();
		Header headersAuthTokenType = h.deepClone();
		headersSessionType = filterHeaders(headersSessionType, headerNamesSessionType);
		headersAuthTokenType = filterHeaders(headersAuthTokenType, headerNamesAuthType);
		prepareHeaderSetInvalid(headersSessionType, headerNamesSessionType);
		prepareHeaderSetInvalid(headersAuthTokenType, headerNamesAuthType);
		return invalidHeadersList;
	}

	public List<Header> getHeadersValid(Header h) {
		validHeadersList.add(filterHeaders(h, headerNamesSessionType));
		validHeadersList.add(filterHeaders(h, headerNamesAuthType));
		return validHeadersList;
	}

	public Header filterHeaders(Header header, String[] nameList) {
		Header newHeader = new Header();
		List<Header> headerList = header.getAllHeaders();
		for (Iterator<Header> iterator = headerList.iterator(); iterator.hasNext();) {
			Header element = (Header) iterator.next();
			if (Arrays.asList(nameList).contains(element.getName())) {
				newHeader.addHeader(element.getName(), element.getValue());
			}
		}
		return newHeader;
	}

	public void prepareHeaderSetInvalid(Header header, String[] nameArray) {
		for (int i = 0; i < nameArray.length; i++) {
			Header element = (Header) header.deepClone();
			element.removeHeader(headerNamesSessionType[i]);
			invalidHeadersList.add(element);
			invalidHeaderType[i] = headerNamesSessionType[i];
		}

	}

	public void addContextTenantID() {
		for (Iterator<Header> iterator = invalidHeadersList.iterator(); iterator.hasNext();) {
			Header header = (Header) iterator.next();
			header.addHeader(contextTenantId, "dummyTenantID");
		}
		for (Iterator<Header> iterator = validHeadersList.iterator(); iterator.hasNext();) {
			Header header = (Header) iterator.next();
			header.addHeader(contextTenantId, "dummyTenantID");
		}
	}

}
