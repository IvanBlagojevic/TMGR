package org.gs4tr.termmanager.webservice.controllers;

import static org.gs4tr.foundation.modules.webmvc.test.utils.RequestHelper.sendRecieve;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.INVALID_PARAMETERS;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.OK;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.RETURN_CODE;
import static org.gs4tr.termmanager.webservice.model.response.ReturnCode.UNAUTHORIZED_ACCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.http.HttpTester.Request;
import org.eclipse.jetty.http.HttpTester.Response;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestCase;
import org.gs4tr.foundation.modules.entities.model.test.annotations.TestSuite;
import org.gs4tr.termmanager.model.Attribute;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.webservice.model.request.AddTermsCommand;
import org.gs4tr.termmanager.webservice.model.response.ReturnCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("webservice")
public class AddTermEntryControllerTest extends AbstractWebServiceTest {

    private static final String ADD_COMMAND = "ADD";

    private static final String ADD_TERM_URL = "rest/v2/addTerm";

    private static final List<String> DESCRIPTION_TYPES = Arrays.asList("definition", "context");

    private static final List<String> DESCRIPTION_VALUES = Arrays.asList("term context", "term entry definition");

    private static final List<String> TERM_VALUES = Arrays.asList("source test term", "target test term");

    /*
     * TERII-5087 Rest V2: Unable to add terms with attributes | User should not be
     * able to add Terms with Attributes or Notes that are not defined in project
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @TestCase("addTerm")
    public void addTermInvalidAttributesTest() throws IOException {

	AddTermsCommand addCommand = getModelObject("addTermsCommand1", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	List<Attribute> attributes = new ArrayList<>();

	when(getProjectService().load(anyLong())).thenReturn(project);

	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	verify(getTermEntyService(), never()).updateTermEntries(anyList(), anyString(), anyLong(), any(Action.class));

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(400, response.getStatus());

	assertEquals("Project doesn't contain selected term entry description type.",
		responseData.get("errorMessage").asText());

	assertFalse(responseData.get("success").asBoolean());

	assertEquals(UNAUTHORIZED_ACCESS, responseData.get("returnCode").asInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @TestCase("addTerm")
    public void addTermInvalidComboAttributesTest() throws IOException {

	AddTermsCommand addCommand = getModelObject("addComboAttributesTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	Attribute attribute = getModelObject("projectComboAttribute", Attribute.class);
	attribute.setComboValues("val4|val5");

	List<Attribute> attributes = Arrays.asList(attribute);

	when(getProjectService().load(anyLong())).thenReturn(project);

	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	verify(getTermEntyService(), never()).updateTermEntries(anyList(), anyString(), anyLong(), any(Action.class));

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertEquals(400, response.getStatus());

	assertFalse(responseData.get("success").asBoolean());

	assertEquals("Project doesn't contain selected term description type.",
		responseData.get("errorMessage").asText());

	assertEquals(UNAUTHORIZED_ACCESS, responseData.get("returnCode").asInt());
    }

    /*
     * TERII-3608: WS | there is no warning for adding terms for the language that
     * doesn't exist on the project
     */
    @Test
    @TestCase("addTerm")
    public void addTermProjectUserLanguagesDoesNotExist() throws IOException {

	AddTermsCommand addCommand = getModelObject("addTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	when(getProjectService().load(anyLong())).thenReturn(project);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	assertEquals("Project doesn't contain selected language.", responseData.get("errorMessage").asText());

	assertEquals(UNAUTHORIZED_ACCESS, responseData.get(ReturnCode.RETURN_CODE).asInt());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @TestCase("addTerm")
    public void addTermValidComboAttributesTest() throws IOException {

	AddTermsCommand addCommand = getModelObject("addComboAttributesTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	Attribute attribute = getModelObject("projectComboAttribute", Attribute.class);

	List<Attribute> attributes = Arrays.asList(attribute);

	when(getProjectService().load(anyLong())).thenReturn(project);

	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(OK, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    /*
     * TERII-5970 restV2 | addTerm is not working
     */
    @Test
    @TestCase("addTerm")
    public void addTermWithDisplayNameStatusTest() throws IOException {
	AddTermsCommand addCommand = getModelObject("addApprovedTermsCommand", AddTermsCommand.class);
	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLocaleCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLocaleCaptor.capture(), projectIdCaptor.capture(), action.capture());

	assertEquals(200, response.getStatus());

	assertNull(sourceLocaleCaptor.getValue());

	assertTrue(projectIdCaptor.getValue().equals(1L));
	List<TranslationUnit> translationUnits = translationUnitCaptor.getValue();
	assertEquals(1, translationUnits.size());

	List<UpdateCommand> sourceTermUpdateCommands = translationUnits.get(0).getSourceTermUpdateCommands();
	assertEquals(1, sourceTermUpdateCommands.size());

	UpdateCommand updateCommand = sourceTermUpdateCommands.get(0);
	assertNotNull(updateCommand);
	assertNotNull(updateCommand.getMarkerId());
	assertNotNull(updateCommand.getParentMarkerId());

	String termValue = updateCommand.getValue();
	assertTrue(TERM_VALUES.contains(termValue));

	assertEquals(updateCommand.getStatus(), ItemStatusTypeHolder.PROCESSED.getName());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());

    }

    @Test
    @TestCase("addTerm")
    public void addTermWithEmptyStringDescriptionBaseType() throws Exception {
	AddTermsCommand addCommand = getModelObject("emptyStringBaseTypeAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(OK, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    @TestCase("addTerm")
    public void addTermWithEmptyStringDescriptionType() throws Exception {
	AddTermsCommand addCommand = getModelObject("emptyStringTypeAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(0)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    @TestCase("addTerm")
    public void addTermWithEmptyStringDescriptionValue() throws Exception {
	AddTermsCommand addCommand = getModelObject("emptyStringValueAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(0)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    /*
     * TERII-5116 Rest V2: Able to add terms with null attribute values and
     * attribute definitions
     */
    @Test
    @TestCase("addTerm")
    public void addTermWithNullDescriptionBaseType() throws Exception {
	AddTermsCommand addCommand = getModelObject("nullBaseTypeAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(OK, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    @TestCase("addTerm")
    public void addTermWithNullDescriptionType() throws Exception {
	AddTermsCommand addCommand = getModelObject("nullTypeAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(0)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    @TestCase("addTerm")
    public void addTermWithNullDescriptionValue() throws Exception {
	AddTermsCommand addCommand = getModelObject("nullValueAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(0)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    public void addTerm_invalid_case1() throws Exception {
	AddTermsCommand addCommand = new AddTermsCommand();

	addCommand.setSecurityTicket(getSecurityTicket());

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(INVALID_PARAMETERS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    /*
     * TERII-3609: If term entry attribute doesn't exist on the project then return
     * UNAUTHORIZED_ACCESS
     */
    @Test
    @TestCase("addTerm")
    public void addTerm_invalid_case2() throws Exception {
	AddTermsCommand addCommand = getModelObject("addTermsCommand2", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().findProjectById(1L, Attribute.class)).thenReturn(project);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(400, response.getStatus());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertFalse(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(UNAUTHORIZED_ACCESS, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @Test
    @TestCase("addTerm")
    public void addTerm_valid_case1() throws Exception {
	AddTermsCommand addCommand = getModelObject("validAddTermsCommand", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(anyListOf(TranslationUnit.class), anyString(),
		anyLong(), action.capture());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(OK, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @TestCase("addTerm")
    public void addTerm_valid_case2() throws IOException {

	AddTermsCommand addCommand = getModelObject("addTermsCommand1", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLocaleCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLocaleCaptor.capture(), projectIdCaptor.capture(), action.capture());

	assertEquals(200, response.getStatus());

	assertNull(sourceLocaleCaptor.getValue());

	assertTrue(projectIdCaptor.getValue().equals(1L));

	List<TranslationUnit> translationUnits = translationUnitCaptor.getValue();

	translationUnits.forEach(e -> assertTranslationUnit(e));

	assertEquals(1, translationUnits.size());

	assertEquals(Action.ADDED_REMOTELY, action.getValue());
    }

    @Before
    public void setUp() {
	reset(getTermEntyService());
	reset(getProjectService());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("addTerm")
    public void updateExistingTermEntryWithTermsAndDescriptions() throws IOException {

	AddTermsCommand addCommand = getModelObject("updateTermEntryRestV2", AddTermsCommand.class);

	addCommand.setSecurityTicket(getSecurityTicket());

	TmProject project = getModelObject("project", TmProject.class);

	@SuppressWarnings("unchecked")
	List<Attribute> attributes = getModelObject("attributes", List.class);

	when(getProjectService().load(anyLong())).thenReturn(project);
	when(getProjectService().getAttributesByProjectId(1L)).thenReturn(attributes);

	String requestContent = OBJECT_MAPPER.writeValueAsString(addCommand);

	Request request = createJsonRequest(ADD_TERM_URL, requestContent);

	Response response = sendRecieve(getLocalConnector(), request);

	assertEquals(200, response.getStatus());

	ArgumentCaptor<Action> action = ArgumentCaptor.forClass(Action.class);

	ArgumentCaptor<List> translationUnit = ArgumentCaptor.forClass(List.class);

	verify(getTermEntyService(), times(1)).updateTermEntries(translationUnit.capture(), anyString(), anyLong(),
		action.capture());

	List<TranslationUnit> units = translationUnit.getValue();

	assertEquals(Action.EDITED_REMOTELY, action.getValue());

	JsonNode responseData = OBJECT_MAPPER.readTree(response.getContent());

	assertTrue(responseData.get("success").asBoolean());

	JsonNode returnCode = responseData.get(RETURN_CODE);

	assertEquals(OK, returnCode.asInt());

	JsonNode jsonTime = responseData.get("time");

	assertNotNull(jsonTime);

    }

    private void assertTranslationUnit(TranslationUnit translationUnit) {
	assertNotNull(translationUnit);

	assertNotNull(translationUnit.getTermEntryId());

	List<UpdateCommand> updateCommands = translationUnit.getSourceTermUpdateCommands();

	assertTrue(updateCommands.stream().map(e -> e.getCommand()).allMatch(e -> e.equals(ADD_COMMAND)));
	assertTrue(updateCommands.stream().map(e -> e.getMarkerId()).allMatch(e -> e != null));
	assertTrue(updateCommands.stream().map(e -> e.getParentMarkerId()).allMatch(e -> e != null));

	updateCommands.forEach(e -> assertUpdateCommand(e));

	assertEquals(5, updateCommands.size());
    }

    private void assertUpdateCommand(UpdateCommand updateCommand) {
	switch (updateCommand.getTypeEnum().getName()) {
	case "description":
	    assertTrue(DESCRIPTION_TYPES.contains(updateCommand.getSubType()));
	    assertTrue(DESCRIPTION_VALUES.contains(updateCommand.getValue()));
	    break;
	case "term":
	    assertTrue(TERM_VALUES.contains(updateCommand.getValue()));
	    // Not needed since we add default status in service
	    // assertEquals(ItemStatusTypeHolder.PROCESSED.getName(),
	    // updateCommand.getStatus());
	    break;
	default:
	    fail("code should not reach here");
	}
    }
}
