package org.gs4tr.termmanager.webmvc.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Metadata;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation3.core.utils.IdEncrypter;
import org.gs4tr.termmanager.model.MultilingualTerm;
import org.gs4tr.termmanager.model.TermSearchRequest;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.service.MultilingualViewModelService;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.webmvc.configuration.UrlConstants;
import org.gs4tr.termmanager.webmvc.model.TestCase;
import org.gs4tr.termmanager.webmvc.model.TestSuite;
import org.gs4tr.termmanager.webmvc.model.commands.DateRange;
import org.gs4tr.termmanager.webmvc.model.commands.SearchCommand;
import org.gs4tr.termmanager.webmvc.model.commands.SearchItem;
import org.gs4tr.termmanager.webmvc.model.search.DoubleMultiComboBoxDefaultValue;
import org.gs4tr.termmanager.webmvc.model.search.InputTextAndComboItem;
import org.gs4tr.termmanager.webmvc.model.search.LinkedComboBoxDefaultValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("controllers")
public class MultilingualViewSearchControllerTest extends AbstractControllerTest {

    public static final String SUBMISSION_TICKET_ID = IdEncrypter.encryptGenericId(1L);

    private static final boolean GRID_FEATURE = false;

    @Autowired
    private MultilingualViewModelService _multilingualViewModelService;

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewBasicSearch() throws Exception {
	String fromDate = "null";
	String toDate = "null";

	String jsonData = getJsonData("multilingualViewSearchComplex.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode success = resultNode.get("success");
	Assert.assertTrue(success.asBoolean());

	assertNumberOfColumn(resultNode, 21);

	validateResults(resultNode, true);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewSearchCommand() throws Exception {

	String jsonData = getJsonData("multilingualViewSearchFilter.json");

	SearchCommand command = new SearchCommand();
	command.setJsonData(jsonData);

	MultilingualViewSearchFilterTestClass testClass = new MultilingualViewSearchFilterTestClass();
	testClass.performTest(command);

	List<SearchItem> filterSearch = command.getFilterSearch();
	SearchItem searchItem = filterSearch.get(1);
	LinkedComboBoxDefaultValue commandSearch = command.getLanguageDirection();

	// Assert source/target locales
	String commandSourceLocale = commandSearch.getValue1();
	String filterSourceLocale = searchItem.getValues().get(0).getValues().get(0);
	assertEquals(commandSourceLocale, filterSourceLocale);
	List<String> commandTargetLocales = commandSearch.getValue2();
	List<String> filterTargetLocales = searchItem.getValues().get(1).getValues();
	assertEquals(commandTargetLocales, filterTargetLocales);

	// Assert hide blanks
	List<String> commandHideBlanks = command.getHideBlanks();
	List<String> filterHideBlanks = filterSearch.get(9).getValues().get(0).getValues();
	assertEquals(commandHideBlanks, filterHideBlanks);

	// Assert creationUsers
	List<String> commandCreationUsers = command.getCreationUsers();
	List<String> filterCreationUsers = filterSearch.get(3).getValues().get(0).getValues();
	assertEquals(commandCreationUsers, filterCreationUsers);

	// Assert modification users
	List<String> commandModificationUsers = command.getModificationUsers();
	List<String> filterModificationUsers = filterSearch.get(6).getValues().get(0).getValues();
	assertEquals(commandModificationUsers, filterModificationUsers);

	// Assert date created range
	DateRange commandDateRange = command.getDateCreatedRange();
	String fromDate = filterSearch.get(4).getValues().get(0).getValues().get(0);
	assertEquals(Long.toString(commandDateRange.getFromDate()), fromDate);
	String toDate = filterSearch.get(4).getValues().get(1).getValues().get(0);
	assertEquals(Long.toString(commandDateRange.getToDate()), toDate);

	// Assert date modification range
	commandDateRange = command.getDateModifiedRange();
	fromDate = filterSearch.get(5).getValues().get(0).getValues().get(0);
	assertEquals(Long.toString(commandDateRange.getFromDate()), fromDate);
	toDate = filterSearch.get(5).getValues().get(1).getValues().get(0);
	assertEquals(Long.toString(commandDateRange.getToDate()), toDate);

	// Assert entity type
	DoubleMultiComboBoxDefaultValue commandEntityType = command.getEntityType();
	List<String> entitiesSearchIn = filterSearch.get(0).getValues().get(0).getValues();
	List<String> entitiesInclude = filterSearch.get(0).getValues().get(1).getValues();
	assertEquals(commandEntityType.getValue2(), entitiesInclude);
	assertEquals(commandEntityType.getValue1(), entitiesSearchIn);

	// Assert entity types
	List<String> commandProjectComboBox = command.getProjectComboBox();
	List<String> filterProjectComboBox = filterSearch.get(2).getValues().get(0).getValues();
	assertEquals(commandProjectComboBox, filterProjectComboBox);

	// Assert textAndComboItem
	InputTextAndComboItem commandInputTextAndComboItem = command.getTermNameAndSearchType();
	String textValue = filterSearch.get(7).getValues().get(0).getValues().get(0);
	String boxValue = filterSearch.get(7).getValues().get(1).getValues().get(0);
	assertEquals(commandInputTextAndComboItem.getValue1(), textValue);
	assertEquals(commandInputTextAndComboItem.getValue2(), boxValue);

	// Assert statuses
	List<String> commandStatuses = command.getStatuses();
	List<String> filterStatuses = filterSearch.get(8).getValues().get(0).getValues();
	assertEquals(commandStatuses, filterStatuses);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewComplexSearchControllerWithDatesTest() throws Exception {

	String fromDate = "1271196000000";
	String toDate = "1397512740000";

	String jsonData = getJsonData("multilingualViewSearchComplex.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));
	verify(getUserProfileService(), atLeastOnce())
		.findById(TmUserProfile.getCurrentUserProfile().getUserProfileId());

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	validateResults(resultNode, true);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewComplexSearchControllerWithoutDatesTest() throws Exception {

	String fromDate = "null";
	String toDate = "null";

	String jsonData = getJsonData("multilingualViewSearchComplex.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	validateResults(resultNode, true);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewOnlySource() throws Exception {
	String fromDate = "null";
	String toDate = "null";

	String jsonData = getJsonData("multilingualViewSearchOnlySource.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode success = resultNode.get("success");
	Assert.assertTrue(success.asBoolean());

	assertNumberOfColumn(resultNode, 15);

	validateResults(resultNode, false);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewSearchControllerTest() throws Exception {

	String jsonData = getJsonData("multilingualViewSearch.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));
	verify(getUserProfileService(), atLeastOnce())
		.findById(TmUserProfile.getCurrentUserProfile().getUserProfileId());

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	validateResults(resultNode, true);
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void multilingualViewSearchGridTest() throws Exception {

	String jsonData = getJsonData("multilingualViewSearchGrid.json");
	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);

	if (GRID_FEATURE) {
	    ResultActions resultActions = _mockMvc.perform(post);

	    verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		    any(PagedListInfo.class));

	    resultActions.andExpect(status().isOk());

	    MockHttpServletResponse response = resultActions.andReturn().getResponse();
	    String result = response.getContentAsString();
	    assertNotNull(result);

	    JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	    assertNotNull(resultNode);

	    JsonNode success = resultNode.get("success");
	    Assert.assertTrue(success.asBoolean());

	    validateResults(resultNode, true);
	}

    }

    /*
     * TERII-5789 Backend - Disable sorting indicator after search is applied
     */
    @Test
    @TestCase("multilingualViewSearch")
    public void searchWithTermAttributeFilter() throws Exception {
	String fromDate = "null";
	String toDate = "null";

	String metadataStr = getJsonData("userProfileMetadata.json");

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Metadata metadata = new Metadata();
	metadata.setKey("term_list");
	metadata.setValue(metadataStr);

	userProfile.setMetadata(Collections.singletonList(metadata));

	String jsonData = getJsonData("multilingualViewSearchComplex.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode termNameAndSearchType = resultNode.get("gridConfig");
	assertNotNull(termNameAndSearchType);

	Iterator<JsonNode> gridConfigNodes = termNameAndSearchType.elements();

	// Test if all columns are sortable
	JsonNode sortableNodes = gridConfigNodes.next();
	boolean sortable = Boolean.parseBoolean(sortableNodes.get("sortable").asText());
	assertFalse(sortable);

    }

    /*
     * TERII-5789 Backend - Disable sorting indicator after search is applied
     */
    @Test
    @TestCase("multilingualViewSearch")
    public void searchWithoutTermAttributeFilter() throws Exception {
	String fromDate = "null";
	String toDate = "null";

	String metadataStr = getJsonData("userProfileMetadata.json");

	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Metadata metadata = new Metadata();
	metadata.setKey("term_list");
	metadata.setValue(metadataStr);

	userProfile.setMetadata(Collections.singletonList(metadata));

	String jsonData = getJsonData("multilingualViewSearchNoTermAttributeFilter.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode termNameAndSearchType = resultNode.get("gridConfig");
	assertNotNull(termNameAndSearchType);

	Iterator<JsonNode> gridConfigNodes = termNameAndSearchType.elements();
	// Test if all columns are sortable
	JsonNode sortableNodes = gridConfigNodes.next();
	boolean sortable = Boolean.parseBoolean(sortableNodes.get("sortable").asText());
	assertTrue(sortable);
    }

    @Before
    public void setUp() throws Exception {
	reset(getUserProfileService());
	reset(getMultilingualViewModelService());
	mockObjects();
    }

    @Test
    @TestCase("multilingualViewSearch")
    public void submissionViewBasicSearch() throws Exception {
	String fromDate = "null";
	String toDate = "null";

	String jsonData = getJsonData("submissionSearchComplex.json",
		new String[] { "$submissionTicket", SUBMISSION_TICKET_ID }, new String[] { "$fromDate", fromDate },
		new String[] { "$toDate", toDate });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/" + UrlConstants.MULTILINGUAL_SEARCH);
	post.param("jsonData", jsonData);
	post.param("searchUserLatestChanges", "true");

	ResultActions resultActions = _mockMvc.perform(post);

	verify(getMultilingualViewModelService(), atLeastOnce()).search(any(TermSearchRequest.class),
		any(PagedListInfo.class));

	resultActions.andExpect(status().isOk());

	MockHttpServletResponse response = resultActions.andReturn().getResponse();
	String result = response.getContentAsString();
	assertNotNull(result);

	JsonNode resultNode = JsonUtils.readValue(result, JsonNode.class);
	assertNotNull(resultNode);

	JsonNode success = resultNode.get("success");
	Assert.assertTrue(success.asBoolean());

	assertNumberOfColumn(resultNode, 18);

	validateResults(resultNode, true);
    }

    private void assertNumberOfColumn(JsonNode resultNode, int ecpectedColumnNumber) {
	JsonNode gridConfigNode = resultNode.get("gridConfig");
	assertNotNull(gridConfigNode);
	Iterator<JsonNode> gridConfigNodes = gridConfigNode.elements();
	assertNotNull(gridConfigNodes);
	Assert.assertTrue(gridConfigNodes.hasNext());

	int columnNumber = 0;
	while (gridConfigNodes.hasNext()) {
	    JsonNode node = gridConfigNodes.next();

	    assertNotNull(node.findValue("dataIndex"));
	    assertNotNull(node.findValue("header"));
	    assertNotNull(node.findValue("width"));
	    assertNotNull(node.findValue("hidden"));
	    assertNotNull(node.findValue("systemHidden"));
	    assertNotNull(node.findValue("sortable"));
	    assertNotNull(node.findValue("sortProperty"));
	    columnNumber++;
	}

	Assert.assertEquals(ecpectedColumnNumber, columnNumber);
    }

    private MultilingualViewModelService getMultilingualViewModelService() {
	return _multilingualViewModelService;
    }

    private void mockObjects() {

	TmUserProfile profile = new TmUserProfile();
	profile.setHasChangedTerms(true);

	MultilingualTerm multilingualTerm1 = getModelObject("multilingualTerm1", MultilingualTerm.class);
	MultilingualTerm[] multilingualTerms = { multilingualTerm1 };

	PagedList<MultilingualTerm> entityPagedList = new PagedList<MultilingualTerm>();
	entityPagedList.setElements(multilingualTerms);
	entityPagedList.setPagedListInfo(new PagedListInfo());
	entityPagedList.setTotalCount(1L);

	TaskPagedList<MultilingualTerm> tmTaskPageList = new TaskPagedList<MultilingualTerm>(entityPagedList);

	when(getMultilingualViewModelService().search(any(TermSearchRequest.class), any(PagedListInfo.class)))
		.thenReturn(tmTaskPageList);
	when(getUserProfileService().findById(TmUserProfile.getCurrentUserProfile().getUserProfileId()))
		.thenReturn(profile);
    }

    private void validateResults(JsonNode resultNode, boolean validateTarget) {
	JsonNode successNode = resultNode.get("success");
	assertNotNull(successNode);
	Assert.assertTrue(successNode.asBoolean());

	JsonNode itemsNode = resultNode.get("items");
	assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    assertNotNull(element.findValue("ticket"));
	    assertNotNull(element.findValue("sourceAlignment"));
	    assertNotNull(element.findValue("sourceTermTooltip"));

	    if (validateTarget) {
		JsonNode targetTermNode = element.get("targetTerms");
		assertNotNull(targetTermNode);

		Iterator<JsonNode> targetTerms = targetTermNode.elements();
		Assert.assertTrue(targetTerms.hasNext());

		while (targetTerms.hasNext()) {
		    JsonNode targetTerm = targetTerms.next();

		    assertNotNull(targetTerm);
		    assertNotNull(targetTerm.findValue("targetTerm"));
		    assertNotNull(targetTerm.findValue("targetTermTooltip"));
		    assertNotNull(targetTerm.findValue("targetAlignment"));
		    assertNotNull(targetTerm.findValue("targetSynonyms"));
		    assertNotNull(targetTerm.findValue("submissionId"));
		    assertNotNull(targetTerm.findValue("languageId"));
		    assertNotNull(targetTerm.findValue("submissionName"));

		    JsonNode firstTargetTerm = targetTerm.findValue("targetTerm");
		    validateTerm(firstTargetTerm);
		}
	    }

	    assertNotNull(element.findValue("targetTermTooltip"));
	    assertNotNull(element.findValue("sourceSynonyms"));
	    assertNotNull(element.findValue("sourceTerm"));

	    JsonNode sourceTermNode = element.get("sourceTerm");
	    validateSourceTerm(sourceTermNode);

	    assertNotNull(element.findValue("targetSynonyms"));
	    assertNotNull(element.findValue("termEntryTicket"));
	    assertNotNull(element.findValue("availableTasks"));
	    assertNotNull(element.findValue("inTranslation"));
	    assertNotNull(element.findValue("sourceInTranslation"));
	    assertNotNull(element.findValue("projectTicket"));
	    assertNotNull(element.findValue("projectName"));

	}
    }

    private void validateSourceTerm(JsonNode node) {
	assertNotNull(node);

	assertNotNull(node.findValue("language"));

	JsonNode languageNode = node.get("language");
	assertNotNull(languageNode);
	assertNotNull(languageNode.findValue("value"));
	assertNotNull(languageNode.findValue("locale"));

	assertNotNull(node.findValue("forbidden"));
	assertNotNull(node.findValue("status"));
	assertNotNull(node.findValue("markerId"));
	assertNotNull(node.findValue("modificationDate"));
	assertNotNull(node.findValue("creationDate"));
	assertNotNull(node.findValue("termName"));
	assertNotNull(node.findValue("termTicket"));
    }

    private void validateTerm(JsonNode node) {
	assertNotNull(node);

	assertNotNull(node.findValue("language"));

	JsonNode languageNode = node.get("language");
	assertNotNull(languageNode);
	assertNotNull(languageNode.findValue("value"));
	assertNotNull(languageNode.findValue("locale"));

	assertNotNull(node.findValue("submissionDate"));
	assertNotNull(node.findValue("completedDate"));
	assertNotNull(node.findValue("commited"));
	assertNotNull(node.findValue("forbidden"));
	assertNotNull(node.findValue("submitter"));
	assertNotNull(node.findValue("assignee"));
	assertNotNull(node.findValue("markerId"));
	assertNotNull(node.findValue("modificationDate"));
	assertNotNull(node.findValue("creationDate"));
	assertNotNull(node.findValue("status"));
	assertNotNull(node.findValue("canceled"));
	assertNotNull(node.findValue("comments"));
	assertNotNull(node.findValue("termName"));
	assertNotNull(node.findValue("termTicket"));
    }

    private class MultilingualViewSearchFilterTestClass extends MultilingualViewSearchController {
	private void performTest(SearchCommand command) {
	    setCommandFields(command);
	}
    }

}
