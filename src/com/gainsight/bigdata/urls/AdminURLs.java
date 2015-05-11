package com.gainsight.bigdata.urls;

/**
 * Created by gainsight on 07/05/15.
 */
public interface AdminURLs extends NSURLs {



    public String ADMIN_TENANTS                 = NS_ADMIN_URL+"/admin/tenants";
    public String ADMIN_COLLECTIONS             = NS_ADMIN_URL+"/admin/collections";
    public String ADMIN_COLLECTIONS_LIST        = ADMIN_COLLECTIONS+"/list";
    public String ADMIN_TOKENS                  = NS_ADMIN_URL+"/admin/tokens";
    public String ADMIN_ACTIVITIES              = NS_ADMIN_URL+"/admin/activities";
    public String ADMIN_TEST_CONNECTION         = NS_ADMIN_URL+"/admin/test/connection";
    public String ADMIN_DATALOAD_AUTHENTICATE   = NS_ADMIN_URL+"/admin/dataload/authenticate";

}
