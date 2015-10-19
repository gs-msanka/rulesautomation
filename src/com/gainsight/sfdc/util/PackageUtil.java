package com.gainsight.sfdc.util;

import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.sforce.soap.metadata.*;
import com.sforce.soap.metadata.Error;
import com.sforce.ws.ConnectionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
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

public class PackageUtil {
    private static MetadataConnection metadataConnection;
    private static Double version = 32.0;
    // manifest file that controls which components get retrieved
    // one second in milliseconds
    private static final long ONE_SECOND = 1000;
    // maximum number of attempts to deploy the zip file
    private static final int MAX_NUM_POLL_REQUESTS = 50;

    public PackageUtil(MetadataConnection metadataConnection, Double version) {
        this.metadataConnection = metadataConnection;
        this.version = version;

    }

    /**
     * Check if the beta package is installed in the org, used when user tries to upgrade the beta package.
     * @return true if beta is installed in the org.
     * @throws ConnectionException
     */
    public boolean isBetaPackageInstalled() throws ConnectionException {
        boolean result = false;
        InstalledPackage installedPackage = getInstalledPackage();
        if(installedPackage !=null && installedPackage.getVersionNumber().contains("Beta")) {
            Log.info("Package Full Name : " + installedPackage.getFullName());
            Log.info("Package Version Number : " + installedPackage.getVersionNumber());
            result = true;
        } else {
            Log.info("No Package installed.");
        }
        return result;
    }

    private InstalledPackage getInstalledPackage() throws ConnectionException {
        ReadResult readResult = metadataConnection.readMetadata("InstalledPackage", new String[]{"JBCXM"});
        Metadata[] metadatas = readResult.getRecords();
        InstalledPackage installedPackage = null;
        if (metadatas != null && metadatas[0] !=null) {
            installedPackage = (InstalledPackage)metadatas[0];
        }
        return installedPackage;
    }

    public String getInstalledPackageVersion() throws ConnectionException {
        InstalledPackage installedPackage = getInstalledPackage();
        if(installedPackage !=null) {
            Log.info("Installed Version : " +installedPackage.getVersionNumber());
            return installedPackage.getVersionNumber();
        } else {
            Log.info("No Installed Package found");
            return null;
        }
    }

    public boolean ispackageInstalled() throws ConnectionException {
        boolean result = false;
        InstalledPackage installedPackage = getInstalledPackage();
        if (installedPackage != null && installedPackage.getFullName().equals("JBCXM")) {
            result = true;
        } else {
            Log.info("No Package installed.");
        }
        return result;
    }

    /**
     * Sets Sales application as default for system admin profile, need this while un-installing no profile should have gainsight app as default.
     * @throws ConnectionException
     */
    public void setSalesAppAsDefaultForSysAdmin() throws ConnectionException {

        Profile profile = new Profile();
        profile.setFullName("Admin");

        ProfileApplicationVisibility appVisibility = new ProfileApplicationVisibility();
        appVisibility.setDefault(true);
        appVisibility.setVisible(true);
        appVisibility.setApplication("standard__Sales");
        profile.setApplicationVisibilities(new ProfileApplicationVisibility[]{appVisibility});

        SaveResult[] saveResults = metadataConnection.updateMetadata(new Metadata[]{profile});

        boolean success = true;
        for (SaveResult saveResult : saveResults) {
            if (!saveResult.isSuccess()) {
                success = false;
                for (Error error : saveResult.getErrors()) {
                    Log.error(error.getMessage());
                    Log.error(error.getStatusCode().toString());
                }
            }
        }
        if (!success) {
            Log.error("Seems Some error while setting sales app a default for admin profile");
            throw new RuntimeException("Seems Some error while setting sales app a default for admin profile, please see the logs, errors will be printed above.");
        }
        Log.info("Sales App set to default for system admin.");
    }


