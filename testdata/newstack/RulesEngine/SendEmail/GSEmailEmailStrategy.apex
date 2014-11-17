delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'Send Email Email Strategy'];
List<JBCXM__AutomatedAlertRules__c> ar = new List<JBCXM__AutomatedAlertRules__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__AutomatedAlertRules__c acc = new JBCXM__AutomatedAlertRules__c(Name ='Send Email Email Strategy',JBCXM__ruleType__c ='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"","refField":"AccountId","select":[{"type":"field","field":"Email","fieldName":"Email","groupable":true,"entity":"Contact","valueType":"STRING","fieldType":"EMAIL","objectName":"Contact","alias":"","aggregation":"","label":"Email"},{"type":"field","field":"AccountId","fieldName":"AccountId","groupable":true,"entity":"Contact","valueType":"STRING","fieldType":"REFERENCE","objectName":"Contact","alias":"","aggregation":"","label":"Account ID","isAccountIdRelatedField":true},{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Contact","valueType":"STRING","fieldType":"ID","objectName":"Contact","alias":"","aggregation":"","label":"Id"}],"criteria":[{"isGainSightCustomer":true,"left":{"type":"field","valueType":"STRING","entity":"Contact","field":"Account.JBCXM__CustomerInfo__c"},"operator":"ne","right":{"type":"value","valueType":"STRING","value":null}}]}]');
   ar.add(acc);
}
insert ar;

JBCXM__AutomatedAlertRules__c AAR = [select id from JBCXM__AutomatedAlertRules__c where Name like 'Send Email Email Strategy'];
AAR.JBCXM__externalId__c=AAR.Id;
update AAR;