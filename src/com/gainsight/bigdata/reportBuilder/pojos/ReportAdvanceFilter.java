package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by Giribabu on 03/09/15.
 */
public class ReportAdvanceFilter {
    @JsonProperty("filters")
    private List<ReportFilter> reportFilters;
    private String expression;

    public List<ReportFilter> getReportFilters() {
        return reportFilters;
    }

    public void setReportFilters(List<ReportFilter> reportFilters) {
        this.reportFilters = reportFilters;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
