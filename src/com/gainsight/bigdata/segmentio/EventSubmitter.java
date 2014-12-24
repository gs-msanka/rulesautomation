package com.gainsight.bigdata.segmentio;

import static us.monoid.web.Resty.content;

import java.util.concurrent.Callable;

import com.gainsight.testdriver.Log;

import us.monoid.web.Resty;

public class EventSubmitter implements Callable<Boolean> {
    private final Resty resty;
    private final String url;
    private final String payload;
    public EventSubmitter(Resty resty,String url,String payload){
    	this.url=url;
    	this.payload=payload;
    	this.resty=resty;
    	
    }
	@Override
	public Boolean call() throws Exception {
		Boolean isSuccess=false;
		try{
			isSuccess=Boolean.parseBoolean((String)resty.json(url, content(payload)).get("status"));
		}
		catch (Exception e){
			Log.info("Error when sending event request " +e.getMessage());
			isSuccess=false;
		}
		return isSuccess;
	}
}
