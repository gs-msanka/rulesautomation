package com.gainsight.sfdc.customer.pojo;

public class TimeLineItem {
	private String ASV;
	private String MRR;
	private String OTR;
	private String users;
	private String type;
    private String term;
    private String bookingDate;
	public String getASV() {
		return ASV;
	}
	public void setASV(String aSV) {
		ASV = aSV;
	}
	public String getMRR() {
		return MRR;
	}
	public void setMRR(String mRR) {
		MRR = mRR;
	}
	public String getOTR() {
		return OTR;
	}
	public void setOTR(String oTR) {
		OTR = oTR;
	}
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

}
