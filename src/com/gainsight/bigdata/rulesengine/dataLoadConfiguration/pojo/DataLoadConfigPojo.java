/**
 * 
 */
package com.gainsight.bigdata.rulesengine.dataLoadConfiguration.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abhilash Thaduka
 *
 */
public class DataLoadConfigPojo {

	private String configureName;
	private List<LoadableObjects> loadableObjects = new ArrayList<LoadableObjects>();

	public String getConfigureName() {
		return configureName;
	}

	public void setConfigureName(String configureName) {
		this.configureName = configureName;
	}

	public List<LoadableObjects> getLoadableObjects() {
		return loadableObjects;
	}

	public void setLoadableObjects(List<LoadableObjects> loadableObjects) {
		this.loadableObjects = loadableObjects;
	}

}
