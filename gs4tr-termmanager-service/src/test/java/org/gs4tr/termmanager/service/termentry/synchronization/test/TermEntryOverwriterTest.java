package org.gs4tr.termmanager.service.termentry.synchronization.test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.gs4tr.foundation3.reader.TermEntryReader;
import org.gs4tr.termmanager.model.ImportSummary;
import org.gs4tr.termmanager.model.ImportSummary.CountWrapper;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.persistence.importer.termentry.synchronizer.ITermEntrySynchronizer;
import org.gs4tr.termmanager.service.termentry.synchronization.SyncOption;
import org.gs4tr.termmanager.service.termentry.synchronization.TermEntrySynchronizerFactory;
import org.gs4tr.tm3.api.Page;
import org.gs4tr.tm3.api.TmException;
import org.junit.Test;

public class TermEntryOverwriterTest extends AbstractTermEntrySyncTest {

    /*
     * TERII-3724: Import XLS file that contains term entries with same ID causes
     * bad count.
     */
    @Test
    public void importTermEntriesWithSameID() throws TmException {
	ITmgrGlossaryConnector connector = getRegularConnector();
	ITmgrGlossaryUpdater updater = connector.getTmgrUpdater();
	updater.deleteAll();

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(UUID.randomUUID().toString());

	Term enTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	Term deTerm = createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	// 1. open project and add term entry.
	prepareTermEntry(existing, new Term[] { enTerm, deTerm }, true);

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);

	// 2. prepare term entry for import[same terms without termEntryId].
	prepareTermEntry(incoming, new Term[] { enTerm, deTerm }, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(incoming);

	// 3. import one term entry first time.
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	// 4. set term entries to be entirely identical (including ID).
	List<TermEntry> termEntries = getBrowser().findAll();
	setTermEntriesToBeIdentical(termEntries);

	TermEntryReader reader = prepareReader(termEntries.toArray(new TermEntry[termEntries.size()]));
	_importProgressInfo = new ImportProgressInfo(2);

	// 5. open project and delete all term entries.
	deleteTermEntries(getBrowser().findAll());

	// 6. import two [identical] term entries second time.
	_tmgrImporter.handleImport(reader, getImportModel(), _importProgressInfo.getImportSummary(), _termEntryCallback,
		null, getTimeConsumer(), null, null, getSynchronizer(), null, getImportTransactionLogHandler(),
		getRegularCollection());

	// Get only term entries that have language terms
	List<TermEntry> filteredEntries = getBrowser().findAll().stream()
		.filter(entry -> MapUtils.isNotEmpty(entry.getLanguageTerms())).collect(toList());

	// We have only one term entry in solr database...
	validateTermEntries(filteredEntries);

	ImportSummary importSummary = _importProgressInfo.getImportSummary();
	// and in import info we can see that also.
	assertEquals(1, importSummary.getNoImportedTermEntries().intValue());
	assertEquals(2, importSummary.getNoImportedTerms().intValue());
    }

