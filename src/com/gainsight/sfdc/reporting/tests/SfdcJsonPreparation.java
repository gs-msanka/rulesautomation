package com.gainsight.sfdc.reporting.tests;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.reportBuilder.pojos.ReportInfoSFDC;

import com.gainsight.sfdc.tests.BaseTest;

import com.sforce.soap.partner.sobject.SObject;

import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;

public class SfdcJsonPreparation extends BaseTest {
	ObjectMapper mapper = new ObjectMapper();

	public void connectSFDCDBAndCompareJson(ReportInfoSFDC reportFilterSFDC)
            throws JsonGenerationException, JsonMappingException, IOException {
        SObject[] records = sfdc.getRecords(
                "SELECT JBCXM__ColumnList__c, JBCXM__Dimensions__c, JBCXM__HavingClause__c, JBCXM__Measures__c, JBCXM__QueryOptions__c, JBCXM__WhereClause__c, name FROM JBCXM__UIViews__c WHERE Name = '"
                        + reportFilterSFDC.getName() + "'");
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getColumns()),
                resolveStrNameSpace(records[0].getField("JBCXM__ColumnList__c").toString()));
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getDimensions()),
                resolveStrNameSpace(records[0].getField("JBCXM__Dimensions__c").toString()));
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getHavingAdvanceFilter()),
                resolveStrNameSpace(records[0].getField("JBCXM__HavingClause__c").toString()));
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getMeasures()),
                resolveStrNameSpace(records[0].getField("JBCXM__Measures__c").toString()));
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getReportQueryOption()),
                resolveStrNameSpace(records[0].getField("JBCXM__QueryOptions__c").toString()));
        compareGivenJSON(new ObjectMapper().writeValueAsString(reportFilterSFDC.getWhereAdvanceFilter()),
                resolveStrNameSpace(records[0].getField("JBCXM__WhereClause__c").toString()));
    }

    private void compareGivenJSON(String expectedJSON, String actualJSON) {
		JsonFluentAssert.assertThatJson(expectedJSON).when(Option.IGNORING_EXTRA_FIELDS).isEqualTo(actualJSON);
	}
}
