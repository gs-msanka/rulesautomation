package com.gainsight.bigdata.util;

import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.bigdata.pojo.CollectionInfo.Column;
import com.gainsight.bigdata.pojo.CollectionInfo.LookUpDetail;
import com.gainsight.testdriver.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gainsight.bigdata.rulesengine.pojo.enums.*;

/**
 * Created by gainsight on 05/09/15.
 */


/**
 * @author Abhilash Thaduka
 *
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
        throw new RuntimeException("Column details not found in collection info supplied for the displayName : "+displayName);
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
