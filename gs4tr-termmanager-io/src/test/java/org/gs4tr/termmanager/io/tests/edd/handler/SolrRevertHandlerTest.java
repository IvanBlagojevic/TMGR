package org.gs4tr.termmanager.io.tests.edd.handler;

import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.dao.backup.BackupException;
import org.gs4tr.termmanager.io.edd.api.Event;
import org.gs4tr.termmanager.io.edd.api.EventDispatcher;
import org.gs4tr.termmanager.io.edd.api.Handler;
import org.gs4tr.termmanager.io.edd.event.ProcessDataEvent;
import org.gs4tr.termmanager.io.edd.handler.BackupUpdateHandler;
import org.gs4tr.termmanager.io.edd.handler.SolrUpdateHandler;
import org.gs4tr.termmanager.io.exception.TransactionError;
import org.gs4tr.termmanager.io.tests.AbstractIOTest;
import org.gs4tr.termmanager.io.tests.TestHelper;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import jetbrains.exodus.entitystore.EntityId;

@Ignore
public class SolrRevertHandlerTest extends AbstractIOTest {

    private static final String NEW_TERM_NAME = "new term name";

    @Autowired
    private BackupUpdateHandler _backUpHandlerOriginal;

    @Mock
    private BackupUpdateHandler _backupUpdateHandlerMock;

    @Autowired
    private EventDispatcher _dispatcher;

    @Resource(name = "handlersMap")
    private Map<Class<? extends Event>, List<Handler<? extends Event>>> _handlersMap;

    @Autowired
    private TransactionLogHandler _logHandler;

    @Autowired
    private SolrUpdateHandler _solrHandlerOriginal;

    @Mock
    private SolrUpdateHandler _solrUpdateHandlerMock;

    @After
    public void after() throws Exception {
	super.tearDown();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void backupSecondSaveActionFailed_RevertTest() throws Exception {
	boolean exceptionThrown = false;

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = 1L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(),
		TestHelper.createTransactionalUnit());
	Assert.assertTrue(optChildId.isPresent());

	/* two termEntries are successfully saved */
	logHandler.finishAppending(projectId, optParentId.get());

	/* mock BackupUpdateHandler to throw exception and put it in handlersMap */
	List<Handler<? extends Event>> handlers = Arrays.asList(getBackupUpdateHandlerMock(), getSolrHandlerOriginal());

	getHandlersMap().put(ProcessDataEvent.class, handlers);

	doThrow(new BackupException("error")).when(getBackupUpdateHandlerMock())
		.onEvent(Mockito.any(ProcessDataEvent.class));

	try {
	    optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import", getRegularCollection());
	    Assert.assertTrue(optParentId.isPresent());

	    optChildId = logHandler.appendAndLink(projectId, optParentId.get(), TestHelper.createTransactionalUnit());
	    Assert.assertTrue(optChildId.isPresent());

	    /* save another two termEntries failed */
	    logHandler.finishAppending(projectId, optParentId.get());

	} catch (TransactionError e) {
	    exceptionThrown = true;
	    /*
	     * number of termEntries in DB and Solr remains the same just like before
	     * exception
	     */
	    List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	    Assert.assertEquals(2, dbEntries.size());

	    List<TermEntry> solrEntries = getBrowser().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	    Assert.assertEquals(2, solrEntries.size());

	}
	Assert.assertTrue(exceptionThrown);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void backupUpdateTermEntriesFailed_RevertTest() throws Exception {
	boolean exceptionThrown = false;

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = 1L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	TransactionalUnit transactionalUnit = TestHelper.createTransactionalUnit();

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(), transactionalUnit);
	Assert.assertTrue(optChildId.isPresent());

	/* save two termEntries */
	logHandler.finishAppending(projectId, optParentId.get());

	List<TermEntry> termEntriesBeforeException = getBrowser().findAll();

	/* list of terms names after we saved termEntries */
	List<String> termNamesBeforeException = new ArrayList<>();

	for (TermEntry termEntry : termEntriesBeforeException) {
	    Set<Term> terms = termEntry.getLanguageTerms().get("en");
	    terms.forEach(term -> termNamesBeforeException.add(term.getName()));
	}

	/* mock BackupUpdateHandler to throw exception and put it in handlersMap */
	List<Handler<? extends Event>> handlers = Arrays.asList(getSolrHandlerOriginal(), getBackupUpdateHandlerMock());

	getHandlersMap().put(ProcessDataEvent.class, handlers);

	doThrow(new BackupException("error")).when(getBackupUpdateHandlerMock())
		.onEvent(Mockito.any(ProcessDataEvent.class));

	try {
	    /* setting new terms names for saved termEntries */
	    List<TermEntry> updatedTermEntries = transactionalUnit.getTermEntries();

	    for (TermEntry termEntry : updatedTermEntries) {
		Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
		Set<Term> terms = languageTerms.get("en");
		terms.forEach(term -> term.setName(NEW_TERM_NAME));
	    }
	    optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import", getRegularCollection());
	    Assert.assertTrue(optParentId.isPresent());

	    optChildId = logHandler.appendAndLink(projectId, optParentId.get(), transactionalUnit);
	    Assert.assertTrue(optChildId.isPresent());

	    /* termEntries update failed */
	    logHandler.finishAppending(projectId, optParentId.get());
	} catch (TransactionError e) {
	    exceptionThrown = true;
	    /*
	     * number of termEntries in DB and Solr remains the same because we are updating
	     * saved termEntries
	     */
	    List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	    Assert.assertEquals(2, dbEntries.size());

	    List<TermEntry> solrEntries = getBrowser().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	    Assert.assertEquals(2, solrEntries.size());

	    List<String> updatedTermNames = new ArrayList<>();

	    for (TermEntry termEntry : solrEntries) {
		Set<Term> terms = termEntry.getLanguageTerms().get("en");
		terms.forEach(term -> updatedTermNames.add(term.getName()));

	    }
	    /* terms names also remains the same */
	    Assert.assertTrue(updatedTermNames.containsAll(termNamesBeforeException));
	    Assert.assertEquals(updatedTermNames.size(), termNamesBeforeException.size());
	    Assert.assertTrue(!updatedTermNames.contains(NEW_TERM_NAME));
	}

	Assert.assertTrue(exceptionThrown);
    }

