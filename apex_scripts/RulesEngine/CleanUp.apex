List<JBCXM__AutomatedAlertRules__c> rules=[select id from JBCXM__AutomatedAlertRules__c];
delete rules;

List<JBCXM__Alert__c> ruleAlerts=[select id from JBCXM__Alert__c where JBCXM__Account__c in (select id from Account where name like 'Rules%')];
delete ruleAlerts;

List<Account> rulesAcc = [select id from Account where name like 'Rules%'];
delete rulesAcc;