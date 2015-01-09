Account Acc= [ Select Id from Account where Name like 'Create CTA No Adv Criteria'];

Acc.C_Text__c= 'Sample Text';
Acc.C_Number__c= 1234;
Acc.C_Checkbox__c=true;
Acc.C_Currency__c=10;
Acc.C_Email__c='pparthiban@gainsight.com';
Acc.C_Percent__c=10;
Acc.C_Phone__c='044-23452';
Acc.C_Picklist__c='Pvalue1';
Acc.C_MultiPicklist__c = 'MPvalue1;MPvalue2';                         
Acc.C_TextArea__c = 'This is Text Area';
Acc.C_EncryptedString__c = 'Encrypted123456';
Acc.C_URL__c = 'http://www.google.com';

Update Acc;

