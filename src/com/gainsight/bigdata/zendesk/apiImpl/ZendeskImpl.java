package com.gainsight.bigdata.zendesk.apiImpl;


import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.zendesk.pojos.TicketLookup;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Abhilash Thaduka on 2/24/2016.
 */
public class ZendeskImpl {

    private ObjectMapper mapper = new ObjectMapper();
    private WebAction wa = new WebAction();
    Header header;

    public ZendeskImpl(Header header) {
        this.header = header;
    }

    /**
     * Method to create zendesk org to Sfdc account lookup
     *
     * @param ticketLookup
     * @return
     * @throws Exception
     */
    public boolean createLookup(TicketLookup ticketLookup) throws Exception {
        return createLookup(mapper.writeValueAsString(ticketLookup));
    }

    /**
     * Method to create zendesk org to Sfdc account lookup
     *
     * @param payload
     * @return true if creation is  successful else false
     * @throws Exception
     */
    public boolean createLookup(String payload) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPost(API_ZENDESK_ORGANIZATION_LOOKUP, header.getAllHeaders(), payload);
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating lookup " + e);
        }
        return result;
    }

    /**
     * Method to delete lookup between zendesk org to Sfdc account lookup
     *
     * @param organizationId
     * @return -  true if deletion successful else false
     * @throws Exception
     */
    public boolean deleteOrganizationLookup(String organizationId) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doDelete(String.format(API_ZENDESK_DELETE_ORGANIZATION_LOOKUP, organizationId), header.getAllHeaders());
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while Deleting Zendesk Org to SFDC customer lookup " + ex);
        }
        return result;
    }

    /**
     * Method zendesk to sfdc common proxy API
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public NsResponseObj zendeskSfdcProxy(String payload) throws Exception {
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = wa.doPost(API_ZENDESK_ALL_IN_ONE_SFDC_PROXY, header.getAllHeaders(), payload);
            Log.info("HttpResponse Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while Deleting Zendesk Org to SFDC customer lookup " + ex);
        }
        return nsResponseObj;
    }

    /**
     * Method to link zendesk ticket and SFDC CTA
     *
     * @param payload  -  payload
     * @param ticketId - ticketId
     * @param ctaId    - ctaId
     * @return
     * @throws Exception
     */
    public boolean linkTicketToCTA(String payload, String ticketId, String ctaId) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPut(String.format(API_ZENDESK_LINK_CTA, ticketId, ctaId), payload, header.getAllHeaders());
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while linking Zendesk ticket to SFDC CTA " + ex);
        }
        return result;
    }

    /**
     * Method to Get the associated CTA linked to a Zendesk ticket
     *
     * @param ticketId - ticketId
     * @return -  ctaID
     * @throws Exception
     */
    public String getCtaByZendeskTicket(String ticketId) throws Exception {
        String ctaID = null;
        try {
            ResponseObj responseObj = wa.doGet(String.format(API_ZENDESK_TICKET_TO_SFDC_CTA, ticketId), header.getAllHeaders());
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                ctaID = nsResponseObj.getData().toString();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while linking Zendesk ticket to SFDC CTA " + ex);
        }
        return ctaID;
    }

    /**
     * Method to Unlink CTA linked to a Zendesk ticket
     *
     * @param ticketId - ticketId
     * @return -  ctaID
     * @throws Exception
     */
    public boolean unLinkTicketToCTA(String ticketId) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doDelete(String.format(API_ZENDESK_UNLINK_CTA, ticketId), header.getAllHeaders());
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while Un-linking Zendesk ticket to SFDC CTA " + ex);
        }
        return result;
    }

    /**
     * Method to create zendesk sync schedule
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public boolean createSyncSchedule(String payload) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doPost(API_ZENDESK_CREATE_DELETE_SYNC_SCHEDULE, header.getAllHeaders(), payload);
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while creating sync schedule" + ex);
        }
        return result;
    }

    /**
     * Method to delete sync scheduler
     *
     * @param payload
     * @return
     * @throws Exception
     */
    public boolean deleteSyncSchedule(String payload) throws Exception {
        boolean result = false;
        try {
            ResponseObj responseObj = wa.doDeleteWithBody(API_ZENDESK_CREATE_DELETE_SYNC_SCHEDULE, header.getAllHeaders(), payload);
            Log.info("Response Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while deleting sync schedule" + ex);
        }
        return result;
    }

    /**
     * Method to do zendesk tickets Sync based on sync time provided
     *
     * @param syncTime
     * @return
     * @throws Exception
     */
    public NsResponseObj doSync(String syncTime) throws Exception {
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = wa.doGet(String.format(API_ZENDESK_SYNC, syncTime), header.getAllHeaders());
            Log.info("HttpResponse Object:--> " + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while Doing Sync " + ex);
        }
        return nsResponseObj;
    }
}