    /**
     * Adds Gainsight as default app for a profile, Adds tabs to gainsight application
     *
     * @param managePackage
     * @param nameSpace
     * @throws ConnectionException
     */
    public void setupGainsightApplicationAndTabs(boolean managePackage, String nameSpace) throws ConnectionException {
        CustomApplication application = new CustomApplication();
        application.setFullName(FileUtil.resolveNameSpace("JBCXM__JBara", managePackage ? nameSpace : null));
        String[] tabs = new String[]{"JBCXM__Gainsight", "JBCXM__Customers", "JBCXM__CustomerSuccess360", "JBCXM__AllAdoption",
                "JBCXM__Survey", "JBCXM__Administration", "JBCXM__Cockpit",
                "JBCXM__GainsightMobile", "JBCXM__Insights", "JBCXM__NPS", "JBCXM__Churn"};
        for(int i=0; i< tabs.length; i++) {
            tabs[i]= FileUtil.resolveNameSpace(tabs[i], managePackage ? nameSpace : null);
        }
        application.setTab(tabs);
        application.setLabel("Gainsight");

        SaveResult[] saveResults = metadataConnection.updateMetadata(new Metadata[]{application});

        boolean success = true;
        for (SaveResult saveResult : saveResults) {
            if (!saveResult.isSuccess()) {
                success = false;
                for (Error error : saveResult.getErrors()) {
                    Log.error(error.getMessage());
                    Log.error(error.getStatusCode().toString());
                }
            }
        }
        if (!success) {
            Log.error("Seems Some error while setting up gainsight application.");
            throw new RuntimeException("Seems Some error while setting up gainsight application, please see the logs, errors will be printed above.");
        }

        Profile profile = new Profile();
        profile.setFullName("Admin");

        ProfileApplicationVisibility appVisibility = new ProfileApplicationVisibility();
        appVisibility.setDefault(true);
        appVisibility.setVisible(true);
        appVisibility.setApplication(FileUtil.resolveNameSpace("JBCXM__JBara", managePackage ? nameSpace : null));


        profile.setTabVisibilities(getTabVisibility(managePackage, nameSpace, new String[]{"JBCXM__Administration", "JBCXM__Churn", "JBCXM__Cockpit", "JBCXM__Customers",
                "JBCXM__CustomerSuccess360", "JBCXM__AllAdoption", "JBCXM__Gainsight", "JBCXM__GainsightMobile", "JBCXM__Insights", "JBCXM__NPS", "JBCXM__Survey"}));
        profile.setApplicationVisibilities(new ProfileApplicationVisibility[]{appVisibility});

        saveResults = metadataConnection.updateMetadata(new Metadata[]{profile});

        success = true;
        for (SaveResult saveResult : saveResults) {
            if (!saveResult.isSuccess()) {
                success = false;
                for (Error error : saveResult.getErrors()) {
                    Log.error(error.getMessage());
                    Log.error(error.getStatusCode().toString());
                }
            }
        }
        if (!success) {
            Log.error("Seems Some error while setting up gainsight application.");
            throw new RuntimeException("Seems Some error while setting up gainsight application, please see the logs, errors will be printed above.");
        }
        Log.info("Gainsight Application/Tabs setup done.");
    }

    /**
     * Returns the profile tab visibility
     *
     * @param managePackage
     * @param nameSpace
     * @param tabs
     * @return
     */
    private ProfileTabVisibility[] getTabVisibility(boolean managePackage, String nameSpace, String[] tabs) {
        if (tabs == null || tabs.length == 0) {
            Log.error("Please send tab info.");
            throw new RuntimeException("Please send tab info.");
        }
        ProfileTabVisibility[] tabVisibilities = new ProfileTabVisibility[tabs.length];
        for (int i = 0; i < tabs.length; i++) {
            ProfileTabVisibility tabVisibility = new ProfileTabVisibility();
            tabVisibility.setVisibility(TabVisibility.DefaultOn);
            tabVisibility.setTab(FileUtil.resolveNameSpace(tabs[i], managePackage ? nameSpace : null));
            tabVisibilities[i] = tabVisibility;
        }
        return tabVisibilities;
    }


