package com.gainsight.bigdata.connectors.mapping;

import org.codehaus.jackson.annotate.JsonProperty;

import com.gainsight.bigdata.connectors.enums.Field;


public class SysDefMappingInfo {
	@JsonProperty("source")
	Source source;
	@JsonProperty("target")
	Target target;

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public SysDefMappingInfo(Field source, Field target) {
		this.source = new Source(source);
		this.target = new Target(target);
	}

	public class Source {
		@JsonProperty("type")
		String type;
		@JsonProperty("objectName")
		String objectName;
		@JsonProperty("dbName")
		String dbName;
		@JsonProperty("displayName")
		String displayName;

		public Source(Field field) {
			if (field.equals(Field.SFDC_USERNAME) || field.equals(Field.SFDC_USEREMAIL)
					|| field.equals(Field.SFDC_ACCOUNTNAME)) {
				this.type = "SFDC";
				if (field.equals(Field.SFDC_ACCOUNTNAME)) {
					objectName = "account";
				} else {
					objectName = "contact";
				}
			} else {
				this.type = "USAGE_FEED";
				objectName = null;
			}
			this.dbName = field.getDBName();
			this.displayName = field.getDisplayName();
		}
	}

	public class Target {
		@JsonProperty("dbName")
		String dbName;
		@JsonProperty("displayName")
		String displayName;

		public Target(Field field) {
			this.dbName = field.getDBName();
			this.displayName = field.getDisplayName();
		}

	}
}