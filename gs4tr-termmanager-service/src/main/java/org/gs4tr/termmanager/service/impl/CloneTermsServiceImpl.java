package org.gs4tr.termmanager.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.dao.ProjectDetailDAO;
import org.gs4tr.termmanager.dao.backup.DbTermEntryDAO;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.ProjectLanguage;
import org.gs4tr.termmanager.model.ProjectLanguageDetail;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.dto.ProjectDetailCountsIO;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTerm;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermDescription;
import org.gs4tr.termmanager.model.glossary.backup.regular.DbTermEntry;
import org.gs4tr.termmanager.model.reindex.BackupSearchCommand;
import org.gs4tr.termmanager.service.CloneTermsService;
import org.gs4tr.termmanager.service.ProjectLanguageDetailService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.ProjectUserLanguageService;
import org.gs4tr.termmanager.service.solr.restore.model.RecodeOrCloneCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("cloneTermsService")
public class CloneTermsServiceImpl implements CloneTermsService {

    @Value("${index.batchSize:500}")
    private int _batchSize;

    @Autowired
    private DbTermEntryDAO _dbTermEntryDAO;

    @Autowired
    private ProjectDetailDAO _projectDetailDAO;

    @Autowired
    private ProjectLanguageDetailService _projectLanguageDetailService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private ProjectUserLanguageService _projectUserLanguageService;

    @Override
    public void cloneProjectDetail(RecodeOrCloneCommand command) {
	/*
	 * Cloned projectLanguageDetail is added to DB and those projectLanguageDetail
	 * counts will be added to existing projectDetail counts for that project
	 */
	Long projectId = command.getProjectId();

	String localeTo = command.getLocaleTo();

	ProjectLanguageDetail clonedPld = getProjectLanguageDetailService().findProjectLangDetailByLangId(projectId,
		localeTo);

	ProjectDetailCountsIO projectDetailCountsIO = new ProjectDetailCountsIO(projectId);
	projectDetailCountsIO.setApprovedTermCount(clonedPld.getApprovedTermCount());
	projectDetailCountsIO.setOnHoldTermCount(clonedPld.getOnHoldTermCount());
	projectDetailCountsIO.setPendingTermCount(clonedPld.getPendingApprovalCount());
	projectDetailCountsIO.setForbiddenTermCount(clonedPld.getForbiddenTermCount());
	projectDetailCountsIO.setTotalCount(clonedPld.getTermCount());
	projectDetailCountsIO.setActiveSubmissionCount(clonedPld.getActiveSubmissionCount());
	projectDetailCountsIO.setCompletedSubmissionCount(clonedPld.getCompletedSubmissionCount());
	projectDetailCountsIO.setTermInSubmissionCount(clonedPld.getTermInSubmissionCount());

	getProjectDetailDAO().incrementalUpdateProjectDetail(projectDetailCountsIO);
    }

    @Override
    public void cloneProjectLanguage(RecodeOrCloneCommand command) {
	Long projectId = command.getProjectId();

	String localeTo = command.getLocaleTo();

	List<ProjectLanguage> projectLanguages = getProjectService().getProjectLanguagesForRecodeOrClone(projectId);

	ProjectLanguage projectLanguage = new ProjectLanguage();
	projectLanguage.setLanguage(localeTo);
	projectLanguages.add(projectLanguage);

	getProjectService().addOrUpdateProjectLanguagesForRecodeOrClone(projectId, projectLanguages);
    }

    @Override
    public void cloneProjectLanguageDetail(RecodeOrCloneCommand command, int dummyTermsCount) {
	Long projectId = command.getProjectId();

	String localeTo = command.getLocaleTo();

	String localeFrom = command.getLocaleFrom();

	ProjectLanguageDetail pldBefore = getProjectLanguageDetailService().findProjectLangDetailByLangId(projectId,
		localeFrom);

	getProjectLanguageDetailService().cloneProjectLangDetail(pldBefore, localeTo, projectId, dummyTermsCount);
    }

    /* This method also clones Statistics and ProjectLanguageUserDetail */
    @Override
    public void cloneProjectUserLanguage(RecodeOrCloneCommand command) {
	Long projectId = command.getProjectId();

	String localeTo = command.getLocaleTo();

	Set<TmUserProfile> projectUsers = getProjectService().getProjectUsersForRecodeOrClone(projectId);

	TmProject project = getProjectService().load(projectId);

	for (TmUserProfile user : projectUsers) {
	    getProjectUserLanguageService().cloneProjectUserLanguages(user, project, localeTo);
	}
    }

    @Override
    public int cloneTerms(RecodeOrCloneCommand command) {
	int dummyTermsCounts = 0;

	Long projectId = command.getProjectId();

	ItemStatusType defaultStatus = getDefaultProjectStatus(projectId);

	PagedListInfo info = new PagedListInfo();
	info.setSize(getBatchSize());

	BackupSearchCommand backupSearchCommand = new BackupSearchCommand(Collections.singletonList(projectId), null);

	PagedList<DbTermEntry> page = getDbTermEntryDAO().getDbTermEntries(info, backupSearchCommand);

	DbTermEntry[] entries = page.getElements();
	while (ArrayUtils.isNotEmpty(entries)) {

	    dummyTermsCounts = cloneTerms(entries, command, defaultStatus, dummyTermsCounts);

	    List<DbTermEntry> dbTermEntries = Arrays.asList(entries);

	    getDbTermEntryDAO().saveOrUpdateLocked(dbTermEntries);

	    info.setIndex(info.getIndex() + 1);
	    page = getDbTermEntryDAO().getDbTermEntries(info, backupSearchCommand);
	    entries = page.getElements();

	}

	return dummyTermsCounts;
    }

