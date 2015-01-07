//Cleanup
//Deleting all the scores for this customer

Delete [select id from Account where Name Like 'Scorecard Account%'];


//Creating an account & customers for Scorecard testing

List<Account> accList = new List<Account>();
for(Integer i=0; i< 20; i++) {
	Account acc = new Account(Name='Scorecard Account '+i,
								Industry = 'Engineering',
								Type = 'Installation Partner');
	accList.add(acc);
}
insert accList;
List<JBCXM__PickList__c> pkList = [SELECT Id, Name from JBCXM__PickList__c where JBCXM__Category__c ='Customer Status'];
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
for(Account acc : accList) {
	JBCXM__CustomerInfo__c cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.ID,
																JBCXM__Status__c = pkList.get(0).Id);
	custList.add(cust);
}
insert custList;

//----------------------------------------------------------------------------------------------------------//

//Enable scorecard section in c360
JBCXM__C360Sections__c sections=[select JBCXM__IsActive__c from JBCXM__C360Sections__c where Name='Scorecard'];
sections.JBCXM__IsActive__c=true;
sections.JBCXM__InAccountWidget__c=true;
sections.JBCXM__InCustomer360__c=true;
sections.JBCXM__InOpportunityWidget__c=true;
update(sections);

//----------------------------------------------------------------------------------------------------------//

//----------------------------------------------------------------------------------------------------------//

