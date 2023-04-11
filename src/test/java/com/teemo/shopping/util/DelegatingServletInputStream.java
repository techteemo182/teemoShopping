package com.teemo.shopping.util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DelegatingServletInputStream extends ServletInputStream {
	private int index = 0;
	private byte[] bytes;
	public DelegatingServletInputStream(String str) {
		this.bytes = str.getBytes(StandardCharsets.UTF_8);
	}
	@Override
	public boolean isFinished() {
		return index == this.bytes.length;
	}
	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
	}
	@Override
	public int read() throws IOException {
		if(isFinished()) return -1;
		return bytes[index++];
	}
}