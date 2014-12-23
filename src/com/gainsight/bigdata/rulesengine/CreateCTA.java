package com.gainsight.bigdata.rulesengine;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.pageobject.core.Report;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.sfdc.util.metadata.CreateObjectAndFields;
import com.gainsight.webaction.WebAction;
import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ObjectUsage;
import com.sforce.soap.metadata.SharingModel;
import com.sforce.soap.partner.sobject.SObject;

public class CreateCTA {
	
	
	private static final String rulesDir = TestEnvironment.basedir + "/testdata/newstack/RulesEngine/CreateCTA/";
    private static final String CreateCTACustomer = rulesDir + "CreateCTACustomer.apex";
    private static final String AAR_CreateConfig_StringFilter = rulesDir + "AAR_CreateConfig_StringFilter.apex";
    private static final String AAR_CreateConfig_StringFilter_LookupShow = rulesDir + "AAR_CreateConfig_StringFilter_LookupShow.apex";
    private static final String AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField.apex";
    private static final String CreateOwnerField = rulesDir + "CreateOwnerField.apex";
    private static final String AssignValuesToStandardFields = rulesDir + "AssignValuesToStandardFields.apex";
    private static final String AAR_CreateConfig_StringFilter_StandardFieldsShow = rulesDir + "AAR_CreateConfig_StringFilter_StandardFieldsShow.apex";
    private static final String AT_CreateConfigNoAdvCriteriaYesPbYesTokenNoOwnerField = rulesDir + "AT_CreateConfigNoAdvCriteriaYesPbYesTokenNoOwnerField.apex";
       
    
    	
	static WebAction wa = new WebAction();
    public Header header = new Header();
    public String LastRunResultFieldName = "JBCXM__LastRunResult__c";

	
	@BeforeClass
    public void beforeClass() throws Exception {
        GSUtil.sfdcLogin(header, wa);
        LastRunResultFieldName = GSUtil.resolveStrNameSpace(LastRunResultFieldName);
    }
	
	@Test
	//Create CTA : No Advance Criteria, No Playbook, No Token, No Owner Field.
	public void NoAdvCriteriaNoPbNoTokenNoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null;
        GSUtil.runApexCode(CreateCTACustomer);
        GSUtil.runApexCode(AAR_CreateConfig_StringFilter);
        GSUtil.runApexCode(AT_CreateConfigNoAdvCriteriaNoPbNoTokenNoOwnerField);
        
