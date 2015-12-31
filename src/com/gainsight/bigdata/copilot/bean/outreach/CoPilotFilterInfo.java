package com.gainsight.bigdata.copilot.bean.outreach;

import com.gainsight.bigdata.rulesengine.bean.RuleSetup.Field;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class CoPilotFilterInfo {

    String alias;
    String collectionId;
    String dataType;
    String dbName;
    String filterOperator;
    String logicalOperator;
    Field fieldInfo;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getFilterOperator() {
        return filterOperator;
    }

    public void setFilterOperator(String filterOperator) {
        this.filterOperator = filterOperator;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public Field getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(Field fieldInfo) {
        this.fieldInfo = fieldInfo;
    }
}
