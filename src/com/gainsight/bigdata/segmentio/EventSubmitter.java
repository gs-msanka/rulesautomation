package com.gainsight.bigdata.segmentio;

import static com.gainsight.bigdata.urls.ApiUrls.API_WEB_HOOK_SENDGRID;
import static us.monoid.web.Resty.content;

import java.util.List;
import java.util.concurrent.Callable;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.codehaus.jackson.map.ObjectMapper;
import us.monoid.web.Resty;

public class EventSubmitter implements Callable<Boolean> {
    private Resty resty;
    private final String url;
    private final HttpEntity httpEntity;
	private List<Header> headers;

	public EventSubmitter(List<Header> headers,String url,HttpEntity httpEntity){
		this.url=url;
		this.httpEntity=httpEntity;
		this.headers = headers;

	}
	@Override
	public Boolean call() throws Exception {
		Boolean isSuccess=false;
		try {
			ResponseObj responseObj = new WebAction().doPost(url, headers, httpEntity);
			if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
				NsResponseObj nsResponseObj = new ObjectMapper().readValue(responseObj.getContent(), NsResponseObj.class);
				isSuccess = nsResponseObj.isResult();
			}
		}
		catch (Exception e){
			Log.info("Error when sending event request " +e.getMessage());
			isSuccess=false;
		}
		return isSuccess;
	}
}
