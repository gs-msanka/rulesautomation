DELETE [SELECT id from Account where Name like 'Account Agg Test Case(20)%' or Account_Ext_NU__c like 'Account Agg Test Case(20)%'];
List<Account> accList = new List<Account>();

String s  = 'Account Agg Test Case(20)';
for(integer i=1; i<= 5; i++) {
   Account acc =  new Account(Name = s +'-'+ i,
                                Account_Ext_NU__c = s +'-'+ i);
    accList.add(acc);
}

accList.get(1).Account_Ext_NU__c = s +'-'+ 1;  //Making duplicates externalId.
accList.get(2).Account_Ext_NU__c = s +'-'+ 1;  //Making duplicates externalId.
insert accList;

accList.remove(3);  //Account will not be added as customer.

List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
List<JBCXM__Picklist__c> pkList = [Select id from  JBCXM__Picklist__c where JBCXM__SystemName__c ='ActiveCustomer' and JBCXM__Category__c ='Customer Status'];
for(Account acc : accList) {
    JBCXM__CustomerInfo__c cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.Id,
                                                                JBCXM__Status__c = pkList.get(0).Id);
    custList.add(cust);
}
insert custList;

