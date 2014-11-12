package com.gainsight.bigdata.connectors.enums;

public enum Field {
	/* Segment Properties */
	SIO_ACCOUNTID("Group groupId", "gsgroup_groupid"),
	SIO_USERID("Identify userId", "gsidentify_userid"),
	SIO_EVENT("Track event", "gstrack_event"),
	SIO_TIMESTAMP("Track timestamp", "gstrack_timestamp"),
	SIO_ACCOUNTNAME("Group traits.name", "gsgroup_traitsname"),
	SIO_USEREMAIL("Identify traits.email", "gsidentify_traitsemail"),
	SIO_USERNAME("Identify traits.name", "gsidentify_traitsname"),
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
