package com.gainsight.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by vmenon on 29/5/15.
 */
public class SfdcConfigLoader {
    private static File confFile = new File(System.getProperty("basedir", ".") + "/conf/sfdc-config.json");
    private static JsonObject loadedSfdcConfig;

    private SfdcConfigLoader(){

    }

    private static void init(){
        if(loadedSfdcConfig == null) {
            try {
                JsonParser parser = new JsonParser();
                loadedSfdcConfig = parser.parse(new FileReader(confFile)).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SfdcConfig getConfig(String env){
        JsonObject config = null;
        init();
        if(loadedSfdcConfig.getAsJsonObject("profiles") != null){
            JsonObject profiles = loadedSfdcConfig.getAsJsonObject("profiles");
            if( profiles.getAsJsonObject(env) != null ) {
                config = new JsonParser().parse(loadedSfdcConfig.toString()).getAsJsonObject();
                config.remove("profiles");
                JsonObject envObject = profiles.getAsJsonObject(env);
                for(Map.Entry<String, JsonElement> entry : envObject.entrySet()){
                    config.addProperty(entry.getKey(), entry.getValue().getAsString());
                }
            } else {
                throw new RuntimeException(String.format("Unable to find environment with name: %s in the config profiles.", env));
            }
        } else {
            throw new RuntimeException("Unable to find profiles under the Json config.");
        }
        return new SfdcConfig(config);
    }

    public static SfdcConfig getConfig(){
        return getConfig("default");
    }
}
