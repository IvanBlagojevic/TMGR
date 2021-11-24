package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.manualtask.ChangeTermStatusTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.ChangeTermStatusCommands;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class ChangeTermStatusTaskHandlerTest extends AbstractManualtaskTest {

    @Autowired
    private ChangeTermStatusTaskHandler _handler;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    @Test
    @TestCase("changeTermStatusTaskHandler")
    public void changeStatusOfAllSourceAndTargetTermsByTermIdsTest() {

	ChangeTermStatusCommands commands = getModelObject("commands", ChangeTermStatusCommands.class);

	List<String> languages = getModelObject("languages", List.class);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getChangeStatusCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(Arrays.asList(termEntry));

	getHandler().processTasks(new Long[] { projectId }, null, commands, null);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLanguageCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLanguageCaptor.capture(), projectIdCaptor.capture(), actionCaptor.capture());

	assertEquals(projectId, projectIdCaptor.getValue());

	assertEquals(commands.getSourceLanguage(), sourceLanguageCaptor.getValue());

	assertEquals(Action.EDITED, actionCaptor.getValue());

	TranslationUnit translationUnit = (TranslationUnit) translationUnitCaptor.getValue().get(0);

	List<UpdateCommand> updateCommands = translationUnit.getSourceTermUpdateCommands();

	assertEquals(6, updateCommands.size());

	for (int i = 0; i < updateCommands.size(); i++) {
	    if (languages.contains(updateCommands.get(i).getLanguageId())) {
		languages.remove(updateCommands.get(i).getLanguageId());
	    }
	    assertEquals(UpdateCommand.CommandEnum.UPDATE.getName(), updateCommands.get(i).getCommand().toLowerCase());
	}

	assertTrue(languages.size() == 0);

    }

    @Test
    @TestCase("changeTermStatusTaskHandler")
    public void changeStatusOfAllSourceAndTargetTermsSynonymsTest() {

	ChangeTermStatusCommands commands = getModelObject("commands", ChangeTermStatusCommands.class);

	commands.setIncludeTargetSynonyms(true);
	commands.setIncludeSourceSynonyms(true);

	// Remove all termIds because they should be changed by languages
	commands.getChangeStatusCommands().get(0).setTermIds(new ArrayList<>());

	Long projectId = 1L;

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getChangeStatusCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(Arrays.asList(termEntry));

	getHandler().processTasks(new Long[] { projectId }, null, commands, null);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLanguageCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLanguageCaptor.capture(), projectIdCaptor.capture(), actionCaptor.capture());

	assertEquals(projectId, projectIdCaptor.getValue());

	assertEquals(commands.getSourceLanguage(), sourceLanguageCaptor.getValue());

	assertEquals(Action.EDITED, actionCaptor.getValue());

	TranslationUnit translationUnit = (TranslationUnit) translationUnitCaptor.getValue().get(0);

	List<UpdateCommand> updateCommands = translationUnit.getSourceTermUpdateCommands();

	// All terms should be changed
	assertEquals(6, updateCommands.size());
    }

    @Test
    @TestCase("changeTermStatusTaskHandler")
    public void changeStatusOfSingleTermByTermIdTest() {

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	List<TermEntry> termEntryList = new ArrayList<>();
	termEntryList.add(termEntry);

	ChangeTermStatusCommands commands = getModelObject("commands", ChangeTermStatusCommands.class);
	commands.setTargetLanguages(new ArrayList<>());
	commands.setSourceLanguage("");

	TermCommandPerProject changeTermStatusCommand = commands.getChangeStatusCommands().get(0);

	String termId = getModelObject("termId_01", String.class);
	List<String> termIds = new ArrayList<>();
	termIds.add(termId);
	changeTermStatusCommand.setTermIds(termIds);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getChangeStatusCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(termEntryList);

	getHandler().processTasks(new Long[] { projectId }, null, commands, null);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLanguageCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLanguageCaptor.capture(), projectIdCaptor.capture(), actionCaptor.capture());

	assertEquals(projectId, projectIdCaptor.getValue());

	assertEquals(commands.getSourceLanguage(), sourceLanguageCaptor.getValue());

	assertEquals(Action.EDITED, actionCaptor.getValue());

	TranslationUnit translationUnit = (TranslationUnit) translationUnitCaptor.getValue().get(0);

	List<UpdateCommand> updateCommands = translationUnit.getSourceTermUpdateCommands();

	// Only term with specific termId should be changed
	assertEquals(1, updateCommands.size());

	UpdateCommand updateCommand = updateCommands.get(0);

	assertEquals(UpdateCommand.CommandEnum.UPDATE.getName(), updateCommand.getCommand().toLowerCase());

	assertEquals("en-US", updateCommand.getLanguageId());

	assertEquals(termId, updateCommand.getMarkerId());
    }

    @Test
    @TestCase("changeTermStatusTaskHandler")
    public void changeStatusOfSourceByLanguageIdAndTargetByTermIdsTest() {

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	List<TermEntry> termEntryList = new ArrayList<>();
	termEntryList.add(termEntry);

	ChangeTermStatusCommands commands = getModelObject("commands", ChangeTermStatusCommands.class);

	/* Source terms should be changed by languageIds */
	commands.setIncludeSourceSynonyms(true);

	commands.setTargetLanguages(new ArrayList<>());

	TermCommandPerProject changeTermStatusCommand = commands.getChangeStatusCommands().get(0);

	String termId = getModelObject("termId_03", String.class);
	List<String> termIds = new ArrayList<>();
	termIds.add(termId);
	changeTermStatusCommand.setTermIds(termIds);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getChangeStatusCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(termEntryList);

	getHandler().processTasks(new Long[] { projectId }, null, commands, null);

	ArgumentCaptor<List> translationUnitCaptor = ArgumentCaptor.forClass(List.class);
	ArgumentCaptor<String> sourceLanguageCaptor = ArgumentCaptor.forClass(String.class);
	ArgumentCaptor<Long> projectIdCaptor = ArgumentCaptor.forClass(Long.class);
	ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);

	verify(getTermEntryService(), times(1)).updateTermEntries(translationUnitCaptor.capture(),
		sourceLanguageCaptor.capture(), projectIdCaptor.capture(), actionCaptor.capture());

	assertEquals(projectId, projectIdCaptor.getValue());

	assertEquals(commands.getSourceLanguage(), sourceLanguageCaptor.getValue());

	assertEquals(Action.EDITED, actionCaptor.getValue());

	TranslationUnit translationUnit = (TranslationUnit) translationUnitCaptor.getValue().get(0);

	List<UpdateCommand> updateCommands = translationUnit.getSourceTermUpdateCommands();

	/*
	 * Three languages should be changed. Two source languages by languageId and one
	 * Target language by termId.
	 */
	assertEquals(3, updateCommands.size());

    }

    @Test
    @TestCase("changeTermStatusTaskHandler")
    public void deleteTermInWorkflowTest() {

	List<Term> terms = getModelObject("terms", List.class);

	/*
	 * Set status to final review. After this user should get proper message
	 */
	terms.get(0).setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());

	ChangeTermStatusCommands commands = getModelObject("commands", ChangeTermStatusCommands.class);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getChangeStatusCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(Arrays.asList(termEntry));

	Long projectId = 1L;

	boolean isUserException = false;
	try {
	    getHandler().processTasks(new Long[] { projectId }, null, commands, null);
	} catch (UserException e) {
	    isUserException = true;
	    assertEquals(MessageResolver.getMessage("TermsInWorkflowDeleteErrorMessage"), e.getDescription());
	}
	assertTrue(isUserException);

    }

    public ChangeTermStatusTaskHandler getHandler() {
	return _handler;
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    public TermService getTermService() {
	return _termService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getTermService());
	reset(getTermEntryService());
    }
}
