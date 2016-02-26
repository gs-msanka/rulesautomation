package com.gainsight.bigdata.urls;

/**
 * Created by Giribabu on 08/05/15.
 *
 */

public interface ApiUrls extends NSURLs {

    String SEND_GRID_SETTINGS                   = NS_URL + "/api/sendgrid/settings";
    String APP_API_TOKENS                       = NS_URL + "/api/tokens";
    String APP_API_TENANT_PROVISION             = NS_URL + "/api/tenants/provision";
    String APP_API_DATA_LOAD_COLLECTIONS        = NS_URL + "/api/dataload/collections";
    String APP_API_EVENTRULE                    = NS_URL + "/api/eventrule";
    String APP_API_RULES_LOADABLE_OBJECT        = NS_URL + "/api/rulesloadableobject";

    //Reporting API
    String API_REPORT_RUN                       = NS_URL + "/api/reports/run/preparation";
    String API_REPORT_RUN_LINKS                 = NS_URL + "/api/reports/run/preparation/links";
    String API_REPORT                           = NS_URL + "/api/reports/";                         //Append Report ID, To get report metadata.
    String API_REPORT_GET_ALL                   = NS_URL + "/api/reports/reporting_v2";             //To get all the created reports.
    String API_COLLECTION_ALL_LITE              = NS_URL + "/api/collections/all/lite";             //To get all the Collections Master details - Lite.
    String API_COLLECTION_TREE                  = NS_URL + "/api/collections/%s/collectionTree";    //To get the collection tree that has lookup information.
    String API_REPORT_EXPORT_EXCEL              = NS_URL + "/api/export/excel";                     //To export the report to a excel.
    String API_REPORT_EXPORT_CHART              = NS_URL + "/api/export/chart";                     //To export the report chart.
    String API_REPORT_EXPORT_MASTER             = NS_URL + "/api/reports/exportmaster/";            //Append the layout id to get the scheduled information and email template information that is saved for a layout.
    String API_REPORT_PPTX_METADATA             = NS_URL + "/qbr/exports/pptx/documents/%s/metadata"; //Get the metadata of the PPTX file uploaded.
    String API_REPORT_DASHBOARD_TEST_EMAIL      = NS_URL + "/api/export/dashboard/pptx";               //Trigger the send email.
    String API_REPORT_EXPORT_PPT                = NS_URL + "/qbr/exports/reports/%s/pptx";             //Provided Report(Success Snapshot id) exported via Success Snapshot
    String API_REPORT_EXPORT_EMAILDATA          = NS_URL + "/api/export/emailData";                    //Export the report to CSV format


    String API_RULE_RUN                         = NS_URL + "/api/eventrule";
    String RULE_PREVIEW_RESULTS                 = NS_URL + "/api/eventrule/%s/result";
    String APP_API_GET_COLLECTION               = NS_URL + "/api/collections/";
    String APP_API_ASYNC_STATUS			        = NS_URL + "/api/async/process/";

    String MDA_SFDC_ACCOUNT_EXISTS              = NS_URL + "/api/accounts/SFDC/exists";
    String MDA_AUTH_REVOKE                      = NS_URL + "/api/accounts/sfdc?accountType=SFDC";
    String CREATE_CONNECTORS_PROJECT            = NS_URL + "/api/tokens/v2";
    String API_TOKENS_EXISTS_GET                = NS_URL + "/api/tokens/exists";    //Check if access key is present for the system.
    String ACCOUNT_DETAIL_GET                   = NS_URL + "/api/accounts/";        //Append Account Id to get account details.
    String COLLECTION_MASTER_GET                = NS_URL + "/api/collections/";       //Append Collection Id to get Collection Master details.
    String ACCOUNT_DELETE                       = NS_URL + "/api/accounts/accountId/";
    String CONNECTOR_DEFAULT_PUT                = NS_URL + "/api/accounts/accountType/%s/default/true";
    String CONNECTOR_DEACTIVATE_PUT             = NS_URL + "/api/accounts/accountType/%s/default/false";

    //MixPanel.
    String MIX_PANEL_CREATE                 = NS_URL + "/api/accounts/mixpanel/create";
    String MIX_PANEL_PROPERTY               = NS_URL + "/api/mixpanel/accounts/";
    String MIX_PANEL_PROJECT_GET            = NS_URL + "/api/accounts/integrations/MIXPANEL";

