package de.siteof.httpclient.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer {

	private static final Log log = LogFactory.getLog(TestServer.class);

	private static TestServer instance = new TestServer();

	private Server server;
	private int serverPort;
	private String serverUrl;
	private String servletPath;

	public static TestServer getInstance() {
		return instance;
	}

	public boolean isStarted() throws Exception {
		return (server != null);
	}

	public void start() throws Exception {
		if (server == null) {
			doStart();
		}
	}

	public void stop() {
		if (server != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn("sleep interrupted", e);
			}
			try {
				server.stop();
			} catch (Exception e) {
				log.warn("server.stop failed", e);
			}
		}
	}

	private void doStart() throws Exception {
		server = new Server(0);
		server.setHandler(new WebAppContext());
        WebAppContext appContext = new WebAppContext();
        appContext.setContextPath("/");

        appContext.addServlet(TestServlet.class, "/TestServlet");

        appContext.setResourceBase(".");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { appContext, new DefaultHandler() });
        server.setHandler(handlers);

		server.start();
		serverPort = server.getConnectors()[0].getLocalPort();
		serverUrl = "http://localhost:" + serverPort;
		servletPath = "/TestServlet";

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TestServer.this.stop();
			}

		});
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

}
