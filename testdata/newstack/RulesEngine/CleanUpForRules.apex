Delete [Select Id from JBCXM__AutomatedAlertRules__c];
Delete [Select Id from JBCXM__CTA__c];


delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'CusotomerSync_Initially no customers'];
delete [select id from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c like 'CusotomerSync_Initially no customers'];