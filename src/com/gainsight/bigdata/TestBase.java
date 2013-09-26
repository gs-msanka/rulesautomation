package com.gainsight.bigdata;

import com.gainsight.bigdata.pojo.Header;
import com.gainsight.bigdata.pojo.NSInfo;
import com.gainsight.bigdata.pojo.SFDCInfo;
import com.gainsight.bigdata.util.NSUtil;
import com.gainsight.bigdata.util.PropertyReader;
import com.gainsight.bigdata.util.SFDCUtil;

public class TestBase {

	public SFDCInfo sfinfo;
	public NSInfo nsinfo;
	public WebAction wa;
	public Header h;
	public String testDataBasePath = PropertyReader.baseDir + "/testdata/newstack";
	public String origin = "https://test.na15.force.com";
	
	public void init() throws Exception {
		sfinfo = SFDCUtil.fetchSFDCinfo();
		nsinfo = NSUtil.fetchNewStackInfo(sfinfo);
		wa = new WebAction();

		h = new Header();
		h.addHeader("Content-Type", "application/json");
		h.addHeader("authToken", nsinfo.getAuthToken());
		h.addHeader("Origin", "https://c.na13.force.com");
	}
}
