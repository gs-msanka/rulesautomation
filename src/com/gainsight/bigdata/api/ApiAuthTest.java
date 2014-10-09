package com.gainsight.bigdata.api;

import com.gainsight.bigdata.TestBase;
import com.gainsight.bigdata.util.ApiUrl;
import com.gainsight.bigdata.util.DynamicHeadersTestData;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiAuthTest extends TestBase {

	String host;
	String version;
	List<Header> invalidHeadersList;
	List<Header> validHeadersList;
	DynamicHeadersTestData headerTestData = new DynamicHeadersTestData();

	@Parameters("version")
	@BeforeClass
	public void setUp(@Optional("") String version) throws Exception {
		this.version = version;
		host = PropertyReader.nsAppUrl;
		init();
		ApiUrl.loadApiUrls();
		invalidHeadersList = headerTestData.getHeadersInvalid(h);
		validHeadersList = headerTestData.getHeadersValid(h);
	}

	@Test
	public void testGetURLAuthValid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> uris = ApiUrl.getApiList.iterator(); uris.hasNext();) {
			String url = host + version + ((String) uris.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("GET:" + url);
				HttpResponseObj result = wa.doGet(url, header.getAllHeaders());
				if (result.getStatusCode() == 403 || result.getStatusCode() == 401) {
					failList.add("\tGET:" + url + "$$ResCode:" + result.getStatusCode() + "$$Headers:"
							+ header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPostURLAuthValid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("POST:" + url);
				HttpResponseObj result = wa.doPost(url, "{}", header.getAllHeaders());
				if (result.getStatusCode() == 403 || result.getStatusCode() == 401) {
					failList.add("\tPOST:" + url + "$$ResCode:" + result.getStatusCode() + "$$Headers:"
							+ header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutURLAuthValid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("PUT:" + url);
				HttpResponseObj result = wa.doPut(url, "{}", header.getAllHeaders());
				if (result.getStatusCode() == 403 || result.getStatusCode() == 401) {
					failList.add("\tPUT:" + url + "$$ResCode:" + result.getStatusCode() + "$$Headers:"
							+ header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testDeleteURLAuthValid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.deleteApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("DELETE:" + url);
				HttpResponseObj result = wa.doDelete(url, header.getAllHeaders());
				if (result.getStatusCode() == 403 || result.getStatusCode() == 401) {
					failList.add("\tDELETE:" + url + "$$ResCode:" + result.getStatusCode() + "$$Headers:"
							+ header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testGetURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> uris = ApiUrl.getApiList.iterator(); uris.hasNext();) {
			String url = host + version + ((String) uris.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("GET:" + url + "\t" + header.toString());
				HttpResponseObj result = wa.doGet(url, header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tGET:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPostURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("POST:" + url + "\t" + header.toString());
				HttpResponseObj result = wa.doPost(url, "{}", header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tPOST:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("PUT:" + url + "\t" + header.toString());
				HttpResponseObj result = wa.doPut(url, "{}", header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tPUT:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testDeleteURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.deleteApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Report.logInfo("DELETE:" + url + "\t" + header.toString());
				HttpResponseObj result = wa.doDelete(url, header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tDELETE:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPostReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Report.logInfo("POST:" + url);
			HttpResponseObj result = wa.doPost(url, "{a}", h.getAllHeaders());
			if (result.getStatusCode() != 400) {
				failList.add("POST:" + url + "$$Payload:\"{a}\"$$ResCode:" + result.getStatusCode() + "$$Headers:"
						+ h.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<String>();
		for (Iterator<String> iterator = ApiUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Report.logInfo("PUT:" + url);
			HttpResponseObj result = wa.doPut(url, "{a}", h.getAllHeaders());
			if (result.getStatusCode() != 400) {
				failList.add("PUT:" + url + "$$Payload:\"{a}\"$$ResCode:" + result.getStatusCode() + "$$Headers:"
						+ h.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

}
