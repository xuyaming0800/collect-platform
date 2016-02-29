package com.autonavi.collect.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GZipFilter implements Filter {
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			// HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			GZipResponseWrapper wrappedResponse = new GZipResponseWrapper(response);
			chain.doFilter(req, wrappedResponse);
			wrappedResponse.finishResponse();
			return;
			// chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		// noop
	}

	@Override
	public void destroy() {
		// noop
	}
}

/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
class GZipResponseStream extends ServletOutputStream {
	private Logger logger = LogManager.getLogger(this.getClass());
	protected ByteArrayOutputStream baos = null;
	protected GZIPOutputStream gzipstream = null;
	protected boolean closed = false;
	protected HttpServletResponse response = null;
	protected ServletOutputStream output = null;

	public GZipResponseStream(HttpServletResponse response) throws IOException {
		super();
		this.closed = false;
		this.response = response;
		this.output = response.getOutputStream();
		this.baos = new ByteArrayOutputStream();
		this.gzipstream = new GZIPOutputStream(this.baos);
	}

	@Override
	public void close() throws IOException {
		if (this.closed) {
			throw new IOException("This output stream has already been closed");
		}
		this.gzipstream.finish();

		byte[] bytes = this.baos.toByteArray();
		if(logger.isDebugEnabled()){
		    logger.debug("输出流大小=" + bytes.length);
		}
		this.response.addHeader("Content-Length", Integer.toString(bytes.length));
		this.response.addHeader("Content-Encoding", "gzip");
		this.output.write(bytes);
		this.output.flush();
		this.output.close();
		this.closed = true;
	}

	@Override
	public void flush() throws IOException {
		if (this.closed) {
			throw new IOException("Cannot flush a closed output stream");
		}
		this.gzipstream.flush();
	}

	@Override
	public void write(int b) throws IOException {
		if (this.closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		this.gzipstream.write((byte) b);
	}

	@Override
	public void write(byte b[]) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (this.closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		this.gzipstream.write(b, off, len);
	}

	public boolean closed() {
		return (this.closed);
	}

	public void reset() {
		//noop
	}
}

/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
class GZipResponseWrapper extends HttpServletResponseWrapper {
	protected HttpServletResponse origResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;

	public GZipResponseWrapper(HttpServletResponse response) {
		super(response);
		this.origResponse = response;
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return (new GZipResponseStream(this.origResponse));
	}

	public void finishResponse() {
		try {
			if (this.writer != null) {
				this.writer.close();
			} else {
				if (this.stream != null) {
					this.stream.close();
				}
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		this.stream.flush();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (this.writer != null) {
			throw new IllegalStateException("getWriter() has already been called!");
		}

		if (this.stream == null) {
			this.stream = this.createOutputStream();
		}
		return (this.stream);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.writer != null) {
			return (this.writer);
		}

		if (this.stream != null) {
			throw new IllegalStateException("getOutputStream() has already been called!");
		}

		this.stream = this.createOutputStream();
		this.writer = new PrintWriter(new OutputStreamWriter(this.stream, "UTF-8"));
		return (this.writer);
	}

	@Override
	public void setContentLength(int length) {
	}
}