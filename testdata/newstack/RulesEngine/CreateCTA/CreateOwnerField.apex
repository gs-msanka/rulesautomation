//Why this script file: After Running this file,we will have at least one Active User other than the User who created the Account 'Create CTA No Adv Criteria'

String UserId=null;
List<User> uList=[SELECT Id, Name, ProfileId, IsActive FROM User where ProfileId in (SELECT Id FROM Profile where Name like '%System Admin%')
and Id not in (SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria')];   

User active = null;
if(uList.size()>0){
    for(User u : uList){
        
        if(u.IsActive){  //Checking for Active Users
            UserId= u.Id;
			active=[Select Id from User where Id =: UserId];
            break;
        }
       
    }
    if(UserId == null){   //we have Inactive Users
        UserId=uList[0].Id;        
        active=[Select Id from User where Id =: UserId];
        active.IsActive=true;
        update active;        
        }
	//System.debug([Select Name from User where Id =:UserId]);
  }
else{
    String uname='User_'+UserInfo.getOrganizationId()+'@gg.com';
	List<User> test=[select id from User where Username=:uname and isActive=true];
	if(test.size()==0){
	List<User> lsUser = new List<User>();
	active=new User();       
	active.Username= uname;
	active.Email = 'DefaultUser312@company.com';
	active.Lastname = 'User';
	active.Firstname = 'Defaultxamngh';
	active.Alias = 'smngh';
	active.CommunityNickname = 'Defaultxasmngh';
	Profile PF = [ SELECT Id FROM Profile where Name like '%System Admin%'];
	active.ProfileId = PF.Id;
	active.TimeZoneSidKey = 'GMT';
	active.LocaleSidKey = 'en_US';
	active.EmailEncodingKey = 'ISO-8859-1';
	active.LanguageLocaleKey = 'en_US';
	active.UserPermissionsMobileUser = false;
	lsUser.add(active);
	insert lsUser;      
	}
}

//Assigning this Active User for "C_Reference__c" lookup field in Account Object.
Account Acc=[Select id from Account where Name like 'Create CTA No Adv Criteria'];
Acc.C_Reference__c=active.Id;
update Acc;




