package com.gainsight.sfdc.gsEmail.setup;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.administration.pages.AdminIntegrationPage;
import com.gainsight.sfdc.administration.pages.AdministrationBasePage;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.sfdc.util.bulk.SFDCInfo;
import com.gainsight.sfdc.util.bulk.SFDCUtil;
import com.gainsight.utils.MongoUtil;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.mongodb.ServerAddress;
import com.sforce.soap.partner.sobject.SObject;

public class GSEmailSetup extends BaseTest {
	WebAction wa = new WebAction();
	private Boolean isPackaged = Boolean.valueOf(env
			.getProperty("sfdc.managedPackage"));
	private static final String EXISTS_CALL = "/api/accounts/SFDC/exists";
	private static final String GET_ACCESS_KEY = "/api/email/account";
	private static final String SEND_EMAIL = "/api/email/template";
	private static final String MANDRILL_ENDPOINT = "https://mandrillapp.com/api/1.0/";
	private static final String MANDRILL_SUBACCOUNT_INFO = "/subaccounts/info.json";
	private String AccessKey;
	private String TenantId;
	private String OrgName;

	// doing via UI...will change as and when the UI code changes!
	// backend automation seems to be complicated :(
	public AdminIntegrationPage enableOAuthForOrg() {
		try {
			AdministrationBasePage admin = basepage.clickOnAdminTab();
			AdminIntegrationPage integ = admin.clickOnIntegrationLink();
			integ.clickOnEnableGSMDP();
			Timer.sleep(2);
			Timer.sleep(2);
			integ.clickOnAuthorize();
			return integ;
		} catch (ElementNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean enableGSEmailInAdmin() {

		return true;
	}

	public boolean validateOAuthEnabled() {
		Header hdrs = new Header();
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		String endPoint = env.getProperty("ns.appurl");
		String sessionid = sfinfo.getSessionId();
		String orgId = sfinfo.getOrg();
		String userId = sfinfo.getUserId();
		try {

			hdrs.addHeader("Content-Type", "application/json");
			hdrs.addHeader("appOrgId", orgId);
			hdrs.addHeader("appUserId", userId);
			hdrs.addHeader("appSessionId", sessionid);
			System.out.println("endpoint:" + sfinfo.getEndpoint());
			// "https://jbcxm.na10.visual.force.com"
			String SFInstance = sfinfo.getEndpoint().split("https://")[1]
					.split("\\.")[0];
			String OriginHeader = "";
			if (isPackaged)
				OriginHeader = "https://jbcxm." + SFInstance
						+ ".visual.force.com";
			else
				OriginHeader = "https://" + SFInstance + ".visual.force.com";

			System.out.println("OriginHeader value=" + OriginHeader);
			hdrs.addHeader("Origin", OriginHeader);
			String uri = endPoint + EXISTS_CALL;

			ResponseObj httpResp = wa.doGet(uri, hdrs.getAllHeaders());
			System.out.println("response==" + httpResp.getContent());

			if (httpResp.toString().contains("\"result\":true"))
				return true;
			else
				return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("error while validating oauth");
			return false;
		}

	}

	// Includes creation of survey via Apex script,
	// getting access key for GS email,
	// Updating the Access key in the Application Settings object
	public void createSurveyWithGSEmail() {
/*		sfdc.runApexCode(getNameSpaceResolvedFileContents(env.basedir
				+ "/apex_scripts/Surveys/EmailService_NonAnonySurvey.apex"));*/ /*Commented for now as the script doesn't exists*/
		Header hdrs = new Header();
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		String endPoint = env.getProperty("ns.appurl");
		String sessionid = sfinfo.getSessionId();
		String orgId = sfinfo.getOrg();
		String userId = sfinfo.getUserId();
		try {
			hdrs.addHeader("Content-Type", "application/json");
			hdrs.addHeader("appOrgId", orgId);
			hdrs.addHeader("appUserId", userId);
			hdrs.addHeader("appSessionId", sessionid);
			System.out.println("endpoint:" + sfinfo.getEndpoint());
			// Creating Origin header value:
			String SFInstance = sfinfo.getEndpoint().split("https://")[1]
					.split("\\.")[0];
			String OriginHeader = "";
			if (isPackaged)
				OriginHeader = "https://jbcxm." + SFInstance
						+ ".visual.force.com";
			else
				OriginHeader = "https://" + SFInstance + ".visual.force.com";

			System.out.println("OriginHeader value=" + OriginHeader);
			hdrs.addHeader("Origin", OriginHeader);
			String uri = endPoint + GET_ACCESS_KEY;

			ResponseObj httpResp = wa.doGet(uri, hdrs.getAllHeaders());
			System.out.println("response==" + httpResp.getContent());
			AccessKey = httpResp.getContent().split("\"accessKey\":\"")[1]
					.split("\"")[0];
			TenantId = httpResp.getContent().split("\"id\":\"")[1].split("\"")[0];
			OrgName = httpResp.getContent().split("\"name\":\"")[1].split("\"")[0];
			System.out.println("Got AccessKey as ..." + AccessKey);
			if (AccessKey != null && AccessKey != "") {
				sfdc.runApexCode(resolveStrNameSpace("String marketoAESKey= '1234567890123456';"
						+ "Blob aesKeyBlob = Blob.valueOf(marketoAESKey);"
						+ "Blob accessKeyBlob = Blob.valueOf('"
						+ AccessKey
						+ "');"
						+ "Blob encryptedBlob = Crypto.encryptWithManagedIV('AES128', aesKeyBlob, accessKeyBlob);"
						+ "String encoded= EncodingUtil.base64Encode(encryptedBlob);"
						+ "List<JBCXM__ApplicationSettings__c> appSet= [select id,JBCXM__AccessKeys__c from JBCXM__ApplicationSettings__c];"
						+ "appSet[0].JBCXM__AccessKeys__c='{\"gainsightEmailKey\":\"'+encoded+'\"}';"
						+ "upsert appSet;"));
				// checkSubAccountInMandrill("fi_h5Ag1dOmqYFV79aYcTA", TenantId,
				// OrgName);

			} else
				System.out
						.println("error in fetching Accesskey!!...could not update the ");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Boolean sendTestEmail() {
		// create contact
		sfdc.runApexCode(resolveStrNameSpace("delete [select id from Contact where AccountId in (select id from Account where Name='Email Test Account')];"
				+ "delete [select id from Account where Name='Email Test Account'];"
				+ "Account ac = new Account(Name='Email Test Account');insert ac;"
				+ "Account acc= [select id from Account where Name='Email Test Account'];"
				+ "Contact ct = new Contact(LastName='Srividya',Email='srallapalli@gainsight.com',AccountId=acc.id);"
				+ "insert ct;"));

		// send test email to contact : /api/email/template
		Header hdrs = new Header();
		SFDCInfo sfinfo = SFDCUtil.fetchSFDCinfo();
		String endPoint = env.getProperty("ns.appurl");
		String sessionid = sfinfo.getSessionId();
		String orgId = sfinfo.getOrg();
		String userId = sfinfo.getUserId();
		try {
			hdrs.addHeader("Content-Type", "application/json");
			hdrs.addHeader("appOrgId", orgId);
			hdrs.addHeader("accessKey", AccessKey);
			System.out.println("endpoint:" + sfinfo.getEndpoint());
			hdrs.addHeader("Origin", "https://jbcxm.na10.visual.force.com");
			String uri = endPoint + SEND_EMAIL;
			SObject[] cts = sfdc
					.getRecords("select id,Name,Email from Contact where AccountId in (select id from Account where Name='Email Test Account')");
			SObject[] sur = sfdc
					.getRecords(resolveStrNameSpace("Select id from JBCXM__Survey__c where JBCXM__Code__c='EmailServices_Test'"));
			String rawBody = "{\"useCase\": \"survey\",\"subject\": \"Send Test email\","
					+ "\"htmlBody\": \"Hi,<br><br>This is a test email {!abcd}\","
					+ "\"textBody\": \"Hi,\\n\\nThis is a test email\","
					+ "\"campaign\":\""
					+ sur[0].getId()
					+ "\","
					+ "\"schedule\":\"test_12345\","
					+ "\"options\":{\"fromEmail\":\"sri@gainsight.com\",\"templateType\":\"sfdc\",\"fromName\":\"Srividya R\"},"
					+ "\"recipients\": ["
					+ "{\"email\": \""
					+ cts[0].getField("Email")
					+ "\","
					+ "\"name\" : \""
					+ cts[0].getField("Name")
					+ "\","
					+ "\"metadata\": \""
					+ cts[0].getId()
					+ "\","
					+ "\"tokens\":{\"abcd\":\"1234\"}}]}";
			System.out.println("json body=" + rawBody);

			ResponseObj httpResp = wa
					.doPost(uri, hdrs.getAllHeaders(), rawBody);
			System.out.println("statuscode==" + httpResp.getStatusCode());
			String result = httpResp.getContent().split("\"result\":")[1]
					.split(",")[0];
			String data = httpResp.getContent().split("\"data\":\"")[1]
					.split("\"")[0];
			if (httpResp.getStatusCode() == 200 && result.contains("true")
					&& data.contains("template sent")) {
				System.out
						.println("Test Email sent successfully....proceed to validation");
				return getEmailActivityLogfromMongo(sur[0].getId());
			} else {
				System.out.println("Failed to send test email");
				return false;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Test Email Send Failed!!");
			return false;
		}
	}

	void sendManualEmail() {

	}

	void sendScheduleEmail() {

	}

	public boolean getEmailActivityLogfromMongo(String campaignId)
			throws UnknownHostException {
		MongoUtil mUtil = new MongoUtil();
		mUtil.createConnection(new ServerAddress("dharma.mongohq.com", 10089));
		mUtil.checkIfCollectionExist("EmailActivityLog");
		HashMap records = new HashMap<String, String>();
		records.put("campaignId", campaignId);
		records.put("useCase", "survey");
		ArrayList<String[]> op = new ArrayList<String[]>();
		String op1[] = { "emailActivityList", "$size", "1" };
		op.add(op1);
		return mUtil.checkIfDocExists("EmailActivityLog", records);

	}

	public boolean checkSubAccountInMandrill(String AccessKey, String TenantId,
			String OrgName) throws Exception {
		Header hmandrill = new Header();
		hmandrill.addHeader("content-type", "application/json");
		String uri = MANDRILL_ENDPOINT + MANDRILL_SUBACCOUNT_INFO;
		String rawBody = "{\"key\":\"" + AccessKey + "\",\"id\":\"" + TenantId
				+ "\"}";
		ResponseObj httpResp = wa.doPost(uri, hmandrill.getAllHeaders(),
				rawBody);
		System.out.println("statuscode==" + httpResp.getStatusCode());
		String status = httpResp.getContent().split("\"status\":")[1]
				.split(",")[0];
		String name = httpResp.getContent().split("\"name\":\"")[1].split("\"")[0];
		System.out.println("name=" + name.getBytes("UTF-8"));
		System.out.println("orgname=" + OrgName.getBytes("UTF-8"));
		if (status.equals("\"active\"") && name.equals(OrgName)) {
			System.out.println("found account in mandrill");
			return true;
		} else
			return false;
	}

	public void deleteOldEmailActivityLogFromMongo() {

	}

}
