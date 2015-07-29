package com.gainsight.util.config;

import com.gainsight.testdriver.Application;
import com.gainsight.utils.config.AbstractConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Created by vmenon on 28/7/15.
 */
public class SfdcConfigProvider extends AbstractConfigProvider<SfdcConfig> {
    private static File sfdcConfFile = new File(Application.basedir + "/conf/sfdc-config.json");
    JsonObject loadedJsonObject;
    public static final String name = "sfdcConfig";

    static {
        ConfigProviderFactory.registerConfig(SfdcConfigProvider.name, new SfdcConfigProvider());
    }

    @Override
    public SfdcConfig getConfig() {
        String profileName = System.getProperty("sfdc_env" , "default");
        return getConfig(profileName);
    }

    @Override
    public SfdcConfig getConfig(String profileName) {
        if(loadedJsonObject == null)
            loadedJsonObject = loadConfig(sfdcConfFile);
        JsonObject profileJsonObject = loadProfileAndGetJson(loadedJsonObject, profileName);
        return new SfdcConfig(profileJsonObject);
    }
}
