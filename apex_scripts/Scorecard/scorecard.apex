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

//----------------------------------------------------------------------------------------------------------//