    /**
     * Removes the pages from account, opportunity, case widget.
     * @param accLayout - true to remove account widget.
     * @param oppLayout - true to remove opportunity widget.
     * @param caseLayout - true to remove case widget.
     * @param managePackage - if its a managed package.
     * @param nameSpace - Name space ex: JBCXM
     */
    public void removeWidgets(boolean accLayout, boolean oppLayout, boolean caseLayout, boolean managePackage, String nameSpace) {
        Log.info("Started to removing widget layouts");
        try {
            String zipFile = "retrieveResults.zip";
            String MANIFEST_FILE = Application.basedir + "/resources/sfdcmetadata/widgets/package.xml";
            String tempDir = Application.basedir + "/resources/sfdcmetadata/temp";
            String tempDirSrc = tempDir + "/src";
            String accLayoutFile = tempDirSrc + "/unpackaged/layouts/Account-Account Layout.layout";
            String oppLayoutFile = tempDirSrc + "/unpackaged/layouts/Opportunity-Opportunity Layout.layout";
            String caseLayoutFile = tempDirSrc + "/unpackaged/layouts/Case-Case Layout.layout";

            File file = new File(tempDir);
            file.deleteOnExit();
            retrieveZip(tempDir, zipFile, MANIFEST_FILE);
            unzip(tempDir + "/" + zipFile, tempDirSrc);

            if (accLayout) removeSection(accLayoutFile,  FileUtil.resolveNameSpace("JBCXM__CustomerSuccess", managePackage ? nameSpace : null));
            if (oppLayout) removeSection(oppLayoutFile, FileUtil.resolveNameSpace("JBCXM__CustomerSuccessOpportunity", managePackage ? nameSpace : null));
            if (caseLayout) removeSection(caseLayoutFile, FileUtil.resolveNameSpace("JBCXM__Summary", managePackage ? nameSpace : null));

            createZipFile(tempDirSrc, tempDir, "playload");
            deployZip(tempDir + "/playload.zip");
        } catch (Exception e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
        Log.info("Deploy widget layouts Successful");
    }

    /**
     * Updates the Account, Opportunity & Case Widget layouts.
     *
     * @param accLayout - true to update account layout.
     * @param oppLayout - true to update opportunity layout.
     * @param caseLayout - true to update case layout.
     */
    public void updateWidgetLayouts(boolean accLayout, boolean oppLayout, boolean caseLayout, boolean managePackage, String nameSpace) {
        Log.info("Started to deploy widget layouts");
        try {
            String zipFile = "retrieveResults.zip";
            String MANIFEST_FILE = Application.basedir + "/resources/sfdcmetadata/widgets/package.xml";
            String tempDir = Application.basedir + "/resources/sfdcmetadata/temp";
            String tempDirSrc = tempDir + "/src";
            String accLayoutFile = tempDirSrc + "/unpackaged/layouts/Account-Account Layout.layout";
            String accWidgetFile = Application.basedir + "/resources/sfdcmetadata/widgets/accountWidget.xml";
            String oppLayoutFile = tempDirSrc + "/unpackaged/layouts/Opportunity-Opportunity Layout.layout";
            String oppWidgetFile = Application.basedir + "/resources/sfdcmetadata/widgets/opportWidget.xml";
            String caseLayoutFile = tempDirSrc + "/unpackaged/layouts/Case-Case Layout.layout";
            String caseWidgetFile = Application.basedir + "/resources/sfdcmetadata/widgets/caseWidget.xml";

            File file = new File(tempDir);
            file.deleteOnExit();
            retrieveZip(tempDir, zipFile, MANIFEST_FILE);
            unzip(tempDir + "/" + zipFile, tempDirSrc);

            if (accLayout && !isPagePresent(accLayoutFile, FileUtil.resolveNameSpace("JBCXM__CustomerSuccess", managePackage ? nameSpace : null)))  {
                updateLayout(accLayoutFile, accWidgetFile);
            }
            if (oppLayout && !isPagePresent(oppLayoutFile, FileUtil.resolveNameSpace("JBCXM__CustomerSuccessOpportunity", managePackage ? nameSpace : null))) {
                updateLayout(oppLayoutFile, oppWidgetFile);
            }
            if (caseLayout && !isPagePresent(caseLayoutFile, FileUtil.resolveNameSpace("JBCXM__Summary", managePackage ? nameSpace : null)))  {
                updateLayout(caseLayoutFile, caseWidgetFile);
            }

            createZipFile(tempDirSrc, tempDir, "playload");
            deployZip(tempDir + "/playload.zip");
        } catch (Exception e) {
            e.printStackTrace();
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
        Log.info("Deploy widget layouts Successful");
    }

    public void deployPermissionSetCode() {
        Log.info("Started Deploying Permission Sets Custom Code");
        try {
            String srcDir = Application.basedir + "/resources/sfdcmetadata/permissionSetCode/src";
            String desDir = Application.basedir + "/resources/sfdcmetadata/temp";
            String zipFileName = "PermPayload";
            createZipFile(srcDir, desDir, zipFileName);
            deployZip(desDir + "/" + zipFileName + ".zip");
        } catch (Exception e) {
            Log.error("Failed to deploy permission sets", e);
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    /**
     * Removed the widget page.
     * @param parentFile - the Widget file path
     * @param pageName - Name of the page that to be removed.
     */
    private void removeSection(String parentFile, String pageName) {
        Log.info("Update the Layout on file : " + parentFile);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(parentFile);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//layoutSections/layoutColumns/layoutItems/page[text()='" + pageName + "']/ancestor::layoutSections");

            Node layoutSectionNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if(layoutSectionNode != null) {
                layoutSectionNode.getParentNode().removeChild(layoutSectionNode);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new FileOutputStream(parentFile));
                transformer.transform(source, result);
            }
        } catch (Exception e) {
            Log.error("Failed to update layout file : " + parentFile, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the layout has already widget page added to it.
     * @param parentFile - Layout XML file path
     * @param pageName - page to check for ex JBCXM__CustomerSuccess.
     * @return true if page as widget, else false.
     */
    private boolean isPagePresent(String parentFile, String pageName) {
        boolean result = false;
        Log.info("Update the Layout on file : " + parentFile);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(parentFile);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            XPathExpression expression = xpath.compile("//layoutSections/layoutColumns/layoutItems/page[text()='" + pageName + "']/ancestor::layoutSections");

            Node layoutSectionNode = (Node) expression.evaluate(document, XPathConstants.NODE);
            if(layoutSectionNode!=null) {
                result = true;
            }
        } catch (Exception e) {
            Log.error("Failed to update layout file : " + parentFile, e);
            throw new RuntimeException(e);
        }
        return result;
    }

    private void updateLayout(String parentFile, String childFile) {
        Log.info("Update the Layout on file : " + parentFile);
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
            Log.error("Failed to update layout file : " + parentFile, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Installs the JBCXM package based on the version provided.
     * if beta is installed already, throws a error.
     * If Same version is already installed, skips the installation.
     * @param version
     * @param password
     * @throws Exception
     */
    public void installApplication(String version, String password) throws Exception {
        if(isBetaPackageInstalled()) {
            throw new RuntimeException("Beta package exists, " +
                    "please un-install the beta to install the package, " +
                    "as beta packages can't be upgraded.");
        }
        String installedVersion = getInstalledPackageVersion();
        if(installedVersion != null && installedVersion.trim().equals(version.trim())) {
            Log.info("Same version exists, skipping installation of : " +version);
            return;
        }
        String srcDir = Application.basedir + "/resources/sfdcmetadata/appInstall";
        String desDir = Application.basedir + "/resources/sfdcmetadata/temp";
        String filePath = Application.basedir + "/resources/sfdcmetadata/appInstall/installedManged/installedPackages/JBCXM.installedPackage";
        Log.info("Started Installation Application Version :" + version);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.parse(filePath);
        Node versionNumber = doc.getElementsByTagName("versionNumber").item(0);
        versionNumber.setTextContent(version);
        Node pass = doc.getElementsByTagName("password").item(0);
        if (password != null && password != "") {
            pass.setTextContent(password);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
        Log.info("package.xml is updated successfully");
        createZipFile(srcDir, desDir, "appInstall");
        deployZip(desDir + "/appInstall.zip");
        Log.info("Installation Successful");
    }

    /**
     * Uninstalls gainsight application.
     * @param managedPackage - true if its a managed package.
     * @param nameSpace - application name space.
     * @throws Exception
     */
    public void unInstallApplication(boolean managedPackage, String nameSpace) throws Exception {
        if(!ispackageInstalled()) {
            return;
        }
        removeWidgets(true, true, true, managedPackage, nameSpace);
        setSalesAppAsDefaultForSysAdmin();
        String srcDir = Application.basedir + "/resources/sfdcmetadata/appUnInstall";
        String desDir = Application.basedir + "/resources/sfdcmetadata/temp/";
        Log.info("Started un-installing application");
        createZipFile(srcDir, desDir, "appUnInstall");
        deployZip(desDir + "appUnInstall.zip");
        Log.info("Application un-install successful");
    }


    private void retrieveZip(String dirPath, String zipFile, String MANIFEST_FILE) throws RemoteException, Exception {
        Log.info("Pulling metadata from SFDC from manifest file : " + MANIFEST_FILE);
        RetrieveRequest retrieveRequest = new RetrieveRequest();
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


            Log.info("Retrieve Status: " + result.getStatus());
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
                Log.info("Retrieve warnings:\n" + buf);
            }

            // Write the zip to the file system
            Log.info("Writing results to zip file");
            ByteArrayInputStream bais = new ByteArrayInputStream(result.getZipFile());
            File desDir = new File(dirPath);
            desDir.mkdirs();
            File resultsFile = new File(dirPath + "/" + zipFile);
            FileOutputStream os = new FileOutputStream(resultsFile);
            try {
                ReadableByteChannel src = Channels.newChannel(bais);
                FileChannel dest = os.getChannel();
                copy(src, dest);

                Log.info("Results written to " + resultsFile.getAbsolutePath());
            } finally {
                os.close();
            }
        }
        Log.info("Pulling metadata from SFDC from manifest file Successful");
    }

    /**
     * Helper method to copy from a readable channel to a writable channel,
     * using an in-memory buffer.
     */
    private void copy(ReadableByteChannel src, WritableByteChannel dest)
            throws IOException {
        // Use an in-memory byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(8092);
        while (src.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
            buffer.clear();
        }
    }

    private void setUnpackaged(RetrieveRequest request, String MANIFEST_FILE) throws Exception {
        // Edit the path, if necessary, if your package.xml file is located elsewhere
        File unpackedManifest = new File(MANIFEST_FILE);
        Log.info("Manifest file: " + unpackedManifest.getAbsolutePath());

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
                    Element ce = (Element) c;
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
     *
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            File folder = new File(destDirectory);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFilePath));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDirectory + File.separator + fileName);
                Log.info("file unzip : " + newFile.getAbsoluteFile());
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
            Log.info("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void createZipFile(String srcDir, String destDir, String fileName) {
        File directoryToZip = new File(srcDir);
        if (!directoryToZip.exists()) {
            directoryToZip.mkdir();
        }
        List<File> fileList = new ArrayList<File>();
        getAllFiles(directoryToZip, fileList);
        Log.info("---Creating zip file");
        writeZipFile(directoryToZip, fileList, destDir, fileName);
        Log.info("---Done");
    }

    public static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    Log.info("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    Log.info("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, String destDir, String fileName) {
        Log.info("Writing all the files to zip");
        try {
            File tempDir = new File(destDir);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            Log.info(destDir + "/" + fileName + ".zip");
            FileOutputStream fos = new FileOutputStream(destDir + "/" + fileName + ".zip");
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
        Log.info("Writing all the files to zip successful");
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
            IOException {

        FileInputStream fis = new FileInputStream(file);
        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        Log.info("Writing '" + zipFilePath + "' to zip file");
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
        Log.info("The file " + filePath
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
            Log.info(stringBuilder.toString());
        }
    }

    private static DeployResult waitForDeployCompletion(String asyncResultId)
            throws Exception {
        int poll = 0;
        long waitTimeMilliSecs = 15 * ONE_SECOND;
        DeployResult deployResult;
        boolean fetchDetails;
        do {
            Thread.sleep(waitTimeMilliSecs);
            // double the wait time for the next iteration
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception(
                        "Request timed out. If this is a large set of metadata components, "
                                + "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
            }
            // Fetch in-progress details once for every 3 polls
            fetchDetails = (poll % 3 == 0);
            deployResult = metadataConnection.checkDeployStatus(asyncResultId,
                    fetchDetails);
            Log.info("Status is: " + deployResult.getStatus());
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
}
