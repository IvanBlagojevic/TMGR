package org.gs4tr.termmanager.tests.controllers.rest.main;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class SampleCode {

    private static final String BASE_URL = "http://172.16.10.202:8080/tmgr/rest"; //$NON-NLS-1$

    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    private static final String PASSWORD = "password1!";// "!0P3us3r@"; //$NON-NLS-1$

    private static final String SHORTCODE = "BAY000003"; //$NON-NLS-1$

    private static final String USERNAME = "filip";// "OPE_user"; //$NON-NLS-1$

    public static void main(String[] args) throws Exception {

	HttpClient client = new HttpClient();

	String userId = loginPOST(client);

	String projectXml = findProjectByShortCode(client, userId);

	String projectTicket = extractProjectTicket(projectXml);

	String glossary = detailedExport(client, projectTicket, userId);

	System.out.println(glossary);
    }

    private static String buildUrl(String... params) {
	if (params == null) {
	    return null;
	}

	StringBuilder builder = new StringBuilder(BASE_URL);

	for (int i = 0; i < params.length; i++) {
	    builder.append(params[i]);
	}

	return builder.toString();
    }

    private static String detailedExport(HttpClient client, String projectTicket, String userId) throws Exception {

	String projectTicketEncoded = encodeUrl(projectTicket);

	// From Year 1979
	String afterDate = "0";

	// ALL changes from NOW
	// String afterDate = String.valueOf(new Date().getTime());

	String source = "en";
	String target = "es-LA";
	String exportFormat = "JSON";

	String url = buildUrl("/detailedExport?", "afterDate=", afterDate, "&projectTicket=", projectTicketEncoded,
		"&sourceLocale=", source, "&targetLocale=", target, "&exportFormat=", exportFormat,
		"&exportForbiddenTerms=true", "&blacklistTermsCount=0", "&userId=", userId);

	GetMethod get = new GetMethod(url);

	int code = client.executeMethod(get);
	validateHttpCode(code);

	String glossary = get.getResponseBodyAsString();

	get.releaseConnection();

	return glossary;
    }

    private static String encodeUrl(String url) {
	try {
	    return URLEncoder.encode(url, ENCODING);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private static String extractProjectTicket(String project) throws Exception {
	InputSource input = new InputSource(new StringReader(project));

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	Document document = dbf.newDocumentBuilder().parse(input);

	Element root = document.getDocumentElement();

	Element projectInfoNode = (Element) root.getElementsByTagName("projectInfo").item(0);

	Node enabledNode = projectInfoNode.getElementsByTagName("enabled").item(0).getFirstChild();

	if (!Boolean.parseBoolean(enabledNode.getNodeValue())) {
	    throw new RuntimeException("Requested project is disabled.");
	}

	Node ticketNode = root.getElementsByTagName("ticket").item(0);

	return ticketNode.getFirstChild().getNodeValue();
    }

    private static String findProjectByShortCode(HttpClient client, String userId) throws Exception {
	String url = buildUrl("/projectByShortcode?", "projectShortcode=", SHORTCODE, "&", "userId=", userId);

	GetMethod get = new GetMethod(url);

	int code = client.executeMethod(get);
	validateHttpCode(code);

	String project = get.getResponseBodyAsString();

	get.releaseConnection();

	return project;
    }

    private static String loginPOST(HttpClient client) throws Exception {
	String loginUrl = buildUrl("/login");

	PostMethod post = new PostMethod(loginUrl);
	post.addParameter("username", USERNAME);
	post.addParameter("password", PASSWORD);

	int code = client.executeMethod(post);
	validateHttpCode(code);

	String userId = post.getResponseBodyAsString();

	post.releaseConnection();

	return userId;
    }

    private static void validateHttpCode(int code) {
	if (code != 200) {
	    throw new RuntimeException(String.format("Invalid HTTP requset. Failed with HTTP code: %d", code));
	}
    }
}
