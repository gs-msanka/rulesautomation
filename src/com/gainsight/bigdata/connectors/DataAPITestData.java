package com.gainsight.bigdata.connectors;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;
import com.gainsight.bigdata.connectors.enums.ConnConstants.AggType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAPITestData {
	AccountDetails accountDetails;
	String collectionId = "6c26cc3e-b75a-4cae-86e0-402ec0830121";
	String timeZone = "UTC";
	String runType = "RUN_NOW";
	String accountType = "DATA_API";
	String displayName = "Automation" + System.currentTimeMillis();
	GlobalMapping globalMapping;
	ReportRequestGenerator dayAggColReport;
	ReportRequestGenerator flippedColReport;

	public DataAPITestData() {
		accountDetails = new AccountDetails();
		globalMapping = accountDetails.getGlobalMapping();
		dayAggColReport = new ReportRequestGenerator();
		flippedColReport = new ReportRequestGenerator();
	}

	public void setDefaultParams(AccountDetails accountDetails) {
		accountDetails.setDefaultScheduler();
		accountDetails.setDefaultUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
	}

	public String getDayAggColReportRequest(String collectionID) throws Exception {
		dayAggColReport.setCollectionID(collectionID);
		return dayAggColReport.requestInfoAsString();
	}

	public String getFlippedColReportRequest(String collectionID) throws Exception {
		flippedColReport.setCollectionID(collectionID);
		return flippedColReport.requestInfoAsString();
	}

	public AccountDetails getMapping_AD_DL() throws Exception {
		displayName = "DLA_AD_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, null, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccDate();
		dayAggColReport.setMeasures(1);
		return accountDetails;
	}

	public AccountDetails getMapping_AD_IDL() throws Exception {
		displayName = "DLA_AD_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, null, null, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccDate();
		dayAggColReport.setMeasures(1);
		return accountDetails;
	}

	public AccountDetails getMapping_AUD_DL() throws Exception {
		displayName = "DLA_AUD_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, null, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addCustomField(Field.SIO_BROWSER, Field.SIO_BROWSER);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserDate();
		dayAggColReport.setMeasures(2);
		dayAggColReport.setCustomFields(1);
		return accountDetails;
	}

	public AccountDetails getMapping_AUD_IDL() throws Exception {
		displayName = "DLA_AUD_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, Field.SIO_USERID, null, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addCustomField(Field.SIO_BROWSER, Field.SIO_BROWSER);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserDate();
		dayAggColReport.setMeasures(2);
		dayAggColReport.setCustomFields(1);
		return accountDetails;
	}

	public AccountDetails getMapping_AED_DL() throws Exception {
		displayName = "DLA_AED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, null, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addMeasure(Field.SIO_PAGEVIEWS, AggType.COUNT);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccEventDate();
		dayAggColReport.setMeasures(3);
		return accountDetails;
	}

	public AccountDetails getMapping_AED_IDL() throws Exception {
		displayName = "DLA_AED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, null, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, null, null);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addMeasure(Field.SIO_PAGEVIEWS, AggType.COUNT);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccEventDate();
		dayAggColReport.setMeasures(3);
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL() throws Exception {
		displayName = "DLA_AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addMeasure(Field.SIO_PAGEVIEWS, AggType.MIN);
		globalMapping.addMeasure(Field.SIO_UNIQUE_USERS, AggType.MAX);
		globalMapping.addCustomField(Field.SIO_BROWSER, Field.SIO_BROWSER);
		globalMapping.addCustomField(Field.SIO_LOGIN_DATE, Field.SIO_LOGIN_DATE);
		globalMapping.addCustomField(Field.SIO_LOGIN_TIMESTAMP, Field.SIO_LOGIN_TIMESTAMP);
		globalMapping.addCustomField(Field.SIO_ISACTIVE, Field.SIO_ISACTIVE);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		dayAggColReport.setMeasures(3);
		dayAggColReport.setCustomFields(4);
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL() throws Exception {
		displayName = "DLA_AUED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_ARR, AggType.SUM);
		globalMapping.addMeasure(Field.SIO_MRR, AggType.AVG);
		globalMapping.addMeasure(Field.SIO_PAGEVIEWS, AggType.MIN);
		globalMapping.addMeasure(Field.SIO_UNIQUE_USERS, AggType.MAX);
		globalMapping.addCustomField(Field.SIO_BROWSER, Field.SIO_BROWSER);
		globalMapping.addCustomField(Field.SIO_LOGIN_DATE, Field.SIO_LOGIN_DATE);
		globalMapping.addCustomField(Field.SIO_LOGIN_TIMESTAMP, Field.SIO_LOGIN_TIMESTAMP);
		globalMapping.addCustomField(Field.SIO_ISACTIVE, Field.SIO_ISACTIVE);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		dayAggColReport.setMeasures(3);
		dayAggColReport.setCustomFields(4);
		return accountDetails;
	}

}
