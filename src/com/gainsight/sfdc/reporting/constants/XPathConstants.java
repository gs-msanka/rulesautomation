package com.gainsight.sfdc.reporting.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by govardhan on 8/27/15.
 */
public class XPathConstants {
    private static enum Type {FORCE, NEW_STACK};

    public static String getXPath(String key) {
        return BaseXPathConstants.XPATHCONSTANTS.get(key);
    }

    public static String getXPath(String key, String type) {
        if(type.replace("-", "_").equals(Type.FORCE)) {
            return SFDCXPathConstants.XPATHCONSTANTS.get(key);
        } else {
            return MDAXPathConstants.XPATHCONSTANTS.get(key);
        }
    }
}

class BaseXPathConstants {
    public static Map<String, String> XPATHCONSTANTS = new HashMap<String, String>();
    static {
        XPATHCONSTANTS.put("READY_INDICATOR", "//a[@class='tooltips h_hide']");
        XPATHCONSTANTS.put("CLICKONDATASOURCE", "//button[@class='ui-multiselect ui-widget ui-state-default ui-corner-all gs-rb-schema-explorer-multi-select']");
		XPATHCONSTANTS.put("SEARCHDATASOURCE",
				"//div[@class='ui-multiselect-filter']/input[@placeholder='Search' and @type='text']");
		XPATHCONSTANTS.put("ADDSHOWME",
				"//div[@class='gs-rb-show-content clearfix']/div[@class='gs-rb-filterAdd-icon']");
		XPATHCONSTANTS.put("SEARCHSHOWMEField", "//div[@data-areatype='SELECT']/div/input[@placeholder='Search...']");
		XPATHCONSTANTS.put("SEARCHBYFIELD", "//div[@data-areatype='BY']/div/input[@placeholder='Search...']");
		XPATHCONSTANTS.put("SEARCHSHOWMEFieldMDA",
				"//div[@class='customTreeContainer']/div/input[@placeholder='Search...']");
		XPATHCONSTANTS.put("WAITTOLOADFILEDSFOROBJECT",
				"//div[@class='customTreeContainer']/div/input[@placeholder='Search...']");

		XPATHCONSTANTS.put("ADDBY", "//div[@class='gs-rb-by-content clearfix']/div[@class='gs-rb-filterAdd-icon']");
		XPATHCONSTANTS.put("REPORTNAME", "//input[@id='reportBuilderName']");
		XPATHCONSTANTS.put("REPOSITORY_BTN_XPATH", "//button[@class='btn dropdown-toggle']");
		XPATHCONSTANTS.put("REPOSITORY_SEARCH_TXT_XPATH",
				"//input[@type='text' and @class='layout_search_input gs-rb-search form-control']");
		XPATHCONSTANTS.put("CLICK_SEARCHREPORTNAME_REPOSITORY",
				"//li[@class='layout_list ui-report-compo' and @title='%s']");
		XPATHCONSTANTS.put("SAVEBUTTON_XPATH", "//div[@title='Save']");
		XPATHCONSTANTS.put("SAVEBUTTON_POPUP_XPATH", "//span[contains(text(),'Saving report')]");
		XPATHCONSTANTS.put("SAVEAS_BTN_XPATH", "//div[@title='Save as']");
        XPATHCONSTANTS.put("SAVE_SUCCESS_POPUP_XPATH", "//div[contains(text(),'Success')]");
        XPATHCONSTANTS.put("SAVE_DESCRIPTION_XPATH", "//label[contains(text(),'Description')]/following-sibling::textarea");
        XPATHCONSTANTS.put("SAVE_POPUP_SAVE_XPATH", "//input[@value='Save']");

		XPATHCONSTANTS.put("NEW", "//div[@title='New']");
		XPATHCONSTANTS.put("RESET", "//div[@title='Reset']");
		XPATHCONSTANTS.put("DELETE_BTN_XPATH", "//div[@title='Delete']");
		XPATHCONSTANTS.put("DELETE_YES_BTN_XPATH",
				"//input[@type='button' and @value = 'Yes' and contains(@class,'btn-save')]");
		XPATHCONSTANTS.put("REFRESH", "//div[@title='Refresh']");
		XPATHCONSTANTS.put("SHOWSALESFORCEHEADER", "//a[@class='tooltips h_show']");
		XPATHCONSTANTS.put("HIDESALESFORCEHEADER", "//a[@class='tooltips h_hide']");
		XPATHCONSTANTS.put("CHARTTYPE", "//ul[@class='gs-rb-selectedType']/li/span[contains(@class,'chart-icon')]");

		XPATHCONSTANTS.put("APPLY_BTN_XPATH", "//input[@type='button' and @value='Apply']");
		XPATHCONSTANTS.put("SAVEAS_TXT_XPATH", "//input[@class='layout_popup_input']");
		XPATHCONSTANTS.put("SAVE_SAVEAS_BTN_XPATH", "//input[contains(@class,'btn-save')]");

		XPATHCONSTANTS.put("CLICKSEARCHEDOBJECTNAME",
				"//li/label/input[@title='%s']/following-sibling::span[contains(text(),'%s')]");
		XPATHCONSTANTS.put("VERIFYSELECTEDOBJECTNAME",
				"//span[@class='ui-multiselect-selected-label' and text()='%s']");

		XPATHCONSTANTS.put("CLICKSEARCHSHOWMEFIELd",
				"//div[@data-areatype='SELECT']//li[@title='%s' and contains(@data-parent,'%s')]");
		XPATHCONSTANTS.put("CLICKSEARCHSHOWMEFIELdMDA", "//div[@class='customTreeContainer']//a/span[text()='%s']");
		XPATHCONSTANTS.put("CLICKSEARCHBYFIELD",
				"//div[@data-areatype='BY']//li[@title='%s' and contains(@data-parent,'%s')]");
		XPATHCONSTANTS.put("SHOWMESETTINGS", "//span[contains(text(),'%s')]/following-sibling::span/a");
		XPATHCONSTANTS.put("SHOWMESETTINGS_EXTRAFIELDS",
				"//label[contains(text(),'Show me')]/following-sibling::div[@class='gs-rb-show-count']");
		XPATHCONSTANTS.put("FIELDDISPLAYNAME", "//label[@for='displayName']/following-sibling::input");
		XPATHCONSTANTS.put("SUMMARIZEDBY", "//label[contains(text(),'Summarized by')]/following-sibling::select");
		XPATHCONSTANTS.put("SUMMARIZEDBY_OPTION", "//option[@value='%s']");
		XPATHCONSTANTS.put("SUMMARIZEDBY_CLOSE",
				"//label[contains(text(),'Summarized by')]/parent::div/preceding-sibling::div[@class='gs-rb-report-close pull-right']");
		XPATHCONSTANTS.put("AGGREGATION", "//label[contains(text(),'Aggregation')]/following-sibling::select");
		XPATHCONSTANTS.put("AGGREGATION_VALUE",
				"//label[contains(text(),'Aggregation')]/parent::div//option[@value='%s']");
		XPATHCONSTANTS.put("AGGREGATION_VALUE_CLOSE",
				"//label[contains(text(),'Aggregation')]/parent::div/preceding-sibling::div[@class='gs-rb-report-close pull-right']");
		XPATHCONSTANTS.put("DISPLAY", "//span[contains(text(),'Display')]/following-sibling::input");
		XPATHCONSTANTS.put("SHOWMEEXPAND",
				"//label[contains(text(),'Show me')]/following-sibling::div[@class='gs-rb-show-count']");
		XPATHCONSTANTS.put("CONFIRM_POPUP",
				"//div[contains(@class,'layout_popup')]");
	}
}

class SFDCXPathConstants extends BaseXPathConstants {

}

class MDAXPathConstants extends BaseXPathConstants {

}