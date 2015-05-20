package com.gainsight.bigdata.urls;

import com.gainsight.util.PropertyReader;

/**
 * Created by gainsight on 07/05/15.
 */
public interface NSURLs {

    String NS_URL = PropertyReader.nsAppUrl+"/"+PropertyReader.nsApiVersion;
    String NS_ADMIN_URL = PropertyReader.nsAdminUrl+"/"+PropertyReader.nsApiVersion;

}
