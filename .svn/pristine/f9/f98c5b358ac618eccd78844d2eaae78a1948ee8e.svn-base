package org.gs4tr.termmanager.service.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.gs4tr.termmanager.service.solr.restore.ICleanUpProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.RestoreInfoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

public class RestResponseRebuildIndexFilter extends GenericFilterBean {

    private static final String CLEANING_HTML_BODY = "<H2>Backup cleaning of disabled terminology is in progress.</H2>\n"
	    + "    <H3>This process will take some time. Take a break and have some coffee.</H3><br>\n"
	    + "    <a href=\"%url\">Return to Login page.</a>";

    private static final String CLEANING_RESPONSE_STRING = "Backup cleaning of disabled terminology is in progress.\n This process will take some time. Take a break and have some coffee.";

    private static final String HTML_CLOSE = "</html>";

    private static final String HTML_OPEN = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "<head>\n"
	    + "    <meta charset=\"UTF-8\">\n" + "    <title>TMGR</title>";

    private static final String PERCENTAGE_REPLACEMENT = "%s";

    private static final Pattern REGEX = Pattern.compile("rest|glossary|v2");

    private static final String REINDEX_HTML_BODY = "<H2>Index rebuild is in progress.</H2>\n"
	    + "    <H3>Currently completed <b>%s</b> percent.</H3><br>\n"
	    + "    <a href=\"%url\">Return to Login page.</a>";

    private static final String REINDEX_RESPONSE_STRING = "Index rebuild is in progress. Currently completed %s percent.";

    private static final String URL_REPLACEMENT = "%url";

    @Autowired(required = false)
    private ICleanUpProcessorV2 _cleanUpProcessorV2;

    private boolean _isClearDisabledTerminology;

    private boolean _isRestoreFromDatabase;

    @Autowired(required = false)
    private RestoreInfoProcessor _restoreInfoProcessor;

    @Autowired(required = false)
    private IRestoreProcessorV2 _restoreProcessorV2;

    private String _serverAddress;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	    throws IOException, ServletException {

	ICleanUpProcessorV2 cleaner = getCleanUpProcessorV2();
	boolean isClearDisabledTerminology = Objects.nonNull(cleaner) && isClearDisabledTerminology();
	if (isClearDisabledTerminology) {
	    if (!cleaner.isFinished()) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String path = httpServletRequest.getServletPath();
		Matcher matcher = REGEX.matcher(path);
		if (matcher.find()) {
		    createStringResponse(servletResponse);
		} else {
		    createHtmlResponse(servletResponse);
		}

		return;
	    }
	}

	boolean isRebuildIndex = Objects.nonNull(getRestoreInfoProcessor())
		&& getRestoreInfoProcessor().isRebuildIndexEnabled();
	if (isRebuildIndex || isRestoreFromDatabase()) {
	    IRestoreProcessorV2 restorer = getRestoreProcessorV2();
	    if (restorer != null && !restorer.isFinished()) {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String path = httpServletRequest.getServletPath();
		Matcher matcher = REGEX.matcher(path);

		if (matcher.find()) {
		    createStringResponse(servletResponse, restorer);
		} else {
		    createHtmlResponse(servletResponse, restorer);
		}

		return;
	    }
	}

	filterChain.doFilter(servletRequest, servletResponse);
    }

    public void setClearDisabledTerminology(boolean clearDisabledTerminology) {
	_isClearDisabledTerminology = clearDisabledTerminology;
    }

    public void setRestoreFromDatabase(boolean restoreFromDatabase) {
	_isRestoreFromDatabase = restoreFromDatabase;
    }

    public void setServerAddress(String serverAddress) {
	_serverAddress = serverAddress;
    }

    private String createBody() {
	String htmlBody = CLEANING_HTML_BODY;
	htmlBody = htmlBody.replace(URL_REPLACEMENT, getServerAddress());
	return htmlBody;
    }

    private String createBody(IRestoreProcessorV2 restorer) {
	int percentage = restorer.getPercentage();
	String htmlBody = REINDEX_HTML_BODY;
	htmlBody = htmlBody.replace(PERCENTAGE_REPLACEMENT, Integer.toString(percentage));
	htmlBody = htmlBody.replace(URL_REPLACEMENT, getServerAddress());
	return htmlBody;
    }

    private void createHtmlResponse(ServletResponse servletResponse, IRestoreProcessorV2 restorer) throws IOException {
	servletResponse.setContentType("text/html");
	PrintWriter printWriter = servletResponse.getWriter();
	printWriter.write(HTML_OPEN);
	printWriter.write(createBody(restorer));
	printWriter.write(HTML_CLOSE);
    }

    private void createHtmlResponse(ServletResponse servletResponse) throws IOException {
	servletResponse.setContentType("text/html");
	PrintWriter printWriter = servletResponse.getWriter();
	printWriter.write(HTML_OPEN);
	printWriter.write(createBody());
	printWriter.write(HTML_CLOSE);
    }

    private void createStringResponse(ServletResponse servletResponse, IRestoreProcessorV2 restorer)
	    throws IOException {

	servletResponse.setContentType("text/plain");
	PrintWriter printWriter = servletResponse.getWriter();

	int percentage = restorer.getPercentage();

	String responseString = REINDEX_RESPONSE_STRING.replace(PERCENTAGE_REPLACEMENT, Integer.toString(percentage));

	printWriter.write(responseString);
    }

    private void createStringResponse(ServletResponse servletResponse) throws IOException {
	servletResponse.setContentType("text/plain");
	servletResponse.getWriter().write(CLEANING_RESPONSE_STRING);
    }

    private ICleanUpProcessorV2 getCleanUpProcessorV2() {
	return _cleanUpProcessorV2;
    }

    private RestoreInfoProcessor getRestoreInfoProcessor() {
	return _restoreInfoProcessor;
    }

    private IRestoreProcessorV2 getRestoreProcessorV2() {
	return _restoreProcessorV2;
    }

    private String getServerAddress() {
	return _serverAddress;
    }

    private boolean isClearDisabledTerminology() {
	return _isClearDisabledTerminology;
    }

    private boolean isRestoreFromDatabase() {
	return _isRestoreFromDatabase;
    }
}
