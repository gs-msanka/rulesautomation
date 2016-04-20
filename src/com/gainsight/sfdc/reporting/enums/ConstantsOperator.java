package com.gainsight.sfdc.reporting.enums;

public class ConstantsOperator {

    public static enum SFDCOperator {
        e("equals"), n("not equals"), ge("greater or equal"), s("starts with"), g("greater than"), le("less or equal"), l(
                "less than"), OPERA("Opera"), FIREFOX("Fireforx");

        String sFDCOperator;

        SFDCOperator(String sFDCOperator) {
            this.sFDCOperator = sFDCOperator;
        }

        public String getSFDCOperator() {
            return sFDCOperator;
        }
    }
}
