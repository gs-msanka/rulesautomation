package com.gainsight.sfdc.util.bulk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.gainsight.pageobject.core.Report;
import com.gainsight.pojo.Header;
import com.gainsight.pojo.HttpResponseObj;
import com.gainsight.sfdc.util.Template;
import com.gainsight.webaction.WebAction;

/**
 * SFDC Operations Implementation
 * @author Sunand
 *
 */
public class SfdcBulkOperationImpl implements ISfdcBulkOperation {

	public static String session_id = "";
	
	public String basedir = System.getProperty("basedir", ".");
	public String templatePath = basedir + "/resources/template";
	public String createJobTemplate = templatePath + "/CreateJobTemplate.xml";
	public String jobStateTemplate = templatePath + "/JobStateTemplate.xml";
	
	WebAction wa;
	
	public SfdcBulkOperationImpl(String sessionId) {
		wa = new WebAction();
		session_id = sessionId;
	}
	
	@Override
	public String createJob(String uri, String operation, String sObject,
			String concurrencyMode, String contentType) throws IOException {
		File templateFile = new File(createJobTemplate);
		File outputFile = new File(basedir + "/result/CreateJob.xml");
		Template t = new Template(templateFile);
		t.setValue("operation", operation);
		t.setValue("object", sObject);
		t.setValue("concurrencyMode", concurrencyMode);
		t.setValue("contentType", contentType);
		t.export(new FileOutputStream(outputFile));

		FileEntity entity = new FileEntity(outputFile, ContentType.create("text/xml", "UTF-8"));
		HttpResponseObj resp = null;
		
		try {
			Report.logInfo("Create Job URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			h.addHeader("Accept", "application/xml");
			h.addHeader("Content-Type", "application/xml");
			resp = wa.doPost(uri, h.getAllHeaders(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Job Response:\n" + resp.getContent());
		return parseXMLResponse(resp.getContent(), "id");
	}

	@Override
	public String addBatchToJob(String uri, String query) throws IOException {
		StringEntity entity = new StringEntity(query, ContentType.create("text/plain", "UTF-8"));
		HttpResponseObj resp = null;
		try {
			Report.logInfo("Adding Batch to Job URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			h.addHeader("Content-Type", "application/xml");
			resp = wa.doPost(uri, h.getAllHeaders(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Batch Response:\n" + resp.getContent());
		return parseXMLResponse(resp.getContent(), "id");
	}
	
/*	public String[] addBatchToJob(String uri, String[] queries) throws IOException {
		String[] ids = new String[queries.length];
		int index = 0;
		for(String query : queries){
			ids[index] = addBatchToJob(uri, query);
		}
		return ids;
	}*/
	
	@Override
	public String addBatchToJob(String uri, File csvFile) throws IOException {
		FileEntity entity = new FileEntity(csvFile, ContentType.create("text/csv", "UTF-8"));
		HttpResponseObj resp = null;
		try {
			Report.logInfo("Adding Batch to Job URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			h.addHeader("Content-Type", "text/csv");
			resp = wa.doPost(uri, h.getAllHeaders(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Batch Response:\n" + resp.getContent());
		return parseXMLResponse(resp.getContent(), "id");
	}

	@Override
	public String getBatchStatus(String uri) {
		HttpResponseObj resp = null;
		try {
			Report.logInfo("Getting Batch Status URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			resp = wa.doGet(uri, h.getAllHeaders());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Batch Status Response:\n" + resp.getContent());
		String status = parseXMLResponse(resp.getContent(), "state");
		int numberRecordsFailed = Integer.parseInt(parseXMLResponse(resp.getContent(), "numberRecordsFailed"));
		if(status.equalsIgnoreCase("Completed") && numberRecordsFailed == 0 ) 
			return "Completed";
		else if(status.equalsIgnoreCase("Completed") && numberRecordsFailed > 0 ) {
			Report.logInfo("Batch completed with failures.");
			Report.logInfo("Operation : " + parseXMLResponse(resp.getContent(), "operation"));
			Report.logInfo("Number of Records Processed : " + parseXMLResponse(resp.getContent(), "numberRecordsProcessed"));
			Report.logInfo("Number of Records Failed : " + numberRecordsFailed);
			return "Records Failed";
		}
		else if(status.equalsIgnoreCase("Failed")) 
			return "Failed";
		else
			return parseXMLResponse(resp.getContent(), "state");
	}

	@Override
	public void getJobStatus(String uri) {
		// TODO Auto-generated method stub

	}

	@Override
	public String fetchResult(String uri) {
		String resultId = getResultId(uri +"/result");
		HttpResponseObj resp = null;
		try {
			uri = uri +"/result/"+ resultId;
			Report.logInfo("Getting Batch Status URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			resp = wa.doGet(uri, h.getAllHeaders());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Fetched Result.");
		return resp.getContent();
	}

	@Override
	public String setJobState(String uri, String jobState) throws IOException {
		File templateFile = new File(jobStateTemplate);
		File outputFile = new File(basedir + "/result/JobState.xml");
		
		Template t = new Template(templateFile);
		t.setValue("state", jobState);
		t.export(new FileOutputStream(outputFile));

		FileEntity entity = new FileEntity(outputFile, ContentType.create("text/xml", "UTF-8"));
		HttpResponseObj resp = null;
		try {
			Report.logInfo("Settting Job State URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			h.addHeader("Content-Type", "text/csv");
			resp = wa.doPost(uri, h.getAllHeaders(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Job State Response:\n" + resp.getContent());
		String status = parseXMLResponse(resp.getContent(), "state");
		Report.logInfo("Job Status: " + status);
		return status;
	}
	
	private String getResultId(String uri) {
		String resultId = null;
		HttpResponseObj resp = null;
		try {
			Report.logInfo("Getting Batch Status URL: " + uri);
			Header h = new Header();
			h.addHeader("X-SFDC-Session", session_id);
			resp = wa.doGet(uri, h.getAllHeaders());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Report.logInfo("Result ID Response:\n" + resp.getContent());
		resultId = parseXMLResponse(resp.getContent(), "result");
		Report.logInfo("Result ID: " + resultId);
		
		return resultId;
	}

	/**
	 * Parse XML file Using JDOM to get the value of specific Node from Root Node.
	 * Mainly to fetch values of Job ID, batch ID, Result ID etc.
	 * @param content
	 * @param first level nodeToSearch
	 * @return
	 */
	public static String parseXMLResponse(String content,
			String nodeToSearch) {
		String result = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder
					.build(new ByteArrayInputStream(content.getBytes()));
			Element rootNode = document.getRootElement();
			List<Element> list = rootNode.getChildren();
			for (int i = 0; i < list.size(); i++) {
				Element node = list.get(i);
				if (node.getName().equals(nodeToSearch)) {
					Report.logInfo("Node: " + nodeToSearch + " || value: "	+ node.getText());
					result = node.getText();
					break;
				}
			}
		} catch (IOException io) {
			Report.logInfo(io.getMessage());
		} catch (JDOMException jdomex) {
			Report.logInfo(jdomex.getMessage());
		}
		return result;
	}
}
