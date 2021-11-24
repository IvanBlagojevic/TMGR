package org.gs4tr.termmanager.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gs4tr.foundation.locale.Locale;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectDetail;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.ProjectLanguageDetailInfoIO;
import org.gs4tr.termmanager.model.ProjectUserLanguage;
import org.gs4tr.termmanager.model.Statistics;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntryHistory;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
@Ignore
public class CloneTermsServiceTest extends AbstractRecodeOrCloneTermsTest {

    /*
     * TERII-6034 Project Dashboard | Bad term count after cloning term which is in
     * submission
     */
    @Test
    public void cloneDummyTermsTest() {
	String localeFrom = Locale.GERMAN.getCode();
	String localeTo = Locale.GERMANY.getCode();
	/*
	 * Create DbTermEntry with dummyTerm(target term with empty name on localeFrom
	 * and in TranslationReview status)
	 */
	DbTermEntry dbTermEntry = crateDbTermEntry();
	getDbTermEntryDAO().saveOrUpdateLocked(Collections.singletonList(dbTermEntry));

	ProjectLanguageDetailInfoIO projectLanguageDetailInfoIO = createProjectLanguageDetailInfoIO(localeFrom);

	// Update Project Language Detail counts for languageFrom
	getProjectLanguageDetailDAO().incrementalUpdateProjectLanguageDetail(projectLanguageDetailInfoIO);

	// Prepare values before cloning for assert
	DbTermEntry[] termEntriesBefore = getTermEntriesByProjectId(PROJECT_ID1);
	Assert.assertEquals(termEntriesBefore.length, 3);

	DbTermEntry termEntryWithDummyTermBefore = termEntriesBefore[2];

	Set<DbTerm> termsBefore = termEntryWithDummyTermBefore.getTerms();
	Assert.assertEquals(termsBefore.size(), 2);

	ProjectLanguageDetail pldBefore = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeFrom);
	Assert.assertNotNull(pldBefore);

	RecodeOrCloneCommand command = createRecodeOrCloneCommand(PROJECT_ID1, localeFrom, localeTo);

	// Perform Clone operation
	int dummyTermsCounts = getCloneTermsService().cloneTerms(command);
	getCloneTermsService().cloneProjectLanguageDetail(command, dummyTermsCounts);

	DbTermEntry[] termEntriesAfter = getTermEntriesByProjectId(PROJECT_ID1);

	DbTermEntry termEntryWithDummyTermAfter = termEntriesAfter[2];

	// Created DbTermEntry has equal terms as before clone operation
	Set<DbTerm> termsAfter = termEntryWithDummyTermAfter.getTerms();
	Assert.assertEquals(termsBefore.size(), termsAfter.size());
	Assert.assertTrue(termsBefore.containsAll(termsAfter));

