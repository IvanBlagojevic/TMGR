package org.gs4tr.termmanager.tests.controllers.rest.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RestTestHelper {

    private static final String BASE_URL = "http://localhost:8080/TMGR/rest"; //$NON-NLS-1$

    private static final String PASSWORD = "password"; //$NON-NLS-1$

    private static String DOWNLOADED_XML_FILE = "target/downloadedTBX.xml"; //$NON-NLS-1$

    private static String ENCODING = "UTF-8"; //$NON-NLS-1$
    private static final String USERNAME = "user_power"; //$NON-NLS-1$

    // private static final String SHORTCODE = "PRO000001"; //$NON-NLS-1$

    public static String addTerm(String userId) throws IOException {
	String addUri = BASE_URL + "/addTerm" + "?projectTicket=" + encodeUrl(IdEncrypter.encryptGenericId(73))
		+ "&sourceTerm=sourceTerm1" + "&targetTerm=targetTerm1" + "&sourceLocale=en-US" + "&targetLocale=de-DE"
		+ "&userId=" + userId;

	return RestUtils.performPostForPlainText(addUri);
    }

    public static String exportDetailedDocumentTest(String userId, long afterDate) throws IOException {

	long projectId = 73L;

	String src = "en-US";
	String trg = "de-DE";

	String projectTicket = encodeUrl(TicketConverter.fromInternalToDto(projectId));

	String exportUrl = BASE_URL + "/detailedExport" + "?afterDate=" + afterDate + "&projectTicket=" + projectTicket
		+ "&sourceLocale=" + src + "&targetLocale=" + trg + "&exportFormat=" + "JSON"
		+ "&exportForbiddenTerms=true"
		// + "&descriptionType=context"
		+ "&blacklistTermsCount=0" + "&exportAllDescriptions=false" + "&userId=" + encodeUrl(userId);
	System.out.println(String.format("GET export url: %s", exportUrl));

	MediaType mediaType = MediaType.APPLICATION_JSON;
	List<MediaType> mediaTypes = new ArrayList<>();
	mediaTypes.add(mediaType);

	HttpHeaders headers = new HttpHeaders();
	headers.setAccept(mediaTypes);

	HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

	ResponseEntity<String> performExcange = RestUtils.performExcange(exportUrl, HttpMethod.GET, requestEntity,
		String.class);

	String body = performExcange.getBody();
	if (body != null) {
	    byte[] bytes = body.getBytes(ENCODING);
	    OutputStream output = new FileOutputStream(new File(DOWNLOADED_JSON_FILE));
	    output.write(bytes);
	    output.flush();
	    output.close();
	}

	return body;
    }

    public static String importTest(String userId) throws IOException {
	Long projectId = 73L;
	String projectTicket = encodeUrl(IdEncrypter.encryptGenericId(projectId));
	String importUri = BASE_URL + "/import" + "?projectTicket=" + projectTicket + "&syncLang=en-US" + "&userId="
		+ encodeUrl(userId);
	System.out.println(String.format("POST import url: %s", importUri));

	String body = readFile(TEST_IMPORT_TBX_FILE, Charset.forName(ENCODING));

	HttpEntity<String> httpEntity = new HttpEntity<String>(body);

	String result = RestUtils.performPostForPlainText(importUri, httpEntity);
	return result;
    }

    public static String encodeUrl(String url) {
	try {
	    return URLEncoder.encode(url, ENCODING);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private static String DOWNLOADED_JSON_FILE = "/home/emisia/GS4TR_TMGR_HOME/downloaded1.json"; //$NON-NLS-1$

    public static String exportTest(String userId) throws IOException {
	long timeInMillis = 0;
	Long projectId = 1L;
	String projectTicket = encodeUrl(TicketConverter.fromInternalToDto(projectId));
	String exportUrl = BASE_URL + "/export" + "?afterDate=" + timeInMillis + "&projectTicket=" + projectTicket
		+ "&sourceLocale=en&targetLocale=it" + "&generateStatistics=true" + "&exportFormat=" + "TBX"
		+ "&exportForbiddenTerms=false" + "&userId=" + encodeUrl(userId);
	System.out.println(String.format("GET export url: %s", exportUrl));

	MediaType mediaType = MediaType.APPLICATION_XML;
	List<MediaType> mediaTypes = new ArrayList<>();
	mediaTypes.add(mediaType);

	HttpHeaders headers = new HttpHeaders();
	headers.setAccept(mediaTypes);

	HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

	ResponseEntity<String> performExcange = RestUtils.performExcange(exportUrl, HttpMethod.GET, requestEntity,
		String.class);

	String body = performExcange.getBody();
	if (body != null) {
	    byte[] bytes = body.getBytes(ENCODING);
	    OutputStream output = new FileOutputStream(new File(DOWNLOADED_XML_FILE));
	    output.write(bytes);
	    output.flush();
	    output.close();
	}

	return body;
    }

    private static String TEST_IMPORT_TBX_FILE = "/home/emisia/GS4TR_TMGR_HOME/tbx/terms_skype_utf8_TMgr4-fixed-by-Marko.tbx"; //$NON-NLS-1$

    public static String loginGET() {
	String loginUrl = BASE_URL + "/login" + "?username=" + USERNAME + "&password=" + PASSWORD;
	System.out.println(String.format("GET login url: %s", loginUrl));
	String result = RestUtils.performGetForObject(loginUrl, String.class);
	return result;
    }
    
    public static String logoutGET(String userId) {
	String logoutUrl = BASE_URL + "/logout" + "?userId=" + userId;
	String result = RestUtils.performGetForObject(logoutUrl, String.class);
	return result;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
	byte[] encoded = Files.readAllBytes(Paths.get(path));
	return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
}
