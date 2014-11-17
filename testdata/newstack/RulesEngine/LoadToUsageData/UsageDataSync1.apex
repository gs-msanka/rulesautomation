delete [select id from JBCXM__ActionTemplates__c];
JBCXM__AutomatedAlertRules__c aar = [Select id from JBCXM__AutomatedAlertRules__c Where Name = 'UsageDataSync' Limit 1];
List<JBCXM__ActionTemplates__c> ar = new List<JBCXM__ActionTemplates__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__ActionTemplates__c acc = new JBCXM__ActionTemplates__c(JBCXM__Action_Type__c ='DATA_SYNC',JBCXM__AutomatedAlertRules__c = aar.Id,JBCXM__ActionInfo__c='{"actionType":"DATA_SYNC","target":"upsert","params":{"areaName":"UsageData"},"mappings":[{"source":{"type":"field","valueType":"STRING","field":"Id"},"target":{"type":"field","valueType":"STRING","field":"JBCXM__Account__c"}},{"source":{"type":"calculated","valueType":"DATETIME","expression":{"left":{"type":"constant","valueType":"DATE","value":"ruleDate"},"operator":"ADD_DATE","right":[{"type":"value","valueType":"INTEGER","value":0},{"type":"value","valueType":"STRING","value":"DAY"}]}},"target":{"type":"field","valueType":"DATE","field":"JBCXM__Date__c"}}]}');
   ar.add(acc);
}
insert ar;