package org.gs4tr.termmanager.io.tests.tlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.junit.Assert;
import org.junit.Test;

import jetbrains.exodus.entitystore.EntityId;

public class TransactionLogHandlerTest extends AbstractIOTest {

    @Test
    public void testImport() throws Exception {

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = TestHelper.PROJECT_ID_2; // 2L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(),
		TestHelper.createTransactionalUnit());
	Assert.assertTrue(optChildId.isPresent());

	logHandler.finishAppending(projectId, optParentId.get());

	List<TermEntry> solrEntries = getBrowser().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	Assert.assertEquals(2, solrEntries.size());

	List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	Assert.assertEquals(2, dbEntries.size());

	ProjectDetail afterUpdate = getProjectDetailDAO().findByProjectId(TestHelper.PROJECT_ID_1);
	validateProjectDetailsAfterUpdate(afterUpdate);
    }

    @Test
    public void testImportFailed() throws Exception {

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = TestHelper.PROJECT_ID_2; // 2L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	logHandler.finishAppending(projectId, optParentId.get());

	List<TermEntry> solrEntries = getBrowser().findAll();
	Assert.assertTrue(CollectionUtils.isEmpty(solrEntries));

	List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	Assert.assertTrue(CollectionUtils.isEmpty(dbEntries));
    }

    @Test
    public void testIsLocked() {
	Long projectId = TestHelper.PROJECT_ID_2; // 2L;

	TransactionLogHandler logHandler = getLogHandler();

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	Assert.assertTrue(logHandler.isLocked(projectId));

	logHandler.finishAppending(projectId, optParentId.get());

	Assert.assertFalse(logHandler.isLocked(projectId));
    }

    private void validateProjectDetailsAfterUpdate(ProjectDetail afterUpdate) {
	assertNotNull(afterUpdate);

	assertEquals(1, afterUpdate.getTermEntryCount());
	assertEquals(1, afterUpdate.getActiveSubmissionCount());
	assertEquals(1, afterUpdate.getApprovedTermCount());
	assertEquals(1, afterUpdate.getCompletedSubmissionCount());
	assertEquals(1, afterUpdate.getForbiddenTermCount());
	assertEquals(1, afterUpdate.getOnHoldTermCount());
	assertEquals(1, afterUpdate.getPendingApprovalCount());
    }
}
