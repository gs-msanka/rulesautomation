List<String> accExtId = new List<String>{'a0Ej0000005cO0IEAU', 'a0Ej0000005cO0JEAU', 'a0Ej0000005cO0KEAU'};
List<String> contExtId = new List<String>{'001j000000Wco0YAAR','001j000000Wco1VAAR','001j000000Wco1WAAR', '001j000000Wco1XAAR', '001j000000Wco1YAAR', '001j000000Wco1ZAAR'};

DELETE [SELECT id from Account where Name like 'Account Agg-22%' OR Account_ExternalId__c In: accExtId];
DELETE [SELECT id from Contact  where email like '%gainsighttest.com' OR account.name like 'Account Agg-22%' OR Contact_ExternalId__c IN:contExtId];

List<Account> accList = new List<Account>();

String s  = 'Account Agg-22';


for(integer i=1; i<= 3; i++) {
   Account acc =  new Account(Name = s +'-'+ i,
                                Account_ExternalId__c = accExtId.get(i-1));
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


List<String> names = new List<String>{'Giribabu', 'Sivaprasad', 'Srividya', 'Jayaprakesh', 'Abilash', 'Sunand'};



Contact con1 = new Contact(LastName = names.get(0), email=names.get(0)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(0), accountId=accList.get(0).Id);
Contact con2 = new Contact(LastName = names.get(1), email=names.get(1)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(1), accountId=accList.get(0).Id);
Contact con3 = new Contact(LastName = names.get(2), email=names.get(2)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(2), accountId=accList.get(1).Id);
Contact con4 = new Contact(LastName = names.get(3), email=names.get(3)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(3), accountId=accList.get(1).Id);
Contact con5 = new Contact(LastName = names.get(4), email=names.get(4)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(4), accountId=accList.get(1).Id);
Contact con6 = new Contact(LastName = names.get(5), email=names.get(5)+'@gainsighttest.com', Contact_ExternalId__c= contExtId.get(5), accountId=accList.get(2).Id);
List<Contact> conList = new List<Contact>{con1, con2, con3, con4, con5, con6};
insert conList;