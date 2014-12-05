package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

public class RelatedList360 extends Customer360Page {
    private final String READT_INDICATOR = "//div[@class='gs_section_title']/h1[text()='']";

    public RelatedList360(String relatedListName) {
        wait.waitTillElementDisplayed("//div[@class='gs_section_title']/h1[text()='"+relatedListName+"']", MIN_TIME, MAX_TIME);
    }

    private WebElement getRelatedList(String relatedListName) {
        WebElement rSection = element.getElement("//h1[text()='"+relatedListName+"']/parent::div[@class='gs_section_title']/parent::div[@class='gs_section']");
        return rSection;
    }

    public boolean isTableHeadersExisting(HashMap<String, String> testData, String relatedListName) {
        boolean result = false;
        List<String> cols = getColHeaders(relatedListName);
        for(int i=1; i<= testData.size(); i++) {
            if(cols.contains(testData.get("col" + i).replaceAll(" ", "").trim())) { //cols.contains(testData.get("col"+i))) {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    public boolean isDataDisplayed(String relatedListName, String testData) {
        boolean result = false;
        String s = "";
        String tableId = getTableId(relatedListName);
        if(table.getValueInListRow(tableId, testData) != -1)  {
            result = true;
        }
        return result;
    }

    public List<String> getColHeaders(String relatedListName) {
        WebElement rSection = getRelatedList(relatedListName);
        List<WebElement> tableList  = rSection.findElements(By.tagName("table"));
        WebElement headerTable;
        List<String> tableHeaders = new ArrayList<String>();
        if(tableList != null && tableList.size() > 0) {
            headerTable = tableList.get(0);
            List<WebElement> tableRowsList = headerTable.findElements(By.tagName("tr"));
            if(tableRowsList != null && tableRowsList.size() > 0) {
                WebElement tableTr = tableRowsList.get(0);
                List<WebElement> tableColumnsList = tableTr.findElements(By.tagName("th"));
                if(tableColumnsList != null) {
                    Report.logInfo("Total Columns in table are :" +tableColumnsList.size());
                    for(WebElement wEle : tableColumnsList) {
                        String hText = wEle.getText().replaceAll(" ", "").trim();
                        tableHeaders.add(hText);
                        Report.logInfo(hText);
                    }
                }
            }
        }
        return tableHeaders;
    }

    public List<HashMap<String, String>> getTableData(String relatedListName) {
        List<String> headerData = getColHeaders(relatedListName);
        WebElement rSection = getRelatedList(relatedListName);
        List<HashMap<String, String>> tableDataList = new ArrayList<HashMap<String, String>>();
        List<WebElement> tableList  = rSection.findElements(By.tagName("table"));
        WebElement dataTable;
        if(tableList != null && tableList.size() > 1) {
            dataTable = tableList.get(1);
            String Id = dataTable.getAttribute("Id");
            List<WebElement> rowsList = dataTable.findElements(By.tagName("tr"));
            for(WebElement row : rowsList) {
                List<WebElement> columnsList = row.findElements(By.tagName("td"));
                int i=0;
                HashMap<String, String> temp = new HashMap<String, String>();
                for(WebElement cell : columnsList) {
                    String cellValue = cell.getText().trim();
                    temp.put("col"+i+1, cellValue);
                    Report.logInfo(headerData.get(i)+" : " +cellValue);
                    i++;
                }
                tableDataList.add(temp);
            }
        }
        return tableDataList;
    }

    public RelatedList360 refreshSection(String secName) {
        String xPath = "//div[@class='gs_section_title']/h1[text()='"+secName.trim()+"']/following-sibling::div[@class='gs_edit_icon']/a[@class='gs_related_refresh' and text()='Refresh']";
        item.click(xPath);
        return new RelatedList360(secName);
    }

    public SalesforceRecordForm clickOnAdd(String secName) {
        String xPath = "//div[@class='gs_section_title']/h1[text()='"+secName.trim()+"']/following-sibling::div[contains(@class,'gs_edit_icon')]/a[contains(text(),'Add')]";
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Report.logInfo("clickOnAdd Count : " +windows.size());
        driver.switchTo().window(aa.get(windows.size()-1));
        return new SalesforceRecordForm();
    }

    public void closeWindow() {
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Report.logInfo("clickOnAdd Count : " +windows.size());
        if(aa != null && aa.size() == 2) {
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
        String a = "//h1[contains(text(), '"+secName+"')]/ancestor::div[@class='gs_section']/descendant::div[@class='noDataFound' and contains(text(), 'No Data Found.')]";
        Report.logInfo("Xpath of No Info Msg :" +a);
        return element.getElement(a).isDisplayed();
    }

    public RelatedList360 selectUIView(String secName, String viewName) {
        amtDateUtil.stalePause();
        String xPath =  "//div[@class='gs_section_title']/h1[text()='"+secName+"' ]/following-sibling::div/select[contains(@class,'case_uiviews')]"+
                            "/following-sibling::button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all']";
        item.click(xPath);
        wait.waitTillElementDisplayed("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+viewName.trim()+"')]", MIN_TIME, MAX_TIME);
        item.click("//ul[@class='ui-multiselect-checkboxes ui-helper-reset']/descendant::span[contains(text(), '"+viewName.trim()+"')]");
        amtDateUtil.sleep(5); //Has we don't actually have control on wait for element, introducing this sleep, will change is required.
        return new RelatedList360(secName);
    }

    public String getTableId(String relatedListName) {
        String Id = null;
        WebElement rSection = getRelatedList(relatedListName);
        List<WebElement> tableList  = rSection.findElements(By.tagName("table"));
        WebElement dataTable;
        if(tableList != null && tableList.size() > 1) {
            dataTable = tableList.get(1);
            Id = dataTable.getAttribute("Id");
        }
        return Id;
    }

    public SalesforceRecordForm viewRecord(String secName, String values) {
        String[] split = values.split("\\|") ;
        String xPath = "//h1[contains(text(),'"+secName+"')]/ancestor::div[@class='gs_section']/descendant::";
        for(String s : split) {
            xPath += "td[contains(text(), '"+s+"')]"+ "/following-sibling::";
        }
        xPath += "td/a[@class='preview-icon']";
        Report.logInfo("Xpath : " +xPath);
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Report.logInfo("viewRecord Count : " +windows.size());
        driver.switchTo().window(aa.get(windows.size()-1));
        return new SalesforceRecordForm();
    }

    public SalesforceRecordForm editRecord(String secName, String values) {
        String[] split = values.split("\\|") ;
        String xPath = "//h1[contains(text(),'"+secName+"')]/ancestor::div[@class='gs_section']/descendant::";
        for(String s : split) {
            xPath += "td[contains(text(), '"+s+"')]"+ "/following-sibling::";
        }
        xPath += "td/a[@class='edit-icon']";
        Report.logInfo("Xpath : " +xPath);
        item.click(xPath);
        Set<String> windows = driver.getWindowHandles();
        List<String> aa = new ArrayList<String>();
        aa.addAll(windows);
        Report.logInfo("editRecord Count : " +windows.size());
        driver.switchTo().window(aa.get(windows.size()-1));
        return new SalesforceRecordForm();
    }



}
