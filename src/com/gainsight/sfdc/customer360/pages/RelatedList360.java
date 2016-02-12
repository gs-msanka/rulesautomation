package com.gainsight.sfdc.customer360.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.testdriver.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class RelatedList360 extends Customer360Page {
    private final String READT_INDICATOR = "//div[@class='gs_section_title']/h1[text()='']";

    public RelatedList360(String relatedListName) {
        wait.waitTillElementDisplayed("//div[@class='gs_section_title']/h1[text()='" + relatedListName + "']", MIN_TIME,
                MAX_TIME);
    }

    private WebElement getRelatedList(String relatedListName) {
        WebElement rSection = element.getElement("//h1[text()='" + relatedListName
                + "']/parent::div[@class='gs_section_title']/parent::div[@class='gs_section']");
        return rSection;
    }

    public boolean isTableHeadersExisting(HashMap<String, String> testData, String relatedListName) {
        boolean result = false;
        List<String> cols = getColHeaders(relatedListName);
        System.out.println(cols);
        for (int i = 1; i <= testData.size(); i++) {
            if (cols.contains(testData.get("col" + i).replaceAll(" ", "").trim())) {
                // {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    public boolean isTableDataExisting(List<HashMap<String, String>> testDataList, String relatedListName) {
        boolean result = false;
        List<HashMap<String, String>> actualDataList = getTableData(relatedListName);
        System.out.println("rows equal =============" + actualDataList);
        for (int i = 0; i < testDataList.size(); i++) {
            HashMap<String, String> testData = testDataList.get(i);
            HashMap<String, String> actualData = actualDataList.get(i);
            for (int j = 0; j < testData.size(); j++) {
                String key = "col" + (j + 1);

                String testValue = testData.get(key).replaceAll(" ", "").trim();
                String actualValue = actualData.get(key).replaceAll(" ", "").trim();
                if (testValue != null && actualValue != null && testValue.equals(actualValue)) {
                    result = true;
                } else {
                    return false;
                }
            }

        }
        return result;
    }

    public boolean isDataDisplayed(String relatedListName, String testData) {
        boolean result = false;
        String s = "";
        String tableId = getTableId(relatedListName);
        System.out.println("get ids for table" + tableId);
        if (table.getValueInListRow(tableId, testData) != -1) {
            result = true;
        }
        return result;
    }

    public List<String> getColHeaders(String relatedListName) {
        WebElement rSection = getRelatedList(relatedListName);
        List<WebElement> tableList = rSection.findElements(By.tagName("table"));
        WebElement headerTable;
        List<String> tableHeaders = new ArrayList<String>();
        if (tableList != null && tableList.size() > 0) {
            headerTable = tableList.get(0);
            System.out.println("headertable ois ======" + headerTable);
            List<WebElement> tableRowsList = headerTable.findElements(By.tagName("tr"));
            if (tableRowsList != null && tableRowsList.size() > 0) {
                WebElement tableTr = tableRowsList.get(0);
                List<WebElement> tableColumnsList = tableTr.findElements(By.tagName("th"));
                if (tableColumnsList != null) {
                    Log.info("Total Columns in table are :" + tableColumnsList.size());
                    for (WebElement wEle : tableColumnsList) {
                        String hText = wEle.getText().replaceAll(" ", "").trim();
                        System.out.println("text in tha table==========" + hText);
                        tableHeaders.add(hText);
                        Log.info(hText);
                    }
                }
            }
        }
        return tableHeaders;
    }

    public List<HashMap<String, String>> getTableData(String relatedListName) {
        WebElement rSection = getRelatedList(relatedListName);
        int rowCount = rSection
                .findElements(By
                        .xpath("//div[@class='ui-jqgrid-bdiv']/descendant::div/descendant::table/descendant::tbody/tr"))
                .size();
        System.out.println("Number Of Rows = " + rowCount);
        // Get number of columns In table.
        int colCount = rSection
                .findElements((By
                        .xpath("//div[@class='ui-jqgrid-bdiv']/descendant::div/descendant::table/descendant::tbody/tr[1]/td")))
                .size();
        System.out.println("Number Of Columns = " + colCount);
        // divided xpath In three parts to pass Row_count and Col_count values.
        String firstPart = "//div[@class='ui-jqgrid-bdiv']/descendant::div/descendant::table/descendant::tbody/tr[";
        String secondPart = "]/td[";
        String thirdPart = "]";
        List<HashMap<String, String>> tableDataList = new ArrayList<HashMap<String, String>>();
        // Used for loop for number of rows.
        for (int i = 2; i <= rowCount; i++) {
            // Used for loop for number of columns.
            HashMap<String, String> temp = new HashMap<String, String>();
            for (int j = 1; j < colCount; j++) {
                String finalXpath = firstPart + i + secondPart + j + thirdPart;
                String tableData = rSection.findElement(By.xpath(finalXpath)).getText();

                temp.put("col" + (j), tableData);
                System.out.print(tableData + " ");
            }
            System.out.println("");
            System.out.println("");
            tableDataList.add(temp);
        }
        return tableDataList;
    }

    private String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    public RelatedList360 refreshSection(String secName) {
        String xPath = "//div[@class='gs_section_title']/h1[text()='" + secName.trim()
                + "']/following-sibling::div[@class='gs_edit_icon']/a[@class='gs_related_refresh' and text()='Refresh']";
        item.click(xPath);
        return new RelatedList360(secName);
    }

    public SalesforceRecordForm clickOnAdd(String secName) {
        String xPath = "//div[@class='gs_section_title']/h1[text()='" + secName.trim()
                + "']/following-sibling::div[contains(@class,'gs_edit_icon')]/a[contains(text(),'Add')]";
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Log.info("clickOnAdd Count : " + windows.size());
        driver.switchTo().window(aa.get(windows.size() - 1));
        driver.getCurrentUrl();
        System.out.println("url above" + driver.getCurrentUrl());

        return new SalesforceRecordForm();
    }

    public void closeWindow() {
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Log.info("clickOnAdd Count : " + windows.size());
        if (aa != null && aa.size() == 2) {
            driver.switchTo().window(aa.get(1));
            driver.close();
            driver.switchTo().window(aa.get(0));
        }
    }

    public SalesforceRecordForm clickOnViewRecord() {
        return new SalesforceRecordForm();
    }

    public SalesforceRecordForm clickOnEditRecords() {
        return new SalesforceRecordForm();
    }

    public boolean isNoDataMsgDisplayed(String secName) {
        String a = "//h1[contains(text(), '" + secName
                + "')]/ancestor::div[@class='gs_section']/descendant::div[@class='noDataFound' and contains(text(), 'No data found.')]";
        Log.info("Xpath of No Info Msg :" + a);
        return element.getElement(a).isDisplayed();
    }

    public RelatedList360 selectUIView(String secName, String viewName) {
        Timer.sleep(2);
        String xPath = "//div[@class='gs_section_title']/h1[text()='" + secName
                + "' ]/following-sibling::div/select[contains(@class,'case_uiviews')]"
                + "/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
        item.click(xPath);
        wait.waitTillElementDisplayed(
                "//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"
                        + viewName.trim() + "')]",
                MIN_TIME, MAX_TIME);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"
                + viewName.trim() + "')]");
        Timer.sleep(5); // Has we don't actually have control on wait for
        // element, introducing this sleep, will change is
        // required.
        return new RelatedList360(secName);
    }

    public String getTableId(String relatedListName) {
        String Id = null;
        WebElement rSection = getRelatedList(relatedListName);
        List<WebElement> tableList = rSection.findElements(By.tagName("table"));
        WebElement dataTable;
        if (tableList != null && tableList.size() > 1) {
            dataTable = tableList.get(1);
            Id = dataTable.getAttribute("id");
        }
        return Id;
    }

    public SalesforceRecordForm viewRecord(String secName, String values) {
        String[] split = values.split("\\|");
        String xPath = "//h1[contains(text(),'" + secName + "')]/ancestor::div[@class='gs_section']/descendant::";
        for (String s : split) {
            xPath += "td[contains(text(), '" + s + "')]" + "/following-sibling::";
        }
        xPath += "td/a[@class='preview-icon']";
        Log.info("Xpath : " + xPath);
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Log.info("viewRecord Count : " + windows.size());
        driver.switchTo().window(aa.get(windows.size() - 1));
        return new SalesforceRecordForm();
    }

    public SalesforceRecordForm editRecord(String secName, String values) {
        String[] split = values.split("\\|");
        String xPath = "//h1[contains(text(),'" + secName + "')]/ancestor::div[@class='gs_section']/descendant::";
        for (String s : split) {
            xPath += "td[contains(text(), '" + s + "')]" + "/following-sibling::";
        }
        xPath += "td/a[@class='edit-icon']";
        Log.info("Xpath : " + xPath);
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Log.info("editRecord Count : " + windows.size());
        driver.switchTo().window(aa.get(windows.size() - 1));
        return new SalesforceRecordForm();
    }

}
