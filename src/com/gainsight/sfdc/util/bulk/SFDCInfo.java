package com.gainsight.sfdc.util.bulk;

import java.lang.reflect.Field;

public class SFDCInfo {

	String org;
	String userId;
	String sessionId;
	String endpoint;
	String userName;
	String userFullName;
	String userEmail;
	String authEndPoint;
	String serviceEndPoint;
	String userCurrencySymbol;
    String userLocale;
    String userTimeZone;

    public String getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(String userLocale) {
        this.userLocale = userLocale;
    }

    public String getUserTimeZone() {
        return userTimeZone;
    }

    public void setUserTimeZone(String userTimeZone) {
        this.userTimeZone = userTimeZone;
    }

    public String getUserCurrencySymbol() {
		return userCurrencySymbol;
	}

	public void setUserCurrencySymbol(String userCurrencySymbol) {
		this.userCurrencySymbol = userCurrencySymbol;
	}
	
	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getAuthEndPoint() {
		return authEndPoint;
	}

	public void setAuthEndPoint(String authEndPoint) {
		this.authEndPoint = authEndPoint;
	}

	public String getServiceEndPoint() {
		return serviceEndPoint;
	}

	public void setServiceEndPoint(String serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}

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
