//Delete [Select id from Account where Name like '%CopilotBULKAccount%'];
//Delete [Select id from contact where LastName like '%CopiotBULKContact%'];
List<Account> accList = new List<Account>();
for(integer i=0; i< 50; i++) {
    Account acc = new Account(Name=DateTime.now().getTime()+'CopilotBULKAccount'+i);
    accList.add(acc);
}
insert accList;
JBCXM__PickList__c status = [SELECT id, JBCXM__SystemName__c from JBCXM__PickList__c where JBCXM__SystemName__c ='ActiveCustomer'];
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
for(Account  acc : accList) {
         JBCXM__CustomerInfo__c cust = new  JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.Id,
                                                JBCXM__Status__c = status.id);
    custList.add(cust);
}
insert custList;

List<Contact> conList = new List<Contact>();
for(Account acc : accList) {
    for(integer i=0; i< 150; i++) {
        Contact con = new Contact(LastName=DateTime.now().getTime()+'CopiotBULKContact'+i,
                                    email =DateTime.now().getTime()+'email'+i+'@automation.gainsighttest.com',
                                    AccountId = acc.Id);
        conList.add(con);
    }
}
insert conList;
