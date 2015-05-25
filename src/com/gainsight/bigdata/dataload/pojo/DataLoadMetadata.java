package com.gainsight.bigdata.dataload.pojo;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 18/05/15.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class DataLoadMetadata {
    private String ruleName;
    private String collectionName;
    private String dataLoadOperation;
    private String clearOperation;
    private String sourceType;
    private String targetType;
    private boolean headerRow = true;
    private Character fieldSeparator;
    private Character escapeCharacter;
    private Character quoteCharacter;
    private boolean dbNameUsed = false;
    @JsonProperty("mapping")
    private List<Mapping> mappings;

    public String getClearOperation() {
        return clearOperation;
    }

    public void setClearOperation(String clearOperation) {
        this.clearOperation = clearOperation;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getDataLoadOperation() {
        return dataLoadOperation;
    }

    public void setDataLoadOperation(String dataLoadOperation) {
        this.dataLoadOperation = dataLoadOperation;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public Character getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(Character escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public Character getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(Character quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    public Character getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(Character fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public boolean isDbNameUsed() {
        return dbNameUsed;
    }

    public void setDbNameUsed(boolean dbNameUsed) {
        this.dbNameUsed = dbNameUsed;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public static class Mapping {
        private String source;
        private String sourceDateFormat;
        private String sourceNumberFormat;
        private String target;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSourceDateFormat() {
            return sourceDateFormat;
        }

        public void setSourceDateFormat(String sourceDateFormat) {
            this.sourceDateFormat = sourceDateFormat;
        }

        public String getSourceNumberFormat() {
            return sourceNumberFormat;
        }

        public void setSourceNumberFormat(String sourceNumberFormat) {
            this.sourceNumberFormat = sourceNumberFormat;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

}
