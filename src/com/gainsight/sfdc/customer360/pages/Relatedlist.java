package com.gainsight.sfdc.customer360.pages;

import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Relatedlist extends Customer360Page {
    private final String READT_INDICATOR = "//div[@class='gs_section_title']/h1[text()='']";

    public Relatedlist(String relatedListName) {
        wait.waitTillElementDisplayed("//div[@class='gs_section_title']/h1[text()='"+relatedListName+"']", MIN_TIME, MAX_TIME);
    }

    private WebElement getRelatedList(String relatedListName) {
        WebElement rSection = element.getElement("//h1[text()='"+relatedListName+"']/parent::div[@class='gs_section_title']/parent::div[@class='gs_section']");
        return rSection;
    }

    public String[] getColHeaders(String relatedListName) {
        WebElement rSection = getRelatedList(relatedListName);
        List<WebElement> tableList  = rSection.findElements(By.tagName("table"));
        WebElement headerTable;
        String[] tableHeaders = null;
        if(tableList != null && tableList.size() > 0) {
            headerTable = tableList.get(0);
            List<WebElement> tableRowsList = headerTable.findElements(By.tagName("tr"));
            if(tableRowsList != null && tableRowsList.size() > 0) {
                WebElement tableTr = tableRowsList.get(0);
                List<WebElement> tableColumnsList = tableTr.findElements(By.tagName("th"));
                if(tableColumnsList != null) {
                    Report.logInfo("Total Columns in table are :" +tableColumnsList.size());
                    tableHeaders = new String[tableColumnsList.size()+1];
                    int i =0;
                    for(WebElement wEle : tableColumnsList) {
                        String hText = wEle.getText().trim();
                        tableHeaders[i] = hText;
                        Report.logInfo(hText);
                        ++i;
                    }
                }
            }
        }
        return tableHeaders;
    }

    public ArrayList<HashMap<String, String>> getTableData(String relatedListName) {
        String[] headerData = getColHeaders(relatedListName);
        WebElement rSection = getRelatedList(relatedListName);
        ArrayList<HashMap<String, String>> tableData = null;
        List<WebElement> tableList  = rSection.findElements(By.tagName("table"));
        WebElement dataTable;
        if(tableList != null && tableList.size() > 1) {
            dataTable = tableList.get(1);
            List<WebElement> rowsList = dataTable.findElements(By.tagName("tr"));
            for(WebElement row : rowsList) {
                List<WebElement> columnsList = row.findElements(By.tagName("td"));
                int i=0;
                HashMap<String, String> temp = new HashMap<String, String>();
                for(WebElement cell : columnsList) {
                    String cellValue = cell.getText();
                    temp.put(headerData[i], cellValue);
                    Report.logInfo(headerData[i]+"," +cellValue);
                    i++;
                }
            }
        }
        return tableData;
    }

}
