JBCXM__ScorecardConfig__c sc= [SELECT Id,IsDeleted,JBCXM__CustomerRollup__c,JBCXM__HistoryTrackingFrequency__c,JBCXM__IsHistoryEnabled__c,JBCXM__OverrideCustomer__c,JBCXM__ScorecardEnabled__c,JBCXM__TrackHistory__c,Name FROM JBCXM__ScorecardConfig__c];
    if( sc.Id <> null && sc.Name== 'SCORECARD CONFIGURATION'){
        //System.debug([Select Name from JBCXM__ScorecardConfig__c]);
		sc.IsDeleted='false';
		sc.JBCXM__CustomerRollup__c='DISABLED';
		sc.JBCXM__HistoryTrackingFrequency__c='12.0';
		sc.JBCXM__IsHistoryEnabled__c='false';
		sc.JBCXM__OverrideCustomer__c='true';
		sc.JBCXM__ScorecardEnabled__c='true';
		sc.JBCXM__TrackHistory__c='false'
     }