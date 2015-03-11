package com.gainsight.sfdc.survey.tests;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.survey.pages.SurveyBasePage;
import com.gainsight.sfdc.survey.pages.SurveyPropertiesPage;
import com.gainsight.sfdc.survey.pojo.SurveyProperties;
import com.gainsight.utils.DataProviderArguments;

public class SurveyPropertiesTest extends SurveySetup{
	
	private final String TEST_DATA_FILE       = "testdata/sfdc/survey/tests/SurveyProperties_Test.xls";
	ObjectMapper mapper=new ObjectMapper();
	  @BeforeClass
	    public void setup() throws Exception {
	    	sfdc.connect();
	        basepage.login();
	       
	    }
	  


	    
	    //anonymous
	    //partialAnonymous 
	    //
	   
	    
	

}
