//CleanUp

List<Opportunity> OppList = new List<Opportunity>();
OppList = [Select Id,AccountId,Name from Opportunity where Opportunity.Account.Name='IVY Comptech'];
Delete OppList;

List<Case> CaseList = new List<Case>();
CaseList = [SELECT Id, AccountId FROM Case where Case.Account.Name='IVY Comptech'];
Delete CaseList;

List<Account> AccList = new List<Account>();
AccList = [Select Id from Account where Name = 'IVY Comptech'];
Delete AccList;

//Create Account

Account IVY = new Account(name='IVY Comptech');
insert(IVY);

//Create Contact for Created Account

List<Account> acc = [Select Id, Name from Account where Name = 'IVY Comptech'];
Contact con = new Contact();
con.FirstName = 'Parthiban';
con.LastName ='Periasami';
con.AccountId = acc[0].Id;
con.Title = 'Software QA';
con.Email='parthibansct@gmail.com';
insert con;

//Add Account as a Customer


JBCXM__CustomerInfo__c cs=new JBCXM__CustomerInfo__c();
cs.JBCXM__Account__c=acc[0].Id;
insert cs;