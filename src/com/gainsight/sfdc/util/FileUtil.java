package com.gainsight.sfdc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

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
            if (nameSpace!=null) {

                String extension = FilenameUtils.getExtension(file.getName());
                File tempFile = new File( "./resources/datagen/process/tempFile."+extension);
                FileOutputStream fOut = new FileOutputStream(tempFile);
                try {
                    fOut.write(resolveNameSpace(getFileContents(file), nameSpace).getBytes());
                    fOut.close();
                    fOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new FileReader(tempFile);
            } else {
                return new FileReader(file);
            }
        } catch (FileNotFoundException e) {
            Log.info(e.getLocalizedMessage());
            throw new RuntimeException("File Not Found : " +file.getName());
        }
    }

    public static String resolveNameSpace(String str, String nameSpace) {
        String result = "";
        if (str != null && nameSpace!=null && !nameSpace.equalsIgnoreCase("JBCXM")) {
            result = str.replaceAll("JBCXM__", nameSpace+"__").replaceAll("JBCXM\\.", nameSpace+".");
            Log.info(result);
            return result;
        }else if(str!= null && nameSpace == null){
        	result = str.replaceAll("JBCXM__", " ");
        	return result;
        }else {
            return str;
        }
    }
}
