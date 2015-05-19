package com.gainsight.bigdata.urls;

import scala.xml.MetaData;

/**
 * Created by gainsight on 07/05/15.
 */
public interface AdminURLs extends NSURLs {



    public String ADMIN_TENANTS                 = NS_ADMIN_URL+"/admin/tenants";
    public String ADMIN_COLLECTIONS             = NS_ADMIN_URL+"/admin/collections";
    public String ADMIN_POST_COLLECTIONS_LIST   = ADMIN_COLLECTIONS+"/list";
    public String ADMIN_TOKENS                  = NS_ADMIN_URL+"/admin/tokens";
    public String ADMIN_ACTIVITIES              = NS_ADMIN_URL+"/admin/activities";
    public String ADMIN_TEST_CONNECTION         = NS_ADMIN_URL+"/admin/test/connection";


    public String ADMIN_DATA_LOAD_AUTHENTICATE      = NS_URL+"/admin/dataload/authenticate";
    public String ADMIN_GET_COLLECTIONS_ALL         = NS_URL+"/admin/collections/ALL";
    public String ADMIN_DATA_RULES_PER_COLLECTION   = NS_URL+"/admin/datarules/collections/"; //Collection name to be appended.
    public String ADMIN_DATA_LOAD_RULES_CREATE      = NS_URL+"/admin/datarules/metadata";
    public String ADMIN_DATA_LOAD_WITH_RULE_ID      = NS_URL+"/admin/dataload/rules/"; //Rule Id, action paths need to be set.
    public String ADMIN_DATA_LOAD_IMPORT            = NS_URL+"/admin/dataload/import";
    public String ADMIN_DATA_LOAD_IMPORT_TOOL       = ADMIN_DATA_LOAD_IMPORT+"?via=DataLoader";
    public String ADMIN_DATA_LOAD_STATUS            = NS_URL+"/admin/dataload/status/"; //Status id should be appended.
    public String ADMIN_DATA_LOAD_EXPORT_FAILURES   = NS_URL+"/admin/dataload/export/failure/status/"; //Status id should be appended.




}
