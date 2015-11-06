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
 * Class to load different configuration files and keep it in the memory
 */
public class ConfigLoader {
    private static String baseDir = System.getProperty("basedir", ".");
    private static File sfdcConfFile = new File( baseDir + "/conf/sfdc-config.json");
    private static File nsConfFile = new File( baseDir + "/conf/ns-config.json");
    private static JsonObject loadedSfdcConfig;
    private static JsonObject loadedNsConfig;

    private ConfigLoader(){

    }

    private static void init(){
        if(loadedSfdcConfig == null) {
            try {
                JsonParser parser = new JsonParser();
                loadedSfdcConfig = parser.parse(new FileReader(sfdcConfFile)).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void initNsConfig(){
        if(loadedNsConfig == null) {
            try {
                JsonParser parser = new JsonParser();
                loadedNsConfig = parser.parse(new FileReader(nsConfFile)).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    /**
     * Returns the SfdcConfig object after loading the sfdc-config.json file from the conf folder.
     * The utility overides any existing master properties in json with those mentioned in a particular profile.
     * @param env Profile to choose for overriding.
     * @return SfdcConfig object of the said loaded config
     */
    public static SfdcConfig getSfdcConfig(String env){
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

    /**
     * Returns the SfdcConfig object after loading the sfdc-config.json file from the conf folder.
     * The utility overrides any existing master properties in json with those mentioned in a particular profile.
     * This method checks if there is any system property <i>sfdc.env</i> set to load a particular profile else it loads
     * a profile named "default" from the json file.
     * @return SfdcConfig object of the said loaded config
     */
    public static SfdcConfig getSfdcConfig(){
        String env = System.getProperty("sfdc_env" , "11Demo");
        return getSfdcConfig(env);
    }

    /**
     * Returns the NsConfig object after loading the ns-config.json file from the conf folder.
     * The utility overrides any existing master properties in json with those mentioned in a particular profile.
     * This method checks if there is any system property <i>nsEnv</i> set to load a particular profile else it loads
     * a profile named "default" from the json file.
     * @return NsConfig object of the said loaded config
     */
    public static NsConfig getNsConfig(){
        String env = System.getProperty("ns_env" , "test1");
        return getNsConfig(env);
    }

    /**
     * Returns the NsConfig object after loading the ns-config.json file from the conf folder.
     * The utility overrides any existing master properties in json with those mentioned in a particular profile.
     * @param env Profile to choose for overriding.
     * @return NsConfig object of the said loaded config
     */
    public static NsConfig getNsConfig(String env){
        JsonObject config = null;
        initNsConfig();
        if(loadedNsConfig.getAsJsonObject("profiles") != null){
            JsonObject profiles = loadedNsConfig.getAsJsonObject("profiles");
            if( profiles.getAsJsonObject(env) != null ) {
                config = new JsonParser().parse(loadedNsConfig.toString()).getAsJsonObject();
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
        return new NsConfig(config);
    }
}
