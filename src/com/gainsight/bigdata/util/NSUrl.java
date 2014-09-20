package com.gainsight.bigdata.util;

public class NSUrl {

	public static final String URL_PARAM = "/urlParam";
	public static final String URL_PARAM1 = URL_PARAM+1;
	public static final String URL_PARAM2 = URL_PARAM+2;
	
	public static String setURLParam(String ...strings){
		String url = strings[0];
		for (int i = 1; i < strings.length; i++) {
			url = url.replace(URL_PARAM+i, strings[i]);
		}
		return url;
	}

	
}
