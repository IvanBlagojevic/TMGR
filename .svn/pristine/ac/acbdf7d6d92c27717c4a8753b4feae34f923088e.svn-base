package org.gs4tr.termmanager.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessage;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.UserMessageTypeEnum;
import org.gs4tr.foundation.modules.entities.model.types.BaseItemStatusTypeHolder;
import org.gs4tr.foundation.modules.service.listener.NotificationListenerSupport;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.dao.ProjectDAO;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.io.exception.TransactionError;
import org.gs4tr.termmanager.io.tlog.impl.TransactionLogHandler;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.PriorityEnum;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryBrowser;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryConnector;
import org.gs4tr.termmanager.persistence.ITmgrGlossarySearcher;
import org.gs4tr.termmanager.persistence.ITmgrGlossaryUpdater;
import org.gs4tr.termmanager.persistence.solr.TmgrSolrConnector;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.ProjectDetailService;
import org.gs4tr.termmanager.service.ProjectService;
import org.gs4tr.termmanager.service.StatisticsService;
import org.gs4tr.termmanager.service.UserProfileService;
import org.gs4tr.termmanager.service.persistence.importer.ITmgrGlossaryImporter;
import org.gs4tr.termmanager.service.persistence.importer.impl.TmgrGlossaryImporter;
import org.gs4tr.termmanager.service.solr.GlossaryConnectionManager;
import org.gs4tr.termmanager.service.solr.SolrServiceConfiguration;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.annotation.Autowired;

import jetbrains.exodus.entitystore.EntityId;

public abstract class AbstractNotifyingService {

    @Autowired
    private GlossaryConnectionManager _connectionManager;

    private NotificationListenerSupport _notificationListenerSupport;

    @Autowired
    private ProjectDAO _projectDAO;

    @Autowired
    private ProjectDetailService _projectDetailService;

    @Autowired
    private ProjectService _projectService;

    @Autowired
    private SolrServiceConfiguration _solrConfig;

    @Autowired
    private StatisticsService _statisticsService;

    @Autowired
    private TransactionLogHandler _tLogHandler;

    @Autowired
    private UserProfileService _userProfileService;

    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    public StatisticsService getStatisticsService() {
	return _statisticsService;
    }

    @Resource(name = "listenerSupport")
    public void setNotificationListenerSupport(NotificationListenerSupport notificationListenerSupport) {
	_notificationListenerSupport = notificationListenerSupport;
    }

    private Term findTermHistory(List<TermEntry> termEntries, Term term) {
	if (termEntries != null) {
	    for (TermEntry t : termEntries) {
		String termId = term.getUuId();
		Term candidate = t.ggetTermById(termId);
		if (Objects.nonNull(candidate)) {
		    // Find term with "Approved", "Pending Approval" or "On Hold" status
		    if (isRegularStatus(candidate)) {
			// Term value before user sent them in submission
			return candidate;
		    }
		}
	    }
	}
	return term;
    }

    private GlossaryConnectionManager getConnectionManager() {
	return _connectionManager;
    }

    private SolrServiceConfiguration getSolrConfig() {
	return _solrConfig;
    }

    private boolean isRegularStatus(Term term) {
	String status = term.getStatus();
	return ItemStatusTypeHolder.PROCESSED.getName().equals(status)
		|| ItemStatusTypeHolder.WAITING.getName().equals(status)
		|| ItemStatusTypeHolder.ON_HOLD.getName().equals(status);
    }

    private void update(Long projectId, String action, TransactionalUnit unit, String collection)
	    throws TransactionError {
	TransactionLogHandler tLogHandler = getTLogHandler();
	Optional<EntityId> optParentId = tLogHandler.startAppending(projectId, TmUserProfile.getCurrentUserName(),
		action, collection);
	if (optParentId.isPresent()) {
	    tLogHandler.appendAndLink(projectId, optParentId.get(), unit);
	    tLogHandler.finishAppending(projectId, optParentId.get());
	}
    }

    private void updateTermEntries(Long projectId, String action, TransactionalUnit unit, String collection)
	    throws InterruptedException {
	try {
	    update(projectId, action, unit, collection);
	} catch (TransactionError e) {
	    Thread.sleep(1000);
	    try {
		update(projectId, action, unit, collection);
	    } catch (TransactionError e1) {
		throw new UserException(Messages.getString("project.is.locked.m"),
			Messages.getString("project.is.locked"), UserMessageTypeEnum.WARNING, e);
	    }
	} catch (EventException ee) {
	    throw new RuntimeException(ee.getMessage(), ee);
	}
    }

