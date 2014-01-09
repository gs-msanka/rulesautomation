//ENABLING Color SCHEME
JBCXM__ScoringScheme__c cur_scheme_type=[select JBCXM__CustomerScheme__c from JBCXM__ScoringScheme__c where JBCXM__CustomerScheme__c=true];
if (cur_scheme_type==null){}
    else{
cur_scheme_type.JBCXM__CustomerScheme__c=false;
update(cur_scheme_type);
    }
JBCXM__ScoringScheme__c scheme_type=[select JBCXM__CustomerScheme__c from JBCXM__ScoringScheme__c where Name like 'Color%'];
scheme_type.JBCXM__CustomerScheme__c=true;
update(scheme_type);


List<JBCXM__ScoringScheme__c> scheme=[SELECT Name FROM JBCXM__ScoringScheme__c where Name like 'Color%'];
List<JBCXM__ScorecardMetric__c> smList_Groups = [select JBCXM__ScoringSchemeId__c from JBCXM__ScorecardMetric__c];
for(JBCXM__ScorecardMetric__c gr :smList_Groups)
    gr.JBCXM__ScoringSchemeId__c=scheme.get(0).Id;
update(smList_Groups);


