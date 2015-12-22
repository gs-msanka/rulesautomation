package com.gainsight.bigdata.copilot.bean.outreach;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class ReportTokenMapping {

    String id;
    String reportType;
    String whereExpression;
    @JsonProperty("filterInfos")
    List<CoPilotFilterInfo> filterInfoList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getWhereExpression() {
        return whereExpression;
    }

    public void setWhereExpression(String whereExpression) {
        this.whereExpression = whereExpression;
    }

    public List<CoPilotFilterInfo> getFilterInfoList() {
        return filterInfoList;
    }

    public void setFilterInfoList(List<CoPilotFilterInfo> filterInfoList) {
        this.filterInfoList = filterInfoList;
    }
}
