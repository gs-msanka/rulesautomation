package com.gainsight.bigdata.copilot.bean.emailTemplate;

import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Giribabu on 05/12/15.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class EmailEmbeddedReport {
    private String reportType;
    private ReportMaster reportMaster;
    private String id;
    private int width;
    private int height;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public ReportMaster getReportMaster() {
        return reportMaster;
    }

    public void setReportMaster(ReportMaster reportMaster) {
        this.reportMaster = reportMaster;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
