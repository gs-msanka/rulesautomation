package com.gainsight.bigdata.connectors.pojo;

public class AccountProperties {
	String accountDetailsID;
	String accountName;
	String sourceCollection;
	String dayAggCollection;
	String flippedColleciton;
	public String getAccountDetailsID() {
		return accountDetailsID;
	}
	public void setAccountDetailsID(String accountID) {
		this.accountDetailsID = accountID;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getSourceCollection() {
		return sourceCollection;
	}
	public void setSourceCollection(String sourceCollection) {
		this.sourceCollection = sourceCollection;
	}
	public String getDayAggCollection() {
		return dayAggCollection;
	}
	public void setDayAggCollection(String dayAggCollection) {
		this.dayAggCollection = dayAggCollection;
	}
	public String getFlippedColleciton() {
		return flippedColleciton;
	}
	public void setFlippedColleciton(String flippedColleciton) {
		this.flippedColleciton = flippedColleciton;
	}
	
}
