Delete[Select Id from JBCXM__CustomerFeatures__c];

Delete[Select Id from JBCXM__Features__c];

Delete [Select Id from JBCXM__AutomatedAlertRules__c where Name Like 'loadToFeat%'];

Delete [select id from Account];

List<String> featureNames = new List<String>{'One','Two','Three','Four','Five'};
List<String> sysNames = new List<String>{'Abc','Def','Ghi','Jkl','Mno'};
List<String> prodNames = new List<String>{'One','One','One','Two','Three'};
List<JBCXM__Features__c> feat = new List<JBCXM__Features__c>();
for(integer i =0;i<5;i++){
JBCXM__Features__c f = new JBCXM__Features__c();
f.JBCXM__Feature__c=featureNames.get(i);
f.JBCXM__Product__c =prodNames.get(i);
f.JBCXM__SystemName__c = sysNames.get(i);
feat.add(f);
}
upsert feat;