    public BackupUpdateHandler getBackUpHandlerOriginal() {
	return _backUpHandlerOriginal;
    }

    public BackupUpdateHandler getBackupUpdateHandlerMock() {
	return _backupUpdateHandlerMock;
    }

    public EventDispatcher getDispatcher() {
	return _dispatcher;
    }

    public Map<Class<? extends Event>, List<Handler<? extends Event>>> getHandlersMap() {
	return _handlersMap;
    }

    @Override
    public TransactionLogHandler getLogHandler() {
	return _logHandler;
    }

    public SolrUpdateHandler getSolrHandlerOriginal() {
	return _solrHandlerOriginal;
    }

    public SolrUpdateHandler getSolrUpdateHandlerMock() {
	return _solrUpdateHandlerMock;
    }

    @Before
    public void setUp() throws Exception {
	super.setUp();
	MockitoAnnotations.initMocks(this);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void solrSaveTermEntriesFailed_RevertTest() throws Exception {
	boolean exceptionThrown = false;

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = 1L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(),
		TestHelper.createTransactionalUnit());
	Assert.assertTrue(optChildId.isPresent());

	/* mock SolrUpdateHandler to throw exception and put it in handlersMap */
	List<Handler<? extends Event>> handlers = Arrays.asList(getBackUpHandlerOriginal(), getSolrUpdateHandlerMock());

	getHandlersMap().put(ProcessDataEvent.class, handlers);

	doThrow(new IllegalArgumentException()).when(getSolrUpdateHandlerMock())
		.onEvent(Mockito.any(ProcessDataEvent.class));

	try {
	    /* save two termEntries failed */
	    logHandler.finishAppending(projectId, optParentId.get());

	} catch (TransactionError e) {
	    exceptionThrown = true;

	    List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	    Assert.assertEquals(2, dbEntries.size());

	    List<TermEntry> solrEntries = getBrowser().findAll();
	    Assert.assertEquals(2, solrEntries.size());
	}
	Assert.assertTrue(exceptionThrown);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void solrUpdateTermEntriesFailed_RevertTest() throws Exception {
	boolean exceptionThrown = false;

	TransactionLogHandler logHandler = getLogHandler();

	Long projectId = 1L;

	Optional<EntityId> optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import",
		getRegularCollection());
	Assert.assertTrue(optParentId.isPresent());

	TransactionalUnit transactionalUnit = TestHelper.createTransactionalUnit();

	Optional<EntityId> optChildId = logHandler.appendAndLink(projectId, optParentId.get(), transactionalUnit);
	Assert.assertTrue(optChildId.isPresent());

	/* save two termEntries */
	logHandler.finishAppending(projectId, optParentId.get());

	/* mock SolrUpdateHandler to throw exception and put it in handlersMap */
	List<Handler<? extends Event>> handlers = Arrays.asList(getBackUpHandlerOriginal(), getSolrUpdateHandlerMock());

	getHandlersMap().put(ProcessDataEvent.class, handlers);

	doThrow(new IllegalArgumentException()).when(getSolrUpdateHandlerMock())
		.onEvent(Mockito.any(ProcessDataEvent.class));

	try {
	    /* setting new terms names for saved termEntries */
	    List<TermEntry> termEntries = transactionalUnit.getTermEntries();

	    for (TermEntry termEntry : termEntries) {
		Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
		Set<Term> terms = languageTerms.get("en");

		terms.forEach(term -> term.setName(NEW_TERM_NAME));
	    }

	    optParentId = logHandler.startAppending(projectId, TestHelper.USER, "import", getRegularCollection());
	    Assert.assertTrue(optParentId.isPresent());

	    optChildId = logHandler.appendAndLink(projectId, optParentId.get(), transactionalUnit);
	    Assert.assertTrue(optChildId.isPresent());

	    /* termEntries update failed */
	    logHandler.finishAppending(projectId, optParentId.get());
	} catch (TransactionError e) {
	    exceptionThrown = true;
	    /*
	     * number of termEntries in DB and Solr remains the same because we are updating
	     * saved termEntries
	     */
	    List<DbTermEntry> dbEntries = getDbTermEntryDAO().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(dbEntries));
	    Assert.assertEquals(2, dbEntries.size());

	    List<TermEntry> solrEntries = getBrowser().findAll();
	    Assert.assertTrue(CollectionUtils.isNotEmpty(solrEntries));
	    Assert.assertEquals(2, solrEntries.size());

	    /* all terms names has been updated */
	    for (TermEntry termEntry : solrEntries) {
		Set<Term> terms = termEntry.getLanguageTerms().get("en");
		terms.forEach(term -> {
		    Assert.assertEquals(term.getName(), NEW_TERM_NAME);
		});
	    }
	}
	Assert.assertTrue(exceptionThrown);
    }
}
