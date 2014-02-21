//Cleanup
delete [select id from JBCXM__SurveyParticipant__c where JBCXM__SurveyMaster__c in (select id from JBCXM__Survey__c where JBCXM__IsAnonymous__c=true and JBCXM__AnonymousType__c='Complete')];
delete[select id from JBCXM__Survey__c where JBCXM__IsAnonymous__c=true and JBCXM__AnonymousType__c='Complete'];
delete [select id from Account where Name like 'Gainsight Survey Account'];

//Design Survey
//Add Surveys - Anonymous without Account Tracking
//JBCXM__Survey__c

Account anonymousAcc=new Account(Name='Gainsight Survey Account');
insert anonymousAcc;

List<JBCXM__Survey__c> Surveys = new List<JBCXM__Survey__c>();
List<String> codes= new List<String> {'Customer Satisfaction'};
List<String> titles = new List<String> {'CSR'};
integer i=0;	
Surveys.add(new JBCXM__Survey__c ( JBCXM__Code__c = codes.get(i) ,
								   JBCXM__Title__c = titles.get(i) ,
			                       JBCXM__IsAnonymous__c=true,
								   JBCXM__AnonymousType__c='Complete',
								   JBCXM__AnonymousAccount__c=anonymousAcc.Id,
							       JBCXM__StartDate__c=system.Date.today(),
								   JBCXM__EndDate__c=system.Date.today().addMonths(1),
								   JBCXM__InternalSubmit__c=false,
									JBCXM__Description__c='Dear Customer:\n	As the manager of [COMPANY], I want to thank you for giving us the opportunity to serve you. Please help us serve you better by taking a couple of minutes to tell us about the service that you have received so far. We appreciate your business and want to make sure we meet your expectations. Attached, you will find a coupon good for ...... We hope that you will accept this as a token of our good will.\nSincerely,\nManager',
									JBCXM__ThankyouMessage__c='Thank you for your feedback. We sincerely appreciate your honest opinion and will take your input into consideration while providing products and services in the future.',
									JBCXM__ThankyouType__c='message',
									JBCXM__FooterMessge__c='If you have any comments or concerns about this survey please Contact: - [MANAGER_NAME],[phone]',
									JBCXM__Status__c = 'Design',
									SurMaster_ExternalID__c=codes.get(i)
								));
 insert Surveys;		
 
 //===========================================================================================================================================================//
 
 //Add Questions to Survey 
 //JBCXM__SurveyQuestion__c
 JBCXM__Survey__c sur=[select Id,SurMaster_ExternalID__c from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR'];
 List<JBCXM__SurveyQuestion__c>  sur1Questions=new List<JBCXM__SurveyQuestion__c>();
 List<String> Questions = new List<String> {'How often do you typically use the product?','How did our PRODUCT perform?','Please share with us a few things [Product/Service] could do better.','Based on your experience with [PRODUCT], how likely are you to buy [PRODUCT] again?','What day/time of the day would you prefer our representatives to contact you?','Have you ever contacted customer service?','Did our representative... (Select all that apply)','What did we do really well?','What can we do to be even better?','What Services offered by us , impressed you the most?','Please Rank the Services provided by us :','Please Select the features you use or would like to use in our product','How likely are you to recommend our Product to others?'};

List<String> QTypes =new List<String>{'Radio', 'MatrixSingleAnswer','Comment','SingleSelect','Checkbox','Radio','MultiSelect','Text','Text','Checkbox','Ranking','MatrixMultipleAnswers','NPS'} ;

  for( Integer qNo=0;qNo<Questions.size();qNo++)
 {
			if(!QTypes.get(qNo).equals('NPS'))			
					{
						sur1Questions.add(new JBCXM__SurveyQuestion__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__IsActive__c=true,JBCXM__IsRequired__c=false,JBCXM__Title__c=Questions.get(qNo),JBCXM__DisplayOrder__c=qNo+1,JBCXM__Type__c=QTypes.get(qNo),SurQues_ExternalID__c=sur.SurMaster_ExternalID__c+'_'+QTypes.get(qNo)+(qNo+1)));
					}
			else
			{
					sur1Questions.add(new JBCXM__SurveyQuestion__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__IsActive__c=true,JBCXM__IsRequired__c=true,JBCXM__Title__c=Questions.get(qNo),JBCXM__DisplayOrder__c=qNo+1,JBCXM__Type__c=QTypes.get(qNo),JBCXM__AllowComment__c=true,JBCXM__AllowHeaderGrouping__c=true,JBCXM__CommentLabel__c='Please tell us why?',JBCXM__GroupHeaderType__c='Text',SurQues_ExternalID__c=sur.SurMaster_ExternalID__c+'_'+QTypes.get(qNo)+(qNo+1)));
		    }
			
}
insert sur1Questions;

