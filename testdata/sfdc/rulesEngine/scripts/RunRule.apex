Map<String,Object>  ruleParams=new Map<String,Object>();

JBCXM__AutomatedAlertRules__c RuleId=[select id from JBCXM__AutomatedAlertRules__c];
String ruleId=RuleId.Id;

ruleParams.put('ruleId',ruleId);

ruleParams.put('ruleRunDate',REPLACE_RUN_DATE);

ruleParams.put('isAlertCreate',true);

ruleParams.put('usageLevel',ACCOUNTMONTHLY);

ruleParams.put('criteriaList','{})';

JBCXM.CEHandler.runRule(ruleParams);
