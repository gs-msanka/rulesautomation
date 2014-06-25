//Account Script.

delete [select id from Account where name like 'Rules Engine %'];

List<String> industryList = new List<String>{'Entertainment',  'Food & Beverage', 'Manufacturing', 'Telecommunications', 'Engineering', 'Communications'};
List<String> typeList = new List<String>{'Customer - Direct', 'Customer - Channel', 'Installation Partner', 'Prospect'};
List<Account> accList = new List<Account>();
Account acc = null;
integer j =0;
integer k =0;
for(integer i =1 ; i < 11 ; i ++) {
	acc = new Account(Name = 'Rules Engine Account' +i,industry = industryList.get(j),type = typeList.get(k),jigsaw='Rules Engine',Data_ExternalID__c='Rules Engine Account' +i,NumberOfEmployees=5000);
	j++;
	k++;
	if (j==industryList.size()) {
		j =0;
	}
	if(k==typeList.size()) {
		k =0;
	}
	accList.add(acc);
}

insert accList;

