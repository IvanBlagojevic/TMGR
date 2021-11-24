package org.gs4tr.termmanager.webservice.rest.template.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.TermV2ModelExtended;
import org.gs4tr.termmanager.service.export.ExportFormatEnum;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webservice.model.request.AddTermsCommand;
import org.gs4tr.termmanager.webservice.model.request.BaseCommand;
import org.gs4tr.termmanager.webservice.model.request.BatchSegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.BrowseProjectDataCommand;
import org.gs4tr.termmanager.webservice.model.request.BrowseUserProjectsCommand;
import org.gs4tr.termmanager.webservice.model.request.ConcordanceSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.DetailedGlossaryExportCommand;
import org.gs4tr.termmanager.webservice.model.request.GetProjectMetadataCommand;
import org.gs4tr.termmanager.webservice.model.request.ImportCommand;
import org.gs4tr.termmanager.webservice.model.request.LoginCommand;
import org.gs4tr.termmanager.webservice.model.request.SegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.request.WsDateFilter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class uses RestTemplate that provides an abstraction for making RESTful
 * HTTP requests.
 * 
 * @author TMGR_Backend
 * 
 */
public class WsV2TestClient {

    private static final String BASE_URL = "http://localhost:8080/TMGR"; //$NON-NLS-1$
    private static final Class<ArrayList> BATCH_RESPONSE_CLAZZ = ArrayList.class;
    private static final String DOWNLOADED_JSON_FILE = "target/exportedGlossary.json"; //$NON-NLS-1$ ;
    @SuppressWarnings("unused")
    private static final String REMOTE_URL = "http://localhost:8080/TMGR"; //$NON-NLS-1$
    private static final Class<String> RESPONSE_CLAZZ = String.class;
    private static final RestTemplate REST_TEMPLATE = initializeHttpClient();
    public static String IMPORT_FILE = "src/test/resources/tmp/medtronic.tbx"; //$NON-NLS-1$

    public static String addTermEntry(String securityTicket, String projectTicket)
	    throws RestClientException, URISyntaxException {
	Description note = new Description();
	note.setBaseType(Description.NOTE);
	note.setType("partOfSpeech");
	note.setValue("dva dva tri tri");

	List<Description> descriptions = new ArrayList<Description>();
	descriptions.add(note);

	TermV2ModelExtended v2Term = new TermV2ModelExtended();
	v2Term.setLocale("en-US");
	v2Term.setTermText("two");
	v2Term.setDescriptions(descriptions);
	// v2Term.setStatus(ItemStatusTypeHolder.PROCESSED.getName());

	TermV2ModelExtended v2Term1 = new TermV2ModelExtended();
	v2Term1.setLocale("de-DE");
	v2Term1.setTermText("zwei");

	List<TermV2ModelExtended> v2Terms = new ArrayList<TermV2ModelExtended>();
	v2Terms.add(v2Term);
	v2Terms.add(v2Term1);

	AddTermsCommand cmd = new AddTermsCommand();
	cmd.setSecurityTicket(securityTicket);
	cmd.setProjectTicket(projectTicket);
	cmd.setTerms(v2Terms);

	URI url = new URI(BASE_URL.concat("/rest/v2/addTerm"));
	return REST_TEMPLATE.postForObject(url, cmd, RESPONSE_CLAZZ);
    }

