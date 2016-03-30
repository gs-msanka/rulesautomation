package com.gainsight.sfdc.util;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.testdriver.Log;
import org.apache.commons.io.FilenameUtils;

/**
 * Created with IntelliJ IDEA.
 * User: gainsight
 * Date: 11/09/14
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {


    private static String nameSpace = null;

    /***
     * Update this field nameSpace to respective value to resolve GainSight headers.     *
     * @param nameSpace
     */
    public static void setNameSpace(String nameSpace){
        nameSpace = nameSpace;
    }

    public static String getFileContents(String fileName) {
        String code = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            code = stringBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getFileContents(File file) {
        String code = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            code = stringBuilder.toString();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static FileReader resolveNameSpace(File file, String nameSpace) {
        try {
            if(!"JBCXM".equalsIgnoreCase(nameSpace)) {
                String extension = FilenameUtils.getExtension(file.getName());
                File tempFile = new File( "./resources/datagen/process/tempFile."+extension);
                FileOutputStream fOut = new FileOutputStream(tempFile);
                    fOut.write(resolveNameSpace(getFileContents(file), nameSpace).getBytes());
                    fOut.close();
                    fOut.flush();
                return new FileReader(tempFile);
            } else {
                return new FileReader(file);
            }
        } catch (FileNotFoundException e) {
            Log.error(e.getLocalizedMessage());
            throw new RuntimeException("File Not Found : " +file.getName());
        } catch (IOException e) {
            Log.error(e.getLocalizedMessage());
            throw new RuntimeException("IO Exception on : " +file.getName());
        }
    }

    public static String resolveNameSpace(String str){
        return resolveNameSpace(str, nameSpace);
    }

    public static String resolveNameSpace(String str, String nameSpace) {
        String result = "";
        if (str != null && nameSpace!=null && !nameSpace.equalsIgnoreCase("JBCXM")) {
            result = str.replaceAll("JBCXM__", nameSpace+"__").replaceAll("JBCXM\\.", nameSpace+".");
            result = result.replaceAll("jbcxm__", nameSpace+"__").replaceAll("jbcxm\\.", nameSpace+".");
            Log.info(result);
            return result;
        }else if(str!= null && nameSpace == null){
        	result = str.replaceAll("JBCXM__", "").replaceAll("JBCXM\\.", "");
	     	result = result.replaceAll("jbcxm__", "").replaceAll("jbcxm\\.","");
        	return result;
        }else {
            return str;
        }
    }

    public static File writeToCSV(List<Map<String, String>> data,  String filePath) throws IOException {
        if(data ==null || data.size() <1) {
            throw new IllegalStateException("You need to have atleast one record.");
        }
        Log.info("Writing to csv file.");
        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), ',', '"', '\\', "\n");
        int headerCount = data.get(0).keySet().size();
        String[] headers = new String[headerCount];
        List<String[]> values =new ArrayList<>();
        values.add(data.get(0).keySet().toArray(headers));
        int i = 0;
        for(Map<String, String> map : data) {
            String[] temp = new String[headerCount];
            for(String header : headers) {
                temp[i]= map.get(header);
                i++;
            }
            i = 0;
            values.add(temp);
        }
        writer.writeAll(values);
        writer.flush();
        return outputFile;
    }
}