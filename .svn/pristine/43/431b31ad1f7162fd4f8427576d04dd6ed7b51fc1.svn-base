package org.gs4tr.termmanager.persistence.solr;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.foundation.modules.entities.model.UserInfo;
import org.gs4tr.foundation.modules.entities.model.UserProfileContext;
import org.gs4tr.foundation3.httpclient.impl.HttpClientFactory;
import org.gs4tr.foundation3.httpclient.model.HttpClientConfiguration;
import org.gs4tr.foundation3.solr.impl.CloudHttpSolrClient;
import org.gs4tr.foundation3.solr.impl.SolrClientFactory;
import org.gs4tr.foundation3.solr.model.CloudHttpConfiguration;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.test.groups.SolrTest;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.tm3.api.TmException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;

@Category(SolrTest.class)
public abstract class AbstractSolrGlossaryTest {

    public static final long PROJECT_ID = 1l;

    protected static final String LANGUAGE = "sr";

    private static final Log LOGGER = LogFactory.getLog(AbstractSolrGlossaryTest.class);

    protected static final String POWER_USER = "power";

    protected static final String SDULIN = "sdulin";

    protected static final String STATUS = ItemStatusTypeHolder.PROCESSED.getName();

    protected static final String SUPER_USER = "super";

    protected static final String TE_ID = UUID.randomUUID().toString();

    @AfterClass
    public static void afterClass() {
	if (getRegularConnector() != null) {
	    getRegularConnector().disconnect();
	}

	if (getSubmissionConnector() != null) {
	    getSubmissionConnector().disconnect();
	}
    }

    @BeforeClass
    public static void beforeClass() throws TmException {
	_zkHosts = System.getProperty("solr.zkhosts", "localhost:9983");

	_regularCollection = System.getProperty("solr.regularV2.terms.path", "regularV2");

	_submissionCollection = System.getProperty("solr.submissionV2.terms.path", "submissionV2");

	_solrClient = createSolrClient();

	initRegularConnector();
	initSubmissionConnector();
    }

    private static String _regularCollection;
    private static ITmgrGlossaryConnector _regularConnector;
    private static CloudHttpSolrClient _solrClient;
    private static String _submissionCollection;
    private static ITmgrGlossaryConnector _submissionConnector;
    private static String _zkHosts;
    protected final int SYNONYM_NUMBER = 5;

    @Before
    public void before() throws Exception {
	long start = System.currentTimeMillis();
	MockitoAnnotations.initMocks(this);

	setCurrentUserProfile();

	getRegularConnector().getTmgrUpdater().deleteAll();
	getSubmissionConnector().getTmgrUpdater().deleteAll();

	populate(getRegularConnector());
	populate(getSubmissionConnector());

	LOGGER.info("Time to startup: " + (System.currentTimeMillis() - start));
    }

    private static TmgrConnectionInfo createConnectionInfo(String collection) {
	TmgrConnectionInfo info = new TmgrConnectionInfo();
	info.setName(collection);
	info.setBatchSize(1000);
	info.setCollectionName(collection);
	return info;
    }

    private static CloudHttpSolrClient createSolrClient() {
	if (_solrClient == null) {
	    HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();
	    CloseableHttpClient httpClient = HttpClientFactory.getInstance().createHttpClient(httpClientConfiguration);

	    CloudHttpConfiguration config = new CloudHttpConfiguration();
	    config.setZkHosts(getZkHosts());
	    config.setHttpClient(httpClient);

	    SolrClientFactory factory = new SolrClientFactory();
	    _solrClient = (CloudHttpSolrClient) factory.createCloudHttp(config);
	}
	return _solrClient;
    }

    private static String getRegularCollection() {
	return _regularCollection;
    }

    private static CloudHttpSolrClient getSolrClient() {
	return _solrClient;
    }

    private static String getSubmissionCollection() {
	return _submissionCollection;
    }

    private static String getZkHosts() {
	return _zkHosts;
    }

    private static void initRegularConnector() throws TmException {
	TmgrConnectionInfo info = createConnectionInfo(getRegularCollection());

	_regularConnector = new TmgrSolrConnector(info, getSolrClient());
	_regularConnector.connect();
    }

