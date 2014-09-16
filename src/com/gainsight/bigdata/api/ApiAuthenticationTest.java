package com.gainsight.bigdata.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;

public class ApiAuthenticationTest extends TestBase {

	String host;

	String[] headerList = { "appOrgId", "appSessionId", "appUserId", "Origin" };
	String authToken = "authToken";
	String version;

	@Parameters("version")
	@BeforeClass
	public void setUp(@Optional("") String version) throws Exception {
		this.version = version;
		init();
		host = PropertyReader.nsAppUrl;
		h.removeHeader(authToken);
		ApiUrl.getAllApiUrlsWithReqType();
	}

	
	@Test
	public void testGetURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.getApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (int j = 0; j < headerList.length; j++) {
				Header headers = (Header) h.deepClone();
				headers.removeHeader(headerList[j]);
				Report.logInfo("GET:" + url);
				HttpResponseObj result = wa.doGet(url, headers.getAllHeaders());
				if (result.getStatusCode() != 403) {
					failList.add("GET:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
							+ headers.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPostURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (int j = 0; j < headerList.length; j++) {
				Header headers = (Header) h.deepClone();
				headers.removeHeader(headerList[j]);
				Report.logInfo("POST:" + url);
				HttpResponseObj result = wa.doPost(url, "{}", headers.getAllHeaders());
				if (result.getStatusCode() != 403) {
					failList.add("POST:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
							+ headers.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (int j = 0; j < headerList.length; j++) {
				Header headers = (Header) h.deepClone();
				headers.removeHeader(headerList[j]);
				Report.logInfo("PUT:" + url);
				HttpResponseObj result = wa.doPut(url, "{}", headers.getAllHeaders());
				if (result.getStatusCode() != 403) {
					failList.add("PUT:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
							+ headers.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testDeleteURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.deleteApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (int j = 0; j < headerList.length; j++) {
				Header headers = (Header) h.deepClone();
				headers.removeHeader(headerList[j]);
				Report.logInfo("DELETE:" + url);
				HttpResponseObj result = wa.doDelete(url, headers.getAllHeaders());
				if (result.getStatusCode() != 403) {
					failList.add("DELETE:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
							+ headers.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPostReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Report.logInfo("POST:" + url);
			HttpResponseObj result = wa.doPost(url, "{a}", h.getAllHeaders());
			if (result.getStatusCode() != 400) {
				failList.add("POST:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
						+ h.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = ApiUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Report.logInfo("PUT:" + url);
			HttpResponseObj result = wa.doPut(url, "{a}", h.getAllHeaders());
			if (result.getStatusCode() != 400) {
				failList.add("PUT:" + url + " ResCode:" + result.getStatusCode() + "\tHeaders:"
						+ h.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}
}
