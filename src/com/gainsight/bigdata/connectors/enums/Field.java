package com.gainsight.bigdata.connectors.enums;

public enum Field {
	/* Segment Properties */
	SIO_ACCOUNTID("AccountID", "aid"),
	SIO_ACCOUNTNAME("Account Name", "aname"),
	SIO_USERID("UserID", "uid"),
	SIO_USEREMAIL("User Email", "uemail"),
	SIO_USERNAME("User Name", "uname"),
	SIO_EVENT("Event", "event"),
	SIO_TITLE("Title", "title"),
	SIO_BROWSER("Browser", "browser"),
	SIO_LOGIN_DATE("Login Date", "ldate"),
	SIO_END_DATE("End Date", "edate"),
	SIO_LOGIN_TIMESTAMP("Login Timestamp", "ltimestamp"),
	SIO_END_TIMESTAMP("End timestamp", "etimestamp"),
	SIO_ARR("ARR", "arr"),
	SIO_MRR("MRR", "mrr"),
	SIO_PAGEVIEWS("PageViews", "pv"),
	SIO_UNIQUE_USERS("Unique Users", "uusers"),

	SIO_ISACTIVE("IsActive", "isactiveuser"),
	/* SFDC Properties */
	SFDC_ACCOUNTID("gssfdcaccountid", "gssfdcaccountid"),
	SFDC_ACCOUNTNAME("Account Name", "Name"),
	SFDC_USEREMAIL("Email", "Email"),
	SFDC_USERNAME("Full Name", "Name"),
	/* System Properties */
	SYS_ACCOUNTID("Account Id", "gsaccountid"),
	SYS_USERID("User Id", "gsuserid"),
	SYS_EVENT("Event", "gsevent"),
	SYS_TIMESTAMP("Timestamp", "gstimestamp"),
	SYS_DATE("Date", "gsdate"),
	SYS_EVENTCOUNT("Event Count", "gseventcount"),
	SYS_ACCOUNTNAME("Account Name", "gsaccountname"),
	SYS_USEREMAIL("User Email", "gsuseremail"),
	SYS_USERNAME("User Name", "gsusername"),
	SYS_CUSTOM1("CustomField1", "gscustom1"),
	SYS_CUSTOM2("CustomField2", "gscustom2"),
	SYS_CUSTOM3("CustomField3", "gscustom3"),
	SYS_CUSTOM4("CustomField4", "gscustom14");
	/* END */

	String displayName;
	String dbName;

	Field(String displaName, String dbName) {
		this.displayName = displaName;
		this.dbName = dbName;
	}

	public String getDBName() {
		return this.dbName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDBName(String dbName) {
		this.dbName = dbName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
