package com.gainsight.sfdc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.CodeCoverageWarning;
import com.sforce.soap.metadata.DeployMessage;
import com.sforce.soap.metadata.DeployOptions;
import com.sforce.soap.metadata.DeployResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.PackageTypeMembers;
import com.sforce.soap.metadata.RetrieveMessage;
import com.sforce.soap.metadata.RetrieveRequest;
import com.sforce.soap.metadata.RetrieveResult;
import com.sforce.soap.metadata.RunTestFailure;
import com.sforce.soap.metadata.RunTestsResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class PackageUtil {
	private static MetadataConnection metadataConnection;
    private static Double version = 32.0;
	// manifest file that controls which components get retrieved
	// one second in milliseconds
	private static final long ONE_SECOND = 1000;
	// maximum number of attempts to deploy the zip file
	private static final int MAX_NUM_POLL_REQUESTS = 50;

    public static void main(String args[]) throws Exception {
        metadataConnection = login();
        PackageUtil packageUtil = new PackageUtil();
        //packageUtil.retrieveZip();
        //packageUtil.unzip("./resources/sfdcmetadatapackage/temp/retrieveResults.zip", "./resources/sfdcmetadata/temp");
        //packageUtil.createZipFile("./resources/sfdcmetadatapackage/code", "./resources/sfdcmetadatapackage/temp");
        //deployZip("./resources/sfdcmetadatapackage/temp/code.zip");
        //packageUtil.installApplication("4.19.1", "JBCXM__Install4.19");
        //packageUtil.unInstallApplication();
        //packageUtil.installApplication("4.19.3", "JBCXM__Install4.19");
        //packageUtil.updateWidgetLayouts();

    }


	public PackageUtil(MetadataConnection metadataConnection, Double version) {
        this.metadataConnection = metadataConnection;
        this.version= version;

	}

    public PackageUtil() {}

    /**
     * This method updates the widget layouts.
     */
    public void updateWidgetLayouts(boolean accLayout, boolean oppLayout, boolean caseLayout)  {
        try {
            String zipFile          = "retrieveResults.zip";
            String MANIFEST_FILE    = "./resources/sfdcmetadata/widgets/package.xml";
            String tempDir          = "./resources/sfdcmetadata/temp";
            String tempDirSrc       = tempDir+"/src";
            String accLayoutFile    = tempDirSrc+"/unpackaged/layouts/Account-Account Layout.layout";
            String accWidgetFile    = "./resources/sfdcmetadata/widgets/accountWidget.xml";
            String oppLayoutFile    = tempDirSrc+"/unpackaged/layouts/Opportunity-Opportunity Layout.layout";
            String oppWidgetFile    = "./resources/sfdcmetadata/widgets/opportWidget.xml";
            String caseLayoutFile   = tempDirSrc+"/unpackaged/layouts/Case-Case Layout.layout";
            String caseWidgetFile   = "./resources/sfdcmetadata/widgets/caseWidget.xml";

            File file = new File(tempDir);
            file.deleteOnExit();
            retrieveZip(tempDir, zipFile, MANIFEST_FILE);
            unzip(tempDir+"/"+zipFile,tempDirSrc);

            if(accLayout)updateLayout(accLayoutFile,accWidgetFile);
            if(oppLayout)updateLayout(oppLayoutFile, oppWidgetFile);
            if(caseLayout)updateLayout(caseLayoutFile, caseWidgetFile);

            createZipFile(tempDirSrc, tempDir, "playload");
            deployZip(tempDir + "/playload.zip");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void updateLayout(String parentFile, String childFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(parentFile);
            Node node = doc.getElementsByTagName("layoutSections").item(0);

            DocumentBuilderFactory docFactory1 = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder1 = docFactory1.newDocumentBuilder();
            Document doc1 = docBuilder1.parse(childFile);
            Node node1 = doc1.getElementsByTagName("layoutSections").item(0);
            Node d = doc.importNode(node1, true);
            node.getParentNode().insertBefore(d, node);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(parentFile));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  void installApplication(String version, String password) throws Exception {
        String srcDir = "./resources/sfdcmetadata/appInstall";
        String desDir = "./resources/sfdcmetadata/temp";
        String filePath = "./resources/sfdcmetadata/appInstall/installedManged/installedPackages/JBCXM.installedPackage";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(filePath);
            Node versionNumber = doc.getElementsByTagName("versionNumber").item(0);
            versionNumber.setTextContent(version);
            Node pass = doc.getElementsByTagName("password").item(0);
            if(password != null && password != "") {
                pass.setTextContent(password);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            System.out.println("package.xml is updated successfully");
            createZipFile(srcDir, desDir,"appInstall");
            deployZip(desDir+"/appInstall.zip");
    }

    public void unInstallApplication() throws Exception {
        String srcDir = "./resources/sfdcmetadata/appUnInstall";
        String desDir = "./resources/sfdcmetadata/temp/";
        createZipFile(srcDir, desDir, "appUnInstall");
        deployZip(desDir+"appUnInstall.zip");
    }


    private void retrieveZip(String dirPath, String zipFile, String MANIFEST_FILE) throws RemoteException, Exception {
        RetrieveRequest retrieveRequest =  new RetrieveRequest();
        // The version in package.xml overrides the version in RetrieveRequest
        retrieveRequest.setApiVersion(version);
        setUnpackaged(retrieveRequest, MANIFEST_FILE);

        // Start the retrieve operation
        AsyncResult asyncResult = metadataConnection.retrieve(retrieveRequest);
        String asyncResultId = asyncResult.getId();

        // Wait for the retrieve to complete
        int poll = 0;
        long waitTimeMilliSecs = ONE_SECOND;
        RetrieveResult result = null;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // Double the wait time for the next iteration
            waitTimeMilliSecs *= 2;
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception("Request timed out.  If this is a large set " +
                        "of metadata components, check that the time allowed " +
                        "by MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            result = metadataConnection.checkRetrieveStatus(
                    asyncResultId);


            System.out.println("Retrieve Status: " + result.getStatus());
        } while (!result.isDone());

        if (result.getStatus() == RetrieveStatus.Failed) {
            throw new Exception(result.getErrorStatusCode() + " msg: " +
                    result.getErrorMessage());
        } else if (result.getStatus() == RetrieveStatus.Succeeded) {
            // Print out any warning messages
            StringBuilder buf = new StringBuilder();
            if (result.getMessages() != null) {
                for (RetrieveMessage rm : result.getMessages()) {
                    buf.append(rm.getFileName() + " - " + rm.getProblem());
                }
            }
            if (buf.length() > 0) {
                System.out.println("Retrieve warnings:\n" + buf);
            }

            // Write the zip to the file system
            System.out.println("Writing results to zip file");
            ByteArrayInputStream bais = new ByteArrayInputStream(result.getZipFile());
            File desDir = new File(dirPath);
            desDir.mkdirs();
            File resultsFile = new File(dirPath+"/"+zipFile);
            FileOutputStream os = new FileOutputStream(resultsFile);
            try {
                ReadableByteChannel src = Channels.newChannel(bais);
                FileChannel dest = os.getChannel();
                copy(src, dest);

                System.out.println("Results written to " + resultsFile.getAbsolutePath());
            } finally {
                os.close();
            }
        }
    }

    /**
     * Helper method to copy from a readable channel to a writable channel,
     * using an in-memory buffer.
     */
    private void copy(ReadableByteChannel src, WritableByteChannel dest)
            throws IOException
    {
        // Use an in-memory byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(8092);
        while (src.read(buffer) != -1) {
            buffer.flip();
            while(buffer.hasRemaining()) {
                dest.write(buffer);
            }
            buffer.clear();
        }
    }

    private void setUnpackaged(RetrieveRequest request, String MANIFEST_FILE) throws Exception
    {
        // Edit the path, if necessary, if your package.xml file is located elsewhere
        File unpackedManifest = new File(MANIFEST_FILE);
        System.out.println("Manifest file: " + unpackedManifest.getAbsolutePath());

        if (!unpackedManifest.exists() || !unpackedManifest.isFile())
            throw new Exception("Should provide a valid retrieve manifest " +
                    "for unpackaged content. " +
                    "Looking for " + unpackedManifest.getAbsolutePath());

        // Note that we populate the _package object by parsing a manifest file here.
        // You could populate the _package based on any source for your
        // particular application.
        com.sforce.soap.metadata.Package p = parsePackage(unpackedManifest);
        request.setUnpackaged(p);
    }

    private com.sforce.soap.metadata.Package parsePackage(File file) throws Exception {
        try {
            InputStream is = new FileInputStream(file);
            List<PackageTypeMembers> pd = new ArrayList<PackageTypeMembers>();
            DocumentBuilder db =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element d = db.parse(is).getDocumentElement();
            for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                if (c instanceof Element) {
                    Element ce = (Element)c;
                    //
                    NodeList namee = ce.getElementsByTagName("name");
                    if (namee.getLength() == 0) {
                        // not
                        continue;
                    }
                    String name = namee.item(0).getTextContent();
                    NodeList m = ce.getElementsByTagName("members");
                    List<String> members = new ArrayList<String>();
                    for (int i = 0; i < m.getLength(); i++) {
                        Node mm = m.item(i);
                        members.add(mm.getTextContent());
                    }
                    PackageTypeMembers pdi = new PackageTypeMembers();
                    pdi.setName(name);
                    pdi.setMembers(members.toArray(new String[members.size()]));
                    pd.add(pdi);
                }
            }
            com.sforce.soap.metadata.Package r = new com.sforce.soap.metadata.Package();
            r.setTypes(pd.toArray(new PackageTypeMembers[pd.size()]));
            r.setVersion(version + "");
            return r;
        } catch (ParserConfigurationException pce) {
            throw new Exception("Cannot create XML parser", pce);
        } catch (IOException ioe) {
            throw new Exception(ioe);
        } catch (SAXException se) {
            throw new Exception(se);
        }
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        try{
            //create output directory is not exists
            File folder = new File(destDirectory);
            if(!folder.exists()){
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFilePath));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(destDirectory + File.separator + fileName);
                System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void createZipFile(String srcDir, String destDir, String fileName) {
        File directoryToZip = new File(srcDir);
        if(!directoryToZip.exists()) {
            directoryToZip.mkdir();
        }
        List<File> fileList = new ArrayList<File>();
        getAllFiles(directoryToZip, fileList);
        System.out.println("---Creating zip file");
        writeZipFile(directoryToZip, fileList, destDir, fileName);
        System.out.println("---Done");
    }

    public static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, String destDir, String fileName) {
        try {
            System.out.println(destDir+"/"+ fileName+ ".zip");
            FileOutputStream fos = new FileOutputStream(destDir+"/"+fileName + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(file);
        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        System.out.println("Writing '" + zipFilePath + "' to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        fis.close();
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
		System.out.println("The file " + filePath
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

    public static MetadataConnection login() throws ConnectionException {
        final String USERNAME = "ggolla@11demo.com";//TestEnvironment.get().getUserName();
        // This is only a sample. Hard coding passwords in source files is a bad
        // practice.
        final String PASSWORD = "1234567gos2Vsyw95cuKpG1KjAXKZibXp";//TestEnvironment.get().getUserPassword()+ TestEnvironment.get().getProperty("sfdc.stoken");
        final String URL = "https://login.salesforce.com/services/Soap/c/32.0";
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

}
