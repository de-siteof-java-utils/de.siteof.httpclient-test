package de.siteof.httpclient.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.siteof.resource.IResource;
import de.siteof.resource.IResourceLoader;
import de.siteof.resource.ResourceDownloader;
import de.siteof.resource.event.IResourceListener;
import de.siteof.resource.event.ResourceLoaderEvent;
import de.siteof.resource.util.IOUtil;

public class AbstractResourceDownloaderTest extends AbstractHttpClientTest {

	private static final Log log = LogFactory.getLog(AbstractResourceDownloaderTest.class);

	protected void doTestResourceDownloader(IResourceLoader resourceLoader, TestDownloadParameters parameters) throws Exception {
		String url = this.getServerUrl() + parameters.path;

		final AtomicReference<TestByteArrayOutputStream> outHolder =
				new AtomicReference<TestByteArrayOutputStream>();
		final AtomicReference<String> filenameHolder = new AtomicReference<String>();

		ResourceDownloader downloader = new ResourceDownloader(resourceLoader, url) {
			@Override
			public OutputStream createOutputStream() {
				filenameHolder.set(this.getFilename());
				TestByteArrayOutputStream out = new TestByteArrayOutputStream();
				outHolder.set(out);
				return out;
			}
		};
		downloader.setMinimumSize(parameters.minimumSize);
		downloader.download();
		boolean result = downloader.waitForDownload();
		if (parameters.expectFail) {
			Assert.assertFalse("downloadResult", result);
		} else {
			Assert.assertTrue("downloadResult", result);
			TestByteArrayOutputStream out = outHolder.get();
			Assert.assertTrue("closed", out.isClosed());
			byte[] actualData = out.toByteArray();
			assertEquals("data", parameters.data, actualData);
			Assert.assertEquals("filename", parameters.filename, filenameHolder.get());
		}
	}

	protected void doTestResourceDownloaderFile(IResourceLoader resourceLoader, TestDownloadParameters parameters,
			String expectedFilename) throws Exception {
		String url = this.getServerUrl() + parameters.path;

		ResourceDownloader downloader = new ResourceDownloader(resourceLoader, url);
		downloader.setDownloadDirectory(new File("."));
		downloader.setTitle(parameters.title);

		File outputFile = new File(downloader.getDownloadDirectory(), expectedFilename);
		if (outputFile.exists()) {
			outputFile.delete();
		}
		downloader.setMinimumSize(parameters.minimumSize);
		downloader.download();
		boolean result = downloader.waitForDownload();
		Assert.assertTrue("downloadResult", result);
		Assert.assertEquals("resourceName", expectedFilename, downloader.getResourceName());
		Assert.assertTrue("outputFile.exists", outputFile.exists());
		byte[] actualData;
		FileInputStream in = new FileInputStream(outputFile);
		try {
			actualData = IOUtil.readAllFromStream(in);
		} finally {
			in.close();
		}
		if (outputFile.exists()) {
			outputFile.delete();
		}
		assertEquals("data", parameters.data, actualData);
	}

	protected void doTestResourceLoaderStream(IResourceLoader resourceLoader, TestDownloadParameters parameters) throws Exception {
		String url = this.getServerUrl() + parameters.path;

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final AtomicBoolean downloadResult = new AtomicBoolean();
//		final AtomicReference<String> filenameHolder = new AtomicReference<String>();
		final Object lock = new Object();

		IResource resource = resourceLoader.getResource(url);
		Assert.assertNotNull("resource", resource);

		resource.getResourceAsStream(new IResourceListener<ResourceLoaderEvent<InputStream>>() {
			@Override
			public void onResourceEvent(ResourceLoaderEvent<InputStream> event) {
				if (event.isComplete()) {
					try {
						InputStream in = event.getResult();
						byte[] data = IOUtil.readAllFromStream(in);
						out.write(data);
						synchronized (lock) {
							downloadResult.set(true);
							lock.notify();
						}
					} catch (IOException e) {
						log.error("read failed");
						synchronized (lock) {
							downloadResult.set(false);
							lock.notify();
						}
					}
				} else if (event.isFailed()) {
					synchronized (lock) {
						downloadResult.set(false);
						lock.notify();
					}
				}
			}
		});

		synchronized (lock) {
			lock.wait();
		}

		boolean result = downloadResult.get();
		if (parameters.expectFail) {
			Assert.assertFalse("downloadResult", result);
		} else {
			Assert.assertTrue("downloadResult", result);
			out.flush();
			byte[] actualData = out.toByteArray();
			assertEquals("data", parameters.data, actualData);
//			Assert.assertEquals("filename", parameters.filename, filenameHolder.get());
		}
	}

}
