package com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 10/07/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GlobalMapping {
    private List<Mapping> systemDefined;
    private List<Mapping> gsDefined;
    private List<Mapping> custom;
    private List<Mapping> measures;
    private Identifier accountIdentifier;
    private Identifier userIdentifier;
    private Identifier eventIdentifier;
    private Identifier instanceIdentifier;
    private Identifier timestampIdentifier;
    private List<EventMeasureMapping> eventMeasureMappings;

    public List<Mapping> getSystemDefined() {
        return systemDefined;
    }

    public void setSystemDefined(List<Mapping> systemDefined) {
        this.systemDefined = systemDefined;
    }

    public List<Mapping> getGsDefined() {
        return gsDefined;
    }

    public void setGsDefined(List<Mapping> gsDefined) {
        this.gsDefined = gsDefined;
    }

    public List<Mapping> getCustom() {
        return custom;
    }

    public void setCustom(List<Mapping> custom) {
        this.custom = custom;
    }

    public List<Mapping> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Mapping> measures) {
        this.measures = measures;
    }

    public Identifier getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Identifier accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public Identifier getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(Identifier userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public Identifier getEventIdentifier() {
        return eventIdentifier;
    }

    public void setEventIdentifier(Identifier eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public Identifier getInstanceIdentifier() {
        return instanceIdentifier;
    }

    public void setInstanceIdentifier(Identifier instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
    }

    public Identifier getTimestampIdentifier() {
        return timestampIdentifier;
    }

    public void setTimestampIdentifier(Identifier timestampIdentifier) {
        this.timestampIdentifier = timestampIdentifier;
    }

    public List<EventMeasureMapping> getEventMeasureMappings() {
        return eventMeasureMappings;
    }

    public void setEventMeasureMappings(List<EventMeasureMapping> eventMeasureMappings) {
        this.eventMeasureMappings = eventMeasureMappings;
    }
}
