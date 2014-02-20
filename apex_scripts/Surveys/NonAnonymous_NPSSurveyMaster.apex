//Cleanup
delete[select id from JBCXM__SurveyMaster__c where Name='Customer Retention'];


List<JBCXM__SurveyMaster__c> NPSSurveyMaster = new List<JBCXM__SurveyMaster__c>();
JBCXM__Survey__c Survey=[select Id,JBCXM__Code__c,JBCXM__Title__c from JBCXM__Survey__c where JBCXM__Code__c='Customer Retention'];

integer i=0;	
NPSSurveyMaster.add(new JBCXM__SurveyMaster__c ( JBCXM__PublishDate__c = system.Date.today(),
									Name=Survey.JBCXM__Code__c,
								   JBCXM__SurveyName__c = Survey.JBCXM__Title__c ,
			                       JBCXM__UserDistributionCount__c=0,
							       JBCXM__CustomerDistributionCount__c=0,
								   JBCXM__Default__c=false,
								   NPSSurMast_ExternalID__c=Survey.JBCXM__Code__c,
								   JBCXM__SurveyId__c=Survey.Id,
								   JBCXM__AccountLevelAggregation__c='{"All Roles":{"pro":0,"pas":0,"det":2},"user":{"pro":0,"pas":0,"det":2}}',
								   JBCXM__UserLevelAggregation__c='{"All Roles":{"pro":2,"pas":3,"det":24},"user":{"pro":2,"pas":3,"det":24}}'
								));
 insert NPSSurveyMaster;		
 
 