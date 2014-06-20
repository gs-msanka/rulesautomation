package com.gainsight.sfdc.rulesEngine.tests;

import java.util.HashMap;

import com.gainsight.sfdc.tests.BaseTest;
import com.sforce.soap.partner.sobject.SObject;

public class RulesEngineDataValidation extends BaseTest{

	public boolean checkAlertsCreated(HashMap<String, String> aC,String severity,String reason,String type,String status) {
		int alertsMatched;
		alertsMatched=soql.getRecordCount(resolveStrNameSpace("SELECT  Id from JBCXM__Alert__c where JBCXM__Reason__c='"+reason+"' and JBCXM__Severity__c='"+severity+"' and JBCXM__Status__c='"+status+"' and Name='"+aC.get("alertSubject")+"' and  JBCXM__Type__c='"+type+"' and isdeleted=false" ));
		System.out.println("query to get count:SELECT  Id from JBCXM__Alert__c where JBCXM__Reason__c='"+reason+"' and JBCXM__Severity__c='"+severity+"' and JBCXM__Status__c='"+status+"' and Name='"+aC.get("alertSubject")+"' and  JBCXM__Type__c='"+type+"' and isdeleted=false");
		System.out.println("Matching alerts="+alertsMatched);
		SObject[] AAR=soql.getRecords(resolveStrNameSpace("select JBCXM__AlertCount__c from JBCXM__AutomatedAlertRules__c"));
		String aarCount=AAR[0].getField(resolveStrNameSpace("JBCXM__AlertCount__c")).toString();
			System.out.println("got alertcount as "+aarCount);
		if(alertsMatched==Integer.parseInt(aC.get("AlertCount")) && Double.parseDouble(aarCount)==Double.parseDouble(aC.get("aarAlertCount"))) return true;
		else return false;
	}	
	
	//Validate data 
		// Validate Alerts - done 
		// Validate Rule status - done
		// Validate in Admin UI if "that" many alerts were triggered
		// If possible verify mail status
}
