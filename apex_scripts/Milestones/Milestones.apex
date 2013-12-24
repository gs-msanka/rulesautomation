delete [select Name from JBCXM__Milestone__c];
delete [select Name from JBCXM__PickList__c where JBCXM__Category__c='Milestones'];

List<JBCXM__PickList__c> pList=new List<JBCXM__PickList__c>();

pList.add(new JBCXM__PickList__c(Name='Onboarding',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='OnBoarding',JBCXM__ShortName__c='Ob',JBCXM__Attribute1__c='#00ee00',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=1));
pList.add(new JBCXM__PickList__c(Name='Client Meeting',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Client Meeting',JBCXM__ShortName__c='CM',JBCXM__Attribute1__c='#ff6600',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=2));
pList.add(new JBCXM__PickList__c(Name='Conduct Survey',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Conduct Survey',JBCXM__ShortName__c='CS',JBCXM__Attribute1__c='#ffff00',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=3));
pList.add(new JBCXM__PickList__c(Name='Analyze Survey Responses',JBCXM__Category__c='Milestones',JBCXM__SystemName__c='Analyze Survey Responses',JBCXM__ShortName__c='ASR',JBCXM__Attribute1__c='#0000cc',JBCXM__Active__c=true,JBCXM__DisplayOrder__c=4));

insert pList;

List<JBCXM__Milestone__c> mList = new List<JBCXM__Milestone__c>();

List<Account> accList=[SELECT id FROM Account where name like 'Via Systems'];
List<Opportunity> opList=[SELECT id FROM Opportunity where name like 'Via Systems - new business'];

List<JBCXM__PickList__c> msList=[SELECT id FROM JBCXM__PickList__c where Name like 'Onboarding'];
mList.add(new JBCXM__Milestone__c(JBCXM__Account__c=accList.get(0).Id,JBCXM__Date__c=date.parse('12/02/2013'),JBCXM__Milestone__c=msList.get(0).Id,JBCXM__Opportunity__c=opList.get(0).Id,JBCXM__Comment__c='Complete Onboarding for this customer'));

List<JBCXM__PickList__c> msList1=[SELECT id FROM JBCXM__PickList__c where name like 'Client Meeting'];
mList.add(new JBCXM__Milestone__c(JBCXM__Account__c=accList.get(0).Id,JBCXM__Date__c=date.parse('12/10/2013'),JBCXM__Milestone__c=msList1.get(0).Id,JBCXM__Opportunity__c=opList.get(0).Id,JBCXM__Comment__c='Set up client meeting'));

List<JBCXM__PickList__c> msList2=[SELECT id FROM JBCXM__PickList__c where name like 'Conduct Survey'];
mList.add(new JBCXM__Milestone__c(JBCXM__Account__c=accList.get(0).Id,JBCXM__Date__c=date.parse('12/18/2013'),JBCXM__Milestone__c=msList2.get(0).Id,JBCXM__Opportunity__c=opList.get(0).Id,JBCXM__Comment__c='Conduct survey for this customer'));

List<JBCXM__PickList__c> msList3=[SELECT id FROM JBCXM__PickList__c where name like 'Analyze Survey Responses'];
mList.add(new JBCXM__Milestone__c(JBCXM__Account__c=accList.get(0).Id,JBCXM__Date__c=date.parse('12/23/2013'),JBCXM__Milestone__c=msList3.get(0).Id,JBCXM__Opportunity__c=opList.get(0).Id,JBCXM__Comment__c='Analyze survey responses and take actions'));

insert mList;