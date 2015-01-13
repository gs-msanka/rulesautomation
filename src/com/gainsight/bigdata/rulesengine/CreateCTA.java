package com.gainsight.bigdata.rulesengine;

import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class CreateCTA extends NSTestBase{
		
	private static final String rulesDir = Application.basedir+"/testdata/newstack/RulesEngine/CreateCTA/";
    private static final String CreateCTACustomer = rulesDir + "CreateCTACustomer.apex";
    private static final String AAR_CreateConfig_StringFilter = rulesDir + "AAR_CreateConfig_StringFilter.apex";
    private static final String AAR_CreateConfig_StringFilter_LookupShow = rulesDir + "AAR_CreateConfig_StringFilter_LookupShow.apex";
    private static final String AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField.apex";
    private static final String CreateOwnerField = rulesDir + "CreateOwnerField.apex";
    private static final String AssignValuesToStandardFields = rulesDir + "AssignValuesToStandardFields.apex";
    private static final String AssignValuesToCustomFields = rulesDir + "AssignValuesToCustomFields.apex";
    private static final String AAR_CreateConfig_StringFilter_StandardFieldsShow = rulesDir + "AAR_CreateConfig_StringFilter_StandardFieldsShow.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbYesStandardTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbYesStandardTokenNoOwnerField.apex";
    private static final String AAR_CreateConfig_StringFilter_CustomFieldsShow = rulesDir + "AAR_CreateConfig_StringFilter_CustomFieldsShow.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbYesCustomTokenYesOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbYesCustomTokenYesOwnerField.apex";    	
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";
    ResponseObj result=null;
    BaseTest bt=new BaseTest();
	
	@BeforeClass
    public void beforeClass() throws Exception {
        LastRunResultFieldName = GSUtil.resolveStrNameSpace(LastRunResultFieldName);		
    }	
	
	/*@Test
	public void dummy(){
		try {
			System.out.println(header.toString());
		    ResponseObj result=	wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" +"a069000000doXxhAAE", header.getAllHeaders(),
		         "{}");
		    System.out.println(result.toString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
	}*/
	
	//Create CTA : No Advance Criteria, No Playbook, No Token, No Owner Field.
	@Test
	public void NoAdvCriteriaNoPbNoTokenNoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null;
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AAR_CreateConfig_StringFilter));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField));
                
        SObject[] CTAreq = sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'"));
               
        for (SObject r : CTAreq) {
            String rawBody = ("{}");            
            result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);
            Log.info("Rule ID:" + r.getId()); 
            Log.info("Request URL"+PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId());    
            Log.info("Request rawBody:"+rawBody);            
            
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);            
            
            SObject[] LRR = sfdc.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'"));
        
            for (SObject obj : LRR) {
            	//Log.info(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = sfdc.getRecords(resolveStrNameSpace("Select Name from JBCXM__CTA__c"));            		
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance
            
            SObject[] P_Priority = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"));
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"));
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = sfdc.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"));
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'"));
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"));
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());            	
            }
        }   
  
	
	//Create CTA : No Advance Criteria, Yes Playbook, No Token, No Owner Field.
	@Test
	public void NoAdvCriteriaYesPbNoTokenNoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null;

		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AAR_CreateConfig_StringFilter));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField));
                
        SObject[] CTAreq = sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'"));
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = sfdc.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'"));
        
            for (SObject obj : LRR) {
            	//Log.info(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = sfdc.getRecords(resolveStrNameSpace("Select Name from JBCXM__CTA__c"));
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage"
            
            SObject[] P_Priority = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"));
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"));
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = sfdc.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"));
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'"));
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"));
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"));
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());
            	Assert.assertEquals(Playbook, obj.getChild("JBCXM__Playbook__c").getValue().toString());
            }
        }
	
	
	//Create CTA : No Advance Criteria, Yes Playbook, No Token, Yes Owner Field.
	@Test
	public void NoAdvCriteriaYesPbNoTokenYesOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null;
		String ReferenceTo="User";  //Reference to User Object
		String ReleationShipName="Acco2untS_AutomationS"; //Relation Name
		String LookupFieldName[]={"C_Reference"} , Reference[]={ReferenceTo,ReleationShipName};
			
		metadataClient.deleteFields("Account",LookupFieldName);
		metadataClient.createLookupField("Account", LookupFieldName, Reference);
		
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateOwnerField));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AAR_CreateConfig_StringFilter_LookupShow));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField));
        
        SObject[] CTAreq = sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'"));        
        
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);           
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = sfdc.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'"));
        
            for (SObject obj : LRR) {
            	//Log.info(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = sfdc.getRecords(resolveStrNameSpace("Select Name from JBCXM__CTA__c"));
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage"
            SObject[] P_Priority = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"));
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"));
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = sfdc.getRecords(resolveStrNameSpace("SELECT C_Reference__c FROM Account where Name like 'Create CTA No Adv Criteria'"));
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("C_Reference__c").getValue().toString();
            }
            SObject[] P_Type = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'"));
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"));
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"));
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());
            	Assert.assertEquals(Playbook, obj.getChild("JBCXM__Playbook__c").getValue().toString());
            }
        }
	
	
	//Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard Object), No Owner Field. 
	//Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, OwnerId. (Token are only Standard fields from Account Object are taken here)
	@Test
	public void NoAdvCriteriaYesPbYesToken_StandardField_NoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment=null,Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null;
		String Id = null, Name = null, AccType = null, Fax = null, Website = null, AnnualRevenue = null, NumberOfEmployees = null, Description = null, OwnerId = null,OwnerName = null;
		
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
		//Assign value to standard fields(Description,fax,Type,Annual Revenue,Employees,website) in Account Object
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AssignValuesToStandardFields));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AAR_CreateConfig_StringFilter_StandardFieldsShow));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(AT_CreateConfigNoAdvCriteriaYesPbYesStandardTokenNoOwnerField));
        
        SObject[] CTAreq = sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'"));
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = sfdc.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'"));
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = sfdc.getRecords(resolveStrNameSpace("Select Name from JBCXM__CTA__c"));
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage and Comment as initialized below"
            SObject[] Query1 = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, CreatedDate, OwnerId FROM Account where Name like '"+RuleName+"'"));
            
            for (SObject obj : Query1) {
           	Id=obj.getChild("Id").getValue().toString();
           	Name=obj.getChild("Name").getValue().toString();
           	AccType=obj.getChild("Type").getValue().toString();
           	Fax=obj.getChild("Fax").getValue().toString();
           	Website=obj.getChild("Website").getValue().toString();
           	AnnualRevenue=obj.getChild("AnnualRevenue").getValue().toString();
           	NumberOfEmployees=obj.getChild("NumberOfEmployees").getValue().toString();
           	Description=obj.getChild("Description").getValue().toString();
           	OwnerId=obj.getChild("OwnerId").getValue().toString();
           }
            
            SObject[] Query2 = sfdc.getRecords(resolveStrNameSpace("SELECT Name from User where Id ='"+OwnerId+"'"));
            for (SObject obj : Query2) {
            	OwnerName=obj.getChild("Name").getValue().toString();
           }
            Comment="  This CTA is assigned to AccountID "+Id+" Account Name:"+Name + Description + Fax + AccType + AnnualRevenue + NumberOfEmployees+" NA "+Website + OwnerId + OwnerName;
            Comment=Comment.replaceAll(" ", "");
            
            SObject[] P_Priority = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"));
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"));
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = sfdc.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"));
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'"));
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"));
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"));
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Log.info(Comment);
            	Log.info(obj.getChild("JBCXM__Comments__c").getValue().toString().trim());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString().replaceAll(" ", ""));
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());
            	Assert.assertEquals(Playbook, obj.getChild("JBCXM__Playbook__c").getValue().toString());
            }
        }
	
	
	//Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard Object), Yes Owner Field. 
	//Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, OwnerId. (Token are only Standard fields from Account Object are taken here)
	@Test
	public void NoAdvCriteriaYesPbYesToken_CustomField_YesOwnerField() throws Exception {

		String RuleName="Create CTA No Adv Criteria",Comment=null,Priority = null,Status= null,Type= null,Reason= null,Playbook= null; //Declaring fields to use in CTA		
		String Id = null, C_Text = null, C_Number = null, C_Checkbox = null, C_Currency = null, C_Email = null, C_Percent = null, C_Phone = null, C_Picklist = null, C_MultiPicklist = null,C_TextArea = null,C_EncryptedString=null,C_URL=null,C_Reference=null;//Declaring fields to use in Account Object
		
		String ReferenceTo="User";  //Reference to User Object
		String ReleationShipName="Acco2untS_AutomationS"; //Relation Name
		C_Reference="C_Reference";
		String LookupFieldName[]={C_Reference} , Reference[]={ReferenceTo,ReleationShipName};
		
		//Custom Fields to Delete from Account Object
		String FieldsToDelete1[]={"C_Text","C_Number","C_Checkbox","C_Currency","C_Email"};
		String FieldsToDelete2[]={"C_Percent","C_Phone","C_Picklist","C_MultiPicklist","C_TextArea"};
		String FieldsToDelete3[]={"C_EncryptedString","C_URL","C_Reference"};		
				
		String TextField[]={"C_Text"} , NumberField[]={"C_Number"} , Checkbox[]={"C_Checkbox"} , Currency[]={"C_Currency"} , Email[]={"C_Email"} , Percent[]={"C_Percent"} ,  Phone[]={"C_Phone"} , Picklist_FieldName="C_Picklist" , 
				Picklist_Values[]={"Pvalue1","Pvalue2","Pvalue3"} , MultiPicklist_FieldName="C_MultiPicklist", MultiPicklist_Values[]={"MPvalue1","MPvalue2","MPvalue3"} , TextArea[]={"C_TextArea"} , EncryptedString[]={"C_EncryptedString"} , URL[]={"C_URL"};
		
		HashMap<String, String[]> pickListFields=new HashMap<String, String[]>();
		pickListFields.put(Picklist_FieldName, Picklist_Values);
		
		HashMap<String, String[]> MultipickListFields=new HashMap<String, String[]>();
		MultipickListFields.put(MultiPicklist_FieldName, MultiPicklist_Values);
		
		//Delete Custom Fields
		metadataClient.deleteFields("Account",FieldsToDelete1);
		metadataClient.deleteFields("Account",FieldsToDelete2);
		metadataClient.deleteFields("Account",FieldsToDelete3);	
		
		//Create Custom Fields
		metadataClient.createTextFields("Account", TextField, false, false, true, false, false);
		metadataClient.createTextFields("Account", TextField, false, false, true, false, false);
		metadataClient.createNumberField("Account", NumberField, false);
		metadataClient.createFields("Account", Checkbox, true, false, false);
		metadataClient.createCurrencyField("Account", Currency);  
		metadataClient.createEmailField("Account", Email);
		metadataClient.createNumberField("Account", Percent, true);
		metadataClient.createFields("Account", Phone, false, true, false);
		metadataClient.createPickListField("Account", pickListFields, false);
		metadataClient.createPickListField("Account", MultipickListFields, true);
		metadataClient.createTextFields("Account", TextArea, false, false, false, true, false); 
		metadataClient.createEncryptedTextFields("Account", EncryptedString); 
		metadataClient.createFields("Account", URL, false, false, true);
		metadataClient.createLookupField("Account", LookupFieldName, Reference);		
		
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateCTACustomer));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CreateOwnerField));//Value to C_Reference__c field is assigned here.
		
		//Assign value to Custom fields("C_Text__c","C_Number__c","C_Checkbox__c","C_Currency__c","C_Email__c","C_Percent__c","C_Phone__c","C_Picklist__c","C_MultiPicklist__c","C_TextArea__c","C_EncryptedString__c","C_URL__c") in Account Object
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AssignValuesToCustomFields));
		
		//Rule Config
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AAR_CreateConfig_StringFilter_CustomFieldsShow));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(AT_CreateConfigNoAdvCriteriaYesPbYesCustomTokenYesOwnerField));
        
        //Execute Rule
        SObject[] CTAreq = sfdc.getRecords(resolveStrNameSpace("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'"));
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            ResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), header.getAllHeaders(),
                    rawBody);
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = sfdc.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'"));
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = sfdc.getRecords(resolveStrNameSpace("Select Name from JBCXM__CTA__c"));
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Assigning Value to Comment which has all Token values
            SObject[] Query1 = sfdc.getRecords(resolveStrNameSpace("SELECT Id, C_Text__c, C_Number__c, C_Checkbox__c, C_Currency__c, C_Email__c, C_Percent__c, C_Phone__c, C_Picklist__c, C_MultiPicklist__c,C_TextArea__c,C_EncryptedString__c,C_URL__c,C_Reference__c FROM Account where Name like '"+RuleName+"'"));
   		 
            for (SObject obj : Query1) {
           	Id=obj.getChild("Id").getValue().toString();
           	C_Text=obj.getChild("C_Text__c").getValue().toString();
           	C_Number=obj.getChild("C_Number__c").getValue().toString();
           	C_Checkbox=obj.getChild("C_Checkbox__c").getValue().toString();
           	C_Currency=obj.getChild("C_Currency__c").getValue().toString();
           	C_Email=obj.getChild("C_Email__c").getValue().toString();
           	C_Percent=obj.getChild("C_Percent__c").getValue().toString();
           	C_Phone=obj.getChild("C_Phone__c").getValue().toString();
           	C_Picklist=obj.getChild("C_Picklist__c").getValue().toString();
           	C_MultiPicklist=obj.getChild("C_MultiPicklist__c").getValue().toString();
           	C_TextArea=obj.getChild("C_TextArea__c").getValue().toString();
           	C_EncryptedString=obj.getChild("C_EncryptedString__c").getValue().toString();
           	C_URL=obj.getChild("C_URL__c").getValue().toString();
           	C_Reference=obj.getChild("C_Reference__c").getValue().toString();
           }            
          
            Comment="  This CTA is assigned to AccountID "+Id+C_Checkbox+C_Currency+C_Email+C_EncryptedString+C_MultiPicklist+C_Number+C_Percent+C_Phone+C_Picklist+C_Reference+C_Text+C_TextArea+C_URL;
            Comment=Comment.replaceAll(" ", "");
            
          //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account.C_Reference__c,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage and Comment as initialized below"
            SObject[] P_Priority = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'")); //Priority
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'")); //Status
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Type = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'"));  //Type         
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'")); //Reason
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'")); //Playbook
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(C_Reference, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Log.info(Comment);
            	Log.info(obj.getChild("JBCXM__Comments__c").getValue().toString().trim());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString().replaceAll(" ", ""));
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());
            	Assert.assertEquals(Playbook, obj.getChild("JBCXM__Playbook__c").getValue().toString());
            }
        }
	
	
	@AfterClass
    public void afterClass() {
        
    }
	
}
