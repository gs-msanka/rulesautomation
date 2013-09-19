package com.gainsight.sfdc.survey.pojo;

public class SurveyData {
	
	private String code;
	private String title;
	private String anomymous_option;
	private String accountname;
	private String toption;
	private String imagename;
	private String url;
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode(){
		
		return code;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		
		return title;
	}
	
	public void setAOption(String anomymous_option) {
		this.anomymous_option = anomymous_option;
	}
	
	public String getAOption(){
		
		return anomymous_option;
	}
	
	public void setAccountName(String accountname) {
		this.accountname = accountname;
	}
	
	public String getAccountName(){
		
		return accountname;
	}

	public void setTUOption(String toption) {
		this.toption = toption;
	}
	
	public String getTUOption(){
		
		return toption;
	}

	
	public void setImageName(String imagename) {
		this.imagename = imagename;
	}
	
	public String getImageName(){
		
		return toption;
	}
	
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public String getURL(){
		
		return url;
	}
	
}

