package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.PriorityEnum;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.DbSubmissionTermEntryService;
import org.gs4tr.termmanager.service.SubmissionDetailService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.undoterm.UndoTermImpl;
import org.gs4tr.termmanager.service.utils.StatisticsUtils;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("submissionTermService")
public class SubmissionTermServiceImpl extends AbstractNotifyingService implements SubmissionTermService {

    @Autowired
    private DbSubmissionTermEntryService _dbSubmissionService;

    @Autowired
    private SubmissionDetailService _submissionDetailService;

    @Autowired
    private SubmissionService _submissionService;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UndoTermImpl _undoTerm;

    @Override
    public void addNewDescription(String type, String tempText, String baseType, String submissionTermId,
	    Long projectId) {

	TermEntry termEntry = findSubTermEntryByTermId(submissionTermId, projectId);
	Term subTerm = termEntry.ggetTermById(submissionTermId);

	validateTermStatus(subTerm);

	Description description = new Description();
	description.setUuid(UUID.randomUUID().toString());
	description.setType(type);
	description.setTempValue(tempText);
	description.setValue(tempText);
	description.setBaseType(baseType);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.7"), //$NON-NLS-1$
		    tempText));
	}

	subTerm.setDateModified(new Date().getTime());
	subTerm.addDescription(description);
	subTerm.setCommited(Boolean.FALSE);

	updateSubmissionTermEntries(projectId, Action.UPDATED_TRANSLATIONS.name(), Arrays.asList(termEntry));
    }

    @Override
    public List<String> approveSubmissionTerms(List<String> submissionTermIds, Long submissionId) {

	if (CollectionUtils.isEmpty(submissionTermIds)) {
	    return null;
	}

	Submission submission = getSubmissionService().load(submissionId);

	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	TmProject project = submission.getProject();

	Long projectId = project.getProjectId();

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(projectId);

	String sourceLanguageId = submission.getSourceLanguageId();

	List<TermEntry> submissionEntryes = findSubTermEntriesByTermIds(submissionTermIds, projectId);

	List<String> termIds = new ArrayList<String>();

	approveSubmissionTerms(submissionTermIds, submissionDetailInfo, sourceLanguageId, submissionEntryes, termIds);

	if (termIds.isEmpty()) {
	    throw new UserException(MessageResolver.getMessage("SubmissionTermServiceImpl.14"), //$NON-NLS-1$
		    MessageResolver.getMessage("SubmissionTermServiceImpl.17")); //$NON-NLS-1$

	}

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.13"), //$NON-NLS-1$
		    TmUserProfile.getCurrentUserName()));
	}

	List<String> approvedTermIds = new ArrayList<String>();

	List<TermEntry> termEntries = findTermEntriesByTermIds(getRegularCollection(), termIds, projectId);

	Set<StatisticsInfo> statisticsInfos = new HashSet<>();

	approveRegularTerms(termEntries, approvedTermIds, sourceLanguageId, termIds, projectDetailInfo,
		statisticsInfos);

	if (CollectionUtils.isNotEmpty(approvedTermIds)) {
	    getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);

	    submission = getSubmissionService().findByIdFetchChilds(submissionId);

	    updateCompletedSubmissionDetails(projectDetailInfo, submission);
	}

	updateSubmissionTermEntries(projectId, Action.APPROVED_TRANSLATIONS.name(), submissionEntryes);
	updateRegularTermEntries(projectId, Action.APPROVED_TRANSLATIONS.name(),
		new TransactionalUnit(termEntries, projectDetailInfo, statisticsInfos));

	return approvedTermIds;
    }

    @Override
    public Set<String> cancelTermTranslation(Long submissionId, List<String> submissionTermIds) {
	Submission submission = getSubmissionService().load(submissionId);

	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	TmProject project = submission.getProject();
	Long projectId = project.getProjectId();
	checkLock(projectId);

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(projectId);

	Set<String> termEntryIds = new HashSet<>();
	Set<String> submisionTermEntryIds = new HashSet<>();
	Set<String> regularTermEntriesIds = new HashSet<>();
	if (CollectionUtils.isNotEmpty(submissionTermIds)) {
	    findSubmissionAndRegularTermEntriesByTermIds(submissionTermIds, getSubmissionCollection(),
		    project.getProjectId(), regularTermEntriesIds, submisionTermEntryIds);

	    Map<String, List<TermEntry>> regularHistory = findRegularHistory(regularTermEntriesIds);

	    Map<String, List<TermEntry>> submissionHistory = findSubmissionHistory(submisionTermEntryIds);

	    List<TermEntry> subTermEntries = findTermEntriesByTermIds(getSubmissionCollection(), submissionTermIds,
		    projectId);

	    Set<String> regularTermIds = new HashSet<>();

	    cancelSubmissionTermTranslation(projectDetailInfo, submissionTermIds, submissionDetailInfo, subTermEntries,
		    termEntryIds, regularTermIds, submissionHistory);
	    cancelRegularTermTranslation(termEntryIds, regularTermIds, submissionDetailInfo.getSourceLanguageId(),
		    projectId, regularHistory);

	    if (CollectionUtils.isNotEmpty(termEntryIds)) {
		getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);
		submission = getSubmissionService().findByIdFetchChilds(submissionId);
		updateCompletedSubmissionDetails(projectDetailInfo, submission);
		projectDetailInfo.setDateModified(new Date());
		getProjectDetailService().incrementUpdateProjectDetail(projectDetailInfo);
	    }
	}
	return termEntryIds;
    }

    @Override
    public Term findById(String submissionTermId, Long projectId) {
	if (StringUtils.isBlank(submissionTermId)) {
	    return null;
	}
	try {
	    return getSubmissionBrowser().findTermById(submissionTermId, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public List<Term> findByIds(List<String> submissionTermIds, Long projectId) {
	if (CollectionUtils.isEmpty(submissionTermIds)) {
	    return null;
	}

	try {
	    return getSubmissionBrowser().findTermsByIds(submissionTermIds, Arrays.asList(projectId));
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public List<Term> findSubmissionTermsBySubmissionId(Long submissionId) {
	List<Term> terms = new ArrayList<Term>();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(submissionId);

	List<TermEntry> termEntries;
	try {
	    termEntries = getSubmissionBrowser().browse(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}

	if (CollectionUtils.isNotEmpty(termEntries)) {
	    for (TermEntry termEntry : termEntries) {
		terms.addAll(termEntry.ggetTerms());
	    }
	}

	return terms;
    }

    @Override
    public TermEntry findTermEntryByTermId(String termId, Long projectId) {
	try {
	    return getBrowser(getSubmissionCollection()).findByTermId(termId, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    @Override
    public Map<String, String> undoTermTranslation(List<String> submissionTermIds, Long projectId) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.8"), //$NON-NLS-1$
		    submissionTermIds.size()));
	}
	Map<String, String> undoMap = new HashMap<String, String>();

	List<TermEntry> submissionEntries = findSubTermEntriesByTermIds(submissionTermIds, projectId);

	for (TermEntry termEntry : submissionEntries) {

	    for (String id : submissionTermIds) {
		Term subTerm = termEntry.ggetTermById(id);
		if (subTerm != null) {
		    validateTermStatus(subTerm);
		    validateTermCommited(subTerm);
		}
	    }
	    revertLastHistory(termEntry.getUuId(), projectId);

	}

	submissionEntries = findSubTermEntriesByTermIds(submissionTermIds, projectId);

	for (TermEntry termEntry : submissionEntries) {
	    for (String id : submissionTermIds) {
		Term subTerm = termEntry.ggetTermById(id);
		if (subTerm != null) {
		    undoMap.put(subTerm.getUuId(), subTerm.getTempText());
		}
	    }
	}

	return undoMap;
    }

    @Override
    public void updateSubmissionSourceTerms(String sourceLanguageId, Set<String> termEntryIds, Long projectId) {
	if (CollectionUtils.isNotEmpty(termEntryIds)) {

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.12"), //$NON-NLS-1$
			sourceLanguageId));
	    }

	    List<TermEntry> regularTermEntries = findTermEntriesByIds(termEntryIds, projectId);

	    for (TermEntry termEntry : regularTermEntries) {
		updateSourceTerms(sourceLanguageId, termEntry);
	    }

	    updateRegularTermEntries(projectId, Action.EDITED.name(), new TransactionalUnit(regularTermEntries));
	}
    }

    @Override
    public void updateTempDescriptionText(String termId, String descriptionId, String tempText, Long projectId) {

	TermEntry subTermEntry = findSubTermEntryByTermId(termId, projectId);

	Term subTerm = subTermEntry.ggetTermById(termId);

	validateTermStatus(subTerm);

	Set<Description> descriptions = subTerm.getDescriptions();
	for (Description desc : descriptions) {
	    if (desc.getUuid().equals(descriptionId)) {
		desc.setValue(tempText);

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.9"), //$NON-NLS-1$
			    descriptionId, tempText));
		}
		break;
	    }
	}
	subTerm.setCommited(Boolean.FALSE);
	updateSubmissionTermEntries(projectId, Action.UPDATED_TRANSLATIONS.name(), Arrays.asList(subTermEntry));
    }

    @Override
    public void updateTempTermText(String submissionTermId, String tempTermText, Long projectId) {
	TermEntry subTermEntry = findSubTermEntryByTermId(submissionTermId, projectId);

	Term submissionTerm = subTermEntry.ggetTermById(submissionTermId);

	validateTermStatus(submissionTerm);

	submissionTerm.setTempText(tempTermText);
	submissionTerm.setCommited(Boolean.FALSE);
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionTermServiceImpl.11"), //$NON-NLS-1$
		    submissionTermId, tempTermText));
	}

	updateSubmissionTermEntries(projectId, Action.UPDATED_TRANSLATIONS.name(), Arrays.asList(subTermEntry));
    }

    private List<String> approveRegularTerms(List<TermEntry> termEntries, List<String> approvedTermIds,
	    String sourceLanguageId, List<String> termIds, ProjectDetailInfo projectDetailInfo,
	    Set<StatisticsInfo> statisticsInfos) {
	if (CollectionUtils.isNotEmpty(termEntries)) {

	    long dateTime = new Date().getTime();
	    String user = TmUserProfile.getCurrentUserName();

	    for (TermEntry termEntry : termEntries) {
		for (String termId : termIds) {
		    Term term = termEntry.ggetTermById(termId);
		    if (term != null && ItemStatusTypeHolder.IN_FINAL_REVIEW.getName().equals(term.getStatus())) {

			StatisticsUtils.incrementStatistics(statisticsInfos, term.getProjectId(), term.getLanguageId(),
				ItemStatusTypeHolder.PROCESSED.getName());

			term.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
			term.setDateModified(dateTime);
			term.setUserModified(user);

			termEntry.setAction(Action.APPROVED_TRANSLATIONS);
			termEntry.setDateModified(dateTime);
			termEntry.setUserModified(user);

			String languageId = term.getLanguageId();
			projectDetailInfo.incrementApprovedTermCount(languageId);
			projectDetailInfo.decrementTermInSubmissionCount(languageId);

			approvedTermIds.add(term.getUuId());
		    }
		}
		updateSourceTerms(sourceLanguageId, termEntry);
	    }

	    projectDetailInfo.setDateModified(new Date(dateTime));
	}

	return approvedTermIds;
    }

    private void approveSubmissionTerm(Term term, SubmissionDetailInfo submissionInfo, List<String> termIds) {
	long dateModified = new Date().getTime();

	term.setName(term.getTempText());
	term.setStatus(ItemStatusTypeHolder.PROCESSED.getName());
	term.setInTranslationAsSource(Boolean.FALSE);

	Priority priority = new Priority();

	priority.setAssigneePriority(PriorityEnum.LOW.getValue());
	priority.setSubmitterPriority(PriorityEnum.LOW.getValue());

	term.setPriority(priority);

	term.setDateCompleted(dateModified);
	term.setDateModified(dateModified);
	term.setReviewRequired(null);

	String languageId = term.getLanguageId();

	submissionInfo.decrementInFinalReviewCount(languageId);

	submissionInfo.incrementCompletedCount(languageId);

	termIds.add(term.getParentUuId());
    }

    private void approveSubmissionTerms(List<String> submissionTermIds, SubmissionDetailInfo submissionInfo,
	    String sourceLanguageId, List<TermEntry> submissionEntryes, List<String> termIds) {
	if (CollectionUtils.isNotEmpty(submissionEntryes)) {
	    for (TermEntry termEntry : submissionEntryes) {
		for (String id : submissionTermIds) {
		    Term subTerm = termEntry.ggetTermById(id);
		    if (subTerm != null && ItemStatusTypeHolder.IN_FINAL_REVIEW.getName().equals(subTerm.getStatus())) {
			approveSubmissionTerm(subTerm, submissionInfo, termIds);
		    }
		}

		updateSourceTerms(sourceLanguageId, termEntry);
	    }
	}
    }

    private Map<String, List<TermEntry>> findRegularHistory(Set<String> termEntryIds) {
	return getTermService().findHistoriesByTermEntryIds(termEntryIds);

    }

    private List<TermEntry> findSubTermEntriesByTermIds(List<String> submissionTermIds, Long projectId) {
	try {
	    return getSubmissionBrowser().findByTermIds(submissionTermIds, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private TermEntry findSubTermEntryByTermId(String termId, Long projectId) {
	try {
	    return getSubmissionBrowser().findByTermId(termId, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private Map<String, List<TermEntry>> findSubmissionHistory(Set<String> termEntryIds) {
	return getSubmissionService().findHistoriesByIds(termEntryIds);

    }

    private DbSubmissionTermEntryService getDbSubmissionService() {
	return _dbSubmissionService;
    }

    private ITmgrGlossaryBrowser getSubmissionBrowser() {
	return getBrowser(getSubmissionCollection());
    }

    private SubmissionDetailService getSubmissionDetailService() {
	return _submissionDetailService;
    }

    private SubmissionService getSubmissionService() {
	return _submissionService;
    }

    private TermEntryService getTermService() {
	return _termEntryService;
    }

    private UndoTermImpl getUndoTerm() {
	return _undoTerm;
    }

    private void revertLastHistory(String termEntryId, Long projectId) {
	try {
	    getUpdater(getSubmissionCollection()).revertLastHistory(projectId, undoTermEntry(termEntryId));
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private TermEntry undoTermEntry(String termEntryId) {
	DbSubmissionTermEntry submissionTermEntry = getDbSubmissionService().findById(termEntryId, true);
	return getUndoTerm().undoTerm(submissionTermEntry);
    }

    private void validateTermCommited(Term subTerm) {
	if (subTerm.getCommited()) {
	    throw new UserException(MessageResolver.getMessage("SubmissionTermServiceImpl.14"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("SubmissionTermServiceImpl.16"))); //$NON-NLS-1$
	}
    }

    private void validateTermStatus(Term submissionTerm) {
	String status = submissionTerm.getStatus();
	if (!status.equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName())) {
	    throw new UserException(MessageResolver.getMessage("SubmissionTermServiceImpl.14"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("SubmissionTermServiceImpl.6"), //$NON-NLS-1$
			    submissionTerm.getTempText()));
	}
    }
}
