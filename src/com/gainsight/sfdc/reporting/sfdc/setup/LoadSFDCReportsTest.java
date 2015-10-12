package com.gainsight.sfdc.reporting.sfdc.setup;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gainsight.sfdc.tests.BaseTest;
import com.gainsight.testdriver.Application;
import com.gainsight.testdriver.Log;
import com.sforce.soap.partner.sobject.SObject;

/**
 * Created by JayaPrakash on 01/09/15.
 */

public class LoadSFDCReportsTest extends BaseTest{
    private final String BASE_PATH = Application.basedir+"/testdata/sfdc/reporting/scripts/";
    private final String CREATE_CASES_SCRIPT    = BASE_PATH+"CreateCases.txt";
    private final String CREATE_USERS_SCRIPT    = Application.basedir+"/apex_scripts/general/CreateUsers.txt";
    private final String CREATE_MILESTONES_SCRIPT = BASE_PATH+"/Milestones.txt";

    private final String CREATE_CS360SECTION_SCRIPT = BASE_PATH+"CreateCS360Section.txt";
    private final String CREATE_LAYOUT_SCRIPT = BASE_PATH+"CreateLayout.txt";
    private final String CREATE_CONTAINER_SCRIPT = BASE_PATH+"CreateContainer.txt";

    private final String CREATE_REPORTS_1M1D_SCRIPT = BASE_PATH+"CreateReportsWith1M1D.txt";
    private final String CREATE_REPORTS_1M2D_SCRIPT = BASE_PATH+"CreateReportsWith1M2D.txt";
    private final String CREATE_REPORTS_2M1D_SCRIPT = BASE_PATH+"CreateReportsWith2M1D.txt";
    private final String CREATE_REPORTS_2M2D_SCRIPT = BASE_PATH+"CreateReportsWith2M2D.txt";
    private final String CREATE_REPORTS_3M1D_SCRIPT = BASE_PATH+"CreateReportsWith3M1D.txt";
    private final String CREATE_REPORTS_3M2D_SCRIPT = BASE_PATH+"CreateReportsWith3M2D.txt";
    private final String CREATE_REPORTS_4M1D_SCRIPT = BASE_PATH+"CreateReportsWith4M1D.txt";
    private final String CREATE_REPORTS_5M1D_SCRIPT = BASE_PATH+"CreateReportsWith5M1D.txt";
    private final String CREATE_REPORTS_6M1D_SCRIPT = BASE_PATH+"CreateReportsWith6M1D.txt";
    private final String CREATE_REPORTS_COLORS_SCRIPT = BASE_PATH+"CreateReportsWithColors.txt";
    private final String CREATE_REPORTS_NORMALIZATION_SCRIPT = BASE_PATH+"CreateReportsWithNormalization.txt";
    private final String CREATE_REPORTS_MULTIPLECOMBO_SCRIPT = BASE_PATH+"CreateReportsWithMultipleCombo.txt";
    private final String CREATE_REPORTS_SUMMARIZEDBY_SCRIPT = BASE_PATH+"CreateReportsWithSummarizedByOption.txt";

