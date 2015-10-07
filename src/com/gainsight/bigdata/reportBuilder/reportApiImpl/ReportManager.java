package com.gainsight.bigdata.reportBuilder.reportApiImpl;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.pojos.ReportFilter;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.util.CollectionUtil;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.*;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Giribabu on 22/05/15.
 */
public class ReportManager extends NSTestBase {


    /**
     * Creates a tabular report with all the columns in the collection schema supplied.
     *
     * @param collectionInfo - Collection Schema for which report need to created.
     * @return - Payload for running a tabular report.
     */
    public String createDynamicTabularReport(CollectionInfo collectionInfo) {
        return createTabularReport(collectionInfo, null);
    }

    /**
     * Creates tabular report with all the columns specified in the columns argument.
     *
     * @param collectionInfo - CollectionInfo Schema.
     * @param columns - Null - includes all the columns in the collectioninfo, includes only the columns that are specified in the columns array.
     * @return - report master string which can be used to create a report.
     */
    public String createTabularReport(CollectionInfo collectionInfo, String[] columns) {
        String report = null;
        try {
            report = mapper.writeValueAsString(createTabularReportMaster(collectionInfo, columns));
            //Log.info("Report JSON :" + report);

        } catch (Exception e) {
            Log.error("Failed to de-serialize report master." + e.getLocalizedMessage() + ", " + e.getMessage());
            throw new RuntimeException("Failed to create report master" + e.getLocalizedMessage() + ", " + e.getMessage());
        }
        Log.info("Report Creating Completed...");
        return report;
    }

    /**
     * Creates a report master pojo so that can be used to create a new report -its a falt report with all the columns included in show me.
     * @param collectionInfo - CollectionInfo Schema.
     * @param columns - Null - includes all the columns in the collectioninfo, includes only the columns that are specified in the columns array.
     * @return - report master string which can be used to create a report.
     *
     * IMP Note - if your collection has more than 17 columns then please dont add all the columns to show me fields this is limitation on reporting.
     */
    public ReportMaster createTabularReportMaster(CollectionInfo collectionInfo, String[] columns) {
        Log.info("Started Creating a report with all the columns in the collection...");
        String report = null;

        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setSchemaName(collectionInfo.getCollectionDetails().getCollectionName());
        reportInfo.setCollectionID(collectionInfo.getCollectionDetails().getCollectionId());

        Set<String> tempColumns = new HashSet<>();
        if (columns != null) {
            tempColumns.addAll(Arrays.asList(columns));
        }

        List<ReportInfo.Dimension> dimensionList = new ArrayList<>();
        ReportInfo.Dimension dimension;
        for (CollectionInfo.Column column : collectionInfo.getColumns()) {
            if (columns != null && columns.length > 0 && !tempColumns.contains(column.getDisplayName())) {
                continue;
            }
            dimension = new ReportInfo.Dimension();
            dimension.setCol(column.getDbName());
            dimension.setAxis("measure");
            dimension.setType(column.getColumnAttributeType());
            dimension.setDataType(column.getDatatype());
            dimension.setAgg_func("count");
            dimension.setFieldDisplayName(column.getDisplayName());
            dimension.setCollectionId(collectionInfo.getCollectionDetails().getCollectionId());
            dimensionList.add(dimension);
        }
        reportInfo.setDimensions(dimensionList);

        ReportMaster reportMaster = new ReportMaster();
        reportMaster.setNewReport(true);
        reportMaster.setReportMasterRequired(false);
        reportMaster.setFormat("JSON");
        reportMaster.setDisplayType("GRID");

        List<ReportInfo> reportInfoList = new ArrayList<>();
        reportInfoList.add(reportInfo);
        reportMaster.setReportInfo(reportInfoList);
        Log.info("Report Creating Completed...");
        return reportMaster;
    }


