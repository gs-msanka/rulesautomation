package com.gainsight.bigdata.urls;

/**
 * Created by Giribabu on 08/05/15.
 *
 */

public interface ApiUrls extends NSURLs {

    public final String SEND_GRID_SETTINGS                  = NS_URL + "/api/sendgrid/settings";
    public final String APP_API_TOKENS                      = NS_URL + "/api/tokens";
    public final String APP_API_TENANT_PROVISION            = NS_URL + "/api/tenants/provision";
    public final String APP_API_DATA_LOAD_COLLECTIONS       = NS_URL + "/api/dataload/collections";
    public final String APP_API_EVENTRULE                   = NS_URL + "/api/eventrule";
    public final String APP_API_RULES_LOADABLE_OBJECT       = NS_URL + "/api/rulesloadableobject";

    //Reporting API
    public final String API_REPORT_RUN                      = NS_URL + "/api/reports/run/preparation";
    public final String API_REPORT_RUN_LINKS                = NS_URL + "/api/reports/run/preparation/links";
    public final String API_REPORT_PUT                      = NS_URL + "/api/reports";
    public final String API_REPORT_DELETE                   = NS_URL + "/api/reports/"; //Append Report ID.
    public final String API_REPORT_GET_ALL                  = NS_URL + "/api/reports/reporting_v2";
    public final String API_COLLECTION_ALL_LITE             = NS_URL + "/api/collections/all/lite";
    public final String API_COLLECTION_TREE                 = NS_URL + "/api/collections/%s/collectionTree";

    public final String API_RULE_RUN                        = NS_URL + "/api/eventrule";
    public final String APP_API_GET_COLLECTION              = NS_URL + "/api/collections/";
    public final String APP_API_ASYNC_STATUS			    = NS_URL + "/api/async/process/";

    public final String MDA_SFDC_ACCOUNT_EXISTS             = NS_URL + "/api/accounts/SFDC/exists";
    public final String MDA_AUTH_REVOKE                     = NS_URL + "/api/accounts/sfdc?accountType=SFDC";
    public final String CREATE_CONNECTORS_PROJECT           = NS_URL + "/api/tokens/v2";
    public final String API_TOKENS_EXISTS_GET               = NS_URL + "/api/tokens/exists";    //Check if access key is present for the system.
    public final String ACCOUNT_DETAIL_GET                  = NS_URL + "/api/accounts/";        //Append Account Id to get account details.
    public final String COLLECTION_MASTER_GET               = NS_URL + "/api/collections/";       //Append Collection Id to get Collection Master details.
    public final String ACCOUNT_DELETE                      = NS_URL + "/api/accounts/accountId/";


    public final String MIX_PANEL_DEACTIVATE_PUT        = NS_URL + "/api/accounts/accountType/MIXPANEL/default/false";
    public final String MIX_PANEL_CREATE                = NS_URL + "/api/accounts/mixpanel/create";
    public final String MIX_PANEL_PROPERTY              = NS_URL + "/api/mixpanel/accounts/";
    public final String MIX_PANEL_PROJECT_GET           = NS_URL + "/api/accounts/integrations/MIXPANEL";
    public final String MIX_PANEL_ACTIVATE_PUT          = NS_URL + "/api/accounts/accountType/MIXPANEL/default/true";


    public final String SEGMENT_DEACTIVATE_PUT          = NS_URL + "/api/accounts/accountType/SEGMENT_IO/default/false";
    public final String SEGMENT_PROJECT_GET             = NS_URL + "/api/accounts/integrations/SEGMENT_IO";
    public final String SEGMENT_ACCESS_KEY_GET          = NS_URL + "/api/tokens/integrations/SEGMENT_IO/projects/"; //Append Account ID to get access-key for segmentIO project.


    public final String GA_DEACTIVATE_PUT               = NS_URL + "/api/accounts/accountType/GOOGLE_ANALYTICS/default/false";
    public final String GA_PROJECT_GET                  = NS_URL + "/api/accounts/integrations/GOOGLE_ANALYTICS";
    public final String GA_PROPERTIES_GET               = NS_URL + "/api/googleanalytics/";

    //DataLoad Agg API End Points.
    public final String DATA_API_GET                    = NS_URL + "/api/accounts/integrations/DATA_API";       //All the project s configured in data load api.
    public final String DATA_API_ALL_COLLECTIONS_GET    = NS_URL + "/api/collections/consumers/DATA_API";       //All the collections for the tenant,
    public final String DATA_API_PROJECT_UPDATE_PUT     = NS_URL + "/api/accounts/%s/update?version=v1.0";      //Update the account / project(data api)

    //Custom Object Management
    public final String COLLECTION_DETAILS_POST         = NS_URL + "/api/collections/details";    //total records count.
    public final String COLLECTION_DATA_VALIDATE        = NS_URL + "/api/dataload/validate";
    public final String COLLECTION_DATA_LOAD            = NS_URL + "/api/dataload/load";
    public final String COLLECTION_DATA_MAPPING         = NS_URL + "/api/dataload/mapping";
    public final String COLLECTION_DETAIL               = NS_URL + "/api/collections/collectionDetail";
    public final String COLLECTION_DATA_ASYNC_IMPORT    = NS_URL + "/api/dataload/import";

    //S3 Connectors API
    public final String S3_ACCOUNT_FETCH                = NS_URL + "/api/s3Connector/account/fetch";
    public final String S3_FOLDERS_FETCH                = NS_URL + "/api/s3Connector/s3Folders/fetch";


    //Common API
    public final String API_SCHEDULE                    = NS_URL + "/api/schedule";

    //Copilot
    public String API_CREATE_SMARTLIST        = NS_URL + "/api/smartlists/";
    
    //GS Email related API's
    public final String GET_ACCESS_KEY                  = NS_URL + "/api/email/account";
    public final String EXISTS_CALL                     = NS_URL + "/api/accounts/SFDC/exists";
    public final String SEND_EMAIL                      = NS_URL + "/api/email/template";
    public final String MANDRILL_ENDPOINT               = "https://mandrillapp.com/api/1.0/";
    public final String MANDRILL_SUBACCOUNT_INFO        = "/subaccounts/info.json";

}