    @BeforeClass
    public void loadInitScripts() throws Exception {
        Assert.assertTrue(sfdc.connect(),"Failed to connect SFDC");
        Log.info("Executing the script to create cases into Case Object...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CASES_SCRIPT));
        Log.info("Executing the script to create Users...");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_USERS_SCRIPT));
        Log.info("Executing the script to create Milestones");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_MILESTONES_SCRIPT).replaceAll("customerRecordCount", "5"));

    }

    public void createReportWithAnyCombination(String ScriptName,String ReportName,String GraphType,String RLName,String LayoutName,String baseObjName){
        //Adding the report to C360 page
        SObject[] CS360SectionID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__C360Sections__c WHERE NAME = '"+RLName+"'"));
        if ((CS360SectionID.length < 1)){
            throw new RuntimeException("No Related list with the name "+RLName);
        }
        sfdc.runApexCode(getNameSpaceResolvedFileContents(ScriptName).replaceAll("viewName", ReportName).replaceAll("graphType", GraphType).replaceAll("cs360SectionID", CS360SectionID[0].getId()));
        //Adding the report to Home page

        SObject[] HomeLayoutID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__Dashboard__c WHERE NAME ='"+LayoutName +"'"));
        SObject[] ReportID = sfdc.getRecords(resolveStrNameSpace("SELECT ID FROM JBCXM__UIViews__c WHERE NAME = '"+ReportName+"'"));

        if (((HomeLayoutID.length < 1)) || ((ReportID.length < 1))){
            throw new RuntimeException("No Home Page with the name "+LayoutName);
        }
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CONTAINER_SCRIPT).replaceAll("LayoutID", HomeLayoutID[0].getId()).replaceAll("viewID", ReportID[0].getId()));
    }



    @Test
    //Creates Reports on Case Object
    public void createReportsWith1M1D(){
        //Creating CS 360 Section
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "1ShowMe_1By_RL").replaceAll("objName", "Case"));
        //Creating Home Page layout
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "1ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"PIE_1_1_CaseObj","PIE", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Bar_1_1_CaseObj","BAR", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Column_1_1_CaseObj","COLUMN", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT, "Line_1_1_CaseObj", "LINE", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Area_1_1_CaseObj","AREA", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT,"Table_1_1_CaseObj","LIST", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M1D_SCRIPT, "D3BUBBLE_1_1_CaseObj", "D3BUBBLE", "1ShowMe_1By_RL", "1ShowMe_1By_Layout", "Case");
    }

    @Test
    public void createReportsWith1M2D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "1ShowMe_2By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "1ShowMe_2By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT, "Table_1_2_CaseObj", "LIST", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT, "Bar_1_2_CaseObj", "BAR", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Column_1_2_CaseObj","COLUMN", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Line_1_2_CaseObj","LINE", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Area_1_2_CaseObj","AREA", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Stacked_Bar_1_2_CaseObj","STACKED-BAR", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"Stacked_Column_1_2_CaseObj","STACKED-COLUMN", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"HeatMap_1_2_CaseObj","HEATMAP", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_1M2D_SCRIPT,"D3BUBBLE_1_2_CaseObj","D3BUBBLE", "1ShowMe_2By_RL", "1ShowMe_2By_Layout", "Case");
    }


    @Test
    public void createReportsWith2M1D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "2ShowMe_1By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "2ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_2M1D_SCRIPT, "Table_2_1_CaseObj", "LIST", "2ShowMe_1By_RL", "2ShowMe_1By_Layout", "Case");
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

    @Test
    public void createReportsWith2M2D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "2ShowMe_2By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "2ShowMe_2By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_2M2D_SCRIPT,"Table_2_2_CaseObj","LIST", "2ShowMe_2By_RL", "2ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_2M2D_SCRIPT,"Scatter_2_2_CaseObj","SCATTER", "2ShowMe_2By_RL", "2ShowMe_2By_Layout", "Case");
    }

    @Test
    public void createReportsWith3M1D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "3ShowMe_1By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "3ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Table_3_1_CaseObj","LIST", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Bar_3_1_CaseObj","BAR", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Column_3_1_CaseObj","COLUMN", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Line_3_1_CaseObj","LINE", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT, "Area_3_1_CaseObj", "AREA", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT, "Stacked_Bar_3_1_CaseObj", "STACKED-BAR", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Stacked_Column_3_1_CaseObj","STACKED-COLUMN", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M1D_SCRIPT,"Bubble_3_1_CaseObj","BUBBLE", "3ShowMe_1By_RL", "3ShowMe_1By_Layout", "Case");

    }

    @Test
    public void createReportsWith3M2D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "3ShowMe_2By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "3ShowMe_2By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_3M2D_SCRIPT,"Table_3_2_CaseObj","LIST", "3ShowMe_2By_RL", "3ShowMe_2By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_3M2D_SCRIPT,"Bubble_3_2_CaseObj","BUBBLE", "3ShowMe_2By_RL", "3ShowMe_2By_Layout", "Case");
    }

    @Test
    public void createReportsWith4M1D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "4ShowMe_1By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "4ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Table_4_1_CaseObj","LIST", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT, "Line_4_1_CaseObj", "LINE", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT, "Area_4_1_CaseObj", "AREA", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Stacked_Bar_4_1_CaseObj","STACKED-BAR", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_4M1D_SCRIPT,"Stacked_COlumn_4_1_CaseObj","STACKED-COLUMN", "4ShowMe_1By_RL", "4ShowMe_1By_Layout", "Case");
    }

    @Test
    public void createReportsWith5M1D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "5ShowMe_1By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "5ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Table_5_1_CaseObj","LIST", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Line_5_1_CaseObj","LINE", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_5M1D_SCRIPT,"Area_5_1_CaseObj","AREA", "5ShowMe_1By_RL", "5ShowMe_1By_Layout", "Case");

    }

    @Test
    public void createReportsWith6M1D(){
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_CS360SECTION_SCRIPT).replaceAll("CS360SectionName", "6ShowMe_1By_RL").replaceAll("objName", "Case"));
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_LAYOUT_SCRIPT).replaceAll("layoutName", "6ShowMe_1By_Layout"));

        createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"CREATE_REPORTS_6M1D_SCRIPT","LIST", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"Line_6_1_CaseObj","LINE", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
        createReportWithAnyCombination(CREATE_REPORTS_6M1D_SCRIPT,"Area_6_1_CaseObj","AREA", "6ShowMe_1By_RL", "6ShowMe_1By_Layout", "Case");
    }

    @Test
    public void createReportWithColors(){
        Log.info("Executing the script to load reports with color combinations ");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_COLORS_SCRIPT));
        Log.info("Completed the script to load reports with color combinations ");
    }

    @Test
    public void createReportWithNormalization(){
        Log.info("Executing the script to load reports with Normalizations ");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_NORMALIZATION_SCRIPT));
        Log.info("Completed the script to load reports with Normalizations ");
    }

    @Test
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

    @Test
    public void createReportsWithSummarizedBy(){
        Log.info("Executing the script to load reports with Summarized By option ");
        sfdc.runApexCode(getNameSpaceResolvedFileContents(CREATE_REPORTS_SUMMARIZEDBY_SCRIPT));
        Log.info("Completed the script to load reports with Summarized By option ");

    }

}
