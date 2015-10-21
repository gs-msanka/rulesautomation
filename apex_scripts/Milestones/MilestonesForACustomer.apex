//Adding Milestones for the selected Customer
List<JBCXM__Milestone__c> mList = new List<JBCXM__Milestone__c>();
Account acc = [SELECT ID FROM Account WHERE Name='Milestones Account'];
List<Opportunity> opList=[SELECT id FROM Opportunity where name like 'Milestones Account - new business' LIMIT 1];
List<JBCXM__PickList__c> msList=[SELECT id FROM JBCXM__PickList__c where JBCXM__Category__c='Milestones' and JBCXM__SystemName__c in ('Implementation end date','Referral','HealthCheck','First Transaction','Go Live!','Renewal!','Software Upgrade','Quarterly Business Review Meet') ORDER BY NAME];
List<String> comments=new List<String>{'First Transactions successfully added','Customer Go Live and using system',
                                        'Check on Customer Scorecard','Customer signed off on Implementation',
                                        '1st Quarterly Business Review Meet', 'Referred to 2 more customers',
                                        'Renewal due for this customer','Software release 4.0.2'};
DateTime myDate = DateTime.now();
Integer d=0;
for(Integer p=0;p<msList.size();p++)
{
		mList.add(new JBCXM__Milestone__c (JBCXM__Account__c=acc.Id,
																		JBCXM__Date__c=myDate.addMonths(d),
																		JBCXM__Milestone__c=msList.get(p).Id,
																		JBCXM__Opportunity__c=opList.get(0).Id,
																		JBCXM__Comment__c=comments.get(p)));
		d++;
		if(d==3) d=0;
}
insert mList;