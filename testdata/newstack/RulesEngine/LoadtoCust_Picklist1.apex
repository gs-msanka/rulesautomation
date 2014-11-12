delete [select id from JBCXM__ActionTemplates__c];
JBCXM__AutomatedAlertRules__c aar = [Select id from JBCXM__AutomatedAlertRules__c Where Name = 'LoadtoCust_Picklist' Limit 1];
List<JBCXM__ActionTemplates__c> ar = new List<JBCXM__ActionTemplates__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__ActionTemplates__c acc = new JBCXM__ActionTemplates__c(JBCXM__Action_Type__c ='DATA_SYNC',JBCXM__AutomatedAlertRules__c = aar.Id,JBCXM__ActionInfo__c='{"actionType":"DATA_SYNC","target":"upsert","params":{"areaName":"Customers"},"mappings":[{"source":{"type":"field","valueType":"STRING","field":"Id"},"target":{"type":"field","valueType":"STRING","field":"JBCXM__Account__c"}}]}');
   ar.add(acc);
}
insert ar;