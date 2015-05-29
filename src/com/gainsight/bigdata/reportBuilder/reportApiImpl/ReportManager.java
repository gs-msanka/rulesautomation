package com.gainsight.bigdata.reportBuilder.reportApiImpl;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.apiimpl.DataLoadManager;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.reportBuilder.pojos.ReportInfo;
import com.gainsight.bigdata.reportBuilder.pojos.ReportMaster;
import com.gainsight.bigdata.urls.ApiUrls;
import com.gainsight.http.ResponseObj;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Log.info("Started Creating a report with all the columns in the collection...");
        String report = null;

        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setSchemaName(collectionInfo.getCollectionDetails().getCollectionName());
        reportInfo.setCollectionID(collectionInfo.getCollectionDetails().getCollectionId());

        List<ReportInfo.Dimension> dimensionList = new ArrayList<>();
        ReportInfo.Dimension dimension;
        for (CollectionInfo.Column column : collectionInfo.getColumns()) {
            dimension = new ReportInfo.Dimension();
            dimension.setCol(column.getDbName());
            dimension.setAxis("measure");
            dimension.setType(column.getColumnAttributeType());
            dimension.setDataType(column.getDatatype());
            dimension.setAgg_func("count");
            dimension.setFieldDisplayName(column.getDisplayName());
            dimensionList.add(dimension);
        }
        reportInfo.setDimensions(dimensionList);

        ReportMaster reportMaster = new ReportMaster();
        reportMaster.setNewReport(true);
        reportMaster.setReportMasterRequired(true);
        reportMaster.setFormat("JSON");

        List<ReportInfo> reportInfoList = new ArrayList<>();
        reportInfoList.add(reportInfo);
        reportMaster.setReportInfo(reportInfoList);

        try {
            report = mapper.writeValueAsString(reportMaster);
            Log.info("Report JSON :" +report);

        } catch (Exception e) {
            Log.error("Failed to de-serialize report master." +e.getLocalizedMessage()+ ", " +e.getMessage());
            throw new RuntimeException("Failed to create report master" +e.getLocalizedMessage()+", "+e.getMessage());
        }
        Log.info("Report Creating Completed...");
        return report;
    }

    /**
     * Runs the report on servers & returns the data returned from server.
     *
     * @param reportMaster - Report payload.
     * @return - Report data.
     */
    public String runReport(String reportMaster) {
        Log.info("Started Running the report on server...");
        try {
            ResponseObj responseObj =  wa.doPost(ApiUrls.API_REPORT_RUN, header.getAllHeaders(), reportMaster);
            if(responseObj!=null && responseObj.getStatusCode()== HttpStatus.SC_OK) {
                Log.info("Report Ran Successfully...");
                return responseObj.getContent();
            } else {
                Log.error("Server Status returned :" +responseObj.getStatusCode());
                throw new RuntimeException("Failed to runReport, server returned status code - "+responseObj.getStatusCode());
            }
        } catch (Exception e) {
            Log.error("Failed to Run Report..." ,e);
            throw new RuntimeException("Failed Run Report..." +e.getLocalizedMessage());
        }
    }

    /**
     * Converts the report data to list of map that can be easily used.
     *
     * @param reportData - Report data returned from server.
     * @return - List of map of the report data.
     */
    public List<Map<String, String>> convertReportData(String reportData) {
        Log.info("Converting String as List<> of Map<>...");
        List<Map<String,String>> dataList = new ArrayList<>();
        try {
            JsonNode node = mapper.readTree(reportData);
            node = node.findPath("data");
            dataList  = mapper.readValue(node.toString(), new TypeReference<List<Map<String,String>>>() {});
        } catch (IOException e) {
            Log.error("Failed to process data." ,e);
            throw new RuntimeException("Failed to process data." +e.getLocalizedMessage());
        }
        Log.info("Data Conversion Done...");
        return dataList;
    }

    /**
     * Populates default value for boolean in the data list.
     *
     * @param dataList - The data in which boolean fields default values need to be populated.
     * @param collectionInfo - The Collection Info.
     * @return
     */
    public static List<Map<String, String>> populateDefaultBooleanValue(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        Log.info("Started populating default boolean values for empty value... ");
        List<String> keyToUpdate = new ArrayList<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            if(column.getDatatype()!=null && column.getDatatype().equals("boolean")) {
                keyToUpdate.add(column.getDisplayName());
            }
        }
        if(keyToUpdate.size()>0) {
           for(Map<String, String> data : dataList ) {
               for(String key : keyToUpdate) {
                   if(data.containsKey(key)) {
                       data.put(key, String.valueOf(data.get(key)==null || data.get(key)=="" ? false : Boolean.valueOf(data.get(key))));
                   }
               }
           }
        }
        Log.info("Returning the boolean processed results...");
        return dataList;
    }

    public static void addKeysWithEmptyValues(List<Map<String, String>> dataList, String[] keys) {
        if(dataList ==null || keys==null || keys.length == 0) {
            throw new IllegalArgumentException("DataList, Keys Should Not be Null & Keys length should be at-least 1" );
        }
        for(Map<String, String> data : dataList) {
            for(String key : keys) {
                data.put(key, "");
            }
        }
    }


    /**
     * Replaces the system names with display names.
     *
     * @param dataList - The Report data.
     * @param collectionInfo - Collection Schema.
     * @return List<Map> with replaced system names/DB names with display name.
     */
    public static List<Map<String, String>> getProcessedReportData(List<Map<String, String>> dataList, CollectionInfo collectionInfo) {
        Log.info("Started changing DB names with display Names...");
        HashMap<String, String> dbDisplayNamesMap = new HashMap<>();

        for(CollectionInfo.Column c :collectionInfo.getColumns()) {
            dbDisplayNamesMap.put(c.getDbName(), c.getDisplayName());
        }

        List<Map<String, String>> processedData = new ArrayList<>();

        HashMap<String, String> temp;
        for(Map<String, String> data : dataList) {
            temp = new HashMap<>();
            for(String a : data.keySet()) {
                if(dbDisplayNamesMap.get(a)!=null) {
                    temp.put(dbDisplayNamesMap.get(a), (data.get(a) == null) ? "" : data.get(a));
                }
            }
            processedData.add(temp);
        }
        Log.info("Changed DB Names to Display Names & returning...");
        return processedData;
    }


}
