package org.gs4tr.termmanager.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.test.groups.SolrTest;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.tm3.api.TmException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SolrTest.class)
public abstract class AbstractSolrGlossaryTest extends AbstractSpringServiceTests {

    public static final String LANGUAGE = "sr";

    public static final long PROJECT_ID = 1L;

    public static final String PROJECT_NAME = "test1";

    public static final String PROJECT_SHORTCODE = "TES000001";
    public static final String STATUS_PROCESSED = "PROCESSED";

    public static final String SUB_TERM_ENTRY_ID_01 = "uuid-sub-term-entry-001";
    public static final String SUB_TERM_ENTRY_ID_02 = "uuid-sub-term-entry-002";
    public static final String SUB_TERM_ID_01 = "c502608e-uuid-sub-term-001";
    public static final String SUB_TERM_ID_02 = "c502608e-uuid-sub-term-002";

    public static final String SUB_TERM_ID_03 = "c502608e-uuid-sub-term-003";
    public static final String SUB_TERM_ID_04 = "c502608e-uuid-sub-term-004";

    public static final String TERM_ENTRY_ID_01 = "c502608e-uuid-term-entry-001";
    public static final String TERM_ENTRY_ID_02 = "c502608e-uuid-term-entry-002";
    public static final String TERM_ID_01 = "c502608e-uuid-term-001";
    public static final String TERM_ID_02 = "c502608e-uuid-term-002";
    public static final String TERM_ID_03 = "c502608e-uuid-term-003";
    public static final String TERM_ID_04 = "c502608e-uuid-term-004";

    public static final String TERM_ID_05 = "c502608e-uuid-term-005";

    public static final String TERM_ID_06 = "c502608e-uuid-term-006";

    public static final String TERM_ID_07 = "c502608e-uuid-term-007";

    public static final String USER = "user";

    private static final Log LOGGER = LogFactory.getLog(AbstractSolrGlossaryTest.class);

    protected static final String STATUS = ItemStatusTypeHolder.PROCESSED.getName();

    protected static final String SUPER_USER = "super";

    protected static final String TE_ID = UUID.randomUUID().toString();

    private static final String TMP = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$

    @Autowired
    private GlossaryConnectionManager _connectionManager;

    private ITmgrGlossaryConnector _regularConnector;

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    @Autowired
    private PersistentStoreHandler _storeHandler;

    private ITmgrGlossaryConnector _submissionConnector;

    @Autowired
    private TransactionLogHandler _transactionLogHandler;

    @After
    public void after() throws Exception {
	if (getRegularConnector() != null) {
	    getRegularConnector().disconnect();
	}

	if (getSubmissionConnector() != null) {
	    getSubmissionConnector().disconnect();
	}
    }

    @Before
    public void before() throws Exception {
	MockitoAnnotations.initMocks(this);

	long start = System.currentTimeMillis();

	initRegularConnector();
	initSubmissionConnector();

	clearSolr();

	checkLock(PROJECT_ID);

	populateRegularEntries();
	populateSubmissionEntries();

	LOGGER.info("Time to startup: " + (System.currentTimeMillis() - start));
    }

