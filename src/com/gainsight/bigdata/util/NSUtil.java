package com.gainsight.bigdata.util;

public class NSUtil {

    public static String convertSFID_15TO18(String id) {
        if(id.length() == 18) {
            return id;
        }
        String suffix = "";

        for(int i=0;i<3;i++) {
            Integer flags = 0;
            for(int j=0;j<5;j++) {
                String c = id.substring(i*5+j,i*5+j+1);
                if(c.compareTo("A")  >= 0 && c.compareTo("Z") <= 0){
                    flags += 1 << j;
                }
            }

            if (flags <= 25) {
                suffix += "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(flags,flags+1);
            }else suffix += "012345".substring(flags-26,flags-26+1);
        }
        return id+suffix;
    }

}
