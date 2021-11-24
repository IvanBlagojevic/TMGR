package org.gs4tr.termmanager.service.mocking.manualtask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.cache.CacheGateway;
import org.gs4tr.termmanager.cache.model.CacheName;
import org.gs4tr.termmanager.model.ProjectTerminologyCounts;
import org.gs4tr.termmanager.model.dto.BatchImportSummary;
import org.gs4tr.termmanager.model.dto.BatchImportSummary.Counts;
import org.gs4tr.termmanager.model.dto.BatchImportSummary.TermCounts;
import org.gs4tr.termmanager.service.impl.ImportProgressInfo;
import org.gs4tr.termmanager.service.manualtask.CheckImportProgressTaskHandler;
import org.gs4tr.termmanager.service.mocking.model.TestCase;
import org.gs4tr.termmanager.service.mocking.model.TestSuite;
import org.gs4tr.termmanager.service.model.command.CheckImportProgressCommand;
import org.gs4tr.termmanager.service.project.terminology.counts.ProjectTerminologyCountsProvider;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@TestSuite("manualtask")
public class CheckImportProgressTaskHandlerTest extends AbstractManualtaskTest {

    private static final String TASK_NAME = "check import progress";

    @Autowired
    @Qualifier("guavaCacheGateway")
    private CacheGateway<String, ImportProgressInfo> _cacheGateway;

    @Autowired
    private CheckImportProgressTaskHandler _taskHandler;

