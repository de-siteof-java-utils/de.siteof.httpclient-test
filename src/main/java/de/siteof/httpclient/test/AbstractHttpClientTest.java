package de.siteof.httpclient.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

public class AbstractHttpClientTest {

//	private static final Log log = LogFactory.getLog(AbstractHttpClientTest.class);

	private final HttpClientTester tester;

	public AbstractHttpClientTest() {
		tester = new HttpClientTester();
	}

	public HttpClientTester getTester() {
		return tester;
	}

	public String getServerUrl() {
		return tester.getServerUrl();
	}

	@Before
	public void beforeTest() throws Exception {
		if (!tester.getTestServer().isStarted()) {
			tester.getTestServer().start();
		}
	}

	@After
	public void afterTest() throws Exception {
		TestServlet.clearResponses();
	}

	public static void assertEquals(String message, byte[] expected, byte[] actual) {
		if (!Arrays.equals(expected, actual)) {
			Assert.fail(message + ".length, expected=[" + expected.length +
					"], actual=[" + actual.length + "]");
			Assert.fail(message + ", expected=[" + new String(expected) + "], actual=[" + new String(actual) + "]");
		}
	}

	protected void setServletResponse(TestDownloadParameters parameters) {
		getTester().setServletResponse(parameters);
	}

	protected void setServletResponse(String path, TestDownloadParameters parameters) {
		getTester().setServletResponse(path, parameters);
	}

	protected TestDownloadParameters getTestParameters() {
		TestDownloadParameters result = new TestDownloadParameters();
		result.data = "Test Content 123".getBytes();
		result.contentType = "text/plain";
		result.filename = "my file.txt";
		result.path = getTester().getServletPath();
		return result;
	}

	protected byte[] getBinaryData(int length) {
		byte[] data = new byte[length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
		return data;
	}

	protected byte[] getStringData(int length) {
		return getStringData("abcdefghij012345689", length);
	}

	protected byte[] getStringData(String s, int length) {
		byte[] data = new byte[length];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) s.charAt(i % s.length());
		}
		return data;
	}

	protected TestDownloadParameters withRedirect(TestDownloadParameters parameters) {
		parameters.redirectPath = this.getTester().getServletPath() + "?x";
		return parameters;
	}

	protected TestDownloadParameters withData(TestDownloadParameters parameters, byte[] data) {
		parameters.data = data;
		return parameters;
	}

	protected TestDownloadParameters withBinaryData(TestDownloadParameters parameters, int length) {
		return withData(parameters, getBinaryData(length));
	}

}