//Sub Questions for questions of Matrix type

List<JBCXM__SurveyQuestion__c>  surSubQuestions=new List<JBCXM__SurveyQuestion__c>();
JBCXM__SurveyQuestion__c MatrixSingleSelect =[select Name,JBCXM__DisplayOrder__c,JBCXM__Type__c from JBCXM__SurveyQuestion__c where JBCXM__Type__c='MatrixSingleAnswer' and 	JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c like 'Customer Satisfaction' and JBCXM__Title__c='CSR')];
JBCXM__SurveyQuestion__c MatrixMultiSelect =[select Name,JBCXM__DisplayOrder__c,JBCXM__Type__c from JBCXM__SurveyQuestion__c where JBCXM__Type__c='MatrixMultipleAnswers' and 	JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c like 'Customer Satisfaction' and JBCXM__Title__c='CSR')];
List<String> mSingle = new List<String>{'Overall quality','Value','Purchase experience','Installation or first use experience','Usage experience','After purchase service (warranty,repair,customer service etc)'};
List<String> mMultiple= new List<String>{'Engage','Promote'};
Integer qNo=0;
for(String ms : mSingle)
{
   surSubQuestions.add(new JBCXM__SurveyQuestion__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__IsActive__c=true,JBCXM__IsRequired__c=true,JBCXM__Title__c=ms,JBCXM__DisplayOrder__c=qNo+1,JBCXM__Type__c='Radio',JBCXM__ParentQuestion__c=MatrixSingleSelect.Id,SurQues_ExternalID__c=sur.SurMaster_ExternalID__c+'_'+MatrixSingleSelect.JBCXM__Type__c+MatrixSingleSelect.JBCXM__DisplayOrder__c+'_Radio'+(qNo+1)));
   qNo++;
 }

 qNo=0;
 for(String mm : mMultiple)
 {
		surSubQuestions.add(new JBCXM__SurveyQuestion__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__IsActive__c=true,JBCXM__IsRequired__c=true,JBCXM__Title__c=mm,JBCXM__DisplayOrder__c=qNo+1,JBCXM__Type__c='Checkbox',JBCXM__ParentQuestion__c=MatrixMultiSelect.Id,SurQues_ExternalID__c=sur.SurMaster_ExternalID__c+'_'+MatrixMultiSelect.JBCXM__Type__c+MatrixMultiSelect.JBCXM__DisplayOrder__c+'_Checkbox'+(qNo+1)));
		qNo++;
 }
insert surSubQuestions;

//=======================================================================================================================================================//

//Inserting Allowed Answers
//JBCXM__SurveyAllowedAnswers__c

