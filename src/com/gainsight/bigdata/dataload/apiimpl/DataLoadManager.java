package com.gainsight.bigdata.dataload.apiimpl;

import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.dataload.pojo.DataLoadStatusInfo;
import com.gainsight.bigdata.dataload.enums.DataLoadStatusType;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gainsight.bigdata.urls.AdminURLs.*;

/**
 * Created by Giribabu on 13/05/15.
 */
public class DataLoadManager extends NSTestBase {

    public Header headers = new Header();

    @BeforeClass
    public void DataLoadManager() {
        headers.addHeader("Content-Type", "application/json");
        headers.addHeader("accessKey", getDataLoadAccessKey());
        headers.addHeader("appOrgId", sfinfo.getOrg());
        headers.addHeader("loginName", sfinfo.getUserName());
    }


    /**
     * Performs MDA authentication used via data load tool.
     *
     * @param sfOrgId - Salesforce Organisation ID - 15 (or) 18 digits.
     * @param accessKey - AccessKey to load data.
     * @param loginName - Salesforce User Login Name.
     * @return NSResponseObj - if authentication is successful with the given params & NULL on failure.
     */
    public NsResponseObj mdaDataLoadAuthenticate(String sfOrgId, String accessKey, String loginName) {
        NsResponseObj nsResponseObj = null;
        ResponseObj responseObj = mdaAuthenticate(sfOrgId, accessKey, loginName);
        try {
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            } else {
                Log.error("Authentication Failure.");
            }
        } catch (Exception e) {
            Log.error("Failed to parse", e);
            throw new RuntimeException("Failed to parse"+e.getLocalizedMessage());
        }
        return nsResponseObj;
    }

    public ResponseObj mdaAuthenticate(String sfOrgId, String accessKey, String loginName) {
        Header head = new Header();
        head.addHeader("appOrgId", NSUtil.convertSFID_15TO18(sfOrgId));
        head.addHeader("accessKey", accessKey);
        head.addHeader("loginName", loginName);
        try {
            ResponseObj responseObj = wa.doGet(ADMIN_DATA_LOAD_AUTHENTICATE, head.getAllHeaders());
            return responseObj;
        } catch (Exception e) {
            Log.error("Failed to perform authentication.", e);
            throw new RuntimeException("Failed to perform authentication");
        }
    }

    /**
     * Creates a collection/Subject area in MDA.
     *
     * @param collectionInfo - Collection Schema/Table Schema to create a subject area.
     * @return - NSResponse Object.
     */
    public NsResponseObj createSubjectArea(CollectionInfo collectionInfo) {
        if (collectionInfo == null) {
            throw new RuntimeException("Collection Info should not be NULL");
        }
        NsResponseObj nsResponseObj = null;
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_COLLECTIONS, headers.getAllHeaders(), mapper.writeValueAsString(collectionInfo));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            }
        } catch (Exception e) {
            Log.error("Failed to created subject area", e);
            throw new RuntimeException("Failed to created subject area" + e.getLocalizedMessage());
        }
        return nsResponseObj;
    }

    /**
     * Gets the data load job details.
     *
     * @param jobId - JobId/StatusId - to check the status.
     * @return JOB Result.
     */
    public DataLoadStatusInfo getDataLoadJobStatus(String jobId) {
        if (jobId == null) {
            throw new RuntimeException("Job Id is mandatory");
        }
        DataLoadStatusInfo statusInfo = null;
        try {
            ResponseObj responseObj = wa.doGet(ADMIN_DATA_LOAD_STATUS + jobId, headers.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if (nsResponseObj.isResult()) {
                    statusInfo = mapper.convertValue(nsResponseObj.getData(), DataLoadStatusInfo.class);
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get job status", e);
            throw new RuntimeException("Failed to get job status" + e.getLocalizedMessage());
        }
        return statusInfo;
    }

    /**
     * Waits for MAX_NO_OF_REQUESTS*10 sec for the Job to complete
     *
     * @param jobId - JobId/StatusId to wait for its completion i.e status != IN_PROGRESS.
     */
    public void waitForDataLoadJobComplete(String jobId) {
        for (int i = 0; i < MAX_NO_OF_REQUESTS; i++) {
            DataLoadStatusInfo statusInfo = getDataLoadJobStatus(jobId);
            if (statusInfo != null) {
                if (statusInfo.getStatusType().equals(DataLoadStatusType.IN_PROGRESS)) {
                    Log.info("Data Load Under Progress...");
                    Log.info("FailureCount :" + statusInfo.getFailureCount() + " ::: " + "SuccessCount : " + statusInfo.getSuccessCount());
                    Timer.sleep(10); //Sleep for 10 seconds and try again.
                } else {
                    Log.info("Data Load Status :" + statusInfo.getStatusType());
                    Log.info("FailureCount :" + statusInfo.getFailureCount() + " ::: " + "SuccessCount : " + statusInfo.getSuccessCount());
                    break;
                }
            } else {
                Log.error("Wait for data load failed for Job Id : " + jobId);
                throw new RuntimeException("Wait for data load for Job Id - " + jobId + " failed.");
            }
        }
    }

    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param DLMetadata - DataLoadMetadata - Used for data load mapping purpose.
     * @param filePath - File to load.
     * @return - Status/JobID after the data load.
     */
    public String dataLoadManage(DataLoadMetadata DLMetadata, String filePath) {
        if(DLMetadata==null || filePath==null) {
            throw new RuntimeException("Meta data & file path is mandatory.");
        }
        return dataLoadManage(DLMetadata, new File(filePath));
    }

    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param metadata - metadata/schema i.e. CSV file header to collection field mapping - used to load the CSV file
     * @param filePath - File path to be loaded to MDA.
     * @return - Status/JobID after the data load.
     */
    public String dataLoadManage(String metadata, String filePath) {
        if(metadata==null || filePath==null) {
            throw new RuntimeException("Meta data & file path is mandatory.");
        }
        return dataLoadManage(metadata, new File(filePath));
    }

    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param DLMetadata - DataLoadMetadata - Used for data load mapping purpose.
     * @param file - File to load.
     * @return - Status/JobID of the data load.
     */
    public String dataLoadManage(DataLoadMetadata DLMetadata, File file) {
        if(DLMetadata==null && file==null) {
            throw new RuntimeException("Metadata & file are not null");
        }
        String metadata = null;
        try {
            metadata = mapper.writeValueAsString(DLMetadata);
        } catch (IOException e) {
            Log.error("Failed to parser metadata.", e);
            throw new RuntimeException("Failed to parse metadata" +e);
        }
        return dataLoadManage(metadata, file);
    }


    /**
     * Load the CSV file by using the metadata information provided.
     *
     * @param metadata - metadata/schema i.e. CSV file header to collection field mapping - used to load the CSV file
     * @param file - File to be loaded to MDA.
     * @return - Status/JobID after the data load.
     */
    public String dataLoadManage(String metadata, File file) {
        if(metadata==null && file==null) {
            throw new RuntimeException("Metadata & file are not null");
        }

        headers.removeHeader("Content-Type");
        String jobId = null;

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        StringBody value = new StringBody(metadata, ContentType.APPLICATION_JSON);
        builder.addPart("metadata", value);

        FileBody body = new FileBody(file, ContentType.MULTIPART_FORM_DATA);
        builder.addPart("file", body);
        HttpEntity reqEntity = builder.build();
        Log.info(ADMIN_DATA_LOAD_IMPORT);
        try {
            ResponseObj responseObj = wa.doPost(ADMIN_DATA_LOAD_IMPORT, headers.getAllHeaders(), reqEntity);
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                NsResponseObj nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                if(nsResponseObj.isResult()) {
                    HashMap<String, String> serverData = (HashMap<String, String>)mapper.convertValue(nsResponseObj.getData(), HashMap.class);
                    Log.info("Status ID : " + serverData.get("statusId"));
                    jobId = serverData.get("statusId");
                } else {
                    throw new RuntimeException("Failed to load data");
                }
            }
        } catch (Exception e) {
            Log.error("Failed to load data", e);
            throw new RuntimeException("Failed to load data, "+e.getLocalizedMessage());
        } finally {
            headers.addHeader("Content-Type","application/json");
        }
        return jobId;
    }

    /**
     * Exports all the failed records as string with new line as line separator & with headers.
     *
     * @param statusId - The StatusId(JobId) to retrieve the failed records
     * @return StringContent returned by server.
     */
    public String exportFailedRecords(String statusId) {
        String result= null;
        try {
            ResponseObj responseObj = wa.doGet(ADMIN_DATA_LOAD_EXPORT_FAILURES + statusId, headers.getAllHeaders());
            if(responseObj.getStatusCode()==HttpStatus.SC_OK) {
                  result = responseObj.getContent();
            }
        } catch (Exception e) {
            Log.error("Failed to export data for status ID :" +statusId);
            throw new RuntimeException("Failed to export data for status ID :"+e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * Exports all the failed records to the CSV file provided.
     *
     * @param statusId - The StatusId(JobId) to retrieve the failed records
     * @param file - Writes to the file specified.
     */
    public void exportFailedRecords(String statusId, File file) {
        String data = exportFailedRecords(statusId);
        if(file.exists()) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(file), ',', '"', '\\', "\n");
                List<String[]> rows = new ArrayList<>();
                String[] cols;
                for(String row : data.split("\\n")) {
                    cols = row.split(",");
                    rows.add(cols);
                }
                writer.writeAll(rows);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new RuntimeException("File not found" +file.getAbsolutePath());
        }
    }




}
