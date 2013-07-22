package com.gainsight.sfdc.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.pages.BasePage;
import com.gainsight.utils.TestDataHolder;

public class BaseTest {
	protected TestDataHolder testDataLoader=new TestDataHolder();
	String[] dirs={"testdata","sfdc"};
	public final String TEST_DATA_PATH_PREFIX=generatePath(dirs);
    TestEnvironment env=new TestEnvironment();	
	public BasePage basepage;
	
	@BeforeSuite
	public void init(){
		env.start();
		basepage = new BasePage();
	}
	
	@AfterSuite
	public void fini(){
		env.stop();
	}
	
	public String generatePath(String[] dirs){
		String path="";
		for(String dir : dirs){
			path=path+dir+"/";			
		}
		return path;
	}
}
