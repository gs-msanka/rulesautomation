delete [select id from opportunity where name ='Acceptance Test Account 15 Generator'];
delete [Select id from JBCXM__ordertransactionmap__c where JBCXM__OrderType__c in (select id from JBCXM__picklist__c where name in ('ATDebook','ATUpsell','ATDownsell'))];
delete [select id from JBCXM__picklist__c where name in ('ATDebook','ATUpsell','ATDownsell')];

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

List<JBCXM__PickList__c> pickList = new List<JBCXM__PickList__c>();
JBCXM__PickList__c atDebook = new JBCXM__PickList__c(JBCXM__SystemDefined__c=true, JBCXM__IncludeInWidget__c=false, JBCXM__Category__c='Order Type', JBCXM__DisplayOrder__c=9, JBCXM__AdoptionMeasure__c=false, JBCXM__Active__c=true, JBCXM__ShortName__c='ATD', JBCXM__SystemName__c='ATDebook', JBCXM__Attribute3__c='', Name='ATDebook', JBCXM__UsageTrackingMeasure__c=false, JBCXM__Attribute2__c='', JBCXM__MapToBookingType__c='Debook', JBCXM__Attribute4__c=false, JBCXM__Attribute1__c='#CCFF33');
JBCXM__PickList__c atDownsell = new JBCXM__PickList__c(JBCXM__SystemDefined__c=true, JBCXM__IncludeInWidget__c=false, JBCXM__Category__c='Order Type', JBCXM__DisplayOrder__c=10, JBCXM__AdoptionMeasure__c=false, JBCXM__Active__c=true, JBCXM__ShortName__c='ATDL', JBCXM__SystemName__c='ATDownsell', JBCXM__Attribute3__c='', Name='ATDownsell', JBCXM__UsageTrackingMeasure__c=false, JBCXM__Attribute2__c='', JBCXM__MapToBookingType__c='Downsell', JBCXM__Attribute4__c=false, JBCXM__Attribute1__c='#CCEE33');
JBCXM__PickList__c atUpsell = new JBCXM__PickList__c(JBCXM__SystemDefined__c=true, JBCXM__IncludeInWidget__c=false, JBCXM__Category__c='Order Type', JBCXM__DisplayOrder__c=11, JBCXM__AdoptionMeasure__c=false, JBCXM__Active__c=true, JBCXM__ShortName__c='ATUL', JBCXM__SystemName__c='ATUpsell', JBCXM__Attribute3__c='', Name='ATUpsell', JBCXM__UsageTrackingMeasure__c=false, JBCXM__Attribute2__c='', JBCXM__MapToBookingType__c='Upsell', JBCXM__Attribute4__c=false, JBCXM__Attribute1__c='#CCDD33');
picklist.add(atDebook);
picklist.add(atDownsell);
picklist.add(atUpsell);
insert pickList;

String atdeID=[select id from JBCXM__PickList__c where name='ATDebook'].get(0).id;
String atdID=[select id from JBCXM__PickList__c where name='ATDownsell'].get(0).id;
String atupID=[select id from JBCXM__PickList__c where name='ATUpsell'].get(0).id;
String subID=[select id from JBCXM__TransactionType__c where name='Subscription'].get(0).id;
String serID=[select id from JBCXM__TransactionType__c where name='Services'].get(0).id;
String usersID=[select id from JBCXM__TransactionType__c where name='Users'].get(0).id;
List<JBCXM__OrderTransactionMap__c> maps=new List<JBCXM__OrderTransactionMap__c>();

  JBCXM__OrderTransactionMap__c r1 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdeID, 
                                                                                         JBCXM__TransactionType__c = subID,
                                                                                         JBCXM__DisplayOrder__c = 1,
                                                                                         JBCXM__Active__C=true); 
   JBCXM__OrderTransactionMap__c r2 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdeID, 
                                                                                         JBCXM__TransactionType__c = usersID,
                                                                                         JBCXM__DisplayOrder__c = 2,
                                                                                         JBCXM__Active__C=true);
  JBCXM__OrderTransactionMap__c r3 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdeID, 
                                                                                         JBCXM__TransactionType__c = serID,
                                                                                         JBCXM__DisplayOrder__c = 3,
                                                                                         JBCXM__Active__C=true);                                                                                         
  JBCXM__OrderTransactionMap__c r4 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdID, 
                                                                                         JBCXM__TransactionType__c = subID,
                                                                                         JBCXM__DisplayOrder__c = 1,
                                                                                         JBCXM__Active__C=true); 
   JBCXM__OrderTransactionMap__c r5= new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdID, 
                                                                                         JBCXM__TransactionType__c = usersID,
                                                                                         JBCXM__DisplayOrder__c = 2,
                                                                                         JBCXM__Active__C=true); 
  JBCXM__OrderTransactionMap__c r6 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atdID, 
                                                                                         JBCXM__TransactionType__c = serID,
                                                                                         JBCXM__DisplayOrder__c = 3,
                                                                                         JBCXM__Active__C=true);                                                                                         
   JBCXM__OrderTransactionMap__c r7 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atupID, 
                                                                                         JBCXM__TransactionType__c = subID,
                                                                                         JBCXM__DisplayOrder__c = 1,
                                                                                         JBCXM__Active__C=true); 
   JBCXM__OrderTransactionMap__c r8 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atupID, 
                                                                                         JBCXM__TransactionType__c = usersID,
                                                                                         JBCXM__DisplayOrder__c = 2,
                                                                                         JBCXM__Active__C=true); 
  JBCXM__OrderTransactionMap__c r9 = new JBCXM__OrderTransactionMap__c( JBCXM__OrderType__c  = atupID, 
                                                                                         JBCXM__TransactionType__c = serID,
                                                                                         JBCXM__DisplayOrder__c = 3,
                                                                                         JBCXM__Active__C=true);    
  maps.add(r1); 
  maps.add(r2); 
  maps.add(r3);  
  maps.add(r4); 
  maps.add(r5); 
  maps.add(r6); 
  maps.add(r7); 
  maps.add(r8); 
  maps.add(r9);                                                                                                                                                                         
insert maps;

