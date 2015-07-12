JBCXM__ScorecardConfig__c score = new JBCXM__ScorecardConfig__c();
List<JBCXM__ScorecardConfig__c> scConfigList = [Select Id from JBCXM__ScorecardConfig__c where Name = 'SCORECARD CONFIGURATION' limit 1];
score = (scConfigList.size() > 0) ? scConfigList[0] : score;
if(score != null && score.Id != null) {
score.JBCXM__ScorecardEnabled__c=true;
update score;
}
else {
score.NAME='SCORECARD CONFIGURATION';
score.JBCXM__CustomerRollup__c='DISABLED';
score.JBCXM__HistoryTrackingFrequency__c = 12;
score.JBCXM__IsHistoryEnabled__c=false;
score.JBCXM__OverrideCustomer__c=true;
score.JBCXM__ScorecardEnabled__c=true;
score.JBCXM__TrackHistory__c=false;
insert score;
}