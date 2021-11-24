package org.gs4tr.termmanager.service.reindex;

import java.util.Collections;

import org.gs4tr.foundation.modules.usermanager.service.SessionService;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TranslationUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CleanUpProcessorV2Test extends AbstractReIndex {

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SessionService _sessionService;

    @Autowired
    private TermEntryService _termEntryService;

    @Before
    public void prepare() {
	cleanUpDB();

	getSessionService().login("power", "password");

	TmProject project = getProjectService().findById(PROJECT_ID);
	if (!project.getProjectInfo().isEnabled()) {
	    getProjectService().enableProject(PROJECT_ID);
	}

	UpdateCommand srcCmd = new UpdateCommand();
	srcCmd.setValue("new term");
	srcCmd.setCommand(UpdateCommand.CommandEnum.ADD.getName());
	srcCmd.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	srcCmd.setItemType(UpdateCommand.TypeEnum.TERM.getName());
	srcCmd.setLanguageId("en-US");

	UpdateCommand trgCmd = new UpdateCommand();
	trgCmd.setValue("neuer Begriff");
	trgCmd.setCommand(UpdateCommand.CommandEnum.ADD.getName());
	trgCmd.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	trgCmd.setItemType(UpdateCommand.TypeEnum.TERM.getName());
	trgCmd.setLanguageId("de-DE");

	TranslationUnit unit = new TranslationUnit();
	unit.setSourceTermUpdateCommands(Collections.singletonList(srcCmd));
	unit.setTargetTermUpdateCommands(Collections.singletonList(trgCmd));

	getTermEntryService().updateTermEntries(Collections.singletonList(unit), "en-US", PROJECT_ID, Action.EDITED);

	long totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertTrue(totalCount > 0);
    }

    @Ignore
    @Test
    public void testCleanUpDisabledTerms() throws Exception {
	getProjectService().enableProject(PROJECT_ID);

	long totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertTrue(totalCount > 0);

	getCleanUpProcessorV2().cleanup();

	totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertEquals(0, totalCount);
    }

    @Test
    public void testCleanUpHiddenTerms() throws Exception {
	long totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertTrue(totalCount > 0);

	getCleanUpProcessorV2().cleanup();

	totalCount = getDbTermEntryService().getTotalCount(null);
	Assert.assertEquals(0, totalCount);
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private SessionService getSessionService() {
	return _sessionService;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}
