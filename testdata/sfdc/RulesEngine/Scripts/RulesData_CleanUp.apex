//Delete all the automated rules.
Delete [Select id from JBCXM__AutomatedAlertRules__c];
//Delete all the account that are related to rules engine.
Delete [Select id from Account Where AccountNumber='RulesAccount'];
//Delete all the CTA created for rule engine accounts.
Delete [Select id from JBCXM__CTA__c Where JBCXM__Account__r.AccountNumber = 'RulesAccount'];
//Delete all the alert created for rule engine accounts.
Delete [Select id from JBCXM__Alert__c Where JBCXM__Account__r.AccountNumber = 'RulesAccount'];
