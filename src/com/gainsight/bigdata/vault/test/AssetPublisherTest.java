package com.gainsight.bigdata.vault.test;

import com.gainsight.bigdata.NSTestBase;
import com.gainsight.bigdata.vault.enums.AssetType;
import com.gainsight.bigdata.vault.pojo.Asset;
import com.gainsight.bigdata.vault.apiImpl.AssetApiImpl;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.annotations.TestInfo;

import static org.testng.Assert.*;

import org.testng.annotations.*;

import java.util.List;

import static com.gainsight.bigdata.vault.enums.AssetType.*;

/**
 * Created by Snasika on 21/03/16.
 */
public class AssetPublisherTest extends NSTestBase {
    Asset asset = null;
    String DEFAULT_ASSET_ID;
    AssetApiImpl assetApi;

    @BeforeClass
    public void setup() throws Exception {
        assetApi = new AssetApiImpl(header);
        DEFAULT_ASSET_ID = "j3_1";
    }

    @AfterClass
    public void clean() throws Exception {
        Log.info("------ Cleaning Folders ------");
        boolean isDeleted;
        List<Asset> assetList = assetApi.getAllAssets();
        assertNotNull(assetList, "Not able get asserts list to delete");
        for (Asset asset : assetList) {
            Log.info("Deleting Folder :" + asset.getAssetId());
            isDeleted = assetApi.deleteAsset(asset.getAssetId());
            assertTrue(isDeleted, "Folder " + asset.getAssetId() + " deletion failed.");
        }
        assetList = assetApi.getAllAssets();
        if(assetList != null && assetList.size() > 0){
            fail("Few assets still exist. Un-deleted assets : " + mapper.writeValueAsString(assetList));
        }
        Log.info("------ All Folder Deletion Completed ------");
    }


    @TestInfo(testCaseIds = {"GS-230494"})
    @Test
    public void testAddParentFolder() throws Exception {
        String folderName = "testAddParentFolder" + System.currentTimeMillis();
        // Create Folder
        Asset assetInfo = setAssetInfo(folderName, null, FOLDER);
        Asset parentFolder = assetApi.createAsset(assetInfo);
        validateResponseData(parentFolder);
        Log.info("Folder Create Successful. FolderID:" + parentFolder.getAssetId() + ". FolderName:" + parentFolder.getAssetName());

    }

    @TestInfo(testCaseIds = {"GS-230495"})
    @Test
    public void testAddChildFolder() throws Exception {

        String parentFolderName = "testAddChildFolder-Parent" + System.currentTimeMillis();
        String childFolderName = "testAddChildFolder-Child" + System.currentTimeMillis();
        // Create Parent Folder
        Asset assetInfo = setAssetInfo(parentFolderName, null, FOLDER);
        Asset parentFolder = assetApi.createAsset(assetInfo);
        assertNotNull(parentFolder, "Parent folder creation failed");
        assertNotNull(parentFolder.getAssetId(), "Parent folder id is not found");
        Log.info("Parent Folder Create Successful. FolderID:" + parentFolder.getAssetId() + ". FolderName:" + parentFolder.getAssetName());
        // Create Child Folder
        assetInfo = setAssetInfo(childFolderName, parentFolder.getAssetId(), FOLDER);
        Asset childFolder = assetApi.createAsset(assetInfo);
        validateResponseData(childFolder);
        assertEquals(childFolder.getAssetName(), childFolderName, "Expected folder name and created folder name not matching.");
        assertEquals(childFolder.getParent(), parentFolder.getAssetId(), "Wrong parent folder");
        Log.info("Child Folder Creation Successful. FolderID:" + childFolder.getAssetId() + ". FolderName:" + childFolder.getAssetName());

    }

