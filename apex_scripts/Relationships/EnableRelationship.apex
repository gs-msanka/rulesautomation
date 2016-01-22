JBCXM__GSFeatureConfig__c config=[SELECT JBCXM__Enabled__c,Name FROM JBCXM__GSFeatureConfig__c where Name='RELATIONSHIPS' limit 1];
config.JBCXM__Enabled__c=true;
update config;