    protected List<TermEntry> browseTermEntries(TmgrSearchFilter filter) {
	try {
	    return getBrowser(getRegularCollection()).browse(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected void cancelRegularTermTranslation(Set<String> regularTermEntryIds, Set<String> regularTermIds,
	    String sourceLanguageId, Long projectId, Map<String, List<TermEntry>> regularHistory) {

	if (CollectionUtils.isNotEmpty(regularTermEntryIds)) {
	    // Find term entries including disabled terms
	    List<TermEntry> regularTermEntries = findTermEntriesByIds(getRegularCollection(), regularTermEntryIds,
		    projectId);

	    long dateTime = new Date().getTime();
	    String user = TmUserProfile.getCurrentUserName();

	    for (TermEntry regularTermEntry : regularTermEntries) {

		for (String regularTermId : regularTermIds) {
		    Term regularTerm = regularTermEntry.ggetTermById(regularTermId);
		    if (regularTerm == null) {
			continue;
		    }

		    String statusOld = regularTerm.getStatusOld() != null ? regularTerm.getStatusOld()
			    : BaseItemStatusTypeHolder.WAITING.getName();

		    Term historyTerm = findTermHistory(regularHistory.get(regularTermEntry.getUuId()), regularTerm);

		    String oldName = historyTerm != null ? historyTerm.getName() : StringUtils.EMPTY;

		    Set<Description> descriptions = historyTerm != null ? historyTerm.getDescriptions() : null;

		    if (StringUtils.isBlank(oldName)
			    || ItemStatusTypeHolder.MISSING_TRANSLATION.getName().equals(statusOld)) {
			regularTerm.setDisabled(Boolean.TRUE);
		    }

		    regularTerm.setStatus(statusOld);
		    regularTerm.setName(oldName);
		    regularTerm.setDateModified(dateTime);
		    regularTerm.setUserModified(user);
		    regularTerm.setDescriptions(descriptions);

		    regularTermEntry.setAction(Action.CANCELED_TRANSLATIONS);
		    regularTermEntry.setDateModified(dateTime);
		    regularTermEntry.setUserModified(user);
		}
		updateSourceTerms(sourceLanguageId, regularTermEntry);
	    }

	    updateRegularTermEntries(projectId, Action.CANCELED_TRANSLATIONS.name(),
		    new TransactionalUnit(regularTermEntries));
	}
    }

    protected void cancelSubmissionTermTranslation(ProjectDetailInfo info, List<String> submissionTermIds,
	    SubmissionDetailInfo submissionInfo, List<TermEntry> subTermEntries, Set<String> regularTermEntryIds,
	    Set<String> regularTermIds, Map<String, List<TermEntry>> submissionHistory) {

	long date = new Date().getTime();

	for (TermEntry subTermEntry : subTermEntries) {

	    for (String subTermId : submissionTermIds) {
		Term submissionTerm = subTermEntry.ggetTermById(subTermId);
		if (submissionTerm == null || submissionTerm.getCanceled()
			|| !ItemStatusTypeHolder.isTermInTranslation(submissionTerm)) {
		    continue;
		}

		regularTermEntryIds.add(subTermEntry.getParentUuId());

		regularTermIds.add(submissionTerm.getParentUuId());

		Term historyTerm = findTermHistory(submissionHistory.get(subTermEntry.getUuId()), submissionTerm);

		submissionTerm.setDescriptions(historyTerm.getDescriptions());
		submissionTerm.setCanceled(Boolean.TRUE);
		submissionTerm.setDateCompleted(date);

		String oldTermName = historyTerm.getName();

		String status = submissionTerm.getStatus();
		String statusOld = StringUtils.isNotEmpty(oldTermName) ? submissionTerm.getStatusOld()
			: ItemStatusTypeHolder.MISSING_TRANSLATION.getName();

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("AbstractNotifyingService.0"), //$NON-NLS-1$
			    status, statusOld));
		}

		if (ItemStatusTypeHolder.MISSING_TRANSLATION.getName().equals(statusOld)) {
		    oldTermName = StringConstants.EMPTY;
		    info.decrementTermCount(submissionTerm.getLanguageId());
		} else if (ItemStatusTypeHolder.PROCESSED.getName().equals(statusOld)) {
		    info.incrementApprovedTermCount(submissionTerm.getLanguageId());
		} else if (ItemStatusTypeHolder.WAITING.getName().equals(statusOld)) {
		    info.incrementPendingTermCount(submissionTerm.getLanguageId());
		} else if (ItemStatusTypeHolder.ON_HOLD.getName().equals(statusOld)) {
		    info.incrementOnHoldTermCount(submissionTerm.getLanguageId());
		} else if (ItemStatusTypeHolder.BLACKLISTED.getName().equals(statusOld)) {
		    info.incrementForbiddenTermCount(submissionTerm.getLanguageId());
		}

		submissionTerm.setStatus(statusOld);
		submissionTerm.setDateModified(date);
		submissionTerm.setName(oldTermName);

		long priority = PriorityEnum.LOWEST.getValue();

		submissionTerm.setPriority(new Priority(priority, priority));

		submissionTerm.setTempText(oldTermName);
		submissionTerm.setCommited(Boolean.TRUE);

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(String.format(Messages.getString("AbstractNotifyingService.1"), //$NON-NLS-1$
			    submissionTerm.getSubmissionName(), submissionTerm.getUuId()));
		}

		String languageId = submissionTerm.getLanguageId();
		submissionInfo.incrementCanceledCount(languageId);

		if (status.equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName())) {
		    submissionInfo.decrementInTranslationCount(languageId);
		} else if (status.equals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName())) {
		    submissionInfo.decrementInFinalReviewCount(languageId);
		}
		info.decrementTermInSubmissionCount(languageId);
	    }

	    updateSourceTerms(submissionInfo.getSourceLanguageId(), subTermEntry);
	}

	updateSubmissionTermEntries(info.getProjectId(), Action.CANCELED_TRANSLATIONS.name(), subTermEntries);
    }

    protected void checkLock(long projectId) {
	boolean locked = getTLogHandler().isLocked(projectId);
	if (locked) {
	    throw new UserException(MessageResolver.getMessage("project.is.locked.m"),
		    MessageResolver.getMessage("project.is.locked"), UserMessageTypeEnum.WARNING);
	}
    }

    protected void findSubmissionAndRegularTermEntriesByTermIds(List<String> termIds, String collection, Long projectId,
	    Set<String> regularTermEntriesIds, Set<String> submissionTermEntriesIds) {
	List<TermEntry> entries = findTermEntriesByTermIds(collection, termIds, projectId);
	if (!entries.isEmpty()) {
	    for (TermEntry termEntry : entries) {
		submissionTermEntriesIds.add(termEntry.getUuId());
		regularTermEntriesIds.add(termEntry.getParentUuId());
	    }
	}
    }

    protected List<TermEntry> findTermEntriesByIds(Collection<String> termEntryIds, Long projectId) {
	return findTermEntriesByIds(getRegularCollection(), termEntryIds, projectId);
    }

    protected List<TermEntry> findTermEntriesByIds(String collection, Collection<String> termEntryIds, Long projectId) {
	try {
	    return getBrowser(collection).findByIds(termEntryIds, Arrays.asList(projectId));
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected List<TermEntry> findTermEntriesByTermIds(String collection, Collection<String> termIds, Long projectId) {
	try {
	    return getBrowser(collection).findByTermIds(termIds, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected TermEntry findTermEntryById(String collection, String termEntryId, Long projectId) {
	try {
	    return getBrowser(collection).findById(termEntryId, projectId);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected ITmgrGlossaryBrowser getBrowser(String collection) {
	try {
	    return getConnectionManager().getConnector(collection).getTmgrBrowser();
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected ITmgrGlossaryConnector getConnector() {
	return getConnectionManager().getConnector(getRegularCollection());
    }

    protected ITmgrGlossaryImporter getImporter() {
	return new TmgrGlossaryImporter(
		(TmgrSolrConnector) getConnectionManager().getConnector(getRegularCollection()));
    }

    protected NotificationListenerSupport getNotificationListenerSupport() {
	return _notificationListenerSupport;
    }

    protected ProjectDAO getProjectDAO() {
	return _projectDAO;
    }

    protected ProjectDetailService getProjectDetailService() {
	return _projectDetailService;
    }

    protected ProjectService getProjectService() {
	return _projectService;
    }

    protected String getRegularCollection() {
	return getSolrConfig().getRegularCollection();
    }

    protected ITmgrGlossarySearcher getSearcher(String collection) {
	try {
	    return getConnectionManager().getConnector(collection).getTmgrSearcher();
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected String getSubmissionCollection() {
	return getSolrConfig().getSubmissionCollection();
    }

    protected TransactionLogHandler getTLogHandler() {
	return _tLogHandler;
    }

    protected ITmgrGlossaryUpdater getUpdater(String collection) {
	try {
	    return getConnectionManager().getConnector(collection).getTmgrUpdater();
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected UserProfileService getUserProfileService() {
	return _userProfileService;
    }

    protected void notifyListeners(NotifyingMessage notifyingMessage) {
	getNotificationListenerSupport().notifyListeners(notifyingMessage);
    }

    protected void updateCompletedSubmissionDetails(ProjectDetailInfo projectDetailInfo, Submission submission) {
	if (submission.isCompleted()) {
	    Set<String> assignees = new HashSet<>();
	    Set<String> languageIds = new HashSet<>();
	    Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	    if (CollectionUtils.isNotEmpty(submissionLanguages)) {
		for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		    assignees.add(submissionLanguage.getAssignee());
		    languageIds.add(submissionLanguage.getLanguageId());
		}
	    }

	    projectDetailInfo.incrementCompletedSubmissionCount();
	    projectDetailInfo.decrementActiveSubmissionCount();

	    String submitter = submission.getSubmitter();
	    TmUserProfile submitterUser = getUserProfileService().findUsersByUserNameNoFetch(submitter);
	    Long userId = submitterUser.getUserProfileId();

	    projectDetailInfo.incrementCompletedSubmissionCount(userId);
	    projectDetailInfo.decrementActiveSubmissionCount(userId);

	    List<TmUserProfile> assigneeUsers = getUserProfileService().findUsersByUsernames(assignees);
	    for (TmUserProfile assingeeUser : assigneeUsers) {
		Long userProfileId = assingeeUser.getUserProfileId();
		if (!userId.equals(userProfileId)) {
		    projectDetailInfo.incrementCompletedSubmissionCount(userProfileId);
		    projectDetailInfo.decrementActiveSubmissionCount(userProfileId);
		}
	    }

	    for (String languageId : languageIds) {
		projectDetailInfo.incrementCompletedSubmissionCount(languageId);
		projectDetailInfo.incrementCompletedSubmissionCount(userId, languageId);
		projectDetailInfo.decrementActiveSubmissionCount(languageId);
		projectDetailInfo.decrementActiveSubmissionCount(userId, languageId);

		for (TmUserProfile assingeeUser : assigneeUsers) {
		    Long userProfileId = assingeeUser.getUserProfileId();

		    if (!userId.equals(userProfileId)) {
			projectDetailInfo.incrementCompletedSubmissionCount(userProfileId, languageId);
			projectDetailInfo.decrementActiveSubmissionCount(userProfileId, languageId);
		    }
		}
	    }
	}
    }

    protected void updateRegularTermEntries(Long projectId, String action, TransactionalUnit unit) {
	try {
	    updateTermEntries(projectId, action, unit, getRegularCollection());
	} catch (InterruptedException e) {
	    throw new UserException(e.getMessage(), e);
	}
    }

    protected void updateRegularTermEntriesInIndex(List<TermEntry> termEntries) {
	if (CollectionUtils.isEmpty(termEntries)) {
	    return;
	}
	try {
	    getUpdater(getRegularCollection()).update(termEntries);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    protected void updateSourceTerms(String sourceLanguageId, TermEntry termEntry) {
	boolean allTermsTranslated = true;
	List<Term> terms = termEntry.ggetTerms();
	Set<Term> sourceTerms = termEntry.getLanguageTerms().get(sourceLanguageId);

	if (CollectionUtils.isNotEmpty(sourceTerms)) {
	    terms.removeAll(sourceTerms);
	}

	for (Term term : terms) {
	    if (ItemStatusTypeHolder.isTermInTranslation(term)) {
		allTermsTranslated = false;
		break;
	    }
	}
	if (allTermsTranslated) {
	    for (Term term : sourceTerms) {
		term.setInTranslationAsSource(Boolean.FALSE);
	    }
	}
    }

    protected void updateSubmissionTermEntries(Long projectId, String action, List<TermEntry> termEntries) {
	try {
	    updateTermEntries(projectId, action, new TransactionalUnit(termEntries), getSubmissionCollection());
	} catch (InterruptedException e) {
	    throw new UserException(e.getMessage(), e);
	}
    }
}
