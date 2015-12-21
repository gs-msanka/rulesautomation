package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Field;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionQuery {

    private Field externalIdentifier;
    private Field[] lookUpFieldInfos;
    private Field identifier;
    private String query;

    public Field getExternalIdentifier() {
        return externalIdentifier;
    }

    public void setExternalIdentifier(Field externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

    public Field[] getLookUpFieldInfos() {
        return lookUpFieldInfos;
    }

    public void setLookUpFieldInfos(Field[] lookUpFieldInfos) {
        this.lookUpFieldInfos = lookUpFieldInfos;
    }

    public Field getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Field identifier) {
        this.identifier = identifier;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
