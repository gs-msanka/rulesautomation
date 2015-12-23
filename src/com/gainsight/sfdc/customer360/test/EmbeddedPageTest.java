package com.gainsight.sfdc.customer360.test;

import java.util.HashMap;

import com.gainsight.pageobject.core.Element;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.gainsight.testdriver.Log;

import com.gainsight.sfdc.customer360.pages.Customer360Page;
import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.utils.DataProviderArguments;
import com.gainsight.utils.annotations.TestInfo;

/**
 * Created by gainsight on 08/01/14.
 */
public class EmbeddedPageTest extends BaseTest {

    private static final String not = null;
    private final String TEST_DATA_FILE = "testdata/sfdc/EmbeddedPage/tests/EmbeddedTests.xls";
    private final String ACCOUNT_CREATE_FILE = env.basedir + "/testdata/sfdc/EmbeddedPage/scripts/Account_Create.txt";
    private final String EMBEDDED_360_PAGESOURCE_SEC_ENABLE_FILE = env.basedir
            + "/testdata/sfdc/EmbeddedPage/scripts/EmbeddedCS360WithPageSource.txt";
    private final String EMBEDDED_360_URL_SEC_ENABLE_FILE = env.basedir
            + "/testdata/sfdc/EmbeddedPage/scripts/EmbeddedCS360WithURL.txt";
    private final String EMBEDDED_360_PARAM_ENABLE_FILE = env.basedir
            + "/testdata/sfdc/EmbeddedPage/scripts/EmbeddedCS360WithParameter.txt";
    private final String EMBEDDED_360_BUNBLED_PARAM_ENABLE = env.basedir
            + "/testdata/sfdc/EmbeddedPage/scripts/EmbeddedCS360BundledParameter.txt";
    public static String visualForcePageUrl = ".visual.force.com/apex/";
    private static String embededList = "//div[@class='gs_section_title']/h1[text()='%s']";
    Element element = new Element();


    @BeforeClass
    public void setUp() throws Exception {
        Log.info("Embedded testcases");
        basepage.login();
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ACCOUNT_CREATE_FILE));
        visualForcePageUrl = "https://"
                + "c"
                + "."
                + sfdcInfo.getEndpoint().substring(8,
                sfdcInfo.getEndpoint().indexOf("."))
                + visualForcePageUrl;
    }

    @TestInfo(testCaseIds = {"GS-5103"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F1")
    public void embeddedWithCustomPage(HashMap<String, String> testData) {

        sfdc.runApexCode(getNameSpaceResolvedFileContents(EMBEDDED_360_PAGESOURCE_SEC_ENABLE_FILE));
        String embeddedListName = testData.get("Section");
        String embiframeurl = visualForcePageUrl + testData.get("URL");
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        c360Page.clickOnEmbededPage(embeddedListName);
        element.switchToFrame("//iframe[contains(@src,'" + embiframeurl + "')]");

        String NO_CUSTOMER_FOUND_MSG = "//div[@class='gs_inavlidCustomerSpan' and contains(text(),'No customer selected. Please search and select a customer')]";
        Assert.assertTrue(element.isElementPresent(NO_CUSTOMER_FOUND_MSG));

    }

    @TestInfo(testCaseIds = {"GS-5103"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F2")
    public void embeddedWithBoxURL(HashMap<String, String> testData) {

        sfdc.runApexCode(getNameSpaceResolvedFileContents(EMBEDDED_360_URL_SEC_ENABLE_FILE));
        String embeddedListName = testData.get("Section");
        String embiframeurl = testData.get("URL");
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);

        c360Page.clickOnEmbededPage(embeddedListName);

        element.switchToFrame("//iframe[contains(@src,'" + embiframeurl + "')]");
        String NO_BOX_WELCOME_MSG = "//form/div/h2[contains(text(),'Sign In to Your Account')]";
        Assert.assertTrue(element.isElementPresent(NO_BOX_WELCOME_MSG));

    }

    @TestInfo(testCaseIds = {"GS-5103"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F3")
    public void embeddedWithParameter(HashMap<String, String> testData) {

        sfdc.runApexCode(getNameSpaceResolvedFileContents(EMBEDDED_360_PARAM_ENABLE_FILE));
        String embeddedListName = testData.get("Section");
        String CustomerName = testData.get("Customer");
        String embiframeurl = visualForcePageUrl + testData.get("URL");
        Log.info("the url is   " + embiframeurl);
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        c360Page.clickOnEmbededPage(embeddedListName);

        element.switchToFrame("//iframe[contains(@src,'" + embiframeurl + "')]");
        String PARAMETER_ACCNAME = "//h1[contains(text(),'" + CustomerName + "')]";
        Assert.assertTrue(element.isElementPresent(PARAMETER_ACCNAME));

    }

    @TestInfo(testCaseIds = {"GS-5103"})
    @Test(dataProviderClass = com.gainsight.utils.ExcelDataProvider.class, dataProvider = "excel")
    @DataProviderArguments(filePath = TEST_DATA_FILE, sheet = "F4")
    public void embeddedWithBundledParameter(HashMap<String, String> testData) {

        sfdc.runApexCode(getNameSpaceResolvedFileContents(EMBEDDED_360_BUNBLED_PARAM_ENABLE));
        String embeddedListName = testData.get("Section");
        String embiframeurl = visualForcePageUrl + testData.get("URL");
        Log.info("the url is   " + embiframeurl);
        Customer360Page c360Page = basepage.clickOnC360Tab().searchCustomer(testData.get("Customer"), false, false);
        c360Page.clickOnEmbededPage(embeddedListName);

        element.switchToFrame("//iframe[contains(@src,'" + embiframeurl + "')]");

        String BUNDLED_PARAMETER = "//h1[contains(text(),'123456')]";
        Assert.assertTrue(element.isElementPresent(BUNDLED_PARAMETER));

    }

}
