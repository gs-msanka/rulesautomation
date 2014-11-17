delete [select id from JBCXM__ActionTemplates__c];
JBCXM__AutomatedAlertRules__c aar = [Select id from JBCXM__AutomatedAlertRules__c Where Name = 'Send Email Contact Strategy' Limit 1];
List<JBCXM__ActionTemplates__c> ar = new List<JBCXM__ActionTemplates__c>();
for(integer i=0; i<1 ; i++) {
   JBCXM__ActionTemplates__c acc = new JBCXM__ActionTemplates__c(JBCXM__Action_Type__c ='DATA_SYNC',JBCXM__AutomatedAlertRules__c = aar.Id,JBCXM__ActionInfo__c='{"params":{"areaName":"EMAIL"},"actionType":"EMAIL","emailTemplateId":"00Xo0000000EGRVEA4","emailTemplateName":"Gainsight sample template - measure below threshold","recipientStrategy":"SPECIFIC_CONTACTS","identifierType":"CONTACT_MAPPING","fromAddress":{"name":"pdalal","emailId":"pdalal@gainsight.com","replyTo":""},"tokenMapping":{"isNotNullable":true,"tokens":[]},"queries":[{"externalIdentifier":{"type":"field","field":"Id","entity":"Account","uniqueName":"Id","parentObj":"Account","valueType":"STRING","aggregation":""},"identifier":{"type":"field","field":"Contact.AccountId","entity":"Contact","uniqueName":"AccountId","parentObj":"Contact","valueType":"string","aggregation":""},"query":"SELECT Contact.Id,Contact.Email,Contact.AccountId,Contact.Account.Id FROM Contact WHERE Contact.AccountId IN (%values%)"}],"recipientFieldName":"Contact.Email"}');
   ar.add(acc);
}
insert ar;