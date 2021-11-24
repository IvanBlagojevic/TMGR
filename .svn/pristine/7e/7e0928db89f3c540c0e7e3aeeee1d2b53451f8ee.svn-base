package org.gs4tr.termmanager.tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.termmanager.io.tlog.config.PersistentStoreHandler;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.glossary.Comment;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.test.groups.SolrTest;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.tm3.api.TmException;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SolrTest.class)
public abstract class AbstractSolrGlossaryTest extends AbstractSpringServiceTests {

    public static final String STATUS_APPROVED = ItemStatusTypeHolder.PROCESSED.getName();
    public static final String STATUS_BLACKLIST = ItemStatusTypeHolder.BLACKLISTED.getName();
    public static final String STATUS_ONHOLD = ItemStatusTypeHolder.ON_HOLD.getName();
    public static final String STATUS_PENDING = ItemStatusTypeHolder.WAITING.getName();

    public static final String SUB_TERM_ENTRY_ID_01 = "uuid-sub-term-entry-001";
    public static final String SUB_TERM_ENTRY_ID_02 = "uuid-sub-term-entry-002";

    public static final String SUB_TERM_ID_01 = "c502608e-uuid-sub-term-001";
    public static final String SUB_TERM_ID_02 = "c502608e-uuid-sub-term-002";
    public static final String SUB_TERM_ID_03 = "c502608e-uuid-sub-term-003";
    public static final String SUB_TERM_ID_04 = "c502608e-uuid-sub-term-004";
    public static final String TERM_DESCRIPTION_ID_1 = "c502608e-uuid-description-001";
    public static final String TERM_ENTRY_ID_01 = "c502608e-uuid-term-entry-001";
    public static final String TERM_ENTRY_ID_02 = "c502608e-uuid-term-entry-002";
    public static final String TERM_ENTRY_ID_03 = "c502608e-uuid-term-entry-003";
    public static final String TERM_ENTRY_ID_04 = "c502608e-uuid-term-entry-004";
    public static final String TERM_ENTRY_ID_05 = "c502608e-uuid-term-entry-005";
    public static final String TERM_ENTRY_ID_06 = "c502608e-uuid-term-entry-006";
    public static final String TERM_ENTRY_ID_07 = "c502608e-uuid-term-entry-007";
    public static final String TERM_ENTRY_ID_08 = "c502608e-uuid-term-entry-008";
    public static final String TERM_ENTRY_ID_09 = "c502608e-uuid-term-entry-009";
    public static final String TERM_ENTRY_ID_10 = "c502608e-uuid-term-entry-010";
    public static final String TERM_ENTRY_ID_11 = "c502608e-uuid-term-entry-011";
    public static final String TERM_ENTRY_ID_12 = "c502608e-uuid-term-entry-012";
    public static final String TERM_ENTRY_ID_13 = "c502608e-uuid-term-entry-013";
    public static final String TERM_ENTRY_ID_14 = "c502608e-uuid-term-entry-014";
    public static final String TERM_ENTRY_ID_15 = "c502608e-uuid-term-entry-015";
    public static final String TERM_ENTRY_ID_16 = "c502608e-uuid-term-entry-016";
    public static final String TERM_ID_01 = "c502608e-uuid-term-001";
    public static final String TERM_ID_02 = "c502608e-uuid-term-002";
    public static final String TERM_ID_03 = "c502608e-uuid-term-003";
    public static final String TERM_ID_04 = "c502608e-uuid-term-004";
    public static final String TERM_ID_05 = "c502608e-uuid-term-005";
    public static final String TERM_ID_06 = "c502608e-uuid-term-006";
    public static final String TERM_ID_07 = "c502608e-uuid-term-007";
    public static final String TERM_ID_08 = "c502608e-uuid-term-008";
    public static final String TERM_ID_09 = "c502608e-uuid-term-009";
    public static final String TERM_ID_10 = "c502608e-uuid-term-010";
    public static final String TERM_ID_11 = "c502608e-uuid-term-011";
    public static final String TERM_ID_12 = "c502608e-uuid-term-012";
    public static final String TERM_ID_13 = "c502608e-uuid-term-013";
    public static final String TERM_ID_14 = "c502608e-uuid-term-014";
    public static final String TERM_ID_15 = "c502608e-uuid-term-015";
    public static final String TERM_ID_16 = "c502608e-uuid-term-016";
    public static final String TERM_ID_17 = "c502608e-uuid-term-017";
    public static final String TERM_ID_18 = "c502608e-uuid-term-018";
    public static final String TERM_ID_19 = "c502608e-uuid-term-019";
    public static final String TERM_ID_20 = "c502608e-uuid-term-020";
    public static final String TERM_ID_21 = "c502608e-uuid-term-021";
    public static final String TERM_ID_22 = "c502608e-uuid-term-022";
    public static final String TERM_ID_23 = "c502608e-uuid-term-023";
    public static final String TERM_ID_24 = "c502608e-uuid-term-024";
    public static final String TERM_ID_25 = "c502608e-uuid-term-025";
    public static final String TERM_ID_26 = "c502608e-uuid-term-026";
    public static final String TERM_ID_27 = "c502608e-uuid-term-027";
    public static final String TERM_ID_28 = "c502608e-uuid-term-028";
    public static final String TERM_ID_29 = "c502608e-uuid-term-029";
    public static final String TERM_ID_30 = "c502608e-uuid-term-030";
    public static final String TERM_ID_31 = "c502608e-uuid-term-031";
    public static final String TERM_ID_32 = "c502608e-uuid-term-032";
    public static final String TERM_ID_33 = "c502608e-uuid-term-033";
    public static final String TERM_ID_34 = "c502608e-uuid-term-034";
    public static final String TERM_ID_35 = "c502608e-uuid-term-035";
    public static final String TERM_ID_36 = "c502608e-uuid-term-036";
    public static final String TERM_ID_37 = "c502608e-uuid-term-037";
    public static final String USER = "pm";

