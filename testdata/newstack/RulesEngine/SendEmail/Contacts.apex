Delete[Select Id from Contact];

Account A = [select Id from Account where Name like 'Abacus Programming Corporation'];
List<Contact> ccList = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='SriVidya' +i,LastName='r' +i,Email='srallapalli@gainsight.com');
   ccList.add(acc);
    acc.AccountId=A.Id;
    
}
insert ccList;


Account AA = [select Id from Account where Name like 'AAR Corp Hardware Abscoa Division'];
List<Contact> ccList1 = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='Sri ' +i,LastName='Vidya' +i,Email='srallapalli@gainsight.com');
   ccList1.add(acc);
    acc.AccountId=AA.Id;
    
}
insert ccList1;

Account AAA = [select Id from Account where Name like 'Abberley Kooiman'];
List<Contact> ccList2 = new List<Contact>();
for(integer i=1; i<2 ; i++) {
   Contact acc = new Contact(FirstName ='Srividya' +i,LastName='Rallapalli' +i,Email='srallapalli@gainsight.com');
   ccList2.add(acc);
    acc.AccountId=AAA.Id;
    
}
insert ccList2;



