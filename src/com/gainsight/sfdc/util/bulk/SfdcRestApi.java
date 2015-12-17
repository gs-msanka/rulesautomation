package com.gainsight.sfdc.util.bulk;


import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.SalesforceConnector;
import com.gainsight.sfdc.beans.SFDCInfo;
import com.gainsight.sfdc.util.FileUtil;
import com.gainsight.util.config.SfdcConfig;
import com.gainsight.util.config.SfdcConfigProvider;
import com.gainsight.utils.config.ConfigProviderFactory;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giriabu on 05/11/15.
 */
public class SfdcRestApi {

    public static SfdcConfig sfdcConfig = ConfigProviderFactory.getConfig(SfdcConfig.class);
    static SalesforceConnector sfdc;
    static SFDCInfo sfdcInfo;
    static String uri;
    static SfdcRestImpl restImpl;
    static ObjectMapper mapper = new ObjectMapper();

    static {
        sfdc = new SalesforceConnector(sfdcConfig.getSfdcUsername(), sfdcConfig.getSfdcPassword()+sfdcConfig.getSfdcStoken(), sfdcConfig.getSfdcPartnerUrl(), sfdcConfig.getSfdcApiVersion());
        sfdc.connect();
        sfdcInfo = sfdc.fetchSFDCinfo();
        uri = sfdcInfo.getEndpoint()+"/services/data/v"+sfdcConfig.getSfdcApiVersion()+"/query?q=";
        restImpl = new SfdcRestImpl(sfdcInfo.getSessionId());

    }

    public static void main(String[] args) throws Exception {
        SfdcRestApi sfdcRestApi = new SfdcRestApi();
        sfdcRestApi.pullDataFromSfdc("Contact", new String[]{"Id", "Name", "Account.Name"}, " limit 10", "./temp/1.csv");
    }

    /**
     * Pull Data from SFDC & return the list Map.
     * @param sObject  - Object to pull the data from.
     * @param fields - Fields to be pulled.
     * @param whereCondition - Any filter condition to be specified.
     * @return list of the records as key value pair's.
     * @throws Exception
     */
    public static List<HashMap<String, String>> pullDataFromSfdc(String sObject, String[] fields, String whereCondition) throws Exception {
        if(sObject==null || fields==null || fields.length <1) {
            throw new IllegalArgumentException("Sobject & fields can't be empty or null");
        }
        System.out.println("Pulling data from sfdc for object "+sObject);
        List<HashMap<String,String>> queryData = new ArrayList<>();
        StringBuffer query = new StringBuffer("Select ");
        for(String field : fields) {
            query.append(field).append(", ");
        }
        query = query.delete(query.length() - 2, query.length()).append(" FROM "+sObject);

        if(whereCondition !=null && !whereCondition.isEmpty()) {
            query.append(" ").append(whereCondition).append(" ");
        }
        System.out.println("Query : " + query);
        ResponseObj responseObj = restImpl.getFromSalesforce(uri + URLEncoder.encode(query.toString(), "UTF-8"));
        JsonNode records =null;
        if(responseObj.getStatusCode() == HttpStatus.SC_OK) {
            JsonNode jsonNode = mapper.readTree(responseObj.getContent());
            records  = jsonNode.get("records");
        } else {
            throw new RuntimeException("Server response is not 200 OK. \n"+responseObj.toString());
        }

        if(records !=null && records.size() >0) {
            for(JsonNode record : records) {
                HashMap<String, String> dataRecord = new HashMap<>();
                for(String field : fields) {
                    if(field.contains(".")) {
                        String[] split = field.split("\\.");
                        JsonNode innerNode  = record.get(split[0]);
                        String val = "";
                        if(innerNode !=null) {
                            val = innerNode.get(split[1]).isNull() ? "" : innerNode.get(split[1]).asText();
                        }
                        dataRecord.put(field, val);

                    } else {
                        dataRecord.put(field, record.get(field).isNull()  ? "" : record.get(field).asText());
                    }
                }
                queryData.add(dataRecord);
            }
        }
        return queryData;
    }

    /** Pull Data from SFDC & write it to a csv file.
    * @param sObject  - Object to pull the data from.
    * @param fields - Fields to be pulled.
    * @param whereCondition - Any filter condition to be specified.
    * @return list of the records as key value pair's.
    * @throws Exception
    */
    public static void  pullDataFromSfdc(String sObject, String[] fields, String whereCondition, String filePath) throws Exception {
        List<HashMap<String, String>> data = pullDataFromSfdc(sObject, fields, whereCondition);
        FileUtil.writeToCSV(data, filePath);
    }


}
