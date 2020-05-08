package com.wei2.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamToStringUtils {
	public static String StreamToString(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int temp = -1;
		while ((temp = is.read(buffer)) != -1) {
			bos.write(buffer, 0, temp);
		}
		return bos.toString();
	}
}
