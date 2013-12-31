delete [select id from opportunity where name ='Acceptance Test Account 15 Generator'];
List<JBCXM__Picklist__c> ids= new List<JBCXM__Picklist__c>([select id from JBCXM__picklist__c where name='ATDebook']);
if (!ids.isEmpty()){
delete [Select id from JBCXM__ordertransactionmap__c where JBCXM__OrderType__c =: ids.get(0).id];
delete ids.get(0);
}
ids= new List<JBCXM__Picklist__c>([select id from JBCXM__picklist__c where name='ATUpsell']);
if (!ids.isEmpty()){
delete [Select id from JBCXM__ordertransactionmap__c where JBCXM__OrderType__c =: ids.get(0).id];
delete ids.get(0);
}
ids= new List<JBCXM__Picklist__c>([select id from JBCXM__picklist__c where name='ATDownsell']);
if (!ids.isEmpty()){
delete [Select id from JBCXM__ordertransactionmap__c where JBCXM__OrderType__c =: ids.get(0).id];
delete ids.get(0);
}
List<Account> accounts=[select id from Account where name like '%Acceptance Test%'];
if (! accounts.isEmpty()){
    delete accounts;
    }
    List<Account> accList = new List<Account>();
for(integer i=1; i<25 ; i++) {
   Account acc = new Account(Name ='Acceptance Test Account ' +i);
   accList.add(acc);
}
insert accList;

List<JBCXM__PickList__c> pkList = [SELECT id FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' AND
                                 JBCXM__SystemName__c = 'ActiveCustomer'];
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
JBCXM__CustomerInfo__c cust = null;
for(Account acc : [SELECT id FROM Account where name like 'Acceptance Test Account%']) {
            cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c         = acc.id,
                                         JBCXM__Status__c                 = (pkList.get(0)).id);
   custList.add(cust);
   
}
insert custList;
delete [select id from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c ='Acceptance Test Account 1'];
delete [select id from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c ='Acceptance Test Account 5'];
}
