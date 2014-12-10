package com.gainsight.sfdc.customer360.pages;

import java.util.HashMap;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.sponsorTracking.sponsorTracking;
import com.gainsight.sfdc.tests.BaseTest;
import com.sforce.soap.partner.sobject.SObject;



public class SponsorTracking360 extends Customer360Page{
	
	public final String TRACK_CONTACTS_BUTTON="//div[contains(text(),'Track Contacts')]";
	public final String TRACK_CONTACTS_ACTION="//td[text()='%s']/..//td[@class='actionCellGrid']/span";
	public final String TRACK_SEARCHED_IMAGE="//div[@class='gs-sponsor-cardview']//div[@class='sponsor-card']/img";
	public final String TRACK_SEARCHED_DIALOG="//span[contains(text(),'Suggested profiles')]/../..";
	
	
	public static SponsorTracking360 init(){
		return new SponsorTracking360();
	}
	
	public void searchSponsor(String CustomerName,String Contactname,String Email,sponsorTracking sp_api){
		Report.logInfo(CustomerName+"  "+Contactname+"  "+Email);
		
		searchCustomer(CustomerName, false, false);
		goToSponsorSection();
		button.click(TRACK_CONTACTS_BUTTON);
		String TrackAction=String.format(TRACK_CONTACTS_ACTION,Contactname);
		item.click(TrackAction);
		wait.waitTillElementPresent(TRACK_SEARCHED_DIALOG, 5, 10);
		
		BaseTest bt = new BaseTest();
		//bt.apex.runApex("List<Contact> Con= [Select Contact.FirstName,Contact.LastName,Contact.Title from Contact where Contact.AccountId in (Select Id from Account where Account.Name='IVY Comptech')];");
		SObject[] Query= bt.soql.getRecords("SELECT FirstName, LastName, Title, Email, Name FROM Contact where AccountId in(Select Id from Account where Name='"+CustomerName+"')");
		int i=(Query.length)-1;
		while(i>=0){
			if((Query[i].getField("Name").toString().equals(Contactname)) && (Query[i].getField("Email").toString().equals(Email)))
			   break;
			else
			   i--;
		}
		HashMap<String, Object>  RequestBody=new HashMap<String, Object> ();
		RequestBody.put("FirstName", Query[i].getField("FirstName"));
		RequestBody.put("LastName", Query[i].getField("LastName"));
		RequestBody.put("Title", Query[i].getField("Title"));
		RequestBody.put("Email", Query[i].getField("Email"));
		RequestBody.put("Name", CustomerName);
		sp_api.SearchAPI(RequestBody);
		
		//Query[i].getField("FirstName"),Query[i].getField("LastName"),Query[i].getField("Title"),CustomerName
		
	}

}
