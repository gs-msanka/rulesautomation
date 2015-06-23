package com.gainsight.bigdata.urls;

import com.gainsight.util.PropertyReader;

/**
 * Created by gainsight on 07/05/15.
 */
public interface NSURLs {

    public String NS_URL = PropertyReader.nsAppUrl+"/"+PropertyReader.nsApiVersion;
    public String NS_ADMIN_URL = PropertyReader.nsAdminUrl+"/"+PropertyReader.nsApiVersion;

}
