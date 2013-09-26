package com.gainsight.bigdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Custom Test Listener to capture failed methods and assertion messages.
 * Also redirecting the AssertionError messages to log4j.
 * Methods are self explanatory
 * @author Sunand
 *
 */
public class AutTestListener extends TestListenerAdapter {
	
	private Logger logger;
	String baseDir;
	private String failedTestFile;
	
	@Override
	public void onTestFailure(ITestResult tr) {
		logger = Logger.getLogger(tr.getInstanceName());
		logger.info("Method Name: " + tr.getName());
		logger.info("Status: (" + tr.getStatus() +") FAILED");
		logger.info("Assertion Message: " + tr.getThrowable().getMessage());
		long elapsedTime = tr.getEndMillis()-tr.getStartMillis();
		logger.info("Elapsed Time: " + elapsedTime + " milliSeconds" + " OR " + (elapsedTime/1000) + " seconds");
		createFailedTestFile(tr.getInstanceName());
	}
	
	@Override
	public void onTestSuccess(ITestResult tr) {
		logger = Logger.getLogger(tr.getInstanceName());
		logger.info("Method Name: " + tr.getName());
		logger.info("Status: (" + tr.getStatus() +") PASSED");
		long elapsedTime = tr.getEndMillis()-tr.getStartMillis();
		logger.info("Elapsed Time: " + elapsedTime + " milliSeconds" + " OR " + (elapsedTime/1000) + " seconds");
	}
	
	@Override
	public void onConfigurationFailure(ITestResult itr) {
		super.onConfigurationFailure(itr);
		logger = Logger.getLogger(itr.getInstanceName());
		logger.info("Configuration Failure");
		logger.info("Method Name: " + itr.getName());
		logger.info("Status: (" + itr.getStatus() +") FAILED");
		logger.info("Assertion Message: " + itr.getThrowable().getMessage());
		createFailedTestFile(itr.getInstanceName());
	}
	
	/**
	 * Creating Failed XML in testsuite/failed/testng-failed.xml
	 * @param instanceName
	 */
	private void createFailedTestFile(String instanceName) {
		boolean alreadyExists = false;
		baseDir = System.getProperty("basedir", ".");
		failedTestFile = baseDir + "/testsuite/failed/testng-failed.xml";
		try {
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(failedTestFile);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			Element test = rootNode.getChild("test");
			Element classes = test.getChild("classes");
			@SuppressWarnings("unchecked")
			List<Element> subItems = classes.getChildren("class");
			Element failedTest = new Element("class");
			failedTest.setAttribute(new Attribute("name", instanceName));
			
			//Avoiding duplicate failed test Node in the failed xml list.
			for(Element ele : subItems) {
				String attributeName = ele.getAttributeValue("name");
				if(attributeName.equals(instanceName)) {
					alreadyExists = true;
					break;
				}
			}
			
			if(!alreadyExists)
				classes.addContent(failedTest);
			
			XMLOutputter xmlOutput = new XMLOutputter(); 
			// display nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(failedTestFile));
			xmlOutput.output(doc, System.out);
			
			System.out.println("File updated!");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