    private int cloneIfTermLanguageIsMatched(Set<DbTerm> terms, Set<DbTerm> clonedTerms, String localeFrom,
	    String localeTo, ItemStatusType defaultStatus, int dummyTermsCount) {

	for (DbTerm term : terms) {
	    if (term.getLanguageId().equals(localeFrom)) {

		if (isClonedTermDummy(term)) {
		    dummyTermsCount++;
		    break;
		}

		DbTerm clonedTerm = getClonedDbTerm(term, localeTo, defaultStatus);
		clonedTerms.add(clonedTerm);
	    }
	}
	return dummyTermsCount;
    }

    // Multiple tables are updated during this action
    private int cloneTerms(DbTermEntry[] termEntries, RecodeOrCloneCommand command, ItemStatusType defaultStatus,
	    int dummyTermsCounts) {

	String localeFrom = command.getLocaleFrom();
	String localeTo = command.getLocaleTo();

	Set<DbTerm> clonedTerms = new HashSet<>();

	for (DbTermEntry termEntry : termEntries) {
	    Set<DbTerm> terms = termEntry.getTerms();

	    dummyTermsCounts = cloneIfTermLanguageIsMatched(terms, clonedTerms, localeFrom, localeTo, defaultStatus,
		    dummyTermsCounts);

	    terms.addAll(clonedTerms);

	    if (!clonedTerms.isEmpty()) {
		clonedTerms.clear();
		termEntry.setAction(Action.CLONED.name());
	    }

	}
	return dummyTermsCounts;
    }

    private int getBatchSize() {
	return _batchSize;
    }

    private DbTerm getClonedDbTerm(DbTerm termForCloning, String languageTo, ItemStatusType defaultStatus) {
	DbTerm clonedTerm = new DbTerm();

	clonedTerm.setDateCreated(termForCloning.getDateCreated());
	clonedTerm.setDateModified(termForCloning.getDateModified());
	clonedTerm.setFirst(termForCloning.getFirst());
	clonedTerm.setForbidden(termForCloning.getForbidden());
	clonedTerm.setInTranslationAsSource(Boolean.FALSE);

	// Cloned Term should not be in translation
	if (isClonedTermInTranslation(termForCloning)) {
	    clonedTerm.setStatusOld(termForCloning.getStatus());
	    clonedTerm.setStatus(defaultStatus.getName());
	} else {
	    clonedTerm.setStatus(termForCloning.getStatus());
	}

	clonedTerm.setLanguageId(languageTo);
	clonedTerm.setName(termForCloning.getName());
	clonedTerm.setTermEntryUuid(termForCloning.getTermEntryUuid());
	clonedTerm.setUserCreated(termForCloning.getUserCreated());
	clonedTerm.setUserModified(termForCloning.getUserModified());
	String uuIdForClone = UUID.randomUUID().toString();
	clonedTerm.setUuId(uuIdForClone);
	clonedTerm.setDescriptions(getClonedDescriptions(termForCloning.getDescriptions(), uuIdForClone));
	clonedTerm.setProjectId(termForCloning.getProjectId());
	clonedTerm.setDisabled(termForCloning.getDisabled());
	clonedTerm.setUserLatestChange(termForCloning.getUserLatestChange());

	return clonedTerm;
    }

    private Set<DbTermDescription> getClonedDescriptions(Set<DbTermDescription> termDescriptionsForCloning,
	    String clonedTermUuId) {

	Set<DbTermDescription> clonedTermDescriptions = new HashSet<>();

	for (DbTermDescription description : termDescriptionsForCloning) {
	    DbTermDescription clonedDescription = new DbTermDescription();

	    clonedDescription.setBaseType(description.getBaseType());
	    clonedDescription.setType(description.getType());
	    clonedDescription.setValue(description.getValue());
	    clonedDescription.setUuid(UUID.randomUUID().toString());
	    clonedDescription.setTermUuid(clonedTermUuId);

	    clonedTermDescriptions.add(clonedDescription);
	}

	return clonedTermDescriptions;

    }

    private DbTermEntryDAO getDbTermEntryDAO() {
	return _dbTermEntryDAO;
    }

    private ItemStatusType getDefaultProjectStatus(Long projectId) {
	TmProject project = _projectService.load(projectId);
	return project.getDefaultTermStatus();
    }

    private ProjectDetailDAO getProjectDetailDAO() {
	return _projectDetailDAO;
    }

    private ProjectLanguageDetailService getProjectLanguageDetailService() {
	return _projectLanguageDetailService;
    }

    private ProjectService getProjectService() {
	return _projectService;
    }

    private ProjectUserLanguageService getProjectUserLanguageService() {
	return _projectUserLanguageService;
    }

    private boolean isClonedTermDummy(DbTerm term) {
	byte[] termName = term.getName();

	if (Objects.isNull(termName)) {
	    return true;
	}

	return termName.length == 0 && isClonedTermInTranslationReview(term.getStatus());
    }

    private boolean isClonedTermInTranslation(DbTerm term) {
	String termStatus = term.getStatus();
	boolean isInFinalReview = ItemStatusTypeHolder.IN_FINAL_REVIEW.getName().equals(termStatus);
	boolean isInTranslationReview = isClonedTermInTranslationReview(termStatus);
	boolean isInTranslationAsSource = term.getInTranslationAsSource();

	return isInTranslationAsSource || isInFinalReview || isInTranslationReview;
    }

    private boolean isClonedTermInTranslationReview(String termStatus) {
	return ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName().equals(termStatus);
    }
}
