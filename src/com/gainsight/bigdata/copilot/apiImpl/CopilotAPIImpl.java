package com.gainsight.bigdata.copilot.apiImpl;

import com.gainsight.bigdata.copilot.bean.emailProp.EmailProperties;
import com.gainsight.bigdata.copilot.bean.emailTemplate.EmailTemplate;
import com.gainsight.bigdata.copilot.bean.outreach.OutReach;
import com.gainsight.bigdata.copilot.bean.outreach.OutReachExecutionHistory;
import com.gainsight.bigdata.copilot.bean.smartlist.SmartList;
import com.gainsight.bigdata.copilot.bean.webhook.mandrill.MandrillWebhookEvent;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.pojo.RuleExecutionHistory;
import com.gainsight.bigdata.pojo.Schedule;
import com.gainsight.bigdata.segmentio.EventManager;
import com.gainsight.bigdata.segmentio.EventSubmitter;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.sfdc.util.DateUtil;
import com.gainsight.testdriver.Log;
import com.gainsight.util.CryptHandler;
import com.gainsight.utils.Verifier;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.*;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Giribabu on 25/11/15.
 */
public class CopilotAPIImpl {

    private Header header;
    WebAction wa = new WebAction();
    ObjectMapper mapper = new ObjectMapper();
    Header tempHeader = new Header();
    private static final String MANDRILL_POST_PARAM_NAME = "mandrill_events";


    public CopilotAPIImpl(Header header) {
        Log.info("Initializing copilot...");
        this.header = header;
        tempHeader.addHeader("Content-Type", "application/json");
    }

    public String createSmartListExecuteAndGetId(String payload) throws Exception {
        SmartList smartList = createSmartListExecuteAndGetSmartList(payload);
        if(smartList !=null) {
            return smartList.getSmartListId();
        }
        return null;
    }

