List<JBCXM__CustomerInfo__c> CInfo=[select JBCXM__CustomerName__c,CInfo_ExternalID__c from JBCXM__CustomerInfo__c];
for(JBCXM__CustomerInfo__c c : CInfo)
{
    c.CInfo_ExternalID__c=c.JBCXM__CustomerName__c;
}

update CInfo;