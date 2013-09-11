package com.gainsight.bigdata.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionInfo {

	@JsonProperty("TenantName")
	String tenantName;
	@JsonProperty("TenantId")
	String tenantId;
	@JsonProperty("Columns")
	List<Columns> columns;
	
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public List<Columns> getColumns() {
		return columns;
	}
	public void setColumns(List<Columns> columns) {
		this.columns = columns;
	}

	public class Columns {
		String name;
		String datatype;
		int hide;
		int indexable;
		int colattribtype;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDatatype() {
			return datatype;
		}
		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}
		public int getHide() {
			return hide;
		}
		public void setHide(int hide) {
			this.hide = hide;
		}
		public int getIndexable() {
			return indexable;
		}
		public void setIndexable(int indexable) {
			this.indexable = indexable;
		}
		public int getColattribtype() {
			return colattribtype;
		}
		public void setColattribtype(int colattribtype) {
			this.colattribtype = colattribtype;
		}
	}
}
