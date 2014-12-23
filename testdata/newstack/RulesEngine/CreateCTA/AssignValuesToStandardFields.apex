//Assign value to standard fields(Description,fax,Type,Annual Revenue,Employees,website) in Account Object

Account Acc=[select Id from Account where Name like 'Create CTA No Adv Criteria'];
Acc.Description='Description by Automation';
Acc.Fax='044-4542456';
Acc.Type='Prospect';
Acc.AnnualRevenue= 30;
Acc.NumberOfEmployees= 300;
Acc.Website='www.google.com';
Update Acc;