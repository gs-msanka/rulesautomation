package com.gainsight.bigdata.rulesengine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gainsight.testdriver.Application;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.testdriver.Log;
import com.gainsight.util.PropertyReader;
import com.sforce.soap.partner.sobject.SObject;

public class RulesUtil extends NSTestBase {
  
 //   public static SFDCUtil sfdcUtil = new SFDCUtil();
   public static ResponseObject convertToObject(String result)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseObject response = objectMapper.readValue(result,
                ResponseObject.class);
        return response;
    }
   public void setupRule(HashMap<String,String> testData){
	   //Create Rule in AutomatedAlertHandler
	   String apexCodeForRuleCreation="JBCXM__AutomatedAlertRules__c rule=new JBCXM__AutomatedAlertRules__c(Name='"+testData.get("Name")+"',"
	   											+"JBCXM__ruleType__c='"+testData.get("JBCXM__ruleType__c")+"',JBCXM__SourceType__c='"+testData.get("JBCXM__SourceType__c")
	   											+"',JBCXM__TriggerCriteria__c='"+getIdResolvedString(testData.get("JBCXM__TriggerCriteria__c"))+"');"
	   											+"insert rule;"
	   											+"rule.JBCXM__externalId__c=rule.Id;"
	   											+"update rule;";
	   Log.info("Creating Rule...."+apexCodeForRuleCreation);
	   sfdc.runApexCode(resolveStrNameSpace(apexCodeForRuleCreation));
	   //Create ActionInfo in ActionTemplate
	   String apexCodeForActionTemplate="JBCXM__AutomatedAlertRules__c AAR = [Select id,Name from JBCXM__AutomatedAlertRules__c Where Name = '"+testData.get("Name")+"' Limit 1];"
	   								+ "JBCXM__ActionTemplates__c NewAT = new JBCXM__ActionTemplates__c(JBCXM__Action_Type__c ='"+testData.get("JBCXM__Action_Type__c")+"'"
	   								+",JBCXM__AutomatedAlertRules__c ='"+getSFId(testData.get("JBCXM__AutomatedAlertRules__c"))+"'"
	   								+",JBCXM__ActionInfo__c='"+getIdResolvedString(testData.get("JBCXM__ActionInfo__c"))+"');"
	   								+"insert NewAT;";
	   Log.info("Creating Action Template...."+apexCodeForActionTemplate);
	   sfdc.runApexCode(resolveStrNameSpace(apexCodeForActionTemplate));
   }
   
   private String getIdResolvedString(String string) {
	while(true){
	   if(string.contains("SFID")){
		 System.out.println("Replacing:  ----   SFID:"+string.split("SFID:")[1].split(":SFID")[0]+":SFID----with"+ getSFId("SFID:"+string.split("SFID:")[1].split(":SFID")[0]));
		string=string.replace("SFID:"+string.split("SFID:")[1].split(":SFID")[0]+":SFID", getSFId("SFID:"+string.split("SFID:")[1].split(":SFID")[0]));
	}
	   else break;
   }
	return string;
}
public boolean isCTACreateSuccessfully(String priorityValue,String statusValue,String accForAssigneeValue,String typeValue,String reasonValue,String Comment,String RuleName,String PlaybookName,boolean refrenceUser){
	   
		String Priority,Status,Assignee,Type,Reason;
       Priority= sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like '"+priorityValue+"'"))[0].getChild("Id").getValue().toString();
       Status=sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like '"+statusValue+"'"))[0].getChild("Id").getValue().toString();
       if(refrenceUser==true) { Assignee = sfdc.getRecords(resolveStrNameSpace("SELECT C_Reference__c FROM Account where Name like 'Create CTA No Adv Criteria'"))[0].getChild("C_Reference__c").getValue().toString(); }
       else { Assignee=sfdc.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like '"+accForAssigneeValue+"'"))[0].getChild("CreatedById").getValue().toString(); }
        Type= sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name, JBCXM__Type__c FROM JBCXM__CTATypes__c where Name = '"+typeValue+"' and JBCXM__Type__c = '"+typeValue+"'"))[0].getChild("Id").getValue().toString();
        Reason=sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__PickList__c where Name like '"+reasonValue+"'"))[0].getChild("Id").getValue().toString();   
  
       boolean check=true;
       SObject[] CTA_created = sfdc.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"+RuleName+"'"));
       for (SObject obj : CTA_created) {
       	if(!Priority.equalsIgnoreCase(obj.getChild("JBCXM__Priority__c").getValue().toString())) { Log.error("Priority did not match!!"); check=false; }
       	if(!Status.equalsIgnoreCase(obj.getChild("JBCXM__Stage__c").getValue().toString()))	{ Log.error("Status did not match!!"); check=false; }
       	if(!Assignee.equalsIgnoreCase(obj.getChild("JBCXM__Assignee__c").getValue().toString())) { Log.error("Assignee did not match!!"); check=false; }
       	if(!Type.equalsIgnoreCase(obj.getChild("JBCXM__Type__c").getValue().toString())) { Log.error("Type did not match!!"); check=false; }
       	if(!Comment.equalsIgnoreCase(obj.getChild("JBCXM__Comments__c").getValue().toString())) {System.out.println("comment:"+obj.getChild("JBCXM__Comments__c").getValue().toString()); Log.error("Comments did not match!!"); check=false; }
       	if(!Reason.equalsIgnoreCase(obj.getChild("JBCXM__Reason__c").getValue().toString())) { Log.error("Reason did not match!!"); check=false; }
        if(PlaybookName!=null) {
        	String Playbook = sfdc.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like '"+PlaybookName+"'"))[0].getChild("Id").getValue().toString(); 
        	if(!Playbook.equalsIgnoreCase(obj.getChild("JBCXM__Playbook__c").getValue().toString())) { Log.error("Playbooks do not match!!"); check=false; }
        	}
       }
       return check;
   }
    public  void runApexCodeByReplacingTemplateId(String fileName, String templateId) {
        String code = FileUtil.getFileContents(fileName);
        code = code.replace("$templateId", templateId);
        if (!isPackage) {
            code = code.replace("JBCXM__", "");
        }
        sfdc.runApexCode(code);
    }

    public static void waitForCompletion(String ruleId, WebAction webAction, Header header) throws Exception {
        boolean flag = true;
        int maxWaitingTime = 3000000;
        long startTime = System.currentTimeMillis();
        long executionTime = 0;
        while (flag && executionTime < maxWaitingTime) {
            Thread.sleep(10000);
            ResponseObj result = webAction.doGet(PropertyReader.nsAppUrl + "/api/async/process/?ruleId=" + ruleId + "", header.getAllHeaders());
            ResponseObject res = RulesUtil.convertToObject(result.getContent());
            List<Object> data = (List<Object>) res.getData();
            Map<String, Object> map = (Map<String, Object>) data.get(0);
            if (map.get("status") != null) {
                String status = (String) map.get("status");
                if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("failed_while_processing")) {
                    flag = false;
                    if(!status.equalsIgnoreCase("completed")){
                    	Log.info("ruledID - "+ruleId+ " "+map.get("executionMessages"));
                    }
                }
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
    }

}
