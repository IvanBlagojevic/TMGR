package org.gs4tr.termmanager.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.gs4tr.foundation.modules.entities.model.UserException;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.foundation.modules.webmvc.i18n.MessageResolver;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.BaseTypeEnum;
import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TermEntryTranslationUnit;
import org.gs4tr.termmanager.model.TmProject;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.UpdateCommand;
import org.gs4tr.termmanager.model.UpdateCommand.CommandEnum;
import org.gs4tr.termmanager.model.UpdateCommand.TypeEnum;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.glossary.Action;
import org.gs4tr.termmanager.model.glossary.Description;
import org.gs4tr.termmanager.model.glossary.Priority;
import org.gs4tr.termmanager.model.glossary.PriorityEnum;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import org.gs4tr.termmanager.persistence.solr.query.TmgrSearchFilter;
import org.gs4tr.termmanager.service.DbSubmissionTermEntryService;
import org.gs4tr.termmanager.service.SubmissionDetailService;
import org.gs4tr.termmanager.service.SubmissionService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.gs4tr.termmanager.service.history.HistoryCreator;
import org.gs4tr.termmanager.service.manualtask.ManualTaskHandlerUtils;
import org.gs4tr.termmanager.service.notification.NotificationData;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.gs4tr.termmanager.service.utils.TermEntryUtils;
import org.gs4tr.termmanager.solr.plugin.utils.SolrParentDocFields;
import org.gs4tr.tm3.api.TmException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("submissionService")
public class SubmissionServiceImpl extends AbstractNotifyingService implements SubmissionService {

    @Autowired
    private BeanFactory _beanFactory;

    @Autowired
    @Qualifier("submissionHistoryCreator")
    private HistoryCreator _historyCreator;

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionDetailService _submissionDetailService;

    @Autowired
    private SubmissionLanguageDAO _submissionLanguageDAO;

    @Autowired
    private DbSubmissionTermEntryService _submissionTermService;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Autowired
    private TermEntryService _termEntryService;

    @Transactional
    @Override
    public void addComments(String commentText, Long submissionId, List<String> termIds, final String languageId) {
	Submission submission = loadSubmission(submissionId);

	List<UpdateCommand> updateCommands = new ArrayList<>();

	if (CollectionUtils.isNotEmpty(termIds)) {
	    for (String id : termIds) {
		UpdateCommand command = ServiceUtils.createCommentUpdateCommand(id, commentText);
		updateCommands.add(command);
	    }
	} else {
	    final TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	    Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();

	    Long projectId = submission.getProject().getProjectId();

	    final boolean isSubmitterUser = ServiceUtils.isSubmitterUser(user, projectId);
	    if ((!isSubmitterUser) || (languageId != null)) {
		CollectionUtils.filter(submissionLanguages, new Predicate() {
		    @Override
		    public boolean evaluate(Object object) {
			SubmissionLanguage subLang = (SubmissionLanguage) object;
			String assignee = subLang.getAssignee();
			String userName = user.getUserName();

			boolean equalsAssigneeOrSubmitter = assignee.equals(userName) || (isSubmitterUser);
			boolean equalsLanguage = true;

			if (languageId != null) {
			    equalsLanguage = subLang.getLanguageId().equals(languageId);
			}
			return equalsAssigneeOrSubmitter && equalsLanguage;
		    }
		});
	    }

	    for (SubmissionLanguage subLanguage : submissionLanguages) {
		UpdateCommand command = ServiceUtils.createCommentUpdateCommand(subLanguage.getMarkerId(), commentText);
		updateCommands.add(command);
	    }
	}

	for (UpdateCommand command : updateCommands) {
	    EventMessage eventMessage = EventMessage.createEvent(EventMessage.EVENT_ADD_COMMENT);
	    eventMessage.addContextVariable(EventMessage.VARIABLE_SUBMISSION, submission);
	    eventMessage.addContextVariable(EventMessage.VARIABLE_COMMAND, command);

	    notifyListeners(eventMessage);
	}
    }

