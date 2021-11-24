package org.gs4tr.termmanager.tests.webservices.restV2;

import java.io.File;

import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.webservices.ConnectionDetails;
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
import org.gs4tr.termmanager.webservice.model.request.RecodeOrCloneTermsCommand;
import org.gs4tr.termmanager.webservice.model.request.SegmentTermSearchCommand;
import org.gs4tr.termmanager.webservice.model.response.ErrorResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class RestV2Client {

    private static final RestTemplate _restTemplate = new RestTemplate();

    private static String extractProjectTicket(String projectTicketRepsonse) {
	JsonNode responseData = JsonUtils.readValue(projectTicketRepsonse, JsonNode.class);
	return responseData.get("projects").get(0).get("projectTicket").asText();

    }

    private static String extractSecurityToken(String loginResponse) {
	JsonNode responseData = JsonUtils.readValue(loginResponse, JsonNode.class);
	return responseData.get("securityTicket").asText();
    }

    private static String logout(String securityTicket, ConnectionDetails connectionDetails) {

	BaseCommand command = new BaseCommand();
	command.setSecurityTicket(securityTicket);
	String logoutURL = connectionDetails.getBaseURL().concat("/v2/logout");

	ResponseEntity<String> logoutResponse = _restTemplate.postForEntity(logoutURL, command, String.class);

	return logoutResponse.getBody();

    }

    protected static String addTermEntry(ConnectionDetails connectionDetails, AddTermsCommand command) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectDataResponse = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectDataResponse);

	    String addTermURL = connectionDetails.getBaseURL().concat("/v2/addTerm");

	    command.setProjectTicket(projectTicket);
	    command.setSecurityTicket(securityTicket);

	    HttpEntity<AddTermsCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> addTermEntryResponse = _restTemplate.postForEntity(addTermURL, request,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return addTermEntryResponse.getBody();

	} catch (HttpClientErrorException e) {
	    return e.getResponseBodyAsString();
	}
    }

    protected static String batchSegmentSearch(ConnectionDetails connectionDetails,
	    BatchSegmentTermSearchCommand command) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectDataResponse = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectDataResponse);

	    String batchSegmentSearchURL = connectionDetails.getBaseURL().concat("/v2/batchSegmentTermSearch");

	    command.setProjectTicket(projectTicket);
	    command.setSecurityTicket(securityTicket);

	    HttpEntity<BatchSegmentTermSearchCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> batchSegmentSearchResponse = _restTemplate.postForEntity(batchSegmentSearchURL,
		    request, String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return batchSegmentSearchResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}
    }

    protected static String detailedGlossaryExport(ConnectionDetails connectionDetails,
	    DetailedGlossaryExportCommand command) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectDataResponse = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectDataResponse);

	    String detailedExportURL = connectionDetails.getBaseURL().concat("/v2/detailedExport");

	    command.setProjectTicket(projectTicket);
	    command.setSecurityTicket(securityTicket);

	    HttpEntity<DetailedGlossaryExportCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> detailedExportResponse = _restTemplate.postForEntity(detailedExportURL, request,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return detailedExportResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}
    }

    protected static String getProjectDataByShortcode(ConnectionDetails connectionDetails, boolean fetchLanguages,
	    String securityTicket) {

	String projectDataURL = connectionDetails.getBaseURL().concat("/v2/projectByShortcode");
	BrowseProjectDataCommand command = new BrowseProjectDataCommand();

	command.setProjectShortcode(connectionDetails.getProjectShortcode());
	command.setFetchLanguages(fetchLanguages);
	command.setSecurityTicket(securityTicket);

	ResponseEntity<String> projectShortcodeResponse = _restTemplate.postForEntity(projectDataURL, command,
		String.class);

	return projectShortcodeResponse.getBody();

    }

    protected static String getProjectMetadata(GetProjectMetadataCommand command, ConnectionDetails connectionDetails) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectMetadataURL = connectionDetails.getBaseURL().concat("/v2/getProjectMetadata");

	    command.setSecurityTicket(securityTicket);

	    HttpEntity<GetProjectMetadataCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> getProjectMetadataResponse = _restTemplate.postForEntity(projectMetadataURL, request,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return getProjectMetadataResponse.getBody();

	} catch (HttpClientErrorException e) {
	    return e.getResponseBodyAsString();
	}

    }

    protected static String getUserProjectData(ConnectionDetails connectionDetails, boolean fetchLanguages) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String userProjectURL = connectionDetails.getBaseURL().concat("/v2/userProjects");

	    BrowseUserProjectsCommand command = new BrowseUserProjectsCommand();
	    command.setFetchLanguages(fetchLanguages);
	    command.setSecurityTicket(securityTicket);

	    ResponseEntity<String> userProjectDataResponse = _restTemplate.postForEntity(userProjectURL, command,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);
	    return userProjectDataResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}

    }

    protected static String importGlossary(ConnectionDetails connectionDetails, String syncLang, File file) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectData = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectData);

	    String importURL = connectionDetails.getBaseURL().concat("/v2/import");

	    ImportCommand command = new ImportCommand();
	    command.setProjectTicket(projectTicket);
	    command.setSyncLang(syncLang);
	    command.setSecurityTicket(securityTicket);

	    HttpHeaders header = new HttpHeaders();
	    header.setContentType(MediaType.MULTIPART_FORM_DATA);

	    MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();

	    Resource resource = new FileSystemResource(file);

	    multipartRequest.add("command", command);
	    multipartRequest.add("file", resource);

	    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multipartRequest, header);

	    ResponseEntity<String> importResponse = _restTemplate.postForEntity(importURL, request, String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return importResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}

    }

    protected static String login(ConnectionDetails connectionDetails) {

	String loginURL = connectionDetails.getBaseURL().concat("/v2/login");
	LoginCommand command = new LoginCommand();
	command.setPassword(connectionDetails.getPassword());
	command.setUsername(connectionDetails.getUsername());

	HttpEntity<LoginCommand> request = new HttpEntity<>(command);

	ResponseEntity<String> loginResponse = _restTemplate.postForEntity(loginURL, request, String.class);

	return loginResponse.getBody();

    }

    protected static void recodeOrCloneTerms(ConnectionDetails connectionDetails, RecodeOrCloneTermsCommand command) {
	String loginResponse = login(connectionDetails);
	String securityTicket = extractSecurityToken(loginResponse);

	command.setSecurityTicket(securityTicket);

	HttpEntity<RecodeOrCloneTermsCommand> request = new HttpEntity<>(command);

	String recodeOrCloneURL = connectionDetails.getBaseURL().concat("/v2/recodeOrClone");

	ResponseEntity<ErrorResponse> recodeOrCloneResponse = null;

	String errorResponseMessage = null;
	try {
	    recodeOrCloneResponse = _restTemplate.postForEntity(recodeOrCloneURL, request, ErrorResponse.class);
	} catch (HttpClientErrorException e) {
	    errorResponseMessage = e.getResponseBodyAsString();
	}

	System.out.println(errorResponseMessage);
	System.out.println(recodeOrCloneResponse);

	String logoutResponse = logout(securityTicket, connectionDetails);
    }

    protected static String segmentTermSearch(ConnectionDetails connectionDetails, SegmentTermSearchCommand command) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectDataResponse = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectDataResponse);

	    String segmentSearchURL = connectionDetails.getBaseURL().concat("/v2/segmentTermSearch");

	    command.setSecurityTicket(securityTicket);
	    command.setProjectTicket(projectTicket);

	    HttpEntity<SegmentTermSearchCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> segmentSearchResponse = _restTemplate.postForEntity(segmentSearchURL, request,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return segmentSearchResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}
    }

    protected static String termEntrySearch(ConnectionDetails connectionDetails, ConcordanceSearchCommand command) {

	try {

	    String loginResponse = login(connectionDetails);
	    String securityTicket = extractSecurityToken(loginResponse);

	    String projectDataResponse = getProjectDataByShortcode(connectionDetails, true, securityTicket);
	    String projectTicket = extractProjectTicket(projectDataResponse);

	    String termEntrySearchURL = connectionDetails.getBaseURL().concat("/v2/searchTermEntries");

	    command.setSecurityTicket(securityTicket);
	    command.setProjectTicket(projectTicket);

	    HttpEntity<ConcordanceSearchCommand> request = new HttpEntity<>(command);

	    ResponseEntity<String> termEntrySearchResponse = _restTemplate.postForEntity(termEntrySearchURL, request,
		    String.class);

	    String logoutResponse = logout(securityTicket, connectionDetails);

	    return termEntrySearchResponse.getBody();

	} catch (HttpClientErrorException ex) {
	    return ex.getResponseBodyAsString();
	}

    }

}
