package com.gainsight.bigdata.connectors.enums;

public enum Field {
	/* Segment Properties */
	SIO_ACCOUNTID("AccountID", "aid"),
	SIO_USERID("UserID", "uid"),
	SIO_EVENT("Browser", "browser"),
	SIO_TIMESTAMP("End Date", "edate"),
	SIO_ACCOUNTNAME("Account Name", "aname"),
	SIO_USEREMAIL("User Email", "uemail"),
	SIO_USERNAME("User Name", "uname"),
	SIO_MEA1("ARR","arr"),
	/* SFDC Properties */
	SFDC_ACCOUNTID("gssfdcaccountid", "gssfdcaccountid"),
	SFDC_ACCOUNTNAME("Account Name", "Name"),
	SFDC_USEREMAIL("Email", "Email"),
	SFDC_USERNAME("Full Name", "Name"),
	/* System Properties */
	SYS_ACCOUNTID("Account Id", "gsaccountid"),
	SYS_USERID("UserID", "gsuserid"),
	SYS_EVENT("Event", "gsevent"),
	SYS_TIMESTAMP("Timestamp", "gstimestamp"),
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

}
