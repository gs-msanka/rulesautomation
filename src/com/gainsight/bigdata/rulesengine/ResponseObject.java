package com.gainsight.bigdata.rulesengine;

import java.util.List;
import java.util.Map;

public class ResponseObject {
	String result;
	String requestId;
	Object data;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
