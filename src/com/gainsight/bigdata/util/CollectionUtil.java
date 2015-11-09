package com.gainsight.bigdata.util;

import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.CollectionInfo.Column;
import com.gainsight.bigdata.pojo.CollectionInfo.LookUpDetail;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.Verifier;
import static org.testng.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.rulesengine.pojo.enums.*;

/**
 * Created by Giribabu on 05/09/15.
 */

public class CollectionUtil {

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


    /**
     * Verifies all the column properties based on the column property.
     * @param expected
     * @param actual
     * @return
     */
    public static boolean verifyColumn(CollectionInfo.Column expected, CollectionInfo.Column actual) {
        boolean result = false;
        Log.info("Verifying column...");
        assertNotNull(expected, "Expected should not be null.");
        assertNotNull(actual, "Actual should not be null.");
        assertNotNull(expected.getDatatype(), "Expected Data Type can't be null.");
        assertNotNull(actual.getDatatype(), "Actual Data Type can't be null.");

        Verifier verifier = new Verifier();
        verifier.verifyEquals(expected.getDisplayName(), actual.getDisplayName(), "Display Name property of column failed");
        verifier.verifyEquals(expected.getDatatype(), actual.getDatatype(), "Data Type property of column failed");
        verifier.verifyEquals(expected.getColumnAttributeType(), actual.getColumnAttributeType(), "Column Attribute Type property of column failed");
        /*
        Need add this in future, now there's a product bug that need to be fixed.
        if(expected.getDatatype().equals("string")) {
            verifier.verifyEquals(expected.getMaxLength(), actual.getMaxLength(), "Max Length property of column failed");
        }*/
        if(expected.getDatatype().equals("number")) {
            verifier.verifyEquals(expected.getDecimalPlaces(), actual.getDecimalPlaces(), "Decimal places property of column failed");
        }
        verifier.verifyEquals(expected.isHidden(), actual.isHidden(), "Hidden property of column failed.");
        verifier.verifyEquals(expected.isIndexed(), actual.isIndexed(), "Hidden property of column failed.");
        verifier.verifyEquals(expected.isDeleted(), actual.isDeleted(), "Hidden property of column failed.");
        verifier.verifyEquals(expected.getDbName(), null, "DBName should not be null.");
        verifier.verifyFalse(expected.getDbName() =="", "DB should not be empty");
        //TODO - Look up information & formula information should also be verified.
        result = verifier.isVerificationFailed();
        if(!result) {
            Log.error("Failed Messages " +verifier.getAssertMessages().toString());
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
        verifier.verifyEquals(expected.getColumns().size(), actual.getColumns().size(), "No of columns doesn't match.");
        boolean result = false;
        for(CollectionInfo.Column expColumn : expected.getColumns()) {
            for(CollectionInfo.Column actualColumn : actual.getColumns()) {
                result = verifyColumn(expColumn, actualColumn);
                if(result) break;
            }
            if(!result) {
                verifier.fail(expColumn.getDisplayName() + " - Column values are not matched.");
            }
            result =false;
        }
        result = verifier.isVerificationFailed();
        if(!result) {
            Log.error("Failed due to : " +verifier.getAssertMessages().toString());
        }
        return result;
    }
    
    
    /**
     * @param collectionInfo - CollectionMaster
     * @param dBName - Column DB Name
     * @return
     */
    public static Column getColumnByDBName(CollectionInfo collectionInfo, String dBName) {
        for(CollectionInfo.Column col : collectionInfo.getColumns()) {
            if(dBName.equals(col.getDbName())) {
                return col;
            }
        }
        throw new RuntimeException("Column details not found in collection info supplied for the DBName : "+dBName);
    }

    
    /**
     * @param baseObject - collectionmaster for which lookup has to be created 
     * @param primaryField - column for which lookup has to be created
     * @param lookUpObject - collectionmaster to  which lookup has to be created 
     * @param foreignField -  column to which lookup has to be created
     * @param useDBName 
     */
    public static void setLookUpDetails(CollectionInfo baseObject, String primaryField, CollectionInfo lookUpObject, String foreignField, boolean useDBName) {
        CollectionInfo.LookUpDetail lookUpDetail = new CollectionInfo.LookUpDetail();
        lookUpDetail.setCollectionId(lookUpObject.getCollectionDetails().getCollectionId());
        lookUpDetail.setDbCollectionName(lookUpObject.getCollectionDetails().getDbCollectionName());
        lookUpDetail.setFieldDBName(useDBName ? getColumnByDBName(lookUpObject, foreignField).getDbName() : getColumnByDisplayName(lookUpObject, foreignField).getDbName());
        if(useDBName) {
        	getColumnByDBName(baseObject, primaryField).setHasLookup(true);
            getColumnByDBName(baseObject, primaryField).setLookupDetail(lookUpDetail);
        } else {
        	getColumnByDisplayName(baseObject, primaryField).setHasLookup(true);
            getColumnByDisplayName(baseObject, primaryField).setLookupDetail(lookUpDetail);
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
			for (Column column : collectionInfo.getColumns()) {
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


}
