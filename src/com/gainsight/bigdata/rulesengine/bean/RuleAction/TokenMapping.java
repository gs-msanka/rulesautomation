package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.FieldInfo;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenMapping {

    private boolean isNotNullable;
    private List<Token> tokens;
    private boolean notNullable;

    public void setNotNullable(boolean notNullable) {
        this.notNullable = notNullable;
    }

    public boolean isNotNullable() {
        return isNotNullable;
    }

    public void setIsNotNullable(boolean isNotNullable) {
        this.isNotNullable = isNotNullable;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static class Token {
        String name;
        FieldInfo value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FieldInfo getValue() {
            return value;
        }

        public void setValue(FieldInfo value) {
            this.value = value;
        }
    }
}
