delete [select id from Account where name like 'CustomRule%'];
Delete [Select Id from JBCXM__AutomatedAlertRules__c];
Delete [Select Id from JBCXM__CTA__c];


delete [select id from JBCXM__CustomerInfo__c where JBCXM__CustomerName__c in ('CusotomerSync_Initially no customers','LoadToCustomer','LoadtoCust_Picklist','WithAndOrConditionFilters','OCD_Today','CountCust','PreviewResults')];

delete [select id from JBCXM__UsageData__c];