    @TestInfo(testCaseIds = {"GS-230505"})
    @Test
    public void testRenameParentFolder() throws Exception {

        String oldFolderName = "testRenameFolder-OldName" + System.currentTimeMillis();
        String newFolderName = "testRenameFolder-NewName" + System.currentTimeMillis();
        // Create Folder
        Asset assetInfo = setAssetInfo(oldFolderName, null, FOLDER);
        Asset oldFolder = assetApi.createAsset(assetInfo);
        assertNotNull(oldFolder, "Source folder creation failed");
        assertNotNull(oldFolder.getAssetId(), "Source folder id is not found");
        Log.info("Folder Create Successful. FolderID:" + oldFolder.getAssetId() + ". FolderName:" + oldFolder.getAssetName());
        // Rename Folder
        assetInfo = setAssetInfo(newFolderName, null, FOLDER);
        assetInfo.setAssetId(oldFolder.getAssetId());
        Asset newFolder = assetApi.createAsset(assetInfo);
        validateResponseData(newFolder);
        assertEquals(newFolder.getAssetName(), newFolderName, "Old and new folder names not matching");
        assertEquals(newFolder.getAssetId(), oldFolder.getAssetId(), "Old and new folder ids not matching");
        Log.info("Folder Rename Successful. FolderID:" + newFolder.getAssetId() + ". FolderName:" + newFolder.getAssetName());

    }

    @TestInfo(testCaseIds = {"GS-230505"})
    @Test
    public void testRenameChildFolder() throws Exception {

        String parentFolderName = "testRenameChildFolder-ParentFolder" + System.currentTimeMillis();
        String oldFolderName = "testRenameChildFolder-OldName" + System.currentTimeMillis();
        String newFolderName = "testRenameChildFolder-NewName" + System.currentTimeMillis();

        Asset assetInfo = setAssetInfo(parentFolderName, null, FOLDER);
        Asset parentFolder = assetApi.createAsset(assetInfo);
        assertNotNull(parentFolder, "Parent folder creation failed");
        assertNotNull(parentFolder.getAssetId(), "Parent folder id is not found");
        Log.info("Parent Folder Created. FolderID:" + parentFolder.getAssetId() + ". FolderName:" + parentFolder.getAssetName());

        assetInfo = setAssetInfo(oldFolderName, parentFolder.getAssetId(), FOLDER);
        Asset oldFolder = assetApi.createAsset(assetInfo);
        assertNotNull(oldFolder, "Child folder creation failed");
        assertNotNull(oldFolder.getAssetId(), "Child folder id is not found");
        Log.info("Child Folder Creation Successful. FolderID: " + oldFolder.getAssetId() + ". FolderName:" + oldFolder.getAssetName());

        assetInfo = setAssetInfo(newFolderName, parentFolder.getAssetId(), FOLDER);
        assetInfo.setAssetId(oldFolder.getAssetId());
        Asset renamedFolder = assetApi.createAsset(assetInfo);
        validateResponseData(renamedFolder);
        assertEquals(renamedFolder.getAssetName(), newFolderName, "Old and new folder names not matching");
        assertEquals(renamedFolder.getAssetId(), oldFolder.getAssetId(), "Old and new folder ids not matching");
        Log.info("Child Rename Success. FolderID: " + renamedFolder.getAssetId() + ". NewFolderName:" + renamedFolder.getAssetName());

    }

    @TestInfo(testCaseIds = {"GS-230498"})
    @Test
    public void testDeleteFolder() throws Exception {
        String folderName = "testDeleteFolder" + System.currentTimeMillis();
        // Folder Creation
        Asset folderInfo = setAssetInfo(folderName, null, FOLDER);
        Asset folder = assetApi.createAsset(folderInfo);
        assertNotNull(folder, "Folder creation failed");
        assertNotNull(folder.getAssetId(), "Folder id is not found");
        Log.info("Folder Creation Success. FolderID: " + folder.getAssetId() + ". NewFolderName:" + folder.getAssetName());
        // Folder Deletion
        boolean isDeleted = assetApi.deleteAsset(folder.getAssetId());
        assertTrue(isDeleted, "Deletion failed");
        Log.info("Folder Deleted Successfully.");
    }


    /**
     * Generate payload for crate, update, rename of folders
     */
    public Asset setAssetInfo(String newAssetName, String parentID, AssetType assetType) {
        asset = new Asset();
        asset.setAssetId(DEFAULT_ASSET_ID);
        asset.setAssetName(newAssetName);
        asset.setAssetType(assetType.getValue());
        asset.setParent(parentID);
        return asset;
    }

    private void validateResponseData(Asset createdFolder) {
        assertNotNull(createdFolder, "Response data is null");
        assertNotNull(createdFolder.getAssetId(), "Unique folder id is missing in response");
        assertNotNull(createdFolder.getAssetName(), "Folder name is missing in response");
        assertEquals(createdFolder.getAssetType(), FOLDER.getValue(), "Wrong asset type");
    }
}
