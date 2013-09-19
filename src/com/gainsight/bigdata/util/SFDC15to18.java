package com.gainsight.bigdata.util;
public class SFDC15to18 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String result = orgIdDigitConverter("00Di0000000d3wa");
		System.out.println("18 digit: " + result);
	}

	/*
	 * Converts SFDC org id from 15 to 18 digit
	 * Copied code from net https://cloudjedi-developer-edition.ap1.force.com/SalesforceIdConverter
	 */
	public static String orgIdDigitConverter(String a) {
        if (a.length() != 15) {
            System.out.println("Please make sure the to put a valid 15 digit Salesforce Id");
            System.exit(1);
        }
        String b = "";
        for (int c = 0; c < 3; c++) {
            int d = 0;
            for (int e = 0; e < 5; e++) {
                char f = a.charAt(c * 5 + e);
                if (f >= 'A' && f <= 'Z') {
                    d += 1 << e;
                }
            }
            if (d <= 25) {
                b += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(d);
            } else {
                b += "012345".charAt(d - 26);
            }
        }
        return a + b;
	}
}