	ProjectLanguageDetail pldAfter = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeTo);
	Assert.assertNotNull(pldAfter);
	assertProjectLangDetail(pldBefore, pldAfter, dummyTermsCounts);
    }

    @Test
    public void cloneProjectDetailTest() {
	String localeFrom = Locale.GERMAN.getCode();
	String localeTo = Locale.GERMANY.getCode();

	// Prepare values before cloning for assert
	ProjectDetail pdBefore = getProjectDetailService().findByProjectId(PROJECT_ID1, ProjectLanguageDetail.class);

	ProjectLanguageDetail pldBefore = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeFrom);

	// Perform ProjectLanguageDetail Clone operation
	getProjectLanguageDetailService().cloneProjectLangDetail(pldBefore, localeTo, PROJECT_ID1, 0);

	ProjectLanguageDetail pldAfter = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeTo);
	// Assert cloned pld counts which will be added to existing projectDetail counts
	Assert.assertNotNull(pldAfter);
	assertProjectLangDetail(pldBefore, pldAfter, 0);

	RecodeOrCloneCommand command = createRecodeOrCloneCommand(PROJECT_ID1, localeFrom, localeTo);

	// Perform ProjectDetail Clone operation
	getCloneTermsService().cloneProjectDetail(command);

	// Assert db state after cloning
	ProjectDetail pdAfter = getProjectDetailService().findByProjectId(PROJECT_ID1, ProjectLanguageDetail.class);
	Assert.assertNotNull(pdAfter);
	assertProjectDetail(pdBefore, pdAfter, pldAfter);
    }

    @Test
    public void cloneProjectLanguageDetailTest() {
	String localeFrom = Locale.GERMAN.getCode();
	String localeTo = Locale.GERMANY.getCode();

	ProjectDetail pdBefore = getProjectDetailService().findByProjectId(PROJECT_ID1, ProjectLanguageDetail.class);
	Set<ProjectLanguageDetail> languageDetailsBefore = pdBefore.getLanguageDetails();

	ProjectLanguageDetail pldBefore = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeFrom);
	// Assert db state before Cloning
	Assert.assertEquals(pldBefore.getApprovedTermCount(), 1L);
	Assert.assertEquals(pldBefore.getOnHoldTermCount(), 0L);
	Assert.assertEquals(pldBefore.getPendingApprovalCount(), 0L);
	Assert.assertEquals(pldBefore.getTermCount(), 3L);
	Assert.assertEquals(pldBefore.getTermEntryCount(), 0L);
	Assert.assertEquals(pldBefore.getForbiddenTermCount(), 0L);
	Assert.assertEquals(pldBefore.getCompletedSubmissionCount(), 0);
	Assert.assertEquals(pldBefore.getTermInSubmissionCount(), 2L);
	Assert.assertEquals(pldBefore.getActiveSubmissionCount(), 1L);
	Assert.assertFalse(pldBefore.isDisabled());

	RecodeOrCloneCommand command = createRecodeOrCloneCommand(PROJECT_ID1, localeFrom, localeTo);

	// Perform ProjectLanguageDetail Clone operation
	getCloneTermsService().cloneProjectLanguageDetail(command, 0);

	ProjectDetail pdAfter = getProjectDetailService().findByProjectId(PROJECT_ID1, ProjectLanguageDetail.class);
	Set<ProjectLanguageDetail> languageDetailsAfter = pdAfter.getLanguageDetails();
	// List of ProjectLanguageDetail has been increased
	Assert.assertEquals(languageDetailsAfter.size(), languageDetailsBefore.size() + 1);

	ProjectLanguageDetail pldAfter = getProjectLanguageDetailService().findProjectLangDetailByLangId(PROJECT_ID1,
		localeTo);
	// Assert db state after cloning
	Assert.assertNotNull(pldAfter);
	assertProjectLangDetail(pldBefore, pldAfter, 0);
    }

    @Test
    public void cloneProjectLanguageTest() {
	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguages(PROJECT_ID1);
	ProjectLanguage localeToBefore = projectLanguages.stream().filter(pl -> pl.getLanguage().equals(EN_US))
		.findAny().orElse(null);
	// No projectLanguage on localeTo before clone operation
	Assert.assertNull(localeToBefore);

	List<String> languageCodesBefore = getProjectService().getProjectLanguageCodes(PROJECT_ID1);
	Assert.assertEquals(projectLanguages.size(), languageCodesBefore.size());

	RecodeOrCloneCommand command = getRecodeOrCloneCommand();

	// Perform Clone operation
	getCloneTermsService().cloneProjectLanguage(command);

	List<ProjectLanguage> clonedProjectLanguages = getProjectService().getProjectLanguages(PROJECT_ID1);
	// List of project languages has been increased
	Assert.assertEquals(clonedProjectLanguages.size(), projectLanguages.size() + 1);

	List<String> languageCodesAfter = getProjectService().getProjectLanguageCodes(PROJECT_ID1);
	Assert.assertEquals(languageCodesAfter.size(), projectLanguages.size() + 1);
	// localeTo is added to project
	Assert.assertTrue(languageCodesAfter.contains(EN_US));
	Assert.assertTrue(languageCodesAfter.containsAll(languageCodesBefore));
    }

    @Test
    public void cloneProjectUserLanguageAndStatisticsTest() {
	String localeTo = getRecodeOrCloneCommand().getLocaleTo();

	// Prepare values before cloning for assert
	Set<TmUserProfile> projectUsers = getProjectService().getProjectUsers(PROJECT_ID1);
	Assert.assertEquals(2, projectUsers.size());

	List<ProjectUserLanguage> pulBefore = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID1);
	Assert.assertEquals(pulBefore.size(), 6);

	// No projectUserLanguage on localeTo before clone operation
	ProjectUserLanguage localeToBefore = pulBefore.stream().filter(pul -> pul.getLanguage().equals(localeTo))
		.findAny().orElse(null);
	Assert.assertNull(localeToBefore);

	List<Statistics> statisticsBefore = getStatisticsService().findStatisticsByProjectId(PROJECT_ID1);
	Assert.assertEquals(12, statisticsBefore.size());

	RecodeOrCloneCommand command = getRecodeOrCloneCommand();

	// Perform Clone operation
	getCloneTermsService().cloneProjectUserLanguage(command);

	List<ProjectUserLanguage> pulAfter = getProjectService().findProjectUserLanguageByProjectId(PROJECT_ID1);
	// List of projectUserLanguages has been increased for number of users
	Assert.assertEquals(pulAfter.size(), pulBefore.size() + projectUsers.size());
	// ProjectUserLanguages that are not on languageTo are same as before cloning
	Assert.assertTrue(pulAfter.containsAll(pulBefore));

	List<ProjectUserLanguage> clonedPul = pulAfter.stream().filter(itPul -> itPul.getLanguage().equals(localeTo))
		.collect(Collectors.toList());
	// Number of projectUserLanguages on localeTo is equals to number of users
	Assert.assertEquals(projectUsers.size(), clonedPul.size());

	// Daily and weekly statistics are added to new projectUserLanguages
	List<Statistics> statisticsAfter = getStatisticsService().findStatisticsByProjectId(PROJECT_ID1);
	Assert.assertEquals(statisticsAfter.size(), statisticsBefore.size() + 4);
    }

    @Test
    public void cloneTermTest() {

	RecodeOrCloneCommand command = getRecodeOrCloneCommand();

	int numOfHistoriesForEnLocale = 18;

	// Prepare values before cloning for assert
	DbTermEntry[] termEntriesBefore = getTermEntriesByProjectId(PROJECT_ID1);

	int numOfTermEntries = termEntriesBefore.length;

	DbTermEntry termEntryBefore1 = termEntriesBefore[0];
	DbTermEntry termEntryBefore2 = termEntriesBefore[1];

	// Check last action
	Assert.assertEquals(Action.EDITED.name(), termEntryBefore1.getAction());
	Assert.assertEquals(Action.EDITED.name(), termEntryBefore2.getAction());

	Set<DbTerm> termsBefore1 = termEntryBefore1.getTerms();
	Set<DbTerm> termsBefore2 = termEntryBefore2.getTerms();

	Set<DbTerm> enTermsBefore1 = getTermsByLanguage(termsBefore1, EN);
	Set<DbTerm> enTermsBefore2 = getTermsByLanguage(termsBefore2, EN);

	Set<DbTerm> enUsTermsBefore1 = getTermsByLanguage(termsBefore1, EN_US);
	Set<DbTerm> enUsTermsBefore2 = getTermsByLanguage(termsBefore2, EN_US);

	Set<DbTermEntryHistory> termEntryHistoriesBefore1 = termEntryBefore1.getHistory();
	Set<DbTermEntryHistory> termEntryHistoriesBefore2 = termEntryBefore2.getHistory();

	int numOfEntryHistoriesBeforeCloning1 = termEntryHistoriesBefore1.size();
	int numOfEntryHistoriesBeforeCloning2 = termEntryHistoriesBefore2.size();

	List<DbTerm> enHistoryTermsBefore = getAllHistoryDbTermsByLanguage(termEntriesBefore, EN);
	List<DbTerm> enUsHistoryTermsBefore = getAllHistoryDbTermsByLanguage(termEntriesBefore, EN_US);

	// Assert db state before Cloning
	Assert.assertEquals(2, numOfTermEntries);
	Assert.assertEquals(7, termsBefore1.size());
	Assert.assertEquals(5, termsBefore2.size());
	Assert.assertEquals(numOfHistoriesForEnLocale, enHistoryTermsBefore.size());
	Assert.assertEquals(0, enUsHistoryTermsBefore.size());
	Assert.assertEquals(2, enTermsBefore1.size());
	Assert.assertEquals(2, enTermsBefore2.size());
	Assert.assertEquals(0, enUsTermsBefore1.size());
	Assert.assertEquals(0, enUsTermsBefore2.size());

	// Perform Clone operation
	getCloneTermsService().cloneTerms(command);

	// Prepare values after cloning for assert
	DbTermEntry[] termEntriesAfter = getTermEntriesByProjectId(PROJECT_ID1);

	DbTermEntry termEntryAfter1 = termEntriesAfter[0];
	DbTermEntry termEntryAfter2 = termEntriesAfter[1];

	// Last entry action should be cloned
	Assert.assertEquals(Action.CLONED.name(), termEntryAfter1.getAction());
	Assert.assertEquals(Action.CLONED.name(), termEntryAfter2.getAction());

	Set<DbTerm> termsAfter1 = termEntryAfter1.getTerms();
	Set<DbTerm> termsAfter2 = termEntryAfter2.getTerms();

	Set<DbTermEntryHistory> termEntryHistoriesAfter1 = termEntryAfter1.getHistory();
	Set<DbTermEntryHistory> termEntryHistoriesAfter2 = termEntryAfter2.getHistory();

	int numOfEntryHistoriesAfterCloning1 = termEntryHistoriesAfter1.size();
	int numOfEntryHistoriesAfterCloning2 = termEntryHistoriesAfter2.size();

	// New term entry histories are added to both term entries
	Assert.assertEquals(numOfEntryHistoriesBeforeCloning1 + 1, numOfEntryHistoriesAfterCloning1);
	Assert.assertEquals(numOfEntryHistoriesBeforeCloning2 + 1, numOfEntryHistoriesAfterCloning2);

	List<DbTerm> enHistoryTermsAfter = getAllHistoryDbTermsByLanguage(termEntriesAfter, EN);
	Assert.assertEquals(numOfHistoriesForEnLocale, enHistoryTermsAfter.size());

	Set<DbTerm> enTermsAfter1 = getTermsByLanguage(termsAfter1, EN);
	Set<DbTerm> enTermsAfter2 = getTermsByLanguage(termsAfter2, EN);

	Set<DbTerm> enUsTermsAfter1 = getTermsByLanguage(termsAfter1, EN_US);
	Set<DbTerm> enUsTermsAfter2 = getTermsByLanguage(termsAfter2, EN_US);

	// Assert db state after cloning
	Assert.assertEquals(numOfTermEntries, termEntriesAfter.length);
	Assert.assertEquals(9, termsAfter1.size());
	Assert.assertEquals(7, termsAfter2.size());

	// Should be unchanged
	Assert.assertEquals(numOfHistoriesForEnLocale, enHistoryTermsAfter.size());

	// Should be unchanged
	Assert.assertEquals(2, enTermsAfter1.size());
	Assert.assertEquals(2, enTermsAfter2.size());

	// Same number as for the en language
	Assert.assertEquals(2, enUsTermsAfter1.size());
	Assert.assertEquals(2, enUsTermsAfter2.size());

	// Assert if en term are unchanged
	for (DbTerm termBefore : enTermsBefore1) {
	    DbTerm enTermAfter = enTermsAfter1.stream().filter(term -> term.equals(termBefore)).findAny().orElse(null);
	    Assert.assertNotNull(enTermAfter);
	}

	// Assert if en terms are cloned to en-US terms
	for (DbTerm enTerm : enTermsBefore1) {
	    DbTerm enUsTerm = enUsTermsAfter1.stream()
		    .filter(term -> term.getNameAsString().equals(enTerm.getNameAsString())).findAny().orElse(null);
	    Assert.assertNotNull(enUsTerm);

	    Assert.assertNotEquals(enTerm.getUuId(), enUsTerm.getUuId());
	    Assert.assertEquals(enTerm.getTermEntryUuid(), enUsTerm.getTermEntryUuid());
	    Assert.assertEquals(enTerm.getForbidden(), enUsTerm.getForbidden());
	    Assert.assertEquals(enTerm.getProjectId(), enUsTerm.getProjectId());
	    Assert.assertEquals(enTerm.getDisabled(), enUsTerm.getDisabled());
	    Assert.assertEquals(enTerm.getDateCreated(), enUsTerm.getDateCreated());
	    Assert.assertEquals(enTerm.getFirst(), enUsTerm.getFirst());
	    Assert.assertEquals(EN_US, enUsTerm.getLanguageId());
	    Assert.assertEquals(enTerm.getTermEntryUuid(), enUsTerm.getTermEntryUuid());
	    Assert.assertEquals(enTerm.getUserCreated(), enUsTerm.getUserCreated());
	    Assert.assertEquals(enTerm.getUserModified(), enUsTerm.getUserModified());
	    Assert.assertEquals(enTerm.getDescriptions(), enUsTerm.getDescriptions());
	    Assert.assertEquals(enTerm.getUserLatestChange(), enUsTerm.getUserLatestChange());
	    Assert.assertEquals(enTerm.getDateModified(), enUsTerm.getDateModified());

	    // Cloned term should not be in translation
	    Assert.assertEquals(Boolean.FALSE, enUsTerm.getInTranslationAsSource());

	    /*
	     * en-US was in translation as source. These terms should have default project
	     * status after cloning
	     */
	    if (new String(enUsTerm.getName()).equals("TermEn1")) {
		Assert.assertEquals(ItemStatusTypeHolder.PROCESSED.getName(), enUsTerm.getStatus());
	    } else {
		Assert.assertEquals(enTerm.getStatus(), enUsTerm.getStatus());
	    }

	    assertTermDescriptions(enTerm, enUsTerm);
	}
    }

    private void assertProjectDetail(ProjectDetail pdBefore, ProjectDetail pdAfter, ProjectLanguageDetail pldAfter) {
	long termCount = pdBefore.getTermCount();
	long approvedTermCount = pdBefore.getApprovedTermCount();
	long onHoldTermCount = pdBefore.getOnHoldTermCount();
	Date dateModified = pdBefore.getDateModified();
	long termEntryCount = pdBefore.getTermEntryCount();
	long pendingCount = pdBefore.getPendingApprovalCount();
	long activeSubmission = pdBefore.getActiveSubmissionCount();
	long termInSubmission = pdBefore.getTermInSubmissionCount();
	long completedSub = pdBefore.getCompletedSubmissionCount();
	long forbiddenTermCount = pdBefore.getForbiddenTermCount();

	Assert.assertEquals(pdAfter.getApprovedTermCount(), pldAfter.getApprovedTermCount() + approvedTermCount);
	Assert.assertEquals(pdAfter.getOnHoldTermCount(), pldAfter.getOnHoldTermCount() + onHoldTermCount);
	Assert.assertEquals(pdAfter.getPendingApprovalCount(), pldAfter.getPendingApprovalCount() + pendingCount);
	Assert.assertEquals(pdAfter.getActiveSubmissionCount(), pldAfter.getActiveSubmissionCount() + activeSubmission);
	Assert.assertEquals(pdAfter.getTermInSubmissionCount(), pldAfter.getTermInSubmissionCount() + termInSubmission);
	Assert.assertEquals(pdAfter.getCompletedSubmissionCount(),
		pldAfter.getCompletedSubmissionCount() + completedSub);
	Assert.assertEquals(pdAfter.getForbiddenTermCount(), forbiddenTermCount + pldAfter.getForbiddenTermCount());
	Assert.assertEquals(pdAfter.getTermEntryCount(), termEntryCount);
	Assert.assertEquals(pdAfter.getTermCount(), termCount + pldAfter.getTermCount());
	Assert.assertEquals(dateModified, pdAfter.getDateModified());

    }

    private void assertProjectLangDetail(ProjectLanguageDetail pldBefore, ProjectLanguageDetail pldAfter,
	    int dummyTermsCount) {
	long approvedTermCount = pldBefore.getApprovedTermCount();
	long onHoldTermCount = pldBefore.getOnHoldTermCount();
	long pendingApprovalCount = pldBefore.getPendingApprovalCount();
	Date dateModified = pldBefore.getDateModified();
	long termCount = pldBefore.getTermCount();
	long termEntryCount = pldBefore.getTermEntryCount();
	long termInSubmission = pldBefore.getTermInSubmissionCount();
	long forbiddenTermCount = pldBefore.getForbiddenTermCount();

	Assert.assertEquals(pldAfter.getApprovedTermCount(), approvedTermCount + (termInSubmission - dummyTermsCount));
	Assert.assertEquals(pldAfter.getOnHoldTermCount(), onHoldTermCount);
	Assert.assertEquals(pldAfter.getPendingApprovalCount(), pendingApprovalCount);
	Assert.assertEquals(pldAfter.getDateModified(), dateModified);
	Assert.assertEquals(pldAfter.getTermCount(), termCount - dummyTermsCount);
	Assert.assertEquals(pldAfter.getTermEntryCount(), termEntryCount);
	Assert.assertEquals(pldAfter.getForbiddenTermCount(), forbiddenTermCount);
	Assert.assertEquals(pldAfter.getCompletedSubmissionCount(), 0);
	Assert.assertEquals(pldAfter.getTermInSubmissionCount(), 0);
	Assert.assertEquals(pldAfter.getActiveSubmissionCount(), 0);
	Assert.assertFalse(pldAfter.isDisabled());
    }

    private void assertTermDescriptions(DbTerm originalTerm, DbTerm clonedTerm) {
	Set<DbTermDescription> originalDescriptions = originalTerm.getDescriptions();
	Set<DbTermDescription> clonedDescriptions = clonedTerm.getDescriptions();

	for (DbTermDescription originalDescription : originalDescriptions) {
	    DbTermDescription clonedDescription = clonedDescriptions.stream()
		    .filter(clone -> clone.equals(originalDescription)).findFirst().orElse(null);
	    Assert.assertNotNull(clonedDescription);

	    Assert.assertNotEquals(originalDescription.getTermUuid(), clonedDescription.getTermUuid());
	    Assert.assertNotEquals(originalDescription.getUuid(), clonedDescription.getUuid());
	}

    }

    private DbTermEntry crateDbTermEntry() {
	DbTermEntry dbTermEntry = new DbTermEntry();

	dbTermEntry.setProjectId(PROJECT_ID1);
	dbTermEntry.setProjectName(PROJECT_NAME);
	dbTermEntry.setShortCode(SHORT_CODE_PROJECT1);
	dbTermEntry.setUserCreated(USER);
	dbTermEntry.setUserModified(USER);
	dbTermEntry.setDateCreated(new Date());
	dbTermEntry.setDateModified(new Date());
	dbTermEntry.setRevisionId(1);
	dbTermEntry.setUuId(TERM_ENTRY_ID);

	Set<DbTerm> dbTerms = new HashSet<>();
	dbTerms.add(createDbTerm(EN, "sourceName", ItemStatusTypeHolder.PROCESSED.getName(), TERM_ID));
	dbTerms.add(createDbTerm(DE, "", ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName(), DUMMY_TERM_ID));

	dbTermEntry.setTerms(dbTerms);

	return dbTermEntry;
    }

    private DbTerm createDbTerm(String languageId, String name, String status, String termUuid) {
	DbTerm dbTerm = new DbTerm();

	dbTerm.setUuId(termUuid);
	dbTerm.setLanguageId(languageId);
	dbTerm.setName(name.getBytes());
	dbTerm.setForbidden(false);
	dbTerm.setStatus(status);
	dbTerm.setUserCreated(USER);
	dbTerm.setUserModified(USER);
	dbTerm.setDateModified(new Date());
	dbTerm.setDateCreated(new Date());
	dbTerm.setProjectId(PROJECT_ID1);
	dbTerm.setFirst(Boolean.TRUE);
	dbTerm.setDisabled(Boolean.FALSE);
	dbTerm.setTermEntryUuid(TERM_ENTRY_ID);

	return dbTerm;
    }

    private ProjectLanguageDetailInfoIO createProjectLanguageDetailInfoIO(String localeFrom) {
	ProjectLanguageDetailInfoIO projectLanguageDetailInfoIO = new ProjectLanguageDetailInfoIO(localeFrom);

	projectLanguageDetailInfoIO.setProjectId(PROJECT_ID1);
	projectLanguageDetailInfoIO.setActiveSubmissionCount(1L);
	projectLanguageDetailInfoIO.setTermCount(1L);
	projectLanguageDetailInfoIO.setTermInSubmissionCount(1L);
	projectLanguageDetailInfoIO.setApprovedTermCount(0);
	projectLanguageDetailInfoIO.setOnHoldTermCount(0);
	projectLanguageDetailInfoIO.setPendingTermCount(0);
	projectLanguageDetailInfoIO.setCompletedSubmissionCount(0);
	projectLanguageDetailInfoIO.setDateModified(new Date());
	projectLanguageDetailInfoIO.setTermEntryCount(0);
	projectLanguageDetailInfoIO.setForbiddenTermCount(0);

	return projectLanguageDetailInfoIO;
    }

    private Set<DbTerm> getTermsByLanguage(Set<DbTerm> terms, String language) {
	return terms.stream().filter(term -> term.getLanguageId().equals(language)).collect(Collectors.toSet());
    }

}
