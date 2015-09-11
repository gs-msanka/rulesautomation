package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

import java.util.Map;

/**
 * Created by Giribabu on 10/07/15.
 */
public class Identifier {
    private Source source;
    private Target target;
    private boolean lookup;
    private boolean directLookup;
    private boolean digitConversionEnable;
    private Map<String, String> properties;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public boolean isLookup() {
        return lookup;
    }

    public void setLookup(boolean lookup) {
        this.lookup = lookup;
    }

    public boolean isDirectLookup() {
        return directLookup;
    }

    public void setDirectLookup(boolean directLookup) {
        this.directLookup = directLookup;
    }

    public boolean isDigitConversionEnable() {
        return digitConversionEnable;
    }

    public void setDigitConversionEnable(boolean digitConversionEnable) {
        this.digitConversionEnable = digitConversionEnable;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
