package com.gainsight.sfdc.reporting.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;

public class ReportingTest extends BaseTest{
	private final String CREATE_CASES_SCRIPT    = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateCases.txt";
	private final String CREATE_REPORTS_1M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith1M1D.txt";
	private final String CREATE_REPORTS_1M2D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith1M2D.txt";
	private final String CREATE_REPORTS_2M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith2M1D.txt";
	private final String CREATE_REPORTS_2M2D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith2M2D.txt";
	private final String CREATE_REPORTS_3M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith3M1D.txt";
	private final String CREATE_REPORTS_3M2D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith3M2D.txt"; 
	private final String CREATE_REPORTS_4M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith4M1D.txt";
	private final String CREATE_REPORTS_5M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith5M1D.txt";
	private final String CREATE_REPORTS_6M1D_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWith6M1D.txt";
	private final String CREATE_REPORTS_COLORS_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWithColors.txt";
	private final String CREATE_REPORTS_NORMALIZATION_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWithNormalization.txt";
	
	
	@BeforeClass
	public void loadCases() {
        Log.info("Executing the script to load cases into Case Object...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CASES_SCRIPT));
        Log.info("Completed the script to load cases into Case Object...");
    }
	
	
	@Test
	/*Creates reports with 1 Measure and 1 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith1M1D(){
		Log.info("Executing the script to load reports with 1 Measure and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_1M1D_SCRIPT));
		Log.info("Completed the script to load reports with 1 Measure and 1 Dimension combination ");		
	}
	
	@Test
	/*Creates reports with 1 Measure and 2 Dimensions combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith1M2D(){
		Log.info("Executing the script to load reports with 1 Measure and 2 Dimensions combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_1M2D_SCRIPT));
		Log.info("Completed the script to load reports with 1 Measure and 2 Dimensions combination ");
		
	}
	
		
	@Test
	/*Creates reports with 2 Measures and 1 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith2M1D(){
		Log.info("Executing the script to load reports with 2 Measures and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_2M1D_SCRIPT));
		Log.info("Completed the script to load reports with 2 Measures and 1 Dimension combination ");
	}
	
	@Test
	/*Creates reports with 2 Measures and 2 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith2M2D(){
		Log.info("Executing the script to load reports with 2 Measures and 2 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_2M2D_SCRIPT));
		Log.info("Completed the script to load reports with 2 Measures and 2 Dimension combination ");
	}
	
	@Test
	/*Creates reports with 3 Measures and 1 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith3M1D(){
		Log.info("Executing the script to load reports with 3 Measures and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_3M1D_SCRIPT));
		Log.info("Completed the script to load reports with 3 Measures and 1 Dimension combination ");
	}
	
	@Test
	/*Creates reports with 3 Measures and 2 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith3M2D(){
		Log.info("Executing the script to load reports with 3 Measures and 2 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_3M2D_SCRIPT));
		Log.info("Completed the script to load reports with 3 Measures and 2 Dimension combination ");
	}
	
	@Test
	/*Creates reports with 4 Measures and 1 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith4M1D(){
		Log.info("Executing the script to load reports with 4 Measures and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_4M1D_SCRIPT));
		Log.info("Completed the script to load reports with 4 Measures and 1 Dimension combination ");
	}
	
	@Test
	/*Creates reports with 5 Measures and 1 Dimension combination
	Creates a Layout and adds the reports
	Creates a Related List and adds the reports*/
	public void createReportsWith5M1D(){
		Log.info("Executing the script to load reports with 5 Measures and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_5M1D_SCRIPT));
		Log.info("Completed the script to load reports with 5 Measures and 1 Dimension combination ");
	}
	
	@Test
	public void createReportsWith6M1D(){
		Log.info("Executing the script to load reports with 6 Measures and 1 Dimension combination ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_6M1D_SCRIPT));
		Log.info("Completed the script to load reports with 6 Measures and 1 Dimension combination ");
	}
	
	/*@Test
	public void createReportWithColors(){
		sfdc.runApexCode(CREATE_REPORTS_COLORS_SCRIPT);
	}*/
	
	/*@Test
	public void createReportWithNormalization(){
		sfdc.runApexCode(CREATE_REPORTS_NORMALIZATION_SCRIPT);
	}*/
	
}
