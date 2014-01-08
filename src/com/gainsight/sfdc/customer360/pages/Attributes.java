package com.gainsight.sfdc.customer360.pages;


import com.gainsight.pageobject.core.Report;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
public class Attributes extends Customer360Page {

    private static final String READY_INDICATOR = "";
    private static final String NO_UIVIEW_CONF_INFO = "//div[@class='gs_accountAttribute']/div[@class='noDataFound']";
    private static final String ATTRIBUTES_TABLE = "//table[@class='gs_accountAttribute_grid']";


    public Attributes(String secName) {
        Report.logInfo("Account Attributes Page Loading");
        wait.waitTillElementDisplayed("//div[@class='gs_section_title']/h1[contains(text(), '"+secName+"')]", MIN_TIME,MAX_TIME);
    }

    public boolean isNoUIViewConfMsgDisplayed() {
        String actualText = element.getText(NO_UIVIEW_CONF_INFO);
        if(actualText.trim().equalsIgnoreCase("UI View not configured")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFieldsDisplayedInOrder(String[] expValues) {
        boolean result = false;
        if(expValues != null && expValues.length >0) {
            String[] actValues = getAllFieldsDisplayed();
            if(actValues!= null && actValues.length >0 && actValues.length >=expValues.length) {
                for(int i=0; i< expValues.length;i++) {
                    Report.logInfo("Acc Value : " +actValues[i]);
                    Report.logInfo("Exp Value : " +expValues[i]);
                    if(actValues[i].contains(expValues[i])) {
                        result = true;
                    } else {
                        result = false;
                        Report.logInfo("Field label failed at : " +i+ "on field " +actValues[i]);
                    }
                }
            }
        }
        return result;
    }

    private String[] getAllFieldsDisplayed() {
        String[] accountFields ;
        WebElement tableWebEle = element.getElement(ATTRIBUTES_TABLE);
        List<WebElement> tableRows = tableWebEle.findElements(By.tagName("tr"));
        accountFields = new String[tableRows.size()*2+1];
        List<WebElement> tableCols = new ArrayList<WebElement>();
        String attLabel;
        int i=0;
        for(WebElement row : tableRows) {
            tableCols = row.findElements(By.cssSelector("td.attribute-head"));
            for(WebElement col : tableCols) {
                attLabel = col.getText().trim();
                Report.logInfo("The Attribute Name :" +attLabel);
                accountFields[i] = attLabel;
                i++;
            }
        }
        return accountFields;
    }

    private HashMap<String, String> getAllFieldsAndValues() {
        WebElement tableWebEle = element.getElement(ATTRIBUTES_TABLE);
        List<WebElement> tableRows = tableWebEle.findElements(By.tagName("tr"));
        List<WebElement> tableCols = new ArrayList<WebElement>();
        String attLabel="", attValue="";
        HashMap<String, String> tempMap = new HashMap<String, String>();
        for(WebElement row : tableRows) {
            tableCols = row.findElements(By.cssSelector("td"));
            int i=0;
            for(WebElement col : tableCols) {
                if(col.getAttribute("class") != null && col.getAttribute("class").equals("attribute-head")) {
                    attLabel = null;
                    attLabel = col.getText().replace(":", "").trim();
                }
                else if(col.getAttribute("class") != null && col.getAttribute("class").equals("attribute-data")) {
                    attValue=null;
                    attValue = col.getText().replace(":", "").trim();
                }
                ++i;
                if(i==2 && attLabel != null && attValue != null) {
                    Report.logInfo("Attribute - " +attLabel+ " - " +attValue);
                    tempMap.put(attLabel, attValue);
                    i=0;
                }
            }
        }
        return tempMap;
    }

    public boolean isValuesForAccountAttDisplayed(HashMap<String, String> testData) {
        boolean result = false;
        Iterator itr = testData.keySet().iterator();
        HashMap<String, String> actualValues = getAllFieldsAndValues();
        while(itr.hasNext()) {
            String key = String.valueOf(itr.next());
            Report.logInfo("Key-" +key);
            Report.logInfo("Exp Data-" +testData.get(key));
            Report.logInfo("Actual Data-" +actualValues.get(key));
            if(testData.get(key) != null && testData.get(key).contains(actualValues.get(key))) {
                result = true;
            } else {
                result = false;
                Report.logInfo("Failed While Comparing :" +key);
                break;
            }
        }
        return result;
    }



}
