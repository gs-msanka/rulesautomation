package com.gainsight.sfdc.reporting.Pojo;

/**
 * Created by JayaPrakash on 04/02/16.
 */
public class SuccessSnapshotExportMaster {
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    private String namespacePrefix;
    private String accountId;
    private String accountName;
}
