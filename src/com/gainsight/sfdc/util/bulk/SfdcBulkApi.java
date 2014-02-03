package com.gainsight.sfdc.util.bulk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.util.db.QueryBuilder;

/**
 * Standalone class for SFDC push/pull mechanism 
 * @author Sunand
 *
 */
public class SfdcBulkApi {

	public static String api_version = "28.0";
	public static String async_url = "/services/async/";
	public static String async_job_url = "";
	public static final int BULK_INSERT_LIMIT = 10000;
	
	static SfdcBulkOperationImpl op;
	static SFDCInfo info;
	
	//Whenever we want to make a separate login/auth module we can modularize this piece of code.
	static {
		info = SFDCUtil.fetchSFDCinfo();
		op = new SfdcBulkOperationImpl(info.getSessionId());
		async_job_url = info.getEndpoint() + async_url + api_version + "/job";
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		pushDataToSfdc("JBCXM__UsageData__c", "insert", new File("./resources/datagen/process/db_Month11UD_3.csv"));
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
		String batch_id = op.addBatchToJob(async_batch_url, csvFile);
		if(batch_id == null) throw new RuntimeException("Failed to add BATCH to JOB. Check the logs for more info.");
		
		String async_batch_status_url = async_job_url + "/" + job_id + "/batch/" + batch_id;
		//Waiting until the batch job is complete
		boolean waitResult = waitUntilBatchJobComplete(async_batch_status_url, 10, 60);
		if(waitResult) {
			//Fetching result from batch
			String output = op.fetchResult(async_batch_status_url);
			Report.logInfo("OUTPUT:\n" + output);
			op.setJobState(async_job_status_url, "Closed");
		}
		else
			op.setJobState(async_job_status_url, "Aborted");
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
			Report.logInfo("OUTPUT:\n" + output);
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
		boolean waitResult = waitUntilBatchJobComplete(async_batch_status_url, 10, 60);
		if(waitResult) {
			//Fetching result from batch
			String output = op.fetchResult(async_batch_status_url);
			Report.logInfo("OUTPUT:\n" + output);
			try {
				//Save the file to the respective destination
				FileOutputStream fos = new FileOutputStream(filePath);
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

	public void cleanUp(String sObject) throws Exception {
		//I need to write logic considering governor limits
		int recordCount = SfdcRestApi.countOfRecordsForASObject(sObject);
		System.out.println("Count of Records : " + recordCount);
		int calculateBatch = recordCount/BULK_INSERT_LIMIT;
		System.out.println("Pulling all Records of " + sObject);
		String query = QueryBuilder.buildSOQLQuery(sObject, "Id");
		System.out.println("Pull Query : " + query);
		String path = "./resources/datagen/process/" + sObject + "_cleanup.csv";
		System.out.println("Output File Loc : " + path);
		SfdcBulkApi.pullDataFromSfdc(sObject, query, path);
		File f = new File(path);
		if(f.exists())
			System.out.println("Pull Completed");
		else
			System.out.println("Pull Failed");
		
		//Here i need to split files 
		System.out.println("Now Lets Delete some data");
		SfdcBulkApi.pushDataToSfdc(sObject, "delete", f);
		System.out.println("push done");
		
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
}