    /**
     * Converts the report data to list of map that can be easily used.
     *
     * @param reportData - Report data returned from server.
     * @return - List of map of the report data.
     */
    public List<Map<String, String>> convertReportData(String reportData) {
        Log.info("Converting String as List<> of Map<>...");
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            JsonNode node = mapper.readTree(reportData);
            node = node.findPath("data");
            dataList = mapper.readValue(node.toString(), new TypeReference<List<Map<String, String>>>() {
            });
        } catch (IOException e) {
            Log.error("Failed to process data.", e);
            throw new RuntimeException("Failed to process data." + e.getLocalizedMessage());
        }
        Log.info("Data Conversion Done...");
        return dataList;
    }

    /**
     * Populates default value for boolean in the data list.
     *
     * @param dataList       - The data in which boolean fields default values need to be populated.
     * @param collectionInfo - The Collection Info.
     * @return
     */
    public static List<Map<String, String>> populateDefaultBooleanValue(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        Log.info("Started populating default boolean values for empty value... ");
        List<String> keyToUpdate = new ArrayList<>();
        for (CollectionInfo.Column column : collectionInfo.getColumns()) {
            if (column.getDatatype() != null && column.getDatatype().equals("boolean")) {
                keyToUpdate.add(column.getDisplayName());
            }
        }
        if (keyToUpdate.size() > 0) {
            for (Map<String, String> data : dataList) {
                for (String key : keyToUpdate) {
                    if (data.containsKey(key) && data.get(key) != null && !data.get(key).equals("") && !data.get(key).isEmpty()) {
                        data.put(key, String.valueOf(Boolean.valueOf(data.get(key))));
                    }
                }
            }
        }
        Log.info("Returning the boolean processed results...");
        return dataList;
    }


    public static List<Map<String, String>> truncateStringData(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        Log.info("Started truncating string data... ");
        List<String> keyToUpdate = new ArrayList<>();
        for (CollectionInfo.Column column : collectionInfo.getColumns()) {
            if (column.getDatatype() != null && column.getDatatype().equalsIgnoreCase("string")) {
                keyToUpdate.add(column.getDisplayName());
            }
        }
        HashMap<String, CollectionInfo.Column> displayColumnMap = CollectionUtil.getDisplayNameColumnsMap(collectionInfo);
        if (keyToUpdate.size() > 0) {
            for (Map<String, String> data : dataList) {
                for (String key : keyToUpdate) {
                    if (data.containsKey(key) && data.get(key) != null
                            && !data.get(key).isEmpty()
                            && displayColumnMap.containsKey(key)    //Note here if the field is not present in the collection master, data will not be truncated, will be ignored.
                            && data.get(key).length() > displayColumnMap.get(key).getMaxLength()) {
                        data.put(key, data.get(key).substring(0, displayColumnMap.get(key).getMaxLength()-3)+"..."); //This is done has we are not accepting strings that are not accepting strings more than 250 characters.
                    }
                }
            }
        }
        Log.info("Returning the boolean processed results...");
        return dataList;
    }

    /**
     * Adds empty keys to the data list provided.
     *
     * @param dataList - DataList i.e. report data as key, values pairs.
     * @param keys     - Array of keys to be added to data list.
     */
    public static void addKeysWithEmptyValues(List<Map<String, String>> dataList, String[] keys) {
        if (dataList == null || keys == null || keys.length == 0) {
            throw new IllegalArgumentException("DataList, Keys Should Not be Null & Keys length should be at-least 1");
        }
        for (Map<String, String> data : dataList) {
            for (String key : keys) {
                data.put(key, "");
            }
        }
    }


    /**
     * Replaces the system names with display names.
     *
     * @param dataList       - The Report data.
     * @param collectionInfo - Collection Schema.
     * @return List of Map of String, String  with replaced system names/DB names with display name.
     */
    public static List<Map<String, String>> getProcessedReportData(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        Log.info("Started changing DB names with display Names...");
        HashMap<String, String> dbDisplayNamesMap = new HashMap<>();

        for (CollectionInfo.Column c : collectionInfo.getColumns()) {
            dbDisplayNamesMap.put(c.getDbName(), c.getDisplayName());
        }

        List<Map<String, String>> processedData = new ArrayList<>();

        HashMap<String, String> temp;
        for (Map<String, String> data : dataList) {
            temp = new HashMap<>();
            for (String a : data.keySet()) {
                if (dbDisplayNamesMap.get(a) != null) {
                    temp.put(dbDisplayNamesMap.get(a), (data.get(a) == null) ? "" : data.get(a));
                }
            }
            processedData.add(temp);
        }
        Log.info("Changed DB Names to Display Names & returning...");
        return processedData;
    }

    /**
     * Runs the report on servers and returns the data returned from server.
     *
     * @param reportMaster - Report payload.
     * @return - Report data.
     */
    public String runReport(String reportMaster) {
        Log.info("Started Running the report on server...");
        try {
            ResponseObj responseObj = wa.doPost(API_REPORT_RUN, header.getAllHeaders(), reportMaster);
            if (responseObj != null && responseObj.getStatusCode() == HttpStatus.SC_OK) {
                Log.info("Report Ran Successfully...");
                return responseObj.getContent();
            } else {
                Log.error("Server Status returned :" + responseObj.getStatusCode());
                throw new RuntimeException("Failed to runReport, server returned status code - " + responseObj.getStatusCode());
            }
        } catch (Exception e) {
            Log.error("Failed to Run Report...", e);
            throw new RuntimeException("Failed Run Report..." + e.getLocalizedMessage());
        }
    }

    /**
     * Runs the links preparation report.
     *
     * @param reportMaster - Reportmaster that's required to run the report.
     * @return - List of hash map of report data, null if there's no data present.
     * @throws Exception
     */
    public List<Map<String, String>> runReportLinksAndGetData(String reportMaster) throws Exception {
        Log.info("Started Running the report on server...");
        NsResponseObj nsResponseObj = runReportLinksGetNsResponse(reportMaster);
        List<Map<String, String>> data = null;
        if(nsResponseObj.isResult()) {
            HashMap<String, Object> resultSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
            if(resultSet.containsKey("data")) {
                data = mapper.convertValue(resultSet.get("data"), new TypeReference<List<Map<String, String>>>(){});
            }
        }
        return data;
    }

    /**
     * Replaces the display names with DB names in the report master.
     *
     * @param reportMaster - Report master
     * @param collectionInfo - Collection Master.
     * @return - report master - with display names replaced with db names.
     */
    public ReportMaster getDBNamesPopulatedReportMaster(ReportMaster reportMaster, CollectionInfo collectionInfo) {
        HashMap<String, String> displayDBNamesMap = getDisplayAndDBNamesMap(collectionInfo);
        ReportInfo reportInfo = reportMaster.getReportInfo().get(0);
        if(reportInfo == null) {
            throw new IllegalArgumentException("Report info can't be null.");
        }
        if(collectionInfo ==null || collectionInfo.getColumns() == null
                || collectionInfo.getCollectionDetails() ==null || collectionInfo.getCollectionDetails().getCollectionId() == null) {
            throw new IllegalArgumentException("Collection info can't be null");
        }

        String collectionId = collectionInfo.getCollectionDetails().getCollectionId();
        Log.info("Collection ID : " + collectionId);

        reportInfo.setCollectionID(collectionId);


        if(reportInfo.getDimensions() !=null) {
            Log.info("Updating DB Names for dimensions...");
            for (ReportInfo.Dimension dimension : reportInfo.getDimensions()) {
                if (displayDBNamesMap.containsKey(dimension.getCol()) && displayDBNamesMap.get(dimension.getCol()) != null) {
                    String dbName = displayDBNamesMap.get(dimension.getCol());
                    dimension.setCol(dbName);
                    dimension.setCollectionId(collectionId);
                } else {
                    throw new RuntimeException(dimension.getCol() + " is not found in displayDBNamesMap.");
                }
            }
        }

        if(reportInfo.getDrillDownReportDimensions() != null) {
            Log.info("Updating DB Names for Drill Down Dimensions...");
            for (ReportInfo.Dimension dimension : reportInfo.getDrillDownReportDimensions()) {
                if (displayDBNamesMap.containsKey(dimension.getCol()) && displayDBNamesMap.get(dimension.getCol()) != null) {
                    String dbName = displayDBNamesMap.get(dimension.getCol());
                    dimension.setCol(dbName);
                    dimension.setCollectionId(collectionId);
                } else {
                    throw new RuntimeException(dimension.getCol() + " is not found in displayDBNamesMap.");
                }
            }
        }

        if(reportInfo.getWhereAdvanceFilter() != null) {
            Log.info("Updating DB Names for Where Advanced Filter...");
            for(ReportFilter reportFilter : reportInfo.getWhereAdvanceFilter().getReportFilters()) {
                if (reportFilter.getDbName() != null && displayDBNamesMap.containsKey(reportFilter.getDbName()) && displayDBNamesMap.get(reportFilter.getDbName()) != null) {
                    String dbName = displayDBNamesMap.get(reportFilter.getDbName());
                    reportFilter.setDbName(dbName);
                    reportFilter.setCollectionId(collectionId);
                } else {
                    throw new RuntimeException(reportFilter.getDbName() + " is not found in displayDBNamesMap.");
                }
            }
        }

        if(reportInfo.getHavingAdvanceFilter() != null) {
            Log.info("Updating DB Names for Having Advanced Filter...");
            for(ReportFilter reportFilter : reportInfo.getHavingAdvanceFilter().getReportFilters()) {
                if (reportFilter.getDbName() != null && displayDBNamesMap.containsKey(reportFilter.getDbName()) && displayDBNamesMap.get(reportFilter.getDbName()) != null) {
                    String dbName = displayDBNamesMap.get(reportFilter.getDbName());
                    reportFilter.setDbName(dbName);
                    reportFilter.setCollectionId(collectionId);
                } else {
                    throw new RuntimeException(reportFilter.getDbName() + " is not found in displayDBNamesMap.");
                }
            }
        }
        return reportMaster;
    }

    /**
     * Return the hash map of display names and db names of a collection columns.
     *
     * @param collectionInfo - Collection master of a subject area.
     * @return - Hash Map with display names as keys and db names has values.
     */
    public static HashMap<String, String> getDisplayAndDBNamesMap(CollectionInfo collectionInfo) {
        if(collectionInfo == null || collectionInfo.getColumns() == null) {
            throw new IllegalArgumentException("Collection info & Columns List can't be null");
        }
        HashMap<String, String> resultMap = new HashMap<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            resultMap.put(column.getDisplayName(), column.getDbName());
        }

        Log.info("Result Map : " +resultMap);
        return resultMap;
    }


    /**
     * Run Report with links (joins).
     * @param reportMaster
     * @return ResponseObj - returned from server.
     * @throws Exception
     */
    public ResponseObj runReportLinksGetResponseObj(String reportMaster) throws Exception {
        Log.info("Running Links Report...");
        ResponseObj responseObj = wa.doPost(API_REPORT_RUN_LINKS, header.getAllHeaders(), reportMaster);
        return responseObj;
    }

    /**
     * Run Report with links.
     *
     * @param reportMaster
     * @return - NsResponseObj - if the http request is 200 ok then nsreponse obj is returned.
     * @throws Exception
     */
    public NsResponseObj runReportLinksGetNsResponse(String reportMaster) throws Exception {
        ResponseObj responseObj = runReportLinksGetResponseObj(reportMaster);
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Status Code is not 200 / 400, Check above...");
        }
        return nsResponseObj;
    }

    /**
     * Save a report master - Creates a new report / updates the existing report.
     *
     * @param reportMaster - Report to be created.
     * @return
     * @throws Exception
     */
    public String saveReport(String reportMaster) throws Exception {
        String reportId = null;
        NsResponseObj nsResponseObj = saveReportGetNsResponse(reportMaster);
        if(nsResponseObj.isResult()) {
            HashMap<String, String> resultSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
            reportId = resultSet.get("ReportId");
        }
        Log.info("Report Id : " +reportId);
        return reportId;
    }

    /**
     * Save a report master - Creates a new report / updates the existing report.
     *
     * @param reportMaster - Report to be created.
     * @return
     * @throws Exception
     */
    public ResponseObj saveReportGetResponsObj(String reportMaster) throws Exception {
        Log.info("Saving Report...");
        ResponseObj responseObj = wa.doPut(API_REPORT_PUT, reportMaster, header.getAllHeaders());
        return responseObj;
    }

    /**
     * Save a report master - Creates a new report / updates the existing report.
     *
     * @param reportMaster
     * @return
     * @throws Exception
     */
    public NsResponseObj saveReportGetNsResponse(String reportMaster) throws Exception {
        ResponseObj responseObj = saveReportGetResponsObj(reportMaster);
        NsResponseObj nsResponseObj =null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK || responseObj.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Status Code is not 200 / 400, Check above...");
        }
        return nsResponseObj;
    }

    /**
     * Fetches all the collection master records with out collection info.
     *
     * @return
     * @throws Exception
     */
    public List<CollectionInfo> getAllCollectionsLite() throws Exception {
        ResponseObj responseObj = wa.doGet(API_COLLECTION_ALL_LITE, header.getAllHeaders());
        List<CollectionInfo> data = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            if(nsResponseObj.isResult()) {
                HashMap<String, Object> resultSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                if(resultSet.containsKey("Collections")) {
                    data = mapper.convertValue(resultSet.get("Collections"), new TypeReference<List<CollectionInfo>>(){});
                }
            }
        }
        return data;
    }

    /**
     * Deletes a report.
     *
     * @param reportId - report id to delete.
     * @return true on success & false on failure
     */
    public boolean deleteReport(String reportId) {
        boolean result = false;
        if(reportId == null) {
            throw new IllegalArgumentException("Report Id Can't be null");
        }
        try {
            ResponseObj responseObj = wa.doDelete(API_REPORT_DELETE+reportId, header.getAllHeaders());
            if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                result = nsResponseObj.isResult();
            }
        } catch (Exception e) {
            Log.error("Failed While deleting report ", e);
        }
        return result;
    }

    /**
     * Get the colleciton master tree structure usually used to fetch joins.
     *
     * @param collectionId - Collection ID.
     * @return
     * @throws Exception
     */
    public NsResponseObj getCollectionTreeNsResponse(String collectionId) throws Exception {
        if(collectionId != null) {
            throw new RuntimeException("Collection Id Can't be null.");
        }
        ResponseObj responseObj = wa.doGet(String.format(API_COLLECTION_TREE, collectionId), header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed while getting collection tree.");
        }
        return nsResponseObj;
    }

    /**
     * Fetch all the reports that are configured.
     *
     * @return
     * @throws Exception
     */
    public NsResponseObj getAllReportsNsResponse() throws Exception {
        ResponseObj responseObj = wa.doGet(API_REPORT_GET_ALL, header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed while getting collection tree.");
        }
        return nsResponseObj;
    }

    /**
     * Fetch all the report that are configured.
     *
     * @return
     * @throws Exception
     */
    public List<ReportMaster> getAllReports() throws Exception {
        NsResponseObj nsResponseObj = getAllReportsNsResponse();
        List<ReportMaster> reportMasters = null;
        if(nsResponseObj.isResult()) {
            HashMap<String, Object> resutlSet = mapper.convertValue(nsResponseObj.getData(), HashMap.class);
            if(resutlSet.containsKey("Reports")) {
                reportMasters = mapper.convertValue(resutlSet.get("Reports"), new TypeReference<List<ReportMaster>>(){});
            }
        }
        return reportMasters;
    }
}
