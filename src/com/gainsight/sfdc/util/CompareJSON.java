package com.gainsight.sfdc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompareJSON {
	public static boolean compareJSONs(File f1, File f2, boolean ignoreSFDCIDs)
			throws IOException, JSONException {
		BufferedReader bfr = new BufferedReader(new FileReader(f1));
		BufferedReader bfr1 = new BufferedReader(new FileReader(f2));
		String c1 = "", c2 = "", temp = "";
		while ((temp = bfr1.readLine()) != null)
			c1 += temp;
		while ((temp = bfr.readLine()) != null)
			c2 += temp;
		bfr.close();
		bfr1.close();
		return compareJSONs(c1, c2, ignoreSFDCIDs);
	}

	public static boolean compareJSONs(String str1, String str2,
			boolean ingoreIDs) throws JSONException {
		Object obj1Converted = convertJsonElement(new JSONObject(str1),
				ingoreIDs);
		Object obj2Converted = convertJsonElement(new JSONObject(str2),
				ingoreIDs);
		return obj1Converted.equals(obj2Converted);
	}

	private static Object convertJsonElement(Object elem, boolean ignoreIDs)
			throws JSONException {
		if (elem instanceof JSONObject) {
			JSONObject obj = (JSONObject) elem;
			@SuppressWarnings("unchecked")
			Iterator<String> keys = obj.keys();
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			while (keys.hasNext()) {
				String key = keys.next();
				if (ignoreIDs) {
					Object value = convertJsonElement(obj.get(key), ignoreIDs);
					if (value instanceof String) {
						if (((String) value)
								.matches("[a-zA-Z0-9]{15}|[a-zA-Z0-9]{18}")) {
							value = "SFDCID";
						}
					}
				} else {
					jsonMap.put(key,
							convertJsonElement(obj.get(key), ignoreIDs));
				}
			}
			return jsonMap;
		} else if (elem instanceof JSONArray) {
			JSONArray arr = (JSONArray) elem;
			Set<Object> jsonSet = new HashSet<Object>();
			for (int i = 0; i < arr.length(); i++) {
				jsonSet.add(convertJsonElement(arr.get(i),ignoreIDs));
			}
			return jsonSet;
		} else {
			return elem;
		}
	}
}
