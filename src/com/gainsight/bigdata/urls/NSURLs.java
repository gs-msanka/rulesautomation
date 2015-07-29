package com.gainsight.bigdata.urls;

import com.gainsight.util.config.NsConfig;
import com.gainsight.util.config.SfdcConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;

/**
 * Created by Giribabu on 07/05/15.
 */
public interface NSURLs {

    public NsConfig nsConfig = ConfigProviderFactory.getConfig(SfdcConfigProvider.name);
    public String NS_URL = nsConfig.getNsURl()+"/"+nsConfig.getNsVersion();
    public String NS_ADMIN_URL = nsConfig.getNsAdminUrl()+"/"+nsConfig.getNsVersion();

}
