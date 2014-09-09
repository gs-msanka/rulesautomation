package com.gainsight.bigdata.pojo;

import java.lang.reflect.Field;

@Deprecated
public class SFDCInfo {

	String org;
	String userId;
	String sessionId;
	String endpoint;

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer summary = new StringBuffer();
		Field[] fList=this.getClass().getDeclaredFields();
		for (Field f: fList){
			try {
				summary.append(f.getName()+" : "+f.get(this)+" | ");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return summary.toString();
	}

}
