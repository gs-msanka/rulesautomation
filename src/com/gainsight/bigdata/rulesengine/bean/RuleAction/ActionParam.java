package com.gainsight.bigdata.rulesengine.bean.RuleAction;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 04/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ActionParam {

    private String areaName;
    private String objName;
    private String operation;
    private String actionType;
    private List<ActionParamFieldInfo> fieldInfo;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public List<ActionParamFieldInfo> getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(List<ActionParamFieldInfo> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public static class ActionParamFieldInfo {
        private String name;
        private boolean isIdentifier;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIdentifier() {
            return isIdentifier;
        }

        public void setIsIdentifier(boolean isIdentifier) {
            this.isIdentifier = isIdentifier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
