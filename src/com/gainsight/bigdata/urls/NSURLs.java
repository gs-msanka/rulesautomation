package com.gainsight.bigdata.urls;

import com.gainsight.util.ConfigLoader;

/**
 * Created by Giribabu on 07/05/15.
 */
public interface NSURLs {

    String NS_URL           = ConfigLoader.getNsConfig().getNsURl()+"/"+ConfigLoader.getNsConfig().getNsVersion();
    String NS_ADMIN_URL     = ConfigLoader.getNsConfig().getNsAdminUrl()+"/"+ConfigLoader.getNsConfig().getNsVersion();

}
