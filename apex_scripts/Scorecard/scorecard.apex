//Cleanup
//Deleting all the scores for this cust
delete [select id from JBCXM__CustomerInfo__c where JBCXM__Account__r.Name ='Scorecard Account'];
delete [select id from Account where name ='Scorecard Account'];
delete [select Name from JBCXM__ScorecardFact__c];

//Creating an account for Scorecard testing
Account SCAccount=new Account(Name = 'Scorecard Account',industry = 'Engineering',type = 'Installation Partner');
insert(SCAccount);

//-------------------------------------------------------------------------------------------------------------//

//Adding the Scorecard Account to Customers.
Account acc=[select Id from Account where Name like 'Scorecard Account'];
JBCXM__CustomerInfo__c SCcust  = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.id);
insert(SCcust);

//----------------------------------------------------------------------------------------------------------//

//Enable scorecard section in c360
JBCXM__C360Sections__c sections=[select JBCXM__IsActive__c from JBCXM__C360Sections__c where Name='Scorecard'];
sections.JBCXM__IsActive__c=true;
update(sections);

//----------------------------------------------------------------------------------------------------------//

//Enabling scorecards  config (doing this through automation - selenium)
//List<JBCXM__ScorecardConfig__c> enable_sc=[SELECT JBCXM__ScorecardEnabled__c,JBCXM__CustomerRollup__c FROM JBCXM__ScorecardConfig__c where Name='SCORECARD CONFIGURATION'];
//
//if(enable_sc.size()==0)
//{
//  JBCXM__ScorecardConfig__c sc=new JBCXM__ScorecardConfig__c(Name='SCORECARD CONFIGURATION',JBCXM__HistoryTrackingFrequency__c=12,JBCXM__OverrideCustomer__c=TRUE,JBCXM__ScorecardEnabled__c=FALSE,JBCXM__CustomerRollup__c='WEIGHT');
//  insert sc;
//}
//else {
//enable_sc.get(0).JBCXM__ScorecardEnabled__c=true;
//enable_sc.get(0).JBCXM__CustomerRollup__c='WEIGHT';
//update(enable_sc);
//}
//
//----------------------------------------------------------------------------------------------------------//

