delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'CusotomerSync_Initially no customers'];
delete [select id from JBCXM__CustomerInfo__c];
List<JBCXM__AutomatedAlertRules__c> ar = new List<JBCXM__AutomatedAlertRules__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__AutomatedAlertRules__c acc = new JBCXM__AutomatedAlertRules__c(Name ='CusotomerSync_Initially no customers',JBCXM__ruleType__c ='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"A","refField":null,"select":[{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"ID","objectName":"Account","alias":"","aggregation":"","label":"Id","isAccountIdRelatedField":true}],"criteria":[{"alias":"A","left":{"keys":[],"type":"field","valueType":"STRING","entity":"Account","field":"Id","fieldName":"Id","objectName":"Account","fieldType":"ID","label":"Id","isExternalCriteria":false},"operator":"ne","right":{"keys":[],"type":"value","valueType":"STRING","isNull":true}}]}]');
   ar.add(acc);
}
insert ar;