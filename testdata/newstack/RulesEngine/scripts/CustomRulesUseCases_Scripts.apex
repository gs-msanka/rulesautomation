//Script contains all the test-setup needed for all the CustomRules Usecases.

//Rule 1 - We need some records in the AccountContactRole for this test case
List<AccountContactRole>	accContRole= new List<AccountContactRole>();
Account acc=[select id from Account where name like 'CustomRule Account 1' limit 1];
List<Contact> cts=new List<Contact>();
cts.add(new Contact(FirstName='Srividya',LastName=' R',AccountId=acc.Id,email='srallapalli@gainsight.com'));
cts.add(new Contact(FirstName='Srividya ',LastName=' Rallapalli',AccountId=acc.Id,email='srallapalli@gainsight.com'));
insert cts;

List<AccountContactRole> actsRole=new List<AccountContactRole>();
List<Contact> cts1=[select id from Contact where AccountId =: acc.Id];
for(Contact ct : cts1){
actsRole.add(new AccountContactRole(AccountId=acc.id,ContactId=ct.id,Role='Evaluator'));
}
insert actsRole;
