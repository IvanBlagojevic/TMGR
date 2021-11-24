package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TaskResponse;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.TermService;
import org.gs4tr.termmanager.service.manualtask.DeleteTermTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.DeleteTermCommands;
import org.gs4tr.termmanager.service.model.command.TermCommandPerProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

@TestSuite("manualtask")
public class DeleteTermTaskHandlerTest extends AbstractManualtaskTest {

    private static final String TASK_NAME = "disable term";

    @Autowired
    private DeleteTermTaskHandler _handler;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private TermService _termService;

    /*
     * TERII-5866: user tries to delete term entry that is already removed by
     * another user on same project
     */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteDeletedTermEntryTest() {

	DeleteTermCommands commands = getModelObject("commands1", DeleteTermCommands.class);
	TermEntry termEntry = getModelObject("termEntry02", TermEntry.class);
	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(Arrays.asList(termEntry));

	TaskResponse response = getHandler().processTasks(new Long[] { projectId }, null, commands, null);

	assertNotNull(response);

    }

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteTermInWorkflowTest() {

	List<Term> terms = getModelObject("terms", List.class);

	/*
	 * Set status to final review. After this user should get proper message
	 */
	terms.get(0).setStatus(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());

	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
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

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteTermsAndTermSynonymsTest() {

	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);

	commands.setIncludeTargetSynonyms(true);
	commands.setIncludeSourceSynonyms(true);

	Long projectId = 1L;

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
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

	// All terms should be deleted
	assertEquals(6, updateCommands.size());
    }

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteTermsByTermIds() {

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	List<TermEntry> termEntryList = new ArrayList<>();
	termEntryList.add(termEntry);

	// Remove all languageIds so all deleted terms must be deleted by
	// termIds
	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);
	commands.setTargetLanguages(new ArrayList<>());
	commands.setSourceLanguage("");

	TermCommandPerProject deleteTermCommand = commands.getDeleteCommands().get(0);

	// Set only one termId(term that should be deleted by termId)
	String termId = getModelObject("termId_01", String.class);
	List<String> termIds = new ArrayList<>();
	termIds.add(termId);
	deleteTermCommand.setTermIds(termIds);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
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

	// Only term with specific termId should be deleted
	assertEquals(1, updateCommands.size());

	UpdateCommand updateCommand = updateCommands.get(0);

	assertEquals(UpdateCommand.CommandEnum.REMOVE.getName(), updateCommand.getCommand().toLowerCase());

	assertEquals("en-US", updateCommand.getLanguageId());

	assertEquals(termId, updateCommand.getMarkerId());
    }

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteTermsNotSynonymsTest() {

	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);

	List<String> languages = getModelObject("languages", List.class);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
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

	/*
	 * At this point there should be only one languageId per language (languages
	 * that are not synonyms should not be deleted)
	 */

	for (int i = 0; i < updateCommands.size(); i++) {
	    languages.remove(updateCommands.get(i).getLanguageId());
	    assertEquals(UpdateCommand.CommandEnum.REMOVE.getName(), updateCommands.get(i).getCommand().toLowerCase());
	}

	assertTrue(languages.size() == 0);

    }

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteWholeTermEntryTest() {

	List<String> languageSet = getModelObject("languages", List.class);
	setNewUserProjectLanguages(languageSet, UserTypeEnum.ORGANIZATION);

	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);

	commands.setDeleteTermEntries(true);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
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

	// All Terms Should be deleted
	assertEquals(6, updateCommands.size());
    }

    /* Delete term in workflow should not be performed 5.1.0 RELEASE */
    @Test
    @TestCase("deleteTermTaskHandler")
    public void deleteWholeTermEntryWithNotVisibleTermsTest() {

	List<String> languageSet = getModelObject("languages", List.class);
	// Remove one language
	languageSet.remove(0);
	setNewUserProjectLanguages(languageSet, UserTypeEnum.ORGANIZATION);

	DeleteTermCommands commands = getModelObject("commands", DeleteTermCommands.class);

	commands.setDeleteTermEntries(true);

	TermEntry termEntry = getModelObject("termEntry01", TermEntry.class);

	Long projectId = 1L;

	when(getTermEntryService()
		.findTermentriesByIds(new ArrayList<>(commands.getDeleteCommands().get(0).getTermEntryIds()), 1L))
			.thenReturn(Arrays.asList(termEntry));

	boolean isUserException = false;
	try {
	    getHandler().processTasks(new Long[] { projectId }, null, commands, null);
	} catch (UserException e) {
	    isUserException = true;
	    assertEquals(MessageResolver.getMessage("AbstractTermTaskHandler.9"), e.getDescription());
	}

	assertTrue(isUserException);
    }

    public TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    @Before
    public void setUp() throws Exception {
	reset(getTermService());
	reset(getTermEntryService());
    }

    private DeleteTermTaskHandler getHandler() {
	return _handler;
    }

    private TermService getTermService() {
	return _termService;
    }

    private void setNewUserProjectLanguages(List<String> userLanguages, UserTypeEnum userType) {

	TmUserProfile userProfile = new TmUserProfile();
	Map<Long, Set<String>> languages = new HashMap<>();

	languages.put(1L, new HashSet<>(userLanguages));

	userProfile.setProjectUserLanguages(languages);

	UserInfo userInfo = new UserInfo();
	userInfo.setUserType(userType);
	userProfile.setUserInfo(userInfo);

	UserProfileContext.setCurrentUserProfile(userProfile);
    }

}
