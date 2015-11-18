package com.gainsight.bigdata.rulesengine;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.RuleExecutionHistory;
import com.gainsight.bigdata.rulesengine.pojo.setupaction.CloseCtaAction;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;
import com.sforce.soap.partner.sobject.SObject;

import org.testng.Assert;

import static com.gainsight.bigdata.urls.ApiUrls.*;
import static com.gainsight.sfdc.pages.Constants.INTERVAL_TIME;
import static com.gainsight.sfdc.pages.Constants.MAX_WAIT_TIME;

/**
 * @author Abhilash Thaduka
 *
 */
public class RulesUtil extends NSTestBase {

	// public static SFDCUtil sfdcUtil = new SFDCUtil();
	private final static String RULES_CLEAN_SCRIPT = "Delete [Select id from JBCXM__AutomatedAlertRules__c]";
	private static HashMap<String, String> featuresMap;
	private static HashMap<String, String> ctaTypesMap;
	private static HashMap<String, String> PickListMap;
	private static HashMap<String, String> emailTemplateMap;
	private static HashMap<String,String> playbookMap;
	private static HashMap<String,String> milestoneMap;
	public DataETL dataETL;
	ResponseObj result = null;
	private final static String CUSTOMER_DELETE_QUERY = "Delete [Select Id From JBCXM__CustomerInfo__c Where JBCXM__Account__r.AccountNumber='CustomRulesAccount'];";

