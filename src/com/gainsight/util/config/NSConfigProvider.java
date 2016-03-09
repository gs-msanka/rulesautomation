package com.gainsight.util.config;

import com.gainsight.testdriver.Application;
import com.gainsight.utils.config.AbstractConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;
import com.gainsight.utils.config.IConfigProvider;
import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Created by vmenon on 28/7/15.
 */
@AutoService(IConfigProvider.class)
public class NSConfigProvider extends AbstractConfigProvider<NsConfig> {

    private static File nsConfFile = new File( Application.basedir + "/conf/ns-config.json");
    JsonObject loadedJsonObject;
    public static final String name = "nsConfig";

    @Override
    public NsConfig getConfig() {
        String profileName = System.getProperty("ns_env" , "incqa01");
        return getConfig(profileName);
    }

    @Override
    public NsConfig getConfig(String profileName) {
        if(loadedJsonObject == null)
            loadedJsonObject = loadConfig(nsConfFile);
        JsonObject profileJsonObject = loadProfileAndGetJson(loadedJsonObject, profileName);
        return new NsConfig(profileJsonObject);
    }

    @Override
    public Class<NsConfig> getSupportedType() {
        return NsConfig.class;
    }
}
