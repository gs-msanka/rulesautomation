List<Account> accTBD =[Select id from Account where name like 'Headless Test%'];
if(!accTBD.isEmpty()){
	delete accTBD;
}

List<Account> accList = new List<Account>();
for(integer i=1; i<20 ; i++) {
   Account acc = new Account(Name ='Headless Test ' +i);
   accList.add(acc);
}
insert accList;

List<JBCXM__PickList__c> pkList = [SELECT id FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' AND
                                 JBCXM__SystemName__c = 'ActiveCustomer'];
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
JBCXM__CustomerInfo__c cust = null;
for(Account acc : [SELECT id FROM Account where name like 'Headless Test%']) {
            cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c         = acc.id,
                                         JBCXM__Status__c                 = (pkList.get(0)).id);
   custList.add(cust);
   
}
insert custList;