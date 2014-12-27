package com.gainsight.sfdc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.gainsight.pageobject.util.Timer;
import com.gainsight.sfdc.tests.BaseTest;

public class AmountsUtil {

	public String currencyFormat(String amt) {
		DecimalFormat moneyFormat = new DecimalFormat("$0");
		return moneyFormat.format(new Long(amt));
	}

	/**
	 * Even we are handling stale element exceptions at framework level it is
	 * time consuming hence if see any element happens to bound to this
	 * exception because of frequent DOM updates, please call this method before
	 * performing any action on that element so that webdriver finds elements
	 * after DOM got updated( 2 seconds is optimal time)
	 */
	public void stalePause() {
		Timer.sleep(2);
	}

	public void sleep(int seconds) {
		Timer.sleep(seconds);
	}

	public String formatNumber(String number) {
		NumberFormat numberFormatter = NumberFormat
				.getNumberInstance(Locale.ENGLISH);
		return numberFormatter.format(Integer.parseInt(number));
	}

}