    @Override
    public Set<String> cancelSubmission(Long submissionId) {
	// new transaction
	Submission submission = getSelf().findByIdFetchChilds(submissionId);

	TmProject project = submission.getProject();
	Long projectId = project.getProjectId();
	checkLock(projectId);

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(projectId);
	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.0"), //$NON-NLS-1$
		    submission.getName()));
	}

	List<String> submissionTermIds = findSubTermIdsBySubmissionId(submissionId);
	Set<String> submisionTermEntryIds = new HashSet<>();
	Set<String> regularTermEntriesIds = new HashSet<>();
	Set<String> termEntryIds = new HashSet<>();

	if (CollectionUtils.isNotEmpty(submissionTermIds)) {
	    findSubmissionAndRegularTermEntriesByTermIds(submissionTermIds, getSubmissionCollection(),
		    project.getProjectId(), regularTermEntriesIds, submisionTermEntryIds);

	    Map<String, List<TermEntry>> regularHistory = getRegularHistory(regularTermEntriesIds);
	    Map<String, List<TermEntry>> submissionHistory = findHistoriesByIds(submisionTermEntryIds);

	    List<TermEntry> subTermEntries = findTermEntriesByTermIds(getSubmissionCollection(), submissionTermIds,
		    projectId);

	    Set<String> regularTermIds = new HashSet<>();

	    cancelSubmissionTermTranslation(projectDetailInfo, submissionTermIds, submissionDetailInfo, subTermEntries,
		    termEntryIds, regularTermIds, submissionHistory);
	    cancelRegularTermTranslation(termEntryIds, regularTermIds, submissionDetailInfo.getSourceLanguageId(),
		    projectId, regularHistory);

	    if (CollectionUtils.isNotEmpty(termEntryIds)) {
		getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);
		// reload submission
		submission = getSelf().findByIdFetchChilds(submissionId);
		updateCompletedSubmissionDetails(projectDetailInfo, submission);
		projectDetailInfo.setDateModified(new Date());
		getProjectDetailService().incrementUpdateProjectDetail(projectDetailInfo);
	    }
	}
	return termEntryIds;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationData collectNotificationData(Long submissionId) {
	NotificationData notificationData = new NotificationData();

	String username = TmUserProfile.getCurrentUserName();

	Submission submission = loadSubmission(submissionId);

	List<Term> submissionTerms = findTermsBySubmissionId(submissionId);

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    List<Term> submissionLanguageTerms = new LinkedList<>();
	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		String assignee = submissionLanguage.getAssignee();
		ItemStatusType statusAssignee = submissionLanguage.getStatusAssignee();
		ItemStatusType completed = ItemStatusTypeHolder.COMPLETED;
		if (assignee.equals(username) && statusAssignee.equals(completed)) {
		    notificationData.setReady(true);

		    String languageId = submissionLanguage.getLanguageId();
		    Map<String, MutableInt> languageTermNumber = notificationData.getLanguageTermNumber();
		    MutableInt numberOfTerms = languageTermNumber.get(languageId);
		    if (numberOfTerms == null) {
			numberOfTerms = new MutableInt(0);
			languageTermNumber.put(languageId, numberOfTerms);
		    }

		    for (Term term : submissionTerms) {
			if (term.getLanguageId().equals(languageId)) {
			    submissionLanguageTerms.add(term);
			}
		    }

		    numberOfTerms.setValue(submissionLanguageTerms.size());
		}

		submissionLanguageTerms.clear();
	    }
	}

	return notificationData;
    }

    @Override
    public Set<String> commitTranslationChanges(Long submissionId, String languageId, List<String> subTermIds) {
	// new transaction
	Submission submission = getSelf().findByIdFetchChilds(submissionId);

	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	TmProject project = getProjectService().load(submission.getProject().getProjectId());

	Long projectId = project.getProjectId();
	checkLock(projectId);

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(projectId);

	List<Term> submissionTerms = new ArrayList<>();

	if (CollectionUtils.isEmpty(subTermIds)) {

	    TmgrSearchFilter filter = new TmgrSearchFilter();
	    filter.setSubmissionId(submissionId);
	    filter.setCommited(Boolean.FALSE);
	    List<String> targetLanguages = new ArrayList<>();
	    if (StringUtils.isNotBlank(languageId)) {
		targetLanguages.add(languageId);
		filter.setTargetLanguages(targetLanguages);
	    }

	    List<TermEntry> termEntries = browseSubTermEntries(filter);

	    if (CollectionUtils.isEmpty(termEntries)) {
		throw new UserException(MessageResolver.getMessage("SubmissionServiceImpl.14"), //$NON-NLS-1$
			MessageResolver.getMessage("SubmissionServiceImpl.15")); //$NON-NLS-1$
	    }

	    for (TermEntry termEntry : termEntries) {
		Collection<Term> terms = StringUtils.isNotEmpty(languageId)
			? termEntry.getLanguageTerms().get(languageId)
			: termEntry.ggetTerms();
		if (CollectionUtils.isNotEmpty(terms)) {
		    for (Term subTerm : terms) {
			if (subTerm.getCommited() != null && !subTerm.getCommited()
				&& !subTerm.getInTranslationAsSource()) {
			    submissionTerms.add(subTerm);
			}
		    }
		}
	    }

	} else {
	    submissionTerms = findSubTermsByIds(subTermIds, projectId);
	}

	Set<String> termEntryIds = new HashSet<>();
	Set<TermEntry> regularTermEntries = new HashSet<>();
	Set<TermEntry> submissionTermEntries = new HashSet<>();

	if (CollectionUtils.isNotEmpty(submissionTerms)) {
	    for (Term submissionTerm : submissionTerms) {
		validateSubmissionTermForCommit(submissionTerm);

		termEntryIds.add(submissionTerm.getTermEntryId());

		List<UpdateCommand> commands = new ArrayList<>();

		UpdateCommand command = new UpdateCommand();
		command.setCommand(CommandEnum.UPDATE_TRANSLATION.getName());
		command.setItemType(TypeEnum.TERM.getName());
		command.setValue(submissionTerm.getTempText());
		command.setMarkerId(submissionTerm.getUuId());

		commands.add(command);

		Set<Description> descriptions = submissionTerm.getDescriptions();
		if (CollectionUtils.isNotEmpty(descriptions)) {
		    for (Description description : descriptions) {
			String baseType = description.getBaseType();
			String type = baseType.equals(BaseTypeEnum.DESCRIPTION.getTypeName()) ? "description" //$NON-NLS-1$
				: "note"; //$NON-NLS-1$
			UpdateCommand descCommand = new UpdateCommand();
			descCommand.setCommand(CommandEnum.UPDATE_TRANSLATION.getName());
			descCommand.setItemType(type);
			descCommand.setSubType(description.getType());
			descCommand.setValue(description.getTempValue());
			descCommand.setParentMarkerId(submissionTerm.getUuId());
			commands.add(descCommand);
		    }
		}

		TermEntry submissionTermEntry = findSubmissionTermEntryById(submissionTerm.getTermEntryId(), projectId);
		TermEntry regularTermEntry = findRegularTermEntryById(submissionTermEntry.getParentUuId(), projectId);

		// new transaction
		getSelf().notifyListeners(project, projectDetailInfo, regularTermEntry, submissionTermEntry,
			submissionDetailInfo, submission, commands, null);

		addOrUpdatedTermEntry(regularTermEntries, regularTermEntry, submissionTermEntry.getParentUuId(),
			submissionTerm.getParentUuId());
		addOrUpdatedTermEntry(submissionTermEntries, submissionTermEntry, submissionTermEntry.getUuId(),
			submissionTerm.getUuId());

	    }

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.4"), //$NON-NLS-1$
			submission.getName(), submission.getSubmissionId()));
	    }

	    getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);
	    // re-load submission
	    submission = getSelf().findByIdFetchChilds(submissionId);

	    /* To change date modified in project */
	    projectDetailInfo.setDateModified(new Date());

	    updateCompletedSubmissionDetails(projectDetailInfo, submission);
	} else {
	    throw new UserException(MessageResolver.getMessage("SubmissionServiceImpl.14"), //$NON-NLS-1$
		    MessageResolver.getMessage("SubmissionServiceImpl.15")); //$NON-NLS-1$
	}

	updateRegularTermEntries(projectId, Action.UPDATED_TRANSLATIONS.name(),
		new TransactionalUnit(new ArrayList<>(regularTermEntries), projectDetailInfo));
	updateSubmissionTermEntries(projectId, Action.UPDATED_TRANSLATIONS.name(),
		new ArrayList<>(submissionTermEntries));

	return termEntryIds;
    }

    @Override
    public Submission createSubmission(Long projectId, List<TermEntryTranslationUnit> translationUnits,
	    String submissionName, String submissionMarkerId, String sourceLanguage, boolean reviewIsRequired,
	    Long submissionId, List<Term> sourceTerms) {
	TmProject project = getProjectService().load(projectId);
	checkLock(projectId);

	ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(project.getProjectId());

	Submission submission = getSubmission(project, submissionName, submissionMarkerId, sourceLanguage,
		submissionId);

	submissionId = submission.getSubmissionId();

	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	Set<String> assignees = new HashSet<>();

	List<TermEntry> regularTermEntries = new ArrayList<>();
	List<TermEntry> submissionTermEntries = new ArrayList<>();

	Set<String> termEntryIds = new HashSet<>();
	if (CollectionUtils.isNotEmpty(translationUnits)) {
	    for (TermEntryTranslationUnit translationUnit : translationUnits) {
		List<UpdateCommand> updateCommands = translationUnit.getUpdateCommands();

		if (CollectionUtils.isEmpty(updateCommands)) {
		    continue;
		}

		ServiceUtils.sortUpdateCommandsByType(updateCommands);

		TermEntry regularTermEntry = null;
		TermEntry submissionTermEntry = null;

		String regularTermEntryId = translationUnit.getTermEntryId();
		if (regularTermEntryId != null) {
		    termEntryIds.add(regularTermEntryId);
		    // Find term entry including disabled terms
		    regularTermEntry = findTermEntryById(getRegularCollection(), regularTermEntryId, projectId);

		    if (regularTermEntry == null) {
			continue;
		    }

		    submissionTermEntry = createSubmissionTermEntry(regularTermEntry, submission);

		    String submissionTermEntryMarkerId = submissionTermEntry.getUuId();

		    TermEntryUtils.fillWithParentMarkerId(updateCommands, submissionTermEntryMarkerId);
		    TermEntryUtils.fillCommentWithParentMarkerId(updateCommands, submissionTermEntryMarkerId);

		    addSourceTerms(submissionTermEntry, regularTermEntry, sourceTerms, submission, reviewIsRequired);
		}

		for (UpdateCommand updateCommand : updateCommands) {
		    assignees.add(updateCommand.getAsssignee());
		}

		// new transaction
		getSelf().notifyListeners(project, projectDetailInfo, regularTermEntry, submissionTermEntry,
			submissionDetailInfo, submission, updateCommands, reviewIsRequired);

		if (regularTermEntry != null) {
		    regularTermEntries.add(regularTermEntry);
		    submissionTermEntries.add(submissionTermEntry);
		}
	    }
	}

	for (@SuppressWarnings("unused")
	String termEntryId : termEntryIds) {
	    submissionDetailInfo.incrementTermEntryCount();
	}

	projectDetailInfo.incrementActiveSubmissionCount();

	TmUserProfile submitterUser = TmUserProfile.getCurrentUserProfile();
	Long submitterProfileId = submitterUser.getUserProfileId();
	projectDetailInfo.incrementActiveSubmissionCount(submitterProfileId);

	List<TmUserProfile> assigneeUsers = getUserProfileService().findUsersByUsernames(assignees);

	for (TmUserProfile assigneeUser : assigneeUsers) {
	    Long assigneeProfileId = assigneeUser.getUserProfileId();
	    if (!submitterProfileId.equals(assigneeProfileId)) {
		projectDetailInfo.incrementActiveSubmissionCount(assigneeProfileId);
	    }
	}

	getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.4"), //$NON-NLS-1$
		    submissionName, submission.getSubmissionId()));
	}

	updateRegularTermEntries(projectId, Action.SENT_TO_TRANSLATION.name(),
		new TransactionalUnit(regularTermEntries, projectDetailInfo));
	updateSubmissionTermEntries(projectId, Action.SENT_TO_TRANSLATION.name(), submissionTermEntries);

	return submission;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(String submissionName) {
	boolean exists = true;

	try {
	    Submission submission = getSubmissionDAO().findByName(submissionName);
	    if (submission == null) {
		exists = false;
	    }
	} catch (Exception e) {
	    exists = false;
	}

	return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public Submission findByIdFetchChilds(Long submissionId) {
	return getSubmissionDAO().findSubmissionByIdFetchChilds(submissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> findByIds(List<Long> submissionIds) {
	return getSubmissionDAO().findByIds(submissionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findBySubmissionAndUserId(Long submissionId, Long userProfileId) {
	return getSubmissionUserDAO().findBySubmissionAndUserId(submissionId, userProfileId);
    }

    @Override
    public Map<String, List<TermEntry>> findHistoriesByIds(Collection<String> termEntryIds) {
	Map<String, List<TermEntry>> result = new HashMap<>();
	List<DbSubmissionTermEntry> dbEntries = getSubmissionTermService().findByIds(termEntryIds, true);
	for (DbSubmissionTermEntry dbSubmmissionTermEntry : dbEntries) {
	    result.put(dbSubmmissionTermEntry.getUuId(), getSubmissionHistory(dbSubmmissionTermEntry));
	}
	return result;

    }

    @Override
    public List<TermEntry> findHistoryById(String termEntryId) {
	DbSubmissionTermEntry submissionEntry = getSubmissionTermService().findById(termEntryId, true);
	return getSubmissionHistory(submissionEntry);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TmProject> findProjectsBySubmissionIds(List<Long> submissionIds) {
	return getSubmissionDAO().findProjectsBySubmissionIds(submissionIds);
    }

    @Override
    public TermEntry findRegularTermEntryById(String termEntryId, Long projectId) {
	return findTermEntryById(getRegularCollection(), termEntryId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Submission findSubmissionByIdFetchChilds(Long submissionId) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.7"), submissionId)); //$NON-NLS-1$
	}

	return getSubmissionDAO().findSubmissionByIdFetchChilds(submissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionLanguage> findSubmissionLanguages(Long submissionId) {
	return getSubmissionLanguageDAO().findSubmissionLanguagesBySubmissionId(submissionId);
    }

    @Override
    public TermEntry findSubmissionTermEntryById(String termId, Long projectId) {
	return findTermEntryById(getSubmissionCollection(), termId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<String>> findSubmissionUsers(List<Long> submissionIds) {
	Map<String, Set<String>> submitterAssingees = new HashMap<>();
	if (CollectionUtils.isNotEmpty(submissionIds)) {
	    List<Submission> subs = getSubmissionDAO().findByIds(submissionIds);
	    for (Submission submission : subs) {
		String submitter = submission.getSubmitter();
		Set<String> assignees = submitterAssingees.get(submitter);
		if (assignees == null) {
		    assignees = new HashSet<>();
		    submitterAssingees.put(submitter, assignees);
		}

		Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();
		for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		    String assignee = submissionLanguage.getAssignee();
		    assignees.add(assignee);
		}
	    }
	}

	return submitterAssingees;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> findSubmissionsByIdsFetchChilds(List<Long> submissionIds) {
	return getSubmissionDAO().findSubmissionsByIdsFetchChilds(submissionIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Submission> findSubmissionsByProjectId(Long projectId) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.8"), //$NON-NLS-1$
		    projectId));
	}

	return getSubmissionDAO().findSubmissionsByProjectId(projectId);
    }

    @Override
    public List<Term> findTermsBySubmissionId(Long submissionId) {
	List<Term> submissionTerms = new ArrayList<>();
	List<TermEntry> termEntries = findSubTermEntriesBySubmissionId(submissionId);

	if (CollectionUtils.isNotEmpty(termEntries)) {
	    for (TermEntry termEntry : termEntries) {
		submissionTerms.addAll(termEntry.ggetTerms());
	    }
	}
	return submissionTerms;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Submission> findUserSubmissionsByProjectIds(Collection<Long> projectIds) {
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	final String username = userProfile.getUserName();

	List<Submission> submissions = getSubmissionDAO().findSubmissionsByProjectIds(projectIds);

	if (CollectionUtils.isEmpty(submissions)) {
	    return submissions;
	}

	if (ServiceUtils.isSubmitterUser(userProfile, 0L)) {
	    CollectionUtils.filter(submissions, new Predicate() {
		@Override
		public boolean evaluate(Object o) {
		    Submission submission = (Submission) o;
		    return username.equals(submission.getSubmitter());

		}
	    });
	} else if (ServiceUtils.isAssigneeUser(userProfile, 0L)) {
	    CollectionUtils.filter(submissions, new Predicate() {
		@Override
		public boolean evaluate(Object o) {
		    Submission submission = (Submission) o;
		    Set<SubmissionLanguage> subLangs = submission.getSubmissionLanguages();
		    if (CollectionUtils.isNotEmpty(subLangs)) {
			for (SubmissionLanguage subLang : subLangs) {
			    if (username.equals(subLang.getAssignee())) {
				return true;
			    }
			}
		    }

		    return false;
		}
	    });
	}

	return submissions;
    }

    @Override
    @Transactional(readOnly = true)
    public Submission load(Long submissionId) {
	return getSubmissionDAO().load(submissionId);
    }

    @Override
    @Transactional
    public void notifyListeners(TmProject project, ProjectDetailInfo projectDetailInfo, TermEntry termEntry,
	    TermEntry submissionTermEntry, SubmissionDetailInfo submissionDetailInfo, Submission submission,
	    List<UpdateCommand> updateCommands, Boolean reviewRequired) {
	for (UpdateCommand command : updateCommands) {
	    EventMessage message = EventMessage.createEvent(EventMessage.EVENT_UPDATE_TERMENTRY);

	    message.addContextVariable(EventMessage.VARIABLE_PROJECT, project);
	    message.addContextVariable(EventMessage.VARIABLE_SUBMISSION, submission);
	    message.addContextVariable(EventMessage.VARIABLE_SUBMISSION_INFO, submissionDetailInfo);
	    message.addContextVariable(EventMessage.VARIABLE_DETAIL_INFO, projectDetailInfo);
	    message.addContextVariable(EventMessage.VARIABLE_COMMAND, command);
	    message.addContextVariable(EventMessage.VARIABLE_TERM_ENTRY, termEntry);
	    message.addContextVariable(EventMessage.VARIABLE_SUBMISSION_TERM_ENTRY, submissionTermEntry);
	    message.addContextVariable(EventMessage.VARIABLE_REVIEW_REQUIRED, reviewRequired);

	    getNotificationListenerSupport().notifyListeners(message);
	}
    }

    @Override
    public Submission reSubmitTerms(Long submissionId, List<String> submissionTermIds) {
	Submission submission = getSelf().load(submissionId);

	Long projectId = submission.getProject().getProjectId();
	checkLock(projectId);

	SubmissionDetailInfo submissionDetailInfo = new SubmissionDetailInfo(submissionId,
		submission.getSourceLanguageId());

	List<TermEntry> subTermEntries = findSubTermEntriesByTermIds(submissionTermIds, projectId);

	List<Term> submissionTerms = new ArrayList<>();

	for (TermEntry termEntry : subTermEntries) {
	    for (String id : submissionTermIds) {
		Term subTerm = termEntry.ggetTermById(id);
		if (subTerm != null) {
		    submissionTerms.add(subTerm);
		}
	    }
	}

	CollectionUtils.filter(submissionTerms, new Predicate() {
	    @Override
	    public boolean evaluate(Object object) {
		Term submissionTerm = (Term) object;
		String status = submissionTerm.getStatus();
		return status.equals(ItemStatusTypeHolder.IN_FINAL_REVIEW.getName());
	    }
	});

	if (CollectionUtils.isNotEmpty(submissionTerms)) {

	    Set<String> regularTermIds = new HashSet<>();

	    Map<String, Set<Term>> groupedSubTerms = ManualTaskHandlerUtils.groupTermsByTermEntry(submissionTerms);

	    for (Map.Entry<String, Set<Term>> entry : groupedSubTerms.entrySet()) {
		Set<Term> subTerms = entry.getValue();

		for (Term submissionTerm : subTerms) {
		    regularTermIds.add(submissionTerm.getParentUuId());

		    long priority = PriorityEnum.NORMAL.getValue();
		    long priorityAssignee = PriorityEnum.HIGH.getValue();
		    String languageId = submissionTerm.getLanguageId();
		    submissionTerm.setPriority(new Priority(priorityAssignee, priority));
		    submissionTerm.setStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());

		    submissionDetailInfo.decrementInFinalReviewCount(languageId);
		    submissionDetailInfo.incrementInTranslationCount(languageId);
		}
	    }

	    updateSubmissionTermEntries(projectId, Action.RESUBMITTED.name(), subTermEntries);

	    getSubmissionDetailService().updateSubmissionDetail(submissionDetailInfo);

	    List<TermEntry> regularTermEntries = getTermEntryService().findTermEntriesByTermIds(regularTermIds,
		    projectId);
	    if (CollectionUtils.isNotEmpty(regularTermEntries)) {
		List<Term> regularTerms = new ArrayList<>();

		Date dateModified = new Date();

		for (TermEntry regularTermEntry : regularTermEntries) {
		    for (String id : regularTermIds) {
			Term regularTerm = regularTermEntry.ggetTermById(id);
			if (regularTerm != null) {
			    regularTerms.add(regularTerm);
			    regularTermEntry.setAction(Action.RESUBMITTED);
			    regularTermEntry.setDateModified(dateModified.getTime());
			}
		    }
		}

		ProjectDetailInfo projectDetailInfo = new ProjectDetailInfo(projectId);

		for (Term regularTerm : regularTerms) {
		    regularTerm.setStatus(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName());
		    projectDetailInfo.getUpdatedLanguages().add(regularTerm.getLanguageId());
		    regularTerm.setDateModified(dateModified.getTime());
		}

		updateRegularTermEntries(projectId, Action.RESUBMITTED.name(),
			new TransactionalUnit(regularTermEntries));

		/* To change date modified in project */
		if (CollectionUtils.isNotEmpty(regularTerms)) {
		    projectDetailInfo.setDateModified(dateModified);
		    getProjectDetailService().incrementUpdateProjectDetail(projectDetailInfo);
		}
	    }
	}

	return submission;
    }

    @Override
    @Transactional
    public void saveOrUpdateSubmissionLanguage(SubmissionLanguage submissionLanguage) {
	getSubmissionLanguageDAO().saveOrUpdate(submissionLanguage);
    }

    @Override
    @Transactional
    public Submission saveSubmission(Submission submission) {
	return getSubmissionDAO().save(submission);
    }

    @Override
    @Transactional
    public SubmissionUser saveSubmissionUser(SubmissionUser submissionUser) {
	return getSubmissionUserDAO().save(submissionUser);
    }

    @Override
    @Transactional
    public void updateLanguageByProjectId(String languageFrom, String languageTo, Long projectId) {
	getSubmissionDAO().updateLanguageByProjectId(languageFrom, languageTo, projectId);
    }

    @Override
    @Transactional
    public void updateSubmissionLanguageByProjectId(String languageFrom, String languageTo, Long projectId) {
	getSubmissionLanguageDAO().updateLanguageByProjectId(languageFrom, languageTo, projectId);
    }

    private void addOrUpdatedTermEntry(Set<TermEntry> termEntriesToUpdate, TermEntry incomingUpdateTermEntry,
	    String termEntryUuId, String termUuId) {

	Optional<TermEntry> updatedTermEntryOptional = termEntriesToUpdate.stream()
		.filter(te -> te.getUuId().equals(termEntryUuId)).findFirst();

	if (!updatedTermEntryOptional.isPresent()) {
	    termEntriesToUpdate.add(incomingUpdateTermEntry);
	} else {

	    TermEntry updatedTermEntry = updatedTermEntryOptional.get();

	    replaceOriginalWithUpdatedTerm(incomingUpdateTermEntry, updatedTermEntry, termUuId);
	}
    }

    private void addSourceTerms(TermEntry submissionTermEntry, TermEntry regularTermEntry, List<Term> sourceTerms,
	    Submission submission, boolean reviewIsRequired) {
	String userName = TmUserProfile.getCurrentUserName();
	String parentId = submissionTermEntry.getParentUuId();
	long date = new Date().getTime();

	for (Term sourceTerm : sourceTerms) {
	    if (sourceTerm.getTermEntryId().equals(parentId)) {
		Term term = regularTermEntry.ggetTermById(sourceTerm.getUuId());
		term.setParentUuId(sourceTerm.getUuId());
		term.setInTranslationAsSource(Boolean.TRUE);

		Term submissionTerm = new Term();
		submissionTerm.setUuId(UUID.randomUUID().toString());
		submissionTerm.setLanguageId(sourceTerm.getLanguageId());
		submissionTerm.setParentUuId(sourceTerm.getUuId());
		submissionTerm.setName(sourceTerm.getName());
		submissionTerm.setTempText(sourceTerm.getName());
		submissionTerm.setForbidden(sourceTerm.isForbidden());
		submissionTerm.setCanceled(Boolean.FALSE);
		submissionTerm.setDateSubmitted(date);
		submissionTerm.setDateModified(date);
		submissionTerm.setDateCreated(sourceTerm.getDateCreated());
		submissionTerm.setPriority(new Priority());
		submissionTerm.setCommited(Boolean.TRUE);
		submissionTerm.setInTranslationAsSource(Boolean.TRUE);
		submissionTerm.setTermEntryId(submissionTermEntry.getUuId());
		submissionTerm.setDescriptions(sourceTerm.getDescriptions());
		submissionTerm.setStatus(sourceTerm.getStatus());
		submissionTerm.setUserCreated(userName);
		submissionTerm.setUserModified(userName);

		submissionTerm.setReviewRequired(reviewIsRequired);

		submissionTermEntry.addTerm(submissionTerm);
	    }
	}
    }

    private List<TermEntry> browseSubTermEntries(TmgrSearchFilter filter) {
	try {
	    return getBrowser(getSubmissionCollection()).browse(filter);
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private Submission createEmptySubmission(TmProject project, String submissionName, String submissionMarkerId,
	    String sourceLanguage) {
	if (StringUtils.isEmpty(submissionMarkerId)) {
	    submissionMarkerId = UUID.randomUUID().toString();
	}
	TmUserProfile userProfile = TmUserProfile.getCurrentUserProfile();

	Submission submission = new Submission(sourceLanguage);
	submission.setName(submissionName);
	submission.setMarkerId(submissionMarkerId);
	submission.setSubmitter(userProfile.getUserName());
	submission.setProject(project);

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.9"), //$NON-NLS-1$
		    submissionName, sourceLanguage, project.getProjectInfo().getName()));
	}

	// new transaction
	submission = getSelf().saveSubmission(submission);

	Set<SubmissionUser> submissionUsers = new HashSet<>();
	SubmissionUser submissionUser = new SubmissionUser(userProfile, submission);
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(String.format(Messages.getString("SubmissionServiceImpl.1"), //$NON-NLS-1$
		    submissionName, submission.getSubmissionId(), TmUserProfile.getCurrentUserName()));
	}
	submissionUser = getSelf().saveSubmissionUser(submissionUser);
	submissionUsers.add(submissionUser);
	submission.setSubmissionUsers(submissionUsers);

	return submission;
    }

    private TermEntry createSubmissionTermEntry(TermEntry termEntry, Submission submission) {
	Long submissionId = submission.getSubmissionId();
	String submissionName = submission.getName();
	String submitter = submission.getSubmitter();

	termEntry.setSubmissionId(submissionId);
	termEntry.setSubmissionName(submissionName);
	termEntry.setSubmitter(submitter);

	TermEntry subTermEntry = new TermEntry();
	subTermEntry.setParentUuId(termEntry.getUuId());
	subTermEntry.setDateCreated(new Date().getTime());
	subTermEntry.setDateModified(new Date().getTime());
	subTermEntry.setDescriptions(termEntry.getDescriptions());
	subTermEntry.setProjectId(termEntry.getProjectId());
	subTermEntry.setShortCode(termEntry.getShortCode());
	subTermEntry.setSubmissionId(submissionId);
	subTermEntry.setSubmissionName(submissionName);
	subTermEntry.setSubmitter(submitter);
	subTermEntry.setUserCreated(TmUserProfile.getCurrentUserName());
	subTermEntry.setUserModified(TmUserProfile.getCurrentUserName());
	subTermEntry.setUuId(UUID.randomUUID().toString());
	return subTermEntry;

    }

    private List<TermEntry> findSubTermEntriesBySubmissionId(Long submissionId) {
	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(submissionId);
	return browseSubTermEntries(filter);
    }

    private List<TermEntry> findSubTermEntriesByTermIds(List<String> submissionTermIds, Long projectId) {
	return findTermEntriesByTermIds(getSubmissionCollection(), submissionTermIds, projectId);
    }

    private List<String> findSubTermIdsBySubmissionId(Long submissionId) {
	List<String> termIds = new ArrayList<>();

	TmgrSearchFilter filter = new TmgrSearchFilter();
	filter.setSubmissionId(submissionId);
	filter.addResultField("*_" + SolrParentDocFields.DYN_TERM_ID_SORT,
		"*_" + SolrParentDocFields.DYN_TERM_NAME_SORT_SUFFIX_NEW,
		"*_" + SolrParentDocFields.DYN_ATTRIBUTE_MULTI_STORE, "*_" + SolrParentDocFields.DYN_TERM_NAME_STORE,
		"*_" + SolrParentDocFields.DYN_TERM_ID_STORE);

	List<TermEntry> termEntries = browseSubTermEntries(filter);

	if (CollectionUtils.isNotEmpty(termEntries)) {
	    for (TermEntry termEntry : termEntries) {
		List<Term> terms = termEntry.ggetTerms();
		if (CollectionUtils.isNotEmpty(terms)) {
		    for (Term term : terms) {
			termIds.add(term.getUuId());
		    }
		}
	    }
	}

	return termIds;
    }

    private List<Term> findSubTermsByIds(List<String> termIds, Long projectId) {
	try {
	    return getBrowser(getSubmissionCollection()).findTermsByIds(termIds, Arrays.asList(projectId));
	} catch (TmException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    private BeanFactory getBeanFactory() {
	return _beanFactory;
    }

    private HistoryCreator getHistoryCreator() {
	return _historyCreator;
    }

    private Map<String, List<TermEntry>> getRegularHistory(Set<String> termIds) {
	return getTermEntryService().findHistoriesByTermEntryIds(termIds);
    }

    private SubmissionService getSelf() {
	return getBeanFactory().getBean(SubmissionService.class);
    }

    private Submission getSubmission(TmProject project, String submissionName, String submissionMarkerId,
	    String sourceLanguage, Long submissionId) {
	if (submissionId != null) {
	    // new transaction
	    return getSelf().findByIdFetchChilds(submissionId);
	}

	Submission submission = createEmptySubmission(project, submissionName, submissionMarkerId, sourceLanguage);
	return submission;
    }

    private SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    private SubmissionDetailService getSubmissionDetailService() {
	return _submissionDetailService;
    }

    private List<TermEntry> getSubmissionHistory(DbSubmissionTermEntry dbEntry) {
	return getHistoryCreator().createHistory(dbEntry);
    }

    private SubmissionLanguageDAO getSubmissionLanguageDAO() {
	return _submissionLanguageDAO;
    }

    private DbSubmissionTermEntryService getSubmissionTermService() {
	return _submissionTermService;
    }

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private TermEntryService getTermEntryService() {
	return _termEntryService;
    }

    private Submission loadSubmission(Long submissionId) {
	Submission submission = null;
	try {
	    submission = getSubmissionDAO().load(submissionId);
	} catch (Exception e) {
	    throw new RuntimeException(String.format(Messages.getString("SubmissionServiceImpl.10"), //$NON-NLS-1$
		    submissionId), e);
	}
	return submission;
    }

    private void replaceOriginalWithUpdatedTerm(TermEntry incomingUpdateTermEntry, TermEntry updatedTermEntry,
	    String termUuId) {

	Optional<Term> originalTermOptional = updatedTermEntry.ggetAllTerms().stream()
		.filter(term -> term.getUuId().equals(termUuId)).findFirst();

	Optional<Term> updatedTermOptional = incomingUpdateTermEntry.ggetAllTerms().stream()
		.filter(term -> term.getUuId().equals(termUuId)).findFirst();

	if (originalTermOptional.isPresent() && updatedTermOptional.isPresent()) {
	    Term originalTerm = originalTermOptional.get();
	    Term updatedTerm = updatedTermOptional.get();

	    updatedTermEntry.getLanguageTerms().get(originalTerm.getLanguageId()).remove(originalTerm);
	    updatedTermEntry.getLanguageTerms().get(originalTerm.getLanguageId()).add(updatedTerm);
	}
    }

    private void validateSubmissionTermForCommit(Term submissionTerm) {
	if (StringUtils.isBlank(submissionTerm.getTempText())) {
	    throw new UserException(MessageResolver.getMessage("SubmissionServiceImpl.6"), //$NON-NLS-1$
		    MessageResolver.getMessage("SubmissionServiceImpl.11")); //$NON-NLS-1$
	} else if (submissionTerm.getCanceled()) {
	    throw new UserException(MessageResolver.getMessage("SubmissionServiceImpl.6"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("SubmissionServiceImpl.12"), //$NON-NLS-1$
			    submissionTerm.getTempText()));
	} else if (!submissionTerm.getStatus().equals(ItemStatusTypeHolder.IN_TRANSLATION_REVIEW.getName())) {
	    throw new UserException(MessageResolver.getMessage("SubmissionServiceImpl.6"), //$NON-NLS-1$
		    String.format(MessageResolver.getMessage("SubmissionServiceImpl.13"), //$NON-NLS-1$
			    submissionTerm.getTempText()));
	}
    }
}
