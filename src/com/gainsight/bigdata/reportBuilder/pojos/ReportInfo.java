package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by Giribabu on 21/05/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportInfo {

    @JsonProperty("SchemaName")
    private String schemaName;
    @JsonProperty("CollectionID")
    private String collectionID;
    private int limit = 500;
    @JsonProperty("Type")
    private String type = "adhoc";
    @JsonProperty("Dimensions")
    private List<Dimension> dimensions;
    private List<String> drillDownReportDimensions;
    @JsonProperty("ReportReadLimit")
    private int reportReadLimit = 1000;
    @JsonProperty("DimensionBrowserReadLimit")
    private int dimensionBrowserReadLimit = 1000;
    @JsonProperty("ReportId")
    private int reportId = 0;
    private String reportName;
    private boolean nonAggregatedResult = true;
    private String assetType;
    private int skip;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(String collectionID) {
        this.collectionID = collectionID;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public List<String> getDrillDownReportDimensions() {
        return drillDownReportDimensions;
    }

    public void setDrillDownReportDimensions(List<String> drillDownReportDimensions) {
        this.drillDownReportDimensions = drillDownReportDimensions;
    }

    public int getReportReadLimit() {
        return reportReadLimit;
    }

    public void setReportReadLimit(int reportReadLimit) {
        this.reportReadLimit = reportReadLimit;
    }

    public int getDimensionBrowserReadLimit() {
        return dimensionBrowserReadLimit;
    }

    public void setDimensionBrowserReadLimit(int dimensionBrowserReadLimit) {
        this.dimensionBrowserReadLimit = dimensionBrowserReadLimit;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public boolean isNonAggregatedResult() {
        return nonAggregatedResult;
    }

    public void setNonAggregatedResult(boolean nonAggregatedResult) {
        this.nonAggregatedResult = nonAggregatedResult;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public static class Dimension {
        private String col;
        private String axis;
        private int type;
        private String dataType;
        private String agg_func;
        private String fieldDisplayName;
        private String collectionId;

        public String getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }

        public String getCol() {
            return col;
        }

        public void setCol(String col) {
            this.col = col;
        }

        public String getAxis() {
            return axis;
        }

        public void setAxis(String axis) {
            this.axis = axis;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getAgg_func() {
            return agg_func;
        }

        public void setAgg_func(String agg_func) {
            this.agg_func = agg_func;
        }

        public String getFieldDisplayName() {
            return fieldDisplayName;
        }

        public void setFieldDisplayName(String fieldDisplayName) {
            this.fieldDisplayName = fieldDisplayName;
        }
    }


}
