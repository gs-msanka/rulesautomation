package com.gainsight.sfdc.util;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.*;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.utils.ApexUtil;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.soap.metadata.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class PackageUtil {
	private static MetadataConnection metadataConnection;
	private static final String ZIP_FILE = "resources/package/components.zip";
	// manifest file that controls which components get retrieved
	private static final String MANIFEST_FILE = "resources/package/package.xml";
	private static final double API_VERSION = 29.0;
	// one second in milliseconds
	private static final long ONE_SECOND = 1000;
	// maximum number of attempts to deploy the zip file
	private static final int MAX_NUM_POLL_REQUESTS = 50;
	private static File tempFile;
	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));

	public PackageUtil() {
	}

	public static void updateAccountLayout(String filePath, String flag,
			String textPath,boolean managed) throws Exception {
		metadataConnection = login();
		//retrieveZip();
		String replaceText=getFileContents(textPath);		
		if(!managed){
			replaceText=replaceText.replaceAll("JBCXM__", "");			
		}		
		updateLayout(filePath, flag, replaceText);
		deployZip(tempFile.getAbsolutePath());
		if (tempFile != null) {
			tempFile.delete();
		}
	}

	private static void updateLayout(String fileName, String flag,
			String replaceContent) throws IOException {
		ZipFile zipFile = new ZipFile(ZIP_FILE);
		tempFile = File.createTempFile(zipFile.getName(), null);
		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				tempFile.getAbsolutePath()));
		for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
			ZipEntry entryIn = (ZipEntry) e.nextElement();
			System.out.println(entryIn.getName());
			if (!entryIn.getName().equalsIgnoreCase(fileName)) {
				zos.putNextEntry(entryIn);
				InputStream is = zipFile.getInputStream(entryIn);
				String contents = IOUtils.toString(is, "UTF-8");
				zos.write(contents.getBytes());
			} else {
				System.out.println("found the file");
				zos.putNextEntry(new ZipEntry(fileName));
				InputStream is = zipFile.getInputStream(entryIn);
				String content = IOUtils.toString(is, "UTF-8");
				if (!content.contains(flag)) {
					content = content.replaceFirst("<\\/layoutSections>",
							replaceContent);
				}
				zos.write(content.getBytes());
			}
			zos.closeEntry();
		}
		zos.close();
		zipFile.close();
	}

	public static MetadataConnection login() throws ConnectionException {
		final String USERNAME = "spulagam@haskell.com";
		// This is only a sample. Hard coding passwords in source files is a bad
		// practice.
		final String PASSWORD = "gainsite1230DZfbQbT7lmcSdvSXjfQ3pqw1";
		final String URL = "https://login.salesforce.com/services/Soap/c/29.0";
		final LoginResult loginResult = loginToSalesforce(USERNAME, PASSWORD,
				URL);
		return createMetadataConnection(loginResult);
	}

	private static MetadataConnection createMetadataConnection(
			final LoginResult loginResult) throws ConnectionException {
		final ConnectorConfig config = new ConnectorConfig();
		config.setServiceEndpoint(loginResult.getMetadataServerUrl());
		config.setSessionId(loginResult.getSessionId());
		return new MetadataConnection(config);
	}

	private static LoginResult loginToSalesforce(final String username,
			final String password, final String loginUrl)
			throws ConnectionException {
		final ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(loginUrl);
		config.setServiceEndpoint(loginUrl);
		config.setManualLogin(true);
		return (new EnterpriseConnection(config)).login(username, password);
	}

	/*
	 * Utility method to present options to retrieve or deploy.
	 */
	private static String getUsersChoice() throws IOException {
		System.out.println(" 1: Retrieve");
		System.out.println(" 2: Deploy");
		System.out.println("99: Exit");
		System.out.println();
		System.out.print("Enter 1 to retrieve, 2 to deploy, or 99 to exit: ");
		// wait for the user input.
		String choice = reader.readLine();
		return choice != null ? choice.trim() : "";
	}

	private static void deployZip(String filePath) throws Exception {
		byte zipBytes[] = readZipFile(filePath);
		DeployOptions deployOptions = new DeployOptions();
		deployOptions.setPerformRetrieve(false);
		deployOptions.setRollbackOnError(true);
		AsyncResult asyncResult = metadataConnection.deploy(zipBytes,
				deployOptions);
		DeployResult result = waitForDeployCompletion(asyncResult.getId());
		if (!result.isSuccess()) {
			printErrors(result, "Final list of failures:\n");
			throw new Exception("The files were not successfully deployed");
		}
		System.out.println("The file " + ZIP_FILE
				+ " was successfully deployed\n");
	}

	/*
	 * Read the zip file contents into a byte array.
	 */
	private static byte[] readZipFile(String filePath) throws Exception {
		byte[] result = null;
		// We assume here that you have a deploy.zip file.
		// See the retrieve sample for how to retrieve a zip file.
		File zipFile = new File(filePath);
		if (!zipFile.exists() || !zipFile.isFile()) {
			throw new Exception(
					"Cannot find the zip file for deploy() on path:"
							+ zipFile.getAbsolutePath());
		}
		FileInputStream fileInputStream = new FileInputStream(zipFile);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead = 0;
			while (-1 != (bytesRead = fileInputStream.read(buffer))) {
				bos.write(buffer, 0, bytesRead);
			}
			result = bos.toByteArray();
		} finally {
			fileInputStream.close();
		}
		return result;
	}

	/*
	 * Print out any errors, if any, related to the deploy.
	 * 
	 * @param result - DeployResult
	 */
	private static void printErrors(DeployResult result, String messageHeader) {
		DeployDetails details = result.getDetails();
		StringBuilder stringBuilder = new StringBuilder();
		if (details != null) {
			DeployMessage[] componentFailures = details.getComponentFailures();
			for (DeployMessage failure : componentFailures) {
				String loc = "(" + failure.getLineNumber() + ", "
						+ failure.getColumnNumber();
				if (loc.length() == 0
						&& !failure.getFileName().equals(failure.getFullName())) {
					loc = "(" + failure.getFullName() + ")";
				}
				stringBuilder.append(
						failure.getFileName() + loc + ":"
								+ failure.getProblem()).append('\n');
			}
			RunTestsResult rtr = details.getRunTestResult();
			if (rtr.getFailures() != null) {
				for (RunTestFailure failure : rtr.getFailures()) {
					String n = (failure.getNamespace() == null ? "" : (failure
							.getNamespace() + ".")) + failure.getName();
					stringBuilder.append("Test failure, method: " + n + "."
							+ failure.getMethodName() + " -- "
							+ failure.getMessage() + " stack "
							+ failure.getStackTrace() + "\n\n");
				}
			}
			if (rtr.getCodeCoverageWarnings() != null) {
				for (CodeCoverageWarning ccw : rtr.getCodeCoverageWarnings()) {
					stringBuilder.append("Code coverage issue");
					if (ccw.getName() != null) {
						String n = (ccw.getNamespace() == null ? "" : (ccw
								.getNamespace() + ".")) + ccw.getName();
						stringBuilder.append(", class: " + n);
					}
					stringBuilder.append(" -- " + ccw.getMessage() + "\n");
				}
			}
		}
		if (stringBuilder.length() > 0) {
			stringBuilder.insert(0, messageHeader);
			System.out.println(stringBuilder.toString());
		}
	}

	private static void retrieveZip() throws Exception {
		RetrieveRequest retrieveRequest = new RetrieveRequest();
		retrieveRequest.setApiVersion(API_VERSION);
		setUnpackaged(retrieveRequest);
		AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
		asyncResult = waitForRetrieveCompletion(asyncResult);
		RetrieveResult result = metadataConnection
				.checkRetrieveStatus(asyncResult.getId());
		// Print out any warning messages
		StringBuilder stringBuilder = new StringBuilder();
		if (result.getMessages() != null) {
			for (RetrieveMessage rm : result.getMessages()) {
				stringBuilder.append(rm.getFileName() + " - " + rm.getProblem()
						+ "\n");
			}
		}
		if (stringBuilder.length() > 0) {
			System.out.println("Retrieve warnings:\n" + stringBuilder);
		}
		System.out.println("Writing results to zip file");
		File resultsFile = new File(ZIP_FILE);
		FileOutputStream os = new FileOutputStream(resultsFile);
		try {
			os.write(result.getZipFile());
		} finally {
			os.close();
		}
	}

	private static DeployResult waitForDeployCompletion(String asyncResultId)
			throws Exception {
		int poll = 0;
		long waitTimeMilliSecs = ONE_SECOND;
		DeployResult deployResult;
		boolean fetchDetails;
		do {
			Thread.sleep(waitTimeMilliSecs);
			// double the wait time for the next iteration
			waitTimeMilliSecs *= 2;
			if (poll++ > MAX_NUM_POLL_REQUESTS) {
				throw new Exception(
						"Request timed out. If this is a large set of metadata components, "
								+ "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
			}
			// Fetch in-progress details once for every 3 polls
			fetchDetails = (poll % 3 == 0);
			deployResult = metadataConnection.checkDeployStatus(asyncResultId,
					fetchDetails);
			System.out.println("Status is: " + deployResult.getStatus());
			if (!deployResult.isDone() && fetchDetails) {
				printErrors(deployResult,
						"Failures for deployment in progress:\n");
			}
		} while (!deployResult.isDone());
		if (!deployResult.isSuccess()
				&& deployResult.getErrorStatusCode() != null) {
			throw new Exception(deployResult.getErrorStatusCode() + " msg: "
					+ deployResult.getErrorMessage());
		}
		if (!fetchDetails) {
			// Get the final result with details if we didn't do it in the last
			// attempt.
			deployResult = metadataConnection.checkDeployStatus(asyncResultId,
					true);
		}
		return deployResult;
	}

	private static AsyncResult waitForRetrieveCompletion(AsyncResult asyncResult)
			throws Exception {
		int poll = 0;
		long waitTimeMilliSecs = ONE_SECOND;
		while (!asyncResult.isDone()) {
			Thread.sleep(waitTimeMilliSecs);
			// double the wait time for the next iteration
			waitTimeMilliSecs *= 2;
			if (poll++ > MAX_NUM_POLL_REQUESTS) {
				throw new Exception(
						"Request timed out. If this is a large set of metadata components, "
								+ "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
			}
			asyncResult = metadataConnection
					.checkStatus(new String[] { asyncResult.getId() })[0];
			System.out.println("Status is: " + asyncResult.getState());
		}
		if (asyncResult.getState() != AsyncRequestState.Completed) {
			throw new Exception(asyncResult.getStatusCode() + " msg: "
					+ asyncResult.getMessage());
		}
		return asyncResult;
	}

	private static void setUnpackaged(RetrieveRequest request) throws Exception {
		// Edit the path, if necessary, if your package.xml file is located
		// elsewhere
		File unpackedManifest = new File(MANIFEST_FILE);
		System.out.println("Manifest file: "
				+ unpackedManifest.getAbsolutePath());
		if (!unpackedManifest.exists() || !unpackedManifest.isFile()) {
			throw new Exception("Should provide a valid retrieve manifest "
					+ "for unpackaged content. Looking for "
					+ unpackedManifest.getAbsolutePath());
		}
		// Note that we use the fully quualified class name because
		// of a collision with the java.lang.Package class
		com.sforce.soap.metadata.Package p = parsePackageManifest(unpackedManifest);
		request.setUnpackaged(p);
	}

	private static com.sforce.soap.metadata.Package parsePackageManifest(
			File file) throws ParserConfigurationException, IOException,
			SAXException {
		com.sforce.soap.metadata.Package packageManifest = null;
		List<PackageTypeMembers> listPackageTypes = new ArrayList<PackageTypeMembers>();
		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		InputStream inputStream = new FileInputStream(file);
		Element d = db.parse(inputStream).getDocumentElement();
		for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
			if (c instanceof Element) {
				Element ce = (Element) c;
				NodeList nodeList = ce.getElementsByTagName("name");
				if (nodeList.getLength() == 0) {
					continue;
				}
				String name = nodeList.item(0).getTextContent();
				NodeList m = ce.getElementsByTagName("members");
				List<String> members = new ArrayList<String>();
				for (int i = 0; i < m.getLength(); i++) {
					Node mm = m.item(i);
					members.add(mm.getTextContent());
				}
				PackageTypeMembers packageTypes = new PackageTypeMembers();
				packageTypes.setName(name);
				packageTypes.setMembers(members.toArray(new String[members
						.size()]));
				listPackageTypes.add(packageTypes);
			}
		}
		packageManifest = new com.sforce.soap.metadata.Package();
		PackageTypeMembers[] packageTypesArray = new PackageTypeMembers[listPackageTypes
				.size()];
		packageManifest.setTypes(listPackageTypes.toArray(packageTypesArray));
		packageManifest.setVersion(API_VERSION + "");
		return packageManifest;
	}

	private static String getFileContents(String fileName) {
		String code = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append("\n");
			}
			code = stringBuilder.toString();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
}
