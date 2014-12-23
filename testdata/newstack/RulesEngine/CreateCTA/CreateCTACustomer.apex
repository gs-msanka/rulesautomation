
Delete[Select ID from Account where Name like 'Create CTA No Adv Criteria'];
Account Acc=new Account(Name='Create CTA No Adv Criteria');
insert Acc;
JBCXM__CustomerInfo__c CI=new JBCXM__CustomerInfo__c(JBCXM__Account__c=Acc.Id);
insert CI;