        SObject[] CTAreq = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'");
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = GSUtil.execute("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = GSUtil.execute("Select Name from JBCXM__CTA__c");
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance
            
            SObject[] P_Priority = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"); //Priority
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"); //Status
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = GSUtil.execute("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"); //Assignee
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = GSUtil.execute("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'");  //Type         
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"); //Reason
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = GSUtil.execute("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c from JBCXM__CTA__c where Name = '"+RuleName+"'");
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());            	
            }
        }   
  
	@Test
	//Create CTA : No Advance Criteria, Yes Playbook, No Token, No Owner Field.
	public void NoAdvCriteriaYesPbNoTokenNoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null;
        GSUtil.runApexCode(CreateCTACustomer);
        GSUtil.runApexCode(AAR_CreateConfig_StringFilter);
        GSUtil.runApexCode(AT_CreateConfigNoAdvCriteriaYesPbNoTokenNoOwnerField);
        
        SObject[] CTAreq = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'");
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = GSUtil.execute("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = GSUtil.execute("Select Name from JBCXM__CTA__c");
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage"
            
            SObject[] P_Priority = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"); //Priority
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"); //Status
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = GSUtil.execute("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"); //Assignee
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = GSUtil.execute("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'");  //Type         
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"); //Reason
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = GSUtil.execute("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"); //Playbook
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = GSUtil.execute("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'");
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
	
	@Test
	//Create CTA : No Advance Criteria, Yes Playbook, No Token, Yes Owner Field.
	public void NoAdvCriteriaYesPbNoTokenYesOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment="Sample",Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null,OwnerField=null;
		String ReferenceTo="User";  //Reference to User Object
		String ReleationShipName="Acco2untS_AutomationS"; //Relation Name
		String LookupFieldName[]={"CSM_Automation"} , Reference[]={ReferenceTo,ReleationShipName};
		
		CreateObjectAndFields COAF= new CreateObjectAndFields();
		COAF.deletefields("Account",LookupFieldName);
		COAF.createLookupField("Account", LookupFieldName, Reference);
		
		GSUtil.runApexCode(CreateCTACustomer);		
        GSUtil.runApexCode(CreateOwnerField);
        GSUtil.runApexCode(AAR_CreateConfig_StringFilter_LookupShow);        
        GSUtil.runApexCode(AT_CreateConfigNoAdvCriteriaYesPbNoTokenYesOwnerField);
        
        SObject[] CTAreq = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'");
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = GSUtil.execute("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = GSUtil.execute("Select Name from JBCXM__CTA__c");
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage"
            
            SObject[] P_Priority = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"); //Priority
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"); //Status
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = GSUtil.execute("SELECT CSM_Automation__c FROM Account where Name like 'Create CTA No Adv Criteria'"); //Assignee is Owner Field. Not Default Owner.
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CSM_Automation__c").getValue().toString();
            }
            SObject[] P_Type = GSUtil.execute("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'");  //Type         
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"); //Reason
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = GSUtil.execute("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"); //Playbook
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = GSUtil.execute("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'");
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
	
	@Test
	//Create CTA : No Advance Criteria, Yes Playbook, Yes Token(Standard Object), Yes Owner Field. 
	//Tokens considered are: Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, OwnerId. (Token are only Standard fields from Account Object are taken here)
	public void NoAdvCriteriaYesPbYesTokenNoOwnerField() throws Exception {
		String RuleName="Create CTA No Adv Criteria",Comment=null,Priority = null,Status= null,Assignee= null,Type= null,Reason= null,Playbook= null,OwnerField=null;
		String Id = null, Name = null, AccType = null, Fax = null, Website = null, AnnualRevenue = null, NumberOfEmployees = null, Description = null, CreatedDate = null, OwnerId = null,OwnerName = null;
		GSUtil.runApexCode(CreateCTACustomer);
		
		//Assign value to standard fields(Description,fax,Type,Annual Revenue,Employees,website) in Account Object
		GSUtil.runApexCode(AssignValuesToStandardFields);
        GSUtil.runApexCode(AAR_CreateConfig_StringFilter_StandardFieldsShow);
        GSUtil.runApexCode(AT_CreateConfigNoAdvCriteriaYesPbYesTokenNoOwnerField);
        
        SObject[] CTAreq = GSUtil.execute("select Id,Name from JBCXM__AutomatedAlertRules__c where Name='"+RuleName+"'");
        for (SObject r : CTAreq) {
            String rawBody = ("{}");
            HttpResponseObj result = wa.doPost(PropertyReader.nsAppUrl + "/api/eventrule/" + r.getId(), rawBody,
                    header.getAllHeaders());
            ResponseObject responseObj = GSUtil.convertToObject(result
                    .getContent());
            Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
            Assert.assertNotNull(responseObj.getRequestId());
            GSUtil.waitForCompletion(r.getId(), wa, header);
            
            SObject[] LRR = GSUtil.execute("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Id='" + r.getId() + "'");
        
            for (SObject obj : LRR) {
            	//Report.logInfo(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertNotNull(obj.getChild(LastRunResultFieldName).getValue().toString());
                Assert.assertEquals("success", obj.getChild(LastRunResultFieldName).getValue().toString().toLowerCase());
            }
        }
            //Verify if CTA is Created.
            SObject[] NewCTA_Created = GSUtil.execute("Select Name from JBCXM__CTA__c");
            Assert.assertEquals(RuleName,NewCTA_Created[0].getChild("Name").getValue().toString());	
            
            //Verify if CTA created has Priority as HIGH, Status as Open, Assigned as same value of Account createdby,Type as Risk, Reason as Product Performance,Playbook as "Drop in Usage and Comment as initialized below"
            SObject[] Query1 = GSUtil.execute("SELECT Id, Name, Type, Fax, Website, AnnualRevenue, NumberOfEmployees, Description, CreatedDate, OwnerId FROM Account where Name like '"+RuleName+"'");
            
            for (SObject obj : Query1) {
           	Id=obj.getChild("Id").getValue().toString();
           	Name=obj.getChild("Name").getValue().toString();
           	AccType=obj.getChild("Type").getValue().toString();
           	Fax=obj.getChild("Fax").getValue().toString();
           	Website=obj.getChild("Website").getValue().toString();
           	AnnualRevenue=obj.getChild("AnnualRevenue").getValue().toString();
           	NumberOfEmployees=obj.getChild("NumberOfEmployees").getValue().toString();
           	Description=obj.getChild("Description").getValue().toString();
           	CreatedDate=obj.getChild("CreatedDate").getValue().toString();
           	OwnerId=obj.getChild("OwnerId").getValue().toString();
           }
            
            SObject[] Query2 = GSUtil.execute("SELECT Name from User where Id ='"+OwnerId+"'");
            for (SObject obj : Query2) {
            	OwnerName=obj.getChild("Name").getValue().toString();
           }
            Comment="This CTA is assigned to AccountID "+Id+" , Account Name:"+Name+" "+Description+" "+Fax+""+AccType+" "+AnnualRevenue+" "+NumberOfEmployees+" NA "+Website+""+OwnerId+" "+OwnerName;
            
            SObject[] P_Priority = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'High'"); //Priority
            for (SObject obj : P_Priority) {
            	 Priority=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Status = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Open'"); //Status
            for (SObject obj : P_Status) {
            	 Status=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Assignee = GSUtil.execute("SELECT CreatedById FROM Account where Name like 'Create CTA No Adv Criteria'"); //Assignee
            for (SObject obj : P_Assignee) {
            	 Assignee=obj.getChild("CreatedById").getValue().toString();
            }
            SObject[] P_Type = GSUtil.execute("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = 'Risk' and JBCXM__Type__c = 'Risk'");  //Type         
            for (SObject obj : P_Type) {
            	 Type=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Reason = GSUtil.execute("SELECT Id, Name FROM JBCXM__PickList__c where Name like 'Product Performance'"); //Reason
            for (SObject obj : P_Reason) {
            	 Reason=obj.getChild("Id").getValue().toString();
            }
            SObject[] P_Playbook = GSUtil.execute("SELECT Id, Name FROM JBCXM__Playbook__c where Name like 'Drop in Usage'"); //Playbook
            for (SObject obj : P_Playbook) {
            	Playbook=obj.getChild("Id").getValue().toString();
            }
            SObject[] CTA_created = GSUtil.execute("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'");
            for (SObject obj : CTA_created) {
            	Assert.assertEquals(Priority, obj.getChild("JBCXM__Priority__c").getValue().toString());
            	Assert.assertEquals(Status, obj.getChild("JBCXM__Stage__c").getValue().toString());
            	Assert.assertEquals(Assignee, obj.getChild("JBCXM__Assignee__c").getValue().toString());
            	Assert.assertEquals(Type, obj.getChild("JBCXM__Type__c").getValue().toString());
            	//Report.logInfo(Comment);
            	//Report.logInfo(obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Comment, obj.getChild("JBCXM__Comments__c").getValue().toString());
            	Assert.assertEquals(Reason, obj.getChild("JBCXM__Reason__c").getValue().toString());
            	Assert.assertEquals(Playbook, obj.getChild("JBCXM__Playbook__c").getValue().toString());
            }
        }
	
	
	@AfterClass
    public void afterClass() {
        GSUtil.soql = null;
    }
	
}
