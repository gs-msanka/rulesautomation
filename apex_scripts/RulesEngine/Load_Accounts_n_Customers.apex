//Account Script.

delete [select id from Account where name like 'Rules Engine %'];

List<String> industryList = new List<String>{'Entertainment',  'Food & Beverage', 'Manufacturing', 'Telecommunications', 'Engineering', 'Communications'};
List<String> typeList = new List<String>{'Customer - Direct', 'Customer - Channel', 'Installation Partner', 'Prospect'};
List<Account> accList = new List<Account>();
Account acc = null;
integer j =0;
integer k =0;
for(integer i =1 ; i < 11 ; i ++) {
	acc = new Account(Name = 'Rules Engine Account' +i,industry = industryList.get(j),type = typeList.get(k),Data_ExternalID__c='Rules Engine Account' +i,NumberOfEmployees=5000);
	j++;
	k++;
	if (j==industryList.size()) {
		j =0;
	}
	if(k==typeList.size()) {
		k =0;
	}
	accList.add(acc);
}

insert accList;

//Accounts to Customers.
delete [select id from JBCXM__CustomerInfo__c where JBCXM__Account__c in (select id from Account where Name like 'Rules Engine%')];
List<String> stList = new List<String>();
List<String> stgList = new List<String>();
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
List<JBCXM__PickList__c> pickList = [SELECT ID, JBCXM__Category__c,JBCXM__SystemName__c FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' OR JBCXM__Category__c ='Customer Stage' ];
for(JBCXM__PickList__c pk :pickList) {
	if(pk.JBCXM__Category__c == 'Customer Status' && pk.JBCXM__SystemName__c =='ActiveCustomer') {
		stList.add(pk.Id);
	}
	if(pk.JBCXM__Category__c == 'Customer Stage') {
		stgList.add(pk.Id);
	}
}

List<Account> aList = [SELECT ID FROM Account WHERE Name like 'Rules Engine%'];
integer stat =0;
integer stg =0;
system.debug(aList.size());
for(Account accnt : aList) {
	JBCXM__CustomerInfo__c cust  = new JBCXM__CustomerInfo__c(JBCXM__Account__c = accnt.id,
										JBCXM__Status__c = stList.get(stat),
										JBCXM__Stage__c = stgList.get(stg),
										JBCXM__Comments__c = 'Test comments : from script.',
										JBCXM__ASV__c=60000.00,
										JBCXM__Users__c=100,
										JBCXM__OriginalContractDate__c = date.parse('01/01/2010'));

	stat++;
	stg++;
	if(stat==stList.size()){
		stat=0;
	}
	if(stg==stgList.size()) {
		stg =0;
	}
	custList.add(cust);
}
insert custList;