    /**
     * Create a smart list with execute and return the smart list id on success.
     * @param payload - Smartlist payload.
     * @return smartlistId that is created.
     * @throws Exception - if the smart list creation is not successful.
     */
    public SmartList createSmartListExecuteAndGetSmartList(String payload) throws Exception {
        ResponseObj responseObj = createSmartListAndExecute(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                SmartList smartList = mapper.convertValue(nsResponseObj.getData(), SmartList.class);
                return smartList;
            }
        }
        Log.error("Smart List Creation Response : " + responseObj.getContent());
        throw new RuntimeException("Failed to create smart list.");
    }

    /**
     * Create a smart list with execute
     * @param payload -Smartlist payload.
     * @return Http Response Object.
     * @throws Exception - is request failed to create a smartlist.
     */
    public ResponseObj createSmartListAndExecute(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_SMARTLIST_CREATE_EXECUTE, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Method which does cloning of a smartList
     *
     * @param payload - Smart list Metadata
     * @return - smartlist Object
     * @throws Exception - if request failed to clone a smart list.
     */
    public SmartList cloneSmartlist(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_SMARTLIST, header.getAllHeaders(), payload);
        if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if (nsResponseObj.isResult()) {
                SmartList smartList = mapper.convertValue(nsResponseObj.getData(), SmartList.class);
                return smartList;
            }
        }
        Log.error("Smart List Clone action Response : " + responseObj.getContent());
        throw new RuntimeException("Failed to Clone smart list.");
    }

    /**
     * Retrive the smart list with Id.
     * @param smartListId - Smartlist Id to retrive.
     * @return - Smart list Bean (or) in case there's no smart list with the Id.
     * @throws Exception - if Request failed to get the smart list.
     */
    public SmartList getSmartList(String smartListId) throws Exception {
        NsResponseObj nsResponseObj = getSmartListNsResponse(smartListId);
        if(nsResponseObj.isResult()) {
            SmartList smartList = mapper.convertValue(nsResponseObj.getData(), SmartList.class);
            return smartList;
        }
        Log.error("Smart List Not found.");
        return null;
    }

    /**
     * Get the smart list with NSResponse Object.
     * @param smartListId - Smart list Id to retrive.
     * @return - NsResponse Object - Basically used to validate MDA Error codes.
     * @throws Exception - In case failed to get he Smart list.
     */
    public NsResponseObj getSmartListNsResponse(String smartListId) throws Exception {
        ResponseObj responseObj  =getSmartListAndResponseObj(smartListId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //TODO - it should be bad request.
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to get smart list.");
    }

    /**
     * Get the smart list as Http Response Object.
     * @param smartlistId - Smart list id to retrieve.
     * @return Http Response Object.
     * @throws Exception - If failed to connect to server.
     */
    public ResponseObj getSmartListAndResponseObj(String smartlistId) throws Exception {
        if(smartlistId == null || smartlistId.isEmpty()) {
            throw new IllegalArgumentException("Smartlist Id is mandatory");
        }
        ResponseObj responseObj = wa.doGet(API_SMARTLIST + smartlistId, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Updates the Smart list metadata.
     * @param payload -
     * @return Updated Smart list bean class.
     * @throws Exception - if the result is not true i.e. update failed.
     */
    public SmartList updateSmartList(String payload) throws Exception {
        NsResponseObj nsResponseObj = updateSmartListNsResponse(payload);
        if(nsResponseObj.isResult()) {
            SmartList smartList = mapper.convertValue(nsResponseObj.getData(), SmartList.class);
            return smartList;
        }
        Log.error("Failed to update Smart List.");
        throw new RuntimeException("Failed to update smart list.");
    }

    /**
     * Update smart list.
     * @param payload
     * @return NsResponse Object if Http status code is 200 OK / 500.
     * @throws Exception - if server connection failed / Status code is none of the above.
     */
    public NsResponseObj updateSmartListNsResponse(String payload) throws Exception {
        ResponseObj responseObj  =updateSmartListResponseObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to get smart list.");
    }

    /**
     * Update Smart list and return Http response object.
     * @param payload - Smart list metadata
     * @return - Http Response Object.
     * @throws Exception -Exception in case server connection failed.
     */
    public ResponseObj updateSmartListResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPut(API_SMARTLIST_CREATE_EXECUTE, payload, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Retrieve all the smart lists
     * @return - Https Response Object.
     * @throws Exception - Server connection failed.
     */
    public ResponseObj getAllSmartListResponseObj() throws Exception {
        ResponseObj responseObj = wa.doGet(API_SMARTLIST, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Delete the smartlist with the given id.
     * @param smartlistId
     * @return Http Response Object.
     * @throws Exception - if failed to connect to server.
     */
    public ResponseObj deleteSmartListResponseObj(String smartlistId) throws Exception {
        if(smartlistId == null || smartlistId.isEmpty()) {
            throw new IllegalArgumentException("Smartlist Id is mandatory");
        }
        ResponseObj responseObj = wa.doDelete(API_SMARTLIST + smartlistId, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Update the name of the smart list.
     * @param smartListId - Smartlist Id.
     * @param name - Updated smart list name
     * @return - entire smart list with updated properties on success else exception.
     * @throws Exception - if update failed.
     */
    public SmartList updateSmartListName(String smartListId, String name) throws Exception {
        String payload = "{\"smartListId\":\""+smartListId+"\",\"name\":\""+name+"\"}";
        ResponseObj responseObj = wa.doPut(API_SMARTLIST_NAME_UPDATE, payload, header.getAllHeaders());
        Log.info("Response : " + responseObj.toString());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                SmartList smartList = mapper.convertValue(nsResponseObj.getData(), SmartList.class);
                return smartList;
            }
        }
        throw new RuntimeException("Failed While Updating the smart list name.");
    }

    /**
     * Get all the email templates.
     * @return List of email template bean objects.
     * @throws Exception - in case of failed to connect to server.
     */
    public List<EmailTemplate> getAllEmailTemplates() throws Exception {
        NsResponseObj nsResponseObj = getAllEmailTemplatesNsResponse();
        List<EmailTemplate> emailTemplates = new ArrayList<>();
        if(nsResponseObj.isResult()) {
            emailTemplates = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<EmailTemplate>>(){});
        }
        return emailTemplates;
    }

    /**
     * Get all the email templates.
     * @return Ns Response object
     * @throws Exception
     */
    public NsResponseObj getAllEmailTemplatesNsResponse() throws Exception {
        ResponseObj responseObj = getAllEmailTemplatesResponseObj();
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to get all email templates.");
    }

    /**
     * Get all email templates.
     * @return
     * @throws Exception
     */
    public ResponseObj getAllEmailTemplatesResponseObj() throws Exception {
        ResponseObj responseObj = wa.doGet(API_EMAIL_TEMPLATE, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Get single email template.
     * @param templateId
     * @return
     * @throws Exception
     */
    public EmailTemplate getEmailTemplate(String templateId) throws Exception {
        NsResponseObj nsResponseObj = getEmailTemplateNsResponseObj(templateId);
        if(nsResponseObj.isResult()) {
            EmailTemplate emailTemplate = mapper.convertValue(nsResponseObj.getData(), EmailTemplate.class);
            return emailTemplate;
        }
        throw new RuntimeException("Failed to get email template : "+templateId);
    }

    /**
     * Get the email template NsResponse object.
     * @param templateId
     * @return
     * @throws Exception
     */
    public NsResponseObj getEmailTemplateNsResponseObj(String templateId) throws Exception {
        ResponseObj responseObj = getEmailTemplateResponseObj(templateId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to get email template : "+templateId);
    }

    /**
     * Get email template Http response object.
     * @param templateId
     * @return
     * @throws Exception
     */
    public ResponseObj getEmailTemplateResponseObj(String templateId) throws Exception {
        if(templateId == null || templateId.isEmpty()) {
            throw new IllegalArgumentException("TemplateId Id is mandatory");
        }
        ResponseObj responseObj = wa.doGet(API_EMAIL_TEMPLATE + templateId, header.getAllHeaders());
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Delete Email template.
     * @param templateId
     * @return true in case on success else false.
     * @throws Exception -    if failed to connect to server.
     */
    public boolean deleteEmailTemplate(String templateId) throws Exception {
        ResponseObj responseObj = deleteEmailTemplateResponseObj(templateId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj.isResult();
        }
        return false;
    }

    /**
     * Delete Email template.
     * @param templateId
     * @return
     * @throws Exception
     */
    public ResponseObj deleteEmailTemplateResponseObj(String templateId) throws Exception {
        if(templateId == null || templateId.isEmpty()) {
            throw new IllegalArgumentException("TemplateId Id is mandatory");
        }
        ResponseObj responseObj = wa.doDelete(API_EMAIL_TEMPLATE + templateId, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Update email template.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj updateEmailTemplate(String payload) throws Exception {
        ResponseObj responseObj = wa.doPut(API_EMAIL_TEMPLATE, payload, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Create email template.
     * @param payload
     * @return email template bean object on successful template creation.
     * @throws Exception
     */
    public EmailTemplate createEmailTemplate(String payload) throws Exception {
        NsResponseObj nsResponseObj = createEmailTemplateNsResponseObj(payload);
        if(nsResponseObj.isResult()) {
            EmailTemplate emailTemplate = mapper.convertValue(nsResponseObj.getData(), EmailTemplate.class);
            return emailTemplate;
        }
        Log.error("Template creation failed.");
        throw new RuntimeException("Failed to Save Email Template.");
    }

    /**
     * Create Email Template.
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj createEmailTemplateNsResponseObj(String payload) throws Exception {
        ResponseObj responseObj = createEmailTemplateResponseObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        Log.error("Failed to Save Email Template.");
        throw new RuntimeException("Failed to Save Email Template.");
    }

    /**
     * Create email template.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj createEmailTemplateResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_EMAIL_TEMPLATE, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Retrieve all the outreaches.
     * @return - on success return all the email outreachs, else empty list.
     * @throws Exception - if failed to connect to server.
     */
    public List<OutReach> getAllOutReach() throws Exception {
        NsResponseObj nsResponseObj = getAllOutReachNsResponse();
        List<OutReach> outReaches = new ArrayList<>();
        if (nsResponseObj.isResult()) {
            outReaches = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<OutReach>>(){});
        }
        return outReaches;
    }

    /**
     * Get all the outreaches.
     * @return
     * @throws Exception
     */
    public NsResponseObj getAllOutReachNsResponse() throws Exception {
        ResponseObj responseObj = getAllOutreachResponseObj();
        Log.info("Response Obj :" + responseObj.toString());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        Log.error("Response Obj :" + responseObj.toString());
        throw new RuntimeException("Failed to get all the outreaches.");
    }

    /**
     * Get all outreaches.
     * @return
     * @throws Exception
     */
    public ResponseObj getAllOutreachResponseObj() throws Exception {
        ResponseObj responseObj =  wa.doGet(API_OUTREACH, header.getAllHeaders());
        //Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Create outreach and return the outreach bean object that's created.
     * @param payload
     * @return Outreach bean object
     * @throws Exception - failed to create a outreach.
     */
    public OutReach createOutReach(String payload) throws Exception {
        NsResponseObj nsResponseObj = createOutReachNsResponseObj(payload);
        if(nsResponseObj.isResult()) {
            OutReach outReach = mapper.convertValue(nsResponseObj.getData(), OutReach.class);
            return outReach;
        }
        throw new RuntimeException("Failed to create outreach.");
    }

    /**
     * Create a outreach.
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj createOutReachNsResponseObj(String payload) throws Exception {
        ResponseObj responseObj = createOutreachResponseObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK ||
                responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to create outreach.");
    }

    /**
     * Create a outreach.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj createOutreachResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_OUTREACH, header.getAllHeaders(), payload);
        Log.info("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Update the outreach and return outreach bean on successful outreach creation.
     * @param payload
     * @return -Outreach bean object.
     * @throws Exception - if failed to update the outreach.
     */
    public OutReach updateOutReach(String payload) throws Exception {
        NsResponseObj nsResponseObj = updateOutReachNsResponse(payload);
        if(nsResponseObj.isResult()) {
            OutReach outReach = mapper.convertValue(nsResponseObj.getData(), OutReach.class);
            return outReach;
        }
        Log.error("Update to outreach failed.");
        throw new RuntimeException("Failed to update outReach.");
    }

    /**
     * Update the outreach
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj updateOutReachNsResponse(String payload) throws Exception {
        ResponseObj responseObj = updateOutreachResponseObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed to update outReach.");
    }

    /**
     * Update the outreach.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj updateOutreachResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPut(API_OUTREACH, payload, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Get the outreach
     * @param outReachId - Outreach id to retrive.
     * @return - Outreach bean object.
     * @throws Exception - if failed to get the outreach / in case of invalid outreach id.
     */
    public OutReach getOutReach(String outReachId) throws Exception {
        NsResponseObj nsResponseObj = getOutReachNsResponse(outReachId);
        if(nsResponseObj.isResult()) {
            OutReach outReach = mapper.convertValue(nsResponseObj.getData(), OutReach.class);
            return outReach;
        }
        throw new RuntimeException("Failed get outreach : " +outReachId);
    }

    /**
     * Get the outreach.
     * @param outReachId
     * @return
     * @throws Exception
     */
    public NsResponseObj getOutReachNsResponse(String outReachId) throws Exception {
        ResponseObj responseObj = getOutreachResponseObj(outReachId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) { //TODO - It should be bad request.
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj;
        }
        throw new RuntimeException("Failed get outreach : " +outReachId);
    }

    /**
     * Get the outreach.
     * @param outReachId
     * @return
     * @throws Exception
     */
    public ResponseObj getOutreachResponseObj(String outReachId) throws Exception {
        ResponseObj responseObj = wa.doGet(API_OUTREACH + outReachId, header.getAllHeaders());
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Get list of execution history of a outreach.
     * @param outreachId
     * @return
     * @throws Exception
     */
    public List<OutReachExecutionHistory> getOutReachExecutionHistory(String outreachId) throws Exception {
        ResponseObj responseObj = getOutreachExecutionHistoryResponseObj(outreachId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                List<OutReachExecutionHistory> outReachExecutionHistoryList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<OutReachExecutionHistory>>(){});
                return outReachExecutionHistoryList;
            }
        }
        Log.error("Response Obj :" + responseObj.toString());
        throw new RuntimeException("Response Obj :" +responseObj.toString());
    }

    /**
     * Get outreach execution histroy.
     * @param outreachId
     * @return
     * @throws Exception
     */
    public ResponseObj getOutreachExecutionHistoryResponseObj(String outreachId) throws Exception {
        if(outreachId == null || outreachId.isEmpty()) {
            throw new IllegalArgumentException("OutreachId Id is mandatory");
        }
        ResponseObj responseObj = wa.doGet(String.format(API_OUTREACH_EXECUTION_HISTORY, outreachId), header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Delete outreach.
     * @param outReachId
     * @return true on successful deletion, false if failed to delete.
     * @throws Exception - failed to connect to server.
     */
    public boolean deleteOutReach(String outReachId) throws Exception {
        ResponseObj responseObj = deleteOutReachResponseObj(outReachId);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj.isResult();
        }
        return false;
    }

    /**
     * Delete Outreach
     * @param outReachId
     * @return
     * @throws Exception
     */
    public ResponseObj deleteOutReachResponseObj(String outReachId) throws Exception {
        if(outReachId ==null) {
            throw new RuntimeException("Out Reach Id should not be null.");
        }
        Log.info("Deleting OutReach....");
        ResponseObj responseObj = wa.doDelete(API_OUTREACH + outReachId, header.getAllHeaders());
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Trigger the outreach
     * @param outreachId
     * @param payload    - Especially used to Trigger Test runs, but can be used for both purposes.
     * @return Status Id of the outreach triggered on success.
     * @throws Exception - if trigger is failed / failed to connect to server.
     */
    public String triggerOutReach(String outreachId, String payload) throws Exception {
        String statusId = null;
        ResponseObj responseObj = triggerOutreachResponseObj(outreachId, payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if (nsResponseObj.isResult()) {
                HashMap<String, String> resultSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                statusId = resultSet.get("statusId");
            }
        }
        if(statusId ==null) {
            throw new RuntimeException(responseObj.toString());
        }
        return statusId;
    }

    /**
     * Trigger the outreach and get the status id.
     * @param outreachId
     * @return
     * @throws Exception
     */
    public String triggerOutReach(String outreachId) throws Exception {
        String payload = "{\"parameters\":{\"ruleRunDate\":\""+ DateUtil.addDays(Calendar.getInstance(), 0, "yyyy-MM-dd")+"\",\"ruleType\":\"CAMPAIGN\",\"campaignId\":\"$OUTREACHID\"}}";
        payload = payload.replace("$OUTREACHID", outreachId);
        return triggerOutReach(outreachId, payload);
    }

    /**
     * Trigger the outreach.
     * @param outreachId - Outreach to trigger.
     * @param payload - Payload.
     * @return - Http response object.
     * @throws Exception - if failed to connect to server.
     */
    public ResponseObj triggerOutreachResponseObj(String outreachId, String payload) throws Exception {
        Log.info("Triggering Outreach...");
        ResponseObj responseObj = wa.doPost(String.format(API_OUTREACH_RUN, outreachId), header.getAllHeaders(), payload);
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Get the smart list schedules.
     * @param smartListId
     * @return
     * @throws Exception
     */
    public List<Schedule> getSmartListSchedules(String smartListId) throws Exception {
        String payload = smartListId == null ? "{\"jobType\":\"SMARTLIST\"}" : "{\"jobType\":\"SMARTLIST\", \"jobIdentifier\":\""+smartListId+"\"}";
        return getAllSchedules(payload);
    }

    /**
     * Get all the smart lust schedules.
     * @return list of schedule bean object.
     * @throws Exception
     */
    public List<Schedule> getAllSmartListSchedules() throws Exception {
        String payload = "{\"jobType\":\"SMARTLIST\"}";
        return getAllSchedules(payload);
    }

    /**
     * Get all outreach schedules.
     * @param outReachId
     * @return
     * @throws Exception
     */
    public List<Schedule> getOutReachSchedules(String outReachId) throws Exception {
        String payload = outReachId == null ? "{\"jobType\":\"CAMPAIGN\"}" : "{\"jobType\":\"CAMPAIGN\", \"jobIdentifier\":\""+outReachId+"\"}";
        return getAllSchedules(payload);
    }

    /**
     * Get all the schedules of outreach's
     * @return - list of all schedules.
     * @throws Exception
     */
    public List<Schedule> getAllOutReachSchedules() throws Exception {
        String payload = "{\"jobType\":\"CAMPAIGN\"}";
        return getAllSchedules(payload);
    }

    /**
     * Get all the schedules of outreach's
     * @param payload
     * @return List of all the schedules.
     * @throws Exception - if failed to connect to server.
     */
    public List<Schedule> getAllSchedules(String payload) throws Exception {
        List<Schedule> scheduleList = null;
        ResponseObj responseObj = getAllSchedulesResposeObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK)  {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                 scheduleList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<Schedule>>(){});
            }
        }
        return scheduleList;
    }

    /**
     * Get all schedules - Http Response Object.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj getAllSchedulesResposeObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_SCHEDULES_ALL, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Get the Outreach Schedule.
     * @param payload
     * @return schedule bean object.
     * @throws Exception - if failed to connect to server / server reponse code is not 200 OK.
     */
    public Schedule scheduleOutReach(String payload) throws Exception {
        ResponseObj responseObj = scheduleOutReachResponseObj(payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                Schedule schedule = mapper.convertValue(nsResponseObj.getData(), Schedule.class);
                return schedule;
            }
        }
        throw new RuntimeException("Response Obj :" +responseObj.toString());
    }

    /**
     * Get Out Reach Schedule - Http Response Object.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj scheduleOutReachResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_SCHEDULE, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Update the schedule.
     * @param payload - Schedule payload.
     * @return - Updated Schedule bean on success, runtime exception on failure.
     * @throws Exception
     */
    public Schedule updateSchedule(String payload) throws Exception {
        ResponseObj responseObj = wa.doPut(API_SCHEDULE, payload, header.getAllHeaders());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                Schedule schedule = mapper.convertValue(nsResponseObj.getData(), Schedule.class);
                return schedule;
            }
        }
        throw new RuntimeException("Failed to Update Scdhuele : " +responseObj.toString());
    }

    /**
     * Hit the un-Subscribe link & return the Http Response.
     * This is the link that's displayed in footer of an non-transactional email.
     * @param postFixUrl - &t=tenantid(base64 encoded)&d=({"gsid":"****"})base64 encoded.
     * @return - Http response that contains the entire page HTML.
     * @throws Exception - if failed to connect to server.
     */
    public ResponseObj checkUnScribeLink(String postFixUrl) throws Exception {
        Header tempHeader = new Header();
        tempHeader.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        ResponseObj responseObj = wa.doGet(API_SUBSCRIBE_EMAIL + postFixUrl, tempHeader.getAllHeaders());
        Log.debug("Response Obj :" + responseObj.toString());
        return responseObj;
    }

    /**
     * Un Subscribe - the participant from survey / Success Communications.
     * @param nameValuePairList - This are the properties & reasons for un-subscribing.
     * @return - Http Response Object.
     * @throws Exception
     */
    public ResponseObj unSubcribe(List<org.apache.http.NameValuePair> nameValuePairList) throws Exception {
        Header tempHeader = new Header();
        tempHeader.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        ResponseObj responseObj = wa.doPost(API_SUBSCRIBE_EMAIL,tempHeader.getAllHeaders(), new UrlEncodedFormEntity(nameValuePairList));
        return responseObj;
    }

    /**
     * Get smart list data.
     * @param smartListId - Smart list id
     * @param records - No of records to be retrieved if 0 then all the records / TOP 100 will be fetched else records will be filtered.
     * @return data as list of map.
     * @throws Exception
     */
    public List<HashMap<String, Object>> getSmartListData(String smartListId, int records) throws Exception {
        ResponseObj responseObj = getSmartListDataResponseObj(smartListId, records);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                JsonNode node = mapper.readTree(responseObj.getContent());
                JsonNode dataNode = node.get("data");
                if(dataNode != null) {
                    List<HashMap<String, Object>> data = mapper.readValue(dataNode.get("result"), new TypeReference<ArrayList<HashMap>>(){});
                    return data;
                }
            }
        }
        throw new RuntimeException("Failed to get data.");
    }

    /**
     * Search the smart list data on server side.
     * @param smartListId - Smart list id.
     * @param payload - Search criteria.
     * @return Filtered records.
     * @throws Exception - if failed to connect to server / server returned status other than 200 OK.
     */
    public List<HashMap<String, Object>> searchSmartList(String smartListId, String payload) throws Exception {
        ResponseObj responseObj =searchSmartListResponseObj(smartListId, payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            JsonNode content = mapper.readTree(responseObj.getContent());
            if(content.get("result").asBoolean()) {
                List<HashMap<String, Object>> data = mapper.readValue(content.get("data"), new TypeReference<ArrayList<HashMap>>() {
                });
                return data;
            }
        }
        throw new RuntimeException("Failed to get the smart list search results");
    }

    /**
     * Search smartlist data.
     * @param smartListId
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj searchSmartListResponseObj(String smartListId, String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(String.format(API_SMARTLIST_SEARCH, smartListId), header.getAllHeaders(), payload);
        return responseObj;
    }

    /**
     * Get the smart list data.
     * @param smartListId
     * @param records - if 0 then no filter is applied.
     * @return Https response object.
     * @throws Exception
     */
    public ResponseObj getSmartListDataResponseObj(String smartListId, int records) throws Exception {
        if(smartListId == null || smartListId.isEmpty()) {
            throw new IllegalArgumentException("SmartListId Id is mandatory");
        }
        String url = String.format(API_SMARTLIST_DATA, smartListId) +(records > 0 ? "?numberOfRecords="+records : "");
        ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Trigger reSync of smart list.
     * @param smartListId
     * @return Status Id of reSync on success, null other wise.
     * @throws Exception - Failed to connect to server.
     */
    public String reSyncSmartListData(String smartListId) throws Exception {
        String payload = "{\"format\":\"yyyy-MM-dd\",\"ruleDate\":\""+ DateUtil.addDays(Calendar.getInstance(), 0, "yyyy-MM-dd")+"\",\"isTestRun\":false}";
        String statusId = null;
        ResponseObj responseObj = reSyncSmartListDataResponseObj(smartListId, payload);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                HashMap<String, String> resultSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                statusId = resultSet.get("statusId");
            }
        }
        return statusId;
    }

    /**
     * Trigger re-Sync of Smart list.
     * @param smartListId
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj reSyncSmartListDataResponseObj(String smartListId, String payload) throws Exception {
        if(smartListId == null || smartListId.isEmpty()) {
            throw new IllegalArgumentException("SmartListId Id is mandatory");
        }
        ResponseObj responseObj = wa.doPost(String.format(API_SMARTLIST_DATA_RESYNC, smartListId), header.getAllHeaders(), payload);
        Log.debug("Response Obj : " +responseObj.toString());
        return responseObj;
    }

    /**
     * Get Email Templates OutReach Information i.e. Email templates usage in different outreachs
     * @param emailTemplateIdList - List of email template Id's
     * @return
     * @throws Exception
     */
    public HashMap<String, List<HashMap<String,String>>> getEmailTemplatesOutReachInfo(String[] emailTemplateIdList) throws Exception {
        ResponseObj responseObj = getEmailTemplatesOutReachInfoResponseObj(mapper.writeValueAsString(emailTemplateIdList));
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            JsonNode response = mapper.readTree(responseObj.getContent());
            if(response.get("result") !=null && response.get("result").asBoolean()) {
                JsonNode dataNode = response.get("data") !=null ? response.get("data").get("templateOutreachInfo") : null;
                if(dataNode != null) {
                    HashMap<String, List<HashMap<String,String>>> referenceDatSet = mapper.readValue(dataNode, new TypeReference<HashMap<String, ArrayList<HashMap<String, String>>>>() {});
                    return referenceDatSet;

                } else {
                    throw new RuntimeException("Data / templateOutreachInfo is not found.");
                }
            }
        }
        throw new RuntimeException("Failed to get email template usage info.");
    }

    /**
     * Get Email Templates OutReach Usage Info.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj getEmailTemplatesOutReachInfoResponseObj(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_EMAIL_TEMPLATE_OUTREACH_INFO, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Get Smart lists execution history.
     * @return
     * @throws Exception
     */
    public HashMap<String, List<RuleExecutionHistory>> getAllSmartListStatus() throws Exception {
        ResponseObj responseObj = getAllSmartListStatusResponseObj();
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            JsonNode content = mapper.readTree(responseObj.getContent());
            if(content.get("result").asBoolean()) {
                JsonNode statusDetails = content.get("data").get("statusDetails");
                HashMap<String, List<RuleExecutionHistory>> executionHistory = mapper.readValue(statusDetails, new TypeReference<HashMap<String, ArrayList<RuleExecutionHistory>>>() {
                });
                return executionHistory;
            }
        }
        Log.error("Failed to get all smart list status.");
        throw new RuntimeException("Failed to get all smart list status.");
    }

    /**
     * Get all smart list status.
     * @return
     * @throws Exception
     */
    public ResponseObj getAllSmartListStatusResponseObj() throws Exception {
        String payload = "{\"sources\" : [\"POWER_LIST\"]}";
        return getCopilotFeatureStatus(payload);
    }

    /**
     * Get all outreach execution stats.
     * @return
     * @throws Exception
     */
    public HashMap<String, RuleExecutionHistory> getAllOutReachExecutionStats() throws Exception {
        ResponseObj responseObj = getAllOutReachExecutionStatusResponseObj();
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            JsonNode content = mapper.readTree(responseObj.getContent());
            if(content.get("result").asBoolean()) {
                JsonNode statusDetails = content.get("data").get("statusDetails");
                HashMap<String, RuleExecutionHistory> executionHistory = mapper.readValue(statusDetails, HashMap.class);
                return executionHistory;
            }
        }
        Log.error("Failed to get all smart list status.");
        throw new RuntimeException("Failed to get all smart list status.");
    }

    /**
     * Get out Reach execution Status.
     * @return
     * @throws Exception
     */
    public ResponseObj getAllOutReachExecutionStatusResponseObj() throws Exception {
        String payload = "{\"sources\" : [\"OUTREACH\"]}";
        return getCopilotFeatureStatus(payload);
    }

    /**
     * Get Copilot Feature Status - Like Smart list / Outreach status.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj getCopilotFeatureStatus(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_COPILOT_FEATURES_STATUS, header.getAllHeaders(), payload);
        Log.debug("Response Obj : " + responseObj.toString());
        return responseObj;
    }

    /**
     * Get all smart lists.
     * @return
     * @throws Exception
     */
    public List<SmartList> getAllSmartList() throws Exception {
        ResponseObj responseObj = getAllSmartListResponseObj();
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                List<SmartList> smartLists = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<SmartList>>(){});
                return  smartLists;
            }
        }
        Log.error("Failed to get SmartLists.." + responseObj.getContent());
        throw new RuntimeException("Failed to get SmartLists.");
    }

    /**
     * Delete smart list.
     * @param smartListId
     * @return true on success - false on failure.
     */
    public boolean deleteSmartList(String smartListId) {
        boolean result = false;
        try {
            ResponseObj responseObj = deleteSmartListResponseObj(smartListId);
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result  = nsResponseObj.isResult();
            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Get Email Log Collection Master.
     * @return Collection Info.
     * @throws Exception - Failed to get the collection master.
     */
    public CollectionInfo getEmailLogsCollection() throws Exception {
        ResponseObj responseObj = wa.doGet(API_COPILOT_EMAIL_LOG_ANALYTICS, header.getAllHeaders());
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                CollectionInfo emailLogCollectionMaster = mapper.convertValue(nsResponseObj.getData(), CollectionInfo.class);
                return emailLogCollectionMaster;
            }
        }
        Log.error("Response Obj " +responseObj.toString());
        throw new RuntimeException("Failed to ger Email Logs Collection Master");
    }

    /**
     * Preview of outreach
     * @param outreachId
     * @param payload
     * @return
     * @throws Exception
     */
    public String previewOutReachAndGetNsResponse(String outreachId, String payload) throws Exception {
        String data = null;
        try {
            ResponseObj responseObj = wa.doPost(String.format(API_OUTREACH_PREVIEW, outreachId), header.getAllHeaders(), payload);
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    nsResponseObj.getData().toString();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while getting preview response " + ex);
        }
        return data;
    }

    /**
     * Image reSize used in email template.
     * @param payload
     * @return
     * @throws Exception
     */
    public ResponseObj resizeImage(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_IMAGE_RESIZE, header.getAllHeaders(), payload);
        return responseObj;
    }

    /**
     * Update email Template Name.
     * @param templateId
     * @param templateName
     * @return
     * @throws Exception
     */
    public EmailTemplate updateEmailTemplateName(String templateId, String templateName) throws Exception {
        ResponseObj responseObj = updateEmailTemplateNameResponseObj(templateId, templateName);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                EmailTemplate emailTemplate = mapper.convertValue(nsResponseObj.getData(), EmailTemplate.class);
                return emailTemplate;
            }
        }
        Log.error("Failed to update Email Template Name, ReponseObj " +responseObj.toString());
        throw new RuntimeException("Failed to update Email Template Name.");
    }

    /**
     * Update email template Name.
     * @param templateId
     * @param templateName
     * @return
     * @throws Exception
     */
    public ResponseObj updateEmailTemplateNameResponseObj(String templateId, String templateName) throws Exception {
        String payload = "{\"templateId\":\""+templateId+"\",\"title\":\""+templateName+"\"}";
        ResponseObj responseObj = wa.doPut(API_EMAIL_TEMPLATE_NAME_UPDATE, payload, header.getAllHeaders());
        return responseObj;
    }

    /**
     * Update outreach Name.
     * @param outReachId
     * @param outReachName
     * @return
     * @throws Exception
     */
    public OutReach updateOutReachName(String outReachId, String outReachName) throws Exception {
        ResponseObj responseObj = updateOutReachNameResponseObj(outReachId, outReachName);
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                OutReach outReach = mapper.convertValue(nsResponseObj.getData(), OutReach.class);
                return outReach;
            }
        }
        Log.error("Failed to update Email Template Name, ReponseObj " +responseObj.toString());
        throw new RuntimeException("Failed to update Email Template Name.");
    }

    /**
     * Update Outreach Name.
     * @param outReachId
     * @param outReachName
     * @return
     * @throws Exception
     */
    public ResponseObj updateOutReachNameResponseObj(String outReachId, String outReachName) throws Exception {
        String payload = "{\"campaignId\": \""+outReachId+"\", \"name\": \""+outReachName+"\"}";
        ResponseObj responseObj = wa.doPut(API_OUTREACH_NAME_UPDATE, payload, header.getAllHeaders());
        return responseObj;
    }

    /**
     * Save the smart list with out execute - used in clone mosstly.
     * @param payLoad
     * @return
     * @throws Exception
     */
    public ResponseObj saveSmartList(String payLoad) throws Exception {
        ResponseObj responseObj = wa.doPost(API_SMARTLIST_SAVE, header.getAllHeaders(), payLoad);
        return responseObj;
    }

    /**
     * Get the Smart list out from the list of smartlist beans.
     * @param smartListList
     * @param smartListId
     * @return
     */
    public SmartList getSmartList(List<SmartList> smartListList, String smartListId) {
        if(smartListId ==null || smartListList ==null) {
            throw new IllegalArgumentException("Smart list Lists and smart list id are required.");
        }
        for(SmartList smartList : smartListList) {
            if(smartListId.equals(smartList.getSmartListId())) {
                return smartList;
            }
        }
        return null;
    }

    /**
     * Verify email template.
     * @param expTemplate
     * @param actualTemplate
     * @return
     */
    public boolean verifyEmailTemplate(EmailTemplate expTemplate, EmailTemplate actualTemplate) {
        Verifier verifier = new Verifier();
        boolean result = false;
        verifier.verifyEquals(expTemplate.getTitle(), actualTemplate.getTitle(), "Tiitle Not Matched.");
        verifier.verifyEquals(expTemplate.getSubject(), actualTemplate.getSubject(), "Subject Not Matched.");
        HashMap<String, EmailTemplate.TokenMetaData> expTokens = expTemplate.getTokens();
        HashMap<String, EmailTemplate.TokenMetaData> actualTokens = actualTemplate.getTokens();
        for(String s : expTokens.keySet()) {
            if(actualTokens.get(s)!=null) {
                verifier.verifyEquals(expTokens.get(s).getDisplayName(), actualTokens.get(s).getDisplayName(), "Display Name Not Matched for token :"+s);
                verifier.verifyEquals(expTokens.get(s).getTokenType(), actualTokens.get(s).getTokenType(), "Token type Not Matched for token : "+s);
                verifier.verifyEquals(expTokens.get(s).getDefaultValue(), actualTokens.get(s).getDefaultValue(), "Default value not matched for token :" +s);
            } else {
                verifier.fail("Token not present "+s);
            }
        }
        result = !verifier.isVerificationFailed();
        Log.error("Verifier Messages "+verifier.getAssertMessages().toString());
        return result;
    }

    /**
     * Get Email Template from list of templates.
     * @param emailTemplateList
     * @param tempateId
     * @return
     */
    public EmailTemplate getEmailTemplate(List<EmailTemplate> emailTemplateList, String tempateId) {
        for(EmailTemplate emailTemplate : emailTemplateList)  {
            if(tempateId.equals(emailTemplate.getTemplateId())) {
                return emailTemplate;
            }
        }
        throw new NoSuchElementException("Template Id Not found : " +tempateId);
    }

    /**
     * Trigger sendgrid events.
     * @param payload
     * @return
     * @throws Exception
     */
    public boolean sendSendGridWebHookEvents(String payload) throws Exception {
        ResponseObj responseObj = wa.doPost(API_WEB_HOOK_SENDGRID, tempHeader.getAllHeaders(), payload);
        Log.info("Response Obj :" +responseObj.toString());
        return responseObj.getStatusCode() == HttpStatus.SC_OK;
    }

    /**
     * Trigger send grid events one by one using thread's.
     * @param events
     * @return
     * @throws Exception
     */
    public boolean sendSendGridWebHookEvents(List<String> events) throws Exception {
        List<HttpEntity> entities = new ArrayList<>();
        for(String s : events) {
            entities.add(new StringEntity(s));
        }
        boolean result = false;
        int successEvents = EventManager.submitEvents(tempHeader.getAllHeaders(), API_WEB_HOOK_SENDGRID, entities);
        if(successEvents == events.size()) {
            result = true;
        }
        return result;
    }

    /**
     * Trigger mandrill webhook events one by one.
     * @param events
     * @return
     * @throws Exception
     */
    public boolean sendMandrillWebHookEventsOneByOne(List<MandrillWebhookEvent> events) throws Exception {
        boolean result = false;
        int successCount = 0;
        for(MandrillWebhookEvent event : events) {
            String actualJson = "["+mapper.writeValueAsString(event)+"]";

            Header tempHeader = new Header();
            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair(MANDRILL_POST_PARAM_NAME, actualJson));
            String url = NS_URL.substring(0, NS_URL.indexOf(".com")+4)+EMAIL_WEB_HOOK_MANDRILL;
            Log.debug("URL :" +url);
            Log.debug("Event :" + actualJson);
            Log.debug("Mandrill :" +nsConfig.getMandrillWebHookSecret());
            StringBuilder signedData = new StringBuilder(actualJson.length() + 200);
            signedData.append(url);
            signedData.append(MANDRILL_POST_PARAM_NAME);
            signedData.append(actualJson);
            tempHeader.addHeader("X-Mandrill-Signature", CryptHandler.calculateRFC2104HMAC(signedData.toString(), nsConfig.getMandrillWebHookSecret()));
            ResponseObj responseObj = wa.doPost(API_WEB_HOOK_MANDRILL, tempHeader.getAllHeaders(), new UrlEncodedFormEntity(nameValuePairs));
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                successCount++;
            } else {
                Log.error("Missed Event : "+ event+"Response Data : "+responseObj.toString());
            }
        }
        if(events.size()==successCount) {
            result = true;
        }
        return result;
    }

    /**
     * Trigger mandrill events in bulk i.e. all the events in single API CALL.
     * @param events
     * @return
     * @throws Exception
     */
    public boolean sendMandrillWebHookEventsInBulk(List<MandrillWebhookEvent> events) throws Exception {
        Header tempHeader = new Header();
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        String actualJson = mapper.writeValueAsString(events);
        nameValuePairs.add(new BasicNameValuePair(MANDRILL_POST_PARAM_NAME, actualJson));
        String url = NS_URL.substring(0, NS_URL.indexOf(".com")+4)+EMAIL_WEB_HOOK_MANDRILL;
        StringBuilder signedData = new StringBuilder(actualJson.length() + 200).append(url).append(MANDRILL_POST_PARAM_NAME).append(actualJson);
        tempHeader.addHeader("X-Mandrill-Signature", CryptHandler.calculateRFC2104HMAC(signedData.toString(), nsConfig.getMandrillWebHookSecret()));
        ResponseObj responseObj = wa.doPost(API_WEB_HOOK_MANDRILL, tempHeader.getAllHeaders(), new UrlEncodedFormEntity(nameValuePairs));
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            return true;
        } else {
            Log.error("Reponse Objec :" +responseObj.toString());
            return false;
        }
    }

    /**
     * Get list of emailid valid properties in gainsight - like are they bounced / un-subscribed.
     * @param emailList
     * @return List of email properties.
     * @throws Exception
     */
    public List<EmailProperties> getEmailValidateProperties(List<String> emailList) throws Exception {
        if(emailList ==null) {
            throw new IllegalArgumentException("Email List can be null.");
        }

        ResponseObj responseObj = wa.doPost(API_EMAIL_VALIDATE, header.getAllHeaders(), mapper.writeValueAsString(emailList));
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                List<EmailProperties> emailPropertiesList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<EmailProperties>>() {
                });
                return emailPropertiesList;
            }
        }
        throw new RuntimeException("Failed to get email properties :" +responseObj.toString());
    }

    /**
     * Knock off the email id from Gainsight black listed once's i.e. from un-subscribe & bounced.
     * @param emailPropertiesList
     * @return
     * @throws Exception
     */
    public boolean knockOffEmail(List<EmailProperties> emailPropertiesList) throws Exception {
        if(emailPropertiesList == null) {
            throw new IllegalArgumentException("Email Properties should not be null.");
        }
        ResponseObj responseObj = wa.doPost(API_EMAIL_KNOCK_OFF, header.getAllHeaders(), mapper.writeValueAsString(emailPropertiesList));
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            return nsResponseObj.isResult();
        }
        Log.error("Response Obj :" +responseObj.toString());
        return false;
    }
}
