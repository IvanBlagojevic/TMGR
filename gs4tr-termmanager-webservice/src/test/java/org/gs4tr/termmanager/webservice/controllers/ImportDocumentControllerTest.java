package org.gs4tr.termmanager.webservice.controllers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.createMultipartRequest;
import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.gs4tr.termmanager.webservice.utils.V2Utils.getProject;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jetty.http.HttpTester;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.foundation.modules.webmvc.test.model.FilePart;
import org.gs4tr.termmanager.webservice.utils.ParameterPart;
import org.gs4tr.foundation.modules.webmvc.test.model.Part;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportTypeEnum;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.persistence.update.ImportOptionsModel;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.webservice.model.request.ImportCommand;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class ImportDocumentControllerTest extends AbstractWebServiceTest {

    private static final String IMPORT_DOCUMENT_URL = "rest/v2/import";

    @Test
    @TestCase("importDocument")
    public void importDocumentFailedInvalidCommand() throws Exception {

	ImportCommand importCommand = getModelObject("invalidImportCommand", ImportCommand.class);

	importCommand.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(importCommand);

	File temp = File.createTempFile("testFile", ".tbx");

	FileInputStream inputFile = new FileInputStream(temp);

	FilePart filePart = new FilePart("file", inputFile);
	ParameterPart objectPart = new ParameterPart("command", requestContent);

	Collection<Part> parts = new ArrayList<>();
	parts.add(filePart);
	parts.add(objectPart);

	HttpTester.Request request = createMultipartRequest(IMPORT_DOCUMENT_URL, parts, Collections.emptyMap());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertFalse(responseData.get("success").asBoolean());
	assertEquals("Project ticket can't be empty or null.", responseData.get("errorMessage").asText());

    }

    @Test
    @TestCase("importDocument")
    public void importDocumentFailedProjectLocked() throws IOException {

	when(getImportTermService().isLocked(1L)).thenReturn(true);

	ImportCommand importCommand = getModelObject("importCommand", ImportCommand.class);

	importCommand.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(importCommand);

	File temp = File.createTempFile("testFile", ".tbx");

	FileInputStream inputFile = new FileInputStream(temp);

	FilePart filePart = new FilePart("file", inputFile);
	ParameterPart objectPart = new ParameterPart("command", requestContent);

	Collection<Part> parts = new ArrayList<>();
	parts.add(filePart);
	parts.add(objectPart);

	HttpTester.Request request = createMultipartRequest(IMPORT_DOCUMENT_URL, parts, Collections.emptyMap());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(423, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertFalse(responseData.get("success").asBoolean());
	assertEquals("The project is locked.", responseData.get("errorMessage").asText());
	assertEquals("3", responseData.get("returnCode").asText());

    }

    @Test
    @TestCase("importDocument")
    public void importDocumentFailsIOException() throws Exception {

	ImportCommand importCommand = getModelObject("importCommand", ImportCommand.class);
	ImportSummary importSummary = getModelObject("importSummary", ImportSummary.class);

	importCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);

	when(getProject(anyLong(), getProjectService())).thenReturn(project);
	when(getImportTermService().importDocumentWS(anyObject(), anyObject(), anyLong(), eq(ImportTypeEnum.TBX),
		eq(SyncOption.MERGE))).thenThrow(new IOException());

	String requestContent = OBJECT_MAPPER.writeValueAsString(importCommand);

	File temp = File.createTempFile("testFile", ".tbx");

	FileInputStream inputFile = new FileInputStream(temp);

	FilePart filePart = new FilePart("file", inputFile);
	ParameterPart objectPart = new ParameterPart("command", requestContent);

	Collection<Part> parts = new ArrayList<>();
	parts.add(filePart);
	parts.add(objectPart);

	HttpTester.Request request = createMultipartRequest(IMPORT_DOCUMENT_URL, parts, Collections.emptyMap());

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(500, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertNotNull(responseData.get("errorMessage"));

    }

    @Test
    @TestCase("importDocument")
    public void importDocumentSuccess() throws Exception {

	ImportCommand importCommand = getModelObject("importCommand", ImportCommand.class);
	ImportSummary importSummary = getModelObject("importSummary", ImportSummary.class);

	importCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("tmProject", TmProject.class);

	when(getProject(anyLong(), getProjectService())).thenReturn(project);
	when(getImportTermService().importDocumentWS(anyObject(), anyObject(), anyLong(), eq(ImportTypeEnum.TBX),
		eq(importCommand.getSyncOption()))).thenReturn(importSummary);

	ArgumentCaptor<ImportOptionsModel> argument = ArgumentCaptor.forClass(ImportOptionsModel.class);

	String requestContent = OBJECT_MAPPER.writeValueAsString(importCommand);

	File temp = File.createTempFile("testFile", ".tbx");

	FileInputStream inputFile = new FileInputStream(temp);

	FilePart filePart = new FilePart("file", inputFile);
	ParameterPart objectPart = new ParameterPart("command", requestContent);

	Collection<Part> parts = new ArrayList<>();
	parts.add(filePart);
	parts.add(objectPart);

	HttpTester.Request request = createMultipartRequest(IMPORT_DOCUMENT_URL, parts, Collections.emptyMap());
	// request.setHeader("Content-Type", "application/json");
	// request.setHeader("Content-Type", "multipart/form-data");
	// request.setHeader("Accept", "application/json");

	HttpTester.Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());
	assertNotNull(responseData.get("importSummary"));

	verify(getImportTermService(), atLeastOnce()).importDocumentWS(argument.capture(), Mockito.anyObject(),
		Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());

	assertEquals(10, argument.getValue().getSynonymNumber());

    }

}
