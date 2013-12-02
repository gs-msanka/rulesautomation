package com.gainsight.sfdc.customer.pojo;

public class SummaryLabels {
	private String ASV="ASV";
	private String MRR="MRR";
	private String OTR="One-time Rev";
	private String users="Users";
	private String ARPU="ARPU";
	private String stage="Stage";
	private String OCD="Orig. Contr. Date";
	private String RD="Renewal Date";
	private String lifeTime="Lifetime In Months";
	private String daysToRenew="Days ";

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

	public String getARPU() {
		return ARPU;
	}

	public void setARPU(String aRPU) {
		ARPU = aRPU;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getOCD() {
		return OCD;
	}

	public void setOCD(String oCD) {
		OCD = oCD;
	}

	public String getRD() {
		return RD;
	}

	public void setRD(String rD) {
		RD = rD;
	}

	public String getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(String lifeTime) {
		this.lifeTime = lifeTime;
	}

	public String getDaysToRenew() {
		return daysToRenew;
	}

	public void setDaysToRenew(String daysToRenew) {
		this.daysToRenew = daysToRenew;
	}
}
