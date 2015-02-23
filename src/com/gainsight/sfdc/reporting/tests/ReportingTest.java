package com.gainsight.sfdc.reporting.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.sforce.soap.partner.sobject.SObject;

public class ReportingTest extends BaseTest{
	private final String CREATE_CASES_SCRIPT    = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateCases.txt";
	private final String CREATE_USERS_SCRIPT    = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateUsers.txt";
	private final String CREATE_CTAS_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateCTAs.txt";
	private final String CREATE_MILESTONES_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/Milestones.txt";
	
	private final String CREATE_CS360SECTION_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateCS360Section.txt";
	private final String CREATE_LAYOUT_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateLayout.txt";
	private final String CREATE_CONTAINER_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateContainer.txt";
	
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
	private final String CREATE_REPORTS_MULTIPLECOMBO_SCRIPT = Application.basedir+"/testdata/sfdc/reporting/scripts/CreateReportsWithMultipleCombo.txt";
	
	//Colors - Accounts should have Current score label in Customer Info object
	//Colors - Accounts should have Current Score Value in Usage data object
	
	@BeforeClass
	public void loadInitScripts() throws Exception {
		sfdc.connect();
		Log.info("Executing the script to create cases into Case Object...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CASES_SCRIPT));
        Log.info("Executing the script to create Users...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
        Log.info("Executing the script to create CTAs...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CTAS_SCRIPT));
        Log.info("Executing the script to create Milestones");      
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_MILESTONES_SCRIPT).replaceAll("customerRecordCount", "5"));
        
    }
	
	public void createReportWithAnyCombination(String ScriptName,String ReportName,String GraphType,String RLName,String LayoutName,String baseObjName){
		//Creating Report and assign the report to CS 360 section
		
		SObject[] CS360SectionID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__C360Sections__c WHERE NAME = '"+RLName+"'"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(ScriptName).replaceAll("viewName", ReportName).replaceAll("graphType", GraphType).replaceAll("cs360SectionID", CS360SectionID[0].getId()));
		//Adding the report to home page	
		SObject[] HomeLayoutID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Dashboard__c WHERE NAME ='"+LayoutName +"'"));
		SObject[] ReportID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__UIViews__c WHERE NAME = '"+ReportName+"'"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTAINER_SCRIPT).replaceAll("LayoutID", HomeLayoutID[0].getId()).replaceAll("viewID", ReportID[0].getId()));		
	}
	
	

	@Test(priority = 1)
	//Creates Reports on Case Object
	public void createReportsWith1M1D(){
		//Creating CS 360 Section
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "1ShowMe_1By_RL").replaceAll("objName", "Case"));
		//Creating Home Page layout
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "1ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"PIE_1_1_CaseObj","PIE", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Bar_1_1_CaseObj","BAR", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Column_1_1_CaseObj","COLUMN", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Line_1_1_CaseObj","LINE", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Area_1_1_CaseObj","AREA", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Table_1_1_CaseObj","LIST", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
	}
	
	@Test(priority = 2)
	public void createReportsWith1M2D(){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "1ShowMe_2By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "1ShowMe_2By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Table_1_2_CaseObj","LIST", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Bar_1_2_CaseObj","BAR", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Column_1_2_CaseObj","COLUMN", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Line_1_2_CaseObj","LINE", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Area_1_2_CaseObj","AREA", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Stacked_Bar_1_2_CaseObj","STACKED-BAR", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Stacked_Column_1_2_CaseObj","STACKED-COLUMN", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
	}
	
		
	@Test(priority = 3)
	public void createReportsWith2M1D(){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "2ShowMe_1By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "2ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"Table_2_1_CaseObj","LIST", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"PIE_2_1_CaseObj","PIE", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"Stacked_Column_2_1_CaseObj","STACKED-COLUMN", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"Stacked_Bar_2_1_CaseObj","STACKED-BAR", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"Column_2_1_CaseObj","COLUMN", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"BAR_2_1_CaseObj","BAR", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"COLUMN_LINE_2_1_CaseObj","COLUMN-LINE", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"Scatter_2_1_CaseObj","SCATTER", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"LINE_2_1_CaseObj","LINE", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT,"AREA_2_1_CaseObj","AREA", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
	}
	
	@Test(priority = 4)
	public void createReportsWith2M2D(){	
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "2ShowMe_2By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "2ShowMe_2By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_2M2D_SCRIPT,"Table_2_2_CaseObj","LIST", "2ShowMe_2By_RL", "2ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_2M2D_SCRIPT,"Scatter_2_2_CaseObj","SCATTER", "2ShowMe_2By_RL", "2ShowMe_2By_Layout", "Case");
	}
	
	@Test(priority = 5)
	public void createReportsWith3M1D(){	
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "3ShowMe_1By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "3ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Table_3_1_CaseObj","LIST", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Bar_3_1_CaseObj","BAR", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Column_3_1_CaseObj","COLUMN", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Line_3_1_CaseObj","LINE", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Area_3_1_CaseObj","AREA", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Stacked_Bar_3_1_CaseObj","STACKED-BAR", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Stacked_Column_3_1_CaseObj","STACKED-COLUMN", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Bubble_3_1_CaseObj","BUBBLE", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
		
	}
	
	@Test(priority = 6)
	public void createReportsWith3M2D(){	
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "3ShowMe_2By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "3ShowMe_2By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_3M2D_SCRIPT,"Table_3_2_CaseObj","LIST", "3ShowMe_2By_RL", "3ShowMe_2By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_3M2D_SCRIPT,"Bubble_3_2_CaseObj","BUBBLE", "3ShowMe_2By_RL", "3ShowMe_2By_Layout", "Case");
	}
	
	@Test(priority = 7)
	public void createReportsWith4M1D(){	
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "4ShowMe_1By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "4ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Table_4_1_CaseObj","LIST", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Line_4_1_CaseObj","LINE", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Area_4_1_CaseObj","AREA", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Stacked_Bar_4_1_CaseObj","STACKED-BAR", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Stacked_COlumn_4_1_CaseObj","STACKED-COLUMN", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");		
	}
	
	@Test(priority = 8)
	public void createReportsWith5M1D(){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "5ShowMe_1By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "5ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Table_5_1_CaseObj","LIST", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Line_5_1_CaseObj","LINE", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Area_5_1_CaseObj","AREA", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");
		
	}
	
	@Test(priority = 9)
	public void createReportsWith6M1D(){
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "6ShowMe_1By_RL").replaceAll("objName", "Case"));
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "6ShowMe_1By_Layout"));
		
		createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"CREATE_REPORTS_6M1D_SCRIPT","LIST", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"Line_6_1_CaseObj","LINE", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
		createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"Area_6_1_CaseObj","AREA", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
	}
	
	@Test(priority = 10)
	public void createReportWithColors(){
		Log.info("Executing the script to load reports with color combinations ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_COLORS_SCRIPT));
		Log.info("Completed the script to load reports with color combinations ");
	}
	
	@Test(priority = 11)
	public void createReportWithNormalization(){
		Log.info("Executing the script to load reports with Normalizations ");
		sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_NORMALIZATION_SCRIPT));
		Log.info("Completed the script to load reports with Normalizations ");
	}
	
	@Test(priority = 12)
	public void createreportsWithMultipleCombinations(){
		Log.info("Executing the script to load reports with multiple combinations ");
		
		SObject[] productPerformance = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'Product Performance' and JBCXM__Category__c='Alert Reason'"));
		SObject[] productAdoption = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'Product Adoption' and JBCXM__Category__c='Alert Reason'"));
		SObject[] productRelease = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'Product Release' and JBCXM__Category__c='Alert Reason'"));
		SObject[] expectationMismatch = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'Expectation Mismatch' and JBCXM__Category__c='Alert Reason'"));
		
		SObject[] open = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'Open' and JBCXM__Category__c = 'Alert Status'"));
		SObject[] inProgress = sfdc.getRecords(resolveStrNameSpace("Select ID from JBCXM__Picklist__c where name = 'In Progress' and JBCXM__Category__c = 'Alert Status'"));
		
		String tmpfileContents = getNameSpaceResolvedFileContents(CREATE_REPORTS_MULTIPLECOMBO_SCRIPT);
	
		tmpfileContents = tmpfileContents.replaceAll("Product Performance", productPerformance[0].getId());
		tmpfileContents = tmpfileContents.replaceAll("Product Adoption", productAdoption[0].getId());
		tmpfileContents = tmpfileContents.replaceAll("Product Release", productRelease[0].getId());
		tmpfileContents = tmpfileContents.replaceAll("Expectation Mismatch", expectationMismatch[0].getId());
		
		tmpfileContents = tmpfileContents.replaceAll("Open", open[0].getId());
		tmpfileContents = tmpfileContents.replaceAll("In Progress", inProgress[0].getId());
				
		sfdc.runApexCode(resolveStrNameSpace(tmpfileContents));
		Log.info("Completed the script to load reports with multiple combinations ");
	}
	
	
}
