package com.gainsight.sfdc.customer.pojo;

import java.lang.reflect.Field;

public class CustomerSummary {
	private String ASV;
	private String MRR;
	private String OTR;
	private String users;
	private String ARPU;
	private String stage;
	private String OCD;
	private String RD;
	private String timeToRenew;
	private String lifetime;
    private String status;
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getTimeToRenew() {
		return timeToRenew;
	}

	public void setTimeToRenew(String timeToRenew) {
		this.timeToRenew = timeToRenew;
	}

	public String getLifetime() {
		return lifetime;
	}

	public void setLifetime(String lifetime) {
		this.lifetime = lifetime;
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
