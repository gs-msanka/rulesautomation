delete [select JBCXM__Feature__c from JBCXM__Features__c];

List<JBCXM__Features__c> features = new List<JBCXM__Features__c>();

features.add(new JBCXM__Features__c(JBCXM__Product__c='P1',JBCXM__Feature__c ='Feature1',JBCXM__SystemName__c='P1F1'));
features.add(new JBCXM__Features__c(JBCXM__Product__c='P1',JBCXM__Feature__c ='Feature2',JBCXM__SystemName__c='P1F2'));
features.add(new JBCXM__Features__c(JBCXM__Product__c='P2',JBCXM__Feature__c ='Feature3',JBCXM__SystemName__c='P2F3'));
features.add(new JBCXM__Features__c(JBCXM__Product__c='P3',JBCXM__Feature__c ='Feat3',JBCXM__SystemName__c='P3F1'));
features.add(new JBCXM__Features__c(JBCXM__Product__c='P4',JBCXM__Feature__c ='Feat4',JBCXM__SystemName__c='P4F1'));
insert features;

delete [select JBCXM__Features__c from JBCXM__CustomerFeatures__c];
List<JBCXM__CustomerFeatures__c> cf = new List<JBCXM__CustomerFeatures__c>();

List<Account> accList=[SELECT id FROM Account where name like 'Via Systems'];

List<JBCXM__Features__c> featList = [SELECT JBCXM__Product__c,JBCXM__Feature__c FROM JBCXM__Features__c where JBCXM__SystemName__c like 'P1F1'];
cf.add(new JBCXM__CustomerFeatures__c(JBCXM__Account__c= accList.get(0).Id,JBCXM__Comment__c='A good feature of Product1',JBCXM__Enabled__c=false,JBCXM__Features__c=featList.get(0).Id,JBCXM__Licensed__c=true));

List<JBCXM__Features__c> featList1 = [SELECT JBCXM__Product__c,JBCXM__Feature__c FROM JBCXM__Features__c where JBCXM__SystemName__c like 'P1F2'];
cf.add(new JBCXM__CustomerFeatures__c(JBCXM__Account__c= accList.get(0).Id,JBCXM__Comment__c='Feature2 of P1',JBCXM__Enabled__c=false,JBCXM__Features__c=featList1.get(0).Id,JBCXM__Licensed__c=false));

List<JBCXM__Features__c> featList2 = [SELECT JBCXM__Product__c,JBCXM__Feature__c FROM JBCXM__Features__c where JBCXM__SystemName__c like 'P2F3'];
cf.add(new JBCXM__CustomerFeatures__c(JBCXM__Account__c= accList.get(0).Id,JBCXM__Comment__c='A good feature of Product2',JBCXM__Enabled__c=false,JBCXM__Features__c=featList2.get(0).Id,JBCXM__Licensed__c=true));

insert cf;
