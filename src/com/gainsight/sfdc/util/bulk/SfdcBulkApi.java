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
	
	static SfdcBulkOperationImpl op;
	static SFDCInfo info;
	
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
		//Examples
		//String query1 = QueryBuilder.buildSOQLQuery("PickList__c", "SystemName__c", "Id");
		//String query2 = QueryBuilder.buildSOQLQuery("Account", "Id", "Name");
		//pullDataFromSfdc("PickList__c", query1, "./result/");
		//pullDataFromSfdc("Account", query2);
		//pushDataToSfdc("CustomerInfo__c", "insert", new File("./resources/datagen/process/job1_final_mod.csv"));
		/*String appSettingQuery = QueryBuilder.buildSOQLQuery("JBCXM__ApplicationSettings__c", "JBCXM__AdoptionAggregationColumns__c", "JBCXM__AdoptionAggregationType__c", "JBCXM__AdoptionGranularity__c", "JBCXM__AdoptionMeasureColMap__c");
		pullDataFromSfdc("JBCXM__ApplicationSettings__c", appSettingQuery, "./result/appSettings.csv");*/
		String appSettingQuery = QueryBuilder.buildSOQLQuery("JBCXM__ApplicationSettings__c", "JBCXM__AdoptionAggregationColumns__c", "JBCXM__AdoptionAggregationType__c", "JBCXM__AdoptionGranularity__c", "JBCXM__AdoptionMeasureColMap__c");
		pullDataFromSfdc("JBCXM__ApplicationSettings__c", appSettingQuery, "./result/appSettings.xml");
		//pushDataToSfdc("Account", "insert", new File("./resources/datagen/data/noduplicates.csv"));
	}

	/**
	 * Push data to SFDC
	 * @param sObject
	 * @param operation
	 * @param csvFile
	 * @throws IOException
	 */
	public static void pushDataToSfdc(String sObject, String operation, File csvFile) throws IOException {
		//Create Job with parameters and change the last parameter to csv or xml for the 4th parameter to change the response content type while registering a job
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
	
	/**
	 * Pull data from SFDC
	 * @param sObject
	 * @param query
	 * @param filePath
	 * @throws IOException
	 */
	public static void pullDataFromSfdc(String sObject, String query, String filePath) throws IOException {
		//Create Job with parameters and change the last parameter to csv or xml for the 4th parameter to change the response content type while registering a job
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
