package com.gainsight.bigdata.util;

import com.gainsight.bigdata.pojo.CollectionInfo;
import com.gainsight.testdriver.Log;

import java.util.HashMap;

/**
 * Created by gainsight on 05/09/15.
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


}
