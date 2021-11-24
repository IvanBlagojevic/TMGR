package org.gs4tr.termmanager.webmvc.controllers;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gs4tr.foundation.modules.entities.model.RepositoryItem;
import org.gs4tr.foundation.modules.entities.model.ResourceInfo;

public class DownloadUtils {

    public static final String CACHE_CONTROL = "Cache-Control";

    public static final String CACHE_CONTROL_VALUE = "max-age=0";

    public static final String CONTENT_ENCODING_TRANSFER_HEADER_NAME = "Content-Transfer-Encoding";

    public static final String CONTENT_ENCODING_TRANSFER_HEADER_VALUE = "binary";

    public static final String DEFAULT_MIME_TYPE = "text/html;charset=utf-8";

    public static final String DOWNLOAD_MIME_TYPE = "application-download";

    public static final String FILENAME_FORMAT_VALUE = "attachment; filename=\"%s\"";

    public static final String FILENAME_HEADER_NAME = "Content-Disposition";

    public static final String FILENAME_NO_ATTACHMENT_VALUE = "inline; filename=\"%s\"";

    public static final String PRAGMA = "Pragma";

    public static final String PRAGMA_VALUE = "public";

    public static void fillDownloadResponse(RepositoryItem repositoryItem, HttpServletResponse response,
	    Boolean disposition) throws IOException {
	fillResponse(repositoryItem, response, disposition);
    }

    public static void fillDownloadResponse(String resourceName, InputStream inputStream, HttpServletResponse response)
	    throws IOException {
	fillResponse(resourceName, inputStream, response, DOWNLOAD_MIME_TYPE, true);
    }

    public static void fillResponse(RepositoryItem repositoryItem, HttpServletResponse response, boolean disposition)
	    throws IOException {

	ResourceInfo resourceInfo = repositoryItem.getResourceInfo();
	String mimeType = resourceInfo.getMimeType();
	InputStream inputStream = repositoryItem.getInputStream();
	String resourceName = resourceInfo.getName();

	fillResponse(resourceName, inputStream, response, mimeType, disposition);
	inputStream.close();
    }

    public static void fillResponse(String resourceName, InputStream inputStream, HttpServletResponse response,
	    String mimeType, boolean disposition) throws IOException {
	ServletOutputStream out = response.getOutputStream();

	response.setContentType(mimeType);
	response.setContentLength(inputStream.available());

	if (disposition) {
	    response.setHeader(FILENAME_HEADER_NAME, String.format(FILENAME_FORMAT_VALUE, resourceName));
	} else {
	    response.setHeader(FILENAME_HEADER_NAME, String.format(FILENAME_NO_ATTACHMENT_VALUE, resourceName));
	}
	response.setHeader(CONTENT_ENCODING_TRANSFER_HEADER_NAME, CONTENT_ENCODING_TRANSFER_HEADER_VALUE);
	response.setHeader(PRAGMA, PRAGMA_VALUE);
	response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUE);

	IOUtils.copy(inputStream, out);

	out.flush();
	out.close();
    }

}