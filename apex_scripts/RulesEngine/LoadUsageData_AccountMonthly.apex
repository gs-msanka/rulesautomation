//Account Level Monthly data

delete [select id from JBCXM__UsageData__c];

List<JBCXM__UsageData__c> ud=new List<JBCXM__UsageData__c>();
for(Integer 