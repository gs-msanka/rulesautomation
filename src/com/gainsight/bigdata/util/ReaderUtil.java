package com.gainsight.bigdata.util;

import java.io.IOException;
import java.io.Reader;

public class ReaderUtil {
	public static void readContent(Reader reader, StringBuffer buf)
			throws IOException {
		char[] buffer = new char[8*1024];
		int len = 0;
		while((len = reader.read(buffer)) != -1) {
			buf.append(buffer, 0, len);
		}
	}
}
