package com.gainsight.bigdata.connectors;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAPITestData {
	AccountDetails accountDetails;
	String collectionId = "6c26cc3e-b75a-4cae-86e0-402ec0830121";
	String timeZone = "UTC";
	String accountType = "DATA_API";
	String displayName = "Automation" + System.currentTimeMillis();

	public DataAPITestData() {
	}

	public AccountDetails getMappingWithAccNDate_DirectLookup() {
		accountDetails = new AccountDetails();
		GlobalMapping globalMapping = accountDetails.globalMapping;
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, null, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_MEA1, "AVG");
		accountDetails.addScheduler();
		accountDetails.setUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
		return accountDetails;
	}

	public AccountDetails getMappingWithAccUserNDate_DirectLookup() {
		accountDetails = new AccountDetails();
		GlobalMapping globalMapping = accountDetails.globalMapping;
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, null, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_MEA1, "AVG");
		accountDetails.addScheduler();
		accountDetails.setUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
		return accountDetails;
	}
	
	public AccountDetails getMappingWithAccUserEventDate_DirectLookup() {
		accountDetails = new AccountDetails();
		GlobalMapping globalMapping = accountDetails.globalMapping;
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_USERID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_MEA1, "AVG");
		accountDetails.addScheduler();
		accountDetails.setUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
		return accountDetails;
	}
	
	public AccountDetails getMappingWithAccEventDate_DirectLookup() {
		accountDetails = new AccountDetails();
		GlobalMapping globalMapping = accountDetails.globalMapping;
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, Field.SIO_EVENT, Field.SIO_TIMESTAMP,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_MEA1, "AVG");
		accountDetails.addScheduler();
		accountDetails.setUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
		return accountDetails;
	}
}
