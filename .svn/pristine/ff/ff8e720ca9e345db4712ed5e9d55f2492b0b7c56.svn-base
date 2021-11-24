package org.gs4tr.termmanager.service.reindex;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.service.AbstractRecodeOrCloneTermsTest;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.termmanager.service.solr.restore.IRestoreProcessorV2;
import org.gs4tr.termmanager.service.solr.restore.Messages;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RestoreProcessorV2RecodeOrCloneTest extends AbstractRecodeOrCloneTermsTest {

    @Autowired
    private PersistentStoreHandler _persistentStoreHandler;

    @Autowired
    private IRestoreProcessorV2 _restoreProcessorV2;

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    @Autowired
    private TransactionLogHandler _transactionLogHandler;

    @After
    public void afterTest() {
	unlockProject(1L);
	unlockProject(2L);
    }

    public SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    @Override
    public void setUp() throws SQLException {
	unlockProject(1L);
	unlockProject(2L);
	super.setUp();
    }

    @Test
    public void testRecodeOrClone() throws Exception {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();

	RecodeOrCloneCommand command1 = createRecodeOrCloneCommand("TES000001", 1L, "en", "en-GB");
	recodeCommands.add(command1);

	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();
	RecodeOrCloneCommand command2 = createRecodeOrCloneCommand("TES000002", 2L, "de", "de-DE");
	cloneCommands.add(command2);

	RecodeOrCloneCommand command3 = createRecodeOrCloneCommand("TES000002", 2L, "en", "en-GB");
	cloneCommands.add(command3);

	// Perform recode or clone operation
	getRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);

	Assert.assertFalse(getTransactionLogHandler().isLocked(1L));

	// Project should remain locked
	Assert.assertFalse(getTransactionLogHandler().isLocked(2L));

	List<TmProject> projectsAfter = getProjectService().findAllProjectsFetchLanguages();

	Assert.assertEquals(2, projectsAfter.size());

	TmProject projectAfter1 = projectsAfter.get(0);
	TmProject projectAfter2 = projectsAfter.get(1);

	Set<ProjectLanguage> projectLanguagesAfter1 = projectAfter1.getProjectLanguages();
	List<String> languagesAfter1 = projectLanguagesAfter1.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());
	Assert.assertFalse(languagesAfter1.contains("en"));
	Assert.assertTrue(languagesAfter1.contains("en-GB"));

	Set<ProjectLanguage> projectLanguagesAfter2 = projectAfter2.getProjectLanguages();
	List<String> languagesAfter2 = projectLanguagesAfter2.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());
	Assert.assertTrue(languagesAfter2.contains("de"));
	Assert.assertTrue(languagesAfter2.contains("de-DE"));

	Assert.assertTrue(languagesAfter2.contains("en"));
	Assert.assertTrue(languagesAfter2.contains("en-GB"));
    }

    @Test
    public void testRecodeOrCloneOneProjectAlreadyLocked() throws Exception {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();

	RecodeOrCloneCommand command = createRecodeOrCloneCommand("TES000001", 1L, "en", "en-GB");
	recodeCommands.add(command);

	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	// Lock fake project 2 to simulate another operation that is already started
	lockProject(2L);

	boolean isDetectedThatProjectIsLocked = false;
	try {
	    getRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);
	} catch (Exception e) {
	    isDetectedThatProjectIsLocked = true;
	}

	// Project in recode command is not already locked by another process
	Assert.assertFalse(isDetectedThatProjectIsLocked);

	Assert.assertFalse(getTransactionLogHandler().isLocked(1L));

	// Project should remain locked
	Assert.assertTrue(getTransactionLogHandler().isLocked(2L));
    }

    @Test
    public void testRecodeOrCloneProjectsAlreadyLocked() throws Exception {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();

	RecodeOrCloneCommand command = createRecodeOrCloneCommand("TES000001", 1L, "en", "en-GB");
	recodeCommands.add(command);

	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	// Lock project TES000001 to simulate another operation that is already started
	lockProject(1L);

	// Lock fake project 2
	lockProject(2L);

	boolean isDetectedThatProjectIsLocked = false;
	try {
	    // Try to perform recode or clone on already locked project
	    getRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);
	} catch (Exception e) {
	    Assert.assertEquals(String.format(Messages.getString("project.is.locked.m"), "TES000001"), e.getMessage());
	    isDetectedThatProjectIsLocked = true;
	}
	Assert.assertTrue(isDetectedThatProjectIsLocked);

	// Project should remain locked
	Assert.assertTrue(getTransactionLogHandler().isLocked(1L));
	Assert.assertTrue(getTransactionLogHandler().isLocked(2L));
    }

    @Test
    public void testRecodeOrCloneTwoCloneCommandsOnDifferentProj() throws Exception {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();

	RecodeOrCloneCommand command1 = createRecodeOrCloneCommand("TES000001", 1L, "en", "en-GB");
	recodeCommands.add(command1);

	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();
	RecodeOrCloneCommand command2 = createRecodeOrCloneCommand("TES000001", 1L, "de", "de-DE");
	cloneCommands.add(command2);

	RecodeOrCloneCommand command3 = createRecodeOrCloneCommand("TES000002", 2L, "fr", "fr-CA");
	cloneCommands.add(command3);

	// Perform recode or clone operation
	getRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);

	Assert.assertFalse(getTransactionLogHandler().isLocked(1L));

	// Project should remain locked
	Assert.assertFalse(getTransactionLogHandler().isLocked(2L));

	List<TmProject> projectsAfter = getProjectService().findAllProjectsFetchLanguages();

	Assert.assertEquals(2, projectsAfter.size());

	TmProject projectAfter1 = projectsAfter.get(0);
	TmProject projectAfter2 = projectsAfter.get(1);

	Set<ProjectLanguage> projectLanguagesAfter1 = projectAfter1.getProjectLanguages();
	List<String> languagesAfter1 = projectLanguagesAfter1.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());
	Assert.assertFalse(languagesAfter1.contains("en"));
	Assert.assertTrue(languagesAfter1.contains("en-GB"));
	Assert.assertTrue(languagesAfter1.contains("de"));
	Assert.assertTrue(languagesAfter1.contains("de-DE"));

	Set<ProjectLanguage> projectLanguagesAfter2 = projectAfter2.getProjectLanguages();
	List<String> languagesAfter2 = projectLanguagesAfter2.stream().map(ProjectLanguage::getLanguage)
		.collect(Collectors.toList());
	Assert.assertTrue(languagesAfter2.contains("fr"));
	Assert.assertTrue(languagesAfter2.contains("fr-CA"));
    }

    @Test
    public void testRecodeOrCloneWithLockedAndNotLockedProjects() {
	List<RecodeOrCloneCommand> recodeCommands = new ArrayList<>();

	RecodeOrCloneCommand command1 = createRecodeOrCloneCommand("TES000001", 1L, "en", "en-GB");
	recodeCommands.add(command1);

	RecodeOrCloneCommand command2 = createRecodeOrCloneCommand("TES000002", 2L, "en", "en-GB");
	recodeCommands.add(command2);

	List<RecodeOrCloneCommand> cloneCommands = new ArrayList<>();

	// Lock fake project 2 to simulate another operation that is already started
	lockProject(2L);

	boolean isDetectedThatProjectIsLocked = false;
	try {
	    getRestoreProcessorV2().restoreRecodeOrClone(recodeCommands, cloneCommands);
	} catch (Exception e) {
	    Assert.assertEquals(String.format(Messages.getString("project.is.locked.m"), "TES000002"), e.getMessage());
	    isDetectedThatProjectIsLocked = true;
	}

	Assert.assertTrue(isDetectedThatProjectIsLocked);

	Assert.assertFalse(getTransactionLogHandler().isLocked(1L));
	Assert.assertTrue(getTransactionLogHandler().isLocked(2L));
    }

    private RecodeOrCloneCommand createRecodeOrCloneCommand(String projectShortCode, Long projectId,
	    String languageFrom, String languageTo) {
	RecodeOrCloneCommand command = new RecodeOrCloneCommand();
	command.setProjectShortCode(projectShortCode);
	command.setProjectId(projectId);
	command.setLocaleFrom(languageFrom);
	command.setLocaleTo(languageTo);
	return command;
    }

    private PersistentStoreHandler getPersistentStoreHandler() {
	return _persistentStoreHandler;
    }

    private IRestoreProcessorV2 getRestoreProcessorV2() {
	return _restoreProcessorV2;
    }

    private TransactionLogHandler getTransactionLogHandler() {
	return _transactionLogHandler;
    }

    private void lockProject(Long projectId) {
	getTransactionLogHandler().startAppending(projectId, "power_user", "import", getRegularCollection());
    }

    private void unlockProject(Long projectId) {
	if (getTransactionLogHandler().isLocked(projectId)) {
	    getPersistentStoreHandler().closeAndClear(projectId);
	}
    }

    protected String getRegularCollection() {
	return getSolrConfig().getRegularCollection();
    }

}
