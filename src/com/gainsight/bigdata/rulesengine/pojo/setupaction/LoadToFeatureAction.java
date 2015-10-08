package com.gainsight.bigdata.rulesengine.pojo.setupaction;

/**
 * Created by vmenon on 9/13/2015.
 */
public class LoadToFeatureAction {
    private String product = "";
    private String feature = "";
    private String comments = "";
    private Licensed licensed;
    private Enabled enabled;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Licensed getLicensed() {
        return licensed;
    }

    public void setLicensed(Licensed licensed) {
        this.licensed = licensed;
    }

    public Enabled getEnabled() {
        return enabled;
    }

    public void setEnabled(Enabled enabled) {
        this.enabled = enabled;
    }

    public static class Licensed {
        public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUpdateType() {
			return updateType;
		}
		public void setUpdateType(String updateType) {
			this.updateType = updateType;
		}
		private String type = "";
        private String updateType = "";
    }

    public static class Enabled {
        public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUpdateType() {
			return updateType;
		}
		public void setUpdateType(String updateType) {
			this.updateType = updateType;
		}
		private String type = "";
        private String updateType = "";
    }
}
