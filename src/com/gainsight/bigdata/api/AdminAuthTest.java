package com.gainsight.bigdata.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.util.AdminUrl;
import com.gainsight.bigdata.util.DynamicHeadersTestData;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;

public class AdminAuthTest extends NSTestBase {

	String host;
	String version;
	List<Header> invalidHeadersList;
	List<Header> validHeadersList;
	DynamicHeadersTestData headerTestData = new DynamicHeadersTestData();

	@Parameters("version")
	@BeforeClass
	public void setUp(@Optional("") String version) throws Exception {
		this.version = version;
		host = nsConfig.getNsURl();
		AdminUrl.loadAdminUrls();
		invalidHeadersList = headerTestData.getHeadersInvalid(header);
		validHeadersList = headerTestData.getHeadersValid(header);
		headerTestData.addContextTenantID();
	}

	@Test
	public void testGetURLAuthValid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> uris = AdminUrl.getApiList.iterator(); uris.hasNext();) {
			String url = host + version + ((String) uris.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("GET:" + url);
				ResponseObj result = wa.doGet(url, header.getAllHeaders());
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("POST:" + url);
				ResponseObj result = wa.doPost(url, header.getAllHeaders(), "{}");
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("PUT:" + url);
				ResponseObj result = wa.doPut(url, "{}", header.getAllHeaders());
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.deleteApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("DELETE:" + url);
				ResponseObj result = wa.doDelete(url, header.getAllHeaders());
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> uris = AdminUrl.getApiList.iterator(); uris.hasNext();) {
			String url = host + version + ((String) uris.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("GET:" + url + "\t" + header.toString());
				ResponseObj result = wa.doGet(url, header.getAllHeaders());
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("POST:" + url);
				ResponseObj result = wa.doPost(url, header.getAllHeaders(), "{}");
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("PUT:" + url);
				ResponseObj result = wa.doPut(url, "{}", header.getAllHeaders());
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
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.deleteApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			int i = 0;
			for (Iterator<Header> headers = invalidHeadersList.iterator(); headers.hasNext();) {
				Header header = (Header) headers.next();
				Log.info("DELETE:" + url);
				ResponseObj result = wa.doDelete(url, header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tDELETE:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	/*@Test
	public void testCollectionURLAuthInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> uris = AdminUrl.collectionApiList.iterator(); uris.hasNext();) {
			String url = host + version + ((String) uris.next());
			int i = 0;
			for (Iterator<Header> headers = validHeadersList.iterator(); headers.hasNext();) {
				Header header = ((Header) headers.next()).deepClone();
				header.removeHeader("contextTenantId");
				Log.info("Col:" + url);
				ResponseObj result = wa.doGet(url, header.getAllHeaders());
				if (result.getStatusCode() != 403 && result.getStatusCode() != 401) {
					failList.add("\tDELETE:" + url + "$$ResCode:" + result.getStatusCode() + "$$MissingParam:"
							+ headerTestData.invalidHeaderType[i++] + "$$Headers:" + header.toString() + "\n");
				}
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}*/

	@Test
	public void testPostReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.postApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Log.info("POST:" + url);
			ResponseObj result = wa.doPost(url, header.getAllHeaders(), "{a}");
			if (result.getStatusCode() != 400) {
				failList.add("POST:" + url + "$$Payload:\"{a}\"$$ResCode:" + result.getStatusCode() + "$$Headers:"
						+ header.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

	@Test
	public void testPutReqTypeCheckInvalid() throws Exception {
		List<String> failList = new ArrayList<>();
		for (Iterator<String> iterator = AdminUrl.putApiList.iterator(); iterator.hasNext();) {
			String url = host + version + ((String) iterator.next());
			Log.info("PUT:" + url);
			ResponseObj result = wa.doPut(url, "{a}", header.getAllHeaders());
			if (result.getStatusCode() != 400) {
				failList.add("PUT:" + url + "$$Payload:\"{a}\"$$ResCode:" + result.getStatusCode() + "$$Headers:"
						+ header.toString() + "\n");
			}
		}
		Assert.assertEquals(failList.size(), 0, failList.toString());
	}

}
