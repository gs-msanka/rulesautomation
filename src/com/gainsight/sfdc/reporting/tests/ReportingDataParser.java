package com.gainsight.sfdc.reporting.tests;

import com.gainsight.sfdc.util.DateUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * This class will help for creating expected report data.
 * It will internally parse the dates and convert to 0, -1's, etc...
 * If we want to parse the expected json data, just call this class by changing input file.
 * Created by Gainsight on 2/16/2016.
 */
public class ReportingDataParser {

    static Date date = Calendar.getInstance().getTime();

    public static void main(String[] args) throws Exception {

        String fileName = "C:\\GainSightAutomation\\ForkCode\\gs-automation\\testdata\\newstack\\reporting\\data\\reportData\\redshift\\RedshiftDateTimeAggData.json";
        String fileContent = FileUtils.readFileToString(
                new File(fileName));
        String newJson = ReportingDataParser.parserDate(fileContent);
        System.out.println(newJson);
    }

    /**
     * This method will help for creating the input json. It will take input as json and replace the actual dates to 0 , -1 etc .
     * Small info why we need this method. If we run any report, we can get some data, but in that data, we can find actual dates for date field(2-18-2016).
     * But we need to replaces those dates to 0, -1 etc. So run time we can replace with actual dates.
     * @param inputJson
     * @return
     */
    public static String parserDate(String inputJson) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(inputJson);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray jsonArray1 = new JsonArray();
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
            if (jsonObject1.get("Date") != null && jsonObject1.get("Date").toString().contains("-")) {
                String value = jsonObject1.get("Date").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date");
                jsonObject2.addProperty("Date", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);

            }
            if (jsonObject1.get("Date1") != null && jsonObject1.get("Date1").toString().contains("-")) {
                String value = jsonObject1.get("Date1").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date1");
                jsonObject2.addProperty("Date1", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);

            }
            if (jsonObject1.get("Date2") != null && jsonObject1.get("Date2").toString().contains("-")) {
                String value = jsonObject1.get("Date2").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date2");
                jsonObject2.addProperty("Date2", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);

            }
            if (jsonObject1.get("Date3") != null && jsonObject1.get("Date3").toString().contains("-")) {
                String value = jsonObject1.get("Date3").getAsString();
                JsonObject jsonObject2;
                jsonObject2 = jsonObject1;
                jsonObject2.remove("Date3");
                jsonObject2.addProperty("Date3", DateUtil.addWeeks(date, Integer.parseInt(value), "MM-dd-yyyy"));

                jsonArray1.add(jsonObject2);

            }
        }
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("data", jsonArray1);
        return jsonObject1.toString();

    }
}
