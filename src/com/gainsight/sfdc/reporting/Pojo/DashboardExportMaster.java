package com.gainsight.sfdc.reporting.Pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by JayaPrakash on 03/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardExportMaster {
    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    private String dashboardId;

    public ExportTemplate getExportTemplate() {
        return exportTemplate;
    }

    public void setExportTemplate(ExportTemplate exportTemplate) {
        this.exportTemplate = exportTemplate;
    }

    private ExportTemplate exportTemplate;



    public static class ExportTemplate {
        private String id;
        private String subject;
        private String body;
        private List<String> toEmailList;
        private String userId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<String> getToEmailList() {
            return toEmailList;
        }

        public void setToEmailList(List<String> toEmailList) {
            this.toEmailList = toEmailList;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }


}
