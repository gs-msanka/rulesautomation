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
    String API_REPORT_PUT                       = NS_URL + "/api/reports";
    String API_REPORT_DELETE                    = NS_URL + "/api/reports/"; //Append Report ID.
    String API_REPORT_GET_ALL                   = NS_URL + "/api/reports/reporting_v2";
    String API_COLLECTION_ALL_LITE              = NS_URL + "/api/collections/all/lite";
    String API_COLLECTION_TREE                  = NS_URL + "/api/collections/%s/collectionTree";

    String API_RULE_RUN                         = NS_URL + "/api/eventrule";
    String APP_API_GET_COLLECTION               = NS_URL + "/api/collections/";
    String APP_API_ASYNC_STATUS			        = NS_URL + "/api/async/process/";

    String MDA_SFDC_ACCOUNT_EXISTS              = NS_URL + "/api/accounts/SFDC/exists";
    String MDA_AUTH_REVOKE                      = NS_URL + "/api/accounts/sfdc?accountType=SFDC";
    String CREATE_CONNECTORS_PROJECT            = NS_URL + "/api/tokens/v2";
    String API_TOKENS_EXISTS_GET                = NS_URL + "/api/tokens/exists";    //Check if access key is present for the system.
    String ACCOUNT_DETAIL_GET                   = NS_URL + "/api/accounts/";        //Append Account Id to get account details.
    String COLLECTION_MASTER_GET                = NS_URL + "/api/collections/";       //Append Collection Id to get Collection Master details.
    String ACCOUNT_DELETE                       = NS_URL + "/api/accounts/accountId/";


    String MIX_PANEL_DEACTIVATE_PUT         = NS_URL + "/api/accounts/accountType/MIXPANEL/default/false";
    String MIX_PANEL_CREATE                 = NS_URL + "/api/accounts/mixpanel/create";
    String MIX_PANEL_PROPERTY               = NS_URL + "/api/mixpanel/accounts/";
    String MIX_PANEL_PROJECT_GET            = NS_URL + "/api/accounts/integrations/MIXPANEL";
    String MIX_PANEL_ACTIVATE_PUT           = NS_URL + "/api/accounts/accountType/MIXPANEL/default/true";


    String SEGMENT_DEACTIVATE_PUT           = NS_URL + "/api/accounts/accountType/SEGMENT_IO/default/false";
    String SEGMENT_PROJECT_GET              = NS_URL + "/api/accounts/integrations/SEGMENT_IO";
    String SEGMENT_ACCESS_KEY_GET           = NS_URL + "/api/tokens/integrations/SEGMENT_IO/projects/"; //Append Account ID to get access-key for segmentIO project.

    //Data Load
    String DATA_LOAD_AUTHENTICATE           = NS_URL + "/admin/dataload/authenticate";
    String DATA_LOAD_COLLECTIONS            = NS_URL + "/admin/collections";
    String DATA_LOAD_GET_COLLECTIONS_ALL    = NS_URL + "/admin/collections/ALL";
    String DATA_LOAD_STATUS                 = NS_URL + "/admin/dataload/status/"; //Status id should be appended.
    String DATA_LOAD_IMPORT                 = NS_URL + "/admin/dataload/import";
    String DATA_LOAD_EXPORT_FAILURES        = NS_URL + "/admin/dataload/export/failure/status/"; //Status id should be appended.



    String GA_DEACTIVATE_PUT                = NS_URL + "/api/accounts/accountType/GOOGLE_ANALYTICS/default/false";
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


    //S3 Connectors API
    String S3_ACCOUNT_FETCH                 = NS_URL + "/api/s3Connector/account/fetch";
    String S3_FOLDERS_FETCH                 = NS_URL + "/api/s3Connector/s3Folders/fetch";


    //Common API
    String API_SCHEDULE                     = NS_URL + "/api/schedule";
    String TENANT_INFO_LITE                 = NS_URL + "/api/tenants/info/"; //Append 18 digit org id.

    //Copilot
    String API_CREATE_SMARTLIST              = NS_URL + "/api/smartlists/";
    
    //GS Email related API's
    String GET_ACCESS_KEY                  = NS_URL + "/api/email/account";
    String EXISTS_CALL                     = NS_URL + "/api/accounts/SFDC/exists";
    String SEND_EMAIL                      = NS_URL + "/api/email/template";
    String MANDRILL_ENDPOINT               = "https://mandrillapp.com/api/1.0/";
    String MANDRILL_SUBACCOUNT_INFO        = "/subaccounts/info.json";

}
