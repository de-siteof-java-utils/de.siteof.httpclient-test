package de.siteof.httpclient.test;

public class HttpClientTester {

	private final TestServer testServer = TestServer.getInstance();

	public TestServer getTestServer() {
		return testServer;
	}

	public String getServerUrl() {
		return getTestServer().getServerUrl();
	}

	public String getServletPath() {
		return getTestServer().getServletPath();
	}

	protected void setServletResponse(TestDownloadParameters parameters) {
		if (parameters.redirectPath != null) {
			TestServlet.setResponseRedirect(parameters.path, getServerUrl() + parameters.redirectPath);
			setServletResponse(parameters.redirectPath, parameters);
		} else {
			setServletResponse(parameters.path, parameters);
		}
	}

	protected void setServletResponse(String path, TestDownloadParameters parameters) {
		TestServlet.setResponseContent(path,
				parameters.data,
				parameters.contentType,
				parameters.filename);
	}

}
