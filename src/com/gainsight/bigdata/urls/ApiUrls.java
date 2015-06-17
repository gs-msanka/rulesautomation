package com.gainsight.bigdata.urls;

/**
 * Created by gainsight on 08/05/15.
 */
public interface ApiUrls extends NSURLs {

    public String SEND_GRID_SETTINGS            = NS_URL + "/api/sendgrid/settings";
    public String APP_API_TOKENS                = NS_URL + "/api/tokens";
    public String APP_API_TENANT_PROVISION      = NS_URL + "/api/tenants/provision";
    public String APP_API_DATA_LOAD_COLLECTIONS = NS_URL + "/api/dataload/collections";
    public String APP_API_EVENTRULE             = NS_URL + "/api/eventrule";
    public String APP_API_RULES_LOADABLE_OBJECT = NS_URL + "/api/rulesloadableobject";
    public String API_REPORT_RUN                = NS_URL + "/api/reports/run/preparation";
    public String API_RULE_RUN                  = NS_URL + "/api/eventrule";
    public String APP_API_GET_COLLECTION        = NS_URL + "/api/collections/";
    public String APP_API_ASYNC_STATUS			= NS_URL + "/api/async/process/";
}
