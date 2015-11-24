package com.gainsight.sfdc.util.bulk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.beans.*;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.sfdc.util.db.QueryBuilder;
import com.gainsight.testdriver.Log;
import com.gainsight.util.config.SfdcConfig;
import com.gainsight.util.config.SfdcConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;

/**
 * Standalone class for SFDC push/pull mechanism 
 * @author Sunand
 *
 */
public class SfdcBulkApi {

    public static String basedir = System.getProperty("basedir", ".");
	public static String api_version = "28.0";
	public static String async_url = "/services/async/";
	public static String async_job_url = "";
	public static final int BULK_INSERT_LIMIT = 10000;
	
	static SfdcBulkOperationImpl op;
    static SFDCInfo sfdcInfo;
    static SalesforceConnector sfdc;
    public static SfdcConfig sfdcConfig = ConfigProviderFactory.getConfig(SfdcConfig.class);;

    static {
        sfdc = new SalesforceConnector(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword()+sfdcConfig.getSfdcStoken(), sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
        sfdc.connect();
        sfdcInfo = sfdc.fetchSFDCinfo();
        op = new SfdcBulkOperationImpl(sfdcInfo.getSessionId());
        async_job_url = sfdcInfo.getEndpoint() + async_url + api_version + "/job";
    }

	/**
	 * Push data to SFDC
	 * @param sObject
	 * @param operation
	 * @param csvFile
	 * @throws IOException
	 */
	public static void pushDataToSfdc(String sObject, String operation, File csvFile) throws IOException {
		//Create Job with parameters and change the last parameter to csv or xml for the 5th parameter to change the response content type while registering a job
		String job_id = op.createJob(async_job_url, operation, sObject, "Parallel", "CSV");
		if(job_id == null) throw new RuntimeException("Failed to create bulk JOB. Check the logs for more info.");
		
		//Framing the url's for batch from resulting job url 
		String async_job_status_url = async_job_url +"/" + job_id;
		String async_batch_url = async_job_status_url + "/batch";

        List<String> batchIds = new ArrayList<String>();
        BufferedReader rdr = new BufferedReader(new FileReader(csvFile));
        File tmpFile =  new File(basedir+"/resources/datagen/process/tempFile.csv");
        // read the CSV header row
        byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
        int headerBytesLength = headerBytes.length;
        try {
            FileOutputStream tmpOut = new FileOutputStream(tmpFile);
            int maxBytesPerBatch = 10000000; // 10 million bytes per batch
            int maxRowsPerBatch = 10000; // 10 thousand rows per batch
            int currentBytes = 0;
            int currentLines = 0;
            String nextLine;
            while ((nextLine = rdr.readLine()) != null) {
                byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
                // Create a new batch when our batch size limit is reached
                if (currentBytes + bytes.length > maxBytesPerBatch
                        || currentLines > maxRowsPerBatch) {
                    String batch_id = op.addBatchToJob(async_batch_url, tmpFile);
                    if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
                    batchIds.add(batch_id);
                    currentBytes = 0;
                    currentLines = 0;
                }
                if (currentBytes == 0) {
                    tmpOut = new FileOutputStream(tmpFile);
                    tmpOut.write(headerBytes);
                    currentBytes = headerBytesLength;
                    currentLines = 1;
                }
                tmpOut.write(bytes);
                currentBytes += bytes.length;
                currentLines++;
            }
            // Finished processing all rows
            // Create a final batch for any remaining data
             if (currentLines > 1) {
                 String batch_id = op.addBatchToJob(async_batch_url, tmpFile);
                 if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
                 batchIds.add(batch_id);

             }
         } finally {
             tmpFile.delete();
         }
        op.setJobState(async_job_status_url, "Closed");
        //String batch_id = op.addBatchToJob(async_batch_url, csvFile);
		//if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
        String async_batch_status_url = null;
        List<String> tempList = new ArrayList<String>(batchIds);
        boolean waitResult = false;
        //Waiting until the batch job is complete
        while (!tempList.isEmpty()) {
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {}
            for(String batch_id : batchIds) {
                async_batch_status_url = async_job_url + "/" + job_id + "/batch/" + batch_id;
                String result = op.getBatchStatus(async_batch_status_url);
                Log.info("OUTPUT:\n" + result);
                if(result.contains("Completed")) {
                    tempList.remove(batch_id);
                } else if(result.equalsIgnoreCase("Records Failed")) {
                    tempList.remove(batch_id);
                } else if(result.equalsIgnoreCase("Failed")) {
                    tempList.remove(batch_id);
                }
            }
            System.out.println("Awaiting results..." + tempList.size());
        }
	}
	
	public static void pushDataToSfdc(String sObject, SfdcOperationType operation, File csvFile, int recordCount) throws IOException {
		//Create Job with parameters and change the last parameter to csv or xml for the 5th parameter to change the response content type while registering a job
		String job_id = op.createJob(async_job_url, operation.toString(), sObject, "Parallel", "CSV");
		if(job_id == null) throw new RuntimeException("Failed to create bulk JOB. Check the logs for more info.");
		
		//Framing the url's for batch from resulting job url 
		String async_job_status_url = async_job_url +"/" + job_id;
		String async_batch_url = async_job_status_url + "/batch";
		
		//Here I need to split the files and give it as multiple batch inputs
		//Yet to COMPLETE - SUNAND


        String batch_id = op.addBatchToJob(async_batch_url, csvFile);
		if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
		
		String async_batch_status_url = async_job_url + "/" + job_id + "/batch/" + batch_id;
		//Waiting until the batch job is complete
		boolean waitResult = waitUntilBatchJobComplete(async_batch_status_url, 10, 60);
		if(waitResult) {
			//Fetching result from batch
			String output = op.fetchResult(async_batch_status_url);
			Log.info("OUTPUT:\n" + output);
			op.setJobState(async_job_status_url, "Closed");
		}
		else
			op.setJobState(async_job_status_url, "Aborted");
	}
	
	/**
	 * Pull data from SFDC
	 * @param sObject
	 * @param query
	 * @param filePath
	 * @throws IOException
	 */
	public static void pullDataFromSfdc(String sObject, String query, String filePath) throws IOException {
		//Create Job with parameters and change the last parameter to csv or xml for the 5th parameter to change the response content type while registering a job
		String job_id = op.createJob(async_job_url, "query", sObject, "Parallel", "CSV");
		if(job_id == null) throw new RuntimeException("Failed to create bulk JOB. Check the logs for more info.");
		
		//Framing the url's for batch from resulting job url 
		String async_job_status_url = async_job_url +"/" + job_id;
		String async_batch_url = async_job_status_url + "/batch";
		String batch_id = op.addBatchToJob(async_batch_url, query);
		if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
		
		String async_batch_status_url = async_job_url + "/" + job_id + "/batch/" + batch_id;
		//Waiting until the batch job is complete
		boolean waitResult = waitUntilBatchJobComplete(async_batch_status_url, 10, 600);
		if(waitResult) {
			//Fetching result from batch
			String output = op.fetchResult(async_batch_status_url);
			Log.info("OUTPUT:\n" + output);
			try {
				//Save the file to the respective destination
                File tempFile = new File(filePath);
                tempFile.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(tempFile);

				fos.write(output.getBytes());
				fos.close();
				op.setJobState(async_job_status_url, "Closed");
			}
			catch (FileNotFoundException e) {
				// TODO: handle exception
				System.out.println("Mostly File Not Found");
				op.setJobState(async_job_status_url, "Aborted");
				e.printStackTrace();
			}
		}
		else
			op.setJobState(async_job_status_url, "Aborted");
	}

    public static void cleanUp(String query) throws IOException {
        String temp = query.substring(query.toLowerCase().lastIndexOf("from")+4).trim();
        String sObject;
        if(temp.indexOf(" ") != -1) {
            sObject = FileUtil.resolveNameSpace(temp.substring(0, temp.indexOf(" ")).trim(), sfdcConfig.getSfdcNameSpace());
        } else {
            sObject = temp;
        }

        int recordCount = sfdc.getRecordCount(query);
        Log.info("Count of Records : " + recordCount);
        if(recordCount == 0) {
            Log.info("No Records to Delete on Object " +sObject);
            return;
        }
        Log.info("Pulling Records for Query : " + query);
        String path = basedir+"/resources/datagen/process/"+sObject+"_cleanup.csv";
        Log.info("Output File Loc : " + path);
        pullDataFromSfdc(sObject, query, path);
        File f = new File(path);
        if(f.exists()) {
            Log.info("Pull Completed For Object " +sObject);
        } else {
            Log.error("Pull Failed For Object " +sObject);
            throw new RuntimeException("Failed to Pull Data for object " +sObject);
        }

        Log.info("Now Lets Delete some data...");
        pushDataToSfdc(sObject, "delete", f);
        Log.info("Data Delete Completed.");

    }

	/**
	 *Wait Logic To wait until the batch job status is returned as "Completed"
	 * 
	 * @param url batch status url
	 * @param interval time in seconds
	 * @param maxTimeout time in seconds
	 * @throws IOException
	 */
	private static boolean waitUntilBatchJobComplete(String url, int interval, int maxTimeout) throws IOException {
		// TODO Auto-generated method stub
		int timeCounter = interval;
		while(true) {
			String status = op.getBatchStatus(url);
			System.out.println("Current State: " + status);
			if(status.equalsIgnoreCase("Completed")) return true;
			else if(status.equalsIgnoreCase("Records Failed")) return false;
			else if(status.equalsIgnoreCase("Failed")) return false;
			else if(timeCounter > maxTimeout ) return false;
			else {
				try {
					Thread.sleep(interval * 1000);
					timeCounter += interval;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

    /**
     *
     * @param sObject
     * @param operation
     * @param csvFile
     * @param externalIDField - for upsert operation, a field should be specified.
     * @throws IOException
     */
    public static void pushDataToSfdc(String sObject, String operation, File csvFile, String externalIDField) throws IOException {
        //Create Job with parameters and change the last parameter to csv or xml for the 5th parameter to change the response content type while registering a job
        String job_id = op.createJob(async_job_url, operation, sObject, "Parallel", "CSV", externalIDField);
        if(job_id == null) throw new RuntimeException("Failed to create bulk JOB. Check the logs for more info.");

        //Framing the url's for batch from resulting job url
        String async_job_status_url = async_job_url +"/" + job_id;
        String async_batch_url = async_job_status_url + "/batch";

        List<String> batchIds = new ArrayList<String>();
        BufferedReader rdr = new BufferedReader(new FileReader(csvFile));
        File tmpFile =  new File(basedir+"/resources/datagen/process/tempFile.csv");
        tmpFile.getParentFile().mkdirs();
        // read the CSV header row
        byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
        int headerBytesLength = headerBytes.length;
        try {
            FileOutputStream tmpOut = new FileOutputStream(tmpFile);
            int maxBytesPerBatch = 10000000; // 10 million bytes per batch
            int maxRowsPerBatch = 10000; // 10 thousand rows per batch
            int currentBytes = 0;
            int currentLines = 0;
            String nextLine;
            while ((nextLine = rdr.readLine()) != null) {
                byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
                // Create a new batch when our batch size limit is reached
                if (currentBytes + bytes.length > maxBytesPerBatch
                        || currentLines > maxRowsPerBatch) {
                    String batch_id = op.addBatchToJob(async_batch_url, tmpFile);
                    if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
                    batchIds.add(batch_id);
                    currentBytes = 0;
                    currentLines = 0;
                }
                if (currentBytes == 0) {
                    tmpOut = new FileOutputStream(tmpFile);
                    tmpOut.write(headerBytes);
                    currentBytes = headerBytesLength;
                    currentLines = 1;
                }
                tmpOut.write(bytes);
                currentBytes += bytes.length;
                currentLines++;
            }
            // Finished processing all rows
            // Create a final batch for any remaining data
            if (currentLines > 1) {
                String batch_id = op.addBatchToJob(async_batch_url, tmpFile);
                if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
                batchIds.add(batch_id);

            }
        } finally {
            tmpFile.delete();
        }
        op.setJobState(async_job_status_url, "Closed");
        //String batch_id = op.addBatchToJob(async_batch_url, csvFile);
        //if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
        String async_batch_status_url = null;
        List<String> tempList = new ArrayList<String>(batchIds);
        boolean waitResult = false;
        //Waiting until the batch job is complete
        while (!tempList.isEmpty()) {
            try {
                Thread.sleep(15000L);
            } catch (InterruptedException e) {}
            for(String batch_id : batchIds) {
                async_batch_status_url = async_job_url + "/" + job_id + "/batch/" + batch_id;
                String result = op.getBatchStatus(async_batch_status_url);
                Log.info("OUTPUT:\n" + result);
                if(result.contains("Completed")) {
                    tempList.remove(batch_id);
                } else if(result.equalsIgnoreCase("Records Failed")) {
                    tempList.remove(batch_id);
                }  else if(result.equalsIgnoreCase("Failed")) {
                    tempList.remove(batch_id);
                }
            }
            System.out.println("Awaiting results..." + tempList.size());
        }
    }
}
