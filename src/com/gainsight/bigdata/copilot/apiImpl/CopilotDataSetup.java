package com.gainsight.bigdata.copilot.apiImpl;

import java.io.FileReader;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class CopilotDataSetup {
	
	 /**
     * Loads CustomObject
     * @param dataETL
     * @throws IOException
     */
    public void loadToObject(DataETL dataETL, String Job) throws IOException {
        ObjectMapper mapper     = new ObjectMapper();
        if(Job != null && Job != "") {
            JobInfo jobInfo= mapper.readValue((new FileReader(Job)), JobInfo.class);
            dataETL.execute(jobInfo);
        }
    }

}
