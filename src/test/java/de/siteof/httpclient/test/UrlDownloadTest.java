package de.siteof.httpclient.test;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import de.siteof.resource.util.IOUtil;

public class UrlDownloadTest extends AbstractHttpClientTest {

	private static final Log log = LogFactory.getLog(UrlDownloadTest.class);

	@Test
	public void testDownloadUrlStream() throws Exception {
		TestDownloadParameters parameters = getTestParameters();
		setServletResponse(parameters);

		String url = getServerUrl() + parameters.path;
		URL url1 = new URL(url);
		InputStream in = url1.openStream();
		try {
			byte[] actualData = IOUtil.readAllFromStream(in);
			log.info("data: " + new String(actualData));
			assertEquals("data", parameters.data, actualData);
		} finally {
			in.close();
		}
	}
	@Test
	public void testDownloadUrlStreamLargeString() throws Exception {
		TestDownloadParameters parameters = withData(getTestParameters(), getStringData(10 * 1024));
		setServletResponse(parameters);

		String url = this.getServerUrl() + parameters.path;
		URL url1 = new URL(url);
		InputStream in = url1.openStream();
		try {
			byte[] actualData = IOUtil.readAllFromStream(in);
			//log.info("data: " + new String(actualData));
			assertEquals("data", parameters.data, actualData);
		} finally {
			in.close();
		}
	}

}
