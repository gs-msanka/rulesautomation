package com.gainsight.bigdata.vault.enums;

/**
 * Created by Snasika on 21/03/16.
 */
public enum AssetType {
    FOLDER("FOLDER"), PLAYBOOK("PLAYBOOK"), SURVEY("SURVEY"), EMAIL_TEMPLATE("EMAIL_TEMPLATE"), RULE("RULE");
    String value;

    AssetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
