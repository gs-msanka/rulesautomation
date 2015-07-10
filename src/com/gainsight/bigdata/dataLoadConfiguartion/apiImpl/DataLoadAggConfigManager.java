package com.gainsight.bigdata.dataLoadConfiguartion.apiImpl;


import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.DataAggProcessJobStatus;
import com.gainsight.bigdata.dataLoadConfiguartion.enums.DataAggProcessStatusType;
import com.gainsight.bigdata.dataLoadConfiguartion.pojo.accountdetails.AccountDetail;
import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.http.ResponseObj;
import com.gainsight.sfdc.pages.Constants;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

import static com.gainsight.bigdata.urls.ApiUrls.*;

/**
 * Created by Giribabu on 19/06/15.
 */
public class DataLoadAggConfigManager extends NSTestBase {


    /**
     * Gets all the DataLoad API Projects.
     *
     * @return
     * @throws Exception
     */
    public NsResponseObj getAllDataAPIProjects() throws Exception {
        ResponseObj responseObj = wa.doGet(DATA_API_GET, header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to get all data api projects");
        }
        return nsResponseObj;
    }

    /**
     * Gets the Project/ Data Load Project configuration with project display Name.
     *
     * @param projectName
     * @return
     */
    public AccountDetail getAccountDetailByProjectName(String projectName) {
        if (projectName == null || projectName.equals("")) {
            throw new IllegalArgumentException("Project Name is Mandatory.");
        }
        try {
            NsResponseObj nsResponseObj = getAllDataAPIProjects();
            if (nsResponseObj != null) {
                List<AccountDetail> accountDetailList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<ArrayList<AccountDetail>>() {
                });
                for (AccountDetail accountDetail : accountDetailList) {
                    return accountDetail;
                }
            }
        } catch (Exception e) {
            Log.error("Failed to get All the projects. ", e);
            throw new RuntimeException("Failed to get All the projects. ", e);
        }
        return null;
    }

    /**
     * Delete the account / Data Load Project.
     * @param accountId
     * @return
     * @throws Exception
     */
    public NsResponseObj deleteAccount(String accountId) throws Exception {
        if (accountId == null || accountId.equals("")) {
            Log.error("Account Id should not be null.");
            throw new IllegalArgumentException("Account Id should not be null.");
        }
        ResponseObj responseObj = wa.doDelete(ACCOUNT_DELETE + accountId, header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to get all data api projects");
        }
        return nsResponseObj;
    }

    /**
     * Gets the Project/ Data Load Project configuration with project Account ID.
     * @param accountId
     * @return
     * @throws Exception
     */
    public NsResponseObj getAccountDetail(String accountId) throws Exception {
        if (accountId == null || accountId.equals("")) {
            Log.error("Account Id should not be null.");
            throw new IllegalArgumentException("Account Id should not be null.");
        }
        ResponseObj responseObj = wa.doGet(ACCOUNT_DETAIL_GET + accountId, header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to get all data api projects");
        }
        return nsResponseObj;
    }

    /**
     * Creating/updating the data-load aggregation project.
     *
     * @param payload - JSON of project/Account detail.
     * @param actionType
     * @param accountId
     * @return
     */
    public NsResponseObj manageDataApiProject(String payload, String actionType, String accountId) {
        if (payload == null) {
            Log.error("Payload should not be null.");
            throw new RuntimeException("payload should not be null");
        }
        NsResponseObj nsResponseObj = null;
        try {
            header.addHeader("actionType", actionType);
            ResponseObj responseObj = wa.doPut(String.format(DATA_API_PROJECT_UPDATE_PUT, accountId), payload, header.getAllHeaders());
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            } else {
                throw new RuntimeException("Failed to update data api projects");
            }
        } catch (Exception e) {
            Log.error("Failed to update data api projects ", e);
            throw new RuntimeException("Failed to update data api projects", e);
        } finally {
            header.removeHeader("actionType");
        }
        return nsResponseObj;
    }

    /**
     * Updates the existing account detail / project.
     * @param payload
     * @param actionType
     * @param accountId
     * @return
     */
    public NsResponseObj updateDataApiProject(String payload, String actionType, String accountId) {
        return manageDataApiProject(payload, actionType, accountId);
    }

    /**
     * Creates a new data load aggregated project
     * @param payload
     * @param actionType
     * @return
     */
    public NsResponseObj createDataApiProject(String payload, String actionType) {
        return manageDataApiProject(payload, actionType, "new");
    }

    /**
     * waits for the aggregation job to complete until the max wait time .
     * @param statusId
     * @return - true is job completed in max wait time, else false.
     */
    public boolean waitForAggregationJobToComplete(final String statusId) {
        boolean result = CommonWait.waitForCondition(Constants.MAX_WAIT_TIME, Constants.INTERVAL_TIME, new ExpectedCommonWaitCondition<Boolean>() {
            @Override
            public Boolean apply() {
                return isDataAggregationComplete(statusId);
            }
        });
        return result;
    }

    /**
     * Return true if aggregation is completed, false if aggregation is still in progress.
     * @param statusId
     * @return
     */
    public boolean isDataAggregationComplete(String statusId) {
        boolean result = false;
        NsResponseObj nsResponseObj = null;
        try {
            nsResponseObj = getAggregationJobStatus(statusId);
        } catch (Exception e) {
            Log.error("Failed to get Aggregation Job details " + statusId, e);
            throw new RuntimeException("Failed to get Aggregation Job details " + statusId, e);
        }
        DataAggProcessJobStatus jobStatus = mapper.convertValue(nsResponseObj.getData(), DataAggProcessJobStatus.class);
        if (jobStatus != null && jobStatus.getStatus() != null
                && (jobStatus.getStatus().equals(DataAggProcessStatusType.COMPLETED.name()) || jobStatus.getStatus().equals(DataAggProcessStatusType.FAILED_WHILE_PROCESSING.name()))) {
            result = true;
        }
        return result;
    }

    /**
     * Gets the aggregation job status.
     * @param statusId
     * @return
     * @throws Exception
     */
    public NsResponseObj getAggregationJobStatus(String statusId) throws Exception {
        ResponseObj responseObj = wa.doGet(APP_API_ASYNC_STATUS + statusId, header.getAllHeaders());
        NsResponseObj nsResponseObj = null;
        if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
        } else {
            throw new RuntimeException("Failed to get Aggregation Job Status for Id : " + statusId);
        }
        return nsResponseObj;
    }


}
