package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 21/05/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ReportMaster {

    private boolean newReport;
    private boolean reportMasterRequired;
    private String format = "JSON";
    private ReportOption reportOptions;
    private String displayType;
    @JsonProperty("ReportInfo")
    private List<ReportInfo> reportInfo;

    public boolean isNewReport() {
        return newReport;
    }

    public void setNewReport(boolean newReport) {
        this.newReport = newReport;
    }

    public boolean isReportMasterRequired() {
        return reportMasterRequired;
    }

    public void setReportMasterRequired(boolean reportMasterRequired) {
        this.reportMasterRequired = reportMasterRequired;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ReportOption getReportOptions() {
        return reportOptions;
    }

    public void setReportOptions(ReportOption reportOptions) {
        this.reportOptions = reportOptions;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public List<ReportInfo> getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(List<ReportInfo> reportInfo) {
        this.reportInfo = reportInfo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReportOption {
        private boolean normalize;
        private boolean enableDataLabels;
        private boolean showLabels;
        private boolean enableRGBColor;
        private boolean enableDualYAxis;

        public boolean isNormalize() {
            return normalize;
        }

        public void setNormalize(boolean normalize) {
            this.normalize = normalize;
        }

        public boolean isEnableDataLabels() {
            return enableDataLabels;
        }

        public void setEnableDataLabels(boolean enableDataLabels) {
            this.enableDataLabels = enableDataLabels;
        }

        public boolean isShowLabels() {
            return showLabels;
        }

        public void setShowLabels(boolean showLabels) {
            this.showLabels = showLabels;
        }

        public boolean isEnableRGBColor() {
            return enableRGBColor;
        }

        public void setEnableRGBColor(boolean enableRGBColor) {
            this.enableRGBColor = enableRGBColor;
        }

        public boolean isEnableDualYAxis() {
            return enableDualYAxis;
        }

        public void setEnableDualYAxis(boolean enableDualYAxis) {
            this.enableDualYAxis = enableDualYAxis;
        }
    }
}