    //Segment IO.
    String SEGMENT_ACCOUNT_GET              = NS_URL + "/api/accounts/integrations/SEGMENT_IO";
    String SEGMENT_ACCESS_KEY_GET           = NS_URL + "/api/tokens/integrations/SEGMENT_IO/projects/"; //Append Account ID to get access-key for segmentIO project.

    //Data Load
    String DATA_LOAD_AUTHENTICATE           = NS_URL + "/admin/dataload/authenticate";
    String DATA_LOAD_COLLECTIONS            = NS_URL + "/admin/collections";
    String DATA_LOAD_GET_COLLECTIONS_ALL    = NS_URL + "/admin/collections/ALL";
    String DATA_LOAD_STATUS                 = NS_URL + "/admin/dataload/status/"; //Status id should be appended.
    String DATA_LOAD_IMPORT                 = NS_URL + "/admin/dataload/import";
    String DATA_LOAD_EXPORT_FAILURES        = NS_URL + "/admin/dataload/export/failure/status/"; //Status id should be appended.

    //Google Analytics.
    String GA_PROJECT_GET                   = NS_URL + "/api/accounts/integrations/GOOGLE_ANALYTICS";
    String GA_PROPERTIES_GET                = NS_URL + "/api/googleanalytics/";

    //DataLoad Agg API End Points.
    String DATA_API_GET                     = NS_URL + "/api/accounts/integrations/DATA_API";       //All the project s configured in data load api.
    String DATA_API_ALL_COLLECTIONS_GET     = NS_URL + "/api/collections/consumers/DATA_API";       //All the collections for the tenant,
    String DATA_API_PROJECT_UPDATE_PUT      = NS_URL + "/api/accounts/%s/update?version=v1.0";      //Update the account / project(data api)

    //Custom Object Management [COM]
    String COLLECTION_DETAILS_POST          = NS_URL + "/api/collections/details";    //total records count.
    String COLLECTION_DETAIL                = NS_URL + "/api/collections/collectionDetail";
    String COLLECTION_CREATE_OR_UPDATE      = NS_URL + "/api/collections/";
    String COLLECTION_DATA_MAPPING          = NS_URL + "/api/dataload/mapping";
    String COLLECTION_DATA_VALIDATE         = NS_URL + "/api/dataload/validate";
    String COLLECTION_DATA_LOAD             = NS_URL + "/api/dataload/load";
    String COLLECTION_DATA_ASYNC_IMPORT     = NS_URL + "/api/dataload/import";
    String COLLECTION_CURL_GENERATE         = NS_URL + "/api/dataload/collections/%s/curl"; //Collection Id.
    String COLLECTION_DATA_DELETE_POST      = NS_URL + "/api/dataload/delete";  //To delete the data from collection.

    //S3 Connectors API
    String S3_ACCOUNT_FETCH                 = NS_URL + "/api/s3Connector/account/fetch";
    String S3_FOLDERS_FETCH                 = NS_URL + "/api/s3Connector/s3Folders/fetch";
    String S3_BUCKET_EXISTS                 = NS_URL + "/api/s3Connector/bucket/exists";
    String S3_FOLDER_CREATE                 = NS_URL + "/api/s3Connector/s3Folder/create";
    String S3_DATALOAD_HISTORY              = NS_URL + "/api/s3Connector/dataload/history";  //?start=0&count=10


    //Common API
    String API_SCHEDULES_ALL                    = NS_URL + "/api/schedule/schedules";
    String API_SCHEDULE                         = NS_URL + "/api/schedule";
    String TENANT_INFO_LITE                     = NS_URL + "/api/tenants/info/"; //Append 18 digit org id.

