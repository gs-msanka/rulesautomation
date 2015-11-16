package com.gainsight.bigdata.tenantManagement.enums;

/**
 * Created by gainsight on 08/05/15.
 */
public enum  MDAErrorCodes {
    TENANT_ALREADY_EXIST(3005),
    UN_AUTHORIZED(2401),
    SUBJECT_AREA_ALREADY_EXISTS(3406),
    COLUMN_DEF_NOT_EXISTS(3203),
    DUPLICATE_DISPLAY_NAME(3431),
    DUPLICATE_HEADERS(3254),
    INVALID_MAPPING(3210),
    FILE_SIZE_EXCEEDED_ONE_MB(3256),
    FORMULA_FIELD_SHOULD_NOT_BE_MAPPED(3255),
    VALIDATION_FAILED(3250),
    NO_HEADERS(3252),
    COLUMN_DISPLAY_NAME_NULL(3416);

    private final int code;

    MDAErrorCodes(int code) {
        this.code = code;
    }

    public String getGSCode() {
        return "GS_"+code;
    }
}
