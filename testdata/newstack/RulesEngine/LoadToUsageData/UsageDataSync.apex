delete[select id from JBCXM__UsageData__c];
delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'UsageDataSync'];
List<JBCXM__AutomatedAlertRules__c> ar = new List<JBCXM__AutomatedAlertRules__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__AutomatedAlertRules__c acc = new JBCXM__AutomatedAlertRules__c(Name ='UsageDataSync',JBCXM__ruleType__c ='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"","refField":null,"select":[{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"ID","objectName":"Account","alias":"","aggregation":"","label":"Id","isAccountIdRelatedField":true}],"criteria":[{"isGainSightCustomer":true,"left":{"type":"field","valueType":"STRING","field":"JBCXM__CustomerInfo__c","entity":"Account"},"operator":"ne","right":{"type":"value","valueType":"STRING","value":null}}]}]');
   ar.add(acc);
}
insert ar;


JBCXM__AutomatedAlertRules__c AAR = [select id from JBCXM__AutomatedAlertRules__c where Name like 'UsageDataSync'];
AAR.JBCXM__externalId__c=AAR.Id;
update AAR;