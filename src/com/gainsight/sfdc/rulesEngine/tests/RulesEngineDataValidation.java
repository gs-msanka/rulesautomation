package com.gainsight.sfdc.rulesEngine.tests;

import java.util.HashMap;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.tests.BaseTest;
import com.sforce.soap.partner.sobject.SObject;

public class RulesEngineDataValidation extends BaseTest {

	public boolean checkAlertsCreated(HashMap<String, String> aC) throws InterruptedException {
		int alertsMatched;
		Boolean isRulesBatchCompleted=false;
		
		//only once the batch runs can we validate the alerts
		for (int l = 0; l < 200; l++) {
			String query = "SELECT Id, JobType, ApexClass.Name, Status FROM AsyncApexJob "
					+ "WHERE JobType ='BatchApex' and Status IN ('Queued', 'Processing', 'Preparing') "
					+ "and ApexClass.Name = 'StatefulBatchHandler'";
			int noOfRunningJobs = getQueryRecordCount(query);
			if (noOfRunningJobs == 0) {
				Report.logInfo("Rules Ran!!");
				isRulesBatchCompleted = true;
				break;
			} else {
				Report.logInfo("Waiting for Rules to Run");
				Thread.sleep(30000L);
			}
		}
		SObject[] alertSeverity = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name='"
						+ aC.get("alertSeverity") + "'"));
		SObject[] alertReason = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name='"
						+ aC.get("alertReason")+ "'"));
		SObject[] alertType = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name='"
						+ aC.get("alertType") + "'"));
		SObject[] alertStatus = soql
				.getRecords(resolveStrNameSpace("select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name='"
						+ aC.get("alertStatus") + "'"));
		alertsMatched = soql
				.getRecordCount(resolveStrNameSpace("SELECT  Id from JBCXM__Alert__c where JBCXM__Reason__c='"
						+ alertReason[0].getId()
						+ "' and JBCXM__Severity__c='"
						+ alertSeverity[0].getId()
						+ "' and JBCXM__Status__c='"
						+ alertStatus[0].getId()
						+ "' and Name='"
						+ aC.get("alertSubject")
						+ "' and  JBCXM__Type__c='"
						+ alertType[0].getId() + "' and isdeleted=false"));
		System.out.println("Matching alerts=" + alertsMatched);
		SObject[] AAR = soql
				.getRecords(resolveStrNameSpace("select JBCXM__AlertCount__c from JBCXM__AutomatedAlertRules__c"));
		String aarCount = AAR[0].getField(
				resolveStrNameSpace("JBCXM__AlertCount__c")).toString();
		System.out.println("got alertcount as " + aarCount);
		if (alertsMatched == Integer.parseInt(aC.get("AlertCount"))
				&& Double.parseDouble(aarCount) == Double.parseDouble(aC
						.get("aarAlertCount")))
			return true;
		else
			return false;
	}

	// Validate data
	// Validate Alerts - done
	// Validate Rule status - done
	// Validate in Admin UI if "that" many alerts were triggered
	// If possible verify mail status
}
