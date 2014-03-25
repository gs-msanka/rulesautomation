List<JBCXM__AutomatedAlertRules__c>  rules =  new List<JBCXM__AutomatedAlertRules__c>();
JBCXM__AutomatedAlertRules__c  rule = null;

//Rule Array format: {filterLogic,filterCriteria,AlertCount,alertSeverity,alertReason,alertType,alertStatus,alertComment,alertSubject,PlayBookIds,SourceType,TaskDefaultOwner,TaskOwnerField,TriggerCriteria,TriggeredUsageOn}

List<List<String>> RulesFields =new List<List<String>>{new List<String>{'','','0','High','Support Issues','Potential Churn','Active','','Usage Alert 2','Alert Playbook 1','Usage','Current','OwnerId','','INSTANCELEVEL'}};

//TriggerCriteria Fields
//{valueRef,type,trendParam,trendParams-traillingMonths,trendParams-isByMonth,trendParams-operation,trendParams-name,value,honorNull,name,logicalOperator,operator
List<List<String>> TCFields=new List<List<String>>{new List<String>{'','Trend based','1','true','AVG','Page Views','20','true','Page Views','AND','m'}};
for(Integer i=0;i<RulesFields.size();i++)
{
    JBCXM__Picklist__c alertSeverity=[select id,Name from JBCXM__Picklist__c where JBCXM__Category__c='Alert Severity' and Name=:RulesFields[i][3]];
   JBCXM__Picklist__c alertReason=[select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Reason' and Name=:RulesFields[i][4]];
   JBCXM__Picklist__c alertType=[select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Type' and Name=:RulesFields[i][5]];
   JBCXM__Picklist__c alertStatus=[select id from JBCXM__Picklist__c where JBCXM__Category__c='Alert Status' and Name=:RulesFields[i][6]];
   //JBCXM__Playbook__c alertPlaybook=[select id from JBCXM__Playbook__c where Name=:RulesFields[i][9] and JBCXM__Type__c='Alert' limit 1];
  
	rule=new JBCXM__AutomatedAlertRules__c(JBCXM__AdvanceCriteria__c='{"filterLogic":"'+RulesFields[i][0]+'","filterCriteria":[]}',
	JBCXM__AlertCount__c=integer.valueOf(RulesFields[i][2]),JBCXM__AlertCriteria__c='{"alertSeverity":"'+alertSeverity.Id+'","alertReason":"'+alertReason.Id+'","alertType":"'+alertType.Id+'","alertStatus":"'+alertStatus.Id+'","alertComment":"'+RulesFields[i][7]+'","alertSubject":"'+RulesFields[i][8]+'"}',
	JBCXM__PlayBookIds__c='',
	JBCXM__SourceType__c=RulesFields[i][10],
	JBCXM__Status__c=true,
	JBCXM__TaskDefaultOwner__c=Userinfo.getUserId(),
	JBCXM__TaskOwnerField__c=RulesFields[i][12],
	JBCXM__TriggerCriteria__c='[{"valueRef":'+(TCFields[i][0].equals('')?'null':'"'+TCFields[i][0]+'"}')+',"type":"'+TCFields[i][1]+'","trendParam":{"traillingMonths":'+TCFields[i][2]+',"isByMonth":'+(TCFields[i][3].equals('true')?'true':'false')+',"operation":"'+TCFields[i][4]+'","name":"'+TCFields[i][5]+'"},"value":'+TCFields[i][6]+',"honorNull":'+TCFields[i][7]+',"name":"'+TCFields[i][8]+'","logicalOperator":"'+TCFields[i][9]+'","operator":"'+TCFields[i][10]+'"}]',
	JBCXM__TriggeredUsageOn__c=RulesFields[i][14]);
    rules.add(rule);
}
insert rules;
