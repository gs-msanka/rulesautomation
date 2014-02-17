delete [select JBCXM__Feature__c from JBCXM__Features__c];

Map<String,List<String>> productNFeatureMAp=new Map<String,List<String>>{'Engage'=>new List<String>{'CoreMotives','Email','Marketing Automation','PlacePunch','Business Assets'},
																																								'Financial Management'=>new List<String>{'Core Financials','Expenses','Revenue Management'},
																																								'Health Care Mgmt Suite'=>new List<String>{'Health Plans','Mobile','Pharmacy','Rewards'},
																																								'HME Software'=>new List<String>{'Billing/Bus Mgmt','Delivery Management','Document Management','Patient Contact','Secure Mobility'},
																																								'Human Capital Management'=>new List<String>{'Human Resource Management','Payroll Solutions','Recruiting','Talent Management'},
																																								'Integration Packages'=>new List<String>{'SAP','Siebel CRM'},
																																								'Learning Cloud'=>new List<String>{'Compliance & Certification Management','Data Sync','Learning Management','Social Collaboration'},
																																								'Recruiting Cloud'=>new List<String>{'Recruitment Management','Screening & Assessment','Sourcing','Talent Pipeline'}};
List<String> products=new List<String>{'Engage','Financial Management','Health Care Mgmt Suite','HME Software','Human Capital Management','Integration Packages','Learning Cloud','Recruiting Cloud'};

List<JBCXM__Features__c> features = new List<JBCXM__Features__c>();

for (Integer p=0;p<products.size();p++)
{
	for(Integer f=0;f<productNFeatureMAp.get(products.get(p)).size();f++)
	{
    	features.add(new JBCXM__Features__c(JBCXM__Product__c=products.get(p),JBCXM__Feature__c =productNFeatureMAp.get(products.get(p)).get(f),JBCXM__SystemName__c='P'+p+'F'+f));
	}
}
insert features;

delete [select JBCXM__Features__c from JBCXM__CustomerFeatures__c];
List<JBCXM__CustomerFeatures__c> CustFeat = new List<JBCXM__CustomerFeatures__c>();

Account accList=[SELECT id FROM Account where name like 'Via Systems'];

List<List<String>> pre=new List<List<String>>{new List<String>{'Financial Management','Core Financials','Yes','No','This is a Premium Feature'},
																			 new List<String>{'Financial Management','Revenue Management','Yes','Yes','This is a free feature'},
																			 new List<String>{'Health Care Mgmt Suite','Rewards','Yes','Yes','This is a Premium Feature'},
																			 new List<String>{'HME Software','Delivery Management','Yes','No','This is a Premium Feature'}};

for(Integer cf=0;cf<pre.size();cf++)
{
    System.debug(pre[cf][4]);
    String query='SELECT Id FROM JBCXM__Features__c where JBCXM__Product__c=\''+pre[cf][0]+'\' and JBCXM__Feature__c=\''+pre[cf][1]+'\'';
 	JBCXM__Features__c feat =database.query(query);
    CustFeat.add(new JBCXM__CustomerFeatures__c(JBCXM__Account__c= accList.Id,
                                                JBCXM__Enabled__c=pre[cf][3].equals('Yes')?true:false,
                                                JBCXM__Features__c=feat.Id,
                                                JBCXM__Licensed__c=pre[cf][2].equals('Yes')?true:false,
                                                JBCXM__Comment__c=pre[cf][4]                                   	                                                
                                               ));
}
insert CustFeat;
