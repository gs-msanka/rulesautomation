package com.gainsight.bigdata.urls;

import scala.xml.MetaData;

/**
 * Created by gainsight on 07/05/15.
 */
public interface AdminURLs extends NSURLs {



    String ADMIN_TENANTS                    = NS_ADMIN_URL+"/admin/tenants";
    String ADMIN_COLLECTIONS                = NS_ADMIN_URL+"/admin/collections";
    String ADMIN_POST_COLLECTIONS_LIST      = ADMIN_COLLECTIONS+"/list";
    String ADMIN_TOKENS                     = NS_ADMIN_URL+"/admin/tokens";
    String ADMIN_ACTIVITIES                 = NS_ADMIN_URL+"/admin/activities";
    String ADMIN_TEST_CONNECTION            = NS_ADMIN_URL+"/admin/tenants/test/connection";



    String ADMIN_DATA_RULES_PER_COLLECTION  = NS_URL+"/admin/datarules/collections/"; //Collection name to be appended.
    String ADMIN_DATA_LOAD_RULES_CREATE     = NS_URL+"/admin/datarules/metadata";
    String ADMIN_DATA_LOAD_WITH_RULE_ID     = NS_URL+"/admin/dataload/rules/"; //Rule Id, action paths need to be set.

}
