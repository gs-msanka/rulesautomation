//Cleanup
//Deleting all the scores for this cust
delete [select id from JBCXM__CustomerInfo__c where JBCXM__Account__r.Name ='Scorecard Account'];
delete [select id from Account where name ='Scorecard Account'];
delete [select Name from JBCXM__ScorecardFact__c];

//Creating an account for Scorecard testing
Account SCAccount=new Account(Name = 'Scorecard Account',industry = 'Engineering',type = 'Installation Partner');
insert(SCAccount);

//-------------------------------------------------------------------------------------------------------------//

//Adding the Scorecard Account to Customers.
Account acc=[select Id from Account where Name like 'Scorecard Account'];
JBCXM__CustomerInfo__c SCcust  = new JBCXM__CustomerInfo__c(JBCXM__Account__c = acc.id);
insert(SCcust);

//----------------------------------------------------------------------------------------------------------//

//Enable scorecard section in c360
JBCXM__C360Sections__c sections=[select JBCXM__IsActive__c from JBCXM__C360Sections__c where Name='Scorecard'];
sections.JBCXM__IsActive__c=true;
update(sections);

//----------------------------------------------------------------------------------------------------------//

//Enabling scorecards  config
List<JBCXM__ScorecardConfig__c> enable_sc=[SELECT JBCXM__ScorecardEnabled__c,JBCXM__CustomerRollup__c FROM JBCXM__ScorecardConfig__c where Name='SCORECARD CONFIGURATION'];

if(enable_sc.size()==0)
{
  JBCXM__ScorecardConfig__c sc=new JBCXM__ScorecardConfig__c(Name='SCORECARD CONFIGURATION',JBCXM__HistoryTrackingFrequency__c=12,JBCXM__OverrideCustomer__c=TRUE,JBCXM__ScorecardEnabled__c=FALSE,JBCXM__CustomerRollup__c='WEIGHT');
  insert sc;
}
else {
enable_sc.get(0).JBCXM__ScorecardEnabled__c=true;
enable_sc.get(0).JBCXM__CustomerRollup__c='WEIGHT';
update(enable_sc);
}

//----------------------------------------------------------------------------------------------------------//

//ENABLING NUMERIC SCHEME
JBCXM__ScoringScheme__c cur_scheme_type=[select JBCXM__CustomerScheme__c from JBCXM__ScoringScheme__c where JBCXM__CustomerScheme__c=true];
if (cur_scheme_type==null){}
    else{
cur_scheme_type.JBCXM__CustomerScheme__c=false;
update(cur_scheme_type);
    }
JBCXM__ScoringScheme__c scheme_type=[select JBCXM__CustomerScheme__c from JBCXM__ScoringScheme__c where Name like 'Numeric%'];
scheme_type.JBCXM__CustomerScheme__c=true;
update(scheme_type);

//----------------------------------------------------------------------------------------------------------//

//CREATING GROUPS AND METRICS
//Adding groups to Metrics
List<JBCXM__ScorecardMetric__c> metricList = [SELECT ID FROM JBCXM__ScorecardMetric__c];
System.debug(metricList.size());
DELETE metricList;
List<JBCXM__ScoringScheme__c> scoringScheme = [SELECT Id FROM JBCXM__ScoringScheme__c WHERE JBCXM__Type__c='Score']; //Grade, Color
Map<String, JBCXM__ScorecardMetric__c> scmMap = new Map<String, JBCXM__ScorecardMetric__c>();
Map<String, List<String>> measuresMap = new Map<String, List<String>>();
List<String> groupMeasuresList = new List<String>{'Usage Configuration', 'Support', 'Alignment', 'Environment'};
List<String> d = new List<String>{'Over all Performance', 'Server Utilization', 'Login Times', 'Technical Solutions'};
List<String> d1 = new List<String>{'Cases Opened', 'Cases Closed','Unplanned Outages'};
List<String> d2 = new List<String>{'Executive Sponsorship', 'Strategic Solution','Implementation','Consulting','Program Governance','Success Blueprint'};
List<String> d3 = new List<String>{'Login Activity', 'DataBase Load Created'};
List<Integer> d4 = new List<Integer>{9,5,7,0,7,7,5,10,10,5,3,7,19,2,4};
List<Integer> d5 = new List<Integer>{8,0,6,9,6,3,9,10,3,14,4,6,8,2,4};
measuresMap.put('Environment', d);
measuresMap.put('Support', d1);
measuresMap.put('Alignment', d2);
measuresMap.put('Usage Configuration', d3);
JBCXM__ScorecardMetric__c scmObj = null;
integer i =2;
for(String mea : groupMeasuresList) {
	scmObj = new JBCXM__ScorecardMetric__c(Name=mea,
										JBCXM__AllowOverride__c=true,
										JBCXM__DisplayOrder__c=i,
										JBCXM__InputType__c='Manual',
										JBCXM__IsActive__c=true,
										JBCXM__IsGroup__c=true);
	i++;
	scmMap.put(mea, scmObj);
}
insert scmMap.values();
System.debug('Total Groups added : ' +scmMap.size());
List<JBCXM__ScorecardMetric__c> measuresList = new List<JBCXM__ScorecardMetric__c>();
i=0;
Integer j=0;
for(String mea : groupMeasuresList) {
    List<String> aList  = measuresMap.get(mea);
    JBCXM__ScorecardMetric__c measure1 = scmMap.get(mea);
	for(String measure : aList) {
		scmObj = new JBCXM__ScorecardMetric__c(Name							= measure,
											JBCXM__AllowOverride__c		= true,
											JBCXM__DisplayOrder__c		= i,
											JBCXM__GroupId__c			= measure1.Id,
											JBCXM__HelpText__c			= '{}',
											JBCXM__InputType__c			= 'Manual',
											JBCXM__IsActive__c			= true,
											JBCXM__IsGroup__c			= false,
											JBCXM__PeriodType__c		= 'WEEKS',
											JBCXM__ScoringSchemeId__c	= scoringScheme.get(0).Id,
											JBCXM__ValidityPeriod__c 	= d5.get(j),
											JBCXM__Weight__c			= d4.get(j));
		measuresList.add(scmObj);
	}
}
insert measuresList;
System.debug('Total Measure added : ' +measuresList.size());


//----------------------------------------------------------------------------------------------------------//