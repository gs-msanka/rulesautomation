package com.gainsight.sfdc.reporting.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.gainsight.bigdata.reportBuilder.pojos.ReportAdvanceFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportAdvanceFilterSFDC;
import com.gainsight.bigdata.reportBuilder.pojos.ReportFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportFilterSFDC;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo.Dimension;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfoSFDC;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfoSFDC.DimensionSFDC;
import com.gainsight.bigdata.reportBuilder.pojos.ReportQueryOption.ReportRanking;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.reportBuilder.pojos.ReportQueryOption;
import com.gainsight.sfdc.reporting.pages.ReportingBasePage;
import com.gainsight.sfdc.reporting.pages.ReportingFilterUtils;
import com.gainsight.sfdc.reporting.pages.ReportingSettingsUtils;
import com.gainsight.sfdc.reporting.tests.MDAConnectBackend;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.MongoUtil;

/**
 * 
 * Contains utils for reporting automation. From here all the reporting utils
 * will be called in a proper format.
 */

public class ReportingUtil extends BaseTest {
	MDAConnectBackend mdaConnectBackend = new MDAConnectBackend();
	TenantManager tenantManager = new TenantManager();

	/**
     * Creates a rule on the ui based on the json input configuration file path
     * and then validates the JSon saved at the backend
     *
     * @param reportMaster
     * @param reportingBasePage
     * @param mongoUtil
     * @throws Exception
     */
    public void createReportFromUiAndVerifyBackedJSON(ReportMaster reportMaster, ReportingBasePage reportingBasePage,
                                                      MongoUtil mongoUtil, String expectedData) throws Exception {

        List<ReportInfo> reportInfos = reportMaster.getReportInfo();
        for (ReportInfo reportInfo : reportInfos) {
            createReportFromUiMDA(reportInfo, reportingBasePage, reportMaster.getDisplayType());
        }

        reportMaster.setReportInfo(reportInfos);
        mdaConnectBackend.connectDbAndCompareJSON(tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(),
                reportInfos.get(0), mongoUtil, reportMaster);

        mdaConnectBackend.verifyData(tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(),
                reportInfos.get(0), mongoUtil, expectedData);
    }

	/**
	 * Creates a rule on the ui based on the json input configuration file path
	 * @param reportInfo
	 * @param reportingBasePage
	 * @param disPlayType
	 */
    public void createReportFromUiMDA(ReportInfo reportInfo, ReportingBasePage reportingBasePage, String disPlayType) {

		reportingBasePage.selectObjectFromSource(reportInfo.getSchemaName());
        Boolean flag = false;
        int showMeCount = 0;
        int byCount = 0;

		List<Dimension> dimensions = reportInfo.getDimensions();
		for (Dimension dimension : dimensions) {
			if (dimension.getAgg_func() != null) {
				reportingBasePage.addShowMeFieldMDA(dimension.getCol());
                showMeCount++;
			} else {
				reportingBasePage.addByFieldMDA(dimension.getCol(), reportInfo.getSchemaName());
				byCount++;
                flag = true;
				if (dimension.getSummarizedBy() != null) {
					reportingBasePage.byFieldSettings(dimension.getFieldDisplayName(), "", dimension.getSummarizedBy());
				}
			}
		}
        if (showMeCount > 4) {
            reportingBasePage.expandShowMe();
		}
        if (flag) {
            for (Dimension dimension : dimensions) {
                if (dimension.getAgg_func() != null) {
                    if (!dimension.getAgg_func().equalsIgnoreCase("Count")) {
                        reportingBasePage.showMeFieldSettings(dimension.getFieldDisplayName(), null,
                                dimension.getAgg_func(), dimension.getDecimalPlaces() + "", dimension.getDataType());
                    }

                }
            }
        }

        ReportingFilterUtils reportingFilterUtils = new ReportingFilterUtils();
        if (reportInfo.getWhereAdvanceFilter() != null) {

            ReportAdvanceFilter reportAdvanceFilters = reportInfo.getWhereAdvanceFilter();

            List<ReportFilter> reportFilters = reportAdvanceFilters.getReportFilters();
            for (ReportFilter reportFilter : reportFilters) {
                String filterName = null;
                reportingFilterUtils.addFilterMDA(filterName, reportFilter.getDataType(), reportFilter.getDbName(),
                        reportFilter.getFilterOperator(), reportFilter.getFilterValues(), "whereItems.getLocked()",
                        reportFilter);
            }
            reportAdvanceFilters.setReportFilters(reportFilters);
            reportingFilterUtils.addWhereExpression(reportAdvanceFilters.getExpression());
            reportInfo.setWhereAdvanceFilter(reportAdvanceFilters);
        }
        if (reportInfo.getHavingAdvanceFilter() != null) {
            ReportAdvanceFilter reportAdvanceFiltersHaving = reportInfo.getHavingAdvanceFilter();

            List<ReportFilter> reportFiltersHaving = reportAdvanceFiltersHaving.getReportFilters();
            for (ReportFilter reportFilter : reportFiltersHaving) {

                String filterName = "";
                filterName = reportFilter.getAggregateFunction().substring(0, 1).toUpperCase()
                        + reportFilter.getAggregateFunction().substring(1) + " of " + reportFilter.getDbName();
                reportingFilterUtils.addFilterMDA(filterName, reportFilter.getDataType(), reportFilter.getDbName(),
                        reportFilter.getFilterOperator(), reportFilter.getFilterValues(), "Test", reportFilter);
            }
            reportingFilterUtils.addHavingExpression(reportAdvanceFiltersHaving.getExpression());
		}
		ReportingSettingsUtils reportingSettingsUtils = new ReportingSettingsUtils();
		for (Dimension dimension : dimensions) {
			if (dimension.getOrder() != null) {
				reportingSettingsUtils.applyRanking(dimension.getCol(), dimension.getOrder(),
						reportInfo.getReportReadLimit() + "");
			}
		}

		reportingSettingsUtils.selectReportType(disPlayType);
		reportingBasePage.saveReport(reportInfo.getReportName());

    }

