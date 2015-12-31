package com.gainsight.bigdata.gsData.pojos;

/**
 * Created by Giribabu on 19/08/15.
 */
public class COMMetadata {

    private int limit = 50;
    private int skip = 0;
    private WhereAdvanceFilter whereAdvanceFilter;
    private String[] includeFields = {"CollectionDetails", "CollectionDescription", "createdByName", "createdDate", "modifiedByName", "modifiedDate", "Columns"};

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public WhereAdvanceFilter getWhereAdvanceFilter() {
        return whereAdvanceFilter;
    }

    public void setWhereAdvanceFilter(WhereAdvanceFilter whereAdvanceFilter) {
        this.whereAdvanceFilter = whereAdvanceFilter;
    }

    public String[] getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(String[] includeFields) {
        this.includeFields = includeFields;
    }

    public static class WhereAdvanceFilter {
        private COMFilters[] filters;
        private String expression;

        public COMFilters[] getFilters() {
            return filters;
        }

        public void setFilters(COMFilters[] filters) {
            this.filters = filters;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }


}