    private static final Log LOGGER = LogFactory.getLog(AbstractSolrGlossaryTest.class);

    protected static final long PROJECT_ID = 1L;

    protected static final long PROJECT_ID_2 = 2L;

    private static final String PROJECT_NAME = "projectName";

    private static final String PROJECT_NAME_2 = "projectName2";

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

    @Before
    public void before() throws Exception {

	long start = System.currentTimeMillis();

	initRegularConnector();
	initSubmissionConnector();

	clearSolr();

	checkLock(PROJECT_ID);
	checkLock(PROJECT_ID_2);

	populate();

	LOGGER.info("Time to startup: " + (System.currentTimeMillis() - start));
    }

    private void createTermEntry01() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_01);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_01, "en-US", "house", false, STATUS_APPROVED, USER, true);
	Term term2 = createTerm(TERM_ID_02, "de-DE", "germanHouse", false, STATUS_APPROVED, USER, true);
	Term term3 = createTerm(TERM_ID_03, "en-US", "houseSynonyme", false, STATUS_APPROVED, USER, false);

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
	subTermEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserCreated(USER);
	subTermEntry.setUserModified(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_01, "de-DE", "Maus", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), USER, true);
	subTerm1.setParentUuId(term2.getUuId());
	subTerm1.setAssignee(USER);
	subTerm1.setSubmitter(USER);
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.FALSE);
	subTerm1.setTempText("Big Maus");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.FALSE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(ItemStatusTypeHolder.WAITING.getName());
	subTerm1.setDateSubmitted(System.currentTimeMillis());

	Comment comment3 = new Comment();
	comment3.setText("This is first de-DE term comment");
	comment3.setUser(USER);

	subTerm1.addComment(comment3);

	Term subTerm2 = createTerm(SUB_TERM_ID_02, "en-US", "house", false,
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), USER, true);
	subTerm2.setParentUuId(term1.getUuId());
	subTerm2.setAssignee(USER);
	subTerm2.setSubmitter(USER);
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.TRUE);
	subTerm2.setTempText("house");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.FALSE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.FALSE);
	subTerm2.setStatusOld(STATUS_PENDING);
	subTerm2.setDateSubmitted(System.currentTimeMillis());

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
	getTermEntryService().updateSubmissionTermEntries(PROJECT_ID, Arrays.asList(subTermEntry));
    }

    private void createTermEntry02() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_02);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_04, "en-US", "bird", false, STATUS_APPROVED, USER, true);
	Term term2 = createTerm(TERM_ID_05, "en-US", "pajaro", false, STATUS_PENDING, USER, false);
	Term term3 = createTerm(TERM_ID_06, "fr-FR", "oiseau", false, STATUS_PENDING, USER, true);
	Term term4 = createTerm(TERM_ID_07, "en-US", "house", false, STATUS_PENDING, USER, true);

	Description description1 = createDescription(Description.ATTRIBUTE, "context",
		"This is some termentry description");
	Description description2 = createDescription(Description.ATTRIBUTE, "definition",
		"This another termentry description");

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);
	termEntry.addTerm(term3);
	termEntry.addTerm(term4);
	termEntry.addDescription(description1);
	termEntry.addDescription(description2);

	// Create submission term entry
	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(TERM_ENTRY_ID_02);
	subTermEntry.setUuId(SUB_TERM_ENTRY_ID_02);
	subTermEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	subTermEntry.setSubmissionId(1L);
	subTermEntry.setUserModified(USER);
	subTermEntry.setUserCreated(USER);

	Term subTerm1 = createTerm(SUB_TERM_ID_03, "fr-FR", "oiseau", false,
		ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), USER, true);
	subTerm1.setParentUuId(term3.getUuId());
	subTerm1.setAssignee(USER);
	subTerm1.setSubmitter(USER);
	subTerm1.setCanceled(Boolean.FALSE);
	subTerm1.setReviewRequired(Boolean.TRUE);
	subTerm1.setTempText("oiseau");
	subTerm1.setSubmissionId(1L);
	subTerm1.setSubmissionName("FirstJob");
	subTerm1.setPriority(new Priority(2, 2));
	subTerm1.setCommited(Boolean.TRUE);
	subTerm1.setTermEntryId(subTermEntry.getUuId());
	subTerm1.setInTranslationAsSource(Boolean.FALSE);
	subTerm1.setStatusOld(STATUS_PENDING);
	subTerm1.setDateSubmitted(System.currentTimeMillis());

	Comment comment1 = new Comment();
	comment1.setText("This is first fr-FR term comment");
	comment1.setUser(USER);
	Comment comment2 = new Comment();
	comment2.setText("This is second fr-FR term comment");
	comment2.setUser(USER);

	subTerm1.addComment(comment1);
	subTerm1.addComment(comment2);

	Description description3 = new Description();
	description3.setUuid(TERM_DESCRIPTION_ID_1);
	description3.setBaseType(Description.ATTRIBUTE);
	description3.setType("definition");
	description3.setValue("termDesc");

	subTerm1.addDescription(description3);

	Term subTerm2 = createTerm(SUB_TERM_ID_04, "en-US", "bird", false, ItemStatusTypeHolder.PROCESSED.getName(),
		USER, true);
	subTerm2.setParentUuId(term1.getUuId());
	subTerm2.setAssignee(USER);
	subTerm2.setSubmitter(USER);
	subTerm2.setCanceled(Boolean.FALSE);
	subTerm2.setReviewRequired(Boolean.FALSE);
	subTerm2.setTempText("bird");
	subTerm2.setSubmissionId(1L);
	subTerm2.setSubmissionName("FirstJob");
	subTerm2.setPriority(new Priority(2, 2));
	subTerm2.setCommited(Boolean.FALSE);
	subTerm2.setTermEntryId(subTermEntry.getUuId());
	subTerm2.setInTranslationAsSource(Boolean.TRUE);
	subTerm2.setStatusOld(STATUS_PENDING);
	subTerm2.setDateSubmitted(System.currentTimeMillis());

	subTermEntry.addTerm(subTerm1);
	subTermEntry.addTerm(subTerm2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
	getTermEntryService().updateSubmissionTermEntries(PROJECT_ID, Arrays.asList(subTermEntry));
    }

    private void createTermEntry05() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_05);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term4 = createTerm(TERM_ID_12, "en-US", "river run", false, STATUS_ONHOLD, USER, true);
	Term term5 = createTerm(TERM_ID_13, "en-US", "river run blacklist", true, STATUS_BLACKLIST, USER, false);

	termEntry.addTerm(term4);
	termEntry.addTerm(term5);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry06() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_06);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term6 = createTerm(TERM_ID_14, "fr-FR", "frenchTerm", false, STATUS_APPROVED, USER, false);
	Term term7 = createTerm(TERM_ID_15, "no-NO", "norwegianTerm", true, STATUS_APPROVED, USER, false);
	Term term8 = createTerm(TERM_ID_16, "en-US", "house", false, STATUS_BLACKLIST, USER, true);

	termEntry.addTerm(term6);
	termEntry.addTerm(term7);
	termEntry.addTerm(term8);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry07() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_07);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term17 = createTerm(TERM_ID_17, "en-US", "englishTerm", false, STATUS_PENDING, USER, true);
	Term term18 = createTerm(TERM_ID_18, "fr-FR", "frenchTerm", false, STATUS_APPROVED, USER, false);
	Term term19 = createTerm(TERM_ID_19, "de-DE", "germanTerm", true, STATUS_BLACKLIST, USER, false);

	termEntry.addTerm(term17);
	termEntry.addTerm(term18);
	termEntry.addTerm(term19);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry08() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_08);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term20 = createTerm(TERM_ID_20, "en-US", "englishTerm", false, STATUS_APPROVED, USER, true);
	Term term21 = createTerm(TERM_ID_21, "fr-FR", "frenchTerm1", false, STATUS_ONHOLD, USER, false);
	Term term22 = createTerm(TERM_ID_22, "de-DE", "germanTerm1", false, STATUS_APPROVED, USER, false);

	termEntry.addTerm(term20);
	termEntry.addTerm(term21);
	termEntry.addTerm(term22);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry09() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_09);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term23 = createTerm(TERM_ID_23, "en-US", "englishTerm", true, STATUS_BLACKLIST, USER, true);
	Term term24 = createTerm(TERM_ID_24, "no-NO", "norwegianTerm", false, STATUS_APPROVED, USER, false);

	termEntry.addTerm(term23);
	termEntry.addTerm(term24);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry10() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_10);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term25 = createTerm(TERM_ID_25, "en-US", "englishTerm1", false, STATUS_APPROVED, USER, true);
	Term term26 = createTerm(TERM_ID_26, "de-DE", "germanTerm", false, STATUS_APPROVED, USER, false);

	termEntry.addTerm(term25);
	termEntry.addTerm(term26);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry11() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_11);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term27 = createTerm(TERM_ID_27, "en-US", "englishTerm one", false, STATUS_APPROVED, USER, true);

	termEntry.addTerm(term27);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry12() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_12);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term28 = createTerm(TERM_ID_28, "en-US", "termWithoutMatch1", false,
		ItemStatusTypeHolder.PROCESSED.getName(), USER, true);

	termEntry.addTerm(term28);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry13() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_13);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term29 = createTerm(TERM_ID_29, "en-US", "termWithoutMatch2", false,
		ItemStatusTypeHolder.PROCESSED.getName(), USER, true);

	termEntry.addTerm(term29);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry14() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_14);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_30, "en-US", "bird", false, STATUS_APPROVED, USER, true);
	Term term2 = createTerm(TERM_ID_31, "en-US", "river run blacklist", true, STATUS_BLACKLIST, USER, false);
	Term term3 = createTerm(TERM_ID_32, "en-US", "river run", false, STATUS_ONHOLD, USER, true);
	Term term4 = createTerm(TERM_ID_33, "en-US", "pajaro", false, STATUS_PENDING, USER, false);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);
	termEntry.addTerm(term3);
	termEntry.addTerm(term4);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry15() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_15);
	termEntry.setProjectId(PROJECT_ID_2);
	termEntry.setProjectName(PROJECT_NAME_2);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_34, "en-US", "dog", true, STATUS_BLACKLIST, USER, true);
	Term term2 = createTerm(TERM_ID_35, "en-US", "puppy", false, STATUS_APPROVED, USER, false);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private void createTermEntry16() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_16);
	termEntry.setProjectId(PROJECT_ID_2);
	termEntry.setProjectName(PROJECT_NAME_2);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_36, "en-US", "cat", false, STATUS_APPROVED, USER, true);
	Term term2 = createTerm(TERM_ID_37, "en-US", "kitty", true, STATUS_BLACKLIST, USER, false);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    private GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    private String getRegularCollection() {
	return getSolrConfig().getRegularCollection();
    }

    private ITmgrGlossaryConnector getRegularConnector() {
	return _regularConnector;
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private PersistentStoreHandler getStoreHandler() {
	return _storeHandler;
    }

    private String getSubmissionCollection() {
	return getSolrConfig().getSubmissionCollection();
    }

    private ITmgrGlossaryConnector getSubmissionConnector() {
	return _submissionConnector;
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

    protected void checkLock(long projectId) throws IOException {
	boolean locked = getTransactionLogHandler().isLocked(projectId);
	if (locked) {
	    getStoreHandler().closeAndClear(projectId);
	}
    }

    protected void clearSolr() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();
	getSubmissionConnector().getTmgrUpdater().deleteAll();
    }

    protected Description createDescription(String baseType, String type, String value) {
	Description description = new Description();
	description.setBaseType(baseType);
	description.setType(type);
	description.setValue(value);
	return description;
    }

    protected Term createTerm(String uuId, String languageId, String name, boolean forbidden, String status,
	    String user, boolean isFirst) {
	Term term = new Term();
	term.setUuId(uuId);
	term.setLanguageId(languageId);
	term.setName(name);
	term.setForbidden(forbidden);
	term.setStatus(status);
	term.setUserCreated(user);
	term.setUserModified(user);
	term.setDateModified(System.currentTimeMillis());
	term.setDateCreated(System.currentTimeMillis());
	term.setCanceled(false);
	return term;
    }

    protected void createTermEntry03() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_03);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_08, "en-US", "house", false, ItemStatusTypeHolder.ON_HOLD.getName(), USER,
		true);
	Term term2 = createTerm(TERM_ID_09, "de-DE", "winterfall", false, ItemStatusTypeHolder.PROCESSED.getName(),
		USER, true);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	termEntry.getLanguageTerms().put("fr-FR", Collections.emptySet());

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    protected void createTermEntry04() throws TmException {
	TermEntry termEntry = new TermEntry();
	termEntry.setUuId(TERM_ENTRY_ID_04);
	termEntry.setProjectId(PROJECT_ID);
	termEntry.setProjectName(PROJECT_NAME);
	termEntry.setUserCreated(USER);
	termEntry.setUserModified(USER);

	Term term1 = createTerm(TERM_ID_10, "en-US", "englishTerm", false, ItemStatusTypeHolder.PROCESSED.getName(),
		USER, true);
	Term term2 = createTerm(TERM_ID_11, "de-DE", "germanHouse", false, ItemStatusTypeHolder.PROCESSED.getName(),
		USER, true);

	termEntry.addTerm(term1);
	termEntry.addTerm(term2);

	getTermEntryService().updateRegularTermEntries(PROJECT_ID, Arrays.asList(termEntry));
    }

    protected ProjectLanguageDetail findProjectLanguageDetailByLanguageId(String languageId,
	    Set<ProjectLanguageDetail> projectLanguageDetails) {
	for (ProjectLanguageDetail projectLanguageDetail : projectLanguageDetails) {
	    if (projectLanguageDetail.getLanguageId().equals(languageId)) {
		return projectLanguageDetail;
	    }
	}
	return null;
    }

    /*
     * TERII-4762 Workflow Submissions | Detail Pane needs to reflect latest change
     * regardless if it's term, status, or attribute/note
     *
     */
    protected Boolean isProjectLanguageDetailDateModifiedChanged(String languageId,
	    Set<ProjectLanguageDetail> projectLanguageDetailsBefore,
	    Set<ProjectLanguageDetail> projectLanguageDetailsAfter) {

	ProjectLanguageDetail projectLanguageDetailBefore = findProjectLanguageDetailByLanguageId(languageId,
		projectLanguageDetailsBefore);
	ProjectLanguageDetail projectLanguageDetailAfter = findProjectLanguageDetailByLanguageId(languageId,
		projectLanguageDetailsAfter);

	return projectLanguageDetailBefore.getDateModified().getTime() < projectLanguageDetailAfter.getDateModified()
		.getTime();
    }

    protected void populate() throws Exception {
	createTermEntry01();
	createTermEntry02();
	createTermEntry03();
	createTermEntry04();
	createTermEntry05();
	createTermEntry06();
	createTermEntry07();
	createTermEntry08();
	createTermEntry09();
	createTermEntry10();
	createTermEntry11();
	createTermEntry12();
	createTermEntry13();
	createTermEntry14();
	createTermEntry15();
	createTermEntry16();
    }

}
