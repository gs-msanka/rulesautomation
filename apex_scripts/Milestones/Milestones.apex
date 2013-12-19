delete [select Name from JBCXM__PickList__c where JBCXM__Category__c='Milestones'];

List<JBCXM__PickList__c> pList=new List<JBCXM__PickList__c>();

pList.add(new JBCXM__PickList__c(Name='Onboarding',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='OnBoarding',JBCXM__ShortName__c='Ob',JBCXM__Attribute1__c='#00ee00',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=1));
pList.add(new JBCXM__PickList__c(Name='Client Meeting',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Client Meeting',JBCXM__ShortName__c='CM',JBCXM__Attribute1__c='#ff6600',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=2));
pList.add(new JBCXM__PickList__c(Name='Conduct Survey',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Conduct Survey',JBCXM__ShortName__c='CS',JBCXM__Attribute1__c='#ffff00',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=3));
pList.add(new JBCXM__PickList__c(Name='Analyze Survey Responses',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Analyze Survey Responses',JBCXM__ShortName__c='ASR',JBCXM__Attribute1__c='#0000cc',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=4));

insert pList;