List<JBCXM__SurveyAllowedAnswers__c>  answersList= new List<JBCXM__SurveyAllowedAnswers__c>();
Integer ansOrder=0;
List<JBCXM__SurveyQuestion__c> que=[select Id,Name,JBCXM__Type__c,SurQues_ExternalID__c from JBCXM__SurveyQuestion__c where JBCXM__ParentQuestion__c=null and 	JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR')  order by JBCXM__DisplayOrder__c];
Map<JBCXM__SurveyQuestion__c,List<String>> qAndaMap = new Map<JBCXM__SurveyQuestion__c,List<String>>();
List<List<String>> answers=new List<List<String>> {new List<String>{'Daily','Weekly Once','Monthly Once','Once in 2-3 Months','Never'},
new List<String>{'Miserably','Somewhat Satisfactory','Very Satisfactory','Delightfully'},
new List<String>{''},
new List<String>{'Definitely will','Probably will','Might or might not','Probably will not','Definitely will not'},
new List<String>{'Weekdays','Weekends','Morning (6-10 AM)','Mid-day( 11- 4 PM)','Evenings (4-9 PM)','Any time of the day on Weekdays','Any time of the day on Weekends'},
new List<String>{'Yes','No'},
new List<String>{'Quickly identify the problem','Appear knowledgeable and competent','Help you understand the cause and the solution to the problem','Handle issues with courtesy and professionalism'},
new List<String>{''},
new List<String>{''},
new List<String>{'Sales','Installation','Services','Customer Service Desk','All Services are very good','Liked none of our Services'},
new List<String>{'Sales','Pre-Sales','Services','Customer Service Desk'},
new List<String>{'CoreMotives','Email','Marketing Automation','Mobile Marketing','PlacePunch'},
new List<String>{'0','1','2','3','4','5','6','7','8','9'}};
Integer qm=0;
for(JBCXM__SurveyQuestion__c q : que)
{
      qAndaMap.put(q,answers.get(qm));
	  qm++;
}

for(Integer m=0;m<qAndaMap.size();m++)
{
   for(Integer an=0;an<qAndaMap.get(que.get(m)).size();an++)
   {
       if(!que.get(m).JBCXM__Type__c.equals('NPS'))
	   {
				answersList.add(new JBCXM__SurveyAllowedAnswers__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__SurveyQuestion__c=que.get(m).Id,JBCXM__Title__c=qAndaMap.get(que.get(m)).get(an),JBCXM__DisplayOrder__c=an+1,JBCXM__IsActive__c=true,JBCXM__UserResponseCount__c=0,SurAllowAns_ExternalID__c=que.get(m).SurQues_ExternalID__c+qAndaMap.get(que.get(m)).get(an)+'_ans'+(an+1)));
		}
		else
		{
			 if(an<4)
			{
				answersList.add(new JBCXM__SurveyAllowedAnswers__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__SurveyQuestion__c=que.get(m).Id,JBCXM__Title__c=qAndaMap.get(que.get(m)).get(an),JBCXM__DisplayOrder__c=an+1,JBCXM__IsActive__c=true,JBCXM__UserResponseCount__c=0,JBCXM__GroupLabel__c='Not likely at all',SurAllowAns_ExternalID__c=que.get(m).SurQues_ExternalID__c+qAndaMap.get(que.get(m)).get(an)+'_ans'+(an+1)));
			}
			else if(an>3 && an<8)
			{
				answersList.add(new JBCXM__SurveyAllowedAnswers__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__SurveyQuestion__c=que.get(m).Id,JBCXM__Title__c=qAndaMap.get(que.get(m)).get(an),JBCXM__DisplayOrder__c=an+1,JBCXM__IsActive__c=true,JBCXM__UserResponseCount__c=0,JBCXM__GroupLabel__c='Neutral',SurAllowAns_ExternalID__c=que.get(m).SurQues_ExternalID__c+qAndaMap.get(que.get(m)).get(an)+'_ans'+(an+1)));
			}
			else
			{
				answersList.add(new JBCXM__SurveyAllowedAnswers__c(JBCXM__SurveyMaster__c=sur.Id,JBCXM__SurveyQuestion__c=que.get(m).Id,JBCXM__Title__c=qAndaMap.get(que.get(m)).get(an),JBCXM__DisplayOrder__c=an+1,JBCXM__IsActive__c=true,JBCXM__UserResponseCount__c=0,JBCXM__GroupLabel__c='Extremely likely',SurAllowAns_ExternalID__c=que.get(m).SurQues_ExternalID__c+qAndaMap.get(que.get(m)).get(an)+'_ans'+(an+1)));
			}
		}
   }
}

