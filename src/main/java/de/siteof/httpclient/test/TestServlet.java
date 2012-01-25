package de.siteof.httpclient.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestServlet extends HttpServlet {

	private static interface ITestServletResponse {

	}

	private static class AbstractTestServletResponse implements ITestServletResponse {
		private Cookie[] cookies;

		public Cookie[] getCookies() {
			return cookies;
		}

		public void setCookies(Cookie[] cookies) {
			this.cookies = cookies;
		}
	}

	private static class TestDataServletResponse extends AbstractTestServletResponse {
		private final byte[] data;
		private final String contentType;
		private final String filename;

		public TestDataServletResponse(byte[] data, String contentType, String filename) {
			this.data = data;
			this.contentType = contentType;
			this.filename = filename;
		}

		public byte[] getData() {
			return data;
		}

		public String getContentType() {
			return contentType;
		}

		public String getFilename() {
			return filename;
		}
	}

	private static class TestRedirectServletResponse extends AbstractTestServletResponse {
		private final String redirectUrl;

		public TestRedirectServletResponse(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}
	}

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(TestServlet.class);

//	private static final ThreadLocal<Map<String, ITestServletResponse>> responseHolder =
//			new ThreadLocal<Map<String, ITestServletResponse>>();

	private static final Map<String, ITestServletResponse> responseMap =
			new HashMap<String, ITestServletResponse>();

	public TestServlet() {
	}

	private static Map<String, ITestServletResponse> getOrCreateResponseMap() {
//		Map<String, ITestServletResponse> responseMap = responseHolder.get();
//		if (responseMap == null) {
//			responseMap = new HashMap<String, ITestServletResponse>();
//			responseHolder.set(responseMap);
//		}
//		return responseMap;
		return TestServlet.responseMap;
	}

	private static synchronized void setResponse(String path, ITestServletResponse response) {
		getOrCreateResponseMap().put(path, response);
	}

	private static synchronized ITestServletResponse getResponse(String path) {
		return getOrCreateResponseMap().get(path);
	}

	public static synchronized void clearResponses() {
		getOrCreateResponseMap().clear();
	}

	public static void setResponseContent(String path, byte[] data, String contentType,
			String filename) {
		setResponse(path, new TestDataServletResponse(data, contentType, filename));
	}

	public static void setResponseContent(String path, byte[] data, String contentType) {
		setResponseContent(path, data, contentType, null);
	}

	public static void setResponseRedirect(String path, String redirectUrl) {
		setResponse(path, new TestRedirectServletResponse(redirectUrl));
	}

	public static Cookie[] getLastRequestCookies(String path) {
		Cookie[] cookies = null;
		ITestServletResponse response = getResponse(path);
		if (response instanceof AbstractTestServletResponse) {
			cookies = ((AbstractTestServletResponse) response).getCookies();
		}
		return cookies;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//super.doGet(req, resp);
		Cookie[] cookies = request.getCookies();
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		if (log.isDebugEnabled()) {
			log.debug("uri=[" + uri + "], queryString=[" + queryString + "]");
		}

		String uriWithQueryString = uri;
		if (!StringUtils.isEmpty(queryString)) {
			uriWithQueryString += "?" + queryString;
		}

		ITestServletResponse testResponse = getResponse(uriWithQueryString);
		if (testResponse == null) {
			throw new IOException("no response mapped for uri: " + uriWithQueryString);
		} else {
			if (testResponse instanceof TestDataServletResponse) {
				TestDataServletResponse dataResponse = (TestDataServletResponse) testResponse;
				dataResponse.setCookies(cookies);
				String contentType = dataResponse.getContentType();
				String filename = dataResponse.getFilename();
				byte[] data = dataResponse.getData();
				if (contentType != null) {
					response.setContentType(contentType);
				}
				response.setContentLength(data.length);
				if (filename != null) {
					response.setHeader("Content-Disposition",
							"attachment; filename=\"" + filename + "\"");
				}
				OutputStream out = response.getOutputStream();
				out.write(data);
				out.flush();
			} else if (testResponse instanceof TestRedirectServletResponse) {
				TestRedirectServletResponse redirectResponse = (TestRedirectServletResponse) testResponse;
				redirectResponse.setCookies(cookies);
				String redirectUrl = redirectResponse.getRedirectUrl();
				response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				response.setHeader("Location", redirectUrl);
			} else {
				throw new IOException("invalid response - " + testResponse);
			}
		}
	}

}
