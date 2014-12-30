
//Enabling scorecards  config 
List<JBCXM__ScorecardConfig__c> enable_sc=[SELECT JBCXM__ScorecardEnabled__c,JBCXM__CustomerRollup__c FROM JBCXM__ScorecardConfig__c where Name='SCORECARD CONFIGURATION'];

if(enable_sc.size()==0)
{
  JBCXM__ScorecardConfig__c sc=new JBCXM__ScorecardConfig__c(Name='SCORECARD CONFIGURATION',
                                                                JBCXM__HistoryTrackingFrequency__c=12,
                                                                JBCXM__OverrideCustomer__c=TRUE,
                                                                JBCXM__ScorecardEnabled__c=FALSE,
                                                                JBCXM__CustomerRollup__c='WEIGHT');
  insert sc;
}
else {
    enable_sc.get(0).JBCXM__ScorecardEnabled__c=true;
    enable_sc.get(0).JBCXM__CustomerRollup__c='WEIGHT';
    update(enable_sc);
}