insert answersList;

//=======================================================================================================================================================//


//Adding Logic Rules
//JBCXM__SurveyLogicRules__c

JBCXM__SurveyQuestion__c ques=[select Name,SurQues_ExternalID__c,JBCXM__HasRules__c from JBCXM__SurveyQuestion__c where JBCXM__ParentQuestion__c=null and JBCXM__DisplayOrder__c=4 and JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR')  ];

JBCXM__SurveyQuestion__c NextQues=[select Name,SurQues_ExternalID__c from JBCXM__SurveyQuestion__c where JBCXM__ParentQuestion__c=null and JBCXM__DisplayOrder__c=5 and JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR')  ];

JBCXM__SurveyAllowedAnswers__c ans=[select Name from JBCXM__SurveyAllowedAnswers__c where JBCXM__Title__c='Definitely will' and JBCXM__DisplayOrder__c=1 and JBCXM__SurveyQuestion__c in (select Id from JBCXM__SurveyQuestion__c where JBCXM__ParentQuestion__c=null and JBCXM__DisplayOrder__c=4 ) and JBCXM__SurveyMaster__c in (select Id from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR')];

JBCXM__SurveyLogicRules__c  lr = new JBCXM__SurveyLogicRules__c(JBCXM__Answer__c=ans.Id,JBCXM__DependentQuestion__c=NextQues.Id,JBCXM__LogicType__c='Ask',JBCXM__SurveyMaster__c=sur.Id,JBCXM__SurveyQuestion__c=ques.Id,surLogicRules_ExternalID__c=ques.SurQues_ExternalID__c+'_Ask_'+NextQues.SurQues_ExternalID__c);

insert lr;

ques.JBCXM__HasRules__c=true;
update ques;
//=======================================================================================================================================================//

//Publish Survey
//updating publish URL and email template
//Goto Setup->Develop->Sites and create Domain before giving that in this script...here http://gainsight-developer-edition.na15.force.com/ is the domain i registered

 JBCXM__Survey__c publishSurvey=[select Id from JBCXM__Survey__c where JBCXM__Code__c='Customer Satisfaction' and JBCXM__Title__c='CSR'];
EmailTemplate email=[select Id, Name, FolderId, Folder.Name from EmailTemplate where Name ='JBara Anonymous Survey without Account Tracking'];
 publishSurvey.JBCXM__PublishedURL__c='http://gainsight-developer-edition.na15.force.com/JBCXM__SurveyResponse';
 publishSurvey.JBCXM__EmailTemplate__c=email.Id;
 publishSurvey.JBCXM__EmailOpenURL__c='http://gainsight-developer-edition.na15.force.com/JBCXM__SurveyEmailOpen';
publishSurvey.JBCXM__Status__c = 'Publish';
 publishSurvey.JBCXM__PublishDate__c=system.Date.today();

 
 update publishSurvey;
 
 
 //=======================================================================================================================================================//
 
 //Distribute Survey
 //Loading contacts into contacts object for Survey Participants
  
// List<Account> accList = new List<Account>();
//for(integer a=1; a<3 ; a++) {
//   Account acc = new Account(Name ='Anonymous Survey Account  ' +a);
//   accList.add(acc);
//}
//insert accList;
// 
//List<Account> SurveyAcc = [SELECT ID FROM Account where name like 'Anonymous Survey%'];
//List<Contact> contactList = new List<Contact>();
//List<String> emails  = new List<String>{'hitesh@release41.com', 'rajesh@release41.com','srividya@release41.com','giri@release41.com'};
//List<String> names = new List<String>{'Hitesh', 'Rajesh','Vidya','Giri'};
//Contact con = null;
//for(Account acc : SurveyAcc) {
//    for(Integer j=0;j<emails.size();j++)
//    {
//		con = new Contact(AccountId = acc.Id,LastName = names.get(j),Email = emails.get(j));
//        contactList.add(con);
//	}
//}
//
//insert contactList;