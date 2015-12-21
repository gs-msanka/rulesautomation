package com.gainsight.bigdata.rulesengine.bean.RuleAction;


import com.gainsight.bigdata.rulesengine.bean.RuleSetup.FieldInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionMapping {

    private FieldInfo source;
    private FieldInfo target;

    public FieldInfo getSource() {
        return source;
    }

    public void setSource(FieldInfo source) {
        this.source = source;
    }

    public FieldInfo getTarget() {
        return target;
    }

    public void setTarget(FieldInfo target) {
        this.target = target;
    }
}