    public Term createTermForLookup() {
	return createTerm(TERM_ID_07, "en-US", "house", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm", true);
    }

    private Description createDescription(String baseType, String type, String value) {
	Description description = new Description();
	description.setBaseType(baseType);
	description.setType(type);
	description.setValue(value);
	description.setUuid(UUID.randomUUID().toString());
	return description;
    }

    private void createSubTermEntry01() throws TmException {
	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_01);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_01);
	subTermEntry.setProjectId(PROJECT_ID);
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setShortCode(PROJECT_SHORTCODE);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_01, "de-DE", "Maus", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm1.setParentUuId(TERM_ID_02);
	subTerm1.setAssignee("pm");
	subTerm1.setSubmitter("pm");
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.TRUE);
	subTerm1.setTempText("Maus");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.TRUE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm1.setProjectId(PROJECT_ID);

	Comment comment3 = new Comment();
	comment3.setText("This is first de-DE term comment");
	comment3.setUser("pm");

	subTerm1.addComment(comment3);

	Term subTerm2 = createTerm(SUB_TERM_ID_02, "en-US", "house", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm2.setParentUuId(TERM_ID_01);
	subTerm2.setAssignee("pm");
	subTerm2.setSubmitter("pm");
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.FALSE);
	subTerm2.setTempText("house");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.TRUE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.FALSE);
	subTerm2.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm2.setProjectId(PROJECT_ID);

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateSubmissionTermEntries(PROJECT_ID, Arrays.asList(subTermEntry));
    }

    private void createSubTermEntry02() throws TmException {
	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_02);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_02);
	subTermEntry.setProjectId(1L);
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setShortCode(PROJECT_SHORTCODE);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_03, "fr-FR", "oiseau", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm1.setParentUuId(TERM_ID_06);
	subTerm1.setAssignee("pm");
	subTerm1.setSubmitter("pm");
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.TRUE);
	subTerm1.setTempText("oiseau");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.TRUE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm1.setProjectId(PROJECT_ID);

	Comment comment1 = new Comment();
	comment1.setText("This is first fr-FR term comment");
	comment1.setUser("pm");
	Comment comment2 = new Comment();
	comment2.setText("This is second fr-FR term comment");
	comment2.setUser("pm");

	subTerm1.addComment(comment1);
	subTerm1.addComment(comment2);

	Description description3 = new Description();
	description3.setBaseType(Description.ATTRIBUTE);
	description3.setType("definition");
	description3.setValue("termDesc");
	description3.setUuid(UUID.randomUUID().toString());

	subTerm1.addDescription(description3);

	Term subTerm2 = createTerm(SUB_TERM_ID_04, "en-US", "bird", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", true);
	subTerm2.setParentUuId(TERM_ID_04);
	subTerm2.setAssignee("pm");
	subTerm2.setSubmitter("pm");
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.FALSE);
	subTerm2.setTempText("bird");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.TRUE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.TRUE);
	subTerm2.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm2.setProjectId(PROJECT_ID);

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateSubmissionTermEntries(PROJECT_ID, Arrays.asList(subTermEntry));
    }

    private Term createTerm(String uuId, String languageId, String name, boolean forbidden, String status, String user,
	    boolean isFirst) {
	Term term = new Term();
	term.setUuId(uuId);
	term.setLanguageId(languageId);
	term.setName(name);
	term.setForbidden(forbidden);
	term.setStatus(status);
	term.setUserCreated(user);
	term.setUserModified(user);
	term.setDateSubmitted(new Date().getTime());
	term.setDateCreated(new Date().getTime());
	term.setDateModified(new Date().getTime());
	term.setProjectId(PROJECT_ID);
	return term;
    }

    private void createTermEntry01() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_01);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setShortCode(PROJECT_SHORTCODE);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_01, "en-US", "house", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm",
		true);
	Term term2 = createTerm(TERM_ID_02, "de-DE", "Maus", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm",
		true);
	Term term3 = createTerm(TERM_ID_03, "en-US", "river", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm",
		false);

	Description termEntryDesc1 = createDescription(Description.ATTRIBUTE, "context",
		"This is some termentry description");
	Description termEntryDesc2 = createDescription(Description.ATTRIBUTE, "definition",
		"This another termentry description");

	Description termDesc2 = createDescription(Description.ATTRIBUTE, "definition", "This is some term description");
	term1.addDescription(termDesc2);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);
	termEntry.addTerm(term3);
	termEntry.addDescription(termEntryDesc1);
	termEntry.addDescription(termEntryDesc2);

	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_01);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_01);
	subTermEntry.setProjectId(PROJECT_ID);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_01, "de-DE", "Maus", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm1.setParentUuId(term2.getUuId());
	subTerm1.setAssignee("pm");
	subTerm1.setSubmitter("pm");
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.TRUE);
	subTerm1.setTempText("Maus");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.TRUE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm1.setProjectId(PROJECT_ID);

	Comment comment3 = new Comment();
	comment3.setText("This is first de-DE term comment");
	comment3.setUser("pm");

	subTerm1.addComment(comment3);

	Term subTerm2 = createTerm(SUB_TERM_ID_02, "en-US", "house", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm2.setParentUuId(term1.getUuId());
	subTerm2.setAssignee("pm");
	subTerm2.setSubmitter("pm");
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.FALSE);
	subTerm2.setTempText("house");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.TRUE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.FALSE);
	subTerm2.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm2.setProjectId(PROJECT_ID);

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry, subTermEntry));
    }

    private void createTermEntry02() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_02);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setShortCode(PROJECT_SHORTCODE);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_04, "en-US", "bird", false, ItemStatusTypeHolder.PROCESSED.getName(), "pm",
		true);
	Term term2 = createTerm(TERM_ID_05, "es-US", "pajaro", false, ItemStatusTypeHolder.WAITING.getName(), "pm",
		false);
	Term term3 = createTerm(TERM_ID_06, "fr-FR", "oiseau", false, ItemStatusTypeHolder.WAITING.getName(), "pm",
		true);

	Description description1 = createDescription(Description.ATTRIBUTE, "context",
		"This is some termentry description");
	Description description2 = createDescription(Description.ATTRIBUTE, "definition",
		"This another termentry description");

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);
	termEntry.addTerm(term3);
	termEntry.addDescription(description1);
	termEntry.addDescription(description2);

	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_02);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_02);
	subTermEntry.setProjectId(1L);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_03, "fr-FR", "oiseau", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), "pm", true);
	subTerm1.setParentUuId(term3.getUuId());
	subTerm1.setAssignee("pm");
	subTerm1.setSubmitter("pm");
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.TRUE);
	subTerm1.setTempText("oiseau");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.TRUE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm1.setProjectId(PROJECT_ID);

	Comment comment1 = new Comment();
	comment1.setText("This is first fr-FR term comment");
	comment1.setUser("pm");
	Comment comment2 = new Comment();
	comment2.setText("This is second fr-FR term comment");
	comment2.setUser("pm");

	subTerm1.addComment(comment1);
	subTerm1.addComment(comment2);

	Description description3 = new Description();
	description3.setBaseType(Description.ATTRIBUTE);
	description3.setType("definition");
	description3.setValue("termDesc");
	description3.setUuid(UUID.randomUUID().toString());

	subTerm1.addDescription(description3);

	Term subTerm2 = createTerm(SUB_TERM_ID_04, "en-US", "bird", false, ItemStatusTypeHolder.PROCESSED.getName(),
		"pm", true);
	subTerm2.setParentUuId(term1.getUuId());
	subTerm2.setAssignee("pm");
	subTerm2.setSubmitter("pm");
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.FALSE);
	subTerm2.setTempText("bird");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.TRUE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.TRUE);
	subTerm2.setStatusOld(ItemStatusTypeHolder.MISSING_TRANSLATION.getName());
	subTerm2.setProjectId(PROJECT_ID);

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry, subTermEntry));
    }

    private GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private PersistentStoreHandler getStoreHandler() {
	return _storeHandler;
    }

    private TransactionLogHandler getTransactionLogHandler() {
	return _transactionLogHandler;
    }

    private void initRegularConnector() throws TmException {
	_regularConnector = getConnectionManager().getConnector(getRegularCollection());
	_regularConnector.connect();
    }

    private void initSubmissionConnector() throws TmException {
	_submissionConnector = getConnectionManager().getConnector(getSubmissionCollection());
	_submissionConnector.connect();
    }

    protected TermEntry addNewTermEntry() throws TmException {
	String languageId = LANGUAGE;
	String name = "Dummy term text that is created by me";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term term = createTerm(languageId, name, forbidden, status, user);

	String name1 = "Synonym term text that is generated by me";
	Term term1 = createTerm(languageId, name1, forbidden, status, user);

	long projectId = 1l;
	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(projectId, projectShortCode, username);
	termEntry.setUuId(TE_ID);
	termEntry.addTerm(term);
	termEntry.addTerm(term1);

	String type3 = "definition";
	String value3 = "This is some dummy term entry attribute";
	Description description3 = createDescription(type3, value3);

	termEntry.addDescription(description3);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);

	return termEntry;
    }

    protected void checkLock(long projectId) throws IOException {
	boolean locked = getTransactionLogHandler().isLocked(projectId);
	if (locked) {
	    getStoreHandler().closeAndClear(projectId);
	}

	Path path = Paths.get(TMP, String.valueOf(projectId));
	File file = path.toFile();
	if (file.exists()) {
	    FileUtils.forceDelete(file);
	}
    }

    protected void clearSolr() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	getSubmissionConnector().getTmgrUpdater().deleteAll();
    }

    protected Description createDescription(String type, String value) {
	Description description = new Description();
	description.setType(type);
	description.setValue(value);
	description.setUuid(UUID.randomUUID().toString());

	return description;
    }

    protected Term createTerm(String languageId, String name, boolean forbidden, String status, String user) {
	Term term = new Term();
	term.setLanguageId(languageId);
	term.setName(name);
	term.setForbidden(forbidden);
	term.setStatus(status);
	term.setUserCreated(user);
	term.setUserModified(user);
	term.setUuId(UUID.randomUUID().toString());
	term.setDateCreated(new Date().getTime());
	term.setDateModified(new Date().getTime());
	return term;
    }

    protected String getRegularCollection() {
	return getSolrConfig().getRegularCollection();
    }

    protected ITmgrGlossaryConnector getRegularConnector() {
	return _regularConnector;
    }

    protected String getSubmissionCollection() {
	return getSolrConfig().getSubmissionCollection();
    }

    protected ITmgrGlossaryConnector getSubmissionConnector() {
	return _submissionConnector;
    }

    protected void populateRegularEntries() throws Exception {
	createTermEntry01();
	createTermEntry02();

	Assert.assertEquals(4, getRegularConnector().getTmgrBrowser().findAll().size());
    }

    protected void populateSubmissionEntries() throws Exception {
	createSubTermEntry01();
	createSubTermEntry02();

	Assert.assertEquals(2, getSubmissionConnector().getTmgrBrowser().findAll().size());
    }
}
