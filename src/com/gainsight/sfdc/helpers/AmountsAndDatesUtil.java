package com.gainsight.sfdc.helpers;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gainsight.pageobject.core.WebPage;

public class AmountsAndDatesUtil extends WebPage{

	public String currencyFormat(String amt) {
		DecimalFormat moneyFormat = new DecimalFormat("$0");
		return moneyFormat.format(new Long(amt));
	}

	public void enterDate(String identifier, String date) {
		field.click(identifier);
		field.click("//td[@class='weekday']");
		field.clearAndSetText(identifier, date);
	}

	/**
	 * Even we are handling stale element exceptions at framework level it is
	 * time consuming hence if see any element happens to bound to this
	 * exception because of frequent DOM updates, please call this method before
	 * performing any action on that element so that webdriver finds elements
	 * after DOM got updated( 2 seconds is optimal time)
	 */
	public void stalePause() {
		sleep(2);
	}

	public String getFormattedDate(String dateStr) {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("unable to  parse date string "
					+ e.getMessage());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		return sdf.format(date);
	}
	
	public String formatNumber(String number){
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.ENGLISH);
		return numberFormatter.format(Integer.parseInt(number));
	}

}
