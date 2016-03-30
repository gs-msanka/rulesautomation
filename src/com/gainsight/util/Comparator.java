package com.gainsight.util;

import au.com.bytecode.opencsv.CSVReader;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.testdriver.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class provides different compare utility methods.
 * Created by gs-vmenon on 21/5/15.
 */
public class Comparator {

    /**
     * This method compares two csv files and give you a list of diff for all the rows in "actual" that is not present in "expected" csv file.
     *
     * @param expected File object of the csv file that contains the expected data
     * @param actual   File object of the csv file that contains the actual data that needs to be verified inside the expected file.
     * @return A List of Map object where each Map object represents one row inside the actual csv file that was not found inside the expected csv file
     */
    public static List<Map<String, String>> compareCsvAndGetDiff(File expected, File actual) {

        try {
            CSVReader expectedReader = new CSVReader(new FileReader(expected));
            CSVReader actualReader = new CSVReader(new FileReader(actual));

            List<Map<String, String>> expectedCsvParsedData = getParsedCsvData(expectedReader);
            List<Map<String, String>> actualCsvParsedData = getParsedCsvData(actualReader);

            return compareListData(expectedCsvParsedData, actualCsvParsedData);

        } catch (FileNotFoundException fnf) {
            throw new RuntimeException(fnf);
        }
    }

    /**
     * This method compares two csv files and returns true or false if any of the line of the  "actual" csv file that is not present in "expected" csv file.
     *
     * @param expected File object of the csv file that contains the expected data
     * @param actual   File object of the csv file that contains the actual data that needs to be verified inside the expected file.
     * @return True  incase all the lines inside the actual csv file is found inside the expected csv file else False
     */
    public static boolean isCsvEqual(File expected, File actual) {
        return compareCsvAndGetDiff(expected, actual).size() == 0;
    }

    /**
     * This method compares two simple Json Arrays and give you a list of diff for all the objects in "actual" json array that is not present in "expected" Json Array.
     *
     * @param expected File object of the csv file that contains the expected data
     * @param actual   File object of the csv file that contains the actual data that needs to be verified inside the expected file.
     * @return A List of Map object where each Map object represents one Json Object  inside the actual object that was not found inside the expected Json Object
     */
    public static List<Map<String, String>> compareSimpleJsonAndGetDiff(JsonArray expected, JsonArray actual) {

        List<Map<String, String>> expectedCsvParsedData = getParsedSimpleJsonArray(expected);
        List<Map<String, String>> actualCsvParsedData = getParsedSimpleJsonArray(actual);

        return compareListData(expectedCsvParsedData, actualCsvParsedData);
    }

    /***
     * Method fetches the parsed csv content with namespaces resolved for Gainsight headers.
     *
     * @param csvReader
     * @param doResolveNameSpace Set this value true to resolve namespace for headers.
     * @return List of hashMap of csv content.
     */
    private static List<Map<String, String>> internalGetParsedCsvData(CSVReader csvReader, boolean doResolveNameSpace){

        List<Map<String, String>> parsedCsv = new ArrayList<Map<String, String>>();
        Map<String, Integer> headerMap = new HashMap<>();
        List<String[]> parsedCsvReader = null;
        try {
            parsedCsvReader = csvReader.readAll();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        String[] headerArray = parsedCsvReader.get(0);
        for (int i = 0; i < headerArray.length; i++) {
            String headerName = headerArray[i];
            if (doResolveNameSpace) {
                headerName = FileUtil.resolveNameSpace(headerArray[i]);
            }
            headerMap.put(headerName, i);
        }


        for (int rowIndex = 1; rowIndex < parsedCsvReader.size(); rowIndex++) {
            String[] csvRowData = parsedCsvReader.get(rowIndex);
            Map<String, String> rowMapData = new HashMap<>();

            for (String columnName : headerMap.keySet()) {
                int columnIndex = headerMap.get(columnName);
                String columnData = "";
                if (columnIndex < csvRowData.length) {
                    columnData = csvRowData[columnIndex];
                }
                rowMapData.put(columnName, columnData);
            }
            parsedCsv.add(rowMapData);
        }
        return parsedCsv;
    }

    /***
     * Method fetches the parsed csv content with namespaces resolved for Gainsight headers.
     *
     * @param csvReader
     * @return List of hashMap of csv content
     */
    public static List<Map<String, String>> getParsedCsvDataWithHeaderNamespaceResolved(CSVReader csvReader) {
        return internalGetParsedCsvData(csvReader, true);
    }

    public static List<Map<String, String>> getParsedCsvDataWithHeaderNamespaceResolved(FileReader fileReader) {
        return getParsedCsvDataWithHeaderNamespaceResolved(new CSVReader(fileReader));
    }

    public static List<Map<String, String>> getParsedCsvDataWithHeaderNamespaceResolved(String filePath) {
        try {
            return getParsedCsvDataWithHeaderNamespaceResolved(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<Map<String, String>> getParsedCsvData(CSVReader csvReader) {
        return internalGetParsedCsvData(csvReader, false);
    }

    private static List<Map<String, String>> getParsedSimpleJsonArray(JsonArray jsonArray) {
        Iterator<JsonElement> jsonIterator = jsonArray.iterator();
        List<Map<String, String>> parsedJsonArray = new ArrayList<>();
        while (jsonIterator.hasNext()) {
            JsonElement jsonElement = jsonIterator.next();
            parsedJsonArray.add(convertSimpleJsonToMap(jsonElement.getAsJsonObject()));
        }
        return parsedJsonArray;
    }

    private static Map<String, String> convertSimpleJsonToMap(JsonObject jsonObject) {
        Map<String, String> jsonMap = new HashMap<>();

        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> jsonElementEntry : entrySet) {
            jsonMap.put(jsonElementEntry.getKey(), jsonElementEntry.getValue().getAsString());
        }

        return jsonMap;
    }

    /**
     * Compared two List of Map&lt; String, String&gt; and check whether all the Map objects in actual object is present inside the expected object
     *
     * @param expected Expected object data set
     * @param actual   Actual data set that need to be searched inside the expected
     * @return A List of Map object where each Map object represents one Map object in actual list that was not found inside the expected list
     */
    public static List<Map<String, String>> compareListData(List<Map<String, String>> expected, List<Map<String, String>> actual) {
        List<Map<String, String>> diff = new ArrayList<Map<String, String>>();

        for (Map<String, String> actualRowData : actual) {
            boolean rowFound = false;

            for (Map<String, String> expectedRowData : expected) {
                if (actualRowData.equals(expectedRowData)) {
                    rowFound = true;
                    break;
                }
            }
            if (!rowFound) {
                diff.add(actualRowData);
            }
        }
        Log.info("No of Records Not Matched : " + diff.size());
        return diff;
    }

}
