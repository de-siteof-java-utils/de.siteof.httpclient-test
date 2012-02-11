package de.siteof.httpclient.test;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

public class TestByteArrayOutputStream extends FilterOutputStream {

	private boolean closed;

	public TestByteArrayOutputStream() {
		super(new ByteArrayOutputStream());
	}

    public byte toByteArray()[] {
        return ((ByteArrayOutputStream) super.out).toByteArray();
    }

	@Override
	public void write(int b) throws IOException {
		if (isClosed()) {
			throw new IOException("stream already closed");
		}
		super.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (isClosed()) {
			throw new IOException("stream already closed");
		}
		super.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) {
			throw new IOException("stream already closed");
		}
		this.closed = true;
		super.close();
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void flush() throws IOException {
		if (isClosed()) {
			throw new IOException("stream already closed");
		}
		super.flush();
	}

}