    /**
     * Creates a rule on the ui for sfdc based on the json input configuration file path and then validates the JSon saved at the backend
     */
    public void createReportFromUiAndVerifyBackedJSONSFDC(ReportInfoSFDC reportInfoSFDC,
                                                          ReportingBasePage reportingBasePage, HashMap<String, String> hmap) {

        createReportFromUiSFDC(reportInfoSFDC, reportingBasePage, reportInfoSFDC.getName(), hmap);
    }

    /**
     * Creates a rule on the ui based on the json input configuration file path
     */
    public void createReportFromUiSFDC(ReportInfoSFDC reportInfoSFDC, ReportingBasePage reportingBasePage,
                                       String disPlayType, HashMap<String, String> hmap) {

        reportingBasePage.selectObjectFromSource(hmap.get(reportInfoSFDC.getBaseObject()));

        List<DimensionSFDC> dimensions = reportInfoSFDC.getDimensions();

        for (DimensionSFDC dimension : dimensions) {
            reportingBasePage.addByField(dimension.getLabel(), dimension.getObjectName());
        }

        List<DimensionSFDC> measures = reportInfoSFDC.getMeasures();

        for (DimensionSFDC measure : measures) {
            String label = measure.getLabel().substring(measure.getLabel().indexOf("of") + 3);
            reportingBasePage.addShowMeField(label, measure.getObjectName());
        }

        ReportingFilterUtils reportingFilterUtils = new ReportingFilterUtils();
        if (reportInfoSFDC.getWhereAdvanceFilter() != null) {

            ReportAdvanceFilterSFDC reportAdvanceFilterSFDC = reportInfoSFDC.getWhereAdvanceFilter();

            List<ReportFilterSFDC> reportFilters = reportAdvanceFilterSFDC.getFilterCriteria();
            for (ReportFilterSFDC reportFilter : reportFilters) {
                reportingFilterUtils.addFilter(reportFilter);
            }
            reportingFilterUtils.addWhereExpression(reportAdvanceFilterSFDC.getFilterLogic());
        }

        if (reportInfoSFDC.getHavingAdvanceFilter() != null) {
            ReportAdvanceFilterSFDC reportAdvanceFiltersHaving = reportInfoSFDC.getHavingAdvanceFilter();

            List<ReportFilterSFDC> reportFiltersHaving = reportAdvanceFiltersHaving.getFilterCriteria();
            for (ReportFilterSFDC reportFilter : reportFiltersHaving) {
                reportingFilterUtils.addFilter(reportFilter);
            }
            reportingFilterUtils.addHavingExpression(reportAdvanceFiltersHaving.getFilterLogic());
        }

        ReportingSettingsUtils reportingSettingsUtils = new ReportingSettingsUtils();
        if (reportInfoSFDC.getReportQueryOption() != null) {
            ReportQueryOption reportQueryOption = reportInfoSFDC.getReportQueryOption();

            List<ReportRanking> reportRankings = reportQueryOption.getReportRanking();
            for (ReportRanking reportRanking : reportRankings) {

                reportingSettingsUtils.applyRanking(reportRanking.getLabel(), reportRanking.getSortOrder(),
                        reportQueryOption.getLimit() + "");
            }
        }

        reportingSettingsUtils.selectReportType(reportInfoSFDC.getGraphType());
        reportingBasePage.saveReport(reportInfoSFDC.getName());

	}

}
