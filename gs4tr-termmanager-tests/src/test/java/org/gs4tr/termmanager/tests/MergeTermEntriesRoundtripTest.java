package org.gs4tr.termmanager.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gs4tr.foundation.modules.webmvc.model.ModelMapResponse;
import org.gs4tr.termmanager.model.TaskModel;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.manualtask.LookupTermTaskHandler;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.MergeManualTaskHandler;
import org.gs4tr.termmanager.service.manualtask.SaveDashboardManualTaskHandler;
import org.gs4tr.termmanager.service.model.command.LookupTermCommands;
import org.gs4tr.termmanager.service.model.command.MergeCommands;
import org.gs4tr.termmanager.service.model.command.SaveDashboardCommands;
import org.gs4tr.termmanager.service.utils.JsonUtils;
import org.gs4tr.termmanager.tests.model.TestCase;
import org.gs4tr.termmanager.tests.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;

@TestSuite("merge_term_entries_round_trip")
public class MergeTermEntriesRoundtripTest extends AbstractSolrGlossaryTest {

    private static final String MULTILINGUAL_SEARCH_URL = "/multilingualView.ter";

    @Test
    @TestCase("test")
    public void mergeTermEntriesRoundtripTest() throws Exception {

	String saveDashboardTaskName = "save dashboard";

	ManualTaskHandler taskHandler = getHandler(saveDashboardTaskName);
	assertEquals(taskHandler.getClass(), SaveDashboardManualTaskHandler.class);

	String projectTicket = TicketConverter.fromInternalToDto(PROJECT_ID);

	Object saveDashboardCommand = getTaskHandlerCommand(taskHandler, "saveDashboardCommand.json",
		new String[] { "$projectTicket", projectTicket });

	assertEquals(saveDashboardCommand.getClass(), SaveDashboardCommands.class);
	SaveDashboardCommands saveDashboardCommands = (SaveDashboardCommands) saveDashboardCommand;

	String termText = getSourceTermTextFromCommand(saveDashboardCommands);
	String sourceLanguageId = getSourceLanguageIdFromCommand(saveDashboardCommands);
	String targetLanguageId = getTargetLanguageIdFromCommand(saveDashboardCommands);

	// Saving new term entry
	ModelMapResponse saveResponse = saveNewTermEntry(taskHandler, saveDashboardCommand);
	assertNotNull(saveResponse);
	assertTrue(saveResponse.isSuccess());

	// Looking for eventually duplicate by source terms
	TaskModel[] taskModel = lookupForDuplicateTermEntry(termText, sourceLanguageId, projectTicket);
	assertNotNull(taskModel);

	assertTrue((Boolean) taskModel[0].getModel().get("matches"));

	// Getting term entry ids for matched term entries, from multilingual view
	List<String> termEntriesIds = createMultilingualView(sourceLanguageId, targetLanguageId, projectTicket);
	assertNotNull(termEntriesIds);
	assertEquals(3, termEntriesIds.size());

	TermEntry termEntry1BeforeMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(0), PROJECT_ID);
	TermEntry termEntry2BeforeMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(1), PROJECT_ID);
	TermEntry termEntry3BeforeMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(2), PROJECT_ID);

	assertNotNull(termEntry1BeforeMerging);
	assertNotNull(termEntry2BeforeMerging);
	assertNotNull(termEntry3BeforeMerging);

	// Merging term entries
	TaskResponse mergeTermEntriesResponse = mergeTermEntries(termEntriesIds, projectTicket, sourceLanguageId);
	assertNotNull(mergeTermEntriesResponse);

	// Term entries after merging
	TermEntry termEntry1AfterMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(0), PROJECT_ID);
	TermEntry termEntry2AfterMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(1), PROJECT_ID);
	TermEntry termEntry3AfterMerging = getTermEntryService().findTermEntryById(termEntriesIds.get(2), PROJECT_ID);

	assertEquals(0, termEntry1AfterMerging.getLanguageTerms().size());
	assertEquals(0, termEntry3AfterMerging.getLanguageTerms().size());
	assertEquals(4, termEntry2AfterMerging.getLanguageTerms().size());

    }

    private List<String> createMultilingualView(String sourceLanguageId, String targetLanguageId, String projectTicket)
	    throws Exception {

	String jsonData = getJsonData("multilingualViewSearch.json", new String[] { "$projectTicket", projectTicket },
		new String[] { "$sourceLanguageId", sourceLanguageId },
		new String[] { "$targetLanguageId", targetLanguageId });

	MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post(MULTILINGUAL_SEARCH_URL);
	post.param("jsonData", jsonData);

	ResultActions resultActions = _mockMvc.perform(post);
	String response = resultActions.andReturn().getResponse().getContentAsString();
	assertNotNull(response);

	List<String> termEntryIds = getTermEntryIdsFromResponse(response);

	return termEntryIds;

    }

    private String getProjectTicketFromCommand(SaveDashboardCommands commands) {
	long projectId = commands.getSaveDashboardCommands().get(0).getProjectId();
	return TicketConverter.fromInternalToDto(projectId);

    }

    private String getSourceLanguageIdFromCommand(SaveDashboardCommands commands) {
	TranslationUnit translationUnit = commands.getSaveDashboardCommands().get(0).getTranslationUnits().get(0);
	return translationUnit.getSourceTermUpdateCommands().get(0).getLanguageId();
    }

    private String getSourceTermTextFromCommand(SaveDashboardCommands commands) {
	TranslationUnit translationUnit = commands.getSaveDashboardCommands().get(0).getTranslationUnits().get(0);
	return translationUnit.getSourceTermUpdateCommands().get(0).getValue();

    }

    private String getTargetLanguageIdFromCommand(SaveDashboardCommands commands) {
	TranslationUnit translationUnit = commands.getSaveDashboardCommands().get(0).getTranslationUnits().get(0);
	return translationUnit.getTargetTermUpdateCommands().get(0).getLanguageId();
    }

    private String getTargetTermTextFromCommand(SaveDashboardCommands commands) {
	TranslationUnit translationUnit = commands.getSaveDashboardCommands().get(0).getTranslationUnits().get(0);
	return translationUnit.getTargetTermUpdateCommands().get(0).getValue();

    }

    private List<String> getTermEntryIdsFromResponse(String response) {

	List<String> termEntryIds = new ArrayList<>();

	HashMap<String, Object> multilingualViewResponse = JsonUtils.readValue(response, HashMap.class);
	Object items = multilingualViewResponse.get("items");

	JsonNode resultNode = JsonUtils.readValue(response, JsonNode.class);

	JsonNode itemsNode = resultNode.get("items");
	Assert.assertNotNull(itemsNode);

	Iterator<JsonNode> elements = itemsNode.elements();
	Assert.assertTrue(elements.hasNext());

	while (elements.hasNext()) {
	    JsonNode element = elements.next();
	    Assert.assertNotNull(element.findValue("termEntryTicket"));

	    String termEntryId = element.findValue("termEntryTicket").asText();
	    termEntryIds.add(termEntryId);

	}

	return termEntryIds;

    }

    private TaskModel[] lookupForDuplicateTermEntry(String termText, String languageId, String projectTicket) {

	String lookupTermTaskName = "lookup term";

	ManualTaskHandler lookupTermTaskHandler = getHandler(lookupTermTaskName);
	assertEquals(lookupTermTaskHandler.getClass(), LookupTermTaskHandler.class);

	Object lookupTermCommand = getTaskHandlerCommand(lookupTermTaskHandler, "lookupTerm.json",
		new String[] { "$projectTicket", projectTicket }, new String[] { "$languageId", languageId },
		new String[] { "$termName", termText });

	assertEquals(lookupTermCommand.getClass(), LookupTermCommands.class);

	TaskModel[] taskModel = lookupTermTaskHandler.getTaskInfos(null, lookupTermTaskName, lookupTermCommand);
	assertNotNull(taskModel);

	return taskModel;

    }

    private TaskResponse mergeTermEntries(List<String> termEntriesIds, String projectTicket, String languageId) {

	String mergeTaskName = "merge term entries";

	ManualTaskHandler manualTaskHandler = getHandler(mergeTaskName);
	assertEquals(manualTaskHandler.getClass(), MergeManualTaskHandler.class);

	Object command = getTaskHandlerCommand(manualTaskHandler, "multipleTermEntriesMergeCommand.json",
		new String[] { "$projectTicket", projectTicket }, new String[] { "$languageId", languageId },
		new String[] { "$matchedTermEntryId1", termEntriesIds.get(0) },
		new String[] { "$matchedTermEntryId2", termEntriesIds.get(1) },
		new String[] { "$matchedTermEntryId3", termEntriesIds.get(2) });

	assertEquals(command.getClass(), MergeCommands.class);

	TaskResponse taskResponse = manualTaskHandler.processTasks(null, null, command, null);
	return taskResponse;

    }

    private ModelMapResponse saveNewTermEntry(ManualTaskHandler taskHandler, Object command) {

	TaskResponse taskResponse = taskHandler.processTasks(null, null, command, null);
	assertNotNull(taskResponse);

	ModelMapResponse modelMap = new ModelMapResponse();
	modelMap.put("taskResponse", taskResponse);

	return modelMap;

    }

}
