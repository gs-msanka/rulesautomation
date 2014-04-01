package com.gainsight.sfdc.rulesEngine.tests;

import java.util.HashMap;

import com.gainsight.sfdc.tests.BaseTest;

public class RulesEngineDataValidation extends BaseTest{

	public boolean checkAlertsCreated(HashMap<String, String> aC) {
		int alertsMatched;
		alertsMatched=soql.getRecordCount(resolveStrNameSpace("SELECT  Id from JBCXM__Alert__c where JBCXM__Account__c in (select id from Account where name like '') and  JBCXM__Comment__c='"+aC.get("comment")+"', JBCXM__LinkedToUsage__c=true, JBCXM__Reason__c='"+aC.get("reason")+"', JBCXM__Severity__c='"+aC.get("severity")+"', JBCXM__Status__c='"+aC.get("status")+"',Name='"+aC.get("subject")+"', JBCXM__Type__c='"+aC.get("type")+"'"));
		if(alertsMatched==1) return true;
		else return false;
	}	
	
	//Validate data 
		// Validate Alerts
		// Validate Rule status
		// Validate in Admin UI if "that" many alerts were triggered
		// If possible verify mail status
}
