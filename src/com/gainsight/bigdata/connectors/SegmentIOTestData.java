package com.gainsight.bigdata.connectors;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.connectors.enums.ConnConstants;
import com.gainsight.bigdata.connectors.enums.Field;
import com.gainsight.bigdata.connectors.enums.SfdcIdentifier;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SegmentIOTestData {
	AccountDetails accountDetails;
	String collectionId = "6c26cc3e-b75a-4cae-86e0-402ec0830121";
	String timeZone = "Asia/Kolkata";
	String runType = "RUN_NOW";
	String accountType = "DATA_API";
	String displayName = "Automation" + System.currentTimeMillis();
	GlobalMapping globalMapping;
	ReportRequestGenerator dayAggColReport;
	ReportRequestGenerator flippedColReport;

	public SegmentIOTestData() {
		accountDetails = new AccountDetails();
		globalMapping = accountDetails.getGlobalMapping();
		dayAggColReport = new ReportRequestGenerator();
		flippedColReport = new ReportRequestGenerator();
	}

	public String getDayAggColReportRequest(String collectionID) throws Exception {
		dayAggColReport.setCollectionID(collectionID);
		return dayAggColReport.requestInfoAsString();
	}

	public String getFlippedColReportRequest(String collectionID) throws Exception {
		flippedColReport.setCollectionID(collectionID);
		return flippedColReport.requestInfoAsString();
	}
	
	public void setDefaultParams(AccountDetails accountDetails) {
		accountDetails.setDefaultScheduler();
		accountDetails.setDefaultUsageConfig();
		accountDetails.setProperties(collectionId, timeZone);
		accountDetails.setAccountType(accountType);
		accountDetails.setDisplayName(displayName);
	}

	public AccountDetails getMapping_AUED_DL() throws Exception {
		displayName = "SIO_AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL() {
		displayName = "SIO_AUED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL_SIOEmail() throws Exception {
		displayName = "SIO_AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SFDC_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL_SIOEmail() {
		displayName = "SIO_AUED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTNAME, Field.SIO_USERID, Field.SIO_EVENT,
				Field.SIO_END_DATE, SfdcIdentifier.AM_CUSTOMER_NAME, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SFDC_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL_SIOUserName() throws Exception {
		displayName = "SIO_AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SIO_USERID);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL_SIOUserName() {
		displayName = "SIO_AUED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USEREMAIL, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL_SIOUserNameAndEmail() throws Exception {
		displayName = "SIO_AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERID);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL_SIOUserNameAndEmail() {
		displayName = "SIO_AUED_IDL";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL_UserOpt() {
		displayName = "SIO_AUED_DL_UserOptional";
		globalMapping.setCommonIdentifiers(Field.SIO_USERID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_CONTACTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL_UserOpt() {
		displayName = "SIO_AUED_IDL_UserOptional";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTNAME, Field.SIO_USERID, Field.SIO_EVENT,
				Field.SIO_END_DATE, SfdcIdentifier.AM_CUSTOMER_NAME, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_DL_UserOpt_FlipMeasure() {
		displayName = "SIO_AUED_DL_UserOptional";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getMapping_AUED_IDL_UserOpt_FlipMeasure() {
		displayName = "SIO_AUED_IDL_UserOptional";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT,
				Field.SIO_END_DATE, SfdcIdentifier.AM_CUSTOMER_NAME, null);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SIO_USEREMAIL, Field.SIO_USERNAME);
		setDefaultParams(accountDetails);
		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();
		return accountDetails;
	}

	public AccountDetails getRequest_AUED_DL_Flip() throws Exception {
		displayName = "AUED_DL";
		globalMapping.setCommonIdentifiers(Field.SIO_ACCOUNTID, Field.SIO_USERID, Field.SIO_EVENT, Field.SIO_END_DATE,
				SfdcIdentifier.AM_ACCOUNTID, SfdcIdentifier.UM_CONTACTID);
		globalMapping.setSysDefIdetifiers(Field.SFDC_ACCOUNTNAME, Field.SFDC_USEREMAIL, Field.SFDC_USERNAME);
		globalMapping.addMeasure(Field.SIO_ARR, ConnConstants.AggType.AVG);
		globalMapping.addMeasure(Field.SIO_EVENT, ConnConstants.AggType.DIST_COUNT);
		globalMapping.addEventMeasureMapping(ConnConstants.Events.CHROME, Field.SIO_ARR, ConnConstants.AggType.AVG);
		globalMapping.addEventMeasureMapping(ConnConstants.Events.CHROME, Field.SIO_EVENT,
				ConnConstants.AggType.DIST_COUNT);
		setDefaultParams(accountDetails);
		System.out.println("Account Details: " + new ObjectMapper().writeValueAsString(accountDetails));

		/* Day Aggregated Collection - Report Request Creation */
		dayAggColReport.setCollectionName(displayName + " Day Agg");
		dayAggColReport.setAccUserEventDate();

		/* Flip Collection - Report Request Creation */
		flippedColReport.setCollectionName(displayName + " Flipped Measure");
		flippedColReport.setAccUserDate();
		flippedColReport.setFlippedMeasures(2);
		return accountDetails;
	}



}
