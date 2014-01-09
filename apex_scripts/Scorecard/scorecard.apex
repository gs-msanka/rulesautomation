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
JBCXM__ScorecardConfig__c enable_sc=[SELECT JBCXM__ScorecardEnabled__c,JBCXM__CustomerRollup__c FROM JBCXM__ScorecardConfig__c];
enable_sc.JBCXM__ScorecardEnabled__c=true;
enable_sc.JBCXM__CustomerRollup__c='WEIGHT';
update(enable_sc);

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
delete [select Name from JBCXM__ScorecardMetric__c];
List<JBCXM__ScorecardMetric__c> smList_Groups = new List<JBCXM__ScorecardMetric__c>();
List<JBCXM__ScoringScheme__c> scheme=[SELECT Name FROM JBCXM__ScoringScheme__c where Name like 'Numeric%'];
smList_Groups.add(new JBCXM__ScorecardMetric__c(Name='Group1',JBCXM__AllowOverride__c=true,JBCXM__DisplayOrder__c=1,JBCXM__HelpText__c='abcd',JBCXM__InputType__c='Manual',JBCXM__IsActive__c=true,JBCXM__IsGroup__c=true,JBCXM__ScoringSchemeId__c=scheme.get(0).Id));
smList_Groups.add(new JBCXM__ScorecardMetric__c(Name='Group2',JBCXM__AllowOverride__c=true,JBCXM__DisplayOrder__c=2,JBCXM__HelpText__c='abcd',JBCXM__InputType__c='Manual',JBCXM__IsActive__c=true,JBCXM__IsGroup__c=true,JBCXM__ScoringSchemeId__c=scheme.get(0).Id));
insert(smList_Groups);

//----------------------------------------------------------------------------------------------------------//

//Adding Measures under groups
List<JBCXM__ScorecardMetric__c> smList_Meas = new List<JBCXM__ScorecardMetric__c>();
List<JBCXM__ScorecardMetric__c> group1=[SELECT Id from JBCXM__ScorecardMetric__c where name like 'Group1'];
for(integer i=1;i<4;i++)
{
smList_Meas.add(new JBCXM__ScorecardMetric__c(Name='M'+i,JBCXM__GroupId__c=group1.get(0).Id,JBCXM__Weight__c=20.00,JBCXM__AllowOverride__c=true,JBCXM__PeriodType__c='WEEKS',JBCXM__DisplayOrder__c=i,JBCXM__HelpText__c='abcd',JBCXM__InputType__c='Manual',JBCXM__IsActive__c=true,JBCXM__IsGroup__c=false,JBCXM__ScoringSchemeId__c=scheme.get(0).Id));
}

List<JBCXM__ScorecardMetric__c> group2=[SELECT Id from JBCXM__ScorecardMetric__c where name like 'Group2'];
for(integer i=4;i<6;i++)
{
smList_Meas.add(new JBCXM__ScorecardMetric__c(Name='M'+i,JBCXM__GroupId__c=group2.get(0).Id,JBCXM__Weight__c=20.00,JBCXM__AllowOverride__c=true,JBCXM__PeriodType__c='WEEKS',JBCXM__DisplayOrder__c=i,JBCXM__HelpText__c='abcd',JBCXM__InputType__c='Manual',JBCXM__IsActive__c=true,JBCXM__IsGroup__c=false,JBCXM__ScoringSchemeId__c=scheme.get(0).Id));
}
insert(smList_Meas);


//----------------------------------------------------------------------------------------------------------//