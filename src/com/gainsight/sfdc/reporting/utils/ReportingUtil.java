package com.gainsight.sfdc.reporting.utils;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.gainsight.bigdata.reportBuilder.pojos.ReportAdvanceFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo.Dimension;
import com.gainsight.bigdata.tenantManagement.apiImpl.TenantManager;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
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
	 * Creates a rule on the ui based on the json input configuration file path and then validates the JSon saved at the backend
	 *
	 * @param reportMaster
	 * @param reportingBasePage
	 * @param mongoUtil
	 */
	public void createReportFromUiAndVerifyBackedJSON(ReportMaster reportMaster, ReportingBasePage reportingBasePage, MongoUtil mongoUtil) {

		for (ReportInfo reportInfo : reportMaster.getReportInfo()) {
			createReportFromUi(reportInfo, reportingBasePage);
			mdaConnectBackend.connectDbAndCompareJSON(
					tenantManager.getTenantDetail(sfdcInfo.getOrg(), null).getTenantId(), reportInfo, mongoUtil,
					reportMaster);
		}

	}


	/**
	 * Creates a rule on the ui based on the json input configuration file path
	 * @param reportInfo
	 * @param reportingBasePage
	 */
	public void createReportFromUi(ReportInfo reportInfo, ReportingBasePage reportingBasePage) {

		reportingBasePage.selectObjectFromSource(reportInfo.getSchemaName());

		List<Dimension> dimensions = reportInfo.getDimensions();
		for (Dimension dimension : dimensions) {
			if (dimension.getAgg_func() != null) {
				reportingBasePage.addShowMeFieldMDA(dimension.getCol());
			} else {
				reportingBasePage.addByFieldMDA(dimension.getCol(), reportInfo.getSchemaName());
			}
		}

		ReportingFilterUtils reportingFilterUtils = new ReportingFilterUtils();
		ReportAdvanceFilter reportAdvanceFilters = reportInfo.getWhereAdvanceFilter();

		List<ReportFilter> reportFilters = reportAdvanceFilters.getReportFilters();
		for (ReportFilter reportFilter : reportFilters) {
			String filterName = null;
			reportingFilterUtils.addFilterMDA(filterName, reportFilter.getDataType(), reportFilter.getDbName(),
					reportFilter.getFilterOperator(), reportFilter.getFilterValues(), "whereItems.getLocked()");
		}

		ReportAdvanceFilter reportAdvanceFiltersHaving = reportInfo.getHavingAdvanceFilter();

		List<ReportFilter> reportFiltersHaving = reportAdvanceFiltersHaving.getReportFilters();
		for (ReportFilter reportFilter : reportFiltersHaving) {

			String filterName = "";
			filterName = reportFilter.getAggregateFunction().substring(0, 1).toUpperCase()
					+ reportFilter.getAggregateFunction().substring(1) + " of " + reportFilter.getDbName();
			reportingFilterUtils.addFilterMDA(filterName, reportFilter.getDataType(), reportFilter.getDbName(),
					reportFilter.getFilterOperator(), reportFilter.getFilterValues(), "Test");
		}
		ReportingSettingsUtils reportingSettingsUtils = new ReportingSettingsUtils();
		for (Dimension dimension : dimensions) {
			if (dimension.getOrder() != null) {
				reportingSettingsUtils.applyRanking(dimension.getCol(), dimension.getOrder(),
						reportInfo.getReportReadLimit() + "");
			}
		}

		reportingBasePage.saveReport(reportInfo.getReportName());

	}

}
