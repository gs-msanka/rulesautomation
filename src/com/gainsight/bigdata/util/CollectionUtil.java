package com.gainsight.bigdata.util;

import com.gainsight.bigdata.dataload.enums.DataLoadOperationType;
import com.gainsight.bigdata.dataload.pojo.DataLoadMetadata;
import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.ColumnAttributeType;
import com.gainsight.bigdata.rulesengine.pojo.enums.RedShiftFormulaType;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.Verifier;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.codehaus.jackson.map.ObjectMapper;

import static org.testng.Assert.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Giribabu on 05/09/15.
 */
public class CollectionUtil {

    static ObjectMapper mapper = new ObjectMapper();

    /**
     * Return the hash map of display names and db names of a collection columns.
     *
     * @param collectionInfo - Collection master of a subject area.
     * @return - Hash Map with display names as keys and db names has values.
     */
    public static HashMap<String, String> getDisplayAndDBNamesMap(CollectionInfo collectionInfo) {
        if(collectionInfo == null || collectionInfo.getColumns() == null) {
            throw new IllegalArgumentException("Collection info & Columns List can't be null");
        }
        HashMap<String, String> resultMap = new HashMap<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            resultMap.put(column.getDisplayName(), column.getDbName());
        }

        Log.info("Result Map : " + resultMap);
        return resultMap;
    }

    /**
     * Returns db names & display names as map.
     * @param collectionInfo
     * @return
     */
    public static HashMap<String, String> getDBNamesAndDisplayNameMap(CollectionInfo collectionInfo) {
        if(collectionInfo == null || collectionInfo.getColumns() == null) {
            throw new IllegalArgumentException("Collection info & Columns List can't be null");
        }
        HashMap<String, String> resultMap = new HashMap<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            resultMap.put(column.getDbName(), column.getDisplayName());
        }

        Log.info("Result Map : " + resultMap.toString());
        return resultMap;
    }


    /**
     * Returns Display Names Columns Map.
     * @param collectionInfo
     * @return
     */
    public static HashMap<String, CollectionInfo.Column> getDisplayNameColumnsMap(CollectionInfo collectionInfo) {
        if(collectionInfo == null || collectionInfo.getColumns() == null) {
            throw new IllegalArgumentException("Collection info & Columns List can't be null");
        }
        HashMap<String, CollectionInfo.Column> resultMap = new HashMap<>();
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            resultMap.put(column.getDisplayName(), column);
        }

        Log.info("Result Map : " + resultMap);
        return resultMap;
    }

    /**
     * Returns the first column matched with display order.
     * @param collectionInfo - CollectionMaster
     * @param displayName -  Column Display Name.
     * @return
     */
    public static CollectionInfo.Column getColumnByDisplayName(CollectionInfo collectionInfo, String displayName) {
        for(CollectionInfo.Column col : collectionInfo.getColumns()) {
            if(displayName.equals(col.getDisplayName())) {
                return col;
            }
        }
        throw new RuntimeException("Column matching the displayName : "+displayName+ " not found.");
    }


    public static CollectionInfo.Column getColumnByDBName(CollectionInfo collectionInfo, String dBName) {
        for(CollectionInfo.Column col : collectionInfo.getColumns()) {
            if(dBName.equals(col.getDbName())) {
                return col;
            }
        }
        throw new RuntimeException("Column matching the displayName : "+dBName+ " not found.");
    }

    /**
     * Create a collection column of type String.
     * @param displayName
     * @return
     */
    public static CollectionInfo.Column createStringColumn(String displayName) {
        Log.info("Creating string column...");
        CollectionInfo.Column column = new CollectionInfo.Column();
        column.setDatatype("string");
        column.setDisplayName(displayName);
        column.setColumnAttributeType(0);  //String is dimension.
        return column;
    }

    /**
     * Create a collection column of type boolean.
     * @param displayName
     * @return
     */
    public static CollectionInfo.Column createBooleanColumn(String displayName) {
        Log.info("Creating boolean column...");
        CollectionInfo.Column column = new CollectionInfo.Column();
        column.setDatatype("boolean");
        column.setDisplayName(displayName);
        column.setColumnAttributeType(0);  //Boolean is dimension.
        return column;
    }

    /**
     * Create a collecion column of type number.
     * @param displayName
     * @param decimalPlaces
     * @return
     */
    public static CollectionInfo.Column createNumberColumn(String displayName, int decimalPlaces) {
        Log.info("Creating number column...");
        CollectionInfo.Column column = new CollectionInfo.Column();
        column.setDatatype("number");
        column.setDisplayName(displayName);
        column.setDecimalPlaces(decimalPlaces);
        column.setColumnAttributeType(1);  //Number is a measure.
        return column;
    }

    /**
     * Create a collection column of type Date.
     * @param displayName
     * @param isDateTime
     * @return
     */
    public static CollectionInfo.Column createDateColumn(String displayName, boolean isDateTime) {
        Log.info("Creating date/datetime column...");
        CollectionInfo.Column column = new CollectionInfo.Column();
        column.setDatatype(isDateTime ? "dateTime" : "date");
        column.setDisplayName(displayName);
        column.setColumnAttributeType(0);  //Date is a dimension.
        return column;
    }

    public static CollectionInfo.Column createLookUpColumn(String displayName, CollectionInfo lookUpObject, String lookUpField) {
        CollectionInfo.Column column = createStringColumn(displayName);
        CollectionInfo.LookUpDetail lookUpDetail = new CollectionInfo.LookUpDetail();
        lookUpDetail.setCollectionId(lookUpObject.getCollectionDetails().getCollectionId());
        lookUpDetail.setDbCollectionName(lookUpObject.getCollectionDetails().getDbCollectionName());
        lookUpDetail.setFieldDBName(getColumnByDisplayName(lookUpObject, lookUpField).getDbName());
        column.setLookupDetail(lookUpDetail);
        return column;
    }

    /**
     * Remove a column from a collection based on display name of the column.
     * @param collectionInfo
     * @param fieldDisplayName
     * @return
     */
    public static boolean removeColumnFromCollection(CollectionInfo collectionInfo, String fieldDisplayName) {
        Log.info("Trying to delete column from collection...");
        CollectionInfo.Column column = null;
        for(int i=0; i < collectionInfo.getColumns().size(); i++) {
            if(collectionInfo.getColumns().get(i).getDisplayName().equals(fieldDisplayName)) {
                column = collectionInfo.getColumns().get(i);
                break;
            }
        }
        if(column !=null) {
            return collectionInfo.getColumns().remove(column);
        }
        Log.error("Column with the display name not found : " + fieldDisplayName);
        return false;
    }

    /**
     * Update the display Name of column.
     * @param collectionInfo
     * @param displayName
     * @param newDisplayName
     * @return
     */
    public static boolean changeDisplayNameOfColumn(CollectionInfo collectionInfo, String displayName, String newDisplayName) {
        Log.info("Trying to change the display name of column...");
        CollectionInfo.Column column = getColumnByDisplayName(collectionInfo, displayName);
        column.setDisplayName(newDisplayName);
        return true;
    }

    public DataLoadMetadata getDBDataLoadMetaData(CollectionInfo collectionInfo, DataLoadOperationType loadType) {
        DataLoadMetadata metadata = new DataLoadMetadata();
        metadata.setCollectionName(collectionInfo.getCollectionDetails().getCollectionId());
        metadata.setDataLoadOperation(loadType.name());
        metadata.setDbNameUsed(true);

        List<DataLoadMetadata.Mapping> mappings = new ArrayList<>();
        DataLoadMetadata.Mapping mapping = null;
        for(CollectionInfo.Column column : collectionInfo.getColumns()) {
            mapping = new DataLoadMetadata.Mapping();
            mapping.setSource(column.getDisplayName());
            mapping.setTarget(column.getDbName());
            mappings.add(mapping);
        }
        metadata.setMappings(mappings);
        return metadata;
    }

    public DataLoadMetadata getDBDataLoadMetaData(CollectionInfo collectionInfo, String[] fields, DataLoadOperationType loadType) {
        DataLoadMetadata metadata = new DataLoadMetadata();
        metadata.setCollectionName(collectionInfo.getCollectionDetails().getCollectionId());
        metadata.setDataLoadOperation(loadType.name());
        metadata.setDbNameUsed(true);

        List<DataLoadMetadata.Mapping> mappings = new ArrayList<>();
        DataLoadMetadata.Mapping mapping = null;
        for(String field : fields) {
            CollectionInfo.Column column = CollectionUtil.getColumnByDisplayName(collectionInfo, field);
            mapping = new DataLoadMetadata.Mapping();
            mapping.setSource(column.getDisplayName());
            mapping.setTarget(column.getDbName());
            mappings.add(mapping);
        }
        metadata.setMappings(mappings);
        return metadata;
    }


    /**
     * Verifies all the column properties based on the column property.
     * @param expected
     * @param actual
     * @return
     */
    public static boolean verifyColumn(CollectionInfo.Column expected, CollectionInfo.Column actual) {
        boolean result = false;
        assertNotNull(expected, "Expected should not be null.");
        Log.info("Verifying column..." +expected.getDisplayName());
        assertNotNull(actual, "Actual should not be null.");
        assertNotNull(expected.getDatatype(), "Expected Data Type can't be null.");
        assertNotNull(actual.getDatatype(), "Actual Data Type can't be null.");

        Verifier verifier = new Verifier();
        verifier.verifyEquals(expected.getDisplayName(), actual.getDisplayName(), "Display Name property of column failed");
        verifier.verifyEquals(expected.getDatatype(), actual.getDatatype(), "Data Type property of column failed");
        verifier.verifyEquals(expected.getColumnAttributeType(), actual.getColumnAttributeType(), "Column Attribute Type property of column failed");
        /*
        Need to add this in future, now there's a product bug that need to be fixed.
        if(expected.getDatatype().equals("string")) {
            verifier.verifyEquals(expected.getMaxLength(), actual.getMaxLength(), "Max Length property of column failed");
        }*/
        if(expected.getDatatype().equals("number")) {
            verifier.verifyEquals(expected.getDecimalPlaces(), actual.getDecimalPlaces(), "Decimal places property of column failed");
        }
        verifier.verifyEquals(expected.isHidden(), actual.isHidden(), "Hidden property of column not matched.");
        verifier.verifyEquals(expected.isIndexed(), actual.isIndexed(), "Indexed property of column not matched.");
        verifier.verifyEquals(expected.isDeleted(), actual.isDeleted(), "Deleted property of column not matched.");
        verifier.verifyFalse(actual.getDbName() == null, "DBName should not be null.");
        verifier.verifyFalse(actual.getDbName() == "", "DB should not be empty");

        if(expected.getLookupDetail() !=null) {
            Log.info("Verifying lookup Details...");
            if(actual.getLookupDetail() !=null) {
                verifier.verifyEquals(expected.getLookupDetail().getCollectionId(), actual.getLookupDetail().getCollectionId());
                verifier.verifyEquals(expected.getLookupDetail().getDbCollectionName(), actual.getLookupDetail().getDbCollectionName());
                verifier.verifyEquals(expected.getLookupDetail().getFieldDBName(), actual.getLookupDetail().getFieldDBName());
            } else {
                verifier.fail("Actual lookup details are null.");
            }
        }

        if(expected.getFormula() !=null && !expected.getFormula().isEmpty()) {
            try {
                JsonFluentAssert.assertThatJson(expected.getFormula()).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(actual.getFormula());
            } catch (AssertionError assertionError) {
                verifier.fail("Formula values didn't match, " +assertionError.getLocalizedMessage());
            }
        }

        if(expected.getCalculatedExpression() != null && !expected.getCalculatedExpression().isEmpty()) {
            verifier.verifyEquals(expected.getCalculatedExpression(), actual.getCalculatedExpression(), "Calculated Expression not matched.");
        }

        CollectionInfo.MappingsSFDC sfdcMapping = expected.getMappings();
        if(sfdcMapping !=null && sfdcMapping.getSfdc()!=null) {
            assertNotNull(actual.getMappings(), "SFDC mapping in actual should not be null.");
            verifier.verifyTrue(sfdcMapping.getSfdc().equals(actual.getMappings().getSfdc()), "SFDC mapping is not matched.");
        }
        result = !verifier.isVerificationFailed();
        if(!result) {
            Log.error("Failed due to : " +expected.getDisplayName() +" - "+verifier.getAssertMessages().toString());
        }
        return result;
    }

    /**
     * Verifies the Collection Columns, Column Data Type and checks DB Name is not null in actual collection.
     *
     * @param expected - Expected Collection Info.
     * @param actual - Actual Collection Info.
     */
    public static boolean verifyCollectionInfo(CollectionInfo expected, CollectionInfo actual) {
        assertNotNull(expected, "Expected Collection info should not be null.");
        assertNotNull(actual, "Actual Collection info should not be null.");
        Verifier verifier = new Verifier();
        verifier.verifyEquals(expected.getCollectionDetails().getCollectionName(), actual.getCollectionDetails().getCollectionName(), "Collection names not matched.");
        verifier.verifyEquals(expected.getCollectionDetails().getDataStoreType(), actual.getCollectionDetails().getDataStoreType(), "DB Store Type not matched.");
        verifier.verifyEquals(expected.getColumns().size(), actual.getColumns().size(), "No of columns doesn't match.");
        boolean result = false;
        for(CollectionInfo.Column expColumn : expected.getColumns()) {
            CollectionInfo.Column column = getColumnByDisplayName(actual, expColumn.getDisplayName());
            if(column == null) {
                verifier.fail(expColumn.getDisplayName() + " - Column Not found in actual collection");
                result = true; //Just to make sure it doesn't add one more message to verifier.
            } else {
                result = verifyColumn(expColumn, column);
            }

            if (!result) {
                verifier.fail(expColumn.getDisplayName() + " - Column values are not matched.");
            }
            result =false;
        }

        result = !verifier.isVerificationFailed();
        if(!result) {
            Log.error("Failed due to : " +verifier.getAssertMessages().toString());
        }
        return result;
    }

    /**
     * @param baseObject - collectionmaster on which lookup field has to be created
     * @param primaryField - Lookup field name.
     * @param lookUpObject - collectionmaster to which lookup has to be created
     * @param foreignField -  column to which lookup has to be created
     * @param useDBName
     */
    public static void setLookUpDetails(CollectionInfo baseObject, String primaryField, CollectionInfo lookUpObject, String foreignField, boolean useDBName) {
        CollectionInfo.LookUpDetail lookUpDetail = new CollectionInfo.LookUpDetail();
        lookUpDetail.setCollectionId(lookUpObject.getCollectionDetails().getCollectionId());
        lookUpDetail.setDbCollectionName(lookUpObject.getCollectionDetails().getDbCollectionName());
        lookUpDetail.setFieldDBName(useDBName ? getColumnByDBName(lookUpObject, foreignField).getDbName() : getColumnByDisplayName(lookUpObject, foreignField).getDbName());
        if(useDBName) {
        	CollectionInfo.Column column=getColumnByDBName(baseObject, primaryField);
        	column.setHasLookup(true);
        	column.setLookupDetail(lookUpDetail);
        } else {
        	CollectionInfo.Column column=getColumnByDisplayName(baseObject, primaryField);
        	column.setHasLookup(true);
        	column.setLookupDetail(lookUpDetail);
        }
    }


	/**
	 * @param collectionInfo - collectionmaster on which calculatedExpression has to be created
	 * @param columnName - column for whih calculated measure has to be created
	 * @param column1
	 * @param column2
	 * @param column3
	 * @param formula - RedShiftFormulaType
	 * @return
	 */
	public static CollectionInfo getcalculatedExpression(CollectionInfo collectionInfo, String columnName,
			String column1, String column2, String column3, RedShiftFormulaType formula){
		switch (formula) {
		case FORMULA1:
			for (CollectionInfo.Column column : collectionInfo.getColumns()) {
				if (column.getDisplayName().equals(columnName)) {
					column.setCalculatedExpression("(" + column1 + "+"
							+ column2 + ")" + "*" + column3);
				}
			}
			break;
		default:
			break;
		}
		return collectionInfo;
	}

    public static void tokenizeCalculatedExpression(CollectionInfo collectionInfo, String[] columns) {
        if(collectionInfo ==null) {
            throw new RuntimeException("Colleciton info can't be null.");
        }

        HashMap<String, CollectionInfo.Column> columnHashMap = getDisplayNameColumnsMap(collectionInfo);
        if(columns == null || columns.length <1) {
            columns = columnHashMap.keySet().toArray(new String[columnHashMap.keySet().size()]);
        }
        for(String columnName : columns) {
            CollectionInfo.Column column = columnHashMap.get(columnName);
            if(column ==null) {
                throw new RuntimeException("Column not found :" +columnName);
            }
            if(column.getColumnAttributeType()!= ColumnAttributeType.CALCULATED.getValue()) {
                Log.info("Not a calculated field "+columnName);
                continue;
            }
            Log.info("Building Calculated expression for : "+columnName);
            List<String> values = getAllDisplayNamesFromCalculatedExpression(column.getCalculatedExpression());
            for(String value : values) {
                CollectionInfo.Column tempColumn = columnHashMap.get(value.substring(2, value.length()-1));
                if(tempColumn ==null) {
                    throw new RuntimeException("Column not found :" +value.substring(2, value.length()-1));
                }
                Log.info("Replacing : " +value + " With " +tempColumn.getDbName());
                column.setCalculatedExpression(column.getCalculatedExpression().replace(value, tempColumn.getDbName()));
            }
        }
    }

    public static List<String> getAllDisplayNamesFromCalculatedExpression(String text) {
        if(text ==null) {
            throw new IllegalArgumentException("Text parameters should not be null.");
        }
        List<String> values = new ArrayList<>();
        String regex = "\\$\\{.+?\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String value = matcher.group();
            values.add(value);
        }
        Log.info("Column List : " + values.toString());
        return values;
    }

    public static HashMap<String, CollectionInfo.Column> getDisplayNameColumnsMap(List<CollectionInfo.Column> columns) {
        HashMap<String, CollectionInfo.Column> result = new HashMap<>();
        for(CollectionInfo.Column column : columns) {
            result.put(column.getDisplayName(), column);
        }
        return result;
    }




}
