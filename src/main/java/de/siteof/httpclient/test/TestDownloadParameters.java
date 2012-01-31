package de.siteof.httpclient.test;

public class TestDownloadParameters {
	public byte[] data;
	public String contentType;
	public String filename;
	public String path;
	public String redirectPath;
	public String title;
	public int minimumSize;
	public boolean expectFail;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRedirectPath() {
		return redirectPath;
	}

	public void setRedirectPath(String redirectPath) {
		this.redirectPath = redirectPath;
	}

	public int getMinimumSize() {
		return minimumSize;
	}

	public void setMinimumSize(int minimumSize) {
		this.minimumSize = minimumSize;
	}

	public boolean isExpectFail() {
		return expectFail;
	}

	public void setExpectFail(boolean expectFail) {
		this.expectFail = expectFail;
	}
}