    @Test
    public void overwriteEqualTermEntries() throws TmException {
	getRegularConnector().getTmgrUpdater().deleteAll();

	final String termEntryId = UUID.randomUUID().toString();

	TermEntry existing = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	existing.setProjectName(PROJECT_NAME);
	existing.setUuId(termEntryId);

	TermEntry incoming = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	incoming.setProjectName(PROJECT_NAME);
	incoming.setUuId(termEntryId);

	Term enTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	Term deTerm = createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	prepareTermEntry(existing, new Term[] { enTerm, deTerm }, true);
	prepareTermEntry(incoming, new Term[] { enTerm, deTerm }, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(incoming);

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	List<TermEntry> termEntries = getBrowser().findAll();
	assertEquals(1, termEntries.size());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertEquals(1, summary.getUnchangedTermEntrieIds().size());

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getUnchangedTermIds().size());

	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);
	assertEquals(1, englishTermCounts.getUnchangedTermIds().size());
    }

    /*
     * TERII-4674 Import Terminology: Import summary doesn't show results properly
     */
    @Test
    public void xlsImportWithOverwriteSyncOptionEquailsBuilderHelperIgnoreCaseTest() throws TmException {

	String termName = "Source term";

	Term term1 = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	term1.addDescription(new Description("CustomAttribute", "VALUE"));
	term1.addDescription(new Description("CustomAttribute", "value"));

	Term term2 = createTerm(DE_LANGUAGE_ID, "Target term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	term2.addDescription(new Description("CustomAttribute2", "Attribute"));

	Term xlsChangedTerm = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	xlsChangedTerm.addDescription(new Description("customattribute", "value"));

	// Remove default term
	TermEntry solrTermEntry = getSolrTermEntry();
	solrTermEntry.getLanguageTerms().remove(EN_LANGUAGE_ID);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));
	_importModel.setOverwriteByTermEntryId(false);

	_synchronizer.initialize(getRegularConnector(), _importModel);

	prepareTermEntry(getSolrTermEntry(), new Term[] { term1, term2 }, true);
	prepareTermEntry(getXlsTermEntry(), new Term[] { xlsChangedTerm, term2 }, false);

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);

	assertEquals(0, germanyTermCounts.getUpdatedTermIds().size());
	assertEquals(1, germanyTermCounts.getUnchangedTermIds().size());

	assertEquals(0, germanyTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedTermCount().intValue());
	assertEquals(0, germanyTermCounts.getDeletedTerms().intValue());
	assertEquals(0, germanyTermCounts.getTermCount().intValue());

	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());
	assertEquals(0, englishTermCounts.getUnchangedTermIds().size());

	assertEquals(0, englishTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedTermCount().intValue());
	assertEquals(0, englishTermCounts.getDeletedTerms().intValue());
	assertEquals(0, englishTermCounts.getTermCount().intValue());
    }

    /*
     * TERII-4674 Import Terminology: Import summary doesn't show results properly
     */
    @Test
    public void xlsImportWithOverwriteSyncOptionEquailsBuilderHelperReplaceValuesTest() throws TmException {

	String termName = "Source term";

	Term term1 = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	term1.addDescription(new Description("CustomAttribute1", "Attribute1"));
	term1.addDescription(new Description("CustomAttribute2", "Attribute2"));

	Term term2 = createTerm(DE_LANGUAGE_ID, "Target term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	term2.addDescription(new Description("CustomAttribute2", "Attribute"));

	Term changedTerm1 = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	changedTerm1.addDescription(new Description("CustomAttribute1", "Attribute2"));
	changedTerm1.addDescription(new Description("CustomAttribute2", "Attribute1"));

	// Remove default term
	TermEntry solrTermEntry = getSolrTermEntry();
	solrTermEntry.getLanguageTerms().remove(EN_LANGUAGE_ID);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));
	_importModel.setOverwriteByTermEntryId(false);

	_synchronizer.initialize(getRegularConnector(), _importModel);

	prepareTermEntry(getSolrTermEntry(), new Term[] { term1, term2 }, true);
	prepareTermEntry(getXlsTermEntry(), new Term[] { changedTerm1, term2 }, false);

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);

	assertEquals(0, germanyTermCounts.getUpdatedTermIds().size());
	assertEquals(1, germanyTermCounts.getUnchangedTermIds().size());

	assertEquals(0, germanyTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedTermCount().intValue());
	assertEquals(0, germanyTermCounts.getDeletedTerms().intValue());
	assertEquals(0, germanyTermCounts.getTermCount().intValue());

	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());
	assertEquals(0, englishTermCounts.getUnchangedTermIds().size());

	assertEquals(0, englishTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedTermCount().intValue());
	assertEquals(0, englishTermCounts.getDeletedTerms().intValue());
	assertEquals(0, englishTermCounts.getTermCount().intValue());
    }

    /*
     * TERII-4674 Import Terminology: Import summary doesn't show results properly
     */
    @Test
    public void xlsImportWithOverwriteSyncOptionEquailsBuilderHelperTest() throws TmException {

	String termName = "Source term";

	int numberOfDescriptions = 21;

	Set<Description> originalDescriptions = new HashSet<>(numberOfDescriptions);
	Set<Description> changedDescriptions = new HashSet<>(numberOfDescriptions);

	for (int i = 0; i < numberOfDescriptions; i++) {
	    Description original = createDescription("CustomAttribute" + i, "myAttribute" + i);
	    originalDescriptions.add(original);
	    Description changed = createDescription("CustomAttribute" + i, "myAttribute" + i);
	    changedDescriptions.add(changed);
	}

	Description changed = changedDescriptions.iterator().next();
	changed.setValue("myChangedAttribute");

	Term term1 = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	term1.setDescriptions(originalDescriptions);

	Term term2 = createTerm(DE_LANGUAGE_ID, "Target term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	term2.setDescriptions(originalDescriptions);

	Term changedTerm1 = createTerm(EN_LANGUAGE_ID, termName, IS_FORBIDDEN, PROCESSED, USER_NAME);
	changedTerm1.setDescriptions(changedDescriptions);

	// Remove default term
	TermEntry solrTermEntry = getSolrTermEntry();
	solrTermEntry.getLanguageTerms().remove(EN_LANGUAGE_ID);

	prepareTermEntry(getSolrTermEntry(), new Term[] { term1, term2 }, true);
	prepareTermEntry(getXlsTermEntry(), new Term[] { changedTerm1, term2 }, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));
	_importModel.setOverwriteByTermEntryId(false);
	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);

	assertEquals(0, germanyTermCounts.getUpdatedTermIds().size());
	assertEquals(1, germanyTermCounts.getUnchangedTermIds().size());

	assertEquals(0, germanyTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedTermCount().intValue());
	assertEquals(0, germanyTermCounts.getDeletedTerms().intValue());
	assertEquals(0, germanyTermCounts.getTermCount().intValue());

	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());
	assertEquals(0, englishTermCounts.getUnchangedTermIds().size());

	assertEquals(0, englishTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedBlacklistedCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingCount().intValue());
	assertEquals(0, englishTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, englishTermCounts.getAddedTermCount().intValue());
	assertEquals(0, englishTermCounts.getDeletedTerms().intValue());
	assertEquals(0, englishTermCounts.getTermCount().intValue());
    }

    /*
     * TEST CASE: Overwrite terms[TERII-3312:change only term status] with terms
     * from xls file
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_01() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, WAITING, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, WAITING, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, WAITING, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	// Validate values from import report
	assertNotNull(summary);
	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, summary.getNoImportedTermAttributes().intValue());
	assertEquals(1, summary.getUpdatedTermEntryIds().size());
	assertEquals(0, summary.getNoDeletedTermEntries().intValue());
	assertEquals(0, summary.getNoDuplicatedTerms().intValue());
	assertEquals(0, summary.getNoDeletedTerms().intValue());
	assertEquals(0, summary.getNoImportedTerms().intValue());
	assertEquals(3, summary.getNoUpdatedTerms().intValue());

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getUpdatedTermIds().size());

	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);
	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());

	CountWrapper frenchTermCounts = termCountsPerLanguage.get(FR_LANGUAGE_ID);
	assertEquals(1, frenchTermCounts.getUpdatedTermIds().size());
    }

    /* TEST CASE:: Overwrite existing source and target term */
    @Test
    public void xlsImportWithOverwriteSyncOption_02() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English xls term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", true, BLACKLISTED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	assertNotNull(summary);
	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, summary.getNoImportedTermAttributes().intValue());
	assertEquals(1, summary.getUpdatedTermEntryIds().size());
	assertEquals(0, summary.getNoDeletedTermEntries().intValue());
	assertEquals(0, summary.getNoDuplicatedTerms().intValue());
	assertEquals(0, summary.getNoDeletedTerms().intValue());
	assertEquals(0, summary.getNoImportedTerms().intValue());
	assertEquals(2, summary.getNoUpdatedTerms().intValue());

	// validate approved and blaclisted term count added to the project view
	assertEquals(-1,
		summary.getNoImportedTermsPerLanguage().get(DE_LANGUAGE_ID).getAddedApprovedCount().intValue());
	assertEquals(1,
		summary.getNoImportedTermsPerLanguage().get(DE_LANGUAGE_ID).getAddedBlacklistedCount().intValue());

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getUpdatedTermIds().size());

	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);
	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());
    }

    /* TEST CASE: Import same source term with two different target terms */
    @Test
    public void xlsImportWithOverwriteSyncOption_03() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", true, BLACKLISTED, USER_NAME),
			createTerm("de-DE", "Germany xls synonym", IS_FORBIDDEN, WAITING, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	assertNotNull(summary);
	assertEquals(0, summary.getNoImportedTermAttributes().intValue());
	assertEquals(1, summary.getUpdatedTermEntryIds().size());
	assertEquals(0, summary.getNoDeletedTermEntries().intValue());
	assertEquals(0, summary.getNoDuplicatedTerms().intValue());
	assertEquals(1, summary.getNoDeletedTerms().intValue());
	assertEquals(2, summary.getNoImportedTerms().intValue());
	assertEquals(0, summary.getNoUpdatedTerms().intValue());
	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getTermCount().intValue());

	// validate approved and blaclisted term count added to the project view
	assertEquals(-1, germanyTermCounts.getAddedApprovedCount().intValue());
	assertEquals(1, germanyTermCounts.getAddedBlacklistedCount().intValue());

	assertEquals(1, germanyTermCounts.getDeletedTerms().intValue());
	assertEquals(1, germanyTermCounts.getAddedPendingStatisticsCount().intValue());
    }

    /*
     * TEST CASE: Overwrite existing source term, import new target terms and new
     * term descriptions for both target languages
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_04() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);

	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "Unlimited Country", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTermWithDesc(DE_LANGUAGE_ID, "Länder-Package", false, PROCESSED, USER_NAME,
				createDescription("context", "context xls term value")),
			createTermWithDesc(FR_LANGUAGE_ID, "Illimité Pays", false, PROCESSED, USER_NAME,
				createDescription("comment", "Traducteur commentaire ajouté")) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(2, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(1, importSummary.getNoImportedTerms().intValue());
	assertEquals(2, importSummary.getNoUpdatedTerms().intValue());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	CountWrapper frenchTermCounts = termCountsPerLanguage.get(FR_LANGUAGE_ID);
	assertEquals(1, frenchTermCounts.getTermCount().intValue());
	assertEquals(1, frenchTermCounts.getAddedApprovedStatisticsCount().intValue());

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getUpdatedTermIds().size());

	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);
	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());

	TermEntry solrTermEntry = getRegularConnector().getTmgrBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	// Validate term entry in solr base after overwrite process is completed
	for (Term languageTerm : solrTermEntry.ggetTerms()) {
	    String termName = languageTerm.getName();
	    Set<Description> descriptions = languageTerm.getDescriptions();
	    switch (languageTerm.getLanguageId()) {
	    case EN_LANGUAGE_ID:
		assertEquals("Unlimited Country", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertNull(descriptions);
		break;
	    case DE_LANGUAGE_ID:
		assertEquals("Länder-Package", termName);
		assertTrue(CollectionUtils.isNotEmpty(descriptions));
		assertEquals("context", descriptions.iterator().next().getType());
		assertEquals("context xls term value", descriptions.iterator().next().getValue());
		break;
	    case FR_LANGUAGE_ID:
		assertEquals("Illimité Pays", termName);
		assertTrue(CollectionUtils.isNotEmpty(descriptions));
		assertEquals("comment", descriptions.iterator().next().getType());
		assertEquals("Traducteur commentaire ajouté", descriptions.iterator().next().getValue());
		break;

	    default:
		throw new IllegalArgumentException("Invalid language ID.");
	    }
	}
    }

    /*
     * TEST CASE: Overwrite German "main" term and synonym with one term from xls
     * file
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_05() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany synonym", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTermWithDesc(DE_LANGUAGE_ID, "Germany xls term", IS_FORBIDDEN, WAITING, USER_NAME,
				createDescription("context", "context xls germany value.")) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	assertNotNull(summary);
	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(1, summary.getNoImportedTermAttributes().intValue());
	assertEquals(1, summary.getUpdatedTermEntryIds().size());
	assertEquals(0, summary.getNoDeletedTermEntries().intValue());
	assertEquals(0, summary.getNoDuplicatedTerms().intValue());
	assertEquals(2, summary.getNoDeletedTerms().intValue());
	assertEquals(1, summary.getNoImportedTerms().intValue());
	assertEquals(0, summary.getNoUpdatedTerms().intValue());
	assertEquals(1, summary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(-1, germanyTermCounts.getTermCount().intValue());

	// validate approved and blaclisted term count added to the project view
	assertEquals(-2, germanyTermCounts.getAddedApprovedCount().intValue());
	assertEquals(0, germanyTermCounts.getAddedBlacklistedCount().intValue());

	assertEquals(2, germanyTermCounts.getDeletedTerms().intValue());
	assertEquals(1, germanyTermCounts.getAddedPendingCount().intValue());
    }

    /*
     * TEST CASE: Overwrite German "main" term with term from xls file and add
     * synonym, overwrite French term with term from xls file and import new
     * description for that term
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_06() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany xls synonym", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTermWithDesc(FR_LANGUAGE_ID, "French xls term", false, PROCESSED, USER_NAME,
				createDescription("comment", "Traducteur commentaire ajouté.")) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(1, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(1, importSummary.getNoDeletedTerms().intValue());
	assertEquals(2, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(1, importSummary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> importedTermsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	assertEquals(1, importedTermsPerLanguage.get(DE_LANGUAGE_ID).getTermCount().intValue());

	// validate approved and blaclisted term count added to the project view
	assertEquals(1,
		importSummary.getNoImportedTermsPerLanguage().get(DE_LANGUAGE_ID).getAddedApprovedCount().intValue());
	assertEquals(0, importSummary.getNoImportedTermsPerLanguage().get(DE_LANGUAGE_ID).getAddedBlacklistedCount()
		.intValue());
    }

    /*
     * TEST CASE: Update English term, delete terms in the German language,
     * overwrite French main term and add description for that language
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_07() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "germany synonym", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "french main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "Fair Usage Policy", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTermWithDesc(FR_LANGUAGE_ID, "Politique d'utilisation équitable.", IS_FORBIDDEN,
				PROCESSED, USER_NAME, createDescription("comment", "Traducteur commentaire ajouté.")) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	assertNotNull(summary);
	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(1, summary.getNoImportedTermAttributes().intValue());
	assertEquals(1, summary.getUpdatedTermEntryIds().size());
	assertEquals(0, summary.getNoDeletedTermEntries().intValue());
	assertEquals(0, summary.getNoDuplicatedTerms().intValue());
	assertEquals(2, summary.getNoDeletedTerms().intValue());
	assertEquals(0, summary.getNoImportedTerms().intValue());
	assertEquals(2, summary.getNoUpdatedTerms().intValue());
	assertEquals(0, summary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();

	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(-2, germanyTermCounts.getTermCount().intValue());
	assertEquals(2, germanyTermCounts.getDeletedTerms().intValue());

	// Validate that German terms are deleted from term entry

	TermEntry solrTermEntry = getRegularConnector().getTmgrBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	for (Term languageTerm : solrTermEntry.ggetTerms()) {
	    String termName = languageTerm.getName();
	    Set<Description> descriptions = languageTerm.getDescriptions();
	    switch (languageTerm.getLanguageId()) {
	    case EN_LANGUAGE_ID:
		assertEquals("Fair Usage Policy", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertNull(descriptions);
		break;
	    case FR_LANGUAGE_ID:
		assertEquals("Politique d'utilisation équitable.", termName);
		assertTrue(CollectionUtils.isNotEmpty(descriptions));
		assertEquals("comment", descriptions.iterator().next().getType());
		assertEquals("Traducteur commentaire ajouté.", descriptions.iterator().next().getValue());
		break;

	    default:
		throw new IllegalArgumentException("Invalid language ID.");
	    }
	}
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_08() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French synonym", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);

	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "Birthday reminder", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Erinnerungsmeldung für Geburtstage", IS_FORBIDDEN, PROCESSED,
				USER_NAME),
			createTermWithDesc(FR_LANGUAGE_ID, "rappel d'anniversaire", false, PROCESSED, USER_NAME,
				createDescription("comment", "Skype rappel.")) },
		false);

	getXlsTermEntry().addDescription(
		createDescription("comment", "UI feature; notifies about upcoming birthdays in your contact list."));

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(1, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(1, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(2, importSummary.getNoDeletedTerms().intValue());
	assertEquals(1, importSummary.getNoImportedTerms().intValue());
	assertEquals(2, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(1, importSummary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	CountWrapper frenchTermCounts = termCountsPerLanguage.get(FR_LANGUAGE_ID);
	assertEquals(-1, frenchTermCounts.getTermCount().intValue());
	assertEquals(2, frenchTermCounts.getDeletedTerms().intValue());
	assertEquals(1, frenchTermCounts.getAddedApprovedStatisticsCount().intValue());

	CountWrapper englishTermCounts = termCountsPerLanguage.get(EN_LANGUAGE_ID);
	assertEquals(1, englishTermCounts.getUpdatedTermIds().size());
	CountWrapper germanyTermCounts = termCountsPerLanguage.get(DE_LANGUAGE_ID);
	assertEquals(1, germanyTermCounts.getUpdatedTermIds().size());

	TermEntry solrTermEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	for (Term languageTerm : solrTermEntry.ggetTerms()) {
	    String termName = languageTerm.getName();
	    Set<Description> descriptions = languageTerm.getDescriptions();
	    switch (languageTerm.getLanguageId()) {
	    case EN_LANGUAGE_ID:
		assertEquals("Birthday reminder", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertNull(descriptions);
		break;
	    case DE_LANGUAGE_ID:
		assertEquals("Erinnerungsmeldung für Geburtstage", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertNull(descriptions);
		break;
	    case FR_LANGUAGE_ID:
		assertEquals("rappel d'anniversaire", termName);
		assertTrue(CollectionUtils.isNotEmpty(descriptions));
		assertEquals("comment", descriptions.iterator().next().getType());
		assertEquals("Skype rappel.", descriptions.iterator().next().getValue());
		break;

	    default:
		throw new IllegalArgumentException("Invalid language ID.");
	    }
	}
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_09() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossaryBrowser browser = getBrowser();

	TermEntry termEntry = browser.findById(TERM_ENTRY_ID, PROJECT_ID);

	// add synonyms
	prepareTermEntry(termEntry,
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany syn1", false, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany syn2", false, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany syn3.", false, PROCESSED, USER_NAME) },
		true);

	assertEquals(5, termEntry.ggetTerms().size());

	// import second time and overwrite synonyms for target lang.
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	termEntry = browser.findById(TERM_ENTRY_ID, PROJECT_ID);

	assertEquals(Action.IMPORTED, termEntry.getAction());
	assertEquals(5, termEntry.ggetTerms().size());
	assertEquals(5, termEntry.ggetAllTerms().size());
    }

    /*
     * All terms in the project language(s) for a given term entry in TMGR will be
     * overwritten with content from the file. Languages that are in TMGR but not in
     * the file shouldn't be changed.
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_10() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(0, importSummary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> importedTermsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	assertEquals(0, importedTermsPerLanguage.get(DE_LANGUAGE_ID).getTermCount().intValue());

	TermEntry termEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);

	assertEquals(Action.IMPORTED, termEntry.getAction());
	assertEquals(3, termEntry.ggetTerms().size());
	assertEquals(3, termEntry.ggetAllTerms().size());
    }

    /*
     * TEST CASE: There is locale for French language in header but we don't have
     * value for that language [EXPECTED: delete this language]
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_11() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(1, importSummary.getNoDeletedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(0, importSummary.getImportedTargetLanguages().size());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> importedTermsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	assertEquals(-1, importedTermsPerLanguage.get(FR_LANGUAGE_ID).getTermCount().intValue());

	TermEntry termEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);

	assertEquals(Action.IMPORTED, termEntry.getAction());
	assertEquals(2, termEntry.ggetTerms().size());
	assertEquals(2, termEntry.ggetAllTerms().size());
    }

    /*
     * TEST CASE: There are terms in Term Manager with "In Translation Review" or
     * "In Final Review" status values. Terms with these statuses shouldn't be
     * changed.
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_12() throws TmException {
	prepareTermEntry(getSolrTermEntry(), new Term[] {
		createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
		createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, IN_TRANSLATION_REVIEW, USER_NAME) }, true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French xls term", IS_FORBIDDEN, PROCESSED, USER_NAME), },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(0, importSummary.getImportedTargetLanguages().size());
	assertEquals(1, importSummary.getNoSkippedTerms().intValue());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> termCountsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	CountWrapper frenchTermCounts = termCountsPerLanguage.get(FR_LANGUAGE_ID);
	assertEquals(0, frenchTermCounts.getTermCount().intValue());
	assertEquals(0, frenchTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, frenchTermCounts.getAddedPendingStatisticsCount().intValue());
	assertEquals(0, frenchTermCounts.getUpdatedTermIds().size());
	assertEquals(0, frenchTermCounts.getDeletedTerms().intValue());

	TermEntry termEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Set<Term> langSet = languageTerms.get(FR_LANGUAGE_ID);
	Iterator<Term> langIterator = langSet.iterator();
	Term term = langIterator.next();

	assertEquals(3, termEntry.ggetTerms().size());
	assertEquals(3, termEntry.ggetAllTerms().size());

	assertEquals(IN_TRANSLATION_REVIEW, term.getStatus());
    }

    /*
     * TEST CASE: In case the content in file is "In Translation Review" or
     * "In Final Review" the user will be informed that terms with these statuses
     * will be skipped during import.
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_13() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm("de-DE", "Germany xls term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French xls term", IS_FORBIDDEN, IN_FINAL_REVIEW, USER_NAME), },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(0, importSummary.getImportedTargetLanguages().size());
	assertEquals(1, importSummary.getNoSkippedTerms().intValue());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> importedTermsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	assertEquals(0, importedTermsPerLanguage.get(FR_LANGUAGE_ID).getTermCount().intValue());

	TermEntry termEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();
	Set<Term> langSet = languageTerms.get(FR_LANGUAGE_ID);
	Iterator<Term> langIterator = langSet.iterator();
	Term term = langIterator.next();

	assertEquals(3, termEntry.ggetTerms().size());
	assertEquals(3, termEntry.ggetAllTerms().size());

	assertEquals(PROCESSED, term.getStatus());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_14() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] {
			createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, IN_TRANSLATION_REVIEW, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(FR_LANGUAGE_ID, "French synonym term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "New english main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany xls term", IS_FORBIDDEN, IN_FINAL_REVIEW, USER_NAME),
			createTerm(EN, "En main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel
		.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID, EN }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(2, importSummary.getNoDeletedTerms().intValue());
	assertEquals(1, importSummary.getNoImportedTerms().intValue());
	assertEquals(1, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(1, importSummary.getImportedTargetLanguages().size());
	assertEquals(1, importSummary.getNoSkippedTerms().intValue());

	// Validate the number of terms that will be added to the project
	Map<String, CountWrapper> importedTermsPerLanguage = importSummary.getNoImportedTermsPerLanguage();

	assertEquals(-2, importedTermsPerLanguage.get(FR_LANGUAGE_ID).getTermCount().intValue());

	assertEquals(1, importedTermsPerLanguage.get(EN).getTermCount().intValue());

	TermEntry solrTermEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	for (Term languageTerm : solrTermEntry.ggetTerms()) {
	    String termName = languageTerm.getName();
	    Set<Description> descriptions = languageTerm.getDescriptions();
	    switch (languageTerm.getLanguageId()) {
	    case EN_LANGUAGE_ID:
		assertEquals("New english main term", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertEquals(PROCESSED, languageTerm.getStatus());
		assertNull(descriptions);
		break;
	    case DE_LANGUAGE_ID:
		assertEquals("Germany main term", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertEquals(IN_TRANSLATION_REVIEW, languageTerm.getStatus());
		assertNull(descriptions);
		break;
	    case EN:
		assertEquals("En main term", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertEquals(PROCESSED, languageTerm.getStatus());
		assertNull(descriptions);
		break;

	    default:
		throw new IllegalArgumentException("Invalid language ID.");
	    }
	}
    }

    // TERII-3289
    @Test
    public void xlsImportWithOverwriteSyncOption_15() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] {
			createTerm(DE_LANGUAGE_ID, "Germany(Germany) main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN, "call forwarding", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE, "Anrufweiterleitung", IS_FORBIDDEN, BLACKLISTED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN, DE }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);

	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());

	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(2, importSummary.getNoImportedTerms().intValue());
	assertEquals(0, importSummary.getNoUpdatedTerms().intValue());

	// validate approved and blaclisted term count added to the project view
	Map<String, CountWrapper> termCountsPerLanguage = importSummary.getNoImportedTermsPerLanguage();
	CountWrapper enTermCounts = termCountsPerLanguage.get(EN);
	assertEquals(1, enTermCounts.getAddedApprovedCount().intValue());

	CountWrapper deTermCounts = termCountsPerLanguage.get(DE);
	assertEquals(1, deTermCounts.getAddedBlacklistedCount().intValue());

	assertEquals(1, enTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, deTermCounts.getAddedApprovedStatisticsCount().intValue());
	assertEquals(0, deTermCounts.getAddedPendingStatisticsCount().intValue());

	TermEntry termEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);

	assertEquals(Action.IMPORTED, termEntry.getAction());
	assertEquals(4, termEntry.ggetTerms().size());
	assertEquals(4, termEntry.ggetAllTerms().size());

    }

    /* TERII-3316: Terms in workflow submission are imported using overwrite */
    @Test
    public void xlsImportWithOverwriteSyncOption_16() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME), },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "New english term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "New germany term", IS_FORBIDDEN, IN_FINAL_REVIEW, USER_NAME), },
		false);

	// Set new TermEntryID and import this term entry as new
	String termEntryID = UUID.randomUUID().toString();

	getXlsTermEntry().setUuId(termEntryID);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	/* This test should validate fix in TmgrGlossaryImporter */
	ImportProgressInfo importProgressInfo = new ImportProgressInfo(1);
	_tmgrImporter.handleImport(getReader(), getImportModel(), importProgressInfo.getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = importProgressInfo.getImportSummary();

	// Validate values from import report
	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(0, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(1, importSummary.getNoImportedTerms().intValue());
	assertEquals(0, importSummary.getNoUpdatedTerms().intValue());
	// Skip terms in submission
	assertEquals(1, importSummary.getNoSkippedTerms().intValue());
    }

    /*
     * Ensure modification date and modification user is not changed if a term is
     * not updated with overwrite on import
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_17() throws TmException {
	Term g_term = createTerm(DE_LANGUAGE_ID, "Germany main term", //$NON-NLS-1$
		IS_FORBIDDEN, PROCESSED, USER_NAME);
	Term f_term = createTerm(FR_LANGUAGE_ID, "French main term", //$NON-NLS-1$
		IS_FORBIDDEN, PROCESSED, USER_NAME);

	Long dateModified_01 = 1428311236979L;
	g_term.setDateModified(dateModified_01);
	g_term.setUserModified("super"); //$NON-NLS-1$

	prepareTermEntry(getSolrTermEntry(), new Term[] { g_term, f_term }, true);

	Term g_clonedTerm = g_term.cloneTerm();
	Long dateModified_02 = 1428311811508L;
	// Term from file with different modified date and modification user
	g_clonedTerm.setDateModified(dateModified_02);
	g_clonedTerm.setUserModified("power"); //$NON-NLS-1$

	Term e_term = createTerm(EN_LANGUAGE_ID, "New English term", //$NON-NLS-1$
		IS_FORBIDDEN, WAITING, USER_NAME);
	Term new_f_term = createTerm(FR_LANGUAGE_ID, "New French term", //$NON-NLS-1$
		IS_FORBIDDEN, WAITING, USER_NAME);

	prepareTermEntry(getXlsTermEntry(), new Term[] { e_term, g_clonedTerm, new_f_term }, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID, FR_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	_tmgrImporter.handleImport(getReader(), getImportModel(), summary, getTermEntryCallback(), null,
		getTimeConsumer(), null, null, getSynchronizer(), null, getImportTransactionLogHandler(),
		getRegularCollection());

	ITmgrGlossaryBrowser browser = getBrowser();

	TermEntry termEntry = browser.findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	Map<String, Set<Term>> languageTerms = termEntry.getLanguageTerms();

	Set<Term> langSet = languageTerms.get(DE_LANGUAGE_ID);

	Iterator<Term> langIterator = langSet.iterator();

	Term d_term = langIterator.next();

	assertNotNull(d_term);
	assertEquals(dateModified_01, d_term.getDateModified());
	assertEquals("super", d_term.getUserModified());

    }

    // TERII-3390: Submitted term are deleted on overwrite on import
    @Test
    public void xlsImportWithOverwriteSyncOption_18() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] {
			createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, IN_FINAL_REVIEW, USER_NAME), },
		true);

	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME), },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(0, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
	assertEquals(0, importSummary.getNoDuplicatedTerms().intValue());
	assertEquals(0, importSummary.getNoDeletedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTerms().intValue());
	assertEquals(0, importSummary.getNoUpdatedTerms().intValue());
	assertEquals(0, importSummary.getImportedTargetLanguages().size());
	assertEquals(0, importSummary.getNoSkippedTerms().intValue());
	assertEquals(0, importSummary.getNoImportedTermEntryAttributes().intValue());

	TermEntry solrTermEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	for (Term languageTerm : solrTermEntry.ggetTerms()) {
	    String termName = languageTerm.getName();
	    Set<Description> descriptions = languageTerm.getDescriptions();
	    switch (languageTerm.getLanguageId()) {
	    case EN_LANGUAGE_ID:
		assertEquals("English main term", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertEquals(PROCESSED, languageTerm.getStatus());
		assertNull(descriptions);
		break;
	    case DE_LANGUAGE_ID:
		assertEquals("Germany main term", termName);
		assertEquals(USER_NAME, languageTerm.getUserModified());
		assertEquals(USER_NAME, languageTerm.getUserCreated());
		assertEquals(IN_FINAL_REVIEW, languageTerm.getStatus());
		assertNull(descriptions);
		break;
	    default:
		throw new IllegalArgumentException("Invalid language ID.");
	    }
	}
    }

    // TERII-3418: Term entry count is off after using overwrite on import
    @Test
    public void xlsImportWithOverwriteSyncOption_19() throws TmException {
	Term enTerm = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);
	Term deTerm = createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	prepareTermEntry(getSolrTermEntry(), new Term[] { enTerm, deTerm }, true);

	prepareTermEntry(getXlsTermEntry(), new Term[] {}, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	assertNotNull(importSummary);
	assertEquals(0, importSummary.getNoImportedTermEntries().intValue());
	assertEquals(0, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(1, importSummary.getNoDeletedTermEntries().intValue());

	TermEntry solrTermEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);
	assertEquals(Action.IMPORTED, solrTermEntry.getAction());

	assertTrue(CollectionUtils.isEmpty(solrTermEntry.ggetTerms()));
    }

    /*
     * TEST CASE: Overwrite only term entry descriptions with term entry
     * descriptions from xls file
     */
    @Test
    public void xlsImportWithOverwriteSyncOption_20() throws TmException {
	prepareTermEntry(getSolrTermEntry(), new Term[0], true);
	Term[] languageTerms = new Term[] {
		createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME), };
	prepareTermEntry(getXlsTermEntry(), languageTerms, false);

	getXlsTermEntry().addDescription(createDescription("definition", "Skype product"));

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary importSummary = getImportProgressInfo().getImportSummary();

	// Validate counts from import report
	assertNotNull(importSummary);
	assertEquals(1, importSummary.getNoImportedTermEntryAttributes().intValue());
	assertEquals(0, importSummary.getNoImportedTermAttributes().intValue());
	assertEquals(1, importSummary.getUpdatedTermEntryIds().size());
	assertEquals(0, importSummary.getNoDeletedTermEntries().intValue());
    }

    // TERII-4190
    // Term demoted through overwrite on import does not get removed in WF3
    @Test
    public void xlsImportWithOverwriteSyncOption_21() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, WAITING, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, WAITING, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setFetchDeleted(true);
	filter.setProjectIds(Arrays.asList(PROJECT_ID));
	filter.setSourceLanguage(EN_LANGUAGE_ID);
	filter.setTargetLanguages(Arrays.asList(DE_LANGUAGE_ID));

	Page<TermEntry> page = searcher.concordanceSearch(filter);

	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	TermEntry termEntry = results.get(0);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	// count disabled terms also
	assertEquals(4, termEntry.ggetAllTerms().size());
	// count just non disabled terms
	assertEquals(2, termEntry.ggetTerms().size());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_22() throws TmException {
	/*
	 * [issue TERII-4122]: Project List - Import - Import (overwrite) does not
	 * remove all terms from project.
	 */
	Term existingGermany = createTerm(DE_LANGUAGE_ID, "property management software", IS_FORBIDDEN, PROCESSED,
		USER_NAME);
	existingGermany.addDescription(createDescription("category", "SEO Keyword"));
	existingGermany.addDescription(createDescription("monthlySearchValue", "50"));
	existingGermany.addDescription(createDescription("priority", "Primary"));

	Term existingGermany1 = createTerm(DE_LANGUAGE_ID, "software voor makelaars", IS_FORBIDDEN, PROCESSED,
		USER_NAME);
	existingGermany1.addDescription(createDescription("category", "SEO Keyword"));
	existingGermany1.addDescription(createDescription("priority", "Secondary"));
	existingGermany1.addDescription(createDescription("monthlySearchValue", "10"));

	prepareTermEntry(getSolrTermEntry(), new Term[] { existingGermany, existingGermany1 }, true);

	Term incomingEnglish = createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME);

	Term incomingGermany = createTerm(DE_LANGUAGE_ID, "property management software", IS_FORBIDDEN, PROCESSED,
		USER_NAME);
	incomingGermany.addDescription(createDescription("category", "SEO Keyword"));
	incomingGermany.addDescription(createDescription("monthlySearchVolume", "50"));
	incomingGermany.addDescription(createDescription("monthlySearchValue", "50"));
	incomingGermany.addDescription(createDescription("priority", "Primary"));

	Term incomingGermany1 = createTerm(DE_LANGUAGE_ID, "software voor makelaars", IS_FORBIDDEN, PROCESSED,
		USER_NAME);
	incomingGermany1.addDescription(createDescription("category", "SEO Keyword"));
	incomingGermany1.addDescription(createDescription("priority", "Secondary"));
	incomingGermany1.addDescription(createDescription("monthlySearchValue", "10"));

	prepareTermEntry(getXlsTermEntry(), new Term[] { incomingEnglish, incomingGermany, incomingGermany1 }, false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	ImportSummary summary = getImportProgressInfo().getImportSummary();

	_tmgrImporter.handleImport(getReader(), getImportModel(), summary, getTermEntryCallback(), null,
		getTimeConsumer(), null, null, getSynchronizer(), null, getImportTransactionLogHandler(),
		getRegularCollection());

	Map<String, CountWrapper> termCountsByLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsByLanguage.get(DE_LANGUAGE_ID).getUpdatedTermIds().size());

	TermEntry emptyTermEntry = new TermEntry(PROJECT_ID, SHORT_CODE, USER_NAME);
	emptyTermEntry.setProjectName(PROJECT_NAME);
	emptyTermEntry.setUuId(TERM_ENTRY_ID);

	_synchronizer = TermEntrySynchronizerFactory.INSTANCE.createTermEntrySynchronizer((SyncOption.OVERWRITE));
	_synchronizer.initialize(getRegularConnector(), _importModel);

	setUpConfiguration(emptyTermEntry);

	_tmgrImporter.handleImport(getReader(), getImportModel(), summary, getTermEntryCallback(), null,
		getTimeConsumer(), null, null, _synchronizer, null, getImportTransactionLogHandler(),
		getRegularCollection());

	TermEntry ovewrittenTermEntry = getBrowser().findById(TERM_ENTRY_ID, PROJECT_ID);

	assertEquals(0, ovewrittenTermEntry.getLanguageTerms().size());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_23_ignoreCaseTrue() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "GERMANY MAIN TERM", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setFetchDeleted(true);
	filter.setProjectIds(Arrays.asList(PROJECT_ID));
	filter.setSourceLanguage(EN_LANGUAGE_ID);
	filter.setTargetLanguages(Arrays.asList(DE_LANGUAGE_ID));

	Page<TermEntry> page = searcher.concordanceSearch(filter);

	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	TermEntry termEntry = results.get(0);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	// count disabled terms also
	assertEquals(2, termEntry.ggetAllTerms().size());
	// count just non disabled terms
	assertEquals(2, termEntry.ggetTerms().size());

	Set<Term> deTerms = termEntry.getLanguageTerms().get(DE_LANGUAGE_ID);
	assertEquals(1, deTerms.size());

	Term deTerm = deTerms.stream().filter(Term::isFirst).findFirst().orElse(null);
	assertNotNull(deTerm);
	assertEquals("Germany main term", deTerm.getName());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	assertEquals(1, summary.getUnchangedTermEntrieIds().size());

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(EN_LANGUAGE_ID).getUnchangedTermIds().size());
	assertEquals(1, termCountsPerLanguage.get(DE_LANGUAGE_ID).getUnchangedTermIds().size());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_24_ignoreCaseFalse() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(EN_LANGUAGE_ID, "English main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "GERMANY MAIN TERM", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	// ignore case on false
	_importModel.setIgnoreCase(false);

	_synchronizer.initialize(getRegularConnector(), _importModel);

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setFetchDeleted(true);
	filter.setProjectIds(Arrays.asList(PROJECT_ID));
	filter.setSourceLanguage(EN_LANGUAGE_ID);
	filter.setTargetLanguages(Arrays.asList(DE_LANGUAGE_ID));

	Page<TermEntry> page = searcher.concordanceSearch(filter);

	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	TermEntry termEntry = results.get(0);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	// count disabled terms also
	assertEquals(3, termEntry.ggetAllTerms().size());
	// count just non disabled terms
	assertEquals(2, termEntry.ggetTerms().size());

	Set<Term> deTerms = termEntry.getLanguageTerms().get(DE_LANGUAGE_ID);
	assertEquals(2, deTerms.size());

	Term deTerm = deTerms.stream().filter(Term::isFirst).findFirst().orElse(null);
	assertNotNull(deTerm);
	assertEquals("GERMANY MAIN TERM", deTerm.getName());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(DE_LANGUAGE_ID).getUpdatedTermIds().size());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_25_ignoreCaseTrue() throws TmException {
	TermEntry existing = getSolrTermEntry();
	existing.addDescription(new Description("definition", "entry definition value"));

	prepareTermEntry(existing, new Term[0], true);

	TermEntry incoming = getXlsTermEntry();
	incoming.addDescription(new Description("DEFINITION", "ENTRY DEFINITION VALUE"));

	prepareTermEntry(incoming, new Term[0], false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));

	setUpConfiguration(incoming);

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(EN_LANGUAGE_ID).getDeletedTerms().intValue());

	assertEquals(0, summary.getNoImportedTermEntryAttributes().intValue());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_26_ignoreCaseFalse() throws TmException {
	TermEntry existing = getSolrTermEntry();
	existing.addDescription(new Description("definition", "entry definition value"));

	prepareTermEntry(existing, new Term[0], true);

	TermEntry incoming = getXlsTermEntry();
	incoming.addDescription(new Description("DEFINITION", "ENTRY DEFINITION VALUE"));

	prepareTermEntry(incoming, new Term[0], false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID }));

	// ignore case on false
	_importModel.setIgnoreCase(false);

	_synchronizer.initialize(getRegularConnector(), _importModel);

	setUpConfiguration(incoming);

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(EN_LANGUAGE_ID).getDeletedTerms().intValue());

	assertEquals(1, summary.getNoImportedTermEntryAttributes().intValue());
	assertEquals("DEFINITION", summary.getImportedTermEntryAttributes().iterator().next());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_27_ignoreCaseTrue() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] {
			createTerm(EN_LANGUAGE_ID, "English main updated term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "GERMANY MAIN TERM", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { EN_LANGUAGE_ID, DE_LANGUAGE_ID }));

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setFetchDeleted(true);
	filter.setProjectIds(Arrays.asList(PROJECT_ID));
	filter.setSourceLanguage(EN_LANGUAGE_ID);
	filter.setTargetLanguages(Arrays.asList(DE_LANGUAGE_ID));

	Page<TermEntry> page = searcher.concordanceSearch(filter);

	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	TermEntry termEntry = results.get(0);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	// count disabled terms also
	assertEquals(3, termEntry.ggetAllTerms().size());
	// count just non disabled terms
	assertEquals(2, termEntry.ggetTerms().size());

	Set<Term> deTerms = termEntry.getLanguageTerms().get(DE_LANGUAGE_ID);
	assertEquals(1, deTerms.size());

	Term deTerm = deTerms.stream().filter(Term::isFirst).findFirst().orElse(null);
	assertNotNull(deTerm);
	assertEquals("Germany main term", deTerm.getName());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(EN_LANGUAGE_ID).getUpdatedTermIds().size());
	assertEquals(0, termCountsPerLanguage.get(DE_LANGUAGE_ID).getUpdatedTermIds().size());
    }

    @Test
    public void xlsImportWithOverwriteSyncOption_TERII_5860() throws TmException {
	prepareTermEntry(getSolrTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		true);
	prepareTermEntry(getXlsTermEntry(),
		new Term[] { createTerm(DE_LANGUAGE_ID, "Germany main term", IS_FORBIDDEN, PROCESSED, USER_NAME),
			createTerm(DE_LANGUAGE_ID, "Germany synonym term", IS_FORBIDDEN, PROCESSED, USER_NAME) },
		false);

	_importModel.setImportLocales(Arrays.asList(new String[] { DE_LANGUAGE_ID }));
	_importModel.setSyncLanguageId(DE_LANGUAGE_ID);

	// ignore case on false
	_importModel.setIgnoreCase(false);
	_importModel.setOverwriteByTermEntryId(false);

	_synchronizer.initialize(getRegularConnector(), _importModel);

	setUpConfiguration(getXlsTermEntry());

	// import first time
	_tmgrImporter.handleImport(getReader(), getImportModel(), getImportProgressInfo().getImportSummary(),
		getTermEntryCallback(), null, getTimeConsumer(), null, null, getSynchronizer(), null,
		getImportTransactionLogHandler(), getRegularCollection());

	ITmgrGlossarySearcher searcher = getSearcher();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setFetchDeleted(true);
	filter.setProjectIds(Arrays.asList(PROJECT_ID));
	filter.setSourceLanguage(DE_LANGUAGE_ID);

	Page<TermEntry> page = searcher.concordanceSearch(filter);

	assertEquals(1, page.getTotalResults());

	List<TermEntry> results = page.getResults();
	assertEquals(1, results.size());

	TermEntry termEntry = results.get(0);
	assertEquals(Action.IMPORTED, termEntry.getAction());

	// count just non disabled terms
	assertEquals(3, termEntry.ggetTerms().size());

	Set<Term> deTerms = termEntry.getLanguageTerms().get(DE_LANGUAGE_ID);
	assertEquals(2, deTerms.size());

	ImportSummary summary = getImportProgressInfo().getImportSummary();
	assertNotNull(summary);

	Map<String, CountWrapper> termCountsPerLanguage = summary.getNoImportedTermsPerLanguage();
	assertEquals(1, termCountsPerLanguage.get(DE_LANGUAGE_ID).getUnchangedTermIds().size());
	assertEquals(1, termCountsPerLanguage.get(DE_LANGUAGE_ID).getAddedTermCount().intValue());
    }

    @Override
    protected void assertOverwriteTermAttributeIgnoreCase(String termEntryId, String newValue, String existingType)
	    throws TmException {
	TermEntry termEntry = getBrowser().findById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);

	List<Term> terms = termEntry.ggetTerms();
	assertTrue(CollectionUtils.isNotEmpty(terms));
	assertEquals(1, terms.size());

	Term term = terms.iterator().next();
	assertNotNull(term);

	Set<Description> descriptions = term.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(descriptions));
	assertEquals(1, descriptions.size());

	Description desc = descriptions.iterator().next();
	assertEquals(newValue, desc.getValue());
	assertEquals(existingType, desc.getType());
    }

    @Override
    protected void assertOverwriteTermEntryAttributeIgnoreCase(String termEntryId, String newValue, String existingType)
	    throws TmException {
	TermEntry termEntry = getBrowser().findById(termEntryId, PROJECT_ID);
	assertNotNull(termEntry);

	Set<Description> descriptions = termEntry.getDescriptions();
	assertTrue(CollectionUtils.isNotEmpty(descriptions));
	assertEquals(1, descriptions.size());

	Description desc = descriptions.iterator().next();
	assertEquals(newValue, desc.getValue());
	assertEquals(existingType, desc.getType());
    }

    @Override
    protected ITermEntrySynchronizer initTermEntrySynchronizer() {
	return TermEntrySynchronizerFactory.INSTANCE.createTermEntrySynchronizer((SyncOption.OVERWRITE));
    }
}
