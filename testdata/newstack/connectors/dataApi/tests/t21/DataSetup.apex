DELETE [SELECT id from Account where Name like 'Account Agg-21%' OR Account_ExternalId__c Like 'Account Agg-21%'];
DELETE [SELECT id from Contact  where email like '%gainsighttest.com' and account.name like 'Account Agg-21%'];

List<Account> accList = new List<Account>();

String s  = 'Account Agg-21';

for(integer i=1; i<= 5; i++) {
   Account acc =  new Account(Name = s +'-'+ i,
                                Account_ExternalId__c = s +'-'+ i);
    accList.add(acc);
}

insert accList;


List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
List<JBCXM__Picklist__c> pkList = [Select id from  JBCXM__Picklist__c where JBCXM__SystemName__c ='ActiveCustomer' and JBCXM__Category__c ='Customer Status'];
for(Account acc : accList) {
    JBCXM__CustomerInfo__c cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.Id,
                                                                JBCXM__Status__c = pkList.get(0).Id);
    custList.add(cust);
}
insert custList;

