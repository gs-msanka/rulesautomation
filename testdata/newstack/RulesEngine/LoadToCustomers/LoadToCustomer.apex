delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'LoadToCustomer'];
List<JBCXM__AutomatedAlertRules__c> ar = new List<JBCXM__AutomatedAlertRules__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__AutomatedAlertRules__c acc = new JBCXM__AutomatedAlertRules__c(Name ='LoadToCustomer',JBCXM__ruleType__c ='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"A","refField":null,"select":[{"type":"field","field":"Name","fieldName":"Name","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"STRING","objectName":"Account","alias":"","aggregation":"","label":"Account Name"},{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"ID","objectName":"Account","alias":"","aggregation":"","label":"Id","isAccountIdRelatedField":true},{"type":"field","field":"JBCXM__CustomerInfo__r.JBCXM__Account__r.Name","fieldName":"JBCXM__Account__r.Name","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"TEXT","objectName":"JBCXM__CustomerInfo__c","alias":"","aggregation":"","label":"Account Name"}],"criteria":[{"alias":"A","left":{"keys":[],"type":"field","valueType":"STRING","entity":"Account","field":"JBCXM__CustomerInfo__r.JBCXM__CustomerName__c","fieldName":"JBCXM__CustomerName__c","objectName":"JBCXM__CustomerInfo__c","fieldType":"STRING","label":"Customer Name","isExternalCriteria":false},"operator":"startsWith","right":{"keys":[],"type":"value","valueType":"STRING","isNull":false,"value":"A"}},{"isGainSightCustomer":true,"left":{"type":"field","valueType":"STRING","field":"JBCXM__CustomerInfo__c","entity":"Account"},"operator":"ne","right":{"type":"value","valueType":"STRING","value":null}}]}]');
   ar.add(acc);
}
insert ar;

JBCXM__AutomatedAlertRules__c AAR = [select id from JBCXM__AutomatedAlertRules__c where Name like 'LoadToCustomer'];
AAR.JBCXM__externalId__c=AAR.Id;
update AAR;