    private static void initSubmissionConnector() throws TmException {
	TmgrConnectionInfo info = createConnectionInfo(getSubmissionCollection());

	_submissionConnector = new TmgrSolrConnector(info, getSolrClient());
	_submissionConnector.connect();
    }

    private void setCurrentUserProfile() {
	UserProfileContext.clearContext();
	// Basically, we just need a user name. So, keep it simple.
	TmUserProfile power = new TmUserProfile();
	power.setUserProfileId(1L);
	UserInfo userInfo = new UserInfo();
	userInfo.setUserName(POWER_USER);
	power.setUserInfo(userInfo);
	UserProfileContext.setCurrentUserProfile(power);
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

    protected void addTwoTermEntries() throws TmException {

	String germany = Locale.GERMANY.getCode();
	String english = Locale.US.getCode();
	String french = Locale.FRANCE.getCode();

	Term en_us = createTerm(english, "source term", false, ItemStatusTypeHolder.PROCESSED.getName(), POWER_USER);
	Term en_us1 = createTerm(english, "source synonym", false, ItemStatusTypeHolder.PROCESSED.getName(),
		SUPER_USER);
	Term de = createTerm(germany, "target term", true, ItemStatusTypeHolder.BLACKLISTED.getName(), POWER_USER);
	Term de1 = createTerm(germany, "target synonym", false, ItemStatusTypeHolder.WAITING.getName(), SUPER_USER);
	Term fr = createTerm(french, "french term", false, ItemStatusTypeHolder.IN_FINAL_REVIEW.getName(), POWER_USER);
	Term fr1 = createTerm(french, "french synonym", false, ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(),
		SUPER_USER);

	String projectShortCode = "TES000001";
	final Long projectId = 1L;

	TermEntry termEntry1 = new TermEntry(projectId, projectShortCode, SDULIN);
	termEntry1.setUuId(UUID.randomUUID().toString());
	termEntry1.addTerm(en_us);
	termEntry1.addTerm(fr);
	termEntry1.addTerm(fr1);

	TermEntry termEntry2 = new TermEntry(projectId, projectShortCode, SDULIN);
	termEntry2.setUuId(UUID.randomUUID().toString());
	termEntry2.addTerm(en_us);
	termEntry2.addTerm(en_us1);
	termEntry2.addTerm(de);
	termEntry2.addTerm(de1);
	termEntry2.addTerm(fr);
	termEntry2.addTerm(fr1);

	ITmgrGlossaryConnector connector = getRegularConnector();

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry1);
	updater.save(termEntry2);
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

    protected ITmgrGlossaryBrowser getBrowser() throws TmException {
	return getRegularConnector().getTmgrBrowser();
    }

    protected static ITmgrGlossaryConnector getRegularConnector() {
	return _regularConnector;
    }

    protected ITmgrGlossarySearcher getSearcher() throws TmException {
	return getRegularConnector().getTmgrSearcher();
    }

    protected static ITmgrGlossaryConnector getSubmissionConnector() {
	return _submissionConnector;
    }

    protected ITmgrGlossaryUpdater getUpdater() throws TmException {
	return getRegularConnector().getTmgrUpdater();
    }

    protected void populate(ITmgrGlossaryConnector connector) throws Exception {
	String type1 = "context";
	String value1 = "This is populate term attribute context";
	Description description1 = createDescription(type1, value1);

	String languageId = "en";
	String name = "populate";
	boolean forbidden = false;
	String status = STATUS;
	String user = SUPER_USER;

	Term term = createTerm(languageId, name, forbidden, status, user);
	term.addDescription(description1);

	String projectShortCode = "TES000001";
	String username = SUPER_USER;

	TermEntry termEntry = new TermEntry(PROJECT_ID, projectShortCode, username);
	termEntry.setUuId(UUID.randomUUID().toString());
	termEntry.addTerm(term);

	Description description = new Description("teType", "teValue");
	description.setUuid(UUID.randomUUID().toString());

	termEntry.addDescription(description);

	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.save(termEntry);
    }
}