	public static ResponseObject convertToObject(String result)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseObject response = objectMapper.readValue(result,
				ResponseObject.class);
		return response;
	}
	
	public void loadToCustomers(HashMap<String, String> testData) throws Exception {
		populateObjMaps();
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		Log.info("request:" + APP_API_EVENTRULE + "/" + ruleId);
		result = wa.doPost(APP_API_EVENTRULE +"/"+ ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+APP_API_EVENTRULE +"/"+ ruleId
				+ "\n Request rawBody:{}");
		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());
		RulesUtil.waitForCompletion(ruleId, wa, header);

		String LRR = sfdc
				.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
						+ RuleName + "'"))[0]
				.getChild(resolveStrNameSpace("JBCXM__LastRunResult__c")).getValue().toString();
		Assert.assertEquals("SUCCESS", LRR);

		int rules1 = sfdc.getRecordCount(resolveStrNameSpace("Select Id, IsDeleted From Account Where ((IsDeleted = false))"));
		Log.info(""+rules1);
		int rules2 = sfdc
				.getRecordCount(resolveStrNameSpace("Select Id,Name FROM JBCXM__CustomerInfo__c where Id!=null and isdeleted=false"));
		Log.info(""+rules2);
		Assert.assertEquals(rules1, rules2);
	}
   /**
 * @param testData - the entire testData Hashmap which we get from excel - from which we generate the rule.
 */
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
	   								+",JBCXM__AutomatedAlertRules__c ='"+getRuleId(testData.get("Name"))+"'"
	   								+",JBCXM__ActionInfo__c='"+getIdResolvedString(testData.get("JBCXM__ActionInfo__c"))+"');"
	   								+"insert NewAT;";
	   Log.info("Creating Action Template...."+apexCodeForActionTemplate);
	   if(apexCodeForActionTemplate.contains("$templateId")) {
		   SObject[] templates = sfdc.getRecords("SELECT Id FROM EmailTemplate where Name='Gainsight sample template - measure below threshold'");
		   String templateId = (String) templates[0].getChild("Id").getValue();
		   apexCodeForActionTemplate=getStringByReplacingTemplateId(apexCodeForActionTemplate, templateId);
	   }
	   sfdc.runApexCode(resolveStrNameSpace(apexCodeForActionTemplate));
   }

	/**
	 * Method to load Accounts and Customers for the Rule Use case automation
	 * @param dataETL
	 * @param accountJobName
	 * @param customerJobName
	 * @throws IOException
	 */
	public void loadAccountsAndCustomers(DataETL dataETL,
			String accountJobName, String customerJobName) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		if (accountJobName != null && accountJobName != "") {
			JobInfo jobInfo = mapper.readValue(
					(new FileReader(accountJobName)), JobInfo.class);
			dataETL.execute(jobInfo);
		}
		if (customerJobName != null && customerJobName != "") {
			sfdc.runApexCode(resolveStrNameSpace(CUSTOMER_DELETE_QUERY));
			// dataETL.cleanUp(CUSTOMER_OBJECT,
			// resolveStrNameSpace(CUSTOMER_DELETE_QUERY));
			JobInfo jobInfo = mapper.readValue(
					(new FileReader(customerJobName)), JobInfo.class);
			dataETL.execute(jobInfo);
		}
	}
	
	/**
	 * Method to generate the static maps which contains the Ids to be replaced for tokens, in the test data
	 */
	public void populateObjMaps() {
		featuresMap = getMapFromObject("JBCXM__Features__c","JBCXM__Feature__c","FT");
		ctaTypesMap = getMapFromObject("JBCXM__CTATypes__c", "Name",
				"CT");
		PickListMap = getMapFromObject("JBCXM__PickList__c",
				"JBCXM__SystemName__c", "PL");
		emailTemplateMap = getMapFromObject("EmailTemplate", "Name", "EML");
		playbookMap=getMapFromObject("JBCXM__Playbook__c","Name","PB");
		milestoneMap=getMapFromObjectUsingFilter("JBCXM__PickList__c","Name","ML","JBCXM__Category__c", "Milestones");
	}

	/**
	 * @param string - the string with Tokens  - pattern of token : #Shortcut.SysName# 
	 * @return - the string where Ids are replaced with values - like system names
	 */
	private String getIdResolvedString(String string) {
		string = replaceSystemNameInRule(replaceSystemNameInRule(
				replaceSystemNameInRule(
						replaceSystemNameInRule(
								replaceSystemNameInRule(replaceSystemNameInRule(string,
										emailTemplateMap), featuresMap),
						ctaTypesMap), PickListMap),playbookMap),milestoneMap);
		return string;
	}

	/**
	 * @param priorityValue - Expected Priority value
	 * @param statusValue - Expected Status value of cTA
	 * @param accForAssigneeValue- Account name from which contains the Assignee value for the CTA in one of the fields.
	 * @param typeValue- Expected CTA Type value
	 * @param reasonValue- Expected Reason value for cTA
	 * @param Comment- Expected Comment
	 * @param RuleName- Rule Name from AutomatedAlertRules__c object
	 * @param PlaybookName- Expected Playbook id - incase CTA has playbook associated
	 * @param refrenceUser- true if the value of the User has to be referenced from Account field...else false
	 * @return - true if the CTA is successfully created./ false if any of the CTA criteria do not match with the required values.
	 */
	public boolean isCTACreateSuccessfully(String priorityValue,
			String statusValue, String accForAssigneeValue, String typeValue,
			String reasonValue, String Comment, String RuleName,
			String PlaybookName, boolean refrenceUser) {

		String Assignee;
		if (refrenceUser == true) {
			Assignee = sfdc
					.getRecords(resolveStrNameSpace("SELECT C_Reference__c FROM Account where Name like 'Create CTA No Adv Criteria'"))[0]
					.getChild("C_Reference__c").getValue().toString();
		} else {
			Assignee = sfdc
					.getRecords(resolveStrNameSpace("SELECT CreatedById FROM Account where Name like '"
							+ accForAssigneeValue + "'"))[0]
					.getChild("CreatedById").getValue().toString();
		}

		boolean check = true;
		SObject[] CTA_created = sfdc
				.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"
						+ RuleName + "'"));
		for (SObject obj : CTA_created) {
			if (!PickListMap.get("PL." + priorityValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Priority__c")).getValue().toString())) {
				Log.error("Priority did not match!!");
				check = false;
			}
			if (!PickListMap.get("PL." + statusValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Stage__c")).getValue().toString())) {
				Log.error("Status did not match!!");
				check = false;
			}
			 if(!Assignee.equalsIgnoreCase(obj.getChild("JBCXM__Assignee__c").getValue().toString()))
			 { Log.error("Assignee did not match!!"); check=false; }
			if (!ctaTypesMap.get("CT." + typeValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Type__c")).getValue().toString())) {
				Log.error("Type did not match!!");
				check = false;
			}
			if (!Comment.equalsIgnoreCase(obj.getChild(resolveStrNameSpace("JBCXM__Comments__c"))
					.getValue().toString())) {
				System.out.println("comment:"
						+ obj.getChild(resolveStrNameSpace("JBCXM__Comments__c")).getValue()
								.toString());
				Log.error("Comments did not match!!");
				check = false;
			}
			if (!PickListMap.get("PL." + reasonValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Reason__c")).getValue().toString())) {
				Log.error("Reason did not match!!");
				check = false;
			}
			if (PlaybookName != null) {
				String Playbook = sfdc
						.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like '"
								+ PlaybookName + "'"))[0].getChild("Id")
						.getValue().toString();
				if (!Playbook.equalsIgnoreCase(obj
						.getChild(resolveStrNameSpace("JBCXM__Playbook__c")).getValue().toString())) {
					Log.error("Playbooks do not match!!");
					check = false;
				}
			}
		}
		return check;
	}

	public String getStringByReplacingTemplateId(String code, String templateId) {
		code = code.replace("$templateId", templateId);
		if (!isPackage) {
			code = code.replace("JBCXM__", "");
		}
		return code;
	}

	/**
	 * @param ruleId
	 *            - Id of the rule for which we need to wait (for the execution
	 *            to be done
	 * @param webAction
	 * @param header
	 * @throws Exception
	 */
	public static void waitForCompletion(String ruleId, WebAction webAction,
			Header header) throws Exception {
		boolean flag = true;
		int maxWaitingTime = 3000000;
		long startTime = System.currentTimeMillis();
		long executionTime = 0;
		while (flag && executionTime < maxWaitingTime) {
			Thread.sleep(10000);
			ResponseObj result = webAction.doGet(APP_API_ASYNC_STATUS
					+ "?ruleId=" + ruleId + "",
					header.getAllHeaders());
			ResponseObject res = RulesUtil.convertToObject(result.getContent());
			List<Object> data = (List<Object>) res.getData();
			Map<String, Object> map = (Map<String, Object>) data.get(0);
			if (map.get("status") != null) {
				String status = (String) map.get("status");
				if (status.equalsIgnoreCase("completed")
						|| status.equalsIgnoreCase("failed_while_processing")) {
					flag = false;
					if (!status.equalsIgnoreCase("completed")) {
						Log.info("ruledID - " + ruleId + " "
								+ map.get("executionMessages"));
					}
				}
			}
			executionTime = System.currentTimeMillis() - startTime;
		}
	}

    /**
     * Deletes the rule based on either ruleId Or RuleName Or Both.
     * If Both are NULL, then all the rules are deleted
     * @param ruleId
     * @param ruleName
     */
    public void deleteRule(String ruleId, String ruleName) {
        String tempQuery = "Select id, Name from JBCXM__AutomatedAlertRules__c ";
        if(ruleId == null && ruleName == null) {
            Log.info("Deleting All the Rules");
            sfdc.runApexCode(resolveStrNameSpace(RULES_CLEAN_SCRIPT));
        } else {
            if (ruleId != null && ruleName != null) {
                tempQuery += " where id='" + ruleId + "' and name='" + ruleName + "'";
            } else if (ruleId != null) {
                tempQuery += " where id='" + ruleId + "'";
            } else if (ruleName != null) {
                tempQuery += "where name='" + ruleName + "'";
            }
        }
        Log.info("Deleting Rules Query : " +tempQuery);
        if(sfdc.getRecords(resolveStrNameSpace(tempQuery)).length > 0 ) {
            sfdc.runApexCode(resolveStrNameSpace("Delete ["+tempQuery+"]"));
        } else {
            Log.error("No Rules Found to Delete");
            throw new RuntimeException("No Rules Found to Delete " +tempQuery);
        }
    }

    /**
     * Return the rule id if name id specified, else returns the rule id that's last created/modified.
     * @param ruleName - Rule name to fetch the rule id
     * @return - rule id if rule exists, else throws an exception.
     */
    public String getRuleId(String ruleName) {
        String tempQuery = "Select id, Name From JBCXM__AutomatedAlertRules__c";
        if(ruleName!=null) {
            tempQuery += " where name='"+ruleName+"'";
        }
        tempQuery += " ORDER BY LastModifiedDate DESC NULLS LAST ";
        Log.info("Query to get Rule Id " +ruleName);
        SObject[] sObjects = sfdc.getRecords(resolveStrNameSpace(tempQuery));
        if(sObjects.length >0) {
            return sObjects[0].getId();
        } else {
            Log.error("No Rules Exists in the system with matched criteria");
            throw new RuntimeException("No Rules Exists in the system with matched criteria");
        }
    }

    /**
     * Replaces the SystemName(which is given in as a pattern) with the Id fetched from the corresponding objects' HashMaps
     * @param text
     * @param replacements
     * @return
     */
    public static String replaceSystemNameInRule(String text, HashMap<String, String> replacements) {
        Pattern pattern = Pattern.compile("#(.+?)#");
        return replaceStringWithTokens(text, pattern, replacements);
    }

    
    /**
     *  Accepts a text, which contains patterns to be replaced and the values are present within the replacements hashmap
     * @param text
     * @param pattern
     * @param replacements
     * @return
     */
    public static String replaceStringWithTokens(String text, Pattern pattern, HashMap<String, String> replacements) {
        Matcher matcher = pattern.matcher(text);
        //populate the replacements map ...
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            Log.info("Found " +matcher.group());
            String replacement = replacements.get(matcher.group().substring(1, matcher.group().length()-1));
            Log.info("replacement : " + replacement);
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null)   {
                builder.append(matcher.group(0));
            }
            else {
                builder.append(replacement);
            }
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        Log.info("Replaced String : " +builder.toString());
        return builder.toString();
    }
    
    /**
     * Extra utility which can be used to generate the system names in test data by replacing the Ids..not used as of now!
     * @param text
     * @param replacements
     * @return
     */
    public static String replaceSFIDWithSystemName(String text, HashMap<String, String> replacements) {
        Pattern pattern = Pattern.compile("\"\\w{18}\"");
        return replaceStringWithTokens(text, pattern, replacements);
    }
    
	public Boolean createAndRunRule(HashMap<String,String> testData) throws Exception {
		setupRule(testData);
		String RuleName = testData.get("Name");
		String ruleId = getRuleId(RuleName);
		result = wa.doPost(API_RULE_RUN+"/" + ruleId,
				header.getAllHeaders(), "{}");
		Log.info("Rule ID:" + ruleId + "\n Request URL"
				+ API_RULE_RUN + "/" + ruleId
				+ "\n Request rawBody:{}");

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		if(!Boolean.valueOf(responseObj.getResult()) || responseObj.getRequestId()==null)
		{
			Log.error("Rule Request itself failed!");
			return false;
		}
		else{
			RulesUtil.waitForCompletion(ruleId, wa, header);

			String LRR = sfdc
					.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '"
							+ RuleName + "'"))[0]
					.getChild(resolveStrNameSpace("JBCXM__LastRunResult__c")).getValue().toString();
			if(!LRR.equalsIgnoreCase("Success"))
			{
				Log.error("Rule Processing failed");
				return false;
			}
		}

		return true;
	}

	/*
        This method creates all fields of all types except LookUp,PickList and MultiPickList
     */
	public void createCustomFieldsAndAddPermissions(String object, HashMap<String, String[]> fieldsMap) {
		sfinfo = sfdc.fetchSFDCinfo();
		try {
			Iterator it = fieldsMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				String fieldType = (String) pair.getKey();
				if ("DATE".equalsIgnoreCase(fieldType)) {
					metadataClient.createDateField(object, fieldsMap.get(fieldType), false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("DATETIME".equalsIgnoreCase(fieldType)){
					metadataClient.createDateField(object, fieldsMap.get(fieldType), true);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("BOOLEAN".equalsIgnoreCase(fieldType)){
					metadataClient.createFields(object, fieldsMap.get(fieldType), true, false, false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("PHONE".equalsIgnoreCase(fieldType)){
					metadataClient.createFields(object, fieldsMap.get(fieldType), false, true, false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("URL".equalsIgnoreCase(fieldType)){
					metadataClient.createFields(object, fieldsMap.get(fieldType), false, false, true);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("NUMBER".equalsIgnoreCase(fieldType)){
					metadataClient.createNumberField(object, fieldsMap.get(fieldType), false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("PERCENT".equalsIgnoreCase(fieldType)){
					metadataClient.createNumberField(object, fieldsMap.get(fieldType), true);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("EMAIL".equalsIgnoreCase(fieldType)){
					metadataClient.createEmailField(object, fieldsMap.get(fieldType));
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("TEXT".equalsIgnoreCase(fieldType)){
					metadataClient.createTextFields(object, fieldsMap.get(fieldType), false, false, true, false, false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else if("TEXTAREA".equalsIgnoreCase(fieldType)){
					metadataClient.createTextFields(object, fieldsMap.get(fieldType), false, false, true, true, false);
					metaUtil.addFieldPermissionsToUsers(resolveStrNameSpace(object), metaUtil.convertFieldNameToAPIName(fieldsMap.get(fieldType)), sfinfo, true);
				}
				else{

				}
				continue;
			}
		}
		catch (Exception e){
			Log.error("Exception occurred while creating and adding field permissions.",e);
			e.printStackTrace();
		}
	}

	public void saveCustomObjectInRulesConfig(String data) throws Exception{
		Log.info("\n Request URL"
				+ APP_API_RULES_LOADABLE_OBJECT
				+ "\n Request rawBody:" + data);

		result = wa.doPost(
				APP_API_RULES_LOADABLE_OBJECT,
				header.getAllHeaders(), data);

		ResponseObject responseObj = RulesUtil.convertToObject(result
				.getContent());
		Assert.assertTrue(Boolean.valueOf(responseObj.getResult()));
		Assert.assertNotNull(responseObj.getRequestId());

	}

	/**
	 * This method will run the rule based on rule name.
	 *
	 * @param ruleName, name of the rule.
	 * @return Returns true if rule ran successfully.
	 */
	public Boolean runRule(String ruleName) throws Exception {
		String ruleId = getRuleId(ruleName);
		result = wa.doPost(API_RULE_RUN + "/" + ruleId, header.getAllHeaders(), "{}");
		ResponseObject responseObj = RulesUtil.convertToObject(result.getContent());
		if (!Boolean.valueOf(responseObj.getResult()) || responseObj.getRequestId() == null) {
			Log.error("Rule Request itself failed!");
			return false;
		} else {
			RulesUtil.waitForCompletion(ruleId, wa, header);
			String LRR = null;
			SObject[] jsondata = sfdc
					.getRecords(resolveStrNameSpace("select JBCXM__LastRunResult__c from JBCXM__AutomatedAlertRules__c where Name like '" + ruleName + "' order by createdDate desc limit 1"));
			if (jsondata.length > 0) {
				LRR = jsondata[0].getField(resolveStrNameSpace(("JBCXM__LastRunResult__c"))).toString();
			}
			if (!LRR.equalsIgnoreCase("Success")) {
				Log.error("Rule Processing failed");
				return false;
			}
		}
		return true;
	}

	/**
	 * Verifies whether a CTA is created successfully
	 * @param priorityValue - Expected Priority value
	 * @param statusValue   - Expected Status value of cTA
	 * @param assignee-     Account name from which contains the Assignee value for the CTA in one of the fields.
	 * @param typeValue-    Expected CTA Type value
	 * @param reasonValue-  Expected Reason value for cTA
	 * @param comment-      Expected Comment
	 * @param ruleName-     Rule Name from AutomatedAlertRules__c object
	 * @param playbookName- Expected Playbook id - incase CTA has playbook associated
	 * @return - true if the CTA is successfully created./ false if any of the CTA criteria do not match with the required values.
	 */
	public boolean isCTACreateSuccessfully(String priorityValue,
										   String statusValue, String assignee, String typeValue,
										   String reasonValue, String comment, String ruleName,
										   String playbookName) {

		boolean check = true;
		SObject[] ctaRecords = sfdc
				.getRecords(resolveStrNameSpace("Select JBCXM__Priority__c,JBCXM__Stage__c,JBCXM__Assignee__c,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Playbook__c from JBCXM__CTA__c where Name = '"
						+ ruleName + "'"));
		for (SObject obj : ctaRecords) {
			if (!PickListMap.get("PL." + priorityValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Priority__c"))
							.getValue().toString())) {
				Log.error("Priority did not match!!");
				check = false;
			}
			if (!PickListMap.get("PL." + statusValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Stage__c"))
							.getValue().toString())) {
				Log.error("Status did not match!!");
				check = false;
			}
			if (!assignee.equalsIgnoreCase(obj.getChild("JBCXM__Assignee__c")
					.getValue().toString())) {
				Log.error("Assignee did not match!!");
				check = false;
			}
			if (!ctaTypesMap.get("CT." + typeValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Type__c"))
							.getValue().toString())) {
				Log.error("Type did not match!!");
				check = false;
			}
			if (!PickListMap.get("PL." + reasonValue).equalsIgnoreCase(
					obj.getChild(resolveStrNameSpace("JBCXM__Reason__c"))
							.getValue().toString())) {
				Log.error("Reason did not match!!");
				check = false;
			}
			if (playbookName == null) {
				Object playBookObj = obj.getField(resolveStrNameSpace("JBCXM__Playbook__c"));
				if (!(playbookName == playBookObj)) {
					Log.error("playBook is not null !!");
					check = false;
				}
			}
			if (playbookName != null && !(playbookName.isEmpty())) {
				String playBook = sfdc
						.getRecords(resolveStrNameSpace("SELECT Id, Name FROM JBCXM__Playbook__c where Name like '"
								+ playbookName + "'"))[0].getChild("Id")
						.getValue().toString();
				if (!playBook.equalsIgnoreCase(obj
						.getChild(resolveStrNameSpace("JBCXM__Playbook__c"))
						.getValue().toString())) {
					Log.error("Playbooks do not match!!");
					check = false;
				}
			}
			if (comment == null) {
				Object object = obj.getField(resolveStrNameSpace("JBCXM__Comments__c"));
				if (!(comment == object)) {
					Log.error("Comments is not null for update cta comments with never option!!");
					check = false;
				}
			}
			if (comment != null) {
				String comments = (String) obj.getField(resolveStrNameSpace("JBCXM__Comments__c"));
				if (!(comments.equalsIgnoreCase(comment))) {
					Log.error("Comments did not match!!");
					check = false;
				}
			}
		}
		return check;
	}
	

	/**
	 * @param ruleName ruleName to get status
	 * @return statusId
	 * @throws Exception
	 */
	public String runRuleAndGetStatusId(String ruleName) throws Exception {
		String statusId = null;
		String ruleId = getRuleId(ruleName);
		ResponseObj responseObj = wa.doPost(API_RULE_RUN + "/" + ruleId, header.getAllHeaders(), "{}");
		if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
			JsonNode jsonNode = mapper.readTree(responseObj.getContent());
			statusId = jsonNode.get("data").findValue("statusId").toString().replace("\"", "");
			Log.info("statusId is " + statusId);
		}
		return statusId;		
	}
	/**
	 * @param ruleName ruleName to Execute
	 * @return RuleExecutionHistory object
	 * @throws Exception
	 */
	public RuleExecutionHistory runRuleAndGetExecutionHistory(String ruleName) throws Exception {
		String statusId = runRuleAndGetStatusId(ruleName);
		if (statusId == null) {
			Log.error("Rule Trigger failed.");
			throw new RuntimeException("Rule Trigger failed.");
		}
		waitForRuleProcessingToComplete(statusId);
		return getExecutionHistory(statusId);
	}
	
	/**
	 * @param statusId statusID for rule to complete
	 */
	public void waitForRuleProcessingToComplete(final String statusId){
		Log.info("Waiting for rule with statusID "+statusId + " to Complete");
		CommonWait.waitForCondition(MAX_WAIT_TIME, INTERVAL_TIME, new ExpectedCommonWaitCondition<Boolean>() {
					@Override
					public Boolean apply() {
						return isRuleProcessingCompleted(statusId);
					}
				});
	}
	
	/**
	 * @param statusId statusID to get executionHistory
	 * @return RuleExecutionHistory object
	 */
	public RuleExecutionHistory getExecutionHistory(String statusId) {
		RuleExecutionHistory ruleExecutionHistory=null;
		try {
			ResponseObj responseObj = wa.doGet(APP_API_ASYNC_STATUS + "/" + statusId + "", header.getAllHeaders());
			if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
				NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
				if (nsResponseObj.isResult()) {
					ruleExecutionHistory = mapper.convertValue(nsResponseObj.getData(), RuleExecutionHistory.class);
				}
			}
		} catch (Exception e) {
			Log.error("Rule processing is not completed", e);
			throw new RuntimeException("Rule processing is not completed" + e.getLocalizedMessage());
		}
		return ruleExecutionHistory;
	}
	
	public boolean isRuleProcessingCompleted(String statusId){
		boolean result = false;
		RuleExecutionHistory executionHistory  = getExecutionHistory(statusId);
		if (executionHistory.getStatus() != null && (executionHistory.getStatus().equalsIgnoreCase("Completed") || 
				executionHistory.getStatus().equalsIgnoreCase("Failed_While_Processing"))) {		
				result = true;
		} 
		return result;
	}
	
	public boolean isCTAclosedSuccessfully(CloseCtaAction closeCtaAction) {
		boolean check = true;
		SObject[] closedCtaRecords = sfdc
				.getRecords(resolveStrNameSpace("SELECT Name,JBCXM__Type__c,JBCXM__Comments__c,JBCXM__Reason__c,JBCXM__Source__c,JBCXM__Stage__c,JBCXM__ClosedDate__c FROM JBCXM__CTA__c where JBCXM__ClosedDate__c!=null"));
		for (SObject obj : closedCtaRecords) {
			if (!ctaTypesMap.get("CT." + closeCtaAction.getType())
					.equalsIgnoreCase(
							obj.getChild(resolveStrNameSpace("JBCXM__Type__c"))
									.getValue().toString())) {
				Log.error("Type did not match!!");
				check = false;
			}
			if (!PickListMap.get("PL." + closeCtaAction.getReason())
					.equalsIgnoreCase(
							obj.getChild(
									resolveStrNameSpace("JBCXM__Reason__c"))
									.getValue().toString())) {
				Log.error("Reason did not match!!");
				check = false;
			}
			if (!PickListMap
					.get("PL." + closeCtaAction.getSetCtaStatusTo())
					.equalsIgnoreCase(
							obj.getChild(resolveStrNameSpace("JBCXM__Stage__c"))
									.getValue().toString())) {
				Log.error("Status did not match!!");
				check = false;
			}
			if (closeCtaAction.getSource() != null) {
				String sourceValue = (String) obj
						.getField(resolveStrNameSpace("JBCXM__Source__c"));
				if (!closeCtaAction.getSource().contains(sourceValue)) {
					Log.error("Source did not match!!");
					check = false;
				}
			}
		}
		return check;
	}
	
}

