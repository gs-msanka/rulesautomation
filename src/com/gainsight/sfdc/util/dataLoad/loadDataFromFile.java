package com.gainsight.sfdc.util.dataLoad;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.gainsight.pageobject.core.TestEnvironment;
import com.gainsight.sfdc.util.bulk.SfdcBulkApi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class loadDataFromFile {

    public static void main(String[] args) throws IOException {
        loadDataFromFile f = new loadDataFromFile();
        f.loadData("", "", "");
    }

    public void loadData(String object, String fileName, String extId) throws IOException {
        File tempFile = resolveNameSpace(fileName);
        if(extId != null) {
            SfdcBulkApi.pushDataToSfdc(object, "upsert", tempFile, extId);
        } else {
            SfdcBulkApi.pushDataToSfdc(object, "insert", tempFile);
        }
    }

   public File resolveNameSpace(String  fileName) throws IOException {
       TestEnvironment env =new TestEnvironment();
       boolean isPackage = Boolean.valueOf(env.getProperty("sfdc.managedPackage"));
       if(!isPackage) {
           CSVReader csvReader = new CSVReader(new FileReader(fileName));

           List<String[]> csvData = csvReader.readAll();
           String[] headerRows = csvData.get(0);
           for(int i=0; i< headerRows.length; i++) {
               if(!isPackage) {
                   headerRows[i] = headerRows[i].replaceAll("JBCXM__", "");
               }
           }
           csvData.remove(0);
           csvData.add(0, headerRows);
           csvReader.close();

           CSVWriter csvWriter = new CSVWriter(new FileWriter("./resources/process/Temp.csv"));
           csvWriter.writeAll(csvData);
           csvWriter.flush();
           csvWriter.close();

           File f = new File("./testdata/sfdc/Temp.csv");
           return f;
       } else {
           return new File(fileName);
       }


    }
}
