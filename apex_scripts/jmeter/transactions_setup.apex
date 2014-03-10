List<Account> accTBD =[Select id from Account where name like 'Headless Test%'];
if(!accTBD.isEmpty()){
	delete accTBD;
}

List<Account> accList = new List<Account>();
for(integer i=1; i<21 ; i++) {
   Account acc = new Account(Name ='Headless Test ' +i);
   accList.add(acc);
}
insert accList;

List<JBCXM__PickList__c> pkList = [SELECT id FROM JBCXM__PickList__c WHERE JBCXM__Category__c = 'Customer Status' AND
                                 JBCXM__SystemName__c = 'ActiveCustomer'];
List<JBCXM__CustomerInfo__c> custList = new List<JBCXM__CustomerInfo__c>();
JBCXM__CustomerInfo__c cust = null;
for(Account acc : [SELECT id FROM Account where name like 'Headless Test%']) {
            cust = new JBCXM__CustomerInfo__c(JBCXM__Account__c         = acc.id,
                                         JBCXM__Status__c                 = (pkList.get(0)).id);
   custList.add(cust);
   
}
insert custList;

    
    //Creating custom booking types - CustomUpsell,CustomDownsell and CustomDebook if they do not exist
    
    List<JBCXM__PickList__c> custom=[select Id from JBCXM__PickList__c where JBCXM__SystemName__c in ('CustomUpsell','CustomDownsell','CustomDebook')];
    if(custom.size()!=3){
    						List<JBCXM__PickList__c> pList=new List<JBCXM__PickList__c>();
    						List<String>  transCustTypes=new List<String>{'CustomUpsell','CustomDownsell','CustomDebook'};
    						List<String> colors=new List<String>{'#ffff00','#ff9900','#3333ff'};
    						List<JBCXM__PickList__c> d = [select JBCXM__DisplayOrder__c from JBCXM__PickList__c where JBCXM__Category__c='Order Type' order by JBCXM__DisplayOrder__c desc];
    						System.debug(d.get(0).JBCXM__DisplayOrder__c);
    						Integer disp=(Integer)d.get(0).JBCXM__DisplayOrder__c;
    						List<String> bType=new List<String>{'Upsell','Downsell','Debook'};
    						for(Integer i=0;i<transCustTypes.size();i++)
    						{
    
    								pList.add(new JBCXM__PickList__c(Name=transCustTypes.get(i),JBCXM__Category__c='Order Type',JBCXM__SystemName__c=transCustTypes.get(i),JBCXM__ShortName__c=transCustTypes.get(i),JBCXM__Attribute1__c=colors.get(i),JBCXM__Active__c=true,JBCXM__DisplayOrder__c=disp++,JBCXM__MapToBookingType__c=bType.get(i)));
    						}
    						insert pList;
    
    
    						//Map transaction types to the above created custom types
    						List<JBCXM__TransactionType__c> tTypes=[select Id from JBCXM__TransactionType__c where Name in ('Subscription','Users','Services')];
    						List<JBCXM__PickList__c> cu=[select Id from JBCXM__PickList__c where JBCXM__SystemName__c in ('CustomUpsell','CustomDownsell','CustomDebook') and JBCXM__Category__c='Order Type'];
    						List<JBCXM__OrderTransactionMap__c> orderMap=new List<JBCXM__OrderTransactionMap__c>();
    						for(Integer i=0;i<cu.size();i++)
    						{
    						 for (Integer j=0;j<tTypes.size();j++)
    						 {
    						   orderMap.add(new JBCXM__OrderTransactionMap__c(JBCXM__Active__c=true,JBCXM__DisplayOrder__c=j+1,JBCXM__OrderType__c=cu.get(i).Id,JBCXM__TransactionType__c=tTypes.get(j).Id));
    						  }
    						}
    						insert orderMap;
    }
