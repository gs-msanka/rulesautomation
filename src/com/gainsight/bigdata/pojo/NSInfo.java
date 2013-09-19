package com.gainsight.bigdata.pojo;

import java.lang.reflect.Field;

public class NSInfo {

	String authToken;
	String tenantID;

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getTenantID() {
		return tenantID;
	}

	public void setTenantID(String tenantID) {
		this.tenantID = tenantID;
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
