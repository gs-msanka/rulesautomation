package com.gainsight.bigdata.reportBuilder.pojos;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by Gainsight on 07/12/15.
 */
public class ReportAdvanceFilterSFDC {
    @JsonProperty("filterCriteria")
    private List<ReportFilterSFDC> filterCriteria;
    private String filterLogic;

    public List<ReportFilterSFDC> getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(List<ReportFilterSFDC> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public String getFilterLogic() {
        return filterLogic;
    }

    public void setFilterLogic(String filterLogic) {
        this.filterLogic = filterLogic;
    }
}
