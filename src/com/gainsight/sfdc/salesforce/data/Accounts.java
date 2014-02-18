package com.gainsight.sfdc.salesforce.data;

import com.gainsight.pageobject.core.Report;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Accounts {

    static JobInfo jobInfo;
    static ObjectMapper mapper = new ObjectMapper();

    public Accounts() {
        Report.logInfo("In Constructor : Loading Accounts");
    }

    public void Load(String jobFilePath) {
        DataETL dataLoader = new DataETL();
        try {
            jobInfo = mapper.readValue(new FileReader(jobFilePath), JobInfo.class);
        } catch (FileNotFoundException e) {
            Report.logInfo("File Not Found");
        } catch (IOException e) {
            Report.logInfo("IO Exception");
        }
        dataLoader.execute(jobInfo);
    }
}
