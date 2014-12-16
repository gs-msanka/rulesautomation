package com.gainsight.sfdc.customer360.pages;

import com.gainsight.sfdc.workflow.pages.WorkflowPage;
import com.gainsight.sfdc.workflow.pojos.CTA;

public class Workflow360Page extends Customer360Page{
    private final String READY_INDICATOR            = "//div[@class='gs_section_title']/h1[contains(.,'Scorecard')]";
	WorkflowPage wfPage;
	
	public Workflow360Page(String value){
        wfPage=new WorkflowPage("360 Page");
	}
	
	//TBD- if any specific page objects related to 360 - cockpit section are to be modified
	public void createCTA(CTA cta){
		cta.setFromCustomer360(true);
		wfPage.createCTA(cta);
	}
}
