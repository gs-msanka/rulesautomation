package com.gainsight.bigdata.tenantManagement.enums;

/**
 * Created by gainsight on 08/05/15.
 */
public enum  MDAErrorCodes {
    TENANT_ALREADY_EXIST(3005),
    UN_AUTHORIZED(2401);

    private final int code;

    MDAErrorCodes(int code) {
        this.code = code;
    }

    public String getGSCode() {
        return "GS_"+code;
    }
}