    //Copilot
    String API_CREATE_SMARTLIST             = NS_URL + "/api/smartlists/";
    String API_SMARTLIST_NAME_UPDATE        = NS_URL + "/api/smartlists/name";
    String API_COPILOT_FEATURES_STATUS      = NS_URL + "/api/async/tasks/features/copilot";    //To get the status of the smart lists that are triggered.
    String API_SMARTLIST                    = NS_URL + "/api/smartlists/";  //To get all the smart list's created.   /data?numberOfRecords=15 - To get top 15 records.
    String API_SMARTLIST_SAVE               = NS_URL + "/api/smartlists/save"; //Create a smart list with out triggering the execution.
    String API_SMARTLIST_CREATE_EXECUTE     = NS_URL + "/api/smartlists/saveExecute"; //Save the smart list & trigger the refresh.
    String API_SMARTLIST_DATA               = NS_URL + "/api/smartlists/%s/data";  //Get the smart list data.
    String API_SMARTLIST_DATA_RESYNC        = NS_URL + "/api/smartlists/%s/execute";  //Refresh the smartlist data.
    String API_EMAIL_TEMPLATE               = NS_URL + "/api/templates/";    //To get all the email templates.
    String API_EMAIL_TEMPLATE_OUTREACH_INFO = NS_URL + "/api/templates/outreachInfo"; //To get the template info related use in different outreaches.
    String API_OUTREACH                     = NS_URL + "/api/campaigns/";  //Get all the outreaches/
    String API_OUTREACH_RUN                 = NS_URL + "/api/campaigns/%s/run";  //To run the outreach.
    String API_OUTREACH_EXECUTION_HISTORY   = NS_URL + "/api/campaigns/%s/executionHistory";      //To get the execution history of a outreach.
    String API_SMARTLIST_SEARCH             = NS_URL + "/api/smartlists/%s/search";
    String API_OUTREACH_PREVIEW             = NS_URL + "/api/campaigns/%s/preview";
    String API_COPILOT_EMAIL_LOG_ANALYTICS  = NS_URL + "/api/collections/assets/ANALYTICS/entities/EMAIL_LOG"; //To get Email logs collection master. //TODO-Why are we not using exisints API to get collection master of email logs.
    String API_IMAGE_RESIZE                 = NS_URL + "/api/image/resize";
    String API_EMAIL_TEMPLATE_NAME_UPDATE   = NS_URL + "/api/templates/name"; //Update the name of email template.
    String API_OUTREACH_NAME_UPDATE         = NS_URL + "/api/campaigns/name"; //Update the name of outreach.
    String API_SUBSCRIBE_EMAIL              = NS_URL + "/api/subscription/email";
    String API_WEB_HOOK_SENDGRID            = NS_URL + "/api/email/webhook/sendgrid";
    String EMAIL_WEB_HOOK_MANDRILL          = "/api/email/webhook/mandrill";
    String API_WEB_HOOK_MANDRILL            = NS_URL + EMAIL_WEB_HOOK_MANDRILL;
    String API_EMAIL_VALIDATE               = NS_URL + "/api/email/validate";
    String API_EMAIL_KNOCK_OFF              = NS_URL + "/api/email/knockOff";


    String API_GET_ALL_COLLECTIONS_SFDC_ACCOUNTID_MAPPED = NS_URL + "/api/collections/all/lite?hasaccount=true";

    //GS Email related API's
    String GET_ACCESS_KEY                  = NS_URL + "/api/email/account";
    String EXISTS_CALL                     = NS_URL + "/api/accounts/SFDC/exists";
    String SEND_EMAIL                      = NS_URL + "/api/email/template";
    String MANDRILL_ENDPOINT               = "https://mandrillapp.com/api/1.0/";
    String MANDRILL_SUBACCOUNT_INFO        = "/subaccounts/info.json";

    //Zendesk

    String API_ZENDESK_ORGANIZATION_LOOKUP = NS_URL + "/api/zendesk/tickets/lookup"; // Creates Zendesk Org to SFDC account lookup
    String API_ZENDESK_DELETE_ORGANIZATION_LOOKUP = NS_URL + "/api/zendesk/tickets/%s/lookup"; // Deletes Zendesk Org to SFDC account lookup
    String API_ZENDESK_ALL_IN_ONE_SFDC_PROXY = NS_URL + "/api/zendesk/sfdc/proxy"; // Common API to get data from Sfdc org
    String API_ZENDESK_LINK_CTA = NS_URL + "/api/zendesk/tickets/%s/ctas/%s"; // Link Zendesk ticket to SFDC CTA
    String API_ZENDESK_TICKET_TO_SFDC_CTA = NS_URL + "/api/zendesk/tickets/%s/ctas"; // Link Zendesk ticket to SFDC CTA
    String API_ZENDESK_UNLINK_CTA = NS_URL + "/api/zendesk/tickets/2006/ctas"; // UnLink Zendesk ticket to SFDC CTA

}
