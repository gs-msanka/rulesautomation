package com.gainsight.bigdata.connectors.enums;

public enum SfdcIdentifier {
	AM_ACCOUNTID("", "", "ID"),
	AM_ACCOUNTNAME("", "", "Name"),
	AM_CONTACTID("Contact", "Id", "AccountId"),
	AM_CONTACTEMAIL("Contact", "Email", "AccountId"),
	AM_CUSTOMER_NAME("JBCXM__customerinfo__c", "JBCXM__CustomerName__c", "JBCXM__Account__c"),
	UM_CONTACTID("Contact", "Id", "Id"),
	UM_CONTACTEMAIL("Contact", "Email", "Id"),
	UM_CONTACT_EXTERNALID("Contact", "EID__c", "id");

	String lookupObject;
	String lookupKey;
	String key;

	private SfdcIdentifier(String lookupObject, String lookupKey, String key) {
		this.lookupObject = lookupObject;
		this.lookupKey = lookupKey;
		this.key = key;
	}

	public String getLookupObject() {
		return lookupObject;
	}

	public String getLookupKey() {
		return lookupKey;
	}

	public String getKey() {
		return key;
	}

}
