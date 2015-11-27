Delete[SELECT Id FROM Opportunity];
Delete[SELECT Id FROM Case];

Delete [select id from Account where name like 'Rules UI'];
Delete [Select Id from JBCXM__AutomatedAlertRules__c];
// Delete [SELECT Id FROM JBCXM__ActionTemplates__c];

// Delete [select id from Account where name like 'Account RULESUI%'];
// Delete [SELECT Id,JBCXM__Account__r.Name FROM JBCXM__CustomerInfo__c where JBCXM__Account__r.Name like 'Account RULESUI%'];

// Delete [select id from JBCXM__CustomerInfo__c];
Delete [SELECT Id FROM JBCXM__CTA__c];
Delete [SELECT Id FROM JBCXM__CustomerFeatures__c];
Delete [SELECT Id FROM JBCXM__ScorecardFact__c];
// Delete [SELECT Id FROM RulesSFDCCustom__c];
Delete [SELECT Id FROM JBCXM__UsageData__c];
Delete [SELECT Id FROM JBCXM__Milestone__c];