    public static String batchSegmentTermSearch(String securityTicket, String projectTicket, boolean fuzzy,
	    boolean searchForbidden, String sourceLanguage, List<String> targetLanguages, int maxNum,
	    List<String> batchSegments) throws URISyntaxException {

	BatchSegmentTermSearchCommand searchCommand = createBatchSegmentTermSearchCommand(securityTicket, projectTicket,
		fuzzy, searchForbidden, sourceLanguage, targetLanguages, maxNum, batchSegments);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/batchSegmentTermSearch")), searchCommand,
		RESPONSE_CLAZZ);
    }

    public static String concordanceSearch(String securityTicket, boolean isCaseSensitive,
	    boolean includeAttributesSearch, boolean matchWholeWords, int maxNum, String projectTicket,
	    boolean searchForbidden, String termText, String sourceLocale, List<String> targetLocales,
	    WsDateFilter creationDateFilter, WsDateFilter modificationDateFilter) throws URISyntaxException {

	ConcordanceSearchCommand searchCommand = createConcordanceSearchCommand(securityTicket, isCaseSensitive,
		includeAttributesSearch, matchWholeWords, maxNum, projectTicket, searchForbidden, termText,
		sourceLocale, targetLocales, creationDateFilter, modificationDateFilter);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/termSearch")), searchCommand,
		RESPONSE_CLAZZ);
    }

    public static void exportGlossary(String securityTicket, boolean isForbidden, ExportFormatEnum exportFormat,
	    String sourceLocale, List<String> targetLocales, String projectTicket)
	    throws URISyntaxException, IOException {

	DetailedGlossaryExportCommand exportCommand = createDetailedGlossaryExportCommand(securityTicket, isForbidden,
		exportFormat, sourceLocale, targetLocales, projectTicket);

	List<MediaType> mediaTypes = new ArrayList<>();

	mediaTypes.add(MediaType.APPLICATION_JSON);

	HttpHeaders headers = new HttpHeaders();
	headers.setAccept(mediaTypes);

	ResponseEntity<String> result = null;
	try {
	    result = REST_TEMPLATE.postForEntity(new URI(BASE_URL.concat("/rest/v2/detailedExport")), exportCommand,
		    RESPONSE_CLAZZ);
	} catch (HttpServerErrorException e) {
	    throw new RuntimeException(e.getResponseBodyAsString(), e);
	} catch (RestClientException e) {
	    throw new RuntimeException(e.getMessage());
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Request URL could not be parsed as a URI reference.", e);
	}
	String body = result.getBody();

	if (body != null) {
	    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
	    OutputStream output = new FileOutputStream(new File(DOWNLOADED_JSON_FILE));
	    output.write(bytes);
	    output.flush();
	    output.close();
	}
    }

    public static String getProjectData(String securityTicket, boolean fetchLanguages, String shortCode)
	    throws URISyntaxException {

	BrowseProjectDataCommand browseCommand = createBrowseProjectCommand(securityTicket, fetchLanguages, shortCode);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/projectByShortcode")), browseCommand,
		RESPONSE_CLAZZ);
    }

    public static String getProjectMetadata(String securityTicket, List<String> languages, String organizationName,
	    String projectName, String projectShortcode, String genericUsername) throws URISyntaxException {

	GetProjectMetadataCommand command = new GetProjectMetadataCommand();
	command.setSecurityTicket(securityTicket);
	command.setLanguages(languages);
	command.setProjectName(projectName);
	command.setOrganizationName(organizationName);
	command.setProjectShortcode(projectShortcode);
	command.setUsername(genericUsername);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/getProjectMetadata")), command,
		RESPONSE_CLAZZ);

    }

    public static String getUserProjectsData(String securityTicket, boolean fetchLanguages) throws URISyntaxException {

	BrowseUserProjectsCommand browseCommand = createBrowseUserProjectsCommand(securityTicket, fetchLanguages);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/userProjects")), browseCommand,
		RESPONSE_CLAZZ);
    }

    public static String importDocument(String securityTicket, String projectTicket, String syncLanguage,
	    String importFilePath) throws URISyntaxException {

	URI url = new URI(BASE_URL.concat("/rest/v2/import"));

	ImportCommand importCommand = new ImportCommand();
	importCommand.setSecurityTicket(securityTicket);
	importCommand.setProjectTicket(projectTicket);
	importCommand.setSyncLang(syncLanguage);

	HttpHeaders requestHeaders = new HttpHeaders();
	requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

	MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
	map.add("file", new FileSystemResource(new File(importFilePath)));
	map.add("command", importCommand);

	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(map,
		requestHeaders);
	ResponseEntity<String> responseEntity = REST_TEMPLATE.postForEntity(url, requestEntity, String.class);

	return responseEntity.getBody();
    }

    public static String loginPost(String userName, String password) throws URISyntaxException {
	LoginCommand loginCommand = new LoginCommand();
	loginCommand.setUsername(userName);
	loginCommand.setPassword(password);

	URI url = new URI(BASE_URL.concat("/rest/v2/login"));

	String loginResponse = REST_TEMPLATE.postForObject(url, loginCommand, RESPONSE_CLAZZ);

	return extractSecurityToken(loginResponse);
    }

    public static String logoutPost(String securityTicket) throws URISyntaxException {
	URI url = new URI(BASE_URL.concat("/rest/v2/logout"));

	BaseCommand cmd = new BaseCommand();
	cmd.setSecurityTicket(securityTicket);

	return REST_TEMPLATE.postForObject(url, cmd, RESPONSE_CLAZZ);
    }

    public static String segmentTermSearch(String securityTicket, String projectTicket, boolean fuzzy,
	    boolean searchForbidden, String sourceLanguage, List<String> targetLanguages, int maxNum, String segment)
	    throws URISyntaxException {

	SegmentTermSearchCommand searchCommand = createSegmentTermSearchCommand(securityTicket, projectTicket, fuzzy,
		searchForbidden, sourceLanguage, targetLanguages, maxNum, segment);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/segmentTermSearch")), searchCommand,
		RESPONSE_CLAZZ);
    }

    public static String termEntrySearch(String securityTicket, int maxNum, String projectTicket,
	    boolean searchForbidden, String sourceLocale, List<String> targetLocales, WsDateFilter creationDateFilter,
	    WsDateFilter modificationDateFilter, String termText) throws URISyntaxException {

	ConcordanceSearchCommand searchCommand = createConcordanceSearchCommand(securityTicket, true, false, true,
		maxNum, projectTicket, searchForbidden, null, sourceLocale, targetLocales, creationDateFilter,
		modificationDateFilter);
	searchCommand.setTerm(termText);

	return REST_TEMPLATE.postForObject(new URI(BASE_URL.concat("/rest/v2/searchTermEntries")), searchCommand,
		RESPONSE_CLAZZ);
    }

    public static String updateTermEntry(AddTermsCommand cmd) throws URISyntaxException {
	URI url = new URI(BASE_URL.concat("/rest/v2/addTerm"));
	return REST_TEMPLATE.postForObject(url, cmd, RESPONSE_CLAZZ);
    }

    protected static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static BatchSegmentTermSearchCommand createBatchSegmentTermSearchCommand(String securityTicket,
	    String projectTicket, boolean fuzzy, boolean searchForbidden, String sourceLanguage,
	    List<String> targetLanguages, int maxNum, List<String> batchSegments) {

	BatchSegmentTermSearchCommand cmd = new BatchSegmentTermSearchCommand();
	cmd.setSecurityTicket(securityTicket);
	cmd.setSearchForbidden(searchForbidden);
	cmd.setFuzzy(fuzzy);
	cmd.setProjectTicket(projectTicket);
	cmd.setSourceLanguage(sourceLanguage);
	cmd.setTargetLanguages(targetLanguages);
	cmd.setMaxNumFound(maxNum);
	cmd.setBatchSegment(batchSegments);

	return cmd;
    }

    private static BrowseProjectDataCommand createBrowseProjectCommand(String securityTicket, boolean fetchLanguages,
	    String shortCode) {
	BrowseProjectDataCommand browseCommand = new BrowseProjectDataCommand();
	browseCommand.setSecurityTicket(securityTicket);
	browseCommand.setFetchLanguages(fetchLanguages);
	browseCommand.setProjectShortcode(shortCode);
	return browseCommand;
    }

    private static BrowseUserProjectsCommand createBrowseUserProjectsCommand(String securityTicket,
	    boolean fetchLanguages) {
	BrowseUserProjectsCommand browseCommand = new BrowseUserProjectsCommand();
	browseCommand.setSecurityTicket(securityTicket);
	browseCommand.setFetchLanguages(fetchLanguages);
	return browseCommand;
    }

    private static ConcordanceSearchCommand createConcordanceSearchCommand(String securityTicket,
	    boolean isCaseSensitive, boolean includeAttributesSearch, boolean matchWholeWords, int maxNum,
	    String projectTicket, boolean searchForbidden, String termText, String sourceLocale,
	    List<String> targetLocales, WsDateFilter creationDateFilter, WsDateFilter modificationDateFilter) {

	ConcordanceSearchCommand searchCommand = new ConcordanceSearchCommand();

	searchCommand.setIncludeAttributesSearch(includeAttributesSearch);
	searchCommand.setSecurityTicket(securityTicket);
	searchCommand.setCaseSensitive(isCaseSensitive);
	searchCommand.setMatchWholeWords(matchWholeWords);
	// searchCommand.setMaxNumFound(maxNum);
	searchCommand.setProjectTicket(projectTicket);
	searchCommand.setSearchForbidden(searchForbidden);
	searchCommand.setTerm(termText);
	searchCommand.setSourceLocale(sourceLocale);
	searchCommand.setTargetLocales(targetLocales);
	searchCommand.setCreationDateFilter(creationDateFilter);
	searchCommand.setModificationDateFilter(modificationDateFilter);

	return searchCommand;
    }

    private static DetailedGlossaryExportCommand createDetailedGlossaryExportCommand(String securityTicket,
	    boolean isForbidden, ExportFormatEnum exportFormat, String sourceLocale, List<String> targetLocales,
	    String projectTicket) {

	DetailedGlossaryExportCommand exportCommand = new DetailedGlossaryExportCommand();

	exportCommand.setSecurityTicket(securityTicket);
	exportCommand.setForbidden(isForbidden);
	exportCommand.setFileType(exportFormat);
	exportCommand.setSourceLocale(sourceLocale);
	exportCommand.setTargetLocales(targetLocales);
	exportCommand.setProjectTicket(projectTicket);

	return exportCommand;
    }

    private static SegmentTermSearchCommand createSegmentTermSearchCommand(String securityTicket, String projectTicket,
	    boolean fuzzy, boolean searchForbidden, String sourceLanguage, List<String> targetLanguages, int maxNum,
	    String segment) {

	SegmentTermSearchCommand searchCommand = new SegmentTermSearchCommand();

	searchCommand.setSecurityTicket(securityTicket);
	searchCommand.setSearchForbidden(searchForbidden);
	searchCommand.setFuzzy(fuzzy);
	searchCommand.setProjectTicket(projectTicket);
	searchCommand.setSourceLanguage(sourceLanguage);
	searchCommand.setTargetLanguages(targetLanguages);
	searchCommand.setMaxNumFound(maxNum);
	searchCommand.setSegment(segment);

	return searchCommand;
    }

    private static String extractSecurityToken(String loginResponse) {
	JsonNode responseData = JsonUtils.readValue(loginResponse, JsonNode.class);

	return responseData.get("securityTicket").asText();
    }

    private static RestTemplate initializeHttpClient() {
	RestTemplate restTemplate = new RestTemplate();

	restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

	return restTemplate;
    }
}
