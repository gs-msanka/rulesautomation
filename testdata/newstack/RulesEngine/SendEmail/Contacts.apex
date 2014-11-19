Delete[Select Id from Contact];

Account A = [select Id from Account where Name like 'Abacus Programming Corporation'];
List<Contact> ccList = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='Vivek' +i,LastName='g' +i,Email='gvivek@gainsight.com');
   ccList.add(acc);
    acc.AccountId=A.Id;
    
}
insert ccList;


Account AA = [select Id from Account where Name like 'AAR Corp Hardware Abscoa Division'];
List<Contact> ccList1 = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='Pawel' +i,LastName='Kumar' +i,Email='pdalal@gainsight.com');
   ccList1.add(acc);
    acc.AccountId=AA.Id;
    
}
insert ccList1;

Account AAA = [select Id from Account where Name like 'Abberley Kooiman'];
List<Contact> ccList2 = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='Parthiban' +i,LastName='Parthiban Periasami' +i,Email='pparthiban@gainsight.com');
   ccList2.add(acc);
    acc.AccountId=AAA.Id;
    
}
insert ccList2;



