package com.gainsight.bigdata.copilot.apiImpl;

import java.io.FileReader;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import com.gainsight.sfdc.util.datagen.DataETL;
import com.gainsight.sfdc.util.datagen.JobInfo;

public class CopilotDataSetup {
	
    //Loads datas to objects via DataLoad API
    public void loadToObject(DataETL dataETL, String job) throws IOException {
        ObjectMapper mapper     = new ObjectMapper();
        if(job != null && job != "") {
            JobInfo jobInfo= mapper.readValue((new FileReader(job)), JobInfo.class);
            dataETL.execute(jobInfo);
        }
    }

}