    @Autowired
    private ProjectTerminologyCountsProvider _terminologyCountsProvider;

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("checkImportProgress")
    public void checkImportPercentageWhenBatchImportRunningInTheBackgroundTest() {
	CheckImportProgressCommand cmd = getModelObject("batchImportProgressCommand", CheckImportProgressCommand.class);

	Map<String, ImportProgressInfo> importInfos = getModelObject("batchImportInfos1", Map.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class))).thenReturn(importInfos);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));

	assertFalse(model.containsKey("importInfo"));
	assertEquals(50, (int) model.get("importPercentage"));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("checkImportProgress")
    public void checkImportPercentageWhenImportRunningInTheBackgroundTest() {
	CheckImportProgressCommand cmd = getModelObject("importProgressCommand", CheckImportProgressCommand.class);

	Map<String, ImportProgressInfo> importInfos = getModelObject("importInfos1", Map.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class))).thenReturn(importInfos);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));

	assertFalse(model.containsKey("importInfo"));
	assertEquals(70, (int) model.get("importPercentage"));
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("checkImportProgress")
    public void checkImportSummaryWhenBatchImportIsFinishedTest() {
	CheckImportProgressCommand cmd = getModelObject("batchImportProgressCommand", CheckImportProgressCommand.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class)))
		.thenReturn(getModelObject("batchImportInfos2", Map.class));
	when(getTerminologyCountsProvider().getProjectTerminologyCounts(any(Long.class), anyListOf(String.class)))
		.thenReturn(getModelObject("postImportCounts2", ProjectTerminologyCounts.class));

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));
	verify(getTerminologyCountsProvider()).getProjectTerminologyCounts(any(Long.class), anyListOf(String.class));

	assertEquals(100, (int) model.get("importPercentage"));

	BatchImportSummary summary = (BatchImportSummary) model.get("importSummary");
	assertFalse(summary.getImportTime().isEmpty());

	Counts termEntries = summary.getTermEntries();
	assertEquals(1, termEntries.getPreTotal());
	assertEquals(148, termEntries.getAdded());
	assertEquals(15, termEntries.getRemoved());
	assertEquals(110, termEntries.getPostTotal());

	Collection<TermCounts> terms = summary.getTerms();
	assertEquals(3, terms.size());

	TermCounts englishTermCounts = findByLanguageCode(terms, Locale.ENGLISH.getCode());
	assertEquals(1, englishTermCounts.getPreTotal());
	assertEquals(148, englishTermCounts.getAdded());
	// assertEquals(9, englishTermCounts.getUnchanged());
	assertEquals(20, englishTermCounts.getRemoved());
	// assertEquals(40, englishTermCounts.getUpdated());
	assertEquals(137, englishTermCounts.getPostTotal());

	TermCounts germanyTermCounts = findByLanguageCode(terms, Locale.GERMANY.getCode());
	assertEquals(1, germanyTermCounts.getPreTotal());
	assertEquals(64, germanyTermCounts.getAdded());
	// assertEquals(15, germanyTermCounts.getUnchanged());
	assertEquals(15, germanyTermCounts.getRemoved());
	// assertEquals(20, germanyTermCounts.getUpdated());
	assertEquals(64, germanyTermCounts.getPostTotal());

	TermCounts frenchTermCounts = findByLanguageCode(terms, Locale.FRANCE.getCode());
	assertEquals(1, frenchTermCounts.getPreTotal());
	assertEquals(164, frenchTermCounts.getAdded());
	// assertEquals(23, frenchTermCounts.getUnchanged());
	assertEquals(30, frenchTermCounts.getRemoved());
	// assertEquals(60, frenchTermCounts.getUpdated());
	assertEquals(108, frenchTermCounts.getPostTotal());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("checkImportProgress")
    public void checkImportSummaryWhenImportIsFinishedTest() {

	CheckImportProgressCommand cmd = getModelObject("importProgressCommand", CheckImportProgressCommand.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class)))
		.thenReturn(getModelObject("importInfos2", Map.class));
	when(getTerminologyCountsProvider().getProjectTerminologyCounts(any(Long.class), anyListOf(String.class)))
		.thenReturn(getModelObject("postImportCounts1", ProjectTerminologyCounts.class));

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));
	verify(getTerminologyCountsProvider()).getProjectTerminologyCounts(any(Long.class), anyListOf(String.class));

	assertEquals(100, (int) model.get("importPercentage"));

	BatchImportSummary summary = (BatchImportSummary) model.get("importSummary");
	assertFalse(summary.getImportTime().isEmpty());

	Counts termEntries = summary.getTermEntries();
	assertEquals(1, termEntries.getPreTotal());
	assertEquals(49, termEntries.getAdded());
	assertEquals(5, termEntries.getRemoved());
	assertEquals(45, termEntries.getPostTotal());

	Collection<TermCounts> terms = summary.getTerms();
	assertEquals(3, terms.size());

	TermCounts englishTermCounts = findByLanguageCode(terms, Locale.ENGLISH.getCode());
	assertEquals(1, englishTermCounts.getPreTotal());
	assertEquals(49, englishTermCounts.getAdded());
	// assertEquals(5, englishTermCounts.getUnchanged());
	assertEquals(5, englishTermCounts.getRemoved());
	// assertEquals(15, englishTermCounts.getUpdated());
	assertEquals(49, englishTermCounts.getPostTotal());

	TermCounts germanyTermCounts = findByLanguageCode(terms, Locale.GERMANY.getCode());
	assertEquals(1, germanyTermCounts.getPreTotal());
	assertEquals(49, germanyTermCounts.getAdded());
	// assertEquals(10, germanyTermCounts.getUnchanged());
	assertEquals(10, germanyTermCounts.getRemoved());
	// assertEquals(10, germanyTermCounts.getUpdated());
	assertEquals(49, germanyTermCounts.getPostTotal());

	TermCounts frenchTermCounts = findByLanguageCode(terms, Locale.FRANCE.getCode());
	assertEquals(1, frenchTermCounts.getPreTotal());
	assertEquals(49, frenchTermCounts.getAdded());
	// assertEquals(15, frenchTermCounts.getUnchanged());
	assertEquals(15, frenchTermCounts.getRemoved());
	// assertEquals(30, frenchTermCounts.getUpdated());
	assertEquals(39, frenchTermCounts.getPostTotal());
    }

    /*
     * TERII-5767 Import Summary | Source pre-import count is incorrect
     */
    @Test
    @TestCase("checkImportProgress")
    public void checkSummaryWhenWeAppendTermEntryWithSameSourceNameAsExistingTest() {
	CheckImportProgressCommand cmd = getModelObject("importProgressCommand", CheckImportProgressCommand.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class)))
		.thenReturn(getModelObject("batchImportInfos4", Map.class));
	when(getTerminologyCountsProvider().getProjectTerminologyCounts(any(Long.class), anyListOf(String.class)))
		.thenReturn(getModelObject("postImportCounts3", ProjectTerminologyCounts.class));

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));
	verify(getTerminologyCountsProvider()).getProjectTerminologyCounts(any(Long.class), anyListOf(String.class));

	assertEquals(100, (int) model.get("importPercentage"));

	BatchImportSummary summary = (BatchImportSummary) model.get("importSummary");

	assertFalse(summary.getImportTime().isEmpty());

	Counts termEntries = summary.getTermEntries();
	assertEquals(1, termEntries.getPreTotal());
	assertEquals(0, termEntries.getAdded());
	assertEquals(0, termEntries.getRemoved());
	assertEquals(1, termEntries.getPostTotal());
	assertEquals(1, termEntries.getUpdated());

	Collection<TermCounts> terms = summary.getTerms();
	assertEquals(2, terms.size());

	TermCounts englishTermCounts = findByLanguageCode(terms, Locale.ENGLISH.getCode());
	assertEquals(1, englishTermCounts.getPreTotal());
	assertEquals(0, englishTermCounts.getAdded());
	assertEquals(0, englishTermCounts.getRemoved());
	assertEquals(1, englishTermCounts.getPostTotal());
	assertEquals(0, englishTermCounts.getUpdated());

	TermCounts germanyTermCounts = findByLanguageCode(terms, Locale.GERMANY.getCode());
	assertEquals(0, germanyTermCounts.getPreTotal());
	assertEquals(1, germanyTermCounts.getAdded());
	assertEquals(0, germanyTermCounts.getRemoved());
	assertEquals(1, germanyTermCounts.getPostTotal());
	assertEquals(0, germanyTermCounts.getUpdated());
    }

    @SuppressWarnings("unchecked")
    @Test
    @TestCase("checkImportProgress")
    public void importProgressCanceledTest() {
	CheckImportProgressCommand cmd = getModelObject("importProgressCommand", CheckImportProgressCommand.class);

	Map<String, ImportProgressInfo> importInfos = getModelObject("batchImportInfos3", Map.class);

	when(getCacheGateway().getAll(any(CacheName.class), anySetOf(String.class))).thenReturn(importInfos);

	Map<String, Object> model = getTaskHandler().getTaskInfos(new Long[] { 1L }, TASK_NAME, cmd)[0].getModel();

	verify(getCacheGateway()).getAll(any(CacheName.class), anySetOf(String.class));

	assertEquals(30, (int) model.get("importPercentage"));
	assertEquals(true, model.get("importCanceled"));
    }

    @SuppressWarnings("unchecked")
    @After
    public void resetMock() {
	reset(getCacheGateway());
	reset(getTerminologyCountsProvider());
    }

    private TermCounts findByLanguageCode(Collection<TermCounts> terms, String code) {
	return terms.stream().filter(term -> code.equals(term.getLanguage().getLocale())).findFirst()
		.orElseThrow(NoSuchElementException::new);
    }

    private CacheGateway<String, ImportProgressInfo> getCacheGateway() {
	return _cacheGateway;
    }

    private CheckImportProgressTaskHandler getTaskHandler() {
	return _taskHandler;
    }

    private ProjectTerminologyCountsProvider getTerminologyCountsProvider() {
	return _terminologyCountsProvider;
    }
}
