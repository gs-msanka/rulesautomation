package com.gainsight.bigdata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.HttpResponseObj;
import com.gainsight.bigdata.util.ReaderUtil;
import com.gainsight.pageobject.core.Report;


public class WebAction {

	public HttpResponseObj doPost(String uri, String rawBody, List<Header> headers) throws Exception {
		
		HttpPost post = new HttpPost(uri);
		if(headers.size() > 0) {
			for(Header h : headers) {
				post.addHeader(h.getName(), h.getValue());
			}
		}
			
		StringEntity entity = null;
		try {
			entity = new StringEntity(rawBody);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		post.setEntity(entity);
		return httpResponse(post);
	}
	
	public HttpResponseObj httpResponse(HttpUriRequest request) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		//For Burp Purpose
		/*HttpHost target = new HttpHost("127.0.0.1", 8080, "http");
		HttpResponse response = client.execute(target, request);*/
		HttpResponse response = client.execute(request);

		// Reading Response
		HttpResponseObj obj = new HttpResponseObj();
		StringBuffer buf = new StringBuffer();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null) {
			InputStream instream = resEntity.getContent();
			try {
				// do something useful
				InputStreamReader reader = new InputStreamReader(instream);
				ReaderUtil.readContent(reader, buf);
				Report.logInfo("Status Code: " + response.getStatusLine().getStatusCode());
				obj.setStatusCode(response.getStatusLine().getStatusCode());
				obj.setStatusLine(response.getStatusLine().toString());
				obj.setContent(buf.toString());
				obj.setContentType(resEntity.getContentType().getValue());
				obj.setContentLength(resEntity.getContentLength());
				obj.setHeaders(response.getAllHeaders());
				Report.logInfo("Response Obj: " + obj.toString());
			} finally {
				instream.close();
			}
		}

		EntityUtils.consume(resEntity);
		
		return obj;
	}
	
	public HttpResponseObj doGet(String uri, List<Header> headers) throws Exception {
		HttpGet get = new HttpGet(uri);
		if(headers.size() > 0) {
			for(Header h : headers) {
				get.addHeader(h.getName(), h.getValue());
			}
		}
		return httpResponse(get);
	}
}
