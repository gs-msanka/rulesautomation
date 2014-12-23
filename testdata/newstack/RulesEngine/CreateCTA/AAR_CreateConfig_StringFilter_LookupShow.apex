Delete[Select Id from JBCXM__AutomatedAlertRules__c where Name='Create CTA No Adv Criteria'];
Delete[Select Id from JBCXM__CTA__c];
JBCXM__AutomatedAlertRules__c NewAAR=new JBCXM__AutomatedAlertRules__c(Name='Create CTA No Adv Criteria',
JBCXM__ruleType__c='CONDITIONAL',JBCXM__SourceType__c='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"A","refField":null,"select":[{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"ID","objectName":"Account","alias":"","aggregation":"","label":"Id","isAccountIdRelatedField":true},{"type":"field","field":"CSM_Automation__c","fieldName":"CSM_Automation__c","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"REFERENCE","objectName":"Account","alias":"","aggregation":"","label":"CSM_Automation"}],"criteria":[{"alias":"A","left":{"keys":[],"type":"field","valueType":"STRING","entity":"Account","field":"Name","fieldName":"Name","objectName":"Account","fieldType":"STRING","label":"Account Name","isExternalCriteria":false},"operator":"startsWith","right":{"keys":[],"type":"value","valueType":"STRING","isNull":false,"value":"Create CTA No Adv Criteria"}},{"isGainSightCustomer":true,"left":{"type":"field","valueType":"STRING","field":"JBCXM__CustomerInfo__c","entity":"Account"},"operator":"ne","right":{"type":"value","valueType":"STRING","value":null}}]}]');
insert NewAAR;


NewAAR.JBCXM__externalId__c=NewAAR.Id;
update NewAAR;