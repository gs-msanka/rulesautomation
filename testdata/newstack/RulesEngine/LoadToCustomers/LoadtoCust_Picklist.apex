delete [select id from JBCXM__AutomatedAlertRules__c where Name like 'LoadtoCust_Picklist'];
List<JBCXM__AutomatedAlertRules__c> ar = new List<JBCXM__AutomatedAlertRules__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__AutomatedAlertRules__c acc = new JBCXM__AutomatedAlertRules__c(Name ='LoadtoCust_Picklist',JBCXM__ruleType__c ='CONDITIONAL',JBCXM__TriggerCriteria__c='[{"whereLogic":"A","refField":null,"select":[{"type":"field","field":"Name","fieldName":"Name","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"STRING","objectName":"Account","alias":"","aggregation":"","label":"Account Name"},{"type":"field","field":"Id","fieldName":"Id","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"ID","objectName":"Account","alias":"","aggregation":"","label":"Id","isAccountIdRelatedField":true},{"type":"field","field":"JBCXM__CustomerInfo__r.JBCXM__Account__r.Name","fieldName":"JBCXM__Account__r.Name","groupable":true,"entity":"Account","valueType":"STRING","fieldType":"TEXT","objectName":"JBCXM__CustomerInfo__c","alias":"","aggregation":"","label":"Account Name"}],"criteria":[{"alias":"A","left":{"keys":[],"type":"field","valueType":"STRING","entity":"Account","field":"Rating","fieldName":"Rating","objectName":"Account","fieldType":"PICKLIST","label":"Account Rating","isExternalCriteria":false},"operator":"nin","right":{"keys":[],"type":"value","valueType":"STRING","value":["Hot","Warm","Cold"]}},{"isGainSightCustomer":true,"left":{"type":"field","valueType":"STRING","field":"JBCXM__CustomerInfo__c","entity":"Account"},"operator":"ne","right":{"type":"value","valueType":"STRING","value":null}}]}]');
   ar.add(acc);
}
insert ar;


JBCXM__AutomatedAlertRules__c AAR = [select id from JBCXM__AutomatedAlertRules__c where Name like 'LoadtoCust_Picklist'];
AAR.JBCXM__externalId__c=AAR.Id;
update AAR;