delete [select id from opportunity where name ='Acceptance Test Account 15 Generator'];
List<Picklist__c> ids= new List<Picklist__c>([select id from picklist__c where name='ATDebook']);
if (!ids.isEmpty()){
delete [Select id from ordertransactionmap__c where OrderType__c =: ids.get(0).id];
delete ids.get(0);
}
List<Account> accounts=[select id from Account where name like '%Acceptance Test%'];
if (! accounts.isEmpty()){
    delete accounts;
    List<Account> accList = new List<Account>();
for(integer i=1; i<25 ; i++) {
   Account acc = new Account(Name ='Acceptance Test Account ' +i);
   accList.add(acc);
}
insert accList;

List<PickList__c> pkList = [SELECT id FROM PickList__c WHERE Category__c = 'Customer Status' AND
                                 SystemName__c = 'ActiveCustomer'];
List<CustomerInfo__c> custList = new List<CustomerInfo__c>();
CustomerInfo__c cust = null;
for(Account acc : [SELECT id FROM Account where name like 'Acceptance Test Account%']) {
            cust = new CustomerInfo__c(Account__c         = acc.id,
                                         Status__c                 = (pkList.get(0)).id);
   custList.add(cust);
   
}
insert custList;
delete [select id from CustomerInfo__c where CustomerName__c ='Acceptance Test Account 1'];
delete [select id from CustomerInfo__c where CustomerName__c ='Acceptance Test Account 5'];
}
