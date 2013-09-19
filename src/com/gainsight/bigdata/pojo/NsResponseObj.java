package com.gainsight.bigdata.pojo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: sundar Date: 6/12/13 Time: 4:01 PM To
 * change this template use File | Settings | File Templates.
 */
public class NsResponseObj {

	private boolean result;

	private Map data;

	public NsResponseObj() {
	}
	
	public NsResponseObj(boolean result, Map data, String errorCode,
			String errorDesc) {
		this.result = result;
		this.data = data;
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	private String errorCode;
	private String errorDesc;

	public NsResponseObj(boolean result, String errorCode, String errorDesc) {
		this.result = result;
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Map getData() {
		return data;
	}

	public void setData(Map data) {
		this.data = data;
	}

}
