//Accounts to Customers.
delete [select id from JBCXM__CustomerInfo__c where JBCXM__Account__c in (select id from Account where Jigsaw ='Rules Engine')];
List<String> stList = new List<String>();
List<String> stgList = new List<String>();
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
List<JBCXM__PickList__c> pickList = [SELECT ID, JBCXM__Category__c,JBCXM__SystemName__c FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' OR JBCXM__Category__c ='Customer Stage' ];
for(JBCXM__PickList__c pk :pickList) {
	if(pk.JBCXM__Category__c == 'Customer Status' && pk.JBCXM__SystemName__c!='ChurnCustomer') {
		stList.add(pk.Id);
	}
	if(pk.JBCXM__Category__c == 'Customer Stage') {
		stgList.add(pk.Id);
	}
}

List<Account> accList = [SELECT ID FROM Account WHERE Jigsaw='Rules Engine'];
integer j =0;
integer k =0;
system.debug(accList.size());
for(Account acc : accList) {
	JBCXM__CustomerInfo__c cust  = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.id,
										JBCXM__Status__c = stList.get(j),
										JBCXM__Stage__c = stgList.get(k),
										JBCXM__Comments__c = 'Test comments : from script.',
										JBCXM__OriginalContractDate__c = date.parse('01/01/2010'));

	j++;
	k++;
	if(j==stList.size()){
		j =0;
	}
	if(k==stgList.size()) {
		k =0;
	}
	custList.add(cust);
}
insert custList;
