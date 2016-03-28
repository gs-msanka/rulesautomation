package com.gainsight.bigdata.vault.apiImpl;

import com.gainsight.bigdata.pojo.NsResponseObj;
import com.gainsight.bigdata.vault.pojo.Asset;
import com.gainsight.http.Header;
import com.gainsight.http.ResponseObj;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gainsight.bigdata.urls.ApiUrls.*;
import static com.gainsight.bigdata.vault.enums.AssetType.*;

/**
 * Created by Snasika on 21/03/16.
 */
public class AssetApiImpl {


    WebAction wa = new WebAction();
    private ObjectMapper mapper = new ObjectMapper();
    Header header;
    Asset asset;

    public AssetApiImpl(Header header) {
        this.header = header;
    }

    /**
     * Create Asset - API Implementation
     *
     * @param assetDetails - Metadata of the asset
     * @return
     * @throws Exception
     */
    public Asset createAsset(Asset assetDetails) throws Exception {
        NsResponseObj nsResponseObj = null;
        Asset newAssetInfo = null;
        try {
            ResponseObj responseObj = wa.doPost(API_ASSET_CRUD, header.getAllHeaders(), mapper.writeValueAsString(assetDetails));
            Log.info("Response Body ::" + String.valueOf(responseObj));
            if (responseObj.getStatusCode() == HttpStatus.SC_OK) {
                nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
                newAssetInfo = mapper.convertValue(nsResponseObj.getData(), Asset.class);
            }
        } catch (Exception e) {
            Log.error("Exception while creating Asset " + e);
            throw new RuntimeException("Exception while creating Asset " + e);
        }
        return newAssetInfo;
    }

    /**
     * Delete Asset - API Implementation
     *
     * @param assetId - Unique identifier of the asset
     * @return
     * @throws Exception
     */
    public boolean deleteAsset(String assetId) throws Exception {
        NsResponseObj nsResponseObj = null;
        boolean isDeleted = false;
        ResponseObj responseObj = wa.doDelete(String.format(API_ASSET_DELETE, assetId), header.getAllHeaders());
        Log.info("Response Body ::" + String.valueOf(responseObj));
        if(nsResponseObj != null && nsResponseObj.isResult() ) {
            nsResponseObj = mapper.readValue(responseObj.getContent(), NsResponseObj.class);
            isDeleted = nsResponseObj.isResult();
        }
        return isDeleted;
    }

    /**
     * Get all assets available
     *
     * @return
     * @throws Exception
     */
    public List<Asset> getAllAssets() throws Exception {
        String url = API_ASSET_CRUD + "?assetTypes=" + FOLDER.getValue() + "," + SURVEY.getValue() + "," + EMAIL_TEMPLATE.getValue() + "," + RULE.getValue();
        NsResponseObj nsResponseObj = null;
        List<Asset> assetList = null;
        ResponseObj responseObj = wa.doGet(url, header.getAllHeaders());
        Log.info("Response Body ::" + String.valueOf(responseObj));
        if(nsResponseObj != null && nsResponseObj.isResult() && nsResponseObj.getData()!= null) {
            assetList = mapper.convertValue(nsResponseObj.getData(), new TypeReference<List<Asset>>() {
            });
        }
        return assetList;
    }

}
