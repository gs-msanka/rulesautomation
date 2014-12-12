delete [select Name from JBCXM__Milestone__c ];
delete [select Name from JBCXM__PickList__c where JBCXM__Category__c='Milestones' and JBCXM__SystemName__c in ('Implementation end date','Referral','HealthCheck','First Transaction','Go Live!','Renewal!','Software Upgrade','Quarterly Business Review Meet')];
delete [SELECT id FROM Opportunity where name like 'Milestones Account - new business'];
delete [select id from Account where name like 'Milestones Account'];

//Adding into picklist for category Milestones
List<JBCXM__PickList__c> pList=new List<JBCXM__PickList__c>();
List<String>  milestones=new List<String>{'Implementation end date','Referral','HealthCheck','First Transaction','Go Live!','Renewal!','Software Upgrade','Quarterly Business Review Meet'};
List<String> colors=new List<String>{'#FF0000','#ff9900','#ffff00','#008b00','#3333ff','#0000cc','#00cd00','#99ffcc'};
for(Integer i=0;i<milestones.size();i++)
{

		pList.add(new JBCXM__PickList__c(Name=milestones.get(i),JBCXM__Category__c='Milestones',JBCXM__SystemName__c=milestones.get(i),JBCXM__ShortName__c=milestones.get(i).substring(0,3),JBCXM__Attribute1__c=colors.get(i),JBCXM__Active__c=true,JBCXM__DisplayOrder__c=i+1));
}
insert pList;

//Enabling Opportunities for Milestones from App settings
List<JBCXM__ApplicationSettings__c> appSettingsList = [SELECT Id, JBCXM__NPSAssociatedObject__c, JBCXM__NPSAssociatedObjectKey__c, JBCXM__NPSAssociatedObjectDisplay__c, JBCXM__NPSAssociatedObjectDisplayName__c, JBCXM__NPSAssociated__c, JBCXM__MilestoneAssociated__c, JBCXM__AlertAssociated__c FROM JBCXM__ApplicationSettings__c];
JBCXM__ApplicationSettings__c appSettings = appSettingsList.get(0);
appSettings.JBCXM__NPSAssociatedObject__c = 'Opportunity';
appSettings.JBCXM__NPSAssociatedObjectKey__c = 'Name';
appSettings.JBCXM__NPSAssociatedObjectDisplay__c = 'Name';
appSettings.JBCXM__NPSAssociatedObjectDisplayName__c  = 'Opportunity';
appSettings.JBCXM__NPSAssociated__c = true;
appSettings.JBCXM__MilestoneAssociated__c = true;
appSettings.JBCXM__AlertAssociated__c = true;
update appSettings;

//Creating an account for adding milestones
Account acc=new Account(Name='Milestones Account');
insert acc;

//Creating a Opportunity for one of the existing customers (from sample data) 
List<Account> accList=[SELECT Id FROM Account where name like 'Milestones Account'];
Opportunity opp = new Opportunity(name ='Milestones Account - new business',
									StageName = 'Closed Won',
									Type = 'New Business',
									Amount = 2000,
									CloseDate = date.today(),
									AccountId = accList.get(0).Id);
insert opp;

//Adding the account to customers
JBCXM__PickList__c stage = [SELECT Id FROM JBCXM__PickList__c WHERE JBCXM__Category__c ='Customer Stage'  limit 1];
JBCXM__PickList__c status = [SELECT ID FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' limit 1];

JBCXM__CustomerInfo__c cust  = new JBCXM__CustomerInfo__c(JBCXM__Account__c = accList.get(0).Id,
										JBCXM__Status__c = status.Id,
										JBCXM__Stage__c = stage.Id,
										JBCXM__Comments__c = 'Valued Customer.',
										JBCXM__OriginalContractDate__c = date.today());
insert cust;

