package com.gainsight.bigdata.pojo;

/**
 * Created by Giribabu on 11/11/15.
 */
public enum  ColumnAttributeType {
    DIMENSION(0),
    MEASURE(1),
    CALCULATED(2);

    private final int code;

    ColumnAttributeType(int code) {
          this.code = code;
    }

    public int getValue(){
        return code;
    }


}
